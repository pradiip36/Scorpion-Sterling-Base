package com.kohls.oms.hardTotals;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

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
import com.yantra.ycp.greex.library.DoubleAdd;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsLoadHardTotalsData implements YIFCustomApi{

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsLoadHardTotalsData.class.getName());
	private YIFApi api;
	

	public KohlsLoadHardTotalsData() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public void loadTable(YFSEnvironment env, Document inXML)
	throws YFSException, RemoteException, TransformerException,ParserConfigurationException, NumberFormatException, YIFClientCreationException {
		log.debug("Input XML to loadTable method of LoadHardTotalsDataAPI class---> \n " + XMLUtil.getXMLString(inXML));
		
		
		Element rootElem = inXML.getDocumentElement();
		if(rootElem != null){
			Element invoiceHdrElem = (Element)rootElem.getElementsByTagName(KohlsXMLLiterals.E_INVOICE_HEADER).item(0);
			int noOfVisaCards = 0;
			int noOfMasterCards = 0;
			int noOfDiscoverCards = 0;
			int noOfAmexCards = 0;
			int noOfGiftCards = 0;
			
			int noOfKMRCCards = 0;
			int noOfKohlsChrgCards = 0;
			
			int noOf3rdPartyVisaMasterCard = 0;
			int noOf3rdPartyDiscoverCard = 0;
			int noOf3rdPartyAmexCard = 0;
			
			int noOfGiftCardsSold = 0;
			BigDecimal dollarOfGiftCardsSold = new BigDecimal("0.00");
			
			BigDecimal amntPaidByVisa = new BigDecimal("0.00");
			BigDecimal amntPaidByMaster = new BigDecimal("0.00");
			BigDecimal amntPaidByDiscover = new BigDecimal("0.00");
			BigDecimal amntPaidByAmex = new BigDecimal("0.00");
			BigDecimal amntPaidByGiftCard = new BigDecimal("0.00");
			BigDecimal amntPaidByKMRC = new BigDecimal("0.00");
			BigDecimal amntPaidByChrgCard = new BigDecimal("0.00");
			
			BigDecimal amntPaidBy3rdPartyVisaMaster = new BigDecimal("0.00");
			BigDecimal amntPaidBy3rdPartyDiscoverCard= new BigDecimal("0.00");
			BigDecimal amntPaidBy3rdPartyAmexCard = new BigDecimal("0.00");
			
			
			
			if(invoiceHdrElem != null){
				Document createKohlsHardTotalsInDoc = XMLUtil.createDocument(KohlsXMLLiterals.E_KL_HARD_TOTALS);
				Element hardTotalsRootElem = createKohlsHardTotalsInDoc.getDocumentElement();
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_MERCHANDISE_TOTAL, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED));
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_SALES_TRANS_CNT, KohlsConstant.SALES_TRANS_CNT_VAL);
				if (invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TIME_STAMP).length() >= 10)
				{
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TRAN_DATE, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TIME_STAMP).substring(0, 10).concat("T00:00:00") );
				}
				else
				{
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TRAN_DATE, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TIME_STAMP));
				}
				
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_INVOICE_NO, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_INVOICE_NO));
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_ORDER_INVOICE_KEY));
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TOTAL_TAX_ON_INVOICE, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TOTAL_TAX));
				if(this.hasValue(invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TOTAL_TAX)) && Double.parseDouble(invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TOTAL_TAX)) > 0){
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TRANS_WITH_TAXES, KohlsConstant.TRANS_WITH_TAXES_VAL);
				}
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TOTAL_DISCS, invoiceHdrElem.getAttribute(KohlsXMLLiterals.A_TOTAL_DISC));
				hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_POS_DATE, this.currentTime());
				
//				Get ReceiptID from Extn element
				Element extnElement = (Element) invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				if(extnElement != null){
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_RECEIPT_ID, extnElement.getAttribute(KohlsXMLLiterals.A_EXTN_RECEIPT_ID));
				}
				
