


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/ProductVerificationExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnProductVerificationExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.shipment.customerpickup.ProductVerificationExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.shipment.customerpickup.ProductVerificationExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_shipNode_info_suspend'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipNode_mashup'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getShipNode_mashup'

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
		 mashupRefId : 			'extn_diamon_comCodes'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}
,
	 		{
		 sourceNamespace : 			'extn_commCode4Receipt_suspend'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_receipt_commCodes'
,
		 callSequence : 			''
,
		 mashupId : 			'getReceiptCommonCodes'

	}

	]

}
);
});

