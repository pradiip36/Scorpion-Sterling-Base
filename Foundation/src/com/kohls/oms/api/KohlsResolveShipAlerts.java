package com.kohls.oms.api;

/*****************************************************************************
 * File Name    : KohlsResolveShipAlerts.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Feb 25,2014    Juned S, Ashalatha         Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.xerces.xni.XMLString;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.kohls.bopus.util.KOHLSCommonCodeList;
import com.kohls.common.util.KOHLSBaseApi;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsLoggerUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsResolveShipAlerts implements YIFCustomApi{
	
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsResolveShipAlerts.class.getName());
	
	private Properties _props = null;
	
	boolean isMultiApiVoid = true;
	
	public Document resolveShpmntNotPckdTier2Alert(YFSEnvironment env, Document inDoc){
		
		log.debug("resolveShipmentAlerts:"+SCXmlUtil.getString(inDoc));
		
		String[] aryQueues = {YFSSystem.getProperties().getProperty(KohlsConstant.KOHLS_STORE_MANAGER_QUEUE), YFSSystem.getProperties().getProperty(KohlsConstant.KOHLS_STORE_ASSOCIATE_QUEUE)} ;
		
		Element eleIn = inDoc.getDocumentElement();
		
		Element eleShipment = SCXmlUtil.getChildElement(eleIn, KohlsXMLLiterals.E_SHIPMENT);
		
		String[] toEmailIDsAry = null;
		
		String toEmailIDs = _props.getProperty(KohlsConstant.TO_EMAIL_IDS);
		String toEmailSeparator = _props.getProperty(KohlsConstant.TO_EMAIL_SEPARATOR);
		
		if(toEmailIDs.indexOf(",") != -1){
			toEmailIDsAry = toEmailIDs.split(",");
			for(int i=0; i<toEmailIDsAry.length; i++){
				if(toEmailIDsAry[i].indexOf("<") != -1){	
					if(i==0)
						toEmailIDs = toEmailIDsAry[i].substring(0, toEmailIDsAry[i].indexOf("<")).concat(eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE)).concat(toEmailIDsAry[i].substring(toEmailIDsAry[i].indexOf(">")+1, toEmailIDsAry[i].length())).trim();
					else
						toEmailIDs += toEmailSeparator+toEmailIDsAry[i].substring(0, toEmailIDsAry[i].indexOf("<")).concat(eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE)).concat(toEmailIDsAry[i].substring(toEmailIDsAry[i].indexOf(">")+1, toEmailIDsAry[i].length())).trim();
				}
			}
		}
		
		eleShipment.setAttribute(KohlsConstant.FROM_EMAIL_ID, _props.getProperty(KohlsConstant.FROM_EMAIL_ID));
		eleShipment.setAttribute(KohlsConstant.TO_EMAIL_IDS, toEmailIDs);
		eleShipment.setAttribute(KohlsConstant.EMAIL_SUBJECT, _props.getProperty(KohlsConstant.EMAIL_SUBJECT));
		resolveAlerts(aryQueues, eleShipment, env);

		// Updated for Tier Flag Set
		Document inChangeShipmentDoc = YFCDocument.createDocument(
				KohlsXMLLiterals.E_SHIPMENT).getDocument();
		Element eChangeShipmentInput = inChangeShipmentDoc.getDocumentElement();
		eChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
				eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		
		eChangeShipmentInput.setAttribute(KohlsConstant.TMP_TIER_FLAG, KohlsConstant.V_TIER2);
		updateShipmentTierFlag(env, inChangeShipmentDoc);

		return inDoc;
		
	}
	
	public Document resolveShpmntNotPickedAlert(YFSEnvironment env, Document inDoc){

		log.debug("resolveShipmentAlerts:"+SCXmlUtil.getString(inDoc));
		System.out.println("Before Calling Update Shipment Details:" + XMLUtil.getXMLString(inDoc));
		
		Element eleShipment = null;

		Element eleIn = inDoc.getDocumentElement();
		eleShipment = SCXmlUtil.getChildElement(eleIn, KohlsXMLLiterals.E_SHIPMENT);
		
		String[] aryQueues = {YFSSystem.getProperties().getProperty(KohlsConstant.KOHLS_STORE_ASSOCIATE_QUEUE)} ;

		resolveAlerts(aryQueues, eleShipment, env);
		
		// Updated for Tier Flag Set

		Document inChangeShipmentDoc = YFCDocument.createDocument(
				KohlsXMLLiterals.E_SHIPMENT).getDocument();
		Element eChangeShipmentInput = inChangeShipmentDoc.getDocumentElement();
		eChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
				eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		
		eChangeShipmentInput.setAttribute(KohlsConstant.TMP_TIER_FLAG, KohlsConstant.V_TIER3);
		updateShipmentTierFlag(env, inChangeShipmentDoc);
		
		return inDoc;

	}
	
	public Document closeShipmentAlerts(YFSEnvironment env, Document inDoc){
       
		log.debug("closeShipmentAlerts:"+SCXmlUtil.getString(inDoc));
		
		Element eleShipment = null;
		
		String[] aryQueues = {""} ;
		
		eleShipment = inDoc.getDocumentElement();

		resolveAlerts(aryQueues, eleShipment, env);


		return inDoc;
	}
	
	
	
	/**
	 * This method update the Extn_Tiers_Alert_Flag with the respective flag to filter the shipment details 
	 * in UI. The system gets flag needs to be stamped from the custom common codes.
	 *  
	 * @param env
	 * @param inDoc
	 * @throws Exception 
	 */
	public void updateShipmentTierFlag(YFSEnvironment env, Document inDoc)
			{

		Element eleInputShipment = null;
		String strCommonCodeTierFlag = "";

		eleInputShipment = inDoc.getDocumentElement();
		String strShipmentKey = eleInputShipment
				.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
		String strTierFlag = eleInputShipment.getAttribute(KohlsConstant.TMP_TIER_FLAG);
		log.verbose("Tier Flag @ updateShipmentTierFlag:'"+strTierFlag+"'");

		if (strTierFlag != null && !("").equals(strTierFlag)) {
						
			try {
				
				strCommonCodeTierFlag = KOHLSCommonCodeList
						.getCommonCodeDescription(env, "TIER_FLAG", strTierFlag);
				
			} catch (Exception exp) {

				log.verbose(exp.getMessage());
				throw new YFSException(
						"The method resolveAlerts() returned Error."+ exp.getMessage());
			}

		}

		Document inChangeShipmentDoc = YFCDocument.createDocument(
				KohlsXMLLiterals.E_SHIPMENT).getDocument();
		Element eChangeShipmentInput = inChangeShipmentDoc.getDocumentElement();
		eChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
				strShipmentKey);
		Element eleExtn = SCXmlUtil.createChild(eChangeShipmentInput, KohlsXMLLiterals.E_EXTN);
		// temp fix for 172, until we figure out how to run complex query against extended attributes
