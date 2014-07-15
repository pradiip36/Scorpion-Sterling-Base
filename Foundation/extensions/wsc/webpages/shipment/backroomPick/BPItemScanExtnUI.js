
scDefine(["dojo/text!./templates/BPItemScanExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dijit/form/TextBox","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/TextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Label"]
 , function(			 
			    templateText
			 ,
			    _dijitButton
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
			 ,
			    _scLabel
){
return _dojodeclare("extn.shipment.backroomPick.BPItemScanExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
					,	
	namespaces : {
		targetBindingNamespaces :
		[
			{
	  value: 'extn_mlsLocationsModel'
						,
	  description: 'Holds mappings of MLS Locations to Items'
						,
	  scExtensibilityArrayItemId: 'extn_TargetNamespaces_2'
						
			}
			
		],
		sourceBindingNamespaces :
								
		[
			{
	  value: 'extn_itemLocations_output'
						,
	  description: 'all the item information'
						,
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_5'
						
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
	  eventId: 'afterScreenLoad'

,	  sequence: '51'




,handler : {
methodName : "displayWidgets"

 
 
}
}
,
{
	  eventId: 'afterScreenLoad'

,	  sequence: '52'




,handler : {
methodName : "hideNextButton"

 
 
}
}
,
{
	  eventId: 'afterScreenLoad'

,	  sequence: '53'




,handler : {
methodName : "checkContextForShipmentLines"

 
 
}
}
,
{
	  eventId: 'extn_btnItemPickingLocation_onClick'

,	  sequence: '51'




,handler : {
methodName : "filterbyPickingLocation"

 
 
}
}
,
{
	  eventId: 'extn_buttonComplete_onClick'

,	  sequence: '51'

,	  description: 'Transitions to Hold location screen after associate has finished picking.'



,handler : {
methodName : "completeButtonAction"

 
 
}
}
,
{
	  eventId: 'extn_buttonRefresh_onClick'

,	  sequence: '51'

,	  description: 'Refreshes the screen'



,handler : {
methodName : "handleReloadScreen"

 
 
}
}
,
{
	  eventId: 'extn_buttonSuspend_onClick'

,	  sequence: '51'

,	  description: 'Suspends the order and return to home screen'



,handler : {
methodName : "suspendButtonAction"

 
 
}
}
,
{
	  eventId: 'extn_buttonStartPick_onClick'

,	  sequence: '51'

,	  description: 'Transitions to the picking screen'



,handler : {
methodName : "startPickButtonAction"

 
 
}
}
,
{
	  eventId: 'pickingContextChanged'

,	  sequence: '51'




,handler : {
methodName : "checkContext"

 
 
}
}
,
{
	  eventId: 'onExtnMashupCompletion'

,	  sequence: '51'




,handler : {
methodName : "handleExtnMashupCompletion"

 
 
}
}
,
{
	  eventId: 'afterBehaviorMashupCall'

,	  sequence: '51'




,handler : {
methodName : "handleAfterBehavior"

 
 
}
}

]
					 	 ,
}

});
});


