package com.kohls.ibm.ocf.pca.tasks.packshipment.popups;

//Java Imports-None

//MISC Imports
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentBehavior;
import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCXmlUtils;

/**
 * *********************************************************************
 * KOHLSWeightRequiredPopup class is used to Show the Waring Message when
 * to prompt the user to calculate the container weight using
 * the existing catalogue information
 * *********************************************************************
 */
public class KOHLSWeightRequiredPopup extends Composite implements
		IYRCComposite {

	private static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.popups.KOHLSWeightRequiredPopup";
	
	private KOHLSPackShipmentPage basePage;
	
	private Composite cmpstRootPnl;

	private Composite cmpsWarnMessage;

	private Label lblShowWeightRequiredMsg;

	private Button bttnRequireWeightCancel;

	private Button button = null;
	
	private String  strScaleConnectionMessage = "";
	
	KOHLSWeightRequiredPopupBehavior myBehavior = null;

	/**
	 * Constructor for the LowesWeightRequiredPopup.
	 * 
	 * @param parent
	 * @param style
	 */
	public KOHLSWeightRequiredPopup(Composite parent, int style) {
		super(parent, style);
		this.basePage = (KOHLSPackShipmentPage)parent;
		initialize();
		setBindingForComponents();
		myBehavior = new KOHLSWeightRequiredPopupBehavior(this, FORM_ID);
	}
	
	
	public KOHLSWeightRequiredPopup(Composite parent, int style, String strScaleConnectionMessage) {
		super(parent, style);
		this.basePage = (KOHLSPackShipmentPage)parent;
		this.strScaleConnectionMessage=strScaleConnectionMessage;
		initialize();
		setBindingForComponents();
		myBehavior = new KOHLSWeightRequiredPopupBehavior(this, FORM_ID);
	}

	/**
	 * This method initializes the LowesWeightRequiredPopup class.
	 */
	private void initialize() {
		this.setData(YRCConstants.YRC_CONTROL_NAME, "this");
		GridLayout thislayout = new GridLayout(1, false);
		this.setLayout(thislayout);
		createCmpstContReqWarning();
	}

	/**
	 * This method initializes the composite.
	 */
	private void createCmpstContReqWarning() {

		cmpstRootPnl = new Composite(this, SWT.NONE);
		cmpstRootPnl.setBackgroundMode(SWT.INHERIT_NONE);

		cmpstRootPnl.setData(YRCConstants.YRC_CONTROL_NAME,
				"cmpstRootPnl");

		GridData cmpstNoWeightlayoutData = new GridData();
		cmpstNoWeightlayoutData.horizontalAlignment = 4;
		cmpstNoWeightlayoutData.verticalAlignment = 4;
		cmpstNoWeightlayoutData.grabExcessHorizontalSpace = true;
		cmpstNoWeightlayoutData.grabExcessVerticalSpace = true;
		cmpstRootPnl.setLayoutData(cmpstNoWeightlayoutData);

		GridLayout cmpstNoWeightlayout = new GridLayout(4, false);
		cmpstNoWeightlayout.numColumns = 5;
		cmpstRootPnl.setLayout(cmpstNoWeightlayout);

		createCmpstErrorMessage();

		Label filler = new Label(cmpstRootPnl, SWT.NONE);
		Label filler1 = new Label(cmpstRootPnl, SWT.NONE);
		button = new Button(cmpstRootPnl, SWT.NONE);
		button.setText(YRCPlatformUI.getString("CALCULATE_WEIGHT"));
		button.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				
				KOHLSPackShipmentBehavior packShipmentPageBehv = basePage.getWizardPageBehavior();
				packShipmentPageBehv.setContainerUnitWt();
				Element eleShipmentDetailsInfo = packShipmentPageBehv.returnModel("getShipmentDetailsForPackShipment");
				String strEnterpriseCode = eleShipmentDetailsInfo.getAttribute("EnterpriseCode");
				
				Element eleCurrentContainer = packShipmentPageBehv.returnModel("CurrentContainerSource");
				String strShipmentContainerKey = eleCurrentContainer.getAttribute("ShipmentContainerKey");
				Document inDoc = YRCXmlUtils.createDocument("Shipment");
				Element eleInputXml = inDoc.getDocumentElement();
				eleInputXml.setAttribute("ShipmentContainerKey", strShipmentContainerKey);
				
				basePage.getWizardPageBehavior().isTAREApplicable(strEnterpriseCode);
				getShell().close();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

		bttnRequireWeightCancel = new Button(cmpstRootPnl,
				SWT.PUSH);
		GridData bttnRequireWeightCancellayoutData = new GridData();
		bttnRequireWeightCancellayoutData.horizontalAlignment = 16777224;
		bttnRequireWeightCancellayoutData.verticalAlignment = 16777216;
		bttnRequireWeightCancellayoutData.grabExcessHorizontalSpace = true;
		bttnRequireWeightCancellayoutData.horizontalSpan = 4;
		bttnRequireWeightCancellayoutData.heightHint = 25;
		bttnRequireWeightCancellayoutData.widthHint = 80;

		bttnRequireWeightCancel.setLayoutData(bttnRequireWeightCancellayoutData);
		bttnRequireWeightCancel.setText(YRCPlatformUI.getString("CANCEL"));
		bttnRequireWeightCancel
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						getShell().close();

					}

					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// do nothing

					}
				});
	}

	/**
	 * closeLowesWeightRequiredPopup() method will close the Current Weight
	 * Warning Pop Up.
	 */
	private void closeLowesWeightRequiredPopup() {
		this.getShell().close();
	}

	/**
	 * createCmpstErrorMessage() will create a Waring Message and Composite to
	 * hold the Waring message.
	 */
	private void createCmpstErrorMessage() {

		cmpsWarnMessage = new Composite(cmpstRootPnl, SWT.NONE);
		cmpsWarnMessage.setBackgroundMode(SWT.INHERIT_NONE);

		cmpsWarnMessage.setData(YRCConstants.YRC_CONTROL_NAME,
				"cmpsWarnMessage");

		GridData cmpstErrorMessagelayoutData = new GridData();
		cmpstErrorMessagelayoutData.horizontalAlignment = 4;
		cmpstErrorMessagelayoutData.verticalAlignment = 16777216;
		cmpstErrorMessagelayoutData.grabExcessHorizontalSpace = true;
		cmpstErrorMessagelayoutData.horizontalSpan = 3;
		cmpsWarnMessage.setLayoutData(cmpstErrorMessagelayoutData);
		GridLayout cmpstErrorMessagelayout = new GridLayout(4, false);
		cmpsWarnMessage.setLayout(cmpstErrorMessagelayout);
		lblShowWeightRequiredMsg = new Label(cmpsWarnMessage, SWT.LEFT);
		GridData lblShowWeightRequiredMsglayoutData = new GridData();
		lblShowWeightRequiredMsglayoutData.horizontalAlignment = 4;
		lblShowWeightRequiredMsglayoutData.verticalAlignment = 4;
		lblShowWeightRequiredMsglayoutData.grabExcessHorizontalSpace = true;
		lblShowWeightRequiredMsglayoutData.grabExcessVerticalSpace = true;
		lblShowWeightRequiredMsglayoutData.horizontalSpan = 2;

		lblShowWeightRequiredMsg
				.setLayoutData(lblShowWeightRequiredMsglayoutData);
		if(!"N".equals(strScaleConnectionMessage)){
			lblShowWeightRequiredMsg.setText(strScaleConnectionMessage + "\n" + YRCPlatformUI.getString("CALCULATE_WEIGHT_MESSAGE"));
		}
		else{
			lblShowWeightRequiredMsg.setText(YRCPlatformUI.getString("CALCULATE_WEIGHT_MESSAGE"));
		}
		
	}

	/**
	 * setBindingForComponents() method will set the Binding for all the
	 * Controls of the POP UP.
	 */
	public void setBindingForComponents() {
		YRCButtonBindingData bttnRequireWeightCancelbd = new YRCButtonBindingData();
		bttnRequireWeightCancelbd.setName("bttnRequireWeightCancel");
		bttnRequireWeightCancel.setData(
				YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				bttnRequireWeightCancelbd);

		YRCLabelBindingData lblShowWeightRequiredMsgbd = new YRCLabelBindingData();
		lblShowWeightRequiredMsgbd.setName("lblShowWeightRequiredMsg");
		lblShowWeightRequiredMsgbd.setThemeName("Label");
		lblShowWeightRequiredMsg.setData(
				YRCConstants.YRC_LABEL_BINDING_DEFINITION,
				lblShowWeightRequiredMsgbd);

	}

	/*
	 * @see com.yantra.yfc.rcp.IYRCComposite#getFormId()
	 */
	public String getFormId() {
		return FORM_ID;
	}

	/*
	 * @see com.yantra.yfc.rcp.IYRCComposite#getHelpId()
	 */
	public String getHelpId() {
		return null;
	}

	/*
	 * @see com.yantra.yfc.rcp.IYRCComposite#getPanelHolder()
	 */
	public IYRCPanelHolder getPanelHolder() {
		return null;
	}

	/*
	 * @see com.yantra.yfc.rcp.IYRCComposite#getRootPanel()
	 */
	public Composite getRootPanel() {
		return this;
	}

}