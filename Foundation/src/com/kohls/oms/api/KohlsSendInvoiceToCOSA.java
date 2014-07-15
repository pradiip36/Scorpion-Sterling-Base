package com.kohls.oms.api;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.is.co.calendar.CalendarType;
import com.kohls.is.co.calendar.KohlsCalendarException;
import com.kohls.is.co.calendar.KohlsCalendarImplementation;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is used for adding additional attributes to the 
 * Invoice XML sent to COSA.
 * It is called by KohlsSendInvoiceSyncService
 * @author Priyadarshini  
 *
 */
public class KohlsSendInvoiceToCOSA implements YIFCustomApi {

	
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsSendInvoiceToCOSA.class.getName());
	private  YIFApi api;

	public KohlsSendInvoiceToCOSA() throws YIFClientCreationException{
	
			api = YIFClientFactory.getInstance().getLocalApi();
		
	}

	public Document modifyInvoice(YFSEnvironment env, Document inXML)
			throws YFSException, TransformerException, ParserConfigurationException, SAXException, IOException, YIFClientCreationException {
		
		

		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemExtn = (Element) elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String sEFC = elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SHIP_NODE);
		Element elemOrder = (Element)elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		String sOrderHeaderKey = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		Document docShipmentList = getShipmentList(env, sOrderHeaderKey);		
			
		String extnShipNode  = elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SHIP_NODE);
		if(extnShipNode != null){
			elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_NODE_TYPE, KohlsUtil.getNodeType(extnShipNode, env));
		}
		
		Document docGetOrderDetials = getOrderDetails(env, inXML);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("########### In modifyInvoice, order details xml ##################"+ 
				KohlsUtil.extractStringFromDocument(docGetOrderDetials));
		}
		appendExtnToInvoiceHeader(env, inXML, docGetOrderDetials, docShipmentList);
		appendExtnToInvoiceOrderHeader(env, inXML);
		appendExtnToInvoiceLineHeader(env, inXML, docGetOrderDetials, sEFC);
		// process Kohl's Cash Coupon
		processKohlsCashCoupon(env, inXML);	
		// adds credit card info if only gift card is available in the invoice
		// this is only informational required for COSA
		addZeroChargeCreditCard(env, inXML, docGetOrderDetials);
		
		addRef1Ref2(docGetOrderDetials, inXML);
		return inXML;
	}

	
	private void addRef1Ref2(Document docGetOrderDetials, Document inXML) {
		
		Map<String, String> mapChrgTransactionKeyRef1 = new HashMap<String, String>();
		Map<String, String> mapChrgTransactionKeyRef2 = new HashMap<String, String>();
		
		Element elemChargeTransactionDetails = (Element) docGetOrderDetials.
										getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS).item(0);		
		NodeList nlChargeTransactionDetail =  elemChargeTransactionDetails.
								getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
		// create a Map of transaction key and reference1,reference2
		for(int j=0; j < nlChargeTransactionDetail.getLength(); j++){
			Element elemChargeTransactionDetail = (Element) nlChargeTransactionDetail.item(j);
			if(!elemChargeTransactionDetail.getAttribute(KohlsXMLLiterals.A_CHARGE_TYPE).equals(KohlsConstant.AUTHORIZATION)){
				continue;
			}
			//added by Baijayanta
			String sAuthId = elemChargeTransactionDetail.getAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID);
			String sAuthorizationId = "";
			String[] tempTran = sAuthorizationId.split("=");
			if (tempTran.length == 2)
				sAuthorizationId = tempTran[0];
			else 
				sAuthorizationId = sAuthId;
			//modification by Baijayanta ends
			Element elemCreditCardTransactions = (Element) elemChargeTransactionDetail.
												getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTIONS).item(0);
			NodeList nlCreditCardTransaction =   elemCreditCardTransactions.
													getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION);
			if(nlCreditCardTransaction == null){
				continue;
			}
			
			Element elemCreditCardTransaction = (Element) nlCreditCardTransaction.item(0);
			if(elemCreditCardTransaction == null ){
				continue;
			}
			String sRef1 = elemCreditCardTransaction.getAttribute(KohlsXMLLiterals.A_REFERENCE1);
			String sRef2 = elemCreditCardTransaction.getAttribute(KohlsXMLLiterals.A_REFERENCE2);
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("########### In addRef1Ref2  sAuthorizationId in order details ##################"
						+ sAuthorizationId);
				log.debug("########### In addRef1Ref2   Ref1 ##################"
						+ sRef1);
				log.debug("########### In addRef1Ref2   Ref2 ##################"
						+ sRef2);
			}
			mapChrgTransactionKeyRef1.put(sAuthorizationId, sRef1);
			mapChrgTransactionKeyRef2.put(sAuthorizationId, sRef2);				
			
		}		
		// Add reference1,reference2

		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemCollectionDetails = (Element) elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAILS).item(0);
		NodeList nlCollectionDetail  = elemCollectionDetails.getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAIL);
		NodeList nlCreditCardTransaction = null;

		for(int i =0; i< nlCollectionDetail.getLength(); i++){

			Element elemCollectionDetail = (Element) nlCollectionDetail.item(i);
			NodeList ndlstCreditCardTransactions = elemCollectionDetail.getElementsByTagName
															(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTIONS);
			if(ndlstCreditCardTransactions.getLength() == 0){
				continue;
			}
			Element elemCreditCardTransactions = (Element) elemCollectionDetail.
			getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTIONS).item(0);		
			
			if(!elemCreditCardTransactions.hasChildNodes()){
				continue;			
			}
			
			nlCreditCardTransaction =   elemCreditCardTransactions.
			getElementsByTagName(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION);	
			//String sAuthorizationId = elemCollectionDetail.getAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID);
			//Added by Baijayanta
			String sAuthId = elemCollectionDetail.getAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID);
			String sAuthorizationId = "";
			String[] tempTran = sAuthorizationId.split("=");
			if (tempTran.length == 2)
				sAuthorizationId = tempTran[0];
			else 
				sAuthorizationId = sAuthId;
			//Modification by Baijayanta ends
			Element elemCreditCardTransaction = (Element) nlCreditCardTransaction.item(0);
			
			if(elemCreditCardTransaction == null){
				continue;
			}
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("########### In addRef1Ref2  sAuthorizationId in credit card##################"+ sAuthorizationId);				
			}
			elemCreditCardTransaction.setAttribute(KohlsXMLLiterals.A_REFERENCE1, 
											mapChrgTransactionKeyRef1.get(sAuthorizationId));
			elemCreditCardTransaction.setAttribute(KohlsXMLLiterals.A_REFERENCE2, 
											mapChrgTransactionKeyRef2.get(sAuthorizationId));
			
		}
		
	}

	private Document getOrderDetails(YFSEnvironment env, Document inXML) throws YFSException, RemoteException {
		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemOrder = (Element)elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		String strOrderHeaderKey = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);		
		
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		Document docGetOrderDetails = api.getOrderDetails(env, getOrderInputXML(strOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);			
		
		return docGetOrderDetails;
	}

	private void addZeroChargeCreditCard(YFSEnvironment env, Document inXML, Document docGetOrderDetails) throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("########### Entered addZeroChargeCreditCard  #################"+ KohlsUtil.extractStringFromDocument(inXML));
		} 
		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemCollectionDetails = (Element)elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAILS).item(0);
		NodeList nlCollectionDetail = elemCollectionDetails.getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAIL);
		boolean isCreditCardCharged = false;
		for(int i = 0; i < nlCollectionDetail.getLength(); i++){
			Element elemCollectionDetail = (Element) nlCollectionDetail.item(i);
			Element elemPaymentMethod = (Element)elemCollectionDetail.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD).item(0);
			if(elemPaymentMethod != null && elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equals(KohlsConstant.CREDIT_CARD)){
				isCreditCardCharged = true;				
				break;
			}
			
		}
		if(!isCreditCardCharged){
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("########### Credit card not available in invoice ....so add manually  #################");
			} 
			// add first credit card with charge as $0 which serves just as information to COSA
			addCreditCard(docGetOrderDetails, inXML);			
		}		
	}
	private void addCreditCard(Document docGetOrderDetails,
			Document inXML) throws TransformerException {		 
		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemCollectionDetails = (Element)elemInvoiceHeader.
													getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAILS).item(0);
		Element elemOrder = (Element) docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		Element nlPaymentMethods = (Element) elemOrder.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
		NodeList nlPaymentMethod = nlPaymentMethods.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);		
		for(int i = 0; i < nlPaymentMethod.getLength(); i++){
			Element elemPaymentMethod = (Element) nlPaymentMethod.item(i);
			if(elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equals(KohlsConstant.CREDIT_CARD)){	
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("########### Before Adding Credit card #################"+ 
							KohlsUtil.extractStringFromNode(elemCollectionDetails));
				} 
				Element elemCollectionDetail = inXML.createElement(KohlsXMLLiterals.E_COLLECITON_DETAIL);
				elemCollectionDetail.setAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED, "0.00");
				elemCollectionDetails.appendChild(elemCollectionDetail);				
				Element elemPaymentMethodAdded = inXML.createElement(KohlsXMLLiterals.E_PAYMENT_METHOD);
				elemCollectionDetail.appendChild(elemPaymentMethodAdded);				
				copy(elemPaymentMethod, elemPaymentMethodAdded);
				elemPaymentMethodAdded.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, "0.00");
				elemPaymentMethodAdded.setAttribute(KohlsXMLLiterals.A_TOTAL_AUTHORIZED, "0.00");
				elemPaymentMethodAdded.setAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED, "0.00");
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("########### After Adding Credit card #################"+
							KohlsUtil.extractStringFromNode(elemCollectionDetails));
				}
				
				// increase TotalLines by 1
				String sTotalLines = elemCollectionDetails.getAttribute(KohlsXMLLiterals.A_TOTAL_LINES);
				elemCollectionDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_LINES, 
						Integer.toString((Integer.parseInt(sTotalLines)+1)));
				break;
			}
		}
		
	}

	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		YFCElement yfcElemPaymentMethods = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElemPaymentMethod = yfcElemPaymentMethods.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);
		//for zero credit card charge		
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_AWAIT_AUTH_INTERFACE_AMT, "");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_AWAIT_CHARGE_INTERFACE_AMT, "");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CHARGE_SEQ, "");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CHECK_NO, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CHECK_REF, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_EXP_DATE, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_EXP_NAME, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_NO, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE, "")  ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CUSTOMER_ACC_NO, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CUSTOMER_PO_NO, "")  ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO, "")  ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_DISP_PAYMENT_REF_1, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_INCOMPLETE_PAYMENT_TYPE, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_SVC_NO, "") ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_KEY, "")  ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_REF_1, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_REF_2, "")  ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_REF_3, "")  ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_REQUESTED_AMT, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_REQUESTED_CHARGE_AMT, "")  ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_SUSPENDED_ANYMORE_CHARGES, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_TOTAL_AUTHORIZED, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED, "") ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_TOTAL_REFUNDED_AMT, "");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_UNLIMITED_CHARGES, "");
		
		//for receipt id 
		YFCElement yfcElemNotes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_NOTES);
		YFCElement yfcElemNote = yfcElemNotes.createChild(KohlsXMLLiterals.E_NOTE);
		yfcElemNote.setAttribute(KohlsXMLLiterals.A_REASON_CODE, "");
		yfcElemNote.setAttribute(KohlsXMLLiterals.A_NOTE_TEXT, "");		
		
		//for refercence1 , refercence2 fields
		YFCElement yfcElemChargeTransactionDetails = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS);
		yfcElemChargeTransactionDetails.setAttribute(KohlsXMLLiterals.A_ADD_EXPECTED_AUTHS, "");
		YFCElement yfcElemChargeTransactionDetail = yfcElemChargeTransactionDetails.createChild(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
		yfcElemChargeTransactionDetail.setAttribute(KohlsXMLLiterals.A_CHARGE_TYPE, "");
		yfcElemChargeTransactionDetail.setAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID, "");
		YFCElement yfcElemCreditCardTransactions = yfcElemChargeTransactionDetail.createChild(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTIONS);
		YFCElement yfcElemCreditCardTransaction = yfcElemCreditCardTransactions.createChild(KohlsXMLLiterals.E_CREDIT_CARD_TRANSACTION);
		yfcElemCreditCardTransaction.setAttribute(KohlsXMLLiterals.A_REFERENCE1, "");
		yfcElemCreditCardTransaction.setAttribute(KohlsXMLLiterals.A_REFERENCE2, "");
		
		
		YFCElement yfcElemOrderLinesDetailsTemp = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcElemOrderLineDetailsTemp = yfcElemOrderLinesDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		
		YFCElement yfcElemLineCharges = yfcElemOrderLineDetailsTemp.createChild(KohlsXMLLiterals.A_LINE_CHARGES);
		YFCElement yfcElemLineCharge =  yfcElemLineCharges.createChild(KohlsXMLLiterals.A_LINE_CHARGE);
		yfcElemLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, "");
		YFCElement yfcElemLineChargeExtn =  yfcElemLineCharge.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElemLineChargeExtn.setAttribute(KohlsXMLLiterals.EXTN_PER_PRORATE_DISC, "");
		yfcElemLineChargeExtn.setAttribute(KohlsXMLLiterals.EXTN_VALUE1, "");
		yfcElemLineChargeExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_SCHEME, "");
		yfcElemLineChargeExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_CODE, "");
		yfcElemLineChargeExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_ID, "");
		yfcElemLineCharge.appendChild(yfcElemLineChargeExtn);
		yfcElemLineCharges.appendChild(yfcElemLineCharge);
		yfcElemOrderLineDetailsTemp.appendChild(yfcElemLineCharges);
		
		yfcElemOrderLineDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		log.debug("getOrderDetails template is:"+ SCXmlUtil.getString(yfcDocGetOrderDetailsTemp.getDocument()));
		return yfcDocGetOrderDetailsTemp.getDocument();
	}
	
	private void appendExtnToInvoiceLineHeader(YFSEnvironment env,
			Document inXML, Document docGetOrderDetials, String sEFC) throws YFSException, RemoteException, TransformerException {
		Element elemLineDetails = (Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_LINE_DETAILS).item(0);
		NodeList nlLineDetails = elemLineDetails
				.getElementsByTagName(KohlsXMLLiterals.E_LINE_DETAIL);
		for (int i = 0; i < nlLineDetails.getLength(); i++) {
			Element elemLineDetail = (Element) nlLineDetails.item(i);
			Element elemOrderLine = (Element) elemLineDetail
					.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE)
					.item(0);
			String strOrderLineKey = elemOrderLine
					.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);		
			
			//Extn at Line Charge
			addExtnAtLineCharge(elemLineDetail, strOrderLineKey, docGetOrderDetials);				
			
			
			// Net Taxable Amount changes
			String sExtenededPrice = elemLineDetail.getAttribute(KohlsXMLLiterals.A_EXTENDED_PRICE);
			BigDecimal dDiscount = new BigDecimal(0.00);
			Element elemInvoiceLineCharges = (Element)elemLineDetail.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES).item(0);
			if(elemInvoiceLineCharges != null){
				NodeList nlInvoiceLineCharge = elemInvoiceLineCharges.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGE);
				if(nlInvoiceLineCharge != null){
					 for(int j = 0; j< nlInvoiceLineCharge.getLength(); j++){
						 Element eleLineCharge = (Element) nlInvoiceLineCharge.item(j);
						 if(eleLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).
								 			equals(KohlsConstant.DiscountChargeCategory)){							 
							 dDiscount = dDiscount.add(new BigDecimal(eleLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT)));			 			
						 }
					 }
				}
			}

			// negate discount
			BigDecimal dNetTaxableAmt = (new BigDecimal(sExtenededPrice)).subtract(dDiscount);
			// set net taxable amount
			Element elemExtn = (Element) elemOrderLine.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_TAXABLE_AMT, dNetTaxableAmt.toString());
			
			// add shipment at OrderLine level
			
			// Start OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment
			//Document docShipmentLineList = getShipmentLineList(env, strOrderLineKey);
			Document docShipmentLineList = null;
			docShipmentLineList = getShipmentLineList(env, strOrderLineKey);	
			// control will go inside below "if condition" for SO line shipped from DSV
			 if(!docShipmentLineList.getDocumentElement().hasChildNodes()){
				docShipmentLineList = getShipmentLineListForDSVLine(env, strOrderLineKey);
			 }
			 //End OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment
			
			Element elemLineShipments = inXML.createElement(KohlsXMLLiterals.E_SHIPMENTS);
			elemOrderLine.appendChild(elemLineShipments);	
			//Start OASIS Support 10/28/2013- PMR 85615,379,000 Canceled Shipment showing up on Invoice
			NodeList nlShipmentLine= docShipmentLineList.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
			for(int k=0;k<nlShipmentLine.getLength();k++){
				Element elemShipmentLine=(Element)nlShipmentLine.item(k);
				double dQuantity=Double.parseDouble(elemShipmentLine.getAttribute(KohlsXMLLiterals.A_QUANTITY));
				if(dQuantity>0){		
					NodeList nlShipment = elemShipmentLine.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
					//End OASIS Support 10/28/2013- PMR 85615,379,000 Canceled Shipment showing up on Invoice
					for(int j=0; j < nlShipment.getLength(); j++){
						Element elemShipment = (Element) nlShipment.item(j);
						//Start OASIS support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
						String strStatus=elemShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
						if(!strStatus.equalsIgnoreCase("9000")){
						//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
						Element elemLineShipment = inXML.createElement(KohlsXMLLiterals.E_SHIPMENT);
						elemLineShipments.appendChild(elemLineShipment);
						elemLineShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
									                         elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));	
						//Start OASIS support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
						}
						
					}
				}
			}
			//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice		
			// Item extn appends
			String shipmentKey = "";
			//Start OASIS support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
			NodeList nlShipment= docShipmentLineList.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
			//End OASIS support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
			Element elemFirstShipment = (Element) nlShipment.item(0);
			
			if(elemFirstShipment != null){
				shipmentKey = elemFirstShipment.getAttribute("ShipmentKey");
				
				log.debug("########### Shipment number before ##################"+ shipmentKey);
				if (YFCObject.isVoid(shipmentKey)){
					shipmentKey = ((Element)(elemFirstShipment.getElementsByTagName("Shipment")).item(0)).getAttribute("ShipmentKey");
				}
				log.debug("########### Shipment number fix ##################"+ shipmentKey);
			}
			//commented by Baijayanta for Testing
			appendItemExtn(env, sEFC, elemOrderLine, shipmentKey);
				}
				

			}
	
	//Start OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment
	private Document getShipmentLineListForDSVLine(YFSEnvironment env, String strOrderLineKey) throws RemoteException {
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_LINE_LIST, getShipmentLineListTemp()); 
		Document docShipmentLineList = api.getShipmentLineList(env, getShipmentLineListInputForDSVLine(strOrderLineKey));
		env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_LINE_LIST);
		return docShipmentLineList;
		}
				
	//End OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment
	
	// Start OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment

	private Document getShipmentLineListInputForDSVLine(String strOrderLineKey) {
		YFCDocument yfcShipmentLine = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE);
		YFCElement eleShipmentLine = yfcShipmentLine.getDocumentElement();
		YFCElement eleOrderLine = eleShipmentLine.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		eleOrderLine.setAttribute(KohlsXMLLiterals.A_CHAINED_FROM_ORDER_LINE_KEY, strOrderLineKey);
		return yfcShipmentLine.getDocument();		 
	}

	//End OASIS Support 4/24/2014 - PMR 43796,379,000 - Get the shipmentNo for SO line having DSV shipment
	
	private void appendItemExtn(YFSEnvironment env, String sEFC,
			Element elemOrderLine, String shipmentKey)
			throws RemoteException, TransformerException {
		Element elemItem = (Element) elemOrderLine.getElementsByTagName(
				KohlsXMLLiterals.E_ITEM).item(0);
		String strItemId = elemItem
				.getAttribute(KohlsXMLLiterals.A_ITEM_ID);

		// getItemDetails to fetch Extn fields
		env.setApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS,
				getItemDetailsOutput());
		Document docItemDetails = api.getItemDetails(env,
				getItemDetailsInputXml(strItemId));
		env.clearApiTemplate(KohlsConstant.API_GET_ITEM_DETAILS);
		Element elemItemExtn = (Element) docItemDetails
				.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);

		Element elemInvoiceItemExtn = elemItem.getOwnerDocument().createElement(
				KohlsXMLLiterals.E_EXTN);
		elemInvoiceItemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_DEPT,
				elemItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_DEPT));
		elemInvoiceItemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_CLASS,
				elemItemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_CLASS));
		elemInvoiceItemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_SUB_CLASS,
				elemItemExtn
						.getAttribute(KohlsXMLLiterals.A_EXTN_SUB_CLASS));			

		elemItem.appendChild(elemInvoiceItemExtn);
		
		// PGC VGC check digit logic			
		appendSerialNo(env, shipmentKey, 
				elemOrderLine, docItemDetails.getDocumentElement(),	elemInvoiceItemExtn, sEFC);
	}

	private Document getShipmentLineList(YFSEnvironment env, String strOrderLineKey)
			throws RemoteException {
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_LINE_LIST, getShipmentLineListTemp()); 
		Document docShipmentLineList = api.getShipmentLineList(env, getShipmentLineListInput(strOrderLineKey));
		env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_LINE_LIST);
		return docShipmentLineList;
	}

	private Document getShipmentLineListInput(String strOrderLineKey) {
		YFCDocument yfcShipmentLine = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINE);
		YFCElement eleShipmentLine = yfcShipmentLine.getDocumentElement();
		eleShipmentLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, strOrderLineKey);
		return yfcShipmentLine.getDocument();		 
	}

	private Document getShipmentLineListTemp() {
		YFCDocument yfcShipmentLines = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT_LINES);
		YFCElement eleShipmentLines = yfcShipmentLines.getDocumentElement();
		YFCElement eleShipmentLine = eleShipmentLines.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		eleShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, "");
		//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		eleShipmentLine.setAttribute(KohlsXMLLiterals.A_QUANTITY,"");
		//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		YFCElement eleShipment = eleShipmentLine.createChild(KohlsXMLLiterals.E_SHIPMENT);
		eleShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		eleShipment.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		eleShipmentLine.setAttribute("ShipmentKey", "");
		eleShipment.setAttribute("ShipmentKey", "");
		//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		return yfcShipmentLines.getDocument();	
	}

	private void processKohlsCashCoupon(YFSEnvironment env, Document inXML) throws YFSException, ParserConfigurationException, SAXException, IOException {
		Element elemInvoiceHeader = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		Element elemOrder = (Element)elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);				
		Element elemExtn = (Element)elemOrder.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		Element elemKohlsCashTableList = (Element)elemExtn.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_CASH_TABLE_LIST).item(0);
		if(elemKohlsCashTableList == null){
			return;
		}
		NodeList nlKohlsCashTable = elemKohlsCashTableList.getElementsByTagName(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
		if(nlKohlsCashTable == null){
			return;
		}
		String strOrderHeaderKey = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		
		// Sum of Line and Header KC discounts
		BigDecimal dLineHeaderKCTotal  = getSumOfAllKCChrgs(elemInvoiceHeader);			
		
		Element elemUpdatedKohlsCashTableList = (Element)inXML.createElement(KohlsXMLLiterals.E_KOHLS_CASH_TABLE_LIST);
		elemExtn.appendChild(elemUpdatedKohlsCashTableList);
		for(int i = 0; i < nlKohlsCashTable.getLength(); i++ ){
			Element elemKohlsCashTable = (Element)nlKohlsCashTable.item(i);
			String strKohlsCashAmt = elemKohlsCashTable.getAttribute(KohlsXMLLiterals.A_KOHLS_CASH_AMOUNT);
			BigDecimal dKohlsCashAmt = new BigDecimal(strKohlsCashAmt);
			if(dKohlsCashAmt.equals(BigDecimal.ZERO)){
				continue;
			}
			BigDecimal dRemainingKohlsCashAmt = dKohlsCashAmt.subtract(dLineHeaderKCTotal);	
			// copy of kohl's cash table sent to COSA showing KC amount used
			Element elemUpdatedKohlsCashTable = (Element)inXML.createElement(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
			copy(elemKohlsCashTable, elemUpdatedKohlsCashTable);
			elemUpdatedKohlsCashTable.removeAttribute(KohlsXMLLiterals.A_TABLE_KEY);			
			elemUpdatedKohlsCashTableList.appendChild(elemUpdatedKohlsCashTable);
			// decide based on remaining KC amount 
			if(dRemainingKohlsCashAmt.doubleValue() >= 0){	
				elemUpdatedKohlsCashTable.setAttribute(KohlsXMLLiterals.A_KOHLS_CASH_AMOUNT, dLineHeaderKCTotal.toString());
				// update KohlsCashCoupon
				changeOrder(env, strOrderHeaderKey, elemKohlsCashTable, dRemainingKohlsCashAmt.toString());
				//  sum of all KC charges is fulfilled with this coupon so break
				break;
				
			}else{
				elemUpdatedKohlsCashTable.setAttribute(KohlsXMLLiterals.A_KOHLS_CASH_AMOUNT, dKohlsCashAmt.toString());
				// sum of all KC charges is not fulfilled with this coupon so  go to next coupon					
				// update KohlsCashCoupon
				changeOrder(env, strOrderHeaderKey, elemKohlsCashTable, "0.00");
				dLineHeaderKCTotal = dRemainingKohlsCashAmt.abs();
			}	
			
		}	
		elemExtn.removeChild(elemKohlsCashTableList);
		
	}

	

	private BigDecimal getSumOfAllKCChrgs(Element elemInvoiceheader) {		
		Element elemTotalSummary = (Element)elemInvoiceheader.getElementsByTagName(KohlsXMLLiterals.E_TOTAL_SUMMARY).item(0);
		Element elemChargeSummary = (Element)elemTotalSummary.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_SUMMARY).item(0);
		BigDecimal dLineHeaderKCTotal = new BigDecimal(0.00);
		if(elemChargeSummary == null){
			return dLineHeaderKCTotal;
		}
		NodeList nlChargeSummaryDetail = elemChargeSummary.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_SUMMARY_DETAIL);
		if(nlChargeSummaryDetail == null){
			return dLineHeaderKCTotal;
		}		
		
		// loop through ChargeSummaryDetail and sum up all KC discounts
		for(int i = 0; i< nlChargeSummaryDetail.getLength(); i++){
			Element elemChargeSummaryDetail = (Element)nlChargeSummaryDetail.item(i);
			if(elemChargeSummaryDetail.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME).contains(KohlsConstant.KOHLS_CASH)){
				dLineHeaderKCTotal  = dLineHeaderKCTotal.
								      add(new BigDecimal(elemChargeSummaryDetail.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT)));
			}
		}
		return dLineHeaderKCTotal;
	}

	

	private void changeOrder(YFSEnvironment env, String strOrderHeaderKey,
			Element elemKohlsCashTable, String strRemainingKCAmt) throws YFSException, ParserConfigurationException, SAXException, IOException {
		elemKohlsCashTable.setAttribute(KohlsXMLLiterals.A_KOHLS_CASH_AMOUNT, strRemainingKCAmt);
		api.changeOrder(env, getChangeOrderInput(strOrderHeaderKey,
				elemKohlsCashTable));
		
	}

	private Document getChangeOrderInput(String strOrderHeaderKey, Element elemKohlsCashTable) throws ParserConfigurationException, SAXException, IOException {
		Document docChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER).getDocument();
		Element elemChangeOrder =  docChangeOrder.getDocumentElement();
		elemChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		elemChangeOrder.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		elemChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		Element elemExtn = docChangeOrder.createElement(KohlsXMLLiterals.E_EXTN);
		elemChangeOrder.appendChild(elemExtn);
		Element elemKohlsCashTableList = docChangeOrder.createElement(KohlsXMLLiterals.E_KOHLS_CASH_TABLE_LIST);
		elemExtn.appendChild(elemKohlsCashTableList);
		Element elemNewKohlsCashTable = docChangeOrder.createElement(KohlsXMLLiterals.E_KOHLS_CASH_TABLE);
		elemKohlsCashTableList.appendChild(elemNewKohlsCashTable);	
		
		copy(elemKohlsCashTable, elemNewKohlsCashTable);
		
		return docChangeOrder;
	}

	private void copy(Element src,
			Element dest) {
		NamedNodeMap attrMap = src.getAttributes();		
		for( int count=0; count<attrMap.getLength(); count++ ) {
			Node attr = attrMap.item(count);
			String attrName = attr.getNodeName();
			String attrValue = attr.getNodeValue();
			dest.setAttribute( attrName, attrValue );
		}
	}
	private void addExtnAtLineCharge(Element elemLineDetail,
			String strOrderLineKey, Document docGetOrderDetials) throws YFSException, RemoteException {
		
		Element elemOrderLine = null;
		Element elemOrderLines =  (Element) docGetOrderDetials.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlOrderLine =  elemOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int i = 0; i < nlOrderLine.getLength() ; i++){
			elemOrderLine = (Element) nlOrderLine.item(i);
			if(elemOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY).equals(strOrderLineKey)){
				break;
			}
		}
		
		Element elemLineCharges = (Element)elemOrderLine.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES).item(0);
		if(elemLineCharges == null){
			return ;
		}
		NodeList nlLineCharge = elemLineCharges.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGE);
		if(nlLineCharge == null){
			return ;
		}
		// logic for line charges with charge amount 0
		for(int i =0 ; i< nlLineCharge.getLength(); i++){
			Element elemLineCharge = (Element)nlLineCharge.item(i);	
			if(Double.parseDouble(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT)) != 0){
				continue;
			}
			Element elemExtn = (Element)elemLineCharge.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			if(elemExtn == null){
				continue;
			}
			Element elemInvoiceLineCharges = (Element)elemLineDetail.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES).item(0);
			if(elemInvoiceLineCharges == null){				
				elemInvoiceLineCharges = XmlUtils.createChild(elemLineDetail, KohlsXMLLiterals.A_LINE_CHARGES);
			}
			// Start -- Added for 08071,379,000 -- OASIS_SUPPORT 01/21/2014, Modified for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
			if(!((Double.parseDouble(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT)) == 0.0) && (elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME).equalsIgnoreCase(KohlsConstant.CHARGE_NAME_YES_WE_CAN))) ){
			// End -- Added for 08071,379,000 -- OASIS_SUPPORT 01/21/2014, Modified for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
			Element elemInvoiceLineCharge = XmlUtils.createChild(elemInvoiceLineCharges, KohlsXMLLiterals.A_LINE_CHARGE);
			XmlUtils.createChild(elemInvoiceLineCharge, KohlsXMLLiterals.E_EXTN);
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, 
									elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME_KEY, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME_KEY));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_REFERENCE, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_REFERENCE));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_IS_BILLABLE, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_IS_BILLABLE));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_IS_DISCOUNT, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_IS_DISCOUNT));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_IS_SHIPPING_CHARGE, 
					elemLineCharge.getAttribute(KohlsXMLLiterals.A_IS_SHIPPING_CHARGE));
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_ORIGINAL_CHARGE_PERLINE, 
					"0.00");
			elemInvoiceLineCharge.setAttribute(KohlsXMLLiterals.A_ORIGINAL_CHARGE_PERUNIT, 
					"0.00");
			// Start -- Added for 08071,379,000 -- OASIS_SUPPORT 01/21/2014, Modified for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
			}	
			// End -- Added for 08071,379,000 -- OASIS_SUPPORT 01/21/2014, Modified for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
		}
		
		Element elemInvoiceLineCharges = (Element)elemLineDetail.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGES).item(0);
		if(elemInvoiceLineCharges == null){
			return ;
		}
		NodeList nlInvoiceLineCharge = elemInvoiceLineCharges.getElementsByTagName(KohlsXMLLiterals.A_LINE_CHARGE);
		if(nlInvoiceLineCharge == null){
			return ;
		}
		for(int i =0 ; i< nlLineCharge.getLength(); i++){
			Element elemLineCharge = (Element)nlLineCharge.item(i);
			for(int j = 0; j < nlInvoiceLineCharge.getLength(); j++){
			Element elemInvoiceLineCharge = (Element)nlInvoiceLineCharge.item(j);
			Element elemExtn = (Element)elemLineCharge.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				if(elemExtn != null && elemExtn.hasAttributes()  && elemInvoiceLineCharge != null && elemInvoiceLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME).
										equals(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){
					
					Element elemInvoiceExtn = (Element)elemInvoiceLineCharge.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
					elemInvoiceExtn.setAttribute(KohlsXMLLiterals.EXTN_PER_PRORATE_DISC, elemExtn.getAttribute(KohlsXMLLiterals.EXTN_PER_PRORATE_DISC));
					elemInvoiceExtn.setAttribute(KohlsXMLLiterals.EXTN_VALUE1, elemExtn.getAttribute(KohlsXMLLiterals.EXTN_VALUE1));
					elemInvoiceExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_SCHEME, elemExtn.getAttribute(KohlsXMLLiterals.EXTN_PROMO_SCHEME));
					elemInvoiceExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_CODE, elemExtn.getAttribute(KohlsXMLLiterals.EXTN_PROMO_CODE));
					elemInvoiceExtn.setAttribute(KohlsXMLLiterals.EXTN_PROMO_ID, elemExtn.getAttribute(KohlsXMLLiterals.EXTN_PROMO_ID));
				}
			}
		}
	}		

	

	private void appendSerialNo(YFSEnvironment env,
			String shipmentKey, Element elemOrderLine,
			Element elemItem, Element elemExtn, String sShipNode) throws RemoteException, TransformerException {
		
		Element elemPrimaryInfo = (Element) elemItem.getOwnerDocument().getElementsByTagName(
				KohlsXMLLiterals.E_PRIMARY_INFORMATION).item(0);
		
		String sItemType = elemPrimaryInfo.getAttribute(KohlsXMLLiterals.A_ITEM_TYPE);
		String sLineType = elemOrderLine.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("sProductLine "+ sItemType);
			log.debug("sLineType"+ sLineType);
		}
		// for Virtual GC SerialNo changes
		Element elemOrderLineExtn = (Element) elemOrderLine.getElementsByTagName(
				KohlsXMLLiterals.E_EXTN).item(1);
		if((sItemType != null && sItemType.equals(KohlsConstant.VIRTUAL_GIFT_CARD))
				|| (sLineType != null && sLineType.equals(KohlsConstant.VIRTUAL_GIFT_CARD))){
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Serial NO for Virtual GC  ");
			}
			appendGCSerialNo(elemExtn, elemOrderLineExtn.getAttribute(KohlsXMLLiterals.A_EXTN_VIRTUAL_GC_NUM));
		}
		// for Plastic GC SerialNo changes
		if((sItemType != null && sItemType.equals(KohlsConstant.PLASTIC_GIFT_CARD))
				|| (sLineType != null && sLineType.equals(KohlsConstant.LINE_TYPE_PGC))){
			if (YFCLogUtil.isDebugEnabled()) { 
				log.debug("Serial NO for Plastic GC  ");
			}
			String sPrimeLineNo = elemOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			Document docShipmentDetails = getShipmentDetails(env, shipmentKey, sShipNode);
			appendPlasticGCSerialNo(env, elemExtn, sPrimeLineNo, docShipmentDetails);
		}
		
		elemOrderLineExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_VIRTUAL_GC_NUM);
	}

	private void appendPlasticGCSerialNo(YFSEnvironment env, Element elemExtn,
			String sPrimeLineNo, Document docShipmentDetails) throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("docShipmentDetails "+ KohlsUtil.extractStringFromNode(docShipmentDetails));
		}
		String sShipmentLineKey = "";
		String sTagSerialNo = "";
		
		// get shipment line key  	
		Element elemShipmentLines =  (Element) docShipmentDetails.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINES).item(0);
		NodeList nlShipmentLine  = elemShipmentLines.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
		for(int k=0; k<nlShipmentLine.getLength(); k++ ){
			Element elemShipmentLine = (Element) nlShipmentLine.item(k);
			String sShipDetailsPrimeLineNo = elemShipmentLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);			
			if(sPrimeLineNo.equals(sShipDetailsPrimeLineNo)){
				sShipmentLineKey = elemShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("sShipmentLineKey "+ sShipmentLineKey);
				}
			}			
		}
		 
		// get	TagSerialNo
		Element elemContainers =  (Element) docShipmentDetails.getElementsByTagName(KohlsXMLLiterals.E_CONTAINERS).item(0);
		NodeList nlContainer  = elemContainers.getElementsByTagName(KohlsXMLLiterals.E_CONTAINER);
		for(int i=0; i < nlContainer.getLength(); i++ ){
			Element elemContainer = (Element) nlContainer.item(i);
			Element elemContainerDetails = (Element) elemContainer.getElementsByTagName
																(KohlsXMLLiterals.E_CONTAINER_DETAILS).item(0);			
			NodeList nlContainerDetail = elemContainerDetails.getElementsByTagName
																(KohlsXMLLiterals.A_CONTAINER_DETAIL);
			for(int j = 0; j < nlContainerDetail.getLength() ; j++){
				Element elemContainerDetail = (Element) nlContainerDetail.item(j);
				Element elemTagSerials = (Element) elemContainerDetail.getElementsByTagName
															(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIALS).item(0);
				Element elemTagSerial = (Element) elemTagSerials.getElementsByTagName
															(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIAL).item(0);
				if(elemTagSerial == null){
					continue;
				}
				if(sShipmentLineKey.equals(elemContainerDetail.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY))){
					sTagSerialNo = elemTagSerial.getAttribute(KohlsXMLLiterals.A_SERIAL_NO);
					 
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("sTagSerialNo "+ sTagSerialNo);
					}
				}
			}
			
		}
			
		appendGCSerialNo(elemExtn, sTagSerialNo);
		
	}

	private Document getShipmentDetails(YFSEnvironment env, String shipmentKey, String sShipNode) throws YFSException, RemoteException {
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS, getShipmentDetailsTemp());
		Document docShipmentDetails =  api.getShipmentDetails(env, getShipemntDetailsInput(shipmentKey, sShipNode));
		env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);		
		return docShipmentDetails;		
	}

	private Document getShipmentDetailsTemp() {
		YFCDocument yfcDocShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);	
		YFCElement elemShipment = yfcDocShipment.getDocumentElement();
		elemShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		
		YFCElement yfcElemContainers = elemShipment.createChild(KohlsXMLLiterals.E_CONTAINERS);
		YFCElement yfcElemContainer = yfcElemContainers.createChild(KohlsXMLLiterals.E_CONTAINER);
		yfcElemContainer.setAttribute(KohlsXMLLiterals.A_CONTAINER_SCM, "");
		
		
		YFCElement yfcElemContainerDetails = yfcElemContainer.createChild(KohlsXMLLiterals.E_CONTAINER_DETAILS);
		YFCElement yfcElemContainerDetail = yfcElemContainerDetails.createChild(KohlsXMLLiterals.A_CONTAINER_DETAIL);
		yfcElemContainerDetail.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcElemContainerDetail.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, "");
		yfcElemContainerDetail.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		yfcElemContainerDetail.setAttribute(KohlsXMLLiterals.A_ITEM_ID, "");
		
		YFCElement yfcElemShipmentTagSerials =  yfcElemContainerDetail.createChild(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIALS);
		YFCElement yfcElemShipmentTagSerial =  yfcElemShipmentTagSerials.createChild(KohlsXMLLiterals.E_SHIPMENT_TAG_SERIAL);
		yfcElemShipmentTagSerial.setAttribute(KohlsXMLLiterals.A_SERIAL_NO, "");
		
		YFCElement yfcElemShipmentLines = elemShipment.createChild(KohlsXMLLiterals.E_SHIPMENT_LINES);
		YFCElement yfcElemShipmentLine =  yfcElemShipmentLines.createChild(KohlsXMLLiterals.E_SHIPMENT_LINE);
		yfcElemShipmentLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		yfcElemShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY, "");
		yfcElemShipmentLine.setAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_NO, "");
		return yfcDocShipment.getDocument();	
		 
	}

	private Document getShipemntDetailsInput(String shipmentKey, String sShipNode) {
		YFCDocument yfcdocShipment = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT);
		YFCElement yfcElementShipment = yfcdocShipment.getDocumentElement();
		yfcElementShipment.setAttribute("ShipmentKey", shipmentKey);
		return yfcdocShipment.getDocument(); 
	}

	private Document getShipmentList(YFSEnvironment env, String sOrderHeaderKey)
						throws YFSException, RemoteException {	
		 
		env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_LIST, getShipmentListTemp());
		Document docShipmentList =  api.getShipmentListForOrder(env, getShipemntListInput(sOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_LIST);
		
		return docShipmentList;
		
	}

	private Document getShipmentListTemp() {
		YFCDocument yfcDocShipmentList = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT_LIST);
		YFCElement yfcElemShipment = yfcDocShipmentList.getDocumentElement().createChild(KohlsXMLLiterals.E_SHIPMENT);
		yfcElemShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, "");
		yfcElemShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, "");
		yfcElemShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, "");
		//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		yfcElemShipment.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
		//Added for excluding the BOPUS shipments Defect#269
		yfcElemShipment.setAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD, "");
		return yfcDocShipmentList.getDocument(); 
	}

	private Document getShipemntListInput(String sOrderHeaderKey) {
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		return yfcDocOrder.getDocument();
	}

	private void appendGCSerialNo(Element elemExtn, String sSerialNo) {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("sSerialNo" + sSerialNo);
		}
		if(sSerialNo == null || sSerialNo.trim().equals("")){
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("##########Error sSerialNo is null or empty #############" + sSerialNo);
			}
			return;
		}
		// Serial No
		elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GC_SERIAL_NO, sSerialNo);
		// UPC
		String sExtnGCUPC = sSerialNo.substring(0, 11);
		elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GC_UPC, sExtnGCUPC);
		// check digit
		String sExtnGCCheckDigit = Integer.toString(KohlsUtil.calcMod10CheckDigit(sExtnGCUPC));
		elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_GC_CHECK_DIGIT, sExtnGCCheckDigit);
	}

	private Document getItemDetailsOutput() {
		YFCDocument yfcDocItemTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ITEM);
		YFCElement yfcElemItemTemp = yfcDocItemTemp.getDocumentElement();
		yfcElemItemTemp.setAttribute(KohlsXMLLiterals.A_ITEM_ID, ""); 		
		YFCElement yfcElemExtnTemp = yfcElemItemTemp.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElemItemTemp.appendChild(yfcElemExtnTemp);
		yfcElemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_DEPT, "");
		yfcElemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_CLASS, "");
		yfcElemExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_SUB_CLASS, "");
		
		YFCElement yfcElemPrimaryInfo = yfcElemItemTemp.createChild(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
		yfcElemPrimaryInfo.setAttribute(KohlsXMLLiterals.A_PRODUCT_LINE, "");
		yfcElemPrimaryInfo.setAttribute(KohlsXMLLiterals.A_ITEM_TYPE, "");
		yfcElemItemTemp.appendChild(yfcElemPrimaryInfo);
		log.debug("getitemdetails template is:"+SCXmlUtil.getString(yfcDocItemTemp.getDocument()));
		return yfcDocItemTemp.getDocument();
	}

	private Document getItemDetailsInputXml(String strItemId) {
		YFCDocument yfcItem = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ITEM);
		yfcItem.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ITEM_ID,
				strItemId);
		yfcItem.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORGANIZATION_CODE,
				KohlsConstant.ITEM_ORGANIZATION_CODE);
		yfcItem.getDocumentElement().setAttribute(KohlsXMLLiterals.A_UOM,
				KohlsConstant.UNIT_OF_MEASURE);
		log.debug("the getItemDetails INP XML:"+ SCXmlUtil.getString(yfcItem.getDocument()));
		return yfcItem.getDocument();
	}
	
	private void appendExtnToInvoiceOrderHeader(YFSEnvironment env,
			Document inXML) throws YFSException, RemoteException {

		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		// set time stamp
		elemInvoiceHeader.setAttribute("TimeStamp", getTimeStamp());
		
		Element elemOrder = (Element) elemInvoiceHeader.getElementsByTagName(
				KohlsXMLLiterals.E_ORDER).item(0);
		// COSA day,week,month,year calculation
		Element eleExtn = (Element) elemOrder.getElementsByTagName(
				KohlsXMLLiterals.E_EXTN).item(0);
		  try {
			KohlsCalendarImplementation fiscalCalendar = 
				new KohlsCalendarImplementation(new CalendarType(CalendarType.FISCAL_454_CALENDAR));
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(new Date());
			eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COSA_DAY, 
					Integer.toString(fiscalCalendar.getDayOfWeek((GregorianCalendar) aCalendar)));
			eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COSA_WEEK, 
					Integer.toString(fiscalCalendar.getWeekOfFiscalPeriod((GregorianCalendar) aCalendar)));
			eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COSA_MONTH, 
					Integer.toString(fiscalCalendar.getPeriodInFiscalYear((GregorianCalendar) aCalendar)));
			eleExtn.setAttribute(KohlsXMLLiterals.A_EXTN_COSA_YEAR, 
					Integer.toString(fiscalCalendar.getFiscalYearCCYY((GregorianCalendar) aCalendar)));
			
		} catch (KohlsCalendarException e) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug(e.getMessage());
			}
			
		} 
		
		if(!isLastInvoice(env, elemInvoiceHeader.getAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY),
				elemOrder.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY))){
			eleExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_NO);
			eleExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_EVENT_ID);
			eleExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_ALGORITHM);
			eleExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_AMT);
			eleExtn.removeAttribute(KohlsXMLLiterals.A_EXTN_KCE_COUPON_BALANCE);
		}
	}		

	private String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.DATE_FORMAT);
		Calendar cCurrntDate = Calendar.getInstance(); 	
		return  new StringBuffer(sdf.format(cCurrntDate.getTime())).replace(10, 11, "T").toString();
	}
	
	private boolean isLastInvoice(YFSEnvironment env, String sOrderInvoiceKey,
			String sOrderHeaderKey) throws YFSException, RemoteException {
		// Not setting output  template as this API is not template driven
		Document docOrderInvoiceList = api.getOrderInvoiceList(env, getOrderInvoiceListInput(sOrderHeaderKey));		
		String sLastInvoiceKey = docOrderInvoiceList.getDocumentElement().getAttribute(KohlsXMLLiterals.A_LAST_ORDER_INVOICE_KEY);
		if(sOrderInvoiceKey.equals(sLastInvoiceKey)){
			return true;
		}
		return false;
	}

	private Document getOrderInvoiceListInput(String sOrderHeaderKey) {
		YFCDocument yfcDocOrderInvoice = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_INVOICE);
		yfcDocOrderInvoice.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
		return yfcDocOrderInvoice.getDocument();
	}

	/*private void appendExtnToInvoiceHeader(YFSEnvironment env, Document inXML, Document docGetOrderDetials, 
												Document docShipmentList)
			throws RemoteException, TransformerException, YFSException, YIFClientCreationException {*/
	private void appendExtnToInvoiceHeader(YFSEnvironment env, Document inXML, Document docGetOrderDetials, 
				Document docShipmentList)
