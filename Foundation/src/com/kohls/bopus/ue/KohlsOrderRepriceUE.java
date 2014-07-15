package com.kohls.bopus.ue;

/*****************************************************************************
 * File Name    : KohlsOrderRepriceUE.java
 *
 * Modification Log :
 * ---------------------------------------------------------------------------
 * Ver #   Date         Author                 Modification
 * ---------------------------------------------------------------------------
 * 0.00a Jan 16,2014    Juned S         Initial Version 
 * ---------------------------------------------------------------------------
 *****************************************************************************/

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;

public class KohlsOrderRepriceUE implements YFSOrderRepricingUE{
	
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsOrderRepriceUE.class.getName());

	@Override
	public Document orderReprice(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException {
		
		try {
						
			log.debug("orderReprice: inDoc:"+SCXmlUtil.getString(inDoc));
			
			Element eleIn = inDoc.getDocumentElement();
			Element eleOrderLines = SCXmlUtil.getChildElement(eleIn, KohlsXMLLiterals.E_ORDER_LINES);
			ArrayList<Element> orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			Iterator<Element> itrOrderLine = orderLineList.iterator();
			Element eleOrderLine = null;
			Element eleModificationTypes = null;
			ArrayList<Element> modificationTypesList = null;
			Iterator<Element> itrModificationType = null;
			Element eleModificationType = null;
			List<Element> cancelOrderLineList = new ArrayList<Element>();
			boolean isCancel = false;
			while(itrOrderLine.hasNext()){
				eleOrderLine = itrOrderLine.next();
				if(!XMLUtil.isVoid(eleOrderLine)){
					eleModificationTypes = SCXmlUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.A_MODIFICATION_TYPES);
					if(!XMLUtil.isVoid(eleModificationTypes)){
						modificationTypesList = SCXmlUtil.getChildren(eleModificationTypes, KohlsXMLLiterals.A_MODIFICATION_TYPE);
						itrModificationType = modificationTypesList.iterator();

						while(itrModificationType.hasNext()){
							eleModificationType = itrModificationType.next();
							if(KohlsConstant.ACTION_CANCEL.equalsIgnoreCase(eleModificationType.getAttribute(KohlsXMLLiterals.A_NAME))){
								cancelOrderLineList.add(eleOrderLine);
								XMLUtil.removeChild(eleOrderLines, eleOrderLine);
								isCancel = true;
								break;
							}
						}
					}
				}
				
			}
			
			
			orderLineList = SCXmlUtil.getChildren(eleOrderLines, KohlsXMLLiterals.E_ORDER_LINE);
			
			
			/***** Commenting the below code as the call to ECommerce webservice is not decided yet *****************************************/
			
			/*if(isCancel && !orderLineList.isEmpty()){
				inDoc = KohlsCommonUtil.invokeService(env, KohlsConstant.KOHLS_ECOMORDRREPIRCE_WEBSERVICE, inDoc);
				eleIn = inDoc.getDocumentElement();
				eleOrderLines = SCXmlUtil.getChildElement(eleIn, KohlsXMLLiterals.E_ORDER_LINES);
				if(!cancelOrderLineList.isEmpty()){
					Iterator<Element> itrCancelOrderLine = cancelOrderLineList.iterator();
					while(itrCancelOrderLine.hasNext()){
						eleOrderLine = itrCancelOrderLine.next();
						SCXmlUtil.importElement(eleOrderLines, eleOrderLine);
					}
				}
			}
			else{
				if(!cancelOrderLineList.isEmpty()){
					Iterator<Element> itrCancelOrderLine = cancelOrderLineList.iterator();
					eleOrderLines = SCXmlUtil.getChildElement(eleIn, KohlsXMLLiterals.E_ORDER_LINES);
					while(itrCancelOrderLine.hasNext()){
						eleOrderLine = itrCancelOrderLine.next();
						SCXmlUtil.importElement(eleOrderLines, eleOrderLine);
					}
				}
			}*/
			
			
			log.debug("orderReprice: inDoc:"+SCXmlUtil.getString(inDoc));
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method orderReprice() returned Error."+e.getStackTrace());
		}
		
		return inDoc;
	}

}
