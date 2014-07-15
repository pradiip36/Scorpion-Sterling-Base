package com.kohls.ibm.ocf.pca.util;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCJasperReportDefinition;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizard;
import com.yantra.yfc.rcp.YRCXPathUtils;

public class KOHLSPCAUtils {
	
   private static DocumentBuilderFactory fac;
    
    /*This will ensure that only one instance of DocumentBuilderFactory is created*/
    static {
    	fac = DocumentBuilderFactory.newInstance();
    }
	
	
	public static IYRCComposite getCurrentPage(){
		Composite comp = YRCDesktopUI.getCurrentPage();
		IYRCComposite currentPage = null;
		if (comp instanceof YRCWizard) {
			YRCWizard wizard = (YRCWizard) YRCDesktopUI.getCurrentPage();
			currentPage = (IYRCComposite) wizard.getCurrentPage();
		}else if(comp instanceof IYRCComposite){
			currentPage = (IYRCComposite)comp;
		}
		return currentPage;
	}
	
	public static String getEnterpriseCodeForStoreUser() {
		String strEnterpriseCode=YRCPlatformUI.getUserElement().getAttribute(KOHLSPCAConstants.A_ENTERPRISE_CODE);
		
		if(!YRCPlatformUI.isVoid(strEnterpriseCode))
			return strEnterpriseCode;
		else 
			return KOHLSPCAConstants.V_CATALOG_ENTERPRISE;
	}
	
	public static String getCurrentStore()
	{
		Element userElement = YRCPlatformUI.getUserElement();
		String strStoreId="";
	
		if (!YRCPlatformUI.isVoid(userElement)) {
			strStoreId=userElement.getAttribute("ShipNode");
		}
		
		return strStoreId;
	}

