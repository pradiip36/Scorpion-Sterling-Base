
/*
 * Created on Jun 09,2013
 * 
 */
package com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages;

import com.kohls.ibm.ocf.pca.rcp.KohlsApplicationInitializer;
import com.kohls.ibm.ocf.pca.util.KOHLSPCAConstants;
import com.yantra.yfc.rcp.YRCButtonBindingData;
import com.yantra.yfc.rcp.YRCComboBindingData;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCTextBindingData;
import com.yantra.yfc.rcp.YRCWizardBehavior;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.yfc.rcp.IYRCComposite;
import com.yantra.yfc.rcp.IYRCPanelHolder;
import com.yantra.yfc.rcp.YRCConstants;
import org.eclipse.swt.layout.GridData;
/**
 * @author srikaheb
 * @build # 200810210800 
 * Copyright © 2005, 2006 Sterling Commerce, Inc. All Rights Reserved.
 */
public class KOHLSBatchPrintReprintScreen extends Composite implements IYRCComposite{

	private KOHLSBatchPrintReprintScreenBehavior myBehavior;
	public static String FORM_ID = "com.kohls.ibm.ocf.pca.tasks.batchprint.wizardpages.KOHLSBatchPrintReprintScreen";
	private YRCWizardBehavior wizBehavior;
	private KOHLSBatchPrintReprintScreenHelper helper;
	Composite pnlroot;
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
	private Composite cmpCartInput = null;
	private Composite cmpPrintButton = null;
	private Composite cmpPrintBtn = null;
	private Button btnPrintOpr = null;
	private Button btnCloseOpr = null;
	private Composite cmpEmpty = null;
	private Label lblFiller = null;
	private Combo cmbPrinter = null;
	
	Element eleInput = null;
	Element elePrinters = null;
	public KOHLSBatchPrintReprintScreen(Composite parent, int style) {
		this(parent, style, null);
	}
	
	public KOHLSBatchPrintReprintScreen(Composite parent, int style, Object input) {
		
		super(parent, style);
		//eleInput = (Element) input;
		eleInput=(Element)(((HashMap) input).get("input"));
		elePrinters=(Element)(((HashMap) input).get("input1"));
		helper = new KOHLSBatchPrintReprintScreenHelper(this, FORM_ID, input);
		initialize();
		helper.screenInitialized();
		setBindingForComponents();
		populateCmbPrinter();//TODO this needs to be changed ideally by setmodel and getmodel;
		helper.bindingRegistered();
		myBehavior = new KOHLSBatchPrintReprintScreenBehavior(this, FORM_ID, input, helper);		
		helper.setBehavior(myBehavior);
	}
	
