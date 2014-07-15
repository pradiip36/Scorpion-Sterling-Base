package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class sets PrintPackSlip true in context
 * 
 * 
 *
 */
public class KOHLSSetPrintPackSlipTrueContext {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSSetPrintPackSlipTrueContext.class.getName());
	
	/**
	 * This method sets PrintPackSlip true in context
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public Document setPrintPackSlip(YFSEnvironment env,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSSetPrintPackSlipTrueContext" +
					" setPrintPackSlip method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			env.setTxnObject(KohlsConstant.O_PRINT_PACK_SLIP, "true");
			
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSSetPrintPackSlipTrueContext" +
					" setPrintPackSlip method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		return inputDoc;
	}
	
}