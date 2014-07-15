


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/home/portlets/PickupOrderPortletExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPickupOrderPortletExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.home.portlets.PickupOrderPortletExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.home.portlets.PickupOrderPortletExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_getSessionInfo_mashup'
,
		 mashupId : 			'extn_getSessionInfo_mashup'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_searchReceiptId'
,
		 mashupId : 			'extn_getShipmentsByReceiptId'

	}
,
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_searchPickupOrders'
,
		 mashupId : 			'extn_getShipmentListForPickup'

	}

	]

}
);
});

