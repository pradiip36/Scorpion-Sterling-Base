package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.pca.bridge.YCDFoundationBridge;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author puneet
 * 
 */
public class KOHLSChangeStatusOnPackAndUnPackAPI extends KOHLSBaseApi {
	/**
	* 
	* @param yfsEnvironment
	* @param inputDoc
	* @return
	* @throws Exception
	*/
	
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSChangeStatusOnPackAndUnPackAPI.class.getName());
	
	public Document changeShipmentStatus(YFSEnvironment env, Document inputDoc)
	throws Exception {

		Document outputDoc = null;

		if (YFCObject.isVoid(inputDoc)) {
			outputDoc = inputDoc;
		}

		if (!YFCObject.isVoid(inputDoc)) {
			Element inputElem = inputDoc.getDocumentElement();
			String sStatus = inputElem.getAttribute(KohlsConstant.A_STATUS);
			String sShipmentContainerizedFlag = inputElem
			.getAttribute(KohlsConstant.A_SHIPMENT_CONTAINERIZED_FLAG);
			String sChangedStatus = sStatus;
			Element  shipNodeElem= (Element)inputElem.getElementsByTagName
			(KohlsConstant.E_SHIP_NODE).item(0);
			String sDcmIntegrationRealTime = shipNodeElem.getAttribute(KohlsConstant.A_DcmIntegrationRealTime);
			if (KohlsConstant.V_SHIPMENT_CONTAINERIZED_01
					.equalsIgnoreCase(sShipmentContainerizedFlag)
					&& !KohlsConstant.V_SHIPMENT_PICK_LIST_PRINTED_STATUS
					.equalsIgnoreCase(sStatus))

			{
				sChangedStatus = KohlsConstant.V_SHIPMENT_PICK_LIST_PRINTED_STATUS;
				String sTransactionId = KohlsConstant.A_PICK_LIST_PRINT_TRANSACTION_ID;
//				callChangeShipmentStatus(env, inputDoc, sTransactionId,
//						sChangedStatus);
				callChangeShipmentStatusForPickListPrint(env, inputDoc, sTransactionId,
					sChangedStatus);

			}

			if (KohlsConstant.V_SHIPMENT_CONTAINERIZED_02
					.equalsIgnoreCase(sShipmentContainerizedFlag)
					&& !KohlsConstant.A_Shipment_Pack_In_Progress
					.equalsIgnoreCase(sStatus))

			{
				sChangedStatus = KohlsConstant.A_Shipment_Pack_In_Progress;
				String sTransactionId = KohlsConstant.A_SHIPMENT_PACK_TRANSACTION_ID;
				callChangeShipmentStatus(env, inputDoc, sTransactionId,
						sChangedStatus);

			}
			if (KohlsConstant.V_SHIPMENT_CONTAINERIZED_03
					.equalsIgnoreCase(sShipmentContainerizedFlag)
					&& !KohlsConstant.A_PACK_COMPLETE_BASE_DROP_STATUS
					.equalsIgnoreCase(sStatus))

			{
				sChangedStatus = KohlsConstant.A_PACK_COMPLETE_BASE_DROP_STATUS;
				String sTransactionId = KohlsConstant.A_PACK_COMPLETE_TRANSACTION_ID;
				callChangeShipmentStatus(env, inputDoc, sTransactionId,
						sChangedStatus);

			}
			

			if(!YFCObject.isVoid(env.getTxnObject(KohlsConstant.A_ACTION_TAKEN)) && 
					!YFCObject.isVoid(env.getTxnObject(KohlsConstant.A_CONTAINERSCM)) && 
					sDcmIntegrationRealTime.equalsIgnoreCase(KohlsConstant.V_Y))
			{
				String sContainerScm = env.getTxnObject(KohlsConstant.A_CONTAINERSCM).toString();
				String sActionTaken =env.getTxnObject(KohlsConstant.A_ACTION_TAKEN).toString();
				Element eleContainerElem = null;
				if(sActionTaken.equalsIgnoreCase(KohlsConstant.V_ACTION_TAKEN_PACK))
				{
				eleContainerElem = (Element) XPathUtil.getNodeList(
						inputElem,
						"/Shipment/Containers/Container[@ContainerScm='"
						+ sContainerScm + "']").item(0);
				}else if (sActionTaken.equalsIgnoreCase(KohlsConstant.V_ACTION_TAKEN_UNPACK) &&
						!YFCObject.isVoid(env.getTxnObject(KohlsConstant.O_CONTAINER_ELEM)))
				{
					eleContainerElem = (Element) env.getTxnObject(KohlsConstant.O_CONTAINER_ELEM);
				}
				
				if(!YFCObject.isVoid(eleContainerElem))
				
				{

					
					callMoveLocationInventory(env,inputElem,sContainerScm,sActionTaken,eleContainerElem);
				}

			}

			inputElem.setAttribute(KohlsConstant.A_STATUS, sChangedStatus);

		}
		return inputDoc;
	}

	private void callMoveLocationInventory(YFSEnvironment env,
			Element inputElem, String sContainerScm, String sActionTaken,
			Element eleContainerElem) throws Exception {
		YFCElement multiApiElement = YFCDocument.createDocument(KohlsConstant.E_MULTI_API).getDocumentElement();
		boolean callmultiApi = false;
		NodeList nContainerDetailList = eleContainerElem.getElementsByTagName(KohlsConstant.E_CONTAINER_DETAIL);
		for (int j = 0; j < nContainerDetailList.getLength(); j++) {
			
			callmultiApi= true;
			Element elemContainDetail = (Element)nContainerDetailList.item(j);
			
			YFCElement apiElement = multiApiElement.createChild(KohlsConstant.E_API);
			apiElement.setAttribute(KohlsConstant.A_NAME, KohlsConstant.API_MOVE_LOCATION_INVENTORY);
			YFCElement apiInputElement = apiElement.createChild(KohlsConstant.E_INPUT);
			YFCElement MoveLocationInventoryElem =apiInputElement.createChild(KohlsConstant.
					E_MOVE_LOCATION_INVENTORY);
			MoveLocationInventoryElem.setAttribute(KohlsConstant.A_ENTERPRISE_CODE, 
					inputElem.getAttribute(KohlsConstant.A_ENTERPRISE_CODE));
			MoveLocationInventoryElem.setAttribute(KohlsConstant.A_Node, 
					inputElem.getAttribute(KohlsConstant.A_SHIP_NODE));
			YFCElement SourceElem =MoveLocationInventoryElem.createChild(KohlsConstant.
					E_SOURCE);
			YFCElement DestinationElem =MoveLocationInventoryElem.createChild(KohlsConstant.
					E_DESTINATION);
			YFCElement InventoryElem =SourceElem.createChild(KohlsConstant.
					E_INVENTORY);
			InventoryElem.setAttribute(KohlsConstant.A_QUANTITY, elemContainDetail.
					getAttribute(KohlsConstant.A_QUANTITY));
			YFCElement InventoryItemElem =InventoryElem.createChild(KohlsConstant.
					E_INVENTORY_ITEM);
			InventoryItemElem.setAttribute(KohlsConstant.A_ITEM_ID, elemContainDetail.
					getAttribute(KohlsConstant.A_ITEM_ID));
			InventoryItemElem.setAttribute(KohlsConstant.A_PRODUCT_CLASS, elemContainDetail.
					getAttribute(KohlsConstant.A_PRODUCT_CLASS));
			InventoryItemElem.setAttribute(KohlsConstant.A_UNIT_OF_MEASURE, elemContainDetail.
					getAttribute(KohlsConstant.A_UNIT_OF_MEASURE));
			if(sActionTaken.equalsIgnoreCase(KohlsConstant.V_ACTION_TAKEN_PACK))
			{
				SourceElem.setAttribute(KohlsConstant.A_LOCATION_ID, KohlsConstant.V_NODE_DEFAULT_LOCATION);
				DestinationElem.setAttribute(KohlsConstant.A_CASE_ID, sContainerScm);
			}else if(sActionTaken.equalsIgnoreCase(KohlsConstant.V_ACTION_TAKEN_UNPACK))

			{
				SourceElem.setAttribute(KohlsConstant.A_CASE_ID, sContainerScm);
				DestinationElem.setAttribute(KohlsConstant.A_LOCATION_ID, KohlsConstant.V_NODE_DEFAULT_LOCATION);
				
			}

           


		}
		if(callmultiApi)
		{
			
			if (YFCLogUtil.isDebugEnabled()) 
			{
				this.log.debug("Input xml for the sort service:"
						+ XMLUtil.getXMLString(multiApiElement.getOwnerDocument().getDocument()));
			}
		
		Document docMultiApiSortedInput=KOHLSBaseApi.invokeService(env, KohlsConstant.SERVICE_KOHLS_MULTIAPI_INPUT_SORT_BY_ITEMID_XSL, multiApiElement.getOwnerDocument().getDocument());
		
		if (YFCLogUtil.isDebugEnabled()) 
		{
			this.log.debug("Input xml for the multiApi :"
					+ XMLUtil.getXMLString(docMultiApiSortedInput));
		}
		YFCDocument multiApiOutputDoc = YCDFoundationBridge.getInstance().multiApi(env, YFCDocument.getDocumentFor(docMultiApiSortedInput));
				
	}


	}
	
	private Document callChangeShipmentStatus(YFSEnvironment env,
			Document inputDoc, String sTransactionId, String sBaseDropStatus)
	throws Exception {

		Document outPutDoc = null;
		Document inputDocForChangeShipmentStatus = XMLUtil
		.createDocument(KohlsConstant.E_SHIPMENT);
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"ShipmentKey",
				inputDoc.getDocumentElement().getAttribute("ShipmentKey"));
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"TransactionId", sTransactionId);
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"BaseDropStatus", sBaseDropStatus);

		// env.setApiTemplate(KOHLSConstants.API_CHANGE_SHIPMENT_STATUS,
		// KOHLSConstants.API_PACK_SHIPMENT_TEMPLATE_PATH);
		outPutDoc = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
				inputDocForChangeShipmentStatus);
		//env.clearApiTemplate(KOHLSConstants.API_CHANGE_SHIPMENT_STATUS);
		// TODO Auto-generated method stub
		return outPutDoc;
	}
	
	private Document callChangeShipmentStatusForPickListPrint(YFSEnvironment env,
			Document inputDoc, String sTransactionId, String sBaseDropStatus)
	throws Exception {

		Document outPutDoc = null;
		Document inputDocForChangeShipmentStatus = XMLUtil
		.createDocument(KohlsConstant.E_SHIPMENT);
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"ShipmentKey",
				inputDoc.getDocumentElement().getAttribute("ShipmentKey"));
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"TransactionId", sTransactionId);
		inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
				"BaseDropStatus", sBaseDropStatus);

		env.setApiTemplate(KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
				KohlsConstant.API_PACK_SHIPMENT_TEMPLATE_VOID_LABEL_PATH);
		outPutDoc = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
				inputDocForChangeShipmentStatus);
		env.clearApiTemplate(KohlsConstant.API_CHANGE_SHIPMENT_STATUS);
		// TODO Auto-generated method stub
		return outPutDoc;
	}

}
