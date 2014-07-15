
package com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.w3c.dom.Element;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCConstants;
import com.yantra.yfc.rcp.YRCLabelBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCWizardBehavior;
/**
 * @author Kiran
 * 
 *
 */
public class KOHLSReprintBatchScreen extends Composite implements IYRCComposite{

	private KOHLSReprintBatchScreenBehavior myBehavior;
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.reprintbatch.wizardpages.KOHLSReprintBatchScreen";  //  @jve:decl-index=0:
	private YRCWizardBehavior wizBehavior;
	private KOHLSReprintBatchScreenHelper helper;
	Composite pnlroot;
	Composite kohlsreprintBatchScreencstm;
	private boolean flag;
	private Composite cmpCart = null;
	private Button radAllCarts = null;
	private Button radCart = null;
	private Button radCartRange = null;
	private Text txtCartNo = null;
	private Text txtFromCartNo = null;
	private Label lblTo = null;
	private Text txtToCartNo = null;
	private Composite cmpTop = null;
	private Composite cmpTitle = null;
	private Composite cmpReprintBatchID = null;
	private Label lblReprintTitle = null;
	private Label lblBatchID = null;
	private Text txtBatchID = null;
	private Label lblSelectPrinter = null;
	private Combo cmbPrinter = null;
	private Composite cmpCartInput = null;
	private Composite cmpPrintButton = null;
	private Composite cmpPrintBtn = null;
	private Button btnPrint = null;
	private Composite cmpEmpty = null;
	private Label lblFiller = null;
	Element eleInput = null;
	Element eleDeviceList = null;
	
	public KOHLSReprintBatchScreen(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public KOHLSReprintBatchScreen(Composite parent, int style, Object input) {
		super(parent, style);
		initialize();
		setBindingForComponents();
		helper=new KOHLSReprintBatchScreenHelper(this,FORM_ID,input);
		myBehavior = new KOHLSReprintBatchScreenBehavior(this, FORM_ID, input, helper);
		helper.setBehavior(myBehavior);
	}
	
	public KOHLSReprintBatchScreen(Composite parent, int style, Object input, Object deviceInput) {
		super(parent, style);
		
		eleInput=(Element)(((HashMap) input).get("input"));
		initialize();
		eleDeviceList = (Element)deviceInput;
		setBindingForComponents();
		helper=new KOHLSReprintBatchScreenHelper(this,FORM_ID,input);
		myBehavior = new KOHLSReprintBatchScreenBehavior(this, FORM_ID, input,deviceInput, helper);
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
		setSize(new org.eclipse.swt.graphics.Point(844,540));

	}

	private void createPnlroot(){
	
		pnlroot = new Composite(this, SWT.NONE);
		
		pnlroot.setBackgroundMode(SWT.INHERIT_NONE);
	
		pnlroot.setData(YRCConstants.YRC_CONTROL_NAME, "pnlroot");
		
		GridData pnlrootlayoutData = new GridData();
		pnlrootlayoutData.horizontalAlignment = 4;
		pnlrootlayoutData.verticalAlignment = 4;
		pnlrootlayoutData.grabExcessHorizontalSpace = true;
		pnlrootlayoutData.horizontalSpan = 2;
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
		
		//disable all text boxes
		
		 txtCartNo.setText("");
		 txtFromCartNo.setText("");
		 txtToCartNo.setText("");
		 
    	 txtCartNo.setEnabled(false);
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
		
		
		if (eleInput==null) {
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
		 	cmbPrinter.addSelectionListener(new SelectionListener(){
				public void widgetSelected(SelectionEvent e) {
			        myBehavior.handleComboBoxSelection(cmbPrinter);
			    }
				public void widgetDefaultSelected(SelectionEvent e) {
			    }
			});
		}
	 	
		
		//binding for text boxes and cart radiobuttons
	 	
		YRCTextBindingData txtCartNoBindingData = new YRCTextBindingData();
	    txtCartNoBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@CartNo");
	    txtCartNoBindingData.setName("txtCartNo");
	    txtCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtCartNoBindingData);
	    txtCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtCartNo");
	    txtCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
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
					 txtCartNo.setText("");
					 txtFromCartNo.setText("");
					 txtToCartNo.setText("");
					 
			    	 txtCartNo.setEnabled(false);
			    	 txtFromCartNo.setEnabled(false);
			    	 txtToCartNo.setEnabled(false);
			    	 
					
				}