//				Get OrderNo & OrderDate from Order element
				Element orderElement = (Element) invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
				if(orderElement != null){
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderElement.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_ORDER_DATE, orderElement.getAttribute(KohlsXMLLiterals.A_ORDER_DATE));
				}
				
				NodeList listOfLineDetail = invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_LINE_DETAIL);
				if(listOfLineDetail.getLength() > 0){
					for(int i=0; i< listOfLineDetail.getLength(); i++){
						Element lineDtlElem = (Element)listOfLineDetail.item(i);
						if(lineDtlElem != null){
							Element orderLineElem = (Element)lineDtlElem.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).item(0);
							if(orderLineElem != null){
								Element itemElement = (Element)orderLineElem.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
								if(itemElement != null){
									String sLineType = orderLineElem.getAttribute(KohlsXMLLiterals.A_LINE_TYPE);
									if(sLineType.equals(KohlsConstant.VIRTUAL_GIFT_CARD) || sLineType.equals(KohlsConstant.PLASTIC_GIFT_CARD_LINE_TYPE)){
										if(this.hasValue(lineDtlElem.getAttribute(KohlsXMLLiterals.A_SHIP_QTY))){
											noOfGiftCardsSold = noOfGiftCardsSold + (new Double(lineDtlElem.getAttribute(KohlsXMLLiterals.A_SHIP_QTY))).intValue();
										}
										if(this.hasValue(lineDtlElem.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE))){
											dollarOfGiftCardsSold = dollarOfGiftCardsSold.add(new BigDecimal(lineDtlElem.getAttribute(KohlsXMLLiterals.A_UNIT_PRICE)));
										}
									}
								}
							}
						}						
					}
				}
				
				//Loop thru diff CollectionDetail elements and find out the type of payment i.e GiftCard of CreditCard
				// and the type of Credit Card used and the amount charged from that particular card
				
				NodeList listOfCollectionDetail = invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_COLLECITON_DETAIL);
				if(listOfCollectionDetail.getLength() > 0){
					for(int i=0; i< listOfCollectionDetail.getLength(); i++){
						Element collectionDtlElem = (Element)listOfCollectionDetail.item(i);
						Element payMethodElem = (Element) collectionDtlElem.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD).item(0);
						if(payMethodElem != null){
							if(payMethodElem.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equalsIgnoreCase(KohlsConstant.GIFT_CARD)){
								if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.KMRC)){
									noOfKMRCCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByKMRC = amntPaidByKMRC.add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.KOHLS_GIFT_CARD)){
									noOfGiftCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByGiftCard = amntPaidByGiftCard .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}	
							}
							else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD)){
								if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_VISA)){
									noOfVisaCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByVisa = amntPaidByVisa .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_AMEX)){
									noOfAmexCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByAmex = amntPaidByAmex .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_DISCOVER)){
									noOfDiscoverCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByDiscover = amntPaidByDiscover .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_MSTR_CARD)){
									noOfMasterCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByMaster = amntPaidByMaster .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.KOHLS_CHARGE_CARD)){
									noOfKohlsChrgCards++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidByChrgCard = amntPaidByChrgCard .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
							}
							else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_PAYMENT_TYPE).equalsIgnoreCase(KohlsConstant.PAYMENT_TYPE_3PL_GIFT_CARD)){
								if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_VISA)){
									noOf3rdPartyVisaMasterCard++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidBy3rdPartyVisaMaster = amntPaidBy3rdPartyVisaMaster .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_AMEX)){
									noOf3rdPartyAmexCard++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidBy3rdPartyAmexCard = amntPaidBy3rdPartyAmexCard .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_DISCOVER)){
									noOf3rdPartyDiscoverCard++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidBy3rdPartyDiscoverCard = amntPaidBy3rdPartyDiscoverCard .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
								else if(payMethodElem.getAttribute(KohlsXMLLiterals.A_CREDIT_CARD_TYPE).equalsIgnoreCase(KohlsConstant.CREDIT_CARD_MSTR_CARD)){
									noOf3rdPartyVisaMasterCard++;
									if(this.hasValue(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED))){
										amntPaidBy3rdPartyVisaMaster = amntPaidBy3rdPartyVisaMaster .add(new BigDecimal(collectionDtlElem.getAttribute(KohlsXMLLiterals.A_AMOUNT_COLLECTED)));
									}
								}
							}
						}
					}
					//Update the table with the no of cards used in this invoice/transaction
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_GIFT_CARDS_TENDERED_CNT, String.valueOf(noOfGiftCards));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_KMRC_TENDERED_CNT, String.valueOf(noOfKMRCCards));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_KOHLS_CHRG_TENDERED_CNT, String.valueOf(noOfKohlsChrgCards));
					//This is sum of Visa and Master Cards used, including 3rd party gift cards with Visa/MC
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_BANK_CARD_TENDERED_CNT, String.valueOf(noOfMasterCards + noOfVisaCards + noOf3rdPartyVisaMasterCard));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_VISA_CARD_TEND_CNT, String.valueOf(noOfVisaCards));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_MSTR_CARD_TEND_CNT, String.valueOf(noOfMasterCards));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_NOVUS_TEND_CNT, String.valueOf(noOfDiscoverCards));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMEX_TEND_CNT, String.valueOf(noOfAmexCards));
					
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_NOVUS3PL_TENDERED_CNT, String.valueOf(noOfDiscoverCards + noOf3rdPartyDiscoverCard));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMEX3PL_TENDERED_CNT, String.valueOf(noOfAmexCards + noOf3rdPartyAmexCard));
					
