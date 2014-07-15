
scDefine(["dojo/text!./templates/BPShipmentLineExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/NumberTextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/ImageDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Label","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			    _idxNumberTextBox
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scButtonDataBinder
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scImageDataBinder
			 ,
			    _scSimpleDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scDataLabel
			 ,
			    _scImage
			 ,
			    _scLabel
			 ,
			    _scLink
){
return _dojodeclare("extn.shipment.backroomPick.BPShipmentLineExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	
	

	,
	hotKeys: [ 
	]

,events : [
	]

,subscribers : {
global : [

{
	  eventId: 'successfulScan'

,	  sequence: '51'




,handler : {
methodName : "showCorrectShortageButton"

 
 
}
}

]
					 	 ,
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
	  eventId: 'updateShortageResolutionImage_onClick'

,	  sequence: '19'




,handler : {
methodName : "onClickShortage"

 
 
}
}
,
{
	  eventId: 'extn_buttonUndoButton_onClick'

,	  sequence: '51'

,	  description: 'Undo the scan'



,handler : {
methodName : "undoQuantity"

 
 
}
}
,
{
	  eventId: 'extn_linkShortDescription_onClick'

,	  sequence: '51'

,	  description: 'Opens item details'



,handler : {
methodName : "openItemDetails"

 
 
}
}
,
{
	  eventId: 'updateItemLocations'

,	  sequence: '51'

,	  description: 'this will update the item locations based on input'



,handler : {
methodName : "updateLocations"

 
 
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
	  eventId: 'checkForUndo'

,	  sequence: '51'




,handler : {
methodName : "checkForUndo"

 
 
}
}
,
{
	  eventId: 'extn_updateShortageResolutionImage_onClick'

,	  sequence: '51'

,	  description: 'This event is fired on click of the updaeShortageResolutionImage button'



,handler : {
methodName : "onClickShortage"

 
, description :  "This subscriber function does stuff (Insert description here)"  
}
}
,
{
	  eventId: 'extn_updateShortageResolutionImage_onClick'

,	  sequence: '52'




,handler : {
methodName : "handleShortageResolutionIcon"

 
, description :  "OOB (Does stuff add descr here)"  
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
	  eventId: 'successfulScan'

,	  sequence: '51'




,handler : {
methodName : "showCorrectShortageButton"

 
 
}
}

]
}

});
});


