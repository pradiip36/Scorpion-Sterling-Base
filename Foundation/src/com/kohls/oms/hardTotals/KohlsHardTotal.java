package com.kohls.oms.hardTotals;

import java.math.BigDecimal;

public class KohlsHardTotal {

	private String storeNumber = "";
	private String registerID= "";
	private String transactionNum= "";
	private String transSalesDate= "";
	private String posDate= "";
	private String transactionTime= "";
	
	//The dollar amount of Kohls Gift Cards sold for this day.
	/**
	 * This will go to Gift_Cards_Sold_Read
	 */
	private BigDecimal dollarAmntOfGC = new BigDecimal("0.00");
	private long  noOfGiftCardsSold= 0;
		
	private long giftCardsTenderedCount= 0;
	private long kmrcTenderedCount= 0;
	private long kohlsChrgTenderedCount= 0;
	private long novus3PLNovusTenderedCount= 0;
	private long amex3PLAmexTenderedCount= 0;
	private long bankCardTenderedCount= 0;
	
	private BigDecimal amntChrgdToAmexCard= new BigDecimal("0.00");
	private BigDecimal amntChrgdToDiscoverCard= new BigDecimal("0.00");
	private BigDecimal amntChrgdToGiftCard= new BigDecimal("0.00");
	private BigDecimal amntChrgdToKMRC= new BigDecimal("0.00");
	private BigDecimal amntChrgdToKohlsChrgCard= new BigDecimal("0.00");
	private BigDecimal amntChrgdToMaster_VisaCard= new BigDecimal("0.00");
	
	private String  KohlsAccountingYear= "";
	private String  KohlsAccountingMonth= "";
	private String  KohlsAccountingWeek= "";
	private String  DayoftheWeek= "";
	
	public String getKohlsAccountingWeek() {
		return KohlsAccountingWeek;
	}


	public void setKohlsAccountingWeek(String kohlsAccountingWeek) {
		KohlsAccountingWeek = kohlsAccountingWeek;
	}


	public String getKohlsAccountingYear() {
		return KohlsAccountingYear;
	}


	public void setKohlsAccountingYear(String kohlsAccountingYear) {
		KohlsAccountingYear = kohlsAccountingYear;
	}


	public String getKohlsAccountingMonth() {
		return KohlsAccountingMonth;
	}


	public void setKohlsAccountingMonth(String kohlsAccountingMonth) {
		KohlsAccountingMonth = kohlsAccountingMonth;
	}


	public String getDayoftheWeek() {
		return DayoftheWeek;
	}


	public void setDayoftheWeek(String dayoftheWeek) {
		DayoftheWeek = dayoftheWeek;
	}


	
	
	/**
	 * This will go to Control_Total_Read
	 */
	private BigDecimal merchandiseTotal = new BigDecimal(0); 
	

	private BigDecimal totalTax = new BigDecimal("0.00");	
	
	private long transactionsWithTax = 0;
	private long salesTransactionsCount = 0;
	
	private long totalNoOfTransactions = 0;
	
	
	public long getTotalNoOfTransactions() {
		return totalNoOfTransactions;
	}


	public void setTotalNoOfTransactions(long totalNoOfTransactions) {
		this.totalNoOfTransactions = totalNoOfTransactions;
	}


	public long getTransactionsWithTax() {
		return transactionsWithTax;
	}


	public void setTransactionsWithTax(long transactionsWithTax) {
		this.transactionsWithTax = transactionsWithTax;
	}


	public long getSalesTransactionsCount() {
		return salesTransactionsCount;
	}


	public void setSalesTransactionsCount(long salesTransactionsCount) {
		this.salesTransactionsCount = salesTransactionsCount;
	}


	public String getStoreNumber() {
		return storeNumber;
	}


	public void setStoreNumber(String storeNumber) {
		this.storeNumber = storeNumber;
	}


	public String getRegisterID() {
		return registerID;
	}


	public void setRegisterID(String registerID) {
		this.registerID = registerID;
	}


	public String getTransactionNum() {
		return transactionNum;
	}


	public void setTransactionNum(String transactionNum) {
		this.transactionNum = transactionNum;
	}


	public String getTransSalesDate() {
		return transSalesDate;
	}


	public void setTransSalesDate(String transSalesDate) {
		this.transSalesDate = transSalesDate;
	}


	public String getPosDate() {
		return posDate;
	}


	public void setPosDate(String posDate) {
		this.posDate = posDate;
	}


	public String getTransactionTime() {
		return transactionTime;
	}


	public void setTransactionTime(String transactionTime) {
		this.transactionTime = transactionTime;
	}


	public BigDecimal getDollarAmntOfGC() {
		return dollarAmntOfGC;
	}


	public void setDollarAmntOfGC(BigDecimal dollarAmntOfGC) {
		this.dollarAmntOfGC = dollarAmntOfGC;
	}

	public long  getNoOfGiftCardsSold() {
		return noOfGiftCardsSold;
	}


	public void setNoOfGiftCardsSold(long noOfGiftCardsSold) {
		this.noOfGiftCardsSold = noOfGiftCardsSold;
	}


	public long getBankCardTenderedCount() {
		return bankCardTenderedCount;
	}


	public void setBankCardTenderedCount(long bankCardTenderedCount) {
		this.bankCardTenderedCount = bankCardTenderedCount;
	}


