package com.kohls.oms.api;


import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/* This class is called on the ON_BACKORDER event of scheduleOrder.This class looks for any existing dependency 
 * on any line and remove them and if not cancel them.
 *  
 *  Copyright 2010, Sterling Commerce, Inc. All rights reserved.
*/
class Temp{
public int noOfParents;
public int noOfBackOrderedParents;
public Double maxAllowedNo;
public Double noOfOrders;
public Double newOrder;
public ArrayList<String> parents;
public ArrayList<String> backOrderedParents;
public Temp(){
		noOfParents=0;
		noOfBackOrderedParents=0;
		maxAllowedNo=0.0d;
		noOfOrders=0.0d;
		newOrder=0.0d;
		parents=new ArrayList<String>();
		backOrderedParents=new ArrayList<String>();
	}

}
public class KohlsCancelBackOrderLinesAPI implements YIFCustomApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsCancelBackOrderLinesAPI.class.getName());
	private  YIFApi api;



	public KohlsCancelBackOrderLinesAPI() throws YIFClientCreationException {		 
			api = YIFClientFactory.getInstance().getLocalApi(); 		 
	}


	@SuppressWarnings("unused")
	public void invoke(YFSEnvironment env, Document inXML) throws Exception
	{	
		HashMap<String, String> hm = new HashMap<String, String>();
		
		HashMap<String,Double> backorderedParents=new HashMap<String, Double>();
		HashMap<String,Temp>  giftsToBeBackOrdered=new HashMap<String,Temp>();
		
		Iterator<String> iter=null;
		if(YFCLogUtil.isDebugEnabled()){

			log.debug("<!-------- Begining of KohlsCancelBackOrderLinesAPI invoke method ----------- >" + XMLUtil.getXMLString(inXML));
		}

		Element eleRoot = inXML.getDocumentElement();
		String sOrderHeaderKey = eleRoot.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		
		YFCDocument yfcDocGetOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderList = yfcDocGetOrderList.getDocumentElement();
		yfcEleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);

		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST, getOrderListTemp());

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Input XML : " + XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}

		Document docGetOrderListOutput = api.getOrderList(env, yfcDocGetOrderList.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList output XML : " + XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}
		//Start Edit by Neha
		//Pushing all parent orderlinekeys to hash
		
		NodeList ndlstOrderLines = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		if(YFCLogUtil.isDebugEnabled()){			
			log.debug(" ndlstOrderLines.getLength="+ndlstOrderLines.getLength());
		}
		for(int i=0;i<ndlstOrderLines.getLength();i++){
			Element eleOrderLine = (Element)ndlstOrderLines.item(i);
			String sOrderedQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
			NodeList ndlstBackOrderedFrom = eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_BACK_ORDERED_FROM);
			Element eleBackOrderedFrom = (Element)ndlstBackOrderedFrom.item(0);
			String sBackOrderedQty = eleBackOrderedFrom.getAttribute(KohlsXMLLiterals.A_BACK_ORDERED_QTY);
			Double iNewOrderedQty = (Double.parseDouble(sOrderedQty) - Double.parseDouble(sBackOrderedQty));
			String orderLineKey=eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
			backorderedParents.put(orderLineKey,iNewOrderedQty);
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("For backorderedParents: orderlinekey= "+orderLineKey+" iNewOrderedQty= "+iNewOrderedQty);
			}
			
		}
		
		//checking which childNodes to be canceled
		NodeList ndlistParentLine=docGetOrderListOutput.getElementsByTagName(E_PARENT_LINE);
		for(int i=0;i<ndlistParentLine.getLength();i++){
			Element eleParentLine=(Element)ndlistParentLine.item(i);
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("Relationship type for parentNode is: "+((Element)eleParentLine.getParentNode()).getAttribute(A_RELATIONSHIP_TYPE));
			}
						if(((Element)eleParentLine.getParentNode()).getAttribute(A_RELATIONSHIP_TYPE).equalsIgnoreCase(GIFT)){
				//assumptions made: 
				// 1. in each relationship node, there is only one parentLine and one childLine
				// 2. in each relationship line, childLine comes before parentLine
				String childKey=((Element)eleParentLine.getPreviousSibling()).getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY); // assumption 2 used here
				String parentKey=eleParentLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
				//noOfParents.put(childKey,noOfParents.get(childKey)+1);
				Temp temp=giftsToBeBackOrdered.get(childKey);
				if(temp==null){
					temp=new Temp();
					giftsToBeBackOrdered.put(childKey,temp);
				}
				temp.parents.add(parentKey);
				if(backorderedParents.containsKey(parentKey)){
					temp.backOrderedParents.add(parentKey);
				}
			}
		}
		//removing entries for which atleast one parent is
		iter=giftsToBeBackOrdered.keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			Temp temp=giftsToBeBackOrdered.get(key);
			Double maxAllowedGift=Double.MAX_VALUE;
			//printing all the parentKeys for the childKey
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("for child key="+key);
			}
			for(int a=0;a<temp.parents.size();a++){
				if(YFCLogUtil.isDebugEnabled()){			
					log.debug("parentkey= "+temp.parents.get(a));
				}
				
			}
			for(int b=0;b<temp.backOrderedParents.size();b++){
				
				if(YFCLogUtil.isDebugEnabled()){			
					log.debug("backordered parentkey="+temp.backOrderedParents.get(b));
				}
			}
			
			if(temp.parents.size()==temp.backOrderedParents.size()){ // all the parents has been backordered atleast once
				maxAllowedGift=Double.MAX_VALUE;
				for(int i=0;i<temp.backOrderedParents.size();i++){
					maxAllowedGift=Math.min(maxAllowedGift,backorderedParents.get(temp.backOrderedParents.get(i)));
				}
			}
			else if((temp.backOrderedParents.size()>0)&&(!isAllowGiftWithOneParentEnabled)){	//atleast one parent has been backordered.
				maxAllowedGift=0.0d;
				for(int i=0;i<temp.backOrderedParents.size();i++){
					maxAllowedGift=Math.max(maxAllowedGift,backorderedParents.get(temp.backOrderedParents.get(i)));
				}
			}
			temp.maxAllowedNo=maxAllowedGift;
		}
		// END Edit by Neha
		NodeList ndlstOrderLinelist = docGetOrderListOutput.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

		String sParentOfDependentGroup = KohlsConstant.NO;

		for (int i=0;i<ndlstOrderLinelist.getLength();i++){			
			Element eleOrderLineList = (Element)ndlstOrderLinelist.item(i);
			String sOrderLineKey = eleOrderLineList.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);
			
			//Start EDIT by Neha
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("Checking if sOrderLineKey is in giftsToBeBackOrdered");
			}
						if(giftsToBeBackOrdered.containsKey(sOrderLineKey)){
				Double orderedQty=0.0;
				try{
					orderedQty=Double.parseDouble(eleOrderLineList.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
				}catch(Exception e){
					log.debug(" exception while parsing double. eleOrderLineList.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY)="+eleOrderLineList.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
					e.printStackTrace();
				}
				giftsToBeBackOrdered.get(sOrderLineKey).noOfOrders=orderedQty;
			}
			//END EDIT
			/* sParentOfDependentGroup will get overwritten with every line, the condition is added to make sure it is never over written 
			 * by N value as it is used as a condition to remove the dependency, the idea here is to just know if there is any 
			 * ParentDependentGroup*/

			if(KohlsConstant.YES.equalsIgnoreCase(eleOrderLineList.getAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP))){
				sParentOfDependentGroup = eleOrderLineList.getAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP);
			}

			Element eleExntGroupCode = (Element)eleOrderLineList.getElementsByTagName(KohlsXMLLiterals.E_EXTN).item(0);
			String sWrapTogetherCode = eleExntGroupCode.getAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE);

			if(null!=sWrapTogetherCode && !sWrapTogetherCode.equalsIgnoreCase("")){
				hm.put(sOrderLineKey, sWrapTogetherCode);
			}
		}
		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("Does Parent Dependecy Exist : " + sParentOfDependentGroup);
		}

		if(KohlsConstant.YES.equalsIgnoreCase(sParentOfDependentGroup)){

			YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrder = yfcDocChangeOrder.getDocumentElement();

			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);

			YFCElement yfcEleOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleOrder.appendChild(yfcEleOrderLines);


			//NodeList ndlstOrderLines = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for(int i=0;i<ndlstOrderLines.getLength();i++){

				Element eleOrderLine = (Element)ndlstOrderLines.item(i);
				String sBackOrderLineKey = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);

				if(hm.containsKey(sBackOrderLineKey)){

					YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP, KohlsConstant.NO);

					YFCElement yfcEleDependency = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_DEPENDENCY);
					yfcEleDependency.setAttribute(KohlsXMLLiterals.A_DEPENDENT_PRIME_LINENO, "");
					yfcEleDependency.setAttribute(KohlsXMLLiterals.A_DEPENDENT_SUB_LINENO, "");
					yfcEleOrderLine.appendChild(yfcEleDependency);
					yfcEleOrderLines.appendChild(yfcEleOrderLine);
				}
			}
			

			//Edit by Neha: Done; 
			// The above code changes the A_PARENT_DEPENDENT_GROUP for the backordered parents. We have to do the same for the backordered gifts
			iter=giftsToBeBackOrdered.keySet().iterator();
			while(iter.hasNext()) {
				String key = iter.next();
				if(hm.containsKey(key)){
					YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, key);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP, KohlsConstant.NO);
					
					YFCElement yfcEleDependency = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_DEPENDENCY);
					yfcEleDependency.setAttribute(KohlsXMLLiterals.A_DEPENDENT_PRIME_LINENO, "");
					yfcEleDependency.setAttribute(KohlsXMLLiterals.A_DEPENDENT_SUB_LINENO, "");
					yfcEleOrderLine.appendChild(yfcEleDependency);
					yfcEleOrderLines.appendChild(yfcEleOrderLine);
				}
			}
			//End Edit
			/*Added to avoid changeOrder Call if there are no Lines to the XML which is likely to happen in scenarios where 
			 * regular and wrap together lines exist in a Order but only regular items are getting BackOrdered.*/
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("Input XML to changeOrder with ParentDependentGroup  : " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
			}
			int ordLineLen = yfcDocChangeOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE).getLength();
			if(ordLineLen >0){
				api.changeOrder(env, yfcDocChangeOrder.getDocument());
			}			

		}else {

			YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrder = yfcDocChangeOrder.getDocumentElement();
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleRoot.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);

			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_CODE, KohlsConstant.REASON_CODE_NO_INV);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_TEXT, KohlsConstant.REASON_TEXT_NO_INV);

			YFCElement yfcEleOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleOrder.appendChild(yfcEleOrderLines);


		//	NodeList ndlstOrderLines = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for(int i=0;i<ndlstOrderLines.getLength();i++){

				Element eleOrderLine = (Element)ndlstOrderLines.item(i);
				String sOrderedQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
				NodeList ndlstBackOrderedFrom = eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_BACK_ORDERED_FROM);
				Element eleBackOrderedFrom = (Element)ndlstBackOrderedFrom.item(0);
				String sBackOrderedQty = eleBackOrderedFrom.getAttribute(KohlsXMLLiterals.A_BACK_ORDERED_QTY);
				Double iNewOrderedQty = (Double.parseDouble(sOrderedQty) - Double.parseDouble(sBackOrderedQty));
				if(YFCLogUtil.isDebugEnabled()){			
					log.debug("new Ordered Qty : " + String.valueOf(iNewOrderedQty));
				}

				YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, String.valueOf(iNewOrderedQty));
				yfcEleOrderLines.appendChild(yfcEleOrderLine);
			}
			//inserting code for canceling child nodes here since by this time all the parent nodes are cancelled.
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("GiftsToBeBackOrdered.size " + giftsToBeBackOrdered.size());
				}
			iter=giftsToBeBackOrdered.keySet().iterator();
			 while(iter.hasNext()) {
			 String key = iter.next();
			 Temp temp=giftsToBeBackOrdered.get(key);
			 Double val = Math.min(temp.maxAllowedNo, temp.noOfOrders);
			 if(YFCLogUtil.isDebugEnabled()){			
				 log.debug("ChildNode entry: Key= " + key+" value= "+val);
				}
			 YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			 yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
			 yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY,key);
			 yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, String.valueOf(val));
			 yfcEleOrderLines.appendChild(yfcEleOrderLine);
			 }
						
						
						
						if(YFCLogUtil.isDebugEnabled()){			
							log.debug("Input XMl to changeOrder: " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
						}


			api.changeOrder(env, yfcDocChangeOrder.getDocument());
		}

	}

	//XML Elements
    public static String E_ORDER_LINE_RELATIONSHIPS              = "OrderLineRelationships";
    public static String E_ORDER_LINE_RELATIONSHIP              =  "OrderLineRelationship";
    public static String E_PARENT_LINE              =  "ParentLine";
    public static String E_CHILD_LINE              =  "ChildLine";
  //XML Attributes
    public static String A_RELATIONSHIP_TYPE               = "RelationshipType";
    public static String GIFT="GWP";
    private static final boolean isAllowGiftWithOneParentEnabled=true;
	private Document getOrderListTemp(){

		YFCDocument yfcDocGetOrderListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcEleDocGetOrderListTemp = yfcDocGetOrderListTemp.getDocumentElement();

		YFCElement yfcEleOrderTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER);
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		
		YFCElement yfcEleOrderLineRelationshipsTemp = yfcDocGetOrderListTemp.createElement(E_ORDER_LINE_RELATIONSHIPS);
		YFCElement yfcEleOrderLineRelationshipTemp = yfcDocGetOrderListTemp.createElement(E_ORDER_LINE_RELATIONSHIP);
		yfcEleOrderLineRelationshipTemp.setAttribute(A_RELATIONSHIP_TYPE, GIFT);
		YFCElement yfcEleParentLineTemp = yfcDocGetOrderListTemp.createElement(E_PARENT_LINE);
		yfcEleParentLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		YFCElement yfcEleChildLineTemp = yfcDocGetOrderListTemp.createElement(E_CHILD_LINE);
		yfcEleChildLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		
		yfcEleOrderLineRelationshipTemp.appendChild(yfcEleChildLineTemp);
		yfcEleOrderLineRelationshipTemp.appendChild(yfcEleParentLineTemp);
		yfcEleOrderLineRelationshipsTemp.appendChild(yfcEleOrderLineRelationshipTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrderLineRelationshipsTemp);
		
		YFCElement yfcEleOrderLinesTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLineTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP, "");

		YFCElement yfcEleExtnTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleExtnTemp);
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrderLinesTemp);
		yfcEleDocGetOrderListTemp.appendChild(yfcEleOrderTemp);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Template : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		}
		
		return yfcDocGetOrderListTemp.getDocument();


	}
	

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
