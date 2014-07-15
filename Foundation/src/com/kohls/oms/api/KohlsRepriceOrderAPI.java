package com.kohls.oms.api;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * This class process Order Repricing XML
 * 1) Removes Hold on Order
 * 2) modifies Gift Card values in the Order if Gift Card canceled
 * @author Priyadarshini
 *
 */
public class KohlsRepriceOrderAPI implements YIFCustomApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsChangeOrderStatusAPI.class.getName());
	private  YIFApi api;
	// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	boolean headerCharges=false;
	// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	// Start -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
	List<String> wrapTogetherCodeHeader= new ArrayList<String>(); 
	List<String> wrapTogetherCodeTax= new ArrayList<String>(); 
	// End -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
	
	public KohlsRepriceOrderAPI() throws YIFClientCreationException{

		api = YIFClientFactory.getInstance().getLocalApi();

	}
	@Override
	public void setProperties(Properties arg0) throws Exception {

	}
	/**
	 * Process reprice XML
	 * 1) Removes Hold on Order
	 * 2) modifies Gift Card values in the Order if Gift Card fully/partially canceled
	 * @param env
	 * @param inXML
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @throws YFSUserExitException 
	 */
	public void processOrderRepricing(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, YFSUserExitException{
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### Reprice ORder Input XML   ###############" + XMLUtil.getXMLString(inXML));
		}
		// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
		HashMap hm = this.createMap(inXML);
		
		Set<Map.Entry> set = hm.entrySet(); 
		for (Map.Entry mapGrp : set) {
			ArrayList <Element> alGrpCode = (ArrayList) mapGrp.getValue(); 
			// Start -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			//populating the wraptogether codes into the list
			wrapTogetherCodeHeader.add((String)mapGrp.getKey());
			wrapTogetherCodeTax.add((String)mapGrp.getKey());
			// End -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			if (alGrpCode.size() > 1) {
				headerCharges=true;
			}
		}
		// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
		Element elemRepriceOrder = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		Element elemRepricePaymentMethods = (Element) elemRepriceOrder.
		getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
		String strOrderNo = elemRepriceOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO);	
		String strEnterpriseCode = elemRepriceOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);
		String strDocumentType = KohlsConstant.SO_DOCUMENT_TYPE;
		// create Map of ScvNo as key and Processed Amt as value from reprice XML
		Map<String, String> mapScvProcessedAmount = createScvProcessAmtMap(elemRepricePaymentMethods);
		
		// getOrderDetails
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		Document docGetOrderDetails = api.getOrderDetails(env, 
				getOrderInputXML(strOrderNo, strEnterpriseCode, strDocumentType));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		String sMinOrderStatus = docGetOrderDetails.getDocumentElement().getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);

		if(Double.parseDouble(KohlsConstant.AWAITING_INVOICE_CREATION)> Double.parseDouble(sMinOrderStatus)){

			updateChargesTaxes(elemRepriceOrder, docGetOrderDetails);

			// update Map based on Gift card non-cancellation/ complete cancellation / partial cancellation
			Map<String, Element> mapScvNoPaymentMethod = new HashMap<String, Element>();
			updateScvProcessAmtMap(env, docGetOrderDetails, mapScvProcessedAmount, mapScvNoPaymentMethod);	
			// Start -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
			env.setTxnObject(("GiftCardAmount"+strOrderNo),mapScvProcessedAmount);
			// Start -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
			if(mapScvProcessedAmount.isEmpty()){			
				// no cancellation, remove PaymentMethods from input reprice XML 
				elemRepriceOrder.removeChild(elemRepricePaymentMethods);
			}else{		
				updatePaymentMethods(mapScvProcessedAmount, mapScvNoPaymentMethod, elemRepriceOrder);
			}

			if(YFCLogUtil.isDebugEnabled()){
				log.debug("######### Reprice ORder Output XML   ###############" + XMLUtil.getXMLString(inXML));
			}
			
		

			/*======Don't move the Order to AwaitingInvoiceCreation if CashActivationHold is present on the order =====*/

			boolean bCashActivationHoldExist = false;
			boolean bRepricingHoldExist = false;
			boolean bOOBHoldExist = false;
			boolean bEcommHoldExist = false;
			boolean bResolveEcommHold = false;
			boolean bAddEcommHold = false;
			Element eleOrder = docGetOrderDetails.getDocumentElement();
			NodeList ndlstOrderHoldTypes = eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);

			for(int i=0;i<ndlstOrderHoldTypes.getLength();i++){
				Element eleOrderHoldType = (Element)ndlstOrderHoldTypes.item(i);
				String sHoldType = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				String sHoldStatus = eleOrderHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);
				if(KohlsConstant.CASH_ACTIVATION_HOLD.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bCashActivationHoldExist = true ;

				}	
				if(KohlsConstant.INVOICE_HOLD_INDICATOR.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bRepricingHoldExist = true ;

				}	
				if(KohlsConstant.OOB_HOLD.equalsIgnoreCase(sHoldType) && KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bOOBHoldExist = true ;

				}
				if(KohlsConstant.ECOMM_HOLD.equalsIgnoreCase(sHoldType) && 
						KohlsConstant.HOLD_CREATED_STATUS.equalsIgnoreCase(sHoldStatus)){
					bEcommHoldExist = true ;					
				}		
			}

			// check for Ecomm hold add/remove
			Element elemRepriceHoldType = (Element) elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE).item(0);
			if(elemRepriceHoldType != null){
				String sRepriceHoldType = elemRepriceHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
				String sRepriceHoldStatus = elemRepriceHoldType.getAttribute(KohlsXMLLiterals.A_STATUS);

				if(KohlsConstant.ECOMM_HOLD.equalsIgnoreCase(sRepriceHoldType)){ 
					if(KohlsConstant.HOLD_REMOVED_STATUS.equalsIgnoreCase(sRepriceHoldStatus)){
						bResolveEcommHold = true;
					}else{
						bAddEcommHold = true;	
					}						
				}
				elemRepriceHoldType.getParentNode().removeChild(elemRepriceHoldType);
			}

			// remove/add holds if any
			addHoldIndicator(inXML, bRepricingHoldExist, (bEcommHoldExist && bResolveEcommHold), bAddEcommHold);


			if(bRepricingHoldExist || (bEcommHoldExist && bResolveEcommHold) || bAddEcommHold ||bOOBHoldExist){
				// remove Gift Wrap line
				removeGiftWrapLine(elemRepriceOrder);
				// call change order if EcommHold or reprice hold exists		 
				api.changeOrder(env, inXML);
			}

			YFCDocument yfcDocOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrderList = yfcDocOrderList.getDocumentElement();
			yfcEleOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrderNo);
			yfcEleOrderList.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
			yfcEleOrderList.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);

			if(YFCLogUtil.isDebugEnabled()){
				log.debug("getOrderList input XML : " + XMLUtil.getXMLString(yfcDocOrderList.getDocument()));
			}

			YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
			.createDocument("OrderList");
			YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
			YFCElement yfcEleOrder = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");	

			YFCElement yfcElePriceInfo = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_PRICE_INFO);
			yfcElePriceInfo.setAttribute(KohlsXMLLiterals.A_TOTAL_AMOUNT, "");
			yfcEleOrder.appendChild(yfcElePriceInfo);

			YFCElement yfcEleChargeTranDetails = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS);
			yfcEleChargeTranDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_CREDITS, "");
			yfcEleChargeTranDetails.setAttribute(KohlsXMLLiterals.A_TOTAL_OPEN_AUTHORIZATION, "");
			yfcEleOrder.appendChild(yfcEleChargeTranDetails);

			YFCElement yfcEleChargeTranDetail = yfcEleChargeTranDetails.createChild(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAIL);
			yfcEleChargeTranDetail.setAttribute(KohlsXMLLiterals.A_AUTHORIZATION_ID, "");
			yfcElemOrderDetailsTemp.appendChild(yfcEleOrder);



			env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST, yfcDocGetOrderDetailsTemp.getDocument());
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("getOrderList Template : " + XMLUtil.getXMLString(yfcDocGetOrderDetailsTemp.getDocument()));
			}
			Document docOrderList = api.getOrderList(env, yfcDocOrderList.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("getOrderList Output XML : " + XMLUtil.getXMLString(docOrderList));
			}

			String sOrderHeaderKey = ((Element)docOrderList.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0)).getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
			Element ElePriceInfo = (Element)docOrderList.getElementsByTagName(KohlsXMLLiterals.E_PRICE_INFO).item(0);
			String sTotalAmount = ElePriceInfo.getAttribute(KohlsXMLLiterals.A_TOTAL_AMOUNT);

			Element eleChanTranDtls= (Element)docOrderList.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_TRANSACTION_DETAILS).item(0);
			String sTotalCredits = eleChanTranDtls.getAttribute(KohlsXMLLiterals.A_TOTAL_CREDITS);
			String sTotalAuthAmount = eleChanTranDtls.getAttribute(KohlsXMLLiterals.A_TOTAL_OPEN_AUTHORIZATION);
			BigDecimal bgTotalCredits = new BigDecimal(sTotalCredits);
			BigDecimal bgTotalAuthAmount = new BigDecimal(sTotalAuthAmount);
			BigDecimal bgTotal = bgTotalAuthAmount.add(bgTotalCredits);


			double dTotalAmountAuthorized = Double.parseDouble(bgTotal.toString());			
			double dTotalOrderAmount = Double.parseDouble(sTotalAmount);


			if(YFCLogUtil.isDebugEnabled()){
				log.debug("dTotalAmountAuthorized : " + dTotalAmountAuthorized);
				log.debug("dTotalOrderAmount : " + dTotalOrderAmount);
			}

			if((dTotalOrderAmount > dTotalAmountAuthorized) && !bOOBHoldExist){	

				bOOBHoldExist=true;

				// put OOB Hold when order total > tender total
				YFCDocument yfcChangeOrder = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleChangeOrder = yfcChangeOrder.getDocumentElement();
				yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);		 
				yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);	
				YFCElement yfcEleChangeOrderHoldTypes = yfcEleChangeOrder.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
				YFCElement yfcEleChangeOrderHoldType = yfcEleChangeOrderHoldTypes.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.OOB_HOLD);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_CREATED_STATUS);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, "Placing OOB Hold");

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("OOB ChangeOrder : " + XMLUtil.getXMLString(yfcChangeOrder.getDocument()));
				}
				api.changeOrder(env, yfcChangeOrder.getDocument());

			}else if ((dTotalOrderAmount <= dTotalAmountAuthorized) && bOOBHoldExist){

				YFCDocument yfcChangeOrder = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleChangeOrder = yfcChangeOrder.getDocumentElement();
				yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);		 
				yfcEleChangeOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);	
				YFCElement yfcEleChangeOrderHoldTypes = yfcEleChangeOrder.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
				YFCElement yfcEleChangeOrderHoldType = yfcEleChangeOrderHoldTypes.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, KohlsConstant.OOB_HOLD);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, KohlsConstant.HOLD_REMOVED_STATUS);
				yfcEleChangeOrderHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, "Removing OOB Hold");

				if(YFCLogUtil.isDebugEnabled()){
					log.debug("OOB ChangeOrder : " + XMLUtil.getXMLString(yfcChangeOrder.getDocument()));
				}
				api.changeOrder(env, yfcChangeOrder.getDocument());

				bOOBHoldExist=false;
			}
			// when none of the  holds exist
			if(!bCashActivationHoldExist && !bOOBHoldExist && (!bEcommHoldExist || bResolveEcommHold) && !bAddEcommHold){
				// change Order Status to 'Awaiting Invoice Creation'
				api.changeOrderStatus(env, getChangeOrderStatusInput(strOrderNo,strDocumentType,strEnterpriseCode));
			}
			// Start -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
			//Calling requestCollectionAPI
			YFCDocument yfcDocRC = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleRC = yfcDocRC.getDocumentElement();
			yfcEleRC.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, strOrderNo);
			yfcEleRC.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
			yfcEleRC.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
			
			api.requestCollection(env,  yfcDocRC.getDocument());
			// Start -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
			/*========== END ================*/	
		}
	
	}


	private void removeGiftWrapLine(Element elemRepriceOrder) {
		Element elemRepriceOrderLines  = (Element) elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlRepriceOrderLine = elemRepriceOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		int iCount = 0;
		while(iCount<nlRepriceOrderLine.getLength()){
			Element elemnlRepriceOrderLine  = (Element) nlRepriceOrderLine.item(iCount);
			String sWrapFlag = elemnlRepriceOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_WRAP);
			if(sWrapFlag!= null && sWrapFlag.trim().equals(KohlsConstant.YES)){
				// strip off Wrap line
				elemnlRepriceOrderLine.getParentNode().removeChild(elemnlRepriceOrderLine);
			}else{
				iCount++;
			}
		}
	}

	private void updateChargesTaxes(Element elemRepriceOrder,
			Document docGetOrderDetails) {

		// Header Charges
		addZeroHeaderCharge(elemRepriceOrder, docGetOrderDetails); 
		// Header Taxes
		addZeroHeaderTax(elemRepriceOrder, docGetOrderDetails);  	
	
		Map<String, Element> mapRepricePrimeLineNoOrderLine = new HashMap<String, Element>();
		// create map from Reprice XML, key as PrimeLineNo and OrderLine as value 
		NodeList nlRepriceOrderLine = elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int i = 0; i < nlRepriceOrderLine.getLength(); i++){
			Element elmRepriceOrderLine = (Element)nlRepriceOrderLine.item(i);
			String sPrimeLineNo = elmRepriceOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			mapRepricePrimeLineNoOrderLine.put(sPrimeLineNo, elmRepriceOrderLine);			
		}

		NodeList nlOrderLine = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		for(int i = 0; i < nlOrderLine.getLength(); i++){
			Element elmOrderLine = (Element)nlOrderLine.item(i);
			String sPrimeLineNo = elmOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			Element elmRepriceOrderLine = mapRepricePrimeLineNoOrderLine.get(sPrimeLineNo);
			if(elmRepriceOrderLine == null){
				continue;
			}
			
			// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
			//check for GIFTFLAG to copy the charges and tax from getOrderDetails output
			if("N".equalsIgnoreCase(elmRepriceOrderLine.getAttribute(KohlsXMLLiterals.A_GIFT_FLAG))){
				addZeroLineCharge(elmOrderLine, elmRepriceOrderLine);	
				addZeroLineTax(elmOrderLine, elmRepriceOrderLine);
			} else {
				//method to copy line charges from getOrderDetails output
				addLineChargeFromGetOrderDetails(elmOrderLine, elmRepriceOrderLine);
				//method to copy line taxes from getOrderDetails output
				addLineTaxFromGetOrderDetails(elmOrderLine, elmRepriceOrderLine);
			}
			// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
		
		}

	}
	
	
	private void addZeroHeaderCharge(Element elemRepriceOrder,
			Document docGetOrderDetails) {
		List<String> listRepriceChargeNames = new ArrayList<String>();
		NodeList nlRepriceHeaderCharge = elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGE);
		for(int i=0; i < nlRepriceHeaderCharge.getLength(); i++ ){
			Element elemRepriceHeaderCharge = (Element) nlRepriceHeaderCharge.item(i);
			listRepriceChargeNames.add(elemRepriceHeaderCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));				
		}		
		NodeList nlHeaderCharge = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGE);
		List<Element> listRemovedCharges = new ArrayList<Element>();
		for(int i=0; i < nlHeaderCharge.getLength(); i++ ){
			Element elemHeaderCharge = (Element) nlHeaderCharge.item(i);
			String sChargeName = elemHeaderCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
			if(!listRepriceChargeNames.contains(sChargeName)){
				listRemovedCharges.add(elemHeaderCharge);
			} 
		}
		
		Element elemRepriceHeaderCharges = (Element) elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGES).item(0);
		for(Element elemRemovedcharge : listRemovedCharges){
			if(elemRepriceHeaderCharges == null){
				elemRepriceHeaderCharges = XmlUtils.createChild(elemRepriceOrder, KohlsXMLLiterals.A_HEADER_CHARGES);
			}
			// Start -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			if(elemRemovedcharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equals(KohlsXMLLiterals.A_GIFT_WRAP)){
				if(wrapTogetherCodeHeader.size()>0){
					if(wrapTogetherCodeHeader.contains(elemRemovedcharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){
						//if the wrap together code exist in the reprice order XML, remove from list
						wrapTogetherCodeHeader.remove(elemRemovedcharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
					
					}else{
						String ExtnWrap=wrapTogetherCodeHeader.get(0);
						String WrapCode=elemRemovedcharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_REFERENCE,ExtnWrap );
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,ExtnWrap );
						//add new header charge with the new wrap together code
						Element elemRepriceHeaderCharge = XmlUtils.createChild(elemRepriceHeaderCharges, KohlsXMLLiterals.A_HEADER_CHARGE);
						copy(elemRemovedcharge, elemRepriceHeaderCharge);
						//update the removed wrap code with $0.00
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_REFERENCE,WrapCode );
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,WrapCode );
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "0.00");
						elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_REMAINING_CHARGE_AMOUNT, "0.00");
						wrapTogetherCodeHeader.remove(ExtnWrap);
					}
				}
			}
			// End -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
			//not setting $0.00 if charge category is GiftWrap
			if((!elemRemovedcharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equals(KohlsXMLLiterals.A_GIFT_WRAP) || !headerCharges))
			{
				elemRemovedcharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "0.00");
			}
			// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
			Element elemRepriceHeaderCharge = XmlUtils.createChild(elemRepriceHeaderCharges, KohlsXMLLiterals.A_HEADER_CHARGE);
			copy(elemRemovedcharge, elemRepriceHeaderCharge);
			
		}
	}
	private void addZeroHeaderTax(Element elemRepriceOrder,
			Document docGetOrderDetails) {
		List<String> listRepriceTaxNames = new ArrayList<String>();
		NodeList nlRepriceHeaderTax = elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAX);
		for(int i=0; i < nlRepriceHeaderTax.getLength(); i++ ){
			Element elemRepriceHeaderTax = (Element) nlRepriceHeaderTax.item(i);
			listRepriceTaxNames.add(elemRepriceHeaderTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));				
		}		
		NodeList nlHeaderTax = docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAX);
		List<Element> listRemovedTaxes = new ArrayList<Element>();
		for(int i=0; i < nlHeaderTax.getLength(); i++ ){
			Element elemHeaderTax = (Element) nlHeaderTax.item(i);
			String sChargeName = elemHeaderTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
			if(!listRepriceTaxNames.contains(sChargeName)){
				listRemovedTaxes.add(elemHeaderTax);
			} 
		}
		Element elemRepriceHeaderTaxes = (Element) elemRepriceOrder.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAXES).item(0);
		for(Element elemRemovedTax : listRemovedTaxes){
			if(elemRepriceHeaderTaxes == null){
				elemRepriceHeaderTaxes = XmlUtils.createChild(elemRepriceOrder, KohlsXMLLiterals.E_HEADER_TAXES);
			}
			// Start -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			if(elemRemovedTax.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equals(KohlsXMLLiterals.A_GIFT_WRAP_TAX)){
				if(wrapTogetherCodeTax.size()>0){
					if(wrapTogetherCodeTax.contains(elemRemovedTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){
						//if the wrap together code exist in the reprice order XML, remove from list
						wrapTogetherCodeTax.remove(elemRemovedTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
					}else{
						String ExtnWrap=wrapTogetherCodeTax.get(0);
						String WrapCode=elemRemovedTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,ExtnWrap );
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_REFERENCE,ExtnWrap );
						//add new header tax with the new wrap together code
						Element elemRepriceHeaderTax = XmlUtils.createChild(elemRepriceHeaderTaxes, KohlsXMLLiterals.E_HEADER_TAX);
						copy(elemRemovedTax, elemRepriceHeaderTax);	
						wrapTogetherCodeTax.remove(ExtnWrap);
						//update the removed wrap code with $0.00
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME,WrapCode );
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_REFERENCE,WrapCode );
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
						elemRemovedTax.setAttribute(KohlsXMLLiterals.A_REMAINING_TAX, "0.00");
					}
				}
			}
			// End -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
			// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
			//making sure giftwrap tax is not updated as $0.00
			if((!elemRemovedTax.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equals(KohlsXMLLiterals.A_GIFT_WRAP_TAX)) || !headerCharges){
							elemRemovedTax.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
							elemRemovedTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
			}
			// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
			Element elemRepriceHeaderTax = XmlUtils.createChild(elemRepriceHeaderTaxes, KohlsXMLLiterals.E_HEADER_TAX);
			copy(elemRemovedTax, elemRepriceHeaderTax);			
		}
	}
	private void addZeroLineTax(Element elmOrderLine,
			Element elmRepriceOrderLine) {
		NodeList nlRepriceLineTax = elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		NodeList nlLineTax = elmOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		Element elemRepriceLineTaxes = (Element) elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
		if(nlRepriceLineTax.item(0) == null){				
			if(elemRepriceLineTaxes == null){
				elemRepriceLineTaxes = XmlUtils.createChild(elmRepriceOrderLine, KohlsXMLLiterals.E_LINE_TAXES);
			}
			for(int j = 0; j < nlLineTax.getLength(); j++){
				Element elemLineTax = (Element) nlLineTax.item(j);
				elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
				elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
				Element elemRepriceLineTax = XmlUtils.createChild(elemRepriceLineTaxes, KohlsXMLLiterals.E_LINE_TAX);
				copy(elemLineTax, elemRepriceLineTax);
			}					
		}else{
			List<String> listRepriceLineTaxNames = new ArrayList<String>(); 
			for(int j = 0; j < nlRepriceLineTax.getLength(); j++){
				Element elemRepriceLineTax = (Element) nlRepriceLineTax.item(j);					
				listRepriceLineTaxNames.add(elemRepriceLineTax.getAttribute(KohlsXMLLiterals.A_TAX_NAME));
			}	
			for(int j = 0; j < nlLineTax.getLength(); j++){
				Element elemLineTax = (Element) nlLineTax.item(j);					
				if(!listRepriceLineTaxNames.contains(elemLineTax.getAttribute(KohlsXMLLiterals.A_TAX_NAME))){
					elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
					elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
					Element elemRepriceLineTax = XmlUtils.createChild(elemRepriceLineTaxes, KohlsXMLLiterals.E_LINE_TAX);
					copy(elemLineTax, elemRepriceLineTax);
				}
			}
		}
	}
	
	// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	//method to copy line taxes from getOrderDetails output
	private void addLineTaxFromGetOrderDetails(Element elmOrderLine,
			Element elmRepriceOrderLine) {
		NodeList nlRepriceLineTax = elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		NodeList nlLineTax = elmOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAX);
		Element elemRepriceLineTaxes = (Element) elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_TAXES).item(0);
		if(nlRepriceLineTax.item(0) == null){				
			if(elemRepriceLineTaxes == null){
				elemRepriceLineTaxes = XmlUtils.createChild(elmRepriceOrderLine, KohlsXMLLiterals.E_LINE_TAXES);
			}
			for(int j = 0; j < nlLineTax.getLength(); j++){
				Element elemLineTax = (Element) nlLineTax.item(j);
				elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX, elemLineTax.getAttribute(KohlsXMLLiterals.A_TAX));
				elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, elemLineTax.getAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE));
				Element elemRepriceLineTax = XmlUtils.createChild(elemRepriceLineTaxes, KohlsXMLLiterals.E_LINE_TAX);
				copy(elemLineTax, elemRepriceLineTax);
			}					
		}else{
			List<String> listRepriceLineTaxNames = new ArrayList<String>(); 
			for(int j = 0; j < nlRepriceLineTax.getLength(); j++){
				Element elemRepriceLineTax = (Element) nlRepriceLineTax.item(j);			
				//add the different charge names into the list
				listRepriceLineTaxNames.add(elemRepriceLineTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
			}	
			for(int j = 0; j < nlLineTax.getLength(); j++){
				Element elemLineTax = (Element) nlLineTax.item(j);					
				if(!listRepriceLineTaxNames.contains(elemLineTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){
					elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX, elemLineTax.getAttribute(KohlsXMLLiterals.A_TAX));
					elemLineTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, elemLineTax.getAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE));
					Element elemRepriceLineTax = XmlUtils.createChild(elemRepriceLineTaxes, KohlsXMLLiterals.E_LINE_TAX);
					copy(elemLineTax, elemRepriceLineTax);
				}
			}
		}
	}

	//method to copy line charges from getOrderDetails output
	private Element addLineChargeFromGetOrderDetails(Element elmOrderLine,
			Element elmRepriceOrderLine) {
		NodeList nlRepriceLineCharge = elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE);
		NodeList nlLineCharge = elmOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE);
		Element elemRepriceLineCharges = (Element) elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
		if(nlRepriceLineCharge.item(0) == null){				
			if(elemRepriceLineCharges == null){
				elemRepriceLineCharges = XmlUtils.createChild(elmRepriceOrderLine, KohlsXMLLiterals.E_LINE_CHARGES);
			}
			for(int j = 0; j < nlLineCharge.getLength(); j++){
				Element elemLineCharge = (Element) nlLineCharge.item(j);
				Element elemRepriceLineCharge = XmlUtils.createChild(elemRepriceLineCharges, KohlsXMLLiterals.E_LINE_CHARGE);
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY));
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));										
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT));
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE));
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT));
			}					
		}else{
			List<String> listRepriceLineChargeNames = new ArrayList<String>(); 
			for(int j = 0; j < nlRepriceLineCharge.getLength(); j++){
				Element elemRepriceLineCharge = (Element) nlRepriceLineCharge.item(j);					
				listRepriceLineChargeNames.add(elemRepriceLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
			}	
			for(int j = 0; j < nlLineCharge.getLength(); j++){
				Element elemLineCharge = (Element) nlLineCharge.item(j);					
				if(!listRepriceLineChargeNames.contains(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){					
					Element elemRepriceLineCharge = XmlUtils.createChild(elemRepriceLineCharges, KohlsXMLLiterals.E_LINE_CHARGE);
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY));
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));										
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT));
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE));
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT));


				}
			}
		}
		return elemRepriceLineCharges;
	}
	// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	
	
	private Element addZeroLineCharge(Element elmOrderLine,
			Element elmRepriceOrderLine) {
		NodeList nlRepriceLineCharge = elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE);
		NodeList nlLineCharge = elmOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE);
		Element elemRepriceLineCharges = (Element) elmRepriceOrderLine.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGES).item(0);
		if(nlRepriceLineCharge.item(0) == null){				
			if(elemRepriceLineCharges == null){
				elemRepriceLineCharges = XmlUtils.createChild(elmRepriceOrderLine, KohlsXMLLiterals.E_LINE_CHARGES);
			}
			for(int j = 0; j < nlLineCharge.getLength(); j++){
				Element elemLineCharge = (Element) nlLineCharge.item(j);
				Element elemRepriceLineCharge = XmlUtils.createChild(elemRepriceLineCharges, KohlsXMLLiterals.E_LINE_CHARGE);
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY));
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, 
						elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));										
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, 
				"0.00");
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE, 
				"0.00");
				elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT, 
				"0.00");
			}					
		}else{
			List<String> listRepriceLineChargeNames = new ArrayList<String>(); 
			for(int j = 0; j < nlRepriceLineCharge.getLength(); j++){
				Element elemRepriceLineCharge = (Element) nlRepriceLineCharge.item(j);					
				listRepriceLineChargeNames.add(elemRepriceLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));
			}	
			for(int j = 0; j < nlLineCharge.getLength(); j++){
				Element elemLineCharge = (Element) nlLineCharge.item(j);					
				if(!listRepriceLineChargeNames.contains(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME))){					
					Element elemRepriceLineCharge = XmlUtils.createChild(elemRepriceLineCharges, KohlsXMLLiterals.E_LINE_CHARGE);
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY));
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, 
							elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME));										
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, 
					"0.00");
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_LINE, 
					"0.00");
					elemRepriceLineCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_PER_UNIT, 
					"0.00");


				}
			}
		}
		return elemRepriceLineCharges;
	}

	private void addHoldIndicator(Document inXML, boolean bRemoveRepriceHold, 
			boolean bRemoveEcommHold, boolean bAddEcommHold) {
		if(!(bRemoveRepriceHold || bRemoveEcommHold || bAddEcommHold)){
			// if none of the holds exist
			return;
		}
		Element elemHoldTypes = inXML.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);				
		if(bRemoveRepriceHold){
			Element elemHoldType = createHoldType(inXML, KohlsConstant.INVOICE_HOLD_INDICATOR, 
					KohlsConstant.HOLD_INVOICE, KohlsConstant.HOLD_REMOVED_STATUS);
			elemHoldTypes.appendChild(elemHoldType);
		}
		if(bRemoveEcommHold){
			Element elemHoldType = createHoldType(inXML, KohlsConstant.ECOMM_HOLD, 
					KohlsConstant.ECOMM_HOLD_RESOLVE_REASON, KohlsConstant.HOLD_REMOVED_STATUS);
			elemHoldTypes.appendChild(elemHoldType);			
		}
		if(bAddEcommHold){
			Element elemHoldType = createHoldType(inXML, KohlsConstant.ECOMM_HOLD, 
					KohlsConstant.ECOMM_HOLD_REASON, KohlsConstant.HOLD_CREATED_STATUS);
			elemHoldTypes.appendChild(elemHoldType);			
		}
		Element elemRepriceOrder = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		elemRepriceOrder.appendChild(elemHoldTypes);
	}

	private Element createHoldType(Document inXML, String sType, String sReason, String sStatus) {
		Element elemHoldType = inXML.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);		
		elemHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, sType);
		elemHoldType.setAttribute(KohlsXMLLiterals.A_REASON_TEXT, sReason);
		elemHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, sStatus);
		return elemHoldType;
	}
	private void updatePaymentMethods(
			Map<String, String> mapScvProcessedAmount,
			Map<String, Element> mapScvNoPaymentMethod, Element elemRepriceOrder) {	
		Element elemRepricePaymentMethods = (Element) elemRepriceOrder.
		getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
		NodeList nlRepricePaymentMethod = elemRepricePaymentMethods.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);		
		Document ownerDocument = elemRepriceOrder.getOwnerDocument();
		Element elemUpdatedPaymentMethods = ownerDocument.createElement(KohlsXMLLiterals.E_PAYMENT_METHODS);
		for(int i = 0; i < nlRepricePaymentMethod.getLength(); i++){
			Element elemRepricePaymentMethod = (Element) nlRepricePaymentMethod.item(i);
			String sScvNo = elemRepricePaymentMethod.getAttribute(KohlsXMLLiterals.A_SVC_NO);
			if(!elemRepricePaymentMethod.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equals(KohlsConstant.GIFT_CARD)
					|| !mapScvProcessedAmount.containsKey(sScvNo)){
				// if not a gift card payment method or no gift card cancellation  
				// continue
				continue;				
			}	
			// Start -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
			//Commented this logic
			// for partially canceled Gift Card 
			/*
			String sUpdatedProcessedAmt = mapScvProcessedAmount.get(sScvNo);			

			Element elemUpdatedPaymentMethod = ownerDocument.createElement(KohlsXMLLiterals.E_PAYMENT_METHOD);
			Element elemUpdatedPaymentDetailsList = ownerDocument.
			createElement(KohlsXMLLiterals.E_PAYMENT_DETAILS_LIST);			
			Element elemUpdatedPaymentDetails = ownerDocument.
			createElement(KohlsXMLLiterals.E_PAYMENT_DETAILS);
			elemUpdatedPaymentDetailsList.appendChild(elemUpdatedPaymentDetails);
			elemUpdatedPaymentMethod.appendChild(elemUpdatedPaymentDetailsList);
			elemUpdatedPaymentMethods.appendChild(elemUpdatedPaymentMethod);			
			// copy payment method
			copy(elemRepricePaymentMethod, elemUpdatedPaymentMethod);			

			Element elemRepricePaymentDetailsList = (Element) elemRepricePaymentMethod
			.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_DETAILS_LIST).item(0);
			Element elemRepricePaymentDetails = (Element) elemRepricePaymentDetailsList
			.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_DETAILS).item(0);
			// copy payment details
			copy(elemRepricePaymentDetails, elemUpdatedPaymentDetails);
			elemUpdatedPaymentDetails.setAttribute(
					KohlsXMLLiterals.A_PROCESSED_AMOUNT, sUpdatedProcessedAmt);
			elemUpdatedPaymentDetails.setAttribute(KohlsXMLLiterals.A_REQUEST_AMOUNT, sUpdatedProcessedAmt);				
	*/
			// End -- Added for 04158,999,000 -- OASIS_SUPPORT 25/02/2013 //
		}
		// for completely canceled Gift Card 
		Set<String> setScvNoKey = mapScvProcessedAmount.keySet(); 
		for(String sScvNo : setScvNoKey){
			String sUpdatedProcessedAmt = mapScvProcessedAmount.get(sScvNo);		
			if(sUpdatedProcessedAmt == null){				
				Element elemUpdatedPaymentMethod = ownerDocument.
				createElement(KohlsXMLLiterals.E_PAYMENT_METHOD);
				Element elemUpdatedPaymentDetailsList = ownerDocument.createElement(KohlsXMLLiterals.E_PAYMENT_DETAILS_LIST);				
				Element elemUpdatedPaymentDetails  = ownerDocument.createElement(KohlsXMLLiterals.E_PAYMENT_DETAILS);
				elemUpdatedPaymentDetailsList.appendChild(elemUpdatedPaymentDetails);
				elemUpdatedPaymentMethod.appendChild(elemUpdatedPaymentDetailsList);

//				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, 
//				"0.00");
				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_SVC_NO, sScvNo);
				Element elemRepricePaymentMethod = mapScvNoPaymentMethod.get(sScvNo);
				String sDisplaySvcNo = elemRepricePaymentMethod.getAttribute(KohlsXMLLiterals.A_DISPLAY_SVC_NO);
				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_DISPLAY_SVC_NO, sDisplaySvcNo);
				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, KohlsConstant.GIFT_CARD);
				
				// Added on 2013-09-13 - Start
				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, elemRepricePaymentMethod.getAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT));
				elemUpdatedPaymentMethod.setAttribute(KohlsXMLLiterals.A_UNLIMITED_CHARGES, "N");
				// Added on 2013-09-13 - End
				
				//Start Adding CreditCardNo to updated PaymentMethod OASIS 15-OCT-2013
				
				elemUpdatedPaymentMethod.setAttribute("CreditCardNo", elemRepricePaymentMethod.getAttribute("CreditCardNo"));
				elemUpdatedPaymentMethod.setAttribute("CreditCardType", elemRepricePaymentMethod.getAttribute("CreditCardType"));
				elemUpdatedPaymentMethod.setAttribute("DisplayCreditCardNo", elemRepricePaymentMethod.getAttribute("DisplayCreditCardNo"));
				
				//End Adding CreditCardNo to updated PaymentMethod OASIS 15-OCT-2013
				
				String sProcessedAmt = elemRepricePaymentMethod.getAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED);
				elemUpdatedPaymentDetails.setAttribute(KohlsXMLLiterals.A_PROCESSED_AMOUNT,
						"-"+ sProcessedAmt);
				elemUpdatedPaymentDetails.setAttribute(KohlsXMLLiterals.A_REQUEST_AMOUNT,
						"-"+ sProcessedAmt);
				elemUpdatedPaymentDetails.setAttribute(KohlsXMLLiterals.A_CHARGE_TYPE,
						KohlsConstant.CHARGE_TYPE_CHARGE);
				elemUpdatedPaymentMethods.appendChild(elemUpdatedPaymentMethod);
			}
		}
		elemRepriceOrder.removeChild(elemRepricePaymentMethods);
		elemRepriceOrder.appendChild(elemUpdatedPaymentMethods);

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
	private void updateScvProcessAmtMap(YFSEnvironment env,
			Document docGetOrderDetails, Map<String, String> mapScvProcessedAmount, Map<String, Element> mapScvNoPaymentMethod)
	throws RemoteException {

		Element elemOrderDetails = (Element)docGetOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		Element nlOrderDetailsPaymentMethods = (Element) elemOrderDetails.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
		NodeList nlOrderDetailsPaymentMethod = nlOrderDetailsPaymentMethods.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);		

		// Iterate through Order details payment methods
		for(int i = 0; i < nlOrderDetailsPaymentMethod.getLength(); i++){
			Element elemPaymentMethod = (Element) nlOrderDetailsPaymentMethod.item(i);			
			if(!elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equals(KohlsConstant.GIFT_CARD)){
				// not a gift card so continue to next payment method
				continue;
			}
			String sSvcNo = elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_SVC_NO);				
			if(mapScvProcessedAmount.containsKey(sSvcNo)){		
				String sRepriceProcessedAmt = mapScvProcessedAmount.get(sSvcNo);

				String sProcessedAmt = elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED);

				double dRepriceProcessedAmt = Double.parseDouble(sRepriceProcessedAmt);
				double dProcessedAmt = Double.parseDouble(sProcessedAmt);				
				if(dRepriceProcessedAmt == dProcessedAmt){
					// this Gift Card is not canceled remove it from Map
					mapScvProcessedAmount.remove(sSvcNo);
					continue;
				}				
				// Gift card partially canceled
				double dDifferenceInProcessedAmt = dRepriceProcessedAmt - dProcessedAmt;				
				mapScvProcessedAmount.put(sSvcNo, "-"+Double.toString(Math.abs(dDifferenceInProcessedAmt)));
				
			}else{
				// Gift Card not sent in Repricing XML which means it is completely canceled					
				mapScvProcessedAmount.put(sSvcNo, null);
				mapScvNoPaymentMethod.put(sSvcNo, elemPaymentMethod);	
			}			
			
		}
		
	}
	private Map<String, String> createScvProcessAmtMap(Element elemRepricePaymentMethods) {
		Map<String, String> mapScvProcessedAmount = new HashMap<String, String>();		
		NodeList nlRepricePaymentMethod = elemRepricePaymentMethods.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);		
		for(int i = 0; i < nlRepricePaymentMethod.getLength(); i++){
			Element elemPaymentMethod = (Element) nlRepricePaymentMethod.item(i);
			Element elemPaymentDetails= (Element) elemPaymentMethod.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_DETAILS).item(0);
			if(elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equals(KohlsConstant.GIFT_CARD)){
				mapScvProcessedAmount.put(elemPaymentMethod.getAttribute(KohlsXMLLiterals.A_SVC_NO),
						elemPaymentDetails.getAttribute(KohlsXMLLiterals.A_PROCESSED_AMOUNT));				
			}		
		}
		return mapScvProcessedAmount;
	}

	public Document getOrderInputXML(String strOrderNo, String strEnterpriseCode, String strDocumentType){		
		YFCDocument yfcDocOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);		
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcDocOrder.getDocumentElement().setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("getOrderDetails Input \n" + XMLUtil.getXMLString(yfcDocOrder.getDocument()));
		}

		return yfcDocOrder.getDocument();
	}
	
	
	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDERNO, "");
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS, "");
		YFCElement yfcElemPaymentMethods = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_PAYMENT_METHODS);
		YFCElement yfcElemPaymentMethod = yfcElemPaymentMethods.createChild(KohlsXMLLiterals.E_PAYMENT_METHOD);				
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_SVC_NO, "") ;
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_DISPLAY_SVC_NO, "") ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_TOTAL_CHARGED, "") ;	
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_MAX_CHARGE_LIMIT, "") ;
		
		//Credit Card Attributes are added to resolve issue with Sales Audit record as "Cash" - start
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_NO,"");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE,"");
		yfcElemPaymentMethod.setAttribute(KohlsXMLLiterals.A_DISP_CREDIT_CARD_NO,"");
		//Credit Card Attributes are added to resolve issue with Sales Audit record as "Cash" - start
		
		YFCElement yfcEleOrderHoldTypes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleOrderHoldType = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleOrderHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleOrderHoldTypes.appendChild(yfcEleOrderHoldType);
		yfcElemOrderDetailsTemp.appendChild(yfcEleOrderHoldTypes);

		// Add header charges/taxes
		YFCElement yfcEleHeaderCharges = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
		yfcEleHeaderCharges.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);

		YFCElement yfcEleHeaderTaxes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		yfcEleHeaderTaxes.createChild(KohlsXMLLiterals.E_HEADER_TAX);

		// add orderline charges/taxes
		YFCElement yfcEleOrderLines = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLine = yfcEleOrderLines.createChild(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, "");
		YFCElement yfcEleOrderLineCharges = yfcEleOrderLine.createChild(KohlsXMLLiterals.E_LINE_CHARGES);
		YFCElement yfcEleOrderLineCharge = yfcEleOrderLineCharges.createChild(KohlsXMLLiterals.E_LINE_CHARGE);

		YFCElement yfcEleOrderLineTaxes = yfcEleOrderLine.createChild(KohlsXMLLiterals.E_LINE_TAXES);
		YFCElement yfcEleOrderLineTax = yfcEleOrderLineTaxes.createChild(KohlsXMLLiterals.E_LINE_TAX);

		return yfcDocGetOrderDetailsTemp.getDocument();
	}

	private Document getChangeOrderStatusInput(String strOrderNo, String strDocumentType, String strEnterpriseCode) {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_INCLUDE_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, strEnterpriseCode);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, strDocumentType);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_INVOICE_CREATION);

		return yfcDocChangeOrderStatus.getDocument();
	}

	/* This method is called from KohlsRepriceHoldRemovalService to remove the hold manually from the UI so that the order could be moved
	 * to Await Invoie Create status
	 * 
	 * Input XML: <Order DocumentType="0001" EnterpriseCode="KOHLS.COM" IgnoreOrdering="Y"
	 *					ModificationReasonCode="" ModificationReasonText=""
	 *					OrderHeaderKey="20110424170926246475" Override="Y">
	 *			   <OrderHoldTypes>
	 *   		<OrderHoldType HoldType="CashActivationHold" Status="1300" />
	 *			</OrderHoldTypes>
	 *			</Order>
	 */

	public void KohlsRemoveRepriceHold(YFSEnvironment env, Document inXML) throws YFSException, RemoteException{

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input XML from the UI : " + XMLUtil.getXMLString(inXML));
		}

		Element eleRoot = inXML.getDocumentElement();
		String sOrderHdrkey = eleRoot.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);

		NodeList ndlstHoldType = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		int len = ndlstHoldType.getLength();
		HashMap mapHoldTypes = new HashMap();
		for(int s=0;s<len;s++){
			Element eleHoldType = (Element)ndlstHoldType.item(s);
			mapHoldTypes.put(eleHoldType.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE), s);
		}

		YFCDocument yfcDocGetOrderDetails = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetails = yfcDocGetOrderDetails.getDocumentElement();
		yfcEleGetOrderDetails.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHdrkey);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Order Details Input XML : " + XMLUtil.getXMLString(yfcDocGetOrderDetails.getDocument()));
		}

		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcEleGetOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		yfcEleGetOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, "");
		yfcEleGetOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, "");
		yfcEleGetOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, "");
		yfcEleGetOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS, "");

		YFCElement yfcEleHoldTypes = yfcDocGetOrderDetailsTemp.createElement(KohlsXMLLiterals.E_ORDER_HOLD_TYPES);
		YFCElement yfcEleHoldType = yfcEleHoldTypes.createChild(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		yfcEleHoldType.setAttribute(KohlsXMLLiterals.A_HOLD_TYPE, "");
		yfcEleHoldType.setAttribute(KohlsXMLLiterals.A_STATUS, "");
		yfcEleHoldTypes.appendChild(yfcEleHoldType);
		yfcEleGetOrderDetailsTemp.appendChild(yfcEleHoldTypes);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Order Details Template XML : " + XMLUtil.getXMLString(yfcDocGetOrderDetailsTemp.getDocument()));
		}

		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, yfcDocGetOrderDetailsTemp.getDocument());
		Document docOrderDetailsOutputXML = api.invoke(env, KohlsConstant.API_GET_ORDER_DETAILS, yfcDocGetOrderDetails.getDocument());
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Order Details output XML : " + XMLUtil.getXMLString(docOrderDetailsOutputXML));
		}

		Element eleOrderDetails = docOrderDetailsOutputXML.getDocumentElement();
		String sMinOrderStauts = eleOrderDetails.getAttribute(KohlsXMLLiterals.A_MIN_ORDER_STATUS);
		String sOrderNo = eleOrderDetails.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
		String sDocumentType = eleOrderDetails.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE);
		String sEnterpriseCode = eleOrderDetails.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE);

		NodeList ndlstOrderHoldType = docOrderDetailsOutputXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER_HOLD_TYPE);
		int OrdHldLen = ndlstOrderHoldType.getLength();
		boolean bOpenHold = false;

		for(int m=0;m<OrdHldLen;m++){

			Element eleOrderHold = (Element)ndlstOrderHoldType.item(m);
			String sHoldStatus = eleOrderHold.getAttribute(KohlsXMLLiterals.A_STATUS);
			String sHoldType = eleOrderHold.getAttribute(KohlsXMLLiterals.A_HOLD_TYPE);
			if(!mapHoldTypes.containsKey(sHoldType) && sHoldStatus.equalsIgnoreCase(KohlsConstant.HOLD_CREATED_STATUS)){
				bOpenHold = true;
				break;
			}
		}

		if(bOpenHold){
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("Change Order when Hold  is present : " + XMLUtil.getXMLString(inXML));
			}
			api.changeOrder(env, inXML);
		}else{
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("Change Order when Only Repricing Hold  is present : " + XMLUtil.getXMLString(inXML));
			}
			api.changeOrder(env, inXML);

			if(sMinOrderStauts.equalsIgnoreCase(KohlsConstant.ORDER_SHIPPED)){
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Change Order Status : " + XMLUtil.getXMLString(getChangeOrderStatusInput(sOrderNo, sDocumentType, sEnterpriseCode)));
				}
				api.changeOrderStatus(env, getChangeOrderStatusInput(sOrderNo, sDocumentType, sEnterpriseCode));
			}
		}
	}
	
	// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	
	/**
	 * @param inXML
	 * Input XML with Order Information
	 * @return
	 * HashMap with Group Code as Key and OrderLine Element as ArrayList for the corresponding Group Code as Value.
	 */
	private HashMap<String, ArrayList<Element>> createMap(Document inXML) {

		String strExtnGrpCode = null;
		Element elemOrder = null;
		NodeList nlOrderLine = null;
		HashMap<String, ArrayList<Element>> hm = null; 

		try {
			hm = new HashMap<String, ArrayList<Element>>();
			elemOrder = inXML.getDocumentElement();	
			nlOrderLine = elemOrder.getElementsByTagName("OrderLine");

			for (int count = 0; count < nlOrderLine.getLength(); count++)  {
				ArrayList<Element> alOrderLines = null;
				Element elemOL = (Element) nlOrderLine.item(count);
				NodeList nlExtn = elemOL.getElementsByTagName("Extn");
				// check for Extn element
				if(nlExtn.getLength()==0){
					YFSException ex = new YFSException();
					ex.setErrorCode("Extn element is missing");
					ex.setErrorDescription("Extn element is missing in the input xml");
					throw ex;	
				}				 
				Element elemExtn = (Element) nlExtn.item(0);

				strExtnGrpCode = elemExtn.getAttribute("ExtnWrapTogetherGroupCode"); 
				strExtnGrpCode = strExtnGrpCode.trim();
				if (!strExtnGrpCode.equalsIgnoreCase("") && strExtnGrpCode != null) {
					if (hm.containsKey(strExtnGrpCode)) {
						alOrderLines = (ArrayList<Element>) hm.get(strExtnGrpCode);
						alOrderLines.add(elemOL);	
					} else 	{
						alOrderLines = new ArrayList<Element>();
						alOrderLines.add(elemOL);
						hm.put(strExtnGrpCode, alOrderLines);
					}	
				}
			}
		} catch (YFSException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hm;
	}
	// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //


}
