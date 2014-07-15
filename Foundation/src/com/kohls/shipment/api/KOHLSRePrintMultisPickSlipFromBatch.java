package com.kohls.shipment.api;

import java.util.ArrayList;
import java.util.Collections;
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
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KOHLSRePrintMultisPickSlipFromBatch extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSRePrintMultisPickSlipFromBatch.class.getName());
	Properties properties = null;

	private Properties props;

	/**
	 * @param properties
	 *            argument from configuration.
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	public Document rePrintMultisBatch(YFSEnvironment env, Document inXML) throws Exception {
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### Input XML   ###############" + XMLUtil.getXMLString(inXML));
		}
		Document docOutput = XMLUtil.createDocument("ApiSuccess");
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
        	   String strCartNumber = inputElem.getAttribute(KohlsConstant.A_CART_NUMBER);
        	   
        	   //Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - Start
        	   //In case of option 3 - i.e. Carts with in a Range - looping us done in the KOHLSRePrintFromBatchID class.
        	   String strOption = XMLUtil.getAttribute(inputElem, "Option");
        	   Document docMergedOutput = XMLUtil.createDocument("MultiPickSlipList");
        	   boolean boolMerge = false;
        	   if(!"3".equals(strOption)){
        		   boolMerge = true;        		   
        	   }
        	   //Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - End
        	   
        	// Preparing input for getShipmentList API
        	   Document getShipmentListInputDoc = createInputForGetShipmentList(strBatchId);
        	// Preparing the template doc
       		Document getShipmentListTemplateDoc = getShipmentListTemplate();
       		if(!YFCObject.isVoid(strCartNumber))
       		{
       			Element eleGetShipmentListInput = getShipmentListInputDoc.getDocumentElement();
       			Element inputElemExtn = XMLUtil.getFirstElementByName(eleGetShipmentListInput, KohlsConstant.E_EXTN);
       			inputElemExtn.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, strCartNumber);
       		}
       		// Calling the getShipmentList API with the input prepared
    		Document getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI
    		(env, getShipmentListTemplateDoc, 
    				KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);
    		Element elemGetShipmentListOut = getShipmentListOutputDoc.getDocumentElement();
    		int iStartCartNo = 0;
    		int iEndCartNo = 0;
    		NodeList nlShipment = elemGetShipmentListOut.getElementsByTagName(KohlsConstant.E_SHIPMENT);
    		int iShipments = nlShipment.getLength();
    		if(iShipments >0)
    		{
    			ArrayList<Integer> alCartNumbers = new ArrayList<Integer>();
    			
    			for(int iIndex=0;iIndex<nlShipment.getLength();iIndex++){
    				int iCartNo=Integer.parseInt(((Element)((Element)nlShipment.item(iIndex)).
    						getElementsByTagName(KohlsConstant.E_EXTN).item(0)).getAttribute(KohlsConstant.A_EXTN_CART_NUMBER));
    				
    				if (!alCartNumbers.contains(iCartNo)){
    					alCartNumbers.add(iCartNo);
    				}
    			}
    			
    			iStartCartNo= Collections.min(alCartNumbers);
    			iEndCartNo = Collections.max(alCartNumbers);
    					
    					
    			
    			for( int j =iStartCartNo; j <= iEndCartNo;j++ )
    			{
    				String strEndCartNo = Integer.toString(iEndCartNo);
    				String tempCartNo = Integer.toString(j);
    				NodeList CartShipments  =  XPathUtil.getNodeList(
    						elemGetShipmentListOut,
    						"/Shipments/Shipment[Extn[@ExtnCartNumber='"
    								+ tempCartNo + "']]");
    				if(CartShipments.getLength()>0)
    				{
    					Document MultiPrintDocument = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
    					Element eleMultiPrint = MultiPrintDocument.getDocumentElement();
    					eleMultiPrint.setAttribute(KohlsConstant.A_ACTION_TAKEN, "Reprint");
    					
    					for(int i=0;i <CartShipments.getLength();i++)
    					{
    						Element ShipmentElem = (Element) CartShipments.item(i);
    						Element newShipmentElem = XMLUtil.createChild(eleMultiPrint, KohlsConstant.E_SHIPMENT);
    						newShipmentElem.setAttribute(KohlsConstant.A_SHIPMENT_KEY, ShipmentElem.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
    						
    					}
    					eleMultiPrint.setAttribute(KohlsConstant.A_CART_NUMBER, tempCartNo);
    					eleMultiPrint.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
    					eleMultiPrint.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
    					if(YFCObject.isVoid(strCartNumber))
    					{
    						eleMultiPrint.setAttribute(KohlsConstant.A_TOTAL_CARTS, strEndCartNo);
    					}
    					docOutput=KOHLSBaseApi.invokeService(env, 
    							KohlsConstant.SERVICE_KOHLS_CHANGE_STATUS_MULTI_PRINT ,MultiPrintDocument);
    					
    					//Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - Start
    	    			if(!XMLUtil.isVoid(docOutput) && boolMerge){
        	    			XMLUtil.addDocument(docMergedOutput, docOutput, false);        	    			
    	    			}
    	    			//Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - End
    				}
    			} 
    			//Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - Start
				if(boolMerge){
	    			return docMergedOutput;
	    		}
				//Pawan - To Merge the Print Docs for each cart if the Option 3 is not selected - End
    		}    		
           }
		}		
		return docOutput;

	}

	private Document createInputForGetShipmentList(String strBatchId) throws ParserConfigurationException  {
		Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element ShipmentElem = docShipmentListInput.getDocumentElement();
		ShipmentElem.setAttribute(KohlsConstant.A_PROFILE_ID, strBatchId);
		Element eleExtn = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_EXTN);
		eleExtn.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, "");
		Element eleOrderBy = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_ORDER_BY);
		Element eleExtn2 = XMLUtil.createChild(eleOrderBy, KohlsConstant.E_EXTN);
		Element eleAttribute = XMLUtil.createChild(eleExtn2, KohlsConstant.E_ATTRIBUTE);
		eleAttribute.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_EXTN_CART_NUMBER);
		eleAttribute.setAttribute(KohlsConstant.A_DESC, KohlsConstant.V_N);
		Element eleComplex = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_COMPLEX_QUERY);
		Element eleOr = XMLUtil.createChild(eleComplex, KohlsConstant.E_OR);
		Element eleExp1 = XMLUtil.createChild(eleOr,KohlsConstant.E_EXP);
		eleExp1.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp1.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_REGULAR);
		Element eleExp2 = XMLUtil.createChild(eleOr,KohlsConstant.E_EXP);
		eleExp2.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp2.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_PRIORITY);
		Element eleExp3 = XMLUtil.createChild(eleOr,KohlsConstant.E_EXP);
		eleExp3.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_TYPE);
		eleExp3.setAttribute(KohlsConstant.A_VALUE,KohlsConstant.SHIP_TYPE_MULTI_GIFT);
		
		
		// TODO Auto-generated method stub
		return docShipmentListInput;
	}
	
public Document getShipmentListTemplate() throws ParserConfigurationException {
		
		Document getShipmentListTemplateDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleShipmentList = getShipmentListTemplateDoc.getDocumentElement();
        Element eleShipment = XMLUtil.createChild(eleShipmentList, KohlsConstant.E_SHIPMENT);
		eleShipment.setAttribute(KohlsConstant.A_PROFILE_ID, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIP_NODE, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY, "");
	    Element eleExtn = XMLUtil.createChild(eleShipment, KohlsConstant.E_EXTN);
	    eleExtn.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, "");
		
		return getShipmentListTemplateDoc;
	}



}
