


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/CustomerIdentificationExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerIdentificationExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.customerpickup.CustomerIdentificationExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.customerpickup.CustomerIdentificationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJforBeginPickup'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_changeShipmentPickUp'
,
		 mashupId : 			'extn_changeShipmentPickUp_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_chkMgrOverride_mashup'
,
		 mashupId : 			'extn_chkMgrOverride_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_saveMgrOverride_mashup'
,
		 mashupId : 			'extn_saveMgrOverride_mashup'

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

