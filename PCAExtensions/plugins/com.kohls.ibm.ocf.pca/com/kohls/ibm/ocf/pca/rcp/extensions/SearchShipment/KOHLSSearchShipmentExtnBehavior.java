package com.kohls.ibm.ocf.pca.rcp.extensions.SearchShipment;

/**
 * Created on Jun 02,2012
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction;
import com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSReprintPackSlipAction;
import com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSReprintPickSlipAction;
import com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentEditorOpenAction;
import com.kohls.ibm.ocf.pca.tasks.printpackslip.actions.KOHLSPrintPackSlipEditorOpenAction;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCJasperReportDefinition;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCWizardExtensionBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfs.japi.YFSException;

/**
 * @author admin © Copyright IBM Corp. All Rights Reserved.
 */
public class KOHLSSearchShipmentExtnBehavior extends YRCWizardExtensionBehavior {

	private static final String WIZARD_ID = "com.yantra.pca.sop.rcp.tasks.outboundexecution.searchshipment.wizards.SOPOBEShipmentSearchWizard";
	public boolean batchPrintErr=false;


	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		// TODO: Write behavior init here.
	}

	public String getExtnNextPage(String currentPageId) {
		// TODO
		return null;
	}

	public IYRCComposite createPage(String pageIdToBeShown) {
		// TODO
		return null;
	}

	public void pageBeingDisposed(String pageToBeDisposed) {
		// TODO
	}

	/**
	 * Called when a wizard page is about to be shown for the first time.
	 * 
	 */
	public void initPage(String pageBeingShown) {
		/*Element eleStatusList=getModel("nmspStatusList_Input");
		System.out.println("sssss111111");
		System.out.println("asa" +YRCXmlUtils.getString(eleStatusList)); */

		Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");
		if(!YRCPlatformUI.isVoid(eleShipmentDetails))
		{
			if(KOHLSPCAUtils.hasPermissionForRecordCustomerPick())
			{
				setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
				disableField("extn_prepcustpick");
				YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", false) ;
				String sStatus =eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_STATUS);
				if(sStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_PICK_LIST_PRINTED))
				{ 
					enableField("extn_prepcustpick");
					setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
					YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", true) ;

				}

				if(sStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_READY_FOR_CUSTOMER))
				{

					enableField("extn_prepcustpick");
					setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_SHIP + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
					YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", true) ;
				} 

			}else
			{
				setControlVisible("extn_prepcustpick", false);
				setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
				disableField("extn_prepcustpick");
				YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", false) ;
			}
		} 

		Document docInputShipmentTypeList=YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT_TYPE_LIST);
		Element eInputShipmentTypeList= docInputShipmentTypeList.getDocumentElement();
		Element ShipmentTypejunk = YRCXmlUtils.createChild(eInputShipmentTypeList,KOHLSPCAConstants.E_SHIPMENT_TYPE);
		ShipmentTypejunk.setAttribute(KOHLSPCAConstants.A_SHIPMENT_TYPE, "");
		try {
			String str = KOHLSPCAConstants.V_SHIPMENT_SEARCH_SHIPMENT_TYPE;
			List<String> elements;  
			elements = Arrays.asList(str.split(";"));  
			for (int k=0 ;k<elements.size();k++)
			{

				Element ShipmentType = YRCXmlUtils.createChild(eInputShipmentTypeList,KOHLSPCAConstants.E_SHIPMENT_TYPE);
				ShipmentType.setAttribute(KOHLSPCAConstants.A_SHIPMENT_TYPE, elements.get(k));
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
		setExtentionModel("extnShipmentTypeList_Input", eInputShipmentTypeList);

		Document docInputgetShipmentStatus=YRCXmlUtils.createDocument(KOHLSPCAConstants.E_STATUS);
		Element eleInputgetShipmentStatus = docInputgetShipmentStatus.getDocumentElement();
		eleInputgetShipmentStatus.setAttribute(KOHLSPCAConstants.A_BASE_PROCESS_TYPE, KOHLSPCAConstants.V_BASE_PROCESS_TYPE_SHIPMENT);
		eleInputgetShipmentStatus.setAttribute(KOHLSPCAConstants.A_CALLING_ORGANIZATION_CODE, YRCPlatformUI.getUserElement().getAttribute(
				KOHLSPCAConstants.A_ENTERPRISE_CODE));
		eleInputgetShipmentStatus.setAttribute(KOHLSPCAConstants.A_DOCUMENT_TYPE, "0001");
		eleInputgetShipmentStatus.setAttribute("IgnoreOrdering", "N");
		eleInputgetShipmentStatus.setAttribute("MaximumRecords", "5000"); 
		//System.out.println("Input for KOHLSGetStatusList" +YRCXmlUtils.getString(docInputgetShipmentStatus)); 
		callApi(KOHLSPCAApiNames.API_KOHLS_GET_STATUS_LIST,docInputgetShipmentStatus); 



	}



	public void handleApiCompletion(YRCApiContext ctx) {
		//System.out.println("handle api " + YRCXmlUtils.getString(ctx.getInputXml()));
		if (ctx.getInvokeAPIStatus() < 1) {
			YRCPlatformUI.trace("API exception in " + getFormId()
					+ " page, ApiName " + ctx.getApiName()
					+ ",Exception : ", ctx.getException());
		}else{


			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_REPRINT_PACK_SLIP_SERVICE)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_REPRINT_PACK_SLIP_SERVICE,
						null);

				if(!YRCPlatformUI.isVoid(KOHLSPCAUtils.getDefaultPrinterID())){
					boolean printed = true;
					Element elePrintPackDocs = ctx.getOutputXml().getDocumentElement();
					NodeList ShipmentList = elePrintPackDocs.getElementsByTagName(KOHLSPCAConstants.E_SHIPMENT);
					for(int k=0;k<ShipmentList.getLength();k++)
					{  try{
						Element eleShipment = (Element) ShipmentList.item(k);
						DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document inputPrintdoc = builder.newDocument();
						Element eleImported = (Element) inputPrintdoc
						.importNode(eleShipment, true);
						inputPrintdoc.appendChild(eleImported);
						KOHLSPCAUtils.jasperRePrintPackSlip(inputPrintdoc,"Container");
					}catch(Exception ex)
					{
						YRCPlatformUI.trace( "Got the following Error While Printing Report" +  KOHLSPCAUtils.getStringTrace(ex));
						printed = false;
					}

					}
					if(printed)
					{
						YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("REPRINT_PACK_SLIP_PRINT_INFO"), new String[]{KOHLSPCAUtils.getDefaultPrinterID()}));
					}else
					{
						YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("REPRINT_PACK_SLIP_PRINT_ERROR_INFO"), new String[]{KOHLSPCAUtils.getDefaultPrinterID()}));
					}

				}




			}


			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_GET_STATUS_LIST)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_KOHLS_GET_STATUS_LIST,
						null);

				Element eOutputGetStatusList= ctx.getOutputXml()
				.getDocumentElement();


				setExtentionModel("extnStatusList_Input", eOutputGetStatusList);

			}

			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_REPRINT_PICK_SLIP_SERVICE)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_REPRINT_PICK_SLIP_SERVICE,
						null);
				//System.out.println("handle api out " + YRCXmlUtils.getString(ctx.getOutputXml()));
				try{

					NodeList nlPrintRePackDoc = ctx.getOutputXml().getElementsByTagName("PrintRePackDoc");

					for (int intPrintRePackCount = 0; intPrintRePackCount < nlPrintRePackDoc
					.getLength(); intPrintRePackCount++) {

						Element elePrintPackDoc = (Element) nlPrintRePackDoc.item(intPrintRePackCount);

						if(!YRCPlatformUI.isVoid(elePrintPackDoc))
						{
							Element elePrintShipment = (Element) elePrintPackDoc.getElementsByTagName(KOHLSPCAConstants.E_SHIPMENT).item(0);
							if(!YRCPlatformUI.isVoid(elePrintShipment))
							{
								DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
								Document inputPrintdoc = builder.newDocument();
								Element eleImported = (Element) inputPrintdoc
								.importNode(elePrintShipment, true);
								inputPrintdoc.appendChild(eleImported);

								KOHLSPCAUtils.jasperRePrintPackSlip(inputPrintdoc,"Shipment");
							}
						}
					}

					Element eOutputChangeShipmentStatus= ctx.getOutputXml()
					.getDocumentElement();

					String sStatus = eOutputChangeShipmentStatus.getAttribute(KOHLSPCAConstants.A_STATUS);
					Element eStatusElem = (Element) eOutputChangeShipmentStatus.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
					String sDescription = eStatusElem.getAttribute(KOHLSPCAConstants.A_DESCRIPTION);
					Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");
					String sOrigStatus =eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_STATUS);
					Element eleOrigStatusElem = (Element) eleShipmentDetails.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
					if(sStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_PICK_LIST_PRINTED) && sOrigStatus.equalsIgnoreCase(KOHLSPCAConstants.V_AWAITING_PICKLIST_PRINT) )
					{
						eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_STATUS,sStatus);
						eleOrigStatusElem.setAttribute(KOHLSPCAConstants.A_DESCRIPTION, sDescription);
						repopulateModel("nmspShipmentDetails_Output");
						enableField("extn_prepcustpick");
						setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
						YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", true) ;
					}


					if(!YRCPlatformUI.isVoid(KOHLSPCAUtils.getDefaultPrinterID())){

						YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("REPRINT_PICK_SLIP_PRINT_INFO"), new String[]{KOHLSPCAUtils.getDefaultPrinterID()}));

					}
				}catch(Exception ex)
				{
					YRCPlatformUI.trace( "Got the following Error While Printing Report" +  KOHLSPCAUtils.getStringTrace(ex));
					YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("REPRINT_PICK_SLIP_PRINT_ERROR_INFO"), new String[]{KOHLSPCAUtils.getDefaultPrinterID()}));
				}

			}

			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS,
						null);

				Element eOutputChangeShipmentStatus= ctx.getOutputXml()
				.getDocumentElement();
				String sStatus = eOutputChangeShipmentStatus.getAttribute(KOHLSPCAConstants.A_STATUS);
				Element eStatusElem = (Element) eOutputChangeShipmentStatus.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
				String sDescription = eStatusElem.getAttribute(KOHLSPCAConstants.A_DESCRIPTION);
				Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");
				Element eleOrigStatusElem = (Element) eleShipmentDetails.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
				if(sStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_PICK_LIST_PRINTED))
				{

					//enableField("extn_prepcustpick");
					setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
					//YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", true) ;
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("SHIPMENT_SHIPPING_INFO"));
					eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_STATUS,sStatus);
					eleOrigStatusElem.setAttribute(KOHLSPCAConstants.A_DESCRIPTION, sDescription);


				}
				if(sStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_READY_FOR_CUSTOMER))
				{    
					//enableField("extn_prepcustpick");
					setFieldValue("extn_prepcustpick", KOHLSPCAConstants.V_BUTTON_PREPARE_FOR_SHIP + "   "+ KOHLSPCAConstants.V_HOT_KEY_PREPARE_CUST_PICK );
					//YRCPlatformUI.enableAction("com.kohls.ibm.ocf.pca.rcp.extensions.shipmentdetails.actions.KOHLSPrepareCustomerPickAction", true) ;
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("SHIPMENT_CUSTOMER_PICK_INFO"));
					eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_STATUS,sStatus);
					eleOrigStatusElem.setAttribute(KOHLSPCAConstants.A_DESCRIPTION, sDescription);

				} 

				repopulateModel("nmspShipmentDetails_Output");


			}

			if(KOHLSPCAApiNames.API_GET_SHIPMENT_DETAILS.equals(ctx.getApiName())){
				YRCApiContext context = new YRCApiContext();
				context.setApiName("KohlsProcessSinglePrintSyncService");
				context.setInputXml(ctx.getOutputXml());
				context.setFormId(getFormId());
				callApi(context);
			}

			if("KohlsProcessSinglePrintSyncService".equals(ctx.getApiName())) {				
				Element eOutputStoreShipmentPickList= ctx.getOutputXml().getDocumentElement();

				try {
//					jasperPrintPickSlip(eOutputStoreShipmentPickList.getOwnerDocument(),"SinglePrint");
					jasperPrintPickSlip(eOutputStoreShipmentPickList.getOwnerDocument(),"SinglePickSlipPrint");

				}catch(Exception e){
					//YRCPlatformUI.showInformation("Pickslip Print",YRCPlatformUI.getString("batch_print_failure"));
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
					throw new YFSException(YRCPlatformUI.getString("batch_print_failure"));
				}
				if(!batchPrintErr && isShipmentStatusChangeRequired()){
					invokeChangeShipmentStatus();
				}
			}

			if(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS_STORE_PICK_SLIP_PRINTED.equals(ctx.getApiName())){

				Element eOutputChangeShipmentStatus= ctx.getOutputXml().getDocumentElement();
				String sStatus = eOutputChangeShipmentStatus.getAttribute(KOHLSPCAConstants.A_STATUS);
				Element eStatusElem = (Element) eOutputChangeShipmentStatus.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
				String sDescription = eStatusElem.getAttribute(KOHLSPCAConstants.A_DESCRIPTION);

				Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");
				Element eleOrigStatusElem = (Element) eleShipmentDetails.getElementsByTagName(KOHLSPCAConstants.E_STATUS).item(0);
				YRCPlatformUI.setMessage(YRCPlatformUI.getString("SHIPMENT_SHIPPING_INFO"));
				eleShipmentDetails.setAttribute(KOHLSPCAConstants.A_STATUS,sStatus);
				eleOrigStatusElem.setAttribute(KOHLSPCAConstants.A_DESCRIPTION, sDescription);

				repopulateModel("nmspShipmentDetails_Output");
			}

		}

		super.handleApiCompletion(ctx);
	}

	@Override
	public boolean preCommand(YRCApiContext ctx) {

		if (YRCPlatformUI.equals(ctx.getApiName(),
				KOHLSPCAApiNames.API_SHP_SEARCH_GET_SHIPMENT_LIST)|| YRCPlatformUI.equals(ctx.getApiName(),
						KOHLSPCAApiNames.API_SHP_SEARCH_GET_SHIPMENT_LIST_NEXT)) {
			Element eleInput=ctx.getInputXml().getDocumentElement();

			/**
			 * Start OASIS 29-OCT-2013
			 * Sort by orderHeaderKey 
			 */
			Element eleOrderBy = YRCXmlUtils.getChildElement(eleInput, "OrderBy");
			Element eleAttribute = YRCXmlUtils.getChildElement(eleOrderBy, "Attribute");
			YRCXmlUtils.setAttribute(eleAttribute, "Name", "OrderHeaderKey");	
			/*
			 * End OASIS 29-OCT-2013
			 */
			/*
			 * Start OASIS 27-NOV-2013 for PMR 85613,379,000
			 * Changing the input to getShipmentList to search shipments between two customer order dates
			 * Here the input dates are converted to  OrderHeaderKey and run the query.
			 */
			String fromCustOrderDate = eleInput.getAttribute("FromOrderHeaderKey");
			String toCustOrderDate = eleInput.getAttribute("ToOrderHeaderKey");
			if( !YRCPlatformUI.isVoid(fromCustOrderDate) && !YRCPlatformUI.isVoid(toCustOrderDate)){
				eleInput.setAttribute("OrderHeaderKeyQryType", "BETWEEN");
				eleInput.setAttribute("FromOrderHeaderKey", fromCustOrderDate.substring(6, 10)+fromCustOrderDate.substring(0, 2)+fromCustOrderDate.substring(3, 5)+"00000000000000");
				eleInput.setAttribute("ToOrderHeaderKey", toCustOrderDate.substring(6, 10)+toCustOrderDate.substring(0, 2)+toCustOrderDate.substring(3, 5)+"23595900000000");

			}

			//End OASIS 27-NOV-2013 for PMR 85613,379,000



			eleInput.setAttribute(KOHLSPCAConstants.A_STATUS_QUERY_TYPE, KOHLSPCAConstants.V_STATUS_QUERY_TYPE);


			if(!YRCPlatformUI.isVoid(eleInput.getAttribute(KOHLSPCAConstants.A_STATUS_FROM_TEMP)))
			{
				eleInput.setAttribute(KOHLSPCAConstants.A_STATUS_FROM, eleInput.getAttribute(KOHLSPCAConstants.A_STATUS_FROM_TEMP));

			}

			if(!YRCPlatformUI.isVoid(eleInput.getAttribute(KOHLSPCAConstants.A_STATUS_TO_TEMP)))
			{
				eleInput.setAttribute(KOHLSPCAConstants.A_STATUS_TO, eleInput.getAttribute(KOHLSPCAConstants.A_STATUS_TO_TEMP));

			}

			try{
				if(!YRCPlatformUI.isVoid(eleInput.getAttribute(KOHLSPCAConstants.A_FORM_REQUESTED_SHIPMENT_DATE)))
				{
					String sFromRequestedShipmentDateOld =eleInput.getAttribute(KOHLSPCAConstants.A_FORM_REQUESTED_SHIPMENT_DATE);
					String sFromRequestedShipmentDateNew =sFromRequestedShipmentDateOld.replaceAll("T00:00:00", "T23:59:59");
					eleInput.setAttribute(KOHLSPCAConstants.A_FORM_REQUESTED_SHIPMENT_DATE, sFromRequestedShipmentDateNew);

				}
			}catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}

		if (YRCPlatformUI.equals(ctx.getApiName(),
				KOHLSPCAApiNames.API_SHP_DTLS_RAISE_EVENT)) {

			Document docInputShpDtlsRaiseEvent = ctx.getInputXml();

			System.out.println(YRCXmlUtils.getString(ctx.getInputXml()));

			String strPrinterID = System
			.getProperty(KOHLSPCAConstants.INI_PROPERTY_PRINTER_ID);

			if (!YRCPlatformUI.isVoid(strPrinterID)
					&& !YRCPlatformUI.isVoid(docInputShpDtlsRaiseEvent)) {

				String strDocInputShpDtlsRaiseEvent = YRCXmlUtils
				.getString(docInputShpDtlsRaiseEvent);

				docInputShpDtlsRaiseEvent = addPrinterIDtoInputDoc(
						strDocInputShpDtlsRaiseEvent, strPrinterID);
				ctx.setInputXml(docInputShpDtlsRaiseEvent);
				//ctx.setApiName(KOHLSPCAApiNames.API_SHP_DTLS_RAISE_EVENT);

			}

		}
		return super.preCommand(ctx);
	}



	private Document addPrinterIDtoInputDoc(
			String strDocInputShpDtlsRaiseEvent, String strPrinterID) {
		// TODO Auto-generated method stub

		Document  document =null;

		String[] parts = strDocInputShpDtlsRaiseEvent.split(
				KOHLSPCAConstants.V_SHIPMENT, 2);

		String strOutputDocument = parts[0] + KOHLSPCAConstants.V_SHIPMENT
		+ KOHLSPCAConstants.V_SPACE + KOHLSPCAConstants.A_PRINTER_ID
		+ "=\'" + strPrinterID + "\'" + parts[1];

		System.out.println(strOutputDocument);


		DocumentBuilderFactory factory =
			DocumentBuilderFactory.newInstance();

		DocumentBuilder builder = null;

		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			document = builder.parse(new InputSource(new StringReader(strOutputDocument)));
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return document;

	}

	/**
	 * Method for validating the text box.
	 * @throws  
	 */
	public YRCValidationResponse validateTextField(String fieldName,
			String fieldValue)   {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		

		// Start OASIS 27-NOV-2013 for PMR 85613,379,000 , logic to validate input dates 
		
		String fldname = fieldName;
		String fldVal = fieldValue;
		boolean isValid = false;
		if(fldname.equals("extn_txtFromCustOrderDate") || fldname.equals("extn_txtToCustOrderDate")){
			isValid = isValidDate(fldVal);


			if(isValid){
				return super.validateTextField(fieldName, fieldValue);
			}
			else
			{
				YRCValidationResponse resObj = new YRCValidationResponse();
				resObj.setStatusMessage("Invalid Date, Please enter a valid Date");
				resObj.setStatusCode(3);
				return resObj;

			}
		}  
		// End OASIS 27-NOV-2013 for PMR 85613,379,000 
		else
		{

			return super.validateTextField(fieldName, fieldValue);
		}

	}

	/**
	 * Method for validating the combo box entry.
	 */
	public void validateComboField(String fieldName, String fieldValue) {
		// TODO Validation required for the following controls.

		// TODO Create and return a response.
		super.validateComboField(fieldName, fieldValue);
	}

	/**
	 * Method called when a button is clicked.
	 */
	public YRCValidationResponse validateButtonClick(String fieldName) {


		// TODO Validation required for the following controls.

		// Control name: extn_PackShipment

		if (fieldName.equals("extn_PackShipment")) {
			YRCPlatformUI
			.fireAction(KOHLSPackShipmentEditorOpenAction.ACTION_ID);
		}

		if (fieldName.equals("extn_pickslip")) {
			YRCPlatformUI
			.fireAction(KOHLSPrintPackSlipEditorOpenAction.ACTION_ID);
		}

		if (fieldName.equals("extn_prepcustpick")) {
			YRCPlatformUI
			.fireAction(KOHLSPrepareCustomerPickAction.ACTION_ID);
		}

		/*
		 * Start OASIS 27-NOV-2013 for PMR 85613,379,000
		 * To Show the calendar on the screen after clicking the button. 
		 */
		if(fieldName.equals("extn_lblButtonFromCustOrderDate")){
			Text extn_fromCustOrderDate = (Text) getControl(this.getOwnerForm(), "extn_txtFromCustOrderDate");
			YRCPlatformUI.showCalendar(extn_fromCustOrderDate);


		}


		//To Show the calendar on the screen after clicking the button. 
		if(fieldName.equals("extn_lblButtonToCustOrderDate")){
			Text extn_fromCustOrderDate = (Text) getControl(this.getOwnerForm(), "extn_txtToCustOrderDate");
			YRCPlatformUI.showCalendar(extn_fromCustOrderDate);


		}


		// Check for from Customer Order Date is less than or equal to To Customer Order Date, else throughs an error message 

		if(fieldName.equals("btnSearch")){
			String frmOrderDate = this.getFieldValue("extn_txtFromCustOrderDate");
			String toOrderDate = this.getFieldValue("extn_txtToCustOrderDate");
			if(!YRCPlatformUI.isVoid(frmOrderDate) && !YRCPlatformUI.isVoid(toOrderDate)){


				int fromDat = Integer.parseInt(frmOrderDate.substring(6, 10)+frmOrderDate.substring(0, 2)+frmOrderDate.substring(3, 5));
				int toDat = Integer.parseInt(toOrderDate.substring(6, 10)+toOrderDate.substring(0, 2)+toOrderDate.substring(3, 5));
				if(toDat < fromDat)
				{
					YRCPlatformUI.showError("Please Enter Valid Dates", "First Date Should be Befor Or Equal to Second Date");

				}

			}
		}
		
		if(fieldName.equals("btnReset")){
			this.setFieldValue("extn_txtFromCustOrderDate","");
			this.setFieldValue("extn_txtToCustOrderDate", "");
		}
		//End OASIS 27-NOV-2013 for PMR 85613,379,000

		//TODO Create and return a response.
		return super.validateButtonClick(fieldName);

	}
	 
	
	/**
	 * @author OASIS
	 * @param composite
	 * @param name
	 * @return the control of the component.
	 * OASIS 27-NOV-2013 for PMR 85613,379,000
	 */
	public static Control getControl(Composite composite, String name) {
		Control[] ctrl = composite.getChildren();
		for(Control object:ctrl){
			if (object instanceof Composite) {
				Composite cmp = (Composite) object;
				Control c = getControl(cmp, name);
				if(!YRCPlatformUI.isVoid(c))
					return c;
			}
			else if(name.equals(object.getData("name")))
				return object;
		}
		return null;
	}
	
	/**
	 * @author OASIS
	 * @param date
	 * @return true if date is valid else false.
	 * OASIS 27-NOV-2013 for PMR 85613,379,000
	 */
	public boolean isValidDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Date testDate = null;
		try
		{
			testDate = sdf.parse(date);
		}
		catch (ParseException e)
		{
			return false;
		}
		if (!sdf.format(testDate).equals(date)){
			return false;
		}

		return true;

	}

	/**
	 * Method called when a link is clicked.
	 */
	public YRCValidationResponse validateLinkClick(String fieldName) {
		// TODO Validation required for the following controls.
		if (fieldName.equals("extn_reprintPackSlip")) {
			YRCPlatformUI
			.fireAction(KOHLSReprintPackSlipAction.ACTION_ID);
		}

		if (fieldName.equals("extn_reprintPickSlip")) {
			YRCPlatformUI
			.fireAction(KOHLSReprintPickSlipAction.ACTION_ID);
		}
		// TODO Create and return a response.
		return super.validateLinkClick(fieldName);
	}


	public void callReprintPackSlip() {

		Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");

		if(!YRCPlatformUI.isVoid(eleShipmentDetails)){

			String strShipmentKey=eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY);	
			Document docInputShipmentDetails=YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
			docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY,strShipmentKey);

			//String strDefaultPrinterID=KOHLSPCAUtils.getDefaultPrinterID();
			//Modified for caching - to send the Default Pack Printer instead of default printer

			String strDefaultPackPrinterID =  KohlsApplicationInitializer.getTerminalPropertyForUISession(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PACK_PRINTER);
			if(!YRCPlatformUI.isVoid(strDefaultPackPrinterID)){

				docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_PRINTER_ID, strDefaultPackPrinterID);
				callApi(KOHLSPCAApiNames.API_REPRINT_PACK_SLIP_SERVICE,docInputShipmentDetails);
			}else{

				YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"), YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
			}


		}




	}

	void callApi(String name, Document inputXml) {
		YRCApiContext context = new YRCApiContext();		
		context.setApiName(name);
		context.setFormId(WIZARD_ID);
		context.setInputXml(inputXml);
		callApi(context);
	}

	public void callReadyForCustomerPick() {
		Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");
		if(!YRCPlatformUI.isVoid(eleShipmentDetails)){

			String strShipmentKey=eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY);	
			Document docInputChangeShipmentStatus=YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
			String sBeforeChangeStatus =eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_STATUS);
			if(sBeforeChangeStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_PICK_LIST_PRINTED))
			{ 
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY,strShipmentKey);
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_BASE_DROP_STATUS,
						KOHLSPCAConstants.V_SHIPMENT_STATUS_READY_FOR_CUSTOMER);
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_TRANSACTION_ID,
						KOHLSPCAConstants.V_TRANSACTION_PREPARE_FOR_CUSTOMER_PICK);
				callApi(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS,docInputChangeShipmentStatus);
			}

			if(sBeforeChangeStatus.equalsIgnoreCase(KOHLSPCAConstants.V_SHIPMENT_STATUS_READY_FOR_CUSTOMER))
			{ 
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY,strShipmentKey);
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_BASE_DROP_STATUS,
						KOHLSPCAConstants.V_SHIPMENT_STATUS_PICK_LIST_PRINTED);
				docInputChangeShipmentStatus.getDocumentElement().setAttribute(KOHLSPCAConstants.A_TRANSACTION_ID,
						KOHLSPCAConstants.V_TRANSACTION_PREPARE_FOR_CUSTOMER_PICK);
				callApi(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS,docInputChangeShipmentStatus);
			}
		}


	}

	public Element getShipmentDetailsModel() {
		// TODO Auto-generated method stub
		return getModel("nmspShipmentDetails_Output");
	}

	public void callReprintPickSlip() {
		Element eleShipmentDetails=getModel("nmspShipmentDetails_Output");

		if(!YRCPlatformUI.isVoid(eleShipmentDetails)){

			String strShipmentKey=eleShipmentDetails.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY);	
			Document docInputShipmentDetails=YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
			docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY,strShipmentKey);
			docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_LABEL_PICK_TICKET,KOHLSPCAConstants.V_Y);
			docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_UPC_BARCODE_PICK_TICKET,KOHLSPCAConstants.V_Y);

			//String strDefaultPrinterID=KOHLSPCAUtils.getDefaultPrinterID();
			String strDefaultPrinterID =  KohlsApplicationInitializer.getTerminalPropertyForUISession(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
			if(!YRCPlatformUI.isVoid(strDefaultPrinterID)){
				docInputShipmentDetails.getDocumentElement().setAttribute(KOHLSPCAConstants.A_PRINTER_ID, strDefaultPrinterID);
				callApi(KOHLSPCAApiNames.API_REPRINT_PICK_SLIP_SERVICE,docInputShipmentDetails);
			}else{

				YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"), YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
			}


		}


	}
