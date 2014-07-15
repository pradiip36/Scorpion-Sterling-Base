package com.kohls.sbc.item.eligibility;

import java.util.Enumeration;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.omg.CORBA._PolicyStub;
import org.w3c.dom.Document;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.api.ycp.util.getProperty;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KohlscheckTotalNoOfRecordsEligibility implements YCPDynamicConditionEx {
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlscheckTotalNoOfRecordsEligibility.class.getName());
	private Map _properties;
	public boolean evaluateCondition(YFSEnvironment env, String strConditionName, Map mapData, Document inDoc){
	
		Document outDoc=null;
		int length=0;
		try {
			Element inEle=inDoc.getDocumentElement();
			String catDept=inEle.getAttribute(KohlsConstants.CAT_DEPARTMENT);
			String catClass=inEle.getAttribute(KohlsConstants.CAT_CLASS);
			String catSubClass=inEle.getAttribute(KohlsConstants.CAT_SUBCLASS);
			String vendor=inEle.getAttribute(KohlsConstants.VENDORID);
			String style=inEle.getAttribute(KohlsConstants.STYLE);
			Document extnEligibilityDoc = SCXmlUtil.createDocument(KohlsConstants.EXTN_ELIGIBLE);
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
			
			Element orElement2 = extnEligibilityDoc.createElement(KohlsConstants.OR);
			orElement1.appendChild(orElement2);
			
			Element expElement4= extnEligibilityDoc.createElement(KohlsConstants.EXP);
			expElement4.setAttribute(KohlsConstants.NAME, KohlsConstants.VENDORID );
			expElement4.setAttribute(KohlsConstants.VALUE, vendor);
			expElement4.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			orElement2.appendChild(expElement4);
			
			Element orElement3 = extnEligibilityDoc.createElement(KohlsConstants.OR);
			orElement1.appendChild(orElement3);
			
			Element expElement5= extnEligibilityDoc.createElement(KohlsConstants.EXP);
			expElement5.setAttribute(KohlsConstants.NAME, KohlsConstants.STYLE );
			expElement5.setAttribute(KohlsConstants.VALUE, style);
			expElement5.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			orElement3.appendChild(expElement5);
			
			 outDoc = KohlsCommonUtil.invokeService(env,KohlsConstants.KOHLS_GET_ITEM_ELIGIBILITY, extnEligibilityDoc);
			
			Element ele=outDoc.getDocumentElement();
			NodeList nodeList=ele.getElementsByTagName(KohlsConstants.ITEM_ELIGIBILITY);
			length=nodeList.getLength();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
		String strNoOfRecords = (String)_properties.get("NoOfRecords");	
		int int1 =Integer.parseInt(strNoOfRecords);
		env.setTxnObject("inDoumentEnv", inDoc);
		env.setTxnObject("outDoumentEnv", outDoc);
		
		if (length == int1)
		{
		return true;
		}
		else 
		return false;
		
	}


		
		
	

	public void setProperties(Map prop) {		
		_properties  = prop;		
	}

}
