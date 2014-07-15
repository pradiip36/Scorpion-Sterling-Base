/*
 * Created on Nov 24,2007
 *
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;


import java.text.SimpleDateFormat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.kohls.ibm.ocf.pca.util.KohlsCommonUtil;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDateTimeUtils;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
import org.w3c.dom.NodeList;

/**
 * @author srikanth
 */
public class KOHLSBatchPrintScreenBehavior extends KOHLSBatchPrintScreenBaseBehavior {
    private Object input;
    private KOHLSBatchPrintScreenHelper helper;
    private String formId;
	private boolean flag;
	Element eleEditorInput;
	private final String strDefaultPrinter = KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER;


    public KOHLSBatchPrintScreenBehavior(KOHLSBatchPrintScreen parent,
	    String formId, Object wizardInput, KOHLSBatchPrintScreenHelper helper) {
    	
		super(parent, formId, wizardInput);
		this.input = wizardInput;
		
		if(!YRCPlatformUI.isVoid(wizardInput))
			eleEditorInput=(Element)((YRCEditorInput)wizardInput).getXml();
			
		this.formId = formId;
		init();
	
    }
 
    public void initPage() {
	
    //callGetDeviceList();
    	String strDefaultPrinterID = KohlsApplicationInitializer.getTerminalPropertyForUISession(strDefaultPrinter);
		KOHLSPCAUtils.rearrangePrinterID(KohlsApplicationInitializer.elePrinterDevices, strDefaultPrinterID);
		setModel("getDeviceList1",KohlsApplicationInitializer.elePrinterDevices,true);
    	
//    Element ePrinters = KohlsApplicationInitializer.elePrinterDevices;
//    String strDefaultPrinterProp = KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER;
//    String strDefaultPrinterID = KohlsApplicationInitializer.getTerminalPropertyForUISession(strDefaultPrinterProp);
//    KOHLSPCAUtils.rearrangePrinterID(ePrinters, strDefaultPrinterID);
    //set the model with ePrinters element
//	setModel("getDeviceList1",ePrinters);
    
    if(!YRCPlatformUI.isVoid(eleEditorInput)){
		  String strBatchID=eleEditorInput.getAttribute(KOHLSPCAConstants.A_PROFILE_ID);
		  
		  if(!YRCPlatformUI.equals(strBatchID,KOHLSPCAConstants.V_BLANK))
			  setModel("ShipmentDetails",eleEditorInput, true);
	  }
    
    boolean str1=KohlsCommonUtil.getUserList();
    if(str1==true){
		disableField("btnPrint");
	}
    }
	
    public void callGetDeviceList() {
		
		callApi(KOHLSPCAApiNames.API_GET_DEVICE_LIST, getInputForDeviceList(KOHLSPCAConstants.V_PRINTER, 
						KOHLSPCAUtils.getCurrentStore()));
		
	}
	
