
scDefine(["dojo/text!./templates/PickupOrderPortletExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLabel
			 ,
			    _scLink
){
return _dojodeclare("extn.home.portlets.PickupOrderPortletExtnUI",
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

,	  sequence: '51'

,	  description: 'test'



,handler : {
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'

,	  description: 'extended mashup handler'



,handler : {
methodName : "extnHandleMashupCompletion"

 
 
}
}
,
{
	  eventId: 'extn_buttonAdvanceSearch_onClick'

,	  sequence: '51'




,handler : {
methodName : "advanceSearchAction"

 
 
}
}

]
					 	 ,
}

});
});


