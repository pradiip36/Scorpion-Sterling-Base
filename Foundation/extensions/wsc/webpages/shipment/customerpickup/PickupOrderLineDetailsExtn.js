scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/customerpickup/PickupOrderLineDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils"],
	function(			 
			_dojodeclare
			,_extnPickupOrderLineDetailsExtnUI
			,_scWidgetUtils
			,_scBaseUtils
			,_scScreenUtils
	){ 
		return _dojodeclare("extn.shipment.customerpickup.PickupOrderLineDetailsExtn", [_extnPickupOrderLineDetailsExtnUI],{
		// custom code here
			
		/**************************************************
		 * @author: Adam Dunmars
		 * @description: We traverse backwards so we don't create an error in removing.
		 **************************************************/
		extnInitialize : function(event, bEvent, ctrl, args) {
			this.removeOOBShortageReasons();
		},
		
		/**************************************************
		 * @author: Adam Dunmars
		 * @description: We traverse backwards so we don't create an error in removing.
		 * TODO: There HAS to be another way to do this aside from this and removing from DB directly. ~Adam
		 **************************************************/
		removeOOBShortageReasons : function() {
			var shortageModel = this.getModel("ShortagaeResolutionTypeList");
			var length = shortageModel.CommonCodeList.CommonCode.length;
			for(var i = length-1; i >= 0; i--) {
				var currentModel = shortageModel.CommonCodeList.CommonCode[i];
				var description = currentModel.CodeLongDescription;
				if(_scBaseUtils.equals(description, "Pick Later")
					|| _scBaseUtils.equals(description, "Cancel")
					|| _scBaseUtils.equals(description, "All Inventory Shortage")) {
					shortageModel.CommonCodeList.CommonCode.splice(i, 1);
				}
			}
			this.setModel("ShortagaeResolutionTypeList", shortageModel);
		},
	
		/**************************************************
		 * @author: Adam Dunmars/Randy Washington/Robert Fea
		 * @description: This subscriber function will:
		 * 1) Decrease the quantity in the UI(WidgetId: extn_datalabelScannedQty.
		 * 2) If the quantity is 0, we decrement the quantity in both of the models,
		 * 	  hide the check mark(WidgetId: productScanImagePanel) and display the shortage
		 *    reason drop down. 
		 * 3) If the quantity was changed and is now zero, we hide the undo button
		 * 
		 * TODO: Determine if this needs to be hidden after a scan or if you can
		 * actually undo it all the way down to zero
		 **************************************************/
		undoQuantity:function( event, bEvent, ctrl, args) {
		
			var scannedQuantityWidget = this.getWidgetByUId("extn_datalabelScannedQty");
			var currentQuantity = Number(scannedQuantityWidget.value);
	
			if (_scBaseUtils.greaterThan(currentQuantity, 0)) {
				var PickAll_Input = _scScreenUtils.getModel(this,"PickAll_Input",false);
				var pickAllQty = PickAll_Input.Quantity;
				currentQuantity--;
				pickAllQty--;
				PickAll_Input.Quantity = pickAllQty;
				_scWidgetUtils.setValue(this,'extn_datalabelScannedQty',currentQuantity,false);	
				_scWidgetUtils.hideWidget(this, "productScanImagePanel", false);
				_scWidgetUtils.showWidget(this, "cmbShortagesResolution", false, null);
				_scScreenUtils.setModel(this,"PickAll_Input",PickAll_Input,false);
			} else { // Current quantity is either zero, or less than zero somehow.
				console.log("Exception in undoQuantity: currentQuantity is " + currentQuantity);
			}
			
			if (_scBaseUtils.equals(currentQuantity, 0) ){
				_scWidgetUtils.hideWidget(this, "extn_button_undo", false);
			}
			
		},
		
		/********************************************************************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: This dyamic binding function returns the backroom picked quantity 
		 * using ShipmentLine/@BackroomPickedQuantity.
		 * TODO: This is causing weird issues when using parseInt (error with Math.floor?) ~Adam
		 ********************************************************************************/
		getBackroomPickQty:function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(modelObj.ShipmentLine.BackroomPickedQuantity)){
				console.warn("WARNING: ShipmentLine/@BackroomPickedQuantity is N/A! Namespace: " +namespace + ", Model: ", modelObj);
				return 0;
			}else{
				return parseInt(modelObj.ShipmentLine.BackroomPickedQuantity).toString();
			}
		},
		
		/*************************************************************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: This dynamic binding function returns the original ordered
		 * quantity of the shipment line, unchanged by shortages using
		 * ShipmentLine/OrderLine/@OriginalOrderedQuantity. 
		 *************************************************************************/
		getOriginalOrderedQty:function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(dataValue)) {
				console.warn("WARNING: ShipmentLine.OrderLine/@OriginalOrderedQuantity: N/A!");
				return 0;
			} else {
				return parseInt(modelObj.ShipmentLine.Quantity);
			}
		},
		
		/*********************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the current scanned
		 * quantity of a shipment's line items using
		 * ShipmentLine/@Quantity from the PickAll_Input namespace. 
		 *********************************/
		getScannedQuantity:function(dataValue, screen, widget, namespace, modelObj, options){
			if(_scBaseUtils.isVoid(dataValue)) {
				console.log("ERROR in getScannedQuantity: DataValue: "+dataValue);
				return "0";
			} else {
				console.log("DATA VALUE IS: "+dataValue);
				return dataValue;
			}
		},
		
		/*******************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will return the item's Kohls UPC
		 * using ShipmentLine/OrderLine/ItemDetails/ItemAliasList/ItemAlias[0]/@AliasValue
		 * attribute by:
		 * 		1) Setting the default UPC to the first one in the alias list.
		 * 		2) Looping through the ItemAliases until finding one that starts with 400.
		 * 			a) If it finds one it will set the UPC to that and break;
		 * 			b) Otherwise the default selected UPC will be used.
		 * 
		 * TODO 1: Determine if this needs to return multiple UPCs.
		 * TODO 2: Determine if this is pulling from the correct attribute and not from MLS.
		 *********************************/
		getKohlsUPC : function(dataValue, screen, widget, namespace, modelObj, options) {
			var upc = "N/A";
			if(_scBaseUtils.isVoid(dataValue)) {
				console.warn("WARNING: ShipmentLine.OrderLine.ItemDetails.ItemAliasList is N/A!");
			} else {
				var aliasList = dataValue;
				if(!_scBaseUtils.isVoid(aliasList.ItemAlias)
					&& !_scBaseUtils.isVoid(aliasList.ItemAlias.length)) {
					var itemAliases = aliasList.ItemAlias;
					upc = itemAliases[0].AliasValue;
					var aliasLength = itemAliases.length;
					for(var i = 0; i < aliasLength; i++) {
						if(itemAliases[i].AliasValue.match("^400")){
							upc = itemAliases[i].AliasValue;
							break;
						}
					}
				}
			}
			return upc;
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns a concatenation
		 * of the item's description and SKU value in parenthesis using
		 *  1) ShipmentLine/@ShortDescription
		 *  2) ShipmentLine/@ItemID
		 *******************************************/
		getItemDescriptionWithSKU : function(dataValue, screen, widget, namespace, modelObj, options) {
			var itemID = null;
			var itemDescription = null;
			itemDescription = _scBaseUtils.isVoid(dataValue) ? "N/A" : dataValue;
			itemID = modelObj.ShipmentLine.ItemID;
			return itemDescription + " (" + itemID + ")";
		}
	});
});
