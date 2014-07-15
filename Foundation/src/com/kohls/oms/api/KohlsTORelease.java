package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
import com.custom.util.XPathUtil;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.oms.agent.KohlsSendTOReleaseToWMoSAgent;
import com.kohls.oms.agent.KohlsSendTOReleaseToWMoSAgentXmls;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class have logic to generate the pickticket # and It has logic where it
 * checks If the receiving node is exists in the list of nodes retrieved by the
 * common code then the first digit of the PickTicket # will be replaced with
 * "X"else the first digit of the PickTicket # will be replaced with "C" logic
 * to Call changeOrderRelease to stamp the PickTicket # in ExtnPickTicketNo it
 * also Invoke the service KohlsSendReleaseOrderToWMoSSyncService to send the
 * release information to WMoS.
 * 
 * @author OASIS
 * Added for 69773,379,000 -- OASIS_SUPPORT 8/6/2013 
 * Added for Inventory transfer Management .
 * 
 */
public class KohlsTORelease {
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendTOReleaseToWMoSAgent.class.getName());
	private YIFApi api;
	private Connection m_conn;

	public KohlsTORelease() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void invoke(YFSEnvironment env, Document inXML) throws Exception
		{
		//System.out.print("<!-------- Begining of KohlsTORelease invoke method ----------- >" + XMLUtil.getXMLString(inXML));
		
		int iCount = 0;
		String strIsHazardous="N";
		String strShipVia="";
		String strExIsPOBox="";
		String strExIsMilitary="";
		String strCartonType = KohlsConstant.CARTON_TYPE_BOX;// changed to BOX for PMR :05616,379,000
		String strLineType = "";
		String strGiftwrap = "";
		String strExtnRG = "";
		String strProdLine = "";
		int strAccQty = 0;
		String strExtnWrapCode = "";
		String sPickTicketNo = "";
		
		Element eleGetOrderReleaseDetails = inXML.getDocumentElement();
		
		Document docWMoSOrderReleaseXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE).getDocument();
		
		Element eleWMoSOrderReleaseXML = docWMoSOrderReleaseXML.getDocumentElement();
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_BILL_TO_ID,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_BILL_TO_ID));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_BUYER_ORGANIZATION_CODE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DIVISION,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DIVISION));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_RECEIVING_NODE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RECEIVING_NODE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SALES_ORDER_NO));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_NAME,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_NAME));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
		
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_RELEASE_NO,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RELEASE_NO));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE));
		eleWMoSOrderReleaseXML.setAttribute(KohlsXMLLiterals.A_SHIPNODE,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
		

		// Order Broker start
		Element elePersonInfoShipTo = (Element)docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails,
				KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO), true);
		Element eleInfoShExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		elePersonInfoShipTo.removeChild(eleInfoShExtn);
		eleWMoSOrderReleaseXML.appendChild(elePersonInfoShipTo);

		Element eleOrderDs = (Element)docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails,
				KohlsXMLLiterals.XP_ORDERRELEASE_ORDER), true);
		Element eleInfoBill = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO).item(0);
		Element eleOrExtn = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		Element eleOrdNotes = (Element)eleOrderDs.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
		eleOrderDs.removeChild(eleInfoBill);
		eleOrderDs.removeChild(eleOrExtn);
		eleOrderDs.removeChild(eleOrdNotes);
		eleWMoSOrderReleaseXML.appendChild(eleOrderDs);

		Node ndOrderLines = docWMoSOrderReleaseXML.importNode(XPathUtil.getNode((Node) eleGetOrderReleaseDetails,
				KohlsXMLLiterals.XP_ORDERRELEASE_ORDERLINES), false);
		eleWMoSOrderReleaseXML.appendChild(ndOrderLines);
		
		Element eleInfoShipTo = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO).item(0);

		Element eleExtnInfoShipTo = (Element) eleInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		strExIsPOBox = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		strExIsMilitary = eleExtnInfoShipTo.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);

		Element eleExtn = docWMoSOrderReleaseXML.createElement(KohlsXMLLiterals.E_EXTN);
		eleWMoSOrderReleaseXML.appendChild(eleExtn);
		Element elePersonInfo = docWMoSOrderReleaseXML.createElement(KohlsXMLLiterals.E_PERSON_INFO_BILL_TO);
		eleWMoSOrderReleaseXML.appendChild(elePersonInfo);
		Element eleOrderDtl = (Element) eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		Element eleExtnOrderDtl = (Element) eleOrderDtl.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		
		String sReceivingNode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_RECEIVING_NODE);
		this.m_conn = KohlsUtil.getDBConnection(env);
		if (0 == iCount) {
			sPickTicketNo = StringUtil.prepadStringWithZeros(KohlsUtil.getNextSeqNo(env, this.m_conn), KohlsConstant.PICKTICKET_LEN);
			
			log.debug(" Pickticket  : " + sPickTicketNo);
			if(isReceivingNodeWareHouse(env, sReceivingNode)){
				
				sPickTicketNo = "X"+sPickTicketNo.substring(1);
				
				
			}else{
				log.debug("Inside else  : ");
				sPickTicketNo = "C"+sPickTicketNo.substring(1);
			}
			log.debug("Generating Pickticket  : " + sPickTicketNo);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("PickTicket No  : " + sPickTicketNo);
			}
		}

		
		iCount++;
		NodeList ndlstOrderLines = eleGetOrderReleaseDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for (int i = 0; i < ndlstOrderLines.getLength(); i++) {
			
			Element eleOrderLine = (Element) ndlstOrderLines.item(i);
			String sCarrierServiceCodeLine = eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
			String sQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_STATUS_QUANTITY);
			
			if(!sQty.equals(KohlsConstant.BLANK))
			strAccQty += (int)Float.parseFloat(sQty);


			Element eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
			String sItemId = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			
			env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, getItemListTemplate());
			Document docGetItemList = this.api.getItemList(env, getItemListInputXML(env, sItemId));
			env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

			//System.out.println("GetItemList OUTPUT::::"+XMLUtil.getXMLString(docGetItemList));

			Element eleGetItemDetails = docGetItemList.getDocumentElement();

			NodeList ndItLst = eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM);

			if(ndItLst.getLength()==0){
				YFSException ex = new YFSException();
				ex.setErrorCode("Item not found");
				ex.setErrorDescription("Catalog: "+sItemId+" does not exist in the system");
				throw ex;
			}
			Element elePrimaryInfo = (Element) docGetItemList.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
		
			String sIsHazMat = elePrimaryInfo.getAttribute(KohlsXMLLiterals.A_IS_HAZMAT);
			if (KohlsConstant.YES.equalsIgnoreCase(sIsHazMat)) {
				strIsHazardous = KohlsConstant.YES;
			}
			Element eleItemDtls = (Element) eleGetItemDetails.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);

			Element eleItemExtn = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			Element eleItemPrimInfo = (Element) eleItemDtls.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
	
							
			Element eleOrderLineDs = (Element)docWMoSOrderReleaseXML.importNode(ndlstOrderLines.item(i), true);
			log.debug("eleOrderLineDs>>>>>"+XMLUtil.getElementXMLString(eleOrderLineDs));	
			Element eleInstr = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_INSTRUCTIONS).item(0);
			Element eleLnPriceInfo = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_PRICE_INFO).item(0);
			Element eleLineChrgs = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
			Element eleLineTaxes = (Element)eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
			eleOrderLineDs.removeChild(eleInstr);
			eleOrderLineDs.removeChild(eleLnPriceInfo);
			eleOrderLineDs.removeChild(eleLineChrgs);
			eleOrderLineDs.removeChild(eleLineTaxes);
			ndOrderLines.appendChild(eleOrderLineDs);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_GIFT_FLAG);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_LINE_TYPE);
			eleOrderLineDs.removeAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
		
		

		
			Element eleOrderLineDsOStatus = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
			Element eleOrderLineDsNotes = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
	
			eleOrderLineDs.removeChild(eleOrderLineDsOStatus);
			eleOrderLineDs.removeChild(eleOrderLineDsNotes);
	
			Element eleOrderLineDsItem = (Element) eleOrderLineDs.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
			eleOrderLineDsItem.removeAttribute(KohlsXMLLiterals.A_PRODUCT_LINE);
			
			eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_DESC));
			eleOrderLineDsItem.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, eleItemPrimInfo.getAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC));
			log.debug("eleOrderLineDs after change>>>>>"+XMLUtil.getElementXMLString(eleOrderLineDs));	
			
		}
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);
		if(strIsHazardous.equalsIgnoreCase("Y") && strExIsMilitary.equalsIgnoreCase("Y")){
			try {

				strShipVia = KohlsUtil.getCommonCodeList(env, KohlsConstant.SHIP_VIA_VALUES, KohlsConstant.IS_HAZ_IS_MILITARY);


			} catch (NullPointerException npExcp) {
				strShipVia = KohlsConstant.SHIP_VIA_PP;
				if(YFCLogUtil.isDebugEnabled()) {
					log.debug("Common code value is not set for code type SHIP_VIA_VALUES. " +
							"Using default value of PP");
				}
			}
		}
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_VIA, strShipVia);

		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TOTAL_REL_UNITS, String.valueOf(strAccQty));

		if(strIsHazardous.equalsIgnoreCase(KohlsConstant.NO)){
			strExtnRG = getRoutingGuideExtnRG(eleGetOrderReleaseDetails);
		}else{
			strExtnRG = KohlsConstant.RG_HAZARDOUS;
		}
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RG, strExtnRG);

		if(strLineType.equalsIgnoreCase(KohlsConstant.LINE_TYPE_PGC)){
			strCartonType = KohlsConstant.CARTON_TYPE_ENV;
		}else if(strProdLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)
				&& strExtnWrapCode.equalsIgnoreCase(KohlsConstant.YES)){
			strCartonType = KohlsConstant.CARTON_TYPE_BRK;
		}else if(strExtnWrapCode.equalsIgnoreCase(KohlsConstant.YES)){
			strCartonType = KohlsConstant.CARTON_TYPE_WRP;
		}else if(strProdLine.equalsIgnoreCase(KohlsConstant.PRODUCT_LINE_BK)){
			strCartonType = KohlsConstant.CARTON_TYPE_BRK;
		}
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CARTON_TYPE, strCartonType);
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_INST_TYPE, KohlsConstant.PRODUCT_LINE_ST);
		this.callChangeRelease(env,eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY) ,sPickTicketNo );
		//System.out.println("Release Message to WMoS : " + XMLUtil.getXMLString(docWMoSOrderReleaseXML));
	    this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_TO_WMOS, docWMoSOrderReleaseXML);
	}
	
	private void callChangeRelease(YFSEnvironment env, String sOrderReleaseKey, String sPickTicketNo) throws YFSException, RemoteException {

		//calling changeRelease
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside changeRelease ----------->");
		}

		YFCDocument yfcDocChangeRelease = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE);
		YFCElement yfcEleChangeRelease = yfcDocChangeRelease.getDocumentElement();
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, sOrderReleaseKey);
		yfcEleChangeRelease.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		

		YFCElement yfcEleChangeReleaseExtn = yfcDocChangeRelease.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleChangeReleaseExtn.setAttribute(KohlsXMLLiterals.A_EXTN_PICK_TICKET_NO, sPickTicketNo);
		yfcEleChangeRelease.appendChild(yfcEleChangeReleaseExtn);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML to changeRelease  : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		}
		//System.out.println("Input XML to changeRelease - REL A : " + XMLUtil.getXMLString(yfcDocChangeRelease.getDocument()));
		this.api.changeRelease(env, yfcDocChangeRelease.getDocument());

	}
	/**
	 * @param env
	 * @param sReceivingNode
	 */
	
	private Boolean isReceivingNodeWareHouse(YFSEnvironment env,
			String sReceivingNode) {
		Boolean isReceivingNodeWareHouse = false;
		try {
			//get list of EFCs
			log.debug("Inside isReceivingNodeWareHouse  : ");
			Set<String> lstReceivingNode = KohlsUtil.getCommonCodeValueList(env, KohlsConstant.WAREHOUSE_TRAN);
			if(null != lstReceivingNode){
				isReceivingNodeWareHouse=  lstReceivingNode.contains(sReceivingNode);
				
			}
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return isReceivingNodeWareHouse;
	}
	private String getRoutingGuideExtnRG(Element eleGetOrderReleaseDetails)
	throws YFSException, RemoteException, TransformerException{

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!--------------- Inside getRoutingGuideExtnRG ----------->");
		}

		Element elePersonInfoShipTo = (Element) XPathUtil.getNode((Node) eleGetOrderReleaseDetails, KohlsXMLLiterals.XP_ORDERRELEASE_PERSON_INFO);

		Element elePersonInfoShipToExtn = (Element)elePersonInfoShipTo.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String strIsMilitary = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_MILITARY);
		String strIsPOBox = elePersonInfoShipToExtn.getAttribute(KohlsXMLLiterals.A_EXTN_IS_PO_BOX);
		String sCarrierServiceCode = eleGetOrderReleaseDetails.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE);
		String sState = elePersonInfoShipTo.getAttribute(KohlsXMLLiterals.A_STATE);

		if (strIsMilitary.equalsIgnoreCase(KohlsConstant.YES)
				|| strIsPOBox.equalsIgnoreCase(KohlsConstant.YES)) {
			return KohlsConstant.RG_POAPO;
		} else if (sCarrierServiceCode.toLowerCase().contains(KohlsConstant.CARRIER_SERVICE_CODE_PRIORITY.toLowerCase())) {
			return KohlsConstant.RG_PRIORITY;
		} else if (KohlsConstant.STATE_AK.equalsIgnoreCase(sState) || KohlsConstant.STATE_HI.equalsIgnoreCase(sState)) {
			return KohlsConstant.RG_AK_HI;
		} else {
			return KohlsConstant.RG_STANDARD;
		}
	}
	/**
	 * provides item list template
	 * @return
	 */
	public static Document getItemListTemplate() {

		YFCDocument yfcDocGetItemListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM_LIST);
		YFCElement yfcEleGetItemListTemp = yfcDocGetItemListTemp.getDocumentElement();

		YFCElement yfcEleItemTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_ITEM);
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleItemTemp.setAttribute(KohlsXMLLiterals.A_UOM, "");

		YFCElement yfcEleItemAliasListTemp = yfcEleItemTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS_LIST);
		YFCElement yfcEleItemAliasTemp = yfcEleItemAliasListTemp.createChild(KohlsXMLLiterals.A_ITEM_ALIAS);
		yfcEleItemAliasTemp.setAttribute(KohlsXMLLiterals.A_ALIAS_NAME, "");
		yfcEleItemAliasTemp.setAttribute("AliasValue", "");
		yfcEleItemAliasListTemp.appendChild(yfcEleItemAliasTemp);
		yfcEleItemTemp.appendChild(yfcEleItemAliasListTemp);

		YFCElement yfcEleItemPrimaryInformationTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_IS_HAZMAT, "");
		yfcEleItemPrimaryInformationTemp.setAttribute(KohlsXMLLiterals.A_ITEM_SHORT_DESC, "");
		yfcEleItemTemp.appendChild(yfcEleItemPrimaryInformationTemp);

		YFCElement yfcEleItemExtnTemp = yfcDocGetItemListTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CAGE_ITEM, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SERVICE_SEQ, "");
		yfcEleItemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_BAGGAGE, "");
		yfcEleItemTemp.appendChild(yfcEleItemExtnTemp);

		yfcEleGetItemListTemp.appendChild(yfcEleItemTemp);

		if (YFCLogUtil.isDebugEnabled()) {
			//log.debug("getItemList Template : " + XMLUtil.getXMLString(yfcDocGetItemListTemp.getDocument()));
		}

		return yfcDocGetItemListTemp.getDocument();
	}
	/**
	 * form a input templete for getitemList call
	 * @param env
	 * @param sItemID
	 * @return
	 */

	public static Document getItemListInputXML(YFSEnvironment env, String sItemID) {

		YFCDocument yfcDocGetItemList = YFCDocument.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcEleGetItemList = yfcDocGetItemList.getDocumentElement();
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_ITEM_ID, sItemID);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, KohlsConstant.PRODUCT_CLASS_GOOD);
		yfcEleGetItemList.setAttribute(KohlsXMLLiterals.A_UOM, KohlsConstant.UNIT_OF_MEASURE);

		if (YFCLogUtil.isDebugEnabled()) {
			//log.debug("getItemList Input XML : " + XMLUtil.getXMLString(yfcDocGetItemList.getDocument()));
		}
		return yfcDocGetItemList.getDocument();
	}
	
	public void callChangeOrderStatus(YFSEnvironment env, Document inXML) throws Exception
	{
		System.out.print("<!-------- Begining of KohlsTestShipment callChangeOrderStatus method ----------- >" + XMLUtil.getXMLString(inXML));
		Element eleOrder=inXML.getDocumentElement();
		Element eleOrderStatuses=(Element)eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
		NodeList nlOrderStatus=eleOrderStatuses.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUS);
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_SEND_TO_RELEASE_TO_WMOS);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
		YFCElement yfcEleOrderStatusOrderLines = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleChangeOrderStatus.appendChild(yfcEleOrderStatusOrderLines);
		for (int i=0;i<nlOrderStatus.getLength();i++){
			Element eleOrderStatus=(Element)eleOrderStatuses.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUS).item(i);
			//Start OASIS Support 10/28/2013
			String status=eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS);
			if(!status.equalsIgnoreCase("9000")){
			//End OASIS Support 10/28/2013- Cancelled Shipment showing up on Invoice
			YFCElement yfcEleOrderStatusOrderLine = yfcDocChangeOrderStatus.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_BASEDROPSTATUS, KohlsConstant.STATUS_SEND_RELEASE_TO_WMOS);
			yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, eleOrderStatus.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY));
			yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));
			yfcEleOrderStatusOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, eleOrderStatus.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY));
			yfcEleOrderStatusOrderLines.appendChild(yfcEleOrderStatusOrderLine);
			//Start OASIS Support 10/28/2013- Cancelled Shipment showing up on Invoice
			}
			//End OASIS Support 10/28/2013- Cancelled Shipment showing up on Invoice
			
		}
		
		this.api.changeOrderStatus(env, yfcDocChangeOrderStatus.getDocument());
	}
}
