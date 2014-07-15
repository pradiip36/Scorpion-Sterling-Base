package com.kohls.shipment.api;

import java.io.File;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSWritePDFOutPutInDir extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSWritePDFOutPutInDir.class.getName());

	public Document writePDFInDirectory(YFSEnvironment env,
			Document inputDoc) throws Exception
			{

		try{
			JRXmlDataSource ds = null;
			String selectExpression = "/" + inputDoc.getDocumentElement().getTagName();
			ds = new JRXmlDataSource(inputDoc, selectExpression);
			String reportName = KohlsConstant.V_KOHLS_RE_PACKSHIP_JASPER;
			InputStream is = getReportStream(reportName);
			JasperPrint jp = JasperFillManager.fillReport(is, null, ds);
			try{
				HashMap<String,String> DirMap = new HashMap<String,String>();
				String sCodeType = KohlsConstant.V_COMMON_CODE_TYPE_FOR_SHIPNODE_DIR;
				Element eleCommonCodeList = callGetCommonCodeList(env,sCodeType);
				NodeList ndlCommonCode = eleCommonCodeList.getElementsByTagName(KohlsConstant.E_COMMON_CODE);
				for(int i=0; i<ndlCommonCode.getLength(); i++){
					Element eleCommonCode = (Element)ndlCommonCode.item(i);
					DirMap.put(eleCommonCode.getAttribute(KohlsConstant.A_CODE_VALUE), eleCommonCode.getAttribute(KohlsConstant.A_CODE_SHORT_DESC));

				}

				Element eleInput = inputDoc.getDocumentElement();
				String sShipNode = eleInput.getAttribute(KohlsConstant.A_SHIP_NODE);
				String sShipmentNumber = eleInput.getAttribute(KohlsConstant.A_SHIPMENT_NO);
				String sPrintBatchNumber = eleInput.getAttribute(KohlsConstant.A_BATCH_ID);
				String sDirToWrite = null ;
				if(!YFCObject.isVoid(sShipNode))
				{	
					if(!YFCObject.isVoid(DirMap.get(sShipNode)))
					{
						if(!YFCObject.isVoid(sPrintBatchNumber))
						{
							sDirToWrite = DirMap.get(sShipNode) + File.separator + sPrintBatchNumber;
						}else
						{
							sDirToWrite = DirMap.get(sShipNode);
						}

					}

					if(!YFCObject.isVoid(sDirToWrite))
					{
						File theDir = new File(sDirToWrite);
						if (!theDir.exists())
						{
							log.debug("creating directory: " + sDirToWrite);
							theDir.mkdir();
						}

						Calendar cal = Calendar.getInstance();
						DateFormat dateFormat = new SimpleDateFormat( KohlsConstant.V_DATE_TIME_FORMAT);
						String sTime = dateFormat.format(cal.getTime());
						String strFileName=sShipmentNumber + "_" + sTime + ".pdf";
						String sFilepath = "";
						sFilepath = sDirToWrite + File.separator + strFileName;

						JasperExportManager.exportReportToPdfFile(jp, sFilepath);


					}

				}
			}catch(Exception ex)
			{
				ex.printStackTrace();
			}



		}catch(Exception ex)
		{
			log.debug("Error In Writing to the Directory. Please Check Configuration");
			ex.printStackTrace();

		}

		return null;

			}

	protected InputStream getReportStream(String reportName)
	{
		InputStream is = getClass().getResourceAsStream(reportName);
		if (is == null) {
			YFCException ex = new YFCException("Report File not found in classpath");
			ex.setAttribute("ReportFile", reportName);
			throw ex;
		}
		return is;
	}

	private Element callGetCommonCodeList(YFSEnvironment env ,String sCodeType) throws Exception {
		Document outDoc = null;
		Document docInputForGetCommonCodeList = XMLUtil.createDocument(KohlsConstant.E_COMMON_CODE);
		Element eleCommonCodeList = docInputForGetCommonCodeList.getDocumentElement();
		eleCommonCodeList.setAttribute(KohlsConstant.A_CODE_TYPE,sCodeType);
		outDoc= KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_GET_COMMON_CODE_LIST, docInputForGetCommonCodeList);
		return outDoc.getDocumentElement();
	}

}