package com.kohls.oms.ue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.dbclasses.YFS_Ship_NodeDBHome;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCCallingProgLogRegistry;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetDistanceUE;

public class KohlsGetDistanceUE implements YFSGetDistanceUE {
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsGetDistanceUE.class.getName());
	
	public Document getDistance(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {

		String strShipNode = KohlsXMLUtil.getAttribute(inDoc.getDocumentElement(), KohlsConstants.SHIP_NODE);
		String orderHeaderKey = KohlsCommonUtil.readOHKFromCache(env);

		String strInvokedFrom = (String)((YFSContext) env).getTxnObject(KohlsConstants.INVOKED_FROM);
		if(kohlsLogger.isDebugEnabled()){
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- UE Called from\t" + strInvokedFrom);
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- UE Called for ShipNode\t" + strShipNode);
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- UE is Called in context of the OrderHeaderKey:\t" + orderHeaderKey);
			kohlsLogger.debug("UE Called from" + strInvokedFrom);
		}
		
		
		Element shipToAddress = KohlsXMLUtil.getChildElement(inDoc.getDocumentElement(), KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO);
		Element shipNodeAddress = KohlsXMLUtil.getChildElement(inDoc.getDocumentElement(), KohlsConstants.PERSON_INFO_SHIP_FROM);
		
		YFCDocument yDoc = YFCDocument.createDocument(KohlsConstants.GET_DISTANCE);
		double dblActualDistance = getActualDistance(env, shipToAddress, shipNodeAddress);
		yDoc.getDocumentElement().setAttribute(KohlsConstants.DISTANCE,dblActualDistance);
		
		if(kohlsLogger.isDebugEnabled()){
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- StarightLine distance between ShipTo and ShipNode Location is:\t" + dblActualDistance);
		}
		
		try{
			if((YFCCommon.equals(identifyCallingAPI(), KohlsConstants.SCHEDULE_ORDER, false)) && 
					(!YFCCommon.equals(KohlsConstants.SUPPLY_CORRECTIONS_UE, strInvokedFrom, false)))
			{
				// Code for Performance fix . call getItemDetails , getOrderDetails only once . 
				String promiseType = "";
				String strNodeType = "";
				boolean isPeakSeason = false;
				//String sPromiseType = ((YFSContext) env).getUEParam(KohlsConstants.PROMISE_TYPE);
				String sPromiseType =  KohlsCommonUtil.getContextProperty(env, KohlsConstants.PROMISE_TYPE);
				kohlsLogger.debug("KohlsGetDistanceUE logs ----------- sPromiseType:: " + sPromiseType);
				if(!YFCObject.isVoid(sPromiseType)){
					promiseType = sPromiseType;
					
				}else {

					kohlsLogger.debug("getDistanceForNodeList called in context of the following Order:\t" + orderHeaderKey );
					Document orderDetails = callGetOrderDetails(env, orderHeaderKey);

					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("orderDetails: " + KohlsXMLUtil.getXMLString(orderDetails));
					}				
					
					Document inputDocForUtil = callGetItemDetails(env, orderDetails);
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("inputDocForUtil: " + KohlsXMLUtil.getXMLString(inputDocForUtil));
					}

					Document identifyPromiseType = KohlsIdentifyPromiseType.identifyPromiseType(env, inputDocForUtil);
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("identifyPromiseType: " + KohlsXMLUtil.getXMLString(identifyPromiseType));
					}

					Element promiseTypeEle = identifyPromiseType.getDocumentElement();
					 promiseType = promiseTypeEle.getAttribute(KohlsConstants.PROMISE_TYPE);				

					 strNodeType=YFS_Ship_NodeDBHome.getInstance().selectWithPK((YFSContext) env, strShipNode).getNode_Type();
					
					 KohlsCommonUtil.setContextObject(env, KohlsConstants.PROMISE_TYPE, promiseType);
					 kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Setting PromiseType " + promiseType);
					
					 if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("promise type: " + promiseType);
						kohlsLogger.debug("KohlsGetDistanceUE logs ----------- ShipNode Type is\t" + strNodeType);
					}
				}
				
				double dblAdditionalCost = 0;
				if (YFCCommon.equals(promiseType, KohlsConstants.STORE, false)) {
					//KohlsCommonUtil.setContextObject(env, KohlsConstants.PROMISE_TYPE, promiseType);
					kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Setting PromiseType");
					((YFSContext) env).setUEParam(KohlsConstants.PROMISE_TYPE, promiseType);
					double radius = getShippingRadius(env, shipToAddress);	
					
					//Peak Season Fix
					//String sPeakSeason = ((YFSContext) env).getUEParam("PeakSeason");
					String sPeakSeason = KohlsCommonUtil.getContextProperty(env, "PeakSeason");
					kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Checking peak Season" +sPeakSeason);
					
					if(YFCObject.equals(sPeakSeason, "Y")){
						isPeakSeason = true;
						kohlsLogger.debug("KohlsGetDistanceUE logs ----------- peak Season in Y::"+ isPeakSeason);
					} else if(YFCObject.equals(sPeakSeason, "N")){
						isPeakSeason = false;
						kohlsLogger.debug("KohlsGetDistanceUE logs ----------- peak Season in N::"+ isPeakSeason);
					}else if(YFCObject.isVoid(sPeakSeason)){


						kohlsLogger.debug("KohlsGetDistanceUE logs ----------- peak Season Neither N/Y::"+ isPeakSeason);
						isPeakSeason = checkIfPeakSeason(env);
						if(!isPeakSeason) {
							//((YFSContext) env).setUEParam("PeakSeason", "N");
							KohlsCommonUtil.setContextObject(env, "PeakSeason", "N");
							kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Setting PeakSeason to N");
						}else{
							//((YFSContext) env).setUEParam("PeakSeason", "Y");
							KohlsCommonUtil.setContextObject(env, "PeakSeason", "Y");

							kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Setting PeakSeason to Y");
						}
					}
					
					 
					
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Zone1 Radius is\t" + radius);
						kohlsLogger.debug("KohlsGetDistanceUE logs -----------  is Peak Season \t" + isPeakSeason);
					}
					
					String strEffectiveNodeType = "";
					
					if(isPeakSeason){
						if(KohlsConstants.STORE.equals(strNodeType) && dblActualDistance < radius){
							strEffectiveNodeType = KohlsConstants.ZONE1_STORE;
						}
						else if(KohlsConstants.STORE.equals(strNodeType) && dblActualDistance > radius){
							strEffectiveNodeType = KohlsConstants.NONZN1STORE;
						}
						else if(KohlsConstants.RDC.equals(strNodeType)){
							strEffectiveNodeType = KohlsConstants.RDC;
						}
						else if(KohlsConstants.EFC.equals(strNodeType)){
							strEffectiveNodeType = KohlsConstants.EFC;
						}
						else {
							strEffectiveNodeType = KohlsConstants.OTHERS;
						}
					}
					else{
						if(KohlsConstants.STORE.equals(strNodeType) && dblActualDistance < radius){
							strEffectiveNodeType = KohlsConstants.ZONE1_STORE;
						}
						else{
							strEffectiveNodeType = KohlsConstants.OTHERS;
						}
					}				
					
					String strCost = getCost(env, strEffectiveNodeType, isPeakSeason);
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("KohlsGetDistanceUE logs -----------  ShipNode has been detected as: \t" + strEffectiveNodeType);
						kohlsLogger.debug("KohlsGetDistanceUE logs -----------  ShipNode Cost is: \t" + strCost);
					}
					
					try{					
						dblAdditionalCost = Double.parseDouble(strCost);
					}
					catch(Exception e){					
						dblAdditionalCost = 0;
					}
					
					yDoc.getDocumentElement().setAttribute(KohlsConstants.DISTANCE, dblActualDistance + dblAdditionalCost);
					yDoc.getDocumentElement().setAttribute(KohlsConstants.EXTN_ACTUAL_DISTANCE, dblActualDistance);
					yDoc.getDocumentElement().setAttribute(KohlsConstants.EXTN_ADDED_COST, dblAdditionalCost);
					
					if(kohlsLogger.isDebugEnabled()){
						kohlsLogger.debug("output XML: " + KohlsXMLUtil.getXMLString(yDoc.getDocument()));	
					}
				}
			}
		}
		catch(Exception e){
		    final YFSUserExitException uee = new YFSUserExitException(e.getMessage());
		    uee.setStackTrace(e.getStackTrace());
		    throw uee;			
		}			
		return yDoc.getDocument();	
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

		Document inputDocForUtil = SCXmlUtil.createDocument(KohlsConstants.PROMISE_LINES); 
		
		Element inputEleForUtil = inputDocForUtil.getDocumentElement();
		
		int length = orderLines.getLength();

		for (int i = 0; i < length; i++) {
			Element orderLineEle = (Element) orderLines.item(i);

			Element itemElement = (Element) orderLineEle.getElementsByTagName(KohlsConstants.ITEM).item(0);

			//Element itemDetails = KohlsIdentifyPromiseType.callGetItemListWrapperAPIforDetails(env, itemElement.getAttribute(KohlsConstants.ITEM_ID), itemElement.getAttribute(KohlsConstants.UNIT_OF_MEASURE), entCode,KohlsConstants.GET_ITEM_LIST_PROMISE_TYPE);
			Element itemDetails=(Element) orderLineEle.getElementsByTagName(KohlsConstants.ITEM_DETAILS).item(0);
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
		if(kohlsLogger.isDebugEnabled()){
			kohlsLogger.debug("Document returned: " + KohlsXMLUtil.getXMLString(inputDocForUtil));
		}
		return inputDocForUtil;
	}
	
	/*
	 * This method will evaluate the distance between the ShipTo and ShipFrom location
	 * using the lat / long. If lat / long are provided in the input to the UE that will
	 * be used, else ZipCode will be used to fetch the lat-long from the YFS_ZIP_CODE_LOCATION 
	 * table.
	 * Incase neither the lat / long nor ZipCode, for any of the two locations, is available in the 
	 * input to UE the distance will be defaulted to 0 Miles.
	 * 
	 */
	double getActualDistance(YFSEnvironment env,  Element eShipToAddress, Element eShipFromAddress){
		double dblDistance = 0;//In sync with product behavior
		
		double dblToLatitude = 0.0;
		double dblToLongitude = 0.0;
		double dblFromLatitude = 0.0;
		double dblFromLongitude = 0.0;
		
		//get ShipTo lat/long
		if(!YFCCommon.isVoid(KohlsXMLUtil.getAttribute(eShipToAddress, KohlsConstants.LATITUDE)) 
				&& !YFCCommon.isVoid(KohlsXMLUtil.getAttribute(eShipToAddress, KohlsConstants.LONGITUDE))){
			dblToLatitude =  KohlsXMLUtil.getDoubleAttribute(eShipToAddress, KohlsConstants.LATITUDE);
	    	dblToLongitude = KohlsXMLUtil.getDoubleAttribute(eShipToAddress, KohlsConstants.LONGITUDE);
		}
		else{
			//lat / long not in input - look up in the YFS_ZIP_CODE_LOCATION table
			Document docShipToCoordinates = KohlsCommonAPIUtil.getCoordinates(env, getCoordinatesInput(eShipToAddress));
			if(YFCCommon.isVoid(docShipToCoordinates)){
				//no data found in the YFS_ZIP_CODE_LOCATION
				kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Coordinates can not be found for the Shipping address:\t Distance will be defaulted to 0 Miles.");
				return dblDistance;
			}
			else{
				//lat/long found in the YFS_ZIP_CODE_LOCATION
				dblToLatitude =  KohlsXMLUtil.getDoubleAttribute(docShipToCoordinates.getDocumentElement(), KohlsConstants.LATITUDE);
		    	dblToLongitude = KohlsXMLUtil.getDoubleAttribute(docShipToCoordinates.getDocumentElement(), KohlsConstants.LONGITUDE);
		    	
				if(kohlsLogger.isDebugEnabled()){
				kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Coordinates of the Shipping Location are:\t" + KohlsXMLUtil.getXMLString(docShipToCoordinates));
				}
			}
		}
		
		//get ShipFrom lat/long
		if(!YFCCommon.isVoid(KohlsXMLUtil.getAttribute(eShipFromAddress, KohlsConstants.LATITUDE)) 
				&& !YFCCommon.isVoid(KohlsXMLUtil.getAttribute(eShipFromAddress, KohlsConstants.LONGITUDE))){
			dblFromLatitude =  KohlsXMLUtil.getDoubleAttribute(eShipFromAddress, KohlsConstants.LATITUDE);
			dblFromLongitude = KohlsXMLUtil.getDoubleAttribute(eShipFromAddress, KohlsConstants.LONGITUDE);
		}
		else
		{
			//lat / long not in input - look up in the YFS_ZIP_CODE_LOCATION table
			Document docShipFromCoordinates = KohlsCommonAPIUtil.getCoordinates(env, getCoordinatesInput(eShipFromAddress));
			if(YFCCommon.isVoid(docShipFromCoordinates)){
				//no data found in the YFS_ZIP_CODE_LOCATION
				kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Coordinates can not be found for the ShipNode address:\t Distance will be defaulted to 0 Miles.");
				return dblDistance;
			}
			else{
				//lat/long found in the YFS_ZIP_CODE_LOCATION
				dblFromLatitude =  KohlsXMLUtil.getDoubleAttribute(docShipFromCoordinates.getDocumentElement(), KohlsConstants.LATITUDE);
		    	dblFromLongitude = KohlsXMLUtil.getDoubleAttribute(docShipFromCoordinates.getDocumentElement(), KohlsConstants.LONGITUDE);
		    	
				if(kohlsLogger.isDebugEnabled()){
					kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Coordinates of the ShipFrom Location are:\t" + KohlsXMLUtil.getXMLString(docShipFromCoordinates));
				}
			}
		}
		
		if(kohlsLogger.isDebugEnabled()){
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- ShipTo Latitude:=" + dblToLatitude + "\t ShipTo Longitude=" + dblToLongitude);
			kohlsLogger.debug("KohlsGetDistanceUE logs ----------- ShipFrom Latitude:=" + dblFromLatitude + "\t ShipFrom Longitude=" + dblFromLongitude);
			}
		
    	dblDistance = KohlsCommonAPIUtil.getDistance(dblToLatitude, dblToLongitude, dblFromLatitude, dblFromLongitude);
		
		return dblDistance;
	}
	
	private Document getCoordinatesInput(Element eAddress){
		Document out = YFCDocument.createDocument(KohlsConstants.ZIP_CODE_LOCATION).getDocument();
        Element  root = out.getDocumentElement();
        root.setAttribute(KohlsConstants.COUNTRY, KohlsXMLUtil.getAttribute(eAddress, KohlsConstants.COUNTRY));
        root.setAttribute(KohlsConstants.ZIPCODE, KohlsXMLUtil.getAttribute(eAddress, KohlsConstants.ZIPCODE));
        return out;
	}
	
	
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
	 * This method is used to get the shipping radius to be considered for the Ship To address. The getRegionList API will be used for this.
	 * @param env
	 * @param shipToAddress
	 * @return Shipping Radius
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 * @throws Exception
	 */
	private double getShippingRadius(YFSEnvironment env, Element shipToAddress) throws ParserConfigurationException, SAXException, IOException, Exception {

		double radius = 0;
		if (!YFCCommon.isVoid(shipToAddress)) {
			String state = shipToAddress.getAttribute(KohlsConstants.STATE);
			String country = shipToAddress.getAttribute(KohlsConstants.COUNTRY);

			if(kohlsLogger.isDebugEnabled()){
				kohlsLogger.debug("performance fix for region details State + Country = "+state+country );
			}
			Document regionDetails = null;
			String sRegionDetails = "";
			YFCDocument inpDoc = null;
			Document yRegionDetails = null;
			//sRegionDetails = ((YFSContext) env).getUEParam(state+country);
			sRegionDetails = KohlsCommonUtil.getContextProperty(env, state+country);
			kohlsLogger.debug("performance fix for sRegionDetails  = "+sRegionDetails);
			
			if(!YFCObject.isVoid(sRegionDetails)){
				yRegionDetails = KohlsXMLUtil.getDocument(sRegionDetails);
				kohlsLogger.debug("performance fix for yRegionDetails  = "+yRegionDetails);
				inpDoc = YFCDocument.getDocumentFor(yRegionDetails);
				kohlsLogger.debug("performance fix for  inpDoc = "+inpDoc);
			}
			YFCElement inpDocElem = null;
			if (inpDoc != null) {
				inpDocElem = inpDoc.getDocumentElement();
			}
			if(inpDoc != null || inpDocElem != null ) {
				regionDetails = yRegionDetails;
			}else{
				regionDetails = callGetRegionListAPI(env, state, country);
				sRegionDetails=  KohlsXMLUtil.getXMLString(regionDetails);
				KohlsCommonUtil.setContextObject(env, state+country, sRegionDetails);

				kohlsLogger.debug("KohlsGetDistanceUE logs ----------- Setting RegionDetails");
				//((YFSContext) env).setUEParam(state+country, sRegionDetails);
			}



			Element regions = regionDetails.getDocumentElement();
			Element regionSchema = (Element) regions.getElementsByTagName(KohlsConstants.REGION_SCHEMA).item(0);
			if (!YFCCommon.isVoid(regionSchema)) {
				Element parentRegion = KohlsXPathUtil.getElementByXpath(regionDetails, KohlsConstants.PARENT_REGION);
				if (!YFCCommon.isVoid(parentRegion)) {
					radius = Double.parseDouble(parentRegion.getAttribute(KohlsConstants.REGION_NAME));
				}
			}

		}

		if(kohlsLogger.isDebugEnabled()){
			kohlsLogger.debug("Radius = " + radius);
		}
		return radius;
	}
	
	
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

		if(kohlsLogger.isDebugEnabled()){
		kohlsLogger.debug("Input to getRegionList API: " + KohlsXMLUtil.getXMLString(inputDoc));
		}

		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_REGIONLIST, inputDoc);
		
		if(kohlsLogger.isDebugEnabled()){
		kohlsLogger.debug("Output from getRegionList API: " + KohlsXMLUtil.getXMLString(outputDoc));
		}

		return outputDoc;
	}
	
	private String identifyCallingAPI() {

		String sTransactionName = ""; 
		ArrayList<String> alstAPIs = (ArrayList<String>)YFCCallingProgLogRegistry.getLogRegistryListForType(KohlsConstants.API);
		kohlsLogger.debug("APIs : "  + printList(alstAPIs));
		if(alstAPIs.contains(KohlsConstants.SCHEDULE_ORDER)){
			sTransactionName = KohlsConstants.SCHEDULE_ORDER;
		}

		ArrayList<String> alstAgents = (ArrayList<String>)YFCCallingProgLogRegistry.getLogRegistryListForType(KohlsConstants.AGENTS);
		String agentsList = printList(alstAgents);
		kohlsLogger.debug("Agents : "  + agentsList);
		if(agentsList.contains(KohlsConstants.SCHEDULE_ORDER_AGENT)){
			sTransactionName = KohlsConstants.SCHEDULE_ORDER;
		}

		return sTransactionName;
	}


	private String printList(ArrayList<String> alstAPIs) {

		String returnStr = "";
		for(int i = 0; i< alstAPIs.size(); i++)
		{
			returnStr += alstAPIs.get(i);
			returnStr += ",";
		}
		return returnStr;
	}
	public  Document callGetOrderDetails(YFSEnvironment env, String orderHeaderKey) throws Exception {

		// TODO what will happen with the OHK is null?

		Document inputDoc = SCXmlUtil.createDocument(KohlsConstants.ORDER);

		Element inElement = inputDoc.getDocumentElement();
		inElement.setAttribute(KohlsConstants.ORDER_HEADER_KEY, orderHeaderKey);

	

		Document outputDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ORDER_DETAILS_OUT_TEMPLATE_DISTANCE_UE, KohlsConstants.GET_ORDER_DETAILS, inputDoc);

	

		return outputDoc;

	}
}
