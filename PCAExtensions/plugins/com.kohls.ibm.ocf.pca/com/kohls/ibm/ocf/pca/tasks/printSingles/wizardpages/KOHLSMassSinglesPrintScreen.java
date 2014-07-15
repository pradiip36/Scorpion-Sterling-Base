
package com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.tasks.printSingles.actions.KOHLSMassSinglesCloseAction;
import com.kohls.ibm.ocf.pca.tasks.printSingles.actions.KOHLSMassSinglesPrintAction;
import com.kohls.ibm.ocf.pca.tasks.printSingles.actions.KOHLSMassSinglesRefreshAction;
import com.yantra.yfc.rcp.IYRCCellModifier;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.IYRCTableColorProvider;
import com.yantra.yfc.rcp.IYRCTableImageProvider;
import com.yantra.yfc.rcp.YRCBaseCellModifier;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCCellModifier2;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTableBindingData;
import com.yantra.yfc.rcp.YRCTblClmBindingData;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCWizardBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;
/**
 * @author Kiran
 * 
 * Created on Sept 12,2012
 * Modified on Sept 12,2012
 */
public class KOHLSMassSinglesPrintScreen extends Composite implements IYRCComposite{

	private KOHLSMassSinglesPrintScreenBehavior myBehavior;
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.printSingles.wizardpages.KOHLSMassSinglesPrintScreen";  //  @jve:decl-index=0:
	private YRCWizardBehavior wizBehavior;
	private KOHLSMassSinglesPrintScreenHelper helper;
	Composite pnlroot;
	Composite cmpst;
	Composite cmpstPrintOptions;
	Composite pnlTitle;
	Composite pnlBody;
	Composite cmpstSearchOption;
	Composite KOHLSMassSinglesPrintScreencstm;
	Composite cmpstShipmentSearchResults;
	Composite pnlTitleSearchResults;
	Table tblItemShipment;
	Label lblTitle;
	Label lblSearchResults;
	Button bttnPrint;
	Button bttnClose;
	TableColumn tblClmnCheckBox;
	TableColumn tblclmSKU;
	TableColumn tblclmShipmentType;
	TableColumn tblclmAvailableQuantity;
	TableColumn tblclmPrintQuantity;
	TableColumn tblclmIsProductFamily;
	private Label lblSelectPrinter;
	private Combo cmbPrinter;
	private boolean flag;
	private Button bttnRefresh = null;
	
