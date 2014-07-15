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
import com.yantra.yfs.japi.YFSExtnLineTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateLineTaxUE;

/**
 * 
 * @author Mudassar
 * This class implements the YFSRecalculateLineTaxUE and calculates the prorated taxes per shipment line
 *
 */
public class KohlsRecalculateLineTax implements YFSRecalculateLineTaxUE {

	private YIFApi api;
	//Logger
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsRecalculateLineTax.class.getName());

	
	public KohlsRecalculateLineTax() throws YIFClientCreationException {
		
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	/**
	 * @author Mudassar
	 * @param YFSEnvironment, YFSExtnLineTaxCalculationInputStruct
	 * @return	YFSExtnTaxCalculationOutStruct
	 * 
	 * This method calculates the prorated taxPerLine for every type of taxName
	 */
	public YFSExtnTaxCalculationOutStruct recalculateLineTax(
			YFSEnvironment env, YFSExtnLineTaxCalculationInputStruct inStruct)
			throws YFSUserExitException {
		
		log.debug("***START --> Values from KohlsRecalculateLineTax UE***");
		YFSExtnTaxCalculationOutStruct outStruct = new YFSExtnTaxCalculationOutStruct();
		
		
		 List<YFSExtnTaxBreakup> taxBreakUpList = new ArrayList<YFSExtnTaxBreakup>();

			// handling GiftWrap tax starts
			
		try {
			
			invoiceHeaderGWTax(env, inStruct.orderLineKey, inStruct.orderHeaderKey, taxBreakUpList);
			
		} catch (Exception e1) {
			log.debug("########### Error invoicing GiftWrap Header Taxes##########" + e1.getMessage());
		} 
			
			// handling GiftWrap tax ends
		 YFSExtnTaxBreakup taxBreakup = new YFSExtnTaxBreakup();
		 
		BigDecimal originalOrderedQuantity = new BigDecimal(0);
		BigDecimal remainingTaxAmnt = new BigDecimal(0);
		
		BigDecimal taxAmnt = new BigDecimal(0);
		BigDecimal zero = new BigDecimal(0);
		try{
			String sOrderLineKey = inStruct.orderLineKey;
			log.debug("currentQty : " + inStruct.currentQty); //Shipment quantity
			BigDecimal bdShipmtQty = new BigDecimal(inStruct.currentQty);
			
			log.debug("Shipment Qty is " + bdShipmtQty);

			log.debug("orderLineKey from UE's inStruct: " + inStruct.orderLineKey); //OrderLineKey
			log.debug("orderHdrKey : " + inStruct.orderHeaderKey);//OrderHeaderKey
	
			log.debug("bLastInvoiceForOrderLine : " + inStruct.bLastInvoiceForOrderLine); //boolean LastInvoiceForOrderLine
		
//			<Order OrderHeaderKey="" DocumentType="0001" EnterpriseCode="KOHLS.COM" />
			Document getOrdDtlsInDoc = XmlUtils.createDocument(KohlsConstant.ELEM_ORDER);
			Element orderRootElem =  getOrdDtlsInDoc.getDocumentElement();
			orderRootElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY,inStruct.orderHeaderKey);
			orderRootElem.setAttribute(KohlsConstant.ATTR_DOC_TYPE,KohlsConstant.SO_DOCUMENT_TYPE);
			orderRootElem.setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE,KohlsConstant.KOHLS_ENTERPRISE_CODE);
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("I/p to GetOrderDetails --->  " + XmlUtils.getString(getOrdDtlsInDoc));
			}
			
