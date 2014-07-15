package com.kohls.bopus.condition;

/*****************************************************************************
 * File Name    : KohlsBOPUSCusFinalPickReminderCondition.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Feb 28,2014    Ashalatha        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.bopus.util.KOHLSCommonCodeList;
import com.kohls.bopus.util.KohlsOrderNotificationUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsInvoiceUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXPathUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * If the current time reaches the cutoff(1 day)time before the 
 * requested Cancel date, then send the pick up reminder notification
 *  
 */
public class KohlsBOPUSCusFinalPickReminderCondition implements YCPDynamicConditionEx {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInvoiceUtil.class.getName());
	private Map propertiesMap = new HashMap();

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1,
			Map arg2, Document inXML) {

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("******** Input to KohlsBOPUSCusFinalPickReminderCondition *************"+XMLUtil.getXMLString(inXML));
		}

		boolean sendPickupReminder = false;
		try{
			Element eleShipment = inXML.getDocumentElement();
			String strStatus=eleShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
			
			//if(strStatus.equalsIgnoreCase(KohlsConstant.STATUS_READY_FOR_CUSTOMER)){
			if(strStatus.equalsIgnoreCase(KOHLSCommonCodeList.statusValue(env,KohlsConstant.CC_DESC_READY_FOR_CUSTOMER, KohlsConstant.CODE_TYPE_SHP))){
				Element eleShipmentLine = KohlsXPathUtil.getElementByXpath(inXML, KohlsXMLLiterals.XPATH_SHIPMENT_LINE);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("************** eleShipmentLine *************"+XMLUtil.getElementXMLString(eleShipmentLine));
				}
				Element eleOrderLine=(Element)eleShipmentLine.getElementsByTagName(KohlsConstant.E_ORDER_LINE).item(0);
				String strReqCancelDate = eleOrderLine.getAttribute(KohlsXMLLiterals.REQ_CANCEL_DATE);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("************* strReqCancelDate *************"+strReqCancelDate);
				}
				SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.TIMESTAMP_DATEFORMAT);
				Date reqCancelDate = sdf.parse(strReqCancelDate);

				Date today = new Date();
				long reqCancelTime = reqCancelDate.getTime();
				long todayTime = today.getTime();


				long diffTime = reqCancelTime - todayTime;				
				long diffDays= diffTime/(24 *60 * 60 * 1000);

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("******* Difference in days between today and canceldate********"+diffDays);
				}
				String cutOffDay = getProperty(KohlsConstant.FINAL_PICKUP_REMINDER_DAYS );
				long pickupReminderCutoff=Long.parseLong(cutOffDay);
				if(diffDays==pickupReminderCutoff){
					sendPickupReminder = true;
				}
			}
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in evaluateCondition method is: "+e.getMessage());
		}
		return sendPickupReminder;
	}

	/**
	 * 
	 * @param key key
	 * @return result result
	 */
	private final String getProperty(final String key) {
		String result = null;
		if (!propertiesMap.isEmpty()) {

			result = (String) (propertiesMap.get(key));
		}
		return result;
	}

	@Override
	public void setProperties(Map gpropertiesMap) {

		propertiesMap = new HashMap(gpropertiesMap);

	}
}
