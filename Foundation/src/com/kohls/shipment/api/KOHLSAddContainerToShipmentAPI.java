package com.kohls.shipment.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsDateUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author puneet.
 * 
 */
public class KOHLSAddContainerToShipmentAPI extends KOHLSBaseApi {
	//private static final Object V_STORE = null;

	/**
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	
	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSAddContainerToShipmentAPI.class.getName());

	public Document addToContainer(YFSEnvironment env, Document inputDoc)
	throws Exception {

		log.debug("KOHLSAddContainerToShipmentAPI: addToContainer: Begin");
		
		Document outputDoc = null;

		if (YFCObject.isVoid(inputDoc)) {
			outputDoc = inputDoc;
		}
		
		/* ProShip Integration Changes Start */
		// Setting this environment object because shipment level details are needed
		// while making proShip call in beforePackShipmentUE
		Element eleShipmentDoc = XMLUtil.getChildElement(inputDoc.getDocumentElement(), "ShipmentDocument");
		env.setTxnObject("ShipmentInfo", eleShipmentDoc);
		
		Element inputDocument = XMLUtil.getChildElement(inputDoc.getDocumentElement(), "InputDocument");
		/* ProShip Integration Changes End */
		
		String sContainerScm = "";
		String sContainerNo = "";
		String sPrinterID= "";
		boolean bRemoveContainer = false;

		/* ProShip Integration Changes Start */
		if (!YFCObject.isVoid(inputDocument)) {
			Element inputElem = XMLUtil.getChildElement(inputDocument, "Shipment");
		/* ProShip Integration Changes End */
		
			log.debug("KOHLSAddContainerToShipmentAPI: addToContainer: inputDoc: " + XMLUtil.getXMLString(inputDoc));
			inputElem
			.removeAttribute(KohlsConstant.A_IS_PACK_PROCESS_COMPLETE);
			
			//Start changes for SFS June Release
			String strcollateflag="N";
			strcollateflag= inputElem.getAttribute("IsCollateFlag");
			
			if(!strcollateflag.isEmpty() && strcollateflag.equalsIgnoreCase("Y"))
			{
				log.debug("strcollateflag="+strcollateflag);
				env.setTxnObject("CollateTxn", strcollateflag);
			
			}
			else
			{
				log.debug("strcollateflag is null");
			}
			//End changes for SFS June Release

			Element eleShipNode=(Element) inputElem.getElementsByTagName(
					KohlsConstant.A_SHIP_NODE).item(0);

			String strNodeType=eleShipNode.getAttribute(KohlsConstant.A_NODE_TYPE);

			String strShipmentType=inputElem.getAttribute(KohlsConstant.A_SHIPMENT_TYPE);


			Element containerElem = (Element) inputElem.getElementsByTagName(
					KohlsConstant.A_CONTAINER).item(0);
			containerElem.setAttribute(KohlsConstant.A_FORM_ID, containerElem.getAttribute(KohlsConstant.A_CONTAINER_TYPE));
			if(!YFCObject.isVoid(containerElem.getAttribute(KohlsConstant.A_COMMITMENT_CODE)))
			{
				containerElem.setAttribute(KohlsConstant.A_COMMITMENT_CODE, containerElem.getAttribute(KohlsConstant.A_COMMITMENT_CODE));
			}
			containerElem.setAttribute(KohlsConstant.A_CONTAINER_TYPE, KohlsConstant.V_CASE);
			containerElem.setAttribute(KohlsConstant.A_IS_PACK_PROCESS_COMPLETE, KohlsConstant.V_Y);
			env.setTxnObject(KohlsConstant.O_CONTAINER_ELEM, containerElem);
			sContainerScm = containerElem
			.getAttribute(KohlsConstant.A_CONTAINERSCM);
			env.setTxnObject(KohlsConstant.A_CONTAINERSCM, sContainerScm);
			env.setTxnObject(KohlsConstant.A_ACTION_TAKEN, KohlsConstant.V_ACTION_TAKEN_PACK);
			sContainerNo = containerElem
			.getAttribute(KohlsConstant.A_CONTAINER_NO);
			sPrinterID = inputElem
			.getAttribute(KohlsConstant.A_PRINTER_ID);
			if (!YFCObject.isVoid(containerElem
					.getAttribute(KohlsConstant.A_ACTION))) {
				if (containerElem.getAttribute(KohlsConstant.A_ACTION)
						.equalsIgnoreCase(KohlsConstant.V_DELETE)) {
					bRemoveContainer = true;
					env.setTxnObject(KohlsConstant.A_ACTION_TAKEN, KohlsConstant.V_ACTION_TAKEN_UNPACK);
				}
			}

			/* ProShip Integration Changes Start */
			outputDoc = callPackShipmentOrUnPackShipment(env, XMLUtil.createDocument(inputElem),
					bRemoveContainer);
					
			//if(!bRemoveContainer)
			//fetchShipmentContainerDetailsForProShip(env, outputDoc, sContainerScm);
			/* ProShip Integration Changes End */

			if(strNodeType.equals(KohlsConstant.V_STORE)){

				outputDoc.getDocumentElement().setAttribute(
						KohlsConstant.A_NODE_TYPE,
						KohlsConstant.V_STORE);

			}

			outputDoc.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIPMENT_TYPE,
					strShipmentType);



