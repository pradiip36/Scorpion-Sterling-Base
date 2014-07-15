package com.kohls.oms.api;

import java.util.Properties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
/**
 * This class is called on the ON_SUCCESS event of Confirm Draft Transfer Order In Transfer order Pipe line.
 * This class will navigate through all the lines of TO and takes the value of PrimeLineNo and set it to PackListType.
 *  
 * @author OASIS
 * Added for PackListType change -- OASIS_SUPPORT 16/05/2013 
 */

public class KohlsAddPackListTypeAPI implements YIFCustomApi {


	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsAddPackListTypeAPI.class.getName());
	private  YIFApi api;

	public  KohlsAddPackListTypeAPI() throws YIFClientCreationException {		 
		api = YIFClientFactory.getInstance().getLocalApi(); 		 
	}

	
	public void invoke(YFSEnvironment env,Document inXML) throws Exception{
		if(YFCLogUtil.isDebugEnabled()){

			log.debug("<!-------- Begining of KohlsAddPackListTypeAPI invoke method ----------- >" + XMLUtil.getXMLString(inXML));
		}
		//Taking values of OrderHeaderKey, EnterprizeCode from input
		Element eleRoot = inXML.getDocumentElement();
		String orderHeaderKey = eleRoot.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		
		
		//Preparing input Document for changeOrder API		
		YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleOrder = yfcDocChangeOrder.getDocumentElement();
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.TO_DOCUMENT_TYPE);
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, orderHeaderKey);
		yfcEleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		
		YFCElement yfcEleOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		yfcEleOrder.appendChild(yfcEleOrderLines);		

		NodeList ndlstOrderLines = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

		for(int i=0;i<ndlstOrderLines.getLength();i++){

			Element eleOrderLine = (Element)ndlstOrderLines.item(i);
			YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY));
			yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PACK_LIST_TYPE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
			yfcEleOrderLines.appendChild(yfcEleOrderLine);
		}
		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("Input XML to changeOrder: " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
		}

		api.changeOrder(env, yfcDocChangeOrder.getDocument());
		
		
	}
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}


}
