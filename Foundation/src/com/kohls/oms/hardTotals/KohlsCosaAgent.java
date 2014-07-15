package com.kohls.oms.hardTotals;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.sql.Connection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.StringUtil;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.is.co.calendar.CalendarType;
import com.kohls.is.co.calendar.KohlsCalendarException;
import com.kohls.is.co.calendar.KohlsCalendarImplementation;
import com.kohls.oms.ue.KohlsBeforeCreateOrderUE;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsCosaAgent extends YCPBaseAgent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsBeforeCreateOrderUE.class.getName());
	private YIFApi api;
	private Connection m_conn;
	
	public KohlsCosaAgent() throws YIFClientCreationException {	

			api = YIFClientFactory.getInstance().getLocalApi();	

	}
	
	String dateOfReportGeneration = "";
	@Override
	public List getJobs(YFSEnvironment env, Document criteria,
			Document lastMessageCreated) throws Exception {
		// TODO Auto-generated method stub
		
		List listOfJobs = new ArrayList();
		
		if(lastMessageCreated == null){
			Calendar today = Calendar.getInstance();  
//			If report/agent is being run before 3am or if it is being re-run on the same day, 
//			agent will not be allowed to run
			if(today.get(Calendar.HOUR_OF_DAY) > Integer.parseInt(KohlsUtil.getCommonCodeValue(env, KohlsConstant.HARD_TOTAL_HOUR)) && !this.wasReportRunEarlierToday(env)){
				int iNumOfRecordsProcessed = 0;
				Map shipNodesMap = this.getShipNodes(env);
//				Max records being fetched at a time from the DB
				int maxRecords = Integer.parseInt(KohlsUtil.getCommonCodeValue(env, KohlsConstant.HARD_TOTAL_RECORDS));
				today.add(Calendar.DATE, Integer.parseInt(KohlsUtil.getCommonCodeValue(env, KohlsConstant.HARD_TOTAL_DAY))); 
				SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.HD_DATE_FORMAT);
				String transactionDate = sdf.format(today.getTime());
				Set keySet = shipNodesMap.keySet();
				Iterator keySetIterator = keySet.iterator();
				while(keySetIterator.hasNext()){
					String shipNode = (String)keySetIterator.next();
				
					Document getHardTotalsListInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_KL_HARD_TOTALS);
					Element rootElem = getHardTotalsListInDoc.getDocumentElement();
					rootElem.setAttribute(KohlsXMLLiterals.A_STORE_NUMBER, shipNode);
					rootElem.setAttribute(KohlsXMLLiterals.A_MAXIMUM_RECORDS, String.valueOf(maxRecords));
					rootElem.setAttribute(KohlsXMLLiterals.A_TRAN_DATE, transactionDate);
					Element orderByElem = XmlUtils.createChild(rootElem, KohlsXMLLiterals.E_ORDER_BY);
					Element attributeElem = XmlUtils.createChild(orderByElem, KohlsXMLLiterals.E_ATTRIBUTE);
					attributeElem.setAttribute(KohlsXMLLiterals.A_NAME, KohlsConstant.HARD_TOTALS_KEY);
					attributeElem.setAttribute(KohlsXMLLiterals.A_DESC, KohlsConstant.NO);
					Document getHardTotalsListOutDoc = this.api.executeFlow(env, KohlsConstant.SERVICE_GET_HARD_TOTALS_LIST, getHardTotalsListInDoc);
					
//					Converting the TransactionDate attribute to to Date
					Document getHardTotalListDoc=convertDate(getHardTotalsListOutDoc);
					
					Element outputRootElem = getHardTotalListDoc.getDocumentElement();
					List<KohlsHardTotal> listHardTotals = new ArrayList<KohlsHardTotal>();
					if(outputRootElem != null && this.hasValue(outputRootElem.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS))){
						
						int totalRecords = Integer.parseInt(outputRootElem.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS));
						iNumOfRecordsProcessed += totalRecords;
						if(totalRecords == 0){
							continue;
						}
						
//						Calculating count to find out how many times GetHardTotalsListService has to be called,
//						after the first call
						String lastHardTotalKey = "";
						int count = 0;
						if(totalRecords > maxRecords){
							if(totalRecords%maxRecords == 0){
								count = (totalRecords/maxRecords) - 1; 
							}
							else{
								count = (totalRecords/maxRecords);
							}
						}
						NodeList listOfHardTotals = outputRootElem.getElementsByTagName(KohlsXMLLiterals.E_KL_HARD_TOTALS);
						Map<String, List<Element>> mapRegNoHardTotal = new HashMap<String, List<Element>>();
						for(int i=0; i < listOfHardTotals.getLength(); i++){
							Element klHardTotalsElem = (Element)listOfHardTotals.item(i);
							if(klHardTotalsElem != null){
								//this.pupulateHardTotalObject(hardTotal, klHardTotalsElem);
								createMapRegNoHardTotals(mapRegNoHardTotal,	klHardTotalsElem);	
								if(i == listOfHardTotals.getLength()-1){
									lastHardTotalKey = klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_HARD_TOTALS_KEY);
								}
							}
						}
						for(int j=0; j < count; j++){
							rootElem.setAttribute(KohlsXMLLiterals.A_HARD_TOTALS_KEY, lastHardTotalKey);
							rootElem.setAttribute(KohlsXMLLiterals.A_HARD_TOTALS_KEY_QRY_TP, KohlsConstant.HD_GT);
							getHardTotalsListOutDoc = this.api.executeFlow(env, KohlsConstant.SERVICE_GET_HARD_TOTALS_LIST, getHardTotalsListInDoc);
							
//							Converting the TransactionDate attribute to to Date							
							getHardTotalListDoc=convertDate(getHardTotalsListOutDoc);
							
							outputRootElem = getHardTotalListDoc.getDocumentElement();
							if(outputRootElem != null){
								listOfHardTotals = outputRootElem.getElementsByTagName(KohlsXMLLiterals.E_KL_HARD_TOTALS);
								for(int i=0; i < listOfHardTotals.getLength(); i++){
									Element klHardTotalsElem = (Element)listOfHardTotals.item(i);
									if(klHardTotalsElem != null){
										//this.pupulateHardTotalObject(hardTotal, klHardTotalsElem);
										createMapRegNoHardTotals(mapRegNoHardTotal,	klHardTotalsElem);	
										if(i == listOfHardTotals.getLength()-1){
											lastHardTotalKey = klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_HARD_TOTALS_KEY);
										}
									}
								}
							}
						}	
						// group by RegNo., populate hardtotals
						Set<String> setRegNo = mapRegNoHardTotal.keySet();						
						for(String sRegNo : setRegNo){
							List<Element> elemKLHardTotals = mapRegNoHardTotal.get(sRegNo);
							KohlsHardTotal hardTotals = new KohlsHardTotal();
							for(Element elemKLHardTotal : elemKLHardTotals){
								pupulateHardTotalObject(hardTotals, elemKLHardTotal);
							}	
							setKohlsAccountingTime(hardTotals);
							hardTotals.setRegisterID(sRegNo);
							hardTotals.setTransSalesDate(transactionDate);
							hardTotals.setPosDate(new Date().toString());
							hardTotals.setStoreNumber(shipNode);	
							hardTotals.setTotalNoOfTransactions(elemKLHardTotals.size());
							listHardTotals.add(hardTotals);
						}
					}
					// Populating Header fields
					Document hardTotalDoc = createHardTotalXML(listHardTotals, this.findSequenceValues(env));
					listOfJobs.add(hardTotalDoc);	
				}
				if(iNumOfRecordsProcessed > 0){
					this.updateDateOfReportGeneration(env);
				}
			}
		}
		return listOfJobs;
	}
	
	
	/**
	 * This method converts the TransactionDate from TimeStamp datatype to Date
	 * 
	 */
	private Document convertDate(Document doc) {
		try{
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("<------------ Begining of convertDate() -------------------->");
			}

			Element eleKLHardTotalsList = doc.getDocumentElement();

			NodeList nlKLHardTotals = eleKLHardTotalsList.getElementsByTagName(KohlsXMLLiterals.E_KL_HARD_TOTALS);

			for (int i=0;i<nlKLHardTotals.getLength();i++){

				Element eleKLHardTotals=(Element)nlKLHardTotals.item(i);
				String strTransactionDate=eleKLHardTotals.getAttribute(KohlsXMLLiterals.A_TRAN_DATE);
				DateFormat formatter = new SimpleDateFormat(KohlsConstant.HD_DATE_FORMAT);
				Date date = formatter.parse(strTransactionDate);
				eleKLHardTotals.setAttribute(KohlsXMLLiterals.A_TRAN_DATE, formatter.format(date));

			}
			if(YFCLogUtil.isDebugEnabled()){
				log.debug("Output from convertDate method : "+XMLUtil.getXMLString(doc));
			}
			return doc;
		}catch(Exception e){
			e.printStackTrace();
			throw new YFSException("Exception in method convertDate : "+e.getStackTrace());
		}

	}
	
	
	private void createMapRegNoHardTotals(
			Map<String, List<Element>> mapRegNoHardTotal,
			Element klHardTotalsElem) {
		// create Map with Reg Num as key and value KLHardTotal Element
		String sReceiptId = klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_RECEIPT_ID);
		String sLastSixChars =  getLastnCharacters(sReceiptId, 6);
		// get first 2 chars as RegNo.
		String sRegNo = sLastSixChars.substring(0, 2);
		if(mapRegNoHardTotal.containsKey(sRegNo)){
			List<Element> listHardTotalsElement = mapRegNoHardTotal.get(sRegNo);
			listHardTotalsElement.add(klHardTotalsElem);
		}else{
			List<Element> listHardTotalsElement = new ArrayList<Element>();
			listHardTotalsElement.add(klHardTotalsElem);
			mapRegNoHardTotal.put(sRegNo, listHardTotalsElement);
		}
	}
	private String getLastnCharacters(String inputString,
            int subStringLength){
		int length = inputString.length();
		if(length <= subStringLength){
		return inputString;
		}
		int startIndex = length-subStringLength;
		return inputString.substring(startIndex);
	}
	private void setKohlsAccountingTime(KohlsHardTotal hardTotal) throws RemoteException {
		
		  try {
			KohlsCalendarImplementation fiscalCalendar = 
				new KohlsCalendarImplementation(new CalendarType(CalendarType.FISCAL_454_CALENDAR));
			Calendar aCalendar = Calendar.getInstance();
			aCalendar.setTime(new Date());
			
			hardTotal.setKohlsAccountingWeek(Integer.toString(fiscalCalendar.getWeekOfFiscalPeriod((GregorianCalendar) aCalendar)));
			hardTotal.setKohlsAccountingMonth(Integer.toString(fiscalCalendar.getPeriodInFiscalYear((GregorianCalendar) aCalendar)));
			hardTotal.setKohlsAccountingYear(Integer.toString(fiscalCalendar.getFiscalYearCCYY((GregorianCalendar) aCalendar)));
			hardTotal.setDayoftheWeek(Integer.toString(fiscalCalendar.getDayOfWeek((GregorianCalendar) aCalendar)));
					
			
		} catch (KohlsCalendarException e) {
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug(e.getMessage());
			}
			
		} 
		
	}

	private Map getShipNodes(YFSEnvironment env) throws YFSException, RemoteException {

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<--------- Beginning of getShipNodes Method ------------------>");
		}
		Map<String,KohlsHardTotal> shipNodesMap = new HashMap<String,KohlsHardTotal>();
		// TODO Auto-generated method stub
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.CODE_SHIP_NODES);
		
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Input for getCommonCodeList : \n" + XMLUtil.getXMLString(yfcDocGetReturnCodeList.getDocument()));
		}
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_COMMON_CODE_LIST);

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Output for getCommonCodeList : \n" + XMLUtil.getXMLString(docGetCommonCodeOutputXML));
		}
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		for(int i=0;i<ndlstCommonCodeList.getLength();i++){

			Element eleCommonCode = (Element)ndlstCommonCodeList.item(i);
			shipNodesMap.put(eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE), new KohlsHardTotal());		
		}

		if(YFCLogUtil.isDebugEnabled()){
			log.debug("Key value pairs of shipNodesMap ---> " + shipNodesMap.toString() + 
			" \n <--------- End of getShipNodes Method ------------------>");
		}
		return shipNodesMap;
	}
	
	private boolean hasValue(String value){
		if(value != null && !value.equalsIgnoreCase("")){
			return true;
		}
		else{
			return false;
		}
	}
	@Override
	public void executeJob(YFSEnvironment env, Document inXML) throws Exception {
		// TODO Auto-generated method stub
		this.api.executeFlow(env, KohlsConstant.SERVICE_DROP_HARD_TOTALS, inXML);
	}
	
	private void pupulateHardTotalObject(KohlsHardTotal hardTotalObj, Element klHardTotalsElem){

		hardTotalObj.setGiftCardsTenderedCount(hardTotalObj.getGiftCardsTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_GIFT_CARDS_TENDERED_CNT)));
		hardTotalObj.setKmrcTenderedCount(hardTotalObj.getKmrcTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_KMRC_TENDERED_CNT)));
		hardTotalObj.setKohlsChrgTenderedCount(hardTotalObj.getKohlsChrgTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_KOHLS_CHRG_TENDERED_CNT)));
		hardTotalObj.setNovus3PLNovusTenderedCount(hardTotalObj.getNovus3PLNovusTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_NOVUS3PL_TENDERED_CNT)));
		hardTotalObj.setAmex3PLAmexTenderedCount(hardTotalObj.getAmex3PLAmexTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_AMEX3PL_TENDERED_CNT)));
		hardTotalObj.setBankCardTenderedCount(hardTotalObj.getBankCardTenderedCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_BANK_CARD_TENDERED_CNT)));
		
		BigDecimal amexChrgAmount = hardTotalObj.getAmntChrgdToAmexCard().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_AMEX_SALE_READ)));
		hardTotalObj.setAmntChrgdToAmexCard(amexChrgAmount);
		BigDecimal discoverChrgAmount = hardTotalObj.getAmntChrgdToDiscoverCard().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_NOVUS_SALE_READ)));
		hardTotalObj.setAmntChrgdToDiscoverCard(discoverChrgAmount);
		BigDecimal giftCardChrgAmount = hardTotalObj.getAmntChrgdToGiftCard().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_GFT_CARD)));
		hardTotalObj.setAmntChrgdToGiftCard(giftCardChrgAmount);
		BigDecimal kMRCChrgAmount =hardTotalObj.getAmntChrgdToKMRC().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_KMRC)));
		hardTotalObj.setAmntChrgdToKMRC(kMRCChrgAmount);
		BigDecimal kohlsChrgCardAmount = hardTotalObj.getAmntChrgdToKohlsChrgCard().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_AMNT_CHARGED_TO_KOHLS_CHRG_CARD)));
		hardTotalObj.setAmntChrgdToKohlsChrgCard(kohlsChrgCardAmount);
		BigDecimal masterChrgAmount = hardTotalObj.getAmntChrgdToMaster_VisaCard().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_MASTER_VISA_SALE_READ)));
		hardTotalObj.setAmntChrgdToMaster_VisaCard(masterChrgAmount);

		hardTotalObj.setSalesTransactionsCount(hardTotalObj.getSalesTransactionsCount() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_SALES_TRANS_CNT)));
		hardTotalObj.setTransactionsWithTax(hardTotalObj.getTransactionsWithTax() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_TRANS_WITH_TAXES)));
		hardTotalObj.setMerchandiseTotal(hardTotalObj.getMerchandiseTotal().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_MERCHANDISE_TOTAL))));

		hardTotalObj.setNoOfGiftCardsSold(hardTotalObj.getNoOfGiftCardsSold() + Long.parseLong(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_GIFT_CARDS_ISSUED_CNT)));
		BigDecimal totalTaxAmount = hardTotalObj.getTotalTax().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_TOTAL_TAX_ON_INVOICE)));
		hardTotalObj.setTotalTax(totalTaxAmount);
		BigDecimal dollarChrgAmount = hardTotalObj.getDollarAmntOfGC().add(new BigDecimal(klHardTotalsElem.getAttribute(KohlsXMLLiterals.A_TOTAL_VALUE_OF_GIFT_CARDS)));
		hardTotalObj.setDollarAmntOfGC(dollarChrgAmount);
		
	}
	
	private Document createHardTotalXML (List<KohlsHardTotal> listHardTotals, String sequenceNo)throws Exception{
//		Populating Header fields
		Document hardTotalDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_HARD_TOTALS);
		Element hardTotalRootElem = hardTotalDoc.getDocumentElement();
		for(KohlsHardTotal hardTotal : listHardTotals){
		Element registerElem = XmlUtils.createChild(hardTotalRootElem, KohlsXMLLiterals.E_REGISTER);
		Element headerElem = XmlUtils.createChild(registerElem, KohlsXMLLiterals.E_HEADER);
		Element storeNumElem = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_STORE_NUMBER);
		storeNumElem.setTextContent(hardTotal.getStoreNumber());
		Element transactionDate = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_TRANS_SALES_DATE);
		Element posDate = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_POS_DATE);
		Element transactionTime = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_TRANS_TIME);
		
		Element transactionNumber = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_TRANS_NUM);
		Element registerID = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_TERMINAL_REG_ID);
		
		transactionNumber.setTextContent(getLastnCharacters(sequenceNo, 4));
		registerID.setTextContent(hardTotal.getRegisterID());
		transactionDate.setTextContent(hardTotal.getTransSalesDate());
		posDate.setTextContent(hardTotal.getTransSalesDate());
		
		transactionTime.setTextContent(this.getCurrentDateTime(false));
		
		// kohls accounting year, month, week
		Element eleKohlsAccountingYear = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_KOHLS_ACCT_YEAR);
		eleKohlsAccountingYear.setTextContent(hardTotal.getKohlsAccountingYear());
		Element eleKohlsAccountingMonth = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_KOHLS_ACCT_MONTH);
		eleKohlsAccountingMonth.setTextContent(hardTotal.getKohlsAccountingMonth());
		Element eleKohlsAccountingWeek = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_KOHLS_ACCT_WEEK);
		eleKohlsAccountingWeek.setTextContent(hardTotal.getKohlsAccountingWeek());
		Element eleDayoftheWeek = XmlUtils.createChild(headerElem, KohlsXMLLiterals.E_DAY_OF_THE_WEEK);
		eleDayoftheWeek.setTextContent(hardTotal.getDayoftheWeek());
		
		
