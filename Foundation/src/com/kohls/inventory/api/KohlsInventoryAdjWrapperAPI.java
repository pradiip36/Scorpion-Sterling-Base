package com.kohls.inventory.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Irwan Chang
 * 
 * */

public class KohlsInventoryAdjWrapperAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsInventoryAdjWrapperAPI.class.getName());
	private Properties properties = new Properties();

	public KohlsInventoryAdjWrapperAPI() throws YIFClientCreationException {
		this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public Document adjustInventory(YFSEnvironment env, Document inputDoc) throws Exception {
		
		Element itemsElement = inputDoc.getDocumentElement();
		String quickMode = itemsElement.getAttribute("QuickMode");
		
		if (quickMode==null || "".equals(quickMode.trim())) {
			String globalQuickModeSetting = YFSSystem.getProperty("inventory.adjustment.UseQuickMode");
			if (globalQuickModeSetting==null || "".equals(globalQuickModeSetting.trim())) {
				globalQuickModeSetting = "N";
			}
			itemsElement.setAttribute("QuickMode", globalQuickModeSetting);
		}
		log.debug("<!-- Input to adjustInventory API -- >" + XMLUtil.getXMLString(inputDoc));
		return api.adjustInventory(env, inputDoc);
	}

	
	public void setProperties(Properties props) throws Exception { 
		this.properties = props;
	}
	public Properties getProperties() throws Exception {
		return properties;
	}
	public Object getProperty(Object key) throws Exception {
		return properties.get(key);
	}
	public String getProperty(String key, String defaultValue) throws Exception {
		return properties.getProperty(key, defaultValue);
	}
	public boolean containsKey(String key) throws Exception {
		return properties.containsKey(key);
	}

}
