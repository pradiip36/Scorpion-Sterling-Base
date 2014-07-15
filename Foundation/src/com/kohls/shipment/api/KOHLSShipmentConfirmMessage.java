package com.kohls.shipment.api;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
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

/**
 * This class is called on receiving the Shipment Confirmation message from WMoS
 * 
 * @author Punit Kumar
 * 
 * Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 */

public class KOHLSShipmentConfirmMessage implements YIFCustomApi {

	private YIFApi api;

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSShipmentConfirmMessage.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KOHLSShipmentConfirmMessage() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public Document formulateMessage(YFSEnvironment env, Document inXML)
			throws Exception {

		YFCDocument inDoc = YFCDocument.getDocumentFor(inXML);
		YFCElement inEle = inDoc.getDocumentElement();
		
		log
				.debug("***************inside massageMsg mehtod of KOHLSShipmentConfirmMessage class **********");
		String strEFCShipNode = KohlsUtil.getEFCForStore(inEle
				.getAttribute("FromNode"), env);
		String strDocumentType=inEle.getAttribute("DocumentType");
		
		if(strDocumentType.equalsIgnoreCase("0006")){

			/*YFCDocument shipmentListTemplateDoc=YFCDocument.createDocument(KohlsConstant.E_SHIPMENTS);
			YFCElement eleShipmentListTemplateDoc=shipmentListTemplateDoc.getDocumentElement();
			YFCElement eleShipmentList= shipmentListTemplateDoc.createElement(KohlsConstant.E_SHIPMENT);
			eleShipmentList.setAttribute(KohlsXMLLiterals.A_RECEIVING_NODE, "");
			eleShipmentListTemplateDoc.appendChild(eleShipmentList);
			
			YFCDocument yfcDocGetShipment = YFCDocument
			.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			YFCElement yfcEleGetShipment = yfcDocGetShipment.getDocumentElement();
			yfcEleGetShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
			inEle.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));

			if (YFCLogUtil.isDebugEnabled()) {
				System.out.println("Input xml for getShipmentList in massageMsg:"
				+ XMLUtil.getXMLString(yfcDocGetShipment.getDocument()));
			}
			
			env.setApiTemplate("getShipmentList", shipmentListTemplateDoc
					.getDocument());
			Document docGetShipment = this.api.getShipmentList(env,
			yfcDocGetShipment.getDocument());
			env.clearApiTemplate("getShipmentList");
			
			Element eleShipment=(Element)docGetShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0);
			
			inEle.setAttribute("ToNode", eleShipment.getAttribute(KohlsXMLLiterals.A_RECIEVING_NODE));*/
			
			// Fix for Defect 6086 - suppressing getShipmentList call since ReceivingNode can be fetched the CREATE_SHIPMENT.ON_SUCCESS event XML
			
			inEle.setAttribute("ToNode", inEle.getAttribute(KohlsXMLLiterals.A_RECIEVING_NODE));
		
		}else{
			inEle.setAttribute("ToNode", strEFCShipNode);
		}
		log.debug("***************Ship Node determined is**********"
				+ strEFCShipNode);
		// inEle.removeAttribute("ShipmentKey");

		NodeList nlLineList = ((Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT_LINES).item(0))
				.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);

		YFCDocument itemtemplateDoc = YFCDocument
				.parse("<ItemList><Item><Extn/></Item></ItemList>");
		env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, itemtemplateDoc
				.getDocument());

		Document getItemListInputDoc = null;
		Element getItemListInputEle = null;
		try {
			getItemListInputDoc = XMLUtil.createDocument("Item");
			getItemListInputEle = getItemListInputDoc.getDocumentElement();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < nlLineList.getLength(); i++) {
			log.debug("**** ShipmentLine Element being processed *****" + i);
			Element elemShipLine = (Element) nlLineList.item(i);
			log.debug("****Printing ShipmentLine Element"
					+ XMLUtil.getElementXMLString(elemShipLine));
			Element shipLineExtn = (Element) elemShipLine.getElementsByTagName(
					"Extn").item(0);
			
			log.debug("Extn element already exist");
			if (shipLineExtn == null) {
				
				log.debug("Creating the Extn element since it doesnt exist");
				shipLineExtn = (Element) inDoc.getDocument().createElement(
						"Extn");
				elemShipLine.appendChild(shipLineExtn);
			}

			getItemListInputEle.setAttribute(KohlsXMLLiterals.A_ITEM_ID,
					elemShipLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
			getItemListInputEle.setAttribute(KohlsXMLLiterals.A_UOM,
					elemShipLine.getAttribute(KohlsXMLLiterals.A_UOM));

			log.debug("*************getItemList is being called with input"
					+ XMLUtil.getXMLString(getItemListInputDoc));

			Document itemListOutDoc = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_GET_ITEM_LIST, getItemListInputDoc);

			log.debug("getItemList has been completed with output"
					+ XMLUtil.getXMLString(itemListOutDoc));
			YFCDocument itemOutDoc = YFCDocument.getDocumentFor(itemListOutDoc);
			YFCElement itemele = itemOutDoc.getDocumentElement()
					.getChildElement("Item");
			if (itemele != null) {
				
				log.debug("inside the loop to stamp the extn attribute");
				YFCElement itemExtnEle = itemele.getChildElement("Extn");
				shipLineExtn.setAttribute("ExtnDept", itemExtnEle
						.getAttribute("ExtnDept"));
				shipLineExtn.setAttribute("ExtnClass", itemExtnEle
						.getAttribute("ExtnClass"));
				shipLineExtn.setAttribute("ExtnSubClass", itemExtnEle
						.getAttribute("ExtnSubClass"));
			}

		}
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);

		return inXML;

		// getitemlist with template for extn attrs

	}

	public void setProperties(Properties arg0) throws Exception {

	}
}