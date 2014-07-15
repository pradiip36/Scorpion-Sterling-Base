package com.kohls.shipment.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class transforms the Tote Id from Numeric to Alphabetic format 
 * 
 * @author Saravana
 *
 */
public class KOHLSTransformToteId {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSTransformToteId.class.getName());
	
	private HashMap<String, String> hmToteId = new HashMap<String, String> ();

	/**
	 * This method transforms the Tote Id from Numeric to Alphabetic format
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	
	public Document transformToteId(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{
	
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSTransformToteId" +
					" transformToteId method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		Element elePickSlip = inputDoc.getDocumentElement();		
		
		transformToteIdFrontPage(elePickSlip);
		transformToteIdBackPage(elePickSlip);
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSTransformToteId" +
					" transformToteId method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		return inputDoc;
	}
	
	
	/**
	 * This method transforms the ToteId value present in the FrontPage from Numeric to Alphabetic format 
	 * 
	 * @param elePickSlip
	 */
	public void transformToteIdFrontPage(Element elePickSlip){
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Beginning of KOHLSTransformToteId" +
					" transformToteIdFrontPage method -- >");
		}
		
		NodeList nlItemInformation = elePickSlip.getElementsByTagName(KohlsConstant.E_ITEM_INFO);
		int iNoOfItemInfo = nlItemInformation.getLength();
		
		for(int iLoop=0; iLoop<iNoOfItemInfo; iLoop++){
			
			Element eleItemInfo = (Element) nlItemInformation.item(iLoop);
			String strToteId = eleItemInfo.getAttribute(KohlsConstant.A_TOTEID);
			
			if(!hmToteId.containsKey(strToteId)){
				
				hmToteId.put(strToteId, convertNumToAlpha(Integer.parseInt(strToteId)));
			}
			eleItemInfo.setAttribute(KohlsConstant.A_TOTEID, hmToteId.get(strToteId));			
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSTransformToteId" +
					" transformToteIdFrontPage method -- >");
		}
	}
	
	
	/**
	 * This method transforms the ToteId value present in the BackPage from Numeric to Alphabetic format 
	 * 
	 * @param elePickSlip
	 */
	public void transformToteIdBackPage(Element elePickSlip){
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Beginning of KOHLSTransformToteId" +
					" transformToteIdBackPage method -- >");
		}
		
		NodeList nlShipment = elePickSlip.getElementsByTagName(KohlsConstant.E_SHIPMENT);
		int iNoOfShipment = nlShipment.getLength();
		
		for(int iLoop=0; iLoop<iNoOfShipment; iLoop++){
			
			Element eleShipment = (Element) nlShipment.item(iLoop);
			String strToteId = eleShipment.getAttribute(KohlsConstant.A_TOTEID);
			
			if(!hmToteId.containsKey(strToteId)){
				
				hmToteId.put(strToteId, convertNumToAlpha(Integer.parseInt(strToteId)));
			}
			eleShipment.setAttribute(KohlsConstant.A_TOTEID, hmToteId.get(strToteId));
			
			NodeList nlShipmentLine = eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
			int iNoOfShipmentLine = nlShipmentLine.getLength();
			
			for(int iInnerLoop=0; iInnerLoop<iNoOfShipmentLine; iInnerLoop++){
				
				Element eleShipmentLine = (Element) nlShipmentLine.item(iInnerLoop);
				strToteId = eleShipmentLine.getAttribute(KohlsConstant.A_TOTEID);
				
				if(!hmToteId.containsKey(strToteId)){
					
					hmToteId.put(strToteId, convertNumToAlpha(Integer.parseInt(strToteId)));
				}
				eleShipmentLine.setAttribute(KohlsConstant.A_TOTEID, hmToteId.get(strToteId));
			}
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSTransformToteId" +
					" transformToteIdBackPage method -- >");
		}
	}
	
	
	/**
	 * This method converts the ToteId from Numeric into Alphabetic value
	 * 
	 * @param iToteId
	 * @return String
	 */
	public String convertNumToAlpha(int iToteId){
	
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSTransformToteId" +
					" convertNumToAlpha method -- >" + iToteId);
		}
		
		iToteId = iToteId - 1;
		String strToteId = "";
	
		// Repeatedly divide the number by 26 and convert the remainder into the appropriate letter.
		while (iToteId >= 0) {
		
			int iRemainder = iToteId % 26;
			strToteId = (char)(iRemainder + 'A') + strToteId;
			iToteId = (iToteId / 26) - 1;
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSTransformToteId" +
					" convertNumToAlpha method -- >" + strToteId);
		}
		
		return strToteId;		  
	}
}