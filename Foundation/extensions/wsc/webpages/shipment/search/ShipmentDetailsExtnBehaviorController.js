


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/search/ShipmentDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.search.ShipmentDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.search.ShipmentDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
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
		 mashupRefId : 			'extn_MarkAsReturned'
,
		 mashupId : 			'extn_MarkAsReturned_mashup'

	}

	]

}
);
});