//			FileInputStream ioStrm = new FileInputStream("/template/api/getOrderDtlsTemplt_ShipLineChrgs.xml");
//			DocumentBuilderFactory docBuilderFac = DocumentBuilderFactory.newInstance();
//			DocumentBuilder docBuilder = docBuilderFac.newDocumentBuilder();
//			Document templ = docBuilder.parse(ioStrm);
//			Document doc = this.createTemplate();
			env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS, this.createTemplate());
			log.debug("TEMPLATE set");
			Document getOrdDtlsOutDoc = api.getOrderDetails(env, getOrdDtlsInDoc);
			log.debug("API invoked");
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("O/p from getOrderDetails RecalculateLineTax--->  " + XmlUtils.getString(getOrdDtlsOutDoc));
			}
			env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
			
			if(getOrdDtlsOutDoc != null){
				Element outputRootElem = getOrdDtlsOutDoc.getDocumentElement();
				
				if(outputRootElem != null){
						
							Element orderLinesElem = (Element)outputRootElem.getElementsByTagName(KohlsConstant.ELEM_ORDER_LINES).item(0);
	//						Checking if OrderLines element exists
							if(orderLinesElem != null){
								NodeList listOfOrdLines = orderLinesElem.getElementsByTagName(KohlsConstant.ELEM_ORDER_LINE);
	
								//Loop through each OrderLine and find out if the orderLine has the same key as the one obtained from the UE inStruct
								for (int i = 0; i < listOfOrdLines.getLength(); i++) {
	
									//Get each orderLine element from the XML and find the original Ordered Quantity
									Element elemOrdLine = (Element) listOfOrdLines.item(i);
									log.debug("OrdLineKey from getOrderDtls o/p: " + elemOrdLine.getAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY));
									if(elemOrdLine.getAttribute("OrderLineKey").equalsIgnoreCase(sOrderLineKey)){
										String origOrderedQuant = elemOrdLine.getAttribute(KohlsConstant.ATTR_ORIG_ORDERED_QTY);
										log.debug("origOrderedQuant frm G.O.D's o/p---> "+origOrderedQuant);
										if(origOrderedQuant != null && !origOrderedQuant.equals("")){
											originalOrderedQuantity = new BigDecimal(origOrderedQuant);
										}
	//									Find the StatusQty from OrderStatuses/OrderStatus
//										Element orderStatusesElem = (Element)elemOrdLine.getElementsByTagName(KohlsConstant.ELEM_ORD_STATUSES).item(0);
//										if(orderStatusesElem != null){
//											NodeList listOfOrderStatus = orderStatusesElem.getElementsByTagName(KohlsConstant.ELEM_ORDER_STATUS);
//	
//											//Loop through each orderStatus And find the quantity where status=3700 and add up all those quantity values
//											
//											for (int k = 0; k < listOfOrderStatus.getLength(); k++) {
//												Element elemOrdStatus = (Element) listOfOrderStatus.item(k);
//												if(elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS) != null && !elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS).equals("")){
//													oLogger.debug("Loop No. "+ k +": status in orderLine>>OrderStatus = "+elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS));
//													if(Double.parseDouble(elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS)) == 3700){
//														String statusQty = elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS_QTY);
//														oLogger.debug("Quantity with status 3700 from G.O.D's o/p= "+elemOrdStatus.getAttribute(KohlsConstant.ATTR_STATUS_QTY));
//														if(statusQty != null && !statusQty.equals("")){
//															quantityWithStatus3700 = quantityWithStatus3700.add(new BigDecimal(statusQty)).setScale(2) ;
//														}
//													}
//												}
//											}
//											
//										}
										
	//									Find the RemainingTaxAmount from LineTaxes/LineTax
										Element lineTaxesElem = (Element)elemOrdLine.getElementsByTagName(KohlsConstant.ELEM_LINE_TAXES).item(0);
										if(lineTaxesElem != null){
											NodeList listOfLineTax = lineTaxesElem.getElementsByTagName(KohlsConstant.ELEM_LINE_TAX);
//											Calculate the value of previous ShippedQuantity of this orderLine
//											BigDecimal prevShippedQuantity = quantityWithStatus3700.subtract(bdShipmtQty).setScale(2);
//											oLogger.debug("prevShippedQuantity---> "+prevShippedQuantity);
//											oLogger.debug("quantityWithStatus3700---> "+quantityWithStatus3700);
//											
//											BigDecimal quantToBeInvoiced = originalOrderedQuantity.subtract(prevShippedQuantity).setScale(2);
//											oLogger.debug("originalOrderedQuantity---> " + originalOrderedQuantity);
//											oLogger.debug("quantToBeInvoiced--->" + quantToBeInvoiced);
											
											//Loop through each linetax And calculate the prorated tax for each taxname
											for (int j = 0; j < listOfLineTax.getLength(); j++) {
												Element elemLineTax = (Element) listOfLineTax.item(j);
												if(elemLineTax.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equals(KohlsConstant.GiftTaxCategory)){
													taxBreakup = new YFSExtnTaxBreakup();
													taxBreakup.tax = Double.parseDouble(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX));
													taxBreakup.chargeCategory = elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY);
													taxBreakup.chargeName = elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_NAME);
													taxBreakup.taxName = elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME);
													taxBreakup.reference1 = elemLineTax.getAttribute("Reference_1");
													if(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT) != null && !elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT).equals("")){
														taxBreakup.taxPercentage=Double.parseDouble(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT));
													}
													taxBreakUpList.add(taxBreakup);
													continue;
												}
												if(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME) != null 
														&& !elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME).equals("")){
	//												
													if(elemLineTax.getAttribute(KohlsConstant.ATTR_RMNG_TAX) != null && !elemLineTax.getAttribute(KohlsConstant.ATTR_RMNG_TAX).equals("")){
														remainingTaxAmnt=new BigDecimal(elemLineTax.getAttribute(KohlsConstant.ATTR_RMNG_TAX));
													}
													if(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX) != null && !elemLineTax.getAttribute(KohlsConstant.ATTR_TAX).equals("")){
														taxAmnt=new BigDecimal(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX));
													}
													//Instantiate a new YFSExtnTaxBreakup object and populate it with the
													//taxName, taxPercentage, chargeName, chargeCategory and the calculated taxPerLine
													taxBreakup = new YFSExtnTaxBreakup();
													if(YFCLogUtil.isDebugEnabled()){
														log.debug("***while looping thru taxLines***");
														log.debug("Tax Name from G.O.D's o/p: "+ elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME));
														log.debug("Charge Category from G.O.D's o/p: "+ elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY));
														log.debug("Charge Name from G.O.D's o/p: "+ elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_NAME));
														log.debug("RemainingTaxAmount from G.O.D's o/p: "+ elemLineTax.getAttribute(KohlsConstant.ATTR_RMNG_TAX));
														log.debug("TaxAmount from G.O.D's o/p: "+ elemLineTax.getAttribute(KohlsConstant.ATTR_TAX));
													}													
													if(originalOrderedQuantity.compareTo(zero) != 0){
														BigDecimal taxPerLine = null;
														if(inStruct.bLastInvoiceForOrderLine == true){ //i.e. last shipment
															//in case of last shipment, instead f making all calculations,
															// we simply assign the value of RemainingTaxAmount to taxPerLine
															taxPerLine = remainingTaxAmnt.setScale(2);
															log.debug("Last Shipment...RemainingTaxAmount = "+remainingTaxAmnt + ",taxPerLine= "+taxPerLine);
														}else{
															taxPerLine = taxAmnt.multiply(bdShipmtQty).divide(originalOrderedQuantity,2,1);
														}
														log.debug("TaxPerLine--->" + taxPerLine + " for " + elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME));
														taxBreakup.tax = Double.parseDouble(taxPerLine.toString());
													}
													taxBreakup.chargeCategory = elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY);
													taxBreakup.chargeName = elemLineTax.getAttribute(KohlsConstant.ATTR_CHARGE_NAME);
													taxBreakup.taxName = elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_NAME);
													taxBreakup.reference1 = elemLineTax.getAttribute("Reference_1");
													if(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT) != null && !elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT).equals("")){
														taxBreakup.taxPercentage=Double.parseDouble(elemLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT));
													}