//	Drop2- Upadting editor input for EmailID	
	@Override
	public void postSetModel(String model) {
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}
		if("com.yantra.pca.sop.rcp.tasks.outboundexecution.searchshipment.wizardpages.SOPGenericShipmentDetails"==currentPage.getFormId()){




			if("nmspShipmentDetails_Output".equals(model)){

				//Update the EditorInput with the Email-ID information from the "nmspShipmentDetails_Output" model
				Element eShipmentDetails = getModel(model);
				Element eToAddress = YRCXmlUtils.getXPathElement(eShipmentDetails,"/Shipment/ToAddress");
				if(!YRCPlatformUI.isVoid(eToAddress)){
					String strEmail = YRCXmlUtils.getAttribute(eToAddress, KOHLSPCAConstants.A_EAMIL_ID);
					if(!YRCPlatformUI.isVoid(getInputObject())){
						Object inputObj	=getInputObject();
						Element eleInputObj =  (Element)inputObj;
						Element editorInputXML = null;
						YRCXmlUtils.getString(editorInputXML);
						Element eEditorToAddress = YRCXmlUtils.getXPathElement(eleInputObj,"/Shipment/ToAddress");
						if(!YRCPlatformUI.isVoid(eEditorToAddress)){
							YRCXmlUtils.setAttribute(eEditorToAddress, KOHLSPCAConstants.A_EAMIL_ID, strEmail);
						}
					}
				}
			}
		}
