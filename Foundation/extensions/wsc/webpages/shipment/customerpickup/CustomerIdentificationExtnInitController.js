


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/CustomerIdentificationExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnCustomerIdentificationExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.shipment.customerpickup.CustomerIdentificationExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.shipment.customerpickup.CustomerIdentificationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_shipNode_info_void'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipNode_mashup_void'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getShipNode_mashup'

	}
,
	 		{
		 sourceNamespace : 			'extn_getKohlsVerificationReasonList_output'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getKohlsCustomerVerificationList'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getKohlsCustomerVerificationList'

	}
,
	 		{
		 sourceNamespace : 			'extn_commCode4Receipt_diamond'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes_diamond'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}
,
	 		{
		 sourceNamespace : 			'extn_commCode4Receipt_void'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes'
,
		 callSequence : 			''
,
		 mashupId : 			'getReceiptCommonCodes'

	}

	]

}
);
});

