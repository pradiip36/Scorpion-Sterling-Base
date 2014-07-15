


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/ResolutionExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnResolutionExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.customerpickup.ResolutionExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.customerpickup.ResolutionExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_saveSuspendedPickup_mashup'
,
		 mashupId : 			'extn_changeShipment_suspend'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipmentDetailsForReceipt_suspend'
,
		 mashupId : 			'extn_getShipmentDetails_receipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_commCode4Receipt_diamond'
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJTransCustomerVerified'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}

	]

}
);
});

