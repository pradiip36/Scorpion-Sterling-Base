package com.kohls.common.util;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.bopus.util.KOHLSCommonCodeList;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsInvoiceUtil {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInvoiceUtil.class.getName());

	private static YIFApi api;
	public KohlsInvoiceUtil() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getLocalApi();

	}

	public static List<Document> getJobsList(YFSEnvironment env, Document InXML,
			Document lastMessageCreated, String transactionID, YIFApi api) throws Exception {
		if (null != lastMessageCreated) {
			return null;
		}
		List<Document> lstTask = new ArrayList<Document>();
		YFCElement eleRoot = YFCDocument.getDocumentFor(InXML).getDocumentElement();
		String sMaxRecord = eleRoot.getAttribute(KohlsXMLLiterals.A_MAX_RECS);
		String sTaskQInterval = eleRoot.getAttribute(KohlsXMLLiterals.A_NEXT_TASK_Q_INTERVAL);
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### MaxRecord ##########"+ sMaxRecord);
		}
		String strLastTaskQKey = "";
		// fetches first sMaxRecord number of records
		Document docGetTask = getTaskQDataList(env, sMaxRecord, strLastTaskQKey, transactionID, api);
		NodeList nlTaskQ = docGetTask.getElementsByTagName(KohlsXMLLiterals.E_TASK_Q);
		// fetches all eligible records by looping,  sMaxRecord number in an iteration
		while(nlTaskQ != null && nlTaskQ.getLength()!=0){				
			Element eleTaskQ = null;
			for (int i = 0; i < nlTaskQ.getLength(); i++) {
				eleTaskQ = (Element) nlTaskQ.item(i);
				eleTaskQ.setAttribute(KohlsXMLLiterals.A_NEXT_TASK_Q_INTERVAL, sTaskQInterval);
				Document doc = XMLUtil.getDocumentForElement(eleTaskQ);
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("######### record added  ##########"
							+ KohlsUtil.extractStringFromNode(doc));
				}
				lstTask.add(doc);
			}
			strLastTaskQKey = eleTaskQ.getAttribute(KohlsXMLLiterals.A_TASK_Q_KEY);
			docGetTask = getTaskQDataList(env, sMaxRecord, strLastTaskQKey, transactionID, api);
			nlTaskQ = docGetTask.getElementsByTagName(KohlsXMLLiterals.E_TASK_Q);
		}

		return lstTask;
	}


	private static Document getTaskQDataList(YFSEnvironment env, String sMaxRecord, String strLastTaskQKey, String transactionID, YIFApi api)
			throws Exception {
		YFCDocument yfcDocGetTask = YFCDocument
				.createDocument(KohlsXMLLiterals.E_GET_TASK_Q_DATA_INPUT);
		YFCElement yfcEleGetTask = yfcDocGetTask.getDocumentElement();
		yfcEleGetTask.setAttribute(KohlsXMLLiterals.A_MAX_RECS, sMaxRecord);
		yfcEleGetTask.setAttribute(KohlsXMLLiterals.A_LAST_TASK_Q_KEY, strLastTaskQKey);
		yfcEleGetTask.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, transactionID);		
		Document docGetTask = api.getTaskQueueDataList(env,
				yfcDocGetTask.getDocument());
		return docGetTask;
	}

	public static void manageTaskQ(YFSEnvironment env, Document InXML, YIFApi api) {
		try {
			api.manageTaskQueue(env, InXML);
		} catch (YFSException e1) {
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("####### Exception in manageTaskQueue #######"+ e1.getMessage());
			}
		} catch (RemoteException e1) {
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("####### Exception in manageTaskQueue #######"+ e1.getMessage());
			}
		}
	}


	public static String processInvoice(YFSEnvironment env, Document InXML, YIFApi api)
			throws RemoteException, TransformerException, YFSException, YIFClientCreationException {
		// get all the EFCs in the system
		Set<String> setEFCs = KohlsUtil.getEFCList(env);
		Map<String, String> mapOrderLineKeyDeliveryMethod = new HashMap<String, String>();		
		String strOrderHeaderKey = InXML.getDocumentElement().getAttribute(KohlsXMLLiterals.A_DATA_KEY);
		log.debug("**** orderheaderkey ******  " + strOrderHeaderKey);
		Map<String, Document> mapEFCInvoiceXML = new HashMap<String, Document>();	
		String strOrderStatus = null;		
		boolean isBOPUS = false;
		boolean isReadyForInvoiceCreation = false;
		
		
		String strShipNode = null;
		//Added by Baijayanta for Defect 333
		String returnStr = null;
		//Ended by Baijayanta for Defect 333

		// get Order Details
		Document docOrderDetails = getOrderDetails(env, strOrderHeaderKey, api);	
		log.debug("***** Order Details XML is *****  " + XMLUtil.getXMLString(docOrderDetails));
		Element elemOrderLines = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlOrderLines = elemOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int j=0; j < nlOrderLines.getLength(); j++){
			Element elemOrderLine = (Element)nlOrderLines.item(j);
			mapOrderLineKeyDeliveryMethod.put(elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY), elemOrderLine.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD));
		}
		Element elemOrderStatuses = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUSES).item(0);
		NodeList nlOrderStatus = elemOrderStatuses.getElementsByTagName(KohlsXMLLiterals.E_ORDER_STATUS);
		for(int i=0; i < nlOrderStatus.getLength(); i++){
			Element elemOrderStatus = (Element)nlOrderStatus.item(i);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### elemOrderStatus ##########"+ KohlsUtil.extractStringFromNode(elemOrderStatus));				
			}					

			//check for BOPUS order line
			if(mapOrderLineKeyDeliveryMethod.get(elemOrderStatus.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY)).equalsIgnoreCase(KohlsConstant.PICK))
			{
				isBOPUS=true;  
				//strOrderStatus=KohlsConstant.PLACED_IN_HOLD_LOCATION;
				try {
					strOrderStatus=KOHLSCommonCodeList.statusValue(env,KohlsConstant.CC_DESC_PLACED_IN_HOLD_LOCATION, KohlsConstant.CODE_TYPE_ORD);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}else{
				isBOPUS=false;  
				strOrderStatus=KohlsConstant.AWAITING_INVOICE_CREATION;		
			}


			// if status is  not Placed In Hold Location, continue 
			if(!elemOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS).equals(strOrderStatus)){
				continue;
			}
			strShipNode = elemOrderStatus.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
			log.debug("**** Ship Node on the Order Status element is ******" + strShipNode);

			// Added by Baijayanta for Defect 581
			
			
			if(isBOPUS) {
				isReadyForInvoiceCreation = false;
				Document getShipmentListforShipNode = null;
				NodeList alist = null;
				Set setA = new HashSet();
				getShipmentListforShipNode = getShipmentListForNode(env, strOrderHeaderKey,strShipNode , api);
				Element egetShipmentListforShipNode = getShipmentListforShipNode.getDocumentElement();
				alist = getShipmentListforShipNode.getElementsByTagName("Shipment");
				
				if(alist.getLength() == 1) {
					isReadyForInvoiceCreation = true;
				} else if(alist.getLength() > 1) {
					for(int m= 0 ; m < alist.getLength(); m++) {
						Element eShipment = (Element)alist.item(m);
						String eStatus = eShipment.getAttribute("Status");
						if(!"9000".equalsIgnoreCase(eStatus)) {
							setA.add(eStatus);
						}

					}
					if(setA.size() == 1) {
						isReadyForInvoiceCreation = true;
					}
					
				} 
				
			}
			
	
			//Ended by Baijayanta for Defect 581

			String sNodeType = KohlsUtil.getNodeType(strShipNode, env);

			log.debug("***** Node Type obtained is *****" + sNodeType);


			boolean isnodeRDC = false;
			isnodeRDC = KohlsConstant.ATTR_RDC.equals(sNodeType) || KohlsConstant.ATTR_RDC_STORE.equals(sNodeType);

			log.debug("***** Is this node a RDC *****" + isnodeRDC);


			//Punit: changed for Omni Pilot, else condition to send 873 instead of actual store.
			if("DSV".equals(sNodeType)){
				strShipNode = KohlsConstant.EFC1;
			}
			else if(KohlsConstant.ATTR_STORE.equals(sNodeType)){
				log.debug("***** Node Type is STORE *****"+sNodeType);

				//fixed for drop2 : settlement - Start
				//strShipNode = KohlsUtil.getEFCForStore(strShipNode, env);
				//log.debug("**** Ship Node is ******" + strShipNode);
				//fixed for drop2 : settlement - End
			}else if(isnodeRDC){
				log.debug("***** Node Type is RDC *****");

				strShipNode = KohlsUtil.getEFCForStore(strShipNode, env);				
				log.debug("**** Ship Node is ******" + strShipNode);

			}else if(isDSVShipment(setEFCs, strShipNode)){
				// for PO, group  invoice by node 873
				strShipNode = KohlsConstant.EFC1;
			} 
			Document docInvoiceXML = mapEFCInvoiceXML.get(strShipNode);
			if(docInvoiceXML == null){
				docInvoiceXML = getCreateOrderInvoiceInputXML(strOrderHeaderKey, isBOPUS);
				// Added By Asha: Fis for Defect 581
				if(isBOPUS){
					String invoiceCreate = String.valueOf(isReadyForInvoiceCreation);
					docInvoiceXML.getDocumentElement().setAttribute(KohlsXMLLiterals.IS_INVOICE_CREATION, invoiceCreate);
				}
				//End: Fix for defect 581
				docInvoiceXML.getDocumentElement().setAttribute("ShipNode", strShipNode);			
				mapEFCInvoiceXML.put(strShipNode, docInvoiceXML);
				log.debug("***document getting formed*****" + XMLUtil.getXMLString(docInvoiceXML));

			}
			// update create invoice XML with order line key and qty
			updateInvoiceXML(docInvoiceXML, elemOrderStatus);	
		}
		
		//added by Baijayanta for Defect 333
		boolean bCashActivationHoldExist = false;
		NodeList ndlstOrderHoldTypes = docOrderDetails.getDocumentElement().getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);

		for(int i=0;i<ndlstOrderHoldTypes.getLength();i++){
			Element eleOrderHoldType = (Element)ndlstOrderHoldTypes.item(i);
			String sHoldType = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
			String sHoldStatus = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);
			if(KohlsConstant.CASH_ACTIVATION_HOLD.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
				bCashActivationHoldExist = true ;
			}	
				
		}
		//Ended by Baijayant for Defect 333
		// create all the invoices at order level
		//Added by Baijayant for Defect 333 - passing bCashActivationHoldExist as a boolean parameter to the method
		//Updated by Asha: Removed the parameter isReadyForInvoiceCreation to the method(Defect 581)
		boolean isInvoicedStat = createNChangeOrderInvoices(env, mapEFCInvoiceXML, api, bCashActivationHoldExist);
		//End: Defect 581
		// update Order Status to 'Invoiced'  
		//changeOrderStatusToInvoiced(env, strOrderHeaderKey, api);

		//BOPUS Drop 4: change the Status of the Shipment to Ready For Customer on invoice creation
		//Commented by Asha: Fix for defect 581
		/*if(isInvoicedStat) {
			invokeChangeShipmentStatusAPI(env,strShipNode,strOrderHeaderKey);
			
		}*/
		//End: Defect 581
        
		
			log.debug("returnString is:" + returnStr );
			returnStr = strOrderHeaderKey + "=" + isInvoicedStat;
			
			return returnStr;
			
	}

	private static Document getOrderDetails(YFSEnvironment env,
			String strOrderHeaderKey, YIFApi api) throws YFSException, RemoteException {
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemplate());
		Document docOrderDetials = api.getOrderDetails(env, getOrderDetailsInput(strOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);		
		return docOrderDetials;		
	}

	private static Document getOrderDetailsInput(String strOrderHeaderKey) {
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		return yfcDocOrder.getDocument();
	}

	private static Document getOrderDetailsTemplate() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		YFCElement yfcElemOrdereStatuses = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_STATUSES);
		YFCElement yfcElemOrdereStatus = yfcElemOrdereStatuses.createChild(KohlsXMLLiterals.E_ORDER_STATUS);
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, ""); 
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_STATUS, ""); 
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY, ""); 
		yfcElemOrdereStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");

		YFCElement yfcElemOrdereLines = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrdereLine = yfcElemOrdereLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcElemOrdereLine.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");
		
		//Added by Baijayanta for Defect 333
		YFCElement yfcEleOrderHoldTypes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderHoldTypes.appendChild(yfcEleOrderHoldType);
		yfcElemOrderDetailsTemp.appendChild(yfcEleOrderHoldTypes);
		//Ended by Baijayanta for Defect 333
		return yfcDocGetOrderDetailsTemp.getDocument();
	}


	/*private static void createNChangeOrderInvoices(YFSEnvironment env,
			Map<String, Document> mapEFCInvoiceXML, YIFApi api , boolean bCashActivationHoldExist) throws YFSException, RemoteException, TransformerException {*/
	private static boolean createNChangeOrderInvoices(YFSEnvironment env,
				Map<String, Document> mapEFCInvoiceXML, YIFApi api , boolean bCashActivationHoldExist) throws YFSException, RemoteException, TransformerException {
		boolean isBOPUS= false;
		//Added by Baijayanta for Defect 333
		boolean isInvoiced = false;
		//Ended by Baijayanta for Defect 333
		Set<String> setShipNode = mapEFCInvoiceXML.keySet();
		Iterator<String> it = setShipNode.iterator();
		while (it.hasNext()) {
			log.debug("****ShipNode is******" + it.next());		    
		}

		for(String sShipNode : setShipNode){
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### in createOrderInvoices  ##########"+ 
						KohlsUtil.extractStringFromNode(mapEFCInvoiceXML.get(sShipNode)));				 
			}
			// create Invoice per EFC
			//Added by Baijayanat for Defect 217
			Document docOrderDetialsPerShip = mapEFCInvoiceXML.get(sShipNode);
			String strTransactionId = docOrderDetialsPerShip.getDocumentElement().getAttribute(KohlsXMLLiterals.A_TRANSACTIONID);

			if(KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX.equals(strTransactionId)) {
				isBOPUS=true;				
				log.verbose("Setting the BOPUS Invoice Indicator");
				env.setTxnObject("IsBopusInvoice", "Y");
			}
			//Added by Baijayanta for Defect 333
			//if(!(isBOPUS && bCashActivationHoldExist)) {
		if(isBOPUS) {
				//Added by Asha: Fix for Defect 581
				Document invoiceXml=mapEFCInvoiceXML.get(sShipNode);
				String strOrderHeaderKey=invoiceXml.getDocumentElement().getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
				String strIsInvoiceCreation=invoiceXml.getDocumentElement().getAttribute(KohlsXMLLiterals.IS_INVOICE_CREATION);
				boolean isReadyForInvoiceCreation = Boolean.valueOf(strIsInvoiceCreation);
				//End :Fix for defect 581
				if(!bCashActivationHoldExist && isReadyForInvoiceCreation) {
					//Added by Asha: Fix for defect 581
					invoiceXml.getDocumentElement().removeAttribute(KohlsXMLLiterals.IS_INVOICE_CREATION);
					Document docOrderInvoiceOutput = api.createOrderInvoice(env,invoiceXml);
					//End: Defect 581
					/*if(KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX.equals(strTransactionId)) {*/
					env.setTxnObject("IsBopusInvoice", " ");
					/*}*/

					if(docOrderInvoiceOutput != null){
					
						Element elemInvoice = (Element)docOrderInvoiceOutput.getElementsByTagName(KohlsXMLLiterals.E_ORDER_INVOICE).item(0);				
						String sOrderInvoiceKey = elemInvoice.getAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY);
						// store corresponding EFC value in yfs_order_invoice 
						String  strShipNode = mapEFCInvoiceXML.get(sShipNode).getDocumentElement().getAttribute("ShipNode");
	
						api.changeOrderInvoice(env, getChangeOrderInvoiceInpt(sOrderInvoiceKey,strShipNode, isBOPUS ));
						
						
					}
					
					isInvoiced = true;
					//Added by Asha: Fix for defect 581
					invokeChangeShipmentStatusAPI(env,sShipNode,strOrderHeaderKey);
					//End: Fix for defect 581
				}
				
			} else {
				if(!bCashActivationHoldExist) {
					Document docOrderInvoiceOutput = api.createOrderInvoice(env, mapEFCInvoiceXML.get(sShipNode));
					if(docOrderInvoiceOutput != null){
					
						Element elemInvoice = (Element)docOrderInvoiceOutput.getElementsByTagName(KohlsXMLLiterals.E_ORDER_INVOICE).item(0);				
						String sOrderInvoiceKey = elemInvoice.getAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY);
						// store corresponding EFC value in yfs_order_invoice 
						String  strShipNode = mapEFCInvoiceXML.get(sShipNode).getDocumentElement().getAttribute("ShipNode");
	
						api.changeOrderInvoice(env, getChangeOrderInvoiceInpt(sOrderInvoiceKey,strShipNode, isBOPUS ));
						
						
					}
					
					isInvoiced = true;
			
				}
				
			}
		}
		
		return isInvoiced;

	}

	private static Document getChangeOrderInvoiceInpt(String strOrderInvoiceKey,
			String sShipNode, boolean isBOPUS) {
		YFCDocument yfcDocChangeOrderInvoice = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_INVOICE);
		YFCElement yfcElemChangeOrderInvoice =  yfcDocChangeOrderInvoice.getDocumentElement();
		yfcElemChangeOrderInvoice.setAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY, strOrderInvoiceKey);
		YFCElement yfcElemExtn = yfcElemChangeOrderInvoice.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_NODE, sShipNode);		
		if(isBOPUS)
			yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_OCF, KohlsConstant.BPS);
		return yfcDocChangeOrderInvoice.getDocument();
	}

	private static void updateInvoiceXML(Document docInvoiceXML,
			Element elemOrderStatus) {
		Element elemExistingOrderLines = (Element)docInvoiceXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlExisitngOrderLine = docInvoiceXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		// create a map of already existing orderlinekey and its OrderLine 
		Map<String, Element> mapExisitngOrderLinekeys = new HashMap<String, Element>();
		for(int i = 0; i< nlExisitngOrderLine.getLength(); i++){
			Element elemExisitingOrderLine  = (Element) nlExisitngOrderLine.item(i);
			mapExisitngOrderLinekeys.put(elemExisitingOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY),
					elemExisitingOrderLine);
		}
		Set<String> setExistingOrderLineKeys = mapExisitngOrderLinekeys.keySet();
		String sInpOrderLineKey = elemOrderStatus.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
		String sInpQuantity = elemOrderStatus.getAttribute(KohlsXMLLiterals.A_STATUS_QTY);
		// check if input orderlinekey already exists, if yes, add its quantity to the existing 
		if(setExistingOrderLineKeys.contains(sInpOrderLineKey)){
			Element eleExistOrderLine = mapExisitngOrderLinekeys.get(sInpOrderLineKey);
			String sExisitngQuantity = eleExistOrderLine.getAttribute(KohlsXMLLiterals.A_QUANTITY);
			double dExisitngQuantity =  Double.parseDouble(sExisitngQuantity);
			double dInpQuantity =  Double.parseDouble(sInpQuantity);
			eleExistOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, 
					Double.toString(dExisitngQuantity+dInpQuantity));

		}else{
			// else create a new orderline
			Element elemOrderLine = docInvoiceXML.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			elemExistingOrderLines.appendChild(elemOrderLine);
			elemOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, 
					sInpQuantity);
			elemOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, 
					sInpOrderLineKey);
		}
	}

	private static boolean isDSVShipment(Set<String> lstEFCs, String strShipNode) {
		return !lstEFCs.contains(strShipNode);		
	}

	private static Document getCreateOrderInvoiceInputXML(String strOrderHeaderKey, boolean isBOPUS) {
		String strTransactionId = null;

		if(isBOPUS){
			strTransactionId=KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX;
		}else{
			strTransactionId=KohlsConstant.TRAN_ID_ORDER_INVOICE_0001_EX;
		}
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrder = yfcDocOrder.getDocumentElement();
		yfcElemOrder.setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		yfcElemOrder.setAttribute(
				KohlsXMLLiterals.A_IGNORE_STATUS_CHECK, KohlsConstant.YES);
		yfcElemOrder.setAttribute(
				KohlsXMLLiterals.A_TRANSACTIONID, strTransactionId);	
		yfcElemOrder.createChild(KohlsXMLLiterals.E_ORDER_LINES);		
		return yfcDocOrder.getDocument();
	}

	
	/**
	 * 
	 * This methods invokes getShipmentList API to
	 * get the corresponding shipment No for the ship Node. 
	 * This Shipment No is used to invoke the changeShipmentStatus API 
	 * for that Shipment.
	 * 
	 * @author Ashalatha
	 * 
	 */
	
	public static void invokeChangeShipmentStatusAPI(YFSEnvironment env, String strShipNode, String strOrderHeaderKey) {
		Map<String, String> mapShipmentKeyShipNode = new HashMap<String, String>();	
		
		try{
			
			YFCDocument yfcDocGetShipmentListInXML = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
			YFCElement yfcEleGetShipmentListInXML = yfcDocGetShipmentListInXML.getDocumentElement();
			yfcEleGetShipmentListInXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, KohlsConstant.KOHLS_ENTERPRISE_CODE);
			yfcEleGetShipmentListInXML.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY , strOrderHeaderKey);
			yfcEleGetShipmentListInXML.setAttribute(KohlsXMLLiterals.E_SHIP_NODE , strShipNode);
			
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### getShipmentList API Input  ##########"+ XMLUtil.getXMLString(yfcDocGetShipmentListInXML.getDocument()));				 
			}
			
			
			Document docGetShipmentListInXML = yfcDocGetShipmentListInXML.getDocument();
			Document docGetShipmentListOutXML = KohlsCommonUtil.invokeAPI(env, KohlsConstant.GET_SHIPMENT_LIST_API, docGetShipmentListInXML);
			
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### getShipmentList API Output  ##########"+ XMLUtil.getXMLString(docGetShipmentListOutXML));				 
			}
			
			Element eleShipments = (Element) docGetShipmentListOutXML.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENTS).item(0);
			NodeList nlShipment =  eleShipments.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);

			for(int i = 0; i < nlShipment.getLength(); i++){
				Element eleShipment = (Element) nlShipment.item(i);
				String strShipmentNo = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO);
				String strStatus = eleShipment.getAttribute(KohlsXMLLiterals.A_STATUS);				
				//if(strStatus.equalsIgnoreCase(KohlsConstant.STATUS_PLACED_IN_HOLD_AREA)){
				if(strStatus.equalsIgnoreCase(KOHLSCommonCodeList.statusValue(env,KohlsConstant.CC_DESC_PLACED_IN_HOLD_LOCATION, KohlsConstant.CODE_TYPE_SHP))){
					mapShipmentKeyShipNode.put(strShipNode, strShipmentNo);
					ChangeShipmentStatus(env, mapShipmentKeyShipNode);
					
					ChangeOrderLineStatus(env, mapShipmentKeyShipNode);
				}
			}

			//call changeShipmentStuats API 
