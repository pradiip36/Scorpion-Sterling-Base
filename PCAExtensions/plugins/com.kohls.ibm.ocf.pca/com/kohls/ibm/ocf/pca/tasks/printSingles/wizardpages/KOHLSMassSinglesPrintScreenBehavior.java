/*
 * Created on Nov 24,2007
 *
 */
package com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages;

import javax.xml.xpath.XPathConstants;

import org.eclipse.swt.widgets.Combo;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSMassSinglesPrintEditor;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * @author Kiran
 */
public class KOHLSMassSinglesPrintScreenBehavior extends
KOHLSMassSinglesPrintScreenBaseBehavior {
	private Object input;

	private KOHLSMassSinglesPrintScreenHelper helper;

	private String formId;

	private boolean flag;


	public KOHLSMassSinglesPrintScreenBehavior(KOHLSMassSinglesPrintScreen parent,
			String formId, Object input, KOHLSMassSinglesPrintScreenHelper helper) {
		super(parent, formId, input);
		this.input = input;
		this.formId = formId;

	}

	public void initPage() {

		callGetDeviceList();

		callMassPrintSinglesService();


	}


	private void callMassPrintSinglesService() {

		Element getMassSinglesShipmentList_input = YRCXmlUtils.createDocument(
		"Shipment").getDocumentElement();
		getMassSinglesShipmentList_input = setInput(getMassSinglesShipmentList_input);
		callApi(KOHLSPCAApiNames.API_GET_MASS_PRINT_SINGLES_LIST, getMassSinglesShipmentList_input.getOwnerDocument());

	}

	private void callGetDeviceList() {
		//		 invoking of the method is no more used. This will be read from the cache which is loaded during
		//		Application initialization		
		//		callApi(KOHLSPCAApiNames.API_GET_DEVICE_LIST, 
		//				getInputForDeviceList(KOHLSPCAConstants.V_PRINTER, 
		//						KOHLSPCAUtils.getCurrentStore()));
		Element elePrinters =	KohlsApplicationInitializer.elePrinterDevices;
		Element elePropertiesList = KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI;
		Element eleProperties = YRCXmlUtils.getChildElement(elePropertiesList, "SIMINIProperties");
		String strDefaultPrinter = eleProperties.getAttribute("DefaultPrinter");
		defaultPrinterID(elePrinters, strDefaultPrinter);
	}
	/**
	 * Methods not being used as the api is not called and the data read from cache
	 */

	//    private  Document getInputForDeviceList(String strDeviceType, String strOrganizationCode){
	//		
	//		Document docDevice = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_DEVICE);
	//		Element eDeviceType = docDevice.getDocumentElement();		
	//		eDeviceType.setAttribute(KOHLSPCAConstants.A_DEVICE_TYPE,strDeviceType);	
	//		eDeviceType.setAttribute(KOHLSPCAConstants.A_ORGANIZATION_CODE,strOrganizationCode);
	//		return docDevice;
	//		
	//	}
	//
	//	void callApi(String name, Document inputXml) {
	//		YRCApiContext context = new YRCApiContext();
	//		context.setApiName(name);
	//		context.setFormId(getFormId());
	//	    context.setInputXml(inputXml);
	//	    callApi(context);
	//	}

	public void handleApiCompletion(YRCApiContext ctx) {

		if (ctx.getInvokeAPIStatus() < 1) {
			YRCPlatformUI.trace("API exception in " + getFormId()
					+ " page, ApiName " + ctx.getApiName()
					+ ",Exception : ", ctx.getException());
		}else{
			//			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_GET_DEVICE_LIST)) {
			//				YRCPlatformUI.trace(
			//						"The command name is "+KOHLSPCAApiNames.API_GET_DEVICE_LIST,
			//						null);
			//				
			//				Element eOutputGetDeviceList= ctx.getOutputXml().getDocumentElement();
			//				
			//				if(!YRCPlatformUI.isVoid(eOutputGetDeviceList)){
			//					
			//				//	String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PRINTER_ID);
			//					String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PICK_PRINTER_ID);
			//					
			//					if(!YRCPlatformUI.isVoid(strPrinterID) && 
			//							!YRCPlatformUI.equals(strPrinterID, "")){
			//					
			//					  defaultPrinterID(eOutputGetDeviceList, strPrinterID);
			//					  
			//					}
			//					
			//				     setModel("getDeviceList",eOutputGetDeviceList);
			//				     
			//				}
			//			
			//			}

			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_GET_MASS_PRINT_SINGLES_LIST)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_GET_MASS_PRINT_SINGLES_LIST,
						null);

				Element eOutputGetItemShipmentListForSinglesPrint = ctx.getOutputXml().getDocumentElement();

				if(!YRCPlatformUI.isVoid(eOutputGetItemShipmentListForSinglesPrint)){

					setModel("PrintPackShipments",eOutputGetItemShipmentListForSinglesPrint);


				}

			}

			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_INVOKE_SINGLES_MASS_PRINT)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_INVOKE_SINGLES_MASS_PRINT,
						null);

				Element eOutputGetItemShipmentListForSinglesPrint = ctx.getOutputXml().getDocumentElement();

				if(!YRCPlatformUI.isVoid(eOutputGetItemShipmentListForSinglesPrint)){

					setModel("PrintPackShipments",eOutputGetItemShipmentListForSinglesPrint);

					repopulateModel("PrintPackShipments");
				}

				String strBatchID=eOutputGetItemShipmentListForSinglesPrint.getAttribute(KOHLSPCAConstants.A_BATCH_ID);
				if(!YRCPlatformUI.isVoid(strBatchID) && !YRCPlatformUI.equals(strBatchID, "")){

					//					String strPrinterID=eOutputGetItemShipmentListForSinglesPrint.getAttribute(KOHLSPCAConstants.A_PRINTER_ID);
					//YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("MULTI_PICK_SLIP_PRINT_INFO"),
					//	new String[]{strPrinterID,strBatchID}));
					YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("RDC_SINGLE_PICK_SLIP_PRINT_INFO"),
							new String[]{strBatchID}));


				}

			}

		}

	}


	private void defaultPrinterID(Element outputGetDeviceList, String strPrinterID ) {

		NodeList nOutputPrinterList = outputGetDeviceList
		.getElementsByTagName(KOHLSPCAConstants.E_DEVICE);

		if (!YRCPlatformUI.isVoid(nOutputPrinterList)
				&& nOutputPrinterList.getLength() > 1) {

			//rearrangePrinterID(outputGetDeviceList, strPrinterID);
			KOHLSPCAUtils.rearrangePrinterID(outputGetDeviceList, strPrinterID);
			setModel("getDeviceList",outputGetDeviceList);

		}

	}
	/**
	 * Method moved to KOHLSPCAUtils
	 * @param outputGetDeviceList
	 * @param strPrinterID
	 */
	private void rearrangePrinterID(Element outputGetDeviceList, String strPrinterID) {
		Element eDevice = (Element) YRCXPathUtils.evaluate(outputGetDeviceList,
				"/Devices/Device[@DeviceId='"
				+ strPrinterID + "']", XPathConstants.NODE);

		if (!YRCPlatformUI.isVoid(eDevice)) {

			Node previousNode = eDevice.getPreviousSibling();
			while (previousNode.getPreviousSibling() != null
					&& !(previousNode == eDevice)) {
				previousNode = previousNode.getPreviousSibling();
			}
			if (previousNode.getPreviousSibling() == null) {
				try {

					if (!previousNode.isSameNode(eDevice)) {
						eDevice = (Element) outputGetDeviceList
						.removeChild(eDevice);
						outputGetDeviceList.insertBefore(eDevice, previousNode);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		} else {
			YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),
					YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
		}


	}

	/**
	 * This Method is called by action for button. It checks for the radio button selection. Based on that it creates
	 * the input for the shipments to be printed. 
	 */
	public void doPrint() {

		Element elePrinter=getTargetModel("PrinterID");

		String strPrinterID;

		if(!YRCPlatformUI.isVoid(elePrinter)){

			strPrinterID=elePrinter.getAttribute(KOHLSPCAConstants.A_PRINTER_ID);

			if(!YRCPlatformUI.isVoid(strPrinterID)){

				if(isValidInput(getModel("PrintPackShipments"))){

					Element eleInputForSinglesPrint=getCheckedSinglesForPrint(getModel("PrintPackShipments"), strPrinterID);
					NodeList nlPrintPackShipments = eleInputForSinglesPrint.getElementsByTagName(KOHLSPCAConstants.E_PRINT_PACK_SHIPMENT);

					if((!YRCPlatformUI.isVoid(nlPrintPackShipments))&& nlPrintPackShipments.getLength() > 0){



						callApi(KOHLSPCAApiNames.API_INVOKE_SINGLES_MASS_PRINT, eleInputForSinglesPrint.getOwnerDocument());

					}else {

						YRCPlatformUI.showError(YRCPlatformUI.getString("Select_Singles"),YRCPlatformUI.getString("Select_Singles_To_Print"));
						return;
					}
				}


			}
			else {

				YRCPlatformUI.showError(YRCPlatformUI.getString("Printer_Missing"),YRCPlatformUI.getString("Select_Printer"));
				return;
			}

		}else {
			YRCPlatformUI.showError(YRCPlatformUI.getString("Printer_Missing"),YRCPlatformUI.getString("Select_Printer"));
			return;
		}

	}

	private Element createInputForSinglesPrint() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * This Method is called by close button fucntion. It closes the page.
	 */
	public void performClose() {

		YRCPlatformUI.closeEditor(KOHLSMassSinglesPrintEditor.EDITOR_ID, true);
	}

	/**
	 * This Method is used to set the inputs for the attributes which are common to input criteria for all 
	 * search methods for this feature.
	 */
	public Element setInput(Element inDoc) {

		YRCXmlUtils.setAttribute(inDoc, "ShipNode", KOHLSPCAUtils.getCurrentStore());
		return inDoc;
	}

	/**
	 * This Method is used create the list of PrintPackShipment which are checked in the table to be printed.
	 * It checks the attribute checked in the PrintPackShipments output model which is set as the 
	 * target binding of the checkbox.
	 * This method is called while creating input for KOHLSInvokeSinglesMassPrintAPI call, only when the there are PrintPackShipment
	 * listed in the table.
	 */
	private Element getCheckedSinglesForPrint(Element elePrintPackShipmentList, String strPrinterID) {
		NodeList nlPrintPackShipments = elePrintPackShipmentList.getElementsByTagName(KOHLSPCAConstants.E_PRINT_PACK_SHIPMENT);
		int counter=0;
		int length = nlPrintPackShipments.getLength();
		Element checkedPrintPackShipments = YRCXmlUtils.createDocument( KOHLSPCAConstants.E_PRINT_PACK_SHIPMENTS)
		.getDocumentElement();



		checkedPrintPackShipments.setAttribute(KOHLSPCAConstants.A_SHIP_NODE, KOHLSPCAUtils.getCurrentStore());
		checkedPrintPackShipments.setAttribute(KOHLSPCAConstants.A_PRINTER_ID, strPrinterID);

		for (int j = 0; j < length; j++) {
			Element elePrintPackShipment = (Element) nlPrintPackShipments.item(j);

			String checked = YRCXmlUtils.getAttribute(elePrintPackShipment, "Checked");
			if ((!(YRCPlatformUI.isVoid(checked)) && checked.equals("Y")))
			{
				elePrintPackShipment.setAttribute(KOHLSPCAConstants.A_PRINTER_ID, strPrinterID);
				elePrintPackShipment.setAttribute(KOHLSPCAConstants.A_SHIP_NODE, KOHLSPCAUtils.getCurrentStore());
				elePrintPackShipment.setAttribute(KOHLSPCAConstants.A_PRINT_TYPE, KOHLSPCAConstants.V_PRINT_TYPE_SINGLE);
				YRCXmlUtils.importUnderTargetElement(checkedPrintPackShipments, elePrintPackShipment,KOHLSPCAConstants.E_PRINT_PACK_SHIPMENT, true);

				counter++;
				YRCXmlUtils.setAttribute(checkedPrintPackShipments, KOHLSPCAConstants.A_TOTAL_NUMBER_OF_RECORDS, Integer.toString(counter));
			}
		}


		return checkedPrintPackShipments;
	}

	public boolean isValidInput(Element elePrintPackShipmentList){

		boolean flag=true;

		NodeList nlPrintPackShipments = elePrintPackShipmentList.getElementsByTagName("PrintPackShipment");
		int length = nlPrintPackShipments.getLength();

		for (int j = 0; j < length; j++) {
			Element elePrintPackShipment = (Element) nlPrintPackShipments.item(j);

			String strTotalShipments = YRCXmlUtils.getAttribute(elePrintPackShipment, "TotalShipments");
			String checked = YRCXmlUtils.getAttribute(elePrintPackShipment, "Checked");

			if((!(YRCPlatformUI.isVoid(checked)) && checked.equals("Y")) ){
				if ( checkIfNumber(strTotalShipments))
				{
					int intAvailableQuantity = YRCXmlUtils.getIntAttribute(elePrintPackShipment,"AvailableQuantity");
					int intTotalShipments = YRCXmlUtils.getIntAttribute(elePrintPackShipment,"TotalShipments");
					if(intTotalShipments <= intAvailableQuantity){	

						/* Start OASIS 07-FEB-2014 
						 * Logic to check cap value ( maximum shipments allowed) for Print Mass Singles.
						 */
						if(!(YRCPlatformUI.isVoid(YRCPlatformUI.getString("Max_Num_Of_Prnt_Quan"))) && ( intTotalShipments <= (Integer.parseInt(YRCPlatformUI.getString("Max_Num_Of_Prnt_Quan"))))){
							continue;
						}
						else
						{
							String errorBody = YRCPlatformUI.getString("Error_Details1") + YRCPlatformUI.getString("Max_Num_Of_Prnt_Quan") + YRCPlatformUI.getString("Error_Details2") + YRCPlatformUI.getString("Max_Num_Of_Prnt_Quan");
							YRCPlatformUI.showError(YRCPlatformUI.getString("Error_Title"),errorBody);

							return false;

						}



					}else{

						YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_PRINT_QTY_INPUT"));
						
						// End OASIS 07-FEM-2014

						return false;
					}

				}else {


					return false;
				}
			}
		}

		return flag;
	}


	public void setLayoutRefresh() {
		parent.layout(true, true);

	}

	/**
	 * Validates if input String is a number
	 */
	public boolean checkIfNumber(String in) {

		try {

			int i= Integer.parseInt(in);

			if(i < 1) return false ;

		} catch (NumberFormatException ex) {
			return false;
		}

		return true;
	}

	public void setIsCellInError(boolean boolIsCellInError){
		flag=boolIsCellInError;
	}

	public  boolean getIsCellInError(){
		return flag;
	}


	/*  String getTimeStampForPrintBatchNo(){
    Calendar cal = Calendar.getInstance();
    DateFormat dateFormat = new SimpleDateFormat(KOHLSPCAConstants.V_DATE_TIME_FORMAT);
    return dateFormat.format(cal.getTime());
    }*/

	public void doRefresh() {
		callMassPrintSinglesService();

	}

	public void handleComboBoxSelection(Combo cmbPrinter) {
		String strDefaultPrinter = "DefaultPrinter";
		KohlsApplicationInitializer.modifyTerminalPropertyForUISession(strDefaultPrinter,cmbPrinter.getText());

	}

}
