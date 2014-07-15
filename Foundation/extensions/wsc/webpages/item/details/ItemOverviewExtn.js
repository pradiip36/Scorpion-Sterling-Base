scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/item/details/ItemOverviewExtnUI",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/EventUtils"]
,
function(			 
	    _dojodeclare,
	    _extnItemOverviewExtnUI,
	    _isccsUIUtils,
	    _scBaseUtils,
	    _scScreenUtils,
	    _scEventUtils
){ 
	return _dojodeclare("extn.item.details.ItemOverviewExtn", [_extnItemOverviewExtnUI],{
		// custom code here
		
		/****************************************
		 * @author: Adam Dunmars
		 * @description: This screen gets the extn_ShipmentLine Model from the 
		 * parent screen and sets it for use in this screen.
		 ****************************************/
		initializeScreen: function(event, bEvent, ctrl, args) {
		
			var parentScreen = this.getOwnerScreen();
			var childScreens = parentScreen._allChildScreens;
			var itemOverviewScreenCount = 0;
			
			for(var i = 0; i < childScreens.length; i++) {
				if(_scBaseUtils.equals(childScreens[i].className, "ItemOverview")) {
					itemOverviewScreenCount++;
				}
			}
			
			if(itemOverviewScreenCount > 1) {
				console.log("Looky looky eggs and cookies, we got "+ itemOverviewScreenCount + " ItemOverviewScreens");
				_scEventUtils.stopEvent(bEvent);
				this.destroy();
			} else {
				var ShipmentLineModel = _scScreenUtils.getModel(parentScreen, "extn_ShipmentLine");
				_scScreenUtils.setModel(this, "itemOverviewModel", ShipmentLineModel, null);
			}
			
        },
        
		/********************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the item's stock room
		 * availability from GIV using the ShipmentLine/@StockRoomQuantity. 
		 ********************************/
		getStockRoomQuantity:function(dataValue, screen, widget, namespace, modelObj){	
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A"; 
		},

		/********************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the item's sales floor
		 * availability from GIV using the ShipmentLine/@SalesFloorQuantity.
		 *********************************/
		getSalesFloorQuantity:function(dataValue, screen, widget, namespace, modelObj){
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function gets the list of UPCs for a given item
		 * using the ShipmentLine/OrderLine/ItemDetails/ItemAliasList values.
		 * This function will list each UPC as comma separated values.
		 * TODO: Determine whether this should also display the UPCs returned from MLS.
		 ***********************/
		getUPCs : function(dataValue, screen, widget, namespace, modelObj) {
			var upcList = "N/A";
			//console.log("UPCListBefore", upcList);
			if(!_scBaseUtils.isVoid(dataValue)
				&& !_scBaseUtils.isVoid(dataValue.ItemAlias)
				&& !_scBaseUtils.isVoid(dataValue.ItemAlias.length)) {
				
				upcList = "";
				var itemAliases = dataValue.ItemAlias;
				for(var i = 0; i < itemAliases.length; i++) {
					upcList += itemAliases[i].AliasValue + "\n";
				}
			}
			//console.log("UPCListAfter", upcList);
			
			return upcList;
		},
		
		/****************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's 
		 * vendor number from MLS using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnVendorStyleNo
		 * attribute.
		 ***************************************/
		getVendorNum : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's
		 * vendor description from MLS using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnVendorStyleDesc
		 * attribute.
		 *******************/
		getVendorDesc : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's
		 * size using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnSizeDesc attribute.
		 *******************/
		getSize : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's color
		 * using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnColorDesc attribute.
		 *******************/
		getColor : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's department
		 * using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnDept attribute.
		 *******************/
		getDepartment : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's major
		 * class using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnClass attribute.
		 *******************/
		getMajorClass : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's sub class
		 * using the ShipmentLine/OrderLine/ItemDetails/Extn/@ExtnSubClass attribute.
		 *******************/
		getSubClass : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's floor pad
		 * location returned from MLS using the ShipmentLine/@Floorpad attribute.
		 *******************/
		getFloorpad : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},

		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function will display the item's SKU
		 * description using the ShipmentLine/OrderLine/ItemDetails/PrimaryInformation/@Description
		 * attribute.
		 *******************/		
		getSKUDescription : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the item's brand using
		 * ShipmentLine/@Brand.
		 *******************/		
		getBrand: function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		},
		
		/*******************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the item's BoxID returned
		 * from MLS.
		 * TODO: Implement this function.
		 *******************/		
		getBoxID: function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
		}
	});
});