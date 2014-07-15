
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.kohls.ibm.ocf.pca.tasks.batchprint.actions.KOHLSBatchPrintAction;
import com.kohls.ibm.ocf.pca.tasks.batchprint.actions.KOHLSBatchPrintReprintAction;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.IYRCCellModifier;
import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.IYRCTableColorProvider;
import com.yantra.yfc.rcp.IYRCTableImageProvider;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTableBindingData;
import com.yantra.yfc.rcp.YRCTblClmBindingData;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCWizardBehavior;

import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.TableViewer;
import org.w3c.dom.Element;
/**
 * @author Kiran
 * 
 *
 */
public class KOHLSBatchPrintScreen extends Composite implements IYRCComposite{

	private KOHLSBatchPrintScreenBehavior myBehavior;
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintScreen";  //  @jve:decl-index=0:
	private YRCWizardBehavior wizBehavior;
	private KOHLSBatchPrintScreenHelper helper;
	Composite pnlroot;
	Composite kohlsreprintBatchScreencstm;
	private boolean flag;
	private Composite cmpTop = null;
	private Composite cmpTitle = null;
	private Composite cmpReprintBatchID = null;
	private Label lblReprintTitle = null;
	private Label lblBatchID = null;
	private Text txtBatchID = null;
	private Label lblSelectPrinter = null;
	private Combo cmbPrinter = null;
	private Composite cmpCartInput = null;
	private Composite cmpCartSearchResult=null;
	private Composite cmpPrintButton = null;
	private Composite cmpPrintBtn = null;
	private Button btnPrint = null;
	private Label lblFiller = null;
	private Label lblFromDt = null;
	private Text txtCstmFromDt;
	private Text txtCstmToDt;
	private Text txtCstmDtRange;
	private Label lblToDt = null;
	private Button buttonSearch = null;
	Composite cstmFromDt;
	Button btnCstmFromDt;
	Button btnCstmToDt;
	Composite cmpstRot;
	Composite cstmToDt;
	private Table tableSearchResults = null;
	private TableViewer tableViewer = null;
	TableColumn tblClmnCheckBox;
	Element tmpVal=null;
	int cntCheck=0;
	private Label lblExtnBatchNo = null;
	private Text txtExtnBatchNo = null;
	public KOHLSBatchPrintScreen(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public KOHLSBatchPrintScreen(Composite parent, int style, Object input) {
		super(parent, style);
		initialize();
		setBindingForComponents();
		helper=new KOHLSBatchPrintScreenHelper(this,FORM_ID,input);
		myBehavior = new KOHLSBatchPrintScreenBehavior(this, FORM_ID, input, helper);
		helper.setBehavior(myBehavior);
	}
	
	private void initialize(){
		this.setData(YRCConstants.YRC_CONTROL_NAME, "this");
			GridLayout thislayout = new GridLayout(1, false);
		thislayout.marginHeight = 0;
		thislayout.numColumns = 2;
		thislayout.marginWidth = 0;
		this.setLayout(thislayout);
		createPnlroot();
		//setSize(new org.eclipse.swt.graphics.Point(844,540));
		//setSize(new org.eclipse.swt.graphics.Point(950,750));

	}

	private void createPnlroot(){
	
		pnlroot = new Composite(this, SWT.NONE);
		
		pnlroot.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlroot.setData(YRCConstants.YRC_CONTROL_NAME, "pnlroot");
		
		GridData pnlrootlayoutData = new GridData();
		pnlrootlayoutData.horizontalAlignment = GridData.FILL;
		pnlrootlayoutData.verticalAlignment = GridData.FILL;
		pnlrootlayoutData.grabExcessHorizontalSpace = true;
		pnlrootlayoutData.horizontalSpan = 2;
		pnlrootlayoutData.verticalSpan = 2;
		pnlrootlayoutData.grabExcessVerticalSpace = true;
		pnlroot.setLayoutData(pnlrootlayoutData);
		createCmpTop();
		pnlroot.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		
		GridLayout pnlrootlayout = new GridLayout(2, false);
		pnlrootlayout.numColumns = 1;
		pnlroot.setLayout(pnlrootlayout);
	
		btnPrint.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Button");
	
	}
	
	public void setBindingForComponents() {
		
		//		binding for screen title
		YRCLabelBindingData lblReprintTitlebd = new YRCLabelBindingData();
		lblReprintTitlebd.setName("lblReprintTitle"); 
		lblReprintTitle.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblReprintTitlebd); 
		
		//binding for BatchID text
		
		YRCTextBindingData txtBatchIDBindingData = new YRCTextBindingData();
		txtBatchIDBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@User");
		String loginId = YRCPlatformUI.getUserElement().getAttribute(KOHLSPCAConstants.A_LOGIN_ID);
		//txtBatchIDBindingData.setSourceBinding("ShipmentDetails:Shipment/@ProfileID");
		txtBatchIDBindingData.setName("txtBatchID");
		txtBatchID.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtBatchIDBindingData);
		txtBatchID.setData(YRCConstants.YRC_CONTROL_NAME,"txtBatchID");
		txtBatchID.setText(loginId);
		//txtBatchIDBindingData.setMandatory(true);
		
