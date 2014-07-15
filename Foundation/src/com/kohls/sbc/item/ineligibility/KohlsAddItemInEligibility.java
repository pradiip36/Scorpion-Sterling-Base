/**
 * 
 */
package com.kohls.sbc.item.ineligibility;

/**
 * 
 */


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author pkrishnaswamy
 *
 */
public class KohlsAddItemInEligibility {

	public void addItemIneligibility(YFSEnvironment env, Document inDoc) throws Exception {

		Element rootElement = inDoc.getDocumentElement();

		NodeList inelgItemList = rootElement.getElementsByTagName(KohlsConstants.EXTN_ITEM_IN_ELIGIBILITY);

		int length = inelgItemList.getLength();

		for (int i = 0; i < length; i++) {
			Element inelgElement = (Element) inelgItemList.item(i);

			Document inDocForService = KohlsXMLUtil.getDocumentForElement(inelgElement);


			KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_ADD_ITEM_INELIGIBILITYU, inDocForService);

		}

	}
}