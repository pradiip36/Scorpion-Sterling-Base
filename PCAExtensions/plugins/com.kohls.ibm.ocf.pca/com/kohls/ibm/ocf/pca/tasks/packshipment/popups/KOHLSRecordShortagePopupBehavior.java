package com.kohls.ibm.ocf.pca.tasks.packshipment.popups;

//Java Imports-None

//MISC Imports
import org.eclipse.swt.widgets.Composite;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.util.KOHLSPCAApiNames;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * *********************************************************************
 * KOHLSRecordShortagePopupBehavior class
 * *********************************************************************
 */


public class KOHLSRecordShortagePopupBehavior extends YRCBehavior{
	
	private Element eleInput = null;
	private String formID = "";

	public KOHLSRecordShortagePopupBehavior(Composite parent, String formID) {
		super(parent, formID);
		this.formID = formID;
		callCommonCodeList();
	}

	private void callCommonCodeList() {
		// TODO Auto-generated method stub
		YRCApiContext ctx = new YRCApiContext();
		ctx.setFormId(formID);
		ctx.setApiName(KOHLSPCAApiNames.API_GET_COMMON_CODE_LIST);
		ctx.setInputXml(createinDoc());
		YRCPlatformUI.callApi(ctx, this);
	}

	private Document createinDoc() {
		// TODO Auto-generated method stub
		Document inDoc = YRCXmlUtils.createDocument(KOHLSPCAConstants.E_COMMON_CODE);
		Element inputEle = inDoc.getDocumentElement();
		inputEle.setAttribute(KOHLSPCAConstants.A_CODE_TYPE, YRCPlatformUI.getString("SHORTAGE_REASON_CODE"));
		inputEle.setAttribute(KOHLSPCAConstants.A_ORGANIZATION_CODE, "DEFAULT");
		return inDoc;
	}

	public void setModel(Element eleModel){
		setModel("CommonCode", eleModel);
	}

	@Override
	public void handleApiCompletion(YRCApiContext context) {
		// TODO Auto-generated method stub
		if(context.getInvokeAPIStatus() > 0){
			if(KOHLSPCAApiNames.API_GET_COMMON_CODE_LIST.equals(context.getApiName())){
				Element eleOutput = context.getOutputXml().getDocumentElement();
				setModel("CommonCode", eleOutput, true);
			}
		}
		super.handleApiCompletion(context);
	}
	
}