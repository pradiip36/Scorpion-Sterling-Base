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

public class KOHLSCheckTrackingNoAndConfirmShipAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSCheckTrackingNoAndConfirmShipAPI.class.getName());

	public Document statusAndTrackingNoCheck(YFSEnvironment env, Document inXML)
	throws Exception {
		Element eleindoc = inXML.getDocumentElement();
		boolean bConfirmShipment = true;
		// INVOKE GETSHIPMENTDETAILS with the template
		Document OutDoc = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_CHECK_TRACKING_NO_TEMPLATE_PATH,
				KohlsConstant.API_GET_SHIPMENT_DETAILS, inXML);
		Element outElem = OutDoc.getDocumentElement();
		String strStatus = outElem.getAttribute(KohlsConstant.A_STATUS);

		if(strStatus.equalsIgnoreCase(KohlsConstant.A_CONFIRM_SHIPMENT_STATUS))
		{
			bConfirmShipment = false;
		}else
		{
			NodeList nodeContainerList = outElem
			.getElementsByTagName("Container");
			for (int iCtnCount = 0; iCtnCount < nodeContainerList
			.getLength(); iCtnCount++) {
				Element eleContainer = (Element) nodeContainerList
				.item(iCtnCount);
				if (YFCObject.isVoid(eleContainer
						.getAttribute("TrackingNo"))) {
					bConfirmShipment = false;
				}
			}
		}



		if(bConfirmShipment)
		{
			eleindoc.setAttribute(KohlsConstant.A_IS_CONFIRM_SHIPMENT, KohlsConstant.V_Y);
		}else
		{
			eleindoc.setAttribute(KohlsConstant.A_IS_CONFIRM_SHIPMENT, KohlsConstant.V_N);
		}
		return inXML;
		//
	}

}
