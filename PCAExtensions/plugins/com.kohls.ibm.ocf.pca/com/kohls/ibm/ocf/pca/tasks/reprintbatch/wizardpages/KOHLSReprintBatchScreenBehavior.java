/*
 * Created on Nov 24,2007
 *
 */
package com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.ibm.ocf.pca.printUtils.KohlesPrintReportAPI;
import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintReprintScreen;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCJasperReportDefinition;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Kiran
 */
public class KOHLSReprintBatchScreenBehavior extends
	KOHLSReprintBatchScreenBaseBehavior {
//	YRCWizardPageBehavior {
    private Object input;

    private KOHLSReprintBatchScreenHelper helper;
    private KOHLSPCAUtils kohlsPCAUtils;

    private String formId;

	private boolean flag;
	Element eleEditorInput;
	Element eleInput = null;
	Element eleDeviceList =  null;
	Element outputModel = null ;
	Element updateModel = null ;
	private boolean batchPrintErr=false;
	private boolean cartValidaErr=false;
	private boolean popupScrn=false;

    public KOHLSReprintBatchScreenBehavior(KOHLSReprintBatchScreen parent,
	    String formId, Object wizardInput, KOHLSReprintBatchScreenHelper helper) {
    	
		super(parent, formId, wizardInput);
		this.input = wizardInput;
		
		if(!YRCPlatformUI.isVoid(wizardInput))
			eleEditorInput=(Element)((YRCEditorInput)wizardInput).getXml();
			
		this.formId = formId;
		init();
    }
 
    public void initPage() {
	
    callGetDeviceList();
    
    if(!YRCPlatformUI.isVoid(eleEditorInput)){
		  String strBatchID=eleEditorInput.getAttribute(KOHLSPCAConstants.A_PROFILE_ID);
		  
		  if(!YRCPlatformUI.equals(strBatchID,KOHLSPCAConstants.V_BLANK))
			  setModel("ShipmentDetails",eleEditorInput, true);
	  }
	
	

    }
    
    public KOHLSReprintBatchScreenBehavior(KOHLSReprintBatchScreen parent,
    	    String formId, Object wizardInput,Object deviceInput, KOHLSReprintBatchScreenHelper helper) {
	        	
	//    	super(parent);
	    	super(parent, formId, wizardInput);
	    	this.input = wizardInput;
	//    	eleInput = (Element) wizardInput;
	//    	 eleDeviceList = (Element) deviceInput;
	//    	 setModel("Model",eleInput);
	//    	 setModel("getDeviceList",eleDeviceList);
	//    	eleEditorInput=(Element) wizardInput;
	    		
	    	this.formId = formId;
	    	init();
	    	outputModel = YRCXmlUtils.createDocument("Reprint_Batch").getDocumentElement();
        }
  
    public void validateInput(){
		try {
			String batchID=parent.getTxtBatchID();
			 popupScrn=false;
//			String printerID=parent.getCmbPrinter();
			Element eleProcessPrintInput=null;
			try {
				eleProcessPrintInput=getTargetModel("ProcessReprintBatchInput");
			} catch (NullPointerException npe){
				popupScrn=true;
			}
			//If opened as popup, binding does not seem to work; hence retrieve all values
			if (!popupScrn) {
				doPrint();
			} else {
				YRCXmlUtils.setAttribute(outputModel, "BatchID", batchID);
//				YRCXmlUtils.setAttribute(outputModel, "PrinterID", printerID);
				 String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
				 YRCXmlUtils.setAttribute(outputModel, "PrinterID", printServiceName);
				YRCXmlUtils.setAttribute(outputModel, "Option", 1); //default setting to all
				
				String strCartNo = parent.getTxtCartNo();
				String strFromCartNo = parent.getTxtFromCartNo();
				String strToCartNo = parent.getTxtToCartNo();
				int noOfShipment= Integer.parseInt(parent.eleInput.getAttribute("NoOfShipments"));
				YRCXmlUtils.setAttribute(outputModel, "noOfShipment", noOfShipment);
				YRCXmlUtils.setAttribute(outputModel, "Option", 1);
				YRCXmlUtils.setAttribute(outputModel, "Store", KOHLSPCAUtils.getCurrentStore());
				YRCXmlUtils.setAttribute(outputModel, "Node", parent.eleInput.getAttribute("Node"));
				YRCXmlUtils.setAttribute(outputModel, "NodeType", parent.eleInput.getAttribute("NodeType"));
				
				if(strCartNo.length()>0) {
					if (strCartNo.startsWith("-") || Integer.parseInt(strCartNo) > noOfShipment){
						YRCPlatformUI.showInformation("Invalid Data", YRCPlatformUI.getString("cart_validation_error") + noOfShipment);
						cartValidaErr=true;
					} else {
						YRCXmlUtils.setAttribute(outputModel, "CartNo", strCartNo);
						YRCXmlUtils.setAttribute(outputModel, "Option", 2);
					}
				} else if (strFromCartNo.length()>0){
					if (Integer.parseInt(strFromCartNo) > noOfShipment || Integer.parseInt(strToCartNo) > noOfShipment
							|| Integer.parseInt(strFromCartNo) >  Integer.parseInt(strToCartNo)){
						YRCPlatformUI.showInformation("Invalid Data", YRCPlatformUI.getString("cart_validation_error") + noOfShipment);
						cartValidaErr=true;
					} else {
							YRCXmlUtils.setAttribute(outputModel, "FromCartNo", strFromCartNo);
							YRCXmlUtils.setAttribute(outputModel, "ToCartNo", strToCartNo);
							YRCXmlUtils.setAttribute(outputModel, "Option", 3);
					}
				}
				
				if (!cartValidaErr){
					doPrint(outputModel);
				}
	//			performBulkPrint(outputModel);
			}
		
		} catch (Exception ex){
			YRCPlatformUI.showInformation("Data Exception", YRCPlatformUI.getString("cart_validation_error"));
		}
	}
    
	
	private void callGetDeviceList() {

//	 invoking of the method is no more used. This will be read from the cache which is loaded during
//	Application initialization
//		callApi(KOHLSPCAApiNames.API_GET_DEVICE_LIST, 
//				getInputForDeviceList(KOHLSPCAConstants.V_PRINTER, 
//						KOHLSPCAUtils.getCurrentStore()));
	Element elePrinters =	KohlsApplicationInitializer.elePrinterDevices;
	
	Element eOutputGetDeviceList = null;
	
	Element elePropertiesList = KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI;
	Element eleProperties = YRCXmlUtils.getChildElement(elePropertiesList, "SIMINIProperties");
	String strDefaultPrinter = eleProperties.getAttribute("DefaultPrinter");
	
	 defaultPrinterID(elePrinters, strDefaultPrinter);
	 
		
	}

	/**
	 * These methods are not used anymore as API call is not made
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
			closePopup();
		}else{
			/*
			 * The code is not used anymore as we are not invoking the API
			 */
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
	//				String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PICK_PRINTER_ID);
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
//			
//			
			
			if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE)) {
				YRCPlatformUI.trace(
						"The command name is "+KOHLSPCAApiNames.API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE,
						null);
				
				Element eOutputReprintBatchCarts = ctx.getOutputXml().getDocumentElement();
				String strBatchID="";
				String strPrinterID="";
				if(!YRCPlatformUI.isVoid(eOutputReprintBatchCarts)){
					if("MultiPickSlipList".equals(eOutputReprintBatchCarts.getNodeName())){
						Iterator itrCartsToPrint = YRCXmlUtils.getChildren(eOutputReprintBatchCarts);					
						while(itrCartsToPrint.hasNext())
						{
							Element eOutputReprintBatch = (Element)itrCartsToPrint.next();
							strBatchID=eOutputReprintBatch.getAttribute(KOHLSPCAConstants.A_BATCH_ID);
							strPrinterID=eOutputReprintBatch.getAttribute(KOHLSPCAConstants.A_PRINTER_ID);
							String strResult=eOutputReprintBatch.getAttribute(KOHLSPCAConstants.A_RESULT);
							String strCartNo=eOutputReprintBatch.getAttribute("CartNo");
								if(!YRCPlatformUI.isVoid(strBatchID) && !YRCPlatformUI.equals(strBatchID, "")){
								
								if(strResult.equals("0"))
								
								YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("BATCHID_NOT_FOUND_IN_SYSTEM"),
										new String[]{strBatchID}));
								
								if(strResult.equals("01"))
									
									YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("BATCHID_CART_NOT_FOUND_IN_SYSTEM"),
											new String[]{strBatchID}));
								
								if(strResult.equals("2"))
									
									YRCPlatformUI.setMessage(YRCPlatformUI.getString("MUTLI_CART_RANGE_INVALID"));
								
								if(strResult.equals("1")||strResult.equals("")){
									
									try {
										//Document outXml=ctx.getOutputXml();
										Document outXml=YRCXmlUtils.createFromString(YRCXmlUtils.getString(eOutputReprintBatch));
										//jasperPrintBatchPickSlip(outXml, "PickSlip");
										jasperBatchPrints(outXml, "RDCMultisBatchPrint");
										if (!batchPrintErr){
											//YRCPlatformUI.showInformation("Singles Print",YRCPlatformUI.getString("batch_print_success"));
											YRCPlatformUI.trace("Multi Reprint Batch - Cart Print Successful:: BatchID\t" + strBatchID + "\tCartNo=" + strCartNo);
										}
									} catch (Exception ex) {
										//YRCPlatformUI.showInformation("Batch Print",YRCPlatformUI.getString("batch_print_failure"));
										YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
										YRCPlatformUI.trace("Multi Reprint Batch - Cart Print Failed:: BatchID\t" + strBatchID + "\tCartNo=" + strCartNo);
										closePopup();
							        	//throw new YFSException(YRCPlatformUI.getString("batch_print_failure"));
									}
									/*YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("BATCHID_PRINT_SUCCESS"),
											new String[]{strBatchID,strPrinterID}));*/							
								}
									
							}
						}
						if(!batchPrintErr){
							//When all the carts are printed successfully
							YRCPlatformUI.setMessage(YRCPlatformUI.getFormattedString(YRCPlatformUI.getString("BATCHID_PRINT_SUCCESS"),
									new String[]{strBatchID,strPrinterID}));
							//YRCPlatformUI.showInformation("Multis Print",YRCPlatformUI.getString("batch_print_success"));
						}
						if(popupScrn && !batchPrintErr){
							//When all the carts are printed successfully and the new cutom batch print screen is used for the reprint
							performUpdateBatchPrintService(outputModel);
						}
					}
					else{
						String strErrorMessage = YRCXmlUtils.getAttribute(eOutputReprintBatchCarts, "Description");
						if(!YRCPlatformUI.isVoid(strErrorMessage)){
							YRCPlatformUI.showError("Error",strErrorMessage);
						}
						else{
							YRCPlatformUI.showError("Error","No Carts To Print");// If we get No specific error description from the Batch Print Service
						}
					}
				}
				else{
					YRCPlatformUI.showError("Error","No Carts To Print"); // If we get a Blank Document in the Response from the Batch Print Service
				}
				closePopup();
				//Drop3 work  statrs here
				/*Element eOutputGetBatchRDCPrintList= ctx.getOutputXml().getDocumentElement();
//				System.out.println(YRCXmlUtils.getString(eOutputGetBatchRDCPrintList));
				try {
					
						Map<String, String> parameters = new HashMap<String, String>();
						parameters.put("HBC_IMG_SRC", KOHLSReprintBatchScreenBehavior.class.getResource("/icons").toString());
						parameters.put("HBC_JASPER_SRC",KOHLSReprintBatchScreenBehavior.class.getResource("/resources/reports").toString());
						KohlesPrintReportAPI printApi = new KohlesPrintReportAPI();
			
						String url = printApi.executeReport("/PickSlip",eOutputGetBatchRDCPrintList,"RDCBatchPrint", parameters);
						printApi.openReport(url);
						
						performUpdateBatchPrintService(outputModel);
					
					} catch (Exception ex) {
//						ex.printStackTrace();
					}*/
			
			} else if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_UPDATE_SERVICE)){
				closePopup();
			}
    	
		}
    	
    }
  
	public void performUpdateBatchPrintService(Element inputModel){
		updateModel = YRCXmlUtils.createDocument("BatchPrint").getDocumentElement();
//		YRCXmlUtils.setAttribute(updateModel, "BatchID", parent.getTxtBatchID());
		YRCXmlUtils.setAttribute(updateModel, "BatchID", inputModel.getAttribute(KOHLSPCAConstants.A_BATCH_ID));
		String prtStatus=parent.getStatus();
		if (batchPrintErr){
			prtStatus=KOHLSPCAConstants.A_PRINT_ST_FAILED;
		}
		if (prtStatus.equalsIgnoreCase(KOHLSPCAConstants.A_PRINT_ST_AWAIT_PRINT)) {
			YRCXmlUtils.setAttribute(updateModel, "Status", KOHLSPCAConstants.A_PRINT_ST_PRINTED);
		} else if (prtStatus.equalsIgnoreCase(KOHLSPCAConstants.A_PRINT_ST_PRINTED)) {
			YRCXmlUtils.setAttribute(updateModel, "Status", KOHLSPCAConstants.A_PRINT_ST_REPRINT);
			YRCXmlUtils.setAttribute(updateModel, "ReprintFlag", "Y");
		} else if (batchPrintErr) {
			YRCXmlUtils.setAttribute(updateModel, "Status", KOHLSPCAConstants.A_PRINT_ST_FAILED);
		}  else if (prtStatus.equalsIgnoreCase(KOHLSPCAConstants.A_PRINT_ST_REPRINT)){
			YRCXmlUtils.setAttribute(updateModel, "Status", KOHLSPCAConstants.A_PRINT_ST_REPRINT);
		} else {
			YRCXmlUtils.setAttribute(updateModel, "Status", KOHLSPCAConstants.A_PRINT_ST_PRINTED);
		}
		callApi(KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_UPDATE_SERVICE, updateModel.getOwnerDocument());
		
	}

	public void jasperBatchPrints(Document inDoc,String type)throws Exception {
		
		JRXmlDataSource ds = null;
		String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
		 ds = new JRXmlDataSource(inDoc, selectExpression);
		 YRCJasperReportDefinition jrd=KOHLSPCAUtils.getReport(type);
		 String reportName=jrd.getFile();
		 InputStream is = getReportStream(reportName);
		
//		 String reportName="C:\\Kohls\\jasper\\KOHLSPickSlipSummaryMAINReport.jasper";
		 
//		 PrintService printServiceName=PrintServiceLookup.lookupDefaultPrintService();
   	  	 String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
   	  	 JasperPrint jp=null;
		  jp = JasperFillManager.fillReport(is, null, ds);
		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
	        printServiceAttributeSet.add(new PrinterName(printServiceName, null));
	        
	        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
	        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
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
	          } catch (Exception e) {
	        	  batchPrintErr=true;
	        	  //YRCPlatformUI.showInformation("Multi Print",YRCPlatformUI.getString("batch_print_failure"));
	        	  YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
	        	  performUpdateBatchPrintService(outputModel);
	          }
	}
	
	protected InputStream getReportStream(String reportName) throws Exception  {
	    InputStream is = getClass().getResourceAsStream(reportName);
	    if (is == null) {
	      Exception ex = new Exception("Batch Print Report File not found in classpath" + reportName);
	    
	      throw ex;
	    }
	    return is;
	  }
	
	public void closePopup() {
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}
		
		if(currentPage instanceof KOHLSReprintBatchScreen){
			YRCDialog popDlg=(YRCDialog) comp.getData("screen");
			if (!YRCPlatformUI.isVoid(popDlg))
				popDlg.close();
		}
	}
	
    private void defaultPrinterID(Element outputGetDeviceList, String strPrinterID ) {

		NodeList nOutputPrinterList = outputGetDeviceList
				.getElementsByTagName(KOHLSPCAConstants.E_DEVICE);

		if (!YRCPlatformUI.isVoid(nOutputPrinterList)
				&& nOutputPrinterList.getLength() > 1) {

			//rearrangePrinterID(outputGetDeviceList, strPrinterID);
			kohlsPCAUtils.rearrangePrinterID(outputGetDeviceList, strPrinterID);
			setModel("getDeviceList",outputGetDeviceList);

		}
		
	}

