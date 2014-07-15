package com.kohls.shipment.api;

import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class prepares an XML from the given input in a format needed by SingleMassPrinting services in SIM screen.
 * Input has value of the Shipnode attribute. With this, getShipmentList API is called to get list of Shipments
 * which are in status "1100.025" - AwaitingPickListPrinted with ShipmentType as Single_Regular/Single_Priority/Single_Gift.
 * With the output of getShipmentList, the no.of shipments Available in the given status is read and the Output of 
 * the service is prepared.    
 * 
 * @author Saravana
 *
 */
public class KOHLSGetMassPrintSinglesList {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSGetMassPrintSinglesList.class.getName());

	private HashMap<String, String> hmShipments = new HashMap<String, String> ();

	/**
	 * This method is the Wrapper for the class.
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */

	public Document getMassPrintSinglesList(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- Begining of KOHLSMassPrintSingles" +
					" getMassPrintSinglesList method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}

		Element eleShipment = inputDoc.getDocumentElement();
		String strShipNode = eleShipment.getAttribute(KohlsConstant.ATTR_SHIP_NODE);

		// Preparing input for getShipmentList API
		Document getShipmentListInputDoc = getShipmentListInput(strShipNode);

		// Preparing the template doc
		Document getShipmentListTemplateDoc = getShipmentListTemplate();

		// Calling the getShipmentList API with the input prepared
		Document getShipmentListOutputDoc = KOHLSBaseApi.invokeAPI
		(yfsEnvironment, getShipmentListTemplateDoc, 
				KohlsConstant.API_actual_GET_SHIPMENT_LIST, getShipmentListInputDoc);

		// Calling the method to populate a HashMap based on the values in the getShipmentList output XML
		populateHashMap(getShipmentListOutputDoc);

		Document outputDoc = prepareOutputDoc();

		if (YFCLogUtil.isDebugEnabled()) {

			this.log.debug("<!-- End of KOHLSMassPrintSingles" +
					" getMassPrintSinglesList method -- >"
					+ XMLUtil.getXMLString(outputDoc));
		}

		return outputDoc;
	}


	/**
	 * This method prepares input for getShipmentList API.
	 * @param strShipNode
	 * 
	 * @throws ParserConfigurationException 
	 */
	public Document getShipmentListInput(String strShipNode) throws ParserConfigurationException{

		Document getShipmentListInputDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
		Element eleShipment = getShipmentListInputDoc.getDocumentElement();

		eleShipment.setAttribute(KohlsConstant.ATTR_SHIP_NODE, strShipNode);
		eleShipment.setAttribute(KohlsConstant.ATTR_STATUS, KohlsConstant.V_AWAITING_PICKLIST_PRINT);

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


	/**
	 * This method prepares input for getShipmentList API.
	 * @throws ParserConfigurationException 
	 * 
	 * @throws ParserConfigurationException 
	 */
	public Document getShipmentListTemplate() throws ParserConfigurationException {

		Document getShipmentListTemplateDoc = XMLUtil.createDocument(KohlsConstant.E_SHIPMENTS);
		Element eleShipmentList = getShipmentListTemplateDoc.getDocumentElement();

		Element eleShipment = getShipmentListTemplateDoc.createElement(KohlsConstant.E_SHIPMENT);
		eleShipment.setAttribute(KohlsConstant.A_SHIPMENT_TYPE, "");
		Element eleExtn = getShipmentListTemplateDoc.createElement(KohlsConstant.E_EXTN);
		Element eleShipmentLines = getShipmentListTemplateDoc.createElement(KohlsConstant.E_SHIPMENT_LINES);
		Element eleShipmentLine = getShipmentListTemplateDoc.createElement(KohlsConstant.E_SHIPMENT_LINE);

		eleShipmentLine.setAttribute(KohlsConstant.A_ITEM_ID, "");
		eleShipment.appendChild(eleExtn);
		eleShipmentLines.appendChild(eleShipmentLine);
		eleShipment.appendChild(eleShipmentLines);
		eleShipmentList.appendChild(eleShipment);

		return getShipmentListTemplateDoc;
	}



	/**
	 * This method popualtes the Hashmap from the getShipmentList API output.
	 * @param getShipmentListOutputDoc
	 * 
	 * @throws ParserConfigurationException 
	 */
	public void populateHashMap(Document getShipmentListOutputDoc) throws ParserConfigurationException {

		Element eleShipmentList = getShipmentListOutputDoc.getDocumentElement();
		NodeList nlShipment = eleShipmentList.getElementsByTagName(KohlsConstant.E_SHIPMENT);

		for(int iLoop=0; iLoop<nlShipment.getLength(); iLoop++){

			Element eleShipment = (Element) nlShipment.item(iLoop);
			String strShipmentType = eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_TYPE);
			if(strShipmentType.equalsIgnoreCase(KohlsConstant.V_MULTI_PRODUCT_FAMILY))
			{
				String strProductFamily = ((Element)eleShipment.getElementsByTagName(KohlsConstant.E_EXTN).item(0)).
				getAttribute(KohlsConstant.A_EXTN_SHIPMENT_FAMILY);
				if(!YFCObject.isVoid(strProductFamily))
				{
					String strHMKey = strProductFamily+KohlsConstant.DELIMITER_SHIP_HASHMAP+strShipmentType + KohlsConstant.DELIMITER_SHIP_HASHMAP +"Y";
					if (hmShipments.containsKey(strHMKey)){

						int iValue = Integer.parseInt(hmShipments.get(strHMKey));					
						hmShipments.put(strHMKey, ""+(++iValue));
					}

					else if (!hmShipments.containsKey(strHMKey)){

						hmShipments.put(strHMKey, KohlsConstant.STRING_ONE);
					}

				}
			}else{
				NodeList nlShipmentLine = eleShipment.getElementsByTagName(KohlsConstant.E_SHIPMENT_LINE);

				for(int iInnerLoop=0; iInnerLoop<nlShipmentLine.getLength(); iInnerLoop++){

					Element eleShipmentLine = (Element) nlShipmentLine.item(iInnerLoop);
					String strItemID = eleShipmentLine.getAttribute(KohlsConstant.A_ITEM_ID);

					String strHMKey = strItemID+KohlsConstant.DELIMITER_SHIP_HASHMAP+strShipmentType + KohlsConstant.DELIMITER_SHIP_HASHMAP +"N";

					if (hmShipments.containsKey(strHMKey)){

						int iValue = Integer.parseInt(hmShipments.get(strHMKey));					
						hmShipments.put(strItemID+KohlsConstant.DELIMITER_SHIP_HASHMAP+strShipmentType + KohlsConstant.DELIMITER_SHIP_HASHMAP +"N", ""+(++iValue));
					}

					else if (!hmShipments.containsKey(strHMKey)){

						hmShipments.put(strItemID+KohlsConstant.DELIMITER_SHIP_HASHMAP+strShipmentType + KohlsConstant.DELIMITER_SHIP_HASHMAP +"N", KohlsConstant.STRING_ONE);
					}
				}
			}
		}
	}


	/**
	 * This method prepares the output of this service with the help of Hashmap values
	 * @throws ParserConfigurationException 
	 */
	public Document prepareOutputDoc() throws ParserConfigurationException{

		Document outputDoc = XMLUtil.createDocument(KohlsConstant.E_PRINT_PACK_SHIPMENTS);
		Element elePrintPackShipments = outputDoc.getDocumentElement();

		for(Entry<String, String> entry : hmShipments.entrySet()){

			String strKey = entry.getKey();
			String[] saItemShipType = strKey.split(KohlsConstant.ESC_CHAR_DELIMITER_SHIP_HASHMAP);

			Element elePrintPackShipment = outputDoc.createElement(KohlsConstant.E_PRINT_PACK_SHIPMENT);
			elePrintPackShipment.setAttribute(KohlsConstant.A_ITEM_ID, saItemShipType[0]);
			elePrintPackShipment.setAttribute(KohlsConstant.A_SHIPMENT_TYPE, saItemShipType[1]);
			elePrintPackShipment.setAttribute(KohlsConstant.A_IS_PRODUCT_FAMILY_SHIPMENT, saItemShipType[2]);
			elePrintPackShipment.setAttribute(KohlsConstant.A_AVAILABLE_QTY, entry.getValue());

			elePrintPackShipments.appendChild(elePrintPackShipment);
		}

		return outputDoc;
	}
}