	public long getGiftCardsTenderedCount() {
		return giftCardsTenderedCount;
	}


	public void setGiftCardsTenderedCount(long giftCardsTenderedCount) {
		this.giftCardsTenderedCount = giftCardsTenderedCount;
	}


	public long getKmrcTenderedCount() {
		return kmrcTenderedCount;
	}


	public void setKmrcTenderedCount(long kmrcTenderedCount) {
		this.kmrcTenderedCount = kmrcTenderedCount;
	}


	public long getKohlsChrgTenderedCount() {
		return kohlsChrgTenderedCount;
	}


	public void setKohlsChrgTenderedCount(long kohlsChrgTenderedCount) {
		this.kohlsChrgTenderedCount = kohlsChrgTenderedCount;
	}

	public long getNovus3PLNovusTenderedCount() {
		return novus3PLNovusTenderedCount;
	}


	public void setNovus3PLNovusTenderedCount(long novus3plNovusTenderedCount) {
		novus3PLNovusTenderedCount = novus3plNovusTenderedCount;
	}


	public long getAmex3PLAmexTenderedCount() {
		return amex3PLAmexTenderedCount;
	}


	public void setAmex3PLAmexTenderedCount(long amex3plAmexTenderedCount) {
		amex3PLAmexTenderedCount = amex3plAmexTenderedCount;
	}


	public BigDecimal getAmntChrgdToAmexCard() {
		return amntChrgdToAmexCard;
	}


	public void setAmntChrgdToAmexCard(BigDecimal amntChrgdToAmexCard) {
		this.amntChrgdToAmexCard = amntChrgdToAmexCard;
	}


	public BigDecimal getAmntChrgdToDiscoverCard() {
		return amntChrgdToDiscoverCard;
	}


	public void setAmntChrgdToDiscoverCard(BigDecimal amntChrgdToDiscoverCard) {
		this.amntChrgdToDiscoverCard = amntChrgdToDiscoverCard;
	}


	public BigDecimal getAmntChrgdToGiftCard() {
		return amntChrgdToGiftCard;
	}


	public void setAmntChrgdToGiftCard(BigDecimal amntChrgdToGiftCard) {
		this.amntChrgdToGiftCard = amntChrgdToGiftCard;
	}


	public BigDecimal getAmntChrgdToKMRC() {
		return amntChrgdToKMRC;
	}


	public void setAmntChrgdToKMRC(BigDecimal amntChrgdToKMRC) {
		this.amntChrgdToKMRC = amntChrgdToKMRC;
	}


	public BigDecimal getAmntChrgdToKohlsChrgCard() {
		return amntChrgdToKohlsChrgCard;
	}


	public void setAmntChrgdToKohlsChrgCard(BigDecimal amntChrgdToKohlsChrgCard) {
		this.amntChrgdToKohlsChrgCard = amntChrgdToKohlsChrgCard;
	}

	public BigDecimal getMerchandiseTotal() {
		return merchandiseTotal;
	}
	/**
	 * This will go to Control_Total_Read
	 */

	public void setMerchandiseTotal(BigDecimal merchandiseTotal) {
		this.merchandiseTotal = merchandiseTotal;
	}

	public BigDecimal getTotalTax() {
		return totalTax;
	}


	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}


	public BigDecimal getAmntChrgdToMaster_VisaCard() {
		return amntChrgdToMaster_VisaCard;
	}


	public void setAmntChrgdToMaster_VisaCard(BigDecimal amntChrgdToMasterVisaCard) {
		amntChrgdToMaster_VisaCard = amntChrgdToMasterVisaCard;
	}


	@Override
	public String toString() {
		return "HardTotal [amex3PLAmexTenderedCount="
				+ amex3PLAmexTenderedCount + ", amntChrgdToAmexCard="
				+ amntChrgdToAmexCard + ", amntChrgdToDiscoverCard="
				+ amntChrgdToDiscoverCard + ", amntChrgdToGiftCard="
				+ amntChrgdToGiftCard + ", amntChrgdToKMRC=" + amntChrgdToKMRC
				+ ", amntChrgdToKohlsChrgCard=" + amntChrgdToKohlsChrgCard
				+ ", amntChrgdToMaster_VisaCard=" + amntChrgdToMaster_VisaCard
				+ ", bankCardTenderedCount=" + bankCardTenderedCount
				+ ", dollarAmntOfGC=" + dollarAmntOfGC
				+ ", giftCardsTenderedCount=" + giftCardsTenderedCount
				+ ", kmrcTenderedCount=" + kmrcTenderedCount
				+ ", kohlsChrgTenderedCount=" + kohlsChrgTenderedCount
				+ ", merchandiseTotal=" + merchandiseTotal
				+ ", novus3PLNovusTenderedCount=" + novus3PLNovusTenderedCount
				+ ", posDate=" + posDate + ", registerID=" + registerID
				+ ", salesTransactionsCount=" + salesTransactionsCount
				+ ", storeNumber="
				+ storeNumber + ", noOfGiftCardsSold=" + noOfGiftCardsSold + ", totalNoOfTransactions="
				+ totalNoOfTransactions + ", totalTax=" + totalTax
				+ ", transSalesDate=" + transSalesDate + ", transactionNum="
				+ transactionNum + ", transactionTime=" + transactionTime
				+ ", transactionsWithTax=" + transactionsWithTax + "]";
	}

}