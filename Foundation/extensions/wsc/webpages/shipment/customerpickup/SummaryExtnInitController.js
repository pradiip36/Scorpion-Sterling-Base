


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/SummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnSummaryExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.shipment.customerpickup.SummaryExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.shipment.customerpickup.SummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 sourceNamespace : 			'extn_shipNode_info'
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
		 mashupRefId : 			'extn_getReceiptCommonCodes_diamond'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}
,
	 		{
		 sourceNamespace : 			'extn_commCode4Receipt_output'
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

