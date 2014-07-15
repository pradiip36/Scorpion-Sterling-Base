package com.kohls.bopus.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.bopus.util.KohlsOrderNotificationUtil;
import com.kohls.oms.ue.KohlsCollectionCreditCardUE;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class KohlsNotification {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCollectionCreditCardUE.class.getName());
	
	public void processPickedUpNotification(YFSEnvironment env,Document inXML) {
		
		Element eleDoc = inXML.getDocumentElement();
		Element eleExtn = XMLUtil.getChildElement(eleDoc, KohlsXMLLiterals.E_EXTN);
		boolean isCustNotifSent = KohlsOrderNotificationUtil.checkExtnCustNotificationSent(eleDoc, KohlsConstant.PICKUP_CONFIRMED);
		if(!isCustNotifSent) {
			try {
				KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_BPS_PICKEDUP_SYNC, inXML);
			} catch (Exception e) {
				log.error(e);
				YFSException ex = new YFSException();
				throw ex;
			}
		
		}
	 
	}
	
	public Document bopusOrderReadyForPick(YFSEnvironment env, Document inDoc){
		
		log.debug("in bopusOrderReadyForPick inDoc:"+SCXmlUtil.getString(inDoc));
		
		try {
			
			Element eleShipment = inDoc.getDocumentElement();
			
			String extnCustNotificationSent = null;
					
			if(eleShipment != null){
				extnCustNotificationSent = eleShipment.getAttribute(KohlsXMLLiterals.A_EXTN_CUST_NOTIFICATION_SENT);
				if(extnCustNotificationSent != null){
					boolean isCustNotifSent = KohlsOrderNotificationUtil.checkExtnCustNotificationSent(eleShipment, KohlsConstant.ORDER_READY_FOR_PICK);
					if(!isCustNotifSent);
				       KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_READY_FOR_CUST_MSG_TO_MRKTNG, inDoc);
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method bopusOrderReadyForPick() returned Error."+e);
		}
		
		return inDoc;
	}


}
