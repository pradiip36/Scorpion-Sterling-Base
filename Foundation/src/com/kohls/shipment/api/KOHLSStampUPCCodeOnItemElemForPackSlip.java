package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSStampUPCCodeOnItemElemForPackSlip extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSStampUPCCodeOnItemElemForPackSlip.class.getName());

	public Document stampUPCCode(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception
			{


		Element eleInput = inputDoc.getDocumentElement();
		if(YFCObject.isVoid(eleInput.getAttribute(KohlsConstant.A_RECEIPT_ID)))
		{
			eleInput.setAttribute(KohlsConstant.A_RECEIPT_ID, "");
		}
		NodeList nlItemList = eleInput
		.getElementsByTagName(KohlsConstant.ELEM_ITEM);
		for (int intItemCount = 0; intItemCount < nlItemList.getLength(); intItemCount++) {
			Element eleItem = (Element) nlItemList.item(intItemCount);


			NodeList nlItemAliasList = eleItem
			.getElementsByTagName(KohlsConstant.A_ITEM_ALIAS);
			for (int intItemAliasCount = 0; intItemAliasCount < nlItemAliasList.getLength(); intItemAliasCount++) {
				Element eleItemAlias = (Element) nlItemAliasList.item(intItemAliasCount);


				String sAliasName = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_NAME);
				if(!YFCObject.isVoid(sAliasName) && sAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
				{
					// invoke getValidUPCCode(String strUPCCode) for a 12 character String value for the UPCCode
					eleItem.setAttribute(sAliasName, getValidUPCCode(eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_VALUE)));
				}

			}





		}
		return inputDoc;

			}
	/**
	 * This method takes the UPCCode ("AliasValue" of the corresponding Item "AliasName") as an argument and
	 * if the length of the String value is less than 12 then,
	 * it adds zeros at the begining of the String to make it a 12 character String value and return.
	 * 
	 * @param strUPCCode ("AliasValue" of the corresponding Item "AliasName")
	 * @return strUPCCode (a 12 character String value)
	 * 
	 */
	private String getValidUPCCode(String strUPCCode){
		int iUPCCodeLength = strUPCCode.length();
		if (iUPCCodeLength < 12){
			for (int count = 0; count < 12 - iUPCCodeLength; count++) {
				strUPCCode = "0" + strUPCCode;
			}
		}
		return strUPCCode;
	}
}