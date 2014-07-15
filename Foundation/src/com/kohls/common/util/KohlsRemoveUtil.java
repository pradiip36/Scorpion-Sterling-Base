package com.kohls.common.util;

import java.util.Enumeration;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsRemoveUtil implements YIFCustomApi {
	private Properties oProperties = new Properties();

	public Document removeUtil(YFSEnvironment env, Document docIn) throws TransformerException{
		Enumeration enumCondition = oProperties.propertyNames();
		String sXpathCount="0";
		String sAttrCount="0";

		while(enumCondition.hasMoreElements()){
			String sName = enumCondition.nextElement().toString();
			if ("XPATHCOUNT".equalsIgnoreCase(sName)){
				sXpathCount=oProperties.getProperty(sName);
			}else if("ATTRCOUNT".equalsIgnoreCase(sName)){
				sAttrCount = oProperties.getProperty(sName);
			}

		}
		
		//Element Removal
		
		for (int iIndex=1; iIndex<=Integer.parseInt(sXpathCount);iIndex++){
			
			String sXpath=oProperties.getProperty("XPATH_"+iIndex);
			CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		    NodeList nodeList = aCachedXPathAPI.selectNodeList(docIn, sXpath);
		    for (int i=0; i<nodeList.getLength();i++){
		    	Element ele = (Element)nodeList.item(i);
		    	(ele.getParentNode()).removeChild(ele);
		    }
		}
		
		//Attribute Removal
		for (int iIndex=1; iIndex<=Integer.parseInt(sAttrCount);iIndex++){
			
			String sAttrXpath=oProperties.getProperty("ATTR_"+iIndex);
			int AttrPosition = sAttrXpath.lastIndexOf("@");
			String sXpath=sAttrXpath.substring(0, AttrPosition - 1);
			String sAttr=sAttrXpath.substring(AttrPosition + 1);
			
			CachedXPathAPI aCachedXPathAPI = new CachedXPathAPI();
		    NodeList nodeList = aCachedXPathAPI.selectNodeList(docIn, sXpath);
		    for (int i=0; i<nodeList.getLength();i++){
		    	Element ele = (Element)nodeList.item(i);
		    	 ele.removeAttribute(sAttr);
		    }
		}

		return docIn;
	}

	@Override
	public void setProperties(Properties properties) throws Exception {
		// TODO Auto-generated method stub
		if (properties != null) {
			this.oProperties = properties;
		}
	}

}
