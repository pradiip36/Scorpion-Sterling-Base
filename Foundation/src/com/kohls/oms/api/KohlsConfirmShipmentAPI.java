package com.kohls.oms.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsConfirmShipmentAPI implements YIFCustomApi {


	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsConfirmShipmentAPI.class.getName());
	private  YIFApi api;

	public  KohlsConfirmShipmentAPI() throws YIFClientCreationException {		 
		api = YIFClientFactory.getInstance().getLocalApi(); 		 
	}

	
	public void confirmShipment(YFSEnvironment env,Document inXML) throws Exception{
		if(YFCLogUtil.isDebugEnabled()){

			log.debug("<!-------- Begining of KohlsConfirmShipmentAPI invoke method ----------- >" + XMLUtil.getXMLString(inXML));
		}
		
		Element eleRoot = inXML.getDocumentElement();
		Element eleShipment = com.kohls.common.util.XMLUtil.getChildElement(eleRoot, KohlsConstant.E_SHIPMENT);
		String strShipmentKey = "";
		if(!com.kohls.common.util.XMLUtil.isVoid(eleShipment)){
			strShipmentKey = eleShipment.getAttribute(KohlsConstant.A_SHIPMENT_KEY);
			if(!"".equals(strShipmentKey)){
				Document docConfirmShipment = com.kohls.common.util.XMLUtil.createDocument(KohlsConstant.E_SHIPMENT);
				docConfirmShipment.getDocumentElement().setAttribute(KohlsConstant.A_SHIPMENT_KEY, strShipmentKey);

				if(YFCLogUtil.isDebugEnabled()){			
					log.debug("Input XML to confirmShipment: " + XMLUtil.getXMLString(docConfirmShipment));
				}

				api.confirmShipment(env, docConfirmShipment);
			}
			
		}

		
		
	}
	
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}


}