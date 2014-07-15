/**
 * 
 */
package com.kohls.oms.ue;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.common.util.KohlsCommonAPIUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsDateUtil;
import com.kohls.common.util.KohlsIdentifyPromiseType;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.omp.OMPTransactionCache;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.ydm.japi.ue.YDMGetTransportationCostUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author pkrishnaswamy
 * 
 */
public class KohlsGetTransportationCostUE implements YDMGetTransportationCostUE {
	
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsGetTransportationCostUE.class);

	/*
	 * This user exit is configured to perform below tasks
	 * 1)	Check the Order type by calling the KohlsIdentifyPromiseType Util. If the Promise Type is "Store" then proceeds further.
	 *      a)	Read the OrderHeaderKey from the transaction cache.
	 *		b)	Call getOrderDetails API with the OrderHeaderKey obtained above and with minimal output template to get the Items in the order.
	 *		c)	Call getItemDetails with the Items obtained above and with a minimal output template to fetch the required input to KohlsIdentifyPromiseType util.
	 * 2)	Get the shipping radius to identify the Zone-1 stores.
	 *		a)	Use the State from the Ship to address of order to call getRegionList API to get the Region Schema (Distance) details.
	 * 3)	Call getSurroundingNodeList API call with the radius obtained above, to get the list of Zone-1 Stores .
	 * 4)	Identify if order is a peak or a non-peak period using the peak period of the ship from node from the date range defined in the EXTN_STORE_PEAK_PRD common code.
	 * 5)	For a non-peak period, 
	 *		a)	If the ShipNode in the UE input belongs to the Zone-1 Store, 
	 *			(1)	assign the cost of common code. EXTN_NON_PK_STORE
	 *		b)	Else
	 *			(1)	 assign the cost of common code. EXTN_NON_PK_OTHER
	 * 6)	For a peak period, do the following:
	 *		a)	If the ShipNode in the UE input belongs to the Zone-1 Store, 
	 *			(1)	assign 0 the cost of common code EXTN_PK_STORE.
	 *		b)	Else
	 *			(1)	Call getShipNodeList API to get the Node type.
	 *				(a)	If the ShipNode is a RDC, 
	 *					(i)	assign a cost of  common code EXTN_PK_RDC
	 *				(b)	If the ShipNode is an eFC, 
	 *					(i)	assign the cost of common code EXTN_PK_eFC
	 * 
	 * @see
	 * com.yantra.ydm.japi.ue.YDMGetTransportationCostUE#getTransportationCost
	 * (com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 */
	public Document getTransportationCost(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		// TODO Auto-generated method stub

		Element inputElement = inDoc.getDocumentElement();
		kohlsLogger.debug("Input: " + KohlsXMLUtil.getElementXMLString(inputElement));

		String orderHeaderKey = KohlsCommonUtil.readOHKFromCache(env);

		Element shipToAddress = null;

		try {

			Document orderDetails = KohlsCommonAPIUtil.callGetOrderDetails(env, orderHeaderKey);
			kohlsLogger.debug("orderDetails: " + KohlsXMLUtil.getXMLString(orderDetails));

			if (!YFCCommon.isVoid(orderDetails)) {
				shipToAddress = KohlsXPathUtil.getElementByXpath(orderDetails, KohlsConstants.ORDER_PERSON_INFO_SHIP_TO_XPATH);
				kohlsLogger.debug("shipToAddress: " + KohlsXMLUtil.getElementXMLString(shipToAddress));
				
			}

			Document inputDocForUtil = callGetItemDetails(env, orderDetails);
			kohlsLogger.debug("inputDocForUtil: " + KohlsXMLUtil.getXMLString(inputDocForUtil));

			Document identifyPromiseType = KohlsIdentifyPromiseType.identifyPromiseType(env, inputDocForUtil);
			kohlsLogger.debug("identifyPromiseType: " + KohlsXMLUtil.getXMLString(identifyPromiseType));

			Element promiseTypeEle = identifyPromiseType.getDocumentElement();
			String promiseType = promiseTypeEle.getAttribute(KohlsConstants.PROMISE_TYPE);
			kohlsLogger.debug("promise type: " + promiseType);

			if (YFCCommon.equals(promiseType, KohlsConstants.STORE, false)) {
				((YFSContext) env).setUEParam("PromiseType", promiseType);
				assignCostToNode(env, inDoc, shipToAddress);
				kohlsLogger.debug("output XML: " + inDoc);
				
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

		kohlsLogger.debug("output from UE: " + KohlsXMLUtil.getXMLString(inDoc));
		return inDoc;

	}

	/**
	 * This method is used to assign cost to a node depending on whether it is a peak or a non-peak period.
	 * It does the following steps:
	 * 1. Check if the current period is a peak or non-peak period.
	 * 2. Get the shipping radius to be considered to identify Zone-1 stores.
	 * 3. Get the list of Zone-1 Stores using getSurroundingNodeList API
	 * 4. For a non-peak period, 
	 *    a.	If the ShipNode in the solution(input) belongs to the Zone-1 Store, 
	 *			i.	assign the cost of common code. EXTN_NON_PK_STORE
	 *    b.	Else
	 * 			i.	 assign the cost of common code. EXTN_NON_PK_OTHER
	 * 5. For a peak period, do the following:
	 *    a.	If the ShipNode in the solution belongs to the Zone-1 Store, 
	 *			i.	assign 0 the cost of common code EXTN_PK_STORE.
	 *	  b.	Else
	 *			i.	Call getOrganizationList API to get the Node type.
	 *			ii.	If the ShipNode is a RDC, 
	 *				1.	assign a cost of  common code EXTN_PK_RDC
	 *			iii.	If the ShipNode is an eFC, 
	 *				1.	assign the cost of common code EXTN_PK_eFC
	 * @param env
	 * @param inDoc
	 * @param shipToAddress
	 * @throws Exception
	 */
	private void assignCostToNode(YFSEnvironment env, Document inDoc, Element shipToAddress) throws Exception {

		kohlsLogger.debug("Checking for peak period...");
		boolean isPeakSeason = checkIfPeakSeason(env);
		kohlsLogger.debug("is peak season? " + isPeakSeason);

		kohlsLogger.debug("Getting radius to consider....");
		int radius = getShippingRadius(env, shipToAddress);

		kohlsLogger.debug("Getting list of Zone-1 Stores...");
		Set<String> zone1Stores = getZoneOneStores(env, radius, shipToAddress);

		Element shipNodeEle = KohlsXPathUtil.getElementByXpath(inDoc, KohlsConstants.ROUTABLE);
		kohlsLogger.debug("shipNodeEle: " + KohlsXMLUtil.getElementXMLString(shipNodeEle));

		Element carrierOptionEle = KohlsXPathUtil.getElementByXpath(inDoc, KohlsConstants.CARRIER_OPTION);
		kohlsLogger.debug("carrierOptionEle: " + KohlsXMLUtil.getElementXMLString(carrierOptionEle));

		String shipNode = shipNodeEle.getAttribute(KohlsConstants.SHIP_FROM_NODE);
		kohlsLogger.debug("ShipNode = " + shipNode);
		
		String storeType = null;

		if (isPeakSeason) {

			if (zone1Stores.contains(shipNode)) {
				kohlsLogger.debug("Zone-1 store");
				storeType = KohlsConstants.ZONE1_STORE;
			} else {
				String nodeType = determineNodeTye(env, shipNode);

				if (nodeType.equals(KohlsConstants.RDC)) {
					kohlsLogger.debug("Node is an RDC");
					storeType = KohlsConstants.RDC;
				} else if (nodeType.equals(KohlsConstants.EFC)) {
					kohlsLogger.debug("Node is an eFC");
					storeType = KohlsConstants.EFC;
					
				} else if (nodeType.equals(KohlsConstants.STORE)){
					kohlsLogger.debug("Node is an Non Zone1 Store");
					storeType = KohlsConstants.NONZN1STORE;
				}else {
					kohlsLogger.debug("Unidentified node");
					storeType = KohlsConstants.OTHERS;
				}
			}
			carrierOptionEle.setAttribute("Cost", getCost(env, storeType, true));

		} else {

			if (zone1Stores.contains(shipNode)) {
				kohlsLogger.debug("Zone-1 store");
				storeType = KohlsConstants.ZONE1_STORE;
				
			} else {
				kohlsLogger.debug("Not a Zone-1 store");
				storeType = KohlsConstants.OTHERS;
				
			}
			carrierOptionEle.setAttribute("Cost", getCost(env, storeType, false));

		}

		((YFSContext) env).setUEParam("StoreType", storeType);
		kohlsLogger.debug("after assigning cost: " + KohlsXMLUtil.getXMLString(inDoc));

	}

	/**
	 * This method is used to get the cost that has to be assigned to the node from the Common Codes
	 * 
	 * @param env
	 * @param string
	 * @param peak or non-peak
	 * @return Cost to be assigned
	 * @throws Exception
	 */
	private String getCost(YFSEnvironment env, String string, boolean peak) throws Exception {
		Document commonCodeDoc = null;
		String cost = null;
		String orgCode = ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE);

		if (peak) {
			if (string.equals(KohlsConstants.ZONE1_STORE)) {
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_PK_STORE, KohlsConstants.COST, orgCode);
			} else if (string.equals(KohlsConstants.RDC)) {
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_PK_RDC, KohlsConstants.COST, orgCode);
			} else if (string.equals(KohlsConstants.EFC)) {
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_PK_EFC, KohlsConstants.COST, orgCode);
			} else if (string.equals(KohlsConstants.NONZN1STORE)){
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_PK_NONZN1_STORE, KohlsConstants.COST, orgCode);
			} else {
				cost = KohlsConstants.COST_200000;
				
			}
		} else {
			if (string.equals(KohlsConstants.ZONE1_STORE)) {
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_NON_PK_STORE, KohlsConstants.COST, orgCode);
			} else {
				commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_NON_PK_OTHER, KohlsConstants.COST, orgCode);
			}
		}

		if(!YFCCommon.isVoid(commonCodeDoc))
		{
		Element commonCodeList = KohlsXMLUtil.getElementByXpath(commonCodeDoc, KohlsConstants.COMMON_CODE_LIST_COMMONCODE);

		if (YFCCommon.isVoid(cost)) {
			cost = commonCodeList.getAttribute("CodeShortDescription");
		}
		}

		return cost;
	}

	/**
	 * This method is used to determine the Node Type
	 * @param env
	 * @param shipNode
	 * @return the Node Type
	 * @throws Exception
	 */
	private String determineNodeTye(YFSEnvironment env, String shipNode) throws Exception {

		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.SHIP_NODE);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.SHIP_NODE, shipNode);

		kohlsLogger.debug("Input to getShipNodeList API: " + KohlsXMLUtil.getXMLString(inputDoc));

		Document outputDoc=KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_SHIP_NODE_LIST, KohlsConstants.GET_SHIP_NODE_LIST_API, inputDoc);

		kohlsLogger.debug("Output from getShipNodeList API: " + KohlsXMLUtil.getXMLString(outputDoc));

		Element shipNodeListEle = outputDoc.getDocumentElement();

		Element shipNodeEle = (Element) shipNodeListEle.getElementsByTagName(KohlsConstants.SHIP_NODE).item(0);

		String nodeType = null;

		if (!YFCCommon.isVoid(shipNodeEle)) {
			nodeType = shipNodeEle.getAttribute(KohlsConstants.NODE_TYPE);
		}

		kohlsLogger.debug("Node type = " + nodeType);
		return nodeType;
	}

	/**
	 * This method is used to get the list of Zone-1 Stores.
	 * 
	 * @param env
	 * @param radius
	 * @param shipToAddress
	 * @return List of Zone-1 Stores.
	 * @throws Exception
	 */
	private Set<String> getZoneOneStores(YFSEnvironment env, int radius, Element shipToAddress) throws Exception {

		Set<String> storeList = new HashSet<String>();

		Document surroundingNodes = KohlsCommonAPIUtil.callGetSurroundingNodeListAPI(env, radius, shipToAddress);

		Element surroundingNodeEle = surroundingNodes.getDocumentElement();

		Element nodeList = (Element) surroundingNodeEle.getElementsByTagName(KohlsConstants.NODE_LIST).item(0);

		if (!YFCCommon.isVoid(nodeList)) {
			NodeList nodes = nodeList.getElementsByTagName(KohlsConstants.NODE);
			int nodeLength = nodes.getLength();
			for (int i = 0; i < nodeLength; i++) {
				Element nodeEle = (Element) nodes.item(i);
				storeList.add(nodeEle.getAttribute(KohlsConstants.SHIP_NODE));
			}
		}

		return storeList;
	}

	/**
	 * This method is used to get the shipping radius to be considered for the Ship To address. The getRegionList API will be used for this.
	 * @param env
	 * @param shipToAddress
	 * @return Shipping Radius
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	private int getShippingRadius(YFSEnvironment env, Element shipToAddress) throws ParserConfigurationException, SAXException, IOException, Exception {

		int radius = 0;
		if (!YFCCommon.isVoid(shipToAddress)) {
			String state = shipToAddress.getAttribute(KohlsConstants.STATE);
			String country = shipToAddress.getAttribute(KohlsConstants.COUNTRY);

			Document regionDetails = callGetRegionListAPI(env, state, country);

			Element regions = regionDetails.getDocumentElement();
			Element regionSchema = (Element) regions.getElementsByTagName(KohlsConstants.REGION_SCHEMA).item(0);
			if (!YFCCommon.isVoid(regionSchema)) {
				Element parentRegion = KohlsXPathUtil.getElementByXpath(regionDetails, KohlsConstants.PARENT_REGION);
				if (!YFCCommon.isVoid(parentRegion)) {
					radius = Integer.parseInt(parentRegion.getAttribute(KohlsConstants.REGION_NAME));
				}
			}

		}

		kohlsLogger.debug("Radius = " + radius);
		return radius;
	}

	/**
	 * This method calls the getRegionList API
	 * @param env
	 * @param state
	 * @param country
	 * @return Output of the getRegionList API
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	private Document callGetRegionListAPI(YFSEnvironment env, String state, String country) throws ParserConfigurationException, SAXException, IOException, Exception {
		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.REGION);


		Document commonCodeDoc = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_REGN_SCHEMA, KohlsConstants.SCHEMA_NAME, ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE));
		Element commonCodeList = KohlsXMLUtil.getElementByXpath(commonCodeDoc, KohlsConstants.COMMON_CODE_LIST_COMMONCODE);

		String regionSchema = commonCodeList.getAttribute(KohlsConstants.CODE_SHORT_DESCRIPTION);
		
		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.REGIONS_SCHEMA_NAME, regionSchema);

		Element personInfoEle = inputDoc.createElement(KohlsConstants.PERSON_INFO);
		inElement.appendChild(personInfoEle);
		personInfoEle.setAttribute(KohlsConstants.COUNTRY, country);
		personInfoEle.setAttribute(KohlsConstants.STATE, state);

		kohlsLogger.debug("Input to getRegionList API: " + KohlsXMLUtil.getXMLString(inputDoc));

		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_REGIONLIST, inputDoc);

		kohlsLogger.debug("Output from getRegionList API: " + KohlsXMLUtil.getXMLString(outputDoc));

		return outputDoc;
	}

	/**
	 * This method is used to check if the current period is peak or non-peak period.
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private boolean checkIfPeakSeason(YFSEnvironment env) throws Exception {

		String startDate = null;
		String endDate = null;

		String currentDate = KohlsDateUtil.getCurrentDateTime(KohlsConstants.DATE_FORMAT);

		Document peakSeasonCommonCode = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.EXTN_STORE_PEAK_PRD, ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE));

		List<Element> commonCodeList = KohlsXMLUtil.getElementListByXpath(peakSeasonCommonCode, KohlsConstants.COMMON_CODE_LIST_COMMONCODE);

		for (Iterator itr = commonCodeList.iterator(); itr.hasNext();) {
			Element commonCode = (Element) itr.next();

			if (commonCode.getAttribute(KohlsConstants.CODE_VALUE).equals(KohlsConstants.START_DATE)) {
				startDate = commonCode.getAttribute(KohlsConstants.CODE_SHORT_DESCRIPTION);
			}
			if (commonCode.getAttribute(KohlsConstants.CODE_VALUE).equals(KohlsConstants.END_DATE)) {
				endDate = commonCode.getAttribute(KohlsConstants.CODE_SHORT_DESCRIPTION);
			}
		}

		if (((KohlsCommonUtil.getYFCDate(currentDate).after(KohlsCommonUtil.getYFCDate(startDate))) && (KohlsCommonUtil.getYFCDate(currentDate).before(KohlsCommonUtil.getYFCDate(endDate))))
				|| (KohlsCommonUtil.getYFCDate(currentDate).equals(KohlsCommonUtil.getYFCDate(startDate))) || (KohlsCommonUtil.getYFCDate(currentDate).equals(KohlsCommonUtil.getYFCDate(endDate)))) {
			return true;
		}

		return false;
	}

	/**
	 * This method invokes the getItemDetails API to get the extn column details
	 * for the Item and also created the input XML that has to be passed to the
	 * KohlsIdentifyPromiseType util.
	 * 
	 * @param env
	 * @param orderDetails
	 * @return Input Document for the KohlsIdentifyPromiseType util.
	 *         <PromiseLines> <PromiseLine ItemID="" ItemType="" ExtnNomadic=""
	 *         ExtnShipAlone="" ExtnBreakable="" ExtnCage=""
	 *         ExtnShipNodeSource="" UnitOfMeasure="" OrganizationCode=""/>
	 *         </PromiseLines>
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	private Document callGetItemDetails(YFSEnvironment env, Document orderDetails) throws ParserConfigurationException, SAXException, IOException, Exception {

		Element orderInput = orderDetails.getDocumentElement();
		String entCode = orderInput.getAttribute(KohlsConstants.ENTERPRISE_CODE);

		Element orderLinesEle = (Element) orderInput.getElementsByTagName(KohlsConstants.ORDER_LINES).item(0);

		NodeList orderLines = orderLinesEle.getElementsByTagName(KohlsConstants.ORDER_LINE);

		Document inputDocForUtil = SCXmlUtil.createDocument(KohlsConstants.PROMISE_LINES); // Input
																				// Document
																				// for
																				// the
																				// KohlsIdentifyPromiseType
																				// util
		Element inputEleForUtil = inputDocForUtil.getDocumentElement();
		
		int length = orderLines.getLength();

		for (int i = 0; i < length; i++) {
			Element orderLineEle = (Element) orderLines.item(i);

			Element itemElement = (Element) orderLineEle.getElementsByTagName(KohlsConstants.ITEM).item(0);

			Element itemDetails = KohlsIdentifyPromiseType.callGetItemDetailsAPI(env, itemElement.getAttribute(KohlsConstants.ITEM_ID), itemElement.getAttribute(KohlsConstants.UNIT_OF_MEASURE), entCode);

			if (!YFCCommon.isVoid(itemDetails)) {
				Element primaryInfoEle = (Element)itemDetails.getElementsByTagName(KohlsConstants.PRIMARY_INFORMATION).item(0);
				Element promiseEleForUtil = inputDocForUtil.createElement(KohlsConstants.PROMISE_LINE);
				promiseEleForUtil.setAttribute(KohlsConstants.ITEM_ID, itemDetails.getAttribute(KohlsConstants.ITEM_ID));
				promiseEleForUtil.setAttribute(KohlsConstants.UNIT_OF_MEASURE, itemDetails.getAttribute(KohlsConstants.UNIT_OF_MEASURE));
				promiseEleForUtil.setAttribute(KohlsConstants.ITEM_TYPE, primaryInfoEle.getAttribute(KohlsConstants.ITEM_TYPE));
				promiseEleForUtil.setAttribute(KohlsConstants.ORGANIZATION_CODE, entCode);

				Element itemExtnEle = (Element) itemDetails.getElementsByTagName(KohlsConstants.EXTN).item(0);
				if (!YFCCommon.isVoid(itemExtnEle)) {
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_NOMADIC, itemExtnEle.getAttribute(KohlsConstants.EXTN_NOMADIC));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_SHIP_ALONE, itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_ALONE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_BREAKABLE, itemExtnEle.getAttribute(KohlsConstants.EXTN_BREAKABLE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_CAGE, itemExtnEle.getAttribute(KohlsConstants.EXTN_CAGE));
					promiseEleForUtil.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, itemExtnEle.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE));
				}

				inputEleForUtil.appendChild(promiseEleForUtil);
			}

		}

		kohlsLogger.debug("Document returned: " + KohlsXMLUtil.getXMLString(inputDocForUtil));
		return inputDocForUtil;
	}


}
