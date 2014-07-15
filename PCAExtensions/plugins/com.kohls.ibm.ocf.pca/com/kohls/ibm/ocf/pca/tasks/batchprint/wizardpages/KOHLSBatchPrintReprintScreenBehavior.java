
/*
 * Created on Jun 09,2013
 *
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
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

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.kohls.ibm.ocf.pca.printUtils.KohlesPrintReportAPI;
import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.YRCJasperReportDefinition;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfs.japi.YFSException;
/**
 * @author srikaheb
 * @build # 200810210800
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public class KOHLSBatchPrintReprintScreenBehavior extends KOHLSBatchPrintReprintScreenBaseBehavior   {

	private Object input ;
	private KOHLSBatchPrintReprintScreenHelper helper;
	private String formId;
	private Element outputModel = null ;
	private Element updateModel = null ;
	private boolean batchPrintErr=false;
	private boolean cartValidaErr=false;
	
	public KOHLSBatchPrintReprintScreenBehavior(KOHLSBatchPrintReprintScreen parent, String formId, 
			Object input, KOHLSBatchPrintReprintScreenHelper helper) {
		
		super(parent, formId, input);
		this.input = input;
		
		this.helper = helper;
		this.formId = formId;
		init();
		initPage();
		outputModel = YRCXmlUtils.createDocument("BatchPrint").getDocumentElement();
	}
	
	public void initPage() {

	    }
	
	
	public void validateInput(){
		try {
			String batchID=parent.getTxtBatchID();
//			String printerID=parent.getCmbPrinter();
			YRCXmlUtils.setAttribute(outputModel, "BatchID", batchID);
//			YRCXmlUtils.setAttribute(outputModel, "PrinterID", printerID);
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
		String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
		YRCXmlUtils.setAttribute(outputModel, "PrinterID", printServiceName);
		
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
		if (!cartValidaErr) {
			performBulkPrint(outputModel);
		}
		
		} catch (Exception ex){
//			ex.printStackTrace();
			YRCPlatformUI.showInformation("Data Exception", ex.getMessage());
		}
	}
	
	/**
	 * Closes the popup screen opened
	 *
	 */
	
	public void closePopup() {
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}
		
		if(currentPage instanceof KOHLSBatchPrintReprintScreen){
			YRCDialog popDlg=(YRCDialog) comp.getData("screen");
			if (!YRCPlatformUI.isVoid(popDlg))
				popDlg.close();
		}
	}
	
	public void performBulkPrint(Element inputModel){
		callApi(KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_SERVICE, inputModel.getOwnerDocument());
	}
	
	public void handleApiCompletion(YRCApiContext ctx){
		
		if(ctx.getInvokeAPIStatus() < 1) {
        	YRCPlatformUI.trace("API exception in " + formId + " page, ApiName " + ctx.getApiName() + ",Exception : ", ctx.getException());
        	closePopup();
        } else if(parent.isDisposed()){
        	YRCPlatformUI.trace(formId + " page is disposed, ApiName " + ctx.getApiName() + ",Exception : ");
        	closePopup();
        } else {
        	if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_SERVICE)) {
				YRCPlatformUI.trace("The command name is "+KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_SERVICE,null);
				
				Element eOutputGetBatchStorePrintList= ctx.getOutputXml().getDocumentElement();
				eOutputGetBatchStorePrintList.setAttribute("Store", KOHLSPCAUtils.getCurrentStore());
//				System.out.println(YRCXmlUtils.getString(eOutputGetBatchStorePrintList));
				try {
					
					/*Map<String, String> parameters = new HashMap<String, String>();
					parameters.put("HBC_IMG_SRC", KOHLSBatchPrintReprintScreenBehavior.class.getResource("/icons").toString());
					parameters.put("HBC_JASPER_SRC",KOHLSBatchPrintReprintScreenBehavior.class.getResource("/resources/reports").toString());
					KohlesPrintReportAPI printApi = new KohlesPrintReportAPI();
		
					String url = printApi.executeReport("/StoreShipments",eOutputGetBatchStorePrintList,"StoreBatchPrint", parameters);
					printApi.openReport(url);*/
					
					if (parent.eleInput.getAttribute("NodeType").equalsIgnoreCase("STORE")) {
						jasperBatchPrints(ctx.getOutputXml(), "StoreBatchPrint");
						performUpdateBatchPrintService(outputModel);
					} else {
						try {
							Document outXml=ctx.getOutputXml();
							
							NodeList nodeShipmentList = outXml.getDocumentElement()
													.getElementsByTagName(KOHLSPCAConstants.E_SHIPMENT);
							int iNumShipments = nodeShipmentList.getLength();

							for (int i = 0; i < iNumShipments; i++) {
								Element eleShipment = (Element) nodeShipmentList.item(i);
								DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						        Document inputPrintdoc = builder.newDocument();
						        Element eleImported = (Element) inputPrintdoc.importNode(eleShipment, true);
						        inputPrintdoc.appendChild(eleImported);
								//jasperPrintBatchSingles(inputPrintdoc, "RDCSinglesBatchPrint");
								jasperBatchPrints(inputPrintdoc, "RDCSinglesBatchPrint");
							}
							performUpdateBatchPrintService(outputModel);
							if (!batchPrintErr){
								  YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_success_message"));
							}
						} catch (Exception ex) {
							YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
							//YRCPlatformUI.showInformation("Batch Print",YRCPlatformUI.getString("batch_print_failure"));
							closePopup();
							 //YRCPlatformUI.trace(ex.getStackTrace());
				        	//  throw new YFSException(YRCPlatformUI.getString("batch_print_failure"));
						}
					}
					
					
				
				
				} catch (Exception ex) {
					//YRCPlatformUI.showInformation("Batch Print",YRCPlatformUI.getString("batch_print_failure"));
					YRCPlatformUI.setMessage(YRCPlatformUI.getString("batch_print_failure"));
					  closePopup();
				}
			
			}  else if (ctx.getApiName().equals(KOHLSPCAApiNames.API_KOHLS_PROCESS_BATCH_PRINT_UPDATE_SERVICE)) {
				closePopup();
			}
        	 if(ctx.getApiName().equals("")){
        	 	Document outXml = ctx.getOutputXml();
        	 }
        
		}
	}
	
	public void performUpdateBatchPrintService(Element inputModel){
		updateModel = YRCXmlUtils.createDocument("BatchPrint").getDocumentElement();
		YRCXmlUtils.setAttribute(updateModel, "BatchID", parent.getTxtBatchID());
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
	 
	/**
	 * This Method is called by base behavior while calling API 
	 */
	public Document getDocument(String apiName){
		return null;
	}

	//this is not used
	@Override
	public void doBatchReprint() {
    	
    	Element eleProcessPrintInput=getTargetModel("ProcessReprintBatchInput");
    	
    }
	
	/*
	 * If SIM User selects a different printer on the UI,
	 * For the given session - that will be treated as the
	 * Default Printer.
	 */
	 public void handleComboBoxSelection(Combo cmbPrinterList){
		 	String strDefaultPrinterPropName = KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER;
	    	KohlsApplicationInitializer.modifyTerminalPropertyForUISession(strDefaultPrinterPropName,cmbPrinterList.getText());
	 }
	//Pawan - Drop2 - End
	 
	 public void jasperBatchPrints(Document inDoc,String type)throws Exception {
//			 loadJasperReports(KOHLSPCAConstants.A_JASPER_PRJ_ROOT);
		 
			JRXmlDataSource ds = null;
			String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
			 ds = new JRXmlDataSource(inDoc, selectExpression);
			 //String reportName="C:\\Kohls\\jasper\\StoreBatchPrintMain.jasper";
			 YRCJasperReportDefinition jrd=KOHLSPCAUtils.getReport(type);
			 String reportName=jrd.getFile();
			 InputStream is = getReportStream(reportName);
			 
//			 PrintService printServiceName=PrintServiceLookup.lookupDefaultPrintService();
	   	  	 String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
			 JasperPrint jp = JasperFillManager.fillReport(is, null, ds);
			 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
//		        printServiceAttributeSet.add(new PrinterName(printServiceName.getName(), null));
		        printServiceAttributeSet.add(new PrinterName(printServiceName, null));
		        
		        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
		        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
		        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
		        printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
//		        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
//		        printRequestAttributeSet.add(MediaSizeName.ISO_A4);
		        
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
		        	 // YRCPlatformUI.showInformation("Batch Print",YRCPlatformUI.getString("batch_print_failure"));
		        	  closePopup();
		        	  performUpdateBatchPrintService(outputModel);
		          }
		}
			
	 
		protected InputStream getReportStream(String reportName) throws Exception {
		    InputStream is = getClass().getResourceAsStream(reportName);
//			 InputStream is = new FileInputStream(reportName);
		    if (is == null) {
		      Exception ex = new Exception("Batch Print Report File not found in classpath" + reportName);
		    
		      throw ex;
		    }
		    return is;
		  }
		
}
