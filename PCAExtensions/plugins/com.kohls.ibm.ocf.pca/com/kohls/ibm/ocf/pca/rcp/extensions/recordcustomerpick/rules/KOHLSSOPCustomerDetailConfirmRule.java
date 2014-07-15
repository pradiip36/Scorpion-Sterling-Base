	
package com.kohls.ibm.ocf.pca.rcp.extensions.recordcustomerpick.rules;

/**
 * Created on Jun 16,2012
 *
 */

import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
//import com.yantra.pca.sop.rcp.application.SOPApplicationContext;
import com.yantra.yfc.rcp.IYRCApiCallbackhandler;
import com.yantra.yfc.rcp.IYRCWizardRule;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class KOHLSSOPCustomerDetailConfirmRule
  implements IYRCWizardRule, IYRCApiCallbackhandler
{
  private String formId = "com.yantra.pca.sop.rcp.tasks.outboundexecution.pickup.recordcustomerpick.wizards.SOPRecordCustomerPickWizard";
  private HashMap namespaceModelMap = new HashMap();
  private String strReturn = "Failure";

  public String execute(HashMap namespaceModelMap) {
    this.strReturn = "Failure";
    this.namespaceModelMap = namespaceModelMap;
    createMultiAPIinput();
    setStatusMessage();
    return this.strReturn;
  }

  private void setStatusMessage()
  {
    Element elemShipmentDetails = (Element)this.namespaceModelMap.get("ShipmentDetails");

    if (("Success".equals(this.strReturn)) || ("SuccessForOrder".equals(this.strReturn))) {
      elemShipmentDetails.setAttribute("Success", "Success");
      elemShipmentDetails.setAttribute("StatusMessage", "SOP_Record_CustomerPick_Successful");
    } else {
      elemShipmentDetails.setAttribute("Success", "Failure");
      elemShipmentDetails.setAttribute("StatusMessage", "SOP_Record_CustomerPick_UnSuccessful");
    }
  }

  public void createMultiAPIinput()
  {
	  
    Element eleShipment = (Element)this.namespaceModelMap.get("ShipmentDetails");

    YRCXmlUtils.setAttribute(eleShipment, "TransactionId", "CONFIRM_SHIPMENT");
    YRCXmlUtils.setAttribute(eleShipment, "ShipmentSortLocationId", KOHLSPCAUtils.getCurrentStore());
    callConfirmCustomerPickAPI(eleShipment);
  }

	private void callConfirmCustomerPickAPI(Element eleInput) {
		YRCApiContext context = new YRCApiContext();
		context.setApiName("ConfirmCustomerPick");
		context.setFormId(this.formId);
		context.setInputXml(eleInput.getOwnerDocument());

		YRCPlatformUI.callApi(context, this);
		doAPICompletion(context);
	}

	public void doAPICompletion(YRCApiContext ctx) {
		if (ctx.getInvokeAPIStatus() < 0)
			this.strReturn = "Failure";
		else if (ctx.getApiName().equals("ConfirmCustomerPick")) {
			this.strReturn = "Success";
			try {

				NodeList nlPrintRePackDoc = ctx.getOutputXml()
						.getElementsByTagName("PrintRePackDoc");

				for (int intPrintRePackCount = 0; intPrintRePackCount < nlPrintRePackDoc
						.getLength(); intPrintRePackCount++) {

					Element elePrintPackDoc = (Element) nlPrintRePackDoc
							.item(intPrintRePackCount);

					if (!YRCPlatformUI.isVoid(elePrintPackDoc)) {
						Element elePrintShipment = (Element) elePrintPackDoc
								.getElementsByTagName(
										KOHLSPCAConstants.E_SHIPMENT).item(0);
						if (!YRCPlatformUI.isVoid(elePrintShipment)) {
							DocumentBuilder builder = DocumentBuilderFactory
									.newInstance().newDocumentBuilder();
							Document inputPrintdoc = builder.newDocument();
							Element eleImported = (Element) inputPrintdoc
									.importNode(elePrintShipment, true);
							inputPrintdoc.appendChild(eleImported);

							KOHLSPCAUtils.jasperRePrintPackSlip(inputPrintdoc,
									"Shipment");
						}
					}
				}

			} catch (Exception e) {
				YRCPlatformUI
						.trace("Got the following Error While Printing Report"
								+ KOHLSPCAUtils.getStringTrace(e));
			}
		}
	}

  public void handleApiCompletion(YRCApiContext ctx)
  {
  }
}