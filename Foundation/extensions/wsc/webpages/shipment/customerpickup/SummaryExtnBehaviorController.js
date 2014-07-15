


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/shipment/customerpickup/SummaryExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnSummaryExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.shipment.customerpickup.SummaryExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.shipment.customerpickup.SummaryExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptInfo_mashup'
,
		 mashupId : 			'extn_getSuspVoidReceipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getOrderDetail4Receipt'
,
		 mashupId : 			'extn_getOrderDetail4Receipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getUserFoReceipt'
,
		 mashupId : 			'extn_getUserFoReceipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipNode_mashup'
,
		 mashupId : 			'extn_getShipNode_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipmentDetails_receipt'
,
		 mashupId : 			'extn_getShipmentDetails_receipt_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getShipmentListForReceiptID'
,
		 mashupId : 			'extn_getShipmentListFor_receipt_maship'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJTransOrderPicked'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes_diamond'
,
		 mashupId : 			'extn_getCommonCodes_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getReceiptCommonCodes'
,
		 mashupId : 			'extn_getReceiptCommonCodes_mashup'

	}

	]

}
);
});