	public KOHLSMassSinglesPrintScreen(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public KOHLSMassSinglesPrintScreen(Composite parent, int style, Object input) {
		super(parent, style);
		initialize();
		setBindingForComponents();
		helper=new KOHLSMassSinglesPrintScreenHelper(this,FORM_ID,input);
		myBehavior = new KOHLSMassSinglesPrintScreenBehavior(this, FORM_ID, input, helper);
		helper.setBehavior(myBehavior);
	}
	
	private void initialize(){
		this.setData(YRCConstants.YRC_CONTROL_NAME, "this");
			GridLayout thislayout = new GridLayout(1, false);
		thislayout.marginHeight = 0;
		thislayout.marginWidth = 0;
		this.setLayout(thislayout);
		createPnlroot();

	}

	private void createPnlroot(){
	
		pnlroot = new Composite(this, SWT.NONE);
		pnlroot.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlroot.setData(YRCConstants.YRC_CONTROL_NAME, "pnlroot");
		
		GridData pnlrootlayoutData = new GridData();
		pnlrootlayoutData.horizontalAlignment = 4;
		pnlrootlayoutData.verticalAlignment = 4;
		pnlrootlayoutData.grabExcessHorizontalSpace = true;
		pnlrootlayoutData.grabExcessVerticalSpace = true;
		pnlroot.setLayoutData(pnlrootlayoutData);
		pnlroot.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		
			GridLayout pnlrootlayout = new GridLayout(2, false);
		pnlroot.setLayout(pnlrootlayout);
		createCmpst();
	
		bttnPrint = new Button(pnlroot, SWT.PUSH);
		GridData bttnPrintlayoutData = new GridData();
		bttnPrintlayoutData.horizontalAlignment = 16777224;
		bttnPrintlayoutData.horizontalIndent = 3;
		bttnPrintlayoutData.verticalAlignment = 16777216;
		bttnPrintlayoutData.grabExcessHorizontalSpace = true;
		bttnPrintlayoutData.verticalIndent = 3;
		bttnPrintlayoutData.widthHint = 120;
	
		bttnPrint.setLayoutData(bttnPrintlayoutData);
		bttnPrint.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Button");
		bttnPrint.setText(YRCPlatformUI.getString("Print_Btn"));
		bttnClose = new Button(pnlroot, SWT.PUSH);
		GridData bttnCloselayoutData = new GridData();
		bttnCloselayoutData.horizontalAlignment = 2;
		bttnCloselayoutData.horizontalIndent = 3;
		bttnCloselayoutData.verticalIndent = 3;
		bttnCloselayoutData.widthHint = 80;
	
		bttnClose.setLayoutData(bttnCloselayoutData);
		bttnClose.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Button");
		bttnClose.setText(YRCPlatformUI.getString("Close_Btn"));
	}

	private void createCmpst(){
	
		cmpst = new Composite(pnlroot, SWT.NONE);
		cmpst.setBackgroundMode(SWT.INHERIT_NONE);
	
		cmpst.setData(YRCConstants.YRC_CONTROL_NAME, "cmpst");
		
		GridData cmpstlayoutData = new GridData();
		cmpstlayoutData.horizontalAlignment = 4;
		cmpstlayoutData.verticalAlignment = 4;
		cmpstlayoutData.grabExcessHorizontalSpace = true;
		cmpstlayoutData.grabExcessVerticalSpace = true;
		cmpstlayoutData.horizontalSpan = 2;
		cmpst.setLayoutData(cmpstlayoutData);
		cmpst.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Composite");
		
		GridLayout cmpstlayout = new GridLayout(1, false);
		cmpstlayout.horizontalSpacing = 0;
		cmpstlayout.verticalSpacing = 4;
		cmpstlayout.marginHeight = 0;
		cmpstlayout.marginWidth = 0;
		cmpst.setLayout(cmpstlayout);
		createCmpstPrintOptions();
		createCmpstSearchOption();

	}

	private void createCmpstSearchOption(){
	
		cmpstSearchOption = new Composite(cmpst, SWT.BORDER);
		cmpstSearchOption.setBackgroundMode(SWT.INHERIT_NONE);
	
		cmpstSearchOption.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstSearchOption");
		
		GridData cmpstSearchOptionlayoutData = new GridData();
		cmpstSearchOptionlayoutData.horizontalAlignment = 4;
		cmpstSearchOptionlayoutData.verticalAlignment = 4;
		cmpstSearchOptionlayoutData.grabExcessHorizontalSpace = true;
		cmpstSearchOptionlayoutData.grabExcessVerticalSpace = true;
		cmpstSearchOption.setLayoutData(cmpstSearchOptionlayoutData);
		cmpstSearchOption.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		
		GridLayout cmpstSearchOptionlayout = new GridLayout(1, false);
		cmpstSearchOptionlayout.horizontalSpacing = 2;
		cmpstSearchOptionlayout.verticalSpacing = 4;
		cmpstSearchOptionlayout.marginHeight = 0;
		cmpstSearchOptionlayout.marginWidth = 0;
		cmpstSearchOption.setLayout(cmpstSearchOptionlayout);
		createCmpstShipmentSearchResults();

	}
	
	
	private void createCmpstShipmentSearchResults(){
	
		cmpstShipmentSearchResults = new Composite(cmpstSearchOption, SWT.NONE);
		cmpstShipmentSearchResults.setBackgroundMode(SWT.INHERIT_NONE);
	
		cmpstShipmentSearchResults.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstShipmentSearchResults");
		
		GridData cmpstShipmentSearchResultslayoutData = new GridData();
		cmpstShipmentSearchResultslayoutData.horizontalAlignment = 4;
		cmpstShipmentSearchResultslayoutData.verticalAlignment = 4;
		cmpstShipmentSearchResultslayoutData.grabExcessHorizontalSpace = true;
		cmpstShipmentSearchResultslayoutData.grabExcessVerticalSpace = true;
		cmpstShipmentSearchResults.setLayoutData(cmpstShipmentSearchResultslayoutData);
		cmpstShipmentSearchResults.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		
			GridLayout cmpstShipmentSearchResultslayout = new GridLayout(1, false);
		cmpstShipmentSearchResultslayout.horizontalSpacing = 0;
		cmpstShipmentSearchResultslayout.verticalSpacing = 0;
		cmpstShipmentSearchResultslayout.marginHeight = 0;
		cmpstShipmentSearchResultslayout.marginWidth = 0;
		cmpstShipmentSearchResults.setLayout(cmpstShipmentSearchResultslayout);
		createPnlTitleSearchResults();
		createItemShipment();
		

	}

	
	private void createItemShipment(){
	
		tblItemShipment = new Table(cmpstShipmentSearchResults, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tblItemShipment.setHeaderVisible(true);
		tblItemShipment.setLinesVisible(true);
		tblItemShipment.setData(YRCConstants.YRC_CONTROL_NAME, "itemShipment");
		tblItemShipment.setEnabled(true);
		
		GridData itemShipmentlayoutData = new GridData();
		itemShipmentlayoutData.horizontalAlignment = 4;
		itemShipmentlayoutData.verticalAlignment = 4;
		itemShipmentlayoutData.grabExcessHorizontalSpace = true;
		itemShipmentlayoutData.grabExcessVerticalSpace = true;
		tblItemShipment.setLayoutData(itemShipmentlayoutData);
		tblItemShipment.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Table");
		
		
		tblClmnCheckBox = new TableColumn(tblItemShipment, SWT.CENTER);
		tblClmnCheckBox.setWidth(35);
		tblClmnCheckBox.setResizable(false);
		tblClmnCheckBox.setMoveable(false);
		
		tblclmSKU = new TableColumn(tblItemShipment, SWT.CENTER);
		tblclmSKU.setWidth(170);
		tblclmSKU.setResizable(true);
		tblclmSKU.setMoveable(false);
		tblclmSKU.setText(YRCPlatformUI.getString("SKU"));
		
		
		tblclmShipmentType = new TableColumn(tblItemShipment, SWT.CENTER);
		tblclmShipmentType.setWidth(148);
		tblclmShipmentType.setResizable(true);
		tblclmShipmentType.setMoveable(false);
		tblclmShipmentType.setText(YRCPlatformUI.getString("Shipment_Type"));
		
		tblclmAvailableQuantity = new TableColumn(tblItemShipment, SWT.CENTER);
		tblclmAvailableQuantity.setWidth(120);
		tblclmAvailableQuantity.setResizable(true);
		tblclmAvailableQuantity.setMoveable(false);
		tblclmAvailableQuantity.setText(YRCPlatformUI.getString("Available_Quantity"));
		
		tblclmPrintQuantity = new TableColumn(tblItemShipment, SWT.CENTER);
		tblclmPrintQuantity.setWidth(130);
		tblclmPrintQuantity.setResizable(true);
		tblclmPrintQuantity.setMoveable(false);
		tblclmPrintQuantity.setText(YRCPlatformUI.getString("Print_Quantity"));
		
		
		tblclmIsProductFamily = new TableColumn(tblItemShipment, SWT.CENTER);
		tblclmIsProductFamily.setWidth(75);
		tblclmIsProductFamily.setResizable(true);
		tblclmIsProductFamily.setMoveable(false);
		tblclmIsProductFamily.setText(YRCPlatformUI.getString("Is_Product_Family"));

	}

	private void createPnlTitleSearchResults(){
	
		pnlTitleSearchResults = new Composite(cmpstShipmentSearchResults, SWT.NONE);
		pnlTitleSearchResults.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlTitleSearchResults.setData(YRCConstants.YRC_CONTROL_NAME, "pnlTitleSearchResults");
		
		GridData pnlTitleSearchResultslayoutData = new GridData();
		pnlTitleSearchResultslayoutData.horizontalAlignment = 4;
		pnlTitleSearchResultslayoutData.verticalAlignment = 4;
		pnlTitleSearchResultslayoutData.grabExcessHorizontalSpace = true;
		pnlTitleSearchResults.setLayoutData(pnlTitleSearchResultslayoutData);
		pnlTitleSearchResults.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		
		GridLayout pnlTitleSearchResultslayout = new GridLayout(4, false);
		pnlTitleSearchResultslayout.horizontalSpacing = 5;
		pnlTitleSearchResultslayout.verticalSpacing = 5;
		pnlTitleSearchResultslayout.marginHeight = 5;
		pnlTitleSearchResultslayout.numColumns = 1;
		pnlTitleSearchResultslayout.marginWidth = 5;
		pnlTitleSearchResults.setLayout(pnlTitleSearchResultslayout);
		
	
		lblSearchResults = new Label(pnlTitleSearchResults, SWT.LEFT);
		GridData lblSearchResultslayoutData = new GridData();
		lblSearchResultslayoutData.verticalAlignment = 16777216;
		lblSearchResultslayoutData.grabExcessHorizontalSpace = false;
	
		lblSearchResults.setLayoutData(lblSearchResultslayoutData);
		lblSearchResults.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "NoTheme");
		lblSearchResults.setText(YRCPlatformUI.getString("Awaiting_PrintList_Shipment_Search_Result_Panel_Header_Title"));
		
	}

	private void createCmpstPrintOptions(){
	
		cmpstPrintOptions = new Composite(cmpst, SWT.NONE);
		cmpstPrintOptions.setBackgroundMode(SWT.INHERIT_NONE);
	
		cmpstPrintOptions.setData(YRCConstants.YRC_CONTROL_NAME, "cmpstPrintOptions");
		
		GridData cmpstPrintOptionslayoutData = new GridData();
		cmpstPrintOptionslayoutData.horizontalAlignment = 4;
		cmpstPrintOptionslayoutData.verticalAlignment = 4;
		cmpstPrintOptionslayoutData.grabExcessHorizontalSpace = true;
		cmpstPrintOptions.setLayoutData(cmpstPrintOptionslayoutData);
		cmpstPrintOptions.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Composite");
		
			GridLayout cmpstPrintOptionslayout = new GridLayout(1, false);
		cmpstPrintOptionslayout.marginHeight = 0;
		cmpstPrintOptionslayout.marginWidth = 0;
		cmpstPrintOptions.setLayout(cmpstPrintOptionslayout);
	
		createPnlTitle();
		createPnlBody();

	}

	private void createPnlBody(){
	
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = true;
		gridData.verticalAlignment = GridData.CENTER;
		pnlBody = new Composite(cmpstPrintOptions, SWT.NONE);
		pnlBody.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlBody.setData(YRCConstants.YRC_CONTROL_NAME, "pnlBody");
		
		GridData pnlBodylayoutData = new GridData();
		pnlBodylayoutData.horizontalAlignment = 4;
		pnlBodylayoutData.verticalAlignment = 4;
		pnlBodylayoutData.grabExcessHorizontalSpace = true;
		pnlBodylayoutData.grabExcessVerticalSpace = true;
		pnlBody.setLayoutData(pnlBodylayoutData);
			GridLayout pnlBodylayout = new GridLayout(2, false);
		pnlBodylayout.marginHeight = 8;
		pnlBodylayout.numColumns = 3;
		pnlBodylayout.verticalSpacing = 5;
		pnlBodylayout.marginWidth = 5;
		pnlBody.setLayout(pnlBodylayout);
		lblSelectPrinter = new Label(pnlBody, SWT.NONE);
		lblSelectPrinter.setText(YRCPlatformUI.getString("Select_Printer"));
		createCmbPrinter();
		bttnRefresh = new Button(pnlBody, SWT.NONE);
		bttnRefresh.setText(YRCPlatformUI.getString("Refresh"));
		bttnRefresh.setLayoutData(gridData);
	
	}
	
	/**
	 * This method initializes cmbPrinter	
	 *
	 */
	private void createCmbPrinter() {
		cmbPrinter = new Combo(pnlBody, SWT.READ_ONLY);
	}   

	private void createPnlTitle(){
	
		pnlTitle = new Composite(cmpstPrintOptions, SWT.NONE);
		pnlTitle.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlTitle.setData(YRCConstants.YRC_CONTROL_NAME, "pnlTitle");
		
		GridData pnlTitlelayoutData = new GridData();
		pnlTitlelayoutData.horizontalAlignment = 4;
		pnlTitlelayoutData.verticalAlignment = 16777216;
		pnlTitlelayoutData.grabExcessHorizontalSpace = true;
		pnlTitle.setLayoutData(pnlTitlelayoutData);
		pnlTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		
			GridLayout pnlTitlelayout = new GridLayout(1, false);
		pnlTitlelayout.horizontalSpacing = 5;
		pnlTitlelayout.verticalSpacing = 5;
		pnlTitlelayout.marginHeight = 5;
		pnlTitlelayout.marginWidth = 5;
		pnlTitle.setLayout(pnlTitlelayout);
		
	
		lblTitle = new Label(pnlTitle, SWT.LEFT);
		GridData lblTitlelayoutData = new GridData();
		lblTitlelayoutData.horizontalAlignment = 16777216;
		lblTitlelayoutData.verticalAlignment = 16777216;
	
		lblTitle.setLayoutData(lblTitlelayoutData);
		lblTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "NoTheme");
		lblTitle.setText(YRCPlatformUI.getString("Print_Mass_Singles"));
	}

