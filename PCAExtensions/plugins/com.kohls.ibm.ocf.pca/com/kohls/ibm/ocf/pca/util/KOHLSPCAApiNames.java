/**
 *  © Copyright  2009 Sterling Commerce, Inc.
 */
package com.kohls.ibm.ocf.pca.util;



public class KOHLSPCAApiNames {
	
	//These entries are commands in the ycml file of custom plugin or SOP plugin
	
	public static final String API_TRANSLATE_BAR_CODE = "TranslateBarCode";
    public static final String API_GET_COMPLETE_ITEM_LIST = "getCompleteItemList";
	public static final String API_GET_SHIPMENT_DETAILS_FOR_PC = "KOHLSGetShipmentDetailsForPackingService";
	public static final String API_GET_ITEM_LIST_FOR_PC = "GetItemListForPackContainer";
	public static final String API_CHANGE_SHIPMENT_CONTAINER_FOR_PC = "ChangeShipmentContainer";
	public static final String API_GET_ITEM_LIST_FOR_ORDERING = "GetItemListForOrdering";
	public static final String API_GET_ITEM_LIST = "GetItemList";
	public static final String API_GET_DEVICE_LIST = "GetDeviceList";
	public static final String API_KOHLS_PRINT_PACK_SLIP_SERVICE = "KOHLSProcessPrintPackSlipService";
	public static final String API_KOHLS_ADD_CONTAINER_TO_SHIPMENT_SERVICE = "KOHLSAddContainerToShipmentService";
	public static final String API_SHP_DTLS_RAISE_EVENT = "shpDtls_RaiseEvent";
	public static final String API_REPRINT_PACK_SLIP_SERVICE = "KOHLSRePrintPackSlipService";
	public static final String API_CONFIRM_CUSTOMER_PICK = "ConfirmCustomerPick";
	public static final String API_CONFIRM_CUSTOMER_PICK_SERVICE = "ConfirmCustomerPick";
	public static final String API_KOHLS_MODIFY_STORE_SHIPMENT = "KohlsModifyStoreShipment";
	public static final String API_CHANGE_SHIPMENT_STATUS = "changeShipmentStatus";
	public static final String API_REPRINT_PICK_SLIP_SERVICE = "KOHLSSopPrintPickTicketService";
	public static final String API_KOHLS_GET_STATUS_LIST = "KOHLSGetStatusList";
	public static final String API_SHP_SEARCH_GET_SHIPMENT_LIST = "shpSearch_GetShipmentList";
	public static final String API_SHP_SEARCH_GET_SHIPMENT_LIST_NEXT = "shpSearch_GetShipmentListNext";
	public static final String API_GET_MASS_PRINT_SINGLES_LIST = "KOHLSGetMassPrintSinglesListService";
	public static final String API_INVOKE_SINGLES_MASS_PRINT = "KOHLSInvokeSinglesMassPrintService";
	public static final String API_GET_AVAILABLE_CARTS = "KOHLSGetAvailableCartsForPrintMultis";
	// Start Drop2 changes for Batch Print
	public static final String API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE = "KOHLSProcessReprintBatchService";
	//public static final String API_KOHLS_PROCESS_REPRINT_BATCH_SERVICE = "KOHLSProcessBatchPrintService";
	public static final String API_KOHLS_BATCH_PRINT_SEARCH_SERVICE = "KohlsgetBatchListService";
	public static final String API_KOHLS_PROCESS_BATCH_PRINT_SERVICE = "KOHLSProcessBatchPrintService";
	public static final String API_KOHLS_PROCESS_BATCH_PRINT_UPDATE_SERVICE = "KohlsUpdateBatchPrintService";
	// End Drop2 changes for Batch Print
	//pawan - Start - Drop2 
	public static final String API_KOHLS_GET_SHIPMENTS_AWAIT_PICK_LIST_PRINT = "KOHLSGetShipmentListForPickSlipPrintService";
	//pawan - End - Drop2 
//	Avinash - Drop2 - Reprint Label Functionality - start
	public static final String API_KOHLS_GET_SHIPMENT_CONTAINER_DETAILS_SERVICE = "KOHLSGetShipmentContainerDetailsService";
	public static final String API_KOHLS_INVOKE_PROSHIPPING_WEB_SERVICE_REPRINT = "KohlsReprintLabelSyncWebService";
	public static final String API_KOHLS_INVOKE_PROSHIPPING_WEB_SERVICE_VOID = "KohlsVoidLabelSyncWebService";
	public static final String API_KOHLS_INVOKE_PROSHIPPING_WEB_SERVICE_CLOSE = "KohlsShipSyncWebService";
	public static final String API_KOHLS_GET_CONTAINER_DETAILS_FOR_CLOSE_CONTAINER = "KOHLSGetShipmentContainerDetailsServiceForClose";
	public static final String API_KOHLS_GET_CONTAINER_DETAILS_FOR_VOID_CONTAINER = "KOHLSGetShipmentContainerDetailsServiceForVoid";
	public static final String API_KOHLS_CHANGE_SHIPMENT_CONTAINER_FOR_CLOSE_CONTAINER = "changeShipmentContainerForCloseContainer";
	public static final String API_KOHLS_CHANGE_SHIPMENT_CONTAINER_FOR_VOID_CONTAINER = "changeShipmentContainerForVoidLabel";
	public static final String API_GET_COMMON_CODE_LIST_TARE_APPLY = "getCommonCodeListForTARE";
	public static final String API_GET_COMMON_CODE_LIST_TARE_VALUE = "getCommonCodeListForTAREValue";
	public static final String API_GET_COMMON_CODE_LIST = "getCommonCodeList";
	public static final String API_CHANGE_SHIPMENT_CONTAINER_FOR_SAVING_REASON_CODE = "changeShipmentContainerForSavingReasonCode";
	public static final String API_GET_COMMON_CODE_LIST_PRINT_LABEL = "getCommonCodeListForPrintLabel";
//	Avinash - Drop2 - Reprint Label Functionality - end	
//Bhaskar - Drop2	- Change shipment address- Start
	public static final String API_KOHLS_CHANGE_SHIPMENT = "changeShipment";
	public static final String API_KOHLS_GENERATE_AND_STAMP_BATCH_NO_AT_SHIMENT_LINE = "KOHLSGenerateBatchNoStampAtShipmentLineService";
	public static final String API_KOHLS_UNPACK_SHIPMENT = "unpackShipment";
	public static final String KOHLS_UNPACK_SHIPMENT_SERVICE = "KOHLSUnpackShipmentService";
	
//	Bhaskar - Drop2	- Change shipment address -End
//Tulasi
	
	public static final String API_STORE_PICK_SLIP = "KOHLSStorePickSlipSyncService";
	public static final String API_GET_SHIPMENT_DETAILS = "KOHLSGetShipmentDetailsSyncService";
	public static final String API_PICK_SLIP_PRINT_FOR_MULTI= "KOHLSPickSlipPrintForMultipleShipments";
	public static final String API_KOHLS_GET_SHIPMENT_DETAILS_ADDRESS_CHANGE="getShipmentDetailsForChangeAddress";
	
	//Pawan - Change Shipment Status post pick list print
	public static final String  API_CHANGE_SHIPMENT_STATUS_STORE_PICK_SLIP_PRINTED="changeShipmentStatusPostStorePickListPrint";
	
	public static final String  API_KOHLS_CHANGESHIPMENT_FOR_BULK= "KOHLSChangeShipmentForBulkPrint";
	public static final String API_KOHLS_CHANGE_SHIPMENT_FOR_MULTI="KOHLSChangeShipmentForPickSlip";
	
}
