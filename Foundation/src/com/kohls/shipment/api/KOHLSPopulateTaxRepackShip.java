package com.kohls.shipment.api;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCStringUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class populates the Tax percentage and Tax Symbol for every ShipmentLine before it goes to the Jasper Component
 * This is a part of RePackSlip component
 * 
 * @author Saravana
 *
 */
public class KOHLSPopulateTaxRepackShip {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSPopulateTaxRepackShip.class.getName());
	
	/**
	 * This method populates the Tax percentage and Tax Symbol for every ShipmentLine before it goes to the Jasper Component
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public Document populateTaxRepack(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSPopulateTaxRepack" +
					" populateTaxRepack method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			try{
			
				Element eleInputDoc = inputDoc.getDocumentElement();
				NodeList nlShipmenLine = eleInputDoc.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
				int iLength = nlShipmenLine.getLength();
				Element elePopulateTaxes = XMLUtil.createChild(eleInputDoc, KohlsConstant.E_POPULATE_TAXES);
				Map<String, String> mTaxPercentage = new HashMap<String, String>();
				
				for(int iLoop=0; iLoop<iLength; iLoop++) {
				
					Element eleShipmentLine = (Element) nlShipmenLine.item(iLoop);
					Element eleLineTax = (Element) eleShipmentLine.getElementsByTagName(KohlsConstant.E_LINE_TAX).item(0);
					
					if(!YFCObject.isVoid(eleLineTax)){
					
						String strTaxPercentage = eleLineTax.getAttribute(KohlsConstant.ATTR_TAX_PERCENT);
						DecimalFormat df = new DecimalFormat("#0.00");
						
						if(!YFCStringUtil.isVoid(strTaxPercentage)){
																					
							if(!YFCObject.isVoid(mTaxPercentage)
								&& !mTaxPercentage.containsKey(strTaxPercentage)){
							
								String strTaxSymbol = KohlsConstant.TAX_SYMBOL_T + (mTaxPercentage.size()+1);
								
								mTaxPercentage.put(strTaxPercentage, strTaxSymbol);
								
								eleShipmentLine.setAttribute(KohlsConstant.A_TAX_SYMBOL, strTaxSymbol);
								eleShipmentLine.setAttribute(KohlsConstant.ATTR_TAX_PERCENT, df.format(Double.parseDouble(strTaxPercentage)*100));
															
								Element elePopulateTax = XMLUtil.createChild(elePopulateTaxes, KohlsConstant.E_POPULATE_TAX);
								
								elePopulateTax.setAttribute(KohlsConstant.A_TAX_SYMBOL, strTaxSymbol);
								elePopulateTax.setAttribute(KohlsConstant.ATTR_TAX_PERCENT, df.format(Double.parseDouble(strTaxPercentage)*100));
							}
							else if(mTaxPercentage.containsKey(strTaxPercentage)){
								
								eleShipmentLine.setAttribute(KohlsConstant.A_TAX_SYMBOL, mTaxPercentage.get(strTaxPercentage));
								eleShipmentLine.setAttribute(KohlsConstant.ATTR_TAX_PERCENT, df.format(Double.parseDouble(strTaxPercentage)*100));
							}
						}
					}
				}
			} catch(Exception e){
				
				this.log.debug("<!-- Exception in KOHLSPopulateTaxRepack" +
						" populateTaxRepack method -- >");
				e.printStackTrace();
			}
			
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSPopulateTaxRepack" +
					" populateTaxRepack method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
}
