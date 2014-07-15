package com.kohls.shipment.api;

import java.util.ArrayList;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSStoreRePrintPickSlipPrintAPI extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSStoreRePrintPickSlipPrintAPI.class.getName());

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
	public Document getRePrintPickSlipDetails(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception
			{
		Document outDoc = null;
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSStoreRePrintPickSlipPrintAPI" +
					"getRePrintPickSlipDetails method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}

		if(!YFCObject.isVoid(inputDoc))
		{
			Element eleInput = inputDoc.getDocumentElement();
			String strPrinterID = eleInput.getAttribute(KohlsConstant.A_PRINTER_ID);

			String strShipmentKey = eleInput.getAttribute(KohlsConstant.A_SHIPMENT_KEY);

			//creating input document for getShipmentDetails API
			Document docShipmentDetailsInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
			Element eleshipmentDetailsInput = docShipmentDetailsInput.getDocumentElement();
			String sLabelPickTicket = eleInput.getAttribute(KohlsConstant.A_LABEL_PICK_TICKET);
			String sUpcBarcodePickTicket = eleInput.getAttribute(KohlsConstant.A_UPC_BARCODE_PICK_TICKET);
			String sPrintJasperReport = eleInput.getAttribute(KohlsConstant.A_PRINT_JASPER_REPORT);
			String sPrintBatchID = eleInput.getAttribute(KohlsConstant.A_BATCH_ID);
			
			eleshipmentDetailsInput.setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);

			if (YFCLogUtil.isDebugEnabled()) 
			{
				this.log.debug("Input xml for getShipmentDetails API:"
						+ XMLUtil.getXMLString(docShipmentDetailsInput));
			}

			yfsEnvironment.setApiTemplate(KohlsConstant.API_GET_SHIPMENT_DETAILS,
					KohlsConstant.API_GET_SHIPMENT_DETAILS_RE_PRINT_SINGLE_PICK_SHIPMENT_TEMPLATE);

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


			Element eleShipmentDetailsOutput = docShipmentDetailsOutput.getDocumentElement();
			if(!YFCObject.isVoid(strPrinterID))
			{
				eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
			}

			if(!YFCObject.isVoid(sLabelPickTicket))
			{
				if(sLabelPickTicket.equalsIgnoreCase(KohlsConstant.V_Y))
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET, KohlsConstant.V_Y);
				}else
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET, KohlsConstant.V_N);
				}
			}else
			{
				eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET, KohlsConstant.V_N);
			}
			
			if(!YFCObject.isVoid(sUpcBarcodePickTicket))
			{
				if(sUpcBarcodePickTicket.equalsIgnoreCase(KohlsConstant.V_Y))
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_UPC_BARCODE_PICK_TICKET, KohlsConstant.V_Y);
				}else
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_UPC_BARCODE_PICK_TICKET, KohlsConstant.V_N);
				}
			}else
			{
				eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_UPC_BARCODE_PICK_TICKET, KohlsConstant.V_N);
			}
			
			if(!YFCObject.isVoid(sPrintJasperReport))
			{
				if(sPrintJasperReport.equalsIgnoreCase(KohlsConstant.V_Y))
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT, KohlsConstant.V_Y);
				}else
				{
					eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT, KohlsConstant.V_N);
				}
			}else
			{
				eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT, KohlsConstant.V_N);
			}
			
			if(!YFCObject.isVoid(sPrintBatchID))
			{
				eleShipmentDetailsOutput.setAttribute(KohlsConstant.A_BATCH_ID, sPrintBatchID);
			}
			outDoc = docShipmentDetailsOutput;
		
			Element eleOutDoc = outDoc.getDocumentElement();
			
			log.debug("KOHLSStoreRePrintPickSlipPrintAPI : docShipmentDetailsOutput" + XMLUtil.getXMLString(outDoc));
			
			Document cloneOutDoc = KOHLSBaseApi.cloneDocument(outDoc);
			
			yfsEnvironment.setTxnObject("docShipmentDetailsOutput", docShipmentDetailsOutput);
			
			String strXPath = "/Shipment/ShipmentLines/ShipmentLine";
			
			//changing to new service to get to force the jasper print  
			if(eleOutDoc.getAttribute(KohlsConstant.A_PRINT_JASPER_REPORT).equals(KohlsConstant.V_N)){
				
				//changing to new service to get to send the report data back 
//				outDoc = KOHLSBaseApi.invokeService(yfsEnvironment,
//						KohlsConstant.SERVICE_RE_PRINT_SINGLE_PICK_SHIPMENT, docShipmentDetailsOutput);
				
				outDoc = KOHLSBaseApi.invokeService(yfsEnvironment,
						KohlsConstant.KOHLS_SINGLE_BATCH_PRINT_SERVICE, docShipmentDetailsOutput);
			}
			
			else {
				
				cloneOutDoc = KOHLSGiftReceiptID.hidePriceInfo(cloneOutDoc, strXPath);
				
				log.debug("KOHLSStoreRePrintPickSlipPrintAPI : hidePriceInfo(cloneOutDoc, strXPath)" + XMLUtil.getXMLString(cloneOutDoc));
				
				KOHLSGiftReceiptID.printMassSingles(yfsEnvironment, cloneOutDoc);
				
				ArrayList<String> alShipmentLineProcessed = new ArrayList<String>();
				ArrayList<String> alShipmentLinePrinted = new ArrayList<String>();
				
				yfsEnvironment.setTxnObject("alShipmentLineProcessed", alShipmentLineProcessed);
				yfsEnvironment.setTxnObject("alShipmentLinePrinted", alShipmentLinePrinted);	
				
				KOHLSGiftReceiptID.printRePackSlipForMassSinglesGiftLines(yfsEnvironment, cloneOutDoc, null);

				outDoc = null;
			}
			
			if(!YFCObject.isVoid(outDoc) && eleOutDoc.getAttribute(KohlsConstant.A_PRINT_JASPER_REPORT).equals(KohlsConstant.V_N))
 			{	
				cloneOutDoc = KOHLSBaseApi.cloneDocument(outDoc);
				
				Document PrintRePackDocs = XMLUtil.createDocument("PrintRePackDocs");
								
				cloneOutDoc = KOHLSGiftReceiptID.hidePriceInfo(cloneOutDoc, strXPath);
				
				log.debug("KOHLSStoreRePrintPickSlipPrintAPI : hidePriceInfo(cloneOutDoc, strXPath)" + XMLUtil.getXMLString(cloneOutDoc));
				
				KOHLSGiftReceiptID.appendPrintRePackElement(yfsEnvironment, cloneOutDoc, PrintRePackDocs, true);						
				
				ArrayList<String> alShipmentLineProcessed = new ArrayList<String>();
				ArrayList<String> alShipmentLinePrinted = new ArrayList<String>();
				
				yfsEnvironment.setTxnObject("alShipmentLineProcessed", alShipmentLineProcessed);
				yfsEnvironment.setTxnObject("alShipmentLinePrinted", alShipmentLinePrinted);	
				
				KOHLSGiftReceiptID.printRePackSlipForGiftLines(yfsEnvironment, cloneOutDoc, PrintRePackDocs);
				
				cloneOutDoc = PrintRePackDocs;
				
				log.debug("KOHLSStoreRePrintPickSlipPrintAPI : cloneOutDoc" + XMLUtil.getXMLString(cloneOutDoc));
				
				outDoc = cloneOutDoc;
 			}
			// commenting the below lines as per new requirement to send the data back to SIM client
             if(eleShipmentDetailsOutput.getAttribute(KohlsConstant.A_STATUS).equalsIgnoreCase(KohlsConstant.V_AWAITING_PICKLIST_PRINT))
            	 
            	 
             {
            	 //commenting the changeShipment call
            	 
            	 if(!YFCObject.isVoid(sPrintBatchID))
     			{
            		 callChangeShipment(eleInput,yfsEnvironment);
     			}
            	
            	 
            	 /////////////////////////////////////////
            	String sChangedStatus = KohlsConstant.V_SHIPMENT_PICK_LIST_PRINTED_STATUS;
 				String sTransactionId = KohlsConstant.A_PICK_LIST_PRINT_TRANSACTION_ID;
 				Document inputDocForChangeShipmentStatus = XMLUtil
 				.createDocument(KohlsConstant.E_SHIPMENT);
 				inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
 						KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);
 				inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
 						"TransactionId", sTransactionId);
 				inputDocForChangeShipmentStatus.getDocumentElement().setAttribute(
 						"BaseDropStatus", sChangedStatus);
 				yfsEnvironment.setApiTemplate(KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
 						KohlsConstant.API_CHANGE_SHIPMENT_STATUS_RE_PRINT_SINGLE_PICK_SHIPMENT_TEMPLATE);
 				Document outDocChangeShipmentStatus = KOHLSBaseApi.invokeAPI(yfsEnvironment,
 						KohlsConstant.API_CHANGE_SHIPMENT_STATUS,
 						inputDocForChangeShipmentStatus);
 				yfsEnvironment.clearApiTemplate(KohlsConstant.API_CHANGE_SHIPMENT_STATUS);
 				Element eleChangeShipmentStatus = outDocChangeShipmentStatus.getDocumentElement();
 				String sNewStatus = eleChangeShipmentStatus.getAttribute(KohlsConstant.A_STATUS);
 				Element eleNewStatus = (Element) eleChangeShipmentStatus.getElementsByTagName(KohlsConstant.A_STATUS).item(0);
 				String sNewStatusDesc = eleNewStatus.getAttribute(KohlsConstant.A_DESCRIPTION);
 				if(!YFCObject.isVoid(cloneOutDoc))
 				{
 				Element eleOutElem = cloneOutDoc.getDocumentElement();
 				eleOutElem.setAttribute(KohlsConstant.A_STATUS, sNewStatus);
 				
 				Element eleOldStatus = cloneOutDoc.createElement(KohlsConstant.A_STATUS);
 				
 				eleOldStatus.setAttribute(KohlsConstant.A_STATUS, sNewStatus);
 				eleOldStatus.setAttribute(KohlsConstant.A_DESCRIPTION, sNewStatusDesc);
 				eleOutElem.appendChild(eleOldStatus);
 				}
 				
             }
             
             
             
            	 
		}
		
		if(!YFCObject.isVoid(outDoc)) {
			
			log.debug("outDoc: "+XMLUtil.getXMLString(outDoc));
		}
		
		return outDoc;
			}

	private void callChangeShipment(Element inEle, YFSEnvironment yfsEnvironment) throws Exception {
		Document inputDocForChangeShipment = XMLUtil
			.createDocument(KohlsConstant.E_SHIPMENT);
		inputDocForChangeShipment.getDocumentElement().setAttribute(
					KohlsConstant.A_SHIPMENT_KEY, inEle.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		inputDocForChangeShipment.getDocumentElement().setAttribute(
					KohlsConstant.A_PROFILE_ID, inEle.getAttribute(KohlsConstant.A_BATCH_ID));
		
		
			Document outDocChangeShipmentStatus = KOHLSBaseApi.invokeAPI(yfsEnvironment,
					KohlsConstant.API_CHANGE_SHIPMENT,
					inputDocForChangeShipment);	
		
	}
}
