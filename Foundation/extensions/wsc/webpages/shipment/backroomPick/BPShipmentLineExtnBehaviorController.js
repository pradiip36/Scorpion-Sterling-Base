


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/backroomPick/BPShipmentLineExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnBPShipmentLineExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.backroomPick.BPShipmentLineExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.backroomPick.BPShipmentLineExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_undoScan'
,
		 mashupId : 			'extn_undoScan'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_setShortageReason'
,
		 mashupId : 			'extnShortageReason'

	}

	]

}
);
});

