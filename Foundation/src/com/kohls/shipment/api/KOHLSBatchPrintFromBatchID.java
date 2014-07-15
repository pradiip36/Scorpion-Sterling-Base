package com.kohls.shipment.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * 
 * @author srikaheb
 * This is used to print the pickslip batches along with header. This does the GIV interface.
 */

public class KOHLSBatchPrintFromBatchID extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSBatchPrintFromBatchID.class.getName());

	Properties properties = null;
	private Properties props;

	/**
	 * @param properties
	 *            argument from configuration.
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	public Document printFromBatchID(YFSEnvironment env, Document inXML) throws Exception {

		Element inputElem = inXML.getDocumentElement();

		Document docOutput = XMLUtil.createDocument("ApiSuccess");
		Element eleDocOutput = docOutput.getDocumentElement();

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KOHLSBatchPrintFromBatchID: printFromBatchID: Input XML: " + XMLUtil.getXMLString(inXML));
		}

		if (!YFCObject.isVoid(inXML)) {

			String strBatchId = inputElem.getAttribute(KohlsConstant.A_BATCH_ID);
			String storeId= inputElem.getAttribute(KohlsConstant.A_STORE);
			String nodeType= inputElem.getAttribute(KohlsConstant.A_NODE_TYPE);
			String strPrinterID = inputElem.getAttribute(KohlsConstant.A_PRINTER_ID);

			if (YFCObject.isVoid(strBatchId)) {
				eleDocOutput.setAttribute("Result", "0");
				eleDocOutput.setAttribute("Description", "Mandatory Parameters are missing from the Input");

				YFSException ex = new YFSException();
				ex.setErrorCode("Mandatory Parameters are missing from the Input");
				ex.setErrorDescription("BatchID must be passed in the input");
				throw ex;
			} else {
				eleDocOutput.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
//				eleDocOutput.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
				
				String strCartNo="";
				String strStartCartNo="1";
				String strEndCartNo=inputElem.getAttribute(KohlsConstant.A_NO_OF_SHIPMENT);
				
				if (inputElem.getAttribute("Option").equals("2")) {
					strCartNo = inputElem.getAttribute(KohlsConstant.A_CART_NUMBER);
					strStartCartNo = strCartNo;
					strEndCartNo = strCartNo;
				} else if (inputElem.getAttribute("Option").equals("3")) {
					strStartCartNo = inputElem.getAttribute(KohlsConstant.A_FROM_CART_NO);
					strEndCartNo = inputElem.getAttribute(KohlsConstant.A_TO_CART_NO);
				}

				// Preparing input for getShipmentList API
				Document getShipmentListInputDoc = createInputForGetShipmentList(strBatchId);

				// Calling the getShipmentList API with the input prepared
				Document getShipmentListOutputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_SHIPMENT_LIST_FOR_BATCH_PRINT, 
						KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);

				Element elemGetShipmentListOut = getShipmentListOutputDoc.getDocumentElement();

				String strTotalNoOfRecords = elemGetShipmentListOut.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS);
				
				if (!YFCObject.isVoid(strTotalNoOfRecords) && Integer.parseInt(strTotalNoOfRecords) > 0) {
					
					Document getAscShipmentListOutputDoc = KOHLSBaseApi.invokeService(env, 
							KohlsConstant.SERVICE_KOHLS_SORT_SHIPMENT_BY_KEY, getShipmentListOutputDoc);
					
//					System.out.println("getSelShipmentListOutputDoc: " + XMLUtil.getXMLString(getAscShipmentListOutputDoc));
					getAscShipmentListOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_FROM, strStartCartNo);
					getAscShipmentListOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_TO, strEndCartNo);
					
					Document getSelShipmentListOutputDoc=KOHLSBaseApi.invokeService(env, 
							KohlsConstant.SERVICE_KOHLS_EXTRACT_SELECTED_SHIPMENTS, getAscShipmentListOutputDoc);
					
					getSelShipmentListOutputDoc=stampUPCCode(env,getSelShipmentListOutputDoc,storeId);
//					System.out.println("getSelShipmentListOutputDoc post upc code : " + XMLUtil.getXMLString(getSelShipmentListOutputDoc));
					
					if (nodeType.equalsIgnoreCase(KohlsConstant.ATTR_STORE)) {
						Document getGroupTotalOutputDoc=handleStoreBatchPrints(env, getSelShipmentListOutputDoc,
							 strBatchId,  strStartCartNo,  strEndCartNo);
					
	//					System.out.println(" getGroupTotalOutputDoc : " + XMLUtil.getXMLString(getGroupTotalOutputDoc));
						return getGroupTotalOutputDoc;
					} else if (nodeType.equalsIgnoreCase(KohlsConstant.A_SINGLES_PRINT)){
						Document getSingleBatchOutputDoc=handleSingleBatchPrints(env, getSelShipmentListOutputDoc,
								 strBatchId,  strStartCartNo,  strEndCartNo, strPrinterID);
						Document getKohlsPackslipOutputDoc = KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_DATA_FOR_PACK_SLIP_FORMAT, getSingleBatchOutputDoc);
						return getKohlsPackslipOutputDoc;
					}
				} else if (!YFCObject.isVoid(strTotalNoOfRecords) && strTotalNoOfRecords.equals("0")) {
					if(strCartNo.equalsIgnoreCase("")){
						eleDocOutput.setAttribute("Result", "0");
						eleDocOutput.setAttribute("Description", "No Shipments returned for the BatchID");
						return docOutput;
					} else {
						eleDocOutput.setAttribute("Result", "01");
						eleDocOutput.setAttribute("Description", "No Shipments returned for the BatchID and Cart Number");
						return docOutput;
					}
				}
			}
		}

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("KOHLSBatchPrintFromBatchID: PrintBatchID: Output XML: " + XMLUtil.getXMLString(docOutput));
		}

		return docOutput;
	}

	private Document handleSingleBatchPrints(YFSEnvironment env, Document getSelShipmentListOutputDoc,
			String strBatchId, String strStartCartNo, String strEndCartNo, String strPrinterID) throws Exception {
		try {
			
			Document getSingleBatchOutputDoc=XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
			NodeList nodeShipmentList = getSelShipmentListOutputDoc.getDocumentElement()
								.getElementsByTagName(KohlsConstant.E_SHIPMENT);
			int iNumShipments = nodeShipmentList.getLength();

			if (KohlsConstant.V_ZERO != iNumShipments) {

				for (int i = 0; i < iNumShipments; i++) {
					Element eleShip = (Element) nodeShipmentList.item(i);
					String strShipmentKey = eleShip.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
					Document docInService = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
					Element eleIndocService = docInService.getDocumentElement();
					eleIndocService.setAttribute(KohlsConstant.A_SHIPMENT_KEY,strShipmentKey);
					eleIndocService.setAttribute(KohlsConstant.A_PRINTER_ID,strPrinterID);
//					eleIndocService.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET,	KohlsConstant.V_N);
					eleIndocService.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET,	KohlsConstant.V_Y);
					eleIndocService.setAttribute(KohlsConstant.A_UPC_BARCODE_PICK_TICKET,KohlsConstant.V_Y);
//					eleIndocService.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT,KohlsConstant.V_Y);
					//To force call the servcie this is passed as N
					eleIndocService.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT,KohlsConstant.V_N);
					if(!YFCObject.isVoid(strBatchId)) {
						eleIndocService.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
					}
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("Input for getShipmentList : \n"	+ XMLUtil.getXMLString(docInService));
					}
					
					// call the service
					Document docGetShipmentListOutput = KOHLSBaseApi.invokeService(env,
							KohlsConstant.API_REPRINT_PICK_SLIP_SERVICE, docInService);

					if (YFCLogUtil.isDebugEnabled()&& !YFCObject.isVoid(docGetShipmentListOutput)) {
						log.debug("Output for getShipmentList : \n"	+ XMLUtil.getXMLString(docGetShipmentListOutput));
					}
					getSingleBatchOutputDoc=XMLUtil.addDocument(getSingleBatchOutputDoc, docGetShipmentListOutput, false);
					getSingleBatchOutputDoc.getDocumentElement().setAttribute("Result", "1");
					getSingleBatchOutputDoc.getDocumentElement().setAttribute("Description", "Success");
					getSingleBatchOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_BATCH_ID,strBatchId );
				}// End of for loop
			}// End of nodelist blank if loop
		
			return getSingleBatchOutputDoc;
		} catch (Exception ex){
			throw ex;
		}
//		return null;
	}
	private Document handleStoreBatchPrints(YFSEnvironment env, Document getSelShipmentListOutputDoc,
			String strBatchId, String strStartCartNo, String strEndCartNo)  throws Exception {
		try {
			
			String yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_GIV_LOC_INV_SUPPLY);
			String yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_GIV_LOC_INV_SUPPLY);
			
			getSelShipmentListOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_USER_ID, yfsEndPointUser);
			getSelShipmentListOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_PASSWORD, yfsEndPointPassword);
			
			Document getGIVInputDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_MAKE_GIV_STORE_LOCATION_INPUT, getSelShipmentListOutputDoc);
			
//			Document getGIVInputDoc=XMLUtil.getDocument("c:/temp/givinput.xml");
			Document getGIVResponseDoc=null;
			//Call GIV interface and get the response 
			try {
				getGIVResponseDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_GIV_LOCATION_INVENROTY_SUPPLY_WEBSERVICE, getGIVInputDoc);
			} catch (Exception spex){
				getGIVResponseDoc=XMLUtil.createDocument(KohlsConstant.A_GET_LOCATION_NS3);
				getGIVResponseDoc.getDocumentElement().setAttribute(KohlsConstant.A_NS3,KohlsConstant.A_NS3_VALUE);
				getGIVResponseDoc.getDocumentElement().setAttribute(KohlsConstant.A_NS4,KohlsConstant.A_NS4_VALUE);
			}
//			System.out.println("getGIVResponseDoc: " + XMLUtil.getXMLString(getGIVResponseDoc));
//			Document getGIVResponseDoc=XMLUtil.getDocument(simulateGIVoutput(env, getGIVInputDoc));
//			getGIVResponseDoc=XMLUtil.getDocument("c:/temp/givresponse.xml");
			getGIVResponseDoc=appendUPCCodetoGIVOutput(env, getGIVInputDoc, getGIVResponseDoc) ;
			Document compoundDoc=XMLUtil.addDocument(getSelShipmentListOutputDoc, getGIVResponseDoc, false);
			
			Document getKesyAddedOutputDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_ADD_KEYS_TO_LINELIST, compoundDoc);
	//		System.out.println("getKesyAddedOutputDoc: " + XMLUtil.getXMLString(getKesyAddedOutputDoc));
			
			Document getItemLocationOutputDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_DETERMINE_ITEM_LOCATION, getKesyAddedOutputDoc);
	//		System.out.println(" getItemLocationOutputDoc : " + XMLUtil.getXMLString(getItemLocationOutputDoc));
			
			Document getRecreateItemShipListOutputDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_RECREATE_SHIPMENT, getItemLocationOutputDoc);
			
			Document getGroupTotalOutputDoc=KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_GROUP_AND_TOTAL, getRecreateItemShipListOutputDoc);
//			System.out.println(" getGroupTotalOutputDoc : " + XMLUtil.getXMLString(getGroupTotalOutputDoc));
			
			Document outTemplate=XMLUtil.createDocument(KohlsConstant.A_PICKSLIP);
			getRecreateItemShipListOutputDoc=XMLUtil.addDocument(outTemplate, getRecreateItemShipListOutputDoc, false);
			getGroupTotalOutputDoc=XMLUtil.addDocument(getGroupTotalOutputDoc, getRecreateItemShipListOutputDoc, false);
			
	//		eleDocOutput.setAttribute("Result", "1");
	//		eleDocOutput.setAttribute("Description", "Success");
			getGroupTotalOutputDoc.getDocumentElement().setAttribute("Result", "1");
			getGroupTotalOutputDoc.getDocumentElement().setAttribute("Description", "Success");
			getGroupTotalOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_BATCH_ID,strBatchId );
			getGroupTotalOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_FROM,strStartCartNo );
			getGroupTotalOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_TO,strEndCartNo );
			
			/* Fix for sorting pick slip pdf shipments according to ShipmentKey START */
			getGroupTotalOutputDoc = KOHLSBaseApi.invokeService(env, 
					KohlsConstant.SERVICE_KOHLS_SORT_PICK_SLIP_BY_SHIPMENT_KEY, getGroupTotalOutputDoc);
			/* Fix for sorting pick slip pdf shipments according to ShipmentKey END */
			
			return getGroupTotalOutputDoc;
		} catch (Exception ex){
			ex.printStackTrace();
			throw ex;
		}