//	private void rearrangePrinterID(Element outputGetDeviceList, String strPrinterID) {
//		
//		Element eDevice = (Element) YRCXPathUtils.evaluate(outputGetDeviceList,
//				"/Devices/Device[@DeviceId='"
//						+ strPrinterID + "']", XPathConstants.NODE);
//		
//
//			if (!YRCPlatformUI.isVoid(eDevice)) {
//
//				// This code to show the New Container Number for auto generated
//				// container
//				// Walk backwards until we find the first sibling in the parent
//				//
//				Node previousNode = eDevice.getPreviousSibling();
//				if(!YRCPlatformUI.isVoid(previousNode)){
//				while (previousNode.getPreviousSibling() != null
//						&& !(previousNode == eDevice)) {
//					previousNode = previousNode.getPreviousSibling();
//				}
//				if (previousNode.getPreviousSibling() == null) {
//					try {
//
//						if (!previousNode.isSameNode(eDevice)) {
//							eDevice = (Element) outputGetDeviceList
//									.removeChild(eDevice);
//							outputGetDeviceList.insertBefore(eDevice, previousNode);
//						}
//
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				else {
//					YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),
//							YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
//				}	
//
//			} 
//			}
//		}	

		

//		if (!YRCPlatformUI.isVoid(eDevice)) {
//
//			Node previousNode = eDevice.getPreviousSibling();
//			if(!YRCPlatformUI.isVoid(previousNode))
//			while (previousNode.getPreviousSibling() != null
//					&& !(previousNode == eDevice)) {
//				previousNode = previousNode.getPreviousSibling();
//			}
//			if (previousNode.getPreviousSibling() == null) {
//				try {
//
//					if (!previousNode.isSameNode(eDevice)) {
//						eDevice = (Element) outputGetDeviceList
//								.removeChild(eDevice);
//						outputGetDeviceList.insertBefore(eDevice, previousNode);
//					}
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//		} else {
//			YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),
//					YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
//		}

		
	

    /**
     * This Method is called by action for button. It checks for the radio button selection. Based on that it creates
     * the input for the shipments to be printed. 
     */
    public void doPrint(Element inputModel) {
    	
//    	Element eleProcessPrintInput=getTargetModel("ProcessReprintBatchInput");
    	Element eleProcessPrintInput=inputModel;
    	
    	if(!YRCPlatformUI.isVoid(eleProcessPrintInput)){
    		
//    		String strPrinterID=eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_PRINTER_ID);
    		
//    		if(!YRCPlatformUI.isVoid(strPrinterID)){
    			
    			String strBatchID=eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_BATCH_ID);
    			
    			if(!YRCPlatformUI.isVoid(strBatchID) || (!strBatchID.equals(""))){
    				
    				String strOption = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_OPTION);
    				
    				boolean canCallApi=false;
    				
    				if(strOption.equals("1")){
    					
    					canCallApi=true;
    					
    					
    				}
    				
    				if(strOption.equals("2")){
    					
    					String strCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_CART_NUMER);
    					
    					if(checkIfNumber(strCartNo)){
    						canCallApi=true;
    					}else {
    						
    						YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_CARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_CARTNO_WRONG_INPUT"));
    	    				getControl("txtCartNo").setFocus();
    	    				return;
    						
    					}
    					
    				}
    				
    				if(strOption.equals("3")){
    					
    					String strFromCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_FROM_CART_NUMER);
    					String strToCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_TO_CART_NUMER);
    					
    					if(checkIfNumber(strFromCartNo)){
    						
    						if(checkIfNumber(strToCartNo)){
    							
    							int intFromCartNo = YRCXmlUtils.getIntAttribute(eleProcessPrintInput,KOHLSPCAConstants.A_FROM_CART_NUMER);
    							int intToCartNo = YRCXmlUtils.getIntAttribute(eleProcessPrintInput,KOHLSPCAConstants.A_TO_CART_NUMER);
    							if(intFromCartNo <= intToCartNo){
    								canCallApi=true;
    								
    							}else{
    								YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_FROM_TO_CARTNO_WRONG_RANGE_INPUT"),YRCPlatformUI.getString("ERROR_FROM_TO_CARTNO_WRONG_RANGE_INPUT"));
            	    				getControl("txtFromCartNo").setFocus();
            	    				return;
    							}
    							
    							
    						}
    						else {
    							YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_TOCARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_TOCARTNO_WRONG_INPUT"));
        	    				getControl("txtToCartNo").setFocus();
        	    				return;
        						
    						}
    						
    						
    					}else {
    						
    						YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_FROMCARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_FROMCARTNO_WRONG_INPUT"));
    	    				getControl("txtFromCartNo").setFocus();
    	    				return;
    						
    					}
    					
    				}
    				
    			
    			if(canCallApi){	
    			callApi(KOHLSPCAApiNames.API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE, eleProcessPrintInput.getOwnerDocument());
    	
    			}
    		}
    			else {
				YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_BATCHID_NO_INPUT"),YRCPlatformUI.getString("ERROR_BATCHID_NO_INPUT"));
				getControl("txtBatchID").setFocus();
				return;	
			}
    		
    		
