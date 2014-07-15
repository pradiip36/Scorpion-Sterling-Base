


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/backroomPick/AbandonedBackroomPickDialogExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnAbandonedBackroomPickDialogExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.backroomPick.AbandonedBackroomPickDialogExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.backroomPick.AbandonedBackroomPickDialogExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getUserName'
,
		 mashupId : 			'extn_getUsername'

	}

	]

}
);
});

