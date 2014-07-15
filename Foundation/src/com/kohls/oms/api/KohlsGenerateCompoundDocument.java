package com.kohls.oms.api;

import java.util.Properties;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsInvokeSDFService;
import com.kohls.common.util.KohlsXMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/*
 * This class will invoke the SDF service and then append the input to 
 * the output of the service. 
 * The Service to invoke will be passed as argument to this component.
 * argument name will be "ServiceName"
 * argument value will be <Name of service to be invoked>
 * 
 */
public class KohlsGenerateCompoundDocument implements YIFCustomApi {
	private Properties props;
	public void setProperties(Properties sysProp) throws Exception {
		if (sysProp != null) {
			this.props = sysProp;
		}
	}
	
	/**
	 * Logger Instance.
	 */
	private static YFCLogCategory logger = YFCLogCategory.instance(KohlsGenerateCompoundDocument.class.getName());
	
	public Document mergeDocument(YFSEnvironment env, Document inDoc){
		logger.verbose("Invoking the mergeDocument() of the KohlsGenerateCompoundDocument Class");
		
		Document outDoc=null;
		String serviceName = props.getProperty(KohlsConstants.SERVICE_NAME);
		
		if (logger.isVerboseEnabled())
		logger.verbose("Invoking service [ " + serviceName + "] Document ["
				+ KohlsXMLUtil.getXMLString(inDoc) + "]");
		
		try{
			outDoc = KohlsCommonUtil.invokeService(env, serviceName, inDoc);
			YFCElement yele = YFCDocument.getDocumentFor(outDoc).getDocumentElement();
			YFCElement yeleIN = YFCDocument.getDocumentFor(inDoc).getDocumentElement();
			yele.importNode(yeleIN);
		}
		catch(Exception e)
		{
			YFSException yfsException = new YFSException();
			yfsException.setErrorCode("EXCEPTION WHILE INVOKING SERVICE USING KohlsGenerateCompoundDocument component");
			yfsException.setErrorDescription("Exception while invoking the service::" + serviceName);
			throw yfsException;
		}
		logger.verbose("Exitting the mergeDocument() of the KohlsGenerateCompoundDocument Class");
		return outDoc;
	}
	
}
