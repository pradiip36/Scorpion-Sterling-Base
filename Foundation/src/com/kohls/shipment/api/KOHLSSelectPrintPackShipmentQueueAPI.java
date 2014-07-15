package com.kohls.shipment.api;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.ycp.core.YCPContext;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSSelectPrintPackShipmentQueueAPI extends KOHLSBaseApi {
	
	
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSGetShipmentDetailsAPI.class.getName());
	
	
	//private Properties properties = null;
	//final String Prefix = getPropertyValue(KohlsConstant.V_PREFIX);
	//final String Middle = getPropertyValue(KohlsConstant.V_MIDDLE_PRINT);
	//final String Suffix = getPropertyValue(KohlsConstant.V_SUFFIX);

	public Document selectPrintPackShipmentQueue(YFSEnvironment yfsEnvironment,
			Document inputDoc) throws Exception {
		
		String strPrintType=inputDoc.getDocumentElement().getAttribute(KohlsConstant.A_PRINT_TYPE);
		
		if(!strPrintType.equals(KohlsConstant.V_SINGLE)){
			String strBatchId = getTimeStampForPrintBatchNo() + "_" +getBatchNo(yfsEnvironment);
			Element inputElem = inputDoc.getDocumentElement();
			
			String shipNode = inputElem.getAttribute(KohlsConstant.A_SHIP_NODE);
			String TotalCarts = inputElem.getAttribute("TotalCarts");
			
			
			inputElem.setAttribute(KohlsConstant.A_BATCH_ID, strBatchId);
			insertBatchNoKlBatchPrintTable(yfsEnvironment,  strBatchId, TotalCarts,  shipNode);
		}
		
		if (YFCLogUtil.isDebugEnabled())
		{
			this.log.debug("<!-- Begining of KOHLSSelectPrintPackShipmentQueueAPI" +
					" selectPrintPackShipmentQueue method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		String shipNode = inputDoc.getDocumentElement().getAttribute(
				KohlsConstant.A_SHIP_NODE);

		//String name =  Prefix +shipNode+Middle + Suffix;

		KOHLSBaseApi.invokeService(yfsEnvironment, "KOHLSGetJobsForPickSlipPrintService", inputDoc);
		
		return inputDoc;
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

	/**
     * This Method is called to insert BatchNO with No of Shipments in custom table.
     */
	

	
	private void insertBatchNoKlBatchPrintTable(YFSEnvironment yfsEnvironment, String strGenratedBatchNO, String batchCount, String node) throws Exception{
		
//		 Creating input to insert batch No in KL_BATCH_PRINT table
		Document docInputBatchPrintToDB = XMLUtil
				.createDocument(KohlsConstant.E_BATCH_PRINT);
		Element eleBatchPrint = docInputBatchPrintToDB
				.getDocumentElement();
		eleBatchPrint.setAttribute(KohlsConstant.A_BATCH_ID,
				strGenratedBatchNO);
		eleBatchPrint.setAttribute(KohlsConstant.A_REPRINT_FLAG,
				KohlsConstant.NO);
		eleBatchPrint.setAttribute(KohlsConstant.A_NO_OF_SHIPMENTS,
				batchCount);
		eleBatchPrint.setAttribute(KohlsConstant.A_STATUS,
				KohlsConstant.V_AWAITING_PRINT);
		eleBatchPrint.setAttribute(KohlsConstant.A_USER,
				yfsEnvironment.getUserId());
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE_TYPE,
				KohlsConstant.A_MULTI_PRINT);
		eleBatchPrint.setAttribute(KohlsConstant.A_NODE,
				node);
		// invoking BatchNo insert Service
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("inserting Batch NO In custom table : "
					+ XMLUtil.getXMLString(docInputBatchPrintToDB));
		}
		KOHLSBaseApi.invokeService(yfsEnvironment, "UpadateBatchPrintTableService",
				docInputBatchPrintToDB);

	} 
	
	
}
