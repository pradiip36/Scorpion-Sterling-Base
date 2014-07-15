scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/home/portlets/PicksInProgressPortletExtnUI","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!isccs/utils/UIUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnPicksInProgressPortletExtnUI
			 , _scBaseUtils
			 , _scWidgetUtils
			 , _scModelUtils
			 , _isccsUIUtils
){ 
	return _dojodeclare("extn.home.portlets.PicksInProgressPortletExtn", [_extnPicksInProgressPortletExtnUI],{
	// custom code here
	extInitialize : function() {
		if (!(
            _scBaseUtils.isVoid("picksInProgressContainer"))) {
                if (
                _scBaseUtils.equals(
                this.firstClickEvent, "true")) {
                    _scWidgetUtils.showWidget(
                    this, "picksInProgressContainer", false, null);
                    var completeInputModel = null;
                    completeInputModel = _scModelUtils.createNewModelObjectWithRootKey("Shipment");
                    _isccsUIUtils.callApi(
                    this, completeInputModel, "getShipmentList", null);
                    this.firstClickEvent = "false";
                } else {
                    if (
                    _scWidgetUtils.isWidgetVisible(
                    this, "picksInProgressContainer")) {
                        _scWidgetUtils.hideWidget(
                        this, "picksInProgressContainer", false);
                    } else {
                        _scWidgetUtils.showWidget(
                        this, "picksInProgressContainer", false, null);
                    }
                }
            }

	}
});
});

