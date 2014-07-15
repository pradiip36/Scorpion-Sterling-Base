package com.kohls.oms.agent;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KOHLSStringUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsItemEligibilityBulkUpdate extends YCPBaseAgent {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.
	 *      YFSEnvironment, org.w3c.dom.Document) This Method will process the
	 *      record from the getJobs Method <ItemEligibilityList>
	 *      <ItemEligibility CatDepartment="15" CatSubClass="93" CatClass="90"
	 *      VendorId="" Processed="N" Style="" ApplyNewItem="" User=""
	 *      SetEligibleAs="STORE" ProgramId="" /> </ItemEligibilityList> So each
	 *      record will be processed and in the end the Processed flag will be
	 *      updated as Y
	 */
	
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsItemEligibilityBulkUpdate.class);
	public void executeJob(YFSEnvironment env, Document inDoc) throws Exception {

		NodeList itemElgList = inDoc.getElementsByTagName(KohlsConstants.ITEM_ELIGIBILITY);

		Element eleLineDetails = null;
		int length = itemElgList.getLength();

		for (int i = 0; i < length; i++) {

			eleLineDetails = ((Element) itemElgList.item(i));
			String dept = eleLineDetails.getAttribute(KohlsConstants.CAT_DEPARTMENT);
			String classID = eleLineDetails.getAttribute(KohlsConstants.CAT_CLASS);
			String subClass = eleLineDetails.getAttribute(KohlsConstants.CAT_SUBCLASS);
			String vendorID = eleLineDetails.getAttribute(KohlsConstants.VENDORID);
			String style = eleLineDetails.getAttribute(KohlsConstants.STYLE);
			String setEligibleAs = eleLineDetails.getAttribute(KohlsConstants.SET_ELIGIBLE_AS);
			String extnStoreEligblKey=eleLineDetails.getAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY);

			Document itemInDoc = createInDocForGetItemList(env, dept, classID, subClass, vendorID, style, null);

			Document itemListOutDoc = callGetItemListAPI(env, itemInDoc);

			Element outItemListEle = itemListOutDoc.getDocumentElement();

			String strTotalItemList = outItemListEle.getAttribute(KohlsConstants.TOTAL_ITEMLIST);
			String strTotalNoOfRecords = outItemListEle.getAttribute(KohlsConstants.TOTAL_NO_OF_RECORDS);

			int iNoOfRecords = Integer.parseInt(strTotalItemList);
			int noofRecords = Integer.parseInt(strTotalNoOfRecords);

			manageItem(env, itemListOutDoc, setEligibleAs,extnStoreEligblKey);

			NodeList nextList = (NodeList) outItemListEle.getElementsByTagName(KohlsConstants.ITEM_LIST);
			Element LastItemKey = (Element) itemListOutDoc.getElementsByTagName(KohlsConstants.ITEM_LIST).item(nextList.getLength());

			String strLastItemKey = LastItemKey.getAttribute(KohlsConstants.LAST_ITEM_KEY);

			while (iNoOfRecords < noofRecords) {

				Document outItemList1 = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_LIST_ITEM_ELIGIBILITY, KohlsConstants.GET_ITEMLIST, createInDocForGetItemList(env, dept, classID, subClass, vendorID, style, strLastItemKey));
				kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: outDocforGetItemList2() :: outDocXML\n")).append(KohlsXMLUtil.getXMLString(outItemList1)).toString());
				manageItem(env, outItemList1, setEligibleAs,extnStoreEligblKey);
				Element outItemListEle1 = outItemList1.getDocumentElement();
				String strTotalItemList1 = outItemListEle1.getAttribute(KohlsConstants.TOTAL_ITEMLIST);
				int iNoOfRecords1 = Integer.parseInt(strTotalItemList1);
				NodeList list2 = (NodeList) outItemListEle1.getElementsByTagName(KohlsConstants.ITEM_LIST);
				Element LastItemKey1 = (Element) outItemList1.getElementsByTagName(KohlsConstants.ITEM_LIST).item(list2.getLength());
				strLastItemKey = LastItemKey1.getAttribute(KohlsConstants.LAST_ITEM_KEY);
				iNoOfRecords = iNoOfRecords + iNoOfRecords1;

			}
		}

		Document updateItemElg = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_ELIGIBILITY);
		Element updateItemElgEle = updateItemElg.getDocumentElement();

		for (int j = 0; j < length; j++) {
			eleLineDetails = ((Element) itemElgList.item(j));
			updateItemElgEle.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.YES);
			updateItemElgEle.setAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY, eleLineDetails.getAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY));
			Document updateSafetyOutDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.UPDATE_ITEMELG, updateItemElg);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: outDocItemEligibilityChange() :: outDocXML\n")).append(KohlsXMLUtil.getXMLString(updateSafetyOutDoc)).toString());
		}

	}

	/*
	 * The method will call getItemList() API
	 */
	private Document callGetItemListAPI(YFSEnvironment env, Document itemInDoc) throws Exception {

		Document outItemList = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_LIST_ITEM_ELIGIBILITY, KohlsConstants.GET_ITEMLIST, itemInDoc);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: callGetItemListAPI() :: outPutXML\n")).append(KohlsXMLUtil.getXMLString(outItemList)).toString());
		return outItemList;
	}

	/*
	 * This method will create the input document for getItemList() API <Item
	 * ItemID="92058832" ItemIDQryType="FLIKE" IsHazmat="" IsLiquid="" >
	 * <PrimaryInformation PrimarySupplier="000338830"
	 * PrimarySupplierQryType="FLIKE" ItemType=""/> <Extn ExtnStyle="8669"
	 * ExtnDepartment="0227" ExtnClass="0010" ExtnSubClass="0010"
	 * ExtnBreakable="" ExtnShipAlone="" ExtnCageItem="" /> </Item>
	 */
	private Document createInDocForGetItemList(YFSEnvironment env, String dept, String classID, String subClass, String vendorID, String style, String startItemKey) throws ParserConfigurationException, TransformerException {

		Document inDocItemList = KohlsXMLUtil.createDocument(KohlsConstants.ITEM);

		Element inDocItemListEle = inDocItemList.getDocumentElement();
		inDocItemListEle.setAttribute(KohlsConstants.MAXIMUM_RECORDS, KohlsConstants.MAXIMUM_RECORDS_5000);
		if (!YFCCommon.isVoid(startItemKey)) {
			inDocItemListEle.setAttribute(KohlsConstants.START_ITEM_KEY, startItemKey);
		}

		Element itemPrimaryInfo = KohlsXMLUtil.createChild(inDocItemListEle, KohlsConstants.PRIMARY_INFORMATION);
		itemPrimaryInfo.setAttribute(KohlsConstants.PRIMARY_SUPPLIER, vendorID);
		itemPrimaryInfo.setAttribute(KohlsConstants.PRIMARY_SUPPLIER_QRY_TYPE, KohlsConstants.FLIKE);

		Element itemInEle = KohlsXMLUtil.createChild(inDocItemListEle, KohlsConstants.EXTN);
		if (!YFCCommon.isVoid(dept))
			itemInEle.setAttribute(KohlsConstants.EXTN_DEPT, dept);
		if (!YFCCommon.isVoid(classID))
			itemInEle.setAttribute(KohlsConstants.EXTN_CLASS, classID);
		if (!YFCCommon.isVoid(subClass))
			itemInEle.setAttribute(KohlsConstants.EXTN_SUBCLASS, subClass);
		if (!YFCCommon.isVoid(style))
			itemInEle.setAttribute(KohlsConstants.EXTN_STYLE, style);

		inDocItemListEle.appendChild(itemPrimaryInfo);
		inDocItemListEle.appendChild(itemInEle);
		Document inputDocForAPI = KohlsCommonUtil.setBulkExclusionForitem(env, inDocItemList);

		kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: createDocforGetItemList() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inputDocForAPI)).toString());

		return inputDocForAPI;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#getJobs(com.yantra.yfs.japi.YFSEnvironment,
	 *      org.w3c.dom.Document, org.w3c.dom.Document) Get Jobs will return the
	 *      set of records from the custom table item eligibility with Processed
	 *      flag as N <ItemEligibilityList > <ItemEligibility CatDepartment="15"
	 *      CatSubClass="93" CatClass="90" VendorId="" Processed="N" Style=""
	 *      ApplyNewItem="" User="" SetEligibleAs="STORE" ProgramId="" />
	 *      </ItemEligibilityList>
	 */
	
	public List<Document> getJobs(YFSEnvironment env, Document criteria, Document lastMessageCreated) throws Exception {

		Document outDoc = getAllRecordsFromDB(env);

		kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: createDocforItemElgBulkUpdate() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(outDoc)).toString());

		List<Document> itemElgListDoc = new ArrayList<Document>();

		NodeList outNodeList = outDoc.getElementsByTagName(KohlsConstants.ITEM_ELIGIBILITY);

		int length = outNodeList.getLength();

		if (outNodeList.getLength() == 0) {
			return super.getJobs(env, criteria, lastMessageCreated);
		}

		for (int i = 0; i < length; i++) {
			((List<Document>) itemElgListDoc).add(KohlsXMLUtil.getDocumentForElement((Element) outNodeList.item(i)));
			
			Document updateSafetyDoc = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_ELIGIBILITY);;
			Element updateEle=updateSafetyDoc.getDocumentElement();
			Element eleLineDetails = ((Element) outNodeList.item(i));
			updateEle.setAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY, eleLineDetails.getAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY));
			updateEle.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.P);
			Document processsedDocument=KohlsCommonUtil.invokeService(env, KohlsConstants.UPDATE_ITEMELG, updateSafetyDoc);
			kohlsLogger.debug((new StringBuilder("\n KohlsItemSafetyFactorOnItem :: updateDocforchangeExtnSafetyPercentage() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(processsedDocument)).toString());
			
			
		}
		
		return itemElgListDoc;

	}

	/*
	 * Input to this method <ItemEligibility Processed="N"/> which will be
	 * passed to list API of item eligibility custom table
	 */
	private Document getAllRecordsFromDB(YFSEnvironment env) throws Exception {

		Document inDoc = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_ELIGIBILITY);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: createDocforItemList() :: InputXML\n")).append(KohlsXMLUtil.getXMLString(inDoc)).toString());
		Element inEle = inDoc.getDocumentElement();
		inEle.setAttribute(KohlsConstants.PROCESSED, KohlsConstants.NO);

		Document outDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.get_ALL_ITEM_ELG_RECS, inDoc);
		kohlsLogger.debug((new StringBuilder("\n KohlsItemEligibilityBulkUpdate :: outDocforItemList() :: OutPutXML\n")).append(KohlsXMLUtil.getXMLString(outDoc)).toString());

		return outDoc;
	}

	/*
	 * Input to this method will be out put from the getItemList API. the method
	 * will setEligibleAs to ExtnShipNodeSource attribute. input to
	 * manageItemAPI <ItemList> <Item ItemKey=""> <Extn ExtnShipNodeSource="">
	 * </Item> <ItemList>
	 */
	private void manageItem(YFSEnvironment env, Document itemListOutDoc, String setEligibleAs,String extnStoreEligblKey) throws Exception {
		try{
			
		
		NodeList itemElementList = itemListOutDoc.getElementsByTagName(KohlsConstants.ITEM);
		
		NodeList itemExtnElementList = itemListOutDoc.getElementsByTagName(KohlsConstants.EXTN);
		
		double extnStoreEligblKeyint=Double.parseDouble(extnStoreEligblKey);
		
		int length = itemElementList.getLength();

		for (int i = 0; i < length; i++) {
			Element itemElement = (Element) itemElementList.item(i);
			Element itemExtnElement = (Element) itemExtnElementList.item(i);
			String kohlsEligibleBulkMarkReq=itemExtnElement.getAttribute(KohlsConstants.KOHLS_ELIGIBLE_BULK_MARK_REQ);
			double kohlsEligibleBulkMarkReqint=KOHLSStringUtil.isEmpty(kohlsEligibleBulkMarkReq) == true ? 0:  Double.parseDouble(kohlsEligibleBulkMarkReq);;
			
			Document inDocMangeItem = KohlsXMLUtil.createDocument(KohlsConstants.ITEM_LIST);
			Element inEleMangeItem = inDocMangeItem.getDocumentElement();
			Element inEleMangeItem1 = KohlsXMLUtil.createChild(inEleMangeItem, KohlsConstants.ITEM);
			String itemKey = itemElement.getAttribute(KohlsConstants.ITEM_KEY);
			inEleMangeItem1.setAttribute(KohlsConstants.ITEM_KEY, itemKey);
			Element inEleMangeItem2 = KohlsXMLUtil.createChild(inEleMangeItem1, KohlsConstants.EXTN);
			Element itemExtnEle = (Element) itemElement.getElementsByTagName(KohlsConstants.EXTN).item(0);
			if(kohlsEligibleBulkMarkReqint<extnStoreEligblKeyint)
			{
			if (!YFCCommon.isVoid(itemExtnEle)) {
				inEleMangeItem2.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, setEligibleAs);
			}
			inEleMangeItem2.setAttribute(KohlsConstants.KOHLS_ELIGIBLE_BULK_MARK_REQ, extnStoreEligblKey);
			KohlsCommonUtil.invokeAPI(env, KohlsConstants.MANAGE_ITEM, inDocMangeItem);
			
		}
			else 
				continue;
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			Document inDoc=KohlsCommonUtil.emailMethod(env);
			KohlsCommonUtil.invokeService(env, KohlsConstants.EMAIL_NOTIFY_ELIGIBILITY, inDoc);
			throw e;
		}
	}

}
