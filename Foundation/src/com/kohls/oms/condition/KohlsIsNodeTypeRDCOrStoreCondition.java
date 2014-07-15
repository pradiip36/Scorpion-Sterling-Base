package com.kohls.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsIsNodeTypeRDCOrStoreCondition implements YCPDynamicConditionEx {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsIsNodeTypeRDCOrStoreCondition.class.getName());

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String arg1,
			Map arg2, Document arg3) {
		// TODO Auto-generated method stub

		boolean retval = false;

		try {
			YIFApi api = YIFClientFactory.getInstance().getLocalApi();
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition with parameters: arg1: " + arg1 + ", arg2: " + 
					arg2 + ", Document: " + ((arg3 == null) ? "Null" : XMLUtil.serialize(arg3)));
		
			String shipnode = (String) arg2.get("ShipNode"); 
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : Got Shipnode: " + shipnode);
	
			YFCDocument yfcDocGetOrgList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORGANIZATION);
			YFCElement yfcEleCreateShipment = yfcDocGetOrgList.getDocumentElement();
			// Start --- Added for SF Case # 00384335 -- OASIS_SUPPORT 22/12/2012
			yfcEleCreateShipment.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, shipnode);


			YFCDocument yfcDocOrgListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORGANIZATION_LIST);
			YFCElement yfcEleOrgListTemp = yfcDocOrgListTemp.getDocumentElement();

			YFCElement yfcEleOrgTemp = yfcEleOrgListTemp.createChild(KohlsXMLLiterals.E_ORGANIZATION);
			yfcEleOrgTemp.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE, "");

			YFCElement yfcEleNodeTypeTemp = yfcEleOrgTemp.createChild(KohlsXMLLiterals.A_NODE);
			yfcEleNodeTypeTemp.setAttribute(KohlsXMLLiterals.A_NODE_TYPE, "");
			
			
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : Setting Template: " + XMLUtil.serialize(yfcDocOrgListTemp.getDocument()));
			env.setApiTemplate(KohlsConstant.API_GET_ITEM_LIST, yfcDocOrgListTemp.getDocument());
			
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : Invoking getOrganizationList: " + XMLUtil.serialize(yfcDocGetOrgList.getDocument()));
			
			Document docGetOrgListEx = api.getOrganizationList(env, yfcDocGetOrgList.getDocument());
			env.clearApiTemplate(KohlsConstant.API_GET_ITEM_LIST);	
			
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : Output of getOrganizationList: " + 
			XMLUtil.serialize(docGetOrgListEx));
			
			YFCDocument orgListOutput = YFCDocument.getDocumentFor(docGetOrgListEx);
			YFCElement orgEle = orgListOutput.getDocumentElement().getChildElement(KohlsXMLLiterals.E_ORGANIZATION);
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : " + orgEle.getNodeName());
			YFCElement nodeEle = orgEle.getChildElement(KohlsXMLLiterals.A_NODE);
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition : " + orgEle.getNodeName());
			
			String nodetype = nodeEle.getAttribute(KohlsXMLLiterals.A_NODE_TYPE);
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition Got NodeType: " + nodetype);
						
			if (KohlsConstant.ATTR_RDC.equals(nodetype) || KohlsConstant.ATTR_STORE.equals(nodetype))
				retval = true;

			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition Returning: " + retval);
			
			
		} catch (Exception e) {
			log.error(e);			
			log.debug("Inside KohlsIsNodeTypeRDCOrStoreCondition returning FALSE due to EXCEPTION!!!!!!!!!!");
		}

		return retval;

	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
