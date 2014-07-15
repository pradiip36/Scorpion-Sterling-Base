package com.kohls.shipment.api;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

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
import com.yantra.yfs.japi.YFSException;

public class KOHLSRePrintSinglePackSlipFromBatch extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSRePrintSinglePackSlipFromBatch.class.getName());
	Properties properties = null;

	private Properties props;

	/**
	 * @param properties
	 *            argument from configuration.
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	public void rePrintSinglesBatch(YFSEnvironment env, Document inXML) throws Exception {
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### KOHLSRePrintSinglePackSlipFromBatch:rePrintSinglesBatch Input XML   ###############" + XMLUtil.getXMLString(inXML));
		}
		if(!YFCObject.isVoid(inXML)) {
           Element inputElem = inXML.getDocumentElement();
           String strBatchId = inputElem.getAttribute(KohlsConstant.A_BATCH_ID);
           String strPrinterID = inputElem.getAttribute(KohlsConstant.A_PRINTER_ID);
           if(YFCObject.isVoid(strBatchId)|| YFCObject.isVoid(strPrinterID) )
           {
        	YFSException ex = new YFSException();
			ex.setErrorCode("Mandatory Parameters are missing from the Input");
			ex.setErrorDescription("BatchID and PrinterID must be passed in the input");
			throw ex;	
        	   
           }else
           {
        	  Document getShipmentListInputDoc = createInputForGetShipmentList(strBatchId);
        	// Preparing the template doc
       		Document getShipmentListTemplateDoc = getShipmentListTemplate();
       		
       		// Calling the getShipmentList API with the input prepared
    		Document getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI
    		(env, getShipmentListTemplateDoc, 
    				KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);
    		Element elemGetShipmentListOut = getShipmentListOutputDoc.getDocumentElement();
    		
    		
    		NodeList nlShipment = elemGetShipmentListOut.getElementsByTagName(KohlsConstant.E_SHIPMENT);
    		int iShipments = 0;
    		
    		if(!YFCObject.isVoid(nlShipment)){
    		
    		iShipments = nlShipment.getLength();
    		if(iShipments >0)
    		{
    			
    			for( int j =0; j < iShipments ;j++ )
    			{
    				
    				if(nlShipment.getLength()>0)
    				{
    					Document docSinglePrint = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
    					Element eleSinglePrint = docSinglePrint.getDocumentElement();
    				    Element newShipmentElem = (Element) nlShipment.item(j);
    				    eleSinglePrint.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
    				    eleSinglePrint.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
    				    eleSinglePrint.setAttribute(KohlsConstant.A_SHIPMENT_KEY, newShipmentElem.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
    				    eleSinglePrint.setAttribute(KohlsConstant.A_PRINT_JASPER_REPORT, KohlsConstant.V_Y);
    				    eleSinglePrint.setAttribute(KohlsConstant.A_LABEL_PICK_TICKET, KohlsConstant.V_N);
    					KOHLSBaseApi.invokeService(env, 
    							KohlsConstant.API_REPRINT_PICK_SLIP_SERVICE ,docSinglePrint);
    				}
    			}
    		}
    		
           }
    	
           }


		}

	}

	private Document createInputForGetShipmentList(String strBatchId) throws ParserConfigurationException  {
		Document getShipmentListInputDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleShipment = getShipmentListInputDoc.getDocumentElement();
		
		eleShipment.setAttribute(KohlsConstant.A_PROFILE_ID, strBatchId);
		
		Element eleComplex = getShipmentListInputDoc.createElement(KohlsConstant.E_COMPLEX_QUERY);
		Element eleOr = getShipmentListInputDoc.createElement(KohlsConstant.E_OR);
		
		Element eleExp1 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp1.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp1.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_SINGLE_REGULAR);
		
		Element eleExp2 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp2.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp2.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_SINGLE_PRIORITY);
		
		Element eleExp3 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp3.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp3.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_SINGLE_GIFT);
		
		Element eleExp4 = getShipmentListInputDoc.createElement(KohlsConstant.E_EXP);
		eleExp4.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp4.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.V_MULTI_PRODUCT_FAMILY);
		
		eleOr.appendChild(eleExp1);
		eleOr.appendChild(eleExp2);
		eleOr.appendChild(eleExp3);
		eleOr.appendChild(eleExp4);
		eleComplex.appendChild(eleOr);
		eleShipment.appendChild(eleComplex);
		
		return getShipmentListInputDoc;
	}
	
public Document getShipmentListTemplate() throws ParserConfigurationException {
		
		Document getShipmentListTemplateDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleShipmentList = getShipmentListTemplateDoc.getDocumentElement();
        Element eleShipment = XMLUtil.createChild(eleShipmentList, KohlsConstant.E_SHIPMENT);
		eleShipment.setAttribute(KohlsConstant.A_PROFILE_ID, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIP_NODE, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY, "");
	   
		
		return getShipmentListTemplateDoc;
	}
	

}
