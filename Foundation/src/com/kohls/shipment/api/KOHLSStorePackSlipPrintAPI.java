package com.kohls.shipment.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSStorePackSlipPrintAPI extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSStorePackSlipPrintAPI.class.getName());
	
	Properties properties = null;
	
	private Properties props;

	/**
	* @param properties
	*            argument from configuration.
	*/
	public void setProperties(Properties props) {
	    this.props = props;
	}
	
	/**
	 * This method invokes respective APIs and services
	 * for reprinting shipments
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public void getPackSlipDetails(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception
	{
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSStorePackSlipPrintAPI" +
					" getPackSlipDetails method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc))
		{
			Element eleInput = inputDoc.getDocumentElement();
			String strPrinterID = eleInput.getAttribute(KohlsConstant.A_PRINTER_ID);
			HashSet HashsetItemID = new HashSet();
			String strShipNode = "";
			
			String strShipmentKey = eleInput.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
			
			//creating input document for getShipmentDetails API
			Document docShipmentDetailsInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
			Element eleshipmentDetailsInput = docShipmentDetailsInput.getDocumentElement();
			eleshipmentDetailsInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
			
			if (YFCLogUtil.isDebugEnabled()) 
			{
				this.log.debug("Input xml for getShipmentDetails API:"
						+ XMLUtil.getXMLString(docShipmentDetailsInput));
			}
		 
			yfsEnvironment.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS,
				     KohlsConstant.API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH_1);
			
			Document docShipmentDetailsOutput = KOHLSBaseApi.invokeAPI(yfsEnvironment,
				     KohlsConstant.API_GET_SHIPMENT_DETAILS, docShipmentDetailsInput);
			
			yfsEnvironment.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
			
			/*Document docShipmentDetailsOutput = KOHLSBaseApi.invokeAPI
			(yfsEnvironment,KohlsConstant.API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH, KohlsConstant.API_GET_SHIPMENT_DETAILS,
					docShipmentDetailsInput);*/

			if (YFCLogUtil.isDebugEnabled()) 
			{
				this.log.debug("getShipmentDetails API Output xml:"
						+ XMLUtil.getXMLString(docShipmentDetailsOutput));
			}
			
			Element eleShipmentDetailsOutput = null;
			String strShipNo = "";
			
			if(!YFCObject.isVoid(docShipmentDetailsOutput))
			{
				eleShipmentDetailsOutput = docShipmentDetailsOutput.getDocumentElement();
				strShipNo = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_SHIPMENT_NO);
				strShipNode = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_SHIP_NODE);
			}
			
			//creating input xml for store pickup
			Document docMultiServiceInput = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENTS);
			Element eleMultiServiceInput = docMultiServiceInput
				.getDocumentElement();
			eleMultiServiceInput.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
			Element elementShip = XMLUtil.createChild(eleMultiServiceInput, KohlsConstant.E_SHIPMENT);
			elementShip.setAttribute(KohlsConstant.A_TOTEID, KohlsConstant.V_TOTE_ID);
			elementShip.setAttribute(KohlsConstant.A_SHIPMENT_NO, strShipNo);
			Element eleShipmentLines = XMLUtil.createChild(elementShip, KohlsConstant.E_SHIPMENT_LINES);
			
			Element eleShipLines = (Element) eleShipmentDetailsOutput.getElementsByTagName
				(KohlsConstant.E_SHIPMENT_LINES).item(0);
			NodeList nlShipmentLines = eleShipLines
			.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
			int iLen = nlShipmentLines.getLength();
			
			if(iLen!=0)
			{
				for(int i=0;i<iLen;i++)
				{
					Element eleShipmentLine = (Element)nlShipmentLines.item(i);
					
					String strDesc = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_DESC);
					String strQty = eleShipmentLine.getAttribute(KohlsConstant.A_QUANTITY);
					String strItemID = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_ID);
					String strUOM = eleShipmentLine.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE);
					//String strNode = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_SHIP_NODE);
					String strCallOrgCode = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_ENTERPRISE_CODE);
					
					HashsetItemID.add(strItemID);
					
					Element eleItemAlias = null;
					String strAliasName = "";
					String strAliasValue = "";
					String strLineNo = "";
					String strLocationID = "";
					
					//creating input xml for getItemNodeDefnDetails API
					Document docGetItemNodeDefnDet = XMLUtil.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
					Element eleGetItemNodeDefnDet = docGetItemNodeDefnDet.getDocumentElement();
					eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_CALLING_ORGANIZATION_CODE, strCallOrgCode);
					eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
					eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_NODE, strShipNode);
					
					String strCatalogOrgCode = props.getProperty(KohlsConstant.A_CATALOG_ORG_CODE);
                    if(!YFCObject.isVoid(strCatalogOrgCode))
                    {
                    	eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, strCatalogOrgCode);
                    }else
                    {
                    	eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, KohlsConstant.A_CATALOG_ORG);
                    }
					
					eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, strUOM);

					//creating template for getItemNodeDefnDetails API
					Document docGetItmNodeDefDetTemp = XMLUtil.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
					Element eleGetItmNodeDefDetTemp = docGetItmNodeDefDetTemp.getDocumentElement();
					eleGetItmNodeDefDetTemp.setAttribute(KohlsConstant.A_CALLING_ORGANIZATION_CODE, KohlsConstant.A_BLANK);
					eleGetItmNodeDefDetTemp.setAttribute(KohlsConstant.A_ITEM_ID, KohlsConstant.A_BLANK);
					eleGetItmNodeDefDetTemp.setAttribute(KohlsConstant.A_NODE, KohlsConstant.A_BLANK);
					eleGetItmNodeDefDetTemp.setAttribute(KohlsConstant.A_ORGANIZATION_CODE, KohlsConstant.A_BLANK);
					eleGetItmNodeDefDetTemp.setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, KohlsConstant.A_BLANK);
					Element eleExtn = docGetItmNodeDefDetTemp.createElement(KohlsConstant.E_EXTN);
					eleGetItmNodeDefDetTemp.appendChild(eleExtn);

					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("Input xml for getItemNodeDefnDetails API:"
								+ XMLUtil.getXMLString(docGetItemNodeDefnDet));
					}

					Document docItemNodeDefnDet = KOHLSBaseApi.invokeAPI
					(yfsEnvironment, docGetItmNodeDefDetTemp, KohlsConstant.API_GET_ITEM_NODE_DEFN_DETAILS,
							docGetItemNodeDefnDet);

					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("getItemNodeDefnDetails API Output xml:"
								+ XMLUtil.getXMLString(docItemNodeDefnDet));
					}

					if(!YFCObject.isVoid(docItemNodeDefnDet))
					{
						Element eleItemNodeDefnDet = docItemNodeDefnDet.getDocumentElement();
						Element elemExtn = (Element) eleItemNodeDefnDet.getElementsByTagName(KohlsConstant.E_EXTN).item(0);
						strLineNo = elemExtn.getAttribute(KohlsConstant.A_EXTN_LINE_NO);
						strLocationID = elemExtn.getAttribute(KohlsConstant.A_EXTN_LOCATION_ID);
					}
					
					/**
					try
					{
						eleItemAlias = (Element) XPathUtil.getNodeList(
							eleShipmentLine,
							"/Shipment/ShipmentLines/ShipmentLine/OrderLine/ItemDetails/ItemAliasList/ItemAlias" +
							"[@AliasName='" + KohlsConstant.V_UPC01 + "']").item(0);
					}
					catch(Exception e)
					{
						if (YFCLogUtil.isDebugEnabled()) 
						{
							this.log.debug("Error occured while retrieving ItemAlias element");
						}
					}
					
					**/

					NodeList nlItemAliasList = eleShipmentLine
					.getElementsByTagName(KohlsConstant.A_ITEM_ALIAS);
					for (int intItemAliasCount = 0; intItemAliasCount < nlItemAliasList.getLength(); intItemAliasCount++) {
						Element eleItemAliasAll = (Element) nlItemAliasList.item(intItemAliasCount);


						String sAliasName = eleItemAliasAll.getAttribute(KohlsConstant.A_ALIAS_NAME);
						if(!YFCObject.isVoid(sAliasName) && sAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
						{
							eleItemAlias = eleItemAliasAll;
						}

					}
					if(!YFCObject.isVoid(eleItemAlias))
					{
						strAliasName = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_NAME);
						strAliasValue = eleItemAlias.getAttribute(KohlsConstant.A_ALIAS_VALUE);
					}
					
					Element eleShipmentLineEle = XMLUtil.createChild(eleShipmentLines,KohlsConstant.E_SHIPMENT_LINE);
					if(strAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
					{
						eleShipmentLineEle.setAttribute(KohlsConstant.V_UPC01, strAliasValue);
					}
					if(i==0)
					{
						eleShipmentLineEle.setAttribute(KohlsConstant.A_SHIPMENT_NO, strShipNo);
					}
					eleShipmentLineEle.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
					eleShipmentLineEle.setAttribute(KohlsConstant.A_TOTEID, KohlsConstant.V_TOTE_ID);
					eleShipmentLineEle.setAttribute(KohlsConstant.A_ITEM_DESC, strDesc);
					eleShipmentLineEle.setAttribute(KohlsConstant.A_QUANTITY, strQty);
					eleShipmentLineEle.setAttribute(KohlsConstant.A_EXTN_LINE_NO, strLineNo);
					eleShipmentLineEle.setAttribute(KohlsConstant.A_EXTN_LOCATION_ID, strLocationID);
					
				} //end of for loop
			} //end of iLen check
			
			if(HashsetItemID.size()>0)
			{
				Iterator iter = HashsetItemID.iterator();
				while(iter.hasNext())
				{
					String strItemIDHashSet = (String) iter.next();
					try
					{
						/*nleleShipLine = XPathUtil.getNodeList(
						eleMultiServiceInput,
						"/Shipments/Shipment/ShipmentLines/ShipmentLine" +
						"[@ItemID='" + strItemIDHashSet + "']");*/

						NodeList nlShipLines = eleMultiServiceInput.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES);

						for(int k=0;k<nlShipLines.getLength();k++)
						{
							int iSumQty = 0;

							Element eleShipmntLines = (Element) nlShipLines.item(k);

							NodeList nlShipLine = eleShipmntLines.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);

							for(int l=0;l<nlShipLine.getLength();l++)
							{
								Element eleShipLine = (Element) nlShipLine.item(l);
								String strItemID = eleShipLine.getAttribute(KohlsConstant.A_ITEM_ID);

								if(!YFCObject.isVoid(strItemID) && strItemID.equalsIgnoreCase(strItemIDHashSet))
								{
									String strqty = eleShipLine.getAttribute(KohlsConstant.A_QUANTITY);

									int iQty = (int) Double.parseDouble(strqty);
									iSumQty = iSumQty + iQty;

								}

							}
							for(int i=0;i<nlShipLine.getLength();i++)
							{
								Element eleShipLine = (Element) nlShipLine.item(i);
								eleShipLine.setAttribute(KohlsConstant.A_SKU_QTY, Integer.toString(iSumQty));
							}

						}
					}
					catch(Exception e)
					{
						if (YFCLogUtil.isDebugEnabled()) 
						{
							this.log.debug("Error occured while retrieving ShipmentLine nodelist");
						}
					}
					/*if(!YFCObject.isVoid(nleleShipLine))
					{
						calculateSkuQty(nleleShipLine);
					}*/
				} //end of while loop
			}
			
			eleMultiServiceInput.setAttribute(KohlsConstant.A_SHIP_NODE, strShipNode);
			
			if (YFCLogUtil.isDebugEnabled())
			{
				this.log.debug("Input xml for the service:"
						+ XMLUtil.getXMLString(docMultiServiceInput));
			}
				
			KOHLSBaseApi.invokeService(yfsEnvironment, 
				KohlsConstant.SERVICE_KOHLS_PICK_SLIP_PRINT  ,docMultiServiceInput);
			
		} //end of input doc null check
	} //end of method

	/*private void calculateSkuQty(NodeList nleleShipLine) 
	{
		int iSumQty = 0;
		int iLength = nleleShipLine.getLength();
		if(iLength>0)
		{
			for(int i=0;i<iLength;i++)
			{
				Element eleShipLine = (Element) nleleShipLine.item(i);
				
				String strqty = eleShipLine.getAttribute(KohlsConstant.A_QUANTITY);
				int iQty = (int) Double.parseDouble(strqty);
				iSumQty = iSumQty + iQty;
			}
		}
		
		if(iLength>0)
		{
			for(int i=0;i<iLength;i++)
			{
				Element eleShipLine = (Element) nleleShipLine.item(i);
				eleShipLine.setAttribute(KohlsConstant.A_SKU_QTY, Integer.toString(iSumQty));
			}
		}
	}*/ //end of calculateSkuQty method
	
}