//													Finally add this YFSExtnTaxBreakup object to the taxBreakUpList list											
													taxBreakUpList.add(taxBreakup);
												}
											}
											
										}
										
										
										//Once we have zeroed-in on our orderLine, we dont have to look at the other orderLines
										//Hence we break out of the for loop								
										break;
									}
								}
							}
							
						
					
				}
			}
	//		
			outStruct.colTax = taxBreakUpList;
//			This is just for debugging purposes
			log.debug("########Output struct results#############");
			List outputList = outStruct.colTax;
			Iterator it = outputList.iterator();
			int i=0;
			while(it.hasNext()){
				
				 YFSExtnTaxBreakup taxBreakup1 = (YFSExtnTaxBreakup) it.next();
				i++;
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Taxes of type : " + i);
					log.debug("taxName : " + taxBreakup1.taxName);
					log.debug("chargeName : " + taxBreakup1.chargeName);
					log.debug("chargeCategory: " + taxBreakup1.chargeCategory);
					log.debug("taxPercentage: " + taxBreakup1.taxPercentage);
					log.debug("tax: " + taxBreakup1.tax);
				}
			}
			 log.debug("***END --> Values from KohlsRecalculateLineTax UE***");
	 
		}
		catch (YFSException ex) {
			log.debug(ex.getMessage());
			throw ex;
		}catch (Exception e) {
			log.debug(e.getMessage());
		}
		return outStruct;
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
	//	Adding orderLines element to Order element
		orderRootElem.appendChild(orderLinesElem);
	//	Creating OrderLine element
		Element orderLineElem = doc.createElement(KohlsConstant.ELEM_ORDER_LINE);
		orderLineElem.setAttribute(KohlsConstant.ATTR_INVOICED_QUANT, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORDERED_QTY, KohlsConstant.BLANK);
		
	//	orderLineElem.setAttribute("OrderingUOM", KohlsConstant.ATTR_STATUS_QTY);
		orderLineElem.setAttribute(KohlsConstant.ATTR_ORIG_ORDERED_QTY, KohlsConstant.BLANK);
	//	orderLineElem.setAttribute("PipelineKey", KohlsConstant.BLANK);
	//	orderLineElem.setAttribute("PricingDate", KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_PRIME_LINE_NO, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_RCVD_QTY, KohlsConstant.BLANK);
	//	orderLineElem.setAttribute("ShipToKey", KohlsConstant.ATTR_CHARGE_CATEGORY);
		orderLineElem.setAttribute(KohlsConstant.ATTR_SHIPPED_QTY, KohlsConstant.BLANK);
		orderLineElem.setAttribute(KohlsConstant.ATTR_SUB_LINE_NO, KohlsConstant.BLANK);
	//	Adding orderLine element to OrderLines element
		orderLinesElem.appendChild(orderLineElem);
	//	Creating OrderStatuses element
		Element orderStatusesElem = doc.createElement(KohlsConstant.ELEM_ORD_STATUSES);
	//	Adding OrderStatuses element to OrderLine element
		orderLineElem.appendChild(orderStatusesElem);
	//	Creating OrderStatus element
		Element orderStatusElem = doc.createElement(KohlsConstant.ELEM_ORDER_STATUS);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_ORD_HDR_KEY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_ORDER_LINE_KEY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_RECV_NODE, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_SHIP_NODE, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_STATUS, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_STATUS_QTY, KohlsConstant.BLANK);
		orderStatusElem.setAttribute(KohlsConstant.ATTR_TOT_QTY, KohlsConstant.BLANK);
	//	Adding OrderStatus element to OrderStatuses element
		orderStatusesElem.appendChild(orderStatusElem);
		
	//	Creating LineCharges element
		Element lineChargesElem = doc.createElement(KohlsConstant.ELEM_LINE_CHARGES);
	//	Adding LineCharges element to OrderLine element
		orderLineElem.appendChild(lineChargesElem);
	//	Creating LineCharge element
		Element lineChargeElem = doc.createElement(KohlsConstant.ELEM_LINE_CHARGE);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_AMNT, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_RMNG_CHARGE_AMNT, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_NAME, KohlsConstant.BLANK);
		lineChargeElem.setAttribute(KohlsConstant.ATTR_CHARGE_CATEGORY, KohlsConstant.BLANK);
		
	//	Adding LineCharge element to LineCharges element
		lineChargesElem.appendChild(lineChargeElem);
		
	//	Creating Item element
		Element itemElem = doc.createElement(KohlsConstant.ELEM_ITEM);
	//	Adding Item element to OrderLine element
		orderLineElem.appendChild(itemElem);
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Template Document for getOrderDetails --> "+ XmlUtils.getString(doc));
		}
		return doc;
	
	}
	private  void invoiceHeaderGWTax(YFSEnvironment env, String strOrderLineKey,  String strOrderHeaderKey,
			List<YFSExtnTaxBreakup> taxBreakUpList) throws YFSException,
			RemoteException {
		// get OrderLine details
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS,
				getOrderLineDetailsTemp());
		Document docOrderLineDetails = api.getOrderLineDetails(env,
				getOrderLineDetailsInput(strOrderLineKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LINE_DETAILS);

		NodeList nlExtn = docOrderLineDetails
				.getElementsByTagName(KohlsXMLLiterals.E_EXTN);
		if (nlExtn == null) {
			// if no gift extn return
			return;
		}
		Element elemExtn = (Element) nlExtn.item(0);
		String strWrapTogetherCode = elemExtn
				.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);
		if (strWrapTogetherCode == null || strWrapTogetherCode.equals("")) {
			// if not a Gift line return
			return;
		}
		// since there is a gift line do getOrderDetails to get header taxes
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
				getOrderDetailsTemp());
		Document docOrderDetails = api.getOrderDetails(env,
				getOrderDetailsInput(strOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);

		Element elemHeaderTaxes = (Element) docOrderDetails
				.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAXES)
				.item(0);
		if (elemHeaderTaxes == null) {
			// if no header tax return
			return;
		}
		NodeList nlHeaderTax = elemHeaderTaxes
				.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAX);
		if (nlHeaderTax == null) {
			// if no header tax return
			return;
		}

		// iterate through header taxes
		for (int i = 0; i < nlHeaderTax.getLength(); i++) {
			Element elemHeaderTax = (Element) nlHeaderTax.item(i);
			String strChargeCategory = elemHeaderTax
			.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY);
			double dTaxAmount = Double.valueOf(elemHeaderTax
					.getAttribute(KohlsXMLLiterals.A_TAX));
			if (elemHeaderTax.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME)
					.equals(strWrapTogetherCode)
					&& dTaxAmount != 0.0
					&& env.getTxnObject(strWrapTogetherCode+strChargeCategory) == null) {
				// there is a matching gift wrap header charge
				YFSExtnTaxBreakup yfsExtnTaxBreakup = new YFSExtnTaxBreakup();
				yfsExtnTaxBreakup.tax = dTaxAmount;
				yfsExtnTaxBreakup.chargeName = elemHeaderTax
						.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
				yfsExtnTaxBreakup.chargeCategory = strChargeCategory;
				yfsExtnTaxBreakup.taxName = elemHeaderTax
				.getAttribute(KohlsXMLLiterals.A_TAX_NAME);
				String strTaxPrecentage = elemHeaderTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT);
				if(strTaxPrecentage != null && !strTaxPrecentage.equals("")){
					yfsExtnTaxBreakup.taxPercentage = Double.parseDouble(strTaxPrecentage);
				}
				taxBreakUpList.add(yfsExtnTaxBreakup);
				env.setTxnObject(strWrapTogetherCode+strChargeCategory, strOrderLineKey);
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
		
		YFCElement yfcElemHeaderCharges = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		YFCElement yfcElemHeaderCharge = yfcElemHeaderCharges.createChild(KohlsXMLLiterals.E_HEADER_TAX);
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_TAX, " ");		
		
		return yfcDocGetOrderDetailsTemp.getDocument();
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
	private Document getOrderLineDetailsInput(String strOrderLineKey) {
		YFCDocument yfcDocGetOrderDetail = YFCDocument
				.createDocument(KohlsXMLLiterals.E_ORDER_LINE_DETAIL);
		YFCElement yfcEleGetOrderDetail = yfcDocGetOrderDetail
				.getDocumentElement();
		yfcEleGetOrderDetail.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY,
				strOrderLineKey);
		return yfcDocGetOrderDetail.getDocument();
	}
}
