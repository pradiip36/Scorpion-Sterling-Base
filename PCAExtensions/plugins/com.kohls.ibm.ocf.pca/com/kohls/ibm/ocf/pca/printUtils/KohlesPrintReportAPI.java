package com.kohls.ibm.ocf.pca.printUtils;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.YRCDialog;
import com.yantra.yfc.rcp.utils.YRCJasperUtils;

public class KohlesPrintReportAPI {
	/**
	 * @param xpathExpression -
	 * @param inputElement -
	 * @param reportId -
	 * @return -
	 */
	public String executeReport(String xpathExpression, Element inputElement,String reportId,Map parameters) {

		String url = "";
		try {
			url = YRCJasperUtils.executeJasperReport((HashMap) parameters, 	xpathExpression, YRCJasperUtils.REPORT_TYPE_PDF, inputElement, reportId, false,false);
		}

		catch (Exception e) {
			e.printStackTrace();
		}
		return url;
	}

	public void openReport(String url){
		//YRCPlatformUI.openEditor(KohlesPrintReportBrowserEditor.ID_Editor, new YRCEditorInput(YRCXmlUtils.createFromString("<ReportDetails JasperUrl=\""+url+"\"/>").getDocumentElement(),new String[] { "JasperUrl" },YRCPlatformUI.getString("REPORT_DETAIL")));
		KohlesPrintReportBrowserPage printScreen = new KohlesPrintReportBrowserPage(new Shell(Display.getDefault()), SWT.NONE, url);
		YRCDialog dialog = new YRCDialog(printScreen, 900, 600, "Print Shipment Details", null);
		dialog.open();
	}
	
}
