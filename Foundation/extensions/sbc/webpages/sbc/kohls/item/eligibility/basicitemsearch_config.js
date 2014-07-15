Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.BasicItemSearchUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "seaBasicItemSearch3",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "hidden",
            sciId: "sbcItemIDQryType",
            value: "LIKE",
            bindingData: {
                defid: "object",
                targetBinding: ["itemSearch_input:Item.ItemIDQryType"],
                sourceBinding: "SavedSearch:SearchWrapper.Item.ItemIDQryType"
            }
        },
        {
            xtype: "hidden",
            sciId: "sbcCategoryPath",
            bindingData: {
                defid: "object",
                sourceBinding: "SavedSearch:SearchWrapper.Item.CategoryFilter.CategoryPath",
                targetBinding: ["itemSearch_input:Item.CategoryFilter.CategoryPath"]
            }
        },
        {
            xtype: "panel",
            sciId: "pnlBasicItemSearchMain",
            layout: "table",
            items: [{
                xtype: "panel",
                sciId: "pnlBasicSearch",
                layout: "table",
                items: [{
                    xtype: "label",
                    sciId: "lblItemSearchCategory",
                    text: "Associated to Category",
                    cls: "sc-plat-label"
                },
                {
                    xtype: "trigger",
                    sciId: "triggerItemSearchCategory",
                    triggerClass: "x-form-search-trigger",
                    onTriggerClick: this.selectCategory,
                    scope: this,
                    ctCls: "sc-plat-triggerfield-text",
                    cls: "sbc-standard-combo",
                    readOnly: true,
                    editable: false,
                    hiddenFieldSciId: ["sbcCategoryPath"],
                    colspan: 2,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "SavedSearch:SearchWrapper.Item.CategoryFilter.CategoryShortDescription",
                        targetBinding: ["itemSearch_input:Item.CategoryFilter.CategoryShortDescription"]
                    }
                },
                {
                    xtype: "label",
                    sciId: "lblItemSearchVendor",
                    text: "Vendor or Brand name",
                    cls: "sc-plat-label"
                },
                {
                    xtype: "trigger",
                    sciId: "triggerItemSearchVendor",
                    triggerClass: "x-form-search-trigger",
                    onTriggerClick: this.selectVendor,
                    ctCls: "sc-plat-triggerfield-text",
                    cls: "sbc-standard-combo",
                    readOnly: true,
                    editable: false,
                    hiddenFieldSciId: ["sbcVendor"],
                    colspan: 2,
                    scope: this,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "SavedSearch:SearchWrapper.Item.PrimaryInformation.PrimarySupplier",
                        targetBinding: ["itemSearch_input:Item.PrimaryInformation.PrimarySupplier"]
                    }
                },
                {
                    xtype: "label",
                    sciId: "lblItemSearchItemId",
                    text: "SKU Contains",
                    cls: "sc-plat-label"
                },
                {
                    xtype: "textfield",
                    sciId: "txtItemSearchItemId",
                    cls: "sc-plat-editable-text sbc-search-textfield",
                    colspan: 2,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "SavedSearch:SearchWrapper.Item.ItemID",
                        targetBinding: ["itemSearch_input:Item.ItemID"]
                    }
                },
                {
                    xtype: "label",
                    sciId: "lblItemShortDescription",
                    text: "Style Contains",
                    cls: "sc-plat-label"
                },
                {
                    xtype: "textfield",
                    sciId: "txtItemSearchStyle",
                    cls: "sc-plat-editable-text sbc-search-textfield",
                    bindingData: {
                        defid: "object",
                        sourceBinding: "SavedSearch:SearchWrapper.Item.Extn.ExtnStyle",
                        targetBinding: ["itemSearch_input:Item.Extn.ExtnStyle"]
                    }
                }],
                border: false,
                layoutConfig: {
                    defid: "tableLayoutConfig",
                    columns: 2
                }
            },
            {
                xtype: "panel",
                sciId: "panel3",
                layout: "table",
                border: false,
                width: 50,
                layoutConfig: {
                    defid: "tableLayoutConfig",
                    columns: 4
                }
            }],
            border: false,
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            }
        }],
        autoWidth: false,
        border: false
    };
}