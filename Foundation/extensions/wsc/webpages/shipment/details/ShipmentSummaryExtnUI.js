
scDefine(["dojo/text!./templates/ShipmentSummaryExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/layout/ContentPane","scbase/loader!idx/layout/TitlePane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/ImageDataBinder","scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _idxTitlePane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scImageDataBinder
			 ,
			    _scAdvancedTableLayout
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
			 ,
			    _scImage
			 ,
			    _scLabel
){
return _dojodeclare("extn.shipment.details.ShipmentSummaryExtnUI",
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
	  value: 'extn_commCode4Receipt_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_1'
						
			}
			,
			{
	  value: 'extn_shipNode_info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_2'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_diamond'
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

,	  sequence: '51'




,handler : {
methodName : "extnInitialize"

 
 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '52'




,handler : {
methodName : "closeSearchResults"

 
 
}
}
,
{
	  eventId: 'extn_buttonStartCustPickUp_onClick'

,	  sequence: '51'

,	  description: 'button to start customer pickup'



,handler : {
methodName : "Ink_RT_CustomerPick_onClick"

 
 
}
}
,
{
	  eventId: 'extn_buttonMarkReturn_onClick'

,	  sequence: '51'

,	  description: 'The function changes status from pending expired return to expired order.'



,handler : {
methodName : "markAsReturned"

 
 
}
}
,
{
	  eventId: 'extn_buttonResumePicking_onClick'

,	  sequence: '51'

,	  description: 'The function resumes customer pickup for suspended orders.'



,handler : {
methodName : "resumePickUp"

 
 
}
}

]
					 	 ,
}

});
});


