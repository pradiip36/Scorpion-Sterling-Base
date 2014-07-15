/*** Eclipse Class Decompiler plugin, copyright (c) 2012 Chao Chen (cnfree2000@hotmail.com) ***/
package com.ibm.wsc.shipment.backroompick.rules;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.ui.web.framework.context.SCUIContext;
import com.sterlingcommerce.ui.web.framework.helpers.SCUIMashupHelper;
import com.sterlingcommerce.ui.web.platform.wizard.ISCUIWizardRule;
import com.sterlingcommerce.ui.web.platform.wizard.SCUIWizardRuleContext;
import javax.servlet.http.HttpServletRequest;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class BackroomPickLandingPageWizardRule implements ISCUIWizardRule {
	public String execute(SCUIWizardRuleContext wizardRuleContext,
			SCUIContext uiContext) {
		Element elemInput = (Element) uiContext.getRequest().getAttribute(
				"scControllerInput");
		String screen = ((Element) elemInput.getParentNode())
				.getAttribute("landingScreenForWizard");

		Element shipInput = SCXmlUtil.createDocument("Shipment")
				.getDocumentElement();
		shipInput.setAttribute("ShipmentKey",
				elemInput.getAttribute("ShipmentKey"));
		Element elemOutput = (Element) SCUIMashupHelper.invokeMashup(
				"shipment_getStatusForShipment", shipInput, uiContext);
		String status = elemOutput.getAttribute("Status");

		if ((!(SCUtil.isVoid(status)))
				&& ((("1100.70.06.10".equals(status))
						|| ("1100.70.06.20".equals(status)) || ("1100.70.06.50.2".equals(status)) || 
						("1100.70.06.50.4".equals(status)) || (("1100.70.06.30"
						.equals(status)) && (!(SCUtil.isVoid(screen))) && (!("ItemScan"
							.equals(screen))))))) {
			if ((!(SCUtil.isVoid(screen)))
					&& ((("ItemScanReadOnly".equals(screen))
							|| ("ItemScanMobile".equals(screen))
							|| ("ItemScan".equals(screen)) || ("UpdateHoldLocation"
								.equals(screen))))) {
				return screen;
			}

			return "ItemScan";
		}

		return "InvalidShipmentStatus";
	}
}