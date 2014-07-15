package com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages;



import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Composite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;
public class KOHLSSIMPropertiesPageBehavior extends YRCBehavior {
	
private KOHLSSIMPropertiesPage owner = null;

public static String strCollatePrintingFlag = "";

String strUserAction = "";
/**
 * This is the constructor for the class LowesCustomerCreationPopupPageBehaviour
 * @param object 
 */
	protected KOHLSSIMPropertiesPageBehavior(Composite ownerComposite, String strFormId,String strAction) {
		super(ownerComposite, strFormId);
		this.owner = (KOHLSSIMPropertiesPage) ownerComposite;
		strUserAction = strAction;
	}
	protected KOHLSSIMPropertiesPageBehavior(Composite ownerComposite, String strFormId) {
		super(ownerComposite, strFormId);
		this.owner = (KOHLSSIMPropertiesPage) ownerComposite;
		
	}
	
	
	public void saveProperties() {
		boolean boolIsValid = validateButtonClick();
		if (boolIsValid) {
			saveSIMINIProperties();
		}
		
		
	}

	private void saveSIMINIProperties() {
		Element eleTerminalProperties = KohlsApplicationInitializer.eleVerifyTerminalProperties;
		if(!YRCPlatformUI.isVoid(eleTerminalProperties) ){
				if(eleTerminalProperties.hasChildNodes() && strUserAction.equals("Modify") ){
			YRCApiContext context = new YRCApiContext();
			context.setApiName("KohlsModifyTerminalPropertiesService");
			context.setFormId(getFormId());
			context.setInputXml(saveSIMINIInput());
			callApi(context);
		}
		
			
		else{
	
		YRCApiContext context = new YRCApiContext();
		boolean showError = false;
		context.setApiName("KohlsManageTerminalPropertiesService");
		context.setFormId(getFormId());
		context.setInputXml(saveSIMINIInput());
		context.setShowError(showError);
		callApi(context);
		}
	}
	}

	private Document saveSIMINIInput() {
		Element eleSaveSIMInput = getTargetModel("SIMINIPropertiesInput");
		Document docSIMINIPropertiesInput = YRCXmlUtils.createDocument("SIMINIPropertiesInput");
		Element SIMINIPropertiesInput = docSIMINIPropertiesInput.getDocumentElement();
		SIMINIPropertiesInput.setAttribute("TerminalID",KohlsApplicationInitializer.strComputername);
		SIMINIPropertiesInput.setAttribute("DefaultLabelPrinter",eleSaveSIMInput.getAttribute("DefaultLabelPrinter"));
		SIMINIPropertiesInput.setAttribute("DefaultPackContainer",eleSaveSIMInput.getAttribute("DefaultPackContainer"));
		SIMINIPropertiesInput.setAttribute("DefaultPackPrinter",eleSaveSIMInput.getAttribute("DefaultPackPrinter"));
		SIMINIPropertiesInput.setAttribute("DefaultPrinter",eleSaveSIMInput.getAttribute("DefaultPrinter"));
		SIMINIPropertiesInput.setAttribute("DefaultPackStation",eleSaveSIMInput.getAttribute("DefaultPackStation"));
		SIMINIPropertiesInput.setAttribute("SerialPort",eleSaveSIMInput.getAttribute("SerialPort"));
		SIMINIPropertiesInput.setAttribute("ShipmentsperTote",eleSaveSIMInput.getAttribute("ShipmentsPerTote"));
		//Start changes for SFS June Release
		//SIMINIPropertiesInput.setAttribute("CollatePrintingEnabled",eleSaveSIMInput.getAttribute("CollatePrintingEnabled"));
		strCollatePrintingFlag = eleSaveSIMInput.getAttribute("CollatePrintingEnabled");
		if(!strCollatePrintingFlag.isEmpty() && strCollatePrintingFlag!=null)
		{
			if(strCollatePrintingFlag.equalsIgnoreCase("Yes"))
			{
				SIMINIPropertiesInput.setAttribute("CollatePrintingEnabled", "Y");
				strCollatePrintingFlag="Y";
			}
			else
			{
				SIMINIPropertiesInput.setAttribute("CollatePrintingEnabled", "N");
				strCollatePrintingFlag="N";
			}
		
		}
		//String strcollateprinbt  = getFieldValue("comboCollatePrintingEnabled");
		//SIMINIPropertiesInput.setAttribute("ExtnCollateFlag",strcollateprinbt);
		//End changes for SFS June Release
		String strr1 = new String();
		if(this.owner.radioButtonCalcualteWeight.getSelection()){
			strr1=this.owner.radioButtonCalcualteWeight.getText();
		}
		if(this.owner.radioButtonUseScale.getSelection()){
			strr1 =this.owner.radioButtonUseScale.getText();
			
		}
		SIMINIPropertiesInput.setAttribute("WeightCaculator",strr1);
		return docSIMINIPropertiesInput;
	
	}

