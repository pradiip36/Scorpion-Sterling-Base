


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/home/PickDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPickDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.home.PickDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.home.PickDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getItemLocations_mashup'
,
		 mashupId : 			'extn_getItemLocationDetails_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJTransPickupResumed'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_assignUser'
,
		 mashupId : 			'assignUser'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_changeShipmentToInProgress'
,
		 mashupId : 			'backroomPick_changeShipmentStatusToInProgress'

	}

	]

}
);
});

