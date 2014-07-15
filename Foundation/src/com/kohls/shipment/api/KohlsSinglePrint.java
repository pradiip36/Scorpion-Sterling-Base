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
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

	public class KohlsSinglePrint extends KOHLSBaseApi {

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

		public Document printSingleShipment(YFSEnvironment env, Document inXML) throws Exception {

			Element inputElem = inXML.getDocumentElement();
			String storeId= inputElem.getAttribute(KohlsConstant.ATTR_SHIP_NODE);

			Document docOutput = XMLUtil.createDocument("ApiSuccess");

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("KOHLSBatchPrintFromBatchID: printFromBatchID: Input XML: " + XMLUtil.getXMLString(inXML));
			}

			if (!YFCObject.isVoid(inXML)) {
				Document docInput =  XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
				XMLUtil.addDocument(docInput, inputElem.getOwnerDocument(), false);
				
				Document getSelShipmentListOutputDoc=stampUPCCode(env,docInput,storeId);
						
				/*
				 * Input to the KohlsMakeGIVStoreLocationInput should have the <Shipments> as the root node. 
				 */
				
						/*Document getGIVInputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_MAKE_GIV_STORE_LOCATION_INPUT, inXML);*/
						
						String yfsEndPointUser = YFSSystem.getProperty(KohlsConstant.END_POINT_USER_GIV_LOC_INV_SUPPLY);
						String yfsEndPointPassword = YFSSystem.getProperty(KohlsConstant.END_POINT_PWD_GIV_LOC_INV_SUPPLY);
						
						docInput.getDocumentElement().setAttribute(KohlsConstant.A_USER_ID, yfsEndPointUser);
						docInput.getDocumentElement().setAttribute(KohlsConstant.A_PASSWORD, yfsEndPointPassword);
				
						Document getGIVInputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_MAKE_GIV_STORE_LOCATION_INPUT, docInput);

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
						
						getGIVResponseDoc=appendUPCCodetoGIVOutput(env, getGIVInputDoc, getGIVResponseDoc) ;
						
						Document compoundDoc=XMLUtil.addDocument(getSelShipmentListOutputDoc, getGIVResponseDoc, false);
						
						Document getKesyAddedOutputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_ADD_KEYS_TO_LINELIST, compoundDoc);
						
//						Document getKesyAddedOutputDoc=KOHLSBaseApi.invokeService(env, 
//								KohlsConstant.SERVICE_KOHLS_ADD_KEYS_TO_LINELIST, docInput);
						
//						docInput =  XMLUtil.createDocument("Shipments");
//						XMLUtil.addDocument(docInput, getKesyAddedOutputDoc, false);
						
						Document getItemLocationOutputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_DETERMINE_ITEM_LOCATION, getKesyAddedOutputDoc);
						
						Document getRecreateItemShipListOutputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_RECREATE_SHIPMENT, getItemLocationOutputDoc);
						
						Document getGroupTotalOutputDoc=KOHLSBaseApi.invokeService(env, 
								KohlsConstant.SERVICE_KOHLS_GROUP_AND_TOTAL, getRecreateItemShipListOutputDoc);
					
						Document outTemplate=XMLUtil.createDocument(KohlsConstant.A_PICKSLIP);
						getRecreateItemShipListOutputDoc=XMLUtil.addDocument(outTemplate, getRecreateItemShipListOutputDoc, false);
						getGroupTotalOutputDoc=XMLUtil.addDocument(getGroupTotalOutputDoc, getRecreateItemShipListOutputDoc, false);
						
						getGroupTotalOutputDoc.getDocumentElement().setAttribute("Result", "1");
						getGroupTotalOutputDoc.getDocumentElement().setAttribute("Description", "Success");
						getGroupTotalOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_FROM,"1" );
						getGroupTotalOutputDoc.getDocumentElement().setAttribute(KohlsConstant.A_TO,"1" );

//						System.out.println(" getGroupTotalOutputDoc : " + XMLUtil.getXMLString(getGroupTotalOutputDoc));
						return getGroupTotalOutputDoc;
					} 
			

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("KOHLSBatchPrintFromBatchID: PrintBatchID: Output XML: " + XMLUtil.getXMLString(docOutput));
			}

			return docOutput;
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
//				String efcStr=KohlsUtil.getEFCForStore(storeId, env);
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
				Element extnDept=(Element) getItemDetailOutputDoc.getElementsByTagName(KohlsConstant.A_EXTN).item(0);
				eleItem.setAttribute(KohlsConstant.A_DEPT, getDepartment(env, extnDept.getAttribute(KohlsConstants.EXTN_DEPT)));
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
		private String getValidUPCCode(String strUPCCode){
			int iUPCCodeLength = strUPCCode.length();
			if (iUPCCodeLength < 12){
				for (int count = 0; count < 12 - iUPCCodeLength; count++) {
					strUPCCode = "0" + strUPCCode;
				}
			}
			return strUPCCode;
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
				return " ";
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
				return " ";
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
//			if (newRespDoc.getDocumentElement().getAttribute("xmlns:ns2").isEmpty()){
//				newRespDoc.getDocumentElement().setAttribute("xmlns:ns2",
//				"http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input");
//			}
			Element returnEle=XMLUtil.createChild(newRespDoc.getDocumentElement(), KohlsConstant.A_RETURN);
			
//			NodeList nlItemList = getGIVResponseDoc.getElementsByTagName(KohlsConstant.E_ITEM);
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
			return newRespDoc;
		}
		
	}