	public void setBindingForComponents() {
		
         //binding for Single Printer Combo
		
	 	YRCComboBindingData bdCmbMPrinterID = new YRCComboBindingData();
	 	bdCmbMPrinterID.setName("cmbPrinter");
	 	bdCmbMPrinterID.setSourceBinding("getDeviceList:Devices/Device/@DeviceId");
	 	bdCmbMPrinterID.setCodeBinding("DeviceId");
	 	bdCmbMPrinterID.setTargetBinding("PrinterID:Printer/@PrinterID");
	 	bdCmbMPrinterID.setListBinding("getDeviceList:Devices/Device");
	 	bdCmbMPrinterID.setDescriptionBinding("@DeviceId");
	 	bdCmbMPrinterID.setMandatory(true);
	 	cmbPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbMPrinterID);
	 	cmbPrinter.setData(YRCConstants.YRC_CONTROL_NAME,"cmbPrinter");
	 	cmbPrinter.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
		        myBehavior.handleComboBoxSelection(cmbPrinter);
		    }
			public void widgetDefaultSelected(SelectionEvent e) {
		     
		    }
		});
	 	
		//binding for Print Button
		
		YRCButtonBindingData bttnPrintbd = new YRCButtonBindingData();
		bttnPrintbd.setName("bttnPrint");
		bttnPrintbd.setActionHandlerEnabled(true);
		bttnPrintbd.setActionId(KOHLSMassSinglesPrintAction.ACTION_ID); 
		bttnPrint.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, bttnPrintbd);
		bttnPrint.setData(YRCConstants.YRC_CONTROL_NAME,"bttnPrint");
		
		//binding for Close Button

		YRCButtonBindingData bttnClosebd = new YRCButtonBindingData();
		bttnClosebd.setName("bttnClose");
		bttnClosebd.setActionHandlerEnabled(true);
		bttnClosebd.setActionId(KOHLSMassSinglesCloseAction.ACTION_ID); 
		bttnClose.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, bttnClosebd);
		bttnClose.setData(YRCConstants.YRC_CONTROL_NAME,"bttnClose");
		
