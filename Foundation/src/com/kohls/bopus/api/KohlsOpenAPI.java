package com.kohls.bopus.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.framework.utils.SCXmlUtils;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Adam Dunmars
 * TODO: May be able to call to get all images with a single OpenAPI call
 */
public class KohlsOpenAPI {

	//Properties
	private Properties props;
	
	//Function specifics
	private YFCLogCategory log = YFCLogCategory.instance(KohlsOpenAPI.class.getName());
	private YIFApi api;
	
	//Constants
	private static final String KOHLS_BOPUS_PLU_WEB_SERVICE = "KohlsBOPUSPLUWebService";
	private static final String GET_SORTED_SHIPMENT_DETAILS = "getSortedShipmentDetails";
	private static final String GET_SHIPMENT_LINE_LIST = "getShipmentLineList";
	private static final String SHIPMENT_LINES = "ShipmentLines";
	private static final String SHIPMENT_LINE= "ShipmentLine";
	private static final String ORDER_LINE = "OrderLine";
	private static final String IMAGE_LOCATION = "ImageLocation";
	private static final String IMAGE_ID = "ImageID";
	private static final String ITEM_ID = "ItemID";
	private static final String PRIMARY_INFORMATION = "PrimaryInformation";
	private static final String ITEM_DETAILS = "ItemDetails";
	private static final String ITEM = "Item";
	
	//Return XML Constants
	private static final String URL = "URL";
	private static final String IMAGE = "Image";
	private static final String IMAGES = "Images";
	private static final String NUMBER_OF_RECORDS = "NumberOfRecords";
	private static final String IMAGE_LABEL = "ImageLabel";
	
	//OpenAPI Constants
	private static final String OPEN_API_PRODUCT = "product";
	private static final String OPEN_API_URL = "url";
	private static final String OPEN_API_IMAGES = "images";
	private static final String OPEN_API_IMAGE = "image";
	
	//private static final String GET_SORTED_SHIPMENT_LIST_OUTPUT_TEMPLATE = "<Shipment ExpectedShipmentDate =\"\"  ShipmentKey =\"\"  EnterpriseCode =\"\"  RequestedDeliveryDate =\"\"  Currency =\"\"  Status =\"\"  HoldLocation =\"\"  OriginalShipmentKey =\"\"  RequestedShipmentDate =\"\"  TransactionId =\"\"  ShipmentNo =\"\"  GiftFlag =\"\"  ShipNode =\"\"  SellerOrganizationCode =\"\" ><ShipmentLines><ShipmentLine ShipmentLineKey =\"\"  OrderHeaderKey =\"\"  ShipmentKey =\"\"  ShipmentSubLineNo =\"\"  Quantity =\"\"  OrderLineKey =\"\"  ShipmentLineNo =\"\"  OrderNo =\"\" ><OrderLine IsBundleParent =\"\"  ItemGroupCode =\"\"  GiftFlag =\"\" ><LinePricedebug UnitPrice =\"\" ></LinePricedebug><PersondebugMarkFor IsCommercialAddress =\"\"  MiddleName =\"\"  LastName =\"\"  DayFaxNo =\"\"  EveningPhone =\"\"  AddressLine1 =\"\"  City =\"\"  MobilePhone =\"\"  ZipCode =\"\"  State =\"\"  DayPhone =\"\"  Country =\"\"  EveningFaxNo =\"\"  PreferredShipAddress =\"\"  EMailID =\"\"  FirstName =\"\"  PersondebugKey =\"\" ></PersondebugMarkFor><ItemDetails UnitOfMeasure =\"\"  ItemGroupCode =\"\"  ItemID =\"\" ><primaryInformation ImageLocation =\"\"  ExtendedDisplayDescription =\"\"  ImageID =\"\"  ImageLabel =\"\" ></primaryInformation></ItemDetails></OrderLine><Order><PersondebugBillTo/><PaymentMethods><PaymentMethod><PersondebugBillTo/></PaymentMethod></PaymentMethods></Order></ShipmentLine></ShipmentLines><BillToAddress IsCommercialAddress =\"\"  MiddleName =\"\"  LastName =\"\"  DayFaxNo =\"\"  EveningPhone =\"\"  AddressLine1 =\"\"  City =\"\"  MobilePhone =\"\"  ZipCode =\"\"  State =\"\"  DayPhone =\"\"  Country =\"\"  EveningFaxNo =\"\"  PreferredShipAddress =\"\"  EMailID =\"\"  FirstName =\"\"  PersondebugKey =\"\" ></BillToAddress></Shipment>";
	private static final String GET_SORTED_SHIPMENT_LIST_OUTPUT_TEMPLATE = "global/template/api/extn/getSortedShipmentDetails_CPA.xml";
	private static final String GET_SHIPMENT_LINE_SUMMARY_OUTPUT_TEMPLATE = "global/template/api/extn/getShipmentSummary_CPA.xml";
	
