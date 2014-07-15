package com.kohls.shipment.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSGenrateAndStampBatchNoAtShimenetLine extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSGetShipmentDetailsAPI.class.getName());

	/**
     * This Method is called to stamp BatchNo at shipment line level, when we click on bulk print button in UI. 
     */
	public Document stampBatchNoToShipments(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception {
		/**
		 	* code has been modified, to generate only the BatchNo and return it to UI
		 	* The code for changeShipment and ChangeShipmentStatus has been moved to a different method.
	 	*/	
		if (!XMLUtil.isVoid(inputDoc)) {
			NodeList eleShipmentList = inputDoc.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			int eleShipmentListLen = eleShipmentList.getLength();
			
			
			if (eleShipmentListLen > 0) {
				String strGenratedBatchNO = getBatchNo(yfsEnvironment);
				Element rootEle = 	inputDoc.getDocumentElement();
				rootEle.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
				
			}
		}
		
		return  inputDoc;
		
//		Element eleInput = inputDoc.getDocumentElement();
//		Document OutDocGetShipmentList =  getShipmentList(yfsEnvironment, inputDoc);
//		Document outExtnBatchNoDoc = XMLUtil.createDocument(KohlsXMLLiterals.E_SHIPMENTS);
//		String strQtyToPrint = eleInput.getAttribute(KohlsConstant.A_QTY_TO_PRINT);
//		String node = eleInput.getAttribute(KohlsConstant.A_NODE);
//		if (YFCLogUtil.isDebugEnabled()) {
//			this.log.debug("OutDocGetShipmentList output: "
//					+ XMLUtil.getXMLString(OutDocGetShipmentList));
//		}
//		if (!XMLUtil.isVoid(OutDocGetShipmentList)) {
//			NodeList eleShipmentList = OutDocGetShipmentList
//					.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
//
//			int eleShipmentListLen = eleShipmentList.getLength();
//			int batchCount = 0;
//			
//			int noOfShipmentsInBatch = Integer.parseInt(strQtyToPrint);
//			Document docOutPutChangeShipment = null;
//			if (eleShipmentListLen > 0) {
//				String strGenratedBatchNO = getBatchNo(yfsEnvironment);
//				for (int i = 0; i < eleShipmentListLen; i++) {
//					Element eleShipment = (Element) eleShipmentList.item(i);
//					if (batchCount >= noOfShipmentsInBatch) {
//						break;
//					}
//					
//					if (YFCLogUtil.isDebugEnabled()) {
//						this.log.debug("eleShipment : "
//								+ XMLUtil.getElementXMLString(eleShipment));
//					}
//					Document docInputChangeShipment = XMLUtil
//							.createDocument(KohlsXMLLiterals.E_SHIPMENT);
//					boolean isAwatingForPrintList = stampBatchIdAtShipmentLine(
//							eleShipment, strGenratedBatchNO, docInputChangeShipment,
//							yfsEnvironment);
//					if (isAwatingForPrintList) {
//						// invoking changeShipment APi
//						
//						if (YFCLogUtil.isDebugEnabled()) {
//							this.log.debug("eleShipment : "
//									+ XMLUtil.getXMLString(docInputChangeShipment));
//						}
//						docInputChangeShipment.getDocumentElement().setAttribute(KohlsConstant.A_PROFILE_ID, strGenratedBatchNO);
//						docOutPutChangeShipment = KOHLSBaseApi.invokeAPI(
//								yfsEnvironment,
//								KohlsConstant.API_CHANGE_SHIPMENT,
//								docInputChangeShipment);
//						changeShipmet(eleShipment,yfsEnvironment);
//						batchCount++;
//					}
//					docInputChangeShipment = null;
//				}
//
//				// call to custom table
//				if (batchCount > 0) {
//					insertBatchNoKlBatchPrintTable( yfsEnvironment,  strGenratedBatchNO,  batchCount, node);
//				Element rootEle = 	outExtnBatchNoDoc.getDocumentElement();
//				rootEle.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
//				}
//			}
//		}
//		return outExtnBatchNoDoc;
	}

	/**
     * This Method is called to generate BatchNo
     */
	private String getBatchNo(YFSEnvironment env) throws Exception {
		String strBatchIdSeq = "";

		Statement statement = null;
		ResultSet resultSet = null;
		YCPContext context = (YCPContext) env;

		try {
			Connection connection = context.getDBConnection();
			statement = connection.createStatement();
			resultSet = statement
					.executeQuery(KohlsConstant.SQL_STATEMENT_FOR_SEQ_YFS_PRINT_BATCH_ID);
			resultSet.next();
			strBatchIdSeq = resultSet.getString(KohlsConstant.A_BATCH_ID);
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} finally {
			try {

				resultSet.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		String strGenratedBatchNO = getTimeStampForPrintBatchNo() + "_" + strBatchIdSeq;
		
		return strGenratedBatchNO;
	}

	private String getTimeStampForPrintBatchNo() {
		Calendar cal = Calendar.getInstance();
		DateFormat dateFormat = new SimpleDateFormat(
				KohlsConstant.V_DATE_FORMAT);
		return dateFormat.format(cal.getTime());
	}
	/**
     * This Method is called to Change Shipment status
     */
	private void changeShipmet(Element eleShipment,YFSEnvironment yfsEnvironment) throws Exception{
		
//		 creating input to changeShipmentStatus
		Document docInputChangeShipmentStatus = XMLUtil
				.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eleChangeShipmentStaus = docInputChangeShipmentStatus
				.getDocumentElement();
		eleChangeShipmentStaus.setAttribute(
				KohlsConstant.A_BASE_DROP_STATUS,
				KohlsConstant.V_BASE_DROP_STATUS);
		eleChangeShipmentStaus.setAttribute(KohlsConstant.A_SHIP_NODE,
						eleShipment.getAttribute(KohlsConstant.A_SHIP_NODE));
		eleChangeShipmentStaus.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
						eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		eleChangeShipmentStaus.setAttribute(KohlsConstant.A_SHIPMENT_NO,
						eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_NO));
		eleChangeShipmentStaus.setAttribute(
				KohlsConstant.A_TRANSACTION_ID,
				KohlsConstant.V_TRANSACTION_ID);
		eleChangeShipmentStaus.setAttribute(
				KohlsConstant.A_NODE_TYPE,
				KohlsConstant.V_STORE);

		// invoking changeShipmentStatus API
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("Change Shipment input : "
					+ XMLUtil.getXMLString(docInputChangeShipmentStatus));
		}
		Document docOutPutChangeShipment = KOHLSBaseApi.invokeAPI(
				yfsEnvironment,
				KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
				docInputChangeShipmentStatus);
		docOutPutChangeShipment = null;
		//batchCount++;
	} 
	
	/**
     * This Method is called to insert BatchNO with No of Shipments in custom table.
     */
	
	private void insertBatchNoKlBatchPrintTable(YFSEnvironment yfsEnvironment, String strGenratedBatchNO, int batchCount, String node) throws Exception{
		
//		 Creating input to insert batch No in KL_BATCH_PRINT table
		Document docInputBatchPrintToDB = XMLUtil
				.createDocument(KohlsConstant.E_BATCH_PRINT);
		Element eleBatchPrint = docInputBatchPrintToDB
				.getDocumentElement();
		eleBatchPrint.setAttribute(KohlsConstant.A_BATCH_ID,
				strGenratedBatchNO);
		eleBatchPrint.setAttribute(KohlsConstant.A_REPRINT_FLAG,
				KohlsConstant.NO);
		eleBatchPrint.setAttribute(KohlsConstant.A_NO_OF_SHIPMENTS,
				String.valueOf(batchCount));
		eleBatchPrint.setAttribute(KohlsConstant.A_STATUS,
				KohlsConstant.V_AWAITING_PRINT);
		eleBatchPrint.setAttribute(KohlsConstant.A_USER,
				yfsEnvironment.getUserId());
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE_TYPE,
				KohlsConstant.V_STORE);
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE,
				node);
		// invoking BatchNo insert Service
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("inserting Batch NO In custom table : "
					+ XMLUtil.getXMLString(docInputBatchPrintToDB));
		}
		KOHLSBaseApi.invokeService(yfsEnvironment, "UpadateBatchPrintTableService",
				docInputBatchPrintToDB);

	} 
	
	
	/**
     * This Method is called to stamp  Batch number at shipment line level
     */
	
	private boolean stampBatchIdAtShipmentLine(Element eleShipment,
			String strGenratedBatchNO, Document docInputChangeShipment,
			YFSEnvironment yfsEnvironment) throws Exception {

		// check batchNo is already printed or not

		boolean isAwatingForPrintList = false;
		Document outDocShipmentDetails = null;
		try {
			// create input to getShipmentDetails.
			Document docInputGetShipmentDetails = XMLUtil
					.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			Element eleShipmentDetails = docInputGetShipmentDetails
					.getDocumentElement();
			eleShipmentDetails.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
					eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug(" input of GetShipment details: "
					+ XMLUtil.getXMLString(docInputGetShipmentDetails));
		}
//		 invoking getShipmentDetails API	
		outDocShipmentDetails = KOHLSBaseApi.invokeAPI(yfsEnvironment,
							KohlsConstant.TEMLATE_GET_SHIPMENT_DETAILS_FOR_SHIPMENT_STATUS,
							KohlsConstant.API_GET_SHIPMENT_DETAILS,
							docInputGetShipmentDetails);
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug(" output of GetShipment details: "
					+ XMLUtil.getXMLString(outDocShipmentDetails));
		}
		} catch (Exception e) {
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.error(e);
			}
		}

		if (outDocShipmentDetails != null) {
			Element shipmentDetails = outDocShipmentDetails
					.getDocumentElement();
			String shipmentStatus = shipmentDetails
					.getAttribute(KohlsConstant.A_STATUS);
			if (null != shipmentStatus
					&& KohlsConstant.V_AWAITING_PICK_LIST_PRINT
							.equalsIgnoreCase(shipmentStatus)) {
				Element eleChangeShipment = docInputChangeShipment
						.getDocumentElement();
				eleChangeShipment.setAttribute(KohlsConstant.A_ACTION,
						KohlsConstant.ACTION_MODIFY);
				eleChangeShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
						eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
				Element eleChangeShipmentLines = XMLUtil.createChild(
						eleChangeShipment, KohlsConstant.E_SHIPMENT_LINES);
				Element eleShipLines = (Element) eleShipment
						.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES)
						.item(0);

				NodeList nlShipmentLines = eleShipLines
						.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
				int iLen = nlShipmentLines.getLength();
				if (iLen != 0) {
					for (int i = 0; i < iLen; i++) {
						Element eleShipmentLine = (Element) nlShipmentLines
								.item(i);
						Element eleShipmentLineExtn = XMLUtil.getChildElement(
								eleShipmentLine, KohlsConstant.E_EXTN);
						String ExtnBatchNoAtShipLine = null; 
						if(!XMLUtil.isVoid(eleShipmentLineExtn)){
							ExtnBatchNoAtShipLine = eleShipmentLineExtn
							.getAttribute(KohlsConstant.A_EXTN_BATCH_NO);
						}
						if (!XMLUtil.isVoid(eleShipmentLineExtn) && null == ExtnBatchNoAtShipLine 
								|| ExtnBatchNoAtShipLine.equalsIgnoreCase("")) {
							Element eleChangeShipmentLine = XMLUtil
									.createChild(eleChangeShipmentLines,
											KohlsConstant.E_SHIPMENT_LINE);
							eleChangeShipmentLine.setAttribute(KohlsConstant.A_ACTION,
									        KohlsConstant.ACTION_MODIFY);
							eleChangeShipmentLine
									.setAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY,
											eleShipmentLine.getAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY));
							Element eleChangeShipExtn = XMLUtil
									.createChild(eleChangeShipmentLine,	KohlsConstant.E_EXTN);
							eleChangeShipExtn.setAttribute(
									KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
							isAwatingForPrintList = true;
						}
					}
				}
				NodeList shiplLineNodeList = XPathUtil.getNodeList(
						eleChangeShipment, "ShipmentLines/ShipmentLine");
				if (null != shiplLineNodeList
						&& shiplLineNodeList.getLength() < 0) {
					isAwatingForPrintList = false;
				}
			}
		}
		return isAwatingForPrintList;
	}

	//Calling calling the getCommonCodeList API to get Maximum shipments to print 
	private String getMaxRecordsToPrint(YFSEnvironment yfsEnvironment) throws Exception{
//		 Creating input CommonCodeLIST api to get Maximum no shipemts to print
		Document docInputCommonCodeForMaxPrint = XMLUtil
				.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		Element eleCommonCode = docInputCommonCodeForMaxPrint
				.getDocumentElement();
		eleCommonCode.setAttribute(KohlsConstant.A_CODE_TYPE,
				KohlsConstant.V_KL_MAX_SHP_PRINT);

		Document outDocCommonCodeoutDoc = KOHLSBaseApi.invokeAPI(
				yfsEnvironment, KohlsConstant.API_GET_COMMON_CODE_LIST,
				docInputCommonCodeForMaxPrint);
		String maxRecordsToPrint = "";
		if (outDocCommonCodeoutDoc != null) {
			Element eleCommonCodeList = outDocCommonCodeoutDoc
					.getDocumentElement();
			maxRecordsToPrint = XPathUtil.getString(eleCommonCodeList,
					"CommonCode/@CodeShortDescription");
		}
		return maxRecordsToPrint;
	}
	
	/**
	 * Call getShipmentlist API for selected enterprise code  and node 
	 * 
	 * 
	 */
	private Document getShipmentList(YFSEnvironment yfsEnvironment,Document inputDoc) throws Exception{

		
		
		String maxRecordsToPrint = getMaxRecordsToPrint(yfsEnvironment);
		
		
		Element eleClientInputFromUI = inputDoc.getDocumentElement();
		eleClientInputFromUI.getAttribute(KohlsConstant.A_ENTERPRISE_CODE);
		// creating input to getShipmentList
		Document docInputShipmentList = XMLUtil
				.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element eledocInputShipmentList = docInputShipmentList
				.getDocumentElement();
		eledocInputShipmentList.setAttribute(KohlsConstant.A_ENTERPRISE_CODE,
				eleClientInputFromUI.getAttribute(KohlsConstant.A_ENTERPRISE_CODE));
		eledocInputShipmentList.setAttribute(KohlsConstant.A_SHIP_NODE,
				eleClientInputFromUI.getAttribute(KohlsConstant.A_SHIP_NODE));
		eledocInputShipmentList.setAttribute(KohlsConstant.A_STATUS,
				KohlsConstant.V_AWAITING_PICK_LIST_PRINT);
		eledocInputShipmentList.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS,
				maxRecordsToPrint);
		eledocInputShipmentList.setAttribute(KohlsConstant.A_SELECT_METHOD,
				KohlsConstant.V_NO_WAIT);
		Element eleOrderBy = XMLUtil.createChild(eledocInputShipmentList,
				KohlsConstant.E_ORDER_BY);
		Element eleAttribute = XMLUtil.createChild(eleOrderBy,
				KohlsConstant.E_ATTRIBUTE);
		eleAttribute.setAttribute(KohlsConstant.A_NAME,
				KohlsConstant.A_SHIPMENT_KEY);
		eleAttribute.setAttribute(KohlsConstant.A_DESC, KohlsConstant.NO);

		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("input to getShipmentList API"
					+ XMLUtil.getXMLString(docInputShipmentList));
		}
		// invoking getShipmentList Api
		Document OutDoc = KOHLSBaseApi.invokeAPI(yfsEnvironment,
				KohlsConstant.TEMLATE_GET_SHIPMENT_LIST_TO_STAMP_BATCH_NO,
				KohlsConstant.API_actual_GET_SHIPMENT_LIST,
				docInputShipmentList);
		return OutDoc;
	}
	
	
	
	/**
	 * This method stamp batch no at shipment line level for selected shipments,
	 *  this method will be called when we click on print button on UI
	 * 
	 * 
	 */
	public Document stampBatchNoForBatchPrint(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception {
		
	//	Document outExtnBatchNoDoc = XMLUtil.createDocument(KohlsXMLLiterals.E_SHIPMENTS);

		if (!XMLUtil.isVoid(inputDoc)) {
			NodeList eleShipmentList = inputDoc.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			int eleShipmentListLen = eleShipmentList.getLength();
			
			
			if (eleShipmentListLen > 0) {
				String strGenratedBatchNO = getBatchNo(yfsEnvironment);
				Element rootEle = 	inputDoc.getDocumentElement();
				rootEle.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
				
			}
		}
		
		return  inputDoc;
		

	}
		

	
	public Document changeShipmentForStorePickSlipPrint(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception{
		Element eleInput = inputDoc.getDocumentElement();
//		
		String strGenratedBatchNO = eleInput.getAttribute(KohlsConstant.A_EXTN_BATCH_NO);
		String strNode="";
		NodeList eleShipmentList = inputDoc
		.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

		int eleShipmentListLen = eleShipmentList.getLength();
		int batchCount = 0;
		
		
		Document docOutPutChangeShipment = null;
		if (eleShipmentListLen > 0) {
			
		
			for (int i = 0; i < eleShipmentListLen; i++) {
				Element eleShipment = (Element) eleShipmentList.item(i);
				strNode = eleShipment.getAttribute("ShipNode");
				Document docInputChangeShipment = XMLUtil
						.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				boolean isAwatingForPrintList = stampBatchIdAtShipmentLine(
						eleShipment, strGenratedBatchNO, docInputChangeShipment,
						yfsEnvironment);
				if (isAwatingForPrintList) {
		//			// invoking changeShipment APi
					
					if (YFCLogUtil.isDebugEnabled()) {
						this.log.debug("eleShipment : "
								+ XMLUtil.getXMLString(docInputChangeShipment));
					}
					docInputChangeShipment.getDocumentElement().setAttribute(KohlsConstant.A_PROFILE_ID, strGenratedBatchNO);
					docInputChangeShipment.getDocumentElement().setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE, eleShipment.getAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE));
					
					docOutPutChangeShipment = KOHLSBaseApi.invokeAPI(
							yfsEnvironment,
							KohlsConstant.API_CHANGE_SHIPMENT,
							docInputChangeShipment);
					changeShipmet(eleShipment,yfsEnvironment);
					batchCount++;
				}
				
			}
		
			// call to custom table
			if (batchCount > 0) {
				
				insertBatchNoKlBatchPrintTable( yfsEnvironment,  strGenratedBatchNO,  batchCount, strNode);
			}
		}

		return inputDoc;
	}
	/**
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return changeShipmentInput for BulkPrint
	 * @throws Exception
	 */
	public Document changeShipmentForBulkPrint(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception{	
		Element eleInput = inputDoc.getDocumentElement();
		String strGenratedBatchNO = eleInput.getAttribute(KohlsConstant.A_EXTN_BATCH_NO);
		Document OutDocGetShipmentList =  getShipmentList(yfsEnvironment, inputDoc);
		Document outExtnBatchNoDoc = XMLUtil.createDocument(KohlsXMLLiterals.E_SHIPMENTS);
		String strQtyToPrint = eleInput.getAttribute(KohlsConstant.A_QTY_TO_PRINT);
		String strNode = eleInput.getAttribute(KohlsConstant.A_NODE);
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("OutDocGetShipmentList output: "
					+ XMLUtil.getXMLString(OutDocGetShipmentList));
		}
		if (!XMLUtil.isVoid(OutDocGetShipmentList)) {
			NodeList eleShipmentList = OutDocGetShipmentList
					.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			int eleShipmentListLen = eleShipmentList.getLength();
			int batchCount = 0;
			
			int noOfShipmentsInBatch = Integer.parseInt(strQtyToPrint);
			Document docOutPutChangeShipment = null;
			if (eleShipmentListLen > 0) {
				
				for (int i = 0; i < eleShipmentListLen; i++) {
					Element eleShipment = (Element) eleShipmentList.item(i);
					if (batchCount >= noOfShipmentsInBatch) {
						break;
					}
					
					if (YFCLogUtil.isDebugEnabled()) {
						this.log.debug("eleShipment : "
								+ XMLUtil.getElementXMLString(eleShipment));
					}
					Document docInputChangeShipment = XMLUtil
							.createDocument(KohlsXMLLiterals.E_SHIPMENT);
					boolean isAwatingForPrintList = stampBatchIdAtShipmentLine(
							eleShipment, strGenratedBatchNO, docInputChangeShipment,
							yfsEnvironment);
					if (isAwatingForPrintList) {
						// invoking changeShipment APi
						
						if (YFCLogUtil.isDebugEnabled()) {
							this.log.debug("eleShipment : "
									+ XMLUtil.getXMLString(docInputChangeShipment));
						}
						docInputChangeShipment.getDocumentElement().setAttribute(KohlsConstant.A_PROFILE_ID, strGenratedBatchNO);
						 KOHLSBaseApi.invokeAPI(
								yfsEnvironment,
								KohlsConstant.API_CHANGE_SHIPMENT,
								docInputChangeShipment);
						changeShipmet(eleShipment,yfsEnvironment);
						batchCount++;
					}
					docInputChangeShipment = null;
				}

				// call to custom table
				if (batchCount > 0) {
					insertBatchNoKlBatchPrintTable( yfsEnvironment,  strGenratedBatchNO,  batchCount, strNode);
				Element rootEle = 	outExtnBatchNoDoc.getDocumentElement();
				rootEle.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
				}
			}
		}
		return outExtnBatchNoDoc;
	}
}
