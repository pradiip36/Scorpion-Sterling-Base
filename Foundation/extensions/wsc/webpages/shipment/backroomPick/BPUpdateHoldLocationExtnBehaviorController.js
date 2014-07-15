


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/backroomPick/BPUpdateHoldLocationExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnBPUpdateHoldLocationExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.backroomPick.BPUpdateHoldLocationExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.backroomPick.BPUpdateHoldLocationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_change_status_br_picked'
,
		 mashupId : 			'backroomPick_changeShipmentStatusToBackroomPicked'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getLocationDetails_mashup'
,
		 mashupId : 			'extn_getLocationDetails_mashup'

	}

	]

}
);
});

