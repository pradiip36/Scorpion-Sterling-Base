package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNode;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Vijay Kumar 
 * This Custom API will be triggered ON_SUCCESS Event of Create an Confirm Shipment in Inbound Shipment line . 
 * Input XML :
 * <?xml version="1.0" encoding="UTF-16"?>
<Shipment DocumentType="0005" EnterpriseCode="KOHLS.COM" CarrierServiceCode=" " SellerOrganizationCode="VR1" ShipNode="VR1" ShipmentNo="100000020" SCAC=" ">
<ShipmentLines>
<ShipmentLine ItemID="LAPTOP" OrderNo="2010122711492075554" ProductClass="Good" Quantity="2.00" ShipmentLineNo="1" UnitOfMeasure="EACH" PurchaseOrderNo="Y100000060_3" PrimeLineNo="1" SubLineNo="1" />
</ShipmentLines>
<Containers>
<Container ContainerScm="CON1" TrackingNo="12" ContainerGrossWeight="0.00" ContainerGrossWeightUOM="LBS">
<ContainerDetails>
<ContainerDetail Quantity="1.00" />
</ContainerDetails>
</Container>
</Containers>
<ToAddress AddressLine1="dsad" AddressLine2="dsds" AddressLine3="" AddressLine4="" AddressLine5="" AddressLine6="" AlternateEmailID="" Beeper="" City="" Company="" Country=" " DayFaxNo="" DayPhone="" Department="" EMailID="" ErrorTxt="" EveningFaxNo="" EveningPhone="" FirstName="" HttpUrl="" JobTitle="" LastName="" MiddleName="" MobilePhone="" OtherPhone="" PersonID="" PersonInfoKey="2011010612420391659" PreferredShipAddress="" State="" Suffix="" Title="" UseCount="0" VerificationStatus="" ZipCode="" />
</Shipment>

This API Will use the ChainedOrderLine key to find the chained SO No and stamp it to the outgoing XML.
 *
 */



