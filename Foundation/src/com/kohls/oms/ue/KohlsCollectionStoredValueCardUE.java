package com.kohls.oms.ue;

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
import com.yantra.yfs.japi.ue.YFSCollectionStoredValueCardUE;

public class KohlsCollectionStoredValueCardUE implements YFSCollectionStoredValueCardUE{

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCollectionStoredValueCardUE.class.getName());
	public KohlsCollectionStoredValueCardUE() throws YIFClientCreationException{
		api = YIFClientFactory.getInstance().getApi();
	}
	
	KohlsPaymentUtil paymentUtil = new KohlsPaymentUtil();
	public YFSExtnPaymentCollectionOutputStruct collectionStoredValueCard(
			YFSEnvironment env, YFSExtnPaymentCollectionInputStruct inStruct)
	throws YFSUserExitException {
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<------------ Begining of collectionStoredValueCard -------------------->");
		}
		YFSExtnPaymentCollectionOutputStruct outStruct = new YFSExtnPaymentCollectionOutputStruct();
		
		try {		
			if(KohlsConstant.CHARGE_TYPE_CHARGE.equals(inStruct.chargeType)) {		
 			    paymentUtil.processSettlementSVCCard(env,inStruct,outStruct);
			}
		}catch (Exception e) {
			log.error(e);
			YFSUserExitException ex = new YFSUserExitException();
			throw ex;
		}
		
		return outStruct;
		
		
	}


}


