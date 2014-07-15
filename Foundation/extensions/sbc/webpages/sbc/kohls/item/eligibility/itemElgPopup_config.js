Ext.namespace('sc.sbc.kohls.item.eligibility');

sc.sbc.kohls.item.eligibility.itemElgPopupUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "screen7",
        header: false,
        layout: "anchor",
        autoScroll: true,
        items: [{
            xtype: "panel",
            sciId: "panel9",
            title: "",
            layout: "table",
            layoutConfig: {
                defid: "tableLayoutConfig",
                columns: 2
            },
            items: [{
                xtype: "label",
                sciId: "label11",
                text: "Set As Eligible From:",				
                width: 200
            }, 
			{
                    xtype: "combo",
                    sciId: "cmbItemCharacteristic",
                    ctCls: "sc-plat-combo-selection",
                    cls: "sbc-standard-combo",
                    listClass: "sc-plat-combo-list-item",
                    mode: "local",
                    readOnly: true,					
                    triggerAction: "all",
                   // displayField: "ItemId", valueField: "ItemId",
					displayField: "CodeShortDescription",
                    valueField: "CodeShortDescription",
                    store: new Ext.data.JsonStore({
                        defid: "jsonstore",
                        //fields: ["ItemId"],
						fields: ["CodeShortDescription"],
                        sortInfo: {
                            defid: "object",
                            //field: "ItemId",
							field: "CodeShortDescription",
                            direction: "ASC"
                        }
                    }),
                    bindingData: {
                        defid: "object",
                        sourceBinding: "SavedSearch:SearchWrapper.Item.ItemCharacteristic",
                        targetBinding: ["itemElgPopupUIOut:ItemList.Item"],
                       // optionsBinding: "itemStatus_input:ItemList.Item"
					    optionsBinding: "itemStatus_output:output.CommonCodeList.CommonCode"
                    }
                },
            {
                xtype: "spacer",
                sciId: "spacer20"
            },
            {
                xtype: "spacer",
                sciId: "spacer21"
            },
            {
                xtype: "label",
                sciId: "label13",
                text: "Default new items with this setting?"
            },
            {
                xtype: "checkbox",
                sciId: "checkbox6",
				bindingData: {
                    defid: "object6",
					checkedValue: "Y",
                    unCheckedValue: "N",
                    //sourceBinding: "copyPricelistElem:pricelistcopyList.NameToDefault",
                    targetBinding: ["itemElgPopupUIOut:checkbox6"]
                }
            }]
        }],
			width:400
    };
}