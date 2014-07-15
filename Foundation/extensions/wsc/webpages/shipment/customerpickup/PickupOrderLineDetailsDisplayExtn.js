scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/customerpickup/PickupOrderLineDetailsDisplayExtnUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils"],
	function(
			_dojodeclare,
			_extnPickupOrderLineDetailsDisplayExtnUI,
			_scBaseUtils
){ 
	return _dojodeclare("extn.shipment.customerpickup.PickupOrderLineDetailsDisplayExtn", [_extnPickupOrderLineDetailsDisplayExtnUI],{
	// custom code here
		
		/************************************************
		 * @author: Randy Washington / Adam Dunmars
		 * @description: This dynamic binding function
		 * returns the unaltered original ordered quantity 
		 * of a shipment line using 
		 * ShipmentLine/OrderLine/@OriginalOrderedQty.
		 * If the field is not populated, it is defaulted to zero.
		 ************************************************/
		getOriginalOrderedQty:function(dataValue, screen, widget, namespace, modelObj, options){
			if(!_scBaseUtils.isVoid(dataValue)) {
				return parseInt(dataValue);
			} else {
				console.warn("WARNING: ShipmentLine.OrderLine.OriginalOrderedQty N/A!");
				return 0;
			}
		},
		
		/*****************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function displays the
		 * item's quantity picked from the back room by the associate using
		 * ShipmentLine/@BackroomPickedQuantity. If the attribute is empty,
		 * "0" is returned.
		 *******************************************/
		getBackroomPickedQty: function(dataValue, screen, widget, namespace, modelObj, options) {
			console.log("MODEL: ",modelObj);
			if(_scBaseUtils.isVoid(dataValue)){
				console.warn("WARNING: ShipmentLine.BackroomPickedQuantity N/A. Model: ", modelObj);
				return 0;
			} else {
				console.log("DATA VALUE: ", dataValue);
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