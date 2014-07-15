package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.PrinterName;

import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.rcp.editors.KOHLSStorePickSlipPrintEditor;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.IYRCDisposable;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.YRCFileUtils;
import com.yantra.yfc.rcp.YRCJasperReportDefinition;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardPageBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfs.japi.YFSException;


/*
 * This Behavior class calls various methods to perform the actions such as:
 * 1. Getting the List of the Shipents which are in the "Awaiting Pick List Print" status
 * 2. Refreshing the screen for new data
 * 3. Print the Pick list for selected shipments
 * 		- Allowes to select one or more shipments
 * 4. Printing the Pick list for bulk shipments
 * 		- Will accept the Quantity configured in the Bundle file for "Max_Qty_For_Pick_Slip_Print"
 * 			i.e. Max No of Shipments to be included in the Bulk Print
 */
public class KOHLSStorePickSlipPrintPageBehavior extends YRCWizardPageBehavior implements KOHLSPCAConstants {
	
	private Object input ;
	protected KOHLSStorePickSlipPrintPage parent;
	protected KOHLSPickSlipsBulkPrintPopup pBulkPrintPopupPage = null;
	protected YRCDialog yrcPopupPageDialog = null;
	private final String strDefaultPrinter = KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER;
	public static String WIZARD_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.wizards.KohlsStorePickSlipPrintWizard";  
	Element eleShipment = null;
	Element eShipmentsSelectedForPrint = null;
	Element eleShipmentnew = null;
	Document docInputForShipment = null;
	Element eleShipmentInput = null;
	private boolean batchPrintErr=false;

	
	public KOHLSStorePickSlipPrintPageBehavior(KOHLSStorePickSlipPrintPage parent, String FORM_ID, Object input) {
		super(parent);
		this.parent = parent;
	
		this.input = input;
	}
	public void initPage() {
		super.init();
		setPrinterList();
	}
	
	/*
	 * This method is called when task is launched first time to get the list of all the shipments
	 * which are in the "Awaiting Pick List Print" status
	 * Input XML
	 * 	<Shipment ShipNode="801" Status="1100.03"/>
	 * 
	 * OutPut XML
	 * 	<Shipments>
			<Shipment ShipNode="" EnterpriseCode="" Status="" ShipDate="" ShipmentType="" TotalQuantity="" ShipmentNo="" ShipmentKey="">
				<ShipmentLines>
					<ShipmentLine ShipmentLineKey="" ItemID="" UnitOfMeasure="" OrganizationCode="" ProductClass="" OrderNo="" 
					OrderHeaderKey="" OrderLineKey="" PrimeLineNo="" ReleaseNo="" ShipmentLineNo="" ShipmentSubLineNo="" SubLineNo="">
						<OrderLine OrderLineKey="" PrimeLineNo="" SubLineNo="">
							<ItemDetails ItemID="" UnitOfMeasure="" OrganizationCode="" ProductClass="">
								<Extn ExtnClass=""  ExtnDept=""  ExtnSubClass=""/>
							</ItemDetails>
						</OrderLine>
					</ShipmentLine>
				</ShipmentLines>
			</Shipment>
		</Shipments>
	 */
	public void invokeGetShipmentList(){
		Document inXML = getInputXML(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT);
		YRCApiContext ctx = new YRCApiContext();
		ctx.setApiName(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT);
		ctx.setFormId(WIZARD_ID);
		ctx.setInputXml(inXML);
		callApi(ctx);
	}
	
	public void invokeGetShipmentListWithoutHeaderRePopulate(){
		Document inXML = getInputXML(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT);
		YRCApiContext ctx = new YRCApiContext();
		ctx.setUserData("RefreshHeader", "N");
		ctx.setApiName(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT);
		ctx.setFormId(WIZARD_ID);
		ctx.setInputXml(inXML);
		callApi(ctx);
	}
	
