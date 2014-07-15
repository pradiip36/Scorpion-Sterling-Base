package com.kohls.wsc.shipment.backroompick.mashups;

import static com.kohls.common.util.KohlsConstants.MASHUP_GET_MLS_GIV_DETAILS;

import org.w3c.dom.Element;

import com.ibm.wsc.shipment.backroompick.mashups.WSCAbandonBackroomPickStartOverMashup;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.framework.mashup.SCUIMashupMetaData;
import com.yantra.yfc.log.YFCLogCategory;

public class KohlsAbandonBackroomPickStartOverMashup extends
		WSCAbandonBackroomPickStartOverMashup {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsAbandonBackroomPickStartOverMashup.class.getName());
	
	public Element massageOutput(Element outEl,
			SCUIMashupMetaData mashupMetaData, SCUIContext uiContext) {
		
		// let the superclass take care of the shipment status
		Element outputElement = super.massageOutput(outEl, mashupMetaData, uiContext);
		
		// call our service MLS / GIV service
		outputElement = (Element) SCUIMashupHelper.invokeMashup(MASHUP_GET_MLS_GIV_DETAILS, outputElement, uiContext);
		log.debug("********getMLSGIVDetails Output**********");
		log.debug(SCXmlUtil.getString(outputElement));
		log.debug("********getMLSGIVDetails Output**********");
		
		return outputElement;
	}
}