public class KohlsDSVConfirmShipmentMsgToEcommAPI implements YIFCustomApi {
	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsDSVConfirmShipmentMsgToEcommAPI.class.getName());
	
	public KohlsDSVConfirmShipmentMsgToEcommAPI() throws YIFClientCreationException {
		// TODO Auto-generated constructor stub
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	

	public Document invoke(YFSEnvironment env, Document inXML) throws YFSException, Exception ,RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered Create and Confirm Shipment method ----------------> ");
		 }
		// get the ChainedFromOrderHeaderKey from the input XML to find the Related Sales Order No 	
		
		log.debug("Input XML before stamping SO Number from KohlsDSVConfirmShipmentMsgToEcommAPI: " + XMLUtil.getXMLString(inXML));
		
		ArrayList listPrimeLine = null;
		
		// get the Ship Confirm XML from the env
		if(null!=env.getTxnObject("ShipmentLineNos")){
			listPrimeLine = (ArrayList) env.getTxnObject("ShipmentLineNos");
		}		
		
		if(null!=listPrimeLine){
			modifyXMLToEcomm(listPrimeLine, inXML);
		}
		
		YFCElement eleShipmemtLines;
		YFCElement eleShipmemtLine;
		YFCDocument yfcDocShipment= YFCDocument.getDocumentFor(inXML);	
		YFCElement eleShipment =   yfcDocShipment.getDocumentElement();
		// if cancel lines exist remove them from input xml
		YFCNodeList ndCancelLines = eleShipment.getElementsByTagName("CancelLines");
		if(ndCancelLines.getLength()>0){
			YFCNode ndCancelLn = ndCancelLines.item(0);
			eleShipment.removeChild(ndCancelLn);
		}
		Map shipVal = null;
		Map primeVal = null;
		String strSONo = "";
		String strOrgPrimLnNo;
		Map mpOrdLineKey = new HashMap<String, String>();
		Map mpChPrimeLnNo = new HashMap<String, String>();
		// set the Ship Advice No
		String strShipAdvice = getShipAdviceNo(yfcDocShipment, env);
		
		YFCNodeList nodeShipmentLines= eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES);
		if(nodeShipmentLines.getLength()>0){
			eleShipmemtLines = (YFCElement) nodeShipmentLines.item(0);
			YFCNodeList nodeShipLine =  eleShipmemtLines
					.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
			if(nodeShipLine.getLength()>0){
				for(int i=0; i< nodeShipLine.getLength(); i++){			
				eleShipmemtLine = (YFCElement)nodeShipLine.item(i);	
				strOrgPrimLnNo = "";
				eleShipmemtLine.setAttribute("PurchaseOrderNo", eleShipmemtLine.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
				
				strOrgPrimLnNo = eleShipmemtLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
				
				// get the ChainedFromOrderHeaderKey from the input XML to find the Related Sales Order No
				String orderHeaderKey = eleShipmemtLine.getAttribute("ChainedFromOrderHeaderKey");
				String orderLineKey = eleShipmemtLine.getAttribute("ChainedFromOrderLineKey");
				// calling a Private Method to Find the Related Chained SalesOrder NO and stamp it in outgoing XML.
				if(null!=orderHeaderKey && !orderHeaderKey.equals("")){
					mpOrdLineKey = getSalesOrdNum(env , orderHeaderKey, orderLineKey);		
				}
				eleShipmemtLine.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, (String) mpOrdLineKey.get("OrderNo"));
				eleShipmemtLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, (String) mpOrdLineKey.get("PrimeLineNo"));
				eleShipmemtLine.setAttribute("ShipAdviceNo", strShipAdvice);
				eleShipmemtLine.removeAttribute("ChainedFromOrderHeaderKey");	
				eleShipmemtLine.removeAttribute("ChainedFromOrderLineKey");	
				mpChPrimeLnNo.put(strOrgPrimLnNo, (String) mpOrdLineKey.get("PrimeLineNo"));
				}			
			}					
		}
		
		// set the ShipAdvice no to Container Details
		YFCElement eleContainers;
		YFCElement eleContainer;
		YFCElement eleContainerDtls;
		YFCElement eleContainerDtl;
		YFCElement eleShipmLine;
		YFCNodeList nodeContainers= eleShipment.getElementsByTagName(KohlsXMLLiterals.E_CONTAINERS);
		if(nodeContainers.getLength()>0){
			eleContainers = (YFCElement) nodeContainers.item(0);
			YFCNodeList nodeContainer =  eleContainers
					.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER);
			if(nodeContainer.getLength()>0){
				for(int j=0; j< nodeContainer.getLength(); j++ ){
				eleContainer = (YFCElement) nodeContainer.item(j);
				YFCNodeList nodeContainerDtls =  eleContainer
					.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER_DETAILS);
				if(nodeContainerDtls.getLength()>0){
					eleContainerDtls = (YFCElement) nodeContainerDtls.item(0);
					YFCNodeList nodeContainerDtl =  eleContainerDtls
						.getElementsByTagName(KohlsXMLLiterals.A_CONTAINER_DETAIL);
					if(nodeContainerDtl.getLength()>0){						
						for(int i=0; i< nodeContainerDtl.getLength(); i++){	
							eleContainerDtl = (YFCElement)nodeContainerDtl.item(i);
							eleShipmLine = eleContainerDtl.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE).item(0);
							eleShipmLine.setAttribute("ShipAdviceNo", strShipAdvice);
								// set ContainerDetail qty to the shipment line
								// within ContainerDetail
								eleShipmLine.setAttribute(
												KohlsXMLLiterals.A_QUANTITY,
												eleContainerDtl.getAttribute(KohlsXMLLiterals.A_QUANTITY));
								eleShipmLine.setAttribute(
												KohlsXMLLiterals.A_PRIME_LINE_NO,
												(String) mpChPrimeLnNo.get(eleShipmLine
														.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO)));
							}
						}
					}
				}
			}
		}		

		eleShipment.setAttribute("Action", "Create");
		

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Output XML after stamping SO Number : " + XMLUtil.getXMLString(yfcDocShipment.getDocument()));
			log.verbose("Output XML after stamping SO Number : " + XMLUtil.getXMLString(yfcDocShipment.getDocument()));
		}
		return yfcDocShipment.getDocument();
			
	}
	
	private void modifyXMLToEcomm(ArrayList primeList, Document inXml) {
		
		List addShipLnNo = new ArrayList<String>();
		Element eleShipmentLines = (Element)inXml.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES).item(0);
		NodeList nlShipmentLine = eleShipmentLines.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		int cntLines = nlShipmentLine.getLength();
		int j=0;
		int p = 1;
		while(j<cntLines){			
			if(p==cntLines){
				break;
			}
			Element eleShipmentLine = (Element)nlShipmentLine.item(j);
			if(primeList.contains(eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO))){
				j++;
			}
			else
			{
				addShipLnNo.add(eleShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO));
				eleShipmentLines.removeChild(eleShipmentLine);
			}
			p++;
		}		
		
		if (null!=inXml.getElementsByTagName(KohlsXMLLiterals.E_CONTAINERS).item(0)){
			Element eleContainers = (Element)inXml.getElementsByTagName(KohlsXMLLiterals.E_CONTAINERS).item(0);
			NodeList nlContainer = eleContainers.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER);	
			
			int conQty = nlContainer.getLength();
			int i=0;
			int q = 1;
			while(i<conQty){				
				if(q==conQty){
					break;
				}
				Element eleContainer = (Element)nlContainer.item(i);
				Element eleContainerDtls = (Element)eleContainer.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER_DETAILS).item(0);
				Element eleContainerDtl = (Element)eleContainerDtls.getElementsByTagName("ContainerDetail").item(0);
				Element eleShipLine = (Element)eleContainerDtl.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE).item(0);
				
				if(addShipLnNo.contains(eleShipLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO))){
					eleContainers.removeChild(eleContainer);
				}	
				else
				{
					i++;
				}
				q++;
			}			
		}				
	}
	
	
	private Map getSalesOrdNum(YFSEnvironment env ,String orderHeaderKey, String orderLineKey) throws RemoteException {
		 
	     // Private Method to get the SO Num for chainedOrderLineKey		 
		 if (YFCLogUtil.isDebugEnabled()) {
			log.debug("orderHeaderKey : " + orderHeaderKey );	
			log.debug("orderLineKey : " + orderLineKey );	
		 }
		 
		 String strPrmLinNo = "";
		 Map mpOrLnKey = null;
		 YFCDocument yfcDocGetOrderTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		 YFCElement yfcEleGetOrderListTemp = yfcDocGetOrderTemp.getDocumentElement();
		 yfcEleGetOrderListTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);	
		 
		 // set the API template for getOrderLineList and then calling it . This will fetch Sales Order No. as OrderNo=""
		 env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST , this.getOrderListTemplate());
		 Document docOutputGetOrderLineList = this.api.getOrderList(env, yfcDocGetOrderTemp.getDocument() );
	     env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);
	     
	     // start of Order broker to get Sales Order No.
	     Element eleOrder = (Element) docOutputGetOrderLineList.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		 String strOrderNo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		 
		 mpOrLnKey = new HashMap<String, String>();
		 
		 	NodeList nodeOrderLines= eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES);
			if(nodeOrderLines.getLength()>0){
				Element eleOderLines = (Element) nodeOrderLines.item(0);
				NodeList nodeOrderLine =  eleOderLines
						.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
				if(nodeOrderLine.getLength()>0){
					for(int i=0; i< nodeOrderLine.getLength(); i++){			
						Element eleOrderLine = (Element)nodeOrderLine.item(i);	
						String strOrderLnKey = (String)eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
						if(strOrderLnKey.equalsIgnoreCase(orderLineKey)){
							strPrmLinNo = (String)eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
						}
					}
				}
			}
		 
		 if (YFCLogUtil.isDebugEnabled()) {
				log.debug("OrderNo  = " + strOrderNo );
				log.debug("PrimeLineNo  = " + strOrderNo );
		}
		 
		 mpOrLnKey.put("OrderNo", strOrderNo);
		 mpOrLnKey.put("PrimeLineNo", strPrmLinNo);
	     
		 return mpOrLnKey ;
	}
	
	
	private Document getOrderLineListInputXML(String strOrderLineKey ) {
		 
		YFCDocument yfcDocGetOrderLineList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		YFCElement yfcEleOrderLineList = yfcDocGetOrderLineList.getDocumentElement();
		yfcEleOrderLineList.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY,strOrderLineKey);
		return yfcDocGetOrderLineList.getDocument();
		}
	
	private String getShipAdviceNo(YFCDocument inXML, YFSEnvironment env) throws ParserConfigurationException, RemoteException{
		
		YFCElement shipmentElem = inXML.getDocumentElement();		
		Document getShipmentDtlsInDoc = XmlUtils.createDocument("Shipment");
		Element rootElem = getShipmentDtlsInDoc.getDocumentElement();
		rootElem.setAttribute("SellerOrganizationCode", shipmentElem.getAttribute("SellerOrganizationCode"));
		rootElem.setAttribute("ShipNode", shipmentElem.getAttribute("ShipNode"));
		rootElem.setAttribute("ShipmentNo", shipmentElem.getAttribute("ShipmentNo"));
		String strShipAdvice="";
		
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, this.getShipDtlsTemplt());
		Document getShipmentDtlsOutDoc = api.getShipmentDetails(env, getShipmentDtlsInDoc);
		log.debug("Output XML from getShipmentDtls : " + XMLUtil.getXMLString(getShipmentDtlsOutDoc));
		env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
		YFCDocument yfcDocShipment= YFCDocument.getDocumentFor(getShipmentDtlsOutDoc);	
		YFCElement eleShipment =   yfcDocShipment.getDocumentElement();
		
		if(eleShipment != null){			
			
			YFCNodeList shipmentLinesList = eleShipment.getElementsByTagName("ShipmentLines");
			if(shipmentLinesList != null && shipmentLinesList.getLength() > 0){
				YFCElement shipmentLinesElem = (YFCElement)shipmentLinesList.item(0);
				if(shipmentLinesElem != null){
					YFCNodeList shipLineList = shipmentLinesElem.getElementsByTagName("ShipmentLine");
					if(shipLineList != null && shipLineList.getLength() > 0){
						YFCElement shipLineElem = (YFCElement)shipLineList.item(0);
						if(shipLineElem != null){
							YFCNodeList orderReleaseList = shipLineElem.getElementsByTagName("OrderRelease");
							if(orderReleaseList != null && orderReleaseList.getLength() > 0){
								YFCElement orderReleaseElem = (YFCElement)orderReleaseList.item(0);
								if(orderReleaseElem != null){
									strShipAdvice = orderReleaseElem.getAttribute("ShipAdviceNo");
									log.debug("orderReleaseElem(ShipAdviceNo) : " + strShipAdvice);
								}
							}							
						}
					}
				}
			}
		}
		
		return strShipAdvice;
	}
	
	private Document getOrderListTemplate() {

		YFCDocument yfcDocGetOrderListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcEleGetOrderListTemp = yfcDocGetOrderListTemp.getDocumentElement();		
		
		YFCElement yfcEleOrderTemp = yfcEleGetOrderListTemp.createChild(KohlsXMLLiterals.E_ORDER);
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		
		yfcEleGetOrderListTemp.appendChild(yfcEleOrderTemp);
		
		YFCElement yfcEleOrderLinesTemp = yfcEleGetOrderListTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleOrderTemp.appendChild(yfcEleOrderLinesTemp);
		
		YFCElement yfcEleOrderLineTemp = yfcEleGetOrderListTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);		
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderList Template : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		}

		return yfcDocGetOrderListTemp.getDocument();
	}
	
	private Document getShipDtlsTemplt() throws ParserConfigurationException{
		
		
		Document templateDoc = XmlUtils.createDocument("Shipment");
		Element shipmentElem = templateDoc.getDocumentElement();
		shipmentElem.setAttribute("ShipmentNo", KohlsConstant.BLANK);
		
		Element shipmentLinesElem = templateDoc.createElement("ShipmentLines");
		shipmentElem.appendChild(shipmentLinesElem);
		
		Element shipmentLineElement = templateDoc.createElement("ShipmentLine");
		shipmentLineElement.setAttribute("PrimeLineNo", KohlsConstant.BLANK);		
		shipmentLinesElem.appendChild(shipmentLineElement);
		
		Element orderReleaseElem = templateDoc.createElement("OrderRelease");
		orderReleaseElem.setAttribute("ShipAdviceNo", KohlsConstant.BLANK);
		shipmentLineElement.appendChild(orderReleaseElem);
		
		Element shipmntLineExtnElem = templateDoc.createElement("Extn");
		shipmentLineElement.appendChild(shipmntLineExtnElem);
		return templateDoc;
	}

	
}
