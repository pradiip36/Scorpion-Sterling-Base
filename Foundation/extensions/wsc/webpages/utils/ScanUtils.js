/**
 * Adam Dunmars
 * IBM Corporation 2014
 * TODOs:
 * 	1. Remove JSON vanilla JavaScript reference and add a DOJO reference.
 *	2. Add an exception in case of incorrect parse?
 *	3. Surround the parse with a try/catch.
 *	4. Check to make sure page has been loaded before any of this is attempted using domReady!
 *	5. Remove the test data string after this is no longer needed.
 *	6. Insert some sound after for success.
 */

scDefine(["scbase/loader!dojo/_base/declare",
	"scbase/loader!dojo/_base/lang",
	"scbase/loader!sc/plat/dojo/utils/WizardUtils",
	"scbase/loader!sc/plat/dojo/utils/ScreenUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils"], 
	
function(
	_dojodeclare, 
	dLang,
	_scWizardUtils,
	_scScreenUtils,
	_scBaseUtils,
	_scEditorUtils
){
	var bopusUtils = dLang.getObject("extn.utils.ScanUtils", true);

	bopusUtils.parseBarcode = function(barcodeString) {
		var barcodeString = "\"BarCodeData\":\"400029121036\",\"BarCodeType\":\"EanJan13\",\"DeviceName\":null";
		
		//Parse the barcode data received from C#.
		if(barcodeString != '' && barcodeString != null) {
			var barcodeDataString = "{"+barcodeString+"}";
			console.log("Barcode String: "+barcodeDataString);
			
			var barcodeJSONArray = JSON.parse(barcodeDataString);				
			return barcodeJSONArray;
		} else { 
			return false;
		}

	},
	
	bopusUtils.updateShipmentLineContainer = function(screen, productScanDetailModel) {

		console.log("updateShipmentLineContainer");
		console.log("productScanDetailModel : ", productScanDetailModel);

		var type = "wsc.shipment.backroomPick.BPShipmentLine";
		var isOverragePresent = false, isProductFound = false;
		
		var lastProductScannedModel = {};
		lastProductScannedModel.ProductConsumed = "N";
		lastProductScannedModel.OrderLine = {};

		var PickAll_Input = {};
		var pickedQty = Number("0");
		var scannedItemID = "", scannedItemPresent = false;
		var shipmentLineKey = "";

		var barCodeDataModel = productScanDetailModel.BarCode;

		if (!scBaseUtils.isVoid(productScanDetailModel) 
				&& !scBaseUtils.isVoid(productScanDetailModel.BarCode) 
				&& !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations)) {

			if (scBaseUtils.equals("0", productScanDetailModel.BarCode.Translations.TotalNumberOfRecords)) {
				scScreenUtils.showWarningMessageBoxWithOk(screen, scScreenUtils.getString(screen, "NoProductFound"), "", "", "");
				return;
			}

			scannedItemPresent = true;
		} else {

			scScreenUtils.showWarningMessageBoxWithOk(screen, scScreenUtils.getString(screen, "NoProductFound"), "", "", "");
			return;
		}

		if (scannedItemPresent
			&& !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation)
			&& !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation.length)
			&& !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo)
			&& !scBaseUtils.isVoid(productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID)) {
				scannedItemID = productScanDetailModel.BarCode.Translations.Translation[0].ItemContextualInfo.ItemID;
				shipmentLineKey = productScanDetailModel.BarCode.Translations.Translation[0].ShipmentContextualInfo.ShipmentLineKey;
		} else {
			scScreenUtils.showWarningMessageBoxWithOk(screen, scScreenUtils.getString(screen, "NoProductFound"), "", "", "");
			return;
		}

		console.log("scannedItemID : ", scannedItemID);

		var container = screen.getWidgetByUId("PickUpItemContainer");
		console.log("container : ", container);
		var listOfChildren = container.getChildren(container);
		console.log("listOfChildren : ", listOfChildren);
		for (var i = 0; i < listOfChildren.length; i++) {
			var child = listOfChildren[i];
			console.log("Iteration : ", (i + 1));
			if (isccsUIUtils.instanceOf(child, type) || (child.screenId && child.screenId == type)) {
				if (child instanceof scScreen) {
					var shipmentLineOutput = scScreenUtils.getTargetModel(child, "ShipmentLine_Output", null);
					if (scBaseUtils.stringEquals(shipmentLineKey, shipmentLineOutput.ShipmentLine.ShipmentLineKey)) {
						isProductFound = true;
						lastProductScannedModel.ShipmentLineKey = shipmentLineOutput.ShipmentLine.ShipmentLineKey;
						lastProductScannedModel.ProductConsumed = "Y";
						lastProductScannedModel.screenUId = child.uId;
						lastProductScannedModel.OrderLine = shipmentLineOutput.ShipmentLine.OrderLine;
						scEventUtils.fireEventInsideScreen(child, "updateScannedProductQuantity", null, {});
						break;
					}
				}
			}
		}

		console.log("isProductFound : ", isProductFound);
		console.log("lastProductScannedModel : ", lastProductScannedModel);

		if (isProductFound && scBaseUtils.stringEquals("N", lastProductScannedModel.ProductConsumed)) {
			scScreenUtils.showWarningMessageBoxWithOk(child, scScreenUtils.getString(screen, "OverrageProductMessage"), "", "", "");
		} else if (!isProductFound) {
			scScreenUtils.showWarningMessageBoxWithOk(screen, scScreenUtils.getString(screen, "InvalidProductScanned"), "", "", "");
		} else if (isProductFound && scBaseUtils.stringEquals("Y", lastProductScannedModel.ProductConsumed)) {
			var lastScannedModelInWizard = isccsUIUtils.getWizardModel(screen, "lastScannedProduct");
			if (!scBaseUtils.isVoid(lastScannedModelInWizard)) {
				// Remove CSS for previously highlighted panel
				scWidgetUtils.removeClass(screen, lastScannedModelInWizard.screenUId, "highlightRepeatingPanel");
			}
			isccsUIUtils.setWizardModel(screen, "lastScannedProduct", lastProductScannedModel, null);
			// Add CSS to Highlight Last Product Scanned panel
			scWidgetUtils.addClass(screen, lastProductScannedModel.screenUId, "highlightRepeatingPanel");
			// scScreenUtils.scrollToWidget(screen,lastProductScannedModel.screenUId);

			console.log("Updating Last Product Scanned Model");
			scWidgetUtils.showWidget(screen,"lastScannedProductDetailPanel", false, null);
			var options = scBaseUtils.getNewBeanInstance();
			scBaseUtils.addBeanValueToBean("lastProductScannedModel", lastProductScannedModel, options);
			var childScreen = scScreenUtils.getChildScreen(screen, "lastProductScannedDetailsScreenRef");
			console.log("childScreen : ", childScreen);
			if (scBaseUtils.isVoid(childScreen)) {
				scScreenUtils.showChildScreen(screen, "lastProductScannedDetailsScreenRef", "", "", options, lastProductScannedModel);
				childScreen = scScreenUtils.getChildScreen(screen, "lastProductScannedDetailsScreenRef");
			}
			childScreen.setModel("lastProductScanned_output", lastProductScannedModel, "");
			scEventUtils.fireEventToChild(screen, "lastProductScannedDetailsScreenRef", "updateLastProductScanned", null);
		}

	},

	bopusUtils.parsePrint = function(/*printString*/) {
		var printString = "";
		if(printString != "" && printString != null) {
			var printDataString = "{"+printString+"}";
			console.log("Print String: "+printString);
			
			var receiptJSONArray = JSON.parse(printString);
			return receiptJSONArray;
		} else {
			return false;
		}		
	},

	bopusUtils.getCommCodeInput = function() {
		
		var commonCodeInput = {};
		commonCodeInput.CommonCode = {};
		commonCodeInput.CommonCode.CodeSymbol = "RECEIPT_MSG";

		return commonCodeInput; 
	},

	bopusUtils.getReceiptType = function(receiptType)
	{
		switch(receiptType)
		{
			case "voidpickup":
				return "VOID_RECEIPT";

			case "customerPickup":
				return "CUST_PICKUP_RECEIPT";

			case "storePickup":
				return "STORE_PICKUP_RECEIPT";

			case "giftNoItem":
				return "GIFT_RECEIPT";

			case "suspend":
				return "SUSPEND_RECEIPT";

			case "callback":
				return "CALLBACK_RECEIPT";

			case "return":
				return "RETURN_RECEIPT";
			default:
				console.error("UNSUPPORTED RECEIPT TYPE");
				return null;
		}

	},
	
	bopusUtils.getMessageModel = function(allCodes, receiptType)
	{
		if(allCodes == null)
			return null;
		
		var currentType = bopusUtils.getReceiptType(receiptType);
		var returnModel = null;	
		
		//using correctly incremented index numbers.		
		var index = 0;

		receiptModel = {};
		receiptModel.AllCodes = [];

		for(var i = 0; i<allCodes.CommonCodeList.CommonCode.length; i++)
		{
			var currentCode = allCodes.CommonCodeList.CommonCode[i];

			if(currentCode.CodeType == currentType)
			{			
				receiptModel.AllCodes[index] = currentCode;			
				index++;
			}
		}

		return receiptModel;

	},

	//TODO: This function shouldn't pass editorScreen just to close screen...~Adam
	bopusUtils.printReceipt = function(editorScreen, modelOutput, commonModel, storeInfo) {
		
		try {
			var strRegisterNum = "MISSING_DATA"; 
			strRegisterNum = window.systemInfo.GetRegisterNum(); 
			var strStoreNum = "MISSING_DATA";
			strStoreNum = window.systemInfo.GetStoreNum();
		} catch(err) {
			//_scScreenUtils.showErrorMessageBox(this, "Cannot print receipt!!!! Kohl's receipt printing service is not available, Message: " + err.message, null, null, null);
			if(!_scBaseUtils.isVoid(editorScreen)) {
				_scWizardUtils.closeWizard(editorScreen);
			}
			return null;
		}	
		
		var receiptType = modelOutput.ReceiptType;
		
		var isCallback = true;
		var gotError = false;
		var errorList = "";
		
		var totalItems = 0;		

		var currentdate = new Date(); 
		var cHour = currentdate.getHours();
		var ampm;  
		
		if(cHour >= 12) 
		{
			ampm = "PM"; 
			
			if(cHour >= 13)
				cHour = cHour - 12; 
		} 
		else 
		{
			ampm = "AM";
		}

	    var currentDateTime = (currentdate.getMonth()+1) + "-" + currentdate.getDate()  + "-" +	currentdate.getFullYear() + " " + cHour + ":" + currentdate.getMinutes() + " " + ampm;

	    var receiptData = bopusUtils.getReceiptJSON();
	    
	    receiptData.receipt.type = receiptType;
	    receiptData.receipt.datetime = currentDateTime;
	    receiptData.receipt.register = strRegisterNum;
		
	    //adding store info
	    try {
		    receiptData.receipt.store.address = storeInfo.ShipNodeList.ShipNode[0].ShipNodePersonInfo.AddressLine1;
		    receiptData.receipt.store.city = storeInfo.ShipNodeList.ShipNode[0].ShipNodePersonInfo.City;
		    receiptData.receipt.store.state = storeInfo.ShipNodeList.ShipNode[0].ShipNodePersonInfo.State;
		    receiptData.receipt.store.zipcode = storeInfo.ShipNodeList.ShipNode[0].ShipNodePersonInfo.ZipCode;
		    receiptData.receipt.store.phone = storeInfo.ShipNodeList.ShipNode[0].ShipNodePersonInfo.DayPhone;
		    receiptData.receipt.store.name = storeInfo.ShipNodeList.ShipNode[0].Organization.OrganizationName;
		    receiptData.receipt.store.storeNumber = strStoreNum;
	    } catch(err) {
	    	gotError = true;
	    	errorList += "Missing Store Info:(" + err.message + "), ";
	    }

	    try {
		    receiptData.receipt.customer.firstName = modelOutput.Shipment.BillToAddress.FirstName;
		    receiptData.receipt.customer.lastName =  modelOutput.Shipment.BillToAddress.LastName;
	    } catch(err) {
	    	gotError = true;
	    	errorList += "Missing Customer Info:(" + err.message + "), ";
	    }
	    
	    try {
	    	receiptData.receipt.order.orderNo = modelOutput.Shipment.OrderNo;
	    	var dateString = modelOutput.Shipment.ShipmentLines.ShipmentLine[0].Order.OrderDate;
		    var purchaseDate = dateString.substring(5,7) + "\/" + dateString.substring(8,10) + "\/" + dateString.substring(2,4);
			//"2014-04-03T15:09:06-04:00" date format from OMS	
		    receiptData.receipt.order.purchaseDate = purchaseDate;
	    } catch(err) {
	    	gotError = true;
	    	errorList += "Missing Order Info:(" + err.message + "), ";
	    }
		    
	    try {
	    	receiptData.receipt.order.receiptid = modelOutput.Shipment.Extn.ExtnStorePreencReceiptID;
	    } catch(err) {
	    	gotError = true;
	    	errorList += "Missing receipt number:(" + err.message + "), ";
	    }
	    
	    try {
		    if(_scBaseUtils.equals(receiptType, "callback"))  {
				var item = {};	
				item.item = {};			
				item.item.itemid = modelOutput.RecallItem.ItemID; //This needs to get the item UPC not SKU value.
				item.item.description = modelOutput.RecallItem.ItemDesc;
				item.item.productClass = modelOutput.RecallItem.ProductClass;
				item.item.department = modelOutput.RecallItem.Department;
				receiptData.receipt.order.lines[0] = item;
				totalItems++;
		    } else  {
		    	var shipLines = modelOutput.Shipment.ShipmentLines.ShipmentLine;
		    	
		    	//adding included items to json
		    	for(var i=0; i<shipLines.length; i++) {
					if(_scBaseUtils.equals(shipLines[i].OrderLine.HoldReasonCode, "CALLBACK")) {
						continue;
					}
					isCallback = false;
	
				    var item = {};	
					item.item = {};			
				    ///item.item.itemid = shipLines[i].ItemID;
					
					if(!_scBaseUtils.isVoid(shipLines[i].OrderLine.ItemDetails.ItemAliasList.ItemAlias[0].AliasValue))
					{
						item.item.itemid = shipLines[i].OrderLine.ItemDetails.ItemAliasList.ItemAlias[0].AliasValue;
					}
					
					item.item.orderedquantity = shipLines[i].Quantity;
				    item.item.description = shipLines[i].ItemDesc;
				
				    //adding diamond class
				    if(_scBaseUtils.equals(receiptType, "customerPickup") || _scBaseUtils.equals(receiptType, "voidpickup")){				    		
				    	if(!_scBaseUtils.isVoid(shipLines[i].OrderLine.ItemDetails.Extn.ExtnDept)){
					    	var isDiamond = bopusUtils.checkDiamondItem(shipLines[i].OrderLine.ItemDetails.Extn.ExtnDept, modelOutput.diamondModel);
					    	if(isDiamond){
					    		item.item.productClass = "diamondClass";
					    	}
					    }
				    }
				    receiptData.receipt.order.lines[i] = item;
					totalItems++;
			    }

				if(isCallback) {
					return;		
				}
		    } 
		    
		    //gift receipts start with 798
		    if(_scBaseUtils.equals(receiptType, "giftNoItem")) {
			    var tempID = receiptData.receipt.order.receiptid;
			    if(!_scBaseUtils.isVoid(tempID)) {
			    	receiptData.receipt.order.receiptid = tempID.replace(tempID.substring(0,3), "798");
			    }
		    } 
		} catch(err) {
		   	gotError = true;
		   	errorList += "Missing orerline info:(" + err.message + "), ";
		}
		    
	    receiptData.receipt.order.totalNumberOfItems = totalItems;
	    //modelOutput.Shipment.ShipmentLines.TotalNumberOfRecords;

	    try {
		    var commCodes = bopusUtils.getMessageModel(commonModel, receiptType);	
		    for(var i = 0; i<commCodes.AllCodes.length; i++) {
		    	var codeLine = commCodes.AllCodes[i];
		    	var newCode = commCodes.AllCodes[i].CodeValue;
		    	
		    	switch(newCode) {
		    		case "MESSAGE_1":
		    			receiptData.receipt.message1 = bopusUtils.getCommonCodeValue(codeLine);
		    			break;
		    		case "MESSAGE_2":
		    			receiptData.receipt.message2 = bopusUtils.getCommonCodeValue(codeLine);
		    			break;
		    		case "MESSAGE_3":
		    			receiptData.receipt.message3 = bopusUtils.getCommonCodeValue(codeLine);
		    			break;
		    		case "BRAND_MESSAGE":
		    			receiptData.receipt.brandMessage = bopusUtils.getCommonCodeValue(codeLine);
		    			break;
	    			default:
	    				console.error("Unsupported newCode value: "+newCode);
	    				break;
		    	}
		    }
	    } catch(err) {
		   	gotError = true;
		   	errorList += "Missing messages common codes:(" + err.message + "), ";
		}

		var userObject = JSON.parse(sessionStorage.userInformation);
		var userID = userObject.CurrentUser.User.UserKey;
	    receiptData.receipt.associate = userID;

		var receiptJson = JSON.stringify(receiptData);
		//_scScreenUtils.showErrorMessageBox(this, receiptJson, null, null, null);
		
		if(gotError) {
			//_scScreenUtils.showErrorMessageBox(this, "Following Error(s) happened during printing::: " + errorList, null, null, null);
			console.error("reciept printing error lisg: " + errorList);
		}
		try {
			window.printDoc.Print(receiptJson);
		} catch(err) {
			alert("Kohls external receipt printing error, Message: " + err.message);
		}

		if(editorScreen !=null) {
			_scWizardUtils.closeWizard(editorScreen);
		}
	},
	
	bopusUtils.checkDiamondItem = function(itemDept, diamondModel)
	{
		var isDiamond = false;
		
		if(itemDept != "")
		{
			for(var i = 0; i<diamondModel.CommonCodeList.CommonCode.length; i++)
			{
				if(itemDept == diamondModel.CommonCodeList.CommonCode[i].CodeValue)
					isDiamond = true;
				
			}
			
		}
			
		return isDiamond;	
	},
	
	bopusUtils.getCommonCodeValue = function(codeLine)
	{
		var attributeList = codeLine.CommonCodeAttributeList.CommonCodeAttribute;
		var allValues = "";
		var arySize = attributeList.length;
		
		for(var i = 0; i<arySize; i++)
		{
						
			if(!_scBaseUtils.isVoid(attributeList[i].Value))
			{
				allValues += attributeList[i].Value;
				
				if(i < arySize - 1)
					allValues += "|";
			}
			
		

		}

		return allValues;
	},

	bopusUtils.getReceiptJSON = function()
	{
        var baseJSONObject ={
                            "receipt": {
                                        "type":"MISSING_DATA", 
	                                    "datetime":"MISSING_DATA",
	                                    "register":"MISSING_DATA",
                                        "store":{
        		                                "address":"MISSING_DATA",
        		                                "city":"MISSING_DATA",
        		                                "state":"MISSING_DATA",
        		                                "zipcode":"MISSING_DATA",
        		                                "phone": "MISSING_DATA",
		                                        "storeNumber":"MISSING_DATA",
		                                        "name":"MISSING_DATA"
        		                                },
					"customer":{
							"firstName":"MISSING_DATA",
							"lastName":"MISSING_DATA"
						   },
                                        "order":{
                                                "receiptid":"MISSING_DATA",
						"lines":[{
							
							"item":{
								"itemid":"MISSING_DATA",
								"description":"MISSING_DATA"
							       }
							}],
                			                    "totalNumberOfItems":"MISSING_DATA"
        		                                },
                                        "message1": "",
                                        "message2": "",
					"message3":"",
	                                "brandMessage":"",
	                                "associate":"MISSING_DATA" 
                                        }
                            }
		return baseJSONObject;
	
	},
	
	bopusUtils.getCurrentWizardInstance = function(){
		var editor = _scEditorUtils.getCurrentEditor();
		var wizard = _scEditorUtils.getScreenInstance(editor);
		if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
			wizard = wizard.getCurrentWizardInstance();
		}
		return wizard;
	},
	
	bopusUtils.getWizardForScreen = function(screen) {
		if(screen.wizardUI){
	        	return screen;
        } else {
            var force = true;
            var parentScreen = screen.getOwnerScreen(force);
            if(parentScreen){
                return this.getWizardForScreen(parentScreen);
            }
        }
        return null;
	}
	
	return bopusUtils;
	
});
