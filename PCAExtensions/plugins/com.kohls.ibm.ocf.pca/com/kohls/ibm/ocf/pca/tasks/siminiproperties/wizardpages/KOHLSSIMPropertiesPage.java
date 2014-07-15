package com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages;

import java.util.ArrayList;
import java.util.Arrays;

import javax.print.PrintService;



import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;


import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions.KOHLSCloseEditorAction;
import com.kohls.ibm.ocf.pca.tasks.siminiproperties.actions.KOHLSSavePropertiesAction;
import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCDesktopUI;
import com.yantra.yfc.rcp.YRCEditorInput;
import com.yantra.yfc.rcp.YRCEditorPart;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCXmlUtils;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Button;
import org.w3c.dom.Element;
import org.eclipse.swt.widgets.Group;


/**
 * The Class was created for SIM INI Properties Creation. 
 * 
 * @author IBM_ADMIN
 *
 */
public class KOHLSSIMPropertiesPage  extends Composite implements IYRCComposite {

//	 The form id for the page.
	public static final String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.siminiproperties.wizardpages.KOHLSSIMPropertiesPage";
	
	private KOHLSSIMPropertiesPageBehavior myBehavior = null;
	
	Element eleSimPropertiesList = KohlsApplicationInitializer.eleVerifyTerminalProperties;  //  @jve:decl-index=0:

	private Composite pnlRoot = null;
	
	public static String strcollatecombo="N";
	
	private Composite cmpstTopPanel = null;

	private Composite cmpstTopLeftPanel = null;

	private Label lblCustAccount = null;

	private Composite cmpstCustInfo = null;

	private Label lblTerminalID = null;

	private Label lblShipmentsPerTote = null;

	private Text txtShipmentsPerTote = null;

	private Label lblDefaultPackContainer = null;

	private Text txtDefaultPackContainer = null;

	private Label lblSerialPort = null;

	private Text txtDefaultPackStation = null;

	//Start changes for SFS June Release
	
	private Text txtCollatePrintingEnabled = null;
	
	private Label lblCollatePrintingEnabled = null;
	
	private Combo comboCollatePrintingEnabled = null;
	
	//End changes for SFS June release

	private Composite cmpstCustomerCreation = null;

	private Composite cmpstBtnPnl = null;

	

	private Text txtTerminalID = null;

	private Combo comboDefaultPackPrinter = null;

	private Combo comboDefaultLabelPrinter = null;

	private Text txtSerialPort = null;

	private Combo comboDefaultPackPrinter1 = null;

	private Label lblPackStation = null;
	private Label lblDefaultLabelPrinter = null;
	private Button buttonSave = null;
	
	private Label lblDefaultPrinter = null;
	private Label lblDefaultPackPrinter = null;
	PrintService[] printServices = KohlsApplicationInitializer.printServices;
	private Button buttonClose1 = null;

	private Group RadioButtonGrp = null;

	public Button radioButtonUseScale = null;

	public Button radioButtonCalcualteWeight = null;
	/**
 * The constructor for the java class LowesCustomerCreationPopupPage. 
 * @param parent
 * @param style
 */
	public KOHLSSIMPropertiesPage(Composite parent, int style,YRCEditorInput input) {
		super(parent, style);
		initialize();	
		Element eleInput =  input.getXml();
		String strAction = eleInput.getAttribute("Action");
		setBindingForAllComponents();
		
		myBehavior = new KOHLSSIMPropertiesPageBehavior(this, FORM_ID,strAction);	
		
	}

/**
 * The method is used to set the binding for all the components.
 *
 */
	private void setBindingForAllComponents() {
	
		setBindingForControls();
		setBindingForButtons();
	}

	
	private void setBindingForButtons() {
		YRCButtonBindingData bttnSearchbd = new YRCButtonBindingData();
		bttnSearchbd.setName("bttnSearch");
		bttnSearchbd.setActionHandlerEnabled(true);
		bttnSearchbd
				.setActionId(KOHLSSavePropertiesAction.ACTION_ID);
		buttonSave.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				bttnSearchbd);
		
