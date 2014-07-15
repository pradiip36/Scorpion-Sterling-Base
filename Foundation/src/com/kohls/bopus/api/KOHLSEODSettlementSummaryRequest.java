package com.kohls.bopus.api;

/*****************************************************************************
 * File Name    : KOHLSEODSettlementSummaryRequest.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Feb 10,2014    Ashalatha        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This extended api is called to 
 * filter the records from KL_HARD_TOTALS table
 * based on Transation Start Date and Transaction End Date
 * 
 * Expected input xml:
 * <KLHardTotals TransactionStartDate="2014-02-10T15:20:00" TransactionEndDate="2014-02-05T16:20:00"/>
 * 
 */


public class KOHLSEODSettlementSummaryRequest{

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSEODSettlementSummaryRequest.class.getName());
	
	

	public Document createComplexQuery(YFSEnvironment env, Document doc){

		try{

			log.debug("Input xml to createComplexQuery : "+XMLUtil.getXMLString(doc));

			Element eleDoc=doc.getDocumentElement();
			String strStartDate = eleDoc.getAttribute(KohlsXMLLiterals.TRANSASCTION_START_DATE);
			String strEndDate = eleDoc.getAttribute(KohlsXMLLiterals.TRANSASCTION_END_DATE);
			
			//Set env variables 
			env.setTxnObject(KohlsXMLLiterals.TRANSASCTION_START_DATE, strStartDate);
			env.setTxnObject(KohlsXMLLiterals.TRANSASCTION_END_DATE, strEndDate);

			Document getHardTotalsListInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_KL_HARD_TOTALS);
			Element rootElem= getHardTotalsListInDoc.getDocumentElement();
			Element ComplexQueryElem = XmlUtils.createChild(rootElem, KohlsXMLLiterals.COMPLEX_QUERY);
			ComplexQueryElem.setAttribute(KohlsXMLLiterals.OPERATOR,KohlsConstant.AND_OPERATOR);


			Element StartDate_ExpElem = XmlUtils.createChild(ComplexQueryElem, KohlsXMLLiterals.EXP);
			StartDate_ExpElem.setAttribute(KohlsXMLLiterals. A_NAME, KohlsXMLLiterals.A_TRAN_DATE);
			StartDate_ExpElem.setAttribute(KohlsXMLLiterals.A_VALUE, strStartDate);
			StartDate_ExpElem.setAttribute(KohlsXMLLiterals.QRY_TYPE, KohlsConstant.GT_OPERATOR);


			Element EndDate_ExpElem = XmlUtils.createChild(ComplexQueryElem, KohlsXMLLiterals.EXP);
			EndDate_ExpElem.setAttribute(KohlsXMLLiterals. A_NAME, KohlsXMLLiterals.A_TRAN_DATE);
			EndDate_ExpElem.setAttribute(KohlsXMLLiterals.A_VALUE, strEndDate);
			EndDate_ExpElem.setAttribute(KohlsXMLLiterals.QRY_TYPE, KohlsConstant.LE_OPERATOR);

			log.debug("Complex query to filter the records: "+XMLUtil.getXMLString(getHardTotalsListInDoc));

			return getHardTotalsListInDoc ;

		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method createComplexQuery : "+e.getStackTrace());

		}


	}

}