	 private  Document getInputForDeviceList(String strDeviceType, String strOrganizationCode){
			
			Document docDevice = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_DEVICE);
			Element eDeviceType = docDevice.getDocumentElement();		
			eDeviceType.setAttribute(KOHLSPCAConstants.A_DEVICE_TYPE,strDeviceType);	
			eDeviceType.setAttribute(KOHLSPCAConstants.A_ORGANIZATION_CODE,strOrganizationCode);
			return docDevice;
			
		}
	 
	void callApi(String name, Document inputXml) {
		YRCApiContext context = new YRCApiContext();
		context.setApiName(name);
		context.setFormId(getFormId());
	    context.setInputXml(inputXml);
	    callApi(context);
	}
    
    public void handleApiCompletion(YRCApiContext ctx) {
    	
    	if (ctx.getInvokeAPIStatus() < 1) {
			YRCPlatformUI.trace("API exception in " + getFormId()
					+ " page, ApiName " + ctx.getApiName()
					+ ",Exception : ", ctx.getException());
		} else{
			
			
		//API_KOHLS_BATCH_PRINT_SEARCH_SERVICE	
			
			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_BATCH_PRINT_SEARCH_SERVICE)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_KOHLS_BATCH_PRINT_SEARCH_SERVICE,null);
				
				Element eOutputReprintBatch = ctx.getOutputXml().getDocumentElement();
				
				if(!YRCPlatformUI.isVoid(eOutputReprintBatch)){
					setModel("PrintPackShipments",eOutputReprintBatch);
				}
				
			} else 
				if (ctx.getApiName().equals(KOHLSPCAApiNames.API_GET_DEVICE_LIST)) {
					YRCPlatformUI.trace(
							"The command name is "+KOHLSPCAApiNames.API_GET_DEVICE_LIST,
							null);
					
					Element eOutputGetDeviceList= ctx.getOutputXml().getDocumentElement();
					setModel("getDeviceList1",eOutputGetDeviceList);
					
					if(!YRCPlatformUI.isVoid(eOutputGetDeviceList)){
						
					//	String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PRINTER_ID);
					//	String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PICK_PRINTER_ID);
						
//						if(!YRCPlatformUI.isVoid(strPrinterID) && 
//								!YRCPlatformUI.equals(strPrinterID, "")){
//						
//						  defaultPrinterID(eOutputGetDeviceList, strPrinterID);
//						  
//						}
					    // setModel("getDeviceList",eOutputGetDeviceList);
					}
				
				}
		}
    }
  

    /**
     * This Method is called by action for button. It checks for the radio button selection. Based on that it creates
     * the input for the shipments to be printed. 
     */
    public void doSearch() {
    	
    	Element eleProcessPrintInput=getTargetModel("ProcessReprintBatchInput");
    	String fromdt= eleProcessPrintInput.getAttribute("FromCreateTS");
    	String todt= eleProcessPrintInput.getAttribute("ToCreateTS");
    	String extnBatchNo = eleProcessPrintInput.getAttribute("BatchID");
    	if(null != extnBatchNo){
    		eleProcessPrintInput.setAttribute("BatchIDQryType", "LIKE");
    	}
    	try {
    		if(fromdt!=null && fromdt.length() >0  && todt!=null && todt.length()>0) {
    	if (YRCDateTimeUtils.getDate(fromdt).after(YRCDateTimeUtils.getDate(todt))){
//    		YRCPlatformUI.showError("Error", "Invalid Date Format");
    		YRCPlatformUI.showError(YRCPlatformUI.getString("dt_compare_error"), YRCPlatformUI.getString("dt_compare_desc"));
    		return;
    		
//    		Document mydoc=YRCXmlUtils.createDocument("Errors");
//			Element errList= mydoc.getDocumentElement();
//			Element eleError = YRCXmlUtils.createChild(errList,"Error");
//			eleError.setAttribute("ErrorCode","DateValidationFailure");
//			eleError.setAttribute("ErrorDescription","From date can not be greater than To date");
//			YRCErrorDialog errorDialog= new YRCErrorDialog("Date Validation","Error arg2",mydoc);
//			errorDialog.setBlockOnOpen(true);
//			errorDialog.open();
    	} 
    		}
    	if(!YRCPlatformUI.isVoid(eleProcessPrintInput)){
    		 SimpleDateFormat dtFormatter = new SimpleDateFormat("yyyy-MM-dd");//Date format for DB comparision
    		 if (fromdt!=null && fromdt.length() >0){
    			 String frmDtString=dtFormatter.format(YRCDateTimeUtils.getDate(fromdt));
        		 eleProcessPrintInput.setAttribute("FromCreateTS", frmDtString);
    		 }
    		 if (todt!=null && todt.length() >0){
	    		 String doDtString=dtFormatter.format(YRCDateTimeUtils.getDate(todt));
	    		 eleProcessPrintInput.setAttribute("ToCreateTS", doDtString);
    		 }
    		 eleProcessPrintInput.setAttribute("Node", KOHLSPCAUtils.getCurrentStore());
    		 
             Element ordby= eleProcessPrintInput.getOwnerDocument().createElement("OrderBy");
             Element attr=ordby.getOwnerDocument().createElement("Attribute");
             attr.setAttribute("Name", "BatchID");
             attr.setAttribute("Desc", "Y");
             ordby.appendChild(attr);
             eleProcessPrintInput.appendChild(ordby);
    		 
    		 callApi(KOHLSPCAApiNames.API_KOHLS_BATCH_PRINT_SEARCH_SERVICE, eleProcessPrintInput.getOwnerDocument());
    	}
    	}catch (Exception ex){
    		ex.printStackTrace();
    	}
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

	@Override
	public void performClose() {
		// TODO Auto-generated method stub
		
	}

	public Element getReprintBatchModel() {
		//Element eleReprintBatchModel=getModel("PrintPackShipments");
		Element eleReprintBatchModel=getModel("SelectedBatchModel");
		return eleReprintBatchModel;
	}

	 public void setSelectedBatchModel(Element ele){
	    	setModel("SelectedBatchModel",ele);
	 }
	 
	 public Element getdeviceListModel() {
			Element eledevicelistModel=getModel("getDeviceList1");
			return eledevicelistModel;
		}
	
}
