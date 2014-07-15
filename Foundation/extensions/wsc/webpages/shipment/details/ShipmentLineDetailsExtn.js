scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/details/ShipmentLineDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!isccs/utils/ContextUtils"]
,
function(			 
		 _dojodeclare,
		 _extnShipmentLineDetailsExtnUI,
		 _scScreenUtils,
		 _scWidgetUtils,
		 _scBaseUtils,
		 _isccsContextUtils
){ 
	return _dojodeclare("extn.shipment.details.ShipmentLineDetailsExtn", [_extnShipmentLineDetailsExtnUI],{
		// custom code here
			
		/*******************************************************************
		 * @author: Randy Washington
		 * @description: A binding function returns the order line status.
		 *******************************************************************/  
		getLineStatus:function(dataValue, screen, widget, namespace, modelObj, options){
			if (!_scBaseUtils.isVoid(modelObj) 
					&& !_scBaseUtils.isVoid(modelObj.ShipmentLine) 
					&& !_scBaseUtils.isVoid(modelObj.ShipmentLine.OrderLine)
					&& !_scBaseUtils.isVoid(modelObj.ShipmentLine.OrderLine.Status)){
				return  (_scBaseUtils.equals(modelObj.ShipmentLine.OrderLine.Status, "Shipped")) 
					? "Picked Up" : "";
			} else{
				console.warn("ShipmentLine.OrderLine.Status  N/A");	
				return "N/A";
			}
		},
		
		/***************************************************
		 * @author: Randy Washington
		 * @description: A binding function converts the back room pick quantity to an integer.
		 * TODO: Removed Math.floor, was causing error somehow replace with parseInt.
		 ***************************************************/
		getBackroomPickQty:function(dataValue, screen, widget, namespace, modelObj, options){
			if (!_scBaseUtils.isVoid(dataValue)) {
				return parseInt(dataValue);
			} else{
				console.warn("ShipmentLine.BackroomPickedQuantity  N/A");	
				return "0";
			}
		},
		
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
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function
		 * concatenates the item's description and item ID in parenthesis
		 * using the ShipmentLine/OrderLine/ItemDetails/PrimaryInformation/@ShortDescription
		 * and the ShipmentLine/@ItemID attributes. If either is not found, N/A is returned.
		 *******************************************/
		getItemDescriptionWithUPC : function(dataValue, screen, widget, namespace, modelObj, options) {
			var itemID = null;
			var itemDescription = null;
			itemDescription = (_scBaseUtils.isVoid(dataValue)) ? "N/A": dataValue;
			itemID = (!_scBaseUtils.isVoid(modelObj)
					&& !_scBaseUtils.isVoid(modelObj.ShipmentLine)
					&& !_scBaseUtils.isVoid(modelObj.ShipmentLine.ItemID)) ?
						modelObj.ShipmentLine.ItemID : "N/A";	
			return itemDescription + " (" + itemID + ")";
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
		}
	});
});