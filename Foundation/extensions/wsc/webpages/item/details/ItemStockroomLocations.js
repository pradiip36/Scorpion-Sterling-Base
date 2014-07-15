scDefine([
	"dojo/text!./templates/ItemStockroomLocations.html",
	"scbase/loader!dijit/form/Button",
	"scbase/loader!dojo/_base/declare",
	"scbase/loader!idx/form/TextBox",
	"scbase/loader!idx/layout/ContentPane",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/WidgetUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/widgets/Label",
	"scbase/loader!sc/plat/dojo/widgets/Link",
	"scbase/loader!sc/plat/dojo/widgets/Screen",
	"scbase/loader!wsc/utils/BackroomPickUtils",
	"scbase/loader!isccs/utils/UIUtils"
],
function(
	templateText,
	_dijitButton,
	_dojodeclare,
	_idxTextBox,
	_idxContentPane,
	_scBaseUtils,
	_scEventUtils,
	_scWidgetUtils,
	_scScreenUtils,
	_scLabel,
	_scLink,
	_scScreen,
	wscBackroomPickUtils,
	_isccsUIUtils
) {
    return _dojodeclare("extn.item.details.ItemStockroomLocations", [_scScreen], {
		templateString: templateText,
		uId: "itemStockroomLocations",
		packageName: "extn.item.details",
		className: "ItemStockroomLocations",
		showRelatedTask: false,
		namespaces: {
			targetBindingNamespaces: [{
                description: 'Output used in changeShipment call to set the shortage reason.',
                value: 'StockRoomLocation'
            }],
		},
		hotKeys: [{
            id: "Popup_cancelButton",
            key: "ESCAPE",
            description: "$(_scSimpleBundle:Close)",
            widgetId: "Popup_cancelButton",
            invocationContext: "",
            category: "$(_scSimpleBundle:General)",
            helpContextId: ""
        }],
		staticBindings: [],
		events: [],
		subscribers: {
            local: [{
                eventId: 'afterScreenInit',
                sequence: '30',
                handler: {
                    methodName: "initializeScreen"
                }
            }],
        },
        
        /******************************************
         * @author: Adam Dunmars
         * @description: This function provides a hot-fix to stop the base defect
         * of this screen displaying twice.
         * 
         * TODO: Remove when software group has developed a fix.
         *********************************************/
        initializeScreen : function(event, bEvent, ctrl, args) {
        	var parentScreen = this.getOwnerScreen();
			var childScreens = parentScreen._allChildScreens;
			var itemStockroomLocationsScreenCount = 0;
			
			for(var i = 0; i < childScreens.length; i++) {
				if(_scBaseUtils.equals(childScreens[i].className, "ItemStockroomLocations")) {
					itemStockroomLocationsScreenCount++;
				}
			}
			
			if(itemStockroomLocationsScreenCount > 1) {
				console.log("Looky looky eggs and cookies, we got "+ itemStockroomLocationsScreenCount + " ItemStockroomLocation Screens");
				_scEventUtils.stopEvent(bEvent);
				this.destroy();
			} 
        },
        
        /******************************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function returns the Location
         * description of the MLS Location using
         * ShipmentLine/StockRoomLocation/@Location.
         ******************************************/
        getLocation : function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue)) ? modelObj.StockRoomLocation.Location : "N/A";
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the bar code number 
		 * of the MLS Location using ShipmentLine/StockRoomLocation/BarCode.
		 ********************************************/
		getBarCode : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? modelObj.StockRoomLocation.BarCode : "N/A";
		},
		
		/*********************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function returns the quantity in the 
		 * stock room Location according to MLS using
		 * ShipmentLine/StockRoomLocation/@Quantity.
		 ********************************************/
		getQuantity : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? modelObj.StockRoomLocation.Quantity : "N/A";
		}
	});
});