//		binding for Refresh Button

		YRCButtonBindingData bttnRefreshbd = new YRCButtonBindingData();
		bttnRefreshbd.setName("bttnRefresh");
		bttnRefreshbd.setActionHandlerEnabled(true);
		bttnRefreshbd.setActionId(KOHLSMassSinglesRefreshAction.ACTION_ID); 
		bttnRefresh.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, bttnRefreshbd);
		bttnRefresh.setData(YRCConstants.YRC_CONTROL_NAME,"bttnRefresh");
		
		//binding for table columns
		
		YRCTblClmBindingData itemShipmentClmBd[] = new YRCTblClmBindingData[tblItemShipment.getColumnCount()];
		String[] itemShipmentEditors = new String[tblItemShipment.getColumnCount()];
		int itemShipmentCounter=0;
		
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblClmnCheckBox");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@Checked");		
		itemShipmentClmBd[itemShipmentCounter].setCheckedBinding("Y");
		itemShipmentClmBd[itemShipmentCounter].setUnCheckedBinding("N");
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setSourceBinding("@Checked");
		itemShipmentClmBd[itemShipmentCounter].setTargetAttributeBinding("@Checked");
		itemShipmentClmBd[itemShipmentCounter].setCellEditorTheme("CheckBoxTheme");
	
        itemShipmentEditors[itemShipmentCounter] = YRCConstants.YRC_CHECK_BOX_CELL_EDITOR;
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblclmSKU");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@ItemID");		
		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("SKU"));
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblclmShipmentType");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@ShipmentType");
		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("Shipment_Type"));
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblclmAvailableQuantity");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@AvailableQuantity");
		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("Available_Quantity"));
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblclmPrintQuantity");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@TotalShipments");
		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("Print_Quantity"));
		itemShipmentClmBd[itemShipmentCounter].setTargetAttributeBinding("@TotalShipments");
		itemShipmentClmBd[itemShipmentCounter].setDataType("PositiveQuantity");
		itemShipmentEditors[itemShipmentCounter] = YRCConstants.YRC_TEXT_BOX_CELL_EDITOR;
		
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("tblclmIsProductFamily");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@isProductFamilyShipment");		
		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("Is_Product_Family"));
		
		
		
		YRCTableBindingData tblItemShipmentbd = new YRCTableBindingData();
		tblItemShipmentbd.setName("itemShipment");
		tblItemShipmentbd.setSourceBinding("PrintPackShipments:/PrintPackShipments/PrintPackShipment");
		tblItemShipmentbd.setTblClmBindings(itemShipmentClmBd);
		tblItemShipmentbd.setKeyNavigationRequired(true);
		tblItemShipment.setData(YRCConstants.YRC_TABLE_BINDING_DEFINATION, tblItemShipmentbd);
		tblItemShipment.setData(YRCConstants.YRC_CONTROL_NAME,"itemShipment");
		
		tblItemShipmentbd.setColorProvider(new IYRCTableColorProvider() {
			public String getColorTheme(Object element, int columnIndex) {
				if (columnIndex == 0 || columnIndex ==4 ) {
					return "EditableColumn";
				}

				return null;
			}
		});
		
		tblItemShipmentbd.setImageProvider(new IYRCTableImageProvider() {
			public String getImageThemeForColumn(Object element, int columnIndex) {
				YRCTableBindingData tblBind = (YRCTableBindingData) tblItemShipment.getData(YRCConstants.YRC_TABLE_BINDING_DEFINATION);
				YRCTblClmBindingData tblClmBind = tblBind.getTblClmBindings()[columnIndex];
				String ctrlName = tblClmBind.getName();
				Element orderLineElement = (Element)element;

				if ("tblClmnCheckBox".equals(ctrlName)) {
					String checked = orderLineElement.getAttribute("Checked");
					if("Y".equals(checked))
						return "TableCheckboxCheckedImageLarge";
					else 
						return "TableCheckboxUnCheckedImageLarge";
				}
			
				return null;
			}
		});

		tblItemShipmentbd.setCellModifier(new IYRCCellModifier(){
			
			
			@Override
			protected void handleCheckBoxSelection(boolean flag, String value, Element element) {
				
				String strChk = element.getAttribute("Checked");
				if ("Y".equalsIgnoreCase(strChk)) {
					moveToNextEditableCell();
				}
				
				super.handleCheckBoxSelection(flag, value, element);
			}

			
			@Override
			protected int allowModifiedValue(String arg0, String arg1, Element arg2) {
				// TODO Auto-generated method stub
				return 0;
			}


			public Object getValue(Object object, String property) {
				return super.getValue(object, property);
			}

			protected boolean allowModify(String property, String value, Element element) {
				if (property.equals("@Checked")) {
					return true;
				}else if (property.equals("@TotalShipments")) {
					String strChk = element.getAttribute("Checked");
					if ("Y".equalsIgnoreCase(strChk)) {
						return true;
					}
				}
				return false; 
			}

			protected String getModifiedValue(String property, String value, Element element) {
				if (property.equals("@Checked")){
					if(value.equals("true")){
						element.setAttribute("TotalShipments", element.getAttribute("TotalShipments"));
					}
					else {
						element.setAttribute("TotalShipments", "");
					}
				}
				return value;
			}
			
			
		
		
		});

		
		tblItemShipmentbd.setCellTypes(itemShipmentEditors);
		tblItemShipmentbd.setCellModifierRequired(true);
		tblItemShipmentbd.setKeyNavigationRequired(true);
		tblItemShipmentbd.setSortRequired(true);
		tblItemShipmentbd.setFilterReqd(true);
		
		//binding for Label Header
		
	
		YRCLabelBindingData lblSearchResultsbd = new YRCLabelBindingData();
		lblSearchResultsbd.setName("lblSearchResults"); 
		lblSearchResults.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblSearchResultsbd);


		//binding for screen title
		YRCLabelBindingData lblTitlebd = new YRCLabelBindingData();
		lblTitlebd.setName("lblTitle"); 
		lblTitle.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblTitlebd);

	}
	
	

	
	void enableField(String ctrlName, boolean enable) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(null != ctrl)
			ctrl.setEnabled(enable);
	}

	void excludeField(String ctrlName, boolean exclude) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(ctrl != null) {
			if (exclude || (!exclude && !myBehavior.isExtendedControlHidden(ctrlName))){
				Object obj = ctrl.getLayoutData();
				if(obj == null) {
					obj = new GridData();
					ctrl.setLayoutData(obj);
				} 
				ctrl.setVisible(!exclude);
				if(obj instanceof GridData){
					((GridData)obj).exclude = exclude;
				}else if(obj instanceof RowData){
					((RowData)obj).exclude = exclude;
				}
			}
		} 
	}

	void hideField(String ctrlName, boolean visible) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(null != ctrl)
			ctrl.setVisible(visible);
	}

	boolean setEditable(String ctrlName, boolean enable) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(null != ctrl){
			try {
				Method method = ctrl.getClass().getMethod("setEditable", new Class[] {boolean.class});
				if(null != method){
					method.invoke(ctrl, new Object[] {new Boolean(enable)});
					return true;
				}
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return false;
	}

	boolean setText(String ctrlName, String text) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(null != ctrl){
			try {
				Method method = ctrl.getClass().getMethod("setText", new Class[] {String.class});
				if(null != method){
					method.invoke(ctrl, new Object[] {new String(text)});
					return true;
				}
			} catch (SecurityException e) {
			} catch (IllegalArgumentException e) {
			} catch (NoSuchMethodException e) {
			} catch (IllegalAccessException e) {
			} catch (InvocationTargetException e) {
			}
		}
		return false;
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
	public KOHLSMassSinglesPrintScreenBehavior getBehavior() {
        return myBehavior;
    }
	
	public KOHLSMassSinglesPrintScreenHelper getHelper() {
        return helper;
    }
	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }
    
   
    
    
   
    

	   
}