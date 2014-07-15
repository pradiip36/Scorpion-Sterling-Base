package com.kohls.bopus.util;

public class KohlsLocDetailsTransaction {
	
	String shipmentNo="";
	String shipmentLineNo=""; 
	String sItemID=""; 
	String desc=""; 
	String barcode=""; 
	String signID="";
	
	public String getShipmentNo() {
		return shipmentNo;
	}
	public void setShipmentNo(String shipmentNo) {
		this.shipmentNo = shipmentNo;
	}
	public String getShipmentLineNo() {
		return shipmentLineNo;
	}
	public void setShipmentLineNo(String shipmentLineNo) {
		this.shipmentLineNo = shipmentLineNo;
	}
	public String getsItemID() {
		return sItemID;
	}
	public void setsItemID(String sItemID) {
		this.sItemID = sItemID;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getSignID() {
		return signID;
	}
	public void setSignID(String signID) {
		this.signID = signID;
	}
	
}