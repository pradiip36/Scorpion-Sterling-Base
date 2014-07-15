scDefine(["scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/customerpickup/ProductScanDetailsExtnUI",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/ContextUtils"],
	function(
			_dojodeclare,
			_extnProductScanDetailsExtnUI, 
			_scBaseUtils, 
			_scWidgetUtils,
			_isccsContextUtils
){ 
	return _dojodeclare("extn.shipment.customerpickup.ProductScanDetailsExtn", [_extnProductScanDetailsExtnUI],{
		// custom code here
		/*****************************
		 * @author: Adam Dunmars
		 * @subscribedTo: afterScreenInit(OOB)
		 * @description: This subscriber function occurs after
		 * the OOB initializeScreenfunction and displays the screen's widgets.
		 *****************************/
		extnInitialize : function(event, bEvent, ctrl, args) {
			this.displayWidgets();
		},
		
		/*****************************
		 * @author: Adam Dunmars
		 * @description: This function overrides the OOB
		 * updateLastProductScanned subscriber function to
		 * stop the UOM label from being shown and to display the
		 * extended item description label.
		 * TODO: Determine if there's a better way...probably not ~Adam
		 *****************************/
		updateLastProductScanned: function(event, bEvent, ctrl, args) {
            var isMobile = null;
            isMobile = _isccsContextUtils.getFromContext("isMobile");
            if (_scBaseUtils.isBooleanTrue(isMobile)) {
                _scWidgetUtils.showWidget(this, "itemdescriptionLink", false, null);
            } else {
                //_scWidgetUtils.showWidget(this, "itemdescriptionLabel", false, null);
                _scWidgetUtils.showWidget(this, "extn_itemDescriptionLabel", false, null);
            }
            //_scWidgetUtils.showWidget(this, "uomLabel", false, null);
            _scWidgetUtils.showWidget(this, "imagePanel", false, null);
        },
		        
		/***************************
		 * @author: Adam Dunmars
		 * @description: This function displays the screen's
		 * expected widgets based on the screen's class name.
		 ***************************/
		displayWidgets : function() {
			var screenName = this.getOwnerScreen().className;
			if(_scBaseUtils.equals(screenName, "ProductVerification")) {
				_scWidgetUtils.hideWidget(this, "extn_lastLocationScanned", false);
			}
		}
	});
});