//		return null;
	}
	
	private Document createInputForGetShipmentList(String strBatchId) throws ParserConfigurationException {
		Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element ShipmentElem = docShipmentListInput.getDocumentElement();
		
		Element elesls = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_SHIPMENT_LINES);
		Element elesl = XMLUtil.createChild(elesls, KohlsConstant.E_SHIPMENT_LINE);
		
		Element eleBatch = XMLUtil.createChild(elesl, KohlsConstant.E_EXTN);
		eleBatch.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, strBatchId);

		return docShipmentListInput;
	}
	
	private Document stampUPCCode(YFSEnvironment env, Document inputDoc, String storeId) throws Exception {
		Element eleInput = inputDoc.getDocumentElement();
		
		String selOrg = ((Element)eleInput.getElementsByTagName(KohlsConstant.E_SHIPMENT).item(0))
						.getAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE);
		
		NodeList nlItemList = eleInput.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
		
		for (int intItemCount = 0; intItemCount < nlItemList.getLength(); intItemCount++) {
			Element eleItem = (Element) nlItemList.item(intItemCount);
			
			Document itDoc=XMLUtil.createDocument(KohlsConstant.E_ITEM);
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_ITEM_ID, eleItem.getAttribute(KohlsConstant.A_ITEM_ID));
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_ORGANIZATION_CODE, selOrg);
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, eleItem.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE));
			
			Document getItemDetailOutputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_UPC_CODE_FOR_BATCH_PRINT, 
					KohlsConstant.API_GET_ITEM_DETAILS, itDoc);
			
			String orderDate ="";
			try {
				orderDate = ((Element)eleItem.getElementsByTagName(KohlsConstant.ELEM_ORDER).item(0)).
								getAttribute(KohlsConstant.A_ORDER_DATE);
				
				SimpleDateFormat dtFormatter = new SimpleDateFormat("yyyyMMdd");//Date format 
	    		 if (orderDate!=null && orderDate.length() >0){
	    			 Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(orderDate);
	    			 orderDate=dtFormatter.format(date);
	    		 }
			} catch(Exception ex){
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("KOHLSBatchPrintFromBatchID: stampUPCCode -> getItemDetails exception: " + ex.getMessage());
				}
			}
			eleItem.setAttribute(KohlsConstant.A_ORDER_DATE, orderDate);
			String unitPrice ="";
			try {
				unitPrice = ((Element)eleItem.getElementsByTagName(KohlsConstant.A_LINE_PRICE_INFO).item(0)).
									getAttribute(KohlsConstant.A_UNIT_PRICE);
			} catch(Exception ex){
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("KOHLSBatchPrintFromBatchID: stampUPCCode -> getLinePriceInfo exception: " + ex.getMessage());
				}
			}
			eleItem.setAttribute(KohlsConstant.A_UNIT_PRICE, unitPrice);
			
			//String efcStr=KohlsUtil.getEFCForStore(storeId, env);
			String receiptId=" ";
			
			NodeList notesList = eleInput.getElementsByTagName(KohlsConstant.A_NOTE);
			//
			String orderHdrKey ="";
			try {
				orderHdrKey = ((Element)eleItem.getElementsByTagName(KohlsConstant.ELEM_ORDER).item(0)).
								getAttribute(KohlsConstant.ATTR_ORD_HDR_KEY);
			} catch(Exception ex){
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("KOHLSBatchPrintFromBatchID: stampUPCCode -> orderHeaderKey exception: " + ex.getMessage());
				}
			}
			
			
			for (int ntCnt = 0; ntCnt < notesList.getLength(); ntCnt++) {
				Element notEle = (Element) notesList.item(ntCnt);
				if (orderHdrKey.equalsIgnoreCase(notEle.getAttribute(KohlsConstant.A_TABLE_KEY)) 
							&& notEle.getAttribute(KohlsConstant.A_SEQUENCE_KEY).equals("1")) {
					receiptId=notEle.getAttribute(KohlsConstant.A_NOTE_TEXT);
				}
			}
			eleItem.setAttribute(KohlsConstant.A_RECEIPTID, receiptId);
			
			Element primaryInfo = (Element) getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_PRIMARY_INFORMATION).item(0);
			eleItem.setAttribute(KohlsConstant.A_VENDOR, getVendor(env, primaryInfo.getAttribute(KohlsConstant.A_PRIMARY_SUPPLIER)));
			try {
				Element extnDept=(Element) getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_EXTN).item(0);
				eleItem.setAttribute(KohlsConstant.A_DEPT, getDepartment(env, extnDept.getAttribute(KohlsConstants.EXTN_DEPT)));
			} catch(Exception ex){
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("KOHLSBatchPrintFromBatchID: stampUPCCode -> getLinePriceInfo exception: " + ex.getMessage());
				}
				eleItem.setAttribute(KohlsConstant.A_DEPT, "");
			}
			
			NodeList nlItemAliasList = getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_ITEM_ALIAS);
			for (int intItemAliasCount = 0; intItemAliasCount < nlItemAliasList.getLength(); intItemAliasCount++) {
				Element eleItemAlias = (Element) nlItemAliasList.item(intItemAliasCount);
				String sAliasName = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_NAME);
				if(!YFCObject.isVoid(sAliasName) && sAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01)) {
					// invoke getValidUPCCode(String strUPCCode) for a 12 character String value for the UPCCode
					eleItem.setAttribute(sAliasName, getValidUPCCode(eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_VALUE)));
				}
			}
			
			String taxPct="";
			String taxSymbl="";
			try {
				KOHLSPopulateTaxRepackShip kohlsGetTax=new KOHLSPopulateTaxRepackShip();
				Document shipmentsDoc=XMLUtil.createDocument(nlItemList.item(intItemCount).getParentNode());
				Document taxRetrDoc=kohlsGetTax.populateTaxRepack(env, shipmentsDoc);
				Element taxEle=(Element)taxRetrDoc.getElementsByTagName(KohlsConstant.E_POPULATE_TAX).item(0);
				taxPct=taxEle.getAttribute(KohlsConstant.ATTR_TAX_PERCENT);
				taxSymbl=taxEle.getAttribute(KohlsConstant.A_TAX_SYMBOL);
			} catch (Exception ex){
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("KOHLSBatchPrintFromBatchID: stampUPCCode -> Tax computation exception: " + ex.getMessage());
				}
			}
			eleItem.setAttribute(KohlsConstant.ATTR_TAX_PERCENT, taxPct);
			eleItem.setAttribute(KohlsConstant.A_TAX_SYMBOL, taxSymbl);
		}
		return inputDoc;
	}
	
	
	/**
	 * This method takes the UPCCode ("AliasValue" of the corresponding Item "AliasName") as an argument and
	 * if the length of the String value is less than 12 then,
	 * it adds zeros at the begining of the String to make it a 12 character String value and return.
	 * 
	 * @param strUPCCode ("AliasValue" of the corresponding Item "AliasName")
	 * @return strUPCCode (a 12 character String value)
	 * 
	 */
	private String getValidUPCCode(String strUPCCode) {
		try {
			int iUPCCodeLength = strUPCCode.length();
			if (iUPCCodeLength < 12){
				for (int count = 0; count < 12 - iUPCCodeLength; count++) {
					strUPCCode = "0" + strUPCCode;
				}
			}
			return strUPCCode;
		} catch (Exception ex) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("KOHLSBatchPrintFromBatchID: getValidUPCCode exception: " + ex.getMessage());
			}
			return "";
		}
	}
	
	private String getDepartment(YFSEnvironment env, String dept) throws Exception {
		try {
			Document itDoc=XMLUtil.createDocument(KohlsConstants.CATEGORY);
			itDoc.getDocumentElement().setAttribute(KohlsConstants.CATEGORY_ID, dept);
			Document getItemDetailOutputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_DEPT_NAME_FOR_BATCH_PRINT, 
					KohlsConstants.GET_CATEGORY_LIST, itDoc);
			Element primaryInfo = (Element) getItemDetailOutputDoc.getElementsByTagName(KohlsConstants.CATEGORY).item(0);
			return primaryInfo.getAttribute(KohlsConstant.A_SHORT_DESCRIPTION);
		} catch (Exception ex){
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("KOHLSBatchPrintFromBatchID: getDepartment exception: " + ex.getMessage());
			}
			return "";
		}
	}
	
	private String getVendor(YFSEnvironment env, String vendorCd) throws Exception {
		try {
			Document itDoc=XMLUtil.createDocument(KohlsConstant.A_ORGANIZATION);
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_ORGANIZATION_CODE, vendorCd);
			
			Document outTemplate=XMLUtil.createDocument(KohlsConstant.A_ORG_LIST);
			Element orgEle=XMLUtil.createChild(outTemplate.getDocumentElement(), KohlsConstant.A_ORGANIZATION);
			orgEle.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, "");
			orgEle.setAttribute(KohlsConstant.A_ORG_NAME, "");
			Document getItemDetailOutputDoc = KohlsCommonUtil.invokeAPI(env, outTemplate, KohlsConstant.API_GET_ORGANIZATION_LIST, itDoc);
			Element primaryInfo = (Element) getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_ORGANIZATION).item(0);
			return primaryInfo.getAttribute(KohlsConstant.A_ORG_NAME);
		} catch (Exception ex) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("KOHLSBatchPrintFromBatchID - getVendor: exception: " + ex.getMessage());
			}
			return "";
		}
	}

