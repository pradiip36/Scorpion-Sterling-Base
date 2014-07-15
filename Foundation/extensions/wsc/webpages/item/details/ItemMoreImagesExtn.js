scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/item/details/ItemMoreImagesExtnUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!wsc/utils/CustomerPickUpUtils"]
,
function(
		_dojodeclare,
		_extnItemMoreImagesExtnUI,
		_scBaseUtils,
		_wscCustomerPickUpUtils
){ 
	return _dojodeclare("extn.item.details.ItemMoreImagesExtn", [_extnItemMoreImagesExtnUI],{
	// custom code here
		
		/***************************************
		 * @author: Adam Dunmars
		 * This function gets the shipment line model from the parent screen
		 * for use in the repeating screen data for the images
		 ****************************************/
		getShipmentLineModel : function(event, bEvent, ctrl, args) {
			var parentScreen = this.getOwnerScreen();
			var ShipmentLineModel = parentScreen.getModel("extn_ShipmentLine");
			_scBaseUtils.setModel(this, "extn_ShipmentLine", ShipmentLineModel, null);
		},
		
		/*****************************************
		 * @author: Adam Dunmars
		 * This function is the repeating screen generator function
		 * for the Item Images.
		 ******************************************/
		getImagesRepeatingScreenData : function(value, screen, widget, namespace, modelObject){
			var repeatingScreenId = "extn.item.details.ItemImages";
            var returnValue = null;
            var screenMode = "default";
            var additionalParamsBean = null;
            additionalParamsBean = _scBaseUtils.getNewBeanInstance();
            _scBaseUtils.addStringValueToBean("screenMode", screenMode, additionalParamsBean);
            var namespaceMapBean = null;
            namespaceMapBean = _scBaseUtils.getNewBeanInstance();
            
            _scBaseUtils.addStringValueToBean("parentnamespace", "extn_ShipmentLine", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childnamespace", "Image", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("parentpath", "OrderLine.ItemDetails.ItemURLs.ItemURL", namespaceMapBean);
            _scBaseUtils.addStringValueToBean("childpath", "ItemURL", namespaceMapBean);
            
            returnValue = _wscCustomerPickUpUtils.getRepeatingScreenData(repeatingScreenId, namespaceMapBean, additionalParamsBean);
            return returnValue;
		}
});
});

