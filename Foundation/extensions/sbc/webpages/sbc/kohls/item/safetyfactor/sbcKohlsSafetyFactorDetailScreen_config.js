Ext.namespace('sc.sbc.kohls.item.safetyfactor');

sc.sbc.kohls.item.safetyfactor.sbcKohlsSafetyFactorDetailScreenUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "sbcKohlsSafetyFactorDetailScreen",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "sbcKohlsSafetyFactorDetailPnl",
            title: "Manage Safety Factor",
            items: [{
                xtype: "panel",
                sciId: "sbcCategoryTreeScreen",
                title: "Catagory tree",
                region: "center",
                disableNodeForSubCatalog: "true",
                disableNodeForNonCatalog: "true",
                xtype: "sbcCategoryTreeScreen",
                width: 200,
                rowspan: 2
            },
            {
                xtype: "panel",
                sciId: "kohlsCategoryDetailHolderPnl",
                title: "Category Details",
                layout: "anchor",
                header: false,
                items: [{
                    xtype: "panel",
                    sciId: "pnlKohlsAdjustSafetyFactor",
                    title: "Adjust Safety Factor",
                    layout: "table",
                    height: 200,
                    items: [{
                        xtype: "label",
                        sciId: "lblCategoryBeingAdjusted",
                        text: "",
                        colspan: 6,
                        autoWidth: true,
                        cls: "sc-plat-label-left",
                        bindingData: {
                            defid: "object",
                            sourceBinding: "sourcemodel_test.CategoryPath"
                        }
                    },
                    {
                        xtype: "label",
                        sciId: "lblKohlsSafetyFactor",
                        text: "Modify By",
                        colspan: 2,
                        cls: "sc-plat-importantNonedit-text"
                    },
                    {
                        xtype: "radio",
                        sciId: "radManageSafetyFactorByPercentage",
                        boxLabel: "By Percentage",
                        handler: this.handlePercentageRadioButtons,
                        scope: this,
                        ctCls: "sc-plat-importantRadioLabel",
                        checked: true,
                        bindingData: {
                            defid: "object",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyOperation"],
                            selectedValue: "P"
                        }
                    },
                    {
                        xtype: "combo",
                        sciId: "cmbKohlsSafetyfactorOptions",
                        ctCls: "sc-plat-combo-selection",
                        cls: "sbc-standard-combo",
                        listClass: "sc-plat-combo-list-item",
                        readOnly: true,
                        triggerAction: "all",
                        displayField: "Option",
                        valueField: "Option",
                        mode: "local",
                        blankText: "Increase/Decrease",
                        bindingData: {
                            defid: "object",
                            sourceBinding: "manageSafetyFactor_namespace:ItemSafetyFactor.Operation",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyOperationPercentage"],
                            optionsBinding: "SafetyFactorOperations:Options"
                        },
                        store: new Ext.data.JsonStore({
                            defid: "jsonstore",
                            fields: ["Option"],
                            sortInfo: {
                                defid: "object",
                                field: "Option",
                                direction: "ASC"
                            }
                        })
                    },
                    {
                        xtype: "bignumberfield",
                        sciId: "txtKohlsSafetyFactorAdjustmentByPercentage",
                        bindingData: {
                            defid: "object",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyFactorPercentage"]
                        }
                    },
                    {
                        xtype: "label",
                        sciId: "lblPercentage",
                        text: "%"
                    },
                    {
                        xtype: "label",
                        sciId: "lblKohlsFiller",
                        text: ""
                    },
                    {
                        xtype: "radio",
                        sciId: "radManageSafetyFactorByQty",
                        colspan: 2,
                        boxLabel: "By Quantity",
                        handler: this.handleQtyRadioButtons,
                        scope: this,
                        ctCls: "sc-plat-importantRadioLabel",
                        bindingData: {
                            defid: "object",
                            selectedValue: "Q",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyOperation"]
                        }
                    },
                    {
                        xtype: "combo",
                        sciId: "cmbKohlsSafetyfactorOptionsForQty",
                        ctCls: "sc-plat-combo-selection",
                        cls: "sbc-standard-combo",
                        listClass: "sc-plat-combo-list-item",
                        readOnly: true,
                        triggerAction: "all",
                        displayField: "Option",
                        valueField: "Option",
                        mode: "local",
                        store: new Ext.data.JsonStore({
                            defid: "jsonstore",
                            fields: ["Option"],
                            sortInfo: {
                                defid: "object",
                                direction: "ASC",
                                field: "Option"
                            }
                        }),
                        bindingData: {
                            defid: "object",
                            sourceBinding: "manageSafetyFactor_namespace:ItemSafetyFactor.Operation",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyOperationQuantity"],
                            optionsBinding: "SafetyFactorOperations:Options"
                        }
                    },
                    {
                        xtype: "bignumberfield",
                        sciId: "txtKohlsSafetyFactorAdjustmentQty",
                        colspan: 2,
                        allowDecimals: false,
                        allowNegative: false,
                        bindingData: {
                            defid: "object",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.SafetyFactorQty"]
                        }
                    },
                    {
                        xtype: "label",
                        sciId: "lblDefaultSafetyFactor",
                        text: "Default Safety Factor",
                        cls: "sc-plat-importantNonedit-text",
                        colspan: 2
                    },
                    {
                        xtype: "bignumberfield",
                        sciId: "txtDefaultSafetyFactor",
                        allowDecimals: false,
                        allowNegative: false,
                        bindingData: {
                            defid: "object",
                            targetBinding: ["kohlsItemSafetyFactorUpdate_input:ExtnItemSafetyFactor.DefaultSafetyFactor"]
                        }
                    }],
                    rowspan: 1,
                    colspan: 4,
                    region: "center",
                    width: 980,
                    sctype: "ScreenMainPanel",
                    bbar: [{
                        xtype: "tbfill",
                        sciId: "tbfill1"
                    },
                    {
                        xtype: "button",
                        sciId: "btnAdjustSafetyFactor",
                        text: "Adjust Safety Factor",
                        handler: this.manageSafetyFactor,
                        scope: this,
                        iconCls: ""
                    }],
                    layoutConfig: {
                        defid: "tableLayoutConfig",
                        columns: 6
                    }
                },
                {
                    xtype: "grid",
                    sciId: "kohlsSafetyFactorItemsGrid",
                    title: "Category Item Details",
                    columns: [{
                        defid: "grid-column",
                        sciId: "kohlsItemId",
                        header: "Item Id",
                        sortable: true,
                        dataIndex: "ItemID",
                        width: 200,
                        align: "left",
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
                        sciId: "kohlsItemUOM",
                        header: "Unit Of Measure",
                        sortable: true,
                        dataIndex: "UnitOfMeasure",
                        width: 100,
                        bindingData: {
                            defid: "object",
                            sFieldConfig: {
                                defid: "object",
                                mapping: "UnitOfMeasure"
                            }
                        }
                    },
                    {
                        defid: "grid-column",
                        sciId: "kohlsItemDescription",
                        header: "Item Description",
                        sortable: true,
                        dataIndex: "PrimaryInformation.ShortDescription",
                        width: 430,
                        align: "left",
                        bindingData: {
                            defid: "object",
                            sFieldConfig: {
                                defid: "object",
                                mapping: "PrimaryInformation.ShortDescription"
                            }
                        }
                    },
                    {
                        defid: "grid-column",
                        sciId: "kohlsItemSafetyFactor",
                        header: "Onhand Safety Factor  (Qty)",
                        sortable: true,
                        width: 200,
                        align: "left",
                        bindingData: {
                            defid: "object",
                            sFieldConfig: {
                                defid: "object",
                                mapping: "InventoryParameters.OnhandSafetyFactorQuantity"
                            }
                        }
                    }],
                    border: true,
                    region: "south",
                    sctype: "GridPanel",
                    autoWidth: true,
                    bbar: this.pagingToolbar,
                    header: true,
                    stripeRows: true,
                    autoHeight: true,
                    bindingData: {
                        defid: "object",
                        sourceBinding: "categoryItemDetails_output:ItemList.Item",
                        pagination: {
                            defid: "pagination",
                            strategy: "NEXTPAGE",
                            root: "ItemList.Item",
                            pageSize: 15,
                            url: this.getUrl()
                        }
                    }
                }],
                autoHeight: true,
                autoScroll: true,
                columns: 1,
                height: 1500,
                border: false,
                autoWidth: true,
                layoutConfig: {
                    defid: "layoutConfig",
                    columns: 4
                }
            }],
            layout: "table",
            sctype: "ScreenMainPanel",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 4
            }
        }],
        sctype: "ScreenMainPanel"
    };
}