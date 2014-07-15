scDefine([
	"dojo/text!./templates/ItemSalesFloorLocations.html",
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
    return _dojodeclare("extn.item.details.ItemSalesFloorLocations", [_scScreen], {
		templateString: templateText,
		uId: "itemSalesFloorLocations",
		packageName: "extn.item.details",
		className: "ItemSalesFloorLocations",
		showRelatedTask: false,
		namespaces: {
			targetBindingNamespaces: [{
                description: 'Output used in changeShipment call to set the shortage reason.',
                value: 'SalesFloorLocation'
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
         * This function provides a hot-fix to stop the base defect
         * of this screen displaying twice.
         * 
         * TODO: Remove when software group has developed a fix.
         *********************************************/
        initializeScreen : function(event, bEvent, ctrl, args) {
        	var parentScreen = this.getOwnerScreen();
			var childScreens = parentScreen._allChildScreens;
			var itemSalesfloorLocationsScreenCount = 0;
			
			for(var i = 0; i < childScreens.length; i++) {
				if(_scBaseUtils.equals(childScreens[i].className, "ItemSalesFloorLocations")) {
					itemSalesfloorLocationsScreenCount++;
				}
			}
			
			if(itemSalesfloorLocationsScreenCount > 1) {
				console.log("Looky looky eggs and cookies, we got "+ itemSalesfloorLocationsScreenCount + " ItemSsalesFloorLocation Screens");
				_scEventUtils.stopEvent(bEvent);
				this.destroy();
			} 
        },
        
        /*******************************************
		 * @author: Adam Dunmars
		 * This dynamic binding function returns the ESign Location
		 * of the MLS Sales Floor Location. Server side it concatenates
		 * the values of the FullDescriptionCode and MaxSectionNumber using
		 * ShipmentLine/SalesFloorLocation/@ESignLocation.
		 ********************************************/
        getESignLocation : function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue)) ? modelObj.SalesFloorLocation.ESignLocation : "N/A";
		},
		
		/*******************************************
		 * @author: Adam Dunmars
		 * This dynamic binding function returns the ESign ID
		 * of the MLS Sales Floor Location using ShipmentLine/SalesFloorLocation/@ESignId
		 * TODO: Determine what to do if an item has multiple ESign IDs
		 ********************************************/
		getESignID : function(dataValue, screen, widget, namespace, modelObj) {
			return (!_scBaseUtils.isVoid(dataValue)) ? modelObj.SalesFloorLocation.ESignId : "N/A";
		}
	});
});