
scDefine(["dojo/text!./templates/ShipmentSearchExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/CheckBox","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CheckBoxDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _idxCheckBox
			 ,
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCheckBoxDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.shipment.search.ShipmentSearchExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_expiredOrders_input'
						,
	  description: 'check to see if search for expired orders '
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						
			}
			
		],
		sourceBindingNamespaces :
		[
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

,	  description: 'custom initialization'



,handler : {
methodName : "extInitialize"

 
 
}
}

]
					 	 ,
}

});
});


