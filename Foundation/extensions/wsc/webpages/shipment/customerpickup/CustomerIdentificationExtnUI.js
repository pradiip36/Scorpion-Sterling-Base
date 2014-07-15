
scDefine(["dojo/text!./templates/CustomerIdentificationExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _idxFilteringSelect
			 ,
			    _idxTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scComboDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLabel
){
return _dojodeclare("extn.shipment.customerpickup.CustomerIdentificationExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_getShipmentLisrForReceiptID_void'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_void'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_3'
						
			}
			,
			{
	  value: 'extn_getShipmentDetailsForReceipt_void'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_4'
						
			}
			
		],
		sourceBindingNamespaces :
								
								
								
								
								
		[
			{
	  value: 'extn_getKohlsVerificationReasonList_output'
						,
	  description: 'output for verification reason'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_2'
						
			}
			,
			{
	  value: 'extn_receiptVoidMessage_info'
						,
	  description: 'void receipt info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_3'
						
			}
			,
			{
	  value: 'extn_shipNode_info_void'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_4'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_diamond'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_5'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_void'
						,
	  description: 'common codes for void receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_6'
						
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

,	  description: 'initializes entry stuff'



,handler : {
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'saveCurrentPage'

,	  sequence: '19'

,	  description: 'Manager Override'



,handler : {
methodName : "checkForOverride"

 
, description :  "Manager Override check"  
}
}
,
{
	  eventId: 'cmbCustVerfMethod_onChange'

,	  sequence: '51'

,	  description: 'check to see if exception is selected'



,handler : {
methodName : "checkForException"

 
 
}
}
,
{
	  eventId: 'extn_button_void_onClick'

,	  sequence: '51'

,	  description: 'This subscriber will display the void dialog box'



,handler : {
methodName : "displayVoidPrompt"

 
 
}
}
,
{
	  eventId: 'extn_button_void_onKeyUp'

,	  sequence: '51'




,handler : {
methodName : "displayVoidPrompt"

 
 
}
}
,
{
	  eventId: 'cmbCustVerfMethod_appValidation'

,	  sequence: '51'




,handler : {
methodName : "afterVerificationDropDownValidation"

 
 
}
}

]
					 	 ,
}

});
});