//					Update the table with the amount collected/charged from each of the cards used in this invoice/transaction
					
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_GFT_CARD, String.valueOf(amntPaidByGiftCard));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_KMRC, String.valueOf(amntPaidByKMRC));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_KOHLS_CHRG_CARD, String.valueOf(amntPaidByChrgCard));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHRGD_TO_VISA_CARD, String.valueOf(amntPaidByVisa));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHRGD_TO_MSTR_CARD, String.valueOf(amntPaidByMaster));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHRGD_TO_DISC_CARD, String.valueOf(amntPaidByDiscover));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMNT_CHRGD_TO_AMEX_CARD, String.valueOf(amntPaidByAmex));
					BigDecimal novusSaleRead = amntPaidByDiscover.add(amntPaidBy3rdPartyDiscoverCard);
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_NOVUS_SALE_READ, novusSaleRead.toString());
					BigDecimal amexSaleRead = amntPaidByAmex.add(amntPaidBy3rdPartyAmexCard);
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_AMEX_SALE_READ, amexSaleRead.toString());
					BigDecimal masterSaleRead = amntPaidByVisa.add(amntPaidByMaster).add(amntPaidBy3rdPartyVisaMaster);
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_MASTER_VISA_SALE_READ, masterSaleRead.toString());
					
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_GIFT_CARDS_ISSUED_CNT, String.valueOf(noOfGiftCardsSold));
					hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_TOTAL_VALUE_OF_GIFT_CARDS, dollarOfGiftCardsSold.toString());
					
				}
				
				
				Element elemExtn = (Element) invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
				if(elemExtn != null){					 
						hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_STORE_NUMBER,
								elemExtn.getAttribute(KohlsXMLLiterals.A_EXTN_SHIP_NODE));
					 
				}
				
//				Get ShippingCharge from ChargeSummaryDetail
				NodeList listOfChrgSummaryDtl =  invoiceHdrElem.getElementsByTagName(KohlsXMLLiterals.E_CHARGE_SUMMARY_DETAIL);
				if(listOfChrgSummaryDtl.getLength() > 0){
					for(int j =0; j<listOfChrgSummaryDtl.getLength(); j++){
						Element chrgSummaryDtl = (Element)listOfChrgSummaryDtl.item(j);
						if(chrgSummaryDtl != null && chrgSummaryDtl.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY).equalsIgnoreCase(KohlsConstant.ShippingChargeCategory)){
							hardTotalsRootElem.setAttribute(KohlsXMLLiterals.A_SHIPPING_CHARGE, chrgSummaryDtl.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT));
							break;
						}
					}
				}
				
				this.api.executeFlow(env, KohlsConstant.CREATE_HARD_TOTALS_SERVICE, createKohlsHardTotalsInDoc);
				log.debug("Input XML to CreateHardTotalsService ---> \n " + XMLUtil.getXMLString(createKohlsHardTotalsInDoc));
			}
		}
		
	}
	
	private String currentTime(){
		Calendar today = Calendar.getInstance();  
		
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.INV_DATE_FORMAT);	
		String currentTime = sdf.format(today.getTime());
		
		log.debug("currenttime : "+currentTime);
		return currentTime;
	}
	
	private boolean hasValue(String value){
		if(value != null && !value.equalsIgnoreCase("")){
			return true;
		}
		else{
			return false;
		}
	}
	
	
}