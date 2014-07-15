
scDefine(["dojo/text!./templates/PickupOrderLineDetailsExtn.html","scbase/loader!dijit/form/Button","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!idx/form/FilteringSelect","scbase/loader!idx/form/NumberTextBox","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/ButtonDataBinder","scbase/loader!sc/plat/dojo/binding/ComboDataBinder","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/ImageDataBinder","scbase/loader!sc/plat/dojo/binding/SimpleDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _idxNumberTextBox
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
){
return _dojodeclare("extn.shipment.customerpickup.PickupOrderLineDetailsExtnUI",
				[], {
			templateString: templateText
	
	
	
	
	
	


, staticBindings : [

		{
				
				

			 	targetBinding : 	{
		 path : 			'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ShortDescription'
,
		 namespace : 			'ShipmentLine_Output'

	}
,			 
			 	sourceBinding : 	{
		 path : 			'ShipmentLine.OrderLine.ItemDetails.PrimaryInformation.ShortDescription'
,
		 namespace : 			'ShipmentLine'

	}
			 
		
		}	
	]
	

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
	  eventId: 'extn_button_undo_onClick'

,	  sequence: '51'

,	  description: 'decrease the scanned quantity'



,handler : {
methodName : "undoQuantity"

 
 
}
}

]
					 	 ,
}

});
});