throws RemoteException, TransformerException, YFSException, YIFClientCreationException {
		ArrayList shipmentNoSet = new ArrayList();
		Element elemInvoiceHeader = (Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
		// add receipt id to header extn
		Element elemExtn = (Element) elemInvoiceHeader.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
		String sEFC = elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SHIP_NODE);
		//Getting EXTN_OCF value
		String sExtnOCF = elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_OCF);  
		elemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_RECEIPT_ID, getReceiptId(env, sEFC, docGetOrderDetials, sExtnOCF));					
			
		// get all the EFCs in the system
		Set<String> setEFCs = KohlsUtil.getEFCList(env);
		// Add Shipments
		//Added by Baijayanta for Defect 493
		//get all the shipments related to the orderlinekey's in the inXML
		NodeList nLineDetail = inXML.getElementsByTagName("LineDetail");
		for(int k=0 ; k < nLineDetail.getLength() ; k++ ){
			Element eleLineDetail = (Element)nLineDetail.item(k);
			Element eOrderLine = SCXmlUtil.getChildElement(eleLineDetail, "OrderLine");
			String aOrderLineKey = eOrderLine.getAttribute("OrderLineKey");
			Document docShipmentListForOrderln =  api.getShipmentList(env, getShipemntListInpt(aOrderLineKey));
			Element eleShipmentListForOrderln = docShipmentListForOrderln.getDocumentElement();
			Element eleShipment = SCXmlUtil.getChildElement(eleShipmentListForOrderln, "Shipment");
			String aShipmentNo = eleShipment.getAttribute("ShipmentNo");
			shipmentNoSet.add(aShipmentNo);
			
		}
		
		//Ended by Baijayanta for Defect 493
		Element elemInvoiceShipments = inXML.createElement(KohlsXMLLiterals.E_SHIPMENTS);
		elemInvoiceHeader.appendChild(elemInvoiceShipments);		
		NodeList nlShipment = docShipmentList.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT);
		for(int i=0; i < nlShipment.getLength(); i++){
			Element elemShipment = (Element) nlShipment.item(i);
			String strDeliveryMethod = elemShipment.getAttribute(KohlsXMLLiterals.A_DELIVERY_METHOD);
			//added by Baijayanta for Defect 493
			String strShipmentNo = elemShipment.getAttribute("ShipmentNo");
			//ended by Baijayanta for Defect 493
			//Added this check for not including the BOPUS shipments
			if(!(KohlsConstant.PICK.equals(strDeliveryMethod))) {
				//Start OASIS Support 10/28/2013- PMR 85615,379,000 PMR 85615,379,000 Cancelled Shipment showing up on Invoice
				String strStatus=elemShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
				//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
				String strShipNode = elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
				// if ship node invoiced matches or if ship node is DSV 
				//Commented as part of PMR 85615,379,000 Cancelled Shipment showing up on Invoice
				//added by Baijayanta for Defect 493
				if(null != shipmentNoSet && shipmentNoSet.contains(strShipmentNo)) {
				//Ended by Baijayanta for Defect 493
				if(strShipNode.equals(sEFC) || (sEFC.equals(KohlsConstant.EFC1) && !setEFCs.contains(strShipNode))){
					//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
					if(!strStatus.equalsIgnoreCase("9000")){
					//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
					Element elemInvoiceShipment = inXML.createElement(KohlsXMLLiterals.E_SHIPMENT);
					elemInvoiceShipments.appendChild(elemInvoiceShipment);
					elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
													 elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
					elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sEFC);
					//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
					}
					//This condition is added for checking if the ship node is RDC
				}else if (sEFC.equals(KohlsConstant.EFC4)){
						if(!strStatus.equalsIgnoreCase("9000")){
						//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
						Element elemInvoiceShipment = inXML.createElement(KohlsXMLLiterals.E_SHIPMENT);
						elemInvoiceShipments.appendChild(elemInvoiceShipment);
						elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
														 elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
						elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sEFC);
						//Start OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
						}
						//This condition is added for checking if the ship node is RDC
					}else if (sEFC.equals(KohlsConstant.EFC4)){
							if(!strStatus.equalsIgnoreCase("9000")){
								Element elemInvoiceShipment = inXML.createElement(KohlsXMLLiterals.E_SHIPMENT);
								elemInvoiceShipments.appendChild(elemInvoiceShipment);
								elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
										elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
								elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sEFC);
							}
						}
			//End OASIS Support 10/28/2013- PMR 85615,379,000 Cancelled Shipment showing up on Invoice
				}
			} 
			//added by Baijayanta for Defect 493
			else if((KohlsConstant.PICK.equals(strDeliveryMethod))) {
				
				String strStatus=elemShipment.getAttribute(KohlsXMLLiterals.A_STATUS);
				String strShipNode = elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
				//String strShipmentNo = elemShipment.getAttribute("ShipmentNo");
				//if(strShipNode.equals(sEFC)){
				if(null != shipmentNoSet && shipmentNoSet.contains(strShipmentNo)){
					 if(!strStatus.equalsIgnoreCase("9000")){
						Element elemInvoiceShipment = inXML.createElement(KohlsXMLLiterals.E_SHIPMENT);
						elemInvoiceShipments.appendChild(elemInvoiceShipment);
						elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO,
														 elemShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
						elemInvoiceShipment.setAttribute(KohlsXMLLiterals.A_SHIPNODE, strShipNode);
						
					}
				 
				}
				
			 }
			
			//Closed by Baijayanta for Defect 493
		
		}
		
	}
	
	private Document getShipemntListInpt(String sOrderLineKey) {
		Document docShipment = SCXmlUtil.createDocument("Shipment");
		Element eleDocShipment = docShipment.getDocumentElement();
		Element eleShipmentLines = SCXmlUtil.createChild(eleDocShipment, "ShipmentLines");
		Element eleShipmentLine = SCXmlUtil.createChild(eleShipmentLines, "ShipmentLine");
		eleShipmentLine.setAttribute("OrderLineKey", sOrderLineKey);
		log.debug("Input doc for getShipmentList:"+SCXmlUtil.getString(docShipment));
		return docShipment;
	}
	
	public Document getOrderInputXML(String strOrderHeaderKey){		
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		return yfcDocOrder.getDocument();
	}

	private String getReceiptId(YFSEnvironment env,	String sEFC, Document docGetOrderDetials, String sExtnOCF) throws RemoteException, TransformerException {		
		String sShipNode = "";
		// getOrderReleaseDetails 	
		Element elemNotes = (Element) docGetOrderDetials.getElementsByTagName(KohlsXMLLiterals.E_NOTES).item(0);
		NodeList nlNote = elemNotes.getElementsByTagName(KohlsXMLLiterals.E_NOTE);
		for(int i=0; i< nlNote.getLength(); i++){			
			Element elemNote = (Element) nlNote.item(i);
			//check for delivery method PICK
			if((KohlsConstant.BPS.equals(sExtnOCF)))  
				sShipNode=KohlsConstant.PREENC+KohlsConstant.BPS+"_"+sEFC;  //BOPUS 
			else
				sShipNode=KohlsConstant.PREENC+sEFC;  // non-BOPUS
			
			if(elemNote.getAttribute(KohlsXMLLiterals.A_REASON_CODE).equals(sShipNode)){				
				return elemNote.getAttribute(KohlsXMLLiterals.A_NOTE_TEXT);				
			}
		}
		return null;		
	}
	
	public void setProperties(Properties arg0) throws Exception {

	}

}
