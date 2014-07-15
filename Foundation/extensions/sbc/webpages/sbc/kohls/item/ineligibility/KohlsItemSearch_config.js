Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsItemSearchUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "KohlsItemSearch",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "pnlItemSearch",
            title: "Set Ineligible Items for Store",
            layout: "table",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            },
            items: [{
                xtype: "label",
                sciId: "lblCategory",
                text: "Associated to Category",
                cls: "sc-plat-label"
            },
            {
                xtype: "trigger",
                sciId: "triggerItemSearchCategory",
                triggerClass: "x-form-search-trigger",
                onTriggerClick: "this.selectCategory",
                scope: this,
                ctCls: "sc-plat-triggerfield-text",
                cls: "sbc-standard-combo",
                readOnly: true,
                editable: false,
                colspan: 2,
                bindingData: {
                    defid: "object",
                    sourceBinding: "SavedSearch:SearchWrapper.Item.CategoryFilter.CategoryShortDescription",
                    targetBinding: ["itemSearch_input:Item.CategoryFilter.CategoryShortDescription"]
                },
                hiddenFieldSciId: ["sbcCategoryPath"]
            },
            {
                xtype: "label",
                sciId: "lblStyle",
                text: "Style Contains",
                cls: "sc-plat-label"
            },
            {
                xtype: "textfield",
                sciId: "txtStyle",
                cls: "sc-plat-editable-text sbc-search-textfield",
                colspan: 2,
                bindingData: {
                    defid: "object",
                    sourceBinding: "SavedSearch:SearchWrapper.Item.ItemID",
                    targetBinding: ["itemSearch_input:Item.Extn.ExtnStyle"]
                }
            },
            {
                xtype: "label",
                sciId: "lblSKU",
                text: "Style Contains",
                cls: "sc-plat-label"
            },
            {
                xtype: "textfield",
                sciId: "txtSKU",
                cls: "sc-plat-editable-text sbc-search-textfield",
                colspan: 2,
                bindingData: {
                    defid: "object",
                    sourceBinding: "SavedSearch:SearchWrapper.Item.ItemID",
                    targetBinding: ["itemSearch_input:Item.ItemID"]
                }
            },
            {
                xtype: "button",
                sciId: "btnSearch",
                text: "Search",
                handler: this.search,
                scope: this
            }]
        }]
    };
}