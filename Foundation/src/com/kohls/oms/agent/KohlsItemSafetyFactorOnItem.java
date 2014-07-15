package com.kohls.oms.agent;


import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSStringUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.oms.ue.KohlsGetSupplyCorrectionsUE;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class KohlsItemSafetyFactorOnItem extends YCPBaseAgent {
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsItemSafetyFactorOnItem.class);

	public void executeJob(YFSEnvironment env, Document inDoc) throws Exception {
		/*
		 * (non-Javadoc)
		 * 
		 * @see com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.YFSEnvironment,
		 *      org.w3c.dom.Document)
		 * 
		 * Execute jobs will process the records it received from getJobs
		 * method. Updates the on hand safety factor for each item depending on
		 * the safetyFactor by calling manageItem API. this will continued till
		 * all record are updated And then updates the Processed flag to Y in
		 * the custom table once all the records are updated. Input to this
		 * method will be <ExtnItemSafetyFactorList> <ExtnItemSafetyFactor
		 * CatDepartment="15" CatClass="90" CatSubClass="93" Processed="N"
		 * SafetyFactor="30" SftFlag="Q" SafetyOperation="D" UserID=""
		 * ProgramID=""/> <ExtnItemSafetyFactorList>
		 */
		Document updateSafetyDoc = KohlsXMLUtil.createDocument(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: createDocforExtnSafetyPercentageChange() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(updateSafetyDoc)).toString());
		Element updateSafetyele = updateSafetyDoc.getDocumentElement();
		Element eleLineDetails = null;
		NodeList outNodeList = inDoc.getElementsByTagName(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
		int length = outNodeList.getLength();
		for (int i = 0; i < length; i++) {

			eleLineDetails = ((Element) outNodeList.item(i));
			Document inDocItemList = KohlsXMLUtil.createDocument(KohlsConstants.ITEM);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: createDocforgetItemList() ::InPutXML\n")).append(KohlsXMLUtil.getXMLString(inDocItemList)).toString());
			Element inDocItemListEle = inDocItemList.getDocumentElement();
			inDocItemListEle.setAttribute(KohlsConstants.MAXIMUM_RECORDS, KohlsConstants.MAXIMUM_RECORDS_5000);
			String dept = eleLineDetails.getAttribute(KohlsConstants.CAT_DEPARTMENT);
			String classID = eleLineDetails.getAttribute(KohlsConstants.CAT_CLASS);
			String subClass = eleLineDetails.getAttribute(KohlsConstants.CAT_SUBCLASS);
			String safetyFactor = eleLineDetails.getAttribute(KohlsConstants.SAFTY_FACTOR);
			String operation = eleLineDetails.getAttribute(KohlsConstants.SAFTY_OPERATION);
			String flag = eleLineDetails.getAttribute(KohlsConstants.SFT_FLAG);
			String default_Safety_Factor = eleLineDetails.getAttribute(KohlsConstants.DEFAULT_SAFETY_FACTOR);
			String extnItemSafetyFactorKey = eleLineDetails.getAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY);

			Element itemInEle = KohlsXMLUtil.createChild(inDocItemListEle, KohlsConstants.EXTN);
			itemInEle.setAttribute(KohlsConstants.EXTN_DEPT, dept);
			itemInEle.setAttribute(KohlsConstants.EXTN_CLASS, classID);
			itemInEle.setAttribute(KohlsConstants.EXTN_SUBCLASS, subClass);

			Document outItemList = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_LIST, KohlsConstants.GET_ITEMLIST, inDocItemList);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: templateDocforgetItemList() :: outPutXML\n")).append(KohlsXMLUtil.getXMLString(outItemList)).toString());
			Element outItemListEle = outItemList.getDocumentElement();
			String strTotalItemList = outItemListEle.getAttribute(KohlsConstants.TOTAL_ITEMLIST);
			String strTotalNoOfRecords = outItemListEle.getAttribute(KohlsConstants.TOTAL_NO_OF_RECORDS);

			int iNoOfRecords = Integer.parseInt(strTotalItemList);
			int noofRecords = Integer.parseInt(strTotalNoOfRecords);
			manageItem(env, outItemList, safetyFactor, flag, operation, default_Safety_Factor, extnItemSafetyFactorKey);
			NodeList list1 = (NodeList) outItemListEle.getElementsByTagName(KohlsConstants.ITEM_LIST);
			Element LastItemKey = (Element) outItemList.getElementsByTagName(KohlsConstants.ITEM_LIST).item(list1.getLength());
			String strLastItemKey = LastItemKey.getAttribute(KohlsConstants.LAST_ITEM_KEY);
			inDocItemListEle.setAttribute(KohlsConstants.START_ITEM_KEY, strLastItemKey);
			while (iNoOfRecords < noofRecords) {

				/*
				 * Input to getItemList API <Item> <Extn ExtnDept=""
				 * ExtnClass="" ExtnSubClass=""/> </Item>
				 * 
				 * Template <ItemList TotalItemList="" TotalNumberOfRecords=""
				 * LastItemKey="" > <Item> <InventoryParameters
				 * OnhandSafetyFactorQuantity="" /> </Item> </ItemList>
				 */

				Document outItemList1 = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_LIST, KohlsConstants.GET_ITEMLIST, inDocItemList);
				kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: outDocforgetItemList2() :: outDocXML\n")).append(KohlsXMLUtil.getXMLString(outItemList1)).toString());

				manageItem(env, outItemList1, safetyFactor, flag, operation, default_Safety_Factor, extnItemSafetyFactorKey);

				Element outItemListEle1 = outItemList1.getDocumentElement();
				String strTotalItemList1 = outItemListEle1.getAttribute(KohlsConstants.TOTAL_ITEMLIST);
				int iNoOfRecords1 = Integer.parseInt(strTotalItemList1);

				NodeList list2 = (NodeList) outItemListEle1.getElementsByTagName(KohlsConstants.ITEM_LIST);
				Element LastItemKey1 = (Element) outItemList1.getElementsByTagName(KohlsConstants.ITEM_LIST).item(list2.getLength());

				String strLastItemKey1 = LastItemKey1.getAttribute(KohlsConstants.LAST_ITEM_KEY);
				inDocItemListEle.setAttribute(KohlsConstants.START_ITEM_KEY, strLastItemKey1);
				iNoOfRecords = iNoOfRecords + iNoOfRecords1;
			}
		}

		for (int j = 0; j < length; j++) {
			eleLineDetails = ((Element) outNodeList.item(j));
			updateSafetyele.setAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY, eleLineDetails.getAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY));
			updateSafetyele.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.YES);
			Document updateSafetyOutDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_GET_ITEM_SAFETY_FACTOR_CHANGE, updateSafetyDoc);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: outDocExtnSafetyPercentageChange() :: outDocXML\n")).append(KohlsXMLUtil.getXMLString(updateSafetyOutDoc)).toString());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#getJobs(com.yantra.yfs.japi.YFSEnvironment,
	 *      org.w3c.dom.Document, org.w3c.dom.Document) Input Document to the
	 *      service which has custom table listAPI <ExtnItemSafetyFactorList
	 *      Processed="N"/> each record from output of service will be passed as
	 *      document to execute jobs.
	 */

	public List<Document> getJobs(YFSEnvironment env, Document criteria, Document lastMessageCreated) throws Exception {
		Document inDoc = KohlsXMLUtil.createDocument(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_LIST);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: createDocforExtnSafetyPercentageList() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
		Element inEle = inDoc.getDocumentElement();
		inEle.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.NO);
		Document outDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_ITEM_SAFTY_FACTOR_LIST, inDoc);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: outDocforExtnSafetyPercentageList() :: OutPutXML\n")).append(KohlsXMLUtil.getXMLString(outDoc)).toString());
		List<Document> itemElgListDoc = new ArrayList<Document>();
		NodeList outNodeList = outDoc.getElementsByTagName(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
		if (outNodeList.getLength() == 0) {
			return super.getJobs(env, criteria, lastMessageCreated);
		}
		int length1 = outNodeList.getLength();
		for (int i = 0; i < length1; i++) {
			((List<Document>) itemElgListDoc).add(KohlsXMLUtil.getDocumentForElement((Element) outNodeList.item(i)));
			
			Document updateSafetyDoc = KohlsXMLUtil.createDocument(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
			Element updateEle=updateSafetyDoc.getDocumentElement();
			Element eleLineDetails = ((Element) outNodeList.item(i));
			updateEle.setAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY, eleLineDetails.getAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY));
			updateEle.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.P);
			Document processsedDocument=KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_GET_ITEM_SAFETY_FACTOR_CHANGE, updateSafetyDoc);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: updateDocforchangeExtnSafetyPercentage() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(processsedDocument)).toString());
			
			
		}
		
		
		return itemElgListDoc;
	}

	@Override
	public List getJobs(YFSEnvironment env, Document inXML) throws Exception {

		return super.getJobs(env, inXML);
	}

	/*
	 * method to add the percentage to the onhandSafetyfactorqty
	 */

	private int percentageTowholevalueAdd(String safetyPerc, float onhandSafetyFactorQuantity) {
		float j = (onhandSafetyFactorQuantity) * Float.valueOf(safetyPerc) / 100 + onhandSafetyFactorQuantity;
		return Math.round(j);

	}

	/*
	 * method to subtract the percentage to the onhandSafetyfactorqty
	 */
	private int percentageTowholevalueSub(String safetyPerc, float onhandSafetyFactorQuantity) {
		float j = onhandSafetyFactorQuantity - onhandSafetyFactorQuantity * Float.valueOf(safetyPerc) / 100;
		return Math.round(j);

	}

	/*
	 * Method which adds or subtracts on hand Safety Factor either by percentage
	 * or quantity Input This Method will be output from the getItemList() API
	 * *outItemList* <ItemList> <Item> </Item> </ItemList>
	 */

	private void manageItem(YFSEnvironment env, Document outItemList, String safetyFactor, String flag, String operation, String default_Safety_Factor, String extnItemSafetyFactorKey) throws Exception {
		try{
			
		
		Element outItemListEle = outItemList.getDocumentElement();
		NodeList inventoryNodeList = (NodeList) outItemListEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS);
		NodeList inventoryNodeList1 = (NodeList) outItemListEle.getElementsByTagName(KohlsConstants.ITEM);
		NodeList extnNodeList = (NodeList) outItemListEle.getElementsByTagName(KohlsConstants.EXTN);
		double extnItemSafetyFactorKeyint = Double.parseDouble(extnItemSafetyFactorKey);
		int length1 = inventoryNodeList.getLength();
		
		for (int j = 0; j < length1; j++) {
			Element inventoryParameters = (Element) inventoryNodeList.item(j);
			Element itemParameters = (Element) inventoryNodeList1.item(j);
			Element extnNodeListElement = (Element) extnNodeList.item(j);
			double kohlsSafetyFactorReqint;
			float onhandSafetyFactorFloat ;
			String kohlsSafetyFactorReq = extnNodeListElement.getAttribute(KohlsConstants.KOHLS_SAFETY_FACTOR_REQ);
			kohlsSafetyFactorReqint=KOHLSStringUtil.isEmpty(kohlsSafetyFactorReq) == true ? 0:  Double.parseDouble(kohlsSafetyFactorReq);
			

			String itemKey = itemParameters.getAttribute(KohlsConstants.ITEM_KEY);
			String onhandSafetyFactorQuantity = inventoryParameters.getAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY);
			onhandSafetyFactorFloat =	KOHLSStringUtil.isEmpty(onhandSafetyFactorQuantity) == true ? 0:  Float.valueOf(onhandSafetyFactorQuantity);
			float default_safety_factor_float=KOHLSStringUtil.isEmpty(default_Safety_Factor) == true ? 0:  Float.valueOf(default_Safety_Factor);
			if (kohlsSafetyFactorReqint < extnItemSafetyFactorKeyint) {
				if (onhandSafetyFactorFloat == 0) {
					
					onhandSafetyFactorFloat = default_safety_factor_float;
					
					if (flag.equalsIgnoreCase(KohlsConstants.P)) {
						if (operation.equalsIgnoreCase(KohlsConstants.I)) {
							onhandSafetyFactorQuantity = String.valueOf(percentageTowholevalueAdd(safetyFactor, onhandSafetyFactorFloat));
						} else {
							onhandSafetyFactorQuantity = String.valueOf(percentageTowholevalueSub(safetyFactor, onhandSafetyFactorFloat));
						}
					} else {
						if (operation.equalsIgnoreCase(KohlsConstants.I)) {
							onhandSafetyFactorQuantity = String.valueOf(onhandSafetyFactorFloat + Float.valueOf(safetyFactor));
						} else {
							if (onhandSafetyFactorFloat < Float.valueOf(safetyFactor)) {
								onhandSafetyFactorQuantity = "0";
							} else
								onhandSafetyFactorQuantity = String.valueOf(onhandSafetyFactorFloat - Float.valueOf(safetyFactor));
						}

					}
					
					
					Document inDocMangeItem = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_LIST);
					kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: inDocformanageItem() :: inDocXML\n")).append(KohlsXMLUtil.getXMLString(inDocMangeItem)).toString());
					Element inEleMangeItem = inDocMangeItem.getDocumentElement();
					Element inEleMangeItem1 = KohlsXMLUtil.createChild(inEleMangeItem, KohlsConstants.ITEM);
					inEleMangeItem1.setAttribute(KohlsConstants.ITEM_KEY, itemKey);
					Element inEleMangeItem2 = KohlsXMLUtil.createChild(inEleMangeItem1, KohlsConstants.INVENTORY_PARAMETERS);
					Element inEleMangeItem3 = KohlsXMLUtil.createChild(inEleMangeItem1, KohlsConstants.EXTN);
					inEleMangeItem3.setAttribute(KohlsConstants.KOHLS_SAFETY_FACTOR_REQ, extnItemSafetyFactorKey);
					inEleMangeItem2.setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, onhandSafetyFactorQuantity);
					KohlsCommonUtil.invokeAPI(env, KohlsConstants.MANAGE_ITEM, inDocMangeItem);
					
					} else {

					

					if (flag.equalsIgnoreCase(KohlsConstants.P)) {
						
						if (operation.equalsIgnoreCase(KohlsConstants.I)) {
							onhandSafetyFactorQuantity = String.valueOf(percentageTowholevalueAdd(safetyFactor, onhandSafetyFactorFloat));
							
						} else {
							onhandSafetyFactorQuantity = String.valueOf(percentageTowholevalueSub(safetyFactor, onhandSafetyFactorFloat));
						}
					} else {
						if (operation.equalsIgnoreCase(KohlsConstants.I)) {
							onhandSafetyFactorQuantity = String.valueOf(onhandSafetyFactorFloat + Float.valueOf(safetyFactor));
								
						} else {
							if (Float.valueOf(onhandSafetyFactorQuantity) < Float.valueOf(safetyFactor)) {
								onhandSafetyFactorQuantity = String.valueOf(0.00);
							} else
								onhandSafetyFactorQuantity = String.valueOf(onhandSafetyFactorFloat - Float.valueOf(safetyFactor));
						}

					}

					/*
					 * Input to mangeItem() API <ItemList> <Item ItemKey="">
					 * <InventoryParameters OnhandSafetyFactorQuantity=""/>
					 * </Item> </ItemList>
					 */

					Document inDocMangeItem = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_LIST);
					kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: inDocformanageItem() :: inDocXML\n")).append(KohlsXMLUtil.getXMLString(inDocMangeItem)).toString());
					Element inEleMangeItem = inDocMangeItem.getDocumentElement();
					Element inEleMangeItem1 = KohlsXMLUtil.createChild(inEleMangeItem, KohlsConstants.ITEM);
					inEleMangeItem1.setAttribute(KohlsConstants.ITEM_KEY, itemKey);
					Element inEleMangeItem2 = KohlsXMLUtil.createChild(inEleMangeItem1, KohlsConstants.INVENTORY_PARAMETERS);
					Element inEleMangeItem3 = KohlsXMLUtil.createChild(inEleMangeItem1, KohlsConstants.EXTN);
					inEleMangeItem3.setAttribute(KohlsConstants.KOHLS_SAFETY_FACTOR_REQ, extnItemSafetyFactorKey);
					inEleMangeItem2.setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, onhandSafetyFactorQuantity);
					KohlsCommonUtil.invokeAPI(env, KohlsConstants.MANAGE_ITEM, inDocMangeItem);
				}

			}

			else
				continue;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Document inDoc=KohlsCommonUtil.emailMethod(env);
			KohlsCommonUtil.invokeService(env, KohlsConstants.EMAIL_NOTIFY_SAFETY, inDoc);
			throw e;
		}

	}
	
	
}
