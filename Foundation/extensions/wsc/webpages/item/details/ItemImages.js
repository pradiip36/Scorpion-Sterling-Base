scDefine([
	"dojo/text!./templates/ItemImages.html",
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
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!wsc/utils/CustomerPickUpUtils",
	"scbase/loader!dojo/_base/url"
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
	_isccsUIUtils,
	_wscCustomerPickUpUtils,
	url
) {
    return _dojodeclare("extn.item.details.ItemImages", [_scScreen], {
		templateString: templateText,
		uId: "itemImages",
		packageName: "extn.item.details",
		className: "ItemImates",
		showRelatedTask: false,
		namespaces: {
			targetBindingNamespaces: [{
                description: "Image namespace for holding repeating screen data",
                value: 'Image'
            }]
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
         * @description: The sole purpose of this function is to verify screen is being initialized. 
         * Insert debug text here as needed.
         * 
         * TODO: Remove when everything is fully finished.
         *********************************************/
		initializeScreen : function(event, bEvent, ctrl, args) {
		}, 
		
		/*******************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function gets the item's
		 * image icon from the ShipmentLine/ItemURL/@ImageID attribute.
		 */
		getImageID : function(screen, widget, namespace, modelObj) {
			return modelObj.ItemURL.ImageID;
		},
		
		/**************************************************
		 * @author: Adam Dunmars
		 * @description: This dynamic binding function gets the item's 
		 * image path from the ShipmentLine/ItemURL/@ImageLocation attribute.
		 *************************************************/
		getImageLocation : function(screen, widget, namespace, modelObj) {
			return modelObj.ItemURL.ImageLocation;
		}
	});
});