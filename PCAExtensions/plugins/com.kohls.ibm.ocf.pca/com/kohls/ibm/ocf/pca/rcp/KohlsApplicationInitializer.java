package com.kohls.ibm.ocf.pca.rcp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.yantra.yfc.rcp.IYRCApiCallbackhandler;
import com.yantra.yfc.rcp.IYRCApplicationInitializer;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;

public class KohlsApplicationInitializer implements IYRCApplicationInitializer {
	public static String strComputername;

	
	//Start changes for SFS June Release
	
	public static String strCollatePrintingFlag="";
	//End changes for SFS June Release
	
	public static Element eleVerifyTerminalProperties;

	public static PrintService[] printServices;

	public static Element elePrinterDevices;

	public static Element elePrinterTerminalPropertiesForUI;

	public void initalize() {
		YRCPlatformUI
				.trace("Initializing KohlsApplicationInitializer class ...");
		try {
			strComputername = InetAddress.getLocalHost().getHostName();
			isTerminalExisting();
			fetchPrinters();
			setPrinterListElement();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

	private void fetchPrinters() {
		YRCPlatformUI
				.trace("Initializing KohlsApplicationInitializer class ... Fetching Printers");
		printServices = PrintServiceLookup.lookupPrintServices(null, null);

	}

	/**
	 * Method to check if the System Name exists in our Database/ Its been
	 * registered. Input XML Passed to Service <SIMINIProperties TerminalID=""/>
	 *
	 */

	private void isTerminalExisting() {
		YRCPlatformUI
				.trace("Initializing KohlsApplicationInitializer class ... Check the Terminal ID");
		YRCApiContext ctx = new YRCApiContext();
		Document docInput = YRCXmlUtils
				.createDocument(KOHLSPCAConstants.KOHLS_SIM_INI_PROP);
		Element eleTerminalProperties = docInput.getDocumentElement();
		eleTerminalProperties.setAttribute(KOHLSPCAConstants.KOHLS_TERMINAL_ID,
				strComputername);
		ctx.setFormId(getFormId());
		ctx.setApiName("KohlsVerifyTerminalPropertiesSyncService");
		ctx.setInputXml(docInput);
		YRCPlatformUI.callApi(ctx, new IYRCApiCallbackhandler() {
			// --handleApiCompletion function used to call API & fetch the
			// response from API
			public void handleApiCompletion(YRCApiContext ctx) {

				if (ctx.getApiName().equals(
						"KohlsVerifyTerminalPropertiesSyncService")) {
					if (ctx.getInvokeAPIStatus() > 0) {

						eleVerifyTerminalProperties = ctx.getOutputXml()
								.getDocumentElement();
						elePrinterTerminalPropertiesForUI = (Element) eleVerifyTerminalProperties
								.cloneNode(true);
						Element elePElementUI = YRCXmlUtils.getChildElement(
								elePrinterTerminalPropertiesForUI,
								"SIMINIProperties");
						strCollatePrintingFlag=elePElementUI.getAttribute("CollatePrintingEnabled");
						if (!YRCPlatformUI.isVoid(eleVerifyTerminalProperties))
							if (!eleVerifyTerminalProperties.hasChildNodes()) {/*
								YRCPlatformUI.showInformation(YRCPlatformUI
										.getString("KHLS_INFO"), YRCPlatformUI
										.getString("NOT_REG"));
								return;

							*/}

					}
				}
			}

		});

	}

	private String getFormId() {
		return "com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer";
	}

	/**
	 * @return the eleVerifyTerminalProperties
	 */
	public static Element getEleVerifyTerminalProperties() {
		return eleVerifyTerminalProperties;
	}

	/**
	 * @param eleVerifyTerminalProperties
	 *            the eleVerifyTerminalProperties to set
	 */
	public static void setEleVerifyTerminalProperties(
			Element eleVerifyTerminalProperties) {

		KohlsApplicationInitializer.eleVerifyTerminalProperties = eleVerifyTerminalProperties;
		KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI = KohlsApplicationInitializer.eleVerifyTerminalProperties;
	}

	public void setPrinterListElement() {
		PrintService[] printService = KohlsApplicationInitializer.printServices;
		if (YRCPlatformUI.isVoid(printService) || printService.length == 0) {
			YRCPlatformUI.trace("No Printer Configured for this Terminal");
		} else {
			elePrinterDevices = YRCXmlUtils.createDocument("Devices")
					.getDocumentElement();
			Element eDevice = null;
			for (PrintService printer : printService) {
				eDevice = YRCXmlUtils.createChild(elePrinterDevices, "Device");
				String strPrinterName = printer.getName();
				YRCXmlUtils.setAttribute(eDevice, "DeviceId", strPrinterName);
				//YRCXmlUtils.setAttribute(eDevice, "PrinterType", arg2);
			}
		}
	}

	public static String getTerminalProperty(String strTerminalProperty) {
		String strPropertyValue = "";
		if (!YRCPlatformUI.isVoid(eleVerifyTerminalProperties)) {
			strPropertyValue = YRCXmlUtils.getAttribute(YRCXmlUtils
					.getChildElement(eleVerifyTerminalProperties,
							"SIMINIProperties"), strTerminalProperty);
		}
		return strPropertyValue;
	}

	public static void modifyTerminalPropertyForUISession(
			String strPropertyName, String PropertyValue) {
		if (!YRCPlatformUI
				.isVoid(KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI)) {
			Element ePropertyElement = YRCXmlUtils.getChildElement(
					elePrinterTerminalPropertiesForUI, "SIMINIProperties");
			YRCXmlUtils.setAttribute(ePropertyElement, strPropertyName,
					PropertyValue);

		}
	}

	public static String getTerminalPropertyForUISession(
			String strTerminalProperty) {
		String strPropertyValue = "";
		if (!YRCPlatformUI.isVoid(elePrinterTerminalPropertiesForUI)) {
			strPropertyValue = YRCXmlUtils.getAttribute(YRCXmlUtils
					.getChildElement(elePrinterTerminalPropertiesForUI,
							"SIMINIProperties"), strTerminalProperty);
		}
		return strPropertyValue;
	}
	
	public static void setTerminalPropertyForUISession(
			Element eleVerifyTerminalProperties) {

		KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI = eleVerifyTerminalProperties;
		
	}


}
