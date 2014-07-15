/*****************************************************************************
 * File Name    : KohlsBeforeCreateOrderUE.java
 *
 * Description  : This class is called BeforeCreateOrderUE
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Oct 4,2010    Sudeepth Bollu         Initial Version
 * 0.00b Oct 5,2010    Sudeepth Bollu		  Dependency among gift wrap lines is replaced with stamping of PackListType value
 * 0.00c Oct 23,2010   Sudeepth Bollu		  PackListType value for Gift Lines and Ship Alone lines is stamped by Ecommerce. So, its logic is removed from the code.
 * 											  GiftWrap line's PrimeLineNo, ItemID are stamped as ExtnGiftPrimeLineNo, ExtnGiftItemID fields on the corresponding order lines to use them during line status updates to Ecommerce. 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

package com.kohls.oms.ue;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCheckOrderBeforeProcessingUE;



/**
 *  This class is added in Release A to Skip Authorization validation before Schedule so as to over the Problem of calling the createOrder
 *  and Schedule Order in the same Service.
 *  
 */
public class KohlsCheckOrderBeforeProcessingUE  implements YFSCheckOrderBeforeProcessingUE {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCheckOrderBeforeProcessingUE.class.getName());

	/**
	 * @param env
	 * YFS Environment
	 * @param inXML
	 * Input XML received from ECommerce
	 * @return
	 * Document with updated Elements and field values
	 * @throws YFSUserExitException
	 * Exception from the User Exit
	 */

	@Override
	public boolean checkOrderBeforeProcessing(YFSEnvironment arg0, Document inXML)
	throws YFSUserExitException {
		
		Element eleRoot = inXML.getDocumentElement();
		
		String sDocType = eleRoot.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);
		if(KohlsConstant.SO_DOCUMENT_TYPE.equals(sDocType))
		{
			String sTransactionId = eleRoot.getAttribute(KohlsXMLLiterals.A_TRANSACTIONID);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("Transaction Id " + sTransactionId);
			}

			if(KohlsConstant.TRAN_ID_SCHEDULE_ORDER.equalsIgnoreCase(sTransactionId) || 
					KohlsConstant.TRAN_ID_RELEASE_ORDER.equalsIgnoreCase(sTransactionId) ||
					KohlsConstant.TRAN_ID_CHAINED_ORDER_CREATE.equalsIgnoreCase(sTransactionId)){
				return true;
			}			
			return false;
		}else{

			return true;
		}

	}

}