	/*
	 * prepares the input to get the shipments in "Awaiting Pick List Print" status
	 */
	public Document getInputXML(String strCommandName){
		Element eInXML = null;
		if(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT.equals(strCommandName)){
			eInXML = YRCXmlUtils.createDocument(E_SHIPMENT).getDocumentElement();
			YRCXmlUtils.setAttribute(eInXML, A_SHIP_NODE, KOHLSPCAUtils.getCurrentStore());
			//YRCXmlUtils.setAttribute(eInXML, A_ENTERPRISE_CODE, KOHLSPCAUtils.getEnterpriseCodeForStoreUser());
			YRCXmlUtils.setAttribute(eInXML, A_STATUS, A_STATUS_AWAIT_PICK_LIST_PRINT);
		}
		return eInXML.getOwnerDocument();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.yantra.yfc.rcp.YRCBaseBehavior#handleApiCompletion(com.yantra.yfc.rcp.YRCApiContext)
	 * Will set the model once the list of the shipments are availbale
	 */
	@Override
	public void handleApiCompletion(YRCApiContext ctx) {
		setPrinterList();
		if(ctx.getInvokeAPIStatus()>=0){
	
	if(KOHLSPCAApiNames.API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT.equals(ctx.getApiName())){
		
		
				//Start OASIS 13-NOV-2013 for PMR 85613,379,000
					Element eleInput = ctx.getOutputXml().getDocumentElement();
					Iterator<Element> shipList = YRCXmlUtils.getChildren(eleInput);
					while(shipList.hasNext()){
						Element eleShipment = (Element) shipList.next();
						Element shipLine = YRCXmlUtils.getChildElement((YRCXmlUtils.getChildElement(eleShipment, "ShipmentLines")), "ShipmentLine");
						String orderHeaderKey = YRCXmlUtils.getAttribute(shipLine, "OrderHeaderKey");	
						if(!YRCPlatformUI.isVoid(orderHeaderKey))
						{
						String orderDate = orderHeaderKey.substring(4, 6)+"/"+orderHeaderKey.substring(6, 8)+"/"+orderHeaderKey.substring(0, 4);
						YRCXmlUtils.setAttribute(eleShipment, "OrderedDate", orderDate);
						}				
						
					}
					
				// End OASIS 13-NOV-2013 for PMR 85613,379,000
					
					setModel(M_SHIPMENTS_AWAIT_PICK_LIST_PRINT, ctx.getOutputXml().getDocumentElement());
					if(!"N".equals(ctx.getUserData("RefreshHeader"))){
					if(YRCPlatformUI.isVoid(YRCXmlUtils.getFirstChildElement(ctx.getOutputXml().getDocumentElement()))){
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("Kohls_Message_No_Shipment_awaiting_Pick_Slip_Print"));
					}
					else{
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("Kohls_Select_Shipment_To_Print_Pick_Slip"));
					}
					}
			}
		}
		
		//for bulk print
		
