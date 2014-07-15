scDefine([
          "scbase/loader!dojo/_base/declare",
          "scbase/loader!extn/shipment/backroomPick/AbandonedBackroomPickDialogExtnUI",
          "scbase/loader!sc/plat/dojo/utils/ScreenUtils",
          "scbase/loader!wsc/utils/BackroomPickUtils",
          "scbase/loader!sc/plat/dojo/utils/WidgetUtils",
          "scbase/loader!isccs/utils/UIUtils",
          "scbase/loader!sc/plat/dojo/utils/BaseUtils",
          "scbase/loader!sc/plat/dojo/utils/ModelUtils"
          ]
,
function(			 
			    _dojodeclare,
			    _extnAbandonedBackroomPickDialogExtnUI,
			    _scScreenUtils,
			    _wscBackroomPickUtils,
			    _scWidgetUtils,
			    _isccsUIUtils,
			    _scBaseUtils,
			    _scModelUtils
			    
){ 
	return _dojodeclare("extn.shipment.backroomPick.AbandonedBackroomPickDialogExtn", [_extnAbandonedBackroomPickDialogExtnUI],{
	// custom code here
		
		/*********************************
		 * @author: Adam Dunmars
		 * @subscribedTo: initializeScreen (OOB)
		 * @description: This subscriber function
		 * overrides initializes screen to change 
		 * the message shown on dialog popup.
		 * It displays the username using Shipment/Extn/@ExtnUsername if
		 * it is available. Otherwise it defaults to using Shipment/@AssignedToUserId.
		 * TODO: REMOVE OLD MASHUP REFERENCE THATS NOT BEING CALLED. ~Adam
		 *********************************/
		initializeScreen: function(event, bEvent, ctrl, args) {
			var shipmentModel = null;
			shipmentModel = _scScreenUtils.getModel(this, "Shipment");
			
			var username = (_scModelUtils.hasModelObjectForPathInModel("Shipment.Extn.ExtnUsername", shipmentModel)) ?
					_scModelUtils.getStringValueFromPath("Shipment.Extn.ExtnUsername", shipmentModel) :
					_scModelUtils.getStringValueFromPath("Shipment.AssignedToUserId", shipmentModel);
			
			var dataArray = _scBaseUtils.getNewArrayInstance();
			_scBaseUtils.appendToArray(dataArray, username);
			var warningString =  _scScreenUtils.getFormattedString(this,"BackroomPickInProgress",dataArray);
			_scWidgetUtils.setValue(this, "confirmationMessage", warningString, false);
			_scWidgetUtils.hideWidget(this, "Popup_navigationPanel", false);
	    },
	    
	    /*********************************
	     * @author: Adam Dunmars
	     * Handles mashup to set the warning string to the User/@Username
	     * If the user returns with a User/@Username we use that User/@Username,
	     * otherwise we are forced to default back to the User/@Loginid.
	     **********************************/
	    handleExtnMashupCompletion: function(event, bEvent, ctrl, args) {
			if (_scBaseUtils.equals(args.inputData.mashupRefId,"extn_getUserName")) {
				var mashupOutput = args.mashupObject.mashupRefOutput;
				var username = null;
				if(!_scBaseUtils.isVoid(mashupOutput.UserList)
					&& !_scBaseUtils.isVoid(mashupOutput.UserList.User)
					&& !_scBaseUtils.isVoid(mashupOutput.UserList.User.length)
					&& !_scBaseUtils.isVoid(mashupOutput.UserList.User[0].Username)) {
					
				} else {
					username = args.inputData.mashupInputObject.User.Loginid;
				}
				
				
			}
		}
	});
});