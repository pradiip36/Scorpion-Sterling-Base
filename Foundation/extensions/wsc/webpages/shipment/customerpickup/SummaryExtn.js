scDefine(["scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/shipment/customerpickup/SummaryExtnUI",
	"scbase/loader!isccs/utils/UIUtils", 
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils", 
	"scbase/loader!sc/plat/dojo/utils/BaseUtils", 
	"scbase/loader!sc/plat/dojo/utils/ModelUtils", 
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils", 
	"scbase/loader!sc/plat/dojo/utils/WizardUtils", 
	"scbase/loader!wsc/utils/CustomerPickUpUtils",
	"scbase/loader!isccs/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!extn/utils/ScanUtils",
	"scbase/loader!isccs/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils"]
,
function(_dojodeclare,
	_extnSummaryExtnUI, 
	_isccsUIUtils, 
	_scScreenUtils, 
	_scBaseUtils, 
	_scModelUtils, 
	_scWidgetUtils, 
	_scWizardUtils, 
	_wscCustomerPickUpUtils, 
	_WizardUtils,
	_scEditorUtils,
	_bopusUtils,
	_wizardUtils,
	_editorUtils
){ 
	return _dojodeclare("extn.shipment.customerpickup.SummaryExtn", [_extnSummaryExtnUI],{
		// custom code here
		
		/*************************************
		 * @author: ???????
		 * @description: event handler for custom "Previous" button
		 *************************************/
		handlePrevious: function(event, bEvent, ctrl, args) {
			var wizard = this.getWizardForScreen(this);
			_scWizardUtils.previousScreen(wizard);
		},

		/********************************
		 * @author: ??? (Rob Fea?)
		 * @description: INSERT DESCRIPTION HERE
		 * TODO: This function should be moved to BOPUS Utility file. ~Adam
		 *******************************/
		 getWizardForScreen: function(/*Screen*/screen){
			 if(screen.wizardUI){
				return screen;
			 } else {
				var force = true;
				var parentScreen = screen.getOwnerScreen(force);
				if(parentScreen){
					return this.getWizardForScreen(parentScreen);
				}
			}
			return null;
		 },
		 
		/********************************************************
		 * @author: Randy Washington
		 * @description: This function changes the confirm button label
		 *	to confirm pickup.
		 *******************************************************/
		changeWizardConfirmButtonLabel : function(){
			var uId = "confirmBttn";
			var uId_2 = "confirmBttn2";
			
			var editor = _editorUtils.getCurrentEditor();
			var wizard = _editorUtils.getScreenInstance(editor);
			if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
				wizard = wizard.getCurrentWizardInstance();
			}
			var wizard = _bopusUtils.getCurrentWizardInstance();
			_wizardUtils.setLabelOnNavigationalWidget(wizard, uId, "Confirm Pickup");
			_wizardUtils.setLabelOnNavigationalWidget(wizard, uId_2, "Confirm Pickup");
	 	},
	 	
	 	/****************************************************
	 	 * @author: ???/Maseeh Sabir(?)
	 	 * @description: INSERT DESCRIPTION HERE
	 	 * TODO: If this is a subscriber it needs the input
	 	 * parameters added.
	 	 ****************************************************/
		extInitialize: function() {
			this.populateGiftReceiptPrintOptions();
			
			var wizard = this.getWizardForScreen(this);
			_wizardUtils.hideNavigationalWidget(wizard, 'closeBttn', true);
			_wizardUtils.hideNavigationalWidget(wizard, 'prevBttn', true);
			//var summaryModel = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output"); Wasn't being used? ~Adam
			this.changeWizardConfirmButtonLabel();
			
		},
	
	    /*************************************************
	     * @author: ???/ Randy Washington
	     * @description: 1.???
		 *
	     * 				 2. Returns to Home Screen after an order is confirmed. 
	     * 					a. Closes the editor for the current screen.
	     * 					b. Closes the editor for shipment editor.
	     * 
	     * TODO: Seems some variables aren't being used here?
	     *************************************************/
	 	handleRecordCustomerPickMashupCall: function(modelOutput) {
	 	    this.enterPickupEJEntry();
	 	    var giftReceiptstorePrint = _scScreenUtils.getWidgetByUId(this,"extn_GiftReceiptFilterSelect").value;
	 	    if(!_scBaseUtils.isVoid(window.systemInfo)) {
	 	    	this.runPrintJob();
	 	    }
		   // this.runPrintJob();
	 	    
	        var confirmStatus = false;
	        confirmStatus = _wscCustomerPickUpUtils.processRecordCustomerPickMultiApiOutput(
	        modelOutput);
	        var wizardInstance = false;
	        wizardInstance = _isccsUIUtils.getParentScreen(this, true);
	        _WizardUtils.disableNavigationalWidget(wizardInstance, "prevBttn");
	        _WizardUtils.disableConfirm(wizardInstance);
		
	        var pickUpSummaryModel = null;
	        pickUpSummaryModel = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output");
	        pickUpSummaryModel.Shipment.NumberOfGiftReceipts = _scScreenUtils.getWidgetByUId(this,"extn_GiftReceiptFilterSelect").value;
	        pickUpSummaryModel.PrintReceipts = "Y";
	        
	        var editorInstance = null;
	        editorInstance = _isccsUIUtils.getEditorFromScreen(this);
			_scEditorUtils.closeEditor(editorInstance);
	        var shipmentEditor = _scEditorUtils.getSpecificOpenEditors("wsc.editors.ShipmentEditor");
	        _scEditorUtils.closeEditor(shipmentEditor[0].editorInstance);
	       // _isccsUIUtils.openWizardInEditor("wsc.shipment.wizards.ShipmentSummaryWizard", pickUpSummaryModel, "wsc.editors.ShipmentEditor", this);
	 	},
	
	    /*********************************************
	     * @author: ??? (Maseeh Sabir?)
	     * @description: INSERT DESCRIPTION HERE
	     ********************************************/
		enterPickupEJEntry: function() 
		{
			var myShipments = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output");
			var lineCount = myShipments.Shipment.ShipmentLines.ShipmentLine;
			
			if(lineCount > 0)
			{	
			
				var mashInput = {};
				mashInput.ShipmentStoreEvents = {};
				mashInput.ShipmentStoreEvents.OrderNo = myShipments.Shipment.ShipmentLines.ShipmentLine[0].OrderNo;
				mashInput.ShipmentStoreEvents.EventType = "Order Picked";
				mashInput.ShipmentStoreEvents.OrderHeaderKey = myShipments.Shipment.ShipmentLines.ShipmentLine[0].OrderHeaderKey;
				mashInput.ShipmentStoreEvents.ShipmentKey = myShipments.Shipment.ShipmentLines.ShipmentLine[0].ShipmentKey;
				_isccsUIUtils.callApi(this, mashInput, "extn_addEJTransOrderPicked", null);		
			}	
		},
	
		/************************************
		 * @author: Maseeh Sabir (?)
		 * @description: INSERT DESCRIPTION HERE
		 ***********************************/
		runPrintJob: function() {
		    var pickUpSummaryModel = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output");
		    var giftReceiptstorePrint = _scScreenUtils.getWidgetByUId(this,"extn_GiftReceiptFilterSelect").value;
		    var diamondDataModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_diamond");
			
		    var modelOutput = null;
		    var shortedItems = 0;
		    modelOutput = this.getCorrectItems(pickUpSummaryModel, false);
		    modelOutput.diamondModel = diamondDataModel;        
	
		    this.printReceipt("customerPickup", null, pickUpSummaryModel);
		    this.printReceipt("storePickup", null, pickUpSummaryModel);
		    
		    //code for printing return (exception) on shorted items
		    shortedItems = pickUpSummaryModel.Shipment.RemainingShipmentLines.ShipmentLines.ShipmentLine.length;
	
			if(!_scBaseUtils.isVoid(pickUpSummaryModel) && shortedItems > 0){
				var shortedModel = this.getCorrectItems(pickUpSummaryModel, true);
			   	this.printReceipt("return", null, shortedModel);
			}
		    	
			//code for printing gift receipts
		    if(!_scBaseUtils.isVoid(giftReceiptstorePrint) && giftReceiptstorePrint>0) {
			    for(var i=1; i<=giftReceiptstorePrint; i++) {	
			    	this.printReceipt("giftNoItem",null, modelOutput);
			    }	
		    }
		},
	
		/******************************************
		 * @author: Maseeh Sabir(?)
		 * @description: The screen wizard will be used for void purpose to close the screen. 
		 * If you pass null it will still print.
		 ******************************************/
		printReceipt: function(receiptType, wizard, modelOutput) {		
		    var commonModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_output");
		    var storeInfo = _scScreenUtils.getModel(this,'extn_shipNode_info');
		    modelOutput.ReceiptType = receiptType;
	  	    _bopusUtils.printReceipt(wizard, modelOutput, commonModel, storeInfo);
		},
	
		/********************************************
		 * @author: Maseeh Sabir(?)
		 * @description: INSERT DESCRIPTION HERE
		 *******************************************/
		getCorrectItems: function(modelOutput, isShort) {
			var pickedLines = null;
			pickedLines = (isShort) ? modelOutput.Shipment.RemainingShipmentLines.ShipmentLines 
					: modelOutput.Shipment.IncludedShipmentLines.ShipmentLines;
			modelOutput.Shipment.ShipmentLines = pickedLines;
			return modelOutput;
		},
		
		/******************************************
		 * @author: ???
		 * @description: INSERT DESCRIPTION HERE
		 ******************************************/
		voidOrder: function() {
			var modelOutput = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output");
			var diamondDataModel = _scScreenUtils.getModel(this, "extn_commCode4Receipt_diamond");
			modelOutput.diamondModel = diamondDataModel; 
			var wizard = this.getWizardForScreen(this);
			this.printReceipt("voidpickup", wizard, modelOutput);
		},	
	
		/***************************************
		 * @author: Maseeh Sabir & Shubhadip Ray
		 * @description: This function populates the gift receipt drop down with 
		 * the number of scanned items.
		 * TODO: Remove unused variables and get rid of warnings
		 ****************************************/	
		populateGiftReceiptPrintOptions: function() {
		    var pickUpSummaryModel = _scScreenUtils.getModel(this, "getCustomerPickupOrderSummaryList_output");
		    var shortedItems = pickUpSummaryModel.Shipment.RemainingShipmentLines.ShipmentLines.ShipmentLine.length; 
		    var markedGift = false;	            
	
		    var shipmentLines = pickUpSummaryModel.Shipment.IncludedShipmentLines.ShipmentLines.ShipmentLine;
		    
		    var defaultValue = 0;
		    
		    for(var j = 0; j<shipmentLines.length; j++) {
		    	var currentLine = shipmentLines[j];
	
		    	//items are not shorted			
		    	if((currentLine.OrderLine.RemainingQty > 0) && (currentLine.OrderLine.GiftFlag == "Y")) {		
		    		defaultValue = 1;
		    		break;
		    	}
		    }
	
		    var GiftReceiptOptions={};
		    var shipmentLines = pickUpSummaryModel.Shipment.IncludedShipmentLines.ShipmentLines.ShipmentLine;
		    var maxnumberofGiftReceipts = 0;
		    
		    for(var k = 0; k<shipmentLines.length; k++)
		    {
		    	if(!_scBaseUtils.isVoid(shipmentLines[k].Quantity))
		    	{
		    		maxnumberofGiftReceipts += Number(shipmentLines[k].Quantity);
		    	}
		    	
		    }
		    
		    var giftReceiptsfilter = _scScreenUtils.getWidgetByUId(this,"extn_GiftReceiptFilterSelect");
		    var options = [];
		    for(var i=0; i<=maxnumberofGiftReceipts; i++) {
		    	options.push({
		        "id": i, 
		        "name": i,
		        "value": i
		      });
		    }
		    var optionsData = {
    		  "data": options
    		};
		    
		    var str = new dojo.store.Memory(optionsData);
		    giftReceiptsfilter.set('store',str);
		    giftReceiptsfilter.setAttribute('value',defaultValue);
		},
	 
		/*******************************************
		 * @author: ???
		 * @descripiton: INSERT DESCRIPTION HERE
		 ******************************************/
		handleMashupOutput: function(mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel) {
		
			console.log("mashupRefId",mashupRefId);
			console.log("modelOutput",modelOutput);
			console.log("mashupInput",mashupInput);
			console.log("mashupContext",mashupContext);
			console.log("applySetModel",applySetModel);
            
			if (_scBaseUtils.equals(mashupRefId, "recordCustomerPickMultiApi")) {
		    	if (!(_scBaseUtils.equals(false, applySetModel)))  { 
		    		_scScreenUtils.setModel(this, "customerpickup_recordCustomerPickMultiApi_output", modelOutput, null);
		        }   
		    	this.handleRecordCustomerPickMashupCall(modelOutput);
		    } else if(_scBaseUtils.equals(mashupRefId, "recordCustomerPick")) {
	            this.handleRecordCustomerPickMashupCall(modelOutput);
		    }
		}
	});
});