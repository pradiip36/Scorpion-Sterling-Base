package com.kohls.oms.ue;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;

import com.yantra.yfs.japi.YFSEnvironment;

import com.yantra.yfs.japi.YFSUserExitException;

import com.yantra.yfs.japi.ue.YFSConfirmAssignmentsUE;



/**
 *  If the number of lines in a release are more than the configured value,
 *  for all the lines beyond this value, RejectAssignment is set as true so that a new
 *  release is created for such lines and grouping of line items is done based on the ship node
 */
public class KohlsConfirmAssignmentsUE  implements YFSConfirmAssignmentsUE {

	
	
	private static final YFCLogCategory log =  
		YFCLogCategory.instance(KohlsConfirmAssignmentsUE.class.getName());
	
	
	public Document confirmAssignments(YFSEnvironment env, Document inDoc) 
		throws YFSUserExitException{
		
		int	maxCount = getReleaseLineCount(env);
		Element eleOrder = inDoc.getDocumentElement();
		
		String strTxnId = eleOrder.getAttribute("TransactionId");
		
		Map <String, Integer> mapLineItem = new HashMap<String, Integer>();
		
		// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
		if ("RELEASE.0001".equals(strTxnId) | "RELEASE.0006".equalsIgnoreCase(strTxnId) ) {
		// End -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
			NodeList orderLinesList = eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
			
			int numOrderLines = orderLinesList.getLength();
			if(numOrderLines <= maxCount){
				return inDoc;
			}
			
			for (int i = 0; i < numOrderLines; i++) {

				Element eleOrderLine = (Element) orderLinesList.item(i);
				Element eleSchedules = (Element) eleOrderLine.getElementsByTagName("Schedules").item(0);
		        NodeList scheduleList = eleSchedules.getElementsByTagName("Schedule");
		        int iScheduleListLen = scheduleList.getLength();
	            Element eleSchedule = null;

	            for (int schIndex = 0; schIndex < iScheduleListLen; schIndex++) {
	    		    eleSchedule = (Element) scheduleList.item(schIndex);
	    		    NodeList shipNodesList = eleSchedule.getElementsByTagName("ShipNodes");
	    		    String strShipNode = null;
	    		    if(shipNodesList != null){
		    		    Element eleShipNodes = (Element)shipNodesList.item(0);
		    		    if(eleShipNodes != null){
		    		    	NodeList shipNodeList = eleShipNodes.getElementsByTagName("ShipNode"); 
		    		    	if(shipNodeList != null){
			    		    	Element eleShipNode = (Element)shipNodeList.item(0);
			    		    	strShipNode = eleShipNode.getAttribute("ShipNode");
		    		    	}
		    		    }
	    		    }
	    		    if(strShipNode != null && !("".equals(strShipNode)) && mapLineItem.containsKey(strShipNode) == true){
	    		    	int iNoOfLineItemForNode = mapLineItem.get(strShipNode).intValue();
	    		    	if(iNoOfLineItemForNode < maxCount){
	    		    		mapLineItem.put(strShipNode, ++iNoOfLineItemForNode);
	    		    		eleSchedule.setAttribute("RejectAssignment", "false");
	    		    	} else {
	    		    		eleSchedule.setAttribute("RejectAssignment", "true");
	    		    	}
	    		    } else if(strShipNode != null && !("".equals(strShipNode)) && mapLineItem.containsKey(strShipNode) == false){
	    		    	mapLineItem.put(strShipNode, new Integer(1));
	    		    	eleSchedule.setAttribute("RejectAssignment", "false");
	    		    }

	    		}
			}
		}
		return inDoc;
	}

	/**
	 * This method determines if the common code value for REL_LINE_COUNT is set to Y
	 * @param env
	 * @return
	 * @throws Exception
	 */
	private int getReleaseLineCount(YFSEnvironment env) {
		int maxCount = 90;
		try {
			String strMaxCount = KohlsUtil.getCommonCodeValue(env, "REL_LINE_COUNT");
			if((strMaxCount != null) && (strMaxCount != "")){
				maxCount = new Integer(strMaxCount).intValue();
			}
		} catch (NullPointerException npExcp) {
			if(YFCLogUtil.isDebugEnabled()) {
				log.debug("Common code value is not set for code type REL_LINE_COUNT. " + 
						"Using default value of 90");
			}
		} catch (Exception e) {
			if(YFCLogUtil.isDebugEnabled()) {
				log.debug("Error in retreiving the Common code value REL_LINE_COUNT. ");
			}
		}

		return maxCount;		
		
	}// end of getReleaseLineCount
	
		
}
