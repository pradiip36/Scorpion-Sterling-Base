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
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KOHLSRePrintFromBatchID extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(KOHLSRePrintFromBatchID.class.getName());

	Properties properties = null;

	private Properties props;

	/**
	 * @param properties
	 *            argument from configuration.
	 */
	public void setProperties(Properties props) {

		this.props = props;
	}

	public Document rePrintBatchID(YFSEnvironment env, Document inXML) throws Exception {

		Element inputElem = inXML.getDocumentElement();

		Document docOutput = XMLUtil.createDocument("ApiSuccess");
		
		
		Element eleDocOutput = docOutput.getDocumentElement();

//		docOutput=XMLUtil.getDocument("c:\\temp\\pickSlipPrintStub.xml");
		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("KOHLSRePrintFromBatchID: rePrintBatchID: Input XML: " + XMLUtil.getXMLString(inXML));
		}

		if (!YFCObject.isVoid(inXML)) {

			String strBatchId = inputElem.getAttribute(KohlsConstant.A_BATCH_ID);
			String strPrinterID = inputElem.getAttribute(KohlsConstant.A_PRINTER_ID);

			if (YFCObject.isVoid(strBatchId) || YFCObject.isVoid(strPrinterID)) {

				eleDocOutput.setAttribute("Result", "0");
				eleDocOutput.setAttribute("Description", "Mandatory Parameters are missing from the Input");

				YFSException ex = new YFSException();
				ex.setErrorCode("Mandatory Parameters are missing from the Input");
				ex.setErrorDescription("BatchID and PrinterID must be passed in the input");
				throw ex;

			} else {
				
				eleDocOutput.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
				eleDocOutput.setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
				
				String strCartNo="";

				if(!YFCObject.isVoid(inputElem.getAttribute(KohlsConstant.A_CART_NUMBER)))
				strCartNo = inputElem.getAttribute(KohlsConstant.A_CART_NUMBER);
				
				String strStartCartNo = inputElem.getAttribute("FromCartNo");
				String strEndCartNo = inputElem.getAttribute("ToCartNo");

					// Preparing input for getShipmentList API
					Document getShipmentListInputDoc = createInputForGetShipmentList(strBatchId, strCartNo);

					// Preparing the template doc
					Document getShipmentListTemplateDoc = getShipmentListTemplate();

					// Calling the getShipmentList API with the input prepared
					Document getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI(env, getShipmentListTemplateDoc,
							KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);

					Element elemGetShipmentListOut = getShipmentListOutputDoc.getDocumentElement();

					String strTotalNoOfRecords = elemGetShipmentListOut.getAttribute(KohlsConstant.ATTR_TOT_NO_RECORDS);

					if (!YFCObject.isVoid(strTotalNoOfRecords) && Integer.parseInt(strTotalNoOfRecords) > 0) {
						
						String strShipmentType = ((Element) elemGetShipmentListOut.getElementsByTagName("Shipment").item(0))
						.getAttribute(KohlsConstant.A_SHIPMENT_TYPE);

						if (inputElem.getAttribute("Option").equals("1") || inputElem.getAttribute("Option").equals("2")) {


							if (strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_REGULAR)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_PRIORITY)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_GIFT)
									|| strShipmentType.equals(KohlsConstant.V_MULTI_PRODUCT_FAMILY)) {

								docOutput=KOHLSBaseApi.invokeService(env, "KOHLSRePrintSinglesPackSlipFromBatch", inXML);
							}

							if (inputElem.getAttribute("Option").equals("2") || strShipmentType.equals(KohlsConstant.SHIP_TYPE_MULTI_REGULAR)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_MULTI_PRIORITY)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_MULTI_GIFT)) {

								docOutput=KOHLSBaseApi.invokeService(env, "KOHLSRePrintMultisPickSlipFromBatch", inXML);
								
							} 
						}

						else if (inputElem.getAttribute("Option").equals("3")) {
							
							if (strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_REGULAR)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_PRIORITY)
									|| strShipmentType.equals(KohlsConstant.SHIP_TYPE_SINGLE_GIFT)
									|| strShipmentType.equals(KohlsConstant.V_MULTI_PRODUCT_FAMILY)) {

								docOutput=KOHLSBaseApi.invokeService(env, "KOHLSRePrintSinglesPackSlipFromBatch", inXML);
							}
							else{

							NodeList nlShipment = elemGetShipmentListOut.getElementsByTagName(KohlsConstant.E_SHIPMENT);
							int iShipments = nlShipment.getLength();
							if (iShipments > 0) {

								int iActualStartCartNo = Integer.parseInt(((Element) ((Element) nlShipment.item(0)).getElementsByTagName(
										KohlsConstant.E_EXTN).item(0)).getAttribute(KohlsConstant.A_EXTN_CART_NUMBER));

								int iActualEndCartNo = Integer.parseInt(((Element) ((Element) nlShipment.item(iShipments - 1)).getElementsByTagName(
										KohlsConstant.E_EXTN).item(0)).getAttribute(KohlsConstant.A_EXTN_CART_NUMBER));

								int iStartCartNo = Integer.parseInt(strStartCartNo);
								int iEndCartNo = Integer.parseInt(strEndCartNo);

								if (iStartCartNo < iActualStartCartNo || iEndCartNo > iActualEndCartNo || iStartCartNo > iEndCartNo) {

									eleDocOutput.setAttribute("Result", "2");
									eleDocOutput.setAttribute("Description", "Multis Cart number range is invalid");
									return docOutput;
								}

								else {
									//Pawan - Document Merge in case of Options 3 for bathc print i.e. carts in a given range - Start
									Document docMergedOutput = XMLUtil.createDocument("MultiPickSlipList");
									for(int iLoop=iStartCartNo; iLoop <= iEndCartNo; iLoop++ ) {
							
										inXML.getDocumentElement().setAttribute(KohlsConstant.A_CART_NUMBER, String.valueOf(iLoop));
										
										if (YFCLogUtil.isDebugEnabled()) {

											log.debug("KOHLSRePrintFromBatchID: rePrintBatchID: Modified Input XML: " + XMLUtil.getXMLString(inXML));
										}
										docOutput=KOHLSBaseApi.invokeService(env, "KOHLSRePrintMultisPickSlipFromBatch", inXML);										
										XMLUtil.addDocument(docMergedOutput, docOutput, false);
					    			}									
									return docMergedOutput;
									//Pawan - Document Merge in case of Options 3 for bathc print i.e. carts in a given range - End
								}
							}
						}
						
						}
						
						eleDocOutput.setAttribute("Result", "1");
						eleDocOutput.setAttribute("Description", "Success");
						return docOutput;
					}

					else if (!YFCObject.isVoid(strTotalNoOfRecords) && strTotalNoOfRecords.equals("0")) {
						
						if(strCartNo.equalsIgnoreCase("")){

						eleDocOutput.setAttribute("Result", "0");
						eleDocOutput.setAttribute("Description", "No Shipments returned for the BatchID");
						return docOutput;
						}
						else{
							eleDocOutput.setAttribute("Result", "01");
							eleDocOutput.setAttribute("Description", "No Shipments returned for the BatchID and Cart Number");
							return docOutput;
						}
					}
				
			}
		}

		if (YFCLogUtil.isDebugEnabled()) {

			log.debug("KOHLSRePrintFromBatchID: rePrintBatchID: Output XML: " + XMLUtil.getXMLString(docOutput));
		}

		return docOutput;
	}

	private Document createInputForGetShipmentList(String strBatchId, String strCartNo) throws ParserConfigurationException {

		Document docShipmentListInput = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);

		Element ShipmentElem = docShipmentListInput.getDocumentElement();
		ShipmentElem.setAttribute(KohlsConstant.A_PROFILE_ID, strBatchId);
		
		Element eleorderby=docShipmentListInput.createElement(KohlsConstant.E_ORDER_BY);
		ShipmentElem.appendChild(eleorderby);
		Element eleattribute=docShipmentListInput.createElement(KohlsConstant.A_ATTRIBUTE);
		eleorderby.appendChild(eleattribute);
		eleattribute.setAttribute(KohlsConstant.A_NAME, KohlsConstant.A_SHIPMENT_NO);

		if (!YFCObject.isVoid(strCartNo) || !strCartNo.equals("")) {

			Element eleExtn = XMLUtil.createChild(ShipmentElem, KohlsConstant.E_EXTN);
			eleExtn.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, strCartNo);
		}

		return docShipmentListInput;
	}

	public Document getShipmentListTemplate() throws ParserConfigurationException {

		Document getShipmentListTemplateDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleShipmentList = getShipmentListTemplateDoc.getDocumentElement();
		eleShipmentList.setAttribute("TotalNumberOfRecords", "");
		Element eleShipment = XMLUtil.createChild(eleShipmentList, KohlsConstant.E_SHIPMENT);
		eleShipment.setAttribute(KohlsConstant.A_PROFILE_ID, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_TYPE, "");
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_KEY, "");
		
		
		Element eleExtn = XMLUtil.createChild(eleShipment, KohlsConstant.E_EXTN);
		eleExtn.setAttribute(KohlsConstant.A_EXTN_CART_NUMBER, "");

		return getShipmentListTemplateDoc;
	}

}
