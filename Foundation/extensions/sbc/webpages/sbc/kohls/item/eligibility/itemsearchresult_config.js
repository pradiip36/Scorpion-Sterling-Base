Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.ItemSearchResultsUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "seaItemSearchResults",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "grid",
            sciId: "seaItemSearchResultsGrid",
            title: "Select / Set Items",
            columns: [this.checkboxSelectionModel, this.checkboxSelectionModel, {
                defid: "grid-column",
                sciId: "seaItemSearchItemId",
                header: "ItemID",
                sortable: true,
                dataIndex: "ItemID",
                width: 200,
                align: "center",
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
                sciId: "seaItemSearchDescription",
                header: "ShortDescription",
                sortable: true,
                dataIndex: "ShortDescription",
                width: 200,
                align: "center",
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
                sciId: "seaItemSearchUOM",
                header: "UOM",
                sortable: true,
                dataIndex: "UnitOfMeasure",
                id: "seaItemSearchUOM",
                width: 80,
                align: "center",
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
                sciId: "seaItemSearchDept",
                header: "Department",
                sortable: true,
                dataIndex: "ExtnDept",
                width: 180,
                align: "center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Extn.ExtnDept"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "seaItemSearchClass",
                header: "Class",
                sortable: true,
                dataIndex: "ExtnClass",
                width: 150,
                align: "center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Extn.ExtnClass"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "seaItemSearchSubclass",
                header: "Subclass",
                sortable: true,
                dataIndex: "ExtnSubClass",
                width: 200,
                align: "center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Extn.ExtnSubClass"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "seaItemSearchStyle",
                header: "Style",
                sortable: true,
                dataIndex: "ExtnStyle",
                width: 200,
                align: "center",
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "Extn.ExtnStyle"
                    }
                }
            }],
            header: true,
            border: true,
            selModel: this.checkboxSelectionModel,
            autoHeight: true,
            stripeRows: true,
            sctype: "GridPanel",
            tbar: [
			{
                xtype: "button",
                sciId: "btnBulkUpdate",
                text: "Bulk Update",
                handler: this.bukUpdateHandler,				
                scope: this,
				disabled: true
            },
            {
                xtype: "button",
                sciId: "btnSetInelg",
                text: "Set as Eligible",
                handler: this.selUpdBttnHandler,
                scope: this,
				disabled: true
            }],
            bbar: this.pagingToolbar,
            bindingData: {
                defid: "object",
                sourceBinding: "itemSearch_output:ItemList.Item",
                pagination: {
					defid: "object", 
                    strategy: "NEXTPAGE",
                    root: "ItemList.Item",
                    pageSize: 15,
                    url: this.getUrl()
                }
            },
            viewConfig: {
                defid: "viewConfig",
                scope: this,
                getRowClass: this.getRowClass,
                emptyText: sc.plat.bundle["b_NoRecordsToDisplay"],
                deferEmptyText: false
            }
        }],
        border: false
    };
}