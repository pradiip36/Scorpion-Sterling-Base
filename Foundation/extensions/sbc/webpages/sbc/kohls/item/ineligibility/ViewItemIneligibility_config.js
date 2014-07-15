Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.KohlsViewItemsUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen3",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "grid",
            sciId: "ineligibilityGrid",
            title: "Ineligible Items For Store",
            columns: [this.checkBoxSelectionModel, {
                defid: "grid-column",
                sciId: "ItemID",
                header: "ItemID",
                sortable: true,
                dataIndex: "ItemID",
                width: 100,
                align: "Center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ItemID"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "ClassID",
                header: "ClassID",
                sortable: true,
                dataIndex: "ClassID",
                width: 200,
                align: "Center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ClassID"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "Style",
                header: "Style",
                sortable: true,
                dataIndex: "Style",
                width: 100,
                align: "Center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Style"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "StartDate",
                header: "Effective Start Date",
                sortable: true,
                width: 200,
                dataIndex: "FromDate",
                align: "Center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "FromDate"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "EndDate",
                sortable: true,
                align: "Center",
                header: "Effective End Date",
                width: 200,
                dataIndex: "ToDate",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ToDate"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "Actions",
                sortable: true,
                align: "Center",
                header: "Actions",
                width: 200,
                layout: "anchor",
                renderer: this.showLinksWithImageInColumns,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        defaultValue: this.getModifyAction()
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "Hidden",
                align: "Center",
                hidden: true,
                dataIndex: "ExtnStoreInEligblKey",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "ExtnStoreInEligblKey"
                    }
                }
            }],
            border: true,
            stripeRows: true,
            selModel: this.checkBoxSelectionModel,
            sctype: "InnerGridPanel",
            width: 1000,
            tbar: [{
                xtype: "button",
                sciId: "inElgDelButton",
                text: "Delete",
                handler: this.deletInelgHandler,
                scope: this
            }],
            bbar: this.pagingToolbar,
            autoHeight: true,
            bindingData: {
                defid: "object",
                sourceBinding: "kohlsitemList_output:ExtnItemInEligibilityList.ExtnItemInEligibility",
                pagination: {
                    defid: "pagination",
                    pageSize: 15,
                    root: "ExtnItemInEligibilityList.ExtnItemInEligibility",
                    url: this.getUrl(),
                    strategy: "NEXTPAGE"
                }
            },
            listeners: {
                defid: "listeners",
                scope: this,
                cellclick: this.handlerOpenModifyPopup
            }
        }]
    };
}