		if (ctx.getApiName().equals(
				KOHLSPCAApiNames.API_KOHLS_GENERATE_AND_STAMP_BATCH_NO_AT_SHIMENT_LINE)) {
			if (ctx.getInvokeAPIStatus() > 0) {
					yrcPopupPageDialog.close();
					Element eleOutput = ctx.getOutputXml().getDocumentElement();
					String extnbatchNo=	eleOutput.getAttribute("ExtnBatchNo");
					//YRCPlatformUI.showInformation("Information", "The Selected Shipments have been sent for printing");
					if(null != extnbatchNo && !(extnbatchNo.trim().equals(""))){
						//YRCPlatformUI.showInformation("Information", "The Selected Shipments have been sent for printing and BatchNo is "+extnbatchNo);
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("The Selected Shipments have been sent for printing and BatchNo is " +extnbatchNo));
						invokeChangeShipmentForBulk(eleOutput);
					}
					else
					{
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
					}
		}
			else
			{
				YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
			}
		}
		if (ctx.getApiName().equals(
				KOHLSPCAApiNames.API_KOHLS_CHANGESHIPMENT_FOR_BULK)) {
						if (ctx.getInvokeAPIStatus() > 0) {
					//YRCPlatformUI.showInformation("Information", "Batch is ready for printing");
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch is ready for printing"));
					autoRefresh();
						}
						else
						{
							YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
						}
				}

		//for multipleshipments
		
		if (ctx.getApiName().equals(
				KOHLSPCAApiNames.API_PICK_SLIP_PRINT_FOR_MULTI)){
			if (ctx.getInvokeAPIStatus() > 0) {
				Element eleOutput = ctx.getOutputXml().getDocumentElement();
				String extnbatchNo=	eleOutput.getAttribute("ExtnBatchNo");
				if(null != extnbatchNo && !(extnbatchNo.trim().equals(""))){
					//YRCPlatformUI.showInformation("Information", "The Selected Shipments have been sent for printing and BatchNo is "+extnbatchNo);
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("The Selected Shipments have been sent for printing and BatchNo is "+extnbatchNo));
					invokeChangeShipment(eleOutput);
				}
				else
				{
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
				}
				
			}
			else
			{
				YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
			}
		}
		
		
			//change shipment for both 
			if(KOHLSPCAApiNames.API_KOHLS_CHANGE_SHIPMENT_FOR_MULTI.equals(ctx.getApiName())){
					
					if (ctx.getInvokeAPIStatus() > 0) {
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch is ready for printing"));
						autoRefresh();
					}else
					{
						YRCPlatformUI.setMessage(YRCPlatformUI.getString("Batch creation failed in Sterling."));
					}
			}
		

		if(KOHLSPCAApiNames.API_GET_SHIPMENT_DETAILS.equals(ctx.getApiName())){
			YRCApiContext context = new YRCApiContext();
			Element eleInput = ctx.getOutputXml().getDocumentElement();
			//Document docinput = YRCXmlUtils.createDocument("Shipment");
			Document docinput = YRCXmlUtils.createDocument("Shipment");
			Element eleShipmentlines = docinput.createElement("ShipmentLines");
			//Element shipmentLineEle = YRCXmlUtils.createChild(eleShipmentlines, "ShipmentLine");
			
			Element eleShipmentLines = YRCXmlUtils.getChildElement(eleInput,A_SHIPMENT_LINES);
			Element eleShipmentLine = YRCXmlUtils.getFirstChildElement(eleShipmentLines);
			Element eleinput = docinput.getDocumentElement();
			
			YRCXmlUtils.importElement(eleinput, eleShipmentLine);
			//context.setApiName(KOHLSPCAApiNames.API_STORE_PICK_SLIP);
			context.setApiName("KohlsProcessSinglePrintSyncService");
			context.setInputXml(eleInput.getOwnerDocument());
			context.setFormId(getFormId());
			callApi(context);
		}
		if("KohlsProcessSinglePrintSyncService".equals(ctx.getApiName())) {
			
			Element eOutputGetBatchStorePrintList= ctx.getOutputXml().getDocumentElement();
			
			try {

				//jasperPrintPickSlip(eOutputGetBatchStorePrintList.getOwnerDocument(),"SinglePrint");
				jasperPrintPickSlip(eOutputGetBatchStorePrintList.getOwnerDocument(),"SinglePickSlipPrint");
				
				
				/*Map<String, String> parameters = new HashMap<String, String>();
				parameters.put("HBC_IMG_SRC", KOHLSStorePickSlipPrintPageBehavior.class.getResource("/icons").toString());
				parameters.put("HBC_JASPER_SRC",KOHLSStorePickSlipPrintPageBehavior.class.getResource("/resources/reports").toString());
				KohlesPrintReportAPI printApi = new KohlesPrintReportAPI();
	
				String url = printApi.executeReport("/StoreShipments",eOutputGetBatchStorePrintList,"SinglePrint", parameters);
				printApi.openReport(url);*/
			
			}catch(Exception e){
				//YRCPlatformUI.showInformation("Pickslip Print",YRCPlatformUI.getString("batch_print_failure"));
				YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
	        	  throw new YFSException(YRCPlatformUI.getString("batch_print_failure"));
			}
			if(!batchPrintErr)	{
				Document docShipment = YRCXmlUtils.createDocument(E_SHIPMENT);
				Iterator iterate  = YRCXmlUtils.getChildren(eShipmentsSelectedForPrint);
				while(iterate.hasNext()){
					Element eleShipment  = (Element) iterate.next();
					Element eleShipmentInput = docShipment.getDocumentElement();
					eleShipmentInput.setAttribute(A_BASE_DROP_STATUS, "1100.03");
					eleShipmentInput.setAttribute(A_SHIP_NODE, eleShipment.getAttribute(A_SHIP_NODE));
					eleShipmentInput.setAttribute(A_SHIPMENT_NO, eleShipment.getAttribute(A_SHIPMENT_NO));
					eleShipmentInput.setAttribute(A_SHIPMENT_KEY, eleShipment.getAttribute(A_SHIPMENT_KEY));
					eleShipmentInput.setAttribute(A_TRANSACTION_ID, "PICK_LIST_PRINT.0001.ex");
				}
					YRCApiContext context = new YRCApiContext();
					context.setInputXml(docShipment);
					context.setFormId(getFormId());
					context.setApiName(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS);
					callApi(context);
				}
			}
		
			if(KOHLSPCAApiNames.API_CHANGE_SHIPMENT_STATUS.equals(ctx.getApiName())){
				if (ctx.getInvokeAPIStatus() > 0) {
					autoRefresh();
					}
			}
				else{
					YRCPlatformUI.trace("Exception from " + getFormId() + " for API:: " + ctx.getApiName());
				}
			}
	
			private void invokeChangeShipmentForBulk(Element eleOutput) {
				
				YRCApiContext context = new YRCApiContext();
				context.setApiName("KOHLSChangeShipmentForBulkPrint");
				context.setInputXml(eleOutput.getOwnerDocument());
				context.setFormId(getFormId());
				callApi(context);
			}
			private void invokeChangeShipment(Element eleOutput) {
				
					YRCApiContext context = new YRCApiContext();
					context.setApiName("KOHLSChangeShipmentForPickSlip");
					context.setInputXml(eleOutput.getOwnerDocument());
					context.setFormId(getFormId());
					callApi(context);
			}


	public void jasperPrintPickSlip(Document inDoc,String type)throws Exception {
		JRXmlDataSource ds = null;
		String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
		 ds = new JRXmlDataSource(inDoc, selectExpression);
		 
		 YRCJasperReportDefinition jrd=KOHLSPCAUtils.getReport(type);
		 String reportName=jrd.getFile();
		 InputStream is = getReportStream(reportName);
//		 String reportName="C:\\Kohls\\jasper\\SinglePickSlipMain.jasper";
		 
//		 PrintService printServiceName=PrintServiceLookup.lookupDefaultPrintService();
   	  	 String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
		 JasperPrint jp = JasperFillManager.fillReport(is, null, ds);
		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
//	        printServiceAttributeSet.add(new PrinterName(printServiceName.getName(), null));
	        printServiceAttributeSet.add(new PrinterName(printServiceName, null));
	        
	        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
//	        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
//	        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
	        
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
	            YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_success_message"));
	          } catch (Exception e) {
	        	  batchPrintErr=true;
	        	  YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
	        	  //YRCPlatformUI.showInformation("Pickslip Print",YRCPlatformUI.getString("batch_print_failure"));
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
	
	/*
	 * Will get the list of the Printers configured on the terminal from the 
	 * KohlsApplicationInitializer and will set the model "getDeviceList" on
	 * screen.
	 * Before Setting the model on the screen - the first printer in the model
	 * will be the Default Printer to be used on this Screen.
	 * The value of the Default Printer to be used, will be picked from the 
	 * KohlsApplicationInitializer.
	 */
	public void setPrinterList(){
		
		String strDefaultPrinterID = KohlsApplicationInitializer.getTerminalPropertyForUISession(strDefaultPrinter);
		KOHLSPCAUtils.rearrangePrinterID(KohlsApplicationInitializer.elePrinterDevices, strDefaultPrinterID);
		setModel("getDeviceList",KohlsApplicationInitializer.elePrinterDevices,true);
		
		/*PrintService[] printService = KohlsApplicationInitializer.printServices;
		if(YRCPlatformUI.isVoid(printService) || printService.length==0){
			YRCPlatformUI.trace("No Printer Configured for this Terminal");
		}
		else{
			Element eDevices = YRCXmlUtils.createDocument("Devices").getDocumentElement();
			Element eDevice = null;
			for(PrintService printer : printService){
				eDevice = YRCXmlUtils.createChild(eDevices, "Device");
				String strPrinterName = printer.getName();
				AttributeSet printerAttrSet = (PrintServiceAttributeSet)printer.getAttributes();				
				YRCXmlUtils.setAttribute(eDevice, "DeviceId", strPrinterName);
			}
			setModel("getDeviceList",eDevices,true);
		}*/
	}
	
	/**
     * This Method is called by "Print Pick Slips" Or F9 Key fucntion. It closes the page.
     * This function evaluates if the Single/Multiple shipment will be used for Pick slip print.
     */
	public void printPickSlips(){
		YRCPlatformUI.trace("Starting::" + this.getFormId() + "printPickSlips()");
		String strSelectionMode="";
		Element eShipments = getModel(M_SHIPMENTS_AWAIT_PICK_LIST_PRINT);
		if(YRCPlatformUI.isVoid(eShipments)){
			YRCPlatformUI.showInformation(YRCPlatformUI.getString("Kohls_Title_No_Shipment_Found"), 
					YRCPlatformUI.getString("Kohls_Message_No_Shipment_awaiting_Pick_Slip_Print"));			
			return;
		}
		else{
			Iterator itrShipments = YRCXmlUtils.getChildren(eShipments);
			int intNoOfShipmentsSelected = 0;
			 eShipmentsSelectedForPrint = YRCXmlUtils.createDocument("Shipments").getDocumentElement();
			Element eShipment = null;
			while(itrShipments.hasNext()){
				eShipment = (Element)itrShipments.next();
				if("Y".equalsIgnoreCase(YRCXmlUtils.getAttribute(eShipment, "Checked"))){
					YRCXmlUtils.importElement(eShipmentsSelectedForPrint, eShipment);
					
					intNoOfShipmentsSelected++;
				}
			}
			if(intNoOfShipmentsSelected == 1){
				docInputForShipment = YRCXmlUtils.createDocument(E_SHIPMENT);
				eleShipmentInput = docInputForShipment.getDocumentElement();
				eleShipmentInput.setAttribute("OrganizationCode", "");
				Element eleShip = YRCXmlUtils.getChildElement(eShipmentsSelectedForPrint,E_SHIPMENT);
				eleShipmentInput.setAttribute("ExtnShipmentDepartment", eleShip.getAttribute("ExtnShipmentDepartment"));
				eleShipmentInput.setAttribute(SEL_ORG_CODE, eleShip.getAttribute(SEL_ORG_CODE));
				eleShipmentInput.setAttribute(A_SHIP_NODE, eleShip.getAttribute(A_SHIP_NODE));
				eleShipmentInput.setAttribute(A_SHIPMENT_KEY, eleShip.getAttribute(A_SHIPMENT_KEY));
				eleShipmentInput.setAttribute(A_SHIPMENT_NO, eleShip.getAttribute(A_SHIPMENT_NO));
			}
			else
			{
				Iterator iterateShipments = YRCXmlUtils.getChildren(eShipmentsSelectedForPrint);
				while(iterateShipments.hasNext()){
					Element shipmentEle = (Element) iterateShipments.next();
					Element eleExtn = YRCXmlUtils.createChild(shipmentEle, "Extn");
					Element elementExtn = YRCXmlUtils.getChildElement(shipmentEle, "Extn");
					eleExtn.setAttribute("Extn_BatchID", elementExtn.getAttribute("Extn_BatchID"));
				}
				
			}
			//setModel("ShipmentsSelectedForPrint",eShipmentsSelectedForPrint);
			switch(intNoOfShipmentsSelected){
			case 0:
				YRCPlatformUI.showInformation("Kohls_No_Shipment_selected", "Kohls_Message_select_least_one_shipment_to_print_pick_slip");
				YRCPlatformUI.setMessage(YRCPlatformUI.getString("Kohls_Message_select_least_one_shipment_to_print_pick_slip"));
				return ;
			case 1:
				strSelectionMode="Single";	
				break;
			default :
				strSelectionMode="Multi";
				break;
			}
		}
		YRCApiContext context = new YRCApiContext();
		if (strSelectionMode.equals("Single")){
			context.setApiName(KOHLSPCAApiNames.API_GET_SHIPMENT_DETAILS);
			context.setFormId(getFormId());
			context.setInputXml(docInputForShipment);
			callApi(context);
		}
		else if(strSelectionMode.equals("Multi")){
			context.setApiName(KOHLSPCAApiNames.API_PICK_SLIP_PRINT_FOR_MULTI);
			context.setFormId(getFormId());
			Element eleUSerNameSpace = getModel("UserNameSpace");
			String node = eleUSerNameSpace.getAttribute("Node");
			eShipmentsSelectedForPrint.setAttribute("Node", node);
			context.setInputXml(eShipmentsSelectedForPrint.getOwnerDocument());
			
			callApi(context);
			//CallBack handler implementation has been removed and moved to handleAPICompletion
			//YRCPlatformUI.callApi(context, new IYRCApiCallbackhandler() {
			//public void handleApiCompletion(YRCApiContext ctx) {
//				if (ctx.getApiName().equals(
//						KOHLSPCAApiNames.API_PICK_SLIP_PRINT_FOR_MULTI)) {
//					if (ctx.getInvokeAPIStatus() > 0) {
//						Element eleOutput = ctx.getOutputXml().getDocumentElement();
//						String extnbatchNo=	eleOutput.getAttribute("ExtnBatchNo");
//						//YRCPlatformUI.showInformation("Information", "The Selected Shipments have been sent for printing");
//						if(null != extnbatchNo && !(extnbatchNo.trim().equals(""))){
//							YRCPlatformUI.showInformation("Information", "The Selected Shipments have been sent for printing and BatchNo is "+extnbatchNo);
//						}
//						return;
//					}
//				}
//			}
//					});
		
		}
	}
	
	/**
     * This Method is called by close button Or Esc Key fucntion. It closes the page.
     */
    public void performClose() {
    	YRCPlatformUI.setMessage(YRCPlatformUI.getString(""));
    	YRCPlatformUI.closeEditor(KOHLSStorePickSlipPrintEditor.EDITOR_ID, true);
    }
	
    /**
     * This Method is called by "Refresh" button Or F5 Key fucntion. It refreshes the page.
     */
    public void performRefresh(){
    	YRCPlatformUI.showInformation("Store Pick Slip Print", "Refreshing Screen");
    	invokeGetShipmentList();
    }
    
    public void autoRefresh(){
    	invokeGetShipmentListWithoutHeaderRePopulate();
    }
    
    /**
     * This Method is called when user enters the valid "Qty To Print" for bulk print
     * and clicks on the print button on the "Pick Slips - Bulk Print" popup page.
     */
    public void performBulkPrint(Element eBulkPrintData){
    	String strQtyToPrint = YRCXmlUtils.getAttribute(eBulkPrintData, "QtyToPrint");
    	
    	Element eleUSerNameSpace = getModel("UserNameSpace");
    	String node = eleUSerNameSpace.getAttribute("Node");
    	
    	Element eleShipmentList = getModel("ShipmentList_AwaitingPickSlipPrint");
    	NodeList list = eleShipmentList.getElementsByTagName("Shipment");
    	if(list.getLength()>0){
    	Element eleShipment =  (Element) list.item(0);
    	String enterpriseCode =  eleShipment.getAttribute("EnterpriseCode");
    	String shipNode =  eleShipment.getAttribute("ShipNode");
    	String aa = eleShipment.getAttribute("EnterpriseCode");

    	Element inputBulkPrintPrintShipment = YRCXmlUtils.createDocument(E_SHIPMENT).getDocumentElement();
    	inputBulkPrintPrintShipment.setAttribute("Node", node);
    	inputBulkPrintPrintShipment.setAttribute(A_SHIP_NODE, shipNode);
    	inputBulkPrintPrintShipment.setAttribute(A_QTY_TO_PRINT, strQtyToPrint);
    	inputBulkPrintPrintShipment.setAttribute(A_ENTERPRISE_CODE, enterpriseCode);
    	
    	
    	YRCApiContext apiContext = new YRCApiContext();
    	apiContext.setFormId(getFormId());
    	apiContext.setApiName(KOHLSPCAApiNames.API_KOHLS_GENERATE_AND_STAMP_BATCH_NO_AT_SHIMENT_LINE);
    	apiContext.setInputXml(inputBulkPrintPrintShipment.getOwnerDocument());
    	callApi(apiContext);
    	}
 //   	CallBack handler implementation has been removed and moved to handleAPICompletion
//    	YRCPlatformUI.callApi(apiContext, new IYRCApiCallbackhandler() {
//			public void handleApiCompletion(YRCApiContext ctx) {
//
//				if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_GENERATE_AND_STAMP_BATCH_NO_AT_SHIMENT_LINE)) {
//					if (ctx.getInvokeAPIStatus() > 0) {
//					Element eleOutput = ctx.getOutputXml().getDocumentElement();
//					String extnbatchNo=	eleOutput.getAttribute("ExtnBatchNo");
//					if(null != extnbatchNo && !(extnbatchNo.trim().equals(""))){
//						YRCPlatformUI.showInformation("Information", "Batch No "+extnbatchNo+" is  generated and submitted to Sterling.");
//					}
//						return;
//					}
//				}
//			}
//					});
//    	
//    	}
    	
    }
    
    /**
     * This Method is called by "Bulk Print" button Or F8 Key fucntion. It closes the page.
     */
    public void launchBulkPrintPopUp(){
    	//YRCPlatformUI.showInformation("Store Pick Slip Print", "Initiating Bulk Print");
    	KOHLSPickSlipsBulkPrintPopup pBulkPrintPopupPage = new KOHLSPickSlipsBulkPrintPopup(
				new Shell(Display.getDefault().getActiveShell()), SWT.NONE,  this);
    	yrcPopupPageDialog = new YRCDialog(pBulkPrintPopupPage,
    			400,200,"Pick Slips - Bulk Printing","ApplicationTitleImage");
    	yrcPopupPageDialog.open();
    }   
    
    public void handleComboBoxSelection(Combo cmbPrinterList){
    	KohlsApplicationInitializer.modifyTerminalPropertyForUISession(strDefaultPrinter,cmbPrinterList.getText());
    }
    
    private static String reportsBaseDir;
    static 
    {
        reportsBaseDir = null;
        reportsBaseDir = (new StringBuilder()).append(Platform.getInstanceLocation().getURL()).append("temp").toString().substring(6);
        if(!Platform.getOS().equals("win32"))
            reportsBaseDir = (new StringBuilder()).append("/").append(reportsBaseDir).toString();
        YRCPlatformUI.addDisposableObject(new IYRCDisposable() {

            public void dispose()
            {
                YRCFileUtils.deleteDirectory(reportsBaseDir);
            }

        });
        try
        {
            File dir = new File(reportsBaseDir);
            if(dir.exists())
            {
                YRCFileUtils.deleteDirectory(reportsBaseDir);
                dir.mkdir();
            }
        }
        catch(Exception e) { }
    }
    

    private static String getDisplayDirectory(String reportDir)
    {
        String str = reportDir + "/reports/display/";
        File fl = new File(str);
        if(!fl.exists())
            fl.mkdirs();
        return (new StringBuilder()).append(str).append(System.currentTimeMillis()).toString();
    }
	public void setPrintModel(Element eleEditorInput) {
		
		Document docInput = YRCXmlUtils.createDocument("Shipments");
		Element eleInput = docInput.getDocumentElement();
		YRCXmlUtils.importElement(eleInput, eleEditorInput);
		setModel(M_SHIPMENTS_AWAIT_PICK_LIST_PRINT, eleInput);
	}
}
