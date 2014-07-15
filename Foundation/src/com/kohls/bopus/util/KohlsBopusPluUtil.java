package com.kohls.bopus.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsInvoiceUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;

public class KohlsBopusPluUtil {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsBopusPluUtil.class.getName());
	
	public Document constructPLURequestDocument(String shipNode, String itemid, String lookupType)
			throws ParserConfigurationException {
				
				Document pluInputDoc = SCXmlUtil.createDocument(KohlsConstant.PLU_MESSAGE);
				Element pluInputEle = pluInputDoc.getDocumentElement();
				SCXmlUtil.setAttribute(pluInputEle, KohlsXMLLiterals.A_TYPE_PLU, KohlsConstant.PLU);
				SCXmlUtil.setAttribute(pluInputEle, KohlsXMLLiterals.A_CONTENT, KohlsConstant.PLU);
				SCXmlUtil.setAttribute(pluInputEle, KohlsXMLLiterals.A_TYPE_REQUEST, KohlsConstant.REQUEST);
				
				Element componentEle = SCXmlUtil.createChild(pluInputEle,
						KohlsXMLLiterals.E_COMPONENT);
				Element parametersEle = SCXmlUtil.createChild(componentEle,
						KohlsXMLLiterals.E_PARAMETERS);
				SCXmlUtil.setAttribute(parametersEle, KohlsConstant.A_STORE, shipNode);
				
				String RequestDateTime = getCurrentDateString();
				
				
				SCXmlUtil.setAttribute(parametersEle, KohlsXMLLiterals.A_TRAN_DATE_TIME, RequestDateTime);
				SCXmlUtil.setAttribute(parametersEle, KohlsXMLLiterals.A_LOOKUPTYPE, lookupType);
				
				SCXmlUtil.setAttribute(parametersEle, KohlsXMLLiterals.A_SKU, itemid);
				

				SCXmlUtil.getString(pluInputDoc);
				return pluInputDoc;

	 }

	 public static String getCurrentDateString() {
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.DATE);
		Calendar cal = Calendar.getInstance();
		return  sdf.format(cal.getTime());
	}
}
