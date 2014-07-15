package com.kohls.shipment.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSGetStatusListAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KOHLSGetStatusListAPI.class.getName());
	Properties properties = null;
	
	private Properties props;

	/**
	* @param properties
	*            argument from configuration.
	*/
	public void setProperties(Properties props) {
	    this.props = props;
	}

	public Document getStatusList(YFSEnvironment env, Document inXML)
			throws Exception {

      
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("input  for KOHLSGetStatusListAPI : \n"
					+ XMLUtil.getXMLString(inXML));
		}
		  Document OutDoc = null;
			String strStatusList = props.getProperty(KohlsConstant.V_STATUS_LIST);
            if(!YFCObject.isVoid(strStatusList))
            {
            	String[] temp;
            	temp = strStatusList.split(";");
            	if(temp.length > 0)
            	{ YFCDocument inputDoc = YFCDocument.getDocumentFor(inXML);
            		YFCElement inputElem = inputDoc.getDocumentElement();
            		YFCElement eleComplexQry = inputElem.createChild("ComplexQuery");
            		YFCElement eleOrQry = eleComplexQry.createChild("Or");
            		for(int i=0; i<temp.length; i++){
            			YFCElement expEle = eleOrQry.createChild("Exp");
            			expEle.setAttribute("Name", "Status");
            			expEle.setAttribute("Value", temp[i]);
            			
            		}
            	}
            }
            OutDoc = KOHLSBaseApi
			.invokeAPI(env,"getStatusList",inXML);
            return OutDoc;
}
	
}