//    	}else {
//			
//			YRCPlatformUI.showError(YRCPlatformUI.getString("Printer_Missing"),YRCPlatformUI.getString("Select_Printer"));
//			return;
//		}
    	}
    	
    	
    }
   
    public void doPrint() {
    	Element eleProcessPrintInput=getTargetModel("ProcessReprintBatchInput");
//    	Element eleProcessPrintInput=inputModel;
    	//set dummy value to bypass the validation
    	//eleProcessPrintInput.setAttribute(KOHLSPCAConstants.A_PRINTER_ID, KOHLSPCAConstants.A_PRINTER_ID);
    	 
    	
    	if(!YRCPlatformUI.isVoid(eleProcessPrintInput)){
    		
    		String strPrinterID=eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_PRINTER_ID);
    		
    		if(!YRCPlatformUI.isVoid(strPrinterID)){
    			
    			String strBatchID=eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_BATCH_ID);
    			
    			if(!YRCPlatformUI.isVoid(strBatchID) || (!strBatchID.equals(""))){
    				
    				String strOption = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_OPTION);
    				
    				boolean canCallApi=false;
    				
    				if(strOption.equals("1")){
    					
    					canCallApi=true;
    					
    					
    				}
    				
    				if(strOption.equals("2")){
    					
    					String strCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_CART_NUMER);
    					
    					if(checkIfNumber(strCartNo)){
    						canCallApi=true;
    					}else {
    						
    						YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_CARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_CARTNO_WRONG_INPUT"));
    	    				getControl("txtCartNo").setFocus();
    	    				return;
    						
    					}
    					
    				}
    				
    				if(strOption.equals("3")){
    					
    					String strFromCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_FROM_CART_NUMER);
    					String strToCartNo = eleProcessPrintInput.getAttribute(KOHLSPCAConstants.A_TO_CART_NUMER);
    					
    					if(checkIfNumber(strFromCartNo)){
    						
    						if(checkIfNumber(strToCartNo)){
    							
    							int intFromCartNo = YRCXmlUtils.getIntAttribute(eleProcessPrintInput,KOHLSPCAConstants.A_FROM_CART_NUMER);
    							int intToCartNo = YRCXmlUtils.getIntAttribute(eleProcessPrintInput,KOHLSPCAConstants.A_TO_CART_NUMER);
    							if(intFromCartNo <= intToCartNo){
    								canCallApi=true;
    								
    							}else{
    								YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_FROM_TO_CARTNO_WRONG_RANGE_INPUT"),YRCPlatformUI.getString("ERROR_FROM_TO_CARTNO_WRONG_RANGE_INPUT"));
            	    				getControl("txtFromCartNo").setFocus();
            	    				return;
    							}
    							
    							
    						}
    						else {
    							YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_TOCARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_TOCARTNO_WRONG_INPUT"));
        	    				getControl("txtToCartNo").setFocus();
        	    				return;
        						
    						}
    						
    						
    					}else {
    						
    						YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_FROMCARTNO_WRONG_INPUT"),YRCPlatformUI.getString("ERROR_FROMCARTNO_WRONG_INPUT"));
    	    				getControl("txtFromCartNo").setFocus();
    	    				return;
    						
    					}
    					
    				}
    				
    			
    			if(canCallApi){		
    			callApi(KOHLSPCAApiNames.API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE, eleProcessPrintInput.getOwnerDocument());
    			
    	
    			}
    		}
    			else {
				YRCPlatformUI.showError(YRCPlatformUI.getString("TITLE_BATCHID_NO_INPUT"),YRCPlatformUI.getString("ERROR_BATCHID_NO_INPUT"));
				getControl("txtBatchID").setFocus();
				return;	
			}
    		
    		
    	}else {
			
			YRCPlatformUI.showError(YRCPlatformUI.getString("Printer_Missing"),YRCPlatformUI.getString("Select_Printer"));
			return;
		}
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
    
    
	 public void handleComboBoxSelection(Combo cmbPrinterList){
		 String strDefaultPrinter = "DefaultPrinter";
		 
	    	KohlsApplicationInitializer.modifyTerminalPropertyForUISession(strDefaultPrinter,cmbPrinterList.getText());
	 }
  
	
}
