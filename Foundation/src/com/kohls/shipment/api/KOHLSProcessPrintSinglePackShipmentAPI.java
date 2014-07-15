/* Copyright  2012, Perficient Inc. All rights reserved.
 * Change Log:
 *    Date(MM/DD/YYYY)  Name          Description
 *     06/02/2012     Shaila Wagle  Included file in the com.kohls.api package.
 */
package com.kohls.shipment.api;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSProcessPrintSinglePackShipmentAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSProcessPrintSinglePackShipmentAPI.class.getName());

	/**
	 * This method will fetch values of the required attributes from the 
	 * input document and invoke respective services.
	 *  
	 * Sample Input documents are: 
	 * Input doc type 1 - 
	 * <PrintPackShipment ItemID="" PrintType="SINGLE" ShipmentType=""
	 * TotalShipments="" PrinterID="" ShipNode="" /> 
	 * Input doc type 2 - 
	 * <Shipment ShipmentKey="" PrinterID="" />
	 * 
	 * @param env
	 *            - environment variable
	 * @param inXML
	 *            - Either PrintPackShipment or Shipment root element
	 * @throws Exception
	 */

	public void processPrintSinglePackShipment(YFSEnvironment env,
			Document inXML) throws Exception {

		String strShipNode="";

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KOHLSProcessPrintSinglePackShipmentAPI processPrintSinglePackShipment method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		Document docIpgetShipmentList = null;
		String strPrinterId = inXML.getDocumentElement().getAttribute(
				KohlsConstant.A_PRINTER_ID);
		String sPrintBatchNumber = inXML.getDocumentElement().getAttribute(
				KohlsConstant.A_BATCH_ID);

		System.out.println("processPrintSinglePackShipment......inXML......."+inXML);
		if(!XMLUtil.isVoid(inXML))
		{
			if (inXML.getDocumentElement().getNodeName()
					.equalsIgnoreCase(KohlsConstant.E_PRINT_PACK_SHIPMENT)) {
				Element eleInputDoc = inXML.getDocumentElement();
				strShipNode = eleInputDoc.getAttribute(
						KohlsConstant.A_SHIP_NODE);
				//create the service
				String strServiceName=KohlsConstant.V_PREFIX+strShipNode+KohlsConstant.V_MIDDLE+KohlsConstant.V_SUFFIX;

				String strNumber = eleInputDoc.getAttribute(
						KohlsConstant.A_TOTAL_SHIPMENTS);
				String strItemId = eleInputDoc.getAttribute(
						KohlsConstant.A_ITEM_ID);
				String strShipmentType = eleInputDoc.getAttribute(
						KohlsConstant.A_SHIPMENT_TYPE);
				//String strstatus=eleInputDoc.getAttribute(KohlsConstant.A_STATUS);
				//Prepare input doc for getShipmentList API
				docIpgetShipmentList = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENT);

				Element eleIpgetShipmentList = docIpgetShipmentList
				.getDocumentElement();
				eleIpgetShipmentList.setAttribute(KohlsConstant.A_IGNORE_ORDERING, KohlsConstant.V_N);
				//eleIpgetShipmentList.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS, KohlsConstant.V_MAXIMUM_RECORDS);
				eleIpgetShipmentList.setAttribute(KohlsConstant.A_STATUS, KohlsConstant.A_AWAITING_PICK_LIST);
				Element eleorderby=docIpgetShipmentList.createElement(KohlsConstant.E_ORDER_BY);
				eleIpgetShipmentList.appendChild(eleorderby);
				Element eleattribute=docIpgetShipmentList.createElement(KohlsConstant.A_ATTRIBUTE);
				eleorderby.appendChild(eleattribute);
				
				/*
				 * Start OASIS -- 05/NOV/2013 for PMR  85613,379,000
				 * Adding logic to getShipmentList order by OrderHeaderKey, to get oldest shipments first
				 * 
				 */
				//eleattribute.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_NO);
				eleattribute.setAttribute(KohlsConstant.A_NAME, "OrderHeaderKey");
				// End OASIS -- 05/NOV/2013 for PMR  85613,379,000
				
				eleIpgetShipmentList.setAttribute(KohlsConstant.A_SHIP_NODE,
						strShipNode);
				//eleIpgetShipmentList.setAttribute(KohlsConstant.A_STATUS,
				//strstatus);
				eleIpgetShipmentList.setAttribute(KohlsConstant.A_SHIPMENT_TYPE,
						strShipmentType);
				eleIpgetShipmentList.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS,
						strNumber);
				if(strShipmentType.equalsIgnoreCase(KohlsConstant.V_MULTI_PRODUCT_FAMILY))
				{
					Element eleShipmentExtn = docIpgetShipmentList
					.createElement(KohlsConstant.E_EXTN);
					eleShipmentExtn.setAttribute(KohlsConstant.A_EXTN_SHIPMENT_FAMILY, strItemId);
					eleIpgetShipmentList.appendChild(eleShipmentExtn);
				}
				else{
					Element eleIpShipmentLines = docIpgetShipmentList
					.createElement(KohlsConstant.E_SHIPMENT_LINES);
					eleIpgetShipmentList.appendChild(eleIpShipmentLines);
					Element eleIpShipmentLine = docIpgetShipmentList
					.createElement(KohlsConstant.E_SHIPMENT_LINE);
					eleIpShipmentLine.setAttribute(KohlsConstant.A_ITEM_ID, strItemId);
					eleIpShipmentLines.appendChild(eleIpShipmentLine);
				}

				// Creating template document for getShipmentList API
				Document docTempGetShpList = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENTS);
				Element eleIpTempGetShptsList = docTempGetShpList.getDocumentElement();
				Element eleIptempGetShpList = docTempGetShpList
				.createElement(KohlsConstant.E_SHIPMENT);
				eleIpTempGetShptsList.appendChild(eleIptempGetShpList);
				eleIptempGetShpList.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
						KohlsConstant.A_BLANK);
				
				Element eleIptempGetShpListLines = docTempGetShpList
				.createElement(KohlsConstant.E_SHIPMENT_LINES);
				eleIptempGetShpList.appendChild(eleIptempGetShpListLines);
				

				Element eleIptempGetShpListLine = docTempGetShpList
				.createElement(KohlsConstant.E_SHIPMENT_LINE);
				eleIptempGetShpListLine.setAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY,
						KohlsConstant.A_BLANK);
				eleIptempGetShpListLines.appendChild(eleIptempGetShpListLine);
				
				
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Input for getShipmentList : \n"
							+ XMLUtil.getXMLString(docIpgetShipmentList));
				}

				Document docOutputGetShipmentList = KOHLSBaseApi.invokeAPI(
						env, docTempGetShpList,
						KohlsConstant.API_actual_GET_SHIPMENT_LIST, docIpgetShipmentList);

				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Output for getShipmentList : \n"
							+ XMLUtil.getXMLString(docOutputGetShipmentList));
				}

				NodeList nodeShipmentList = docOutputGetShipmentList.getDocumentElement()
				.getElementsByTagName(KohlsConstant.E_SHIPMENT);
				int iNumShipments = nodeShipmentList.getLength();

				if (KohlsConstant.V_ZERO != iNumShipments) {
					
					//bhaskar Start

					for (int i = 0; i < iNumShipments; i++) {
						Element eleShip = (Element) nodeShipmentList.item(i);
						//call to changeShipment
						stampExtnBatchNoAtShipemtLine(eleShip,  sPrintBatchNumber, env);
						//call to changeShipmentStatus
						changeShipmentStatus( eleShip, env);
						//inserting BatchNo in KL_BATCH_PRINT table.
						
						
						/*String strShipmentKey = eleShip
						.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
						Document docInService = XMLUtil
						.createDocument(KohlsConstant.E_SHIPMENT);
						Element eleIndocService = docInService.getDocumentElement();
						eleIndocService.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
								strShipmentKey);
							eleIndocService.setAttribute(KohlsConstant.A_PRINTER_ID,
								strPrinterId);
						eleIndocService.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET,
								KohlsConstant.V_N);
						eleIndocService.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT,
								KohlsConstant.V_Y);
						if(!YFCObject.isVoid(sPrintBatchNumber))
						{
							eleIndocService.setAttribute(KohlsConstant.A_BATCH_ID,
									sPrintBatchNumber);
						}
						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("Input for getShipmentList : \n"
									+ XMLUtil.getXMLString(docInService));
						}*/

						//					Create Service name

						//String strServiceName=KohlsConstant.V_PREFIX+strShipNode+KohlsConstant.V_MIDDLE+KohlsConstant.V_SUFFIX;

						// call the service
						
						//Bbhaskar Start
						/*Document docGetShipmentListOutput = KOHLSBaseApi.invokeService(env,
								KohlsConstant.API_REPRINT_PICK_SLIP_SERVICE, docInService);
                        
						
						if (YFCLogUtil.isDebugEnabled()&& !YFCObject.isVoid(docGetShipmentListOutput)) {
							log.debug("Output for getShipmentList : \n"
									+ XMLUtil.getXMLString(docGetShipmentListOutput));
						}
						*/
						//Bhaskar  END 
						
						
						// call to changeShipmentAPI & status
						
						
						
						
						
						
						
						
						
						
						
						
						
						
					}// End of for loop
					
				}// End of nodelist blank if loop
				
				insertBatchNoKlBatchPrintTable( env,  sPrintBatchNumber, strNumber, strShipNode);//Need to check
				
			} /*
		Commenting this code as we dont want pick slips for singles. It will be pack slips

		else if (inXML.getDocumentElement().getNodeName()
				.equalsIgnoreCase(KohlsConstant.E_SHIPMENT)) {
			String strShipmentKey = inXML.getDocumentElement().getAttribute(
					KohlsConstant.A_SHIPMENT_KEY);
			Document docIpShipmentDetails = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENT);
			Element eleIpShipmentDetails = docIpShipmentDetails
					.getDocumentElement();
			eleIpShipmentDetails.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
					strShipmentKey);
			Document docOutputGetShipmentDetails = KOHLSBaseApi.invokeAPI(
					env, KohlsConstant.API_GET_SHIPMENT_DETAILS,
					docIpShipmentDetails);

			Element eleOutputGetShipmentDetails = docOutputGetShipmentDetails
					.getDocumentElement();
			eleOutputGetShipmentDetails.setAttribute(
					KohlsConstant.A_PRINTER_ID, strPrinterId);





			// change the Service NAME
			Document docGetShipmentListOutput = KOHLSBaseApi.invokeService(env,
					KohlsConstant.SERVICE_KOHLS_CHANGE_STATUS_SINGLE_PRINT_SERVICE,
					inXML);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Output for getShipmentList : \n"
						+ XMLUtil.getXMLString(docGetShipmentListOutput));
			}

		} // End of else if loop
			 */
		}

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- End of KOHLSProcessPrintSinglePackShipmentAPI processPrintSinglePackShipment method -- >");
		}
	}
	
	private boolean stampExtnBatchNoAtShipemtLine(Element outDocShipmentEle, String strGenratedBatchNO,YFSEnvironment env) throws Exception {
		
		boolean isExtnBatchNoUpdated = false;
		Document docInputChangeShipment = XMLUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);

		
			Element eleChangeShipmentInput = docInputChangeShipment
					.getDocumentElement();
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_ACTION,
					KohlsConstant.ACTION_MODIFY);
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
					outDocShipmentEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_PROFILE_ID,
					strGenratedBatchNO);
			Element eleChangeShipmentLinesInput = XMLUtil.createChild(
					eleChangeShipmentInput, KohlsConstant.E_SHIPMENT_LINES);
			Element eleShipLines = (Element) outDocShipmentEle
					.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES)
					.item(0);

			NodeList nlShipmentLines = eleShipLines
					.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
			int iLen = nlShipmentLines.getLength();
			if (iLen != 0) {
				for (int i = 0; i < iLen; i++) {
					Element eleShipmentLine = (Element) nlShipmentLines
							.item(i);
					
					Element eleChangeShipmentLineInput = XMLUtil.createChild(
							eleChangeShipmentLinesInput, KohlsConstant.E_SHIPMENT_LINE);
					
					eleChangeShipmentLineInput.setAttribute(KohlsConstant.A_ACTION,
					        KohlsConstant.ACTION_MODIFY);
					eleChangeShipmentLineInput.setAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY,
							eleShipmentLine.getAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY));
					
					
					Element eleChangeShipExtnInput = XMLUtil
							.createChild(eleChangeShipmentLineInput, KohlsConstant.E_EXTN);
					eleChangeShipExtnInput.setAttribute(
							KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
				}
			}
			System.out.println("ChangeShipment ----RDC"+XMLUtil.getXMLString(eleChangeShipmentInput.getOwnerDocument()));
			NodeList shiplLineNodeList = XPathUtil.getNodeList(
					eleChangeShipmentInput, "ShipmentLines/ShipmentLine");
			if (null != shiplLineNodeList
					&& shiplLineNodeList.getLength() > 0) {
				KOHLSBaseApi.invokeAPI(env,KohlsConstant.API_CHANGE_SHIPMENT,
						eleChangeShipmentInput.getOwnerDocument());
				isExtnBatchNoUpdated = true;
			}
	
		return isExtnBatchNoUpdated;
	}
	
	
	/**
     * This Method is called to Change Shipment status
     */
	private void changeShipmentStatus(Element eleShipment,YFSEnvironment yfsEnvironment) throws Exception{
		
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
	
	
	
	private void insertBatchNoKlBatchPrintTable(YFSEnvironment yfsEnvironment, String strGenratedBatchNO, String batchCount,String strShipNode) throws Exception{
		
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
				batchCount);
		eleBatchPrint.setAttribute(KohlsConstant.A_STATUS,
				KohlsConstant.V_AWAITING_PRINT);
		eleBatchPrint.setAttribute(KohlsConstant.A_USER,
				yfsEnvironment.getUserId());
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE_TYPE,
				KohlsConstant.A_SINGLES_PRINT);
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE,
				strShipNode);
		
		System.out.println("docInputBatchPrintToDB...."+XMLUtil.getXMLString(docInputBatchPrintToDB));
		// invoking BatchNo insert Service
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("inserting Batch NO In custom table : "
					+ XMLUtil.getXMLString(docInputBatchPrintToDB));
		}
		KOHLSBaseApi.invokeService(yfsEnvironment, "UpadateBatchPrintTableService",
				docInputBatchPrintToDB);

	}

}