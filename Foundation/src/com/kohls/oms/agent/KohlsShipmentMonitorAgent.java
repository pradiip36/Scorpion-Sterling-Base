package com.kohls.oms.agent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsShipmentMonitorAgent extends YCPBaseAgent {
	/*
	 * This class is used as Shipment monitor to confirm the shipments 
	 * that are packed and having the IsPackProcessComplete as Y. 
	 *  
	 * @see com.yantra.ycp.japi.util.YCPBaseAgent#executeJob(com.yantra.yfs.japi.
	 *      YFSEnvironment, org.w3c.dom.Document) This Method will process the
	 *      record from the getJobs Method 
	 */
	
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsShipmentMonitorAgent.class);
	
	
	public List<Document> getJobs(YFSEnvironment env, Document criteria, Document lastMessageCreated) throws Exception {

		Document outDoc = getEligibleRecordsFromDB(env,criteria,lastMessageCreated);
		if (kohlsLogger.isDebugEnabled())
			kohlsLogger.debug("\n KohlsShipmentMonitorAgent :: Shipment documents \n" + KohlsXMLUtil.getXMLString(outDoc));
		
		List<Document> shpOutList = new ArrayList<Document>();
		NodeList outNodeList = outDoc.getElementsByTagName("Shipment");
		int length = outNodeList.getLength();

		for (int i = 0; i < length; i++) {
			((List<Document>) shpOutList).add(KohlsXMLUtil.getDocumentForElement((Element) outNodeList.item(i)));
		}
		return shpOutList;
	}
	
	
	public void executeJob(YFSEnvironment env, Document inDoc) throws Exception {

		try {
		if (kohlsLogger.isDebugEnabled())
			kohlsLogger.debug("\n KohlsShipmentMonitorAgent :: executeJob XML \n" + KohlsXMLUtil.getXMLString(inDoc));
		
			Document processsedDocument=KohlsCommonUtil.invokeAPI(env, "confirmShipment", inDoc);
		} catch (Exception ex) {
			if (kohlsLogger.isDebugEnabled())
				kohlsLogger.debug("\n Exception in KohlsShipmentMonitorAgent :: executeJob XML \n" + ex.getMessage());
			
		}
	}

	private Document getEligibleRecordsFromDB(YFSEnvironment env,Document criteria,Document lastMessageCreated) throws Exception {

		Document getShipmentListInputDoc = createInputForGetShipmentList(env,criteria,lastMessageCreated);
		// Calling the getShipmentList API with the input prepared
		Document getShipmentListOutputDoc = KohlsCommonUtil.invokeAPI(env, 
				KohlsConstant.API_GET_SHIPMENT_LIST_FOR_PACK_SHIPMENT_KEY_TEMPLATE_PATH, 
				KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);
		
		return getShipmentListOutputDoc;
	}

	private Document createInputForGetShipmentList(YFSEnvironment env,Document criteria,Document lastMessageCreated) throws ParserConfigurationException {
		Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element ShipmentElem = docShipmentListInput.getDocumentElement();
		
		if (lastMessageCreated!=null) { //Append shipmentkey query if last messages executed
			String shipmentKey= lastMessageCreated.getDocumentElement().getAttribute(KohlsConstant.A_SHIPMENT_KEY) ;
			ShipmentElem.setAttribute(KohlsConstant.A_SHIPMENT_KEY,shipmentKey);
			ShipmentElem.setAttribute("ShipmentKeyQryType",KohlsConstant.HD_GT);
		}
				
		ShipmentElem.setAttribute(KohlsConstant.ATTR_ENTERPRISE_CODE,KohlsConstant.KOHLS_ENTERPRISE_CODE);	 
		//ShipmentElem.setAttribute(KohlsConstant.A_IS_PACK_PROCESS_COMPLETE,"Y");
		ShipmentElem.setAttribute(KohlsConstant.A_STATUS,KohlsConstant.A_SHIPMENT_PACKED_STATUS);	
		ShipmentElem.setAttribute(KohlsConstant.A_STATUS_DATE_QRY_TYPE,KohlsConstant.A_LESS_THAN);
		ShipmentElem.setAttribute(KohlsConstant.ATTR_DOC_TYPE,KohlsConstant.SO_DOCUMENT_TYPE);
		ShipmentElem.setAttribute(KohlsConstant.A_SHIPMENT_CLOSED_FLAG,"N");
		
		String diffVal=criteria.getDocumentElement().getAttribute("confirmShipmentTime") ;
		if (diffVal==null || diffVal.isEmpty() || diffVal.trim().length()<1)
				diffVal="30";
		Calendar calTemp = Calendar.getInstance(); 
	    calTemp.add(Calendar.MINUTE, -Integer.valueOf(diffVal));
	    SimpleDateFormat dtFormatter = new SimpleDateFormat(KohlsConstant.INV_DATE_FORMAT);//Date format "yyyy-MM-dd'T'HH:mm:ss" for DB comparision
	    String formatDate=dtFormatter.format(calTemp.getTime());
		ShipmentElem.setAttribute(KohlsConstant.A_STATUS_DATE,formatDate);
		
		Element eleOdrBy = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_ORDER_BY);
		Element eleAttr = XMLUtil.createChild(eleOdrBy, KohlsConstant.A_ATTRIBUTE);
		eleAttr.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_KEY);
		ShipmentElem.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS,KohlsConstant.A_SHIP_MONITOR_MAX_RECS);
		
		return docShipmentListInput;
	}
}
