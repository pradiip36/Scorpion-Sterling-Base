package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 *  This class will 
 *  a. Get the ON SUCCESS XML for SCHEDULE and create a set of ItemID
 *  b. Get the Available inv and Supply inv map from the env
 *  c. Calculate the demand based of Supply- Available
 *  d. Create a XML with all inv info for all EFCs'
 *  e. Post the XML message into a queue
 */
//Start -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //
public class KohlsSourcingAudit{
	private YIFApi api ;
	private static final YFCLogCategory log =  YFCLogCategory.instance(KohlsSourcingAudit.class.getName());
	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsSourcingAudit() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
	}


	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Document  invoke(YFSEnvironment env,Document inXML){
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<----------- Begining of KohlsSourcingAudit ----------->");

		}
		System.out.println("<----------- Begining of KohlsSourcingAudit ----------->");
		Element eleOrder =(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		String strOrderNo = eleOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO);	
		
		//Start :: OASIS Support 18-JUN-2013
		//logic changed to handle NullPointerExc in 921 env
		Element eleOrderLines=(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINES).item(0);
		NodeList nlOrderLine=eleOrderLines.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		//End OASIS Support 18-JUN-2013
		
		//Commented getting the Ship Nodes from Common Code
		/*Set<String> lstEfcShipNodeNo=null;
	try {
		//get list of EFCs
		 lstEfcShipNodeNo = KohlsUtil.getEFCList(env);
	} catch (YFSException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (RemoteException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (YIFClientCreationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}*/
		//create a set of ItemID for the order

		//Set<String> lstOrderLineItem = new HashSet<String>();

		for (int i=0; i<  nlOrderLine.getLength();i++) {
			Element eleOrderLine = (Element) nlOrderLine.item(i);
			Element eleItem = (Element) eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
			//lstOrderLineItem.add(eleItem.getAttribute(KohlsXMLLiterals.A_ItemID));					

			//}
			//iterate through the list
			//Iterator itemIterator= lstOrderLineItem.iterator();
			//while (itemIterator.hasNext()){	

			//String sItemID =(String) itemIterator.next();
			String sItemID =(String) eleItem.getAttribute(KohlsXMLLiterals.A_ItemID);
			String strPrimeLineNo = (String) eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO);
			//get the Supply picture from the env
			Map<String,String> mapSupplyCorrection= new HashMap<String,String>();
			mapSupplyCorrection=(Map<String,String>)env.getTxnObject(KohlsConstant.SUPPLY_CORRECTION+sItemID);

			//get the Available picture from the env
			Map<String,String> mapAvailabilityCorrection= new HashMap<String,String>();
			mapAvailabilityCorrection=(Map<String,String>)env.getTxnObject(KohlsConstant.AVAILABILITY_CORRECTION+sItemID);
			//Create Sourcing Audit XML
			YFCDocument yfcDocSourcingInvAud = YFCDocument.createDocument(KohlsXMLLiterals.E_SOURCINGAUDIT);
			YFCElement yfcEleSourcingInvAud = yfcDocSourcingInvAud.getDocumentElement();
			yfcEleSourcingInvAud.setAttribute(KohlsXMLLiterals.A_ITEMID,sItemID);
			yfcEleSourcingInvAud.setAttribute(KohlsXMLLiterals.A_ORDERREFERENCE,mapSupplyCorrection.get("OrderReference"));
			yfcEleSourcingInvAud.setAttribute(KohlsXMLLiterals.A_ORDERNO, strOrderNo);
			yfcEleSourcingInvAud.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, strPrimeLineNo);
			Set set = mapAvailabilityCorrection.entrySet();
			Iterator itr = set.iterator();
			while (itr.hasNext()) {
				Map.Entry entry = (Map.Entry) itr.next();
				String sEFCShipNode = (String) entry.getKey();
				String sSupply="0.00";
				String sAvailable="0.00";
				String sDemand="0.00";
				YFCElement yfsEleAvailableToPromiseInv=yfcDocSourcingInvAud.createElement(KohlsXMLLiterals.E_AVAILABLETOPROMISEINV);
				yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_SHIPNODE, sEFCShipNode);
				//add supply picture to the XML
				if(mapSupplyCorrection!=null){
					if(mapSupplyCorrection.get(sEFCShipNode)!=null){
						yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_SUPPLY,mapSupplyCorrection.get(sEFCShipNode));
						sSupply=(String)mapSupplyCorrection.get(sEFCShipNode);
					}else{
						yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_SUPPLY,"0.00");
						sSupply="0.00";
					}
				}
				//add available inv picture to the XML
				if(mapAvailabilityCorrection!=null){
					if(mapAvailabilityCorrection.get(sEFCShipNode)!=null){
						yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_AVAILABILITY,mapAvailabilityCorrection.get(sEFCShipNode));
						sAvailable=(String)mapAvailabilityCorrection.get(sEFCShipNode);
					}else{
						yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_AVAILABILITY,"0.00");
						sAvailable="0.00";
					}
				}
				// add demand picture to the XML
				if(sSupply!=null && sAvailable!=null){
					sDemand=String.valueOf(Double.valueOf(sSupply)-Double.valueOf(sAvailable));
					yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_DEMAND,sDemand);

				}else{

					yfsEleAvailableToPromiseInv.setAttribute(KohlsXMLLiterals.A_DEMAND,"0.00");
				}
				yfcEleSourcingInvAud.appendChild(yfsEleAvailableToPromiseInv);	        
			}
			
			
				if(YFCLogUtil.isDebugEnabled()){
					log.debug("Kohls Suppy Demand Picture Message : " + XMLUtil.getXMLString(yfcDocSourcingInvAud.getDocument()));
				}
				System.out.println("Kohls Suppy Demand Picture Message : " + XMLUtil.getXMLString(yfcDocSourcingInvAud.getDocument()));
				try{
					this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_POST_AUDIT_MSG, yfcDocSourcingInvAud.getDocument());
				}catch(Exception e){
					e.printStackTrace();
				}

			

		}

		return inXML;

	}

}
//End -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //