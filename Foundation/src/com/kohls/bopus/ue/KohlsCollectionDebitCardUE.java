package com.kohls.bopus.ue;

import com.kohls.bopus.util.KohlsPaymentUtil;
import com.kohls.common.util.KohlsConstant;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionInputStruct;
import com.yantra.yfs.japi.YFSExtnPaymentCollectionOutputStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSCollectionDebitCardUE;

public class KohlsCollectionDebitCardUE implements YFSCollectionDebitCardUE{

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCollectionDebitCardUE.class.getName());

	KohlsPaymentUtil paymentUtil = new KohlsPaymentUtil();


	public KohlsCollectionDebitCardUE() throws YIFClientCreationException{
		api = YIFClientFactory.getInstance().getApi();
	}

	/** 
	 *  
	 *  @author Baijayanta Bhattacharjee
	 *  
	 *  
	 *  
	 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.

	 **/
	public  YFSExtnPaymentCollectionOutputStruct collectionDebitCard 
	(YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct) throws YFSUserExitException {

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<------------ Begining of KohlsCollectionDebitCardUE -------------------->");
		}
		
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
			
		try { 
			if (KohlsConstant.CHARGE_TYPE_AUTH.equalsIgnoreCase(inStruct.chargeType)) {
				log.debug("Charge Type is Authorization....");
				log.debug("auth id:"+ inStruct.authorizationId);
					
				paymentUtil.authorizePayment(env, inStruct, outStruct);
					
			} else if(KohlsConstant.CHARGE_TYPE_CHARGE.equals(inStruct.chargeType)) {		
													
 			    paymentUtil.processSettlement(env,inStruct,outStruct);
			}
		
		   }
			catch (Exception e) {
				log.error(e);
				YFSUserExitException ex = new YFSUserExitException();
				throw ex;
			}
		
		
		return outStruct;
		
		
	}

}


