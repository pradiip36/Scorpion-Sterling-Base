package com.kohls.ibm.ocf.pca.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSInputXMLUtils {
	
	//Input xml for translare bar code api
	
	public static Document getInputForTranslateBarCodeApi(String barCodeData,
			String organizationCode, String enterprise) {
		Document doc = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_BAR_CODE);
		Element rootElem = doc.getDocumentElement();
			
		rootElem.setAttribute(KOHLSPCAConstants.A_BAR_CODE_DATA,barCodeData);
		rootElem.setAttribute(KOHLSPCAConstants.A_BAR_CODE_TYPE,KOHLSPCAConstants.V_ITEM);
		Element contextualInfo = YRCXmlUtils.createChild(rootElem,KOHLSPCAConstants.E_CONTEXTUALINFO);
		if(!YRCPlatformUI.isVoid(organizationCode))
			contextualInfo.setAttribute(KOHLSPCAConstants.A_ORGANIZATION_CODE,organizationCode);
		if(!YRCPlatformUI.isVoid(enterprise))
			contextualInfo.setAttribute(KOHLSPCAConstants.A_ENTERPRISE_CODE,enterprise);
		return doc;
	}
	
	public static Document getInputForItemList(String strItemID, String callingOrganizationCode, String strItemGroupCode ){
		
		Document docItem = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_ITEM);
		Element eItem = docItem.getDocumentElement();		
		eItem.setAttribute(KOHLSPCAConstants.A_ITEM_ID,strItemID);	
		eItem.setAttribute(KOHLSPCAConstants.A_CALLING_ORGANIZATION_CODE,callingOrganizationCode);
		eItem.setAttribute(KOHLSPCAConstants.A_ITEM_GROUP_CODE, strItemGroupCode);
		return docItem;
		
	}


}
