
scDefine(["dojo/text!./templates/SummaryExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _scBaseUtils
			 ,
			    _scLabel
){
return _dojodeclare("extn.shipment.customerpickup.SummaryExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_getShipmentLisrForReceiptID_output'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_0'
						
			}
			,
			{
	  value: 'extn_receiptUserInfo_output'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_1'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_output'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_diamond'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_3'
						
			}
			
		],
		sourceBindingNamespaces :
								
								
								
								
								
								
								
								
								
		[
			{
	  value: 'extn_shipNode_info'
						,
	  description: 'the store information'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_1'
						
			}
			,
			{
	  value: 'extn_receiptSuspendMessage_info'
						,
	  description: 'suspend receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_2'
						
			}
			,
			{
	  value: 'extn_receiptVoidMessage_info'
						,
	  description: 'void receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_3'
						
			}
			,
			{
	  value: 'extn_receiptGiftMessage_info'
						,
	  description: 'gift receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_4'
						
			}
			,
			{
	  value: 'extn_receiptReturnMessage_info'
						,
	  description: 'return receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_5'
						
			}
			,
			{
	  value: 'extn_GiftReceiptOptions'
						,
	  description: 'Namespace to store number of gift receipts'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_6'
						
			}
			,
			{
	  value: 'extn_getShipmentDetailsForReceipt_output'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_7'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_diamond'
						,
	  description: 'common codes for diamond departments'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_8'
						
			}
			,
			{
	  value: 'extn_commCode4Receipt_output'
						,
	  description: 'common codes for receipt messages'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_9'
						
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
	  eventId: 'afterScreenLoad'

,	  sequence: '51'




,handler : {
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'extn_testPrintBtn_onClick'

,	  sequence: '51'

,	  description: 'tests printertes'



,handler : {
methodName : "testPrinter"

 
 
}
}
,
{
	  eventId: 'extn_print_btn_onClick'

,	  sequence: '51'

,	  description: 'test print'



,handler : {
methodName : "runPrintJob"

 
 
}
}
,
{
	  eventId: 'extn_button_void_onClick'

,	  sequence: '51'

,	  description: 'voids the current order'



,handler : {
methodName : "voidOrder"

 
 
}
}
,
{
	  eventId: 'extn_btn_void_onClick'

,	  sequence: '51'

,	  description: 'voids order'



,handler : {
methodName : "voidOrder"

 
 
}
}
,
{
	  eventId: 'extn_btn_previous_onClick'

,	  sequence: '51'




,handler : {
methodName : "handlePrevious"

 
 
}
}

]
					 	 ,
}

});
});


