scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/item/details/ItemDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils",
          "scbase/loader!isccs/utils/ContextUtils"]
,
function(			 
		    _dojodeclare,
		    _extnItemDetailsExtnUI,
		    _scBaseUtils,
		    _scWidgetUtils,
		    _isccsUIUtils,
		    _scScreenUtils,
		    _scModelUtils,
		    _isccsContextUtils
){ 
	return _dojodeclare("extn.item.details.ItemDetailsExtn", [_extnItemDetailsExtnUI],{
	// custom code here
		
		/******************************************************
		 * @author: Adam Dunmars
		 * @description: This function overrides the OOB 
		 * initializeScreen function in order to use the itemDetails model
		 * in this and other child screens(ItemLocations and ItemImages).
		 ******************************************************/
		initializeScreen: function(event, bEvent, ctrl, args) {
            var initialInputData = null;
            
            this.apiInputsforTabs = _scBaseUtils.getNewArrayInstance();
            initialInputData = _scScreenUtils.getInitialInputData(this);
            if (!(_scBaseUtils.isVoid(initialInputData))) {
                _scBaseUtils.setModel(this, "apiInput", initialInputData, null);
            }
            if (_isccsContextUtils.isMobileContainer()) {
                _scWidgetUtils.hideTabPanel(this, "tab1", "pnlComponents");
                _scWidgetUtils.hideTabPanel(this, "tab1", "pnlSpecificationInformation");
            } else {
                _scWidgetUtils.hideTabPanel(this, "tab1", "pnlImages");
            } 
            
            var itemModel = null;
            itemModel = _scScreenUtils.getModel(this, "itemDetails");
            console.log("Item Model", itemModel);
            
            _scBaseUtils.setModel(this, "itemDetailsModel", itemModel, null);
            var currentShipmentLine = null;
            var ShipmentLine = null;
            if(!_scBaseUtils.isVoid(itemModel)
            && !_scBaseUtils.isVoid(itemModel.Shipment)
            && !_scBaseUtils.isVoid(itemModel.Shipment.ShipmentLines)
            && !_scBaseUtils.isVoid(itemModel.Shipment.ShipmentLines.ShipmentLine)
            && !_scBaseUtils.isVoid(itemModel.Shipment.ShipmentLines.ShipmentLine.length)) {
            	for(var i = 0; i < itemModel.Shipment.ShipmentLines.ShipmentLine.length; i++) {
            		currentShipmentLine = itemModel.Shipment.ShipmentLines.ShipmentLine[i];
            		if(_scBaseUtils.equals(currentShipmentLine.ShipmentLineKey, initialInputData.Shipment.ShipmentLines.ShipmentLine.ShipmentLineKey)) {
            			ShipmentLine = currentShipmentLine;
            			break;
            		}
            	}
            }
            if(_scBaseUtils.isVoid(ShipmentLine)) {
            	console.error("ERROR (initializeScreen extn): Shipment Line cannot be found on the order.");
            } else {
            	_scBaseUtils.setModel(this, "extn_ShipmentLine", ShipmentLine, null);
            }
            console.log("Finalized ShipmentLine", ShipmentLine);
            this.initializMainInfoPanelewidgets(itemModel);
		},
		
		/**************************************************
		 * @author: Adam Dunmars
		 * @description: This function overrides the OOB initializMainInfoPanelWidgets
		 * function in order to use the itemModel.
		 * TODO: Ensure this is still necessary.
		 *****************************************************/
		initializMainInfoPanelewidgets: function(itemModel) {
            var bundleModel = null;
            bundleModel = _scModelUtils.getModelObjectFromPath("Item", itemModel);
           // if (!(_scBaseUtils.or(_scBaseUtils.or(_isccsItemUtils.isBundleParent(this, bundleModel), _isccsItemUtils.isPhysicalKit(this, bundleModel)), _scBaseUtils.equals(_scModelUtils.getStringValueFromPath("PrimaryInformation.IsModelItem", bundleModel), "Y")))) {
                _scWidgetUtils.hideTabPanel(this, "tab1", "pnlComponents");
           // }
        },
                
        /***************************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function displays the item's description 
         * using the ShipmentLine/OrderLine/ItemDetails/PrimaryInformation/@DisplayItemDescription
         ***************************************/
        getItemDescription : function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
        },
        
        /**********************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function displays the item's SKU using the
         * ShipmentLine/@ItemID.
         **********************************/
        getSKU : function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue)) ? dataValue : "N/A";
        },
        
        /*****************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function displays the item's UPC 
         * using the ShipmentLine/OrderLine/ItemDetails/ItemAliasList/ItemAlias/@AliasValue.
         * 
         * TODO: 
         * 	1) At the moment this function will only list the first alias value on the list.
         *  2) Also, this function is assuming that the value should be looking at the specified XPath
         * 		and not be using the values that are located in MLS.
         ******************************/
        getUPC: function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue) && !_scBaseUtils.isVoid(dataValue.length)) ? dataValue[0].AliasValue : "N/A";
        },
      
        /***************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function displays the item's retail price using the
         * ShipmentLine/OrderLine/LinePriceInfo/@RetailPrice.
         * TODO: Add the currency symbol based on what the specified currency is.
         ***************************/
        getRetailPrice : function(dataValue, screen, widget, namespace, modelObj) {
        	return (!_scBaseUtils.isVoid(dataValue)) ? dataValue: "N/A";
        },
        
        /***************************
         * @author: Adam Dunmars
         * @description: This dynamic binding function displays the SKU status returned from MLS 
         * using ShipmentLine/@SkuStatus. If the SKU status is not "Active" it highlights the text red. 
         * TODO: Determine if the removeClass line can ever occur and actually remove the errorText.
         **************************/
        getSKUStatus : function(dataValue, screen, widget, namespace, modelObj) {
        	if (!_scBaseUtils.isVoid(dataValue)) {
        		if(!_scBaseUtils.equals("Active", dataValue)) {
        			_scWidgetUtils.addClass(this, widget, "errorText");
        		} else {
        			_scWidgetUtils.removeClass(this, widget, "errorText");
        		}
        		return dataValue;
        	} else {
        		_scWidgetUtils.addClass(this, widget, "errorText");
        		return "N/A";
        	}
        }
	});
});