	public Composite getpnlroot() {
		return pnlroot;
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
	
		//btnPrintOpr.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "Button");
	
	}
	
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
		cmpTop.setData(YRCConstants.YRC_CONTROL_NAME,"cmpTop");
		cmpTop.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "TaskComposite");
		createCmpTitle();
		cmpTop.setLayout(gridLayout5);
		createCmpReprintBatchID();
		createCmpCartInput();
	}
	
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
		//lblReprintTitle = new Label(cmpTitle, SWT.NONE);
		//lblReprintTitle.setText("Reprint Batch");
		//lblReprintTitle.setLayoutData(gridData6);
		//lblReprintTitle.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE, "PanelHeader");
	}

	/**
	 * This method initializes cmpReprintBatchID	
	 *
	 */
	private void createCmpReprintBatchID() {
		GridData gridData13 = new GridData();
		gridData13.horizontalAlignment = GridData.END;
		gridData13.verticalAlignment = GridData.CENTER;
		GridData gridData12 = new GridData();
		//gridData12.widthHint = 120;
		gridData12.grabExcessHorizontalSpace = false;
		gridData12.verticalAlignment = GridData.CENTER;
		gridData12.horizontalAlignment = GridData.FILL;
		GridLayout gridLayout3 = new GridLayout();
		gridLayout3.numColumns = 3;
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
		lblBatchID.setText("BatchID: ");
		
		txtBatchID = new Text(cmpReprintBatchID, SWT.NONE);
		txtBatchID.setEditable(false);
		txtBatchID.setLayoutData(gridData12);
		String strBatchID = eleInput.getAttribute("BatchID");
		txtBatchID.setText(strBatchID);
		lblFiller = new Label(cmpReprintBatchID, SWT.NONE);
		lblFiller.setText("                     ");
		
		
		Label lblSelectPrinter = new Label(cmpReprintBatchID, SWT.NONE);
		lblSelectPrinter.setText("Printer: ");
		lblSelectPrinter.setLayoutData(gridData12);
		createCmbPrinter();
	}
	
	private void createCmbPrinter() {
		cmbPrinter = new Combo(cmpReprintBatchID, SWT.READ_ONLY);
//		cmbPrinter.add("One", 0);
//		cmbPrinter.add("Two", 1);
		cmbPrinter.addSelectionListener(new SelectionListener(){
			public void widgetSelected(SelectionEvent e) {
		        myBehavior.handleComboBoxSelection(cmbPrinter);
		    }
			public void widgetDefaultSelected(SelectionEvent e) {
		      
		    }
		});
	}
	
	private void populateCmbPrinter(){
		
		NodeList nOutputPrinterList = elePrinters.getElementsByTagName(KOHLSPCAConstants.E_DEVICE);
		String printServiceName = KohlsApplicationInitializer.getTerminalProperty(KOHLSPCAConstants.INI_PROPERTY_DEFAULT_PRINTER);
		Element mynode=null;
		int defInd=0;
		for (int i=0; i<nOutputPrinterList.getLength();i++) {
			mynode=(Element)nOutputPrinterList.item(i);
			cmbPrinter.add(mynode.getAttribute("DeviceId"), i);
			if (printServiceName.equalsIgnoreCase(mynode.getAttribute("DeviceId"))){
				defInd=i;
			}
		}
		cmbPrinter.select(defInd);
	}
	
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
		radAllCarts.setText(YRCPlatformUI.getString("ALL_SHIPMENTS_IN_BATCH"));
		radAllCarts.setSelection(true);
		radAllCarts.setLayoutData(gridData2);
		Label filler7 = new Label(cmpCart, SWT.NONE);
		Label filler9 = new Label(cmpCart, SWT.NONE);
		Label filler = new Label(cmpCart, SWT.NONE);
		Label filler1 = new Label(cmpCart, SWT.NONE);
		radCart = new Button(cmpCart, SWT.RADIO);
		radCart.setText(YRCPlatformUI.getString("ENTER_SHIP_SEQUENCE"));
		txtCartNo = new Text(cmpCart, SWT.BORDER);
		Label filler8 = new Label(cmpCart, SWT.NONE);
		Label filler3 = new Label(cmpCart, SWT.NONE);
		Label filler4 = new Label(cmpCart, SWT.NONE);
		radCartRange = new Button(cmpCart, SWT.RADIO);
		radCartRange.setText(YRCPlatformUI.getString("ENTER_RANGE_SEQUENCE"));
		txtFromCartNo = new Text(cmpCart, SWT.BORDER);
		lblTo = new Label(cmpCart, SWT.NONE);
		lblTo.setText("To");
		txtToCartNo = new Text(cmpCart, SWT.BORDER);
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
		gridLayout8.numColumns = 3;
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
		
		btnPrintOpr = new Button(cmpPrintBtn, SWT.NONE);
		btnPrintOpr.setLayoutData(gridData9);
		//btnPrint.setText(YRCPlatformUI.getString("Reprint_Batch"));
		btnPrintOpr.setText("Print");
		btnPrintOpr.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				myBehavior.validateInput();
			}
		});
		