				public void widgetSelected(SelectionEvent e) {
					 txtCartNo.setText("");
					 txtFromCartNo.setText("");
					 txtToCartNo.setText("");
					 
			    	 txtCartNo.setEnabled(false);
			    	 txtFromCartNo.setEnabled(false);
			    	 txtToCartNo.setEnabled(false);
					
				}
			       });
	    
	    YRCButtonBindingData radCartBindingData  = new YRCButtonBindingData();
	    radCartBindingData.setName("radCart");
	    radCartBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@Option");
	    radCartBindingData.setCheckedBinding("2");
		radCart.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, radCartBindingData); 
		radCart.setData(YRCConstants.YRC_CONTROL_NAME,"radCart");
		
		
		radCart.addSelectionListener(new SelectionListener() {
		  

			public void widgetDefaultSelected(SelectionEvent e) {
				 txtCartNo.setEnabled(true);
				 txtCartNo.setFocus();
		    	 txtFromCartNo.setText("");
		    	 txtFromCartNo.setEnabled(false);
		    	 txtToCartNo.setText("");
		    	 txtToCartNo.setEnabled(false);
				
			}

			public void widgetSelected(SelectionEvent e) {
				 
				 txtCartNo.setEnabled(true);
				 txtCartNo.setFocus();
		    	 txtFromCartNo.setText("");
		    	 txtFromCartNo.setEnabled(false);
		    	 txtToCartNo.setText("");
		    	 txtToCartNo.setEnabled(false);
				
			}
		       });
		
		
		 YRCButtonBindingData radCartRangeBindingData  = new YRCButtonBindingData();
		 radCartRangeBindingData.setName("radCartRange");
		 radCartRangeBindingData.setTargetBinding("ProcessReprintBatchInput:Shipment/@Option");
		 radCartRangeBindingData.setCheckedBinding("3");
		 radCartRange.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, radCartRangeBindingData); 
		 radCartRange.setData(YRCConstants.YRC_CONTROL_NAME,"radCartRange");
		 
		 radCartRange.addSelectionListener(new SelectionListener() {
			  

				public void widgetDefaultSelected(SelectionEvent e) {
					 txtCartNo.setText("");
			    	 txtCartNo.setEnabled(false);
			    	 txtFromCartNo.setEnabled(true);
			    	 txtFromCartNo.setFocus();
			    	 txtToCartNo.setEnabled(true);
					
				}

				public void widgetSelected(SelectionEvent e) {
					 txtCartNo.setText("");
			    	 txtCartNo.setEnabled(false);
			    	 txtFromCartNo.setEnabled(true);
			    	 txtFromCartNo.setFocus();
			    	 txtToCartNo.setEnabled(true);
					
				}
			       });
		 
	
		 
		    
		    //binding for Print Button
			
			/*YRCButtonBindingData btnPrintbd = new YRCButtonBindingData();
			btnPrintbd.setName("btnPrint");
			btnPrintbd.setActionHandlerEnabled(true);
			btnPrintbd.setActionId(KOHLSReprintBatchPrintAction.ACTION_ID); 
			btnPrint.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnPrintbd);
			btnPrint.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrint");*/
		
		

	}
	
	public String getTxtBatchID() {
		return this.txtBatchID.getText();
	}
	
	public String getStatus(){
		return eleInput.getAttribute("Status");
	}
	
	public String getReprintFlag(){
		return eleInput.getAttribute("ReprintFlag");
	}
	
	public String getCmbPrinter() {
		return this.cmbPrinter.getText();
	}
	
	public String getTxtCartNo(){
		return this.txtCartNo.getText();
	}
	
	public String getTxtFromCartNo(){
		return this.txtFromCartNo.getText();
	}
	
	public String getTxtToCartNo(){
		return this.txtToCartNo.getText();
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
	public KOHLSReprintBatchScreenBehavior getBehavior() {
        return myBehavior;
    }
	
	public KOHLSReprintBatchScreenHelper getHelper() {
        return helper;
    }
	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }

	/**
	 * This method initializes cmpCart	
	 *
	 */
	private void createCmpCart() {
		GridData gridData1 = new GridData();
		gridData1.horizontalAlignment = GridData.CENTER;
		gridData1.grabExcessHorizontalSpace = false;
		gridData1.grabExcessVerticalSpace = false;
		gridData1.verticalAlignment = GridData.CENTER;
		GridData gridData2 = new GridData();
		gridData2.horizontalAlignment = GridData.FILL;
		gridData2.grabExcessHorizontalSpace = true;
		gridData2.verticalAlignment = GridData.CENTER;
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 5;
		cmpCart = new Composite(cmpCartInput, SWT.NONE);
		cmpCart.setLayout(gridLayout);
		cmpCart.setLayoutData(gridData1);
		radAllCarts = new Button(cmpCart, SWT.RADIO);
		radAllCarts.setText(YRCPlatformUI.getString("ALL_IN_BATCHID"));
		radAllCarts.setLayoutData(gridData2);
		Label filler7 = new Label(cmpCart, SWT.NONE);
		Label filler9 = new Label(cmpCart, SWT.NONE);
		Label filler = new Label(cmpCart, SWT.NONE);
		Label filler1 = new Label(cmpCart, SWT.NONE);
		radCart = new Button(cmpCart, SWT.RADIO);
		radCart.setText(YRCPlatformUI.getString("ENTER_CART_NUMBER"));
		txtCartNo = new Text(cmpCart, SWT.BORDER);
		Label filler8 = new Label(cmpCart, SWT.NONE);
		Label filler3 = new Label(cmpCart, SWT.NONE);
		Label filler4 = new Label(cmpCart, SWT.NONE);
		radCartRange = new Button(cmpCart, SWT.RADIO);
		radCartRange.setText(YRCPlatformUI.getString("ENTER_CART_RANGE"));
		txtFromCartNo = new Text(cmpCart, SWT.BORDER);
		lblTo = new Label(cmpCart, SWT.NONE);
		lblTo.setText("To");
		txtToCartNo = new Text(cmpCart, SWT.BORDER);
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
		gridData10.heightHint = 190;
		gridData10.grabExcessHorizontalSpace = true;
		gridData10.grabExcessVerticalSpace = false;
		gridData10.verticalAlignment = GridData.FILL;
		cmpTop = new Composite(pnlroot, SWT.NONE);
		cmpTop.setLayoutData(gridData10);
		cmpTop.setData(YRCConstants.YRC_CONTROL_NAME,
		"cmpTop");
		cmpTop.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createCmpTitle();
		cmpTop.setLayout(gridLayout5);
		createCmpReprintBatchID();
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
		lblReprintTitle.setText(YRCPlatformUI.getString("Reprint_Title"));
		lblReprintTitle.setLayoutData(gridData6);
		lblReprintTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
	}

	/**
	 * This method initializes cmpReprintBatchID	
	 *
	 */
	private void createCmpReprintBatchID() {
		GridData gridData14 = new GridData();
		gridData14.horizontalAlignment = GridData.END;
		gridData14.verticalAlignment = GridData.CENTER;
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.END;
		gridData13.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		gridData12.widthHint = 120;
		gridData12.grabExcessHorizontalSpace = false;
		gridData12.horizontalAlignment = GridData.FILL;
		gridData12.verticalAlignment = GridData.CENTER;
		gridData12.horizontalSpan = 2;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 6;
		gridLayout3.makeColumnsEqualWidth = false;
		GridData gridData3 = new GridData();
		gridData3.horizontalAlignment = GridData.FILL;
		gridData3.grabExcessHorizontalSpace = true;
		gridData3.verticalAlignment = GridData.FILL;
		cmpReprintBatchID = new Composite(cmpTop, SWT.NONE);
		cmpReprintBatchID.setLayoutData(gridData3);
		cmpReprintBatchID.setLayout(gridLayout3);
		
		cmpReprintBatchID.setData(YRCConstants.YRC_CONTROL_NAME,
		"cmpReprintBatchID");
		cmpReprintBatchID.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Composite");
		
		lblBatchID = new Label(cmpReprintBatchID, SWT.NONE);
		lblBatchID.setText("BatchID");
		lblBatchID.setLayoutData(gridData13);
		txtBatchID = new Text(cmpReprintBatchID, SWT.NONE);
		
		txtBatchID.setLayoutData(gridData12);
		if (eleInput!=null) {
			String strBatchID = eleInput.getAttribute("BatchID");
			txtBatchID.setText(strBatchID);
			txtBatchID.setEditable(false);
		}
		lblFiller = new Label(cmpReprintBatchID, SWT.NONE);
		lblFiller.setText("        ");
		if (eleInput==null) {
			lblSelectPrinter = new Label(cmpReprintBatchID, SWT.NONE);
			lblSelectPrinter.setText("Printer");
			lblSelectPrinter.setLayoutData(gridData14);
			createCmbPrinter();
		}
	}

	/**
	 * This method initializes cmbPrinter	
	 *
	 */
	private void createCmbPrinter() {
		cmbPrinter = new Combo(cmpReprintBatchID, SWT.READ_ONLY);
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
		gridData5.verticalAlignment = GridData.FILL;
		cmpCartInput = new Composite(cmpTop, SWT.NONE);
		createCmpCart();
		cmpCartInput.setLayout(gridLayout4);
		cmpCartInput.setLayoutData(gridData5);
		createCmpEmpty();
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
		btnPrint.setText(YRCPlatformUI.getString("Reprint_Batch"));
		btnPrint.setLayoutData(gridData9);
		btnPrint.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				myBehavior.validateInput();
			}
		});
	}

	/**
	 * This method initializes cmpEmpty	
	 *
	 */
	private void createCmpEmpty() {
		GridData gridData7 = new GridData();
		gridData7.horizontalAlignment = GridData.FILL;
		gridData7.verticalAlignment = GridData.FILL;
		cmpEmpty = new Composite(cmpCartInput, SWT.NONE);
		cmpEmpty.setLayout(new GridLayout());
		cmpEmpty.setLayoutData(gridData7);
	}
    
   
    
    
   
    

	   
}