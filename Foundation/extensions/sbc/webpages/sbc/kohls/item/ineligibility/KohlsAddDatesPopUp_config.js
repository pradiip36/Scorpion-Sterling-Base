Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsAddDatesPopUpUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "scrAddDatesPop",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "pnlAddItems",
            title: "Add Ineligible Items",
            layout: "table",
            height: 150,
            sctype: "ScreenMainPanel",
            width: 450,
            items: [{
                xtype: "label",
                sciId: "lblFromDate",
                text: "Effective Start Date",
                cls: "sc-plat-label"
            },
            {
                xtype: "datefield",
                sciId: "fldStartDate",
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.FromDate"]
                }
            },
            {
                xtype: "label",
                sciId: "lblToDate",
                text: "Effective End Date",
                cls: "sc-plat-label"
            },
            {
                xtype: "datefield",
                sciId: "fldToDate",
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.ToDate"]
                }
            }],
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            }
        }],
        autoHeight: true,
        autoWidth: false,
        width: 450
    };
}