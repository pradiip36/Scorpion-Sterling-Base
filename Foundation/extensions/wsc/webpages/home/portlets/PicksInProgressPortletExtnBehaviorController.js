


scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/text","scbase/loader!extn/home/portlets/PicksInProgressPortletExtn","scbase/loader!sc/plat/dojo/controller/ExtnServerDataController"]
 , function(			 
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojotext
			 ,
			    _extnPicksInProgressPortletExtn
			 ,
			    _scExtnServerDataController
){

return _dojodeclare("extn.home.portlets.PicksInProgressPortletExtnBehaviorController", 
				[_scExtnServerDataController], {

			
			 screenId : 			'extn.home.portlets.PicksInProgressPortletExtn'

			
			
			
			
			
						,

			
			
			 mashupRefs : 	[
	 		{
		 extnType : 			'ADD'
,
		 mashupRefId : 			'extn_addEJTransPickupResumed'
,
		 mashupId : 			'extn_EJRecord_mashup'

	}

	]

}
);
});

