package com.kohls.po.api;

import java.rmi.RemoteException;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
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
 * 
 * This class is used by KohlsProcessShipConfirmCancelSyncService.
 * It performs shipment confirmation/cancellation for Purchase Orders. 
 * @author Priyadarshini
 *
 */
public class KohlsProcessPOShipConfirmCancelAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsProcessPOShipConfirmCancelAPI.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 * 
	 */
	public KohlsProcessPOShipConfirmCancelAPI() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	/**
	 * This method performs ship confirmation/cancellation for 
	 * kohls purchase orders.
	 * @param env
	 * @param inXml
	 * @throws Exception
	 */
	public void processDSVShipConf(YFSEnvironment env, Document inXml)
			throws Exception {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered processDSVShipConf Module--------------------------> ");
		}
		// check cancel lines exist
		if (shipCancelLinesExist(inXml)) {
			// call changeRelease API for canceled lines
			

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("changeRelease XML for PO: "
						+ XMLUtil.getXMLString(inXml));
			}			
			changeRelease(env, inXml);
		}

		// check if shipment confirm lines exist
		if (shipConfirmLinesExist(inXml)) {
			// call confirmShipment API for confirmed lines
			confirmShipment(env, inXml);
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit processDSVShipConf Module--------------------------> ");
		}
	}

	private void changeRelease(YFSEnvironment env, Document inXml)
			throws YFSException, RemoteException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered changeRelease Module--------------------------> ");
		}
		// get all cancel lines
		Element elemCancelLines = (Element) inXml
		.getElementsByTagName(KohlsXMLLiterals.E_CANCEL_LINES).item(0);
		String sShipAdviceNo = elemCancelLines.getAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO);
		NodeList nodeListCancelLine = elemCancelLines
				.getElementsByTagName(KohlsXMLLiterals.E_CANCEL_LINE);
		
		YFCDocument yfcDocOrderRelease = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_RELEASE); 
		YFCElement yfcElemOrderRelease = yfcDocOrderRelease
		.getDocumentElement();
		yfcElemOrderRelease.setAttribute(KohlsXMLLiterals.A_ACTION,
				KohlsConstant.ACTION_MODIFY);		
		yfcElemOrderRelease
				.setAttribute(KohlsXMLLiterals.A_SHIP_ADVICE_NO, sShipAdviceNo);
		yfcElemOrderRelease.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		
		YFCElement yfcElemOrderLines = yfcElemOrderRelease
				.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		for (int i = 0; i < nodeListCancelLine.getLength(); i++) {
					
			YFCElement yfcElemOrderLine = yfcElemOrderLines
			.createChild(KohlsXMLLiterals.E_ORDER_LINE);
			yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION,
					KohlsConstant.ACTION_CANCEL);
			Element elemCancelLine = (Element) nodeListCancelLine.item(i);
			yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO,
					elemCancelLine .getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
			// hardcode subline no to 1
			yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO,
					KohlsConstant.A_SUB_LINE_NO);
			yfcElemOrderLine.setAttribute(KohlsXMLLiterals.A_CHANGE_IN_QUANTITY,
					"-"+elemCancelLine
							.getAttribute(KohlsXMLLiterals.A_CHANGE_IN_QUANTITY));			
			
		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("ChangeRelease XML for PO: "
					+ XMLUtil.getXMLString(yfcDocOrderRelease.getDocument()));
		}
		api.changeRelease(env, yfcDocOrderRelease.getDocument());
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit changeRelease Module--------------------------> ");
		}

	}	
	
	private void confirmShipment(YFSEnvironment env, Document inXml)
			throws YFSException, RemoteException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered confirmShipment Module--------------------------> ");
		}
		// get input XML for confirmShipment API
		createConfirmShipmentInputXml(inXml);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Confirm Shipment XML for PO: "
					+ XMLUtil.getXMLString(inXml));
		}
		api.confirmShipment(env, inXml);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit confirmShipment Module--------------------------> ");
		}
	}

	private boolean shipConfirmLinesExist(Document inXml) {

		if (inXml.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES).item(0) == null) {
			return false;
		}
		return true;
	}

	private boolean shipCancelLinesExist(Document inXml) {
		Element elemCancelLines = (Element)inXml.getElementsByTagName(KohlsXMLLiterals.E_CANCEL_LINES).item(0);
		if (elemCancelLines == null  || elemCancelLines.getElementsByTagName(KohlsXMLLiterals.E_CANCEL_LINE).item(0) == null) {
			return false;
		}
		return true;
	}

	private void createConfirmShipmentInputXml(Document inXml) {
		// remove unwanted attributes
		Element elemShipment = (Element) inXml.getElementsByTagName(
				KohlsXMLLiterals.E_SHIPMENT).item(0);
		elemShipment.removeAttribute(KohlsXMLLiterals.A_CANCEL_EXISTS);
		Element elemContainerDetail = (Element) inXml
		.getElementsByTagName(KohlsXMLLiterals.A_CONTAINER_DETAIL)
		.item(0);
		if(elemContainerDetail != null){
			Element elemShipmentLine = (Element) elemContainerDetail.getChildNodes().item(1);
			elemShipmentLine.removeAttribute(KohlsXMLLiterals.A_QUANTITY);
		}
		// remove canceled lines
		if (shipCancelLinesExist(inXml)) {
			NodeList nodeListCancelLines = inXml
					.getElementsByTagName(KohlsXMLLiterals.E_CANCEL_LINES);
			for (int i = 0; i < nodeListCancelLines.getLength(); i++) {
				inXml.getDocumentElement().removeChild(
						(Element) nodeListCancelLines.item(i));
			}
		}

	}

	@Override
	public void setProperties(Properties arg0) throws Exception {

	}

}
