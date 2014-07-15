package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author puneet
 * 
 */
public class KOHLSUpdateContainerTrackingNoAPI extends KOHLSBaseApi {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSUpdateContainerTrackingNoAPI.class.getName());
	
	/**
	 * 
	 * @param yfsEnvironment
	 * @param inXML
	 * @return
	 * @throws Exception
	 */

	public Document updateContainerTracking(YFSEnvironment env,
			Document inXML) throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KOHLSUpdateContainerTrackingNoAPI updateContainerTracking method -- >"
					+ XMLUtil.getXMLString(inXML));
		}
		
		Document docOutput = null;

		if (YFCObject.isVoid(inXML)) {
			docOutput = inXML;
		}

		String strContainerScm = "";

		if (!YFCObject.isVoid(inXML)) {
			Element eleInput = inXML.getDocumentElement();
			strContainerScm = ((Element) eleInput.getElementsByTagName(
					KohlsConstant.A_CONTAINER).item(0))
					.getAttribute(KohlsConstant.A_CONTAINERSCM);
			String strTrackingNo = ((Element) eleInput.getElementsByTagName(
					KohlsConstant.A_CONTAINER).item(0))
					.getAttribute("TrackingNo");
			String strBasicFreightCharge = ((Element) eleInput
					.getElementsByTagName(KohlsConstant.A_CONTAINER).item(0))
					.getAttribute("BasicFreightCharge");
			String strShipmentNo = eleInput
					.getAttribute(KohlsConstant.A_SHIPMENT_NO);
			String strShipNode = eleInput
					.getAttribute(KohlsConstant.A_SHIP_NODE);
			String strSCAC = eleInput
			.getAttribute(KohlsConstant.A_SCAC);
			String strCarrierServiceCode = eleInput
			.getAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE);
			String strCarrierAccountNo = eleInput
			.getAttribute(KohlsConstant.A_CARRIER_ACCOUNT_NO);

			Document docInGetShipmentList = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENT);
			Document docOutGetShipmentList = null;
			docInGetShipmentList.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIPMENT_NO, strShipmentNo);
			docInGetShipmentList.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIP_NODE, strShipNode);
			env
					.setApiTemplate(
							KohlsConstant.API_actual_GET_SHIPMENT_LIST,
							KohlsConstant.API_GET_SHIPMENT_LIST_FOR_UPDATE_CONTAINER_TRACKING);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input for getShipmentList : \n"
						+ XMLUtil.getXMLString(docInGetShipmentList));
			}
			
			docOutGetShipmentList = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_actual_GET_SHIPMENT_LIST, docInGetShipmentList);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Output for getShipmentList : \n"
						+ XMLUtil.getXMLString(docOutGetShipmentList));
			}
			
			env.clearApiTemplate(KohlsConstant.API_actual_GET_SHIPMENT_LIST);
			Element eleOutGetShipmentList = docOutGetShipmentList
					.getDocumentElement();
			String strShipmentKey = ((Element) eleOutGetShipmentList
					.getElementsByTagName("Shipment").item(0))
					.getAttribute("ShipmentKey");
			if (!YFCObject.isVoid(strContainerScm)) {
				Element eleMatchedContainer = getMatchedContainerElem(
						strContainerScm, eleOutGetShipmentList);
				
				if (!YFCObject.isVoid(eleMatchedContainer)) {
					YFCDocument inputDocForUnPackShipment = YFCDocument
							.createDocument(KohlsConstant.E_SHIPMENT);

					YFCElement eleInputForUnPackShipment = inputDocForUnPackShipment
							.getDocumentElement();
					eleInputForUnPackShipment.setAttribute(
							KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					YFCElement eleContainsInputForUnPackShipment = eleInputForUnPackShipment
							.createChild("Containers");
					YFCElement eleContainerInputForUnPackShipment = eleContainsInputForUnPackShipment
							.createChild("Container");
					eleContainerInputForUnPackShipment
							.setAttribute(
									KohlsConstant.A_CONTAINER_NO,
									eleMatchedContainer
											.getAttribute(KohlsConstant.A_CONTAINER_NO));
					eleContainerInputForUnPackShipment.setAttribute(
							"ShipmentContainerKey", eleMatchedContainer
									.getAttribute("ShipmentContainerKey"));
					eleContainerInputForUnPackShipment.setAttribute(
							"ContainerScm", eleMatchedContainer
									.getAttribute("ContainerScm"));
					
					callPackShipmentOrUnPackShipment(env,
							inputDocForUnPackShipment.getDocument(), true);
					Document docInputForPackShipment = XMLUtil
							.createDocument(KohlsConstant.E_SHIPMENT);

					Element eleInputForPackShipment = docInputForPackShipment
							.getDocumentElement();
					eleInputForPackShipment.setAttribute(
							KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					Element eleContainsInputForPackShipment = XMLUtil
							.createChild(eleInputForPackShipment, "Containers");
					eleMatchedContainer.setAttribute("TrackingNo",
							strTrackingNo);
					//Added  by Zubair - Begin
					eleMatchedContainer.setAttribute(KohlsConstant.A_IS_PACK_PROCESS_COMPLETE, KohlsConstant.FLAG_Y);
					//Added  by Zubair - End
					eleMatchedContainer.setAttribute("BasicFreightCharge",
							strBasicFreightCharge);
					Element eleImported = (Element) docInputForPackShipment
							.importNode(eleMatchedContainer, true);
					eleContainsInputForPackShipment.appendChild(eleImported);

					
					docOutput = callPackShipmentOrUnPackShipment(env,
							docInputForPackShipment, false);
					
					Document docInputForChangeShipment = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENT);

			Element eleinputForChangeShipment = docInputForChangeShipment
					.getDocumentElement();
			eleinputForChangeShipment.setAttribute(
							KohlsConstant.A_SHIPMENT_KEY,
							strShipmentKey);
			eleinputForChangeShipment.setAttribute(
					KohlsConstant.A_SCAC,
					strSCAC);
			eleinputForChangeShipment.setAttribute(
					KohlsConstant.A_CARRIER_SERVICE_CODE,
					strCarrierServiceCode);
			eleinputForChangeShipment.setAttribute(
					KohlsConstant.A_CARRIER_ACCOUNT_NO,
					strCarrierAccountNo);
		Document 	docOutputForChangeShipment = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_CHANGE_SHIPMENT, docInputForChangeShipment);
					
					if (docOutput.getDocumentElement().getAttribute(
							"ShipmentContainerizedFlag").equalsIgnoreCase(
							KohlsConstant.V_SHIPMENT_CONTAINERIZED_03)) {
						boolean bCallConfirmShipment = true;
						if (YFCObject.isVoid(strTrackingNo)) {

							bCallConfirmShipment = false;
						} 
						
						/*else {

							NodeList nodeContainerList = eleOutGetShipmentList
									.getElementsByTagName("Container");
							for (int iCtnCount = 0; iCtnCount < nodeContainerList
									.getLength(); iCtnCount++) {
								Element eleContainer = (Element) nodeContainerList
										.item(iCtnCount);
								String strLastSCM = eleContainer
										.getAttribute(KohlsConstant.A_CONTAINERSCM);
								if (!strLastSCM.equalsIgnoreCase(strContainerScm)) {
									if (YFCObject.isVoid(eleContainer
											.getAttribute("TrackingNo"))) {
										bCallConfirmShipment = false;
									}
								}
							}
						}*/
						

						
							if (bCallConfirmShipment) {
							Document docInputForConfirmShipment = XMLUtil
									.createDocument(KohlsConstant.E_SHIPMENT);

							Element eleinputForConfirmShipment = docInputForConfirmShipment
									.getDocumentElement();
							eleinputForConfirmShipment
									.setAttribute(
											KohlsConstant.A_SHIPMENT_KEY,
											strShipmentKey);

							if (YFCLogUtil.isDebugEnabled()) {
								log.debug("Input for confirmShipment : \n"
										+ XMLUtil.getXMLString(docInputForConfirmShipment));
							}
							
							KOHLSBaseApi.invokeService(env,
									KohlsConstant.SERVICE_POST_MESSAGE_CONFIRM_SHIPMENT, docInputForConfirmShipment);

							if (YFCLogUtil.isDebugEnabled()) {
								log.debug("Output for confirmShipment : \n"
										+ XMLUtil.getXMLString(docOutput));
							}
						}
						

					}
				}

				// TODO Auto-generated method stub

			}
		}
		
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- End of KOHLSUpdateContainerTrackingNoAPI updateContainerTracking method -- >");
		}
		
		return docOutput;
	}

	private Document callPackShipmentOrUnPackShipment(YFSEnvironment env,
			Document docInput, boolean bRemoveContainer) throws Exception {
		
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Beginning of KOHLSUpdateContainerTrackingNoAPI callPackShipmentOrUnPackShipment method -- >");
		}
		
		Document docOut = null;
		if (bRemoveContainer) {
			env.setApiTemplate(KohlsConstant.API_UN_PACK_SHIPMENT,
					KohlsConstant.API_PACK_SHIPMENT_TEMPLATE_PATH);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input for unpackShipment : \n"
						+ XMLUtil.getXMLString(docInput));
			}
			
			docOut = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_UN_PACK_SHIPMENT, docInput);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Output for unpackShipment : \n"
						+ XMLUtil.getXMLString(docOut));
			}
			
			env.clearApiTemplate(KohlsConstant.API_UN_PACK_SHIPMENT);

		} else {
			env.setApiTemplate(KohlsConstant.API_PACK_SHIPMENT,
					KohlsConstant.API_PACK_SHIPMENT_TEMPLATE_PATH);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input for packShipment : \n"
						+ XMLUtil.getXMLString(docInput));
			}
			
			docOut = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_PACK_SHIPMENT, docInput);
			
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Output for packShipment : \n"
						+ XMLUtil.getXMLString(docOut));
			}
			
			env.clearApiTemplate(KohlsConstant.API_PACK_SHIPMENT);
		}

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- End of KOHLSUpdateContainerTrackingNoAPI callPackShipmentOrUnPackShipment method -- >");
		}
		
		// TODO Auto-generated method stub
		return docOut;
	}

	private Element getMatchedContainerElem(String strContainerScm,
			Element eleOutGetShipmentList) throws Exception {
		
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Beginning of KOHLSUpdateContainerTrackingNoAPI getMatchedContainerElem method -- >");
		}
		
		Element eleOut = null;
		eleOut = (Element) XPathUtil.getNodeList(
				eleOutGetShipmentList,
				"/Shipments/Shipment/Containers/Container[@ContainerScm='"
						+ strContainerScm + "']").item(0);

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- End of KOHLSUpdateContainerTrackingNoAPI getMatchedContainerElem method -- >");
		}
		
		return eleOut;
	}
}