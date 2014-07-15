package com.kohls.bopus.condition;

/*****************************************************************************
 * File Name    : KohlsBOPUSCusPickReminderCondition.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a May 16,2014    Balakrishnan Rajenderan        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsDateUtil;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * If the current time reaches the cutoff(4 days)time before the 
 * requested Cancel date, then send the pick up reminder notification
 *  
 */

public class KohlsBOPUSShipmentTier3AlertCondition implements YCPDynamicConditionEx {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsBOPUSShipmentTier3AlertCondition.class.getName());
	private Map propertiesMap = new HashMap();

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1,
			Map arg2, Document inXML) {

		System.out.println(XMLUtil.getXMLString(inXML));
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("******** Input to KohlsBOPUSShipmentTier3AlertCondition *************"+XMLUtil.getXMLString(inXML));
		}

		boolean xHoursFlag = false;
		Element eleShipment = inXML.getDocumentElement();
		SimpleDateFormat sdf1 = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		
		
		String strExpectedShipDate = eleShipment
				.getAttribute("ExpectedShipmentDate");
		String strSystemDate = KohlsDateUtil
				.getCurrentDateTime(KohlsConstants.STERLING_TS_FORMAT);
	
		if ((strExpectedShipDate.length() > 20) && (strExpectedShipDate.substring(22, 23).equals(":"))) {
			String sDate1 = strExpectedShipDate.substring(0, 22);
			strExpectedShipDate = sDate1.concat(strExpectedShipDate.substring(23, 25));
						
		}

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("******** expectedShipDate *************"+strExpectedShipDate + "systemDate" + strSystemDate);
		}

		Date dtExpectedShipDate = null;
		Date dtSystemDate = null;
		
		try {
			dtExpectedShipDate = KohlsDateUtil.convertDate(sdf1.format(sdf2.parse(strExpectedShipDate)), "MM/dd/yyyy hh:mm a");
			dtSystemDate = KohlsDateUtil.convertDate(sdf1.format(sdf2.parse(strSystemDate)), "MM/dd/yyyy hh:mm a");
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (ParseException e2) {
			e2.printStackTrace();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("******** dtExpectedShipDate *************"+dtExpectedShipDate + "dtSystemDate" + dtSystemDate);
		}
		
		
		if (dtExpectedShipDate.after(dtSystemDate)) {

			try {

				Date dtSlotEndTime = KohlsDateUtil.addToDate(
						dtExpectedShipDate, Calendar.MINUTE, -290);
				Date dtSlotStartTime = KohlsDateUtil.addToDate(
						dtExpectedShipDate, Calendar.MINUTE, -300);
				
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("dtSystemDate.after(dtSlotStartTime)"
						+ dtSystemDate + ">" + dtSlotStartTime
						+ " && dtSystemDate.before(dtSlotEndTime)"
						+ dtSystemDate + ">" + dtSlotEndTime);
				}
				
				if (dtSystemDate.after(dtSlotStartTime)
						&& dtSystemDate.before(dtSlotEndTime)) {

					xHoursFlag = true;
				}
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return xHoursFlag;
		
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


	private void testWebserviceCall() throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
		
		YFSEnvironment env = null;
		File fileInput = new File("C:\\ShipmentMonitor_1.xml");
		Document inDoc = KohlsXMLUtil.getDocument(new FileInputStream(fileInput));
		boolean isTrue = this.evaluateCondition(env, null, null, inDoc);
	}
	

}
