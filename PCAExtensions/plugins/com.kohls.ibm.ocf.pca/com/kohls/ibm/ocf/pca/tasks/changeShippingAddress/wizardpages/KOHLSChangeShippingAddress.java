/*
 * Created on Jun 08,2013
 * 
 */
package com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.actions.KOHLSSaveChangeShippingAddressAction;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCWizardBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author Bhaskar
 * @build # 200810210800 
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public class KOHLSChangeShippingAddress extends Composite implements IYRCComposite{

	private KOHLSChangeShippingAddressBehavior myBehavior;
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.changeShippingAddress.wizardpages.KOHLSChangeShippingAddress";
	private YRCWizardBehavior wizBehavior;
	private KOHLSChangeShippingAddressHelper helper;
	Element inputEle = null;
	private Composite pnlRoot = null;
	private Composite pnlFooter = null;
	private Group grpChangeShipmentAddress = null;
	private Label lblShipmentNo = null;
	private Text txtShipmentNo = null;
	private Label lblCustomerName = null;
	private Label lblFirstLastName = null;
	private Label lblAddressLine1 = null;
	private Label lblAddressLine2 = null;
	private Label lblCity = null;
	private Label lblState = null;
	private Label lblPhoneNo = null;
	private Label lblEmailId = null;
	private Text txtAddress1 = null;
	private Text txtAddress2 = null;
	private Text txtCity = null;
	private Text txtState = null;
	private Text txtPhone = null;
	private Text txtEmaild = null;
	private Button buttonSave = null;
	private Label lblZip = null;
	private Text txtZip = null;
	public KOHLSChangeShippingAddress(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public KOHLSChangeShippingAddress(Composite parent, int style, Object input) {
		super(parent, style);
		helper = new KOHLSChangeShippingAddressHelper(this, FORM_ID, input);
		YRCEditorInput yEIn = (YRCEditorInput)input;
		inputEle = YRCXmlUtils.getXPathElement(yEIn.getXml(),"/ChangeShipmentAddress/ShipmentDetail/Shipment");
		 
		initialize();
		helper.screenInitialized();
		setBindingForComponents();
		helper.bindingRegistered();
		myBehavior = new KOHLSChangeShippingAddressBehavior(this, FORM_ID, inputEle, helper);
		helper.setBehavior(myBehavior);
	}
	
	private void initialize(){
		this.setData(YRCConstants.YRC_CONTROL_NAME, "this");
			createPnlRoot();
			createPnlFooter();
			this.setSize(new Point(820, 363));
			GridLayout thislayout = new GridLayout(2, false);
			thislayout.numColumns = 1;
		this.setLayout(thislayout);
	}

	public String getFormId() {
		return FORM_ID;
	}

	public String getHelpId() {
		return null;
	}

	public IYRCPanelHolder getPanelHolder() {
		return null;
	}
	
	public Composite getRootPanel() {
		return this;
	}
	
	public boolean setFocus(){
		return helper.setFocus();
	}
	public KOHLSChangeShippingAddressBehavior getBehavior() {
        return myBehavior;
    }
	
	public KOHLSChangeShippingAddressHelper getHelper() {
        return helper;
    }
	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = -1;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalSpan = 60;
		gridData.verticalAlignment = GridData.FILL;
		pnlRoot = new Composite(this, SWT.NONE);
		pnlRoot.setLayout(new GridLayout());
		createGrpChangeShipmentAddress();
		pnlRoot.setLayoutData(gridData);
	}

	/**
	 * This method initializes pnlFooter	
	 *
	 */
	private void createPnlFooter() {
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		pnlFooter = new Composite(this, SWT.NONE);
		pnlFooter.setLayout(new GridLayout());
		pnlFooter.setLayoutData(gridData1);
	}

	/**
	 * This method initializes grpChangeShipmentAddress	
	 *
	 */
	private void createGrpChangeShipmentAddress() {
		GridData gridData20 = new GridData();
		gridData20.widthHint = 150;
		GridData gridData19 = new GridData();
		gridData19.widthHint = 150;
		GridData gridData18 = new GridData();
		gridData18.widthHint = 150;
		GridData gridData17 = new GridData();
		gridData17.widthHint = 150;
		GridData gridData16 = new GridData();
		gridData16.widthHint = 150;
		GridData gridData15 = new GridData();
		gridData15.widthHint = 150;
		GridData gridData14 = new GridData();
		gridData14.widthHint = 150;
		GridData gridData13 = new GridData();
		gridData13.widthHint = 150;
		GridData gridData12 = new GridData();
		gridData12.horizontalAlignment = GridData.END;
		gridData12.verticalAlignment = GridData.CENTER;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.END;
		gridData11.verticalAlignment = GridData.CENTER;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.END;
		gridData10.verticalAlignment = GridData.CENTER;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.END;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.END;
		gridData8.verticalAlignment = GridData.CENTER;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.END;
		gridData7.verticalAlignment = GridData.CENTER;
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.END;
		gridData6.verticalAlignment = GridData.CENTER;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.END;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.END;
		gridData4.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalSpan = 2;
		gridData2.verticalAlignment = GridData.CENTER;
		gridData2.horizontalIndent = 1;
		gridData2.widthHint = 80;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.horizontalAlignment = GridData.BEGINNING;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.grabExcessVerticalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		grpChangeShipmentAddress = new Group(pnlRoot, SWT.NONE);
		grpChangeShipmentAddress.setText("Change Shipment Address");
		grpChangeShipmentAddress.setLayout(gridLayout);
		grpChangeShipmentAddress.setLayoutData(gridData3);
		lblShipmentNo = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblShipmentNo.setText("Shipment No");
		lblShipmentNo.setLayoutData(gridData4);
		txtShipmentNo = new Text(grpChangeShipmentAddress, SWT.NONE);
		txtShipmentNo.setEditable(false);
		txtShipmentNo.setEnabled(false);
		txtShipmentNo.setLayoutData(gridData13);
		lblCustomerName = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblCustomerName.setText("Customer Name");
		lblCustomerName.setLayoutData(gridData5);
		lblFirstLastName = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblFirstLastName.setText("");
		lblAddressLine1 = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblAddressLine1.setText("Address1");
		lblAddressLine1.setLayoutData(gridData6);
		txtAddress1 = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtAddress1.setLayoutData(gridData14);
		lblAddressLine2 = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblAddressLine2.setText("Address2");
		lblAddressLine2.setLayoutData(gridData7);
		txtAddress2 = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtAddress2.setLayoutData(gridData15);
		lblCity = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblCity.setText("City");
		lblCity.setLayoutData(gridData8);
		txtCity = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtCity.setLayoutData(gridData16);
		lblState = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblState.setText("State");
		lblState.setLayoutData(gridData9);
		txtState = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtState.setLayoutData(gridData17);
		lblZip = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblZip.setText("Zip Code");
		lblZip.setLayoutData(gridData10);
		txtZip = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtZip.setLayoutData(gridData18);
		lblPhoneNo = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblPhoneNo.setText("Phone");
		lblPhoneNo.setLayoutData(gridData11);
		txtPhone = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtPhone.setLayoutData(gridData19);
		lblEmailId = new Label(grpChangeShipmentAddress, SWT.NONE);
		lblEmailId.setText("Email-ID");
		lblEmailId.setLayoutData(gridData12);
		txtEmaild = new Text(grpChangeShipmentAddress, SWT.BORDER);
		txtEmaild.setLayoutData(gridData20);
		buttonSave = new Button(grpChangeShipmentAddress, SWT.NONE);
		buttonSave.setText("Save");
		buttonSave.setLayoutData(gridData2);
	}      
	
public void setBindingForComponents() {
		
		Element eleToAdress = YRCXmlUtils.getChildElement(inputEle, KOHLSPCAConstants.E_TO_ADDRESS);
		YRCLabelBindingData lblShipmentNobd = new YRCLabelBindingData();
		lblShipmentNobd.setName("lblShipmentNo"); 
		lblShipmentNo.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblShipmentNobd);
		helper.setAdditionalLabelBindingAttributes(lblShipmentNobd, "lblShipmentNo");
		YRCTextBindingData txtShipmentbd = new YRCTextBindingData();
		txtShipmentbd.setName("txtShipment");
		String strShipmentNo= inputEle.getAttribute(KOHLSPCAConstants.A_SHIPMENT_NO);
		txtShipmentNo.setText(strShipmentNo);
		txtShipmentNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtShipmentbd);
		txtShipmentbd.setTargetBinding("ChangeShippingAddressInput:/Shipment/@ShipmentNo");	
		
		
		YRCLabelBindingData lblCustomerNamebd = new YRCLabelBindingData();
		lblCustomerNamebd.setName("lblCustomerName"); 
		lblCustomerName.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblCustomerNamebd);
		helper.setAdditionalLabelBindingAttributes(lblCustomerNamebd, "lblCustomerName");
		YRCLabelBindingData lblCustomerNameValuebd = new YRCLabelBindingData();
		lblCustomerNameValuebd.setName("lblCustomerName");
		String firstName = eleToAdress.getAttribute(KOHLSPCAConstants.A_FIRST_NAME);
		String lastName = eleToAdress.getAttribute(KOHLSPCAConstants.A_LAST_NAME);
		
		String custName =firstName+" "+lastName; 
		lblFirstLastName.setText(custName);
		lblFirstLastName.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblCustomerNameValuebd);
		//txtAddressbd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@AddressLine1");
		YRCLabelBindingData lblAddressbd = new YRCLabelBindingData();
		lblAddressbd.setName("lblAddress"); 
		lblAddressLine1.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblAddressbd);
		helper.setAdditionalLabelBindingAttributes(lblAddressbd, "lblAddress");

		YRCTextBindingData txtAddressbd = new YRCTextBindingData();
		txtAddressbd.setName("txtAddress");
		String address1 = eleToAdress.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE1);
		txtAddress1.setText(address1);
		txtAddress1.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtAddressbd);
		txtAddressbd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@AddressLine1");
		txtAddressbd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@AddressLine1");
	
		
		YRCLabelBindingData lblAddressbd2 = new YRCLabelBindingData();
		lblAddressbd2.setName("lblAddress2"); 
		lblAddressLine2.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblAddressbd2);
		helper.setAdditionalLabelBindingAttributes(lblAddressbd2, "lblAddress2");
		YRCTextBindingData txtAddressbd2 = new YRCTextBindingData();
		txtAddressbd2.setName("txtAddress2");
		String address2 = eleToAdress.getAttribute(KOHLSPCAConstants.A_ADDRESS_LINE2);
		txtAddress2.setText(address2);
		txtAddress2.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtAddressbd2);
		txtAddressbd2.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@AddressLine2");
		txtAddressbd2.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@AddressLine2");
		
		
		
		
		YRCLabelBindingData lblCitybd = new YRCLabelBindingData();
		lblCitybd.setName("lblState"); 
		lblCity.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblCitybd);
		helper.setAdditionalLabelBindingAttributes(lblAddressbd2, "lblCity");
		YRCTextBindingData txtCitybd = new YRCTextBindingData();
		txtCitybd.setName("txtCity");
		String strCity = eleToAdress.getAttribute(KOHLSPCAConstants.A_CITY);
		txtCity.setText(strCity);
		txtCity.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtCitybd);
		txtCitybd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@City");
		txtCitybd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@City");
		
		YRCLabelBindingData lblStatebd = new YRCLabelBindingData();
		lblStatebd.setName("lblState"); 
		lblState.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblStatebd);
		helper.setAdditionalLabelBindingAttributes(lblStatebd, "lblState");
		YRCTextBindingData txtStatebd = new YRCTextBindingData();
		txtStatebd.setName("txtState");
		String strState = eleToAdress.getAttribute(KOHLSPCAConstants.A_STATE);
		txtState.setText(strState);
		txtState.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtStatebd);
		txtStatebd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@State");
		txtStatebd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@State");
				
		YRCLabelBindingData lblZipbd = new YRCLabelBindingData();
		lblZipbd.setName("lblZip"); 
		lblZip.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblZipbd);
		helper.setAdditionalLabelBindingAttributes(lblZipbd, "lblZip");
		YRCTextBindingData txtZipbd = new YRCTextBindingData();
		txtZipbd.setName("txtZip");
		String strZipCode = eleToAdress.getAttribute(KOHLSPCAConstants.A_ZIP_CODE);
		txtZip.setText(strZipCode);
		txtZip.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtZipbd);
		txtZipbd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@ZipCode");
		txtZipbd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@ZipCode");
		
		YRCLabelBindingData lblPhonebd = new YRCLabelBindingData();
		lblPhonebd.setName("lblPhone"); 
		lblPhoneNo.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblPhonebd);
		helper.setAdditionalLabelBindingAttributes(lblPhonebd, "lblPhone");
		YRCTextBindingData txtPhonebd = new YRCTextBindingData();
		txtPhonebd.setName("txtPhone");
		String strDayPhone = eleToAdress.getAttribute(KOHLSPCAConstants.A_DAY_PHONE);
		txtPhone.setText(strDayPhone);
		txtPhone.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtPhonebd);
		txtPhonebd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@DayPhone");
		txtPhonebd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@DayPhone");
		
		
		
		YRCLabelBindingData lblEmailbd = new YRCLabelBindingData();
		lblEmailbd.setName("lblEmailId"); 
		lblEmailId.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblEmailbd);
		helper.setAdditionalLabelBindingAttributes(lblEmailbd, "lblEmailId");
		YRCTextBindingData txtEmailbd = new YRCTextBindingData();
		txtEmailbd.setName("txtEmailId");
		String strEMailID = eleToAdress.getAttribute(KOHLSPCAConstants.A_EAMIL_ID);
		txtEmaild.setText(strEMailID);
		txtEmaild.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtEmailbd);
		txtEmailbd.setTargetBinding("ChangeShippingAddressInput:/Shipment/ToAddress/@EMailID");
		txtEmailbd.setSourceBinding("CurrentShipToAddress:/Shipment/ToAddress/@EMailID");
		
		YRCButtonBindingData bttnSearchbd = new YRCButtonBindingData();
		bttnSearchbd.setName("buttonSave");
		buttonSave.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				bttnSearchbd);
		
		buttonSave.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				YRCPlatformUI.fireAction(KOHLSSaveChangeShippingAddressAction.ACTION_ID);
			}
		});
		
		
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"