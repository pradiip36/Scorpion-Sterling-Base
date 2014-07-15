package com.kohls.shipment.api;

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
public class KOHLSFrontPageReStructure {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSFrontPageReStructure.class.getName());
	
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
	public Document reStructureFrontPage(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSFrontPageReStructure" +
					" reStructureFrontPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			Element eleInputDoc = inputDoc.getDocumentElement();
			NodeList nlFrontPage = eleInputDoc.getElementsByTagName(KohlsConstant.E_FRONT_PAGE);
			
			if(nlFrontPage.getLength()>0){
				
				Element eleFrontPage = (Element) nlFrontPage.item(0);
				NodeList nlShipLines = eleFrontPage.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
				int iLength = nlShipLines.getLength();				
				String strCurrentExtnLocationID = "";
				int iNewSeqNo = 0;
				int iLastILoopCount = 0;
				
				for(int iLoop=0; iLoop<iLength; iLoop++) {
					
					Element eleShipLine = (Element) nlShipLines.item(iLoop);					
					String strExtnLocationID = eleShipLine.getAttribute(KohlsConstant.A_EXTN_LOCATION_ID);
					String strItemID = eleShipLine.getAttribute(KohlsConstant.A_ITEM_ID);
					String strItemDesc = eleShipLine.getAttribute(KohlsConstant.A_ITEM_DESC);
					String strQuantity = eleShipLine.getAttribute(KohlsConstant.A_QUANTITY);
					String strToteId = eleShipLine.getAttribute(KohlsConstant.A_TOTEID);
					String strUPCCode = eleShipLine.getAttribute((String) KohlsConstant.A_UPC_01);
										
					if((iLoop==0) || (!strExtnLocationID.equals(strCurrentExtnLocationID))){
												
						Element eleItemInfo = XMLUtil.createChild(eleShipLine, KohlsConstant.E_ITEM_INFO);						
						strCurrentExtnLocationID = strExtnLocationID;
						iNewSeqNo = iNewSeqNo + 1;
						
						eleItemInfo.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
						eleItemInfo.setAttribute(KohlsConstant.A_ITEM_DESC, strItemDesc);
						eleItemInfo.setAttribute(KohlsConstant.A_QUANTITY, strQuantity);
						eleItemInfo.setAttribute(KohlsConstant.A_TOTEID, strToteId);
						eleItemInfo.setAttribute((String) KohlsConstant.A_UPC_01, strUPCCode);
						
						iLastILoopCount = iLoop;
					}
					
					else if((strExtnLocationID.equals(strCurrentExtnLocationID))) {
						
						eleFrontPage.removeChild(eleShipLine);
						iLoop--;
						iLength--;
						
						eleShipLine = (Element) nlShipLines.item(iLastILoopCount);
						
						Element eleItemInfo = XMLUtil.createChild(eleShipLine, KohlsConstant.E_ITEM_INFO);
						
						eleItemInfo.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
						eleItemInfo.setAttribute(KohlsConstant.A_ITEM_DESC, strItemDesc);
						eleItemInfo.setAttribute(KohlsConstant.A_QUANTITY, strQuantity);
						eleItemInfo.setAttribute(KohlsConstant.A_TOTEID, strToteId);
						eleItemInfo.setAttribute((String) KohlsConstant.A_UPC_01, strUPCCode);
					}
					
					String strNewSeqNo =  Integer.toString(iNewSeqNo);
					eleShipLine.setAttribute(KohlsConstant.A_SEQ_NO, strNewSeqNo);
					
					eleShipLine.removeAttribute(KohlsConstant.A_ITEM_ID);
					eleShipLine.removeAttribute(KohlsConstant.A_ITEM_DESC);
					eleShipLine.removeAttribute(KohlsConstant.A_QUANTITY);
					eleShipLine.removeAttribute(KohlsConstant.A_TOTEID);
					eleShipLine.removeAttribute((String) KohlsConstant.A_UPC_01);
				}
			}			
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSFrontPageReStructure" +
					" reStructureFrontPage method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
}