package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.shared.dbclasses.YFS_ShipmentDBHome;
import com.yantra.shared.dbi.YFS_Shipment;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dblayer.YFCDBContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfc.util.YFCStringUtil;

/**
 * This class invokes respective APIs and services for
 * Multipack shipments
 * 
 * @author Suraj
 * @Modified Kiran
 *
 */
public class KOHLSProcessPrintMultiPackShipmentAPI extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSProcessPrintMultiPackShipmentAPI.class.getName());
	
	/**
	 * This method invokes respective APIs and services
	 * for multipack shipments
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public void processPrintMultiPackShipment(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception
	{
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSProcessPrintMultiPackShipmentAPI" +
					" processPrintMultiPackShipment method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		int TotalCartsToPrint = 0;
		Element eleInput = inputDoc.getDocumentElement();
		String strPrinterID = eleInput.getAttribute(KohlsConstant.A_PRINTER_ID);
		String strBatchId = eleInput.getAttribute(KohlsConstant.A_BATCH_ID);
		String strShipNode = eleInput.getAttribute(KohlsConstant.A_SHIP_NODE);
		
		//create dynamic service name to be called for the shipnode
		
		String strServiceName=KohlsConstant.V_PREFIX+strShipNode+KohlsConstant.MULTI_PRINT_SERVICE;

		if(!YFCObject.isVoid(inputDoc))
		{
			String strNode = eleInput.getNodeName();

			if(strNode.equalsIgnoreCase(KohlsConstant.E_PRINT_PACK_SHIPMENT))  
			{
				
				String strTotalCarts = eleInput.getAttribute(KohlsConstant.A_TOTAL_CARTS);
				String strTotesPerCart = eleInput.getAttribute(KohlsConstant.A_TOTES_PER_CART);
				String strTotalShipmentList = "";
				int iTotesPerCart = 0;
				
				if((!YFCStringUtil.isVoid(strTotesPerCart) && (!YFCStringUtil.isVoid(strTotalCarts)))){
				
					iTotesPerCart = Integer.parseInt(strTotesPerCart);
					int iTotalCarts = Integer.parseInt(strTotalCarts);
					
					int iTotalShipmentList = iTotesPerCart * iTotalCarts;
					strTotalShipmentList = iTotalShipmentList+"";
				}

				// Creating input for getShimentList API
				Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
				Element eleShipmentListInput = docShipmentListInput.getDocumentElement();
				eleShipmentListInput.setAttribute(KohlsConstant.A_IGNORE_ORDERING, KohlsConstant.V_N);
				//eleShipmentListInput.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS, KohlsConstant.V_MAXIMUM_RECORDS);
				eleShipmentListInput.setAttribute(KohlsConstant.A_STATUS, KohlsConstant.A_AWAITING_PICK_LIST);
				eleShipmentListInput.setAttribute(
						KohlsConstant.A_IS_PICKTICKET_PRINTED, KohlsConstant.V_N);
				Element eleorderby=docShipmentListInput.createElement(KohlsConstant.E_ORDER_BY);
				eleShipmentListInput.appendChild(eleorderby);
				Element eleattribute=docShipmentListInput.createElement(KohlsConstant.A_ATTRIBUTE);
				eleorderby.appendChild(eleattribute);
				
				/*
				 * Start OASIS -- 05/NOV/2013 for PMR  85613,379,000
				 * Adding logic to getShipmentList order by OrderHeaderKey, to get oldest shipments first
				 * 
				 */
				//eleattribute.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_NO);
				eleattribute.setAttribute(KohlsConstant.A_NAME, "OrderHeaderKey");
				// End OASIS -- 05/NOV/2013 for PMR  85613,379,000
				
				String strShipmentType = eleInput.getAttribute(KohlsConstant.A_SHIPMENT_TYPE);		
				eleShipmentListInput.setAttribute(KohlsConstant.A_SHIP_NODE, strShipNode);
				eleShipmentListInput.setAttribute(KohlsConstant.A_SHIPMENT_TYPE, strShipmentType);
				eleShipmentListInput.setAttribute(KohlsConstant.A_MAXIMUM_RECORDS, strTotalShipmentList);
				
				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("Input xml for getShipmentList API:"
							+ XMLUtil.getXMLString(docShipmentListInput));
				}

				// Creating template document for getShipmentList API
				Document docGetShipListTemp = XMLUtil
				.createDocument(KohlsConstant.E_SHIPMENTS);
				Element eleGetShpmntsListTemp = docGetShipListTemp.getDocumentElement();
				Element eleGetShipListTemp = docGetShipListTemp
				.createElement(KohlsConstant.E_SHIPMENT);
				eleGetShipListTemp.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
						KohlsConstant.A_BLANK);
				eleGetShpmntsListTemp.appendChild(eleGetShipListTemp);

				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("Template for getShipmentList API:"
							+ XMLUtil.getXMLString(docGetShipListTemp));
				}

				Document docShipmentListOutput = KOHLSBaseApi.invokeAPI
				(yfsEnvironment, docGetShipListTemp, 
						KohlsConstant.API_actual_GET_SHIPMENT_LIST, docShipmentListInput);

				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("getShipmentList API Output xml:"
							+ XMLUtil.getXMLString(docShipmentListOutput));
				}

				Element eleShipmentListOutput = docShipmentListOutput.getDocumentElement();

				NodeList nodeShipmentList = eleShipmentListOutput
				.getElementsByTagName(KohlsConstant.E_SHIPMENT);
				int iShipmentsToPrint = nodeShipmentList.getLength();

				if(iShipmentsToPrint!=0 && iTotesPerCart!=0)
				{					
					int iLoopTime = iShipmentsToPrint/iTotesPerCart;
					TotalCartsToPrint = iLoopTime;
				   int iRemainder = iShipmentsToPrint%iTotesPerCart;
					
					if(iRemainder>0){
						TotalCartsToPrint = TotalCartsToPrint + 1;
					}
					String strTotalCartsToPrint = Integer.toString(TotalCartsToPrint);
					for(int i=0;i<iLoopTime;i++) {

						// creating input xml for the dynamic service
						Document docInputForService = XMLUtil
						.createDocument(KohlsConstant.E_SHIPMENTS);
						Element eleInputForService = docInputForService.getDocumentElement();
						eleInputForService.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
						eleInputForService.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
						eleInputForService.setAttribute(KohlsConstant.A_TOTAL_CARTS,strTotalCartsToPrint);
						eleInputForService.setAttribute(KohlsConstant.A_CART_NUMBER,Integer.toString(i+1));
						
						int iLoopStart = i*iTotesPerCart;
						int iLoopEnd = (i+1)*iTotesPerCart-1;
						
						for(int j=iLoopStart;j<=iLoopEnd;j++)
						{
							Element eleShipment = (Element)nodeShipmentList.item(j);
							String strShipmentKey = eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY);

							Element eleShip = docInputForService
							.createElement(KohlsConstant.E_SHIPMENT);
							eleShip.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
									strShipmentKey);
							eleInputForService.appendChild(eleShip);
						}
						
						if (YFCLogUtil.isDebugEnabled()) 
						{
							this.log.debug("Input xml for the service:"
									+ XMLUtil.getXMLString(docInputForService));
						}
						
						// call dynamic service for the shipnode
						
						callChangeShipmentSetPickTicketFlag(yfsEnvironment,docInputForService);
						KOHLSBaseApi.invokeService(yfsEnvironment, 
								strServiceName, docInputForService);						
					}
					
					int iRemainderLeft = iShipmentsToPrint%iTotesPerCart;
					
					if(iRemainderLeft>0){
						
						// creating input xml for the dynamic service
						Document docInputForService = XMLUtil
						.createDocument(KohlsConstant.E_SHIPMENTS);
						Element eleInputForService = docInputForService.getDocumentElement();
						eleInputForService.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
						eleInputForService.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
						eleInputForService.setAttribute(KohlsConstant.A_TOTAL_CARTS,strTotalCartsToPrint);
						eleInputForService.setAttribute(KohlsConstant.A_CART_NUMBER,Integer.toString(iLoopTime+1));
						
						int iLoopStart = iLoopTime*iTotesPerCart;
						
						for(int j=iLoopStart;j<iShipmentsToPrint;j++)
						{
							Element eleShipment = (Element)nodeShipmentList.item(j);
							String strShipmentKey = eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY);

							Element eleShip = docInputForService
							.createElement(KohlsConstant.E_SHIPMENT);
							eleShip.setAttribute(KohlsConstant.A_SHIPMENT_KEY,
									strShipmentKey);
							eleInputForService.appendChild(eleShip);	
						}
						
						if (YFCLogUtil.isDebugEnabled()) 
						{
							this.log.debug("Input xml for the service:"
									+ XMLUtil.getXMLString(docInputForService));
						}
						
						callChangeShipmentSetPickTicketFlag(yfsEnvironment,docInputForService);
						
						// call dynamic service for the shipnode
						KOHLSBaseApi.invokeService(yfsEnvironment, 
								strServiceName, docInputForService);
						
						
					}
					
				} //end of iLength check
			}

			else
			{
				
				if (YFCLogUtil.isDebugEnabled()) 
				{
					this.log.debug("Input xml for the service:"
							+ XMLUtil.getXMLString(inputDoc));
				}
				
				
				/*
				 * Bhaskar RDC -multi for Drop2
				 * 
					KOHLSBaseApi.invokeService(yfsEnvironment, 
						KohlsConstant.SERVICE_KOHLS_CHANGE_STATUS_MULTI_PRINT , inputDoc);
				*/
				//call to our KOHLSChangeMultiShipmentStatusAPI_Drop2 service
				
				
				
				KOHLSBaseApi.invokeService(yfsEnvironment, 
						KohlsConstant.SERVICE_KOHLS_CHANGE_STATUS_MULTI_PRINT_DROP2 , inputDoc);
				
				
			}
		}
	}

	
	//update each shipment in the input to IsPickTicketPrinted='Y'
	private void callChangeShipmentSetPickTicketFlag(YFSEnvironment env, Document docCartShipments) {
		
		Element eleShipmentsListInCart=docCartShipments.getDocumentElement();
		NodeList nShipmentList = eleShipmentsListInCart.getElementsByTagName(KohlsConstant.E_SHIPMENT);
		for (int j = 0; j < nShipmentList.getLength(); j++) {
			
			Element elemShipmentDocInput = (Element)nShipmentList.item(j);
			String strShipmentKey=elemShipmentDocInput.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
			if(YFCObject.isVoid(strShipmentKey))
				return ;
			YFSContext ctx = (YFSContext) env;
			YFS_Shipment oShipment = YFS_ShipmentDBHome.getInstance().selectWithPKForUpd(ctx, strShipmentKey);
			if(YFCObject.isVoid(oShipment))
				return;
		//	oShipment.setIs_Pickticket_Printed(KohlsConstant.V_Y);
			
			oShipment.update();
			

		}
		
	}
}
