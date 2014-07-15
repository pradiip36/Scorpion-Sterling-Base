package com.kohls.bopus.api;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Properties;
import org.w3c.dom.Document;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/************************
 * @author Adam Dunmars
 * TODO: Move constants to another file.
 * TODO: Possibly create a SYSTEM_ID constant.
 **********************/
public class KohlsTranslator {
	private YIFApi api;
	private YFSEnvironment env;
	private String barcode;
	private Properties props;
	
	private static final YFCLogCategory logger = YFCLogCategory.instance(KohlsTranslator.class.getName());
	
	private static final String PROPERTY_MLS_DEFAULT_SCANNING_LOCATION = "MLS_DEFAULT_SCANNING_LOCATION"; 
	
	//Output Templates
	private static final String REGISTER_BARCODE_FOR_BACKROOM_PICK_OUTPUT_TEMPLATE = "global/template/api/extn/registerBarcodeForBackroomPick.xml";
	private static final String GET_SHIPMENT_LINE_LIST_OUTPUT_TEMPLATE = "<ShipmentLines SellerOrganizationCode=''><ShipmentLine ShipmentLineKey='' ShipmentLineNo='' ItemID='' BackroomPickedQuantity='' LocationId='' StoreNumber='' ShortPickIndicator='' AssignedToUser='' ReasonCode='' ReasonText=''><Shipment ShipmentNo = '' ShipmentKey='' /></ShipmentLine></ShipmentLines>";
	private static final String TRANSLATE_BARCODE_OUTPUT_TEMPLATE = "global/template/api/extn/translateBarcode.xml";
	
	//XML Element/Attribute Constants
	private static final String BARCODE = "BarCode";
	private static final String BARCODE_DATA = "BarCodeData";
	private static final String BARCODE_TYPE = "BarCodeType";
	private static final String TRANSLATIONS = "Translations";
	private static final String TRANSLATION = "Translation";
	private static final String TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";
	private static final String SHIPMENT_CONTEXTUAL_INFO = "ShipmentContextualInfo";
	private static final String LOCATION_CONTEXTUAL_INFO = "LocationContextualInfo";

	//XML Attribute value Constants
	private static final String ITEM = "Item";
	
	//API Constants
	private static final String REGISTER_BARCODE_FROM_BACKROOM_PICK = "registerBarcodeForBackroomPick";
	private static final String TRANSLATE_BARCODE = "translateBarCode";
	private static final String MLS_SCAN_SERVICE = "KohlsBOPUSUpdatePicking";
	private static final String GET_SHIPMENT_LINE_LIST = "getShipmentLineList";
	
	//Kohls Constants
	private static final String KOHLS_UPC_PREFIX = "400";
	private static final String KOHLS_BLACKHAWK_PREFIX = "076750";
	
