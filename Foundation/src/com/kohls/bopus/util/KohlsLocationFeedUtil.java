package com.kohls.bopus.util;

import java.rmi.RemoteException;

import org.w3c.dom.Document;

import com.kohls.common.util.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsLocationFeedUtil {
	
	

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsLocationFeedUtil.class.getName());

	/**
	 * Helper method which calls API.
	 * 
	 * @param env
	 * @param input
	 * @param apiName
	 * @param templateName
	 * 
	 * @exception <code>Exception</code>
	 * @return <code>Document</code>
	 */

	public static Document callAPI(YFSEnvironment env, Document input,
			String apiName, String templateName)
			throws YIFClientCreationException, RemoteException, YFSException {
		if (templateName != null) {
			env.setApiTemplate(apiName, templateName);
		}
		
		YIFApi api = YIFClientFactory.getInstance().getApi();
		log.debug("inp doc is:"+ XMLUtil.getXMLString(input));
		log.debug("api name is:"+ apiName);
		Document outDoc = api.invoke(env, apiName, input);

		if (templateName != null) {
			env.clearApiTemplate(apiName);
		}
		return outDoc;
	}

	/**
	 * Helper method which calls a Service.
	 * 
	 * @param env
	 * @param input
	 * @param apiName
	 * 
	 * @exception <code>Exception</code>
	 * @return <code>Document</code>
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 */

	public static Document callService(YFSEnvironment env, Document input,
			String serviceName, String templateName)
			throws YIFClientCreationException, RemoteException, YFSException {

		if (templateName != null) {
			env.setApiTemplate(serviceName, templateName);
		}
		YIFApi api = YIFClientFactory.getInstance().getApi();
		Document outDocument = api.executeFlow(env, serviceName, input);
		env.clearApiTemplate(serviceName);
		return outDocument;
	}

	/**
	 * Helper method which calls API from an Agent.
	 * 
	 * @param env
	 * @param input
	 * @param apiName
	 * @param templateName
	 * 
	 * @exception <code>Exception</code>
	 * @return <code>Document</code>
	 */

	public static Document callAPIFromAgent(YFSEnvironment env, Document input,
			String apiName, String templateName)
			throws YIFClientCreationException, RemoteException, YFSException {

		if (templateName != null) {
			env.setApiTemplate(apiName, templateName);
		}
		YIFApi api = YIFClientFactory.getInstance().getLocalApi();
		return api.invoke(env, apiName, input);
	}



	/**
	 * Create default Api object and return it.
	 * 
	 * @return <code>YIFApi <code>
	 * @throws <Code>YIFClientCreationException <Code>
	 */
	public static YIFApi getApi() throws YIFClientCreationException {
		return (YIFApi) YIFClientFactory.getInstance().getApi();
	}


	/**
	 * 
	 * Method to set objects in environment object
	 * 
	 * @param env
	 *            - YFSEnvironment Object passed to the API.
	 * @param objectTobeSet
	 *            object to be set in env
	 * @param objectKey
	 *            key for the object
	 * 
	 */
	public static void setInEnvironment(final YFSEnvironment env,
			final Object objectTobeSet, final String objectKey) {
		env.setTxnObject(objectKey, objectTobeSet);
	}

	/**
	 * This method read a property value from yfs.Properties files. it calls the
	 * getProperty API to get the property value.
	 * 
	 * @param env
	 * @param propName
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 * @throws YFSException
	 */



}
