package com.kohls.shipment.api;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class prepares an XML from the given input in a format needed by MultiMassPrinting services in SIM screen.
 * Input has value of the Shipnode attribute. With this, getShipmentList API is called to get list of Shipments
 * which are in status "1100.025" - AwaitingPickListPrinted with ShipmentType as Multi_Regular/Multi_Priority/Multi_Gift.
 * With the output of getShipmentList, the no.of shipments Available in the given status is read and the Output of 
 * the service is prepared.    
 * 
 * @author Kiran Potnuru
 *
 */
public class KOHLSGetAvailableCartsForPrintMultis {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSGetAvailableCartsForPrintMultis.class.getName());


	/**
	 * This method is the Wrapper for the class.
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */

	public Document getAvailableCarts(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KOHLSGetAvailableCartsForPrintMultis" +
					" getAvailableCarts method -- >"
					+ XMLUtil.getXMLString(inputDoc));
			
			
		}

		Element eleShipment = inputDoc.getDocumentElement();
		String strShipNode = eleShipment.getAttribute(KohlsConstant.ATTR_SHIP_NODE);
		String strTotesPerCart = eleShipment.getAttribute(KohlsConstant.ATTR_TOTES_PER_CART);

		// Preparing input for getShipmentList API
		Document getShipmentListInputDoc = getShipmentListInput(strShipNode);

		// Preparing the template doc
		Document getShipmentListTemplateDoc = getShipmentListTemplate();

		// Calling the getShipmentList API with the input prepared
		Document getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI(yfsEnvironment, getShipmentListTemplateDoc, 
				KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);

		// Calling the method to return the Available carts based on the values in the getShipmentList output XML and TotesPerCart

		Document outputDoc = prepareOutputDoc(getShipmentListOutputDoc,strTotesPerCart);

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- End of KOHLSGetAvailableCartsForPrintMultis" +
					" getAvailableCarts method -- >"
					+ XMLUtil.getXMLString(outputDoc));
		}

		return outputDoc;
	}


	private Document prepareOutputDoc(Document getShipmentListOutputDoc, String strTotesPerCart) throws Exception {
		Element eleShipmentList = getShipmentListOutputDoc.getDocumentElement();
		Document outputDocAvailableCarts = XMLUtil.createDocument(KohlsConstant.E_AVAILABLE_CARTS);
		Element eleAvailableCarts = outputDocAvailableCarts.getDocumentElement();
		
		//Count Available number carts for ShipmentType="Multi_Regular"
		
	   NodeList nlMultiRegShipmentList=XPathUtil.getNodeList(eleShipmentList, "Shipment[@ShipmentType='"+KohlsConstant.SHIP_TYPE_MULTI_REGULAR+"']");
		if(nlMultiRegShipmentList != null && nlMultiRegShipmentList.getLength() > 0){
			
			eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_REGULAR, getCarts(nlMultiRegShipmentList.getLength(),strTotesPerCart));
			
		}else{
			eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_REGULAR,"0");
		}
		
		//Count Available number carts for ShipmentType="Multi_Gift"
		
		  NodeList nlMultiGiftShipmentList=XPathUtil.getNodeList(eleShipmentList, "Shipment[@ShipmentType='"+KohlsConstant.SHIP_TYPE_MULTI_GIFT+"']");
			if(nlMultiGiftShipmentList != null && nlMultiGiftShipmentList.getLength() > 0){
				
				eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_GIFT, getCarts(nlMultiGiftShipmentList.getLength(),strTotesPerCart));
				
			}else{
				eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_GIFT,"0");
			}
		
		//Count Available number carts for ShipmentType="Multi_Priority"
			
			  NodeList nlMultiPriorityShipmentList=XPathUtil.getNodeList(eleShipmentList, "Shipment[@ShipmentType='"+KohlsConstant.SHIP_TYPE_MULTI_PRIORITY+"']");
				if(nlMultiPriorityShipmentList != null && nlMultiPriorityShipmentList.getLength() > 0){
					
					eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_PRIORITY, getCarts(nlMultiPriorityShipmentList.getLength(),strTotesPerCart));
					
				}else{
					eleAvailableCarts.setAttribute(KohlsConstant.A_CARTS_MULTI_PRIORITY,"0");
				}
				
				
			return outputDocAvailableCarts;
			
	}


	private String getCarts(int nShipmentTypes, String strTotesPerCart) {
		
		try {
			
			double dCarts=0.0d;
			dCarts=nShipmentTypes/(double)Integer.parseInt(strTotesPerCart);
			
			return Integer.toString((int)(Math.ceil(dCarts)));
			
		} catch (NumberFormatException e) {
			return "0";
		}
	
	}


	/**
	 * This method prepares input for getShipmentList API.
	 * @param strShipNode
	 * 
	 * @throws ParserConfigurationException 
	 */
	public Document getShipmentListInput(String strShipNode) throws ParserConfigurationException{

		Document getShipmentListInputDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleShipment = getShipmentListInputDoc.getDocumentElement();

		eleShipment.setAttribute(KohlsConstant.ATTR_SHIP_NODE, strShipNode);
		eleShipment.setAttribute(KohlsConstant.ATTR_STATUS, KohlsConstant.V_AWAITING_PICKLIST_PRINT);
		eleShipment.setAttribute(KohlsConstant.A_IS_PICKTICKET_PRINTED, KohlsConstant.V_N);

		Element eleComplex = getShipmentListInputDoc.createElement(KohlsConstant.E_COMPLEX_QUERY);
		Element eleOr = getShipmentListInputDoc.createElement(KohlsConstant.E_OR);

		Element eleExp1 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp1.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp1.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_REGULAR);

		Element eleExp2 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp2.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp2.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_PRIORITY);

		Element eleExp3 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp3.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp3.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_GIFT);

		
		eleOr.appendChild(eleExp1);
		eleOr.appendChild(eleExp2);
		eleOr.appendChild(eleExp3);
		eleComplex.appendChild(eleOr);
		eleShipment.appendChild(eleComplex);

		return getShipmentListInputDoc;
	}


	/**
	 * This method prepares input for getShipmentList API.
	 * @throws ParserConfigurationException 
	 * 
	 * @throws ParserConfigurationException 
	 */
	public Document getShipmentListTemplate() throws ParserConfigurationException {

		Document getShipmentListTemplateDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleShipmentList = getShipmentListTemplateDoc.getDocumentElement();

		Element eleShipment = getShipmentListTemplateDoc.createElement(KohlsConstant.E_SHIPMENT);
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_TYPE, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY, "");

		
		eleShipmentList.appendChild(eleShipment);

		return getShipmentListTemplateDoc;
	}

}