		YRCTextBindingData txtBatchNoBindingData = new YRCTextBindingData();
		txtBatchNoBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@BatchID");
		txtBatchNoBindingData.setName("txtExtnBatchNo");
		txtExtnBatchNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtBatchNoBindingData);
		txtExtnBatchNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtExtnBatchNo");
		//binding for Single Printer Combo
		
	 	YRCComboBindingData bdCmbMPrinterID = new YRCComboBindingData();
	 	bdCmbMPrinterID.setName("cmbPrinter");
	 	bdCmbMPrinterID.setSourceBinding("getDeviceList:Devices/Device/@DeviceId");
	 	//bdCmbMPrinterID.setSourceBinding("getDeviceList1:Devices/Device/@DeviceId");
	 	bdCmbMPrinterID.setCodeBinding("DeviceId");
	 	bdCmbMPrinterID.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@Status");
	 	bdCmbMPrinterID.setListBinding("getDeviceList:Devices/Device");
	 	bdCmbMPrinterID.setDescriptionBinding("@DeviceId");
	 	bdCmbMPrinterID.setMandatory(true);
	 	cmbPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbMPrinterID);
	 	cmbPrinter.setData(YRCConstants.YRC_CONTROL_NAME,"cmbPrinter");
	 	
	 	txtCstmDtRange = new Text(cstmToDt, SWT.Hide);
	 	txtCstmDtRange.setData("DATERANGE");
	 	txtCstmDtRange.setText("DATERANGE");
	 	txtCstmDtRange.setVisible(false);
	 	
	 	YRCTextBindingData txtdateRangeBindingData = new YRCTextBindingData();
	 	txtdateRangeBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@CreateTSQryType");
	 	txtdateRangeBindingData.setName("txtCstmDtRange");
	 	txtCstmDtRange.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtdateRangeBindingData);
	 	txtCstmDtRange.setData(YRCConstants.YRC_CONTROL_NAME,"txtCstmDtRange");
	 	
	 	YRCTextBindingData txtfromDateBindingData = new YRCTextBindingData();
//	 	txtfromDateBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/ComplexQuery/@Createts");
	 	txtfromDateBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@FromCreateTS");
	 	txtfromDateBindingData.setName("txtCstmFromDt");
	 	txtCstmFromDt.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtfromDateBindingData);
	 	txtCstmFromDt.setData(YRCConstants.YRC_CONTROL_NAME,"txtCstmFromDt");
	 	
	 	YRCTextBindingData txtToDateBindingData = new YRCTextBindingData();
	 	txtToDateBindingData.setTargetBinding("ProcessReprintBatchInput:BatchPrint/@ToCreateTS");
	 	txtToDateBindingData.setName("txtCstmToDt");
	 	txtCstmToDt.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION, txtToDateBindingData);
	 	txtCstmToDt.setData(YRCConstants.YRC_CONTROL_NAME,"txtCstmToDt");
	 	//CreatetsQryType="DATERANGE" FromCreatets="20130607" ToCreatets="20130608"
	 	
	    //binding for Print Button
		YRCButtonBindingData btnPrintbd = new YRCButtonBindingData();
		btnPrintbd.setName("btnPrint");
		btnPrintbd.setActionHandlerEnabled(true);
		btnPrintbd.setActionId(KOHLSBatchPrintReprintAction.ACTION_ID);
		btnPrint.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnPrintbd);
		btnPrint.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrint");
		
		//binding for table
		
		YRCTblClmBindingData itemShipmentClmBd[] = new YRCTblClmBindingData[tableSearchResults.getColumnCount()];
		String[] itemShipmentEditors = new String[tableSearchResults.getColumnCount()];
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
		itemShipmentClmBd[itemShipmentCounter].setName("colBatchId");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@BatchID");	
