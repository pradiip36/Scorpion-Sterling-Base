
scDefine(["dojo/text!./templates/PickDetailsExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
			 ,
			    _scLabel
){
return _dojodeclare("extn.home.PickDetailsExtnUI",
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

,	  sequence: '18'

,	  description: 'Enable Resume Button'



,handler : {
methodName : "EnableResume"

 
 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '19'

,	  description: 'Enable the Update Hold Button'



,handler : {
methodName : "EnableUpdateHold"

 
 
}
}
,
{
	  eventId: 'extn_update_hold_onClick'

,	  sequence: '51'

,	  description: 'Update the hold location'



,handler : {
methodName : "updateHold"

 
, description :  "This Method Updates the hold locations"  
}
}
,
{
	  eventId: 'extn_continue_onClick'

,	  sequence: '51'

,	  description: 'Show Continue Button for different Statuses'



,handler : {
methodName : "showContinue"

 
, description :  "Show Continue Butoon"  
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'

,	  description: 'custom mashup handler event'



,handler : {
methodName : "handleExtnMashupCompletion"

 
 
}
}
,
{
	  eventId: 'extn_Resume_onClick'

,	  sequence: '51'

,	  description: 'Resume the picking process'



,handler : {
methodName : "addResumeEJRecord"

 
, description :  "This method resumes the picking process  at which point an audit entry will be created to indicate that the picking has resumed."  
}
}
,
{
	  eventId: 'extn_assignUser_onClick'

,	  sequence: '19'




,handler : {
methodName : "assignUser"

 
 
}
}
,
{
	  eventId: 'extn_assignUser_onKeyUp'

,	  sequence: '19'




,handler : {
methodName : "assignUser"

 
 
}
}

]
					 	 ,
}

});
});


