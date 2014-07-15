package com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages;

import org.eclipse.swt.widgets.Composite;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCWizardPageBehavior;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

public class KOHLSPickSlipsBulkPrintPopup extends Composite implements
		IYRCComposite {
	public static final String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.printStorePickSlips.wizardpages.KOHLSPickSlipsBulkPrintPopup";
	private KOHLSPickSlipsBulkPrintPopupBehavior myBehavior = null;
	private Composite pnlRoot = null;
	private Composite pnlUserInput = null;
	private Composite pnlInfo = null;
	private Label lblQtyToPrint = null;
	private Text txtQtyToPrint = null;
	private Button btnPrintBulkPrintSlips = null;
	private Label lblMsg = null;
	private Label label2 = null;
	public KOHLSPickSlipsBulkPrintPopup(Composite parent, int style) {
		super(parent, style);
		initialize();
		myBehavior = new KOHLSPickSlipsBulkPrintPopupBehavior(this, FORM_ID, null,null);
		// TODO Auto-generated constructor stub
	}

	public KOHLSPickSlipsBulkPrintPopup(Composite parent, int style, YRCWizardPageBehavior parentWizardPage) {
		super(parent, style);
		initialize();
		//setBindingForComponents();
		myBehavior = new KOHLSPickSlipsBulkPrintPopupBehavior(this, FORM_ID, null,parentWizardPage);
		// TODO Auto-generated constructor stub
	}
	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        createPnlRoot();
        this.setSize(new Point(389, 176));
			
	}

	public String getFormId() {
		// TODO Auto-generated method stub
		return FORM_ID;
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
		return this;
	}
	
	public KOHLSPickSlipsBulkPrintPopupBehavior getBehavior() {
        return myBehavior;
    }

	/**
	 * This method initializes pnlRoot	
	 *
	 */
	private void createPnlRoot() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		pnlRoot = new Composite(this, SWT.NONE);
		pnlRoot.setLayout(gridLayout);
		createPnlUserInput();
		createPnlInfo();
		pnlRoot.setBounds(new Rectangle(1, 3, 388, 173));
	}

	/**
	 * This method initializes pnlUserInput	
	 *
	 */
	private void createPnlUserInput() {
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout1 = new GridLayout();
		gridLayout1.numColumns = 2;
		gridLayout1.makeColumnsEqualWidth = true;
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = false;
		gridData.verticalSpan = 2;
		gridData.verticalAlignment = GridData.CENTER;
		pnlUserInput = new Composite(pnlRoot, SWT.NONE);
		pnlUserInput.setLayoutData(gridData);
		pnlUserInput.setLayout(gridLayout1);
		lblQtyToPrint = new Label(pnlUserInput, SWT.NONE);
		lblQtyToPrint.setText(YRCPlatformUI.getString("lbl_No_Of_Shipments_In_Pick_Slip"));
		lblQtyToPrint.setLayoutData(gridData3);
		txtQtyToPrint = new Text(pnlUserInput, SWT.BORDER);
	}

	/**
	 * This method initializes pnlInfo	
	 *
	 */
	private void createPnlInfo() {
		GridData gridData5 = new GridData();
		gridData5.grabExcessHorizontalSpace = false;
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.horizontalAlignment = GridData.FILL;
		gridData5.verticalAlignment = GridData.CENTER;
		gridData5.grabExcessHorizontalSpace = true;
		gridData5.grabExcessVerticalSpace = false;
		gridData5.heightHint = 30;
		gridData5.heightHint = -1;
		gridData5.grabExcessVerticalSpace = true;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessVerticalSpace = false;
		gridData2.verticalAlignment = GridData.CENTER;
		GridData gridData4 = new GridData();
		gridData4.horizontalAlignment = org.eclipse.swt.layout.GridData.BEGINNING;
		gridData4.grabExcessHorizontalSpace = true;
		gridData4.widthHint = 80;
		gridData4.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout2 = new GridLayout();
		gridLayout2.numColumns = 2;
		gridLayout2.makeColumnsEqualWidth = true;
		GridData gridData1 = new GridData();
		gridData1.grabExcessHorizontalSpace = true;
		gridData1.verticalAlignment = GridData.FILL;
		gridData1.grabExcessVerticalSpace = true;
		gridData1.horizontalAlignment = GridData.FILL;
		pnlInfo = new Composite(pnlRoot, SWT.NONE);
		pnlInfo.setLayoutData(gridData1);
		pnlInfo.setLayout(gridLayout2);
		lblMsg = new Label(pnlInfo, SWT.NONE);
		lblMsg.setText(YRCPlatformUI.getFormattedString(
				YRCPlatformUI.getString("Msg_Max_Qty_For_Pick_Slip_Print"), 
				new String[]{YRCPlatformUI.getString("Max_Qty_For_Pick_Slip_Print")}));
		lblMsg.setLayoutData(gridData2);
	
		
		btnPrintBulkPrintSlips = new Button(pnlInfo, SWT.CENTER);
		btnPrintBulkPrintSlips.setLayoutData(gridData4);
		btnPrintBulkPrintSlips.setText(YRCPlatformUI.getString("btn_Print_Bulk_Print_Slip"));
		label2 = new Label(pnlInfo, SWT.NONE);
		
		label2.setText(YRCPlatformUI.getFormattedString(
				YRCPlatformUI.getString("Msg_Max_Qty_For_Pick_Slip_Print1"), 
				new String[]{YRCPlatformUI.getString("Msg_Max_Qty_For_Pick_Slip_Print1")}));
		label2.setLayoutData(gridData5);
		label2.setLayoutData(gridData5);
		label2.setLayoutData(gridData5);
		btnPrintBulkPrintSlips.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				myBehavior.validateInput();
			}
		});
	}
	
	public String getQtyToPrint(){
		return this.txtQtyToPrint.getText();
	}
	
	public void setBindingForComponents(){
		//binding for # of shipment
	    
		YRCTextBindingData txtQtyToPrintBindingData = new YRCTextBindingData();		
		txtQtyToPrintBindingData.setTargetBinding("PickSlipBulkPrint:BulkPrintInput/@QtyToPrint");
		txtQtyToPrintBindingData.setName("txtQtyToPrint");
		txtQtyToPrint.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtQtyToPrintBindingData);
		txtQtyToPrint.setData(YRCConstants.YRC_CONTROL_NAME,"txtQtyToPrint");
		txtQtyToPrint.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
		txtQtyToPrintBindingData.setMandatory(true);
		txtQtyToPrintBindingData.setDataType("PositiveQuantity");
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
