scDefine(["scbase/loader!dojo/_base/declare",
	"scbase/loader!extn/home/HomeExtnUI",
	"scbase/loader!isccs/utils/UIUtils",
	"scbase/loader!sc/plat/dojo/utils/EditorUtils",
	"scbase/loader!sc/plat/dojo/utils/EventUtils",
	"scbase/loader!sc/plat/dojo/utils/BaseUtils"
],
function(			 
	_dojodeclare,
	_extnHomeExtnUI,  
	_isccsUIUtils,
	_scEditorUtils,
	_scEventUtils,
	_scBaseUtils
){ 
	return _dojodeclare("extn.home.HomeExtn", [_extnHomeExtnUI],{
	// custom code here
	
	/***********************************
	 * @author: Adam Dunmars
	 * @subscribedTo: afterScreenInit
	 * @description: This subscriber function occurs after the initializeScreen function
	 * in the Home screen.
	 ***********************************/
	extnInitialize : function(event, bEvent, ctrl, args) {
		this.closeTestEditor();
	},
	
	/*********************************
	 * @author: Adam Dunmars
	 * @description: This function closes the Test Editor that's opened
	 * after the Home Screen's initialized.
	 ********************************/
	closeTestEditor: function() {
		var openEditors = _scEditorUtils.getSpecificOpenEditors("isccs.editors.TestScreenEditor");
		var testPageEditor = null;
		for(var i = 0; i < openEditors.length; i++) {
			if(_scBaseUtils.equals(openEditors[i].editorInstance.className, "TestScreenEditor")){
				testPageEditor = openEditors[i].editorInstance;
				_scEditorUtils.closeEditor(testPageEditor);
				break;
			}
		}		
	},
		
	/******************************************
	 * @author: Adam Dunmars
 	 * @description: This function sets the barcode scanned in the Order No/ Receipt ID Text field
	 * and fires off the search.
	 ******************************************/
	setBarcode : function(barcodeString) {
		console.log(barcodeString);

		var screen = this.getWidgetByUId("pickupOrderPortlet");
		var uId = "txtOrderNo";
		var barcode = barcodeString.BarCodeData;
		var dirty = false;
		
		sc.plat.dojo.utils.WidgetUtils.setValue(screen, uId, barcode, dirty); 
	
		screen.pickUpOrderSearchAction();
	}
});
});

/***************************************************************************************************
 * @author: Adam Dunmars
 * -----------------------------------------
 * This function will be called by the scanner in the CEFSharp application whenever the 
 * physical scanner is used.
 * 
 * The function will:
 * 	1. Get the current editor instance.
 * 	2. Get the current wizard instance.
 * 	3. Check to see if the wizard is a vershion of the WizardUI widget(gotcha):
 *		a. If it is, get the current wizard instance(instead of the generic?)
 * 		b. Otherwise use the wizard.
 * 	4. If the screen contains the function setBarcode it will call its version of the function.
 ***************************************************************************************************/
function scannedBarcode(barcodeString) {
	var editor = sc.plat.dojo.utils.EditorUtils.getCurrentEditor();
	var wizard = sc.plat.dojo.utils.EditorUtils.getScreenInstance(editor); 
	if(wizard instanceof sc.plat.dojo.widgets.WizardUI) {
		wizard = wizard.getCurrentWizardInstance();
		var screen = sc.plat.dojo.utils.WizardUtils.getCurrentPage(wizard);
	} else if(wizard instanceof sc.plat.dojo.widgets.Screen) { // There was no wizard
		var screen = wizard;
	} else {
		console.log("Unsupported Type");
	}
	console.log("in setBarcode");
	if(typeof screen.setBarcode == "function") {	
		screen.setBarcode(barcodeString);
	}
}
