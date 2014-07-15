Ext.namespace('sc.sbc.kohls.item.ineligibility');

sc.sbc.kohls.item.ineligibility.VendorSearchResultsUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "seaVendorSearchResults",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "grid",
            sciId: "tblSearchResults",
            title: "Search Results",
            columns: [{
                defid: "grid-column",
                sciId: "clmVendor",
                header: "Vendor ID",
                sortable: true,
                renderer: this.showLinksWithImageInColumns,
                dataIndex: "OrganizationCode",
                width: 350,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "OrganizationCode"
                    }
                }
            },
            {
                defid: "grid-column",
                sciId: "clmVendorName",
                header: "Vendor Name",
                sortable: true,
                dataIndex: "OrganizationName",
                width: 400,
                bindingData: {
                    defid: "object",
                    sFieldConfig: {
                        defid: "object",
                        mapping: "OrganizationName"
                    }
                }
            }],
            sctype: "GridPanel",
            border: true,
            stripeRows: true,
            bindingData: {
                defid: "object",
                sourceBinding: "getVendorListOutput:OrganizationList.Organization"
            },
            listeners: {
                defid: "listeners",
                scope: this,
                cellclick: this.handleCellClick,
                headerClick: this.handleCellClick
            },
            height: 400
        }],
        autoWidth: true,
        height: 400
    };
}