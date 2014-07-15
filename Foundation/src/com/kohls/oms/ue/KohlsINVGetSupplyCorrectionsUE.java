package com.kohls.oms.ue;



import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonAPIUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsDateUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KohlsXPathUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.shared.dbclasses.YFS_Ship_NodeDBHome;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;

import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.INVGetSupplyCorrectionsUE;

/**
 *  This class will create a map of Ship Node and Supply inv picture
 *  and save the map into the env which will be collated and posted into
 *  a queue for getting the inventory picture at time of Scheduling
 */
//Start -- KohlsOCF by IBM - 10/06/2013//
public class KohlsINVGetSupplyCorrectionsUE implements INVGetSupplyCorrectionsUE{

	private static final YFCLogCategory log =  YFCLogCategory.instance(KohlsINVGetSupplyCorrectionsUE.class.getName());

	@Override
	public Document getSupplyCorrections(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		// TODO Auto-generated method stub
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<----------- Begining of KohlsINVGetSupplyCorrectionsUE ----------->");
		}
		//create Map to store the supply picture
		Map<String,String> mSupplyCorrection = new HashMap<String,String>();
		
		Element eleItems=(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ITEMS).item(0);
		String sOrderRef=eleItems.getAttribute(KohlsXMLLiterals.A_ORDERREFERENCE);
		//add order header key as OrderReference into the map
		mSupplyCorrection.put(KohlsXMLLiterals.A_ORDERREFERENCE, sOrderRef);
		Element eleItem=(Element)eleItems.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
		String sItem=eleItem.getAttribute(KohlsXMLLiterals.A_ItemID);
		if(eleItem.getElementsByTagName(KohlsXMLLiterals.A_SUPPLIES).getLength()>0){
			Element eleSupplies=(Element)eleItem.getElementsByTagName(KohlsXMLLiterals.A_SUPPLIES).item(0);
			NodeList nlSupply=eleSupplies.getElementsByTagName(KohlsXMLLiterals.A_SUPPLY);
			if(nlSupply.getLength()>0){
				for(int i=0;i<nlSupply.getLength();i++){
					Element eleSupply=(Element)nlSupply.item(i);
					String sShipNode=eleSupply.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
					String sSupply=eleSupply.getAttribute(KohlsXMLLiterals.A_QUANTITY);
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("ItemID---->"+sItem+"----ShipnNode---->"+sShipNode+"----Supply----->"+sSupply);
				
					}
					//add ship node and supply to the map
					mSupplyCorrection.put(sShipNode, sSupply);
			}
		}
		//add supply map to the env
		env.setTxnObject(KohlsConstant.SUPPLY_CORRECTION+sItem, mSupplyCorrection);
		}
		

		Document outDoc=getSupplyCorrectionsUE(env,inXML);
		/*Checking OrderReference is Empty .If it is Empty invoke KohlsValidateExtnShipNodeSource class
		 * else Bypass the code
		 * 
		 */
		if (sOrderRef.isEmpty()){
			//Invoke KohlsCreateSupplyForRTAM Class
			KohlsCreateSupplyForRTAM	objExtnShpNdeSource = new KohlsCreateSupplyForRTAM();
			objExtnShpNdeSource.getExtnShipNodeSource(env,outDoc);
		}
		return outDoc;
	}
	/*
	 *  KohlsOCF by IBM - 10/06/2013 and  - KohlsOCF by IBM - 10/06/2013
	 * 
	 * @see com.yantra.yfs.japi.ue.INVGetSupplyCorrectionsUE#getSupplyCorrections(com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document)
	 * com.yantra.yfs.japi.ue.INVGetSupplyCorrectionsUE#getSupplyCorrections
	 * (com.yantra.yfs.japi.YFSEnvironment, org.w3c.dom.Document) Checking the
	 * common code for distance capping whether its required or not
	 * geCommonCodeList()API input formation <CommonCode
	 * CodeType="DISTANCE_CAP_REQUIRE" CodeValue="DISTANCE_CAP_REQUIRED"/>
	 */

	public Document getSupplyCorrectionsUE(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: getSupplyCorrections() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
		try {

			Element inEle = inDoc.getDocumentElement();
			Element InEleByXpAth = KohlsXMLUtil.getElementByXpath(inDoc, KohlsConstants.ITEMS_ITEM);
			String strOrganizationCode = ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE);
			Document outDocCommonCodeList = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.DISTANCE_CAP_REQ, strOrganizationCode);
			log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: getCommonCodeList() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
			Element commonCodeListOutputEle = outDocCommonCodeList.getDocumentElement();
			NodeList commonCodeListOutputEleList = commonCodeListOutputEle.getElementsByTagName(KohlsConstants.COMMON_CODE);
			int length=commonCodeListOutputEleList.getLength();
			for (int i = 0; i <length ; i++) {
				Document outDoc=null;
				Element commonCodeEle = (Element) commonCodeListOutputEleList.item(i);
				String strCodeValue = commonCodeEle.getAttribute(KohlsConstants.CODE_VALUE);
				String strCodeLongDescription = commonCodeEle.getAttribute(KohlsConstants.CODE_LONG_DESC);
				if (strCodeValue.equalsIgnoreCase(KohlsConstants.DISTANCE_CAP_REQUIRED) && strCodeLongDescription.equalsIgnoreCase(KohlsConstants.YES)) {
					 outDoc=doDistanceCapping(env, inDoc);
					 doSupplyCorrectionsOnItem(env, outDoc);
				}
				else
					doSupplyCorrectionsOnItem(env, inDoc);
			}
		} catch (Exception e1) {
			   final YFSUserExitException uee = new YFSUserExitException(e1.getMessage());
			    uee.setStackTrace(e1.getStackTrace());
			    throw uee;
		}
		
		return inDoc;
	}

	/*
	 * If the Distance capping is required then doDistanceCapping() method is
	 * called. getCommonCodeList() API called to get the common code for max
	 * capping distance <CommonCode CodeType="MAX_CAPPING" CodeValue="MAX_CAP"/>
	 * getSurroundingNodelist() API called to get the surrounding nodes for the
	 * given Distance Cap <>
	 */

	private Document doDistanceCapping(YFSEnvironment env, Document inDoc) throws Exception {
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: doDistanceCapping() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
		Element inEle = inDoc.getDocumentElement();
		Element InEleByXpAth = KohlsXMLUtil.getElementByXpath(inDoc, KohlsConstants.ITEMS_ITEM);
		String strOrganizationCode = ((YFSContext) env).getUEParam(KohlsConstants.ENTERPRISE_CODE);
		Document outDocCommonCodeForDistance = KohlsCommonUtil.getCommonCodeList(env, KohlsConstants.MAX_CAPPING, strOrganizationCode);
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: getCommonCodeList() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(outDocCommonCodeForDistance)).toString());
		Element ExtnIneleigibleList = KohlsXMLUtil.getElementByXpath(outDocCommonCodeForDistance, KohlsConstants.COMMON_CODE_LIST_COMMONCODE);
		String MAX_CAP = ExtnIneleigibleList.getAttribute(KohlsConstants.CODE_LONG_DESC);

		Set<String> storeList = new HashSet<String>();

		Element getInDocElement = inDoc.getDocumentElement();
		String orderHeaderKey = getInDocElement.getAttribute(KohlsConstants.OREDER_REFERENCE);
		/*
		 * Supply Correction based on the distance capping logic should be carried out only 
		 * in context of an order. Hence a null check is being put.
		 * This case occurs when user try to check the inventory from the Inventory Console
		 * 
		 */
		if(!YFCCommon.isVoid(orderHeaderKey)){		
			Document outDoc = KohlsCommonAPIUtil.callGetOrderDetails(env, orderHeaderKey);
	
			Element outDocEle = (Element) outDoc.getElementsByTagName(KohlsConstants.ORDER).item(0);
			Element personInfo = (Element) outDoc.getElementsByTagName(KohlsConstants.PERSON_INFO_SHIP_TO).item(0);
			
			//Setting the Object so that its available in the getDistanceUE to decide if Additional Cost is to be added.
			((YFSContext) env).setTxnObject("InvokedFrom", "INVSuplyCorrectionUE");
			Document outDocGetSurroundingNodeList = KohlsCommonAPIUtil.callGetSurroundingNodeListAPI(env, Integer.parseInt(MAX_CAP), personInfo);
			((YFSContext) env).setTxnObject("InvokedFrom", "");
			log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: getSurroundingNodeList() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(outDocGetSurroundingNodeList)).toString());
			NodeList itemList = getInDocElement.getElementsByTagName(KohlsConstants.ITEM);
			int itemListLength=itemList.getLength();
			for (int j = 0; j < itemListLength; j++) {
				try{
				Element itemElement = (Element) itemList.item(j);
				Element supplies = (Element) itemElement.getElementsByTagName(KohlsConstants.SUPPLIES).item(0);
				NodeList supplyList = supplies.getElementsByTagName(KohlsConstants.SUPPLY);
				int supplyListLength=supplyList.getLength();
				for (int k = 0; k < supplyListLength; k++) {
					Element supplyEle = (Element) supplyList.item(k);
					if(YFCCommon.isVoid(supplyEle))
					{
						log.debug("No More Supplies to iterate..!!");
						break;
					}
					String shipNode = supplyEle.getAttribute(KohlsConstants.SHIP_NODE);
					storeList.add(shipNode);
					Node node = (Node) KohlsXPathUtil.getNode(outDocGetSurroundingNodeList, KohlsConstants.XPATH_SURROUNDING_LIST + shipNode + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET);
					//Remove the supply only for the STORES which are outside the MAX_CAPPING radius
					String strNodeType=YFS_Ship_NodeDBHome.getInstance().selectWithPK((YFSContext) env, shipNode).getNode_Type();
					if (node == null && KohlsConstants.STORE.equals(strNodeType) ) {
						supplies.removeChild(supplyEle);
						k--;
					}
				}
				}
				catch(Exception e)
				{
				    final YFSUserExitException uee = new YFSUserExitException(e.getMessage());
				    uee.setStackTrace(e.getStackTrace());
				    throw uee;
				}
			}
		}
		if(log.isDebugEnabled()){
			log.debug("******************KohlsINVGetSupplyCorrectionsUE::: -  NO ORDER CONTEXT AVAILABLE... HENCE NO DISTANCE CAPPING LOGIC HAS BEEN PERFORMED******");
		}
		return inDoc;

	}
	/*supplyCorrection UE implementation logic
	 * the Input XML to this method is same as the one being supplied to getSupplyCorrections() method
	 * inputXML for the service DB which has  Extended database API component which gives the list of details for these ShipNodes
	 * Complex Query Formation 
	 * <ExtnIneleigible> 
	 * 	 <ComplexQuery Operator="OR"> 
	 * 		<Or> 
	 * 			<Exp Name="ShipNode" QryType="EQ" Value="KohlsStor3"/> 
	 * 			<Exp Name="ShipNode" QryType="EQ" Value="KohlsStore2"/> 
	 * 			<Exp Name="ShipNode" QryType="EQ" Value="KohlsStore1"/> 
	 * 		</Or> 
	 * 	 </ComplexQuery> 
	 * </ExtnIneleigible>
	 *  Using these list of nodes we will call xpathutil method passing ShipNode,ItemID,UOM 
	 *  and ShipNode, ClassID combination.
	 *  The outPut will be XML which would have removed supply for nodes where current date falls within date range of the nodes.
	 *  OutputXML
	 *  <Items OrderReference="2013042618403890703">
     *		<Item ItemID="87890371" OrganizationCode="DEFAULT" ProductClass="Good" UnitOfMeasure="EACH">
     *   		<Supplies>
     *       		<Supply AvailabilityType="TRACK" Quantity="100.00"
     *           	Segment="" SegmentType="" ShipNode="KohlsStor3"
     *           	SupplyLineReference="" SupplyReference=""
     *           	SupplyReferenceType="" SupplyType="ONHAND"/>
     *       		<Supply AvailabilityType="TRACK" Quantity="100.00"
     *           	Segment="" SegmentType="" ShipNode="KohlsStore2"
     *           	SupplyLineReference="" SupplyReference=""
     *           	SupplyReferenceType="" SupplyType="ONHAND"/>
     *   		</Supplies>
     *		</Item>
	 *	</Items>
     *
	 *   
	 * 
	 */
	private Document doSupplyCorrectionsOnItem(YFSEnvironment env, Document inDoc) throws YFSUserExitException {
		
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: doSupplyCorrectionsOnItem() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
		Set<String> storeList = new HashSet<String>();
		Element inEle = inDoc.getDocumentElement();
		Document ExtnIneliDoc = SCXmlUtil.createDocument(KohlsConstants.EXTN_ITEM_IN_ELIGIBILITY);
		Element ExtnIneliEle = ExtnIneliDoc.getDocumentElement();
		NodeList itemList = inEle.getElementsByTagName(KohlsConstants.ITEM);
		int itemListLength=itemList.getLength();
		for (int i = 0; i < itemListLength; i++) {
			Element itemElement = (Element) itemList.item(i);
			Element supplies = (Element) itemElement.getElementsByTagName(KohlsConstants.SUPPLIES).item(0);
			NodeList supplyList = supplies.getElementsByTagName(KohlsConstants.SUPPLY);
			int supplyListLength=supplyList.getLength();
			for (int j = 0; j < supplyListLength; j++) {
				Element supplyEle = (Element) supplyList.item(j);
				storeList.add(supplyEle.getAttribute(KohlsConstants.SHIP_NODE));
			}
		}
		Element complexQry = ExtnIneliDoc.createElement(KohlsConstants.COMPLEX_QUERY);
		ExtnIneliEle.appendChild(complexQry);
		complexQry.setAttribute(KohlsConstants.OPERATOR, KohlsConstants.OR);
		Element orElement = ExtnIneliDoc.createElement(KohlsConstants.OR);
		orElement = createExpList(ExtnIneliDoc, orElement, storeList);
		complexQry.appendChild(orElement);
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: invokeService() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(ExtnIneliDoc)).toString());
		try {
			Document outDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_ITEM_IN_ELIGIBILITY, ExtnIneliDoc);
			log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: invokeService() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(outDoc)).toString());
			NodeList extnInelgList = outDoc.getElementsByTagName(KohlsConstants.EXTN_ITEM_IN_ELIGIBILITY);

			if (extnInelgList.getLength() == 0) {
				
				return inDoc;

			}
			int itemListLength1=itemList.getLength();
			for (int i = 0; i < itemListLength1; i++) {
				Element itemElement = (Element) itemList.item(i);
				String ItemID = itemElement.getAttribute(KohlsConstants.ITEM_ID);
				String UOM = itemElement.getAttribute(KohlsConstants.UNIT_OF_MEASURE);
				String organizationCode = itemElement.getAttribute(KohlsConstants.ORGANIZATION_CODE);
				Element supplies = (Element) itemElement.getElementsByTagName(KohlsConstants.SUPPLIES).item(0);
				NodeList supplyList = supplies.getElementsByTagName(KohlsConstants.SUPPLY);
				int supplylistLength1=supplyList.getLength();
				for (int j = 0; j < supplylistLength1; j++) {
					Element supplyEle = (Element) supplyList.item(j);
					if(YFCCommon.isVoid(supplyEle))
					{
						log.debug("No More Supplies to iterate..!!");
						break;
					}
					String node = supplyEle.getAttribute(KohlsConstants.SHIP_NODE);
					Element node1 = (Element) KohlsXPathUtil.getNode(outDoc, KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY + ItemID + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE + node + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_UOM + UOM + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET);
					if (node1 != null) {
						if (isCurrentDateWithinRange(node1)) {
							supplies.removeChild(supplyEle);
							j--;
						} else {
							continue;
						}
					} else {

						Document outTemplate = createDocforItemDetails(ItemID, UOM, organizationCode, env);
						log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: createDocforItemDetails() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(outTemplate)).toString());
						Element outEle = KohlsXMLUtil.getElementByXpath(outTemplate, KohlsConstants.ITEM_EXTN);
						String extnDept = outEle.getAttribute(KohlsConstants.EXTN_DEPT);
						String extnClass = outEle.getAttribute(KohlsConstants.EXTN_CLASS);
						String extnSubClass = outEle.getAttribute(KohlsConstants.EXTN_SUBCLASS);
						String extnStyle=outEle.getAttribute(KohlsConstants.EXTN_STYLE);
						String classID = null;
						if (extnSubClass != null) {
							classID = extnDept + "/" + extnClass + "/" + extnSubClass;
							Element classEle = (Element) KohlsXPathUtil.getNode(outDoc, KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_CLASS_ID + classID + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE + node + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET);
							if (classEle != null) {
								if (isCurrentDateWithinRange(classEle)) {
									supplies.removeChild(supplyEle);
									j--;
								}
							}
						}
						if (extnClass != null) {
							classID = extnDept + "/" + extnClass;
							Element classEle1 = (Element) KohlsXPathUtil.getNode(outDoc, KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_CLASS_ID + classID + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE + node + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET);
							if (classEle1 != null) {
								if (isCurrentDateWithinRange(classEle1)) {
									supplies.removeChild(supplyEle);
									j--;
								}
							}
						}
						if (extnDept != null) {
							classID = extnDept;
							Element classEle2 = (Element) KohlsXPathUtil.getNode(outDoc, KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_CLASS_ID + classID + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE + node + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET);
							if (classEle2 != null) {
								if (isCurrentDateWithinRange(classEle2)) {
									supplies.removeChild(supplyEle);
									j--;
								}
							}
						}
						if(extnStyle!=null)
						{
							Element style=(Element) KohlsXPathUtil.getNode(outDoc,KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_STYLE_ID + extnStyle + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET + KohlsConstants.XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE + node + KohlsConstants.XPATH_SURROUNDING_LIST_SQUARE_BRACKET );
							if (style != null) {
								if (isCurrentDateWithinRange(style)) {
									supplies.removeChild(supplyEle);
									j--;
								}
							}
						}

					}
				}

			}
		}

		catch (Exception e) {
			   final YFSUserExitException uee = new YFSUserExitException(e.getMessage());
			    uee.setStackTrace(e.getStackTrace());
			    throw uee;
		}
	
		return inDoc;
	}

	/* Input formation to get the details of the nodes present in the 
	 * EXTN_STORE_INELIGIBILITY table
	 * Complex Query Formation 
	 * <Or> 
	 * 		<Exp Name="ShipNode" Value="node1" QryType="EQ"/> 
	 * 		<Exp Name="ShipNode" Value="node2" QryType="EQ"/> 
	 * </Or>
	 */
	private Element createExpList(Document indoc, Element orElement, Set<String> storeList) {
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: createExpList() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(indoc)).toString());
		Iterator itr = storeList.iterator();
		while (itr.hasNext()) {
			String value = (String) itr.next();
			Element expElement = indoc.createElement(KohlsConstants.EXP);
			expElement.setAttribute(KohlsConstants.NAME, KohlsConstants.SHIP_NODE);
			expElement.setAttribute(KohlsConstants.VALUE, value);
			expElement.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
			orElement.appendChild(expElement);

		}
		return orElement;
	}
	/*
	 * Method to determine whether the current date falls between FromDate and ToDate Range in the 
	 * EXTN_STORE_INELIGIBILITY table.
	 * Returns boolean value true/false.
	 */
	private boolean isCurrentDateWithinRange(Element ExtnIneleigibleList) throws ParseException {
		SimpleDateFormat dateFormatter = new SimpleDateFormat(KohlsConstants.DATE_FORMAT);
		String strDate = ExtnIneleigibleList.getAttribute(KohlsConstants.FROM_DATE);
		String endDate = ExtnIneleigibleList.getAttribute(KohlsConstants.TO_DATE);
		Date utilStartDate = dateFormatter.parse(strDate);
		Date utilEndDate = dateFormatter.parse(endDate);
		String curDate = KohlsDateUtil.getCurrentDateTime(KohlsConstants.DATE_FORMAT);
		if (((KohlsCommonUtil.getYFCDate(curDate).before(utilEndDate)) && (KohlsCommonUtil.getYFCDate(curDate).after(utilStartDate))) 
				|| (KohlsCommonUtil.getYFCDate(curDate).equals(utilStartDate)) || (KohlsCommonUtil.getYFCDate(curDate).equals(utilEndDate))) {
			return true;
		}
		return false;
	}
	 /*Input XML for getItemDetails() API
	  * <Item ItemID="" UnitOfMeasure="" OrganizationCode=""/>
	 * 
	 */
	private Document createDocforItemDetails(String ItemID, String UOM, String organizationCode, YFSEnvironment env) throws Exception {
		Document inTemplate = SCXmlUtil.createDocument(KohlsConstants.ITEM);
		Element inElement = inTemplate.getDocumentElement();
		inElement.setAttribute(KohlsConstants.ITEM_ID, ItemID);
		inElement.setAttribute(KohlsConstants.UNIT_OF_MEASURE, UOM);
		inElement.setAttribute(KohlsConstants.ORGANIZATION_CODE, organizationCode);
		Document outTemplate=KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_DETAILS_SUPPLY_CORRECTIONS, KohlsConstants.GET_ITEM_DETAILS, inTemplate);
		log.debug((new StringBuilder("\n KohlsGetSupplyCorrectionsUE :: invokeAPI() :: OutputXML\n")).append(KohlsXMLUtil.getXMLString(outTemplate)).toString());
		
		return outTemplate;

	}

}
//End --KohlsOCF by IBM - 10/06/2013//