		YRCButtonBindingData bttnClosebd = new YRCButtonBindingData();
		bttnClosebd.setName("buttonClose1");
		bttnClosebd.setActionHandlerEnabled(true);
		bttnClosebd
				.setActionId(KOHLSCloseEditorAction.ACTION_ID);
		buttonClose1.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				bttnClosebd);
		//helper.setAdditionalButtonBindingAttributes(bttnSearchbd, "bttnSearch");

	
}
	/**
	 * The method is used to set binding for Controls
	 *
	 */

	private void setBindingForControls() {
		
		
		YRCLabelBindingData lblTerminalIDbd = new YRCLabelBindingData();
		lblTerminalIDbd.setName("lblTerminalID"); 
		lblTerminalID.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblTerminalIDbd);
		Element eleSimProperties ;

		YRCTextBindingData txttxtTerminalIDbd = new YRCTextBindingData();
		txttxtTerminalIDbd.setName("txtTerminalID");
		if(!YRCPlatformUI.isVoid(eleSimPropertiesList)){
			eleSimProperties = YRCXmlUtils.getChildElement(eleSimPropertiesList, "SIMINIProperties");
			if(!YRCPlatformUI.isVoid(eleSimProperties)){
		//txttxtTerminalIDbd.setSourceBinding("SIMINIProperties:/SIMINIProperties/@TerminalID");
		txtTerminalID.setText(eleSimProperties.getAttribute("TerminalID"));
			}
			else
			{
				txtTerminalID.setText(KohlsApplicationInitializer.strComputername);
			}
		txttxtTerminalIDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@TerminalID");
		txtTerminalID.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txttxtTerminalIDbd);
		}
		if(!YRCPlatformUI.isVoid(eleSimPropertiesList)){
		eleSimProperties = YRCXmlUtils.getChildElement(eleSimPropertiesList, "SIMINIProperties");
		if(!YRCPlatformUI.isVoid(eleSimProperties)){
		
			txtShipmentsPerTote.setText(eleSimProperties.getAttribute("ShipmentsperTote"));
			txtDefaultPackContainer.setText(eleSimProperties.getAttribute("DefaultPackContainer"));
			txtDefaultPackStation.setText(eleSimProperties.getAttribute("DefaultPackStation"));
			comboDefaultPackPrinter1.setText(eleSimProperties.getAttribute("DefaultPrinter"));
			comboDefaultPackPrinter.setText(eleSimProperties.getAttribute("DefaultPackPrinter"));
			comboDefaultLabelPrinter.setText(eleSimProperties.getAttribute("DefaultLabelPrinter"));
			txtSerialPort.setText(eleSimProperties.getAttribute("SerialPort"));
			strcollatecombo = eleSimProperties.getAttribute("CollatePrintingEnabled");
			if(strcollatecombo.isEmpty())
			{
				comboCollatePrintingEnabled.setText("N");
			}
			else
			{
				comboCollatePrintingEnabled.setText(strcollatecombo);
			}
			
			
			
				if(!YRCPlatformUI.isVoid(eleSimProperties.getAttribute("WeightCaculator"))){
					if(eleSimProperties.getAttribute("WeightCaculator").equals("Calculate System Weight for Container")){
			
					radioButtonCalcualteWeight.setSelection(true);
					radioButtonCalcualteWeight.setEnabled(true);
				}else{
					radioButtonUseScale.setSelection(true);
					radioButtonUseScale.setEnabled(true);
				}
				
					
			}
		}
	}
			
		

		YRCLabelBindingData lblShipmentsPerToteDbd = new YRCLabelBindingData();
		lblShipmentsPerToteDbd.setName("lblShipmentsPerTote"); 
		lblShipmentsPerTote.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblShipmentsPerToteDbd);
		
		YRCTextBindingData txtShipmentsPerToteIDbd = new YRCTextBindingData();
		txtShipmentsPerToteIDbd.setName("txtShipmentsPerTote");
		txtShipmentsPerTote.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtShipmentsPerToteIDbd);
		txtShipmentsPerToteIDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@ShipmentsPerTote");
		
		YRCLabelBindingData lblDefaultPackContainerDbd = new YRCLabelBindingData();
		lblDefaultPackContainerDbd.setName("lblDefaultPackContainer"); 
		lblDefaultPackContainer.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblDefaultPackContainerDbd);
		
		YRCTextBindingData txtDefaultPackContainerIDbd = new YRCTextBindingData();
		txtDefaultPackContainerIDbd.setName("txtDefaultPackContainer");
		txtDefaultPackContainer.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtDefaultPackContainerIDbd);
		txtDefaultPackContainerIDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@DefaultPackContainer");
		
		YRCLabelBindingData lblDefaultPackStationDbd = new YRCLabelBindingData();
		lblDefaultPackContainerDbd.setName("lblDefaultPackContainer"); 
		lblDefaultPackContainer.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblDefaultPackStationDbd);
		
		YRCTextBindingData txtDefaultPackStationbd = new YRCTextBindingData();
		txtDefaultPackStationbd.setName("txtDefaultPackStation");
		txtDefaultPackStation.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtDefaultPackStationbd);
		txtDefaultPackStationbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@DefaultPackStation");
		
		YRCLabelBindingData lblPackStationDbd = new YRCLabelBindingData();
		lblDefaultPackContainerDbd.setName("lblPackStation"); 
		lblDefaultPackContainer.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblPackStationDbd);
		
		YRCComboBindingData cmbDefaultPrintDbd= new YRCComboBindingData();
		cmbDefaultPrintDbd.setName("comboDefaultPackPrinter1");
		comboDefaultPackPrinter1.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, cmbDefaultPrintDbd);
		cmbDefaultPrintDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@DefaultPrinter");
		
		YRCLabelBindingData lblDefaultPackPrinterDbd = new YRCLabelBindingData();
		lblDefaultPackPrinterDbd.setName("lblDefaultPackPrinter"); 
		lblDefaultPackPrinter.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblDefaultPackPrinterDbd);
		
		YRCComboBindingData cmbDefaultLabelPrinterDbd = new YRCComboBindingData();
		cmbDefaultLabelPrinterDbd.setName("comboDefaultPackPrinter");
		comboDefaultPackPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, cmbDefaultLabelPrinterDbd);
		cmbDefaultLabelPrinterDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@DefaultPackPrinter");
		
		YRCLabelBindingData lblDefaultLabelPrinterDbd = new YRCLabelBindingData();
		lblDefaultLabelPrinterDbd.setName("lblDefaultPackContainer"); 
		lblDefaultLabelPrinter.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblDefaultLabelPrinterDbd);
		
		YRCComboBindingData cmbcomboDefaultLabelPrinterDbd = new YRCComboBindingData();
		cmbcomboDefaultLabelPrinterDbd.setName("comboDefaultLabelPrinter");
		comboDefaultLabelPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, cmbcomboDefaultLabelPrinterDbd);
		cmbcomboDefaultLabelPrinterDbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@DefaultLabelPrinter");
		
		YRCLabelBindingData lblSerialPortDbd = new YRCLabelBindingData();
		lblSerialPortDbd.setName("lblSerialPort"); 
		lblSerialPort.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblSerialPortDbd);
		
		YRCTextBindingData txtSerialPortbd = new YRCTextBindingData();
		txtSerialPortbd.setName("txtSerialPort");
		txtSerialPort.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtSerialPortbd);
		txtSerialPortbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@SerialPort");
		
		//Start changes for SFS June Release
		
		YRCLabelBindingData lblCollatePrintingEnabledbd = new YRCLabelBindingData();
		lblCollatePrintingEnabledbd.setName("lblCollatePrintingEnabled"); 
		lblCollatePrintingEnabled.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblCollatePrintingEnabledbd);
		
		
		YRCComboBindingData cmbcomboCollatePrintingEnabledbd = new YRCComboBindingData();
		cmbcomboCollatePrintingEnabledbd.setName("comboDefaultLabelPrinter");
		comboCollatePrintingEnabled.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, cmbcomboCollatePrintingEnabledbd);
		cmbcomboCollatePrintingEnabledbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@CollatePrintingEnabled");
		
		//End changes for SFS June Release
		
	/*	YRCTextBindingData txttxtCollatePrintingEnabledbd = new YRCTextBindingData();
		txttxtCollatePrintingEnabledbd.setName("txtCollatePrintingEnabled");
		txtCollatePrintingEnabled.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txttxtCollatePrintingEnabledbd);
		txttxtCollatePrintingEnabledbd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@ExtnCollateFlag");*/
	}

	

		/**
	 * The method is used to initialize
	 */
	private void initialize() {
		this.setData(YRCConstants.YRC_CONTROL_NAME, "root");
		setLayout(new FillLayout());
		createPnlRoot();
		
	    setSize(new Point(1100, 550));
		
	}
