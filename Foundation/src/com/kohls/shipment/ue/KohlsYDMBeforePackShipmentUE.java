package com.kohls.shipment.ue;
import java.text.SimpleDateFormat;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsDateUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.ydm.japi.ue.YDMBeforePackShipment;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class KohlsYDMBeforePackShipmentUE implements YDMBeforePackShipment{
	
	
	private static final YFCLogCategory log = YFCLogCategory
	.instance(KohlsYDMBeforePackShipmentUE.class.getName());
	
	boolean collteflag=false;

	
	public Document beforePackShipment(YFSEnvironment env, Document inDoc) throws YFSUserExitException{
		
		log.debug("KohlsYDMBeforePackShipmentUE: beforePackShipment: Begin");
		log.debug("Input Document to BeforePackShipmentUE: "+XMLUtil.getXMLString(inDoc));
		
		Element eleInputShipment=inDoc.getDocumentElement();
		Element eleShipmentDocument = (Element)env.getTxnObject("ShipmentInfo");
		Element eleShipmentDetails = XMLUtil.getChildElement(eleShipmentDocument, KohlsConstant.E_SHIPMENT);
		
		
		
		Document outProShipResponse = null;
		try {
		//Demo Test Passed
			Document inpupToProShip = createInputForProShipRequest(env, eleInputShipment, eleShipmentDetails);
			log.debug("Input Document going to ProShip is :"+XMLUtil.getXMLString(inpupToProShip));
			
			Element eleInputShipmentContainer = (Element) XPathUtil.getNode(eleInputShipment, "/Shipment/Containers/Container");
			outProShipResponse = KOHLSBaseApi.invokeService(env, KohlsConstant.API_SHIP_SYNC_WEB_SERVICE, inpupToProShip);
			log.debug("Output Document from ProShip is :"+XMLUtil.getXMLString(outProShipResponse));
			// setting this object for calling Label Printer from client side code
			env.setTxnObject(KohlsConstant.PROSHIP_RESPONSE, outProShipResponse);
			
			Element eleOutputFromProShip = outProShipResponse.getDocumentElement();
			String strCarrierServiceCode = eleOutputFromProShip.getAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE);
			String strSCAC = eleOutputFromProShip.getAttribute(KohlsConstant.A_SCAC);
			
			//To Stamp the SCAC and CarrierServiceCode on the Shipment after ProShip Response - Start
			String strShipmentKey= eleInputShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
			if(!YFCCommon.isStringVoid(strShipmentKey)){
				if(log.isDebugEnabled()){
					log.debug("Calling changeShipmentAPI to stamp the SCAC and CarrierServiceCode on the Shipment");
				}
				Document inChangeShipmentDoc = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
				Element eChangeShipmentInput = inChangeShipmentDoc.getDocumentElement();
				eChangeShipmentInput.setAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE, strCarrierServiceCode);
				eChangeShipmentInput.setAttribute(KohlsConstant.A_SCAC, strSCAC);
				eChangeShipmentInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
				Document outDoc = KOHLSBaseApi.invokeAPI(env, "changeShipment", inChangeShipmentDoc);
			}
			//To Stamp the SCAC and CarrierServiceCode on the Shipment after ProShip Response - End commnets.
			
			Element eleContainerFromProShip = (Element) XPathUtil.getNode(eleOutputFromProShip, "/Shipment/Containers/Container");
			String strBasicFreightCharge = eleContainerFromProShip.getAttribute(KohlsConstant.A_BASIC_FREIGHT_CHARGE);
			String strTrackingNo = eleContainerFromProShip.getAttribute(KohlsConstant.A_TRACKING_NO);
			
			
			eleInputShipment.setAttribute(KohlsConstant.A_CARRIER_SERVICE_CODE, strCarrierServiceCode);
			eleInputShipment.setAttribute(KohlsConstant.A_SCAC, strSCAC);
			
			eleInputShipmentContainer.setAttribute(KohlsConstant.A_ACTUAL_FREIGHT_CHARGE, strBasicFreightCharge);
			eleInputShipmentContainer.setAttribute(KohlsConstant.A_TRACKING_NO, strTrackingNo);	
			eleInputShipmentContainer.setAttribute("ExternalReference1", strCarrierServiceCode);
			eleInputShipmentContainer.setAttribute(KohlsConstant.A_SCAC, strSCAC);	
			
			
			Element eleInputExtn=XMLUtil.createChild(eleInputShipmentContainer, KohlsConstant.E_EXTN);
			Element eleOutputExtn=XMLUtil.getChildElement(eleContainerFromProShip,KohlsConstant.E_EXTN);
			eleInputExtn.setAttribute(KohlsConstant.A_EXTN_VOID_LABEL_ID, eleOutputExtn.getAttribute(KohlsConstant.A_EXTN_VOID_LABEL_ID));
			eleInputExtn.setAttribute(KohlsConstant.A_EXTN_LBL_PRINT_FORMAT, eleOutputExtn.getAttribute(KohlsConstant.A_EXTN_LBL_PRINT_FORMAT));			
			log.debug("Return Doc to PackShipment is :"+XMLUtil.getXMLString(inDoc));
			
			return inDoc;
			
			
		} catch (Exception e) {
			YFSUserExitException yfse = new YFSUserExitException();
			yfse.setErrorCode(KohlsConstant.PROSHIP_ERROR);
			String strExceptionMessage = e.getMessage();
			log.debug("KohlsYDMBeforePackShipmentUE :: Exception Handling::: Exception Message is:::\t"+strExceptionMessage);
			String strUIErrorMessage = "";
			if(!YFCObject.isVoid(strExceptionMessage)){
				int intIndexOfErrorMessage = strExceptionMessage.indexOf(":");
				if(intIndexOfErrorMessage >= 0){
					strUIErrorMessage = strExceptionMessage.substring(intIndexOfErrorMessage+1);
				}
				log.debug("KohlsYDMBeforePackShipmentUE :: Exception Handling::: UI Error Message is:::\t"+strUIErrorMessage);
			}
			if(!"".equals(strUIErrorMessage)){
				yfse.setErrorDescription(strUIErrorMessage);
				log.debug("KohlsYDMBeforePackShipmentUE :: Exception Handling::: Setting UI Error Message to:::\t"+strUIErrorMessage);
			}
			else{
				yfse.setErrorDescription("Conatiner Can Not Be Closed.");
				log.debug("KohlsYDMBeforePackShipmentUE :: Exception Handling::: Setting UI Error Message to:::\t"+"Conatiner Can Not Be Closed.");
			}
			throw yfse;
			
			/*if(outProShipResponse!=null){
				Element eleOutputFromProShip = outProShipResponse.getDocumentElement();
				Element eleError = XMLUtil.getChildElement(eleOutputFromProShip,"Error");
				if(!YFCCommon.isVoid(eleError)){
					String errorDesc = eleError.getAttribute("ErrorDescription");
					String errorCode = eleError.getAttribute("ErrorCode");
					String errorRelatedMoreInfo = eleError.getAttribute("ErrorRelatedMoreInfo");
					String strUserMessage = "";
					if(!"".equals(errorRelatedMoreInfo)){
						int intIndexOfErrorMessage = errorRelatedMoreInfo.indexOf(":");
						if(intIndexOfErrorMessage >= 0){
							strUserMessage =  errorRelatedMoreInfo.substring(intIndexOfErrorMessage+1);
						}
					}
					if(!"".equals(strUserMessage)){
						yfse.setErrorDescription(strUserMessage);
					}
					else{
						yfse.setErrorDescription(errorRelatedMoreInfo);
					}					
				}
				else{
					yfse.setErrorDescription("Container can not be closed.");
				}				
			}
			else {
				yfse.setErrorDescription("Container can not be closed.");
			}
			throw yfse;*/
		}	
	}

	private Document createInputForProShipRequest(YFSEnvironment env, Element eleInputShipment, Element eleShipmentDetails) throws Exception{
		// TODO Auto-generated method stub
		
		Element eleInputContainers = XMLUtil.getChildElement(eleInputShipment, KohlsConstant.E_CONTAINERS);
		Element eleInputContainer = XMLUtil.getChildElement(eleInputContainers, KohlsConstant.E_CONTAINER);
		
		Element eleShipToAddress = XMLUtil.getChildElement(eleShipmentDetails, "ToAddress");
		Element eleShipNode = XMLUtil.getChildElement(eleInputShipment, KohlsConstant.E_SHIP_NODE);
		
		String strPrintFormat = "";
		//Start changes for SFS June Release
		
		try{
			//Start changes for SFS June Release
			String strcollateTxn = (String)env.getTxnObject("CollateTxn");
			 if(!strcollateTxn.isEmpty() && strcollateTxn.equalsIgnoreCase("Y") && strcollateTxn!=null)
			 {
				 collteflag=true;
			 }
			 //End Changes for SFS June Release
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//End changes for SFS June Release
		try {
			strPrintFormat = fetchPrintFormat(env, KohlsConstant.KOHLS_ENTERPRISE_CODE);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Document docInputToProship = null;
		try {
			docInputToProship = XMLUtil.createDocument(KohlsConstant.A_CONTAINER);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Element eleInputToProship = docInputToProship.getDocumentElement();
		
		String strActualWeight= eleInputContainer.getAttribute("ActualWeight");
		String strActualWeightUOM= eleInputContainer.getAttribute("ActualWeightUOM");
		String strContainerGrossWeight= eleInputContainer.getAttribute("ContainerGrossWeight");
		String strContainerGrossWeightUOM= eleInputContainer.getAttribute("ContainerGrossWeightUOM");
		String strContainerHeight= eleInputContainer.getAttribute("ContainerHeight");
		String strContainerHeightUOM= eleInputContainer.getAttribute("ContainerHeightUOM");
		String strContainerLength= eleInputContainer.getAttribute("ContainerLength");
		String strContainerLengthUOM= eleInputContainer.getAttribute("ContainerLengthUOM");
		String strContainerNo= eleInputContainer.getAttribute("ContainerNo");
		String strContainerType= eleInputContainer.getAttribute("ContainerType");
		String strContainerWidth= eleInputContainer.getAttribute("ContainerWidth");
		String strContainerWidthUOM= eleInputContainer.getAttribute("ContainerWidthUOM");
		
		String strCarrierServiceCode= eleShipmentDetails.getAttribute("CarrierServiceCode");
		String strDocumentType= eleShipmentDetails.getAttribute("DocumentType");
		String strEnterpriseCode= eleShipmentDetails.getAttribute("EnterpriseCode");
		String strRequestedCarrierServiceCode= eleShipmentDetails.getAttribute("RequestedCarrierServiceCode");
		String strSCAC= eleShipmentDetails.getAttribute("SCAC");
		String strShipmentKey= eleShipmentDetails.getAttribute("ShipmentKey");
		String strShipmentNo= eleShipmentDetails.getAttribute("ShipmentNo");
		
		String strShipNodeKey = eleShipNode.getAttribute("ShipnodeKey");
		String strShipnodeType = eleShipNode.getAttribute("ShipnodeType");
		String strNodeType = eleShipNode.getAttribute("NodeType");
		
		eleInputToProship.setAttribute("ActualWeight", strActualWeight);
		eleInputToProship.setAttribute("ActualWeightUOM", strActualWeightUOM);
		eleInputToProship.setAttribute("ContainerGrossWeight", strContainerGrossWeight);
		eleInputToProship.setAttribute("ContainerGrossWeightUOM", strContainerGrossWeightUOM);
		eleInputToProship.setAttribute("ContainerHeight", strContainerHeight);
		eleInputToProship.setAttribute("ContainerHeightUOM", strContainerHeightUOM);
		eleInputToProship.setAttribute("ContainerLength", strContainerLength);
		eleInputToProship.setAttribute("ContainerLengthUOM", strContainerLengthUOM);
		eleInputToProship.setAttribute("ContainerNo", strContainerNo);
		eleInputToProship.setAttribute("ContainerType", strContainerType);
		eleInputToProship.setAttribute("ContainerWidth", strContainerWidth);
		eleInputToProship.setAttribute("ContainerWidthUOM", strContainerWidthUOM);
		eleInputToProship.setAttribute("TrackingNo", "");
		
		Element eleInpExtn = XMLUtil.createChild(eleInputToProship, KohlsConstant.E_EXTN);
		eleInpExtn.setAttribute("ExtnLblPrintFormat", strPrintFormat);
		eleInpExtn.setAttribute("ExtnVoidLabelId", "");
		
		Element eleInpShipment = XMLUtil.createChild(eleInputToProship, KohlsConstant.E_SHIPMENT);
		eleInpShipment.setAttribute("CarrierServiceCode", strCarrierServiceCode);
		eleInpShipment.setAttribute("DocumentType", strDocumentType);
		eleInpShipment.setAttribute("EnterpriseCode", strEnterpriseCode);
		eleInpShipment.setAttribute("RequestedCarrierServiceCode", strRequestedCarrierServiceCode);
		eleInpShipment.setAttribute("SCAC", strSCAC);
		eleInpShipment.setAttribute("ShipmentKey", strShipmentKey);
		eleInpShipment.setAttribute("ShipmentNo", strShipmentNo);
		
		//June SFS change
		String strOrderDate = this.getOrderDate(eleShipmentDetails);
		String formatedOrderDate = this.getFormatedOrderDate(strOrderDate);
		eleInpShipment.setAttribute("OrderDate", formatedOrderDate);
		//End June SFS change
		
		Element eleInpShipNode = XMLUtil.createChild(eleInpShipment, KohlsConstant.E_SHIP_NODE);
		eleInpShipNode.setAttribute("ShipnodeKey", strShipNodeKey);
		eleInpShipNode.setAttribute("ShipnodeType", strShipnodeType);
		eleInpShipNode.setAttribute("NodeType", strNodeType);
		
		String strAddressLine1= eleShipToAddress.getAttribute("AddressLine1");
		String strAddressLine2= eleShipToAddress.getAttribute("AddressLine2");
		String strCity= eleShipToAddress.getAttribute("City");
		String strCountry= eleShipToAddress.getAttribute("Country");
		String strDayPhone= eleShipToAddress.getAttribute("DayPhone");
		String strEMailID= eleShipToAddress.getAttribute("EMailID");
		String strFirstName= eleShipToAddress.getAttribute("FirstName");
		String strIsCommercialAddress= eleShipToAddress.getAttribute("IsCommercialAddress");
		String strLastName= eleShipToAddress.getAttribute("LastName");
		String strState= eleShipToAddress.getAttribute("State");
		String strZipCode= eleShipToAddress.getAttribute("ZipCode");
		
		Element eleInpShipToAddress = XMLUtil.createChild(eleInpShipment, "ToAddress");
		eleInpShipToAddress.setAttribute("AddressLine1", strAddressLine1);
		eleInpShipToAddress.setAttribute("AddressLine2", strAddressLine2);
		eleInpShipToAddress.setAttribute("City", strCity);
		eleInpShipToAddress.setAttribute("Country",strCountry );
		eleInpShipToAddress.setAttribute("DayPhone",strDayPhone );
		eleInpShipToAddress.setAttribute("EMailID",strEMailID );
		eleInpShipToAddress.setAttribute("FirstName",strFirstName );
		eleInpShipToAddress.setAttribute("IsCommercialAddress", strIsCommercialAddress);
		eleInpShipToAddress.setAttribute("LastName", strLastName);
		eleInpShipToAddress.setAttribute("State", strState);
		eleInpShipToAddress.setAttribute("ZipCode", strZipCode);
		
		return docInputToProship;
		
	}

	private String fetchPrintFormat(YFSEnvironment env, String strEnterpriseCode) throws Exception{

		String strPrintFormat = "";
		
		Document inDocForCommonCode = XMLUtil.createDocument(KohlsConstant.E_COMMON_CODE);
		Element eleInput = inDocForCommonCode.getDocumentElement();
		//Start changes for SFS June Release
		if(collteflag)
		{
			eleInput.setAttribute(KohlsConstant.A_CODE_TYPE, "EXTN_PNG_PRT_FORMAT");
			log.debug("collate flag has been set");
			
		}
		else
		{
			eleInput.setAttribute(KohlsConstant.A_ENTERPRISE_CODE, strEnterpriseCode);
			eleInput.setAttribute(KohlsConstant.A_CODE_TYPE, KohlsConstant.A_EXTN_PRINT_FORMAT);
		}
		//End changes for SFS June Release
		
		Document outCommonCodeDetails = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_GET_COMMON_CODE_LIST, inDocForCommonCode);
		Element eleOutput = outCommonCodeDetails.getDocumentElement();
		Element eleCommonCode = XMLUtil.getChildElement(eleOutput, KohlsConstant.E_COMMON_CODE);
		if(!XMLUtil.isVoid(eleCommonCode)){
			strPrintFormat = eleCommonCode.getAttribute(KohlsConstant.A_CODE_LONG_DESC);
			log.debug("common code ProShip is :"+XMLUtil.getXMLString(outCommonCodeDetails));
		}
		
		return strPrintFormat;
	}

//Begin changes for SFS-June Release
	
	/**
	 * This method format the OrderDate to "yyyy/MM/dd HH:mm:ss" which is required by Proship.
	 * @param strorderdate
	 * @return
	 */
	private String getFormatedOrderDate(String strorderdate) throws Exception{
		
		//Start changes for SFS-June Release
		
		
		log.debug("strorderdateDDT="+strorderdate);
        	String strordformdate="";
       
	        if((strorderdate==null)) return strordformdate;
	       
	        if(!strorderdate.isEmpty()) {
	        
	                        SimpleDateFormat dateFormatt = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	                        strordformdate= dateFormatt.format(KohlsDateUtil.convertDate(strorderdate));
	                        
	                        
	                        
	                		log.debug("strordformdateddr"+strordformdate);

	        }
        
	        return strordformdate;
       
        
	}
	
	/**
	 * Fetch the OrderDate from shipment document at ..\ShipmentLine\Oder element...
	 * @param eleInputShipment
	 * @return order Date
	 * @throws Exception
	 */
	private String getOrderDate(Element eleInputShipment ) throws Exception{
		
		String ordDate="";
		
		Element orderElement = (Element) XPathUtil.getNode(eleInputShipment, "//Shipment/ShipmentLines/ShipmentLine/Order");
		
		if (orderElement!=null) {
			ordDate = orderElement.getAttribute("OrderDate");
			System.out.println("OrdDateFromShpLine1="+ordDate);
    		log.debug("OrdDateFromShpLine2="+ordDate);
		}
		
		return ordDate;
	}
//End changes for SFS-June Release

}