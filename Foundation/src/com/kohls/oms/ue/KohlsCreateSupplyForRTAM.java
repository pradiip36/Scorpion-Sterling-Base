package com.kohls.oms.ue;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.Hashtable;

import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;



/*
 * 
 * This class will Fetch the Item Details by invoking getItemList API and will check the value of Attribute 
 * ExtnShipNodeSource. if the value is Store it will return the input . 
 * if it is blank it will Remove the Supply from the input for the ShipNode whose
 * NodeType is STORE.
 *  
 */
public class KohlsCreateSupplyForRTAM {
	
	private static final YFCLogCategory log =  YFCLogCategory.instance(KohlsCreateSupplyForRTAM.class.getName());
	/*
	 * 
	 * This method will Fetch the Item Details by invoking getItemList API and 
	 * will check the value of Attribute 
	 * ExtnShipNodeSource.If it is Store it will return the 
	 * input otherwise it will remove supply from the input.

	 *  
	 */
	  public Document getExtnShipNodeSource(YFSEnvironment env, Document inXML)
			    throws YFSUserExitException
			  {
			    log.debug("<----------- Begining of getExtnShipNodeSource Method ----------->");
			    

			    Element itemElement = inXML.getDocumentElement();
			    Element eleitems = (Element)itemElement.getElementsByTagName(KohlsConstants.ITEM).item(0);
			    String ItemID = eleitems.getAttribute(KohlsConstants.ITEM_ID);
			    String UOM = eleitems.getAttribute(KohlsConstants.UNIT_OF_MEASURE);
			    String organizationCode = eleitems.getAttribute(KohlsConstants.ORGANIZATION_CODE);
			    Document outputTemp1 = null;
			    try
			    {
			      outputTemp1 = createDocforItemList(ItemID, UOM, organizationCode, env);
			    }
			    catch (Exception exception) 
			    {
			      log.error("YFSException occured" + exception.getMessage());
			      throw new YFSException("YFSException occured" + exception);
			    }

			    Element itemListElement = outputTemp1.getDocumentElement();
			    Element eleitemList = (Element)itemListElement.getElementsByTagName(KohlsConstants.ITEM).item(0);
			    NodeList Extn = eleitemList.getElementsByTagName(KohlsConstants.EXTN);
			    Element itemExtn = (Element)Extn.item(0);
			    String strExtnShipNodeSource = itemExtn.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE);
			    //String strStore = "STORE";

			    if (strExtnShipNodeSource.equalsIgnoreCase(KohlsConstants.STORE))
			    {
			      return inXML;
			    }

			    doesStoreSupplyExist(env, inXML);

			    return inXML;
			  }

			  public Document doesStoreSupplyExist(YFSEnvironment env, Document inXML)
			  {
			    log.verbose("\n KohlsGetSupplyCorrectionsUE :: doesShipNodeStore() :: InputXML\n" + KohlsXMLUtil.getXMLString(inXML));

			    Hashtable<String, String> mShipNodeList = new Hashtable<String, String>();
			    Document docShipNodeList = SCXmlUtil.createDocument(KohlsConstants.SHIP_NODE);
			    Element eleShipNodeList = docShipNodeList.getDocumentElement();
			    eleShipNodeList.setAttribute(KohlsConstants.NODE_TYPE, KohlsConstants.STORE);
			    Document outputTemp = null;
			    try
			    {
			      log.verbose("\n KohlsGetSupplyCorrectionsUE :: invokegetShipNodeList API() :: InputXML\n" + KohlsXMLUtil.getXMLString(docShipNodeList));
			      
			      outputTemp = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_SHIP_NODE_LIST, KohlsConstants.GET_SHIP_NODE_LIST_API, docShipNodeList);
			      
			      log.verbose("\n KohlsGetSupplyCorrectionsUE :: invokegetShipNodeList API() :: OutputXML\n" + KohlsXMLUtil.getXMLString(outputTemp));
			    }
			    catch (Exception exception) {
			      log.error("YFSException occured" + exception.getMessage());
			      throw new YFSException("YFSException occured" + exception);
			    }

			    NodeList ShipNodeList = outputTemp.getElementsByTagName(KohlsConstants.SHIP_NODE);

			    int ShipNodeListLength = ShipNodeList.getLength();
			    //System.out.println("Length :\n" + ShipNodeListLength);
			    for (int j = 0; j < ShipNodeListLength; j++) {
			      Element ShipNode = (Element)ShipNodeList.item(j);

			      String strshipNode = ShipNode.getAttribute(KohlsConstants.SHIP_NODE);
			      String strNodetype = ShipNode.getAttribute(KohlsConstants.NODE_TYPE);

			      mShipNodeList.put(strshipNode, strNodetype);


			    }

			    Element itemElement = inXML.getDocumentElement();
			    Element elesupplies = (Element)itemElement.getElementsByTagName(KohlsConstants.SUPPLIES).item(0);
			    NodeList supplyList = elesupplies.getElementsByTagName(KohlsConstants.SUPPLY);

			    int supplyListLength = supplyList.getLength();

			    for (int j = 0; j < supplyListLength; j++) {
			      Element supplyEle = (Element)supplyList.item(j);
			      if (YFCCommon.isVoid(supplyEle))
			      {
			        break;
			      }

			      String sshipNode = supplyEle.getAttribute(KohlsConstants.SHIP_NODE);

			      if (mShipNodeList.containsKey(sshipNode))
			      {
			        elesupplies.removeChild(supplyEle);
			        j--;
			      }

			    }

			    return inXML;
			  }
			/*
			 * 
			 * This method will fetch the Item Details by invoking the getItemLIst API 
			 * 	 */
			
			  private Document createDocforItemList(String ItemID, String UOM, String organizationCode, YFSEnvironment env)
					    throws Exception
			  {
				    Document inTemp = SCXmlUtil.createDocument(KohlsConstants.ITEM);
				    Element inElement = inTemp.getDocumentElement();
				    inElement.setAttribute(KohlsConstants.ITEM_ID, ItemID);
				    inElement.setAttribute(KohlsConstants.UNIT_OF_MEASURE, UOM);
				    inElement.setAttribute(KohlsConstants.ORGANIZATION_CODE, organizationCode);
	
				    Document outputTemp = KohlsCommonUtil.invokeAPI(env, KohlsConstants.GET_ITEM_LIST, KohlsConstants.GET_ITEMLIST, inTemp);
				    
				    log.verbose("\n KohlsGetSupplyCorrectionsUE :: invokegetItemList API() :: OutputXML\n" + KohlsXMLUtil.getXMLString(outputTemp));
	
				    return outputTemp;
			  }
			
			

}
