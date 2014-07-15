package com.kohls.bopus.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSCommonCodeList {

	// Method to obtain Status Code Value for BOPUS Order-Shipment pipe line
	public static String statusValue(YFSEnvironment env, String strStatName, String strDocument) throws Exception {
		
		String strCodeType = null;
		String strStatusValue = null;
		if (strDocument.equals(KohlsConstant.CODE_TYPE_ORD))
			strCodeType = KohlsConstant.V_CODE_TYPE_ORD;
		else 
			strCodeType = KohlsConstant.V_CODE_TYPE_SHP;

		Document docCommoncode = SCXmlUtil.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		Element eleCommonCode = docCommoncode.getDocumentElement();
		eleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, strCodeType);
		eleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION, strStatName);
		
		Document docCommonCodeListOutput = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_COMMON_CODE_LIST_TEMPLATE, KohlsConstant.API_GET_COMMON_CODE_LIST, docCommoncode);
		
		if (docCommonCodeListOutput != null) {
			Element eleCommonCodeListOutput = docCommonCodeListOutput.getDocumentElement();
			Element eleCommonCodeOutput = SCXmlUtil.getChildElement(eleCommonCodeListOutput, KohlsXMLLiterals.E_COMMON_CODE);
			strStatusValue  = eleCommonCodeOutput.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
			if (strStatusValue == null)
				strStatusValue=" ";
		}
		
		return strStatusValue;
		
	}
	
	// Method to obtain Status Name for BOPUS Order-Shipment pipe line
	public static String statusName(YFSEnvironment env, String strStatName, String strDocument) throws Exception {

		String strCodeType = null;
		String strStatusName = null;
		if (strDocument.equals(KohlsConstant.CODE_TYPE_ORD))
			strCodeType = KohlsConstant.V_CODE_TYPE_ORD;
		else 
			strCodeType = KohlsConstant.V_CODE_TYPE_SHP;

		Document docCommoncode = SCXmlUtil.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		Element eleCommonCode = docCommoncode.getDocumentElement();
		eleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, strCodeType);
		eleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION, strStatName);

		Document docCommonCodeListOutput = 
			KohlsCommonUtil.invokeAPI( env, KohlsConstant.API_GET_COMMON_CODE_LIST_TEMPLATE, KohlsConstant.API_GET_COMMON_CODE_LIST, docCommoncode);

		if (docCommonCodeListOutput != null) {
			Element eleCommonCodeListOutput = docCommonCodeListOutput.getDocumentElement();
			Element eleCommonCodeOutput = SCXmlUtil.getChildElement(eleCommonCodeListOutput, KohlsXMLLiterals.E_COMMON_CODE);
			strStatusName  = eleCommonCodeOutput.getAttribute(KohlsXMLLiterals.A_CODE_LONG_DESCRIPTION);
			if (strStatusName == null)
				strStatusName=" ";
		}

		return strStatusName;

	}
	
	public static Document getCCListAsXML(YFSEnvironment env,
			String commonCodeType) throws Exception {
		Document commonCodeOutXML = null;
		Document commonCodeInXML = null;

		// /1. Build Input XML
		commonCodeInXML = getCCInputXML(commonCodeType);
		// /2. Invoke API
		try {
			commonCodeOutXML = KOHLSBaseApi.invokeAPI(env, "getCommonCodeList",
					commonCodeInXML);

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		// /3. Return Output Doc
		return commonCodeOutXML;

	}
	
	/**
	 * <CommonCode CodeType="CREDIT_CARD" >
	 * </CommonCode>
	 * @return
	 */
	private static Document getCCInputXML(String commonCodeType) {
		Document commonCodeInXML = null;
		Element root = null;
		try {
			commonCodeInXML = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE)
					.getDocument();
			root = commonCodeInXML.getDocumentElement();
			root.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, commonCodeType);
		} finally {
		}
		return commonCodeInXML;
	}
	
	/**
	 * 
	 * @param env
	 * @param commonCodeType
	 * @param commonCodeValue
	 * @return
	 * @throws Exception
	 */
	public static String getCommonCodeDescription(YFSEnvironment env,
			String commonCodeType, String commonCodeValue) throws Exception {

		String commonCodeDescription = "";
		Document commonCodeOutXML = getCCListAsXML(env,	commonCodeType);
		Element eleShipmentLines = KohlsXPathUtil.getElementByXpath(
				commonCodeOutXML, "CommonCodeList/CommonCode[@CodeValue='"
						+ commonCodeValue + "']");
		commonCodeDescription = eleShipmentLines
				.getAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION);

		return commonCodeDescription;
	}
}
