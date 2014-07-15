scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!sc/plat/dojo/controller/ScreenController", 
          "scbase/loader!extn/item/details/ItemImages"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _wscItemImages) {
    return _dojodeclare("extn.item.details.ItemImagesInitController", [_scScreenController], {
        screenId: 'extn.item.details.ItemImages',
    });
});