//		lblFiller = new Label(cmpPrintBtn, SWT.NONE);
//		lblFiller.setText("             ");
//		btnCloseOpr = new Button(cmpPrintBtn, SWT.NONE);
//		btnCloseOpr.setLayoutData(gridData9);
//		//btnPrint.setText(YRCPlatformUI.getString("Reprint_Batch"));
//		btnCloseOpr.setText("Close");		
//		btnCloseOpr.addSelectionListener(new SelectionAdapter(){
//			public void widgetSelected(SelectionEvent e) {
//				myBehavior.closePopup();
//			}
//		});
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
	
	public void setBindingForComponents() {
		
		YRCComboBindingData bdCmbMPrinterID = new YRCComboBindingData();
	 	bdCmbMPrinterID.setName("cmbPrinter");
	 	bdCmbMPrinterID.setSourceBinding("elePrinters:Devices/Device/@DeviceId");
	 	bdCmbMPrinterID.setCodeBinding("DeviceId");
	 	bdCmbMPrinterID.setTargetBinding("ProcessBatchPrintInput:Shipment/@PrinterID");
	 	bdCmbMPrinterID.setListBinding("elePrinters:Devices/Device");
	 	bdCmbMPrinterID.setDescriptionBinding("@DeviceId");
	 	bdCmbMPrinterID.setMandatory(true);
	 	cmbPrinter.setData(YRCConstants.YRC_COMBO_BINDING_DEFINATION, bdCmbMPrinterID);
	 	cmbPrinter.setData(YRCConstants.YRC_CONTROL_NAME,"cmbPrinter");
	 	
	 	//binding for text boxes and cart radiobuttons
	 	
		YRCTextBindingData txtCartNoBindingData = new YRCTextBindingData();
	    txtCartNoBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@CartNo");
	    txtCartNoBindingData.setName("txtCartNo");
	    txtCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtCartNoBindingData);
	    txtCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtCartNo");
	    txtCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
	    txtCartNoBindingData.setDataType("PositiveQuantity");
	    
	    YRCTextBindingData txtFromCartNoBindingData = new YRCTextBindingData();
	    txtFromCartNoBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@FromCartNo");
	    txtFromCartNoBindingData.setName("txtFromCartNo");
	    txtFromCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtFromCartNoBindingData);
	    txtFromCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtFromCartNo");
	    txtFromCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
	    txtFromCartNoBindingData.setDataType("PositiveQuantity");
	    
		YRCTextBindingData txtToCartNoBindingData = new YRCTextBindingData();
		
	    txtToCartNoBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@ToCartNo");
	    txtToCartNoBindingData.setName("txtToCartNo");
	    txtToCartNo.setData(YRCConstants.YRC_TEXT_BINDING_DEFINATION,txtToCartNoBindingData);
	    txtToCartNo.setData(YRCConstants.YRC_CONTROL_NAME,"txtToCartNo");
	    txtToCartNo.setData(YRCConstants.YRC_CONTROL_CUSTOMTYPE,"DisplayText");
	    txtToCartNoBindingData.setDataType("PositiveQuantity");
	 	   
	    YRCButtonBindingData radAllCartsBindingData  = new YRCButtonBindingData();
		radAllCartsBindingData.setName("radAllCarts");
		radAllCartsBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@Option");
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
	    radCartBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@Option");
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
		 radCartRangeBindingData.setTargetBinding("ProcessBatchPrintInput:Shipment/@Option");
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
			
			/*YRCButtonBindingData btnPrintOprbd = new YRCButtonBindingData();
			btnPrintOprbd.setName("btnPrint");
			btnPrintOprbd.setActionHandlerEnabled(true);
			btnPrintOprbd.setActionId(KOHLSBatchPrintReprintPerformAction.ACTION_ID); 
			btnPrintOpr.setData(YRCConstants.YRC_BUTTON_BINDING_DEFINATION, btnPrintOprbd);
			btnPrintOpr.setData(YRCConstants.YRC_CONTROL_NAME,"btnPrintOpr");*/
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
	 
	void enableField(String ctrlName, boolean enable) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(null != ctrl)
			ctrl.setEnabled(enable);
	}

	void excludeField(String ctrlName, boolean exclude) {
		Control ctrl = myBehavior.getControl(ctrlName);
		if(ctrl != null) {
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
	public KOHLSBatchPrintReprintScreenBehavior getBehavior() {
        return myBehavior;
    }
	
	public KOHLSBatchPrintReprintScreenHelper getHelper() {
        return helper;
    }
	public YRCWizardBehavior getWizardBehavior() {
        return this.wizBehavior;
    }
	
    public void setWizBehavior(YRCWizardBehavior wizBehavior) {
        this.wizBehavior = wizBehavior;
    }      
}