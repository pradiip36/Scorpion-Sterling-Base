package com.kohls.shipment.api;

import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
import com.kohls.common.util.KohlsXPathUtil;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSGetShipmentListForPickSlipPrint extends KOHLSBaseApi {
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KOHLSGetShipmentListForPickSlipPrint.class);
	public KOHLSGetShipmentListForPickSlipPrint() {
		// TODO Auto-generated constructor stub
	}
	
	public Document getShipmentListForPickSlipPrint(YFSEnvironment env, Document inDoc){
		kohlsLogger.debug("In KOHLSGetShipmentListForPickSlipPrint class::");
		kohlsLogger.debug("In put to the getShipmentList API is::" + "\n" + KohlsXMLUtil.getXMLString(inDoc));
		Document outDoc= inDoc;
		try{
			outDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstants.TEMPLATE_SHIPMENTS_PICK_LIST_PRINT, 
					KohlsConstants.API_GET_SHIPMENT_LIST, inDoc);
				//YFCElement yShipmentListDoc = YFCDocument.getDocumentFor(docShipmentList).getDocumentElement();
				//YFCNodeList yShipments = yShipmentListDoc.getChildNodes();
				Iterator itrShipments = KohlsXMLUtil.getChildren(outDoc.getDocumentElement());
				HashMap<String, String> deptMap = new HashMap<String, String>();
				while(itrShipments.hasNext()){
					Element eleShipment = (Element)itrShipments.next();
					
				String extnShipmentDeptCode = eleShipment.getAttribute(KohlsConstants.Extn_SHIPMENT_DEPT);
				if(null != extnShipmentDeptCode && !deptMap.containsKey(extnShipmentDeptCode)){
	    			
	    			//callToAPI
					Document inputCategoryDoc = XMLUtil.createDocument(KohlsConstants.CATEGORY);
					Element ele =  inputCategoryDoc.getDocumentElement();
					ele.setAttribute(KohlsConstants.PARENT_CATEGORY_KEY, "");
					ele.setAttribute(KohlsConstants.CATEGORY_ID, extnShipmentDeptCode);
					
					Document outCategoryDoc= KOHLSBaseApi.invokeAPI(env, KohlsConstants.TEMPLATE_SHIPMENTS_PICK_LIST_PRINT_GET_CATEGORY_LIST, 
							KohlsConstants.GET_CATEGORY_LIST, inputCategoryDoc);
					
					Element rootEle = outCategoryDoc.getDocumentElement();
					Element cateEle = KohlsXMLUtil.getFirstElementByName(rootEle, KohlsConstants.CATEGORY);
					String description = cateEle.getAttribute(KohlsConstants.DESCRIPTION);
					eleShipment.setAttribute(KohlsConstants.EXTN_SHIPMENT_DEPT_DESC,description);
					deptMap.put(extnShipmentDeptCode, description);
					
	    		}else{
	    			eleShipment.setAttribute(KohlsConstants.EXTN_SHIPMENT_DEPT_DESC, deptMap.get(extnShipmentDeptCode));
	    		}
					
					NodeList eShipmentLines = eleShipment.getElementsByTagName(KohlsXMLLiterals.E_SHIPMENT_LINE);
					String strShipmentDept = KohlsConstants.BLANK;
					if(!KohlsXMLUtil.isVoid(eShipmentLines)){
						for(int i=0;i<eShipmentLines.getLength();i++){
							Element eShipmentLine = (Element)eShipmentLines.item(i);
							kohlsLogger.debug("Evaluating ShipmentLine" + eShipmentLine.getAttribute(KohlsXMLLiterals.A_SHIPMENT_LINE_KEY) 
									+ " for ShipmentNo " + eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_NO));
							String tmpExtnDept = KohlsXPathUtil.getString(eShipmentLine, KohlsConstants.XPATH_EXTN_DEPT);
							kohlsLogger.debug("Department for shipment line is:: " + tmpExtnDept);
							if(KohlsConstants.BLANK.equals(strShipmentDept)){
								strShipmentDept = tmpExtnDept;
							}
							else if(!strShipmentDept.equals(tmpExtnDept) && !KohlsConstants.BLANK.equals(tmpExtnDept)){
								kohlsLogger.debug("found diff Departments on the Shipment lines... This is multi dept shipment... Exitting... for this Shipment");
								strShipmentDept=KohlsConstants.MULTI_DEPT_SHIPMENT;
								break;
							}
						}
						eleShipment.setAttribute(KohlsXMLLiterals.A_EXTN_SHIP_DEPT, strShipmentDept);
					}
				}
			}
		catch(Exception e){
			e.printStackTrace();
		}		

		return outDoc;
	}

}
