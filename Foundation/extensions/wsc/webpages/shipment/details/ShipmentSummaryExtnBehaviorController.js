


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/details/ShipmentSummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnShipmentSummaryExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.details.ShipmentSummaryExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.details.ShipmentSummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
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

