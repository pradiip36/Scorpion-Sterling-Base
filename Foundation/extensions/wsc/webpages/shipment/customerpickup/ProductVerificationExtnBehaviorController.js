


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/ProductVerificationExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnProductVerificationExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.customerpickup.ProductVerificationExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.customerpickup.ProductVerificationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getPickupInfo_mashup'
,
		 mashupId : 			'extn_getShipment_pickupInfo'

	}
,
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
		 mashupRefId : 			'extn_getConfigurablePrompts_mashup'
,
		 mashupId : 			'extn_getConfigurablePrompts'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJTransCustomerVerified'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipmentListForReceiptID_suspend'
,
		 mashupId : 			'extn_getShipmentListFor_receipt_maship'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes_suspend'
,
		 mashupId : 			'extn_getReceiptCommonCodes_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipmentDetails_receipt_suspend'
,
		 mashupId : 			'extn_getShipmentDetails_receipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes_diamond'
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}

	]

}
);
});