//		Populating ClosingMajorSequence fields
		Element closingMajorSeqElem = XmlUtils.createChild(registerElem, KohlsXMLLiterals.E_CLOSING_MAJOR_SEQ);
		Element dollarOfGCsSold = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_GIFT_CARDS_SOLD_READ);
		Element amntChrgdToGiftCard = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_GIFT_CARDS_TEND_READ);
		Element amntChrgdToKMRC = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_KMRC_TEND_READ);
		Element amntChrgdToKohlsChrgCard = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_KOHLS_CHG_SALE_READ);
		Element amntChrgdToVisa_Master = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_BANK_CARD_SALE_READ);
		
		Element amntChrgdToDiscoverCard = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_NOVUS_SALE_READ);
		Element amntChrgdToAmexCard = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_AMEX_SALE_READ);
		Element merchandiseTotal = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_CNTRL_TOTAL_READ);
		Element transactionsWithTax = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_SALES_TAX_CNT);
		Element salesTransactionsCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_SALES_CNT);
		Element noOfGiftCardsSold = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_GIFT_CARDS_CNT);
		Element giftCardsTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_GIFT_CARD_TEND_CNT);
		Element kmrcTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_KMRC_TEND_CNT);
		Element kohlsChrgTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_KOHLS_CHRG_SALE_CNT);
		Element bankCardTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_BANK_CARD_SALE_CNT);
		Element novus3PLNovusTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_NOVUS_SALE_CNT);
		Element amex3PLAmexTenderedCount = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_AMEX_SALE_CNT);
		Element totalNoOfTransactions = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_CNTRL_TOTAL_CNT);
		Element totalTax = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_SALES_TAX_READ);
		Element merchandiseTotal1 = XmlUtils.createChild(closingMajorSeqElem, KohlsXMLLiterals.E_SALE_READ);
		
		dollarOfGCsSold.setTextContent(hardTotal.getDollarAmntOfGC().toString());//Dollar amount of GCs sold
		amntChrgdToGiftCard.setTextContent(hardTotal.getAmntChrgdToGiftCard().toString());
		amntChrgdToKMRC.setTextContent(hardTotal.getAmntChrgdToKMRC().toString());
		amntChrgdToKohlsChrgCard.setTextContent(hardTotal.getAmntChrgdToKohlsChrgCard().toString());
		amntChrgdToVisa_Master.setTextContent(hardTotal.getAmntChrgdToMaster_VisaCard().toString());
		
		amntChrgdToDiscoverCard.setTextContent(hardTotal.getAmntChrgdToDiscoverCard().toString());
		amntChrgdToAmexCard.setTextContent(hardTotal.getAmntChrgdToAmexCard().toString());
		merchandiseTotal.setTextContent(hardTotal.getMerchandiseTotal().toString());
		transactionsWithTax.setTextContent(String.valueOf(hardTotal.getTransactionsWithTax()));
		salesTransactionsCount.setTextContent(String.valueOf(hardTotal.getSalesTransactionsCount()));
		noOfGiftCardsSold.setTextContent(String.valueOf(hardTotal.getNoOfGiftCardsSold()));
		giftCardsTenderedCount.setTextContent(String.valueOf(hardTotal.getGiftCardsTenderedCount()));
		kmrcTenderedCount.setTextContent(String.valueOf(hardTotal.getKmrcTenderedCount()));
		
		kohlsChrgTenderedCount.setTextContent(String.valueOf(hardTotal.getKohlsChrgTenderedCount()));
		bankCardTenderedCount.setTextContent(String.valueOf(hardTotal.getBankCardTenderedCount()));
		novus3PLNovusTenderedCount.setTextContent(String.valueOf(hardTotal.getNovus3PLNovusTenderedCount()));
		amex3PLAmexTenderedCount.setTextContent(String.valueOf(hardTotal.getAmex3PLAmexTenderedCount()));
		totalNoOfTransactions.setTextContent(String.valueOf(hardTotal.getTotalNoOfTransactions()));
		totalTax.setTextContent(hardTotal.getTotalTax().toString());
		merchandiseTotal1.setTextContent(hardTotal.getMerchandiseTotal().toString());
		}
		return hardTotalDoc;
	}
	
	private String getCurrentDateTime(boolean bValue){
		Calendar today = Calendar.getInstance();  
		   
		SimpleDateFormat sdf = new SimpleDateFormat(KohlsConstant.INV_DATE_FORMAT);	
		
		String currentDateTime = "";
		if(bValue){
			//To get only Date in yyyy-MM-dd format
			currentDateTime = sdf.format(today.getTime()).substring(0,10);
		}
		else{
			//To get only time in HH:MM format
			currentDateTime = sdf.format(today.getTime()).substring(11,16);
		}
		return currentDateTime;
	}
	
	private boolean wasReportRunEarlierToday(YFSEnvironment env)throws Exception{
		
		Document getCosaReportInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_KL_COSA_REP_RUN);
		Element rootElem = getCosaReportInDoc.getDocumentElement();
		dateOfReportGeneration = this.getCurrentDateTime(true);
		rootElem.setAttribute(KohlsXMLLiterals.A_LAST_RUN_DATE, dateOfReportGeneration);
		
		boolean returnVal = false;
		Document getCosaReportOutDoc = this.api.executeFlow(env, KohlsConstant.SERVICE_GET_COSA_REPORT_RUN_LST, getCosaReportInDoc);
		Element outputRootElem = getCosaReportOutDoc.getDocumentElement();
		if(outputRootElem != null && this.hasValue(outputRootElem.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS))){
			if(Integer.parseInt(outputRootElem.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS)) > 0){
				returnVal = true;
			}
		}
		
		return returnVal;
	}
	
	private void updateDateOfReportGeneration(YFSEnvironment env) throws Exception{
		Document createCosaAgentRunInDoc = XmlUtils.createDocument(KohlsXMLLiterals.E_KL_COSA_REP_RUN);
		Element rootElem = createCosaAgentRunInDoc.getDocumentElement();
		rootElem.setAttribute(KohlsXMLLiterals.A_LAST_RUN_DATE, dateOfReportGeneration);
		this.api.executeFlow(env, KohlsConstant.SERVICE_CREATE_COSA_AGENT_RUN_REC, createCosaAgentRunInDoc);
	}
	
	private String findSequenceValues(YFSEnvironment env){
		String sRegTransValues="";
		try
		{
			this.m_conn = KohlsUtil.getDBConnection(env);
			sRegTransValues = KohlsUtil.getNextRegTransNo(env, this.m_conn);
		}
		catch (Exception e)
		{
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug(e.getMessage());
			}
		}
		return sRegTransValues;
	}

}
