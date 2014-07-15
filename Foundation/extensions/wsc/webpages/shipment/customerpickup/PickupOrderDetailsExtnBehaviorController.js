


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/PickupOrderDetailsExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPickupOrderDetailsExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.customerpickup.PickupOrderDetailsExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.customerpickup.PickupOrderDetailsExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJforBeginPickup '
,
		 mashupId : 			'extn_EJRecord_mashup'

	}

	]

}
);
});

