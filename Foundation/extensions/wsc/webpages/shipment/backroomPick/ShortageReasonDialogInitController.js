scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!sc/plat/dojo/controller/ScreenController", 
          "scbase/loader!extn/shipment/backroomPick/ShortageReasonDialog"], function(
_dojodeclare, _dojokernel, _dojotext, _scScreenController, _wscShortageReasonDialog) {
    return _dojodeclare("extn.shipment.backroomPick.ShortageReasonDialogInitController", [_scScreenController], {
        screenId: 'extn.shipment.backroomPick.ShortageReasonDialog',
        mashupRefs: [{
            sourceNamespace: 'extn_getShortageResolutions_input',
            mashupRefId: 'extn_getShortageResolutions',
            sequence: '',
            mashupId: 'extn_getShortageResolutions',
            callSequence: '',
            sourceBindingOptions: ''
        }]
    });
});