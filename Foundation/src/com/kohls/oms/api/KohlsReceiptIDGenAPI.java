package com.kohls.oms.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.inventory.api.KohlsDSVInventoryAPI;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is used to generate the ReceiptID
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsReceiptIDGenAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsDSVInventoryAPI.class.getName());
	private Connection m_conn;
	// SQL for register and transaction number in gift receipt number
	public static final String SQL_ORACLE_REG_TRANS_SEQ_NO = "SELECT SEQ_REG_TRANS_NO.NEXTVAL from DUAL";
	public static final String  SQL_ORACLE_REG_TRANS_SEQ_NO_STORE = "SELECT SEQ_REG_TRANS_NO_STORE.NEXTVAL from DUAL";

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsReceiptIDGenAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	/**
	 * Method to check and generate the receipt id
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void generateReceiptID(YFSEnvironment env, Document inXML)
	throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsReceiptIDGenAPI generateReceiptID method -- >"
					+ XMLUtil.getXMLString(inXML));
		}

		log.debug("Inside generateReceiptID method");
		String strExtnHdrGiftRcptId = "";
		String strOrderHdKey = "";
		String strShipNode = "";
		String strReasonCode = "";
		String sOrderDate = "";
		String rootName = "";
		String sNodeType = "";
		boolean noteVal = true;
		String sDeliveryMethod = "";

		Element eleInputList = inXML.getDocumentElement();
		rootName = eleInputList.getNodeName();
		strOrderHdKey = eleInputList
		.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		strShipNode = eleInputList.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
		
		sNodeType = KohlsUtil.getNodeType(strShipNode, env);
		if (rootName.equalsIgnoreCase(KohlsXMLLiterals.E_ORDER)) {
			strShipNode = KohlsConstant.DSV_NODE_RECEIPT_ID;
		}
		if (strShipNode.equalsIgnoreCase(KohlsConstant.VGC_SHIP_NODE)) {
			strShipNode = KohlsConstant.DSV_NODE_RECEIPT_ID;
		}
		if(KohlsConstant.ATTR_STORE.equals(sNodeType)){
			//fixed for drop2 : settlement -Start
			//strShipNode = KohlsUtil.getEFCForStore(strShipNode, env);
			//fixed for drop2 : settlement -Start
		}
		if(KohlsConstant.ATTR_RDC.equals(sNodeType)){
			strShipNode = KohlsUtil.getEFCForStore(strShipNode, env);
		}
		sOrderDate = eleInputList.getAttribute(KohlsXMLLiterals.A_ORDER_DATE);
		sDeliveryMethod = eleInputList.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
		
		NodeList nodeExtn = eleInputList
		.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
		if (nodeExtn.getLength() != 0) {
			Element eleExtn = (Element) nodeExtn.item(0);
			strExtnHdrGiftRcptId = eleExtn.getAttribute(KohlsXMLLiterals.A_EXTN_HDR_GFT_RECP_ID);
			if (strExtnHdrGiftRcptId.trim().equals("")) {
				YFCDocument yfcDocOrder = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleOrder = yfcDocOrder.getDocumentElement();
				yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY,
						strOrderHdKey);

				env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
						this.getOrderDetailsTemplate());
				Document docGetOrderDtlOutputXML = api.getOrderDetails(env,
						yfcDocOrder.getDocument());
				env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("Output for getOrderDetails : \n"
							+ XMLUtil.getXMLString(docGetOrderDtlOutputXML));
				}
				NodeList nodegetOrderDtls = docGetOrderDtlOutputXML
				.getElementsByTagName(KohlsXMLLiterals.E_NOTES);

				if (nodegetOrderDtls.getLength() > 0) {
					Element eleOutNotes = (Element) nodegetOrderDtls.item(0);
					String strNumOfNotes = eleOutNotes
					.getAttribute(KohlsXMLLiterals.A_NUMBER_OF_NOTES);
					int numNotes = Integer.parseInt(strNumOfNotes);
					if (numNotes > 0) {						
						NodeList ndOutNote = eleOutNotes
						.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
						Element eleOutNote;
						List rcList = new ArrayList<String>();
						for(int j=0;j<ndOutNote.getLength();j++){
							eleOutNote = (Element)ndOutNote.item(j);
							strReasonCode = eleOutNote
							.getAttribute(KohlsXMLLiterals.A_REASON_CODE);
							rcList.add(strReasonCode);
						}
						
						if (rcList.contains(KohlsConstant.BPS+"_"+strShipNode) && (KohlsConstant.PICK.equals(sDeliveryMethod))) {
							log.debug("rclist contains BPS_" + strShipNode + ", hence setting noteval to false");
							noteVal = false;
						} else if (rcList.contains(strShipNode) && (!KohlsConstant.PICK.equals(sDeliveryMethod))) {
							log.debug("rclist contains strShipNode or  hence setting noteval to false");
							noteVal = false;
						}
					}
					if (numNotes == 0 || noteVal) {
						// generate the receipt id
						//fixed for drop2 : settlement -Start
						String sReceiptIDValues = getRegTransValues(env, sNodeType);
						//fixed for drop2 : settlement - End
						/*******Added to avoid sending TranID ending with 0000 to COSA**********/
						int iReceiptIDValues = Integer.parseInt(sReceiptIDValues);
						int modRcptID = (iReceiptIDValues % 10000);
						if(0==modRcptID){
							//fixed for drop2 : settlement -Start
							sReceiptIDValues = getRegTransValues(env, sNodeType);
							//fixed for drop2 : settlement - End
						}
						/******** END ***********/
						
						String sShipNodeForRcptId = StringUtil
						.prepadStringWithZeros(strShipNode,
								KohlsConstant.SHIPNODE_LEN);

						String sFinalOrdDate = sOrderDate.substring(5, 7)
						+ sOrderDate.substring(8, 10)
						+ sOrderDate.substring(2, 4)
						+ sOrderDate.substring(11, 19).replace(":", "");
						String sExtnPreEncodedHdrReceiptID = KohlsConstant.RCPT_HDR_LINE_NUM
						+ sFinalOrdDate
						+ sShipNodeForRcptId
						+ sReceiptIDValues;
						String strExtnHdrGiftReceiptID = getFinalReceiptIDValue(sExtnPreEncodedHdrReceiptID);
						// create the input xml for changeOrder api
						YFCDocument yfcDocChOrder = YFCDocument
						.createDocument(KohlsXMLLiterals.E_ORDER);
						YFCElement yfcEleChOrder = yfcDocChOrder
						.getDocumentElement();
						yfcEleChOrder.setAttribute(
								KohlsXMLLiterals.A_ORDER_HEADER_KEY,
								strOrderHdKey);
						yfcEleChOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE,
								KohlsConstant.YES);

						YFCElement yfcEleNotes = yfcDocChOrder
						.createElement(KohlsXMLLiterals.E_NOTES);
						yfcEleChOrder.appendChild(yfcEleNotes);

						YFCElement yfcEleNote1 = yfcDocChOrder
						.createElement(KohlsXMLLiterals.E_NOTE);
						YFCElement yfcEleNote2 = yfcDocChOrder
						.createElement(KohlsXMLLiterals.E_NOTE);
						
						//Checking for BOPUS line
						if(KohlsConstant.PICK.equals(sDeliveryMethod))
						{
							yfcEleNote1.setAttribute(
									KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.BPS+"_"+strShipNode);
							yfcEleNote2.setAttribute(
									KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.PREENC+ KohlsConstant.BPS+"_"+strShipNode);
						}else{ 
							//non-BOPUS line
							yfcEleNote1.setAttribute(
									KohlsXMLLiterals.A_REASON_CODE, strShipNode);
							yfcEleNote2.setAttribute(
									KohlsXMLLiterals.A_REASON_CODE, KohlsConstant.PREENC+strShipNode);
						}

						yfcEleNote1.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT,
								strExtnHdrGiftReceiptID);
						yfcEleNote2.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT,
								sExtnPreEncodedHdrReceiptID);

						//Changes added by Naveen. Changes start here. This attribute required for release information
						env.setTxnObject(KohlsXMLLiterals.A_EXTN_PREENC_HDR_RECP_ID+strOrderHdKey, sExtnPreEncodedHdrReceiptID);
						//Changes added by Naveen. Changes end here

						yfcEleNotes.appendChild(yfcEleNote1);
						yfcEleNotes.appendChild(yfcEleNote2);

						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("Input xml to changeOrder api \n:"
									+ XMLUtil.getXMLString(yfcDocChOrder
											.getDocument()));
						}
						api.changeOrder(env, yfcDocChOrder.getDocument());
					}
				}
			}
		}
	}

	//sNodeType arrgument add for  drop2 settlement fix
	private String getRegTransValues(YFSEnvironment env,String sNodeType) throws SQLException {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("############## Getting Next Register,Transaction No ##################");
		}
		
		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String sRegisterTransSeqNo = null;
		
		YFSConnectionHolder connHolder = (YFSConnectionHolder) env;
		m_conn= connHolder.getDBConnection();
		
		try{
			//String sql = SQL_ORACLE_REG_TRANS_SEQ_NO;
			
			String sql = null;
			if(KohlsConstant.ATTR_STORE.equals(sNodeType)){
				sql = SQL_ORACLE_REG_TRANS_SEQ_NO_STORE;
			}else {
			 sql = SQL_ORACLE_REG_TRANS_SEQ_NO;
			}
			stmt = m_conn.prepareStatement(sql);
			rSet = stmt.executeQuery();
			if (rSet.next()) {
				sRegisterTransSeqNo = rSet.getString(1);
			}

			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("The sequential Register Transaction Number : " + sRegisterTransSeqNo);
			}
		}catch(SQLException SqlEx){

			SqlEx.printStackTrace();
		}finally{
			if(rSet!=null){
				rSet.close();
			}
			if(stmt!=null){
				stmt.close();
			}
		}

		return sRegisterTransSeqNo;
	}

	public String getFinalReceiptIDValue(String PreEncodedRcptId) {
		String RcptId = "";
		try {
			RcptId = PreEncodedRcptId;
			// Calculate the Mod 10 Check Digit Value
			int CheckDigit = KohlsUtil.computeMod10CheckDigit(RcptId);

			//System.out.println("ChkDigit " + CheckDigit);

			// Append Check Digit to the Receipt ID
			String RcpId = RcptId + CheckDigit;

			//System.out.println("Rpt Id" + RcpId);
			// Compute 10's Compliment
			RcptId = KohlsUtil.computeTensComplement(RcpId);

			//System.out.println("Receipt ID " + RcptId);

			// Obtain the Check Digit for the transformed Receipt ID
			CheckDigit = KohlsUtil.computeMod10CheckDigit(RcptId);

			//System.out.println("2nd ChkDigit " + CheckDigit);

			// Append Check Digit to the Receipt ID
			RcptId = RcptId + CheckDigit;

			// Format Receipt ID Value with -
			RcptId = KohlsUtil.formatReceiptID(RcptId);

			//System.out.println("Formatted Rpt ID: " + RcptId);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return RcptId;
	}

	/**
	 * Template for getDistributionList
	 */
	private Document getOrderDetailsTemplate() {

		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrder = yfcDocOrder.getDocumentElement();
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");

		YFCElement yfcEleNotes = yfcDocOrder
		.createElement(KohlsXMLLiterals.E_NOTES);
		yfcEleOrder.appendChild(yfcEleNotes);

		YFCElement yfcEleNote = yfcDocOrder
		.createElement(KohlsXMLLiterals.E_NOTE);
		yfcEleNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcEleNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");

		yfcEleNotes.appendChild(yfcEleNote);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug(" Output Template of getOrderDetails \n"
					+ XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}
		return yfcDocOrder.getDocument();
	}

	/**
	 * @param arg0
	 *            Properties
	 * 
	 * @throws Exception
	 *             e
	 * 
	 */
	public void setProperties(Properties arg0) throws Exception {

	}
}
