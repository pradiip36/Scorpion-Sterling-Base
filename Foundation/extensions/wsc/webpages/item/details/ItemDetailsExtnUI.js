
scDefine(["dojo/text!./templates/ItemDetailsExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!extn/item/details/ItemLocationsInitController","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/binding/ImageDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/ControllerWidget","scbase/loader!sc/plat/dojo/widgets/DataLabel","scbase/loader!sc/plat/dojo/widgets/IdentifierControllerWidget","scbase/loader!sc/plat/dojo/widgets/Image","scbase/loader!sc/plat/dojo/widgets/Label"]
 , function(			 
			    templateText
			 ,
			    _dojodeclare
			 ,
			    _dojokernel
			 ,
			    _dojolang
			 ,
			    _dojotext
			 ,
			    _extnItemLocationsInitController
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scImageDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scControllerWidget
			 ,
			    _scDataLabel
			 ,
			    _scIdentifierControllerWidget
			 ,
			    _scImage
			 ,
			    _scLabel
){
return _dojodeclare("extn.item.details.ItemDetailsExtnUI",
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
	  value: 'extn_ShipmentLine'
						,
	  description: 'This is the shipment line that contains the item for this screen to be displayed'
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

]
					 	 ,
}

});
});


