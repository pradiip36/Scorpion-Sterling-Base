package com.kohls.sbc.item.eligibility;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsCommonUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class createEligibilityRecord {
	public static Document createEligMethod(YFSEnvironment env,Document inDoc){
		
		Document outDoc1=null;
		Document outDoc=(Document)env.getTxnObject("inDoumentEnv");
		try {
			 outDoc1=KohlsCommonUtil.invokeService(env, "KohlsCreateItemEligibility", outDoc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outDoc1;
		
	}
	
	
}
