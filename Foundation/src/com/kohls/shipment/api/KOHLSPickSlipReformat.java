package com.kohls.shipment.api;

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
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class restructures the XML which goes to the Jasper component
 * 
 * @author Saravana
 *
 */
public class KOHLSPickSlipReformat {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSPickSlipReformat.class.getName());
	

	/**
	 * This method restructures the XML which goes to the Jasper component
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	
	public Document reFormat(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{
	
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSPickSlipReformat" +
					" reFormat method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		reFormatFrontPage(yfsEnvironment, inputDoc);
		reFormatBackPage(yfsEnvironment, inputDoc);
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSPickSlipReformat" +
					" reFormat method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
	
	/**
	 * This method restructures the FrontPage part of the XML which goes to the Jasper component
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public Document reFormatFrontPage(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSPickSlipReformat" +
					" reFormatFrontPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			Element eleInputDoc = inputDoc.getDocumentElement();
			NodeList nlFrontPage = eleInputDoc.getElementsByTagName(KohlsConstant.E_FRONT_PAGE);
			
			if(nlFrontPage.getLength()>0){
				
				Element eleFrontPage = (Element) nlFrontPage.item(0);
				NodeList nlShipLines = eleFrontPage.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
				int iShipLinesLength = nlShipLines.getLength();
				
				for(int iLoop=0; iLoop<iShipLinesLength; iLoop++) {
					
					Element eleShipLine = (Element) nlShipLines.item(iLoop);
					NodeList nlItemInfo = eleShipLine.getElementsByTagName(KohlsConstant.E_ITEM_INFO);
					int iItemInfoLength = nlItemInfo.getLength();
					Map<String, Double> mToteQty = new HashMap<String, Double>();
					
					for(int iInnerLoop=0; iInnerLoop<iItemInfoLength; iInnerLoop++) {
						
						Element eleItemInfo = (Element) nlItemInfo.item(iInnerLoop);
						String strToteId = eleItemInfo.getAttribute(KohlsConstant.A_TOTEID);
						String strToteQty = eleItemInfo.getAttribute(KohlsConstant.A_QUANTITY);						
						double dToteQty = Double.parseDouble(strToteQty);
						
						if(mToteQty!=null && !mToteQty.containsKey(strToteId)){
							
							mToteQty.put(strToteId, dToteQty);
						}
						else if(mToteQty.containsKey(strToteId)){
														
							eleShipLine.removeChild(eleItemInfo);
							iInnerLoop--;
							iItemInfoLength--;
							
							eleItemInfo = (Element) nlItemInfo.item(iInnerLoop);
							
							dToteQty = Double.parseDouble(strToteQty);
							dToteQty = dToteQty+Double.parseDouble(eleItemInfo.getAttribute(KohlsConstant.A_QUANTITY));
							
							eleItemInfo.setAttribute(KohlsConstant.A_QUANTITY, Double.toString(dToteQty));
						}						
					}
				}
			}					
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSPickSlipReformat" +
					" reFormatFrontPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
	
	/**
	 * This method restructures the BackPage part of the XML which goes to the Jasper component
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public Document reFormatBackPage(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSPickSlipReformat" +
					" reFormatBackPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			Element eleInputDoc = inputDoc.getDocumentElement();
			NodeList nlBackPage = eleInputDoc.getElementsByTagName(KohlsConstant.E_BACK_PAGE);
			
			if(nlBackPage.getLength()>0){
				
				Element eleFrontPage = (Element) nlBackPage.item(0);
				NodeList nlShipment = eleFrontPage.getElementsByTagName(KohlsConstant.E_SHIPMENT);
				int iShipmentLength = nlShipment.getLength();
				
				for(int iLoop=0; iLoop<iShipmentLength; iLoop++) {
					
					Element eleShipment = (Element) nlShipment.item(iLoop);
					NodeList nlShipLine = eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
					int iShipLineLength = nlShipLine.getLength();
					Map<String, Double> mItemQty = new HashMap<String, Double>();
					
					for(int iInnerLoop=0; iInnerLoop<iShipLineLength; iInnerLoop++) {
						
						Element eleShipLine = (Element) nlShipLine.item(iInnerLoop);
						String strItemID = eleShipLine.getAttribute(KohlsConstant.A_ITEM_ID);
						String strQuantity = eleShipLine.getAttribute(KohlsConstant.A_QUANTITY);						
						double dQuantity = Double.parseDouble(strQuantity);
						
						if(mItemQty!=null && !mItemQty.containsKey(strItemID)){
							
							mItemQty.put(strItemID, dQuantity);
						}
						else if(mItemQty.containsKey(strItemID)){
														
							eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0).removeChild(eleShipLine);
							iInnerLoop--;
							iShipLineLength--;
							
							eleShipLine = (Element) nlShipLine.item(iInnerLoop);
							
							dQuantity = Double.parseDouble(strQuantity);
							dQuantity = dQuantity+Double.parseDouble(eleShipLine.getAttribute(KohlsConstant.A_QUANTITY));
							
							eleShipLine.setAttribute(KohlsConstant.A_QUANTITY, Double.toString(dQuantity));
						}
					}
				}
			}					
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSPickSlipReformat" +
					" reFormatBackPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
}