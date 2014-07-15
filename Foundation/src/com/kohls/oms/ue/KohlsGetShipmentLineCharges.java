package com.kohls.oms.ue;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSExtnInputLineChargesShipment;
import com.yantra.yfs.japi.YFSExtnLineChargeStruct;
import com.yantra.yfs.japi.YFSExtnOutputLineChargesShipment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetLineChargesForShipmentUE;

/**
 * 
 * @author Mudassar
 * This class implements the YFSGetLineChargesForShipmentUE and calculates the prorated charges and discounts per shipment line
 *
 * @author Priyadarshini 
 * 22nd Feb 2011
 * Invoicing  Giftwrap header charge at line charge level without prorating
 */
public class KohlsGetShipmentLineCharges implements
		YFSGetLineChargesForShipmentUE {
	private YIFApi api;
	//Logger
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsGetShipmentLineCharges.class.getName());;

	
	public KohlsGetShipmentLineCharges() throws YIFClientCreationException {
		
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	/**
	 * @author Mudassar
	 * @param YFSEnvironment, YFSExtnInputLineChargesShipment
	 * @return	YFSExtnOutputLineChargesShipment
	 * 
	 * This method calculates the prorated chargePerLine for every type of charge and discount
	 */
	public YFSExtnOutputLineChargesShipment getLineChargesForShipment(
			YFSEnvironment env, YFSExtnInputLineChargesShipment inputLineCharge)
			throws YFSUserExitException {
		 
		List<YFSExtnLineChargeStruct> chargesLineList = new ArrayList<YFSExtnLineChargeStruct>();
		YFSExtnLineChargeStruct lineChargeStruct = null;
		String sOrderLineKey = inputLineCharge.orderLineKey;
		String orderHeaderKey = inputLineCharge.orderHeaderKey;
		
		
		YFSExtnOutputLineChargesShipment outStruct = new YFSExtnOutputLineChargesShipment();
		try {
			// Moving  GiftWrap header charge at line charge level
			invoiceHeaderGWCharges(env, sOrderLineKey, orderHeaderKey, chargesLineList);
			
		} catch (YFSException e1) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Error while invoicing header Gift Wrap Charges" + e1.getMessage());
			}
		} catch (RemoteException e1) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Error while invoicing header Gift Wrap Charges" + e1.getMessage());
			}
		}
		
		BigDecimal originalOrderedQuantity = new BigDecimal(0);
		BigDecimal remainingChrgAmnt = new BigDecimal(0);
		
		BigDecimal chargeAmnt = new BigDecimal(0);
		BigDecimal zero = new BigDecimal(0);
		log.debug("***START --> Values from KohlsGetShipmentLineCharges UE***");
		try {			
			
			log.debug("HdrKey is " + orderHeaderKey);
			log.debug("OrderLineKey is : " + sOrderLineKey);

			BigDecimal bdShipmtQty = new BigDecimal(inputLineCharge.shipmentQty);
			
			log.debug("Shipment Qty is " + bdShipmtQty);
			

			Document getOrdDtlsInDoc = XmlUtils.createDocument(KohlsConstant.ELEM_ORDER);
			Element orderRootElem =  getOrdDtlsInDoc.getDocumentElement();
			orderRootElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY,orderHeaderKey);
			orderRootElem.setAttribute(KohlsConstant.ATTR_DOC_TYPE,KohlsConstant.SO_DOCUMENT_TYPE);
			orderRootElem.setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE,KohlsConstant.KOHLS_ENTERPRISE_CODE);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("I/p to GetOrderDetails --->  " + XmlUtils.getString(getOrdDtlsInDoc));
			}
		
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, this.createTemplate());
			log.debug("TEMPLATE set");
			Document getOrdDtlsOutDoc = api.getOrderDetails(env, getOrdDtlsInDoc);
			log.debug("API invoked");
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("O/p from getOrderDetails ShipmentLineCharges --->  " + XmlUtils.getString(getOrdDtlsOutDoc));
			}
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
			
			if (getOrdDtlsOutDoc != null) {
				Element outputRootElem = getOrdDtlsOutDoc.getDocumentElement();
				
				if (outputRootElem != null) {
						
							Element orderLinesElem = (Element) outputRootElem
									.getElementsByTagName(
											KohlsConstant.ELEM_ORDER_LINES)
									.item(0);
							// Checking if OrderLines element exists
							if (orderLinesElem != null) {
								NodeList listOfOrdLines = orderLinesElem
										.getElementsByTagName(KohlsConstant.ELEM_ORDER_LINE);

								// Loop through each OrderLine and find out if
								// the orderLine has the same key as the one
								// obtained from the UE inStruct
								for (int i = 0; i < listOfOrdLines.getLength(); i++) {

									// Get each orderLine element from the XML
									// and find the original Ordered Quantity
									Element elemOrdLine = (Element) listOfOrdLines
											.item(i);
									log.debug("OrdLineKey frm G.O.D's o/p: "
											+ elemOrdLine
													.getAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY));
									if (elemOrdLine.getAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY)
											.equalsIgnoreCase(sOrderLineKey)) {
										String origOrderedQuant = elemOrdLine
												.getAttribute(KohlsConstant.ATTR_ORIG_ORDERED_QTY);
										log.debug("origOrderedQuant frm G.O.D's o/p---> "
												+ origOrderedQuant);
										if (origOrderedQuant != null
												&& !origOrderedQuant
														.equals(KohlsConstant.BLANK)) {
											originalOrderedQuantity = new BigDecimal(
													origOrderedQuant);
										}

										// Find the RemainingChargeAmount from  LineCharges/LineCharge
										Element lineChargesElem = (Element) elemOrdLine
												.getElementsByTagName(
														KohlsConstant.ELEM_LINE_CHARGES)
												.item(0);
										if (lineChargesElem != null) {
											NodeList listOfLineCharge = lineChargesElem
													.getElementsByTagName(KohlsConstant.ELEM_LINE_CHARGE);
											for (int j = 0; j < listOfLineCharge
													.getLength(); j++) {
												Element elemLineCharge = (Element) listOfLineCharge
														.item(j);
												if(elemLineCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY)
												   .equals(KohlsConstant.GiftChargeCategory)){
													// do not pro rate GiftWrap charges
													lineChargeStruct = new YFSExtnLineChargeStruct();
													lineChargeStruct.chargeCategory = elemLineCharge
													.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY);
													lineChargeStruct.chargeName = elemLineCharge
															.getAttribute(KohlsConstant.ATTR_CHARGE_NAME);
													lineChargeStruct.chargePerLine = Double.parseDouble(elemLineCharge
																						.getAttribute(KohlsConstant.ATTR_CHARGE_AMNT));
													chargesLineList.add(lineChargeStruct);
													continue;
												}
												if (elemLineCharge
														.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY) != null
														&& !elemLineCharge
																.getAttribute(
																		KohlsConstant.ATTR_CHARGE_CATEGORY)
																.equals(KohlsConstant.BLANK)) {

													if (elemLineCharge
															.getAttribute(KohlsConstant.ATTR_CHARGE_AMNT) != null
															&& !elemLineCharge
																	.getAttribute(
																			KohlsConstant.ATTR_CHARGE_AMNT)
																	.equals(KohlsConstant.BLANK)) {
														chargeAmnt = new BigDecimal(
																elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_CHARGE_AMNT));
													}

													if (elemLineCharge
															.getAttribute(KohlsConstant.ATTR_RMNG_CHARGE_AMNT) != null
															&& !elemLineCharge
																	.getAttribute(
																			KohlsConstant.ATTR_RMNG_CHARGE_AMNT)
																	.equals(KohlsConstant.BLANK)) {
														remainingChrgAmnt = new BigDecimal(
																elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_RMNG_CHARGE_AMNT));
													}

													// Instantiate a new  YFSExtnLineChargeStruct  object and populate it
													// with the  chargeName,  chargeCategory and the  calculated chargePerLine
													lineChargeStruct = new YFSExtnLineChargeStruct();
													if (YFCLogUtil.isDebugEnabled()) {
														log.debug("***while looping thru chargeLines***");
														log.debug("Charge Category from G.O.D's o/p: "
																+ elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY));
														log.debug("Charge Name from G.O.D's o/p: "
																+ elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_CHARGE_NAME));
														log.debug("RemainingChargeAmount from G.O.D's o/p: "
																+ elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_RMNG_CHARGE_AMNT));
														log.debug("ChargeAmount from G.O.D's o/p: "
																+ elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_CHARGE_AMNT));
													}
													if (originalOrderedQuantity
															.compareTo(zero) != 0) {
														BigDecimal chargePerLine = null;
														if (inputLineCharge.bLastInvoiceForOrderLine == true) { 
															// i.e. last shipment  in case of last  shipment, instead
															// of making all calculations, we simply assign the value of
															// RemainingChargeAmount to chargePerLine
															chargePerLine = remainingChrgAmnt
																	.setScale(2);
															log.debug("Last Shipment...RemainingChargeAmount = "
																	+ remainingChrgAmnt
																	+ ",chargePerLine= "
																	+ chargePerLine);
														} else {
															chargePerLine = chargeAmnt
																	.multiply(
																			bdShipmtQty)
																	.divide(originalOrderedQuantity,
																			2,
																			1);
														}
														log.debug("chargePerLine--->"
																+ chargePerLine
																+ " for "
																+ elemLineCharge
																		.getAttribute(KohlsConstant.ATTR_CHARGE_NAME));
														lineChargeStruct.chargePerLine = Double
																.parseDouble(chargePerLine
																		.toString());
													}
													lineChargeStruct.chargeCategory = elemLineCharge
															.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY);
													lineChargeStruct.chargeName = elemLineCharge
															.getAttribute(KohlsConstant.ATTR_CHARGE_NAME);
													// Finally add this  YFSExtnLineChargeStruct  object to the
													// chargesLineList list
													chargesLineList
															.add(lineChargeStruct);
												}
											}

										}

										// Once we have zeroed-in on our  orderLine, we dont have to look at
										// the other orderLines  Hence we break out of the for loop

										break;
									}
								}
							

						}
					

				}
			}
			
			outStruct.newLineCharges = chargesLineList;
			//	This is just for debugging purposes
			log.debug("########Output struct results#############");
			List<YFSExtnLineChargeStruct> outputList = outStruct.newLineCharges;
			Iterator<YFSExtnLineChargeStruct> it = outputList.iterator();
			int i=0;
			while(it.hasNext()){
				
				YFSExtnLineChargeStruct lineChrgStruct1 = (YFSExtnLineChargeStruct) it.next();
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Charges of type : " + i);
					log.debug("chargePerLine : " + lineChrgStruct1.chargePerLine);
					log.debug("chargeName : " + lineChrgStruct1.chargeName);
					log.debug("chargeCategory: " + lineChrgStruct1.chargeCategory);
				}
			}
			log.debug("***END --> Values from KohlsGetShipmentLineCharges UE***");
			
		}  catch (YFSException ex) {
			log.debug(ex.getMessage());
			throw ex;
		}catch (Exception e) {
			log.debug(e.getMessage());
		}
		return outStruct;
	}
	
	

	
	private  void invoiceHeaderGWCharges(YFSEnvironment env, String strOrderLineKey, 
			       String strOrderHeaderKey, List<YFSExtnLineChargeStruct> chargesLineList) throws YFSException, RemoteException {
		// get OrderLine details
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS, getOrderLineDetailsTemp());
		Document docOrderLineDetails = api.getOrderLineDetails(env, getOrderLineDetailsInput(strOrderLineKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);
		
		NodeList nlExtn =  docOrderLineDetails.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
		if(nlExtn == null ){
			// if no gift extn return
			return;
		}		
		Element elemExtn = (Element)nlExtn.item(0);
		String strWrapTogetherCode = elemExtn .getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);
		if(strWrapTogetherCode == null
				|| strWrapTogetherCode.equals("")){
			// if not a Gift line return
			return;
		}
		// since there is a gift line do getOrderDetails to get header charges
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, getOrderDetailsTemp());
		Document docOrderDetails = api.getOrderDetails(env, getOrderDetailsInput(strOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		
		Element elemHeaderCharges = (Element)docOrderDetails.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGES).item(0);
		NodeList  nlHeaderCharge=  elemHeaderCharges.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGE);
		if(nlHeaderCharge == null ){
			// if no header charge return
			return;
		}
		
		// iterate through header charges
		for(int i=0; i < nlHeaderCharge.getLength(); i++){
			Element elemHeaderCharge = (Element)nlHeaderCharge.item(i);
			double dchargeAmount = Double.valueOf(elemHeaderCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT));
			String strChargeName = elemHeaderCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
			String strChargeCategory = elemHeaderCharge.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY);
			if(strChargeName.equals(strWrapTogetherCode)
					&& dchargeAmount != 0.0 && env.getTxnObject(strChargeName+strChargeCategory) == null){
				// there is a matching gift wrap header charge
				YFSExtnLineChargeStruct yfsExtnLineChargeStruct  = new YFSExtnLineChargeStruct();				
				yfsExtnLineChargeStruct.chargeAmount = dchargeAmount;
				yfsExtnLineChargeStruct.chargeName =  strChargeName;				
				yfsExtnLineChargeStruct.chargeCategory =  strChargeCategory;
				yfsExtnLineChargeStruct.chargePerLine =  dchargeAmount;
				chargesLineList.add(yfsExtnLineChargeStruct);
				env.setTxnObject(strChargeName+strChargeCategory, strOrderLineKey);
			}
		}
		

	}

	private Document getOrderDetailsInput(String strOrderHeaderKey) {
		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		return yfcDocOrder.getDocument();
	}

	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		
		YFCElement yfcElemHeaderCharges = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
		YFCElement yfcElemHeaderCharge = yfcElemHeaderCharges.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, " ");		
		
		return yfcDocGetOrderDetailsTemp.getDocument();
	}

	private Document getOrderLineDetailsInput(String strOrderLineKey) {
		YFCDocument yfcDocGetOrderDetail = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_LINE_DETAIL);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetail
				.getDocumentElement();
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY,
				strOrderLineKey);
		return yfcDocGetOrderDetail.getDocument();
	}

	private Document getOrderLineDetailsTemp() {
		YFCDocument yfcDocGetOrderLineDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER_LINE);	
		YFCElement yfcEleGetOrderLineDetailsTemp = yfcDocGetOrderLineDetailsTemp.getDocumentElement();
		yfcEleGetOrderLineDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		YFCElement yfcElemExtn = yfcDocGetOrderLineDetailsTemp.getDocumentElement()
		.createChild(KohlsXMLLiterals.E_EXTN);
		yfcElemExtn.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		
		return yfcDocGetOrderLineDetailsTemp.getDocument();
	}

	public Document createTemplate() throws Exception{

		
		Document doc = XmlUtils.createDocument(KohlsConstant.ELEM_ORDER);
		Element orderRootElem =  doc.getDocumentElement();
		orderRootElem.setAttribute(KohlsConstant.ATTR_DOC_TYPE, KohlsConstant.BLANK);
		orderRootElem.setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE, KohlsConstant.BLANK);
		orderRootElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, KohlsConstant.BLANK);
		orderRootElem.setAttribute(KohlsConstant.ATTR_ORDER_NO, KohlsConstant.BLANK);
		orderRootElem.setAttribute(KohlsConstant.ATTR_MIN_ORD_STATUS, KohlsConstant.BLANK);
		Element orderLinesElem = doc.createElement(KohlsConstant.ELEM_ORDER_LINES);
		orderLinesElem.setAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS, KohlsConstant.BLANK);
		//		Adding orderLines element to Order element
		orderRootElem.appendChild(orderLinesElem);
		//		Creating OrderLine element
		Element orderLineElem = doc.createElement(KohlsConstant.ELEM_ORDER_LINE);
		orderLineElem.setAttribute(KohlsConstant.ATTR_INVOICED_QUANT, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORDERED_QTY, KohlsConstant.BLANK);
		
		//		orderLineElem.setAttribute("OrderingUOM", KohlsConstant.ATTR_STATUS_QTY);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORIG_ORDERED_QTY, KohlsConstant.BLANK);
		//		orderLineElem.setAttribute("PipelineKey", KohlsConstant.BLANK);
		//		orderLineElem.setAttribute("PricingDate", KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_PRIME_LINE_NO, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_RCVD_QTY, KohlsConstant.BLANK);
		//		orderLineElem.setAttribute("ShipToKey", KohlsConstant.ATTR_CHARGE_CATEGORY);
		orderLineElem.setAttribute(KohlsConstant.ATTR_SHIPPED_QTY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_SUB_LINE_NO, KohlsConstant.BLANK);
		//		Adding orderLine element to OrderLines element
		orderLinesElem.appendChild(orderLineElem);
		//		Creating OrderStatuses element
		Element orderStatusesElem = doc.createElement(KohlsConstant.ELEM_ORD_STATUSES);
		//		Adding OrderStatuses element to OrderLine element
		orderLineElem.appendChild(orderStatusesElem);
		//		Creating OrderStatus element
		Element orderStatusElem = doc.createElement(KohlsConstant.ELEM_ORDER_STATUS);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_RECV_NODE, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_SHIP_NODE, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_STATUS, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_STATUS_QTY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_TOT_QTY, KohlsConstant.BLANK);
		//		Adding OrderStatus element to OrderStatuses element
		orderStatusesElem.appendChild(orderStatusElem);
		
		//		Creating LineCharges element
		Element lineChargesElem = doc.createElement(KohlsConstant.ELEM_LINE_CHARGES);
		//		Adding LineCharges element to OrderLine element
		orderLineElem.appendChild(lineChargesElem);
		//		Creating LineCharge element
		Element lineChargeElem = doc.createElement(KohlsConstant.ELEM_LINE_CHARGE);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_AMNT, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_RMNG_CHARGE_AMNT, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_NAME, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY, KohlsConstant.BLANK);
		
		//		Adding LineCharge element to LineCharges element
		lineChargesElem.appendChild(lineChargeElem);
		
		//		Creating Item element
		Element itemElem = doc.createElement(KohlsConstant.ELEM_ITEM);
		//		Adding Item element to OrderLine element
		orderLineElem.appendChild(itemElem);
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Template Document for getOrderDetails --> "+ XmlUtils.getString(doc));
		}
		return doc;
	
	}
}