/*			else if("com.yantra.pca.sop.rcp.tasks.outboundexecution.searchshipment.wizardpages.SOPGenericShipmentSearch"==currentPage.getFormId()){
				if("nmspShipmentList_Output".equals(model)){
					Element eShipmentList = getModel(model);
					this.repopulateModel("nmspShipmentList_Output");
		
					
					
				}
			}
*/			
	}

	/*
	 * This method will print the Pick Slip for the Store Shipments if the status of the Shipment >= 1100.03
	 *  and not cancelled. Also if Shipment Status = Awaiting Pick List Printed then changeShipmentStatus 
	 *  will be called to change the status to "Pick List Printed".
	 */

	public void printPickSlipForStoreShipment(){
		if(isValidForPickListPrint()){
			invokeGetShipmentDetailsAPI();			
		}
	}

	private boolean isValidForPickListPrint(){
		boolean res = false;
		Element eleShipmentDtls= getShipmentDetailsModel();
		if(!YRCPlatformUI.isVoid(eleShipmentDtls)){
			if(!YRCPlatformUI.isVoid(eleShipmentDtls.getAttribute("Status")) && eleShipmentDtls.getAttribute("Status").compareTo(KOHLSPCAConstants.V_AWAITING_PICKLIST_PRINT) >= 0
					&& !eleShipmentDtls.getAttribute("Status").equals("9000")){
				return true;
			}
			else
			{
				YRCPlatformUI.showError("Error", "Pick slip cannot be printed for the Shipment in this status");
			}
		}
		return res;
	}

	private void invokeGetShipmentDetailsAPI(){		
		Document inDoc = getShipmentDetailsForStorePickSlip();		
		YRCApiContext context = new YRCApiContext();
		context.setApiName(KOHLSPCAApiNames.API_GET_SHIPMENT_DETAILS);
		context.setFormId(getFormId());
		context.setInputXml(inDoc);
		callApi(context);
	}

	private Document getShipmentDetailsForStorePickSlip(){
		Element eleShipmentDtls= getShipmentDetailsModel();

		Document docInputForShipment = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
		Element eleShipmentInput = docInputForShipment.getDocumentElement();

		eleShipmentInput.setAttribute(KOHLSPCAConstants.SEL_ORG_CODE, eleShipmentDtls.getAttribute(KOHLSPCAConstants.SEL_ORG_CODE));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIP_NODE, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIP_NODE));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIPMENT_NO, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIPMENT_NO));

		return docInputForShipment;
	}

	public void jasperPrintPickSlip(Document inDoc,String type)throws Exception {
		JRXmlDataSource ds = null;
		String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
		ds = new JRXmlDataSource(inDoc, selectExpression);

		YRCJasperReportDefinition jrd=KOHLSPCAUtils.getReport(type);
		String reportName=jrd.getFile();

//		String reportName="C:\\Kohls\\jasper\\SinglePickSlipMain.jasper";
		InputStream is = getReportStream(reportName);
		String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
		JasperPrint jp = JasperFillManager.fillReport(is, null, ds);
		PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
//		printServiceAttributeSet.add(new PrinterName(printServiceName.getName(), null));
		printServiceAttributeSet.add(new PrinterName(printServiceName, null));

		JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
//		printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
//		printRequestAttributeSet.add(MediaSizeName.ISO_A4);

		int noOfCopies = 1;
		printRequestAttributeSet.add(new Copies(noOfCopies));
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
		exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
		exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
		exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);

		try
		{
			exporter.exportReport();
			batchPrintErr=false;
			YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_success"));
			//YRCPlatformUI.showInformation("Pickslip Print",YRCPlatformUI.getString("batch_print_success"));
		} catch (Exception e) {
			batchPrintErr=true;
			YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
			// YRCPlatformUI.showInformation("Pickslip Print",YRCPlatformUI.getString("batch_print_failure"));
			// e.printStackTrace();
			// throw new YFSException(YRCPlatformUI.getString("batch_print_failure"));
		}
	}

	protected InputStream getReportStream(String reportName) throws Exception
	{
		InputStream is = getClass().getResourceAsStream(reportName);
		if (is == null) {
			Exception ex = new Exception("Pickslip Report File not found in classpath" + reportName);

			throw ex;
		}
		return is;
	}

	private void invokeChangeShipmentStatus(){
		Element eleShipmentDtls= getShipmentDetailsModel();

		Document docShipment = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_SHIPMENT);
		Element eleShipmentInput = docShipment.getDocumentElement();
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_BASE_DROP_STATUS, "1100.03");
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIP_NODE, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIP_NODE));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIPMENT_NO, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIPMENT_NO));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY, eleShipmentDtls.getAttribute(KOHLSPCAConstants.A_SHIPMENT_KEY));
		eleShipmentInput.setAttribute(KOHLSPCAConstants.A_TRANSACTION_ID, "PICK_LIST_PRINT.0001.ex");

		YRCApiContext context = new YRCApiContext();
		context.setInputXml(docShipment);
		context.setFormId(getFormId());
		context.setApiName(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS_STORE_PICK_SLIP_PRINTED);
		callApi(context);
	}

	private boolean isShipmentStatusChangeRequired(){
		Element eleShipmentDtls= getShipmentDetailsModel();
		if(KOHLSPCAConstants.V_AWAITING_PICKLIST_PRINT.equals(eleShipmentDtls.getAttribute("Status"))){
			return true;
		}
		else{
			return false;
		}
	}



	@Override
	/*
	 * Start OASIS 29-OCT-2013
	 * Logic to generate OrderedDate from OrderHeaderKey
	 * Later this OrderedDate is binded to Customer Order Date
	 */
	public void postCommand(YRCApiContext ctx) {

		if (YRCPlatformUI.equals(ctx.getApiName(),
				KOHLSPCAApiNames.API_SHP_SEARCH_GET_SHIPMENT_LIST)|| YRCPlatformUI.equals(ctx.getApiName(),
						KOHLSPCAApiNames.API_SHP_SEARCH_GET_SHIPMENT_LIST_NEXT)) {
			Element eleInput=ctx.getOutputXml().getDocumentElement();			

			Iterator<Element> itListShipments = YRCXmlUtils.getChildren(eleInput);
			while(itListShipments.hasNext()){
				Element eleShipment = (Element) itListShipments.next();
				String orderHeaderKey = YRCXmlUtils.getAttribute(eleShipment, "OrderHeaderKey");	
				if(!YRCPlatformUI.isVoid(orderHeaderKey))
				{
					String orderDate = orderHeaderKey.substring(4, 6)+"/"+orderHeaderKey.substring(6, 8)+"/"+orderHeaderKey.substring(0, 4);
					YRCXmlUtils.setAttribute(eleShipment, "OrderedDate", orderDate);
				}				

			}



		}
	}
	/*
	 * End OASIS 29-OCT-2013
	 */




}