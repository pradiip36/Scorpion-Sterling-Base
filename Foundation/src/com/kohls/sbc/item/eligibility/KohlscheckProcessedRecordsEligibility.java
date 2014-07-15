package com.kohls.sbc.item.eligibility;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlscheckProcessedRecordsEligibility implements YCPDynamicConditionEx{
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlscheckProcessedRecordsEligibility.class.getName());

	
	private Map _properties;
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1, Map arg2, Document inDoc) {
		
		
		
		Document outDocProcessed=(Document)env.getTxnObject("outDoumentEnv");
		Element ele1=outDocProcessed.getDocumentElement();
		String str1=(String)_properties.get("Processed");
		NodeList nodeList=ele1.getElementsByTagName(KohlsConstants.ITEM_ELIGIBILITY);
		int length=nodeList.getLength();
		for(int i=0;i<length;i++)
		{
			Element ele2=(Element)nodeList.item(i);
			String str2=ele2.getAttribute(KohlsConstants.PROCESSED);
			String str3=ele2.getAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY);
			if(str2.equalsIgnoreCase(str1))
			{ 
			try{
				throw new RuntimeException("Record not yet Processed");
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
				return true;
			}
			try {
				Document inDoc1=KohlsXMLUtil.createDocument(KohlsConstants.ITEM_ELIGIBILITY);
				Element ele3=inDoc1.getDocumentElement();
				ele3.setAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY, str3);				
				Document outDoc=KohlsCommonUtil.invokeService(env, "deleteEligItems", inDoc1);
				
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return false;
		
	}

	@Override
	public void setProperties(Map prop) {
		 _properties=prop;
		
	}

}