	public KohlsTranslator() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}
	
	public String getPropertyValue(String property) {

		String propValue;
		propValue = YFSSystem.getProperty(property);
		if(YFCCommon.isVoid(propValue)){
			propValue = property;
		}
		return propValue;

	}
	
	/**************************************************************
	 * This is the original service call made from the BarcodeTranslation service defined in
	 * Application Platform.
	 * 
	 * @author Adam Dunmars
	 * @param env
	 * @param doc
	 * @return
	 * @throws Exception
	 ***************************************************************/
	public Document kohlsTranslator(YFSEnvironment env, Document doc) throws Exception {
		this.env = env;

		YFCDocument document = YFCDocument.getDocumentFor(doc);
		YFCElement barcodeElement = (YFCElement) document.getElementsByTagName(BARCODE).item(0);
		String barcode = barcodeElement.getAttribute(BARCODE_DATA);
		String barcodeType = barcodeElement.getAttribute(BARCODE_TYPE);
		barcodeElement.setAttribute(BARCODE_TYPE, ITEM);
		this.barcode = barcode;
		
		YFCElement scElement = barcodeElement.getChildElement(SHIPMENT_CONTEXTUAL_INFO);
		boolean isBackroomPick = (scElement == null) ? false : true;

		logger.debug("Barcode: " + this.barcode + ", Barcode Type: "+barcodeType+", isBackroomPick: "+isBackroomPick);

		Document outputXML = performItemListValidation(barcode, doc, isBackroomPick);
		
		return (!isSuccessfulTranslation(outputXML)) ? mapItemAlias(doc, isBackroomPick) : outputXML;
	}
	
	/************************************************************************
	 * This function is used to translate the bar code and return the correct output
	 * based on the @isBackroomPick flag.
	 * 
	 * If isBackroomPick flag is checked, the API call will be to registerBarcodeForBackroomPick;
	 * Otherwise the call will be made to getItemListForOrdering
	 * 
	 * @author Adam Dunmars
	 * @param barcode
	 * @param organizationCode
	 * @return
	 * @throws Exception
	 ***************************************************************************/
	public Document mapItemAlias(Document inputXML, boolean isBackroomPick) throws Exception{
		YFCDocument document = YFCDocument.getDocumentFor(inputXML);
		YFCElement barcodeElement = (YFCElement) document.getElementsByTagName(BARCODE).item(0);
		
		String barcode = barcodeElement.getAttribute(BARCODE_DATA);
		
		String itemAlias = "";
		String upc = "";
		String upca = "";
		String upce = "";
		String ean13 ="";
		Document docCheck = null;
		switch (barcode.length()) {
		case 6:
			itemAlias = ConvertUpceToUpca(barcode);
			docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			if(!isSuccessfulTranslation(docCheck)) {
				upca = "00" + barcode;
				upc = CreateUPCFromSKU(upca);
				itemAlias = upc;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				if(!isSuccessfulTranslation(docCheck)) {
					upc = "00" + barcode;
					itemAlias = ConvertEan8ToEan13(upc);
					docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				}
			}
			break;
		case 7:
			if (barcode.startsWith("0")) {
				upce = barcode;
				upce += CalculateCheckDigit(barcode);
				itemAlias = ConvertUpceToUpca(upce);
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			} else {
				upce = barcode;
				itemAlias = ConvertUpceToUpca(upce);
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			}
			if(!isSuccessfulTranslation(docCheck)) {
				upca = "0" + barcode;
				itemAlias = CreateUPCFromSKU(upca);
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			}
			break;
		case 8:
			upc = CreateUPCFromSKU(barcode);
			itemAlias = upc;
			docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			if(!isSuccessfulTranslation(docCheck)) {
				upca = ConvertUpceToUpca(barcode);
				itemAlias = upca;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				if(!isSuccessfulTranslation(docCheck)) {
					ean13 = ConvertEan8ToEan13(barcode);
					itemAlias = ean13;
					docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				}
			}
			break;
		case 9:
			break;
		case 10:
			itemAlias ="0"+barcode + CalculateCheckDigit(barcode);
			docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			break;
		case 11:
			if(barcode.startsWith(KOHLS_UPC_PREFIX)) {
				upca = barcode;
				upca += CalculateCheckDigit(barcode);
				itemAlias = upca;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				if(!isSuccessfulTranslation(docCheck)) {
					upca = "0"+barcode;
					itemAlias = upca;
					docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
					if(!isSuccessfulTranslation(docCheck)) {
						upca = barcode;
						upca += CalculateCheckDigit(barcode);
						itemAlias = upca;
						docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
					}
				}
			} else {
				upca = "0"+barcode;
				itemAlias = upca;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				if(!isSuccessfulTranslation(docCheck)) {
					upca = barcode;
					upca += CalculateCheckDigit(barcode);
					itemAlias = upca;
					docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				}
			}
			break;
		case 12:
			if(barcode.startsWith(KOHLS_UPC_PREFIX)) {
				upca = barcode;
				upca = upca.substring(3, 11);
				itemAlias = upca;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			} else {
				if (ValidateCheckDigit(barcode)) {
					itemAlias = barcode;
					docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
				}
			}
			break;
		case 13:
			if (ValidateCheckDigit(barcode)) {
				itemAlias = barcode;
				docCheck = performItemListValidation(itemAlias, inputXML, isBackroomPick);
			}
		}
		return docCheck;
	}
	
	/*****************************************************************************
	 * @author Adam Dunmars
	 * 
	 * This helper function will check whether the output from a 
	 * translateBarCode or registerBarcodeForBackroomPick API call
	 * returned a successful translation or not by checking the 
	 * presumed XPath: 
	 * 		
	 * 		BarCode/Translations/@TotalNumberOfRecords.
	 * 
	 * @param outputXML 	The output XML from the 
	 * 						registerBarcodeForBackroomPick/translateBarcode API.
	 * @return 				True if the translation was successful, false otherwise.
	 * 
	 * TODO: Add surrounding with try/catch.
	 *****************************************************************************/
	public boolean isSuccessfulTranslation(Document outputXML) {
		
		if(!YFCObject.isVoid(outputXML)) {
			int totalNumberOfRecords = 0;
			
			YFCDocument yfcOutputXML = YFCDocument.getDocumentFor(outputXML);
			
			YFCElement barcodeElement = yfcOutputXML.getDocumentElement();
			YFCElement translationsElement = barcodeElement.getChildElement(TRANSLATIONS);
			totalNumberOfRecords = translationsElement.getIntAttribute(TOTAL_NUMBER_OF_RECORDS);
			
			return totalNumberOfRecords > 0;
		} else {
			return false;
		}

	}
	
	/*********************************************************
	 * @function updateItemAliasInputFile
	 * @author 	 Adam Dunmars
	 * 
	 * @param barcode	The new barcode which will be set in the BarCode/@BarCodeData path.
	 * @param inputXML	The document in which to place the barcode.
	 * 
	 * @return			Returns the new updated document.
	 *********************************************************
	 * This helper function will update the value of the barcode in the input XML.
	 * 
	 * The purpose of this helper function is to update the barcode. 
	 * through each iteration of the Kohl's Barcode Translation algorithm which will
	 * change the barcode's lookup value each time.
	 * 
	 * TODO: Determine if YFCDocument intermediary causes performance impact.
	 * TODO: Change legacy function name
	 ********************************************************/
	public Document updateItemAliasInputFile(String barcode, Document inputXML) {
		YFCDocument yfcInputXML = YFCDocument.getDocumentFor(inputXML);
		YFCElement barcodeElement = yfcInputXML.getDocumentElement();
		barcodeElement.setAttribute(BARCODE_DATA, barcode);
		return yfcInputXML.getDocument();
	}

	/********************************************************************************
	 * @function: performItemListValidation
	 * @author: Adam Dunmars
	 * @param barcode		The barcode to be included in the inputXML.
	 * @param inputXML		The input document used for either the registerBarcodeForBackroomPick
	 * 						or translateBarcode API call.
	 * @param isBackroomPick	This determines whether the scan was performed as
	 * 						a scan on the mobile application(Associate Picking Application)
	 * 						or the web application (Customer Pickup Application)
	 * @return				The output document
	 * @throws Exception
	 * ****************************************************************************
	 * This function will perform either make a call to translateBarcode or
	 * registerBarcodeForBackroomPick API based on the @backroomPick input parameter.
	 * 
	 * This function first updates the input XML with the barcode. Following this:
	 * 		1. If the scan was performed in the Associate Picking Application it will:
	 * 			a. 	Retrieve the location of the scan based on following XPath:
	 * 					Barcode/LocationContextualInfo/@Location ID
	 * 				i. 	(If no location was returned, we assume that this was a scan performed in the
	 * 					Salesfloor context and add a location from the properties file).
	 * 			b. 	Remove the location information so that the registerBarcodeForBackroomPick 
	 * 			 	API call completes successfully.
	 * 			c. 	Makes a call to registerBarcodeForBackroomPick API.
	 * 			d. 	Retrieves the shipment line key from the output based on the following XPath:
	 * 					Barcode/Translations/Translation/ShipmentContextualInfo/@ShipmentLineKey 
	 * 			e. 	Uses this information to create input for the MLS Scan service call.
	 * 			f. 	Makes a call to the service KohlsBopusUpdatePicking.
	 * 		2. Otherwise:
	 * 			a.	Make a call to the translateBarcode API.
	 * After the API calls have been finished the final output document will be returned.
	 * 
	 * TODO: Possibly clean up function definition to not include the barcode.
	 * TODO: Surround with try/catch and exception handling
	 ********************************************************/
	public Document performItemListValidation(String barcode, Document inputXML, boolean isBackroomPick) throws Exception{
		Document returnDoc = null;
		
		logger.debug("Initial Input XML: "+SCXmlUtil.getString(inputXML));
		
		Document updatedInputXML = updateItemAliasInputFile(barcode, inputXML);
		logger.debug("Updated Input XML:"+SCXmlUtil.getString(updatedInputXML));
				
		YFCDocument yfcInputXML = YFCDocument.getDocumentFor(updatedInputXML);
		
		if(isBackroomPick) {
			logger.debug("-------BACKROOM PICK TYPE SCAN---------");
			
			YFCElement yfcBarcodeElement = yfcInputXML.getDocumentElement();
			YFCElement yfcLocationContextualInfoElement = yfcBarcodeElement.getChildElement(LOCATION_CONTEXTUAL_INFO);
			String mlsFlag = yfcBarcodeElement.getAttribute("MLSFlag");
			String pickingLocation = null;
			String locationContext = null;
			boolean updateMLS = YFCObject.isVoid(mlsFlag) ? false : mlsFlag.equalsIgnoreCase("Y");
			
			logger.debug("****************MLS Flag is set to: "+updateMLS+"*****************");
			if(YFCObject.isVoid(yfcLocationContextualInfoElement) && 
					!YFCObject.isVoid(props.getProperty(PROPERTY_MLS_DEFAULT_SCANNING_LOCATION)) ) {
				logger.debug("Did not find a picking location, adding one from the property file...");
				pickingLocation = props.getProperty(PROPERTY_MLS_DEFAULT_SCANNING_LOCATION);
				locationContext = KohlsConstant.SFO;
			} else if (!YFCObject.isVoid(yfcLocationContextualInfoElement)){
				pickingLocation = yfcLocationContextualInfoElement.getAttribute(KohlsConstant.A_LOCATION_ID);
				locationContext = yfcLocationContextualInfoElement.getAttribute(KohlsConstant.A_LOCATION_TYPE);
			} else {
				//Error case if no property is defined. ~Adam
			}
			
			updatedInputXML = removePickingLocation(updatedInputXML);
			logger.debug("Input XML with removed Picking Location: "+SCXmlUtil.getString(updatedInputXML));
			env.setApiTemplate(TRANSLATE_BARCODE, TRANSLATE_BARCODE_OUTPUT_TEMPLATE);
			returnDoc = api.invoke(env, TRANSLATE_BARCODE, updatedInputXML);
			logger.debug("Output XML from TranslateBarCode: "+SCXmlUtil.getString(returnDoc));
			
			if(isSuccessfulTranslation(returnDoc)) {
				logger.debug("Input XML to RegisterBarcodeForBackroomPick: "+SCXmlUtil.getString(updatedInputXML));
				env.setApiTemplate(REGISTER_BARCODE_FROM_BACKROOM_PICK, REGISTER_BARCODE_FOR_BACKROOM_PICK_OUTPUT_TEMPLATE);
				returnDoc = api.invoke(env, REGISTER_BARCODE_FROM_BACKROOM_PICK, updatedInputXML);
				
				logger.debug("Output XML from RegisterBarcodeForBackroomPick: "+SCXmlUtil.getString(returnDoc));
				if(isSuccessfulTranslation(returnDoc) && updateMLS) {
					YFCDocument yfcReturnDoc = YFCDocument.getDocumentFor(returnDoc);
					YFCElement barcodeElement = yfcReturnDoc.getDocumentElement();
					YFCElement translationsElement = barcodeElement.getChildElement(TRANSLATIONS);
					YFCElement translationElement = translationsElement.getChildElement(TRANSLATION);
					YFCElement shipmentContextualInfo = translationElement.getChildElement(SHIPMENT_CONTEXTUAL_INFO);
					String shipmentLineKey = shipmentContextualInfo.getAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY);
							
					YFCDocument getShipmentLineListInput = YFCDocument.createDocument(KohlsConstant.E_SHIPMENT_LINE);
					YFCElement shipmentLineElement = getShipmentLineListInput.getDocumentElement();
					shipmentLineElement.setAttribute(KohlsConstant.A_SHIPMENT_LINE_KEY, shipmentLineKey);
					
					env.setApiTemplate(GET_SHIPMENT_LINE_LIST, XmlUtils.createFromString(GET_SHIPMENT_LINE_LIST_OUTPUT_TEMPLATE));
					Document shipmentLineListOutput = KOHLSBaseApi.invokeAPI(env, GET_SHIPMENT_LINE_LIST, getShipmentLineListInput.getDocument());
					
					YFCDocument yfcShipmentLineListOutput = YFCDocument.getDocumentFor(shipmentLineListOutput);
					YFCElement yfcShipmentLinesElement = yfcShipmentLineListOutput.getDocumentElement();
					YFCElement yfcShipmentLineElement = yfcShipmentLinesElement.getChildElement(KohlsConstant.E_SHIPMENT_LINE);
					
					yfcShipmentLineElement.setAttribute(KohlsXMLLiterals.A_PICKING_LOCATION, pickingLocation);
					yfcShipmentLineElement.setAttribute(KohlsConstant.A_LOCATION_CONTEXT, locationContext);
					
					logger.debug("Input to MLS Scan Service: " +SCXmlUtil.getString(yfcShipmentLineListOutput.getDocument()));
					Document mlsReturnDoc = KOHLSBaseApi.invokeService(env, MLS_SCAN_SERVICE, yfcShipmentLineListOutput.getDocument());
					
					logger.debug("Output from MLS Scan Service: "+SCXmlUtil.getString(mlsReturnDoc));
				} else {
					//Probably should throw some YFSException here. ~Adam.
				}
			}
		} else {
			env.setApiTemplate(TRANSLATE_BARCODE, TRANSLATE_BARCODE_OUTPUT_TEMPLATE);
			returnDoc = api.invoke(env, TRANSLATE_BARCODE, updatedInputXML);
		}
		
		env.clearApiTemplates();
		
		return returnDoc;
	}
	
	/******************************************************
	 * @author: Adam Dunmars
	 * @param:  inputXML
	 * @return:
	 ******************************************************/
	public Document removePickingLocation(Document inputXML) {
		YFCDocument yfcInputDoc = YFCDocument.getDocumentFor(inputXML);
		YFCElement barcodeElement = yfcInputDoc.getDocumentElement();
		YFCElement locationContextualInfoElement = barcodeElement.getChildElement(LOCATION_CONTEXTUAL_INFO);
		if(!YFCCommon.isVoid(locationContextualInfoElement)) {
			barcodeElement.removeChild(barcodeElement.getChildElement(LOCATION_CONTEXTUAL_INFO));
		}
		return yfcInputDoc.getDocument();
	}
	
	/**********************************************************************
	 * @author: Adam Dunmars
	 * 
	 * @description: This function will get the picking location for 
	 * This function determines if a call to MLS needs to be made by checking the input XML file.
	 * If the inputXML contains a LocationContextualInfo Element AND a LocationId attribute,
	 * the function will assume a call to MLS will be made to the LocationId and will return true;
	 * otherwise it will return false.
	 * 
	 * @param inputXML: The input document provided by the mashup call.
	 * @return true if a call to MLS is to be made or not to update.
	 *************************************************************************/
	public String getPickingLocation(Document inputXML){
		YFCDocument yfcInputXML = YFCDocument.getDocumentFor(inputXML);
		YFCElement barcodeElement = yfcInputXML.getDocumentElement();
		YFCElement locationContextualInfoElement = barcodeElement.getChildElement(LOCATION_CONTEXTUAL_INFO);
		if(!YFCObject.isVoid(locationContextualInfoElement)) {
			String pickingLocation = locationContextualInfoElement.getAttribute(KohlsConstant.A_LOCATION_ID);
			return pickingLocation;
		}
		return null;
	}

	/**
	 * 
	 * @param sUpc
	 * @return
	 */
	public static String ExpandShortUpc(String sUpc) {
		if (sUpc.length() > 11)
			return sUpc;

		char[] cUpc = new char[11];
		for(int i = 0; i < sUpc.length(); i++){
			cUpc[i] = '0';
		}
		for(int i = 0; i < sUpc.length(); i++) {
			cUpc[i] = sUpc.charAt(i);
		}

		switch (cUpc[5]) {
		case '0':
		case '1':
		case '2':
			cUpc[0] = '0';
			cUpc[1] = sUpc.charAt(0);
			cUpc[2] = sUpc.charAt(1);
			cUpc[3] = sUpc.charAt(5);
			cUpc[4] = '0';
			cUpc[5] = '0';
			cUpc[6] = '0';
			cUpc[7] = '0';
			cUpc[8] = sUpc.charAt(2);
			cUpc[9] = sUpc.charAt(3);
			cUpc[10] = sUpc.charAt(4);
			break;
		case '3':
			cUpc[0] = '0';
			cUpc[1] = sUpc.charAt(0);
			cUpc[2] = sUpc.charAt(1);
			cUpc[3] = sUpc.charAt(2);
			cUpc[4] = '0';
			cUpc[5] = '0';
			cUpc[6] = '0';
			cUpc[7] = '0';
			cUpc[8] = '0';
			cUpc[9] = sUpc.charAt(3);
			cUpc[10] = sUpc.charAt(4);
			break;
		case '4':
			cUpc[0] = '0';
			cUpc[1] = sUpc.charAt(0);
			cUpc[2] = sUpc.charAt(1);
			cUpc[3] = sUpc.charAt(2);
			cUpc[4] = sUpc.charAt(3);
			cUpc[5] = '0';
			cUpc[6] = '0';
			cUpc[7] = '0';
			cUpc[8] = '0';
			cUpc[9] = '0';
			cUpc[10] = sUpc.charAt(4);
			break;
		default:
			cUpc[0] = '0';
			cUpc[1] = sUpc.charAt(0);
			cUpc[2] = sUpc.charAt(1);
			cUpc[3] = sUpc.charAt(2);
			cUpc[4] = sUpc.charAt(3);
			cUpc[5] = sUpc.charAt(4);
			cUpc[6] = '0';
			cUpc[7] = '0';
			cUpc[8] = '0';
			cUpc[9] = '0';
			cUpc[10] = sUpc.charAt(5);
		}

		return new String(cUpc);
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static boolean ValidateCheckDigit(String input) {
		String upc = input.substring(0, input.length() - 1);
		char checkDigit = input.charAt(input.length() - 1);

		return (checkDigit - '0' == CheckDigitCreate(upc, CheckDigitConstants.UPC_WEIGHT, 10,5));
	}

	/**
	 * 
	 * @param input
	 * @return
	 */
	public static char CalculateCheckDigit(String input) {
		return (char) (48 + CheckDigitCreate(input, CheckDigitConstants.UPC_WEIGHT, 10, 5));
	}

	/**
	 * 
	 * @param barcode
	 * @param sysDigitProvided
	 * @param checkDigitProvied
	 * @return
	 */
	public static String ConvertUpceToUpca(String barcode,
			boolean sysDigitProvided, boolean checkDigitProvied) {
		int nCheckDigit = 0;

		barcode = ExpandShortUpc(barcode);
		nCheckDigit = CheckDigitCreate(barcode, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		barcode = barcode.concat(String.valueOf(nCheckDigit));

		return barcode;
	}

	public static String ConvertUpceToUpca(String barcode) {
		if (barcode.length() == 8) {
			barcode = barcode.substring(1, barcode.length()-1);
			barcode = barcode.substring(0, 6);
		}
		if (barcode.length() == 7) {
			if (Integer.valueOf(barcode.charAt(0)).intValue() == 0)
				barcode = barcode.substring(1, barcode.length() - 1);
			else
				barcode = barcode.substring(0, 6);
		}
		int nCheckDigit = 0;
		barcode = ExpandShortUpc(barcode);
		nCheckDigit = CheckDigitCreate(barcode, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		barcode = barcode.concat(String.valueOf(nCheckDigit));
		
		return barcode;
	}

	/**
	 * 
	 * @param sSku
	 * @return
	 */
	public static String CreateUPCFromSKU(String sSku) {
		int nCheckDigit = 0;

	    String sUpc = sSku;
	
	    switch (sSku.length())
	    {
		    case 6:
		    case 7:
		      sUpc = ExpandShortUpc(sUpc);
		      nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		      sUpc = sUpc.concat(String.valueOf(nCheckDigit));
		
		      break;
		    case 8:
		    	NumberFormat df = NumberFormat.getInstance(Locale.US);
		        String pattern = "00000000";
		        if (df instanceof DecimalFormat) {
		          ((DecimalFormat)df).applyPattern(pattern);
		        }
		        sUpc = df.format(Double.valueOf(sUpc));
		        sUpc = KOHLS_UPC_PREFIX+sUpc;
		
		        nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		      sUpc = sUpc.concat(String.valueOf(nCheckDigit));
		
		      break;
		    case 10:
		      sUpc = "0";
		      StringBuilder builder = new StringBuilder(sUpc);
		      builder.insert(1, sSku);
		      sUpc = sUpc.toString();
		      nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		
		      sUpc = sUpc.concat(String.valueOf(nCheckDigit));
		      break;
		    case 11:
		      if ((sUpc.startsWith(KOHLS_UPC_PREFIX)) || (sUpc.startsWith(KOHLS_BLACKHAWK_PREFIX))) {
		    	  nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		    	  sUpc = sUpc.concat(String.valueOf(nCheckDigit));
		      }
		
		      int nOldCheckDigit = 0;
		      df = NumberFormat.getInstance(Locale.US);
		      pattern = "000000000000";
		      if (df instanceof DecimalFormat) {
		        ((DecimalFormat)df).applyPattern(pattern);
		      }
		      sUpc = df.format(Double.valueOf(sUpc));
		
		      nOldCheckDigit = sUpc.charAt(10);
		
		      nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		      if (nCheckDigit == nOldCheckDigit) {
		        sUpc = sUpc.concat(String.valueOf(nCheckDigit)); 
		      }
		
		      nCheckDigit = CheckDigitCreate(sUpc, CheckDigitConstants.UPC_WEIGHT, 10, 5);
		
		      sUpc = sUpc.concat(String.valueOf(nCheckDigit));
		    case 9:
		    case 12:
		    case 13:
	    }
	    return sUpc;
  }

	public static String ConvertEan8ToEan13(String barcode) {
		return "00000"+barcode;
	}

	/**
	 * 
	 * @param Suspect
	 * @param Weight
	 * @param Modulus
	 * @param CVD_Method
	 * @return
	 */
	public static int CheckDigitCreate(String Suspect, String Weight, int Modulus, int CVD_Method) {
		int PRODUCT_DIGIT_ADD = 1;

		int error = -1;
		try {
			if (Suspect != null)
				Suspect.trim();

			Suspect.length();
			Weight.length();

			String validChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_";
			Suspect = Suspect.trim().toUpperCase();

			int sum = 0;

			for (int i = 0; i < Suspect.length(); ++i) {
				char ch = Suspect.charAt(Suspect.length() - i - 1);
				"0123456789ABCDEFGHIJKLMNOPQRSTUVYWXZ_".indexOf(ch);

				int digit = ch - '0';
				int weighting = (Weight.charAt(Weight.length() - i - 2) - '0')
						* digit;

				if (CVD_Method == 1) {
					String strTemp = String.valueOf(weighting);
					for (int j = 0; j < strTemp.length(); ++j) {
						sum += Integer.valueOf(strTemp.charAt(j) - '0')
								.intValue();
					}
				} else {
					sum += weighting;
				}
			}
			sum = Math.abs(sum) + Modulus;
			return ((Modulus - (sum % Modulus)) % Modulus);
		} catch (Exception localException) {
		}

		return error;
	}

	public class CheckDigitConstants {
		public static final int UPC_MOD = 10;
		public static final String UPC_WEIGHT = "1313131313131";
		public static final String EAN_WEIGHT = "13131313131313";
		public static final String CREATE_EAN_WEIGHT = "313131313131";
		public static final int CHECK_DIGIT_MOD = 10;
		public static final String CHECK_DIGIT_CREATE_WEIGHT = "21212121212121212121212";
		public static final String CHECK_DIGIT_WEIGHT = "1212121212121212121212121";
		public static final int PRODUCT_ADD = 5;
	}
}