	public KohlsOpenAPI() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getApi();
	}
	
	
	
	/**
	 * 
	 * @param env
	 * @param inputXML
	 * @return
	 * @throws Exception
	 */
	public Document getOpenAPIImage(YFSEnvironment env, Document inputXML) throws Exception {
		Document outputXML = null;
		NodeList shipmentLineList = null;
		YFCDocument yfcGSSDOX = null;
		String shipmentNo = "";
		String shipNode = "";
		
				
		if (YFCCommon.equals(inputXML.getFirstChild().getNodeName(), KohlsXMLLiterals.E_SHIPMENT))
		{	
			env.setApiTemplate(GET_SORTED_SHIPMENT_DETAILS, GET_SORTED_SHIPMENT_LIST_OUTPUT_TEMPLATE);
			outputXML = api.invoke(env, GET_SORTED_SHIPMENT_DETAILS, inputXML);
			env.clearApiTemplates();
			
			yfcGSSDOX = YFCDocument.getDocumentFor(outputXML);
			Element shipmentEle = outputXML.getDocumentElement();
	    	shipmentLineList = shipmentEle.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
	    	shipmentNo = shipmentEle.getAttribute(KohlsConstant.A_SHIPMENT_NO);
	    	shipNode = shipmentEle.getAttribute(KohlsConstant.A_SHIP_NODE);
		}
		
		else
		{
			env.setApiTemplate(GET_SHIPMENT_LINE_LIST, GET_SHIPMENT_LINE_SUMMARY_OUTPUT_TEMPLATE);
			outputXML = api.invoke(env, GET_SHIPMENT_LINE_LIST, inputXML);
			env.clearApiTemplates();
			
			yfcGSSDOX = YFCDocument.getDocumentFor(outputXML);
			Element shipmentEle = outputXML.getDocumentElement();
	    	shipmentLineList = shipmentEle.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);			
			
			
		}

		for(int i = 0; i < shipmentLineList.getLength(); i++) 
		{
			
			Element eleShipmentLine = (Element) shipmentLineList.item(i);
			
			String itemID = "";
			
			// see if we have a translation
    		if (!YFCObject.isVoid(YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID)))) {
				// use the translated value
    			itemID = YFSSystem.getProperty(KohlsConstant.BOPUS_TRANSLATION_ITEM_PREFIX + eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
			} else {
				// no translation
				itemID = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ITEM_ID);
			}
    		
		
			String openAPIBaseURL = YFSSystem.getProperty(KohlsConstant.V_OPEN_API_BASEURL);
			String skuDetail = YFSSystem.getProperty(KohlsConstant.V_SKU_DETAIL);

    		Element eleOrderLine = SCXmlUtil.getChildElement(eleShipmentLine, KohlsXMLLiterals.E_ORDER_LINE );
    		Element eleItemDetails = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ITEM_DETAILS);

    		//PLU Callback code
    		String orderNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER);
    		String orderHdrKey = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
    		String orderLineNo = eleShipmentLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_NO);
    		
    		Document PLU_InputXml = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_VALIDATION);
    		Element itemValidationEle = PLU_InputXml.getDocumentElement();
    		itemValidationEle.setAttribute(KohlsConstant.A_SHIPMENT_NO, shipmentNo);
    		itemValidationEle.setAttribute(KohlsConstant.A_SHIP_NODE, shipNode);
    		itemValidationEle.setAttribute(KohlsXMLLiterals.A_ORDER_NUMBER, orderNo);
    		itemValidationEle.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHdrKey);
    		itemValidationEle.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_NO, orderLineNo);
    		itemValidationEle.setAttribute("ItemId", itemID);
    		itemValidationEle.setAttribute(KohlsXMLLiterals.A_LOOKUPTYPE, "SKU");
    		System.out.println(SCXmlUtil.getString(PLU_InputXml));
    		
    		try
    		{
    		
    			Document PLU_OutputXml = KOHLSBaseApi.invokeService(env, KOHLS_BOPUS_PLU_WEB_SERVICE, PLU_InputXml);
    		
	    		Element itemValidationRespEle = PLU_OutputXml.getDocumentElement();
	    		String callBackIndicator = itemValidationRespEle.getAttribute(KohlsXMLLiterals.A_CALL_BACK_INDICATOR);
	    		
	    		if(callBackIndicator.equalsIgnoreCase("Y"))
	    		{
	    			eleOrderLine.setAttribute(KohlsXMLLiterals.E_HOLD_REASON_CODE, "CALLBACK");
	    		}
	    	
    				
    		}
    		catch(Exception et)
    		{
    			System.out.println("Exception Happened while calling Kohls PLU Service" + et.getMessage());
    			
    		}
				    		
    		
    		// if item details are null, create it
    		if (YFCObject.isVoid(eleItemDetails)) {
    			// add it to the order line
    			eleItemDetails = outputXML.createElement(KohlsXMLLiterals.E_ITEM_DETAILS);
    			eleOrderLine.appendChild(eleItemDetails);
    		}
    		
    		String openAPIItemURL = openAPIBaseURL + "?skuCode=" + itemID + "&skuDetail=" + skuDetail;
    		log.debug(openAPIItemURL);
			
			String respString = openImageHttpRequest(env, openAPIItemURL);
			Document respDoc = XMLUtil.getDocument(respString);
			
			if (respDoc != null) {
        		Element respEle = respDoc.getDocumentElement();
        		NodeList imagesList =  respEle.getElementsByTagName(KohlsXMLLiterals.E_IMAGES);

        		Document docItemUrls = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_URLS);
				Element eleItemUrls = docItemUrls.getDocumentElement();
        		for (int j=0; j<imagesList.getLength(); j++) {
        			String sURL = "";
        			String altText = "";
        			Element imagesEle = (Element) imagesList.item(j);
        			Element imageEle = SCXmlUtil.getChildElement(imagesEle, KohlsXMLLiterals.E_IMAGE);
        			sURL = imageEle.getElementsByTagName(KohlsXMLLiterals.E_URL).item(0).getTextContent();
        			if (!YFCObject.isVoid(imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT)) && !YFCObject.isVoid(imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT).item(0))) {
        				altText = imageEle.getElementsByTagName(KohlsXMLLiterals.E_ALT_TEXT).item(0).getTextContent();
        			}
        			Element eleItemUrl = SCXmlUtil.createChild(eleItemUrls, KohlsXMLLiterals.E_ITEM_URL);
        			eleItemUrl.setAttribute(KohlsXMLLiterals.A_URL, sURL);
        			// use the first image as the primary image
        			if (j==0) {
        				// get the item details
        				NodeList itemDetailsList = eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_ITEM_DETAILS);
        				if (!YFCObject.isVoid(itemDetailsList)) {
        					Element itemDetailsElement = (Element) itemDetailsList.item(0);
        					// get the primary information
        					NodeList primaryInformationList = itemDetailsElement.getElementsByTagName(KohlsXMLLiterals.E_PRIMARY_INFORMATION);
        					if (!YFCObject.isVoid(primaryInformationList)) {
        						Element primaryInformationElement = (Element) primaryInformationList.item(0);
        						// break the URL up into image location and image id
        						int lastSlash = sURL.lastIndexOf("/");
        						if (lastSlash > 0) {
	        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_LOCATION, sURL.substring(0, lastSlash));
	        						String imageId = sURL.substring(lastSlash+1, sURL.length());
	        						//ootb doesn't like ? remove query parameters
	        						int lastQuestion = imageId.lastIndexOf("?");
	        						imageId = imageId.substring(0,lastQuestion);
	        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_ID, imageId);
	        						primaryInformationElement.setAttribute(KohlsXMLLiterals.A_IMAGE_LABEL, altText);
        						}
        					}
        				}
        			}
        		}
			}
		}
		
		log.debug("OPEN API: " + yfcGSSDOX.toString());
		log.debug("Exiting getOpenAPIImage function");
		return outputXML;
	}
	
	/**
	 * 
	 * @param itemID
	 * @return
	 */
	public Document getOpenAPIUrl(String itemID) {
		/*HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet("http://qe11-openapi.kohlsecommerce.com/v1/product/662308");
		//HttpGet get = new HttpGet("http://qe11-openapi.kohlsecommerce.com/v1/product/"+itemID);
		get.addHeader("X-APP-API_KEY", "QTnFGZPyKhoN6hjLKWrS3A37yer6eW3T");
		try {
			HttpResponse response = client.execute(get);
			System.out.println("Response Code : "
					+ response.getStatusLine().getStatusCode());
			BufferedReader rd = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			StringBuffer result = new StringBuffer();
			String line;
			
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
			String resultString = result.toString();
			
			
			Document responseDoc = XmlUtils.createFromString(resultString);
			
			YFCDocument returnDocYfc = YFCDocument.createDocument(IMAGES);
			YFCElement imagesElement = returnDocYfc.getDocumentElement();
			
			log.debug(YFCDocument.getDocumentFor(responseDoc).toString());
			YFCDocument responseDocYfc = YFCDocument.getDocumentFor(responseDoc);
			YFCElement products = responseDocYfc.getDocumentElement();
			YFCElement product = products.getElementsByTagName(OPEN_API_PRODUCT).item(0);
			if(product == null) {
				YFCElement imageElement = imagesElement.createChild(IMAGE);
				imageElement.setAttribute(URL, "");
			} else {
				YFCElement images = product.getElementsByTagName(OPEN_API_IMAGES).item(0);
				if(images == null) {
					YFCElement imageElement = imagesElement.createChild(IMAGE);
					imageElement.setAttribute(URL, "");
				} else {
					YFCNodeList<YFCElement> imageNodes = images.getElementsByTagName(OPEN_API_IMAGE);
					String[] imageUrls = new String[imageNodes.getLength()];
					if(imageUrls.length == 0) {
						YFCElement imageElement = imagesElement.createChild(IMAGE);
						imageElement.setAttribute(URL, "");
					} else {
						for (int i = 0; i < imageUrls.length; ++i) {
							String imageURL = imageNodes.item(i).getChildElement(OPEN_API_URL).getNodeValue();
							imageUrls[i] = imageURL;
						}
						
						imagesElement.setAttribute(NUMBER_OF_RECORDS, imageUrls.length);
						
						for (int i = 0; i < imageUrls.length; ++i) {
							YFCElement imageElement = imagesElement.createChild(IMAGE);
							imageElement.setAttribute(URL, imageUrls[i]);
						}
					}
				}
			}
			

			return returnDocYfc.getDocument();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (FactoryConfigurationError e) {
			e.printStackTrace();
		}
		return null;*/
		return null;
	}
	
	public String openImageHttpRequest ( YFSEnvironment env,  String openAPIItemURL) throws IOException, Exception
	{
		// see if we need to bypass open api
		String openApiOutput = "";
		if (YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_BYPASS_OPEN_API)) || "N".equalsIgnoreCase(YFSSystem.getProperty(KohlsConstant.V_BYPASS_OPEN_API))) 
		{
			StringBuffer outputContents = new StringBuffer();
			char[] cbuf = new char[500];
			BufferedReader reader = null;
			HttpClient httpClient = new HttpClient();
			HttpMethod openApiGet = new GetMethod(openAPIItemURL);
			
			// check to see if we need to use a proxy / port combination
			try {
				if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_HOST)) && !YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_PORT))) {
					httpClient.getHostConfiguration().setProxy(
							YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_HOST), Integer.parseInt(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_PROXY_PORT)));
				}
			} catch (Exception ex) {
				// problem with setting proxy, default to the following settings
				httpClient.getHostConfiguration().setProxy("proxy.kohls.com", 3128);
			}
						
			openApiGet.setRequestHeader(KohlsConstant.V_OPEN_API_HEADER_KEY_PARAM, YFSSystem.getProperty(KohlsConstant.V_OPEN_API_KEY));
			
			// set per default
			httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, 
			  new DefaultHttpMethodRetryHandler());
			try {
				httpClient.executeMethod(openApiGet);
				
				/*
				Setting the Connection timeout to input millisecs.
				connection.setConnectTimeout(Integer.parseInt(connectionTimeout));
				connection.setReadTimeout(Integer.parseInt(connectionReadTimeout));
				*/
				reader = new BufferedReader(new InputStreamReader(
						openApiGet.getResponseBodyAsStream()));
				int retVal = 0;
				int currentIteration = 0;
				while(true)	{
					retVal = reader.read(cbuf);
					if(retVal==-1)
						break;
					outputContents.append(String.valueOf(cbuf).substring(0, retVal));
					currentIteration++;
				}
				log.debug(outputContents.toString());
				openApiOutput = outputContents.toString();
			} catch (Exception ex) {
				// problem hitting open API, restort to default
				if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE))) {
					String openAPIImage =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE);
					String altText =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_ALT_TEXT);
					Document images = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_DETAILS);
					Element imagesElement =  images.createElement(KohlsXMLLiterals.E_IMAGES);
					Element imageElement =  images.createElement(KohlsXMLLiterals.E_IMAGE);
					Element urlElement =  images.createElement(KohlsXMLLiterals.E_URL);
					Element altTextElement =  images.createElement(KohlsXMLLiterals.E_ALT_TEXT);
					imagesElement.appendChild(imageElement);
					urlElement.setTextContent(openAPIImage);
					altTextElement.setTextContent(altText);
					imageElement.appendChild(urlElement);
					imageElement.appendChild(altTextElement);
					images.getDocumentElement().appendChild(imagesElement);
					openApiOutput = SCXmlUtil.getString(images);
				}
			}
		} else {
			// we are bypassing, return the system property
			if (!YFSObject.isVoid(YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE))) {
				String openAPIImage =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_RESPONSE);
				String altText =  YFSSystem.getProperty(KohlsConstant.V_OPEN_API_ALT_TEXT);
				Document images = SCXmlUtil.createDocument(KohlsXMLLiterals.E_ITEM_DETAILS);
				Element imagesElement =  images.createElement(KohlsXMLLiterals.E_IMAGES);
				Element imageElement =  images.createElement(KohlsXMLLiterals.E_IMAGE);
				Element urlElement =  images.createElement(KohlsXMLLiterals.E_URL);
				Element altTextElement =  images.createElement(KohlsXMLLiterals.E_ALT_TEXT);
				imagesElement.appendChild(imageElement);
				urlElement.setTextContent(openAPIImage);
				altTextElement.setTextContent(altText);
				imageElement.appendChild(urlElement);
				imageElement.appendChild(altTextElement);
				images.getDocumentElement().appendChild(imagesElement);
				openApiOutput = SCXmlUtil.getString(images);
			}
		}
		return openApiOutput;
	}
	
	public void setProperties(Properties prop) throws Exception {
		this.props = prop;
	}

}