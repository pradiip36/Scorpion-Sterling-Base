scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!sc/plat/dojo/controller/ServerDataController", 
          "scbase/loader!extn/item/details/ItemStockroomLocations"
          ], 
		  function(
			  _dojodeclare, 
			  _dojokernel, 
			  _dojotext, 
			  _scServerDataController, 
			  _wscItemStockroomLocations) 
			  {

	return _dojodeclare("extn.item.details.ItemStockroomLocationsBehaviorController", [_scServerDataController], {
        screenId: 'extn.item.details.ItemStockroomLocations'
    });
});