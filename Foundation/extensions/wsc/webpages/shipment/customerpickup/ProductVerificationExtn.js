scDefine([
	"scbase/loader!dojo/_base/declare", 
	"scbase/loader!extn/shipment/customerpickup/ProductVerificationExtnUI", 
	"scbase/loader!isccs/utils/UIUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/BaseUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils", 
	"scbase/loader!sc/plat/dojo/utils/WizardUtils", 
	"scbase/loader!wsc/utils/CustomerPickUpUtils",
	"scbase/loader!isccs/utils/WizardUtils", 
	"scbase/loader!extn/utils/ScanUtils", 
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!extn/utils/ScanUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!sc/plat/dojo/utils/BundleUtils"],
    function (
        _dojodeclare,
        _extnProductVerificationExtnUI, 
		_isccsUIUtils, 
		_scScreenUtils, 
		_scBaseUtils, 
		_scModelUtils, 
		_scWidgetUtils, 
		_scWizardUtils, 
		_wscCustomerPickUpUtils, 
		_isccsWizardUtils, 
		scanUtils, 
		_scEventUtils,
		_bopusUtils,
		_scEditorUtils,
		_scBundleUtils
    ) {
        return _dojodeclare("extn.shipment.customerpickup.ProductVerificationExtn", [_extnProductVerificationExtnUI], {
        	//custom code here

	/******************************************************
	 * @author: ???/Adam Dunmars
	 * @description: This subscriber function happens after the 
	 * OOB screen initialization occurs. It
	 * 		1) Hides the close button of the wizard
	 * 
	 * TODO: Finish this description 'n function
	 * TODO: Make most of these mashups init mashups:/ ~Adam.
	 *****************************************************/
	extInitialize: function (event, bEvent, ctrl, args) {
		var wizard = this.getWizardForScreen(this);
		_isccsWizardUtils.hideNavigationalWidget(wizard, 'closeBttn', true);

		var pickUpOrderDetailsModel = null;
		pickUpOrderDetailsModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
		var shipmentInfoInput = null;
		shipmentInfoInput = _scModelUtils.createNewModelObjectWithRootKey("Shipment");
		shipmentInfoInput.Shipment = {};
		// grab the first order no available
	    for (var i = 0; i < pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine.length; i++) {
	        if (!_scBaseUtils.isVoid(pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i])) {
	            shipmentInfoInput.Shipment.OrderNo = pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i].OrderNo;
	            break;
	        }
	    }
	    
	    _scScreenUtils.setModel(this, "extn_shipmentInfo_input", shipmentInfoInput, null);
	    var holdLocation = this.getHoldLocation();
	    
	    _scWidgetUtils.setValue(this, 'extn_datalabel_HoldLocation', holdLocation, false);
	    
	    var input = {};
	    input.Shipment = {};
	    input.Shipment.ShipmentKey = pickUpOrderDetailsModel.Shipment.ShipmentKey;
	    _isccsUIUtils.callApi(this, input, "extn_getPickupInfo_mashup", null);
		
	    _isccsUIUtils.callApi(this, input, "extn_getShipmentDetails_receipt_suspend", null);	

		// change the label on the page
	   var wizard = this.getWizardForScreen(this);
	   _isccsWizardUtils.setLabelOnNavigationalWidget(wizard, 'closeBttn', "Void");
	   

	},
	
    /******************************
     * @author: ???
     * @description: saves the page
     ******************************/
    save: function(event, bEvent, ctrl, args) {
        var pickUpOrderLineVerificationModel = null;
        var ShortageResolutionModel = null;
        var args = null;
        var initialInputData = null;
        //var baseResolutionModel = null;
        var baseModel;
        var pluModel;

        args = _scBaseUtils.getNewBeanInstance();
        initialInputData = _scScreenUtils.getInitialInputData(_isccsUIUtils.getEditorFromScreen(this));

        baseModel = _scScreenUtils.getTargetModel(this, "ItemScan_Output", null);
        pluModel = _scScreenUtils.getModel(this, "ShipmentLinesForPickup_input", null);	

        pickUpOrderLineVerificationModel = this.addPluModel(baseModel, pluModel);

        _isccsUIUtils.setWizardModel(this, "pickUpOrderLinesResolution_input", pickUpOrderLineVerificationModel, null);

        //baseResolutionModel = _wscCustomerPickUpUtils.getShortageResolutionModel(pickUpOrderLineVerificationModel);
        //ShortageResolutionModel = this.getResolutionModel(baseResolutionModel);

        ShortageResolutionModel = _wscCustomerPickUpUtils.getShortageResolutionModel(pickUpOrderLineVerificationModel);
        
        var hasShortages = null;
        hasShortages = _scModelUtils.getStringValueFromPath("hasShortages", ShortageResolutionModel);
        var hasConfirmed = null;
        hasConfirmed = _scModelUtils.getStringValueFromPath("hasConfirmed", ShortageResolutionModel);
        var hasShipmentsWithNoPickItems = null;
        hasShipmentsWithNoPickItems = _scModelUtils.getStringValueFromPath("hasShipmentsWithNoPickItems", ShortageResolutionModel);
        var hasCancelledLines = null;
        hasCancelledLines = _scModelUtils.getStringValueFromPath("hasCancelledLines", ShortageResolutionModel);
        if (_scBaseUtils.equals(hasShortages, "Y")) {
            _isccsUIUtils.setWizardModel(this, "ShortageResolutionModel", ShortageResolutionModel, null);
            _scEventUtils.fireEventToParent(this, "GoToResolution", args);
            initialInputData = _wscCustomerPickUpUtils.addModelToModelPath("ShortageResolutionModel", ShortageResolutionModel, initialInputData);
            initialInputData = _wscCustomerPickUpUtils.appendModelToInitialInputData(this, initialInputData);
            _scBaseUtils.setAttributeValue("argumentList.model", initialInputData, args);
            var wizardInstance = null;
            wizardInstance = _isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor());
            _scScreenUtils.setInitialInputData(wizardInstance, initialInputData);
            this.gotoNextScreen();
        } else {
            _isccsUIUtils.setWizardModel(this, "ShortageResolutionModel_Resolved", ShortageResolutionModel, null);
            _scEventUtils.fireEventToParent(this, "GoToSummary", args);
            initialInputData = _wscCustomerPickUpUtils.addModelToModelPath("ShortageResolutionModel_Resolved", ShortageResolutionModel, initialInputData);
            initialInputData = _wscCustomerPickUpUtils.appendModelToInitialInputData(this, initialInputData);
            _scBaseUtils.setAttributeValue("argumentList.model", initialInputData, args);
            var wizardInstance = null;
            wizardInstance = _isccsUIUtils.getCurrentWizardInstance(_scEditorUtils.getCurrentEditor());
            _scScreenUtils.setInitialInputData(wizardInstance, initialInputData);
            if (_scBaseUtils.equals(hasCancelledLines, "Y")) {
                this.updateCancelReasonCode();
            } else {
                this.gotoNextScreen();
            }
        }
    },

    /***********************************
     * @author: Maseeh Sabir
     * description: INSERT DESCRIPTION HERE
     ***********************************/
    addPluModel: function(modelBase, modelPlu){
		for(var i = 0; i<modelBase.ShipmentLines.ShipmentLine.length; i++) {
			var pluLine = modelPlu.ShipmentLines.ShipmentLine[i];
			var pickedQty = Number(pluLine.PickedQty);
			if(!_scBaseUtils.isVoid(pluLine.OrderLine.HoldReasonCode) && pluLine.OrderLine.HoldReasonCode == 'CALLBACK')
			{
				pluLine.ShortageReason = "Callback";
				pluLine.PickedQty = Number("0");
				modelBase.ShipmentLines.ShipmentLine[i] = pluLine;
			}
		}
		return modelBase;
    },
    
    /*******************************
     * @author: Maseeh Sabir(?)
     * @description: INSERT DESCRIPTION HERE!
     * TODO: this code is not needed after drop 12 but we are keeping it for precautionary reason
     ******************************
    getResolutionModel: function(baseModel)
    {
		var mIndex = 0;
		var sIndex = baseModel.ShortageShipmentLines.ShipmentLine.length;
		var ShipmentLine = [];
		
		for(var i = 0; i<baseModel.ConfirmedShipmentLines.ShipmentLine.length; i++) 
		{
			var shipLine = baseModel.ConfirmedShipmentLines.ShipmentLine[i];
			if(!_scBaseUtils.isVoid(shipLine.OrderLine.HoldReasonCode) && shipLine.OrderLine.HoldReasonCode == "CALLBACK")
			if(shipLine.ItemID == "100001")	
			{	
				baseModel.ConfirmedShipmentLines.ShipmentLine[i] = null;
				shipLine.PickeQty = Number("0");		
				baseModel.ShortageShipmentLines.ShipmentLine[sIndex] = shipLine;
				sIndex++;
			}
		}

		for(var i = 0; i<baseModel.ConfirmedShipmentLines.ShipmentLine.length; i++) {
			if(!_scBaseUtils.isVoid(baseModel.ConfirmedShipmentLines.ShipmentLine[i])) {
				ShipmentLine[mIndex] = baseModel.ConfirmedShipmentLines.ShipmentLine[i];
				mIndex++;
			}
		}
	
		baseModel.ConfirmedShipmentLines.ShipmentLine = ShipmentLine;
		return baseModel;
    }, */

        	
	/************************************
 	 * @author: Adam Dunmars
	 * @description: This function concatenate's the customer's
	 * name using 
	 * 	1) Shipment/BillToAddress/@FirstName
	 *  2) Shipment/BillToAddress/@LastName
	 **************************************/
	concatCustomerName : function(dataValue, screen, widget, namespace, modelObj, options) {
		var pickUpOrderDetailsModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
		var firstName = pickUpOrderDetailsModel.Shipment.BillToAddress.FirstName;
		var lastName = pickUpOrderDetailsModel.Shipment.BillToAddress.LastName;
		return firstName + " " + lastName;
	},

	/**********************************
	 * @author: Maseeh Sabir (?)
	 * TODO: Enter description here
	 **********************************/
	voidOrder: function() {
    	var modelOutput = _scScreenUtils.getModel(this, "extn_getShipmentDetailsForReceipt_suspend");
    	var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_suspend");
    	var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_suspend");
    	var diamondDataModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_diamond");
		
		var wizard = this.getWizardForScreen(this);
		
		modelOutput.diamondModel = diamondDataModel;
		modelOutput.ReceiptType = "voidpickup";
		
		_bopusUtils.printReceipt(wizard, modelOutput, commonModel, storeInfo);
	},

	/**********************************
	 * @author: Maseeh Sabir (?)
	 * TODO: Enter description here
	 **********************************/
    addEJEntryCustomerVerified: function() {			
		var mashInput = {};

		var shipmentModel = _scScreenUtils.getModel(this, "ShipmentLinesForPickup_input");
		mashInput.ShipmentStoreEvents = {};
		
		mashInput.ShipmentStoreEvents.OrderNo = shipmentModel.ShipmentLines.ShipmentLine[0].OrderNo;
		mashInput.ShipmentStoreEvents.EventType = "Customer Verified";
		mashInput.ShipmentStoreEvents.OrderHeaderKey = shipmentModel.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
		mashInput.ShipmentStoreEvents.ShipmentKey = shipmentModel.ShipmentLines.ShipmentLine[0].ShipmentKey;

		_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransCustomerVerified", null);

	},

	/**********************************
	 * @author: Maseeh Sabir (?)
	 * @description: This function gets called when an order gets suspended. It adds an entry to
	 *       Store event hang off table
	 **********************************/
    addEJEntryOrderSuspended: function() {			
		var shipmentModel = _scScreenUtils.getModel(this, "ShipmentLinesForPickup_input");
		var mashInput = {};
		mashInput.ShipmentStoreEvents = {};
		console.log(shipmentModel);
		mashInput.ShipmentStoreEvents.OrderNo = shipmentModel.ShipmentLines.ShipmentLine[0].OrderNo;
		mashInput.ShipmentStoreEvents.EventType = "Pickup Suspended";
		mashInput.ShipmentStoreEvents.OrderHeaderKey = shipmentModel.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
		mashInput.ShipmentStoreEvents.ShipmentKey = shipmentModel.ShipmentLines.ShipmentLine[0].ShipmentKey;

		_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransCustomerVerified", null);
	},

	/****************************************************
	 * @author: Adam Dunmars
	 * @description: This function is called by the physical barcode object to set the scanned text field automatically.
	 * After setting the value, the scanProduct function is fired immediately so that the user doesn't have
	 * to click the "Scan" button.
	 * TODO: Add global usage of scannedBarcode
	 ****************************************************/
	setBarcode: function(barcodeString) {
		console.log(barcodeString);
		var screen = this;
		var uId = "scanProductIdTxt";
		var barcode = barcodeString.BarCodeData;
		var dirty = false;
		
		sc.plat.dojo.utils.WidgetUtils.setValue(screen, uId, barcode, dirty); 
		this.scanProduct();
	},
		
	/****************************************************
	 * @author: Adam Dunmars
	 * @description: This subscriber overwrites the OOB subscriber to do a validation check on the entered barcode to ensure
	 * that it is only digits as per Kohl's Barcode Algorithm translation.
	 ******************************************************/
	scanProduct: function() {
        var barCodeModel = null;
        var barCodeData = null;
        barCodeModel = _scScreenUtils.getTargetModel(this, "translateBarCode_input", null);
	   
		barCodeData = _scModelUtils.getStringValueFromPath("BarCode.BarCodeData", barCodeModel);

		var isnum = /^\d+$/.test(barCodeData);

    	if (_scBaseUtils.isVoid(barCodeData)) {
    		_scScreenUtils.showErrorMessageBox(this, "Please enter a value into the scan field.", null, null, null);
    	} else if(!isnum){
    		_scScreenUtils.showErrorMessageBox(this, "Please ensure that only digits are entered for the barcode.", null, null, null);
    	} else {
    		_isccsUIUtils.callMashup(this, barCodeModel, "translateBarCode", null);
            var eventDefn = null;
            eventArgs = _scBaseUtils.getNewBeanInstance();
            eventDefn = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.setAttributeValue("argumentList", _scBaseUtils.getNewBeanInstance(), eventDefn);
    	}
    	var eventDefn = _scBaseUtils.getNewBeanInstance();
        var eventArgs = _scBaseUtils.getNewBeanInstance();
    	_scEventUtils.fireEventInsideScreen(this, "resetBarCodeTextField", eventDefn, eventArgs);
	},

	
	/******************************************************
	 * @author: ???
	 * TODO: Add description here
	 *****************************************************/
	getWizardForScreen: function ( /*Screen*/ screen) {
        if (screen.wizardUI) {
            return screen;
        } else {
            var force = true;
            var parentScreen = screen.getOwnerScreen(force);
            if (parentScreen) {
                return this.getWizardForScreen(parentScreen);
            }
        }
        return null;
    },

    /******************************************************
	 * @author: ???
	 * @description: ???
	 * TODO: orderNo variable isn't used? ~Adam
	 *****************************************************/
    suspendPickup: function (event, bEvent, ctrl, args) {
        // save the shipment information
        var pickUpOrderDetailsModel = null;
        pickUpOrderDetailsModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
	    var orderNo = null;
	    var totalQuantity = 0;
        var input = {};
        input.Shipment = {};
        input.Shipment.Extn = {};
        // set the suspended flag
        input.Shipment.Extn.ExtnSuspendedPickup = 'Y';
        input.Shipment.ShipmentKey = pickUpOrderDetailsModel.Shipment.ShipmentKey;
        input.Shipment.ShipmentLines = {};
        input.Shipment.ShipmentLines.ShipmentLine = [];
        // loop through all the child screens to pull the value
        for (var i = 0; i < pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine.length; i++) {
            // get the current child screen
            var childScreen = _scScreenUtils.getChildScreen(this, "CustomerPickUp_PickupOrderLineDetails" + i);
            if (!isNaN(_scWidgetUtils.getValue(childScreen, 'txtScannedQuantity')) && _scWidgetUtils.getValue(childScreen, 'txtScannedQuantity') > 0) {
                var ShipmentLine = {};
                ShipmentLine.ShipmentLineKey = _scWidgetUtils.getValue(childScreen, 'ShipmentLineKey');
                ShipmentLine.Extn = {};
                ShipmentLine.Extn.ExtnSuspendedPickupQty = _scWidgetUtils.getValue(childScreen, 'txtScannedQuantity');
                input.Shipment.ShipmentLines.ShipmentLine.push(ShipmentLine);
            }
			// set the information for the suspend receipt
			orderNo = pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i].OrderNo;
			totalQuantity += pickUpOrderDetailsModel.Shipment.ShipmentLines.ShipmentLine[i].Quantity * 1;
        }
        _isccsUIUtils.callApi(this, input, 'extn_saveSuspendedPickup_mashup', null);

        this.printSuspendReceipt();

        this.addEJEntryOrderSuspended();

        // close the screen
        var wizardScreen = this.getWizardForScreen(this);
        _scWizardUtils.closeWizard(wizardScreen);
    },

    /******************************************************
	 * @author: ???(Maseeh Sabir)
	 * @description: INSERT DESCRIPTION HERE
	 * TODO: This is making an additional call to getShipmentDetails 
	 * AND getShipmentList when not needed...
	 *****************************************************/
	printSuspendReceipt: function() {
	    var modelOutput = _scScreenUtils.getModel(this, "extn_getShipmentDetailsForReceipt_suspend");
	    var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_suspend");
	    var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_suspend");
		//var receiptNoModel = _scScreenUtils.getModel(this, "extn_getShipmentLisrForReceiptID_suspend");

	    modelOutput.ReceiptType = "suspend";
	    
        _bopusUtils.printReceipt(null, modelOutput, commonModel, storeInfo);
        _bopusUtils.printReceipt(null, modelOutput, commonModel, storeInfo);
	},

	/******************************************************
	 * @author: ???(Maseeh Sabir)
	 * @description: INSERT DESCRIPTION HERE
	 * TODO: This is making an additional call to getShipmentDetails 
	 * AND getShipmentList when not needed...
	 *****************************************************/
	printCallbackReceipt: function(productDetailModel) {
		var modelOutput = _scScreenUtils.getModel(this, "extn_getShipmentDetailsForReceipt_suspend");
		var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_suspend");
    	var storeInfo = _scScreenUtils.getModel(this,"extn_shipNode_info_suspend");
		var receiptNoModel = _scScreenUtils.getModel(this, "extn_getShipmentLisrForReceiptID_suspend");

		var recallModel = {};
		var itemID = productDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID;
		
		for(var i = 0; i<modelOutput.Shipment.ShipmentLines.ShipmentLine.length; i++) 
		{
			var currentLine = modelOutput.Shipment.ShipmentLines.ShipmentLine[i];			
			if (itemID == currentLine.ItemID) 
			{
				recallModel.ItemID = modelOutput.Shipment.ShipmentLines.ShipmentLine[i].OrderLine.ItemDetails.ItemAliasList.ItemAlias[0].AliasValue;
				recallModel.ItemDesc = currentLine.ItemDesc;
				recallModel.Department = currentLine.OrderLine.ItemDetails.CategoryList.Category[0].CategoryID;
				recallModel.ProductClass = currentLine.ProductClass;
			}
		}
		
		modelOutput.RecallItem = recallModel;
		modelOutput.ReceiptType = "callback";
		
		_bopusUtils.printReceipt(null, modelOutput, commonModel, storeInfo);
	},

	/******************************************************
	 * @author: Maseeh Sabir / Adam Dunmars
	 * @description: INSERT DESCRIPTION HERE
	 * TODO: Create isProductValid function inside of BOPUS Utils
	 * until they finish working on the OOB one. ~Adam
	 * TODO 2: Due to change in Base Product first TODO may be obsolete
	 *****************************************************/
    updateProductQuantity: function (translateBarCodeOutputModel) {
        var productDetailModel = null;
        var selectedShipmentLinesModel = null;
        var isProductValid = false;
        var isProductRecall = false;
        productDetailModel = translateBarCodeOutputModel;
        selectedShipmentLinesModel = _scScreenUtils.getModel(this, "ShipmentLinesForPickup_input");
        isProductValid = true;//_wscCustomerPickUpUtils.isScannedProductValid(selectedShipmentLinesModel, productDetailModel);
        if(_scBaseUtils.isVoid(productDetailModel) 
        	|| _scBaseUtils.isVoid(productDetailModel.BarCode)
        	|| _scBaseUtils.isVoid(productDetailModel.BarCode.Translations)
        	|| _scBaseUtils.isVoid(productDetailModel.BarCode.Translations.TotalNumberOfRecords)
        	|| !_scBaseUtils.equals(productDetailModel.BarCode.Translations.TotalNumberOfRecords, "1")) {
        	isProductValid = false;
        }
        if (_scBaseUtils.isBooleanTrue(isProductValid)){	
        	isProductRecall = this.checkProductRecall(selectedShipmentLinesModel, productDetailModel);	
        	if(_scBaseUtils.isBooleanTrue(isProductRecall)) {
				//do recall business
				this.updateShipmentLineOnCallback(this, productDetailModel, selectedShipmentLinesModel);
			} else {
	            _wscCustomerPickUpUtils.updateShipmentLineContainer(this, productDetailModel, this.shipmentLineContainerList);
		            // check for configurable prompt messages
	            var input = {};
	            input.Item = {};
	            input.Item.ItemID = productDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID;
	            input.Item.UnitOfMeasure = 'EACH';
	            _isccsUIUtils.callApi(this, input, 'extn_getConfigurablePrompts_mashup', null);
	            this.hideShipLinesUndoButton();
	            this.showUndoButton();
			}
        } else {
        	var errorMessage = _scBundleUtils.getString("NoProductFound");
        	var textObj = {};
        	textObj.OK = "Ok";
        	var errorMessageCallback = "errorMessageCallback";
            _scScreenUtils.showErrorMessageBox(this, errorMessage, errorMessageCallback, textObj, null);
        }
    },
    
    /*******************************************
     * @author: Adam Dunmars
     * @description: This is a placeholder callback function that 
     * stops error message from displaying in the log
     * after selecting OK from an error message dialog.
     * TODO: See if this exists in a utility function or
     * add to BopusUtils.
     * TODO 2: Add input parameters.
     ******************************************/
    errorMessageCallback : function() {
    	
    },

    /******************************************************
	 * @author: ??? (Maseeh Sabir?)
	 * @description: INSERT DESCRIPTION HERE
	 *****************************************************/
    checkProductRecall: function(shipmentLines, productDetail) {
		var isCallback = false;
		var scannedItemID = null;
		scannedItemID = productDetail.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID;
			
		//only for unit testing		
		//if(scannedItemID == "100001") return true;
		
		var container = this.getWidgetByUId("PickUpItemContainer");

		var listOfChildren = container.getChildren(container);

		for (var i=0;i<listOfChildren.length;i++)  {
			var child = listOfChildren[i];
			var shipmentLineOutput = _scScreenUtils.getModel(child, "ShipmentLine", null);

			if(_scBaseUtils.stringEquals(scannedItemID,shipmentLineOutput.ShipmentLine.OrderLine.ItemDetails.ItemID)) {
				if(!_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.OrderLine.HoldReasonCode) && shipmentLineOutput.ShipmentLine.OrderLine.HoldReasonCode == "CALLBACK") {
					isCallback = true;
					break;
				}		
			}
		}
		
		return isCallback;
	},	

	/******************************************************
	 * @author: ??? (Maseeh Sabir?)
	 * @description: ADD DESCRIPTION HERE
	 *****************************************************/
    updateShipmentLineOnCallback: function(screen, productScanDetailModel, shipmentLineContainerList) {
		
		var type = "wsc.shipment.customerpickup.PickupOrderLineDetails";
		var isOverragePresent = false, isProductFound= false;
		var lastProductScannedModel = {};
		lastProductScannedModel.ProductConsumed = "N";
		lastProductScannedModel.OrderLine = {};

		var childLine = null;
		
		var PickAll_Input = {};
		var pickedQty=Number("0");
		var scannedItemID = "", inventoryUOM = "", kitCode= "", productClass = "",scannedItemPresent = false;
		
		var barCodeDataModel = productScanDetailModel.BarCode;
		
		if(!_scBaseUtils.isVoid(productScanDetailModel) && !_scBaseUtils.isVoid(productScanDetailModel.BarCode) && !_scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations))  {
		
			if(_scBaseUtils.equals("0",productScanDetailModel.BarCode.Translations.TotalNumberOfRecords)) {
				textObj = {};
				textObj.OK=_scScreenUtils.getString(screen,"Ok");
				_scScreenUtils.showErrorMessageBox(screen,_scScreenUtils.getString(screen,"ItemNotFound"),"", textObj,"");
				return;
			}  else if(!_scBaseUtils.equals("1",productScanDetailModel.BarCode.Translations.TotalNumberOfRecords)) {
				textObj = {};
				textObj.OK=_scScreenUtils.getString(screen,"Ok");
				_scScreenUtils.showErrorMessageBox(screen,_scScreenUtils.getString(screen,"MultipleItemsFound"),"", textObj,"");
				return;
			} 
				
			scannedItemPresent = true;
				
		} else {
			textObj = {};
			textObj.OK=_scScreenUtils.getString(screen,"Ok");
			_scScreenUtils.showErrorMessageBox(screen,_scScreenUtils.getString(screen,"ItemNotFound"),"", textObj,"");
				return;
		}
		
		if(scannedItemPresent && !_scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation) && !_scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation.length) && !_scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo) && !_scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID)) {
			scannedItemID = productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID;
			inventoryUOM =  productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.InventoryUOM;
			kitCode =  productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.KitCode;
			productClass =  productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ProductClass;
		} else {
			textObj = {};
			textObj.OK=_scScreenUtils.getString(screen,"Ok");
			_scScreenUtils.showErrorMessageBox(screen,_scScreenUtils.getString(screen,"ItemNotFound"),"", textObj,"");
				return;
		}

		//console.log("scannedItemID : ",scannedItemID);
		
		var container = screen.getWidgetByUId("PickUpItemContainer");
		//console.log("container : ",container);
		var listOfChildren = container.getChildren(container);
		//console.log("listOfChildren : ",listOfChildren);
		for (var i=0;i<listOfChildren.length;i++) {
			var child = listOfChildren[i];
			//console.log("FireChildEvent 1 : ",child.id);
			var shipmentLineOutput = _scScreenUtils.getModel(child, "ShipmentLine", null);
			//var shipmentLineOutput = _scScreenUtils.getModel(child, "ShipmentLine");
			//console.log("shipmentLineOutput : ",shipmentLineOutput);				
			
			if(!_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.ItemID) && !_scBaseUtils.stringEquals(scannedItemID,shipmentLineOutput.ShipmentLine.ItemID)) {
				continue;
			}
			
			if(!_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.UnitOfMeasure) && !_scBaseUtils.stringEquals(inventoryUOM,shipmentLineOutput.ShipmentLine.UnitOfMeasure)) {
				continue;
			}
			
			if(!_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.KitCode) && !_scBaseUtils.stringEquals(kitCode,shipmentLineOutput.ShipmentLine.KitCode)) {
				continue;
			}
				
			if(!_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.ProductClass) && !_scBaseUtils.stringEquals(productClass,shipmentLineOutput.ShipmentLine.ProductClass)) {
				continue;
			}
				
			isProductFound = true;
			
			if(isProductFound) {
				if(_scBaseUtils.isVoid(shipmentLineOutput.ShipmentLine.PickedQty)) {
					pickedQty=Number("0");
				} else {
					pickedQty=Number(shipmentLineOutput.ShipmentLine.PickedQty);
				}
					
				//console.log("pickedQty : ",pickedQty);
				
				if(Number(shipmentLineOutput.ShipmentLine.Quantity) < (Number(pickedQty) + Number("1"))) {
					continue;
				} else {
					
					lastProductScannedModel.ShipmentLineKey = shipmentLineOutput.ShipmentLine.ShipmentLineKey;
					lastProductScannedModel.ProductConsumed = "Y";
					lastProductScannedModel.screenUId = child.uId;
					//PickAll_Input.Quantity = Number(pickedQty) + Number("1");										
					//child.setModel("PickAll_Input", PickAll_Input, "");
					lastProductScannedModel.OrderLine = shipmentLineOutput.ShipmentLine.OrderLine;
					//_scEventUtils.fireEventInsideScreen(child, "updateScannedProductQuantity", null, {});
					
				}
				_scScreenUtils.showErrorMessageBox(this, "Item is on Legal Callback", null, null, null);
				_scWidgetUtils.showWidget(child,"extn_image_shortage",false,"");
				this.deActivateLine(child, productScanDetailModel);
				childLine = child;	
									
				break;
			}
		}

		if(isProductFound && _scBaseUtils.stringEquals("N",lastProductScannedModel.ProductConsumed)) {
			textObj = {};
			textObj.OK=_scScreenUtils.getString(screen,"Ok");
			_scScreenUtils.showErrorMessageBox(child,_scScreenUtils.getString(screen,"ItemOverrage"),"", textObj,"");			
		} else if(!isProductFound) {
			textObj = {};
			textObj.OK=_scScreenUtils.getString(screen,"Ok");
			_scScreenUtils.showErrorMessageBox(screen,_scScreenUtils.getString(screen,"ItemNotInShipment"),"", textObj,"");
		} else if(isProductFound && _scBaseUtils.stringEquals("Y",lastProductScannedModel.ProductConsumed)) {
			//console.log("Updating Last Product Scanned Model");
			_scWidgetUtils.showWidget(screen,"lastScannedProductDetailPanel",false,null);
			var options = _scBaseUtils.getNewBeanInstance();
			_scBaseUtils.addBeanValueToBean("lastProductScannedModel",lastProductScannedModel,options);
			var childScreen = _scScreenUtils.getChildScreen(screen, "lastProductScannedDetailsScreenRef");
			//console.log("childScreen : ",childScreen);
			if(_scBaseUtils.isVoid(childScreen)) {
				_scScreenUtils.showChildScreen(screen,"lastProductScannedDetailsScreenRef","","",options,lastProductScannedModel);
				childScreen = _scScreenUtils.getChildScreen(screen, "lastProductScannedDetailsScreenRef");
			} 
			childScreen.setModel("lastProductScanned_output",lastProductScannedModel,"");
			_scEventUtils.fireEventToChild(screen, "lastProductScannedDetailsScreenRef", "updateLastProductScanned", null);
			shipmentLineOutput.ShipmentLine.PickedQty = Number(pickedQty) + Number("1");
			_scScreenUtils.setModel(childLine,"ShipmentLine",shipmentLineOutput,null);
		}		
	},		
	
	/******************************************************
	 * @author: ???
	 * TODO: Add description here
	 *****************************************************/
	deActivateLine: function(screen, productDetail) {
		var reasonDropDown = _scScreenUtils.getWidgetByUId(screen,"cmbShortagesResolution");
		reasonDropDown.setAttribute('value',"Callback");
		reasonDropDown.set('disabled',true);
		reasonDropDown.set('readOnly',true);
		_scWidgetUtils.addClass(screen,"shipmentLineDetailsContainer","scDisabled");
		this.printCallbackReceipt(productDetail);	
	},		
	
	/****************************************
	 * @author: Randy Washington / AQD
	 * @description: Shows the undo button for the child screen 
	 * of the last scanned product.
	 *****************************************/
	showUndoButton: function() {
		var lastScannedModelInWizard = _isccsUIUtils.getWizardModel(this,"lastScannedProduct");
        var childScreenUID = lastScannedModelInWizard.screenUId;
        var childScreen= _scScreenUtils.getChildScreen(this,childScreenUID);
        _scWidgetUtils.showWidget(childScreen, "extn_button_undo", false, null);
	},
	
	/******************************************************
	 * @author: ???
	 * TODO: Add description here
	 *****************************************************/            
    handleMashupOutput: function (mashupRefId, modelOutput, modelInput, mashupContext, applySetModel) 
    {  	
  	
    	if (mashupRefId == "extn_getShipmentDetails_receipt_suspend") 
    		_scScreenUtils.setModel(this, "extn_getShipmentDetailsForReceipt_suspend", modelOutput, null);
    	
    	if (_scBaseUtils.equals(mashupRefId, 'extn_getPickupInfo_mashup')) {
                    // check for suspended information
        if (_scBaseUtils.equals(modelOutput.Shipment.Extn.ExtnSuspendedPickup, "Y")) {
            // we have a suspended pickup, load the information and reset the flag and quantities
            var input = {};
            input.Shipment = {};
            input.Shipment.Extn = {};
            // set the suspended flag back to N
            input.Shipment.Extn.ExtnSuspendedPickup = 'N';
            input.Shipment.ShipmentKey = modelOutput.Shipment.ShipmentKey;
            input.Shipment.ShipmentLines = {};
            input.Shipment.ShipmentLines.ShipmentLine = [];
            // loop through all the child screens to pull the value
            for (var i = 0; i < modelOutput.Shipment.ShipmentLines.ShipmentLine.length; i++) {
                // get the current child screen
                var childScreen = _scScreenUtils.getChildScreen(this, "CustomerPickUp_PickupOrderLineDetails" + i);
                _scWidgetUtils.setValue(childScreen, 'txtScannedQuantity', modelOutput.Shipment.ShipmentLines.ShipmentLine[i].Extn.ExtnSuspendedPickupQty, false);
                // see if we need to change the UI display
                if (_scBaseUtils.equals(parseInt(modelOutput.Shipment.ShipmentLines.ShipmentLine[i].Extn.ExtnSuspendedPickupQty), parseInt(modelOutput.Shipment.ShipmentLines.ShipmentLine[i].Quantity))) {
                    // hide the shortage drop down and show the complete pick checkmark
                    _scWidgetUtils.showWidget(childScreen, "productScanImagePanel", false, null);
                    _scWidgetUtils.hideWidget(childScreen, "cmbShortagesResolution", false);
                }
                var ShipmentLine = {};
                ShipmentLine.ShipmentLineKey = _scWidgetUtils.getValue(childScreen, 'ShipmentLineKey');
                ShipmentLine.Extn = {};
                ShipmentLine.Extn.ExtnSuspendedPickupQty = 0;
                input.Shipment.ShipmentLines.ShipmentLine.push(ShipmentLine);
            }
         	_isccsUIUtils.callApi(this, input, 'extn_saveSuspendedPickup_mashup', null);
         }
    	} else if (_scBaseUtils.equals(mashupRefId, 'extn_getConfigurablePrompts_mashup')) {
            // see if we have a configurable prompt
            // change this check as need be to determine where the prompts are
            if (!_scBaseUtils.isVoid(modelOutput.Item.AdditionalAttributeList.AdditionalAttribute)) {
            	var attributeList = modelOutput.Item.AdditionalAttributeList.AdditionalAttribute;
            	for(var i = 0; i < attributeList.length; i++)
            	{
            		var attrib = modelOutput.Item.AdditionalAttributeList.AdditionalAttribute[i].Name;
                	var n = attrib.search("Popup");
                	if( n > -1)
                		_scScreenUtils.showInfoMessageBox(this, modelOutput.Item.AdditionalAttributeList.AdditionalAttribute[i].Value, null, null, null);
            	}
            }
        } else if (_scBaseUtils.equals(mashupRefId, "translateBarCode")) {
            _scScreenUtils.setModel(this, "translateBarCode_output", modelOutput, null);
            this.updateProductQuantity(modelOutput);
            
        } else if (_scBaseUtils.equals(mashupRefId, 'extn_getShipNode_mashup')) {
                // set the response in the page so we can refer to it later
                _scScreenUtils.setModel(this, "extn_shipNodeInfo", modelOutput, null);
                _scScreenUtils.setModel(this, "extn_shipNode_info_suspend", modelOutput, null);
        } 
    },
    
    /******************************************************
	 * @author: ???/Adam Dunmars
	 * TODO: Add description here
	 *****************************************************/
    getHoldLocation: function() {
    	var pickUpOrderDetailsModel = null;
        pickUpOrderDetailsModel = _isccsUIUtils.getWizardModel(this, "PickUpOrderDetailsModel");
        if(!_scBaseUtils.isVoid(pickUpOrderDetailsModel.Shipment)
        	&& !_scBaseUtils.isVoid(pickUpOrderDetailsModel.Shipment.Extn)
        	&& !_scBaseUtils.isVoid(pickUpOrderDetailsModel.Shipment.Extn.ExtnHoldLocationDesc)) {
        	return pickUpOrderDetailsModel.Shipment.Extn.ExtnHoldLocationDesc;
        } else {
        	return "N/A";
        }
    }, 
    
    /******************************************************
	 * @author: Randy Washington
	 * TODO: Add description here
	 *****************************************************/
    hideShipLinesUndoButton: function(){
		var lastScannedModelInWizard = _isccsUIUtils.getWizardModel(this,"lastScannedProduct");
		//console.log("lastScannedModelInWizard",lastScannedModelInWizard);
		var lastScreenUID = lastScannedModelInWizard.screenUId;
		//console.log("lastScreenUID",lastScreenUID);
		var lastScannedScreen= _scScreenUtils.getChildScreen(this,lastScreenUID);
		//console.log("lastScannedScreen",lastScannedScreen);
		//console.log("lastScannedScreen.id",lastScannedScreen.id);
		var targetId = lastScannedScreen.id;
		//console.log("target",targetId);
		var parentScreen = this.getOwnerScreen(true);
		var childScreens = parentScreen.uIdMap.CustomerPickUp_ProductVerification._allChildScreens;
		//console.log("childScreens",childScreens);
		var uId=null;
		var cId=null;
		var childScreen=null;
        for (var i = 0; i < childScreens.length; i++) {
			if(!_scBaseUtils.isVoid(childScreens[i].uId)){
				uId = childScreens[i].uId;
				if (!_scBaseUtils.equals(childScreens[i].id,targetId)) {
					//console.log(i,childScreens[i].id," != ",targetId,_scBaseUtils.equals(childScreens[i].id,targetId));
					childScreen= _scScreenUtils.getChildScreen(this,uId);
					_scWidgetUtils.hideWidget(childScreen, "extn_button_undo", false, null);
				} else {
					console.log(!_scBaseUtils.isVoid(childScreens[i].uId));
	            }
			}    
	    }
    }
  });        
});
/***************************************************************************************************
 * @author: Adam Dunmars
 * -----------------------------------------
 * This function will be called by the scanner in the CEFSharp application whenever the 
 * physical scanner is used.
 * 
 * The function will:
 * 	1. Get the current editor instance.
 * 	2. Get the current wizard instance.
 * 	3. Check to see if the wizard is a vershion of the WizardUI widget(gotcha):
 *		a. If it is, get the current wizard instance(instead of the generic?)
 * 		b. Otherwise use the wizard.
 * 	4. If the screen contains the function setBarcode it will call its version of the function.
 ***************************************************************************************************/
function scannedBarcode(barcodeString) {
	var editor = sc.plat.dojo.utils.EditorUtils.getCurrentEditor();
	var wizard = sc.plat.dojo.utils.EditorUtils.getScreenInstance(editor); 
	if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
		wizard = wizard.getCurrentWizardInstance();
		var screen = sc.plat.dojo.utils.WizardUtils.getCurrentPage(wizard);
	} else if(wizard instanceof sc.plat.dojo.widgets.Screen) { // There was no wizard
		var screen = wizard;
	} else {
		console.log("Unsupported Type");
	}
	console.log("in setBarcode");
	if(typeof screen.setBarcode == "function") {	
		screen.setBarcode(barcodeString);
	}
}
