package com.kohls.ibm.ocf.pca.tasks.packshipment.popups;

//Java Imports-None

//MISC Imports
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.kohls.ibm.ocf.pca.tasks.packshipment.wizardpages.KOHLSPackShipmentPage;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;

/**
 * *********************************************************************
 * KOHLSRecordShortagePopup class is used to Show the Waring Message when
 * to prompt the user to calculate the container record using
 * the existing catalogue information
 * *********************************************************************
 */
public class KOHLSRecordShortagePopup extends Composite implements
		IYRCComposite {

	private static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.packshipment.popups.KOHLSRecordShortagePopup";  //  @jve:decl-index=0:

	private KOHLSRecordShortagePopupBehavior myBehavior = null;
	
	private Composite cmpstRootPnl;

	private Composite cmpsWarnMessage;

	private Label lblRecordReasonMsg;

	private Button bttnRecordReasonCancel;

	private Button buttonOk = null;

	private Label label = null;

	private Label lblShortageReason = null;

	private Combo cmbReasonCode = null;
	
	private Composite parent = null;

	/**
	 * Constructor for the LowesrecordRequiredPopup.
	 * 
	 * @param parent
	 * @param style
	 */
	public KOHLSRecordShortagePopup(Composite parent, int style) {
		super(parent, style);
		initialize();
		setBindingForComponents();
		this.parent = parent;
		myBehavior = new KOHLSRecordShortagePopupBehavior(this, FORM_ID);
	}

	/**
	 * This method initializes the LowesrecordRequiredPopup class.
	 */
	private void initialize() {
		this.setData(YRCConstants.YRC_CONTROL_NAME, "this");
		GridLayout thislayout = new GridLayout(1, false);
		this.setLayout(thislayout);
		createCmpstContReqWarning();
		setComboBinding();
	}

	private void setComboBinding() {
		// TODO Auto-generated method stub
		YRCComboBindingData bdCmbRecordShortageNo = new YRCComboBindingData();
    	bdCmbRecordShortageNo.setName("cmbReasonCode");
    	bdCmbRecordShortageNo.setSourceBinding("CommonCode:CommonCodeList/CommonCode/@CodeLongDescription");
    	bdCmbRecordShortageNo.setCodeBinding("CodeLongDescription");
    	//bdCmbRecordShortageNo.setTargetBinding("getShipmentDetailsForPackShipment:Shipment/ShipmentStatusAudit/@ReasonCode");
    	bdCmbRecordShortageNo.setListBinding("CommonCode:CommonCodeList/CommonCode");
    	bdCmbRecordShortageNo.setDescriptionBinding("@CodeLongDescription");
    	cmbReasonCode.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbRecordShortageNo);
		bdCmbRecordShortageNo.setMandatory(true);
		
	}

	/**
	 * This method initializes the composite.
	 */
	private void createCmpstContReqWarning() {

		cmpstRootPnl = new Composite(this, SWT.NONE);
		cmpstRootPnl.setBackgroundMode(SWT.INHERIT_NONE);

		cmpstRootPnl.setData(YRCConstants.YRC_CONTROL_NAME,
				"cmpstRootPnl");

		GridData cmpstNorecordlayoutData = new GridData();
		cmpstNorecordlayoutData.horizontalAlignment = 4;
		cmpstNorecordlayoutData.verticalAlignment = 4;
		cmpstNorecordlayoutData.grabExcessHorizontalSpace = true;
		cmpstNorecordlayoutData.grabExcessVerticalSpace = true;
		cmpstRootPnl.setLayoutData(cmpstNorecordlayoutData);

		GridLayout cmpstNorecordlayout = new GridLayout(4, false);
		cmpstNorecordlayout.numColumns = 7;
		cmpstRootPnl.setLayout(cmpstNorecordlayout);

		createCmpstErrorMessage();

		Label filler = new Label(cmpstRootPnl, SWT.NONE);
		Label filler1 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler2 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler5 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler4 = new Label(cmpstRootPnl, SWT.NONE);
		lblShortageReason = new Label(cmpstRootPnl, SWT.NONE);
		lblShortageReason.setText("SHORTAGE_REASON");
		createCmbReasonCode();
		label = new Label(cmpstRootPnl, SWT.NONE);
		label.setText("");
		Label filler6 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler7 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler8 = new Label(cmpstRootPnl, SWT.NONE);
		Label filler3 = new Label(cmpstRootPnl, SWT.NONE);
		buttonOk = new Button(cmpstRootPnl, SWT.NONE);
		buttonOk.setText("OK");
		
		GridData bttnRecordOklayoutData = new GridData();
		bttnRecordOklayoutData.grabExcessHorizontalSpace = true;
		bttnRecordOklayoutData.horizontalSpan = 2;
		bttnRecordOklayoutData.heightHint = 25;
		bttnRecordOklayoutData.widthHint = 80;

		buttonOk.setLayoutData(bttnRecordOklayoutData);
		buttonOk.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
			public void widgetSelected(
					org.eclipse.swt.events.SelectionEvent e) {
				String strReasonCode = myBehavior.getFieldValue("cmbReasonCode");
				KOHLSPackShipmentPage packShipPage = (KOHLSPackShipmentPage)parent;
				packShipPage.getWizardPageBehavior().recordShortage(strReasonCode);
				getShell().close();
				// add code to save reason code
			}

			public void widgetDefaultSelected(SelectionEvent e) {
							
			}
		});

		bttnRecordReasonCancel = new Button(cmpstRootPnl,
				SWT.PUSH);
		GridData bttnRecordReasonCancellayoutData = new GridData();
		bttnRecordReasonCancellayoutData.horizontalAlignment = 16777224;
		bttnRecordReasonCancellayoutData.verticalAlignment = 16777216;
		bttnRecordReasonCancellayoutData.grabExcessHorizontalSpace = true;
		bttnRecordReasonCancellayoutData.horizontalSpan = 4;
		bttnRecordReasonCancellayoutData.heightHint = 25;
		bttnRecordReasonCancellayoutData.widthHint = 80;

		bttnRecordReasonCancel.setLayoutData(bttnRecordReasonCancellayoutData);
		bttnRecordReasonCancel.setText(YRCPlatformUI.getString("CANCEL"));
		bttnRecordReasonCancel
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// do nothing
						getShell().close();
					}

					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						// do nothing
						
					}
				});
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
		lblRecordReasonMsg = new Label(cmpsWarnMessage, SWT.LEFT);
		GridData lblRecordReasonMsglayoutData = new GridData();
		lblRecordReasonMsglayoutData.horizontalAlignment = 4;
		lblRecordReasonMsglayoutData.verticalAlignment = 4;
		lblRecordReasonMsglayoutData.grabExcessHorizontalSpace = true;
		lblRecordReasonMsglayoutData.grabExcessVerticalSpace = true;
		lblRecordReasonMsglayoutData.horizontalSpan = 2;

		lblRecordReasonMsg
				.setLayoutData(lblRecordReasonMsglayoutData);
		lblRecordReasonMsg.setText("CONTAINER_CLOSE_RECORD_SHORTAGE_PROMPT");
	}

	/**
	 * setBindingForComponents() method will set the Binding for all the
	 * Controls of the POP UP.
	 */
	public void setBindingForComponents() {
		YRCButtonBindingData bttnRecordReasonCancelbd = new YRCButtonBindingData();
		bttnRecordReasonCancelbd.setName("bttnRecordReasonCancel");
		bttnRecordReasonCancel.setData(
				YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				bttnRecordReasonCancelbd);

		YRCLabelBindingData lblRecordReasonMsgbd = new YRCLabelBindingData();
		lblRecordReasonMsgbd.setName("lblRecordReasonMsg");
		lblRecordReasonMsgbd.setThemeName("Label");
		lblRecordReasonMsg.setData(
				YRCConstants.YRC_LABEL_BINDING_DEFINITION,
				lblRecordReasonMsgbd);

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

	/**
	 * This method initializes cmbReasonCode	
	 *
	 */
	private void createCmbReasonCode() {
		cmbReasonCode = new Combo(cmpstRootPnl, SWT.READ_ONLY);
	}

	public KOHLSRecordShortagePopupBehavior returnBehavior(){
		return myBehavior;
	}
}