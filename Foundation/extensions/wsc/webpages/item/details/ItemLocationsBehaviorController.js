scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!sc/plat/dojo/controller/ServerDataController", 
          "scbase/loader!extn/item/details/ItemLocations"
          ], 
		  function(
			  _dojodeclare, 
			  _dojokernel, 
			  _dojotext, 
			  _scServerDataController, 
			  _wscItemLocations) 
			  {

	return _dojodeclare("extn.item.details.ItemLocationsBehaviorController", [_scServerDataController], {
        screenId: 'extn.item.details.ItemLocations'
    });
});