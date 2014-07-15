package com.kohls.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSConfirmCustomerPickandPrintAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSConfirmCustomerPickandPrintAPI.class.getName());

	public Document confirmCustomerPickandPrint(YFSEnvironment env, Document inXML)
	throws Exception {
		Document OutDoc = null;
		Element eleindoc = inXML.getDocumentElement();
		String sPrinterId = eleindoc.getAttribute(KohlsConstant.A_PRINTER_ID);
		String sLabelPickTicket = eleindoc.getAttribute(KohlsConstant.A_LABEL_PICK_TICKET);

		// INVOKE confirmCustomerPick Api with the template
		OutDoc = KOHLSBaseApi.invokeAPI(env,KohlsConstant.API_CONFIRM_CUSTOMER_PICK,inXML);
		Document docInputForPrintPickTicket = XMLUtil
		.createDocument(KohlsConstant.E_SHIPMENT);

		docInputForPrintPickTicket.getDocumentElement().setAttribute(
				KohlsConstant.A_SHIPMENT_KEY, eleindoc.getAttribute(KohlsConstant.A_SHIPMENT_KEY));
		
		docInputForPrintPickTicket.getDocumentElement().setAttribute(
				KohlsConstant.A_PRINTER_ID, sPrinterId);
		if(!YFCObject.isVoid(sLabelPickTicket))
		{
			docInputForPrintPickTicket.getDocumentElement().setAttribute(
					KohlsConstant.A_LABEL_PICK_TICKET, sLabelPickTicket);
		}
		OutDoc = KOHLSBaseApi.invokeService(env,
				KohlsConstant.SERVICE_SOP_PRINT_PICK_TICKET, docInputForPrintPickTicket);
		return OutDoc ;
	}

}

