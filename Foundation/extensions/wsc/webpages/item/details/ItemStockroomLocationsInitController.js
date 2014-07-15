scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!sc/plat/dojo/controller/ScreenController", 
          "scbase/loader!extn/item/details/ItemStockroomLocations"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _wscItemStockroomLocations) {
    return _dojodeclare("extn.item.details.ItemStockroomLocationsInitController", [_scScreenController], {
        screenId: 'extn.item.details.ItemStockroomLocations',
    });
});