package com.kohls.sbc.item.safetyfactor;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.sbc.item.safetyfactor.KohlscheckTotalNoOfRecordsSafety;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlscheckTotalNoOfRecordsSafety implements YCPDynamicConditionEx {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlscheckTotalNoOfRecordsSafety.class.getName());
	private Map _properties;
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1, Map arg2, Document inDoc) {
		Document outDoc=null;
		int length=0;
		try {
			Element inEle=inDoc.getDocumentElement();
			String catDept=inEle.getAttribute(KohlsConstants.CAT_DEPARTMENT);
			String catClass=inEle.getAttribute(KohlsConstants.CAT_CLASS);
			String catSubClass=inEle.getAttribute(KohlsConstants.CAT_SUBCLASS);
			Document extnEligibilityDoc = SCXmlUtil.createDocument(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
			Element extnEligibileEle = extnEligibilityDoc.getDocumentElement();
			
			Element complexQry = extnEligibilityDoc.createElement(KohlsConstants.COMPLEX_QUERY);
			extnEligibileEle.appendChild(complexQry);
			complexQry.setAttribute(KohlsConstants.OPERATOR, KohlsConstants.AND);
			
			Element orElement1 = extnEligibilityDoc.createElement(KohlsConstants.OR);
			complexQry.appendChild(orElement1);
			
			Element andElement1 = extnEligibilityDoc.createElement(KohlsConstants.AND1);
			orElement1.appendChild(andElement1);
			
			Element expElement1 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
			expElement1.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_DEPARTMENT );
			expElement1.setAttribute(KohlsConstants.VALUE, catDept);
			expElement1.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			andElement1.appendChild(expElement1);
		
			Element expElement2 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
			expElement2.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_CLASS );
			expElement2.setAttribute(KohlsConstants.VALUE, catClass);
			expElement2.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			andElement1.appendChild(expElement2);
			
			Element expElement3= extnEligibilityDoc.createElement(KohlsConstants.EXP);
			expElement3.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_SUBCLASS );
			expElement3.setAttribute(KohlsConstants.VALUE, catSubClass);
			expElement3.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			andElement1.appendChild(expElement3);
			
			
			 outDoc = KohlsCommonUtil.invokeService(env,KohlsConstants.KOHLS_ITEM_SAFTY_FACTOR_LIST, extnEligibilityDoc);
			
			Element ele=outDoc.getDocumentElement();
			NodeList nodeList=ele.getElementsByTagName(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
			length=nodeList.getLength();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		String strNoOfRecords = (String)_properties.get("NoOfRecords");	
		int int1 =Integer.parseInt(strNoOfRecords);
		env.setTxnObject("inDoumentEnv1", inDoc);
		env.setTxnObject("outDoumentEnv1", outDoc);
		
		if (length == int1)
		{
		return true;
		}
		else 
		return false;
		
		
	}

	@Override
	public void setProperties(Map prop) {
		_properties=prop;

	}

}
