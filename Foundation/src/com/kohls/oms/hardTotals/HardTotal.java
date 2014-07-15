package com.kohls.oms.hardTotals;

import java.math.BigDecimal;

public class HardTotal {

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
	private double dollarAmntOfGC = 0.0;
	private double noOfGiftCardsSold= 0;
		
	private long giftCardsTenderedCount= 0;
	private long kmrcTenderedCount= 0;
	private long kohlsChrgTenderedCount= 0;
	private long novus3PLNovusTenderedCount= 0;
	private long amex3PLAmexTenderedCount= 0;
	private long bankCardTenderedCount= 0;
	
	private double amntChrgdToAmexCard= 0.0;
	private double amntChrgdToDiscoverCard= 0.0;
	private double amntChrgdToGiftCard= 0.0;
	private double amntChrgdToKMRC= 0.0;
	private double amntChrgdToKohlsChrgCard= 0.0;
	private double amntChrgdToMaster_VisaCard= 0.0;
	
	
	/**
	 * This will go to Control_Total_Read
	 */
	private BigDecimal merchandiseTotal = new BigDecimal(0); 
	
//	private double shippingCharge = 0.0;
//	private double totalDiscount = 0.0;
	private double totalTax = 0.0;	
	
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


	public double getDollarAmntOfGC() {
		return dollarAmntOfGC;
	}


	public void setDollarAmntOfGC(double dollarAmntOfGC) {
		this.dollarAmntOfGC = dollarAmntOfGC;
	}

	public double getNoOfGiftCardsSold() {
		return noOfGiftCardsSold;
	}


	public void setNoOfGiftCardsSold(double noOfGiftCardsSold) {
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


	public double getAmntChrgdToAmexCard() {
		return amntChrgdToAmexCard;
	}


	public void setAmntChrgdToAmexCard(double amntChrgdToAmexCard) {
		this.amntChrgdToAmexCard = amntChrgdToAmexCard;
	}


	public double getAmntChrgdToDiscoverCard() {
		return amntChrgdToDiscoverCard;
	}


	public void setAmntChrgdToDiscoverCard(double amntChrgdToDiscoverCard) {
		this.amntChrgdToDiscoverCard = amntChrgdToDiscoverCard;
	}


	public double getAmntChrgdToGiftCard() {
		return amntChrgdToGiftCard;
	}


	public void setAmntChrgdToGiftCard(double amntChrgdToGiftCard) {
		this.amntChrgdToGiftCard = amntChrgdToGiftCard;
	}


	public double getAmntChrgdToKMRC() {
		return amntChrgdToKMRC;
	}


	public void setAmntChrgdToKMRC(double amntChrgdToKMRC) {
		this.amntChrgdToKMRC = amntChrgdToKMRC;
	}


	public double getAmntChrgdToKohlsChrgCard() {
		return amntChrgdToKohlsChrgCard;
	}


	public void setAmntChrgdToKohlsChrgCard(double amntChrgdToKohlsChrgCard) {
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

	public double getTotalTax() {
		return totalTax;
	}


	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;
	}


	public double getAmntChrgdToMaster_VisaCard() {
		return amntChrgdToMaster_VisaCard;
	}


	public void setAmntChrgdToMaster_VisaCard(double amntChrgdToMasterVisaCard) {
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