package com.kohls.common.util;

import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.oms.api.KohlsDSVConfirmShipmentMsgToEcommAPI;
import com.kohls.shipment.api.KohlsShipmentConfirmAPI;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsMsgGenShipConfirmToEcomm implements YIFCustomApi{
	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsMsgGenShipConfirmToEcomm.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsMsgGenShipConfirmToEcomm() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	} 
	
	/**
	 * Method to create and confirm shipment
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void messageGeneratorToEcomm(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KohlsMsgGenShipConfirmToEcomm messageGeneratorToEcomm method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strShipNode = "";
		String strSellOrgCode = "";
		String strShipNo = "";
		KohlsShipmentConfirmAPI kohlsShipConfirmAPI = null;
		KohlsDSVConfirmShipmentMsgToEcommAPI kohlsDScShipConfirmAPI = null;
		Document docOutMsg;

		Element eleInputList = inXML.getDocumentElement();
		NodeList nodeGetShipmentLst = eleInputList
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

		if (nodeGetShipmentLst.getLength() > 0) {

			Set<String> setEFCs = KohlsUtil.getEFCList(env);
			
			for(int i=0;i<nodeGetShipmentLst.getLength();i++){
			Element eleShipment = (Element) nodeGetShipmentLst.item(i);
			strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			strSellOrgCode = eleShipment.getAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE);
			strShipNo = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
			
			// form the input xml for getShipmentDetails
			YFCDocument yfcDocGetShipmentDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			YFCElement yfcEleGetShipmentDetails = yfcDocGetShipmentDetails.getDocumentElement();
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipNo);
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, strSellOrgCode);
			yfcEleGetShipmentDetails.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode);
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML to getShipmentDetails : " + XMLUtil.getXMLString(yfcDocGetShipmentDetails.getDocument()));
			}
			
			if(isEFCShipNode(setEFCs, strShipNode)){				
				env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, this.getShipmentDetailsSOTemp());
				Document docOutputGetShipmentDetails = this.api.getShipmentDetails(env, yfcDocGetShipmentDetails.getDocument());
				env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("getOrderDetails SO Output XML : " + XMLUtil.getXMLString(docOutputGetShipmentDetails));
				}				
				kohlsShipConfirmAPI = new KohlsShipmentConfirmAPI();
				docOutMsg = kohlsShipConfirmAPI.kohlsAddGiftWrapLine(env, docOutputGetShipmentDetails);
				this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SHIP_CONFIRM_TO_ECOMM, docOutMsg);
			} 
			else 
			{						
				env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, this.getShipmentDetailsDSVTemp());
				Document docOutputGetShipmentDetails = this.api.getShipmentDetails(env, yfcDocGetShipmentDetails.getDocument());
				env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("getOrderDetails DSV Output XML : " + XMLUtil.getXMLString(docOutputGetShipmentDetails));
				}						
				kohlsDScShipConfirmAPI = new KohlsDSVConfirmShipmentMsgToEcommAPI();
				docOutMsg = kohlsDScShipConfirmAPI.invoke(env, docOutputGetShipmentDetails);
				this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SHIP_CONFIRM_TO_ECOMM, docOutMsg);
				}
			}
		}	
	}
	
	private boolean isEFCShipNode(Set<String> lstEFCs, String strShipNode) {
		return lstEFCs.contains(strShipNode);		
	}
	
	
	private Document getShipmentDetailsSOTemp() {

		YFCDocument yfcDocShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		YFCElement yfcEleShipment = yfcDocShipment.getDocumentElement();

		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_ACTUAL_SHIPMENT_DATE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_PICK_TICKET_NO, "");		

		YFCElement yfcEleToAddress = yfcDocShipment.createElement(KohlsXMLLiterals.E_TO_ADDRESS);
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_DAY_PHONE, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_DEPARTMENT, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_EMAIL_ID, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_FIRST_NAME, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_LAST_NAME, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_MIDDLE_NAME, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_STATE, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");		
		yfcEleShipment.appendChild(yfcEleToAddress);		
				
		YFCElement yfcEleContainers = yfcDocShipment.createElement(KohlsXMLLiterals.E_CONTAINERS);
		yfcEleShipment.appendChild(yfcEleContainers);
		YFCElement yfcEleContainer = yfcEleContainers.createChild(KohlsXMLLiterals.E_CONTAINER);
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_BASIC_FREIGHT_CHARGE, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONT_GROSS_WEIGHT, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONT_GROSS_WEIGHT_UOM, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONTAINER_SCM, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_TRACKING_NO, "");
		yfcEleContainers.appendChild(yfcEleContainer);
		YFCElement yfcEleContainerDetails = yfcEleContainer.createChild(KohlsXMLLiterals.E_CONTAINER_DETAILS);
		yfcEleContainer.appendChild(yfcEleContainerDetails);
		YFCElement yfcEleContainerDetail = yfcEleContainerDetails.createChild(KohlsXMLLiterals.E_CONTAINER_DETAIL);
		yfcEleContainerDetail.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleContainerDetails.appendChild(yfcEleContainerDetail);
		YFCElement yfcEleContShipmentLine = yfcEleContainerDetail.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleContainerDetail.appendChild(yfcEleContShipmentLine);
		YFCElement yfcEleShipTagSerials = yfcEleContainerDetail.createChild(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIALS);
		yfcEleContainerDetail.appendChild(yfcEleShipTagSerials);
		YFCElement yfcEleShipTagSerial = yfcEleShipTagSerials.createChild(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIAL);
		yfcEleShipTagSerial.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleShipTagSerial.setAttribute(KohlsXMLLiterals.A_SERIAL_NO, "");
		yfcEleShipTagSerials.appendChild(yfcEleShipTagSerial);
		
		YFCElement yfcEleShipmentLines = yfcDocShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
		yfcEleShipment.appendChild(yfcEleShipmentLines);
		YFCElement yfcEleShipmentLine = yfcEleShipmentLines.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_RELEASE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_DESC, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");		
		yfcEleShipmentLines.appendChild(yfcEleShipmentLine);
		
		YFCElement yfcEleExtn = yfcDocShipment.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_IS_SHIP_ALONE, "");
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WMS_SHIPMENT_NO, "");
		yfcEleShipment.appendChild(yfcEleExtn);
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getShipmentDetails Template for SO :: " + XMLUtil.getXMLString(yfcDocShipment.getDocument()));
		}
		
		return yfcDocShipment.getDocument();

	}
	
	private Document getShipmentDetailsDSVTemp() {

		YFCDocument yfcDocShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		YFCElement yfcEleShipment = yfcDocShipment.getDocumentElement();

		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SCAC, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		yfcEleShipment.setAttribute(KohlsXMLLiterals.A_ACTUAL_SHIPMENT_DATE, "");
		
		YFCElement yfcEleToAddress = yfcDocShipment.createElement(KohlsXMLLiterals.E_TO_ADDRESS);
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_1, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_2, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ADD_LINE_3, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_CITY, "");
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_COUNTRY, "");		
		yfcEleToAddress.setAttribute(KohlsXMLLiterals.A_ZIP_CODE, "");		
		yfcEleShipment.appendChild(yfcEleToAddress);		
				
		YFCElement yfcEleContainers = yfcDocShipment.createElement(KohlsXMLLiterals.E_CONTAINERS);
		yfcEleShipment.appendChild(yfcEleContainers);
		YFCElement yfcEleContainer = yfcEleContainers.createChild(KohlsXMLLiterals.E_CONTAINER);		
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONT_GROSS_WEIGHT, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONT_GROSS_WEIGHT_UOM, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_CONTAINER_SCM, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_TRACKING_NO, "");
		yfcEleContainer.setAttribute(KohlsXMLLiterals.A_BASIC_FREIGHT_CHARGE, "");
		yfcEleContainers.appendChild(yfcEleContainer);
		YFCElement yfcEleContainerDetails = yfcEleContainer.createChild(KohlsXMLLiterals.E_CONTAINER_DETAILS);
		yfcEleContainer.appendChild(yfcEleContainerDetails);
		YFCElement yfcEleContainerDetail = yfcEleContainerDetails.createChild(KohlsXMLLiterals.E_CONTAINER_DETAIL);
		yfcEleContainerDetail.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleContainerDetails.appendChild(yfcEleContainerDetail);
		YFCElement yfcEleContShipmentLine = yfcEleContainerDetail.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO, "");
		yfcEleContShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleContainerDetail.appendChild(yfcEleContShipmentLine);
				
		YFCElement yfcEleShipmentLines = yfcDocShipment.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
		yfcEleShipment.appendChild(yfcEleShipmentLines);
		YFCElement yfcEleShipmentLine = yfcEleShipmentLines.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_UOM, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_HEADER_KEY, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, "");
		yfcEleShipmentLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");		
		yfcEleShipmentLines.appendChild(yfcEleShipmentLine);
		YFCElement yfcEleShipmentLineExtn = yfcEleShipmentLine.createChild(KohlsXMLLiterals.E_EXTN);		
		yfcEleShipmentLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_LINE_HANDL_CHRG, "");
		yfcEleShipmentLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_UNIT_COST, "");
		yfcEleShipmentLineExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_MISC_CHRG, "");
		yfcEleShipmentLine.appendChild(yfcEleShipmentLineExtn);
		
		YFCElement yfcEleExtn = yfcDocShipment.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DSV_INVOICE_NUM, "");
		yfcEleShipment.appendChild(yfcEleExtn);
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getShipmentDetails Template for DSV :: " + XMLUtil.getXMLString(yfcDocShipment.getDocument()));
		}
		
		return yfcDocShipment.getDocument();
	}
	
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