	public static String getStringTrace(Exception ex)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		ex.printStackTrace(pw);
		return(sw.toString());
	}
	
	public static String getDefaultPrinterID()
	{
		
		String strPrinterID= KohlsApplicationInitializer.getTerminalPropertyForUISession(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
		//String strPrinterID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_PRINTER_ID);
		
		
		if(!YRCPlatformUI.isVoid(strPrinterID) && 
				!YRCPlatformUI.equals(strPrinterID, "")){
		
			return strPrinterID;
		  
		}else {
		
		return null;
		
		}
	}
	
	public static String getDefaultMultiContainerTypeID()
	{
		
		//String strMultiContainerTypeID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_MUTLI_CONTAINER_TYPE);
		String strMultiContainerTypeID=KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_MUTLI_CONTAINER_TYPE);
		
		if(!YRCPlatformUI.isVoid(strMultiContainerTypeID) && 
				!YRCPlatformUI.equals(strMultiContainerTypeID, "")){
		
			return strMultiContainerTypeID;
		  
		}else {
		
		return null;
		
		}
	}
	
	public static String getDefaultPackStationID()
	{
		
		//String strPackStationID=System.getProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PACK_STATION);
		
		String strPackStationID=KohlsApplicationInitializer.getTerminalPropertyForUISession(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PACK_STATION);
		if(!YRCPlatformUI.isVoid(strPackStationID) && 
				!YRCPlatformUI.equals(strPackStationID, "")){
		
			return strPackStationID;
		  
		}else {
		
		return null;
		
		}
	}

	public static boolean hasPermissionForRecordShortage(){
		
		String sApp = KOHLSPCAConstants.SOP_RECORD_SHORTAGE_PERMISSION_ID;
		
		return YRCPlatformUI.hasPermission(sApp);
	}
	
	public static boolean hasPermissionForRecordCustomerPick(){
		
		String sApp = KOHLSPCAConstants.SOP_RECORD_CUSTOMER_PICK_PERMISSION_ID;
		
		return YRCPlatformUI.hasPermission(sApp);
	}
	public static void jasperRePrintPackSlip(Document inDoc,String type)throws Exception 
	{
		JRXmlDataSource ds = null;
		String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
		 ds = new JRXmlDataSource(inDoc, selectExpression);
		 //Map reportParameters = new HashMap();
		// reportParameters.put("REPORT_LOCALE", "en_US");
		// reportParameters.put("APP_USER_LOCALE", "en_US_EST");
		 String reportName = "";
		 if(type.equalsIgnoreCase("Shipment"))
		 {
		  reportName = KOHLSPCAConstants.V_KOHLS_RE_PACKSHIP_JASPER ;
		 }else
		 {
			 reportName = KOHLSPCAConstants.V_KOHLS_PACKSHIP_JASPER ;
		 }
		 KOHLSPCAUtils ma = new KOHLSPCAUtils();
		 InputStream is = ma.getReportStream(reportName);
		 String printerName = KOHLSPCAUtils.getDefaultPrinterID();
		 JasperPrint jp = JasperFillManager.fillReport(is, null, ds);
		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
	        printServiceAttributeSet.add(new PrinterName(printerName, null));

	        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	        //printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
	        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
	        int noOfCopies = 1;
	        printRequestAttributeSet.add(new Copies(noOfCopies));
	        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	          exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
	          exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	          
	          try
	          {
	            exporter.exportReport();
	          } catch (Exception e) {
	        
	          e.printStackTrace();
	          throw e;
	          
	          }
		
		
		
	}
	
	//Start changes for SFS-June Release 
	
	
	public static void jasperRePrintPackSlipcollate(Document inDoc,Map imagemap)throws Exception 
	{
		JRXmlDataSource ds = null;
		String selectExpression = "/" + inDoc.getDocumentElement().getTagName();
		
		 ds = new JRXmlDataSource(inDoc, selectExpression);
		
		 String type = "CollatePrintingEnabled";
		 YRCJasperReportDefinition jrd=KOHLSPCAUtils.getReport(type);
		 
		 String reportName=jrd.getFile();
		
 
		
		 KOHLSPCAUtils ma = new KOHLSPCAUtils();
		 InputStream is = ma.getReportStream(reportName);
		 String printerName = KOHLSPCAUtils.getDefaultPrinterID();
		 JasperPrint jp = JasperFillManager.fillReport(is, imagemap, ds);
		 PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
	        printServiceAttributeSet.add(new PrinterName(printerName, null));

	        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
	        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
	        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
	        //printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 120, 120, 25400));
	        printRequestAttributeSet.add(MediaSizeName.NA_LEGAL);

	        printRequestAttributeSet.add(OrientationRequested.PORTRAIT);
	        int noOfCopies = 1;
	        printRequestAttributeSet.add(new Copies(noOfCopies));
	        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
	          exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
	          exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
	          
	          try
	          {
	            exporter.exportReport();
	          } catch (Exception e) {
	        
	          e.printStackTrace();
	          throw e;
	          
	          }
		
		
		
	}
	
	
	//End changes for SFS June Release
	
	
	
	
	 protected InputStream getReportStream(String reportName) throws Exception
	  {
	    InputStream is = getClass().getResourceAsStream(reportName);
	    if (is == null) {
	      Exception ex = new Exception("Report File not found in classpath" + reportName);
	    
	      throw ex;
	    }
	    return is;
	  }

	
	/*
	 * Pawan - Drop 2- Start
	 */
	 public static void rearrangePrinterID(Element outputGetDeviceList, String strPrinterID) {
			Element eDevice = (Element) YRCXPathUtils.evaluate(outputGetDeviceList,
					"/Devices/Device[@DeviceId='"
							+ strPrinterID + "']", XPathConstants.NODE);

			if (!YRCPlatformUI.isVoid(eDevice)) {

				// This code to show the New Container Number for auto generated
				// container
				// Walk backwards until we find the first sibling in the parent
				//
				Node previousNode = eDevice.getPreviousSibling();
				if(!YRCPlatformUI.isVoid(previousNode)){
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
				}

			} else {
				YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),
						YRCPlatformUI.getString("ERROR_DEFAULTING_PRINTER_ID"));
			}	
		}	
	 //Pawan - Drop 2- End
	 
	 //Srikanth --start
	 
	 public static void loadJasperReports(String pluginId)    {
	        IConfigurationElement reportPointElements[] = Platform.getExtensionRegistry().
	        getConfigurationElementsFor("com.yantra.yfc.rcp.YRCJasperReports");
	        if(reportPointElements == null)
	            return;
	        for(int i = 0; i < reportPointElements.length; i++)
	        {
	            String id = reportPointElements[i].getNamespace();
	            if(pluginId.equalsIgnoreCase(id))
	            {
	                YRCJasperReportDefinition report = new YRCJasperReportDefinition();
	                report.setId(reportPointElements[i].getAttribute("id"));
	                report.setDescription(reportPointElements[i].getAttribute("description"));
	                report.setPermissionId(reportPointElements[i].getAttribute("permissionId"));
	                report.setFile(reportPointElements[i].getAttribute("file"));
	                report.setPluginId(pluginId);
	                reportsMap.put(report.getId(), report);
	            }
	        }

	    }
	 public static YRCJasperReportDefinition getReport(String reportId)   {
		 	loadJasperReports(KOHLSPCAConstants.A_JASPER_PRJ_ROOT);
	        return (YRCJasperReportDefinition)reportsMap.get(reportId);
	    }

	    public static HashMap getReportsMap()    {
	        return reportsMap;
	    }

	    private static HashMap reportsMap = new HashMap();
	    
}
