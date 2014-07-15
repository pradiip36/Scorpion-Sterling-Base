


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/ResolutionExtn","scbase/loader!sc/plat/dojo/controller/ExtnScreenController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnResolutionExtn
			 ,
			    _scExtnScreenController
){

return _dojodeclare("extn.shipment.customerpickup.ResolutionExtnInitController", 
				[_scExtnScreenController], {

			
			 screenId : 			'extn.shipment.customerpickup.ResolutionExtn'

			
			
			
			
			
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
		 sourceNamespace : 			'extn_DiamondCommonCodes'
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
		 sourceNamespace : 			'extn_SuspendCommonCodes'
,
		 sequence : 			''
,
		 sourceBindingOptions : 			''
,
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes_suspend'
,
		 callSequence : 			''
,
		 mashupId : 			'extn_getReceiptCommonCodes_mashup'

	}

	]

}
);
});

