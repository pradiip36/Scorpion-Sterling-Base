package com.kohls.bopus.condition;

/*****************************************************************************
 * File Name    : KohlsBOPUSCheckBopusLine.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Mar 5,2014    Ashalatha        Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * This condition class is added to stop the 
 * demand update to GIV for BOPUS lines on create Order. 
 *   
 */

public class KohlsBOPUSCheckBopusLine implements YCPDynamicConditionEx{

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1,
			Map map, Document doc) {

		boolean sendDemandUpdate = false;
		String bopus_Line_str = KohlsConstant.A_BLANK;
		if(!YFCCommon.isVoid(env.getTxnObject(KohlsConstant.BOPUS_LINES))){
			bopus_Line_str = (String)env.getTxnObject(KohlsConstant.BOPUS_LINES);
		}
		Element eleDemand = doc.getDocumentElement();
		String strPrimeLineNo=eleDemand.getAttribute(KohlsXMLLiterals.PRIME_LINE_NO);

		if(bopus_Line_str!=null && bopus_Line_str.contains(strPrimeLineNo)){
			sendDemandUpdate = false;
		}else
			sendDemandUpdate = true;

		return sendDemandUpdate;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}


}
