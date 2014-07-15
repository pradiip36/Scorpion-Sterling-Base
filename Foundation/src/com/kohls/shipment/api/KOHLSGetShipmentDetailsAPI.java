package com.kohls.shipment.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;

/**
 * This class provides output of getShipmentDetails API
 * 
 * @author kiran
 * 
 */
public class KOHLSGetShipmentDetailsAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSGetShipmentDetailsAPI.class.getName());

	/**
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */

	public Document getShipmentDetails(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception {

		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSGetShipmentDetailsAPI" +
					" getShipmentDetails method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}

		Document output = null;
		String strAction="";
		Document docWorldShipFile=null;

		if (YFCObject.isVoid(inputDoc)) {
			output = inputDoc;

		}else {

			Element eleInputDoc=inputDoc.getDocumentElement();
			
			//Defaulting Seller Organization code to KOHLS.COM
			
			eleInputDoc.setAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE,
					KohlsConstant.V_SELLER_ORGANIZATION_CODE);
			
			//
			
			
			strAction=eleInputDoc.getAttribute(KohlsConstant.A_ACTION);

			if(YFCObject.equals(strAction, KohlsConstant.V_PACK)){

				String strWorldShip=XMLUtil.getXMLString(inputDoc);

				docWorldShipFile=XMLUtil.getDocument(strWorldShip);

				String strShipmentNo=eleInputDoc.getAttribute(KohlsConstant.A_SHIPMENT_NO);
				String strShipNode=eleInputDoc.getAttribute(KohlsConstant.A_SHIP_NODE);
				if(!YFCObject.isVoid(strShipmentNo)){
					inputDoc=getDocInputForShipmentDetails(strShipmentNo,strShipNode);
				}
			}

			if(YFCObject.equals(eleInputDoc.getAttribute(KohlsConstant.A_ACTION_TAKEN), KohlsConstant.V_UNPACK)){

				String strShipmentNo=eleInputDoc.getAttribute(KohlsConstant.A_SHIPMENT_NO);
				String strShipNode=eleInputDoc.getAttribute(KohlsConstant.A_SHIP_NODE);
				if(!YFCObject.isVoid(strShipmentNo)){
					inputDoc=getDocInputForShipmentDetails(strShipmentNo,strShipNode);
				}

			}
		}


		if (YFCLogUtil.isDebugEnabled()) 
		{
			this.log.debug("Input xml for getShipmentDetails API:"
					+ XMLUtil.getXMLString(inputDoc));
		}

		Document docOutputShipmentDetails = KOHLSBaseApi
		.invokeAPI(
				yfsEnvironment,
				KohlsConstant.API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH,
				KohlsConstant.API_GET_SHIPMENT_DETAILS, inputDoc);
		
	
		
		
		
		Element eleShipmentLines=null;
		
		if(!YFCObject.isVoid(docOutputShipmentDetails)){
			
			Element eleOutputShipmentDetails=docOutputShipmentDetails.getDocumentElement();
			
			eleShipmentLines=XMLUtil.getFirstElementByName(eleOutputShipmentDetails, KohlsConstant.E_SHIPMENT_LINES);
			
		}else{
			
			docOutputShipmentDetails=XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
			docOutputShipmentDetails.getDocumentElement().setAttribute(KohlsConstant.A_TOTAL_NUMBER_RECORDS,"0");
			output = docOutputShipmentDetails;
			
		}
		
		//Assumption - Shipment will have atleast one ShipmentLine
		
		if (YFCObject.isVoid(eleShipmentLines)) {
			docOutputShipmentDetails=XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
			docOutputShipmentDetails.getDocumentElement().setAttribute(KohlsConstant.A_TOTAL_NUMBER_RECORDS,"0");
			output = docOutputShipmentDetails;
		} else {


			

				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("getShipmentDetails API Output xml:"
							+ XMLUtil.getXMLString(docOutputShipmentDetails));
				}
				
			    log.debug("getShipmentDetails API Output xml:"
						+ XMLUtil.getXMLString(docOutputShipmentDetails));
				
				calculatedPlacedQuantity(docOutputShipmentDetails);

				String strEnterpriseCode = docOutputShipmentDetails
				.getDocumentElement().getAttribute(
						KohlsConstant.A_ENTERPRISE_CODE);

				// callContainerTypeItemList;

				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("Calling itemList API:");
				}

				Document docOutputContainerTypeItemList = KOHLSBaseApi
				.invokeAPI(
						yfsEnvironment,
						KohlsConstant.API_GET_ITEM_LIST_FOR_CONTAINER_TYPE_TEMPLATE_PATH,
						KohlsConstant.API_GET_ITEM_LIST,
						getDocInputForContainerTypeItemList(strEnterpriseCode));


				if (!YFCObject.isVoid(docOutputContainerTypeItemList)) {

					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("getItemList API Output xml:"
								+ XMLUtil.getXMLString(docOutputContainerTypeItemList));
					}

					XMLUtil
					.importElement(docOutputShipmentDetails
							.getDocumentElement(),
							docOutputContainerTypeItemList
							.getDocumentElement());

					/*	Generate Container Number for Shipment and mark new container element as new only if
					Shipment is in Pack List Printed or Packing in Progess status*/


					String strShipmentStatus = docOutputShipmentDetails
					.getDocumentElement().getAttribute(
							KohlsConstant.A_STATUS);


					boolean isValidStatusForPacking = YFCObject.equals(strShipmentStatus,
							KohlsConstant.STATUS_SHIPMENT_PICK_LIST_PRINTED) || YFCObject
							.equals(
									strShipmentStatus,
									KohlsConstant.STATUS_SHIPMENT_PACK_IN_PROGRESS);

					if (isValidStatusForPacking) {

						String strContainerNo = getContainerNo(yfsEnvironment);

						if (!YFCObject.isVoid(strContainerNo)) {

							Element docOutputContainers = XMLUtil
							.getChildElement(docOutputShipmentDetails
									.getDocumentElement(),
									KohlsConstant.E_CONTAINERS, true);

							Element eNewContainer = docOutputShipmentDetails
							.createElement(KohlsConstant.E_CONTAINER);
							docOutputShipmentDetails.getDocumentElement()
							.setAttribute(
									KohlsConstant.A_CONTAINER_NO,
									strContainerNo);
							eNewContainer.setAttribute(
									KohlsConstant.A_CONTAINER_NO,
									strContainerNo);
							eNewContainer.setAttribute(
									KohlsConstant.A_IS_NEW_CONTAINER,
									KohlsConstant.FLAG_Y);

							XMLUtil.appendChild(docOutputContainers,
									eNewContainer);

						}

					}

				}


				if(YFCObject.equals(strAction, KohlsConstant.V_PACK) && !YFCObject.isVoid(docWorldShipFile)){
					XMLUtil.importElement(docOutputShipmentDetails.getDocumentElement(), docWorldShipFile.getDocumentElement());

				}

				output = docOutputShipmentDetails;

			

		}
		if(!YFCObject.isVoid(yfsEnvironment.getTxnObject(KohlsConstant.O_PRINT_PACK_SLIP)))
		{
			String isPrintPackSlip = yfsEnvironment.getTxnObject(KohlsConstant.O_PRINT_PACK_SLIP).toString();
			if(isPrintPackSlip.equalsIgnoreCase("true"))
			{
				Document AddContainerDoc = (Document) yfsEnvironment.getTxnObject(KohlsConstant.O_ADD_CONTAINER_OUTPUT);
				
				log.debug("AddContainerDoc:" + XMLUtil.getXMLString(AddContainerDoc));
												
				AddContainerDoc = KOHLSGiftReceiptID.hidePriceInfo(AddContainerDoc);
				
				log.debug("hidePriceInfo(AddContainerDoc):" + XMLUtil.getXMLString(AddContainerDoc));
				
				Document PrintOutDoc = KOHLSBaseApi.invokeService(yfsEnvironment, KohlsConstant.SERVICE_PACK_SLIP_PRINT_SERVICE, AddContainerDoc);
				
				Document PrintPackDocs = XMLUtil.createDocument("PrintPackDocs");				
				KOHLSGiftReceiptID.appendPrintPackElement(yfsEnvironment, PrintOutDoc, output, PrintPackDocs, true);							
				
				ArrayList<String> alContainerDetailProcessed = new ArrayList<String>();
				ArrayList<String> alContainerDetailPrinted = new ArrayList<String>();
				
				yfsEnvironment.setTxnObject("alContainerDetailProcessed", alContainerDetailProcessed);
				yfsEnvironment.setTxnObject("alContainerDetailPrinted", alContainerDetailPrinted);	
				
				PrintOutDoc = KOHLSGiftReceiptID.printPackSlipForGiftLines(yfsEnvironment, AddContainerDoc, output, PrintPackDocs);
			}
		}
		
		log.debug("Just before returning :" + XMLUtil.getXMLString(output));
		
		return output;
	}

	private Document getDocInputForContainerTypeItemList(
			String strEnterpriseCode) throws ParserConfigurationException {

		Document docInputGetItemListForContainer = XMLUtil
		.createDocument(KohlsConstant.E_ITEM);
		Element eItemInput = docInputGetItemListForContainer
		.getDocumentElement();
		eItemInput.setAttribute(KohlsConstant.A_CALLING_ORGANIZATION_CODE,
				strEnterpriseCode);
		eItemInput.setAttribute(KohlsConstant.A_ITEM_GROUP_CODE,
				KohlsConstant.V_PROD);
		eItemInput.setAttribute(KohlsConstant.A_IS_SHIPPING_CONTAINER,
				KohlsConstant.FLAG_Y);
		return docInputGetItemListForContainer;
	}

	private Document getDocInputForShipmentDetails(
			String strShipmentNo, String strShipNode) throws ParserConfigurationException {
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSGetShipmentDetailsAPI" +
			" getDocInputForShipmentDetails method -- >");
		}
		Document docInputForShipmentDetails = XMLUtil
		.createDocument(KohlsConstant.E_SHIPMENT);
		Element eShipmentInput = docInputForShipmentDetails
		.getDocumentElement();
		eShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_NO,
				strShipmentNo);
		eShipmentInput.setAttribute(KohlsConstant.A_SHIP_NODE,
				strShipNode);
		
		//Use SellerOrganization code as KOHLS.COM
		eShipmentInput.setAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE,
				KohlsConstant.V_SELLER_ORGANIZATION_CODE);

		return docInputForShipmentDetails;
	}

	private void calculatedPlacedQuantity(Document docOuputShipmentDetails) {
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSGetShipmentDetailsAPI" +
					" calculatedPlacedQuantity method -- >"
					+ XMLUtil.getXMLString(docOuputShipmentDetails));
		}
		try {
			NodeList nlShipmentLineList = XPathUtil.getNodeList(
					docOuputShipmentDetails.getDocumentElement(),
			"/Shipment/ShipmentLines/ShipmentLine");

			if (!YFCObject.isVoid(nlShipmentLineList)
					&& nlShipmentLineList.getLength() > 0) {

				for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList
				.getLength(); intShipmentLineCount++) {

					double dShipmentLinePlacedQty = 0.00;
					Element eleShipmentLine = (Element) nlShipmentLineList
					.item(intShipmentLineCount);

					String strShipmentLineKey = eleShipmentLine
					.getAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY);
					NodeList nlContainerDetailList = XPathUtil
					.getNodeList(
							docOuputShipmentDetails
							.getDocumentElement(),
							"/Shipment/Containers/Container/ContainerDetails/ContainerDetail[@ShipmentLineKey='"
							+ strShipmentLineKey + "']");

					if (!YFCObject.isVoid(nlContainerDetailList)
							&& nlContainerDetailList.getLength() > 0) {
						for (int intCtnCount = 0; intCtnCount < nlContainerDetailList
						.getLength(); intCtnCount++) {

							Element eleContainerDetails = (Element) nlContainerDetailList
							.item(intCtnCount);
							double dCtnQty = XMLUtil.getDoubleAttribute(
									eleContainerDetails,
									KohlsConstant.A_QUANTITY);
							dShipmentLinePlacedQty = dShipmentLinePlacedQty
							+ dCtnQty;
						}

					}

					eleShipmentLine.setAttribute(
							KohlsConstant.A_PLACED_QUANTITY, Double
							.toString(dShipmentLinePlacedQty));

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method will generate the random container number for shipment
	 * packing.
	 * 
	 * @param env
	 *            It contains YFSEnvironment values
	 * @return String It contains container number
	 * @throws Exception
	 */
	private String getContainerNo(YFSEnvironment env) throws Exception {
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSGetShipmentDetailsAPI" +
			" getContainerNo method -- >");
		}
		Statement statement = null;
		ResultSet resultSet = null;
		YCPContext context = (YCPContext) env;
		Connection connection = context.getDBConnection();
		String strContainerNo = "";

		try {
			statement = connection.createStatement();
			resultSet = statement
			.executeQuery(KohlsConstant.SQL_STATEMENT_FOR_SEQ_YFS_CONTAINER_NO);
			resultSet.next();
			strContainerNo = resultSet.getString(KohlsConstant.A_CONTAINER_NO);
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

		return strContainerNo;
	}

}