private Document appendUPCCodetoGIVOutput(YFSEnvironment env, Document getGIVInputDoc, Document getGIVResponseDoc) throws Exception {
		
		Document newRespDoc=XMLUtil.createDocument(KohlsConstant.A_GET_LOCATION_NS3);
		NamedNodeMap nlm=getGIVResponseDoc.getDocumentElement().getAttributes();
		for (int i=0; i<nlm.getLength();i++){
			newRespDoc.getDocumentElement().setAttribute(nlm.item(i).getNodeName(),nlm.item(i).getNodeValue());
		}
		if (newRespDoc.getDocumentElement().getAttribute(KohlsConstant.A_NS4).isEmpty()){
			newRespDoc.getDocumentElement().setAttribute(KohlsConstant.A_NS4,KohlsConstant.A_NS4_VALUE);
		}
//		if (newRespDoc.getDocumentElement().getAttribute("xmlns:ns2").isEmpty()){
//			newRespDoc.getDocumentElement().setAttribute("xmlns:ns2",
//			"http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input");
//		}
		Element returnEle=XMLUtil.createChild(newRespDoc.getDocumentElement(), KohlsConstant.A_RETURN);
		
//		NodeList nlItemList = getGIVResponseDoc.getElementsByTagName(KohlsConstant.E_ITEM);
		NodeList nReqpList = getGIVInputDoc.getElementsByTagName(KohlsConstant.A_INP_INV_SUPPLY);
		NodeList nRespList = getGIVResponseDoc.getElementsByTagName(KohlsConstant.A_NS4_ITEM);
		
		for (int intReqCount = 0; intReqCount < nReqpList.getLength(); intReqCount++) {
			Element eleReqItem = (Element) nReqpList.item(intReqCount);
			boolean found=false;
			for (int intItemCount = 0; intItemCount < nRespList.getLength(); intItemCount++) {
				
				Element eleItem = (Element) nRespList.item(intItemCount);
				if (eleReqItem.getAttribute(KohlsConstant.A_ITEM_ID).equalsIgnoreCase(eleItem.getAttribute(KohlsConstant.A_ITEM_ID))){
					found=true;
					Element itemEle=XMLUtil.createChild(returnEle, KohlsConstant.A_NS4_ITEM);
					XMLUtil.copyElement(newRespDoc, eleItem, itemEle);
				}
			}
			if (!found){
				Element itemEle=XMLUtil.createChild(returnEle, KohlsConstant.A_NS4_ITEM);
				itemEle.setAttribute(KohlsConstant.A_ITEM_ID, eleReqItem.getAttribute(KohlsConstant.A_ITEM_ID));
				itemEle.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, eleReqItem.getAttribute(KohlsConstant.A_ORGANIZATION_CODE));
				itemEle.setAttribute(KohlsConstant.A_PRODUCT_CLASS, eleReqItem.getAttribute(KohlsConstant.A_PRODUCT_CLASS));
				itemEle.setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, eleReqItem.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE));
				Element suppEle=XMLUtil.createChild(itemEle, KohlsConstant.A_NS4_SUPPLIES);
				Element invStkSuppEle=XMLUtil.createChild(suppEle, KohlsConstant.A_NS4_INV_SUPPLY);
				invStkSuppEle.setAttribute(KohlsConstant.A_QUANTITY, "0");
				invStkSuppEle.setAttribute(KohlsConstant.A_SUPPLY_TYPE, KohlsConstant.A_STOCK_ROOM_EX);
				Element invFlSuppEle=XMLUtil.createChild(suppEle, KohlsConstant.A_NS4_INV_SUPPLY);
				invFlSuppEle.setAttribute(KohlsConstant.A_QUANTITY, "0");
				invFlSuppEle.setAttribute(KohlsConstant.A_SUPPLY_TYPE, KohlsConstant.A_SALES_FLOOR_EX);
				returnEle.appendChild(itemEle);
			}
		}
		
		NodeList nlItemList = newRespDoc.getElementsByTagName(KohlsConstant.A_NS4_ITEM);
		for (int intItemCount = 0; intItemCount < nlItemList.getLength(); intItemCount++) {
			Element eleItem = (Element) nlItemList.item(intItemCount);
			
			Document itDoc=XMLUtil.createDocument(KohlsConstant.E_ITEM);
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_ITEM_ID, eleItem.getAttribute(KohlsConstant.A_ITEM_ID));
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_ORGANIZATION_CODE, eleItem.getAttribute(KohlsConstant.A_ORGANIZATION_CODE));
			itDoc.getDocumentElement().setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, eleItem.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE));
			
			Document getItemDetailOutputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_UPC_CODE_FOR_BATCH_PRINT, 
					KohlsConstant.API_GET_ITEM_DETAILS, itDoc);
		
			NodeList nlItemAliasList = getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_ITEM_ALIAS);
			for (int intItemAliasCount = 0; intItemAliasCount < nlItemAliasList.getLength(); intItemAliasCount++) {
				Element eleItemAlias = (Element) nlItemAliasList.item(intItemAliasCount);
				String sAliasName = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_NAME);
				if(!YFCObject.isVoid(sAliasName) && sAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01)) {
					// invoke getValidUPCCode(String strUPCCode) for a 12 character String value for the UPCCode
					eleItem.setAttribute(sAliasName, getValidUPCCode(eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_VALUE)));
				}
			}
		}
//		System.out.println("newRespDoc *** " + XMLUtil.getXMLString(newRespDoc));
		return newRespDoc;
	}
	
}