//		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TIER_ALERTS_FLAG, strCommonCodeTierFlag);
		eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TIER_ALERTS_FLAG, KohlsConstant.V_Y);

		log.verbose("Change Shipment XML"
				+ XMLUtil.getXMLString(inChangeShipmentDoc));
		
		try {
			
			Document outDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_CHANGE_SHIPMENT,
					inChangeShipmentDoc);
		} catch (Exception exp) {
			log.verbose(exp.getMessage());
			throw new YFSException(
					"The method resolveAlerts() returned Error."+ exp.getMessage());
		}

	}
	
	
	public void resolveAlerts(String[] aryQueues, Element eleShipment, YFSEnvironment env){
		Document inDocGetExceptionList = SCXmlUtil.createDocument(KohlsXMLLiterals.E_INBOX);
		Element eleGetExceptionList = inDocGetExceptionList.getDocumentElement();
		eleGetExceptionList.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
		
		Element eleQueue = SCXmlUtil.createChild(eleGetExceptionList, KohlsXMLLiterals.A_QUEUE);

		Document docMultiApi = SCXmlUtil.createDocument(KohlsXMLLiterals.E_MULTI_API);
		Element eleMultiApi = docMultiApi.getDocumentElement();
		
		for(int i=0; i< aryQueues.length; i++){
			
			eleQueue.setAttribute(KohlsXMLLiterals.A_QUEUE_ID, aryQueues[i]);

			try {
				Document docOutGetExceptionList = KohlsCommonUtil.invokeAPI(env, KohlsConstant.GETEXCEPTIONLIST_API, inDocGetExceptionList);
				Element eleOutGetExceptionList = docOutGetExceptionList.getDocumentElement();

				ArrayList<Element> eleInboxList = SCXmlUtil.getChildren(eleOutGetExceptionList, KohlsXMLLiterals.E_INBOX);
				Iterator<Element> itrInboxList = eleInboxList.iterator();
				Element eleInbox = null;

				Element eleApi = null;			 
				Element eleInput = null;			 
				Element eleResolutionDetails = null;

				while(itrInboxList.hasNext()){
					eleInbox = itrInboxList.next();
					if(!KohlsConstant.CLOSED.equals(eleInbox.getAttribute(KohlsXMLLiterals.A_STATUS))){
						eleApi = SCXmlUtil.createChild(eleMultiApi, KohlsXMLLiterals.E_API);				 
						eleApi.setAttribute(KohlsXMLLiterals.A_NAME, KohlsXMLLiterals.A_RESOLVE_EXCEPTION);
						eleInput = SCXmlUtil.createChild(eleApi, KohlsXMLLiterals.E_INPUT);
						eleResolutionDetails = SCXmlUtil.createChild(eleInput, KohlsXMLLiterals.E_RESOLUTION_DETAILS);
						SCXmlUtil.importElement(eleResolutionDetails, eleInbox);
						isMultiApiVoid = false;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new YFSException(
						"The method resolveAlerts() returned Error."+e);
			}

		}
		
		if(!isMultiApiVoid && docMultiApi !=null){
			try {
				KohlsCommonUtil.invokeAPI(env, KohlsConstant.MULTIAPI_API, docMultiApi);
			} catch (Exception e) {
				e.printStackTrace();
				throw new YFSException(
						"The method resolveAlerts() returned Error."+e);
			}
		}
	}

	@Override
	public void setProperties(Properties _props) throws Exception {
		this._props = _props;
		
	}

}
