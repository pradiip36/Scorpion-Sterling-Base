package com.kohls.shipment.api;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class invokes respective APIs and services for
 * Multipack print
 * 
 * @author Suraj
 *
 */
public class KOHLSChangeMultiShipmentStatusAPI_Drop2 extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSChangeMultiShipmentStatusAPI_Drop2.class.getName());
	Properties properties = null;
	
	/**
	 * This method invokes respective APIs and services
	 * for multipack print
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	
	
	private Properties props;

	/**
	* @param properties
	*            argument from configuration.
	*/
	public void setProperties(Properties props) {
	    this.props = props;
	}
	public Document processChangeMultiShipment(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception
	{
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSChangeMultiShipmentStatusAPI" +
					" changeMultiShipmentStatus method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		Document docOutput = XMLUtil.createDocument("ApiSuccess");
		String strActionTaken="";
		
		if(!YFCObject.isVoid(inputDoc))
		{
			Document docMultiServiceInput = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENTS);
			Element eleMultiServiceInput = docMultiServiceInput
				.getDocumentElement();
			
			String strShipNode_v = "";
			HashSet HashsetItemID = new HashSet();
			
			Element eleShipmentList = inputDoc.
						getDocumentElement();
			
			strActionTaken=eleShipmentList.getAttribute(KohlsConstant.A_ACTION_TAKEN);
			
			String strPrinterID = eleShipmentList.getAttribute(KohlsConstant.A_PRINTER_ID);
			String strBatchId = eleShipmentList.getAttribute(KohlsConstant.A_BATCH_ID);
			String strTotalCarts = eleShipmentList.getAttribute(KohlsConstant.A_TOTAL_CARTS);
			String strCartNumber = eleShipmentList.getAttribute(KohlsConstant.A_CART_NUMBER);

			eleMultiServiceInput.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
			if(!YFCObject.isVoid(strBatchId))
			{
				eleMultiServiceInput.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
			}
			
			if(!YFCObject.isVoid(strTotalCarts))
			{
				eleMultiServiceInput.setAttribute(KohlsConstant.A_TOTAL_CARTS, strTotalCarts);
				eleMultiServiceInput.setAttribute(KohlsConstant.A_TOTAL_CARTS_AVAILABLE, KohlsConstant.V_Y);
			}else{
				eleMultiServiceInput.setAttribute(KohlsConstant.A_TOTAL_CARTS_AVAILABLE, KohlsConstant.V_N);
			}
			
			if(!YFCObject.isVoid(strCartNumber))
			{
				eleMultiServiceInput.setAttribute(KohlsConstant.A_CART_NUMBER, strCartNumber);
				eleMultiServiceInput.setAttribute(KohlsConstant.A_CART_NUMBER_AVAILABLE, KohlsConstant.V_Y);
			}else{
				eleMultiServiceInput.setAttribute(KohlsConstant.A_CART_NUMBER_AVAILABLE, KohlsConstant.V_N);
			}
			
			NodeList nodeShipmentList = eleShipmentList
			.getElementsByTagName(KohlsConstant.E_SHIPMENT);
			int iLength = nodeShipmentList.getLength();
			
			if(iLength!=0)
			{
				for(int i=0;i<iLength;i++)
				{
					Element eleShipment = (Element)nodeShipmentList.item(i);
					
					String strShipmentKey = eleShipment.getAttribute
						(KohlsConstant.A_SHIPMENT_KEY);
					
					//creating input xml for getshipmentlist api
					Document docGetShipmentList = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
					Element eleGetShipmentList = docGetShipmentList.getDocumentElement();
					eleGetShipmentList.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
					
					
					//creating template for getshipmentlist api
					
					String strStatus="";
					String strShipKey ="";
					String strShipNode ="";
					String strSellerOrgCode="";
					
					Element eleShipmentDetails = null;
					if(!strActionTaken.equalsIgnoreCase("Reprint")){
					Document docGetShipListTemp = XMLUtil
					.createDocument(KohlsConstant.E_SHIPMENTS);
					Element eleGetShpmntsListTemp = docGetShipListTemp.getDocumentElement();
					Element eleGetShipListTemp = docGetShipListTemp
					.createElement(KohlsConstant.E_SHIPMENT);
					eleGetShipListTemp.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
							KohlsConstant.A_BLANK);
					eleGetShipListTemp.setAttribute(KohlsConstant.A_STATUS, 
							KohlsConstant.A_BLANK);
					eleGetShipListTemp.setAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE, 
							KohlsConstant.A_BLANK);
					eleGetShipListTemp.setAttribute(KohlsConstant.A_SHIP_NODE, 
							KohlsConstant.A_BLANK);
					
					//RDC Multi Print Drop2 ----Start
					
					
					
					Element eleChangeShipmentLines = XMLUtil.createChild(
							eleGetShipListTemp, KohlsConstant.E_SHIPMENT_LINES);
					
					
					Element eleChangeShipmentLine = XMLUtil.createChild(
							eleChangeShipmentLines, KohlsConstant.E_SHIPMENT_LINES);
					

					Element eleChangeShipmentLineExtn = XMLUtil.createChild(
							eleChangeShipmentLine, KohlsConstant.E_EXTN);
					eleChangeShipmentLineExtn.setAttribute(KohlsConstant.A_EXTN_BATCH_NO, KohlsConstant.A_BLANK);
					//RDC Multi Print Drop2 ----END
					
					
					
					eleGetShpmntsListTemp.appendChild(eleGetShipListTemp);
					
					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("Template for getShipmentList API:"
								+ XMLUtil.getXMLString(docGetShipListTemp));
					}

					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("Input xml for getShipmentList API:"
								+ XMLUtil.getXMLString(docGetShipmentList));
					}
					
					Document docShipmentListOutput = KOHLSBaseApi.invokeAPI
					(yfsEnvironment, docGetShipListTemp, 
							KohlsConstant.API_actual_GET_SHIPMENT_LIST, docGetShipmentList);

					
					if (YFCLogUtil.isDebugEnabled()) 
					{
						this.log.debug("getShipmentList API Output xml:"
								+ XMLUtil.getXMLString(docShipmentListOutput));
					}
					
					Element eleShipmentListOutput = docShipmentListOutput.getDocumentElement();
					Element eleShip = (Element) eleShipmentListOutput.getElementsByTagName
							(KohlsConstant.E_SHIPMENT).item(0);
					strStatus = eleShip.getAttribute(KohlsConstant.A_STATUS);
					strShipKey = eleShip.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
					strShipNode = eleShip.getAttribute(KohlsConstant.A_SHIP_NODE);
					strSellerOrgCode = eleShip.getAttribute(KohlsConstant.A_SELLER_ORGANIZATION_CODE);
					
					//RDC Drop2 Start
					eleShipmentDetails = eleShip;
					//RDC Drop2
					
					}
					
					if(strStatus.equalsIgnoreCase(KohlsConstant.V_AWAITING_PICKLIST_PRINT)|| strActionTaken.equalsIgnoreCase("Reprint"))
					{
						
						Element eleDocChangeShipStatusOutput=null;
						
						if(!strActionTaken.equalsIgnoreCase("Reprint")) {
						
						//creating input document for changeShipmentStatus api
						
						
						Document docChangeShipmentStatus = XMLUtil.createDocument
						(KohlsConstant.E_SHIPMENT);
						Element eleChangeShipmentStatus = docChangeShipmentStatus
							.getDocumentElement();
						eleChangeShipmentStatus.setAttribute(
								KohlsConstant.E_BASE_DROP_STATUS,
								KohlsConstant.V_BASE_DROP_STATUS);
						eleChangeShipmentStatus.setAttribute(
								KohlsConstant.A_SELLER_ORGANIZATION_CODE, strSellerOrgCode);
						eleChangeShipmentStatus.setAttribute(
								KohlsConstant.A_SHIP_NODE, strShipNode);
						eleChangeShipmentStatus.setAttribute(
								KohlsConstant.A_SHIPMENT_KEY, strShipKey);
						eleChangeShipmentStatus.setAttribute(
								KohlsConstant.A_TRANSACTION_ID,
								KohlsConstant.V_TRANSACTION_ID);
						
						
						if (YFCLogUtil.isDebugEnabled()) 
						{
							this.log.debug("Input xml for changeShipmentStatus API:"
									+ XMLUtil.getXMLString(docChangeShipmentStatus));
						}
						
						Document docChangeShipStatusOutput = KOHLSBaseApi.invokeAPI
						(yfsEnvironment, KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
								docChangeShipmentStatus);

						if (YFCLogUtil.isDebugEnabled())
						{
							this.log.debug("changeShipmentStatus API Output xml:"
									+ XMLUtil.getXMLString(docChangeShipStatusOutput));
						}
						
						
						// Added to avoid Duplicate Prints
						eleDocChangeShipStatusOutput = docChangeShipStatusOutput.getDocumentElement();
						
					}//
						
						if(!YFCObject.isVoid(eleDocChangeShipStatusOutput)|| strActionTaken.equalsIgnoreCase("Reprint")){
							
							Element eleStatus = null;
							if(!YFCObject.isVoid(eleDocChangeShipStatusOutput))
							eleStatus = (Element) eleDocChangeShipStatusOutput.getElementsByTagName("Status").item(0);
							
							if(!YFCObject.isVoid(eleStatus)|| strActionTaken.equalsIgnoreCase("Reprint")){
								 
								String strShipmentStatus ="";
								
								if(!YFCObject.isVoid(eleStatus)){
								strShipmentStatus = eleStatus.getAttribute("Status");
								}
								
								
								
								if((!strActionTaken.equalsIgnoreCase("Reprint")) &&
										(!YFCObject.isVoid(strShipmentStatus) && strShipmentStatus.equals(KohlsConstant.V_BASE_DROP_STATUS))){
									
									/**Commenting out the code for changeShipment to avoid deadlocks **/
									//creating input document for changeShipment api
									
									Document docChangeShipment = XMLUtil.createDocument
									(KohlsConstant.E_SHIPMENT);
									Element eleChangeShipment = docChangeShipment
										.getDocumentElement();
									eleChangeShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
											strShipKey);
									eleChangeShipment.setAttribute(
											KohlsConstant.A_IS_PICK_LIST_PRINTED, KohlsConstant.V_Y);
									
									if(!YFCObject.isVoid(strBatchId))
									{
										eleChangeShipment.setAttribute(KohlsConstant.A_PROFILE_ID, strBatchId);
									}
									
									if(!YFCObject.isVoid(strCartNumber))
									{
										Element ExtnElem = XMLUtil.createChild(eleChangeShipment, "Extn");
										ExtnElem.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, strCartNumber);
									}
									if (YFCLogUtil.isDebugEnabled()) 
									{
										this.log.debug("Input xml for changeShipment API:"
												+ XMLUtil.getXMLString(docChangeShipment));
									}
									//Stamp Extn_Batch_No docChangeShipment, eleShipmentDetails 
									
									
									
									stampExtnBatchNoAtShipemtLine(eleShipmentDetails,  strBatchId, yfsEnvironment);
									
									
									
								
									Document docChangeShipmentOutput = KOHLSBaseApi.invokeAPI
									(yfsEnvironment, KohlsConstant.API_CHANGE_SHIPMENT,
											docChangeShipment);

									if (YFCLogUtil.isDebugEnabled()) 
									{
										this.log.debug("changeShipment API Output xml:"
												+ XMLUtil.getXMLString(docChangeShipmentOutput));
									}
								}
									
									if (YFCLogUtil.isDebugEnabled()) 
									{
										this.log.debug("Input xml for getShipmentDetails API:"
												+ XMLUtil.getXMLString(docGetShipmentList));
									}
									
									yfsEnvironment.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS,
										     KohlsConstant.API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH_1);
									
									Document docShipmentDetailsOutput = KOHLSBaseApi.invokeAPI(yfsEnvironment,
										     KohlsConstant.API_GET_SHIPMENT_DETAILS, docGetShipmentList);
									
									yfsEnvironment.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS);
									
								

									if (YFCLogUtil.isDebugEnabled()) 
									{
										this.log.debug("getShipmentDetails API Output xml:"
												+ XMLUtil.getXMLString(docShipmentDetailsOutput));
									}
									
									
									Element eleShipmentDetailsOutput = docShipmentDetailsOutput
										.getDocumentElement();
									int j = i+1;
									String strToteId = Integer.toString(j);
									String strShipNo = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_SHIPMENT_NO);
									strShipNode_v = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_SHIP_NODE);
									
									//creating shipment element for jasper input
									Element elementShip = XMLUtil.createChild(eleMultiServiceInput, KohlsConstant.E_SHIPMENT) ;
									elementShip.setAttribute(KohlsConstant.A_TOTEID, strToteId);
									elementShip.setAttribute(KohlsConstant.A_SHIPMENT_NO, strShipNo);
									elementShip.setAttribute("IsPickListPrintedStatus", "Y");
									
									
									Element eleShipmentLines =XMLUtil.createChild(elementShip, KohlsConstant.E_SHIPMENT_LINES) ;
									
									
									Element eleShipLines = (Element) eleShipmentDetailsOutput.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0);
									NodeList nlShipmentLines = eleShipLines
									.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
									int iLen = nlShipmentLines.getLength();
									
									if(iLen!=0)
									{
										for(int l=0;l<iLen;l++)
										{
											Element eleShipmentLine = (Element)nlShipmentLines.item(l);
											
											String strDesc = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_DESC);
											String strQty = eleShipmentLine.getAttribute(KohlsConstant.A_QUANTITY);
											String strCallOrgCode = eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_ENTERPRISE_CODE);
											String strItemID = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_ID);
											String strUOM = eleShipmentLine.getAttribute(KohlsConstant.A_UNIT_OF_MEASURE);
											
											HashsetItemID.add(strItemID);
											
											Element eleItemAlias = null;
											String strAliasName = "";
											String strAliasValue = "";
											
											//creating input xml for getItemNodeDefnDetails API
											Document docGetItemNodeDefnDet = XMLUtil.createDocument(KohlsConstant.E_ITEM_NODE_DEFN);
											Element eleGetItemNodeDefnDet = docGetItemNodeDefnDet.getDocumentElement();
											eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_CALLING_ORGANIZATION_CODE, strCallOrgCode);
											eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
											eleGetItemNodeDefnDet.setAttribute(KohlsConstant.A_NODE, strShipNode_v);
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

											
											Element eleItemNodeDefnDet = docItemNodeDefnDet.getDocumentElement();
											Element elemExtn = (Element) eleItemNodeDefnDet.getElementsByTagName(KohlsConstant.E_EXTN).item(0);
											String strLineNo = elemExtn.getAttribute(KohlsConstant.A_EXTN_LINE_NO);
											String strLocationID = elemExtn.getAttribute(KohlsConstant.A_EXTN_LOCATION_ID);
											
											
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
											
										    Element eleShipmentLineEle= XMLUtil.createChild(eleShipmentLines,KohlsConstant.E_SHIPMENT_LINE);
											if(strAliasName.equalsIgnoreCase(KohlsConstant.V_UPC01))
											{
												eleShipmentLineEle.setAttribute(KohlsConstant.V_UPC01, strAliasValue);
											}
											if(l==0)
											{
												eleShipmentLineEle.setAttribute(KohlsConstant.A_SHIPMENT_NO, strShipNo);
											}
											eleShipmentLineEle.setAttribute(KohlsConstant.A_ITEM_ID, strItemID);
											eleShipmentLineEle.setAttribute(KohlsConstant.A_TOTEID, strToteId);
											eleShipmentLineEle.setAttribute(KohlsConstant.A_ITEM_DESC, strDesc);
											eleShipmentLineEle.setAttribute(KohlsConstant.A_QUANTITY, strQty);
											eleShipmentLineEle.setAttribute(KohlsConstant.A_EXTN_LINE_NO, strLineNo);
											eleShipmentLineEle.setAttribute(KohlsConstant.A_EXTN_LOCATION_ID, strLocationID);
											
											
										} // end of for loop 'l'
									} //end of iLen if
								}
							}
						}
						
					 //end of status chk if
										
				} //end of for loop 'i'
			} //end of nodelist length check
			
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
						
						NodeList nlShipmentLine = eleMultiServiceInput.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
						int iLeng = nlShipmentLine.getLength();
						int iSumQty = 0;
						
						for(int k=0;k<iLeng;k++)
						{
							Element eleShipmentLine = (Element) nlShipmentLine.item(k);
							String strItemID = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_ID);
							if(!YFCObject.isVoid(strItemID) && strItemID.equalsIgnoreCase(strItemIDHashSet))
							{
								String strqty = eleShipmentLine.getAttribute(KohlsConstant.A_QUANTITY);
								int iQty = (int) Double.parseDouble(strqty);
								iSumQty = iSumQty + iQty;
							}
						}
						
						for(int k=0;k<iLeng;k++)
						{
							Element eleShipmentLine = (Element) nlShipmentLine.item(k);
							String strItemID = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_ID);
							if(!YFCObject.isVoid(strItemID) && strItemID.equalsIgnoreCase(strItemIDHashSet))
							{
								eleShipmentLine.setAttribute(KohlsConstant.A_SKU_QTY, Integer.toString(iSumQty));
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
					/*if(!YFCObject.isVoid(eleShipLines))
					{
						calculateSkuQty(eleShipLines);
					}*/
					
				} //end of while loop
			}
			
			eleMultiServiceInput.setAttribute(KohlsConstant.A_SHIP_NODE, strShipNode_v);
			
			if (YFCLogUtil.isDebugEnabled()) 
			{
				this.log.debug("Input xml for the service:"
						+ XMLUtil.getXMLString(docMultiServiceInput));
			}
			
			boolean bIsPrintedStatusShipment = false;
			NodeList nlShipment = eleMultiServiceInput.getElementsByTagName(KohlsConstant.E_SHIPMENT);
			int iNoOfShipments = nlShipment.getLength();
						
			for(int iLoop=0;iLoop<iNoOfShipments;iLoop++)
			{
				Element eleShipment = (Element) nlShipment.item(iLoop);				
				String strIsPickListPrintedStatus = eleShipment.getAttribute("IsPickListPrintedStatus");
				
				if(!YFCObject.isVoid(strIsPickListPrintedStatus) && strIsPickListPrintedStatus.equalsIgnoreCase("Y"))
				{
					bIsPrintedStatusShipment = true;
					break;
				}
			}
			
		/*	
		 * Bhasker -- RDC for Multi for Drop2
		 * if(bIsPrintedStatusShipment || strActionTaken.equalsIgnoreCase("Reprint")){
				
				docOutput=KOHLSBaseApi.invokeService(yfsEnvironment, 
						KohlsConstant.SERVICE_JASPER_MULTI_PRINT_SERVICE ,docMultiServiceInput);
			}*/
			
		} //end of input document null check
		return docOutput;
	} //end of class


	
	
