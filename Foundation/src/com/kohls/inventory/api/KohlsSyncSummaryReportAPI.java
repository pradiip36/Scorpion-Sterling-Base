package com.kohls.inventory.api;

import java.util.Properties;

import org.apache.log4j.Level;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.custom.util.log.Logger;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsSyncSummaryReportAPI implements YIFCustomApi {

	private YIFApi api;
	private Logger log = new Logger(KohlsSyncSummaryReportAPI.class.getName());
	private Properties _properties = null;
	

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsSyncSummaryReportAPI() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public Document executeSyncRptProcess(YFSEnvironment env, Document inXML) throws Exception {

		String strLogData = "";

		log.debug("Beginning of KohlsSyncSummaryReportAPI executeSyncRptProcess method");
		
		if (YFCLogUtil.isDebugEnabled()) {

			this.log.verbose("<!-- Begining of KohlsSyncSummaryReportAPI executeSyncRptProcess method -- >"
					+ KohlsUtil.extractStringFromDocument(inXML));
		}

		String strShipNode = null;

		Element eleInputList = inXML.getDocumentElement();
		NamedNodeMap ndInlstGet = eleInputList.getAttributes();

		Node ndInData = ndInlstGet.getNamedItem(KohlsXMLLiterals.E_SHIP_NODE);
		strShipNode = ndInData.getNodeValue();
		
		log.debug("Retrieved ShipNode:" + strShipNode);
		
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("Retrieved ShipNode: "
					+ strShipNode);
		}
		
		String job_name = "", email_id = "", kohlsJobPath = "";
		if (_properties != null) {
			job_name = _properties.getProperty("JobName");
			email_id = _properties.getProperty("Email");
			
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Job Name: " + job_name);
				this.log.debug("Email ID: " + email_id);
			}
		}
		
		kohlsJobPath = YFSSystem.getProperty("kohls_job_path");
		
		log.debug("The kohlsJobPath is: " + kohlsJobPath);
		
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("kohlsJobPath: " + kohlsJobPath);
		}
		
		log.debug("The script being called is: /usr/bin/ksh " + kohlsJobPath + job_name + " " + strShipNode + " " + email_id);
		
		if (YFCLogUtil.isDebugEnabled()) {
			this.log.debug("The script being called is: /usr/bin/ksh" + kohlsJobPath + job_name + " " + strShipNode + " " + email_id);
		}

		if (kohlsJobPath != null && kohlsJobPath.length() > 0) {
				
			try
			{		
				Process proc = Runtime.getRuntime().exec("/usr/bin/ksh " + 
						kohlsJobPath + job_name + " " + strShipNode + " " + email_id);
				//int exitValue = proc.exitValue();
				
				if (YFCLogUtil.isDebugEnabled()) {
					this.log.debug("Process has been executed!  Check logs in /logs/<env>/of!");
				}
			}
			catch (Exception e)
			{
				log.log(Level.ERROR, e);
				e.printStackTrace();
			}
			
			log.debug("Completed executeSyncRptProcess method for the Inventory Sync Summary Report Job " + job_name + " at " + strShipNode);
			
			if (YFCLogUtil.isDebugEnabled()) {
				this.log.debug("Called Inventory Sync Summary Report Job: "
						+ job_name);
			}
		}
		
		return inXML;
	}
	
	public void setProperties(Properties prop) throws Exception {
        this._properties = prop;
    }
}