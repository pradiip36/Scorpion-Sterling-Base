Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.BasicVendorSearchUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "seaBasicVendorSearch",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "pnlBasicSearch",
            title: "Find Vendor",
            layout: "table",
            ctCls: "panelWidth",
            border: false,
            header: false,
            items: [{
                xtype: "label",
                sciId: "lblVendorID",
                text: "VendorID",
                cls: "sc-plat-label"
            },
            {
                xtype: "combo",
                sciId: "cmbContains",
                editable: false,
                value: ["Contains"],
                width: 100,
                colspan: 1
            },
            {
                xtype: "textfield",
                sciId: "txtVendorID",
                cls: "sc-plat-editable-text sbc-search-textfield",
                colspan: 1,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getVendorListInput:Organization.OrganizationCode"],
                    sourceBinding: "SavedSearch:SearchWrapper.Organization.OrganizationCode"
                }
            },
            {
                xtype: "label",
                sciId: "lblName",
                text: "Name",
                cls: "sc-plat-label"
            },
            {
                xtype: "combo",
                sciId: "cmbContains1",
                width: 100,
                editable: false,
                readOnly: true,
                value: "Contains"
            },
            {
                xtype: "textfield",
                sciId: "txtVendorName",
                cls: "sc-plat-editable-text sbc-search-textfield",
                colspan: 2,
                bindingData: {
                    defid: "object",
                    targetBinding: ["getVendorListInput:Organization.OrganizationName"],
                    sourceBinding: "SavedSearch:SearchWrapper.Organization.OrganizationName"
                }
            }],
            autoHeight: false,
            autoWidth: true,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 3
            }
        }]
    };
}