//		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("batch_no_string"));
		itemShipmentClmBd[itemShipmentCounter].setTargetAttributeBinding("@BatchID");
		itemShipmentEditors[itemShipmentCounter] = YRCConstants.YRC_TEXT_BOX_CELL_EDITOR;
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("colStatus");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@Status");
//		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("status_string"));
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("colNoOfShp");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@NoOfShipments");
//		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("no_of_shipments"));
		itemShipmentClmBd[itemShipmentCounter].setDataType("PositiveQuantity");
				
	
		itemShipmentCounter++;
		itemShipmentClmBd[itemShipmentCounter] = new YRCTblClmBindingData();
		itemShipmentClmBd[itemShipmentCounter].setName("colReprint");
		itemShipmentClmBd[itemShipmentCounter].setAttributeBinding("@ReprintFlag");
//		itemShipmentClmBd[itemShipmentCounter].setSortReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setFilterReqd(true);
		itemShipmentClmBd[itemShipmentCounter].setColumnBinding(YRCPlatformUI.getString("reprint_flag"));
		itemShipmentClmBd[itemShipmentCounter].setTargetAttributeBinding("@TotalShipments");
//		itemShipmentClmBd[itemShipmentCounter].setDataType("PositiveQuantity");
//		itemShipmentEditors[itemShipmentCounter] = YRCConstants.YRC_TEXT_BOX_CELL_EDITOR;
		
		
		YRCTableBindingData tblItemShipmentbd = new YRCTableBindingData();
		tblItemShipmentbd.setName("itemShipment");
		tblItemShipmentbd.setSourceBinding("PrintPackShipments:/BatchPrintList/BatchPrint");
		tblItemShipmentbd.setTblClmBindings(itemShipmentClmBd);
		tblItemShipmentbd.setKeyNavigationRequired(true);
		tableSearchResults.setData(YRCConstants.YRC_TABLE_BINDING_DEFINATION, tblItemShipmentbd);
		tableSearchResults.setData(YRCConstants.YRC_CONTROL_NAME,"itemShipment");
//		tableSearchResults.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
//			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
//				helper.widgetSelected(e, "tblSearchList");
//			}
//
//			public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
//				helper.widgetDefaultSelected(e, "tblSearchList");
//			}
//		});
		
