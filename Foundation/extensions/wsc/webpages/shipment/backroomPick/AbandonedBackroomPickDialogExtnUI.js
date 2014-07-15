
scDefine(["dojo/text!./templates/AbandonedBackroomPickDialogExtn.html","scbase/loader!dijit/form/TextBox","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
 , function(			 
			    templateText
			 ,
			    _dijitTextBox
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
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.shipment.backroomPick.AbandonedBackroomPickDialogExtnUI",
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
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'




,handler : {
methodName : "handleExtnMashupCompletion"

 
 
}
}

]
					 	 ,
}

});
});


