package com.kohls.shipment.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;

import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class Calls Singles Print Class for each  PrintPackShipment element
 * 
 * @author Puneet
 *
 */
public class KOHLSInvokeSinglesMassPrintAPI {


	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSInvokeSinglesMassPrintAPI.class.getName());
	
	/**
	 * This method calls KOHLSProcessPrintPackSlipService service for each PrintPackShipment element
	 * 
	 * @param yfsEnvironment
	 * 					YFSEnvironment
	 * @param inputDoc
	 * 					Document
	 * @throws Exception
	 * 					e
	 */
	public Document InvokeSinglesMassPrint(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception	{
          Document outDoc = null;
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- Begining of KOHLSInvokeSinglesMassPrintAPI" +
					" InvokeSinglesMassPrint method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		if(!YFCObject.isVoid(inputDoc)) {
			
			Element eleInputDoc = inputDoc.getDocumentElement();
			String strPrinterID=eleInputDoc.getAttribute(KohlsConstant.A_PRINTER_ID);
			String sShipNode = null ;
			NodeList nlPrintPackShipment = eleInputDoc.getElementsByTagName(KohlsConstant.E_PRINT_PACK_SHIPMENT);
			String sPrintBatchNumber = getTimeStampForPrintBatchNo() + "_" +getBatchNo(yfsEnvironment);
			
			for(int i =0;i<nlPrintPackShipment.getLength();i++){
				Element PrintPackShipmentElem = (Element) nlPrintPackShipment.item(i);
				sShipNode = PrintPackShipmentElem.getAttribute(KohlsConstant.A_SHIP_NODE);
				
               String sTotalShipments= PrintPackShipmentElem.getAttribute(KohlsConstant.A_TOTAL_SHIPMENTS);
				
				
				if(!YFCObject.isVoid(sPrintBatchNumber))
				{
					PrintPackShipmentElem.setAttribute(KohlsConstant.A_BATCH_ID, sPrintBatchNumber);
				}
				 DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		        Document inputPrintdoc = builder.newDocument();
		        Element eleImported = (Element) inputPrintdoc
				.importNode(PrintPackShipmentElem, true);
		        inputPrintdoc.appendChild(eleImported);
		        /*
				 * Bhaskar- start
				 * 
				 *
		        Document OutPutDoc = KOHLSBaseApi.invokeService(yfsEnvironment,
						KohlsConstant.API_KOHLS_PRINT_PACK_SLIP_SERVICE, inputPrintdoc);
				
				Bhasker-End
				*/
				
				// call KohlsRDCMassSinglePrintService this is new Service
				System.out.println("KOHLSRDCMassSinglePrintService....."+XMLUtil.getXMLString(inputPrintdoc));
				Document OutPutDoc = KOHLSBaseApi.invokeService(yfsEnvironment,
						"KOHLSRDCMassSinglePrintService", inputPrintdoc);
			}
			
			if(!YFCObject.isVoid(sShipNode))
			{
			Document docInputForMassPrintSinglesListService = XMLUtil
			.createDocument(KohlsConstant.E_SHIPMENT);
			Element docInputElem = docInputForMassPrintSinglesListService.getDocumentElement();
			docInputElem.setAttribute(KohlsConstant.A_SHIP_NODE, sShipNode);
			
			outDoc =KOHLSBaseApi.invokeService(yfsEnvironment,
					KohlsConstant.SERVICE_GET_MASS_PRINT_SINGLES_LIST_SERVICE, docInputForMassPrintSinglesListService);
			
			outDoc.getDocumentElement().setAttribute(KohlsConstant.A_BATCH_ID, sPrintBatchNumber);
			outDoc.getDocumentElement().setAttribute(KohlsConstant.A_PRINTER_ID, strPrinterID);
			
			}
		}
		
		if (YFCLogUtil.isDebugEnabled()) {
			
			this.log.debug("<!-- End of KOHLSProcessPrintPackSlipService" +
					" InvokeSinglesMassPrint method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		return outDoc;
	}
	
	private String getBatchNo(YFSEnvironment env) throws Exception {
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSSelectPrintPackShipmentQueueAPI" +
			" getBatchNo method -- >");
		}
		Statement statement = null;
		ResultSet resultSet = null;
		YCPContext context = (YCPContext) env;
		Connection connection = context.getDBConnection();
		String strBatchId = "";

		try {
			statement = connection.createStatement();
			resultSet = statement
			.executeQuery(KohlsConstant.SQL_STATEMENT_FOR_SEQ_YFS_PRINT_BATCH_ID);
			resultSet.next();
			strBatchId = resultSet.getString(KohlsConstant.A_BATCH_ID);
		} catch (SQLException sqle) {
			sqle.printStackTrace();

		} finally {
			try {

				resultSet.close();
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return strBatchId;
	}
	String getTimeStampForPrintBatchNo(){
	    Calendar cal = Calendar.getInstance();
	    DateFormat dateFormat = new SimpleDateFormat(KohlsConstant.V_DATE_FORMAT);
	    return dateFormat.format(cal.getTime());
	    }
	private String getPropertyValue(String vPrefix) {

		return vPrefix;
	}
}