			if (bRemoveContainer) {
				outputDoc.getDocumentElement().setAttribute(
						KohlsConstant.A_ACTION_TAKEN,
						KohlsConstant.V_ACTION_TAKEN_UNPACK);

			} else {
				outputDoc.getDocumentElement().setAttribute(
						KohlsConstant.A_ACTION_TAKEN,
						KohlsConstant.V_ACTION_TAKEN_PACK);



			}

			if(!bRemoveContainer){
				outputDoc.getDocumentElement().setAttribute(
						KohlsConstant.A_LATEST_CONTAINER_SCM, sContainerScm);
				outputDoc.getDocumentElement().setAttribute(
						KohlsConstant.A_PRINTER_ID, sPrinterID);
				//System.out.println("here we come " +sContainerScm + " " + XMLUtil.getXMLString(outputDoc) );

				ArrayList<String> aContainerScm = new ArrayList();

				Element outContainersElem = (Element) outputDoc.getDocumentElement()
				.getElementsByTagName(KohlsConstant.A_CONTAINERS).item(0);
				NodeList nlContainerList = outContainersElem
				.getElementsByTagName(KohlsConstant.A_CONTAINER);
				String sContainerNetWeight = "0.00";
				for (int intCtnCount = 0; intCtnCount < nlContainerList.getLength(); intCtnCount++) {
					Element eleContainer = (Element) nlContainerList.item(intCtnCount);
					String sLastSCM = eleContainer
					.getAttribute(KohlsConstant.A_CONTAINERSCM);
					String sLastContainerNo =eleContainer
					.getAttribute(KohlsConstant.A_CONTAINER_NO);
					if(sLastContainerNo.equalsIgnoreCase(sContainerNo))
					{
						sContainerNetWeight = eleContainer.getAttribute(KohlsConstant.a_CONTAINER_NET_WEIGHT);
					}
					if (!YFCObject.isVoid(sLastSCM)) {

						if (aContainerScm.contains(sLastSCM) || 
								!(sLastContainerNo.equalsIgnoreCase(sContainerNo)&& 
										sLastSCM.equalsIgnoreCase(sContainerScm))) {
							XMLUtil.removeChild(outContainersElem, eleContainer);
							intCtnCount=intCtnCount -1;

						} else {
							aContainerScm.add(sLastSCM);
						}

					}

				}
				double dContainerNetWeight = Double.parseDouble(sContainerNetWeight);
				boolean isPOBox = true;
				Element EleShipmentLines = (Element) outputDoc.getDocumentElement().getElementsByTagName(KohlsConstant.E_SHIPMENT_LINES).item(0);
				NodeList nlShipmentLinesOrderLine = EleShipmentLines.getElementsByTagName(KohlsConstant.E_ORDER_LINE);
				if(!(nlShipmentLinesOrderLine.getLength()>0))
				{
					isPOBox= false;
				}
				for(int z=0;z<nlShipmentLinesOrderLine.getLength();z++)
				{
					Element PersonInfoShipToEle = (Element) ((Element) nlShipmentLinesOrderLine.item(z)).getElementsByTagName(KohlsXMLLiterals.E_PERSON_INFO_SHIP_TO).item(0);
					if(YFCObject.isVoid(PersonInfoShipToEle))
					{
						isPOBox= false;	
					}else
					{

						Element PersonInfoExtnEle = (Element) PersonInfoShipToEle.getElementsByTagName(KohlsConstant.E_EXTN).item(0);
						if(YFCObject.isVoid(PersonInfoExtnEle))
						{
							isPOBox= false;	
						}else
						{
							String strExtnIsPOBox = PersonInfoExtnEle.getAttribute(KohlsConstant.A_EXTN_IS_PO_BOX);
							if(YFCObject.isVoid(strExtnIsPOBox))
							{
								isPOBox= false;	
							}else if(!strExtnIsPOBox.equalsIgnoreCase(KohlsConstant.V_Y))
							{
								isPOBox= false;	
							}
						}
					}
				}

				if(!isPOBox)
				{
					outputDoc.getDocumentElement().setAttribute("StampServiceType", "N");
				}else{
					outputDoc.getDocumentElement().setAttribute("StampServiceType", "Y");
					if(dContainerNetWeight > 1.00)
					{
						outputDoc.getDocumentElement().setAttribute(KohlsConstant.A_SERVICE_TYPE, KohlsConstant.V_SERVICE_TYPE_USG);
					}else
					{
						outputDoc.getDocumentElement().setAttribute(KohlsConstant.A_SERVICE_TYPE, KohlsConstant.V_SERVICE_TYPE_USL);
					}
				}
			}
		}
		//System.out.println("here we come2 " +sContainerScm + " " + XMLUtil.getXMLString(outputDoc) );
		env.setTxnObject(KohlsConstant.O_PRINT_PACK_SLIP, "false");
		env.setTxnObject(KohlsConstant.O_ADD_CONTAINER_OUTPUT, outputDoc);
	
		log.debug("KOHLSAddContainerToShipmentAPI: addToContainer: End");
		
		return outputDoc;

	}

	private Document callPackShipmentOrUnPackShipment(YFSEnvironment env,
			Document inputDoc, boolean bRemoveContainer) throws Exception {
		
		log.debug("KOHLSAddContainerToShipmentAPI: callPackShipmentOrUnPackShipment: Begin");
		
		Document outDoc = null;
		if (bRemoveContainer) {
			env.setApiTemplate(KohlsConstant.API_UN_PACK_SHIPMENT,
					KohlsConstant.API_UNPACK_SHIPMENT_TEMPLATE_PATH);
			outDoc = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_UN_PACK_SHIPMENT, inputDoc);
			env.clearApiTemplate(KohlsConstant.API_UN_PACK_SHIPMENT);

		} else {
			env.setApiTemplate(KohlsConstant.API_PACK_SHIPMENT,
					KohlsConstant.API_PACK_SHIPMENT_TEMPLATE_PATH);
			outDoc = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_PACK_SHIPMENT, inputDoc);
			env.clearApiTemplate(KohlsConstant.API_PACK_SHIPMENT);
		}

		log.debug("KOHLSAddContainerToShipmentAPI: callPackShipmentOrUnPackShipment: End");
		
		// TODO Auto-generated method stub
		return outDoc;
	}
	
	private void fetchShipmentContainerDetailsForProShip(YFSEnvironment env, Document outputDoc, String containerScm) throws Exception {
		// TODO Auto-generated method stub
		String strShipmentContainerKey = "";
		Element eleOutputXml = outputDoc.getDocumentElement();
		String strEnterpriseCode = eleOutputXml.getAttribute(KohlsConstant.A_ENTERPRISE_CODE);
		Element eleContainers = XMLUtil.getChildElement(eleOutputXml, KohlsConstant.E_CONTAINERS);
		if(!XMLUtil.isVoid(eleContainers)){
			Iterator itContainer = XMLUtil.getChildren(eleContainers);
			while(itContainer.hasNext()){
				Element eleContainer = (Element)itContainer.next();
				String strContainerNo = eleContainer.getAttribute(KohlsConstant.A_CONTAINER_NO);
				if(containerScm.equals(strContainerNo)){
					strShipmentContainerKey = eleContainer.getAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY);
				}
			}
		}
		
		if(!"".equals(strShipmentContainerKey)){
			
			Document inDocForFetchContainerDetails = XMLUtil.createDocument(KohlsConstant.E_CONTAINER);
			Element eleInput = inDocForFetchContainerDetails.getDocumentElement();
			eleInput.setAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY, strShipmentContainerKey);
			
			env.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_CONTAINER_DETAILS,
					KohlsConstant.API_GET_SHIPMENT_CONTAINER_DETAILS_FOR_PROSHIP_TEMPLATE_PATH);
			Document outShipmentContainerDetails = KOHLSBaseApi.invokeAPI(env,
					KohlsConstant.API_GET_SHIPMENT_CONTAINER_DETAILS, inDocForFetchContainerDetails);
			env.clearApiTemplate(KohlsConstant.API_GET_SHIPMENT_CONTAINER_DETAILS);
			
			String strPrintFormat = fetchPrintFormat(env, strEnterpriseCode);
			Element eleContainerDetails = outShipmentContainerDetails.getDocumentElement();
			Element eleExtnContainer = XMLUtil.getChildElement(eleContainerDetails, KohlsConstant.E_EXTN);
			if(XMLUtil.isVoid(eleExtnContainer)){
				eleExtnContainer = XMLUtil.createChild(eleContainerDetails, KohlsConstant.E_EXTN);
				eleExtnContainer.setAttribute(KohlsConstant.A_EXTN_LBL_PRINT_FORMAT, strPrintFormat);
			}
			
			callProShipOnCloseWebService(env, outShipmentContainerDetails);
		}
		
	}

	private void callProShipOnCloseWebService(YFSEnvironment env, Document inDocForProShip) throws Exception{
		// TODO Auto-generated method stub
		Document outFromProShip = KOHLSBaseApi.invokeService(env,
				KohlsConstant.API_SHIP_SYNC_WEB_SERVICE, inDocForProShip);
		
		Element eleOutputFromProShip = outFromProShip.getDocumentElement();
		
		Document udpateShipmentDoc = getInputForUpdateShipment(eleOutputFromProShip);
		
		/*KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_CHANGE_SHIPMENT, udpateShipmentDoc);*/
		
		KOHLSBaseApi.invokeService(env,
				KohlsConstant.API_LABEL_PRINT_SERVICE, outFromProShip);
	}

	private Document getInputForUpdateShipment(Element eleOutputFromProShip) throws Exception{

		String strShipmentKey = eleOutputFromProShip.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
		String strCarrierServiceCode = eleOutputFromProShip.getAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE);
		String strSCAC = eleOutputFromProShip.getAttribute(KohlsConstant.A_SCAC);
		String strShipmentContainerKey = "";
		String strBasicFreightCharge = "";
		String strTrackingNo = "";
		String strExtnVoidLabelId = "";
		
		Element eleContainers = XMLUtil.getChildElement(eleOutputFromProShip, KohlsConstant.E_CONTAINERS);
		if(!XMLUtil.isVoid(eleContainers)){
			Element eleContainer  = XMLUtil.getChildElement(eleContainers, KohlsConstant.E_CONTAINER);
			strShipmentContainerKey = eleContainer.getAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY);
			strBasicFreightCharge = eleContainer.getAttribute(KohlsConstant.A_BASIC_FREIGHT_CHARGE);
			strTrackingNo = eleContainer.getAttribute(KohlsConstant.A_TRACKING_NO);
			
			Element eleExtn = XMLUtil.getChildElement(eleContainer, KohlsConstant.E_EXTN);
			strExtnVoidLabelId = eleExtn.getAttribute(KohlsConstant.A_VOID_LABEL_ID);
		}
		
		Document inDocForUpdate = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleInputXml = inDocForUpdate.getDocumentElement();
		eleInputXml.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
		eleInputXml.setAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE, strCarrierServiceCode);
		eleInputXml.setAttribute(KohlsConstant.A_SCAC, strSCAC);
		
		Element eleInpContainers = XMLUtil.createChild(eleInputXml, KohlsConstant.E_CONTAINERS);
		Element eleInpContainer = XMLUtil.createChild(eleInpContainers, KohlsConstant.E_CONTAINER);
		eleInpContainer.setAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY, strShipmentContainerKey);
		eleInpContainer.setAttribute(KohlsConstant.A_BASIC_FREIGHT_CHARGE, strBasicFreightCharge);
		eleInpContainer.setAttribute(KohlsConstant.A_TRACKING_NO, strTrackingNo);
		
		Element eleInpExtn = XMLUtil.createChild(eleInpContainer, KohlsConstant.E_EXTN);
		eleInpExtn.setAttribute(KohlsConstant.A_VOID_LABEL_ID, strExtnVoidLabelId);
		
		return inDocForUpdate;
	}

	private String fetchPrintFormat(YFSEnvironment env, String strEnterpriseCode) throws Exception{

		String strPrintFormat = "";
		
		Document inDocForCommonCode = XMLUtil.createDocument(KohlsConstant.E_COMMON_CODE);
		Element eleInput = inDocForCommonCode.getDocumentElement();
		eleInput.setAttribute(KohlsConstant.A_ENTERPRISE_CODE, strEnterpriseCode);
		eleInput.setAttribute(KohlsConstant.A_CODE_TYPE, KohlsConstant.A_EXTN_PRINT_FORMAT);
		
		Document outCommonCodeDetails = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_GET_COMMON_CODE_LIST, inDocForCommonCode);
		Element eleOutput = outCommonCodeDetails.getDocumentElement();
		Element eleCommonCode = XMLUtil.getChildElement(eleOutput, KohlsConstant.E_COMMON_CODE);
		if(!XMLUtil.isVoid(eleCommonCode)){
			strPrintFormat = eleCommonCode.getAttribute(KohlsConstant.A_CODE_LONG_DESC);
		}
		
		return strPrintFormat;
	}
	
	public Document unPackContainer(YFSEnvironment env, Document inputDoc) throws Exception{
		String sContainerScm = "";
		Element eleShipment = inputDoc.getDocumentElement();
		Element eleContainers = XMLUtil.getChildElement(eleShipment, KohlsConstant.E_CONTAINERS);
		Element containerElem = (Element) XMLUtil.getChildren(eleContainers).next();
		String strShipmentContainerKey = containerElem.getAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY);
		
		env.setTxnObject(KohlsConstant.O_CONTAINER_ELEM, containerElem);
		sContainerScm = containerElem.getAttribute(KohlsConstant.A_CONTAINERSCM);
		env.setTxnObject(KohlsConstant.A_CONTAINERSCM, sContainerScm);

		env.setTxnObject(KohlsConstant.A_ACTION_TAKEN, KohlsConstant.V_ACTION_TAKEN_UNPACK);

		Document inDocToUnpack = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleUnpackElement = inDocToUnpack.getDocumentElement();
		eleUnpackElement.setAttribute(KohlsConstant.A_SHIPMENT_KEY, eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		Element eleContainersToUnpack = XMLUtil.createChild(eleUnpackElement, KohlsConstant.E_CONTAINERS);
		Element eleContainerToUnpack = XMLUtil.createChild(eleContainersToUnpack, KohlsConstant.E_CONTAINER);
		eleContainerToUnpack.setAttribute(KohlsConstant.A_SHIPMENT_CONTAINER_KEY, strShipmentContainerKey);
		
		Document outUnpackContainer = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_UN_PACK_SHIPMENT, inDocToUnpack);
		return outUnpackContainer;
	}

}