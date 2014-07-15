
scDefine(["dojo/text!./templates/ShipmentDetailsExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/ImageDataBinder","scbase/loader!sc/plat/dojo/layout/AdvancedTableLayout","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Label","scbase/loader!sc/plat/dojo/widgets/Link"]
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
			 ,
			    _scLink
){
return _dojodeclare("extn.shipment.search.ShipmentDetailsExtnUI",
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

,	  sequence: '19'

,	  description: 'check which info to show or hide'



,handler : {
methodName : "extInitialize"

 
 
}
}
,
{
	  eventId: 'extn_button_startPick_onClick'

,	  sequence: '51'

,	  description: 'start picking the shipment'



,handler : {
methodName : "openShipmentDetails"

 
 
}
}
,
{
	  eventId: 'extn_button_resumePick_onClick'

,	  sequence: '51'

,	  description: 'resumes the pick up of an order'



,handler : {
methodName : "resumePickUp"

 
 
}
}
,
{
	  eventId: 'extn_link:lblOrderNo_onClick'

,	  sequence: '51'




,handler : {
methodName : "openShipmentDetails"

 
 
}
}
,
{
	  eventId: 'extn_buttonMarkAsReturned_onClick'

,	  sequence: '51'

,	  description: 'This function changes status from pending expired return to expired order.'



,handler : {
methodName : "markAsReturned"

 
 
}
}
,
{
	  eventId: 'extn_button_startPick_onKeyUp'

,	  sequence: '19'

,	  description: 'start pickup'



,handler : {
methodName : "openShipmentDetails"

 
 
}
}

]
					 	 ,
}

});
});


