package com.kohls.shipment.api;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsAddContainerToShipmentMergeOutput {
	
	private static final YFCLogCategory log = YFCLogCategory
	.instance(KohlsAddContainerToShipmentMergeOutput.class.getName());
	
	public Document stampProShipResponse(YFSEnvironment env, Document inDoc){
		
		Document mergeDoc = null;
		Element eleInputDocument = inDoc.getDocumentElement();
		Document docProShipResponse = (Document)env.getTxnObject(KohlsConstant.PROSHIP_RESPONSE);
		Element eleProShipResponse = docProShipResponse.getDocumentElement();
		if(!XMLUtil.isVoid(docProShipResponse)){
			try {
				mergeDoc = XMLUtil.createDocument(KohlsConstant.MERGED_DOCUMENT);
				Element eleMergeDoc = mergeDoc.getDocumentElement();
				
				Element eleInput = XMLUtil.createChild(eleMergeDoc, KohlsConstant.INPUT_DOCUMENT);
				Element eleProShipElement = XMLUtil.createChild(eleMergeDoc, KohlsConstant.PROSHIP_DOCUMENT);
				
				XMLUtil.importElement(eleInput, eleInputDocument);
				XMLUtil.importElement(eleProShipElement, eleProShipResponse);
				
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return mergeDoc;
		
	}

}