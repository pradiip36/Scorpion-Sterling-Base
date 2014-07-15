package com.kohls.inventory.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is used to create Distribution for DSV Inventory
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsDSVInventoryAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsDSVInventoryAPI.class.getName());
	String strDistGroup = "";

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsDSVInventoryAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	/**
	 * Method to check and create distribution
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void createDistribution(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsDSVInventoryAPI createDistribution method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		Element eleGetNode;
		String strShipNode = "";
		String strOperation = "";
		YFCDocument yfcDocGetDistLst;
		YFCElement yfcEleGetDistLst;

		Element eleInputList = inXML.getDocumentElement();
		strOperation = eleInputList
				.getAttribute(KohlsXMLLiterals.A_VENDOR_OPERATION);
		NodeList nodeGetNode = eleInputList
			.getElementsByTagName(KohlsXMLLiterals.A_NODE);
		if (nodeGetNode.getLength() != 0) {
			eleGetNode = (Element) nodeGetNode.item(0);
			strShipNode = eleGetNode.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		}
		// check the operation in vendor input xml
		if (strOperation.equalsIgnoreCase(KohlsConstant.DELETE_DIST_GROUP)) {
			// deleteDistribution input xml
			YFCDocument yfcDocDeleteDist = YFCDocument
					.createDocument(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
			YFCElement yfcEleDeleteDist = yfcDocDeleteDist.getDocumentElement();
			yfcEleDeleteDist.setAttribute(KohlsXMLLiterals.A_OWNER_KEY,
					KohlsConstant.ITEM_ORGANIZATION_CODE);
			yfcEleDeleteDist.setAttribute(KohlsXMLLiterals.A_SHIPNODE_KEY,
					strShipNode);
			yfcEleDeleteDist.setAttribute(KohlsXMLLiterals.A_ITEMID,
					KohlsConstant.ALL_ITEM_ID);
			yfcEleDeleteDist.setAttribute(
					KohlsXMLLiterals.A_DISTRIBUTION_RULE_ID, strDistGroup);
			// call deleteDistribution api
			api.deleteDistribution(env, yfcDocDeleteDist.getDocument());
		} else {
		// create getDistributionList input xml
		yfcDocGetDistLst = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
		yfcEleGetDistLst = yfcDocGetDistLst.getDocumentElement();
		yfcEleGetDistLst.setAttribute(KohlsXMLLiterals.A_OWNER_KEY,
				KohlsConstant.ITEM_ORGANIZATION_CODE);
		yfcEleGetDistLst.setAttribute(KohlsXMLLiterals.A_SHIPNODE_KEY,
				strShipNode);
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

		// if no records invoke createDistribution api
		if (nodegetDistList.getLength() == 0) {

			YFCDocument yfcDocCreateDist = YFCDocument
					.createDocument(KohlsXMLLiterals.E_ITEM_SHIP_NODE);
			YFCElement yfcEleCreateDist = yfcDocCreateDist.getDocumentElement();
			yfcEleCreateDist.setAttribute(KohlsXMLLiterals.A_OWNER_KEY,
					KohlsConstant.ITEM_ORGANIZATION_CODE);
			yfcEleCreateDist.setAttribute(KohlsXMLLiterals.A_SHIPNODE_KEY,
					strShipNode);
			yfcEleCreateDist.setAttribute(
					KohlsXMLLiterals.A_DISTRIBUTION_RULE_ID, strDistGroup);
			yfcEleCreateDist.setAttribute(KohlsXMLLiterals.A_ITEMID,
					KohlsConstant.ALL_ITEM_ID);
			yfcEleCreateDist.setAttribute(KohlsXMLLiterals.A_PRIORITY,
					KohlsConstant.DSV_PRIORITY);
			yfcEleCreateDist.setAttribute(KohlsXMLLiterals.A_ACTIVITY_FLAG,
					KohlsConstant.YES);

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML for createDistribution : \n"
						+ XMLUtil.getXMLString(yfcDocCreateDist.getDocument()));
			}
			api.createDistribution(env, yfcDocCreateDist.getDocument());
		}
		}
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

	/**
	 * set the properties to instance variables
	 * 
	 * @param arg0
	 *            Properties
	 * 
	 * @throws Exception
	 *             e
	 * 
	 */
	public void setProperties(Properties arg0) throws Exception {

		this.strDistGroup = arg0.getProperty(KohlsConstant.DIST_GROUP);
	}
}
