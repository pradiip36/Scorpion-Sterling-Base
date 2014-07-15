package com.kohls.bopus.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfs.japi.YFSEnvironment;

public class KohlsBopusProcessPendingReturnService {
	
	public void processPendingReturns(YFSEnvironment env, Document inDoc) throws Exception {
		
		Element eleMonitorConsolidation = inDoc.getDocumentElement();
		Element eleShipment = SCXmlUtil.getChildElement(eleMonitorConsolidation, KohlsXMLLiterals.E_SHIPMENT);
		
		String sShipmentKey = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY);
		String sShipNode = eleShipment.getAttribute(KohlsXMLLiterals.A_SHIP_NODE);
		
		/*
		<MultiApi>
		<API Name="" > <Input>	</Input> </API>
		<API Name="" > <Input>	</Input> </API>
		</MultiApi>
		*/
		Document docMultiAPI = SCXmlUtil.createDocument(KohlsXMLLiterals.E_MULTI_API);
		Element eleMultiAPI = docMultiAPI.getDocumentElement();
		
		Element eleConfirmShimentAPI = SCXmlUtil.createChild(eleMultiAPI, KohlsXMLLiterals.E_API);
		eleConfirmShimentAPI.setAttribute(KohlsXMLLiterals.A_NAME, KohlsConstant.API_CONFIRM_SHIPMENT);
		Element eleConfirmShipmentInput = SCXmlUtil.createChild(eleConfirmShimentAPI, KohlsXMLLiterals.E_INPUT);
		Element eleConfirmShipment = SCXmlUtil.createChild(eleConfirmShipmentInput, KohlsXMLLiterals.E_SHIPMENT);
		eleConfirmShipment.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, KohlsConstant.KOHLS_OrganizationCode);
		eleConfirmShipment.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, sShipNode );
		eleConfirmShipment.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, sShipmentKey );
		eleConfirmShipment.setAttribute(KohlsXMLLiterals.A_TRANSACTION_ID, KohlsConstant.TRAN_CONFIRM_SHIPMENT);

		Element eleChangeShipmentStatusAPI = SCXmlUtil.createChild(eleMultiAPI, KohlsXMLLiterals.E_API);
		eleChangeShipmentStatusAPI.setAttribute(KohlsXMLLiterals.A_NAME, KohlsConstant.API_CHANGE_SHIPMENT_STATUS);
		Element eleChangeShipmentStatusInput = SCXmlUtil.createChild(eleChangeShipmentStatusAPI, KohlsXMLLiterals.E_INPUT);
		Element eleChangeShipmentStatus = SCXmlUtil.createChild(eleChangeShipmentStatusInput, KohlsXMLLiterals.E_SHIPMENT);
		eleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SELLER_ORGANIZATION_CODE, KohlsConstant.KOHLS_OrganizationCode);
		eleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SHIP_NODE, sShipNode );
		eleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_SHIPMENT_KEY, sShipmentKey );
		eleChangeShipmentStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTION_ID, KohlsConstant.TRAN_PROCESS_PENDING_RET);
		
		KohlsCommonUtil.invokeAPI( env, KohlsConstant.MULTIAPI_API, docMultiAPI);

		
	}
	

}
