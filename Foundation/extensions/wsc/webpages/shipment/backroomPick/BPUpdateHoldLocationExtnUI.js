
scDefine(["dojo/text!./templates/BPUpdateHoldLocationExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
return _dojodeclare("extn.shipment.backroomPick.BPUpdateHoldLocationExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
		],
		sourceBindingNamespaces :
								
		[
			{
	  value: 'extn_holdLocationList'
						,
	  description: 'extended hold location info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_3'
						
			}
			
		]
	}

	

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

,	  description: 'custom initialization method'



,handler : {
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'afterScreenLoad'

,	  sequence: '51'




,handler : {
methodName : "hideWizardButtons"

 
 
}
}
,
{
	  eventId: 'extn_skipThisStepButton_onClick'

,	  sequence: '51'




,handler : {
methodName : "displayAssignLaterConfirmation"

 
 
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'

,	  description: 'custom mashup completion handler'



,handler : {
methodName : "handleExtnMashupCompletion"

 
 
}
}
,
{
	  eventId: 'extn_Complete_onClick'

,	  sequence: '51'




,handler : {
methodName : "displayCompleteConfirmation"

 
 
}
}

]
					 	 ,
}

});
});


