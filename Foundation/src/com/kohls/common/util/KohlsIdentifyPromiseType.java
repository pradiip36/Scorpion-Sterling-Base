/**
 * 
 */
package com.kohls.common.util;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * @author pkrishnaswamy
 * 
 */
public class KohlsIdentifyPromiseType {

	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsIdentifyPromiseType.class);
	/*
	 * This common utility will be used to identify the type of the order and which in turn will be the deciding 
	 * factor for the need to invoke the YDMGetTransportationCostUE for the fulfillment process.
	 * This util performs below tasks:
	 * 1)	Eliminate all the non-regular items from the input.
	 * 2)	Eliminate all nomadic items from the input
	 * 3)	Eliminate all ship alone items from the input
	 * 4)	If no items are left, then return PromiseType as "Others" which means, YDMGetTransportationCostUE will not be used.
	 * 5)	If there are more items, check if they are only "Store" eligible items. If not, then identify the PromiseType as "Mixed" which means it is a Mixed Order.
	 * 6)	If there are only Store items then identify PromiseType as "Store", which means, YDMGetTransportationCostUE will used

	 * @inDoc <PromiseLines> <PromiseLine ItemID="" ItemType="" ExtnNomadic=""
	 * ExtnShipAlone="" ExtnBreakable="" ExtnCage="" ExtnShipNodeSource=""
	 * UOM="" OrganizationCode=""/> </PromiseLines>
	 * 
	 * return Document <PromiseLines PromiseType="Store/Mixed"/>
	 */
	public static Document identifyPromiseType(YFSEnvironment env, Document inDoc) throws Exception {

		Element inputElement = inDoc.getDocumentElement();
		kohlsLogger.debug("Input: " + KohlsXMLUtil.getElementXMLString(inputElement));

		NodeList promiseLines = inputElement.getElementsByTagName(KohlsConstants.PROMISE_LINE);

		Element verifyElement = inDoc.createElement(KohlsConstants.PROMISE_LINES);

		int promiseFlag = 2;
		int length=promiseLines.getLength();
		for (int i = 0; i < length; i++) {
			Element promiseElement = (Element) promiseLines.item(i);

			String itemType = promiseElement.getAttribute(KohlsConstants.ITEM_TYPE);
			String isNomadic = promiseElement.getAttribute(KohlsConstants.EXTN_NOMADIC);
			String isShipAlone = promiseElement.getAttribute(KohlsConstants.EXTN_SHIP_ALONE);
			String isBreakable = promiseElement.getAttribute(KohlsConstants.EXTN_BREAKABLE);
			String isCage = promiseElement.getAttribute(KohlsConstants.EXTN_CAGE);
			String itemID = promiseElement.getAttribute(KohlsConstants.ITEM_ID);
			String uom = promiseElement.getAttribute(KohlsConstants.UNIT_OF_MEASURE);
			String orgCode = promiseElement.getAttribute(KohlsConstants.ORGANIZATION_CODE);
			String shipNodeSource = promiseElement.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE);
			String fulfillType = promiseElement.getAttribute(KohlsConstants.FULFILLMENT_TYPE);

			if (YFCCommon.isVoid(itemType) && YFCCommon.isVoid(isNomadic) && YFCCommon.isVoid(isShipAlone) && YFCCommon.isVoid(isBreakable) && YFCCommon.isVoid(isCage)) {
				// TODO call getItemDetails for this ItemID to get the details.
				Element itemDetaislEle = callGetItemDetailsAPI(env, itemID, uom, orgCode); // mainly used when called from other promising APIs
				
				Element primaryInfoEle = (Element)itemDetaislEle.getElementsByTagName(KohlsConstants.PRIMARY_INFORMATION).item(0);
				
				itemType = primaryInfoEle.getAttribute(KohlsConstants.ITEM_TYPE);

				Element itemExtnEle = (Element) itemDetaislEle.getElementsByTagName(KohlsConstants.EXTN).item(0);
				if (!YFCCommon.isVoid(itemExtnEle)) {
					isNomadic = itemExtnEle.getAttribute(KohlsConstants.EXTN_NOMADIC);
					isShipAlone = itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_ALONE);
					isBreakable = itemExtnEle.getAttribute(KohlsConstants.EXTN_BREAKABLE);
					isCage = itemExtnEle.getAttribute(KohlsConstants.EXTN_CAGE);
					shipNodeSource = itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE);
				}

				promiseElement.setAttribute(KohlsConstants.ITEM_TYPE, itemType);
				promiseElement.setAttribute(KohlsConstants.EXTN_NOMADIC, isNomadic);
				promiseElement.setAttribute(KohlsConstants.EXTN_SHIP_ALONE, isShipAlone);
				promiseElement.setAttribute(KohlsConstants.EXTN_BREAKABLE, isBreakable);
				promiseElement.setAttribute(KohlsConstants.EXTN_CAGE, isCage);
				promiseElement.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, shipNodeSource);
			}

			boolean toBeAdded = true;
			
			/*
			 * Changing the else condition - now only ExtnShipAlone Attribute will be while 
			 * evaluating an promise line.
			 * Previous Condiions - isShipAlone=Y and isBreakable=Y and isCage='Y'		
			 */
			
			if (!YFCCommon.isVoid(fulfillType)){
				toBeAdded = false;
			}else if (!itemType.equalsIgnoreCase(KohlsConstants.REGULER)) {
				toBeAdded = false;
			} else if (isNomadic.equalsIgnoreCase(KohlsConstants.YES)) {
				toBeAdded = false;
			} else if ((isShipAlone.equalsIgnoreCase(KohlsConstants.YES))) {
				toBeAdded = false;
			}
			
			if (toBeAdded) {
				verifyElement.appendChild(promiseElement.cloneNode(false));
			}
		}

		Document outputDoc = KohlsXMLUtil.getDocument();

		Element outputElement = outputDoc.createElement(KohlsConstants.PROMISE_LINES);
		outputDoc.appendChild(outputElement);

		NodeList finalPromiseLines = verifyElement.getElementsByTagName(KohlsConstants.PROMISE_LINE);
		
		String finalPromiseType = null;

		if (finalPromiseLines.getLength() > 0) {
			int finalpromiseLength=finalPromiseLines.getLength();
			for (int i = 0; i < finalpromiseLength; i++) {

				//Element promiseElement = (Element) promiseLines.item(i);
				Element promiseElement = (Element) finalPromiseLines.item(i);

				String shipNodeSource1 = promiseElement.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE);
				
				if (!shipNodeSource1.equalsIgnoreCase(KohlsConstants.STORE)) {
					if(YFCCommon.equals(finalPromiseType, KohlsConstants.STORE, false)){
						finalPromiseType = KohlsConstants.MIXED;
					}
					else
					{
						finalPromiseType = KohlsConstants.OTHERS;
					}
					promiseFlag = 0;
					
				} else {
					if (promiseFlag == 2) // for first time
					{
						finalPromiseType = KohlsConstants.STORE;
						promiseFlag = 1;
					} else if (promiseFlag != 1) {
						if(YFCCommon.equals(finalPromiseType, KohlsConstants.STORE, false)){
							finalPromiseType = KohlsConstants.MIXED;
						}
						else if(YFCCommon.equals(finalPromiseType, KohlsConstants.OTHERS, false)){
                            finalPromiseType = KohlsConstants.MIXED;        
						}
						promiseFlag = 0;
					}
				}
				if(KohlsConstants.MIXED.equals(finalPromiseType)){
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("KohlsIdentifyPromiseType ::: Identified the Order Type as MIXED.. Hence Exitting the iteration");
					}
					break;
				}
			}

			outputElement.setAttribute(KohlsConstants.PROMISE_TYPE, finalPromiseType);
			
		} else {
			outputElement.setAttribute(KohlsConstants.PROMISE_TYPE, KohlsConstants.OTHERS);
		}

		kohlsLogger.debug("Output from Util: " + KohlsXMLUtil.getXMLString(outputDoc));
		return outputDoc;
	}

	/**
	 * This method is used to call the getItemDetails API to get the values of the extended columns.
	 * @param env
	 * @param itemID
	 * @param uom
	 * @param orgCode
	 * @return
	 * @throws Exception
	 */
	public static Element callGetItemDetailsAPI(YFSEnvironment env, String itemID, String uom, String orgCode) throws Exception {
		// TODO Auto-generated method stub

		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.ITEM);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.ITEM_ID, itemID);
		inElement.setAttribute(KohlsConstants.UNIT_OF_MEASURE, uom);
		inElement.setAttribute(KohlsConstants.ORGANIZATION_CODE, orgCode);

		kohlsLogger.debug("Input to getItemDetails API: " + KohlsXMLUtil.getXMLString(inputDoc));
		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_DETAILS_PROMISE_TYPE, KohlsConstants.GET_ITEM_DETAILS, inputDoc);
		kohlsLogger.debug("Output from getItemDetails API: " + KohlsXMLUtil.getXMLString(outputDoc));

    	return outputDoc.getDocumentElement();
	}
	


}
