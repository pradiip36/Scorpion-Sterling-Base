package com.kohls.inventory.agent;


import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.log.Logger;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDateUtils;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This is Time Triggered Agent which will call getInventorySnapShot API to
 * fetch all Shipnodes Inventory based on certain criterion configured in the
 * Transaction. It will the populate this output XML into the external EDW feed
 * for the external System to pick up.
 * 
 * 
 * @author Vijay Kumar
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * 
 *         The input XML to the implemented getJobs method will be in the format
 *         : <MessageXml Action="Get" DocumentParamsKey="" DocumentType=""
 *         ProcessType="" ProcessTypeKey="" TransactionId="" TransactionKey=""
 *         CustomParameter1="A" CustomParameter2="B" NumRecordsToBuffer="5000" >
 *         </MessageXml> Organization Code will be set as one of the Criterion
 *         parameters .
 **/

public class KohlsSendInventorySnapShotAgent extends YCPBaseAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendInventorySnapShotAgent.class.getName());
	private YIFApi api;
	private Connection m_conn;

	/** Constructor Method * */

	public KohlsSendInventorySnapShotAgent() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	/* Override the getJobs method from YFSBaseAgent Interface */

	public List getJobs(YFSEnvironment env, Document inXML, Document lastMessage)
			throws Exception {

		ArrayList InvSnapShot = new ArrayList();
		YFCDocument yfcDocGetInvSnap = null;
		YFCElement yfcEleGetInvSnap;
		YFCDocument yfcDocGetDistLst;
		YFCElement yfcEleGetDistLst;
		String strShipNode="";

		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("Input XML to getJobs new method in KohlsSendInventorySnapShotAgent start: "
					+ XMLUtil.getXMLString(inXML));
		}

		Element eleRoot = inXML.getDocumentElement();
		String sOrganizationCode = eleRoot
				.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE);
		String strMaxRec = eleRoot.getAttribute(KohlsXMLLiterals.A_MAX_RECORDS);
		String strDistGroup = eleRoot.getAttribute(KohlsConstant.DIST_GROUP);

		if (null != lastMessage) {
			return null;
		}

		// create getDistributionList input xml
		yfcDocGetDistLst = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
		yfcEleGetDistLst = yfcDocGetDistLst.getDocumentElement();
		yfcEleGetDistLst.setAttribute(KohlsXMLLiterals.A_OWNER_KEY,
				KohlsConstant.ITEM_ORGANIZATION_CODE);
		yfcEleGetDistLst.setAttribute(KohlsXMLLiterals.A_DISTRIBUTION_RULE_ID,
				strDistGroup);

		env.setApiTemplate(KohlsConstant.API_GET_DISTRIBUTION_LIST,
				this.getDistributionListTemplate());
		Document docGetDistLstOutputXML = api.getDistributionList(env,
				yfcDocGetDistLst.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_DISTRIBUTION_LIST);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Output for getDistributionList : \n"
					+ XMLUtil.getXMLString(docGetDistLstOutputXML));
		}
		
		NodeList nodegetDistList = docGetDistLstOutputXML
			.getElementsByTagName(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
		
		for (int i = 0; i < nodegetDistList.getLength(); i++) {

			Element eleCommonCode = (Element) nodegetDistList.item(i);
			strShipNode = eleCommonCode
					.getAttribute(KohlsXMLLiterals.A_SHIPNODE_KEY);

			yfcDocGetInvSnap = YFCDocument
					.createDocument(KohlsXMLLiterals.E_GET_INV_SNAP_SHOT);
			yfcEleGetInvSnap = yfcDocGetInvSnap.getDocumentElement();
			yfcEleGetInvSnap.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,
					sOrganizationCode);
			yfcEleGetInvSnap.setAttribute(KohlsXMLLiterals.A_SHIPNODE,
					strShipNode);
			yfcEleGetInvSnap.setAttribute(KohlsXMLLiterals.A_MAX_NO_OF_ITEMS,
					strMaxRec);

			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("getInventorySnapShot Input XML::"
						+ XMLUtil.getXMLString(yfcDocGetInvSnap.getDocument()));
			}
			InvSnapShot.add(yfcDocGetInvSnap.getDocument());
		}
		
		return InvSnapShot;
	}

	/* overriding execute job method from YCPBaseAgent Interface */

	public void executeJob(YFSEnvironment env, Document inInvSnapShot)
			throws Exception {
		
		m_conn = KohlsUtil.getDBConnection(env);
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("Input XML executeJob start: "
					+ XMLUtil.getXMLString(inInvSnapShot));
		}

		YFCDateUtils dt;
		YFCDate yfcCurrntDt;
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.DATE_FORMAT);
		String str;
		String strInvItemKey = "0";
		int iCount = 1;

		Document outFsgetInventorySnapShot = null;
		env.setApiTemplate(KohlsConstant.API_GET_INVENTORY_SNAP_SHOT, getInventorySnapShotTemplate());
		
		dt = new YFCDateUtils();
		yfcCurrntDt = YFCDateUtils.getCurrentDate(true);
		str = sdf.format(yfcCurrntDt);

		// paginate based on the max records set
		while (null != strInvItemKey && !strInvItemKey.equalsIgnoreCase("")) {
			
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("getInventorySnapShot Input XML:: "
						+ XMLUtil.getXMLString(inInvSnapShot));
			}	

		outFsgetInventorySnapShot = this.api.getInventorySnapShot(env, inInvSnapShot);
		Element eleFsOutInvSnap = outFsgetInventorySnapShot.getDocumentElement();		
		strInvItemKey = ((Element)eleFsOutInvSnap.getElementsByTagName(KohlsXMLLiterals.E_SHIP_NODE).item(0)).getAttribute(KohlsXMLLiterals.A_LAST_INV_ITEM_KEY);
		
		inInvSnapShot.getDocumentElement().setAttribute(KohlsXMLLiterals.A_LAST_INV_ITEM_KEY, strInvItemKey);
				
		eleFsOutInvSnap.setAttribute(KohlsXMLLiterals.A_SNAP_SHOT_TIME, str);
		eleFsOutInvSnap.setAttribute("MessageCount", String.valueOf(iCount));
		
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("getInventorySnapShot Output XML:: "
					+ XMLUtil.getXMLString(outFsgetInventorySnapShot));
		}	
		
		if(!strInvItemKey.equalsIgnoreCase("")){
		this.api.executeFlow(env,
				KohlsConstant.SERVICE_KOHLS_GET_INV_SNAP_SHOT,
					outFsgetInventorySnapShot); 
		}
		
		m_conn.commit();		
		iCount++;
		
		}
		
		env.clearApiTemplate(KohlsConstant.API_GET_INVENTORY_SNAP_SHOT);
	}

	/**
	 * Template for getDistributionList
	 */
	private Document getDistributionListTemplate() {

		YFCDocument yfcDocDistList = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM_SHIP_NODE_LIST);
		YFCElement yfcEleDistList = yfcDocDistList.getDocumentElement();

		YFCElement yfcEleItemShip = yfcDocDistList
				.createElement(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
		yfcEleItemShip.setAttribute(KohlsXMLLiterals.A_SHIPNODE_KEY, "");
		yfcEleDistList.appendChild(yfcEleItemShip);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getDistributionList Output Template \n"
					+ XMLUtil.getXMLString(yfcDocDistList.getDocument()));
		}

		return yfcDocDistList.getDocument();
	}
	
	
	/** getInventorySnapSot Template**/
	
	public Document getInventorySnapShotTemplate(){
		
		YFCDocument yfcDocGetInventorySnapShot = YFCDocument.createDocument(KohlsXMLLiterals.E_INVENTORY_SNAP_SHOT);
		YFCElement yfcEleGetInventorySnapShot = yfcDocGetInventorySnapShot.getDocumentElement();
		
		YFCElement yfcEleShipNode = yfcEleGetInventorySnapShot.createChild(KohlsXMLLiterals.E_SHIP_NODE);
		yfcEleShipNode.setAttribute(KohlsXMLLiterals.A_LAST_INV_ITEM_KEY, "");
		yfcEleShipNode.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		
		//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/19/2012
		yfcEleShipNode.setAttribute(KohlsXMLLiterals.A_COMPLETE_INV_FALG, "");
		//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/19/2012
		
		
		
		YFCElement yfcEleItem = yfcEleShipNode.createChild(KohlsXMLLiterals.E_ITEM);
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_PRODUCT_CLASS, "");		
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_UOM, "");
		
		//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_INV_ITEM_KEY, "");
		yfcEleItem.setAttribute(KohlsXMLLiterals.A_INV_ORG_CODE, "");
		//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		
		//Start --- Commented for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		//yfcEleItem.setAttribute(KohlsXMLLiterals.A_DESCRIPTION, "");
		//yfcEleItem.setAttribute(KohlsXMLLiterals.A_SHORT_DESCRIPTION, "");
		//End --- Commented for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		
		YFCElement yfcEleSupplyDetails = yfcEleItem.createChild(KohlsXMLLiterals.E_SUPPLY_DETAILS);
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SUPPLY_TYPE, "");
		//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_TRACK, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_ETA, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SEGMENT, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SEGMENT_TYPE, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SHIP_BY_DATE, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SUPPLY_REFERENCE, "");
		yfcEleSupplyDetails.setAttribute(KohlsXMLLiterals.A_SUPPLY_REFERENCE_TYPE, "");
		//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		
		
		
		YFCElement yfcEleDemandDetails = yfcEleItem.createChild(KohlsXMLLiterals.E_DEMAND_DETAILS);
		
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_DEMAND_TYPE, "");
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_QUANTITY, "");
		//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_SEGMENT, "");
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_SEGMENT_TYPE, "");
		yfcEleDemandDetails.setAttribute(KohlsXMLLiterals.A_DEMAND_SHIP_DATE, "");
		//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
		
		
		
		if(YFCLogUtil.isDebugEnabled()){
			
			log.debug("getInventorySnapShot Template : " + XMLUtil.getXMLString(yfcDocGetInventorySnapShot.getDocument()));
		}
		
		return yfcDocGetInventorySnapShot.getDocument();
	}

}
