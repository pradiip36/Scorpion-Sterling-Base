
scDefine(["dojo/text!./templates/ItemMoreImagesExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!extn/item/details/ItemImages","scbase/loader!idx/layout/ContentPane","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/utils/BaseUtils"]
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
			    _extnItemImages
			 ,
			    _idxContentPane
			 ,
			    _scplat
			 ,
			    _scBaseUtils
){
return _dojodeclare("extn.item.details.ItemMoreImagesExtnUI",
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
	  scExtensibilityArrayItemId: 'extn_SourceNamespaces_0'
						
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
methodName : "getShipmentLineModel"

 
 
}
}

]
					 	 ,
}

});
});


