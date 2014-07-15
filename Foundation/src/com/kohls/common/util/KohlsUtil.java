package com.kohls.common.util;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.custom.util.xml.XMLUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDateUtils;
import com.yantra.yfs.japi.YFSConnectionHolder;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class Encapsulated all the Utility methods used in Kohls Solution //
 */
public class KohlsUtil {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsUtil.class.getName());
	private YIFApi api;
	public static final String SQL_ORACLE_PICKTICKET_SEQ_NO = "SELECT SEQ_PICK_TICKET.NEXTVAL from DUAL";

	// SQL for register and transaction number in gift receipt number
	public static final String SQL_ORACLE_REG_TRANS_SEQ_NO = "SELECT SEQ_REG_TRANS_NO.NEXTVAL from DUAL";
	//End of possible change


	public KohlsUtil() throws YIFClientCreationException{
		 
			api = YIFClientFactory.getInstance().getLocalApi();
		 
	}

	/**
	 * This methods is used in TaskQBased Custom Agent to remove the task present 
	 * in Task Q table after successful completion of the task 
	 * @throws YIFClientCreationException */
	public static void registerProcessCompletion(YFSEnvironment env, String sDataKey, String sDataType, String sTaskQKey) 
	throws YFSException, RemoteException, YIFClientCreationException {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("########## Inside registerProcessCompletion Method ################");
		}
		YFCDocument yfcDocRegisterTaskCompletion = YFCDocument.createDocument(KohlsXMLLiterals.E_REGISTER_PROCESS_COMPLETION_INPUT);
		YFCElement yfcEleRegisterTaskCompletion = yfcDocRegisterTaskCompletion.getDocumentElement();

		yfcEleRegisterTaskCompletion.setAttribute(KohlsXMLLiterals.A_KEEP_TASK_OPEN, KohlsConstant.NO);

		YFCElement yfcEleCurrentTask = yfcDocRegisterTaskCompletion.createElement(KohlsXMLLiterals.E_CURRENT_TASK);
		yfcEleCurrentTask.setAttribute(KohlsXMLLiterals.A_DATA_KEY, sDataKey);
		yfcEleCurrentTask.setAttribute(KohlsXMLLiterals.A_DATA_TYPE, sDataType);
		yfcEleCurrentTask.setAttribute(KohlsXMLLiterals.A_TASK_Q_KEY, sTaskQKey);
		yfcEleRegisterTaskCompletion.appendChild(yfcEleCurrentTask);

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input XML for registerProcessCompletion : " + XMLUtil.getXMLString(yfcDocRegisterTaskCompletion.getDocument()));
		}

		new KohlsUtil().api.registerProcessCompletion(env, yfcDocRegisterTaskCompletion.getDocument());
	}

	/**
	 * This methods is used get the DBConnection from YFSEnvironment 
	 */
	public static Connection getDBConnection(YFSEnvironment env) {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("########### Getting DB Connection From YFCEnvironment ##############");
		}

		YFSConnectionHolder connHolder = (YFSConnectionHolder) env;
		return connHolder.getDBConnection();
	}

	/**
	 * This methods is used in KohlsSendToWMoSAgent to generate the PickTicketNo 
	 */
	public static String getNextSeqNo(YFSEnvironment env, Connection m_conn) throws SQLException {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("############## Getting Next Sequence No ##################");
		}

		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String sPickTicketSeqNo = null;

		stmt = m_conn.prepareStatement(SQL_ORACLE_PICKTICKET_SEQ_NO);
		rSet = stmt.executeQuery();

		if (rSet.next()) {
			sPickTicketSeqNo = rSet.getString(1);
		}
		
		if (rSet != null)
			rSet.close();
		if (stmt != null)
			stmt.close();
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("The pickTicket Number : " + sPickTicketSeqNo);
		}

		return sPickTicketSeqNo;
	}

	/**
	 * This method is used in KohlsSendToWMoSAgent to generate the Register & Transaction No for Gift Receipt ID 
	 */
	public static String getNextRegTransNo(YFSEnvironment env, Connection m_conn) throws SQLException {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("############## Getting Next Register,Transaction No ##################");
		}

		PreparedStatement stmt = null;
		ResultSet rSet = null;
		String sRegisterTransSeqNo = null;

		stmt = m_conn.prepareStatement(SQL_ORACLE_REG_TRANS_SEQ_NO);
		rSet = stmt.executeQuery();

		if (rSet.next()) {
			sRegisterTransSeqNo = rSet.getString(1);
		}
		
		if (rSet != null)
			rSet.close();
		if (stmt != null)
			stmt.close();
		
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("The sequential Register Transaction Number : " + sRegisterTransSeqNo);
		}

		return sRegisterTransSeqNo;
	}


	/**
	 * Method to check and process virtual gift card
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 * @return inXML Document
	 */
	public static Document addGiftWrapLine(YFSEnvironment env,
			Document inXML) throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<!-- Begining of KohlsUtil addGiftWrapLine method -- >"
					+ XMLUtil.getXMLString(inXML));
		}
		NodeList nodeShipmentLines;
		Element eleShipmentLine;		
		YFCDocument yfcDocGetOrder;
		YFCElement yfcEleGetOrder;		
		Document docOrderDetails;		
		String strItemID;
		String strOrderNo = "";
		String strEntCode;
		String strDocType;
		Set<String> itemList = new HashSet<String>();

		Element eleInputShipment = inXML.getDocumentElement();
		strEntCode = eleInputShipment.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
		strDocType = eleInputShipment.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);
		nodeShipmentLines = eleInputShipment
		.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES);
		Element eleShipmemtLines = (Element) nodeShipmentLines.item(0);
		List nodeShipmentLine = XMLUtil.getElementsByTagName(eleShipmemtLines,
				KohlsXMLLiterals.E_SHIPMENT_LINE);
		// iterate through the shipment lines and get the item details
		for (Iterator iterator = nodeShipmentLine.iterator(); iterator
		.hasNext();) {
			eleShipmentLine = (Element) iterator.next();
			strItemID = eleShipmentLine
			.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			// store the items in a hash set
			itemList.add(strItemID); 			
			// assuming that a shipment contains only one order
			strOrderNo = eleShipmentLine
			.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		}
		yfcDocGetOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcEleGetOrder = yfcDocGetOrder.getDocumentElement();
		yfcEleGetOrder.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER,
				strOrderNo);
		yfcEleGetOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE,
				strEntCode);
		yfcEleGetOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE,
				strDocType);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input xml for getOrderDetails in addGiftWrapLine:"
					+ XMLUtil.getXMLString(yfcDocGetOrder.getDocument()));
		}
		// get the order details for the order
		docOrderDetails = new KohlsUtil().api
		.getOrderDetails(env, yfcDocGetOrder.getDocument());
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("getOrderDetails output xml"
					+ XMLUtil.getXMLString(docOrderDetails));
		}
		// method to create the shipment line for extn group code
		Document outXML = giftWrapAddShipmentLine(env, docOrderDetails, inXML,
				itemList);
		return outXML;
	}

	/**
	 * Method to check for gift wrap code and add shipment line
	 * 
	 * @param docOrderDetails
	 *            Document
	 * @param inXML
	 *            Document
	 * @param itemList
	 *            Set
	 * 
	 * @throws Exception
	 *             e
	 * @return inXML Document
	 */
	private static Document giftWrapAddShipmentLine(YFSEnvironment env, Document docOrderDetails,
			Document inXML, Set<String> itemList) throws Exception {

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("<!-- Begining of KohlsUtil giftWrapAddShipmentLine method -- >"
					+ XMLUtil.getXMLString(inXML));
		}
		String orderLineKey = "";
		Element eleWrapGrpCodeLine;
		Element eleOrderDetail;
		Element eleOrderLineDetail;
		Element eleOrderLineExtn;
		Element eleLineChrgDetail;
		Element eleItem;
		NodeList nodeOrderHeadCharge;
		NodeList nodeOrderLineCharges;
		NodeList nodeOrderLineCharge;
		Element eleOrderHeadCharge;
		String strExWrapToGroupCode;
		String strLineParam;
		Element eleOrderLines;
		Element eleShipLine;
		Element eleOrderHeadCharges;
		Element eleOrderLineCharges;
		String chargeCategory;
		Element eleContLineDetail;
		Element eleContainer;
		String strOrItemID;
		String strPrimeLineNo;
		//Start --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012
		String strShipLineNo=null;
		NodeList nodeContainerDetails;
		//End --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012
		List chkPrimLn;
		String strExGiftWrapLineNo;
		List nodeContDetailLst = null;
		List lstContainer = null;
		String strExGiftItemID;
		NodeList nodeContainers;
		HashMap<String, String> extnGroupCode = null;
		HashMap<String, String> extnGroupCodeItem = null;
		HashMap<String, List<String>> extnGroupCodePrLn = null;
		List<String> prLnLst = null;

		Element eleInputShipment = inXML.getDocumentElement();
		nodeContainers = eleInputShipment.getElementsByTagName("Containers");
		Element eleContainers = (Element) nodeContainers.item(0);
		NodeList nodeContainer = eleContainers.getElementsByTagName("Container");
		if(nodeContainer.getLength()>0){
			lstContainer = XMLUtil.getElementsByTagName(eleContainers,"Container");	
			//Start --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012
			for (Iterator it = lstContainer.iterator(); it.hasNext();) {
				eleContainer = (Element) it.next();											
				nodeContainerDetails = eleContainer
				.getElementsByTagName("ContainerDetails");
				if(nodeContainerDetails.getLength()>0){
					Element eleContainerDetails = (Element) nodeContainerDetails.item(0);
					NodeList nodeContainerDetail = eleContainerDetails.getElementsByTagName("ContainerDetail");
					if(nodeContainerDetail.getLength()>0){				
						nodeContDetailLst = XMLUtil.getElementsByTagName(eleContainerDetails,
						"ContainerDetail");			
					}			
				}											
				if(null!=nodeContDetailLst){
					for (Iterator itera = nodeContDetailLst.iterator(); itera.hasNext();) {
						eleContLineDetail = (Element) itera.next();											
				eleShipLine = (Element) eleContLineDetail.getElementsByTagName("ShipmentLine").item(0);
				//storing the first shipment line no
				if(null == strShipLineNo){
					strShipLineNo=eleShipLine.getAttribute("PrimeLineNo");
				}
			   }
			 }	
			}
		}		
		//End --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012				
		eleOrderDetail = docOrderDetails.getDocumentElement();
		eleOrderLines = (Element) eleOrderDetail.getElementsByTagName(
				KohlsXMLLiterals.E_ORDER_LINES).item(0);
		eleOrderHeadCharges = (Element) eleOrderDetail.getElementsByTagName(
				KohlsXMLLiterals.A_HEADER_CHARGES).item(0);
		nodeOrderHeadCharge = eleOrderHeadCharges
		.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGE);
		List nodeOrderLine = XMLUtil.getElementsByTagName(eleOrderLines,
				KohlsXMLLiterals.E_ORDER_LINE);
		
		List listOfWrapTogetherCodes = new ArrayList();//List to have all the wrapTogetherCodes from the current shipment
		// iterate through the order lines to check the extn wrap group code
		
		for (Iterator iterator = nodeOrderLine.iterator(); iterator.hasNext();) {
			eleOrderLineDetail = (Element) iterator.next();
			strLineParam=null;
			strPrimeLineNo = eleOrderLineDetail.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			eleItem = (Element) eleOrderLineDetail.getElementsByTagName(
					KohlsXMLLiterals.E_ITEM).item(0);
			strOrItemID = eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			if (itemList.contains(strOrItemID)) {
				eleOrderLineExtn = (Element) eleOrderLineDetail
				.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				strExWrapToGroupCode = eleOrderLineExtn
				.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);
				strExGiftWrapLineNo = eleOrderLineExtn
				.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_WRAP_LINE_NO);
				strExGiftItemID = eleOrderLineExtn
				.getAttribute(KohlsXMLLiterals.A_EXTN_GIFT_ITEM_ID);
				nodeOrderLineCharges = eleOrderLineDetail
				.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES);

				if(nodeOrderLineCharges.getLength()>0){
					eleOrderLineCharges = (Element) eleOrderLineDetail
					.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES).item(0);
					nodeOrderLineCharge = eleOrderLineCharges
					.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGE);

					if(nodeOrderLineCharge.getLength()>0){
						List nodeLineCharge = XMLUtil.getElementsByTagName(eleOrderLineCharges,
								KohlsXMLLiterals.A_LINE_CHARGE);
						// iterate through the order lines to check the extn wrap group code
						for (Iterator iter = nodeLineCharge.iterator(); iter.hasNext();) {
							eleLineChrgDetail = (Element) iter.next();
							chargeCategory = eleLineChrgDetail.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY);
							if(chargeCategory.equalsIgnoreCase(KohlsXMLLiterals.A_GIFT_WRAP)){
								if(hasAlreadyBeenShipped(env, inXML, docOrderDetails, 
										eleOrderLineDetail.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY), strExWrapToGroupCode)){
									strLineParam="yes";
									chkPrimLn = new ArrayList<String>();
									if(null!=lstContainer){
										for (Iterator ite = lstContainer.iterator(); ite.hasNext();) {
											eleContainer = (Element) ite.next();											
											nodeContainerDetails = eleContainer
											.getElementsByTagName("ContainerDetails");
											if(nodeContainerDetails.getLength()>0){
												Element eleContainerDetails = (Element) nodeContainerDetails.item(0);
												NodeList nodeContainerDetail = eleContainerDetails.getElementsByTagName("ContainerDetail");
												if(nodeContainerDetail.getLength()>0){				
													nodeContDetailLst = XMLUtil.getElementsByTagName(eleContainerDetails,
													"ContainerDetail");			
												}			
											}											
											if(null!=nodeContDetailLst){
												for (Iterator itera = nodeContDetailLst.iterator(); itera.hasNext();) {
													eleContLineDetail = (Element) itera.next();											
											eleShipLine = (Element) eleContLineDetail.getElementsByTagName("ShipmentLine").item(0);
											if(strPrimeLineNo.equalsIgnoreCase(eleShipLine.getAttribute("PrimeLineNo"))){
												if(!chkPrimLn.contains(strPrimeLineNo)){
												// append a gift wrap line for line charge
												eleWrapGrpCodeLine = inXML
												.createElement("GiftWrapLine");
												eleWrapGrpCodeLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,
														strExGiftItemID);
												eleWrapGrpCodeLine.setAttribute(
														KohlsXMLLiterals.A_PRIME_LINE_NO,
														strExGiftWrapLineNo);
												eleWrapGrpCodeLine.setAttribute(
														KohlsXMLLiterals.A_QUANTITY,
														KohlsConstant.GIFT_WRAP_QUANTITY);
												eleContLineDetail.appendChild(eleWrapGrpCodeLine);
												chkPrimLn.add(strPrimeLineNo);
												}
											}
											}
										}
									}
								}
							}
						}				
					}	
				}}
					if (null != strExWrapToGroupCode
							&& !strExWrapToGroupCode
							.equalsIgnoreCase(KohlsConstant.BLANK) && null==strLineParam  && !listOfWrapTogetherCodes.contains(strExWrapToGroupCode)) {
						//					Here we check if a wrapTogetherCode is not null nor empty nor existing in the listOfWrapTogetherCodes List
						//					This check is done so that once a wrapTogetherCode has been processed and the code to check for previous shipments and to 
						//					add the GiftWrap line has been executed, this code is not invoked again. This is because the 
						//					requirement now is to add the GiftWrap Line only once for a wrapTogetherCode no matter how many times and how many ways you ship 
						//					the orderLines bound by a particular wrapTogetherCode.
						listOfWrapTogetherCodes.add(strExWrapToGroupCode);
						if(hasAlreadyBeenShipped(env, inXML, docOrderDetails, 
								eleOrderLineDetail.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY), strExWrapToGroupCode)){
						if (null == extnGroupCode) {
							extnGroupCode = new HashMap<String, String>();
							extnGroupCodeItem = new HashMap<String, String>();
							extnGroupCodePrLn = new HashMap<String, List<String>>();						
						}

						if(extnGroupCodePrLn.containsKey(strExWrapToGroupCode)){
							List<String> lstKeyVal = extnGroupCodePrLn.get(strExWrapToGroupCode);
							lstKeyVal.add(strPrimeLineNo);
							extnGroupCodePrLn.put(strExWrapToGroupCode, lstKeyVal);
						}else{
							prLnLst = new ArrayList<String>();
							//Start --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012
							//Adding the shipment line no to the prime line list
							prLnLst.add(strShipLineNo);
							//End --- Added for 91087,379,000 --- OASIS_SUPPORT 08/06/2012
							extnGroupCodePrLn.put(strExWrapToGroupCode, prLnLst);
						}					
						extnGroupCode.put(strExWrapToGroupCode,
								strExGiftWrapLineNo);
						extnGroupCodeItem.put(strExWrapToGroupCode,
								strExGiftItemID);	
					}
				} 
			}
		}
		if (null != extnGroupCode) {
			for (int i = 0; i < nodeOrderHeadCharge.getLength(); i++) {
				eleOrderHeadCharge = (Element) nodeOrderHeadCharge.item(i);
				String strRef = eleOrderHeadCharge.getAttribute(
						KohlsXMLLiterals.A_REFERENCE);
				Set set = extnGroupCode.entrySet();
				Iterator itr = set.iterator();
				while (itr.hasNext()) {
					Map.Entry entry = (Map.Entry) itr.next();
					String strExWrapToGro = (String) entry.getKey();
					String strExGiftWrLine = (String) entry.getValue();
					if (strRef.equalsIgnoreCase(strExWrapToGro)) {
						List<String> lstPrimeVal = extnGroupCodePrLn.get(strExWrapToGro);
						chkPrimLn = new ArrayList<String>();
						if(null!=lstContainer){
							for (Iterator ite = lstContainer.iterator(); ite.hasNext();) {
								eleContainer = (Element) ite.next();											
								 nodeContainerDetails = eleContainer
								.getElementsByTagName("ContainerDetails");
								if(nodeContainerDetails.getLength()>0){
									Element eleContainerDetails = (Element) nodeContainerDetails.item(0);
									NodeList nodeContainerDetail = eleContainerDetails.getElementsByTagName("ContainerDetail");
									if(nodeContainerDetail.getLength()>0){				
										nodeContDetailLst = XMLUtil.getElementsByTagName(eleContainerDetails,
										"ContainerDetail");			
									}			
								}											
								if(null!=nodeContDetailLst){
									for (Iterator itera = nodeContDetailLst.iterator(); itera.hasNext();) {
										eleContLineDetail = (Element) itera.next();	
						
										eleShipLine = (Element) eleContLineDetail.getElementsByTagName("ShipmentLine").item(0);
										if(lstPrimeVal.contains(eleShipLine.getAttribute("PrimeLineNo"))){
											if(!chkPrimLn.contains(eleShipLine.getAttribute("PrimeLineNo"))){
											// append a gift wrap line for header charge
											eleWrapGrpCodeLine = inXML
											.createElement("GiftWrapLine");
											eleWrapGrpCodeLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,
													extnGroupCodeItem.get(strExWrapToGro));
											eleWrapGrpCodeLine.setAttribute(
													KohlsXMLLiterals.A_PRIME_LINE_NO,
													strExGiftWrLine);
											eleWrapGrpCodeLine.setAttribute(
													KohlsXMLLiterals.A_QUANTITY,
													KohlsConstant.GIFT_WRAP_QUANTITY);							
											eleContLineDetail.appendChild(eleWrapGrpCodeLine);
											chkPrimLn.add(eleShipLine.getAttribute("PrimeLineNo"));
											}
										}
						
									}
								}								
							}
						}	
						
					}
				}			
			}
		}		

		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("Input xml after gift wrap is added in giftWrapAddShipmentLine:"
					+ XMLUtil.getXMLString(inXML));
		}
		return inXML;
	}


	/**
	 *	Create a new blank XML Document
	 *	@throws ParserConfigurationException
	 */
	public static Document newDocument()
	throws ParserConfigurationException {
		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder docBuilder = fac.newDocumentBuilder();

		return docBuilder.newDocument();
	}

	private static boolean hasAlreadyBeenShipped(YFSEnvironment env, Document shipmentDoc, 
			Document getOrdDetailsDoc, String orderLineKey, String wrapTogetherCode) 
	throws ParserConfigurationException, RemoteException, YFSException, YIFClientCreationException{

		log.debug("Values recieved by hasAlreadyBeenShipped method: \n ShipmentDoc: " + XMLUtil.getXMLString(shipmentDoc) + 
				" \n getOrderDetailsDoc: " + XMLUtil.getXMLString(getOrdDetailsDoc) + " \n orderLineKey: " + orderLineKey + ", wrapTogetherCode: " + wrapTogetherCode);
		boolean returnValue = true;
		//for this orderline>>orderLineKey, check the getShipmentList API
		//if noOfRecords is more than 1, dont go further (ie return false)
		//else, call getOrderLineList to get list of items in this order which have the same wrapTogetherGrpCode
		//loop thru all items which do not have the same ItemID/orderLineKey as the current one
		//call getShipmentList again for each of these items
		//if noOfRecords is more than zero, check if the shipment No is same as the current one.
		//if not, return false and break out , 
		//		else return true


		Element inXMLRootElem = shipmentDoc.getDocumentElement();

		//Create i/p XML for getShipmentList with this OrderHeaderKey and OrderLineKey
		//<Shipment  DocumentType="0001"
		//EnterpriseCode="KOHLS.COM" 
		//SellerOrganizationCode="KOHLS.COM"  >
		//<ShipmentLines>
		//<ShipmentLine     OrderHeaderKey="20110304145309160078"
		//OrderLineKey="20110304145309160079" />
		//</ShipmentLines>
		//</Shipment>

		Document getShipListInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		Element getShipListInRootElem = getShipListInDoc.getDocumentElement();
		getShipListInRootElem.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, inXMLRootElem.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		getShipListInRootElem.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, inXMLRootElem.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
		getShipListInRootElem.setAttribute(KohlsXMLLiterals.A_SELLERORGCODE, inXMLRootElem.getAttribute(KohlsXMLLiterals.A_SELLERORGCODE));
		Element shipmentLinesElem = getShipListInDoc.createElement(KohlsXMLLiterals.E_SHIPMENT_LINES);
		getShipListInRootElem.appendChild(shipmentLinesElem);
		Element shipmentLineElement = getShipListInDoc.createElement(KohlsXMLLiterals.E_SHIPMENT_LINE);
		shipmentLineElement.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, getOrdDetailsDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
		shipmentLineElement.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, orderLineKey);
		shipmentLinesElem.appendChild(shipmentLineElement);

		log.debug("getShipmentList i/p Doc: \n" + XMLUtil.getXMLString(getShipListInDoc));
		Document shipmentListTempl = createGetShipmentListTempl();
		env.setApiTemplate(KohlsConstant.API_actual_GET_SHIPMENT_LIST, shipmentListTempl);
		Document getShipListOutDoc = new KohlsUtil().api.getShipmentList(env, getShipListInDoc);

		log.debug("getShipmentList ouput Doc: \n" + XMLUtil.getXMLString(getShipListOutDoc));

		if(Integer.parseInt(getShipListOutDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS)) > 1){
			//This orderLineKey has more than one shipment i.e. it has been shipped earlier as well. Hence, return false
			returnValue = false;
		}
		else{
			//Call createGetOrderLineListInput() to get I/p XML for getOrderLineList 

			Document getOrderLineListInDoc = createGetOrderLineListInput(getOrdDetailsDoc, wrapTogetherCode);
			//Document getOrderLineListTemplate = callSomeMethod();
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_LIST, createGetOrdLineListTempl());
			Document getOrderLineListOutDoc = new KohlsUtil().api.getOrderLineList(env, getOrderLineListInDoc);
			log.debug("getOrderLineList Output Document: \n" + XMLUtil.getXMLString(getOrderLineListOutDoc));
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_LIST);
			if(getOrderLineListOutDoc != null){
				Element orderLineListRootElem = getOrderLineListOutDoc.getDocumentElement();
				String totNoOfRecs = orderLineListRootElem.getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS);
				if(totNoOfRecs != null && !totNoOfRecs.equals("")){
					int totalNoOfRecords = Integer.parseInt(totNoOfRecs);
					if(totalNoOfRecords > 0){
						NodeList orderLineList = orderLineListRootElem.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
						for(int i=0; i < orderLineList.getLength(); i++){
							Element orderLineElem = (Element)orderLineList.item(i);
							if(orderLineElem != null){
								String orderLineKey1 = (String)orderLineElem.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
								if(orderLineKey1 != null && !orderLineKey1.equals("") && !orderLineKey1.equalsIgnoreCase(orderLineKey)){
									shipmentLineElement.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, orderLineKey1);
									log.debug("getShipmentList i/p Doc for other items with same wrapTogetherCode: \n" + XMLUtil.getXMLString(getShipListInDoc));
									env.setApiTemplate(KohlsConstant.API_actual_GET_SHIPMENT_LIST, createGetShipmentListTempl());
									getShipListOutDoc = new KohlsUtil().api.getShipmentList(env, getShipListInDoc);
									log.debug("getShipmentList ouput Doc for other items with same wrapTogetherCode: \n" + XMLUtil.getXMLString(getShipListOutDoc));
									if(Integer.parseInt(getShipListOutDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS)) > 1){
										//	    			i.e. another orderline with the same wrapTogetherGroupCode has been shipped earlier also
										//		    		because it has two or more shipments to its name
										//		    		Hence, pass a false
										returnValue = false;
										break;
									}
									else if(Integer.parseInt(getShipListOutDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS)) > 0){
										String shipmentNo = ((Element)getShipListOutDoc.getDocumentElement().getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT).item(0)).getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
										if(shipmentNo != null && !shipmentNo.equalsIgnoreCase("") && !shipmentNo.equalsIgnoreCase(inXMLRootElem.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO))){
											//		    			i.e. another orderline with the same wrapTogetherGroupCode has been shipped earlier
											//		    			because the current shipmentNo is not the same as the one obtained from getShipmentList API call
											//		    			Hence, pass a 'false'
											returnValue = false;
											break;
										}
									}
								}
							}
						}
					}
				}
			}
		}
		env.clearApiTemplate(KohlsConstant.API_actual_GET_SHIPMENT_LIST);
		return returnValue;
	}

	/**
	 * This method creates the input Document for a call to GetOrderLineList API
	 * @param getOrdDetailsDoc
	 * @param wrapTogetherCode
	 * @return
	 * @throws ParserConfigurationException
	 * @throws RemoteException
	 */
	private static Document createGetOrderLineListInput (Document getOrdDetailsDoc, String wrapTogetherCode)
	throws ParserConfigurationException, RemoteException{

		//Create I/p XML for getOrderLineList for this ExtnWrapTogetherGroupCode and OrderHeaderKey
		//<OrderLine>
		//<Extn ExtnWrapTogetherGroupCode="AAA"/>
		//<Order DocumentType="0001" EnterpriseCode="KOHLS.COM" 
		//OrderHeaderKey="20110304145309160078" />
		//</OrderLine>
		Document getOrdLineListInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_ORDER_LINE);
		Element getOrdLineListInRootElem = getOrdLineListInDoc.getDocumentElement(); 
		Element extnElement = getOrdLineListInDoc.createElement(KohlsXMLLiterals.E_EXTN);
		extnElement.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, wrapTogetherCode);
		getOrdLineListInRootElem.appendChild(extnElement);

		Element orderElement = getOrdLineListInDoc.createElement(KohlsXMLLiterals.E_ORDER);
		orderElement.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, getOrdDetailsDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
		orderElement.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, getOrdDetailsDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY));
		orderElement.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, getOrdDetailsDoc.getDocumentElement().getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));	
		getOrdLineListInRootElem.appendChild(orderElement);
		log.debug("getOrderLineList Input Document: \n" + XMLUtil.getXMLString(getOrdLineListInDoc));
		return getOrdLineListInDoc;
	}

	/**
	 * This method creates the template Document for a call to the GetOrderLineList API
	 * @return
	 * @throws ParserConfigurationException
	 * @throws RemoteException
	 */
	private static Document createGetOrdLineListTempl() throws ParserConfigurationException, RemoteException{
		//<OrderLineList  TotalLineList="" TotalNumberOfRecords="">
		//<OrderLine  OrderHeaderKey="" OrderLineKey="" >
		//</OrderLine>
		//</OrderLineList>
		Document templateDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_ORDER_LINE_LIST);
		Element templateRootElem = templateDoc.getDocumentElement(); 
		templateRootElem.setAttribute(KohlsXMLLiterals.E_TOTAL_LINE_LIST, KohlsConstant.BLANK);
		templateRootElem.setAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS, KohlsConstant.BLANK);

		Element orderLineElem = templateDoc.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		orderLineElem.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, KohlsConstant.BLANK);
		templateRootElem.appendChild(orderLineElem);
		log.debug("getOrderLineList Template Document: \n" + XMLUtil.getXMLString(templateDoc));
		return templateDoc;
	}

	/**
	 * This method creates the template Document for a call to the GetShipmentList API
	 * @return
	 * @throws ParserConfigurationException
	 * @throws RemoteException
	 */
	private static Document createGetShipmentListTempl() throws ParserConfigurationException, RemoteException{
		//<Shipments TotalNumberOfRecords="">
		//<Shipment ShipmentNo=""/>
		//</Shipments>
		Document templateDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_SHIPMENTS);
		Element templateRootElem = templateDoc.getDocumentElement(); 

		templateRootElem.setAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS, KohlsConstant.BLANK);

		Element shipmentElem = templateDoc.createElement(KohlsXMLLiterals.E_SHIPMENT);
		shipmentElem.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, KohlsConstant.BLANK);

		templateRootElem.appendChild(shipmentElem);
		log.debug("getShipmentList Template Document: \n" + XMLUtil.getXMLString(templateDoc));
		return templateDoc;
	}


	/**
	 * Parse an XML string or a file, to return the Document.
	 *
	 * @param inXML if starts with '&lt;', it is an XML string; otherwise it should be an XML file name.
	 *
	 * @return the Document object generated
	 * @throws ParserConfigurationException when XML parser is not properly configured.
	 * @throws SAXException when failed parsing XML string.
	 * @throws IOException
	 */
	public static Document getDocument( String inXML )
	throws ParserConfigurationException, SAXException, IOException {
		if( (inXML != null) ) {
			inXML = inXML.trim();
			if ( inXML.length() > 0 ) {
				if ( inXML.startsWith("<")) {
					StringReader strReader = new StringReader(inXML);
					InputSource iSource = new InputSource(strReader);
					return getDocument( iSource );
				}

				// It's a file
				FileReader inFileReader = new FileReader( inXML );
				Document retVal = null;
				try {
					InputSource iSource = new InputSource( inFileReader );
					retVal = getDocument( iSource );
				} finally {
					inFileReader.close();
				}
				return retVal;
			}
		}
		return null;
	}

	/**
	 *	Generate a Document object according to InputSource object.
	 * @throws ParserConfigurationException when XML parser is not properly configured.
	 * @throws SAXException when failed parsing XML string.
	 * @throws IOException
	 */
	public static Document getDocument( InputSource inSource )
	throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

		return dbdr.parse( inSource );
	}

	/**
	 *	Generate a Document object according to InputStream object.
	 * @throws ParserConfigurationException when XML parser is not properly configured.
	 * @throws SAXException when failed parsing XML string.
	 * @throws IOException
	 */
	public static Document getDocument( InputStream inStream )
	throws ParserConfigurationException, SAXException, IOException {
		Document retDoc = getDocument( new InputSource(new InputStreamReader(inStream)));
		inStream.close();
		return retDoc;
	}

	/**
	 * Parse an XML file, to return the Document.
	 *
	 * @deprecated use getDocument(String) instead.
	 *
	 * @return the Document object generated
	 * @throws ParserConfigurationException when XML parser is not properly configured.
	 * @throws SAXException when failed parsing XML string.
	 * @throws IOException
	 */
	public static Document getDocument( String inXMLFileName, boolean isFile )
	throws ParserConfigurationException, SAXException, IOException {
		if( (inXMLFileName != null) && (!inXMLFileName.equals("")) ) {
			FileReader inFileReader = new FileReader( inXMLFileName );
			InputSource iSource = new InputSource( inFileReader );
			Document doc = getDocument( iSource );
			inFileReader.close();
			return doc;
		}
		return null;
	}

	/**
	 *	Create a Document object with input as the name of document element.
	 *
	 *	@param docElementTag: the document element name.
	 *	@throws ParserConfigurationException
	 */
	public static Document createDocument(String docElementTag)
	throws ParserConfigurationException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

		Document doc = dbdr.newDocument();
		Element ele = doc.createElement(docElementTag);
		doc.appendChild(ele);
		return doc;
	}

	/**
	 *	Create a Document object with input node 
	 *	@param docNode: the Node to create a document from.
	 *	@throws ParserConfigurationException
	 */
	public static Document createDocument(Node docNode)
	throws ParserConfigurationException {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();
		Document doc = dbdr.newDocument();
		Element inElement = (Element)doc.importNode(docNode,true);
		doc.appendChild(inElement);
		return doc;
	}

	/**
	 *  Merges document doc2 in to doc1.
	 * For e.g., <p>
	 * if doc1 =   &lt;Root1>&lt;A1/>&lt;/Root1> <p>
	 * & doc2 =  &lt;Root2>&lt;B1/>&lt;Root2> <p>
	 *  then the merged Doc will be
	 *  doc1 =  &lt;Root1>&lt;A1/>&lt;B1/>&lt;/Root1>
	 * @param doc1
	 * @param doc2
	 * @return
	 * @deprecated use  addDocument(Document doc1,Document doc2, boolean ignoreRoot)
	 */
	public static Document addDocument(Document doc1,Document doc2) {
		Element  rt1 = doc1.getDocumentElement();
		Element  rt2 = doc2.getDocumentElement();

		NodeList nlst2 = rt2.getChildNodes();
		int len = nlst2.getLength();
		Node nd = null;
		for (int i=0; i < len; i++) {
			nd = doc1.importNode(nlst2.item(i), true);
			rt1.appendChild(nd);
		}
		return doc1;
	}


	/**
	 * Merges document doc2 in to doc1. Root node of doc2 is included only if ignoreRoot flag is set to false.
	 * <p/>
	 * For e.g., <p>
	 * if doc1 =   &lt;Root1>&lt;A1/>&lt;/Root1> <p>
	 * & doc2 =  &lt;Root2>&lt;B1/>&lt;Root2> <p>
	 * then the merged Doc will be
	 * doc1 =  &lt;Root1>&lt;A1/><B>&lt;Root2>&lt;B1/>&lt;Root2></B>&lt;/Root1>  <B>if ignoreRoot = false</B> <p>
	 * <B>if ignoreRoot = true</B> then the merged Doc will be <p>
	 * doc1 =  &lt;Root1>&lt;A1/><B>&lt;B1/></B>&lt;/Root1>
	 *
	 * @param doc1
	 * @param doc2
	 * @param ignoreRoot ignores root element of doc2 in the merged doc.
	 * @return
	 */
	public static Document addDocument(Document doc1, Document doc2, boolean ignoreRoot) {
		Element rt1 = doc1.getDocumentElement();
		Element rt2 = doc2.getDocumentElement();
		if (!ignoreRoot) {
			Node nd = doc1.importNode(rt2, true);
			rt1.appendChild(nd);
			return doc1;
		}
		NodeList nlst2 = rt2.getChildNodes();
		int len = nlst2.getLength();
		Node nd = null;
		for (int i = 0; i < len; i++) {
			nd = doc1.importNode(nlst2.item(i), true);
			rt1.appendChild(nd);
		}
		return doc1;
	}


	/**
	 * Create a new Document with the given Element as the root node.
	 * @param inElement
	 * @return
	 * @throws Exception
	 */
	public static Document getDocumentForElement(Element inElement) throws
	Exception {

		DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
		fac.setNamespaceAware(true);
		DocumentBuilder dbdr = fac.newDocumentBuilder();

		Document doc = dbdr.newDocument();
		Element docElement = doc.createElement(inElement.getNodeName());
		doc.appendChild(docElement);
		copyElement(doc, inElement, docElement);
		return doc;
	}





	/**
	 * Returns a formatted XML string for the Node, using encoding 'iso-8859-1'.
	 *
	 * @param node   a valid document object for which XML output in String form is required.
	 *
	 * @return the formatted XML string.
	 */

	public static String serialize( Node node ) {
		return serialize(node, "iso-8859-1", true);
	}

	/**
	 *	Return a XML string for a Node, with specified encoding and indenting flag.
	 *	<p>
	 *	<b>Note:</b> only serialize DOCUMENT_NODE, ELEMENT_NODE, and DOCUMENT_FRAGMENT_NODE
	 *
	 *	@param node the input node.
	 *	@param encoding such as "UTF-8", "iso-8859-1"
	 *	@param indenting indenting output or not.
	 *
	 *	@return the XML string
	 */
	public static String serialize(Node node, String encoding, boolean indenting) {
		OutputFormat outFmt = null;
		StringWriter strWriter = null;
		XMLSerializer xmlSerializer = null;
		String retVal = null;

		try{
			outFmt = new OutputFormat("xml", encoding, indenting);
			outFmt.setOmitXMLDeclaration(true);

			strWriter = new StringWriter();

			xmlSerializer = new XMLSerializer(strWriter, outFmt);
			if(node == null)
			{
				return "null";
			}
			short ntype = node.getNodeType();

			switch(ntype) {
			case Node.DOCUMENT_FRAGMENT_NODE: xmlSerializer.serialize((DocumentFragment)node); break;
			case Node.DOCUMENT_NODE: xmlSerializer.serialize((Document)node); break;
			case Node.ELEMENT_NODE: xmlSerializer.serialize((Element)node); break;
			default: throw new IOException("Can serialize only Document, DocumentFragment and Element type nodes");
			}

			retVal = strWriter.toString();
		} catch (IOException e) {
			retVal = e.getMessage();
		} finally{
			try {
				strWriter.close();
			} catch (IOException ie) {}
		}

		return retVal;
	}

	/**
	 *	Return a decendent of first parameter, that is the first one to match the XPath specified in
	 *	the second parameter.
	 *
	 *	@param ele The element to work on.
	 *	@param tagName format like "CHILD/GRANDCHILD/GRANDGRANDCHILD"
	 *
	 *	@return	the first element that matched, null if nothing matches.
	 */
	public static Element getFirstElementByName(Element ele, String tagName) {
		StringTokenizer st = new StringTokenizer(tagName, "/");
		Element curr = ele;
		Node node;
		String tag;
		while (st.hasMoreTokens()) {
			tag = st.nextToken();
			node = curr.getFirstChild();
			while (node != null) {
				if (node.getNodeType() == Node.ELEMENT_NODE && 
						(tag.equals(node.getNodeName()) || tag.equals(node.getLocalName()))) {
					break;
				}
				node = node.getNextSibling();
			}

			if (node != null)
				curr = (Element)node;
			else
				return null;
		}

		return curr;
	}

	/**
	 * csc stands for Convert Special Character. Change &, <, ", ' into XML acceptable.
	 * Because it could be used frequently, it is short-named to 'csc'.
	 * Usually when a string is used for XML values, the string should be parsed first.
	 *
	 * @param str the String to convert.
	 * @return converted String with & to &amp;amp;, < to &amp;lt;, " to &amp;quot;, ' to &amp;apos;
	 */
	public static String csc(String str) {
		if (str == null || str.length() == 0)
			return str;

		StringBuffer buf = new StringBuffer(str);
		int i = 0;
		char c;

		while (i < buf.length()) {
			c = buf.charAt(i);
			if (c == '&') {
				buf.replace(i, i+1, "&amp;");
				i += 5;
			} else if (c == '<') {
				buf.replace(i, i+1, "&lt;");
				i += 4;
			} else if (c == '"') {
				buf.replace(i, i+1, "&quot;");
				i += 6;
			} else if (c == '\'') {
				buf.replace(i, i+1, "&apos;");
				i += 6;
			} else if (c == '>') {
				buf.replace(i, i+1, "&gt;");
				i += 4;
			} else
				i++;
		}

		return buf.toString();
	}


	/**
	 *  For an Element node, return its Text node's value; otherwise return the node's value.
	 * @param node
	 * @return
	 */
	public static String getNodeValue(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Node child = node.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.TEXT_NODE)
					return child.getNodeValue();
				child = child.getNextSibling();
			}
			return null;
		} else
			return node.getNodeValue();
	}

	/**
	 *	For an Element node, set its Text node's value (create one if it does not have);
	 *	otherwise set the node's value.
	 * @param node
	 * @param val
	 */
	public static void setNodeValue(Node node, String val) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			Node child = node.getFirstChild();
			while (child != null) {
				if (child.getNodeType() == Node.TEXT_NODE)
					break;
				child = child.getNextSibling();
			}
			if (child == null) {
				child = node.getOwnerDocument().createTextNode(val);
				node.appendChild(child);
			} else
				child.setNodeValue(val);
		} else
			node.setNodeValue(val);
	}

	/**
	 *	@deprecated Recommended to use Document.getDocumentElement() directly.
	 */
	public static Element getRootElement( Document doc ) {
		return doc.getDocumentElement();
	}

	/**
	 * Creates an element with the supplied name and attributevalues.
	 * @param doc XML Document on which to create the element
	 * @param elementName the name of the node element
	 * @param hashAttributes usually a Hashtable containing name/value pairs for the attributes of the element.
	 */
	public static Element createElement( Document doc, String elementName, Object hashAttributes ) {
		return createElement( doc, elementName, hashAttributes, false );
	}

	/**
	 * Creates an node text node element with the text node value supplied
	 * @param doc the XML document on which this text node element has to be created.
	 * @param elementName the name of the element to be created
	 * @param textStr should be a String for the value of the text node
	 */
	public static Element createTextElement( Document doc, String elementName, Object textStr ) {
		return createElement( doc, elementName, textStr, true );
	}

	/**
	 * Creates an element with the text node value supplied
	 * @param doc the XML document on which this text node element has to be created.
	 * @param elementName the name of the element to be created
	 * @param attributes usually a Hashtable containing name/value pairs for the attributes of the element.
	 * @param textValue the value for the text node of the element.
	 */
	public static Element createTextElement( Document doc, String elementName, String textValue, Hashtable attributes ) {
		Element elem = doc.createElement(elementName);
		elem.appendChild( doc.createTextNode(textValue) );
		if( attributes != null ) {
			Enumeration e = attributes.keys();
			while ( e.hasMoreElements() ) {
				String attributeName =  (String)e.nextElement();
				String attributeValue = (String)( attributes.get(attributeName) );
				elem.setAttribute( attributeName, attributeValue );
			}
		}
		return elem;
	}

	/**
	 * Creates an element with the text node value supplied
	 * @param doc the XML document on which this text node element has to be created.
	 * @param parentElement the parent element on which this text node element has to be appended
	 * @param elementName the name of the element to be created
	 * @param attributes usually a Hashtable containing name/value pairs for the attributes of the element.
	 * @param textValue the value for the text node of the element.
	 */
	public static Element appendTextChild( Document doc, Element parentElement, String elementName, String textValue, Hashtable attributes ) {
		Element elem = doc.createElement(elementName);
		elem.appendChild( doc.createTextNode(textValue) );
		if( attributes != null ) {
			Enumeration e = attributes.keys();
			while ( e.hasMoreElements() ) {
				String attributeName =  (String)e.nextElement();
				String attributeValue = (String)( attributes.get(attributeName) );
				elem.setAttribute( attributeName, attributeValue );
			}
		}
		parentElement.appendChild(elem);
		return elem;
	}

	/**
	 * Create an element with either attributes or text node.
	 * @param doc the XML document on which the node has to be created
	 * @param elementName the name of the element to be created
	 * @param hashAttributes the value for the text node or the attributes for the node element
	 * @param textNodeFlag a flag signifying whether te node to be created is the text node
	 */
	public static Element createElement( Document doc, String elementName, Object hashAttributes, boolean textNodeFlag ) {
		Element elem = doc.createElement(elementName);
		if (hashAttributes != null) {
			if (hashAttributes instanceof String) {
				if (textNodeFlag ) {
					elem.appendChild( doc.createTextNode( (String)hashAttributes ) );
				}
			} else if(hashAttributes instanceof Hashtable) {
				Enumeration e = ((Hashtable)hashAttributes).keys();
				while ( e.hasMoreElements() ) {
					String attributeName =  (String)e.nextElement();
					String attributeValue = (String)((Hashtable)hashAttributes).get( attributeName );
					elem.setAttribute( attributeName, attributeValue );
				}
			}
		}
		return elem;
	}

	/**
	 * This method is for adding child Nodes to parent node element, the child element has to be created first.
	 * @param doc
	 * @param      parentElement Parent Element under which the new Element should be present
	 * @param      elementName   Name of the element to be created
	 * @param      value         Can be either a String ,just the element value if it is a single attribute
	 * @return
	 */
	public static Element appendChild( Document doc, Element parentElement, String elementName, Object value ) {
		Element childElement = createElement( doc, elementName, value);
		parentElement.appendChild(childElement);
		return childElement;
	}

	/**
	 *	@deprecated Use appendChild(Element, Element) instead.
	 */
	public static void appendChild( Document doc, Element parentElement, Element childElement ) {
		parentElement.appendChild(childElement);
		return;
	}

	/**
	 * This method is for adding child Nodes to parent node element.
	 * @param      parentElement Parent Element under which the new Element should be present
	 * @param      childElement  Child Element which should be added.
	 */
	public static void appendChild( Element parentElement, Element childElement ) {
		parentElement.appendChild(childElement);
	}

	/**
	 * This method is for setting the attribute of an element
	 * @param      objElement     Element where this attribute should be set
	 * @param      attributeName  Name of the attribute
	 * @param      attributeValue Value of the attribute
	 */
	public static void setAttribute(Element objElement, String attributeName, String attributeValue) {
		objElement.setAttribute(attributeName,attributeValue);
	}

	/**
	 * This method is for removing an attribute from an Element.
	 * @param      objElement     Element from where the attribute should be removed.
	 * @param      attributeName  Name of the attribute
	 */
	public static void removeAttribute( Element objElement, String attributeName ) {
		objElement.removeAttribute(attributeName);
	}

	/**
	 * This method is for removing the child element of an element
	 * @param      parentElement     Element from where the child element should be removed.
	 * @param      childElement  Child Element which needs to be removed from the parent
	 */
	public static void removeChild( Element parentElement, Element childElement ) {
		parentElement.removeChild(childElement);
	}

	/**
	 * Method to create a text mode for an element
	 * @param doc the XML document on which the node has to be created
	 * @param parentElement the element for which the text node has to be created.
	 * @param elementValue the value for the text node.
	 */
	public static void createTextNode( Document doc, Element parentElement, String elementValue ) {
		parentElement.appendChild( doc.createTextNode( elementValue ) );
	}

	/**
	 * If this class was used for building XML from scratch , this method
	 * would give constructed XML as String.
	 *	@deprecated use serialize(Node) instead.
	 */
	public static String constructXML( Document doc ) {
		return serialize( doc );
	}

	/**
	 * This method takes Document as input and returns the XML String.
	 * @param document   a valid document object for which XML output in String form is required.
	 */
	public static String getXMLString( Document document ) {
		if (document == null){
			return "null";
		}
		else {
			return serialize( document );
		}
	}

	/**
	 *
	 * This method takes a document Element as input and returns the XML String.
	 * @param element   a valid element object for which XML output in String form is required.
	 * @return XML String of the given element
	 */

	public static String getElementXMLString( Element element ) {
		if (element == null){
			return "null";
		}
		else {
			return serialize( element );
		}
	}

	/**
	 *	Convert the Document to String and write to a file.
	 * @param document
	 * @param fileName
	 * @throws IOException
	 */
	public static void flushToAFile( Document document, String fileName )
	throws IOException {
		if( document != null ) {
			OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
			oFmt.setPreserveSpace(true);
			XMLSerializer xmlOP = new XMLSerializer(oFmt);
			FileWriter out = new FileWriter(new File(fileName));
			xmlOP.setOutputCharStream(out);
			xmlOP.serialize(document);
			out.close();
		}
	}


	/**
	 *	Serialize a Document to String and output to a java.io.Writer.
	 * @param document
	 * @param writer
	 * @throws IOException
	 */
	public static void flushToAFile( Document document, Writer writer ) throws IOException {
		if( document != null ) {
			OutputFormat oFmt = new OutputFormat( document,"iso-8859-1",true );
			oFmt.setPreserveSpace(true);
			XMLSerializer xmlOP = new XMLSerializer(oFmt);
			xmlOP.setOutputCharStream(writer);
			xmlOP.serialize(document);
			writer.close();
		}
	}


	/**
	 * This method  constructs and inserts a process Instruction in the given document
	 * @param doc
	 * @param rootElement
	 * @param strTarget
	 * @param strData
	 */
	public static void createProcessingInstruction( Document doc, Element rootElement, String strTarget, String strData ) {
		ProcessingInstruction p = doc.createProcessingInstruction( strTarget, strData );
		doc.insertBefore( p, (Node)rootElement );
	}



	/**
	 *
	 * @param element
	 * @param attributeName
	 * @return the value of the attribute in the element.
	 */
	public static String getAttribute(Element element, String attributeName) {
		if (element != null)
			return element.getAttribute(attributeName);
		else
			return null;
	}

	/**
	 *	Get the first direct child Element with the name.
	 *	@deprecated use getFirstElementByName() instead.
	 */
	public static Element getUniqueSubNode( Element element, String nodeName ) {
		Element uniqueElem = null;
		NodeList nodeList = element.getElementsByTagName( nodeName );
		if( nodeList != null && nodeList.getLength()>0 ) {
			int size = nodeList.getLength();
			for( int count=0; count<size; count++ ) {
				uniqueElem = (Element)(nodeList.item(count));
				if( uniqueElem != null ) {
					if( uniqueElem.getParentNode() == element )
						break;
				}
			}
		}
		return uniqueElem;
	}

	/**
	 *	Gets the node value for a sub element under a Element with unique name.
	 *	@deprecated the logic is not clear as the implementation gets the value of grand-child instead of direct child.
	 *	should use getFirstElementByName() and getNodeValue() combination for application logic.
	 */
	public static String getUniqueSubNodeValue( Element element, String nodeName ) {
		NodeList nodeList = element.getElementsByTagName( nodeName );
		if( nodeList != null ) {
			Element uniqueElem = (Element)(nodeList.item(0));
			if( uniqueElem != null ) {
				if( uniqueElem.getFirstChild() != null )
					return uniqueElem.getFirstChild().getNodeValue();
				else
					return null;
			} else
				return null;
		} else
			return null;
	}

	/**
	 * Return the sub elements with given name, as a List.
	 * @param element
	 * @param nodeName
	 * @return
	 */
	public static List getSubNodeList( Element element, String nodeName ) {
		NodeList nodeList = element.getElementsByTagName( nodeName );
		List elemList = new ArrayList();
		for( int count=0; count<nodeList.getLength(); count++ )
			elemList.add( nodeList.item( count ) );
		return elemList;
	}

	/**
	 *	Same as getSubNodeList().
	 *	@see #getSubNodeList(Element, String).
	 */
	public static List getElementsByTagName( Element startElement, String elemName ) {
		NodeList nodeList = startElement.getElementsByTagName( elemName );
		List elemList = new ArrayList();
		for( int count=0; count<nodeList.getLength(); count++ )
			elemList.add(nodeList.item(count));
		return elemList;
	}

	/**
	 *	Gets the count of sub nodes under one node matching the sub node name
	 *	@param parentElement Element under which sub nodes reside
	 *   @param subElementName Name of the sub node to look for in the parent node
	 */
	public static int getElementsCountByTagName( Element parentElement, String subElementName ) {
		NodeList nodeList = parentElement.getElementsByTagName( subElementName );
		if( nodeList != null )
			return nodeList.getLength();
		else
			return 0;
	}

	/**
	 *	Augment a destination Element with a source Element. Including the source Element's Attributes and child nodes.
	 *	<p>
	 *	The behavior is a little inconsistant: attributes in destElem are replaced, but child nodes are added, i.e. no
	 *	equality check of child nodes. So the meaningful way to use it is to start with an empty destination Element.
	 *	<br>
	 *	It's better be replaced by a method with signature: <i>Element copyElement(Document destDoc, Element srcElem)</i>
	 *
	 *	@param destDoc the Document for destination Element, must be the same as destElem.getDocument().
	 *	@param srcElem the source Element.
	 *	@param destElem the destination Element.
	 */


	public static void copyElement( Document destDoc, Element srcElem, Element destElem ) {
		NamedNodeMap attrMap = srcElem.getAttributes();
		int attrLength = attrMap.getLength();
		for( int count=0; count<attrLength; count++ ) {
			Node attr = attrMap.item(count);
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			destElem.setAttribute( attrName, attrValue );
		}

		if( srcElem.hasChildNodes() ) {
			NodeList childList = srcElem.getChildNodes();
			int numOfChildren = childList.getLength();
			for( int cnt=0; cnt<numOfChildren; cnt++ ) {
				Object childSrcNode = childList.item(cnt);
				if( childSrcNode instanceof CharacterData ) {
					if( childSrcNode instanceof Text ) {
						String data = ((CharacterData)(childSrcNode)).getData();
						Node childDestNode = destDoc.createTextNode( data );
						destElem.appendChild( childDestNode );
					} else if( childSrcNode instanceof Comment ) {
						String data = ((CharacterData)(childSrcNode)).getData();
						Node childDestNode = destDoc.createComment( data );
						destElem.appendChild( childDestNode );
					}
				} else {
					Element childSrcElem = (Element)(childSrcNode);
					Element childDestElem = appendChild( destDoc, destElem, childSrcElem.getNodeName(), null );
					copyElement( destDoc, childSrcElem, childDestElem );
				}
			}
		}
	}
	/**
	 *	Finds the first child node with the given name and returns it.
	 *  This method can be useful for getting first level children and significantly faster than using XPATH
	 *	@param parentNode The parent Node 
	 *	@param childnodeName The child node name to be searched
	 */
	public static Node getChildNodeByName(Node parentNode, String childnodeName) {
		NodeList childrenNodes = parentNode.getChildNodes();
		int length = childrenNodes.getLength();
		for(int index = 0; index < length; index++){
			Node childNode = childrenNodes.item(index);
			String name = childNode.getNodeName();
			if (childnodeName.equalsIgnoreCase(name)){
				return childNode;
			}
		}
		return null;
	}
	/**
	 * Creates a Document object
	 * 
	 * @return empty Document object
	 * @throws ParserConfigurationException
	 */
	public static Document getDocument() throws ParserConfigurationException {
		//Create a new Document Bilder Factory instance
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory
		.newInstance();

		documentBuilderFactory.setNamespaceAware(true);

		//Create new document builder
		DocumentBuilder documentBuilder = documentBuilderFactory
		.newDocumentBuilder();

		//Create and return document object
		return documentBuilder.newDocument();
	}


	public static String extractStringFromDocument(Document doc) throws TransformerException 
	{
		DOMSource domSource = new DOMSource(doc);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();

	}
	public static String extractStringFromNode(Node node) throws TransformerException 
	{
		DOMSource domSource = new DOMSource(node);
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		transformer.transform(domSource, result);
		return writer.toString();

	}

	// COMPUTE CHECK DIGIT

	public static int computeMod10CheckDigit(String id) {
		StringBuffer sb = new StringBuffer(id);

		int len = sb.length();
		int sum = 0;

		boolean alternate = true;
		for (int i = len - 1; i >= 0; i--, alternate = !alternate) {
			// Every other digit, starting with the first one on the right,
			// should be replaced with double its value in the StringBuffer.
			// For example, if the current digit is 7 and it is doubled,
			// then the 7 would be replaced with a 1 and 4 (thus increasing
			// the size of the StringBuffer).
			if (alternate) {
				int digit = Character.digit(sb.charAt(i), 10);
				sb.replace(i, i + 1, String.valueOf(digit * 2));
			}
		}

		// Get the new length of the StringBuffer and add up the values of
		// the individual digits.
		len = sb.length();

		for (int i = 0; i < len; i++) {
			int digit = Character.digit(sb.charAt(i), 10);
			sum += digit;
		}

		// If the sum MOD 10 is 0, then the check digit is 0, otherwise,
		// the check digit is 10 minus the sum MOD 10.
		return (sum % 10 == 0) ? 0 : 10 - (sum % 10);
	}

	// COMPUTE TENS COMPLIMENT

	public static String computeTensComplement(String id) {
		StringBuffer sb = new StringBuffer(id);
		int idlen = sb.length();

		// Subtract every digit in id from 9
		for (int i = 0; i < idlen; i++) {
			int digit = 9 - Character.digit(sb.charAt(i), 10);
			char newChar = Character.forDigit(digit, 10);
			sb.setCharAt(i, newChar);
		}

		// The string of digits will likely be too big to build a
		// primitive int or its Integer wrapper. So use a BigInteger instead.
		BigInteger bi = new BigInteger(sb.toString(), 10);

		// Add 1 to that value
		bi = bi.add(BigInteger.ONE);

		// Get the String representation (using a StringBuffer) of the
		// BigInteger representing the 10's complement value.
		StringBuffer tensCompSb = new StringBuffer(bi.toString(10));

		int tensCompLen = tensCompSb.length();

		// Depending on the value of the BigInteger, adding 1 may have added
		// an extra character. Any carry-over should be discarded, so only
		// use the last idlen amount of characters.
		if (tensCompLen > idlen) {
			tensCompSb.substring(tensCompLen - idlen);
		}

		return tensCompSb.toString();
	}

	//FORMAT RECEIPT ID
	public static String formatReceiptID(String id) 
	{ 	
		StringBuffer sb = new StringBuffer(id); 
		int len = sb.length(); 
		int count = 1; 

		for (int i = len - 1; i >= 0; i--, count++) { 
			if (count % 4 == 0 && i > 0) { 
				sb.insert(i, '-'); 
			} 
		} 
		return sb.toString(); 
	}
	

	/** The Method makes an getCommonCodeList to fetch the Authorization Expiration Duration.
	 *  
	 *  @author Prashanth T G
	 *  
	 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.
	 * @throws YIFClientCreationException 

	 **/

	public static String getCommonCodeList(YFSEnvironment env,String sCodeType, String sCodeValue) throws YFSException, RemoteException, YIFClientCreationException {

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<--------- Begingin of getAuthExpiryDuration Method ------------------>");
		}

		String sCode = "";
		// TODO Auto-generated method stub
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, sCodeType);
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_VALUE, sCodeValue);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input for getOrderDetails : \n" + XMLUtil.getXMLString(yfcDocGetReturnCodeList.getDocument()));
		}

		env.setApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST, getCommonCodeListTemp());
		Document docGetCommonCodeOutputXML = new KohlsUtil().api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Output for getCommonCodeList : \n" + XMLUtil.getXMLString(docGetCommonCodeOutputXML));
		}

		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		for(int i=0;i<ndlstCommonCodeList.getLength();i++){

			Element eleCommonCode = (Element)ndlstCommonCodeList.item(i);
			sCode = eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION);		
		}

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<--------- End of getAuthExpiryDuration Method ------------------>");
		}
		return sCode;
	}


	public static String getNewDate(String sAuthTime, String sExpiryDuration){

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<--------- Begingin of getNewDate Method ------------------>");
		}

		YFCDate yfcAuthExpirationDate = null ;
		YFCDate yfcAuthTime = null;
		if(YFCLogUtil.isDebugEnabled()){							
			log.debug("The Auth Time is " + sAuthTime);
		}
		if(null!=sAuthTime && !sAuthTime.equalsIgnoreCase("")){
			yfcAuthTime = YFCDate.getYFCDate(sAuthTime);
			yfcAuthExpirationDate = yfcAuthTime.getNewDate(Integer.parseInt(sExpiryDuration));

			if(YFCLogUtil.isDebugEnabled()){							
				log.debug("The Auth Expiration Time is " + yfcAuthExpirationDate.getString(YFCDate.ISO_DATETIME_FORMAT));
			}

		}

		return yfcAuthExpirationDate.getString(YFCDate.ISO_DATETIME_FORMAT);
	}

	
	public static Document getCommonCodeListTemp(){

		YFCDocument yfcDocCommonCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE_LIST);
		YFCElement yfcEleCommonCodeList = yfcDocCommonCodeList.getDocumentElement();

		YFCElement yfcEleCommonCode = yfcDocCommonCodeList.createElement(KohlsXMLLiterals.E_COMMON_CODE);
		yfcEleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION, "");
		yfcEleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, "");
		yfcEleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_VALUE, "");
		yfcEleCommonCode.setAttribute(KohlsXMLLiterals.A_CODE_LONG_DESCRIPTION, "");
		yfcEleCommonCodeList.appendChild(yfcEleCommonCode);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getCommonCodeList Output Template \n" + XMLUtil.getXMLString(yfcDocCommonCodeList.getDocument()));
		}

		return yfcDocCommonCodeList.getDocument();
	}
	
	public static int calcMod10CheckDigit(String upcNum) { 
        int total = 0; 
        int temp = 0; 
        //Get sum of numbers in Odd positions 
        for (int i = 0; i < upcNum.length(); i+=2) { 
                temp += Integer.parseInt(upcNum.substring(i, i+1));               
        } 
        //Multiply the odd position sum by 3 
        total = temp * 3; 
        //Get sum of numbers at Even positions 
        for (int i = 1; i < upcNum.length(); i+=2) { 
            total += Integer.parseInt(upcNum.substring(i, i+1)); 
        } 
        //Determine the checksum 
        int checkDigit = (10 - (total % 10)) % 10; 
        
        return checkDigit; 
    } 
	
	public static Set<String> getEFCList(YFSEnvironment env) throws YFSException, RemoteException, YIFClientCreationException {		
		
		env.setApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST, getCommonCodeListTemp());
		Document docCommonCodeList = new KohlsUtil().api.getCommonCodeList(env, getCommonCodeInput(KohlsConstant.CODE_SHIP_NODES));
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);
		Set<String> lstEFCs = new HashSet<String>();		
		NodeList nlCommonCodeList = ((Element)docCommonCodeList.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE_LIST).item(0))
												.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		for(int i=0; i< nlCommonCodeList.getLength(); i++){
			Element elemCommonCode = (Element)nlCommonCodeList.item(i);
			lstEFCs.add(elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE));
		}
		return lstEFCs;
	}
	
