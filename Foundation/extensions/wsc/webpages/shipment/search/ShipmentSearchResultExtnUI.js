
scDefine(["dojo/text!./templates/ShipmentSearchResultExtn.html","scbase/loader!dojo/_base/declare","scbase/loader!dojo/_base/kernel","scbase/loader!dojo/_base/lang","scbase/loader!dojo/text","scbase/loader!sc/plat","scbase/loader!sc/plat/dojo/binding/CurrencyDataBinder","scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/widgets/Label"]
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
			    _scplat
			 ,
			    _scCurrencyDataBinder
			 ,
			    _scBaseUtils
			 ,
			    _scLabel
){
return _dojodeclare("extn.shipment.search.ShipmentSearchResultExtnUI",
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
	  eventId: 'callListApi'

,	  sequence: '18'

,	  description: 'apply custom filters'



,handler : {
methodName : "applyCustomFilters"

 
 
}
}
,
{
	  eventId: 'callListApi'

,	  sequence: '19'

,	  description: 'clear the enterprise code'



,handler : {
methodName : "clearEnterpriseCode"

 
 
}
}

]
					 	 ,
}

});
});


