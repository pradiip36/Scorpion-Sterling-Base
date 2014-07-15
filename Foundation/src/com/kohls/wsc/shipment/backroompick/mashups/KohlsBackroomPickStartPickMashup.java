/**
 * 
 */
package com.kohls.wsc.shipment.backroompick.mashups;

//Static Import of Constants since Java 1.5. ~Adam
import static com.kohls.common.util.KohlsConstants.MASHUP_GET_MLS_GIV_DETAILS;
import static com.kohls.common.util.KohlsConstants.MASHUP_GET_USER_LIST;
import static com.kohls.common.util.KohlsConstants.MASHUP_SET_USER_NAME;
import static com.kohls.common.util.KohlsXMLLiterals.A_ASSIGNED_TO_USER_ID;
import static com.kohls.common.util.KohlsXMLLiterals.A_EXTN_USERNAME;
import static com.kohls.common.util.KohlsXMLLiterals.A_LOGIN_ID;
import static com.kohls.common.util.KohlsXMLLiterals.A_USERNAME;
import static com.kohls.common.util.KohlsXMLLiterals.E_EXTN;
import static com.kohls.common.util.KohlsXMLLiterals.E_SHIPMENT;
import static com.kohls.common.util.KohlsXMLLiterals.E_USER;
import static com.kohls.common.util.KohlsXMLLiterals.A_SHIPMENT_KEY;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.ibm.wsc.shipment.backroompick.mashups.BackroomPickStartPickMashup;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;

/*********************************************
 * @author: Robert Fea/Adam Dunmars
 * @description: This mashup class will call the service
 * to get details for MLS and GIV. It will also make a call
 * to the getUserList mashup to get the Username and dynamically 
 * add it to the Shipment/@ExtnUsername column.
 *********************************************/
public class KohlsBackroomPickStartPickMashup extends
		BackroomPickStartPickMashup {
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsBackroomPickStartPickMashup.class.getName());
	/**
	 * 
	 */
	public KohlsBackroomPickStartPickMashup() {
	}

	@Override
	/******************************************
	 * @author: Robert Fea/Adam Dunmars
	 * @description: This function massages the output by
	 * calling the MLS/GIV mashup service and the getUserList mashup API
	 * to get the MLS/GIV details and the Shipment/@ExtnUsername using 
	 * the UserList/User/@Username from the first returned User element.
	 *****************************************/
	public Element massageOutput(Element outEl,
			SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		
		// let the superclass take care of the shipment status
		Element outputElement = super.massageOutput(outEl, mashupMetaData, uiContext);
		
		// call our service MLS / GIV service
		outputElement = (Element) SCUIMashupHelper.invokeMashup(MASHUP_GET_MLS_GIV_DETAILS, outputElement, uiContext);
		log.debug("********getMLSGIVDetails Output**********");
		log.debug(SCXmlUtil.getString(outputElement));
		log.debug("********getMLSGIVDetails Output**********");
		String assignedToUserId = outputElement.getAttribute(A_ASSIGNED_TO_USER_ID);
		
		Document userInputDoc = SCXmlUtil.createDocument(E_USER);
		Element userElement = userInputDoc.getDocumentElement();
		userElement.setAttribute(A_LOGIN_ID, assignedToUserId);
		
		log.debug("*******getUserList Input**********");
		log.debug(SCXmlUtil.getString(userInputDoc));
		log.debug("*******getUserList Input**********");
		
		Element userListOutputElement = (Element) SCUIMashupHelper.invokeMashup(MASHUP_GET_USER_LIST, userElement, uiContext);
		Element userOutputElement = SCXmlUtil.getChildElement(userListOutputElement, E_USER);
		
		log.debug("*******getUserList Output**********");
		log.debug(SCXmlUtil.getString(userOutputElement));
		log.debug("*******getUserList Output**********");
		
		if(!YFCObject.isVoid(userOutputElement)) {
			String username = userOutputElement.getAttribute(A_USERNAME);
			String shipmentKey = outputElement.getAttribute(A_SHIPMENT_KEY);
			
			Document changeShipmentInputDoc = SCXmlUtil.createDocument(E_SHIPMENT);
			Element shipmentElement = changeShipmentInputDoc.getDocumentElement();
			shipmentElement.setAttribute(A_SHIPMENT_KEY, shipmentKey);
			
			Element extnElement = SCXmlUtil.createChild(shipmentElement, E_EXTN);
			extnElement.setAttribute(A_EXTN_USERNAME, username);
			Element changeShipmentOutputElement = (Element) SCUIMashupHelper.invokeMashup(MASHUP_SET_USER_NAME, shipmentElement, uiContext);
			
			Element outputExtnElement = SCXmlUtil.getChildElement(outputElement, E_EXTN);
			if(YFCObject.isVoid(outputExtnElement)) {
				outputExtnElement = SCXmlUtil.createChild(outputElement, E_EXTN);
			}
			outputExtnElement.setAttribute(A_EXTN_USERNAME, username);
		}
		return outputElement;
	}
}