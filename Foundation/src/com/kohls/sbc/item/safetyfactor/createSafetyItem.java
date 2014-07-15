package com.kohls.sbc.item.safetyfactor;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsCommonUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class createSafetyItem {
public static Document createSafetyMethod(YFSEnvironment env,Document inDoc){
		
		Document outDoc1=null;
		Document outDoc=(Document)env.getTxnObject("inDoumentEnv1");
		try {
			 outDoc1=KohlsCommonUtil.invokeService(env, "KohlsCreateItemSafetyFactor", outDoc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return outDoc1;
		
	}
}