/**
 * This method returns set of code value for provided  common code type
 * @param env
 * @param commonCodeType
 * @return
 * @throws YFSException
 * @throws RemoteException
 * @throws YIFClientCreationException
 */
public static Set<String> getCommonCodeValueList(YFSEnvironment env,String commonCodeType) throws YFSException, RemoteException, YIFClientCreationException {		
		
		env.setApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST, getCommonCodeListTemp());
		Document docCommonCodeList = new KohlsUtil().api.getCommonCodeList(env, getCommonCodeInput(commonCodeType/*KohlsConstant.CODE_SHIP_NODES*/));
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);
		Set<String> lstEFCs = new HashSet<String>();		
		NodeList nlCommonCodeList = ((Element)docCommonCodeList.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE_LIST).item(0))
												.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		for(int i=0; i< nlCommonCodeList.getLength(); i++){
			Element elemCommonCode = (Element)nlCommonCodeList.item(i);
			lstEFCs.add(elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE));
		}
		return lstEFCs;
	}


	public static Document getCommonCodeInput(String sCodeType) {
		YFCDocument yfcDocCommonCode = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		yfcDocCommonCode.getDocumentElement().setAttribute(KohlsXMLLiterals.A_CODE_TYPE, sCodeType);
		return yfcDocCommonCode.getDocument();
	}
	
	public static  String  getCommonCodeValue(YFSEnvironment env, String sCodeType) throws YFSException, RemoteException, YIFClientCreationException {		
		
		env.setApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST, getCommonCodeListTemp());
		Document docCommonCodeList = new KohlsUtil().api.getCommonCodeList(env, getCommonCodeInput(sCodeType));
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);		 	
		Element  eleCommonCode = (Element) ((Element)docCommonCodeList.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE_LIST).item(0))
												.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE).item(0);
		 
		return eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE);
	}
	/** Gets the current system date to be used in xsl
	 *
	 * @return the current system date
	 */
	public static String getCurrSysDate() {
		
		YFCDate yfcCurrntDt;
		String str;
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.INV_DATE_FORMAT);
		yfcCurrntDt = YFCDateUtils.getCurrentDate(true);
		str = sdf.format(yfcCurrntDt);
		return str;
	}
	
	public static String getEFCForStore(String sShipNode, YFSEnvironment env) throws YFSException, RemoteException, YIFClientCreationException{
		String sEFCForStore = "";
		env.setApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST, KohlsUtil.getCommonCodeListTemp());
		Document docCommonCodeList = new KohlsUtil().api.getCommonCodeList(env, KohlsUtil.getCommonCodeInput("StoreToEFCRltn"));
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);
		Set<String> lstEFCs = new HashSet<String>();		
		NodeList nlCommonCodeList = ((Element)docCommonCodeList.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE_LIST).item(0))
												.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		for(int i=0; i< nlCommonCodeList.getLength(); i++){
			Element elemCommonCode = (Element)nlCommonCodeList.item(i);
			if(elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE).equals(sShipNode)){
				sEFCForStore = elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION);
				break;
			}
		}
	
		if("".equals(sEFCForStore)){
			for(int i=0; i< nlCommonCodeList.getLength(); i++){
				Element elemCommonCode = (Element)nlCommonCodeList.item(i);
				if(elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE).equals("DEFAULT")){
					sEFCForStore = elemCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_SHORT_DESCRIPTION);
}
			}
		}
		
		return sEFCForStore;
	}
	
	public static String getNodeType(String sShipNode, YFSEnvironment env) throws YFSException, RemoteException, YIFClientCreationException {
		YFCDocument yfcInDocGetOrgList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORGANIZATION);
		yfcInDocGetOrgList.getDocumentElement().setAttribute(KohlsConstant.ATTR_IS_NODE,KohlsConstant.YES);
		yfcInDocGetOrgList.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE,sShipNode);
		
		YFCDocument yfctemplateDocGetOrgList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORGANIZATION);
		YFCElement yfctemplateEleGetOrgList = yfctemplateDocGetOrgList.getDocumentElement();
		yfctemplateEleGetOrgList.createChild(KohlsXMLLiterals.A_NODE);
		
		env.setApiTemplate(KohlsConstant.API_GET_ORGANIZATION_LIST, yfctemplateDocGetOrgList.getDocument());
		
		Document docOutputGetOrganizationList = new KohlsUtil().api.getOrganizationList(env, yfcInDocGetOrgList.getDocument());
		YFCElement eleOrgList = YFCDocument.getDocumentFor(docOutputGetOrganizationList).getDocumentElement();
		String sNodeType = eleOrgList.getChildElement(KohlsXMLLiterals.E_ORGANIZATION).getChildElement(KohlsXMLLiterals.A_NODE).getAttribute("NodeType");
		env.clearApiTemplate(KohlsConstant.API_GET_ORGANIZATION_LIST);
		return sNodeType;
	}
	
	
}
