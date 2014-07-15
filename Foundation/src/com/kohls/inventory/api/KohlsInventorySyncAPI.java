package com.kohls.inventory.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
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
 * This class is called on receiving the Inventory Sync messages for items
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsInventorySyncAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInventorySyncAPI.class.getName());

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsInventorySyncAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	/**
	 * Method to get all the integration servers and suspend server on receiving
	 * SOF based on the node
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void getSuspendServer(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsInventorySyncAPI getSuspendServer method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strServerId = null;
		String strServerName = null;
		String strShipNode = null;
		YFCElement yfcEleServer;
		String servName = "";
		YFCDocument yfcDocServer;
		Element eleInputList = inXML.getDocumentElement();
		NamedNodeMap ndInlstGet = eleInputList.getAttributes();

		Node ndInData = ndInlstGet.getNamedItem(KohlsXMLLiterals.E_SHIP_NODE);
		strShipNode = ndInData.getNodeValue();

		YFCDocument yfcDocGetServer = YFCDocument
				.createDocument(KohlsXMLLiterals.E_SERVER);
		YFCElement yfcEleGetServer = yfcDocGetServer.getDocumentElement();
		yfcEleGetServer.setAttribute(KohlsXMLLiterals.A_TYPE,
				KohlsConstant.INTEGRATION_AGENT_SERVER);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getServerList Input XML : "
					+ XMLUtil.getXMLString(yfcDocGetServer.getDocument()));
		}
		// gets all the available integration servers
		Document docServerList = this.api.getServerList(env,
				yfcDocGetServer.getDocument());
		Element eleOutServList = docServerList.getDocumentElement();
		NodeList nodeServLst = eleOutServList
				.getElementsByTagName(KohlsXMLLiterals.E_SERVER);

		// the server name is got from common codes, which will take care of any
		// new nodes added
		servName = KohlsUtil.getCommonCodeList(env,
				KohlsConstant.INV_ADJ_SERVER_CODE_TYPE, strShipNode);

		if (0 != nodeServLst.getLength()) {
			for (int i = 0; i < nodeServLst.getLength(); i++) {
				Element eleData = (Element) nodeServLst.item(i);
				strServerName = eleData.getAttribute(KohlsXMLLiterals.A_NAME);
				if (strServerName.equalsIgnoreCase(servName)) {
					strServerId = eleData.getAttribute(KohlsXMLLiterals.A_ID);
					yfcDocServer = YFCDocument
							.createDocument(KohlsXMLLiterals.E_SERVER);
					yfcEleServer = yfcDocServer.getDocumentElement();
					yfcEleServer.setAttribute(KohlsXMLLiterals.A_ACTION,
							KohlsConstant.SUSPEND);
					yfcEleServer.setAttribute(KohlsXMLLiterals.A_ID,
							strServerId);
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("modifyServer Input XML : "
								+ XMLUtil.getXMLString(yfcDocServer
										.getDocument()));
					}
					this.api.modifyServer(env, yfcDocServer.getDocument());
				}
			}
		}
	}

	/**
	 * Method to write data in the Kohls custom table on receiving the EOF
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void createInvSyncTimeStamp(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsInventorySyncAPI createInvSyncTimeStamp method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strShipNode = null;
		String strInvSyncKey = "";
		Element eleInputList = inXML.getDocumentElement();
		NamedNodeMap nodeGetList = eleInputList.getAttributes();

		Node nodeGetData = nodeGetList
				.getNamedItem(KohlsXMLLiterals.A_SHIPNODE);
		strShipNode = nodeGetData.getNodeValue();

		YFCDocument yfcDocKohlsInvSyncTime = YFCDocument
				.createDocument(KohlsXMLLiterals.E_KOHLS_INV_SYNC_TIME_STAMP);
		YFCElement yfcEleKohlsInvSyncTime = yfcDocKohlsInvSyncTime
				.getDocumentElement();
		yfcEleKohlsInvSyncTime.setAttribute(KohlsXMLLiterals.A_SHIPNODE,
				strShipNode);

		Document docGetSyncTimeData = this.api.executeFlow(env,
				KohlsConstant.SERVICE_KOHLS_GET_INV_SYNC_TIME,
				yfcDocKohlsInvSyncTime.getDocument());

		// Assuming that a single record exists for every node
		NodeList nodelistGetSyncTime = docGetSyncTimeData
				.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_INV_SYNC_TIME_STAMP);
		// check to see if record for node exists
		if (0 == nodelistGetSyncTime.getLength()) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("modifyServer Input XML : "
						+ XMLUtil.getXMLString(inXML));
			}
			this.api.executeFlow(env,
					KohlsConstant.SERVICE_KOHLS_CREATE_INV_SYNC_TIME, inXML);
		} else {
			Element eleDataSyncTime = (Element) nodelistGetSyncTime.item(0);
			strInvSyncKey = eleDataSyncTime
					.getAttribute(KohlsXMLLiterals.A_INV_SYNC_TIME_KEY);
			eleInputList.setAttribute(KohlsXMLLiterals.A_INV_SYNC_TIME_KEY,
					strInvSyncKey);
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("modifyServer Input XML : "
						+ XMLUtil.getXMLString(inXML));
			}
			this.api.executeFlow(env,
					KohlsConstant.SERVICE_KOHLS_CHANGE_INV_SYNC_TIME, inXML);
		}

	}

	/**
	 * Method to get all the integration servers and resume server on receiving
	 * EOF based on the node
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void getResumeServer(YFSEnvironment env, Document inXML)
			throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsInventorySyncAPI getResumeServer method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		String strServerId = null;
		String strServerName = null;
		String strShipNode = null;
		YFCElement yfcEleServer;
		String servName = "";
		YFCDocument yfcDocServer;
		Element eleInputList = inXML.getDocumentElement();
		NamedNodeMap ndInlstGet = eleInputList.getAttributes();

		Node ndInData = ndInlstGet.getNamedItem(KohlsXMLLiterals.E_SHIP_NODE);
		strShipNode = ndInData.getNodeValue();

		YFCDocument yfcDocGetServer = YFCDocument
				.createDocument(KohlsXMLLiterals.E_SERVER);
		YFCElement yfcEleGetServer = yfcDocGetServer.getDocumentElement();
		yfcEleGetServer.setAttribute(KohlsXMLLiterals.A_TYPE,
				KohlsConstant.INTEGRATION_AGENT_SERVER);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getServerList Input XML : "
					+ XMLUtil.getXMLString(yfcDocGetServer.getDocument()));
		}
		// gets all the available integration servers
		Document docServerList = this.api.getServerList(env,
				yfcDocGetServer.getDocument());
		Element eleOutServList = docServerList.getDocumentElement();
		NodeList nodeServLst = eleOutServList
				.getElementsByTagName(KohlsXMLLiterals.E_SERVER);

		// the server name is got from common codes, which will take care of any
		// new nodes added
		servName = KohlsUtil.getCommonCodeList(env,
				KohlsConstant.INV_ADJ_SERVER_CODE_TYPE, strShipNode);

		if (0 != nodeServLst.getLength()) {
			for (int i = 0; i < nodeServLst.getLength(); i++) {
				Element eleData = (Element) nodeServLst.item(i);
				strServerName = eleData.getAttribute(KohlsXMLLiterals.A_NAME);
				if (strServerName.equalsIgnoreCase(servName)) {
					strServerId = eleData.getAttribute(KohlsXMLLiterals.A_ID);
					yfcDocServer = YFCDocument
							.createDocument(KohlsXMLLiterals.E_SERVER);
					yfcEleServer = yfcDocServer.getDocumentElement();
					yfcEleServer.setAttribute(KohlsXMLLiterals.A_ACTION,
							KohlsConstant.RESUME);
					yfcEleServer.setAttribute(KohlsXMLLiterals.A_ID,
							strServerId);
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("modifyServer Input XML : "
								+ XMLUtil.getXMLString(yfcDocServer
										.getDocument()));
					}
					this.api.modifyServer(env, yfcDocServer.getDocument());
				}
			}
		}
	}

	/**
	 * @param arg0
	 *            Properties
	 * @throws Exception
	 *             e
	 */
	public void setProperties(Properties arg0) throws Exception {

	}
}