//		tableSearchResults.addMouseListener(new MouseAdapter() {
//		    public void mouseDoubleClick(MouseEvent e)   {
//		    	myBehavior.handleTableRowSelected(tableSearchResults);
//		    }
//		});

		
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
				YRCTableBindingData tblBind = (YRCTableBindingData) tableSearchResults.getData(YRCConstants.YRC_TABLE_BINDING_DEFINATION);
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
						if (cntCheck>0 && !tmpVal.getAttribute("BatchID").equals(element.getAttribute("BatchID"))) {
//							/Errors/Error/@ErrorCode
							/*Document mydoc=YRCXmlUtils.createDocument("Errors");
							Element errList= mydoc.getDocumentElement();
							Element eleError = YRCXmlUtils.createChild(errList,"Error");
							eleError.setAttribute("ErrorCode","Single Selection");
							eleError.setAttribute("ErrorDescription","Only one checkbox needs to be selected");
							YRCErrorDialog errorDialog= new YRCErrorDialog("Single selection","Error arg2",mydoc);
							errorDialog.setBlockOnOpen(true);
							errorDialog.open();*/
							YRCPlatformUI.showError(YRCPlatformUI.getString("single_clik_error"), YRCPlatformUI.getString("single_clik_desc"));
//							YRCPlatformUI.showError("Single selection", "Only one checkbox needs to be selected");
				    		//return "Error";

						}
						cntCheck=1;
						if (tmpVal!=null){
							tmpVal.setAttribute("Checked", "N");
						} 
						tmpVal=element;
						myBehavior.setSelectedBatchModel(element);
						myBehavior.refreshTable(tableSearchResults);
						//element.setAttribute("BatchID", element.getAttribute("BatchID"));	
						//myBehavior.setMyMasterBactchModel(element,tableSearchResults);
					} else {
						cntCheck=0;
						tmpVal=null;
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
		//binding for table

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
	public KOHLSBatchPrintScreenBehavior getBehavior() {
        return myBehavior;
    }
	
	public KOHLSBatchPrintScreenHelper getHelper() {
        return helper;
    }
	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }

	/**
	 * This method initializes cmpTop	
	 *
	 */
	private void createCmpTop() {
		GridLayout gridLayout5 = new GridLayout();
		gridLayout5.makeColumnsEqualWidth = false;
		gridLayout5.verticalSpacing = 0;
		gridLayout5.horizontalSpacing = 0;
		GridData gridData10 = new GridData();
		gridData10.horizontalAlignment = GridData.FILL;
		gridData10.heightHint = -1;
		gridData10.grabExcessHorizontalSpace = true;
		gridData10.grabExcessVerticalSpace = true;
		gridData10.verticalAlignment = GridData.FILL;
		cmpTop = new Composite(pnlroot, SWT.NONE);
		cmpTop.setLayoutData(gridData10);
		cmpTop.setData(YRCConstants.YRC_CONTROL_NAME,"cmpTop");
		cmpTop.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createCmpTitle();
		cmpTop.setLayout(gridLayout5);
		createCmpSearchConditions();
		createCmpSearchResult();
		createCmpCartInput();
	}

	/**
	 * This method initializes cmpTitle	
	 *
	 */
	private void createCmpTitle() {
		GridData gridData6 = new GridData();
		gridData6.horizontalAlignment = GridData.FILL;
		gridData6.grabExcessHorizontalSpace = false;
		gridData6.grabExcessVerticalSpace = false;
		gridData6.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout6 = new GridLayout();
		gridLayout6.numColumns = 1;
		GridData gridData11 = new GridData();
		gridData11.horizontalAlignment = GridData.FILL;
		gridData11.grabExcessHorizontalSpace = true;
		gridData11.grabExcessVerticalSpace = false;
		gridData11.verticalAlignment = GridData.CENTER;
		cmpTitle = new Composite(cmpTop, SWT.NONE);
		cmpTitle.setLayoutData(gridData11);
		cmpTitle.setLayout(gridLayout6);
		cmpTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
		lblReprintTitle = new Label(cmpTitle, SWT.NONE);
		lblReprintTitle.setText(YRCPlatformUI.getString("Batch_Print"));
		lblReprintTitle.setLayoutData(gridData6);
		lblReprintTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
	}

	/**
	 * This method initializes cmpReprintBatchID	
	 *
	 */
	private void createCmpSearchConditions() {
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.END;
		gridData14.verticalAlignment = GridData.CENTER;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.END;
		gridData13.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		gridData12.widthHint = 120;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 8;
		gridLayout3.makeColumnsEqualWidth = false;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalSpan = 2;
		gridData3.verticalAlignment = GridData.FILL;
		cmpReprintBatchID = new Composite(cmpTop, SWT.NONE);
		cmpReprintBatchID.setLayoutData(gridData3);
		cmpReprintBatchID.setLayout(gridLayout3);
		
		
		cmpReprintBatchID.setData(YRCConstants.YRC_CONTROL_NAME,"cmpReprintBatchID");
		cmpReprintBatchID.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Composite");
		
		lblFromDt = new Label(cmpReprintBatchID, SWT.NONE);
		lblFromDt.setText("From:");
		
		createCstmFromDt();
		lblFiller = new Label(cmpReprintBatchID, SWT.NONE);
		lblFiller.setText("                     ");
				
		lblSelectPrinter = new Label(cmpReprintBatchID, SWT.NONE);
		lblSelectPrinter.setText("Batch Status");
		lblSelectPrinter.setLayoutData(gridData14);
		createCmbPrinter();
		lblFiller = new Label(cmpReprintBatchID, SWT.NONE);
		lblFiller.setText("                     ");
		lblBatchID = new Label(cmpReprintBatchID, SWT.NONE);
		lblBatchID.setText("Employee TK#");
		lblBatchID.setLayoutData(gridData13);
		txtBatchID = new Text(cmpReprintBatchID, SWT.BORDER);
		txtBatchID.setLayoutData(gridData12);
		lblToDt = new Label(cmpReprintBatchID, SWT.NONE);
		lblToDt.setText("To:");
		createCstmToDt();
		//textToDt = new Text(cmpReprintBatchID, SWT.BORDER);
		Label filler2 = new Label(cmpReprintBatchID, SWT.NONE);
		lblExtnBatchNo = new Label(cmpReprintBatchID, SWT.NONE);
		lblExtnBatchNo.setText("Batch No");
		txtExtnBatchNo = new Text(cmpReprintBatchID, SWT.BORDER);
		txtExtnBatchNo.setLayoutData(gridData12);
		Label filler5 = new Label(cmpReprintBatchID, SWT.NONE);
		Label filler6 = new Label(cmpReprintBatchID, SWT.NONE);
		buttonSearch = new Button(cmpReprintBatchID, SWT.NONE);
		buttonSearch.setText("Search");
		
//		Label filler2 = new Label(cmpReprintBatchID, SWT.NONE);
//		Label filler3 = new Label(cmpReprintBatchID, SWT.NONE);
//		Label filler4 = new Label(cmpReprintBatchID, SWT.NONE);
//		Label filler5 = new Label(cmpReprintBatchID, SWT.NONE);
//		Label filler6 = new Label(cmpReprintBatchID, SWT.NONE);
//		buttonSearch = new Button(cmpReprintBatchID, SWT.NONE);
//		buttonSearch.setText("Search");
		
		YRCButtonBindingData btnSearchbd = new YRCButtonBindingData();
		btnSearchbd.setName("buttonSearch");
		btnSearchbd.setActionHandlerEnabled(true);
		btnSearchbd.setActionId(KOHLSBatchPrintAction.ACTION_ID); 
		buttonSearch.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnSearchbd);
		buttonSearch.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrint");
		
	}

	private void createCstmFromDt(){
		cstmFromDt = new Composite(cmpReprintBatchID, SWT.NONE);
		cstmFromDt.setData(YRCConstants.YRC_CONTROL_NAME, "cstmFromDt");
	    GridLayout cstmFromDtLayout = new GridLayout(2,false);
	    cstmFromDtLayout.marginWidth = 0;
	    cstmFromDtLayout.marginHeight = 0;
	    cstmFromDtLayout.horizontalSpacing = 5;
	    cstmFromDtLayout.verticalSpacing = 0;
	    cstmFromDt.setLayout(cstmFromDtLayout);
		GridData cstmFromDtlayoutData = new GridData();
		cstmFromDtlayoutData.horizontalAlignment = 2;
		cstmFromDt.setLayoutData(cstmFromDtlayoutData);
		GridData lblGridData = new org.eclipse.swt.layout.GridData();
		lblGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData txtGridData = new GridData();
		txtGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		
		txtCstmFromDt = new Text(cstmFromDt, SWT.BORDER);
		txtCstmFromDt.setLayoutData(txtGridData);
		
		btnCstmFromDt = new Button(cstmFromDt, SWT.NONE);
		btnCstmFromDt.setImage(YRCPlatformUI.getImage("DateLookup"));
		btnCstmFromDt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				YRCPlatformUI.showCalendar(txtCstmFromDt);
			}
		});
		btnCstmFromDt.setLayoutData(new GridData());
		btnCstmFromDt.setData(YRCConstants.YRC_CONTROL_NAME, "btnCstmFromDt");
		
		}
	
	private void createCstmToDt(){
		cstmToDt = new Composite(cmpReprintBatchID, SWT.NONE);
		cstmToDt.setData(YRCConstants.YRC_CONTROL_NAME, "cstmToDt");
	    GridLayout cstmToDtLayout = new GridLayout(2,false);
	    cstmToDtLayout.marginWidth = 0;
	    cstmToDtLayout.marginHeight = 0;
	    cstmToDtLayout.horizontalSpacing = 5;
	    cstmToDtLayout.verticalSpacing = 0;
	    cstmToDt.setLayout(cstmToDtLayout);
		GridData cstmToDtlayoutData = new GridData();
		cstmToDtlayoutData.horizontalAlignment = 2;
		cstmToDt.setLayoutData(cstmToDtlayoutData);
		GridData lblGridData = new org.eclipse.swt.layout.GridData();
		lblGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.END;
		GridData txtGridData = new GridData();
		txtGridData.horizontalAlignment = org.eclipse.swt.layout.GridData.FILL;
		
		txtCstmToDt = new Text(cstmToDt, SWT.BORDER);
		txtCstmToDt.setLayoutData(txtGridData);
		
		btnCstmToDt = new Button(cstmToDt, SWT.NONE);
		btnCstmToDt.setImage(YRCPlatformUI.getImage("DateLookup"));
		btnCstmToDt.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				YRCPlatformUI.showCalendar(txtCstmToDt);
			}
		});
		btnCstmToDt.setLayoutData(new GridData());
		btnCstmToDt.setData(YRCConstants.YRC_CONTROL_NAME, "btnCstmToDt");
		
		}
	/**
	 * This method initializes cmbPrinter	
	 *
	 */
	private void createCmbPrinter() {
		cmbPrinter = new Combo(cmpReprintBatchID, SWT.READ_ONLY);
		cmbPrinter.add("", 0);
		cmbPrinter.add(KOHLSPCAConstants.A_PRINT_ST_PRINTED, 1);
		cmbPrinter.add(KOHLSPCAConstants.A_PRINT_ST_AWAIT_PRINT, 2);
		cmbPrinter.add(KOHLSPCAConstants.A_PRINT_ST_REPRINT, 3);
		cmbPrinter.add(KOHLSPCAConstants.A_PRINT_ST_FAILED, 4);
	}
	
	private void createCmpSearchResult(){
		GridData gridData = new GridData();
		gridData.verticalSpan = 3;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = false;
		gridData.grabExcessVerticalSpace = false;
		GridLayout gridLayout001 = new GridLayout();
		gridLayout001.numColumns = 1;
		gridLayout001.makeColumnsEqualWidth = true;
		GridData gridData001 = new GridData();
		gridData001.horizontalAlignment = GridData.FILL;
		gridData001.grabExcessVerticalSpace = true;
		gridData001.grabExcessHorizontalSpace = true;
		gridData001.verticalAlignment = GridData.FILL;
		cmpCartSearchResult = new Composite(cmpTop, SWT.NONE);
		cmpCartSearchResult.setLayout(null);
		cmpCartSearchResult.setLayoutData(gridData);
		
		//tableSearchResults = new Table(cmpCartSearchResult, SWT.NONE);
		 tableSearchResults= new Table(cmpCartSearchResult,SWT.H_SCROLL
				| SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.MULTI);
		//tableSearchResults = new Table(cmpCartSearchResult, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		tableSearchResults.setHeaderVisible(true);
		tableSearchResults.setLinesVisible(true);
		tableSearchResults.setData(YRCConstants.YRC_CONTROL_NAME, "itemShipment");
		tableSearchResults.setEnabled(true);
		//tableSearchResults.setBounds(new Rectangle(5, 5, 767, 81));
		tableSearchResults.setBounds(new Rectangle(5, 5, 1372, 350));
		//tableSearchResults.setBounds(new Rectangle(5, 5, 1080, 45));
		tableViewer = new TableViewer(tableSearchResults);
		
		
		tableSearchResults.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Table");
		
		tblClmnCheckBox = new TableColumn(tableSearchResults, SWT.CENTER);
		tblClmnCheckBox.setWidth(35);
		tblClmnCheckBox.setResizable(false);
		tblClmnCheckBox.setMoveable(false);
		
		TableColumn colBatchId = new TableColumn(tableSearchResults, SWT.NONE);
		colBatchId.setWidth(250);
		colBatchId.setText("Batch ID");
		TableColumn colStatus = new TableColumn(tableSearchResults, SWT.NONE);
		colStatus.setWidth(250);
		colStatus.setText("Status");
		TableColumn colNoOfShp = new TableColumn(tableSearchResults, SWT.NONE);
		colNoOfShp.setWidth(250);
		colNoOfShp.setText("No of Shipments");
		TableColumn colReprint = new TableColumn(tableSearchResults, SWT.NONE);
		colReprint.setWidth(250);
		colReprint.setText("Reprinted");
		
	}
	/**
	 * This method initializes cmpCartInput	
	 *
	 */
	private void createCmpCartInput() {
		GridLayout gridLayout4 = new GridLayout();
		gridLayout4.numColumns = 2;
		gridLayout4.makeColumnsEqualWidth = true;
		GridData gridData5 = new GridData();
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.grabExcessVerticalSpace = true;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.verticalSpan = 2;
		gridData5.verticalAlignment = GridData.FILL;
		cmpCartInput = new Composite(cmpTop, SWT.NONE);
		cmpCartInput.setLayout(gridLayout4);
		cmpCartInput.setLayoutData(gridData5);
		createCmpPrintButton();
		createCmpPrintBtn();
	}

	/**
	 * This method initializes cmpPrintButton	
	 *
	 */
	private void createCmpPrintButton() {
		GridData gridData8 = new GridData();
		gridData8.horizontalAlignment = GridData.FILL;
		gridData8.grabExcessHorizontalSpace = false;
		gridData8.grabExcessVerticalSpace = true;
		gridData8.verticalAlignment = GridData.FILL;
		GridLayout gridLayout7 = new GridLayout();
		gridLayout7.makeColumnsEqualWidth = true;
		gridLayout7.horizontalSpacing = 0;
		gridLayout7.verticalSpacing = 0;
		gridLayout7.marginWidth = 0;
		gridLayout7.marginHeight = 0;
		gridLayout7.numColumns = 2;
		cmpPrintButton = new Composite(cmpCartInput, SWT.NONE);
		cmpPrintButton.setLayout(gridLayout7);
		cmpPrintButton.setLayoutData(gridData8);
	}

	/**
	 * This method initializes cmpPrintBtn	
	 *
	 */
	private void createCmpPrintBtn() {
		GridData gridData9 = new GridData();
		gridData9.verticalAlignment = GridData.FILL;
		gridData9.grabExcessHorizontalSpace = true;
		gridData9.heightHint = -1;
		gridData9.widthHint = 120;
		gridData9.horizontalAlignment = GridData.END;
		GridLayout gridLayout8 = new GridLayout();
		gridLayout8.numColumns = 1;
		gridLayout8.verticalSpacing = 0;
		gridLayout8.marginWidth = 0;
		gridLayout8.marginHeight = 0;
		gridLayout8.horizontalSpacing = 0;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = GridData.BEGINNING;
		gridData4.grabExcessHorizontalSpace = false;
		gridData4.grabExcessVerticalSpace = true;
		gridData4.verticalAlignment = GridData.CENTER;
		cmpPrintBtn = new Composite(cmpCartInput, SWT.NONE);
		cmpPrintBtn.setLayoutData(gridData4);
		cmpPrintBtn.setLayout(gridLayout8);
		btnPrint = new Button(cmpPrintBtn, SWT.NONE);
		btnPrint.setText(YRCPlatformUI.getString("batch_print_button"));
		btnPrint.setLayoutData(gridData9);
	}
    
   
	/*public void setBindingForComponents_old() {
	
	//disable all text boxes
	
	 txtFromCartNo.setText("");
	 txtToCartNo.setText("");
	 
	 txtFromCartNo.setEnabled(false);
	 txtToCartNo.setEnabled(false);
	
	//		binding for screen title
	YRCLabelBindingData lblReprintTitlebd = new YRCLabelBindingData();
	lblReprintTitlebd.setName("lblReprintTitle"); 
	lblReprintTitle.setData(YRCConstants.YRC_LABEL_BINDING_DEFINITION, lblReprintTitlebd); 
	
	//binding for BatchID text
	
	YRCTextBindingData txtBatchIDBindingData = new YRCTextBindingData();
	
	txtBatchIDBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@BatchID");
	txtBatchIDBindingData.setSourceBinding("ShipmentDetails:Shipment/@ProfileID");
	txtBatchIDBindingData.setName("txtBatchID");
	txtBatchID.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,
			txtBatchIDBindingData);
	txtBatchID.setData(YRCConstants.YRC_CONTROL_NAME,"txtBatchID");
	txtBatchIDBindingData.setMandatory(true);
	//TODO
	
	//binding for Single Printer Combo
	
 	YRCComboBindingData bdCmbMPrinterID = new YRCComboBindingData();
 	bdCmbMPrinterID.setName("cmbPrinter");
 	bdCmbMPrinterID.setSourceBinding("getDeviceList:Devices/Device/@DeviceId");
 	bdCmbMPrinterID.setCodeBinding("DeviceId");
 	bdCmbMPrinterID.setTargetBinding("ProcessReprintBatchInput:Shipment/@PrinterID");
 	bdCmbMPrinterID.setListBinding("getDeviceList:Devices/Device");
 	bdCmbMPrinterID.setDescriptionBinding("@DeviceId");
 	bdCmbMPrinterID.setMandatory(true);
 	cmbPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbMPrinterID);
 	cmbPrinter.setData(YRCConstants.YRC_CONTROL_NAME,"cmbPrinter");
 	
	
	//binding for text boxes and cart radiobuttons
 	
	YRCTextBindingData txtCartNoBindingData = new YRCTextBindingData();
    txtCartNoBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@CartNo");
    txtCartNoBindingData.setName("txtCartNo");
//    txtCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtCartNoBindingData);
//    txtCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtCartNo");
//    txtCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
    txtCartNoBindingData.setDataType("PositiveQuantity");
    
    YRCTextBindingData txtFromCartNoBindingData = new YRCTextBindingData();
    txtFromCartNoBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@FromCartNo");
    txtFromCartNoBindingData.setName("txtFromCartNo");
    txtFromCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtFromCartNoBindingData);
    txtFromCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtFromCartNo");
    txtFromCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
    txtFromCartNoBindingData.setDataType("PositiveQuantity");
    
	YRCTextBindingData txtToCartNoBindingData = new YRCTextBindingData();
	
    txtToCartNoBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@ToCartNo");
    txtToCartNoBindingData.setName("txtToCartNo");
    txtToCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtToCartNoBindingData);
    txtToCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtToCartNo");
    txtToCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
    txtToCartNoBindingData.setDataType("PositiveQuantity");
 	   
    YRCButtonBindingData radAllCartsBindingData  = new YRCButtonBindingData();
	radAllCartsBindingData.setName("radAllCarts");
	radAllCartsBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@Option");
	radAllCartsBindingData.setCheckedBinding("1");
	radAllCarts.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, radAllCartsBindingData); 
	radAllCarts.setData(YRCConstants.YRC_CONTROL_NAME,"radAllCarts");
	radAllCarts.setSelection(true);
	
	radAllCarts.addSelectionListener(new SelectionListener() {
		  

			public void widgetDefaultSelected(SelectionEvent e) {
//				 txtCartNo.setText("");
				 txtFromCartNo.setText("");
				 txtToCartNo.setText("");
				 
//		    	 txtCartNo.setEnabled(false);
		    	 txtFromCartNo.setEnabled(false);
		    	 txtToCartNo.setEnabled(false);
		    	
			}

			public void widgetSelected(SelectionEvent e) {
//				 txtCartNo.setText("");
				 txtFromCartNo.setText("");
				 txtToCartNo.setText("");
				 
//		    	 txtCartNo.setEnabled(false);
		    	 txtFromCartNo.setEnabled(false);
		    	 txtToCartNo.setEnabled(false);
				
			}
		       });
    
    YRCButtonBindingData radCartBindingData  = new YRCButtonBindingData();
    radCartBindingData.setName("radCart");
    radCartBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@Option");
    radCartBindingData.setCheckedBinding("2");
	
	 YRCButtonBindingData radCartRangeBindingData  = new YRCButtonBindingData();
	 radCartRangeBindingData.setName("radCartRange");
	 radCartRangeBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@Option");
	 radCartRangeBindingData.setCheckedBinding("3");
	 radCartRange.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, radCartRangeBindingData); 
	 radCartRange.setData(YRCConstants.YRC_CONTROL_NAME,"radCartRange");
	 
	 radCartRange.addSelectionListener(new SelectionListener() {
		  

			public void widgetDefaultSelected(SelectionEvent e) {
//				 txtCartNo.setText("");
//		    	 txtCartNo.setEnabled(false);
		    	 txtFromCartNo.setEnabled(true);
		    	 txtFromCartNo.setFocus();
		    	 txtToCartNo.setEnabled(true);
				
			}

			public void widgetSelected(SelectionEvent e) {
//				 txtCartNo.setText("");
//		    	 txtCartNo.setEnabled(false);
		    	 txtFromCartNo.setEnabled(true);
		    	 txtFromCartNo.setFocus();
		    	 txtToCartNo.setEnabled(true);
				
			}
		       });
	 
	    
	    //binding for Print Button
		
		YRCButtonBindingData btnPrintbd = new YRCButtonBindingData();
		btnPrintbd.setName("btnPrint");
		btnPrintbd.setActionHandlerEnabled(true);
		btnPrintbd.setActionId(KOHLSBatchPrintAction.ACTION_ID); 
		btnPrint.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnPrintbd);
		btnPrint.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrint");
	

}*/    
    
   
    

	   
}