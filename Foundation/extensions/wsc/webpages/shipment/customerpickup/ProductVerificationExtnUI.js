
scDefine(["dojo/text!./templates/ProductVerificationExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel"]
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
			    _scDataLabel
){
return _dojodeclare("extn.shipment.customerpickup.ProductVerificationExtnUI",
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
	  value: 'extn_shipmentInfo_input'
						,
	  description: 'additional shipment info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_6'
						
			}
			,
			{
	  value: 'extn_shipNode_info'
						,
	  description: 'ship node information'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						
			}
			,
			{
	  value: 'extn_receiptSuspendMessage_info'
						,
	  description: 'suspend receipt info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_8'
						
			}
			,
			{
	  value: 'extn_receiptVoidMessage_info'
						,
	  description: 'void receipt message info'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_9'
						
			}
			,
			{
	  value: 'extn_getShipmentLisrForReceiptID_suspend'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_10'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_suspend'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_11'
						
			}
			,
			{
	  value: 'extn_shipNode_info_suspend'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_12'
						
			}
			,
			{
	  value: 'extn_getShipmentDetailsForReceipt_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_13'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_diamond'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_14'
						
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
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'afterScreenInit'

,	  sequence: '52'

,	  description: 'adds EJ record for customer verification'



,handler : {
methodName : "addEJEntryCustomerVerified"

 
 
}
}
,
{
	  eventId: 'extn_button_suspend_onClick'

,	  sequence: '51'

,	  description: 'save the pickup information and suspend'



,handler : {
methodName : "suspendPickup"

 
 
}
}
,
{
	  eventId: 'extn_button_void_onClick'

,	  sequence: '51'

,	  description: 'process void op'



,handler : {
methodName : "voidOrder"

 
 
}
}

]
					 	 ,
}

});
});