	private boolean validateButtonClick() {
		Element eleSIMINIPropertiesInput = getTargetModel("SIMINIPropertiesInput");
		if(!YRCPlatformUI.isVoid(eleSIMINIPropertiesInput)){
			String strShipmentsPerTote = eleSIMINIPropertiesInput.getAttribute("ShipmentsPerTote");
			if(!YRCPlatformUI.isVoid(strShipmentsPerTote)){
				//check whether it is a number else throw an error message.
				boolean boolIsNumber = isNumber(strShipmentsPerTote);
				if(!boolIsNumber){
					YRCPlatformUI.showError(YRCPlatformUI.getString("ERROR"),YRCPlatformUI.getString("SHIPMENTS_PER_TOTE_NUMBER"));
					
					return false;
				}
				
			}
			
		}return true;
	}


		private static boolean isNumber(String val) {
			if(!YRCPlatformUI.isVoid(val)){
			String pattern = "^*[0-9](|.*[0-9]|,*[0-9])?$"; // "^[0-9]*$";
			if (val == null || val.trim().length() == 0) {
				return false;
			}
			boolean isNumber = false;
			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(val.trim());
			isNumber = m.matches();
			return isNumber;
			}
			return false;
		}

		/* (non-Javadoc)
		 * @see com.yantra.yfc.rcp.YRCBaseBehavior#handleApiCompletion(com.yantra.yfc.rcp.YRCApiContext)
		 */
		@Override
		public void handleApiCompletion(YRCApiContext ctx) {
			
			if (ctx.getInvokeAPIStatus() < 1) {
				YRCPlatformUI.trace("API exception in " + 
						 " page, ApiName " + ctx.getApiName() + ",Exception : ",
						ctx.getException());
				Element eleErrors = ctx.getOutputXml().getDocumentElement();
				Element eleError = YRCXmlUtils.getChildElement(eleErrors, "Error");
				String strErrorCode = eleError.getAttribute("ErrorCode");
				
				if (strErrorCode.equals("YFC0001")) {
					YRCPlatformUI.showError("Error",
							"Record Already exists in the Data Base. Please use Edit Terminal Properties to Change the Properties");
					return;
				}

			} else
			{
				//Start - Added for CR859 - Delivery Drop A
				if (YRCPlatformUI.equals(ctx.getApiName(),"KohlsManageTerminalPropertiesService")) {
					Element eleSimlist =KohlsApplicationInitializer.getEleVerifyTerminalProperties();
					Element eleUpdatedProperties = ctx.getOutputXml().getDocumentElement();
					YRCXmlUtils.importElement(eleSimlist,eleUpdatedProperties);
					KohlsApplicationInitializer.setEleVerifyTerminalProperties(eleSimlist);
					YRCPlatformUI.showInformation(YRCPlatformUI.getString("Info"),YRCPlatformUI.getString("INFO_CREATED"));
						return;
		}
				if (YRCPlatformUI.equals(ctx.getApiName(),"KohlsModifyTerminalPropertiesService")) {
					Element eleSimlist =KohlsApplicationInitializer.eleVerifyTerminalProperties;
					Element eleUpdatedProperties = ctx.getOutputXml().getDocumentElement();
					NodeList nodel = eleSimlist.getChildNodes();
					if(nodel.getLength() > 0){
						for(int i=0;i< nodel.getLength();i++){
							Node n1= nodel.item(i);
							eleSimlist.removeChild(n1);
						}
					
					}
					
					Element eleList =YRCXmlUtils.createDocument("SIMINIPropertiesList").getDocumentElement();
					YRCXmlUtils.importElement(eleList,eleUpdatedProperties);
					KohlsApplicationInitializer.setEleVerifyTerminalProperties(eleList);
					YRCPlatformUI.showInformation(YRCPlatformUI.getString("Info"),YRCPlatformUI.getString("INFO_UPDATED"));
					return;
	}}
		}
		
		public void setSIMProperties() {
			setModel("SimProperites", KohlsApplicationInitializer.eleVerifyTerminalProperties);
		}
		
		public Element getSimProperties(){
			return getModel("SimProperites");
		}
}
	
	

		

