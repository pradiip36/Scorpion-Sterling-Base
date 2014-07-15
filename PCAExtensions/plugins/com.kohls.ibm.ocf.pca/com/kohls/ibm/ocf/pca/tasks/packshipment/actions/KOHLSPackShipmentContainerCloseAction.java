package com.kohls.ibm.ocf.pca.tasks.packshipment.actions;

/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */

import org.eclipse.jface.action.IAction;
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentBehavior;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizards.KOHLSPackShipmentWizard;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizards.KOHLSPackShipmentWizardBehavior;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAUtils;
import com.kohls.ibm.ocf.pca.util.KohlsCommonUtil;
import com.kohls.ibm.ocf.pca.util.KohlsScaleFactory;
import com.yantra.ycp.ui.io.YCPWeighingScaleConnector;
import com.yantra.yfc.rcp.YRCAction;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCWizardBehavior;
import com.yantra.yfc.rcp.YRCWizardPageBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;

public class KOHLSPackShipmentContainerCloseAction extends YRCAction {

	public static final String ACTION_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.actions.KOHLSPackShipmentContainerCloseAction";

	public void execute(IAction action) {

		if (KOHLSPCAUtils.getCurrentPage() instanceof KOHLSPackShipmentPage) {

			KOHLSPackShipmentPage kohlsPackShipmentPage = (KOHLSPackShipmentPage) KOHLSPCAUtils
			.getCurrentPage();
			//<!-- Start - Drop2 changes for "functionality"  -->
			Element eleProperties = KohlsApplicationInitializer.eleVerifyTerminalProperties;
			Element elePropertiesList = YRCXmlUtils.getChildElement(eleProperties, "SIMINIProperties");
			String strWeighScale = elePropertiesList.getAttribute("WeightCaculator");
		
			KOHLSPackShipmentBehavior packShipmentPageBehv = kohlsPackShipmentPage.getWizardPageBehavior();
			
			Element eleShipmentDetailsInfo = packShipmentPageBehv.returnModel("getShipmentDetailsForPackShipment");
			String strEnterpriseCode = eleShipmentDetailsInfo.getAttribute("EnterpriseCode");
			
			if("Use Scale for Container Weight".equals(strWeighScale)){
				// call weighing scale interface to fetch the weight and update the container weight
				String strScaleWt = "";
				try{
				 strScaleWt = getWeight();
				}
				catch(Exception e){
					YRCPlatformUI.trace("KOHLSPackShipmentContainerCloseAction :: Exception while trying to read weighing scale");
					String strMessage = e.getMessage();
					if(null == strMessage || "".equals(strMessage)){
						strMessage=YRCPlatformUI.getString("CANNOT_CONNECT_TO_WEIGHING_SCALE");
						YRCPlatformUI.trace("KOHLSPackShipmentContainerCloseAction :: No exception message from Scale"+
								"\nSetting the message as " + strMessage);
						//YRCPlatformUI.showError("Weighing Scale Error", YRCPlatformUI.getString("CANNOT_CONNECT_TO_WEIGHING_SCALE"));
					}
					/*else{
					YRCPlatformUI.showError("Weighing Scale Error", YRCPlatformUI.getString(strMessage));
					}*/					
					kohlsPackShipmentPage.promptUserForWeightCalculation(strMessage);
					return;
				}
				Double dScaleWeight = Double.parseDouble(strScaleWt);
				if(dScaleWeight <= 0.00){
					kohlsPackShipmentPage.promptUserForWeightCalculation("N");
				}else{
					Element currentContainerSource = packShipmentPageBehv.returnModel("CurrentContainerSource");
					currentContainerSource.setAttribute(KOHLSPCAConstants.A_CONTAINER_NET_WT, Double.toString(dScaleWeight));
					currentContainerSource.setAttribute(KOHLSPCAConstants.A_CONTAINER_GROSS_WT, Double.toString(dScaleWeight));
					packShipmentPageBehv.isTAREApplicable(strEnterpriseCode);
				}
			}else if("Calculate System Weight for Container".equals(strWeighScale)){
				setWeightFromCatalogueInformation();
				packShipmentPageBehv.isTAREApplicable(strEnterpriseCode);
				} 
			}
			//			<!-- End - Drop2 changes for "functionality"  -->
		}

	private void setWeightFromCatalogueInformation() {
		// TODO Auto-generated method stub
		Composite comp1 = YRCDesktopUI.getCurrentPage();
		KOHLSPackShipmentWizard comp2 = (KOHLSPackShipmentWizard)comp1;
		YRCWizardBehavior myWizBehv = comp2.getWizardBehavior();
		if(myWizBehv instanceof KOHLSPackShipmentWizardBehavior){
			KOHLSPackShipmentWizardBehavior packShipWizBehv = (KOHLSPackShipmentWizardBehavior)myWizBehv;
			YRCWizardPageBehavior pageBehv = packShipWizBehv.getCurrentPageBehavior();
			if(pageBehv instanceof KOHLSPackShipmentBehavior){
				KOHLSPackShipmentBehavior packShipPageBehv = (KOHLSPackShipmentBehavior)pageBehv;
				packShipPageBehv.setContainerUnitWt();						
			}
		}
	}

	public boolean checkForModifications() {
		return false;
	}

	public boolean checkForErrors() {
		return false;
	}
	
	private String getWeight() throws Exception{
		Document params = null;
        KohlsScaleFactory factory = null;
        YCPWeighingScaleConnector scale = null;
        double weight = -1;
        
            // these parameters will come from:
            // port from INI file
            // others from customer_overrides.properties
        Element eleTerminalProperties = KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI;
        Element eleSIMINIProperties = YRCXmlUtils.getChildElement(eleTerminalProperties, KOHLSPCAConstants.KOHLS_SIM_INI_PROP);
        String strSerialPort = "";
        if(!YRCPlatformUI.isVoid(eleSIMINIProperties)){
        strSerialPort = eleSIMINIProperties.getAttribute(KOHLSPCAConstants.SERIAL_PORT);
        
            params = KohlsCommonUtil.getParams(strSerialPort, 9600, 8, 1, "None",
                    "None", "None", "Y");
            // System.out.println(XMLUtility.toXml(params));
            //
            factory = KohlsScaleFactory.getInstance();
            factory.init(params);
            scale = factory.getConnector();
            weight = scale.getWeight();
            //System.out.println(weight);
        }
        return Double.toString(weight);
	}
}