//			ChangeShipmentStatus(env, mapShipmentKeyShipNode);
//			
//			ChangeOrderLineStatus(env, mapShipmentKeyShipNode);

		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method invokeChangeShipmentStatusAPI : "+e.getStackTrace());
		}

	}
	
	
	/**
	 * 
	 * This method invokes changeShipment Status API using
	 * the ShipmentNo. Shipment Status changes to Ready For Customer
	 * 
	 *  @author Ashalatha
	 * 
	 */
	private static void ChangeShipmentStatus(YFSEnvironment env,Map<String, String> mapShipmentKeyShipNode) {
		try{
			Set<String> setShipNode = mapShipmentKeyShipNode.keySet();
			Iterator<String> it = setShipNode.iterator();
			while (it.hasNext()) {
				log.debug("****ShipNode in ChangeShipmentStatus method is******" + it.next());		    
			}

			for(String sShipNode : setShipNode){

				String strShipmentNo = mapShipmentKeyShipNode.get(sShipNode);
				YFCDocument yfcDocChangeShipmentStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
				YFCElement yfcEleChangeShipmentStatus = yfcDocChangeShipmentStatus.getDocumentElement();
				yfcEleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE,KohlsConstant.KOHLS_OrganizationCode);
				yfcEleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE , sShipNode);
				yfcEleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
				yfcEleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID ,KohlsConstant.TRAN_ID_CUSTOMER_PICK_0001_EX);

				Document DocChangeShipmentStatus = yfcDocChangeShipmentStatus.getDocument();
				
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("######### changeShipmentStatus API Input  ##########"+ XMLUtil.getXMLString(DocChangeShipmentStatus));				 
				}

				KohlsCommonUtil.invokeAPI(env, KohlsConstant.API_CHANGE_SHIPMENT_STATUS, DocChangeShipmentStatus);

			}
		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method ChangeShipmentStatus : "+e.getStackTrace());
		}


	}
		
	private static void ChangeOrderLineStatus(YFSEnvironment env,Map<String, String> mapShipmentKeyShipNode) {
		try{
			
			String strOrderHeaderKey="";
			String strOrderLineKey="";			
			String PrimeLineNo = "";
			String SubLineNo = "";
			double quantity=0.0;
			String OrderReleaseKey="";
			Document inChangeOrderLineDoc = null;
			Element eChangeOrderLineInput = null;
			Element elschangeOrderLines = null;
			
			Set<String> setShipNode = mapShipmentKeyShipNode.keySet();
			Iterator<String> it = setShipNode.iterator();
			while (it.hasNext()) {
				log.debug("****ShipNode in ChangeOrderLineStatus method is******" + it.next());		    
			}

			for(String sShipNode : setShipNode){
				
				String strShipmentNo = mapShipmentKeyShipNode.get(sShipNode);
				Document inputShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();				
				Element ele =  inputShipmentDoc.getDocumentElement();
				ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
								
				log.debug("Input to getshipmentList API "+SCXmlUtil.getString(inputShipmentDoc));
				
				Document outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS, 
						KohlsConstants.API_GET_SHIPMENT_LIST, inputShipmentDoc);			
				
				
				Element eleShipment = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment");
				if (!YFCCommon.isVoid(eleShipment)) {
					OrderReleaseKey = eleShipment.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);					
				}
				
				Element eleReadShipLines = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment/ShipmentLines");	
				NodeList nlReadShipmentLine = eleReadShipLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);	
				
				for(int l=0; l<nlReadShipmentLine.getLength(); l++){
					Element eleShipLine = (Element)nlReadShipmentLine.item(l);	
					strOrderHeaderKey = eleShipLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);	
					quantity = Double.parseDouble(eleShipLine.getAttribute(KohlsXMLLiterals.A_QUANTITY));
					//Added by Asha : Fix for Invoice creation Issue found during Shipment Consolidation
					String strOrderReleaseKey = eleShipLine.getAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY);	
					//End
					Element eleOrder = SCXmlUtil.getChildElement(eleShipLine, KohlsXMLLiterals.E_ORDER_LINE);								
					strOrderLineKey = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);	
					PrimeLineNo = eleOrder.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);	
					SubLineNo = eleOrder.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO);
					if(l==0){
						inChangeOrderLineDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE).getDocument();
						eChangeOrderLineInput = inChangeOrderLineDoc.getDocumentElement();
						eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
						eChangeOrderLineInput.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX);
						elschangeOrderLines = SCXmlUtil.createChild(eChangeOrderLineInput, KohlsXMLLiterals.E_ORDER_LINES);
					}							
					
					Element elschangeOrderLine = SCXmlUtil.createChild(elschangeOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strOrderLineKey);			
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, PrimeLineNo);
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, SubLineNo);
					elschangeOrderLine.setAttribute("ChangeForAllAvailableQty","N");
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, String.valueOf(quantity));
					//Added by Asha : Fix for Invoice creation Issue found during Shipment Consolidation
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_RELEASE_KEY, strOrderReleaseKey);
					//End
					//elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.SHIPMENT_READY_FOR_CUSTOMER_PICK_UP);
					elschangeOrderLine.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KOHLSCommonCodeList.statusValue(env,KohlsConstant.CC_DESC_READY_FOR_CUSTOMER, KohlsConstant.CODE_TYPE_ORD));
				}		
				
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("######### ChangeOrderLineStatus API Input  ##########"+ XMLUtil.getXMLString(inChangeOrderLineDoc));				 
				}
				
				KohlsCommonUtil.invokeAPI(env, "changeOrderStatus", inChangeOrderLineDoc);

			}		
	
		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method ChangeOrderLineStatus : "+e.getStackTrace());
		}
	}
	
	private static Document getShipmentListForNode(YFSEnvironment env,String OrderHeaderKey, String shipnode,YIFApi api) throws YFSException, RemoteException {
		
		YFCDocument yfcDocOrder = YFCDocument.createDocument("Shipment");
		yfcDocOrder.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, OrderHeaderKey);
		yfcDocOrder.getDocumentElement().setAttribute(
				"ShipNode", shipnode);
		
		yfcDocOrder.getDocumentElement().setAttribute(
				"DeliveryMethod", "PICK");
		Document getShipmentListDoc = api.getShipmentList(env, yfcDocOrder.getDocument());
		log.debug("the document is:"+ SCXmlUtil.getString(getShipmentListDoc));
		return getShipmentListDoc;
		
	}
		
	
}