/**
 * The method returns the form id
 */
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

		return pnlRoot;
	}

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 1;
		gridLayout1.verticalSpacing = 0;
		gridLayout1.marginWidth = 0;
		gridLayout1.marginHeight = 0;
		gridLayout1.horizontalSpacing = 0;
		pnlRoot = new Composite(this, SWT.NONE);
		pnlRoot.setLayout(gridLayout1);
		createCmpstCustomerCreation();
	}

	/**
	 * This method initializes cmpstTopPanel	
	 *
	 */
	private void createCmpstTopPanel() {
		GridData gridData31 = new GridData();
		gridData31.horizontalAlignment = GridData.FILL;
		gridData31.grabExcessHorizontalSpace = true;
		gridData31.grabExcessVerticalSpace = false;
		gridData31.verticalAlignment = GridData.FILL;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 2;
		gridLayout.marginHeight = 0;
		gridLayout.makeColumnsEqualWidth = true;
		cmpstTopPanel = new Composite(cmpstCustomerCreation, SWT.NONE);
		cmpstTopPanel.setBackgroundMode(SWT.INHERIT_NONE);
		cmpstTopPanel.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstTopPanel");
		cmpstTopPanel.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createCmpstTopLeftPanel();
		cmpstTopPanel.setLayout(gridLayout);
		cmpstTopPanel.setLayoutData(gridData31);
			gridData31.heightHint = -1;
			cmpstTopPanel.setLayoutData(gridData31);
	}

	/**
	 * This method initializes cmpstTopLeftPanel	
	 *
	 */
	private void createCmpstTopLeftPanel() {
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = true;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.heightHint = 20;
		gridData6.verticalAlignment = GridData.BEGINNING;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 1;
		gridLayout3.horizontalSpacing = 5;
		gridLayout3.marginHeight = 0;
		gridLayout3.marginWidth = 0;
		gridLayout3.verticalSpacing = 0;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.heightHint = -1;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		cmpstTopLeftPanel = new Composite(cmpstTopPanel, SWT.BORDER);
		cmpstTopLeftPanel.setLayoutData(gridData2);
		cmpstTopLeftPanel.setLayout(gridLayout3);
	
		lblCustAccount = new Label(cmpstTopLeftPanel, SWT.BORDER);
		lblCustAccount.setText("Properties");
		lblCustAccount.setLayoutData(gridData6);
		lblCustAccount.setData(YRCConstants.YRC_CONTROL_NAME, "compositeHeader");
		lblCustAccount.setBackgroundImage(YRCPlatformUI.getImage("PanelHeaderImage"));
		lblCustAccount.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "NoTheme");
		lblCustAccount.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		
		createCmpstCustInfo();
	}

	

	/**
	 * This method initializes cmpstCustInfo	
	 *
	 */
	private void createCmpstCustInfo() {
		GridData gridData61 = new GridData();
		gridData61.widthHint = 80;
		GridData gridData5 = new GridData();
		gridData5.widthHint = 100;
		GridData gridData = new GridData();
		gridData.widthHint = 80;
		gridData.verticalAlignment = org.eclipse.swt.layout.GridData.END;
		gridData.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		GridData gridData3 = new GridData();
		gridData3.widthHint = 100;
		GridData gridData35 = new GridData();
		gridData35.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData35.grabExcessHorizontalSpace = true;
		gridData35.widthHint = 100;
		gridData35.verticalAlignment = GridData.CENTER;
		GridData gridData34 = new GridData();
		gridData34.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData34.grabExcessHorizontalSpace = true;
		gridData34.widthHint = 100;
		gridData34.verticalAlignment = GridData.CENTER;
		GridData gridData23 = new GridData();
		gridData23.grabExcessHorizontalSpace = true;
		gridData23.verticalAlignment = GridData.BEGINNING;
		gridData23.widthHint = 100;
		gridData23.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.grabExcessHorizontalSpace = true;
		gridData7.grabExcessVerticalSpace = true;
		gridData7.verticalSpan = 2;
		gridData7.heightHint = -1;
		gridData7.verticalAlignment = GridData.FILL;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 2;
		gridLayout6.verticalSpacing = 8;
		gridLayout6.marginWidth = 10;
		gridLayout6.marginHeight = 4;
		gridLayout6.horizontalSpacing = 30;
		cmpstCustInfo = new Composite(cmpstTopLeftPanel, SWT.NONE);
		cmpstCustInfo.setLayout(gridLayout6);
		cmpstCustInfo.setLayoutData(gridData7);
		lblTerminalID = new Label(cmpstCustInfo, SWT.NONE);
		lblTerminalID.setText("Terminal ID");
		txtTerminalID = new Text(cmpstCustInfo, SWT.BORDER | SWT.READ_ONLY);
		txtTerminalID.setLayoutData(gridData5);
		Element eleTerminalProperties = KohlsApplicationInitializer.eleVerifyTerminalProperties;
		
		//String strTerminalIdD = eleTerminalProperties.getAttribute("TerminalID");
		//txtTerminalID.setText(strTerminalIdD);
		lblShipmentsPerTote = new Label(cmpstCustInfo, SWT.NONE);
		lblShipmentsPerTote.setText("Shipments Per Tote");
		txtShipmentsPerTote = new Text(cmpstCustInfo, SWT.BORDER);
		txtShipmentsPerTote.setLayoutData(gridData23);
		
		lblDefaultPackContainer = new Label(cmpstCustInfo, SWT.NONE);
		lblDefaultPackContainer.setText("Default Pack Container");
		txtDefaultPackContainer = new Text(cmpstCustInfo, SWT.BORDER);
		txtDefaultPackContainer.setLayoutData(gridData34);
		lblPackStation = new Label(cmpstCustInfo, SWT.NONE);
		lblPackStation.setText("Default Pack Station");
		txtDefaultPackStation = new Text(cmpstCustInfo, SWT.BORDER);
		txtDefaultPackStation.setLayoutData(gridData35);
		lblDefaultPrinter = new Label(cmpstCustInfo, SWT.NONE);
		lblDefaultPrinter.setText("Default Printer");
		createComboDefaultPackPrinter1();
		lblDefaultPackPrinter = new Label(cmpstCustInfo, SWT.NONE);
		lblDefaultPackPrinter.setText("Default Pack Printer");
		createComboDefaultPackPrinter();
		lblDefaultLabelPrinter = new Label(cmpstCustInfo, SWT.NONE);
		lblDefaultLabelPrinter.setText("Default Label Printer");
		createComboDefaultLabelPrinter();
		lblSerialPort = new Label(cmpstCustInfo, SWT.NONE);
		lblSerialPort.setText("Serial Port");
		txtSerialPort = new Text(cmpstCustInfo, SWT.BORDER);
		txtSerialPort.setLayoutData(gridData3);
		
		
		//Start changes for SFS June Release
		lblCollatePrintingEnabled = new Label(cmpstCustInfo, SWT.NONE);
		lblCollatePrintingEnabled.setText("Collate Printing Enabled");
		createComboCollatePrintingEnabled();
		//End changes for SFS June Release
		
		
		/*txtCollatePrintingEnabled = new Text(cmpstCustInfo, SWT.BORDER);
		txtCollatePrintingEnabled.setLayoutData(gridData3);*/
		
		createRadioButtonGrp();
		Label filler4 = new Label(cmpstCustInfo, SWT.NONE);
		buttonClose1 = new Button(cmpstCustInfo, SWT.NONE);
		buttonClose1.setText("CLOSE");
		buttonClose1.setLayoutData(gridData61);
		buttonSave = new Button(cmpstCustInfo, SWT.NONE);
		buttonSave.setText("SAVE");
		buttonSave.setLayoutData(gridData);
		
		
	}

	

	
	
	
	

	/**
	 * This method initializes cmpstCustomerCreation	
	 *
	 */
	private void createCmpstCustomerCreation() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		GridLayout gridLayout15 = new GridLayout();
		gridLayout15.numColumns = 3;
		gridLayout15.verticalSpacing = 0;
		gridLayout15.marginWidth = 0;
		gridLayout15.marginHeight = 0;
		gridLayout15.horizontalSpacing = 0;
		cmpstCustomerCreation = new Composite(getRootPanel(), SWT.NONE);
		cmpstCustomerCreation.setBackgroundMode(SWT.INHERIT_NONE);
		cmpstCustomerCreation.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstCustomerCreation");
		cmpstCustomerCreation.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createCmpstTopPanel();
		cmpstCustomerCreation.setLayout(gridLayout15);
		cmpstCustomerCreation.setLayoutData(gridData1);
		Label filler7 = new Label(cmpstCustomerCreation, SWT.NONE);
		Label filler11 = new Label(cmpstCustomerCreation, SWT.NONE);
		createCmpstBtnPnl();
		Label filler5 = new Label(cmpstCustomerCreation, SWT.NONE);
		Label filler9 = new Label(cmpstCustomerCreation, SWT.NONE);
		
		
	}

	/**
	 * This method initializes cmpstBtnPnl	
	 *
	 */
	private void createCmpstBtnPnl() {
		GridData gridData29 = new GridData();
		gridData29.horizontalAlignment = GridData.END;
		gridData29.grabExcessHorizontalSpace = false;
		gridData29.verticalAlignment = GridData.END;
		GridLayout gridLayout16 = new GridLayout();
		gridLayout16.numColumns = 2;
		gridLayout16.marginWidth = 10;
		gridLayout16.horizontalSpacing = 11;
		cmpstBtnPnl = new Composite(cmpstCustomerCreation, SWT.NONE);
		cmpstBtnPnl.setBackgroundMode(SWT.INHERIT_NONE);
		cmpstBtnPnl.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstBtnPnl");
		cmpstBtnPnl.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		cmpstBtnPnl.setLayout(gridLayout16);
		cmpstBtnPnl.setLayoutData(gridData29);
		
		
		
	}

	public KOHLSSIMPropertiesPageBehavior getPanelBehavior() {

		return myBehavior;
	}

	


	/**
	 * This method gets the behavior of the class
	 *
	 */
	public KOHLSSIMPropertiesPageBehavior getMyBheavior(){
		return myBehavior;
	}
	/**
	 * This method initializes comboDefaultPackPrinter	
	 *
	 */
	private void createComboDefaultPackPrinter() {
		GridData gridData8 = new GridData();
		gridData8.widthHint = 100;
		comboDefaultPackPrinter = new Combo(cmpstCustInfo, SWT.NONE);
		comboDefaultPackPrinter.setText("Select Printer");
		comboDefaultPackPrinter.setLayoutData(gridData8);
		for(int i=0; i<printServices.length;i++){
			String strPrintService = printServices[i].toString();
			int intPrinter = strPrintService.indexOf(":");
			comboDefaultPackPrinter.add(strPrintService.substring(intPrinter+2));
		}
				
		}
	
	
	//Start changes for SFS June Release
	private void createComboCollatePrintingEnabled() {
		String sArray[] = new String[] { "No", "Yes" };
		java.util.List<String> lList = Arrays.asList(sArray);

		GridData gridData15 = new GridData();
		gridData15.widthHint = 100;
		gridData15.grabExcessVerticalSpace = true;
		gridData15.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		comboCollatePrintingEnabled = new Combo(cmpstCustInfo, SWT.NONE);
		comboCollatePrintingEnabled.setText("No");
		comboCollatePrintingEnabled.setLayoutData(gridData15);
		for (int i = 0; i < lList.size(); i++) {
			String collatevalue= (lList.get(i));
			comboCollatePrintingEnabled.add(collatevalue);
		}
	}
	//End changes for SFS June Release
	
	
	
	
	
	/**
	 * This method initializes comboDefaultLabelPrinter	
	 *
	 */
	private void createComboDefaultLabelPrinter() {
		GridData gridData9 = new GridData();
		gridData9.widthHint = 100;
		comboDefaultLabelPrinter = new Combo(cmpstCustInfo, SWT.NONE);
		comboDefaultLabelPrinter.setText("Select Printer");

		comboDefaultLabelPrinter.setLayoutData(gridData9);
		for(int i=0; i<printServices.length;i++){
			String strPrintService = printServices[i].toString();
			int intPrinter = strPrintService.indexOf(":");
			comboDefaultLabelPrinter.add(strPrintService.substring(intPrinter+2));
		}
	}
	/**
	 * This method initializes comboDefaultPackPrinter1	
	 *
	 */
	private void createComboDefaultPackPrinter1() {
		GridData gridData4 = new GridData();
		gridData4.widthHint = 100;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		comboDefaultPackPrinter1 = new Combo(cmpstCustInfo, SWT.NONE);
		comboDefaultPackPrinter1.setText("Select Printer");
		comboDefaultPackPrinter1.setLayoutData(gridData4);
		
		for(int i=0; i<printServices.length;i++){
			String strPrintService = printServices[i].toString();
			int intPrinter = strPrintService.indexOf(":");
			comboDefaultPackPrinter1.add(strPrintService.substring(intPrinter+2));
		}
	}
	
	public KOHLSSIMPropertiesPageBehavior getBehavior() {
        return myBehavior;
    }

	/**
	 * This method initializes RadioButtonGrp	
	 *
	 */
	private void createRadioButtonGrp() {
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		RadioButtonGrp = new Group(cmpstCustInfo, SWT.NONE);
		RadioButtonGrp.setText("Calculate Weight");
		RadioButtonGrp.setLayout(gridLayout2);
		radioButtonCalcualteWeight = new Button(RadioButtonGrp, SWT.RADIO);
		radioButtonCalcualteWeight.setText("Calculate System Weight for Container");
		radioButtonUseScale = new Button(RadioButtonGrp, SWT.RADIO);
		radioButtonUseScale.setText("Use Scale for Container Weight");
		YRCButtonBindingData radiobutton1bd = new YRCButtonBindingData();
		radiobutton1bd.setName("radioButtonCalcualteWeight");
		radioButtonCalcualteWeight.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,radiobutton1bd);
		radiobutton1bd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@WeightCalculator");
		radioButtonUseScale.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				radiobutton1bd);
		YRCButtonBindingData radiobutton2bd = new YRCButtonBindingData();
		radiobutton2bd.setName("radioButtonUseScale");
		radiobutton2bd.setTargetBinding("SIMINIPropertiesInput:/SIMINIProperties/@WeightCalculator");
		radioButtonCalcualteWeight.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION,
				radiobutton1bd);
		
		
	}
}
