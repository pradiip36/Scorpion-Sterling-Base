
scDefine(["dojo/text!./templates/AlertNotificationDetailsExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.alert.AlertNotificationDetailsExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	

	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {
local : [

{
	  eventId: 'afterScreenInit'

,	  sequence: '19'




,handler : {
methodName : "hideExistingButtons"

 
 
}
}
,
{
	  eventId: 'extn_button_pick_onClick'

,	  sequence: '51'




,handler : {
methodName : "pickReadyForPickOrders"

 
 
}
}

]
					 	 ,
}

});
});


