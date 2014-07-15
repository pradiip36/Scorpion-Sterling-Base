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
 * This class Populates LineNo for every ShipmentLine using a sequence
 * 
 * @author Saravana
 *
 */
public class KOHLSPopulateSeqNo {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSPopulateSeqNo.class.getName());
	
	/**
	 * This method Populates Sequence No for every ShipmentLine using a sequence
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public void populateSeqNo(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSPopulateSeqNo" +
					" populateSeqNo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			Element eleInputDoc = inputDoc.getDocumentElement();
			NodeList nlFrontPage = eleInputDoc.getElementsByTagName(KohlsConstant.E_FRONT_PAGE);
			
			if(nlFrontPage.getLength()>0){
				
				Element eleFrontPage = (Element) nlFrontPage.item(0);
				NodeList nlShipLines = eleFrontPage.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
				final int iLength = nlShipLines.getLength();
				String strCurrentItemID = "";
				int iNewSeqNo = 0;
				
				for(int iLoop=0; iLoop<iLength; iLoop++) {
					
					Element eleShipLine = (Element) nlShipLines.item(iLoop);
					String strItemID = eleShipLine.getAttribute(KohlsConstant.A_ITEM_ID);
					
					if((iLoop==0) || (!strItemID.equals(strCurrentItemID))){
						
						strCurrentItemID = strItemID;
						iNewSeqNo = iNewSeqNo + 1;
					}
					
					String strNewSeqNo =  Integer.toString(iNewSeqNo);
					eleShipLine.setAttribute(KohlsConstant.A_SEQ_NO, strNewSeqNo);
				}
			}			
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSPopulateSeqNo" +
					" populateSeqNo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
	}
}