


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/backroomPick/BPItemScanExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnBPItemScanExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.backroomPick.BPItemScanExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.backroomPick.BPItemScanExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_resolve_exception'
,
		 mashupId : 			'alertNotificationDetails_resolveException'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getItemLocation_mashup'
,
		 mashupId : 			'extn_getItemLocationDetails_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addSuspendEJEntry'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getLocationDetails'
,
		 mashupId : 			'extn_getLocationDetails_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_refreshShipmentDetails'
,
		 mashupId : 			'getShipmentDetailsMLSGIV'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getMLSGIVDetails'
,
		 mashupId : 			'extn_getMLSGIVDetails'

	}

	]

}
);
});

