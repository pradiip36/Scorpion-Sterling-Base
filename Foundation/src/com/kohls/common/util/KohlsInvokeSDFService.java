package com.kohls.common.util;

import java.util.Properties;
import java.util.Set;

import org.w3c.dom.Document;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.util.YFCUtils;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsInvokeSDFService implements YIFCustomApi {

	private Properties props;
	public void setProperties(Properties sysProp) throws Exception {
		if (sysProp != null) {
			this.props = sysProp;
		}
	}
	
	/**
	 * Logger Instance.
	 */
	private static YFCLogCategory logger = YFCLogCategory.instance(KohlsInvokeSDFService.class.getName());
			

	/**
	
	/**
	 * Creates a new instance of InvokeSterling CommerceService
	 */
	public KohlsInvokeSDFService() {
	}

	/**
	 * This method invokes a SDF service.
	 * <p>
	 * The method acts as wrapper to invoke SDF service as the OOB service
	 * invocation component does does not return the response of the service
	 * being called.
	 * 
	 * <p>
	 * The service to be invoked should be configured as an API argument with
	 * the key "ServiceName" The exceptions can be supressed by
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param inDoc
	 *            Input Document to be passed to the Service.
	 * @return Input document is returned as the output.
	 * @throws Exception
	 */
	public Document invokeService(YFSEnvironment env, Document inDoc)
	throws Exception {
		logger.verbose("Entering invokeService of KohlsInvokeSDFService");
		Document outDoc = inDoc; // set this to default
		String serviceName = props.getProperty(KohlsConstants.SERVICE_NAME);
		
		if (logger.isVerboseEnabled())
			logger.verbose("Invoking service [ " + serviceName + "] Document ["
					+ KohlsXMLUtil.getXMLString(inDoc) + "]");
		
		String supressExceptionFlag = props.getProperty(KohlsConstants.KEY_SUPRESS_EXCEPTION);
		
		boolean supressException = (new Boolean(supressExceptionFlag))
				.booleanValue();
		if (supressException) {
			// if we have to supress exception we catch it else let the calling
			// method do the handling
			try {
				outDoc = KohlsCommonUtil.invokeService(env, serviceName, inDoc);
			} catch (Exception e) {
				Object[] args = new Object[] { serviceName };
			}
		} else {
			outDoc = KohlsCommonUtil.invokeService(env, serviceName, inDoc);
		}
		
		logger.verbose("Exiting invokeService of KohlsInvokeSDFService");
		return outDoc;
		}
	
	/*
	 * 
	 * This method will call multiple services 
	 * and all the services are called with the same input xml.
	 * 
	 * The service to be invoked should be configured as an API argument with
	 * the key which starts with "ServiceName" The exceptions can be supressed by
	 * passing "SuppressException" key
	 * 
	 * @param env
	 *            Sterling Commerce Environment Context.
	 * @param inDoc
	 *            Input Document to be passed to the Service.
	 *            
	 * @ output of this service will be the 
	 * combined ouput of all the service calls.
	 * 
	 */
	public Document invokeServicesAndMergeOutput(YFSEnvironment env, Document inDoc)
	throws Exception {
		logger.verbose("Entering invokeServicesAndMergeOutput() of KohlsInvokeSDFService");		
		Document outDoc = inDoc; // set this to default
		//String NoOfServicesToBeInvoked = props.getProperty("NoOfServices");
		//String serviceName = props.getProperty(KohlsConstants.SERVICE_NAME);
		if(props != null){
			YFCElement yMultiApis = YFCDocument.createDocument(KohlsConstants.ELEMENT_MULTI_APIS).getDocumentElement();
			Set<String> propertyNames = props.stringPropertyNames();		
			if(!propertyNames.isEmpty()){
				String[] arProperties = propertyNames.toArray(new String[0]);
				Document tmpOutDoc=null;
				for(int i=0;i<arProperties.length;i++){					
					if(arProperties[i].startsWith(KohlsConstants.SERVICE_NAME)){
						String serviceName = props.getProperty(arProperties[i]);						
						if (logger.isVerboseEnabled())
							logger.verbose("Invoking service [ " + serviceName + "] Document ["
									+ KohlsXMLUtil.getXMLString(inDoc) + "]");
						
						String supressExceptionFlag = props.getProperty(KohlsConstants.KEY_SUPRESS_EXCEPTION);
							
							boolean supressException = (new Boolean(supressExceptionFlag))
									.booleanValue();
							if (supressException) {
								// if we have to supress exception we catch it else let the calling
								// method do the handling
								try {
									tmpOutDoc = KohlsCommonUtil.invokeService(env, serviceName, inDoc);
								} catch (Exception e) {
									Object[] args = new Object[] { serviceName };
								}
							} else {
								tmpOutDoc = KohlsCommonUtil.invokeService(env, serviceName, inDoc);
							}				
						}
						yMultiApis.importNode(YFCDocument.getDocumentFor(tmpOutDoc).getDocumentElement());
					}
				outDoc = yMultiApis.getOwnerDocument().getDocument();
				}
			}
		logger.verbose("Exiting invokeServicesAndMergeOutput() of KohlsInvokeSDFService");
		return outDoc;
		}
	}
