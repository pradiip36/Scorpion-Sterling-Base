
scDefine(["dojo/text!./templates/ResolutionExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
return _dojodeclare("extn.shipment.customerpickup.ResolutionExtnUI",
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
	  value: 'extn_shipNode_info_suspend'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_5'
						
			}
			,
			{
	  value: 'extn_SuspendCommonCodes'
						,
	  description: 'Suspend Common Codes'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_6'
						
			}
			,
			{
	  value: 'extn_DiamondCommonCodes'
						,
	  description: 'Diamond Common Codes'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						
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

,	  sequence: '51'




,handler : {
methodName : "extnInitialize"

 
 
}
}
,
{
	  eventId: 'extn_voidButton_onClick'

,	  sequence: '51'




,handler : {
methodName : "voidShipmentLines"

 
 
}
}
,
{
	  eventId: 'extn_voidButton_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "voidShipmentLines"

 
 
}
}
,
{
	  eventId: 'extn_suspendButton_onClick'

,	  sequence: '51'




,handler : {
methodName : "suspendShipment"

 
 
}
}
,
{
	  eventId: 'extn_suspendButton_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "suspendShipment"

 
 
}
}

]
					 	 ,
}

});
});


