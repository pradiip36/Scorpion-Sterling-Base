/**
 * 
 */
package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author ZSYED
 * 
 */
public class KOHLSTranslateBarCodeValidation extends KOHLSBaseApi {
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSTranslateBarCodeValidation.class.getName());

	private Document docOutput = null;

	/**
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	/*<BarCode BarCodeData="400983100375" BarCodeType="Item" MaxTranslations="0">
    <BarCodeTranslation AliasName="" BarCodeEndPosition="0"
        BarCodeLength="0" BarCodeStartPosition="0"
        BarCodeTranslationKey="2012061313254694468"
        BarCodeTranslationSource="ExternalSource" BarCodeType="Item"
        Description="Item Alias Validation"
        FlowKey="2012061312405294055" OrganizationCode="DEFAULT"
        TranslationSequence="0" VariableLengthFlag="Y"/>
    <ContextualInfo EnterpriseCode="DEFAULT" OrganizationCode="DEFAULT"/>
	</BarCode>*/
	
	public Document translateItemAlias(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("<!-- Begining of KOHLSTranslateBarCodeValidation"
					+ " translateItemAlias method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		String strItemAliasValue = "";
		log.debug("*****Input to translateItemAlias Method is*****"
				+ XMLUtil.getXMLString(inputDoc));
		if (!YFCObject.isVoid(inputDoc)) {
			this.log
					.debug("******Input to translateItemAlias has a valid input document**************");
			strItemAliasValue = inputDoc.getDocumentElement().getAttribute(
					KohlsConstant.A_BARCODE_DATA);
			if (!"".equals(strItemAliasValue)) {
				log.debug("******BarCode data scanned as an item has been retrieved**************"
								+ strItemAliasValue);
				
           //Strip leading zero, if any, in Scanned UPC code
				
				if (!"".equals(strItemAliasValue)) {
					
				try {
					strItemAliasValue= strItemAliasValue.replaceFirst ("^0*", "");
					}catch ( Exception ex)
					{
						ex.printStackTrace();
					}
				}
				
		   //Code ends
				
				
				Document docInputGetItemList = XMLUtil
						.createDocument(KohlsConstant.E_ITEM);
				Element eleItemAliasList = docInputGetItemList
						.createElement(KohlsConstant.A_ITEM_ALIAS_LIST);
				docInputGetItemList.getDocumentElement().appendChild(
						eleItemAliasList);

				Element eleItemAlias = docInputGetItemList
						.createElement(KohlsConstant.A_ITEM_ALIAS);
				eleItemAliasList.appendChild(eleItemAlias);
				eleItemAlias.setAttribute(KohlsConstant.A_ALIAS_VALUE, strItemAliasValue);
				log.debug("*****Input to getItemList API is ********************* "
								+ XMLUtil.getXMLString(docInputGetItemList));
				Document docOuputItemList = KOHLSBaseApi.invokeAPI(
						yfsEnvironment, KohlsConstant.API_GET_ITEM_LIST,
						KohlsConstant.API_GET_ITEM_LIST, docInputGetItemList);

				if (!YFCObject.isVoid(docOuputItemList)) {
					log.debug("********************* getItemList API output is ******************** "
									+ XMLUtil.getXMLString(docOuputItemList));

					NodeList nItems = docOuputItemList.getDocumentElement()
							.getChildNodes();
					int iItems = nItems.getLength();
					log.debug("******************* No. child nodes returned by getItemList API ************"
									+ iItems);
					if (iItems >= 1) {
						Element eContextualInfo = (Element) inputDoc
								.getDocumentElement().getElementsByTagName(
										KohlsConstant.A_CONTEXTUAL_INFO).item(0);
						Element eItem = (Element) nItems.item(0);
						log.debug("*************Valid Items have been obtained*******"
										+ XMLUtil.getElementXMLString(eItem));
						docOutput = XMLUtil.createDocument(KohlsConstant.A_BAR_CODE);
						docOutput.getDocumentElement().setAttribute(
								"BarCodeData", eItem.getAttribute(KohlsConstant.A_ITEM_ID));
						docOutput.getDocumentElement().setAttribute(
								"BarCodeType", KohlsConstant.E_ITEM);
						Element eleTranslations = docOutput
								.createElement(KohlsConstant.A_TRANSLATIONS);
						docOutput.getDocumentElement().appendChild(
								eleTranslations);
						eleTranslations.setAttribute(
								KohlsConstant.ATTR_TOT_NO_RECORDS,
								docOuputItemList.getDocumentElement()
										.getAttribute(KohlsConstant.A_TOTAL_ITEM_LIST));
						Element eleTranslation = docOutput
								.createElement(KohlsConstant.A_TRANSLATION);
						eleTranslations.appendChild(eleTranslation);
						Element eleItemContextualInfo = docOutput
								.createElement(KohlsConstant.A_ITEM_CONTEXTUAL_INFO);
						Element eleContextualInfo = docOutput
								.createElement(KohlsConstant.A_CONTEXTUAL_INFO);
						eleTranslation.appendChild(eleItemContextualInfo);
						eleTranslation.appendChild(eleContextualInfo);
						eleContextualInfo.setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE,
								eContextualInfo.getAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE));
						eleContextualInfo.setAttribute(KohlsConstant.A_ORGANIZATION_CODE,
								eContextualInfo
										.getAttribute(KohlsConstant.A_ORGANIZATION_CODE));
						eleItemContextualInfo.setAttribute(KohlsConstant.A_INVENTORY_UOM,
								KohlsConstant.UNIT_OF_MEASURE);
						eleItemContextualInfo.setAttribute(KohlsConstant.A_ITEM_ID, eItem
								.getAttribute(KohlsConstant.A_ITEM_ID));
						log.debug("*************output of translateItemAlias getting formed is ******* "
										+ XMLUtil.getXMLString(docOutput));
						return docOutput;
					} else {
						log.debug("*************no valid item found, sending back the input for other translateBarCode validations ******* "
								+ XMLUtil.getXMLString(inputDoc));
						return inputDoc;
					}
				}
			}
		}
		return docOutput;
	}
}
