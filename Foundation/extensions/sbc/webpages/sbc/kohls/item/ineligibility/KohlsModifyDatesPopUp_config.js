Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsModifyDatesPopUpUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen5",
        header: false,
        layout: "anchor",
        autoScroll: false,
        items: [{
            xtype: "panel",
            sciId: "pnlModifyItems",
            title: "Modify Ineligible Items",
            layout: "table",
            border: false,
            items: [{
                xtype: "label",
                sciId: "lblSKU",
                text: "SKU",
                cls: "sc-plat-label"
            },
            {
                xtype: "textfield",
                sciId: "txtSKU",
                cls: "sc-plat-noneditable-text",
                readOnly: true,
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.ItemID"]
                }
            },
            {
                xtype: "label",
                sciId: "lblClassID",
                text: "ClassID",
                cls: "sc-plat-label"
            },
            {
                xtype: "textfield",
                sciId: "txtClassID",
                cls: "sc-plat-noneditable-text",
                readOnly: true,
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.ClassID"]
                }
            },
            {
                xtype: "label",
                sciId: "lblStyle",
                text: "Style",
                cls: "sc-plat-label"
            },
            {
                xtype: "textfield",
                sciId: "txtStyle",
                cls: "sc-plat-editable-text",
                readOnly: true,
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.Style"]
                }
            },
            {
                xtype: "label",
                sciId: "lblStartDate",
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
                sciId: "lblEndDate",
                text: "Effective End Date",
                cls: "sc-plat-label"
            },
            {
                xtype: "datefield",
                sciId: "fldEndDate",
                width: 200,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.ToDate"]
                }
            },
            {
                xtype: "textfield",
                sciId: "txtKey",
                hidden: true,
                bindingData: {
                    defid: "object",
                    targetBinding: ["adjustDatesModelTarget:ExtnItemInEligibility.ExtnStoreInEligblKey"]
                }
            }],
            height: 250,
            sctype: "ScreenMainPanel",
            autoWidth: false,
            width: 450,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            }
        }],
        border: false,
        autoHeight: true,
        autoWidth: false,
        width: 450
    };
}