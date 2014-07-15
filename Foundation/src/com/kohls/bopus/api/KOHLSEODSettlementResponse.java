package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KOHLSEODSettlementResponse.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Feb 10,2014    Ashalatha        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This extended api is called to 
 * to form the EOD_Setllement Summary response message 
 * from the reocrds filtered from KL_HARD_TOTALS
 * 
 */


public class KOHLSEODSettlementResponse {

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSEODSettlementResponse.class.getName());
	
	
	/**
	 * This method is called to create the 
	 * EODSettlement Summary response 
	 * 
	 * Sample response message :
	 * 
	 * 	<?xml version="1.0" encoding="UTF-8"?>
	 * 	<OMS-EOD-RECORD>
	 *  <FILE-CREATION-DATE>2014-02-11T16:00:23</FILE-CREATION-DATE>
	 *  <START-DATE-TIME>2014-02-10T15:20:00</START-DATE-TIME>
	 * 	<END-DATE-TIME>2014-02-05T16:20:00</END-DATE-TIME>
	 *  <TOTAL-VISA-SALE-AMOUNT>200.0</TOTAL-VISA-SALE-AMOUNT>
	 *  <TOTAL-VISA-REFUND-AMOUNT>0.00</TOTAL-VISA-REFUND-AMOUNT>
	 *  <TOTAL-MASTERCARD-SALE-AMOUNT>400.0</TOTAL-MASTERCARD-SALE-AMOUNT>
	 *  <TOTAL-MASTERCARD-REFUND-AMOUNT>0.00</TOTAL-MASTERCARD-REFUND-AMOUNT>
	 *  <TOTAL-DISCOVERY-SALE-AMOUNT>600.0</TOTAL-DISCOVERY-SALE-AMOUNT>
	 *  <TOTAL-DISCOVERY-REFUND-AMOUNT>0.00</TOTAL-DISCOVERY-REFUND-AMOUNT>
	 *  <TOTAL-AMEX-SALE-AMOUNT>800.0</TOTAL-AMEX-SALE-AMOUNT>
	 *  <TOTAL-AMEX-REFUND-AMOUNT>0.00</TOTAL-AMEX-REFUND-AMOUNT>
	 *  <TOTAL-KOHLS_CHARGE-SALE-AMOUNT>1000.0</TOTAL-KOHLS_CHARGE-SALE-AMOUNT>
	 *  <TOTAL-KOHLS_CHARGE-REFUND-AMOUNT>0.00</TOTAL-KOHLS_CHARGE-REFUND-AMOUNT>
	 *  </OMS-EOD-RECORD>
	 * 
	 * 
	 * 
	 */

	public Document createResponse(YFSEnvironment env, Document doc){

		Document docOmsEodRecord = null;
		
		String strTransactionStartDate = (String)env.getTxnObject(KohlsXMLLiterals.TRANSASCTION_START_DATE);
		String strTransactionEndDate = (String)env.getTxnObject(KohlsXMLLiterals.TRANSASCTION_END_DATE);
		
		try{

			log.debug("Input xml to createResponse : "+XMLUtil.getXMLString(doc));
			
			Calendar today = Calendar.getInstance();  
			SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.TIMESTAMP_DATEFORMAT);
			String transactionDate = sdf.format(today.getTime());		
			

			double totAmntChargedToVisaCard = 0.0;
			double totAmntChargedToMasterCard = 0.0;
			double totAmntChargedToDiscoverCard = 0.0;
			double totAmntChargedToAmexCard = 0.0;
			double totAmntChargedToKohlsChrgCard = 0.0;
			ArrayList<Element> kLHardTotalsList= null;
			Iterator<Element> itrkLHardTotals = null;
			Element eleKLHardTotals = null;

			kLHardTotalsList = SCXmlUtil.getChildren(doc.getDocumentElement(), KohlsXMLLiterals.E_KL_HARD_TOTALS);
			itrkLHardTotals = kLHardTotalsList.iterator();

			while(itrkLHardTotals.hasNext()){
				eleKLHardTotals = itrkLHardTotals.next();
				totAmntChargedToVisaCard += Double.parseDouble(eleKLHardTotals.getAttribute(KohlsXMLLiterals.VISA_SALE_AMOUNT));
				totAmntChargedToMasterCard += Double.parseDouble(eleKLHardTotals.getAttribute(KohlsXMLLiterals.MASTERCARD_SALE_AMOUNT));
				totAmntChargedToDiscoverCard += Double.parseDouble(eleKLHardTotals.getAttribute(KohlsXMLLiterals.DISCOVERY_SALE_AMOUNT));
				totAmntChargedToAmexCard += Double.parseDouble(eleKLHardTotals.getAttribute(KohlsXMLLiterals.AMEX_SALE_AMOUNT));
				totAmntChargedToKohlsChrgCard += Double.parseDouble(eleKLHardTotals.getAttribute(KohlsXMLLiterals.KOHLS_CHARGE_SALE_AMOUNT)); 
			}
			
			docOmsEodRecord = SCXmlUtil.createDocument(KohlsXMLLiterals.OMS_EOD_RECORD);
			
			Element eleOmsEodRecord = docOmsEodRecord.getDocumentElement();
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.FILE_CREATION_DATE).setTextContent(transactionDate);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.START_DATE_TIME ).setTextContent(strTransactionStartDate);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.END_DATE_TIME).setTextContent(strTransactionEndDate);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_VISA_SALE_AMOUNT).setTextContent(String.valueOf(totAmntChargedToVisaCard));
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_VISA_REFUND_AMOUNT).setTextContent(KohlsConstant.zero);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_MASTERCARD_SALE_AMOUNT).setTextContent(String.valueOf(totAmntChargedToMasterCard));
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_MASTERCARD_REFUND_AMOUNT).setTextContent(KohlsConstant.zero);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_DISCOVERY_SALE_AMOUNT).setTextContent(String.valueOf(totAmntChargedToDiscoverCard));
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_DISCOVERY_REFUND_AMOUNT).setTextContent(KohlsConstant.zero);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_AMEX_SALE_AMOUNT).setTextContent(String.valueOf(totAmntChargedToAmexCard));
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_AMEX_REFUND_AMOUNT).setTextContent(KohlsConstant.zero);
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_KOHLS_CHARGE_SALE_AMOUNT).setTextContent(String.valueOf(totAmntChargedToKohlsChrgCard));
			SCXmlUtil.createChild(eleOmsEodRecord, KohlsXMLLiterals.TOTAL_KOHLS_CHARGE_REFUND_AMOUNT).setTextContent(KohlsConstant.zero);
			
		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method createResponse : "+e.getStackTrace());

		}


		log.debug("EOD_SettlementSummary Response "+SCXmlUtil.getString(docOmsEodRecord));		

		return docOmsEodRecord;
	}
}