private boolean stampExtnBatchNoAtShipemtLine(Element outDocShipmentEle, String strGenratedBatchNO,YFSEnvironment env) throws Exception {
		
		boolean isExtnBatchNoUpdated = false;
		Document docInputChangeShipment = XMLUtil.createDocument(KohlsXMLLiterals.E_SHIPMENT);

		
			Element eleChangeShipmentInput = docInputChangeShipment
					.getDocumentElement();
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_ACTION,
					KohlsConstant.ACTION_MODIFY);
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
					outDocShipmentEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
			eleChangeShipmentInput.setAttribute(KohlsConstant.A_PROFILE_ID,
					strGenratedBatchNO);
			Element eleChangeShipmentLinesInput = XMLUtil.createChild(
					eleChangeShipmentInput, KohlsConstant.E_SHIPMENT_LINES);
			Element eleShipLines = (Element) outDocShipmentEle
					.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES)
					.item(0);

			NodeList nlShipmentLines = eleShipLines
					.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);
			int iLen = nlShipmentLines.getLength();
			if (iLen != 0) {
				for (int i = 0; i < iLen; i++) {
					Element eleShipmentLine = (Element) nlShipmentLines
							.item(i);
					
					Element eleChangeShipmentLineInput = XMLUtil.createChild(
							eleChangeShipmentLinesInput, KohlsConstant.E_SHIPMENT_LINE);
					
					eleChangeShipmentLineInput.setAttribute(KohlsConstant.A_ACTION,
					        KohlsConstant.ACTION_MODIFY);
					eleChangeShipmentLineInput.setAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY,
							eleShipmentLine.getAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY));
					
					
					Element eleChangeShipExtnInput = XMLUtil
							.createChild(eleChangeShipmentLineInput, KohlsConstant.E_EXTN);
					eleChangeShipExtnInput.setAttribute(
							KohlsConstant.A_EXTN_BATCH_NO, strGenratedBatchNO);
				}
			}
			System.out.println("ChangeShipment ----RDC"+XMLUtil.getXMLString(eleChangeShipmentInput.getOwnerDocument()));
			NodeList shiplLineNodeList = XPathUtil.getNodeList(
					eleChangeShipmentInput, "ShipmentLines/ShipmentLine");
			if (null != shiplLineNodeList
					&& shiplLineNodeList.getLength() > 0) {
				KOHLSBaseApi.invokeAPI(env,KohlsConstant.API_CHANGE_SHIPMENT,
						eleChangeShipmentInput.getOwnerDocument());
				isExtnBatchNoUpdated = true;
			}
	
		return isExtnBatchNoUpdated;
	}
	
}