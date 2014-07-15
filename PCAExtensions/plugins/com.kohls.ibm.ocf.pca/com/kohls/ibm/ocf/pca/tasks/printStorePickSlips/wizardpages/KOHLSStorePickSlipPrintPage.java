package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages;

import java.util.Iterator;

import javax.print.PrintService;

import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCCellModifier;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.IYRCTableColorProvider;
import com.yantra.yfc.rcp.IYRCTableImageProvider;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTableBindingData;
import com.yantra.yfc.rcp.YRCTblClmBindingData;
import com.yantra.yfc.rcp.YRCWizardBehavior;
import com.yantra.yfc.rcp.YRCXmlUtils;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.graphics.Color;
import org.w3c.dom.Element;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions.KOHLSPrintStorePickSlipAction;
import com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizards.KohlsStorePickSlipPrintWizardBehavior;

public class KOHLSStorePickSlipPrintPage extends Composite implements
		IYRCComposite {
	
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages.KOHLSStorePickSlipPrintPage";
	private KOHLSStorePickSlipPrintPageBehavior myBehavior = null;
	private YRCWizardBehavior wizBehavior;  //  @jve:decl-index=0:
	private Composite pnlRoot = null;
	private Composite pnlHeader = null;
	private Composite pnlCenter = null;
	private Composite pnlFooter = null;
	private Label lblSelectPrinter = null;
	private Combo cmbPrinterList = null;
	private Table tblShipmentList = null;
	private Button btnPrintPickSlip = null;
	private Composite pnlResultDetails = null;
	private Label lblSearchResultDescription = null;
	private Button btnClose = null;
	private Button btnBulkPrint = null;
	private Button btnRefresh = null;
	public KOHLSStorePickSlipPrintPage(Composite parent, int style, Object input) {
		super(parent, style);
		initialize();
		setBindingForComponents();
		myBehavior = new KOHLSStorePickSlipPrintPageBehavior(this, FORM_ID, input);		
		setShipmentListModel();
		//myBehavior.setPrinterList();
	}
	
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		createPnlRoot();
        this.setLayoutData(new GridLayout());
        this.setLayout(new GridLayout());
        this.setBackground(new Color(Display.getCurrent(), 244, 246, 248));
        this.setLocation(new Point(0, 0));
        this.setVisible(true);
        this.setEnabled(true);
        this.setSize(new Point(1028, 505));
			
	}

	public String getFormId() {
		// TODO Auto-generated method stub
		return this.FORM_ID;
	}

	public String getHelpId() {
		// TODO Auto-generated method stub
		return null;
	}

	public IYRCPanelHolder getPanelHolder() {
		// TODO Auto-generated method stub
		return null;
	}

	public Composite getRootPanel() {
		// TODO Auto-generated method stub
		return pnlRoot;
	}

	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }
    
    public KOHLSStorePickSlipPrintPageBehavior getBehavior() {
        return myBehavior;
    }

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		GridData gridData11 = new GridData();
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.verticalAlignment = GridData.FILL;
		gridData11.grabExcessVerticalSpace = true;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.makeColumnsEqualWidth = true;
		gridLayout1.numColumns = 1;
		pnlRoot = new Composite(this, SWT.NONE);
		pnlRoot.setBackground(new Color(Display.getCurrent(), 128, 128, 128));
		//pnlRoot.setBackgroundMode(SWT.INHERIT_NONE);
		//pnlRoot.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createPnlHeader();
		pnlRoot.setLayout(gridLayout1);
		createPnlCenter();
		pnlRoot.setLayoutData(gridData11);
		createPnlFooter();
	}

	/**
	 * This method initializes pnlHeader	
	 *
	 */
	private void createPnlHeader() {
		GridData gridData6 = new GridData();
		gridData6.verticalAlignment = GridData.CENTER;
		gridData6.grabExcessHorizontalSpace = false;
		gridData6.horizontalAlignment = GridData.BEGINNING;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 4;
		gridLayout2.makeColumnsEqualWidth = false;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.verticalSpan = 15;
		pnlHeader = new Composite(pnlRoot, SWT.BORDER);
		pnlHeader.setLayoutData(gridData1);
		pnlHeader.setLayout(gridLayout2);
		//pnlHeader.setBackgroundMode(SWT.INHERIT_NONE);
		//pnlHeader.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		lblSelectPrinter = new Label(pnlHeader, SWT.NONE);
		lblSelectPrinter.setText("Select Printer");
		lblSelectPrinter.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		//pnlRoot.setBackgroundMode(SWT.INHERIT_NONE);
		pnlRoot.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		lblSelectPrinter.setLayoutData(gridData6);
		createCmbPrinterList();
	}

	/**
	 * This method initializes pnlCenter	
	 *
	 */
	private void createPnlCenter() {
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.FILL;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = GridData.FILL;
		GridData gridData2 = new GridData();
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = true;
		gridData2.verticalSpan = 70;
		gridData2.horizontalAlignment = GridData.FILL;
		pnlCenter = new Composite(pnlRoot, SWT.BORDER);
		pnlCenter.setLayout(new GridLayout());
		pnlCenter.setBackground(new Color(Display.getCurrent(), 192, 192, 192));
		createPnlResultDetails();
		pnlCenter.setLayoutData(gridData2);
		
		tblShipmentList = new Table(pnlCenter, SWT.BORDER);
		
		//tblShipmentList = new Table(pnlCenter, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tblShipmentList.setHeaderVisible(true);
		tblShipmentList.setLayoutData(gridData4);
		tblShipmentList.setLinesVisible(true);
		//tblShipmentList.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Table");
		
		TableColumn clmSelectToPrint = new TableColumn(tblShipmentList, SWT.NONE);
		clmSelectToPrint.setWidth(40);
		clmSelectToPrint.setMoveable(false);
		clmSelectToPrint.setResizable(false);
		clmSelectToPrint.setText(YRCPlatformUI.getString("clm_Select_To_Print"));
		
		//Start OASIS 13-NOV-2013 for PMR 85613,379,000
		
		TableColumn clmCustOrderDate = new TableColumn(tblShipmentList, SWT.NONE);
		clmCustOrderDate.setWidth(60);
		clmCustOrderDate.setText(YRCPlatformUI.getString("clm_CustOrder_Date"));
		
		//End OASIS 13-NOV-2013 for PMR 85613,379,000
		
		TableColumn clmShipmentDate = new TableColumn(tblShipmentList, SWT.NONE);
		clmShipmentDate.setWidth(60);
		clmShipmentDate.setText(YRCPlatformUI.getString("clm_Shipment_Date"));
		
		
		
		TableColumn clmShipmentType = new TableColumn(tblShipmentList, SWT.NONE);
		clmShipmentType.setWidth(60);
		clmShipmentType.setText(YRCPlatformUI.getString("clm_Shipment_Type"));
		
		TableColumn clmShipmentQty = new TableColumn(tblShipmentList, SWT.NONE);
		clmShipmentQty.setWidth(60);
		clmShipmentQty.setText(YRCPlatformUI.getString("clm_Shipment_Quantity"));
		
		TableColumn clmDepartment = new TableColumn(tblShipmentList, SWT.NONE);
		clmDepartment.setWidth(60);
		clmDepartment.setText(YRCPlatformUI.getString("clm_Department"));
	}

	/**
	 * This method initializes pnlFooter	
	 *
	 */
	private void createPnlFooter() {
		GridData gridData21 = new GridData();
		gridData21.horizontalAlignment = GridData.END;
		gridData21.grabExcessHorizontalSpace = true;
		gridData21.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		gridData12.grabExcessHorizontalSpace = false;
		gridData12.verticalAlignment = GridData.CENTER;
		gridData12.horizontalAlignment = GridData.END;
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = false;
		gridData.horizontalAlignment = GridData.END;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.horizontalSpacing = 10;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.END;
		gridData5.grabExcessHorizontalSpace = false;
		gridData5.verticalAlignment = GridData.CENTER;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessVerticalSpace = false;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalSpan = 10;
		gridData3.verticalAlignment = GridData.FILL;
		pnlFooter = new Composite(pnlRoot, SWT.BORDER);
		pnlFooter.setLayoutData(gridData3);
		pnlFooter.setLayout(gridLayout);
		pnlFooter.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		btnRefresh = new Button(pnlFooter, SWT.NONE);
		btnRefresh.setLayoutData(gridData21);
		btnRefresh.setText(YRCPlatformUI.getString("btn_Refresh_Print_Pick_Slip"));
		btnBulkPrint = new Button(pnlFooter, SWT.NONE);
		btnBulkPrint.setText(YRCPlatformUI.getString("btn_Bulk_Print_Pick_Slip"));
		
		btnBulkPrint.setLayoutData(gridData12);
		
		btnPrintPickSlip = new Button(pnlFooter, SWT.NONE);
		btnPrintPickSlip.setText(YRCPlatformUI.getString("btn_Print_Pick_Slip"));
		btnPrintPickSlip.setLayoutData(gridData5);
		
		btnClose = new Button(pnlFooter, SWT.NONE);
		btnClose.setLayoutData(gridData);
		btnClose.setText(YRCPlatformUI.getString("btn_Close_Pick_Slip"));
		
		btnPrintPickSlip.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				YRCPlatformUI.fireAction("com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions.KOHLSPrintStorePickSlipAction");
			}
		});
		
		btnClose.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				YRCPlatformUI.fireAction("com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions.KOHLSPrintStorePickSlipCloseAction");
			}
		});
		
		btnBulkPrint.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				myBehavior.launchBulkPrintPopUp();
			}
		});
		
		btnRefresh.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				YRCPlatformUI.fireAction("com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.actions.KOHLSPrintStorePickSlipRefreshAction");
			}
		});
	}

	/**
	 * This method initializes cmbPrinterList	
	 *
	 */
	private void createCmbPrinterList() {
		GridData gridData7 = new GridData();
		gridData7.verticalAlignment = GridData.CENTER;
		gridData7.horizontalSpan = 3;
		gridData7.grabExcessHorizontalSpace = false;
		gridData7.horizontalAlignment = GridData.BEGINNING;
		cmbPrinterList = new Combo(pnlHeader, SWT.READ_ONLY);
		cmbPrinterList.setLayoutData(gridData7);
		cmbPrinterList.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
		        myBehavior.handleComboBoxSelection(cmbPrinterList);
		    }
			public void widgetDefaultSelected(SelectionEvent e) {
		     
		    }
		});
	}

	/**
	 * This method initializes pnlResultDetails	
	 *
	 */
	private void createPnlResultDetails() {
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 1;
		gridLayout3.makeColumnsEqualWidth = false;
		GridData gridData9 = new GridData();
		gridData9.horizontalAlignment = GridData.BEGINNING;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.verticalAlignment = GridData.CENTER;
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessVerticalSpace = false;
		gridData8.grabExcessHorizontalSpace = true;
		gridData8.verticalSpan = 5;
		gridData8.verticalAlignment = GridData.FILL;
		pnlResultDetails = new Composite(pnlCenter, SWT.NONE);
		pnlResultDetails.setLayoutData(gridData8);
		pnlResultDetails.setLayout(gridLayout3);
		pnlResultDetails.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		lblSearchResultDescription = new Label(pnlResultDetails, SWT.NONE);
		lblSearchResultDescription.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		lblSearchResultDescription.setText(YRCPlatformUI.getString("Shipments_waiting_Pick_List_Print_Search_Description"));
		lblSearchResultDescription.setLayoutData(gridData9);
	}
	
	public void setBindingForComponents(){
		
		//Binding for the Printer Listing
		YRCComboBindingData bdCmbMPrinterID = new YRCComboBindingData();
	 	bdCmbMPrinterID.setName("cmbPrinter");
	 	bdCmbMPrinterID.setSourceBinding("getDeviceList:Devices/Device/@DeviceId");
	 	bdCmbMPrinterID.setCodeBinding("DeviceId");
	 	bdCmbMPrinterID.setTargetBinding("PrinterID:Printer/@PrinterID");
	 	bdCmbMPrinterID.setListBinding("getDeviceList:Devices/Device");
	 	bdCmbMPrinterID.setDescriptionBinding("@DeviceId");
	 	bdCmbMPrinterID.setMandatory(true);
	 	cmbPrinterList.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbMPrinterID);
	 	cmbPrinterList.setData(YRCConstants.YRC_CONTROL_NAME,"cmbPrinter");
		
	 	//binding for Print Button		
		
		YRCButtonBindingData bttnPrintbd = new YRCButtonBindingData();
		bttnPrintbd.setName("bttnPrint");
		bttnPrintbd.setActionHandlerEnabled(true);
		//bttnPrintbd.setActionId(KOHLSPrintStorePickSlipAction.ACTION_ID); 
		btnPrintPickSlip.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, bttnPrintbd);
		btnPrintPickSlip.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrintPickSlip");
		
		YRCTblClmBindingData tblShipmentListClmBd[] = new YRCTblClmBindingData[tblShipmentList.getColumnCount()];
		String[] tblShipmentListClm = new String[tblShipmentList.getColumnCount()];
		int tblShipListClmCounter=0;
		
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmSelectToPrint");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@Checked");		
		tblShipmentListClmBd[tblShipListClmCounter].setCheckedBinding("Y");
		tblShipmentListClmBd[tblShipListClmCounter].setUnCheckedBinding("N");
		tblShipmentListClmBd[tblShipListClmCounter].setFilterReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setSourceBinding("@Checked");
		tblShipmentListClmBd[tblShipListClmCounter].setTargetAttributeBinding("@Checked");
		tblShipmentListClmBd[tblShipListClmCounter].setCellEditorTheme("CheckBoxTheme");
	
        tblShipmentListClm[tblShipListClmCounter] = YRCConstants.YRC_CHECK_BOX_CELL_EDITOR;
        
        /*
         * Start OASIS 13-NOV-2013 for PMR 85613,379,000
         */
        tblShipListClmCounter++;
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmCustOrderDate");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@OrderedDate");		
		tblShipmentListClmBd[tblShipListClmCounter].setSortReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setColumnBinding(YRCPlatformUI.getString("Customer Order Date"));
		
		//End OASIS 13-NOV-2013 for PMR 85613,379,000
        
        
        tblShipListClmCounter++;
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmShipmentDate");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@ShipDate");		
		tblShipmentListClmBd[tblShipListClmCounter].setSortReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setColumnBinding(YRCPlatformUI.getString("clm_Shipment_Date"));
	
		
	
		tblShipListClmCounter++;
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmShipmentType");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@ShipmentType");
		tblShipmentListClmBd[tblShipListClmCounter].setSortReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setFilterReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setColumnBinding(YRCPlatformUI.getString("clm_Shipment_Type"));
	
		tblShipListClmCounter++;
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmShipmentQty");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@TotalQuantity");
		tblShipmentListClmBd[tblShipListClmCounter].setSortReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setFilterReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setColumnBinding(YRCPlatformUI.getString("clm_Shipment_Quantity"));
		
	
		tblShipListClmCounter++;
		tblShipmentListClmBd[tblShipListClmCounter] = new YRCTblClmBindingData();
		tblShipmentListClmBd[tblShipListClmCounter].setName("clmDepartment");
		tblShipmentListClmBd[tblShipListClmCounter].setAttributeBinding("@ExtnShipmentDepartment");
		tblShipmentListClmBd[tblShipListClmCounter].setSortReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setFilterReqd(true);
		tblShipmentListClmBd[tblShipListClmCounter].setColumnBinding(YRCPlatformUI.getString("clm_Department"));
		
		YRCTableBindingData tblItemShipmentbd = new YRCTableBindingData();
		tblItemShipmentbd.setName("tblShipmentList");
		tblItemShipmentbd.setSourceBinding("ShipmentList_AwaitingPickSlipPrint:/Shipments/Shipment");
		tblItemShipmentbd.setTblClmBindings(tblShipmentListClmBd);
		tblItemShipmentbd.setKeyNavigationRequired(true);
		tblShipmentList.setData(YRCConstants.YRC_TABLE_BINDING_DEFINATION, tblItemShipmentbd);
		tblShipmentList.setData(YRCConstants.YRC_CONTROL_NAME,"itemShipment");
		
		
		tblItemShipmentbd.setColorProvider(new IYRCTableColorProvider() {
			public String getColorTheme(Object element, int columnIndex) {
				if (columnIndex == 0 ) {
					return "EditableColumn";
				}
				return null;
			}
		});
		
		tblItemShipmentbd.setImageProvider(new IYRCTableImageProvider() {
			public String getImageThemeForColumn(Object element, int columnIndex) {
				YRCTableBindingData tblBind = (YRCTableBindingData) tblShipmentList.getData(YRCConstants.YRC_TABLE_BINDING_DEFINATION);
				YRCTblClmBindingData tblClmBind = tblBind.getTblClmBindings()[columnIndex];
				String ctrlName = tblClmBind.getName();
				Element orderLineElement = (Element)element;

				if ("clmSelectToPrint".equals(ctrlName)) {
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
				}
				return false; 
			}

			protected String getModifiedValue(String property, String value, Element element) {
				
				return value;
			}
			
			
		
		
		});

		
		tblItemShipmentbd.setCellTypes(tblShipmentListClm);
		tblItemShipmentbd.setCellModifierRequired(true);
		tblItemShipmentbd.setKeyNavigationRequired(true);
		tblItemShipmentbd.setSortRequired(true);
		tblItemShipmentbd.setFilterReqd(true);
		
		//binding for Label Header
	}
	
	public void setShipmentListModel(){
		myBehavior.invokeGetShipmentList();
	}
	

}  //  @jve:decl-index=0:visual-constraint="32,15"
