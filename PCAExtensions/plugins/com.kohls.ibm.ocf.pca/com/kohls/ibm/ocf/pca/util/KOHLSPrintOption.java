package com.kohls.ibm.ocf.pca.util;

import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.xml.xpath.XPathConstants;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXPathUtils;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfc.rcp.utils.YRCJasperUtils;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSException;

public class KOHLSPrintOption {

	public Document executemethod(Document imputxml, Document lblprt) {

		NodeList containers = null;
		Element container = null;
		String b64 = null;
		byte[] label = null;
		String printerName = null;
		PrintService[] services = null;
		PrintService lblService = null;
		DocPrintJob job = null;
		DocFlavor flavor = null;
		Doc printDoc = null;
		Map parameters = new HashMap();
		Document outdoc = null;

		try {
			containers = lblprt.getElementsByTagName("Container");
			int containerscnt = containers.getLength();
			
			if (containers.getLength() == 0) {
				// TODO -- log error
				final YFSException yfs = new YFSException(
						"No containers found in input document.");
				throw yfs;
			}// end: if (containers.getLength() == 0)
			//
			container = (Element) containers.item(0);
			
			b64 = container.getAttribute("ExtnB64Label");
			
			if ((b64 == null) || (b64.length() == 0)) {
				// TODO -- log error
				final YFCException yfs = new YFCException(
						"Empty label returned from ProShip.");
				throw yfs;
			}
			label = Base64.decode(b64);

			InputStream imageStream = new ByteArrayInputStream(label);
			BufferedImage imageBufferdefaultLogo = ImageIO.read(imageStream);
			
			

			if(imageBufferdefaultLogo==null)
			{
				final YFCException yfs = new YFCException(
				"Proship Is not sending the PNG Image-Deconde for the Image is Blank");
				throw yfs;
				
			}
		
			
			//Change for Image Stretching Fix.
			//imageBufferdefaultLogo=rotate90ToRight(imageBufferdefaultLogo);
			
			int height = imageBufferdefaultLogo.getHeight();
			int width = imageBufferdefaultLogo.getWidth();
			imageBufferdefaultLogo = KOHLSImageRotation.createResizedCopy(imageBufferdefaultLogo,width,height, 90, 0,0);
			//End of Defect stretchfix
			parameters.put("SHIP_LABEL", imageBufferdefaultLogo);

			int noofline = getLineCount(imputxml);
			
			
			
			if(noofline>0)
			{
				 outdoc = KOHLSCollateUtil.getReportCompatableXML(imputxml,noofline);
			}
			
			else
			{
				outdoc=imputxml;
			}
			
			KOHLSPCAUtils.jasperRePrintPackSlipcollate(outdoc, parameters);

		} catch (Exception ex) {
			// TODO -- log error
			final YFCException yfs = new YFCException(ex);
			yfs.setStackTrace(ex.getStackTrace());
			throw yfs;
		}
		return imputxml;
	}



	

	/**
	 * This method to generate PDF for pack slip instead of sending it to printer
	 * @param jasperName
	 *          Jaseper File Name
	 * @param paramMap
	 *           jasper report parameter map
	 * @param datasrc
	 *           Jasper Report Data source
	 * @param pdfName
	 *           Target.output PDF file nanme
	 * @throws Exception
	 *         
	 */
	public static void generatePDFReport(String jasperName, Map paramMap,
			JRXmlDataSource datasrc, String pdfName) throws Exception {
		JasperPrint print = JasperFillManager.fillReport(jasperName, paramMap,
				datasrc);
		JRExporter exporter = new net.sf.jasperreports.engine.export.JRPdfExporter();
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, pdfName);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, print);
		exporter.exportReport();

	}
	
		
	public static int getLineCount (Document outputDoc) throws Exception {  
//		YRCPlatformUI.trace("Document passed to the method parseApiOutput is ", YRCXmlUtils.getString(outputDoc));

		Element elmShipment = outputDoc.getDocumentElement();
		
		NodeList nooflinelist = (NodeList) YRCXPathUtils.evaluate(elmShipment,"//Container/ContainerDetails/ContainerDetail", XPathConstants.NODESET);
		
		int noofline = nooflinelist.getLength();
		
		int totalnopage=KOHLSPCAConstants.TOTATL_NO_PAGE_VALUE;
		
		int initialvalue=0;
		
		int noofdummyspace = noofline%6;
		
		
			if(noofdummyspace==5)
			{
				initialvalue=KOHLSPCAConstants.TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_5;
			}
			else if(noofdummyspace==0)
			{
				initialvalue=KOHLSPCAConstants.TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_4;
			}
			else if(noofdummyspace==3)
			{
				initialvalue=KOHLSPCAConstants.TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_1;
			}
			else if(noofdummyspace==2)
			{
				initialvalue=KOHLSPCAConstants.TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_2;
			}
			else if(noofdummyspace==1)
			{
				initialvalue=KOHLSPCAConstants.TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_3;
			}
			
	
		
		
		
		
		return initialvalue;

		}

}
