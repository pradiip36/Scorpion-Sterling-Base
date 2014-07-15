package com.kohls.common.util;
/**
 * 
 * Kohls Constant File
 * 
 */
public class KohlsConstant {		 
	public static final String KOHLS_OrganizationCode = "KOHLS.COM";
	public static final String KOHLS_ENTERPRISE_CODE = "KOHLS.COM";
	public static final String SO_ENTERPRISE_CODE = "KOHLS.COM";
	public static final String SO_DOCUMENT_TYPE = "0001";
	public static final String PO_DOCUMENT_TYPE = "0005";
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String ACTION_CREATE = "CREATE";
	public static final String ACTION_MODIFY = "MODIFY";
	public static final String ACTION_CANCEL = "CANCEL";
	public static final String A_SUB_LINE_NO = "1";
	public static final String REASON_CODE_NO_INV = "NO_INV";
	public static final String REASON_TEXT_NO_INV = "No inventory";
	public static final String PRODUCT_LINE_SA = "SA";
	public static final String PRODUCT_LINE_GW = "GW";
	public static final String PRODUCT_LINE_ST = "ST";
	public static final String PRODUCT_LINE_VGC = "VGC";
	public static final String PRODUCT_CLASS_GOOD = "Good";
	public static final String UNIT_OF_MEASURE = "EACH";
	public static final String ORDER_RELEASE_KEY = "OrderReleaseKey";
	public static final int PICKTICKET_LEN = 10;
	public static final String SHIP_ALONE_QTY = "1";
	public static final String TRAN_ID_SEND_RELEASE_TO_WMOS = "KOHLS_SEND_RELEASE.0001.ex";
	public static final String TRAN_ID_SA_SEND_RELEASE_TO_WMOS = "KOHLS_SA_SEND_RELEASE.0001.ex";
	public static final String TRAN_ID_SCHEDULE_ORDER = "SCHEDULE.0001";
	public static final String TRAN_ID_RELEASE_ORDER = "RELEASE.0001";
	public static final String TRAN_ID_CHAINED_ORDER_CREATE = "CHAINED_ORDER_CREATE";
	public static final String TRAN_ID_KOHLS_CLOSE_ORDER = "KOHLS_CLOSE_ORDER.0001.ex";
	public static final String STATUS_SEND_TO_WMOS = "3200.03";
	public static final String STATUS_RELEASED = "3200";
	public static final String STATUS_MSG_SEND_TO_WMOS = "Sent To WMoS";
	public static final String STATUS_MSG_SEND_SHIP_ALONE_TO_WMOS = "Ship Alone Sent To WMoS";
	public static final String SUPPLY_TYPE = "ONHAND";
	public static final String INTEGRATION_AGENT_SERVER = "IntegrationAgentServer";
	public static final String INV_ADJ_SERVER_CODE_TYPE = "INV_ADJ_SERVERS";
	public static final String EFC1 = "873";
	public static final String EFC2 = "809";
	public static final String EFC4 = "829";
	public static final String SUSPEND = "Suspend";
	public static final String RESUME = "Resume";
	public static final String SHIPMENT_CREATED_STATUS = "1100";
	public static final String HOLD_CREATED_STATUS = "1100";
	public static final String HOLD_REMOVED_STATUS = "1300";
	public static final String ORDER_LINE_SOUR_CNTRL_REASON = "Source Cntrl";
	public static final String ITEM_ORGANIZATION_CODE = "DEFAULT";
	public static final String VIRTUAL_GIFT_CARD = "VGC";
	public static final String PLASTIC_GIFT_CARD = "GC";
	public static final String PLASTIC_GIFT_CARD_LINE_TYPE = "PGC";
	public static final String BLANK = "";
	public static final String GIFT_WRAP = "GW";
	public static final String GIFT_WRAP_QUANTITY = "1";
	public static final String EFC1_SERVER = "InvAdj873IntegServer";
	public static final String EFC2_SERVER = "InvAdj809IntegServer";
	public static final String RG_HAZARDOUS = "Hazardous";
	public static final String RG_POAPO = "POAPO_STANDARD";
	public static final String RG_PRIORITY = "Priority";
	public static final String RG_AK_HI = "AK_HI_STANDARD";
	public static final String RG_STANDARD = "STANDARD";
	public static final String CARRIER_SERVICE_CODE_APO = "APO";
	public static final String CARRIER_SERVICE_CODE_FPO = "FPO";
	public static final String CARRIER_SERVICE_CODE_PRIORITY = "Priority";
	public static final String ADDRESS_LINE_PO = "PO";
	public static final String STATE_AK = "AK";
	public static final String STATE_HI = "HI";
	public static final String EXTN_PICK_TICKET_ERROR = "ExtnPickTicketNo is missing";
	public static final String ORDER_NO_ERROR = "OrderNo is missing";
	public static final String ELEM_ORDER = "Order";
	public static final String ATTR_DOC_TYPE = "DocumentType";
	public static final String ATTR_ENTERPRISE_CODE = "EnterpriseCode";
	public static final String ATTR_ORD_HDR_KEY = "OrderHeaderKey";
	public static final String ATTR_ORDER_NO = "OrderNo";
	public static final String ATTR_MIN_ORD_STATUS = "MinOrderStatus";
	public static final String ELEM_ORDER_LINES = "OrderLines";
	public static final String ELEM_ORDER_LINE = "OrderLine";
	public static final String ATTR_TOT_NO_RECORDS = "TotalNumberOfRecords";
	public static final String ATTR_INVOICED_QUANT = "InvoicedQuantity";
	public static final String ATTR_ORDER_LINE_KEY = "OrderLineKey";
	public static final String ATTR_ORDERED_QTY = "OrderedQty";
	public static final String ATTR_ORIG_ORDERED_QTY = "OriginalOrderedQty";
	public static final String ATTR_PRIME_LINE_NO = "PrimeLineNo";
	public static final String ATTR_RCVD_QTY = "ReceivedQty";
	public static final String ATTR_SHIPPED_QTY = "ShippedQuantity";
	public static final String ATTR_SUB_LINE_NO = "SubLineNo";
	public static final String ELEM_ORD_STATUSES = "OrderStatuses";
	public static final String ELEM_ORDER_STATUS = "OrderStatus";
	public static final String ATTR_RECV_NODE = "ReceivingNode";
	public static final String ATTR_SHIP_NODE = "ShipNode";
	public static final String ATTR_STATUS = "Status";
	public static final String ATTR_STATUS_QTY = "StatusQty";
	public static final String ATTR_TOT_QTY = "TotalQuantity";
	public static final String ELEM_LINE_CHARGES = "LineCharges";
	public static final String ELEM_LINE_CHARGE = "LineCharge";
	public static final String ATTR_CHARGE_AMNT = "ChargeAmount";
	public static final String ATTR_RMNG_CHARGE_AMNT = "RemainingChargeAmount";
	public static final String ATTR_CHARGE_NAME = "ChargeName";
	public static final String ATTR_CHARGE_CATEGORY = "ChargeCategory";
	public static final String ELEM_ITEM = "Item";
	public static final String ELEM_LINE_TAXES = "LineTaxes";
	public static final String ELEM_LINE_TAX = "LineTax";
	public static final String ATTR_TAX_NAME = "TaxName";
	public static final String ATTR_RMNG_TAX = "RemainingTax";
	public static final String ATTR_TAX = "Tax";
	public static final String ATTR_TAX_PERCENT = "TaxPercentage";
	// Gift Charge related values
	public static final String GiftChargeCategory = "GiftWrap";
	public static final String GiftChargeName = "GiftWrapCharge";
	public static final String GiftTaxCategory = "GiftWrapTax";
	public static final String ShippingChargeCategory = "Shipping";
	public static final String SHIPPING_TAX_CHARGE_CATEGORY = "ShippingTax";
	public static final String ChargeNameSurcharge = "Surcharge";
	// APIs
	public static final String API_GET_ORDER_INVOICE_LIST = "getOrderInvoiceList";
	public static final String API_GET_ORDER_LIST = "getOrderList";
	public static final String API_GET_ITEM_LIST = "getItemList";
	public static final String API_GET_SHIPMENT_LIST = "getShipmentListForOrder";
	public static final String API_GET_SHIPMENT_LINE_LIST = "getShipmentLineList";
	public static final String API_GET_ORDER_RELEASE_DETAILS = "getOrderReleaseDetails";
	public static final String API_GET_SHIPMENT_DETAILS = "getShipmentDetails";
	public static final String API_GET_ORDER_DETAILS = "getOrderDetails";
	public static final String API_GET_ORDER_LINE_DETAILS = "getOrderLineDetails";
	public static final String API_GET_COMMON_CODE_LIST = "getCommonCodeList";
	public static final String API_GET_ORDER_LINE_LIST = "getOrderLineList";
	public static final String API_GET_ITEM_DETAILS = "getItemDetails";
	public static final String API_actual_GET_SHIPMENT_LIST = "getShipmentList";
	public static final String API_GET_DISTRIBUTION_LIST = "getDistributionList";
	public static final String API_GET_INVENTORY_SNAP_SHOT = "getInventorySnapShot";
	// Added SIM Drop 3
	public static final String A_PLACED_QUANTITY = "PlacedQuantity";
	public static final String A_SELLER_ORGANIZATION_CODE = "SellerOrganizationCode";
	public static final String V_AWAITING_PICKLIST_PRINT = "1100.025";
	public static final String A_TRANSACTION_ID = "TransactionId";
	public static final String V_TRANSACTION_ID = "PICK_LIST_PRINT.0001.ex";
	public static final String E_PRINT_PROCESS_SHIPMENT = "PrintProcessShipment";
	// Service
	public static final String SERVICE_KOHLS_SEND_RELEASE_TO_WMOS = "KohlsSendReleaseOrderToWMoSSyncService";
	public static final String SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM = "KohlsSendReleaseStatusToEcommSyncService";
	public static final String SERVICE_KOHLS_SEND_SHIP_ALONE_RELEASE_TO_WMOS = "KohlsSendShipAloneOrderToWMoSSyncService";
	public static final String SERVICE_KOHLS_SEND_SHIP_ALONE_STATUS_TO_ECOMM = "KohlsSendShipAloneStatusToEcommSyncService";
	public static final String SERVICE_KOHLS_SHIP_CONFIRM_TO_ECOMM = "KohlsShipConfirmToEcommDropService";
	public static final String SERVICE_KOHLS_GET_INV_SYNC_TIME = "KohlsGetInvSyncTime";
	public static final String SERVICE_KOHLS_CREATE_INV_SYNC_TIME = "KohlsCreateInvSyncTime";
	public static final String SERVICE_KOHLS_CHANGE_INV_SYNC_TIME = "KohlsChangeInvSyncTime";
	public static final String SERVICE_INV_ADJ_GET_TIME = "InvAdjGetTimeSubService";
	public static final String SERVICE_SHIP_CONFIRM_SYNC_SERV = "ShipConfirmationSyncService";
	public static final String SERVICE_ALT_SHIP_CONFIRM_SYNC_SERV = "AltrntShipConfirmEcommSyncService";
	public static final String SERVICE_KOHLS_GET_INV_SNAP_SHOT = "KohlsGetInvSnapShotService";
	public static final String SERVICE_KOHLS_SEND_REGION_SOURCING_RULE = "KohlsSourcingRuleDetailsService";
	public static final String SERVICE_KOHLS_PAYMENT_REVERSE_AUTH_FAILURE_ALERT = "KohlsReverseAuthorizationFailureAlert";
	public static final String SERVICE_KOHLS_PAYMENT_REVERSE_AUTH_TIME_OUT_ALERT = "KohlsReverseAuthorizationTimeOutAlert";
	public static final String SERVICE_KOHLS_PAYMENT_RE_AUTH_FAILURE_ALERT = "KohlsReAuthorizationFailureAlert";
	public static final String SERVICE_KOHLS_PAYMENT_RE_AUTH_TIME_OUT_ALERT = "KohlsReAuthorizationTimeOutAlert";
	public static final String SERVICE_KOHLS_STORED_VALUE_CARD_REFUND_SERVICE = "KohlsStoredValueCardRefundService";
	public static final String SERVICE_KOHLS_CANCEL_MSG_TO_ECOMM = "CancelOrderSyncService";
	public static final String SERVICE_RAISE_KOHLS_CASH_EARNED_EXCEPTION = "RaiseKohlsCashEarnedExceptionAlert";
	//For Drop2 Batch print Begin
	public static final String SERVICE_KOHLS_SORT_SHIPMENT_BY_KEY="KohlsSortShipmentByKey";
	public static final String SERVICE_KOHLS_EXTRACT_SELECTED_SHIPMENTS="KohlsExtractSelectedShipments";
	public static final String SERVICE_KOHLS_MAKE_GIV_STORE_LOCATION_INPUT ="KohlsMakeGIVStoreLocationInput";
	public static final String SERVICE_KOHLS_GIV_LOCATION_SIMULATOR="KohlsGIVLocationSimulator";
	public static final String SERVICE_KOHLS_GIV_LOCATION_INVENROTY_SUPPLY_WEBSERVICE="KohlsGIVLocationInventorySupplyWebservice";
	public static final String SERVICE_KOHLS_ADD_KEYS_TO_LINELIST ="KohlsAddKeysToLineList";
	public static final String SERVICE_KOHLS_DETERMINE_ITEM_LOCATION ="KohlsDetermineItemLocation";
	public static final String SERVICE_KOHLS_RECREATE_SHIPMENT ="KohlsRecreateShipmentList";
	public static final String SERVICE_KOHLS_GROUP_AND_TOTAL="KohlsGroupAndTotal";
	public static final String SERVICE_KOHLS_DATA_FOR_PACK_SLIP_FORMAT="KohlsConvertToPackSlipFormat";
	
	
	// SIM Pilot-drop3
	public static final String SERVICE_KOHLS_JASPER_MULTI_SERVICE = "KOHLSJasperMultiPrintService";
	public static final String SERVICE_KOHLS_CHANGE_STATUS_SINGLE_PRINT_SERVICE = "KOHLSChangeStatusSinglePrintService";
	public static final String SERVICE_JASPER_SINGLE_PRINT_SERVICE = "KOHLSPickSlipPrintService";
	public static final String SERVICE_JASPER_MULTI_PRINT_SERVICE = "KOHLSPickSlipPrintService";
	public static final String LINE_TYPE_PGC = "PGC";
	public static final String PRODUCT_LINE_BK = "BK";
	public static final String SHIP_VIA_PP = "PP";
	public static final String SHIP_VIA_PM = "PM";
	public static final String SHIP_VIA_PMDC = "PMDC";
	public static final String CARTON_TYPE_ENV = "ENV";
	public static final String CARTON_TYPE_BRK = "BRK";
	public static final String CARTON_TYPE_WRP = "WRP";
	public static final String WRAP_SINGLE_TO_S = "S";
	public static final String WRAP_SINGLE_TO_T = "T";
	// XPATH
	public static final String XPATH_ORDER_EXTN = "/Order/Extn";
	// NetcoolLog
	public static final String NET_LOG_APPENDER = "NetcoolLogger.";
	public static final String LOW = "Low";
	public static final String MEDIUM = "Medium";
	public static final String HIGH = "High";
	// Payment
	public static final String PAYMENT_STATUS_AUTH = "AUTHORIZED";
	public static final String PAYMENT_STATUS_NOT_APPLICABLE = "NOT_APPLICABLE";
	public static final String PAYMENT_STATUS_PAID = "PAID";
	public static final String CHARGE_TYPE_AUTH = "AUTHORIZATION";
	public static final String CHARGE_TYPE_CHARGE = "CHARGE";
	public static final String PAYMENT_CREDIT = "Credit";
	public static final String PAYMENT_RETURN_CODE_0 = "0";
	public static final String PAYMENT_RETURN_CODE_1 = "1";
	public static final String PAYMENT_RETURN_CODE_2 = "2";
	public static final String PAYMENT_RETURN_CODE_3 = "3";
	public static final String PAYMENT_RETURN_CODE_5 = "5";
	public static final String PAYMENT_RETURN_CODE_6 = "6";
	public static final String PAYMENT_RETURN_CODE_8 = "8";
	public static final String PAYMENT_RETURN_CODE_10 = "10";
	public static final String PAYMENT_RETURN_CODE_14 = "14";
	public static final String PAYMENT_RETURN_MSG_SUCCESSFUL = "SUCCESSFUL";
	public static final String PAYMENT_RETURN_MSG_DECLINED = "DECLINED";
	public static final String PAYMENT_RETURN_MSG_TIMEOUT = "TIMEOUT";
	public static final String RTRN_CODE_APRVD = "RTRN_CODE_APRVD";
	public static final String RTRN_CODE_DECL = "RTRN_CODE_DECL";
	public static final String RTRN_CODE_TIME_OUT = "RTRN_CODE_TO";
	public static final String RESP_101_MSG_FLD = "101_RES_MSG_FLD";
	public static final String AUTHORIZATION_EXPIRATION_DATE = "authorizationExpirationDate";
	public static final String AUTHORIZATION_AMOUNT = "authorizationAmount";
	public static final String AUTHORIZATION_ID = "authorizationId";
	public static final String AUTH_RETURN_CODE = "authReturnCode";
	public static final String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
	public static final String CODE_TYPE_RETRY_INTERVAL = "RETRY_INTERVAL";
	public static final String PAYMENT_TYPE_CREDIT_CARD = "Credit Card";
	public static final String PAYMENT_TYPE_CREDIT_AUTH = "credit";
	public static final String PAYMENT_TYPE_AUTH_TRAN_TYPE = "Credit";
	public static final String PAYMENT_TYPE_3PL_GIFT_CARD = "3PL Gift Card";
	public static final String AUTH_EXP_DAYS = "AUTH_EXP_DAYS";
	public static final String AUTH_ID_DUMMY = "000000";
	public static final String CREDIT_CARD_AMEX = "AMEX";
	public static final String PAYMENT_RULE_NO_AUTH = "KOHLS_NO_AUTH";
	// Receipt ID Related fields
	public static final int REGISTER_LEN = 2;
	public static final int TRANSACTION_LEN = 4;
	public static final int SHIPNODE_LEN = 4;
	public static final int PMLINENO_LEN = 3;
	public static final String RCPT_HDR_LINE_NUM = "000";
	// SSO constants
	public static final String SSO_HEADER_USERNAME = "sso.header.username";
	public static final String CUSTOMER_OVERRIDES_PROPERTIES = "customer_overrides.properties";
	public static final String SSO_LOGGER = "SSOLogger.";
	public static final int REG_TRANS_LEN = 0;
	public static final Object A_UPC_01 = "Upc01";
	// Reprice constants
	public static final String ORDER_SHIPPED = "3700";
	public static final String SHIPMENT_SHIPPED = "1400";
	public static final String INVOICE_HOLD_INDICATOR = "InvoiceHoldIndicator";
	public static final String HOLD_INVOICE = "Hold Invoice";
	public static final String FINAL_SHIPPED = "1400.001";
	public static final String FINAL_SHIPMENT_0001_EX = "Final Shipment.0001.ex";
	public static final String SHIPMENT_CREATED_FOR_NON_SA = "1100.02";
	public static final String CHANGE_SHIPMENT_STATUS_TRAN = "CHANGE_SHMNT_STATUS.0001.ex";
	public static final String PO_INCLUDED_IN_SHIPMENT = "3350.0001";
	public static final String FINAL_SHIPMENT_0005_EX = "Final Shipment.0005.ex";
	public static final String SO_CANCEL = "Cancel";
	public static final int COSA_HT_REPORT_TIME = 3;
	public static final String KOHLS_CASH = "KOHLS_CASH";
	public static final String CREDIT_CARD_TYPE_KOHLS_CASH = "KOHLS CASH";
	public static final String DiscountChargeCategory = "Discount";
	public static final String SELECT_METHOD_WAIT = "WAIT";
	public static final String DIST_GROUP = "DistributionGroup";
	public static final String ALL_ITEM_ID = "ALL";
	public static final String DSV_PRIORITY = "4.00";
	public static final String INV_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String DELETE_DIST_GROUP = "Delete";
	public static final String EQUALS = "=";
	public static final String DELIMITER = ";";
	public static final String TRAN_ID_ORDER_INVOICE_0001_EX = "KOHLS_ORDER_INVOICE.0001.ex";
	public static final String TRAN_ID_INCLUDE_INVOICE_0001_EX = "KOHLS_INCLUDE_INVOICE.0001.ex";
	public static final String AWAITING_INVOICE_CREATION = "3700.0003";
	public static final String AWAITING_ORDER_CLOSURE = "3700.0001";
	public static final String INVOICED = "3700.0004";
	public static final String MODIFY = "Modify";
	public static final String DELETE = "Delete";
	public static final String CREDIT_CARD = "Credit Card";
	public static final String GIFT_CARD = "Gift Card";
	public static final String A_SUSPEND_BOTH_PAYMENT_REFUND = "B";
	public static final String PREENC = "Preenc";
	public static final String DSV_NODE_RECEIPT_ID = "873";
	public static final String VGC_SHIP_NODE = "SVC";
	public static final String AUTHORIZATION = "AUTHORIZATION";
	public static final String CASH_ACTIVATION_HOLD = "CashActivationHold";
	public static final String CASH_ACTIVATION_REASON = "KCE Activation Hold";
	public static final String CODE_SHIP_NODES = "SHIP_NODES";
	// Hard Totals
	public static final String HD_DATE_FORMAT = "yyyy-MM-dd";
	public static final String HARD_TOTALS_KEY = "HardTotalsKey";
	public static final String SERVICE_GET_HARD_TOTALS_LIST = "GetHardTotalsListService";
	public static final String HD_GT = "GT";
	public static final String SERVICE_DROP_HARD_TOTALS = "DropHardTotalsService";
	public static final String SERVICE_GET_COSA_REPORT_RUN_LST = "GetCosaReportRunListService";
	public static final String SERVICE_CREATE_COSA_AGENT_RUN_REC = "CreateCosaAgentRunRecordService";
	public static final String SALES_TRANS_CNT_VAL = "1";
	public static final String TRANS_WITH_TAXES_VAL = "1";
	public static final String KMRC = "KMRC";
	public static final String KOHLS_GIFT_CARD = "KL_GIFT_CARD";
	public static final String CREDIT_CARD_VISA = "VISA";
	public static final String CREDIT_CARD_DISCOVER = "DISC";
	public static final String KOHLS_CHARGE_CARD = "KL_CHRG_CARD";
	public static final String CREDIT_CARD_MSTR_CARD = "MSTR_CARD";
	public static final String GIFT_CARD_SKU_ID = "GIFTCARD_SKU_ID";
	public static final String IS_PROCESS_AVAILABILITY_ACTIVE = "PRO_AVAIL_ACTIV";
	public static final String DSV_INV_ADJ_PROCESS_SUB_SERVICE = "DSVInvAdjProcessSubService";
	public static final String CREATE_HARD_TOTALS_SERVICE = "CreateHardTotalsService";
	public static final String HARD_TOTAL_HOUR = "HARD_TOTAL_HOUR";
	public static final String HARD_TOTAL_RECORDS = "HARD_TOTAL_REC";
	public static final String HARD_TOTAL_DAY = "HARD_TOTAL_DAY";
	// RelC Constants
	public static final String INS_TYPE_BOGO = "BOGO";
	public static final String TAX_NAME_SALES_TAX = "SALES_TAX";
	public static final String AWAIT_PAY_INFO = "AWAIT_PAY_INFO";
	public static final String OOB_HOLD = "OMSOutOfBalanceHold";
	public static final String ECOMM_HOLD = "EComOutOfBalanceHold";
	public static final String ECOMM_HOLD_REASON = "EComm Hold";
	public static final String ECOMM_HOLD_RESOLVE_REASON = "Resolving EComm Hold";
	public static final String SERVICE_KOHLS_CASH_TABLE_LIST = "KohlsGetCashTableList";
	// Start --- Added for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
	public static final String SHIP_VIA_VALUES = "SHIP_VIA_VALUES";
	public static final String IS_HAZ_IS_MILITARY = "IS_HAZ_IS_MILITARY";
	// End --- Added for PMR 54606,379,000 -- OASIS_SUPPORT 14/03/2012
	
//Start -- added for PMR :05616,379,000 default carton type as "BOX"
	
	public static final String CARTON_TYPE_BOX = "BOX";
		
   //End -- added for PMR :05616,379,000 default carton type as "BOX"
	// Start -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //
	public static final String SERVICE_KOHLS_POST_AUDIT_MSG = "KohlsPostAuditMsgSyncService";
	public static final String SUPPLY_CORRECTION="SupplyCorrection";
	public static final String AVAILABILITY_CORRECTION="AvailabilityCorrection";
	// End -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //
	// offshore drop2
	public static final String API_GET_SHIPMENT_LIST_FOR_PACK_SHIPMENT_KEY_TEMPLATE_PATH = "global/template/api/getShipmentListForPackShipmentKey_output.xml";
	public static final String API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH = "global/template/api/getShipmentDetailsForPackShipment_output.xml";
	public static final String A_SHIPMENT_LINE_KEY = "ShipmentLineKey";
	public static final String A_QUANTITY = "Quantity";
	// public static final String API_GET_SHIPMENT_DETAILS =
	// "getShipmentDetails";
	// public static final String API_GET_SHIPMENT_LIST = "getShipmentList";
	// public static final String API_GET_ITEM_LIST = "getItemList";
	public static final String API_GET_ITEM_LIST_FOR_CONTAINER_TYPE_TEMPLATE_PATH = "global/template/api/getItemListForContainerType_output";
	public static final String A_ENTERPRISE_CODE = "EnterpriseCode";
	public static final String A_SHIPMENT_KEY = "ShipmentKey";
	public static final String A_ORGANIZATION_CODE = "OrganizationCode";
	public static final String A_CALLING_ORGANIZATION_CODE = "CallingOrganizationCode";
	public static final String A_ITEM_GROUP_CODE = "ItemGroupCode";
	public static final String V_PROD = "PROD";
	public static final String A_IS_SHIPPING_CONTAINER = "IsShippingCntr";
	public static final String FLAG_Y = "Y";
	public static final String E_ITEM = "Item";
	public static final String E_SHIPMENT = "Shipment";
	public static final String A_CONTAINER_NO = "ContainerNo";
	public static final String E_CONTAINER = "Container";
	public static final String A_IS_PACK_PROCESS_COMPLETE = "IsPackProcessComplete";
	public static final String E_NEW_CONTAINER = "NewContainer";
	public static final String E_CONTAINERS = "Containers";
	public static final String API_CHANGE_SHIPMENT_CONTAINER = "changeShipmentContainer";
	public static final String API_CHANGE_SHIPMENT_CONTAINER_TEMPLATE_PATH = "global/template/api/changeShipmentContainer_output";
	public static final String A_SHIPMENT_CONTAINERIZED_FLAG = "ShipmentContainerizedFlag";
	public static final Object V_SHIPMENT_CONTAINERIZED = "03";
	public static final String E_ADDINFO = "AddInfo";
	public static final String A_DcmIntegrationRealTime = "DcmIntegrationRealTime";
	public static final String API_CHANGE_SHIPMENT = "changeShipment";
	public static final String API_CHANGE_SHIPMENT_STATUS = "changeShipmentStatus";
	public static final String API_PACK_SHIPMENT = "packShipment";
	public static final String API_PACK_SHIPMENT_TEMPLATE_PATH = "global/template/api/changeShipmentToAddContainer.xml";
	public static final String A_PACK_COMPLETE_TRANSACTION_ID = "PACK_SHIPMENT_COMPLETE";
	public static final String A_PACK_COMPLETE_BASE_DROP_STATUS = "1300";
	public static final String A_Shipment_Pack_In_Progress = "1100.04";
	public static final String A_SHIPMENT_PACK_TRANSACTION_ID = "SHIPMENT_PACK.0001.ex";
	public static final String A_CONTAINERS = "Containers";
	public static final String A_CONTAINER = "Container";
	public static final String A_CONTAINERSCM = "ContainerScm";
	public static final String A_LATEST_CONTAINER_SCM = "LatestContainerScm";
	public static final String V_SHIPMENT_CONTAINERIZED_01 = "01";
	public static final String V_SHIPMENT_CONTAINERIZED_02 = "02";
	public static final String V_SHIPMENT_CONTAINERIZED_03 = "03";
	public static final String V_SHIPMENT_PICK_LIST_PRINTED_STATUS = "1100.03";
	public static final String A_PICK_LIST_PRINT_TRANSACTION_ID = "PICK_LIST_PRINT.0001.ex";
	public static final String A_ACTION = "Action";
	public static final String V_DELETE = "Delete";
	public static final String API_UN_PACK_SHIPMENT = "unpackShipment";
	public static final String A_ACTION_TAKEN = "ActionTaken";
	public static final String V_ACTION_TAKEN_UNPACK = "unpack";
	public static final String V_ACTION_TAKEN_PACK = "pack";
	public static final String A_SHIPMENT_NO = "ShipmentNo";
	public static final String A_SHIP_NODE = "ShipNode";
	public static final String API_GET_SHIPMENT_LIST_FOR_UPDATE_CONTAINER_TRACKING = "global/template/api/getShipmentListForUpdateContainerTracking.xml";
	public static final String A_IS_NEW_CONTAINER = "IsNewContainer";
	public static final String SQL_STATEMENT_FOR_SEQ_YFS_CONTAINER_NO = "select SEQ_YFS_CONTAINER_NO.nextval as ContainerNo from dual";
	public static final String A_STATUS = "Status";
	public static final String STATUS_SHIPMENT_PICK_LIST_PRINTED = "1100.03";
	public static final String STATUS_SHIPMENT_PACK_IN_PROGRESS = "1100.04";
	public static final Integer V_ZERO = 0;
	public static final String A_ITEM_ID = "ItemID";
	public static final String A_BLANK = "";
	public static final String A_TOTAL_SHIPMENTS = "TotalShipments";
	public static final String A_SHIPMENT_TYPE = "ShipmentType";
	public static final String E_SHIPMENT_LINE = "ShipmentLine";
	public static final String E_SHIPMENT_LINES = "ShipmentLines";
	public static final String A_MAXIMUM_RECORDS = "MaximumRecords";
	public static final String A_PRINTER_ID = "PrinterID";
	public static final String E_ORDER_INVOICE = "OrderInvoice";
	public static final String E_PAYMENT_METHOD = "PaymentMethod";
	public static final String E_SHIPMENTS = "Shipments";
	public static final String API_UNPACK_SHIPMENT_TEMPLATE_PATH = "global/template/api/changeShipmentToUnPackContainer.xml";
	public static final String V_PACK = "Pack";
	public static final String V_UNPACK = "unpack";
	public static final String E_BASE_DROP_STATUS = "BaseDropStatus";
	public static final String V_BASE_DROP_STATUS = "1100.03";
	//
	public static final String A_NUMBER = "Number";
	//
	public static final String V_PREFIX = "KOHLS";
	public static final String V_MIDDLE = "SinglePrint";
	public static final String V_SUFFIX = "Service";
	public static final String V_AWAITING_PICKLIST = "Awaiting PickList";
	public static final String V_PRINT_LIST_PRINTED = "Printlist Printed";
	public static final String A_IS_PRINTLIST_PRINTED = "Is PrintList Printed";
	public static final String V_Y = "Y";
	public static final String SERVICE_JASPER_SERVICE1 = "Jasperservice1";
	public static final String A_IS_PICK_LIST_PRINTED = "IsPickListPrinted";
	public static final String A_SHIPMENT_LINES = "ShipmentLines";
	public static final String A_SHIPMENT_LINE = "ShipmentLine";
	public static final String A_UNIT_OF_MEASURE = "UnitOfMeasure";
	public static final String E_ITEM_NODE_DEFN = "ItemNodeDefn";
	public static final String A_ITEM_NODE_DEFN_KEY = "ItemNodeDefnKey";
	public static final String A_NODE = "Node";
	public static final String A_EXTN = "Extn";
	public static final String API_GET_ITEM_NODE_DEFN_DETAILS = "getItemNodeDefnDetails";
	public static final String A_ITEM_DESC = "ItemDesc";
	public static final String A_TOTE_ID = "ToteID";
	public static final String V_TOTE_ID = "1";
	public static final String A_PRIMARY_INFORMATION = "PrimaryInformation";
	public static final String A_LINE = "Line";
	public static final String A_LOCATION = "Location";
	public static final String A_EXTN_LOCATION_ID = "ExtnLocationID";
	public static final String A_EXTN_LINE_NO = "ExtnLineNo";
	public static final String E_EXTN = "Extn";
	public static final String A_TOTEID = "ToteId";
	public static final String E_PRINT_PACK_SHIPMENT = "PrintPackShipment";
	public static final String A_TOTAL_CARTS = "TotalCarts";
	public static final String A_TOTES_PER_CART = "TotesPerCart";
	public static final String KOHLS = "KOHLS";
	public static final String SERVICE_KOHLS_CHANGE_STATUS_MULTI_PRINT = "KOHLSChangeStatusMultiPrintService";
	public static final String MULTI_PRINT_SERVICE = "MultiPrintService";
	public static final String V_MIDDLE_PRINT = "PrintPickSlip";
	// added 6/5/12 from Punit Kumar
	public static final String API_GET_ORGANIZATION_LIST = "getOrganizationList";
	public static final String ATTR_SHIPNODESOURCE = "ExtnShipNodeSource";
	public static final String ATTR_RDC = "RDC";
	public static final String ATTR_STORE = "STORE";
	public static final String ATTR_RDC_STORE = "RDC_STORE";
	public static final String ATTR_FULFILLMENT_TYPE = "FulfillmentType";
	public static final String ATTR_GIFT_PRIORITY = "Gift_Priority";
	public static final String ATTR_ASSOCIATION_ACTION = "AssociationAction";
	public static final String ATTR_IS_NODE = "IsNode";
	public static final String ATTR_GIFT = "Gift";
	public static final String ATTR_MULTI = "Multi";
	public static final String ATTR_REGULAR = "Regular";
	public static final String ATTR_SINGLE = "Single";
	// added by puneet on 15th june - start
	public static final String E_SHIP_NODE = "ShipNode";
	public static final String E_MULTI_API = "MultiApi";
	public static final String E_CONTAINER_DETAIL = "ContainerDetail";
	public static final String E_API = "API";
	public static final String A_NAME = "Name";
	public static final String API_MOVE_LOCATION_INVENTORY = "moveLocationInventory";
	public static final String E_INPUT = "Input";
	public static final String E_MOVE_LOCATION_INVENTORY = "MoveLocationInventory";
	public static final String E_SOURCE = "Source";
	public static final String E_DESTINATION = "Destination";
	public static final String E_INVENTORY = "Inventory";
	public static final String E_INVENTORY_ITEM = "InventoryItem";
	public static final String A_PRODUCT_CLASS = "ProductClass";
	public static final String A_LOCATION_ID = "LocationId";
	public static final String A_LOCATION_TYPE = "LocationType";
	public static final String A_LOCATION_CONTEXT = "LocationContext";
	public static final String V_NODE_DEFAULT_LOCATION = "Receiving";
	public static final String A_CASE_ID = "CaseId";
	public static final String A_Node = "Node";
	public static final String O_CONTAINER_ELEM = "sContainerElem";
	public static final String A_CONTAINER_TYPE = "ContainerType";
	public static final String V_CASE = "Case";
	public static final String SERVICE_POST_MESSAGE_CONFIRM_SHIPMENT = "KOHLSPostMessageInQueueForConfirmShipment";
	public static final String API_CONFIRM_CUSTOMER_PICK = "confirmCustomerPick";
	public static final String SERVICE_SOP_PRINT_PICK_TICKET = "KOHLSSopPrintPickTicketService";
	// added by puneet on 15th june - end
	// added by puneet on 16th june-start
	public static final String API_SHIPMENT_DETAIL_PICK_LIST = "global/template/api/getShipmentDetailsForPickList_output.xml";
	// added by puneet on 16th june - end
	// added by puneet on 17th june-start
	public static final String A_RECEIPT_ID = "ReceiptId";
	// added by puneet on 17th june - end
	public static final String A_SKU_QTY = "SkuQty";
	// public static final String A_CATALOG_ORG = "KOHLS.COM";
	public static final String A_CATALOG_ORG = "DEFAULT";
	// added by puneet on 20th june start
	public static final String A_CATALOG_ORG_CODE = "CatalogOrgCode";
	public static final String A_FORM_ID = "FormID";
	
//	 added by puneet on 20th june end
	
	//added by kiran on 21th June
	public static final String V_UPC01 = "Upc01";
	public static final String A_ALIAS_NAME = "AliasName";
	public static final String A_ALIAS_VALUE = "AliasValue";
	public static final String SERVICE_KOHLS_PICK_SLIP_PRINT = "KOHLSPickSlipPrintService";
	public static final String A_ITEM_ALIAS_LIST = "ItemAliasList";
	public static final String A_ITEM_ALIAS = "ItemAlias";
	public static final String SERVICE_PACK_SLIP_PRINT_SERVICE = "KOHLSPackSlipPrintService";
public static final String A_BARCODE_DATA = "BarCodeData";
public static final String A_CONTEXTUAL_INFO = "ContextualInfo";
	public static final String A_ITEM_CONTEXTUAL_INFO = "ItemContextualInfo";
	public static final String A_BAR_CODE = "BarCode";
	public static final String A_TRANSLATIONS = "Translations";
	public static final String A_TRANSLATION = "Translation";
public static final String A_TOTAL_ITEM_LIST = "TotalItemList";
	public static final String A_INVENTORY_UOM = "InventoryUOM";
	//added by puneet 25 june 2012 start
	public static final String V_COMMON_CODE_TYPE_FOR_SHIPNODE_DIR = "SHPNODE_PRT_DIR";
	public static final String E_COMMON_CODE = "CommonCode";
	public static final String A_CODE_TYPE = "CodeType";
	public static final String A_CODE_VALUE = "CodeValue";
	public static final String A_CODE_SHORT_DESC = "CodeShortDescription";
	public static final String V_DATE_TIME_FORMAT = "yyyyMMddHHmmss" ;
	
	//added by puneet 25 june 2012 end
//added by shaila on 26th june
public static final String A_IGNORE_ORDERING = "IgnoreOrdering";
	public static final String V_N = "N";
	public static final String V_MAXIMUM_RECORDS = "5000";
	public static final String E_ORDER_BY = "OrderBy";
	public static final String A_ATTRIBUTE = "Attribute";
	public static final String API_GET_SHIPMENT_DETAILS_PACK_SHIPMENT_TEMPLATE_PATH_1 = "global/template/api/getShipmentDetailsForPackShipment_output.xml";
	public static final String A_AWAITING_PICK_LIST = "1100.025";
	// added by puneet on 30 june 2012- start
	public static final String A_LABEL_PICK_TICKET = "LabelPickTicket";
	public static final String API_GET_SHIPMENT_DETAILS_RE_PRINT_SINGLE_PICK_SHIPMENT_TEMPLATE = "global/template/api/getShipmentDetailsForRePrintSinglePickShipment_output.xml";
	public static final String SERVICE_RE_PRINT_SINGLE_PICK_SHIPMENT = "KOHLSSinglePickRePrintService";
	public static final String API_CHANGE_SHIPMENT_STATUS_RE_PRINT_SINGLE_PICK_SHIPMENT_TEMPLATE = "global/template/api/changeShipmentStatus.xml";
	public static final String E_ORDER_LINE = "OrderLine";
	public static final String V_KL_MAX_SHP_PRINT = "KL_MAX_SHIP_PRINT";
	
	
	// added by puneet on 30 june 2012- end
	
	//added by Kiran on 2 july 2012- Start
	
	//added by Kiran on 2 july 2012- End
	//added by puneet on 4th july 2012 - start
	public static final String API_REPRINT_PICK_SLIP_SERVICE = "KOHLSSopPrintPickTicketService";
	//added by puneet on 4th july 2012 - end
	public static final String A_NODE_TYPE = "NodeType";
	public static final String V_STORE = "STORE";
	
//	added by kiran on 5th july 2012 - start
	
	
	//added by Saravana on 5 July 2012 - Start
	public static final String E_FRONT_PAGE = "FrontPage";
	public static final String A_SEQ_NO = "SeqNo";
	//added by Saravana on 5 July 2012 - End	
	      //added by Saravana on 6 July 2012 - Start
      public static final String E_ITEM_INFO = "ItemInformation";
      //added by Saravana on 6 July 2012 - End
	//added by Puneet on 8 July 2012 - Start
	public static final String A_SCAC = "SCAC";
	public static final String A_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	public static final String A_CARRIER_ACCOUNT_NO = "CarrierAccountNo";
	//added by Puneet on 8 July 2012 - End
	//added by Saravana on 9 July 2012 - Start
	public static final String E_LINE_TAX = "LineTax";
	public static final String A_TAX_SYMBOL = "TaxSymbol";
	public static final String TAX_SYMBOL_T = "T";
	public static final String E_POPULATE_TAXES = "PopulateTaxes";
	public static final String E_POPULATE_TAX = "PopulateTax";
	//added by Saravana on 9 July 2012 - End
	//added by puneet on 26 july 2012 - start
	public static final String V_STATUS_LIST = "KOHLSShipmentStatusList";
	//added by puneet on 26 july 2012 - end
    //added by puneet on 09 Aug 2012 - start
	public static final String A_UPC_BARCODE_PICK_TICKET = "UpcBarcodePrint";
	//added by puneet on 09 Aug 2012 - End
	public static final String E_BACK_PAGE = "BackPage";
	public static final String A_DESCRIPTION = "Description";
	public static final String O_ADD_CONTAINER_OUTPUT = "AddContainerOutDoc";
	public static final String O_PRINT_PACK_SLIP = "PrintPackSlip";
	public static final String E_PRINT_PACK_DOC = "PrintPackDoc";
    
	public static final String A_PRINT_JASPER_REPORT = "PrintJasperReport";
		//Added for Mass Singles Print, Modified by Kiran on 20th Sept
	public static final String E_COMPLEX_QUERY = "ComplexQuery";
	public static final String E_OR = "Or";
	public static final String E_EXP = "Exp";
	public static final String A_VALUE = "Value";
	public static final String SHIP_TYPE_SINGLE_REGULAR = "Single_Regular";
	public static final String SHIP_TYPE_SINGLE_PRIORITY = "Single_Priority";
	public static final String SHIP_TYPE_SINGLE_GIFT = "Single_Gift";
	public static final String DELIMITER_SHIP_HASHMAP = "++";
	public static final String ESC_CHAR_DELIMITER_SHIP_HASHMAP = "\\++";
	public static final String E_PRINT_PACK_SHIPMENTS = "PrintPackShipments";
	
	public static final String A_AVAILABLE_QTY = "AvailableQuantity";
	public static final String STRING_ONE = "1";
	//Add by Puneet on 19th Sept
	public static final String API_KOHLS_PRINT_PACK_SLIP_SERVICE = "KOHLSProcessPrintPackSlipService";
	public static final String A_PRINT_BATCH_NUMBER = "PrintBatchNo";
	public static final String V_KOHLS_RE_PACKSHIP_JASPER = "/KohlsRePackShip.jasper";
	public static final String SERVICE_GET_MASS_PRINT_SINGLES_LIST_SERVICE = "KOHLSGetMassPrintSinglesListService";
     
	 public static final String A_COMMITMENT_CODE = "CommitmentCode";
	 
	 public static final String SQL_STATEMENT_FOR_SEQ_YFS_PRINT_BATCH_ID= "select SEQ_YFS_PRINT_BATCH_ID.nextval as BatchID from dual";
	 public static final String A_BATCH_ID = "BatchID";
	public static final String V_DATE_FORMAT = "yyyyMMdd";
	public static final String A_CART_NUMBER = "CartNo";
	public static final String A_TOTAL_CARTS_AVAILABLE = "TotalCartsAvailable";
	public static final String A_CART_NUMBER_AVAILABLE = "CartNoAvailable";
	public static final String A_PROFILE_ID = "ProfileID";
	public static final String A_EXTN_CART_NUMBER = "ExtnCartNumber";
	public static final String E_ATTRIBUTE = "Attribute";
	public static final String A_DESC = "Desc";
	public static final String A_IS_CONFIRM_SHIPMENT = "IsConfirmShipment";
	
	public static final String API_CHECK_TRACKING_NO_TEMPLATE_PATH = "global/template/api/getShipmentDetailsToCheckTrackingNo.xml";
	public static final String A_CONFIRM_SHIPMENT_STATUS = "1400";
	public static final String SHIP_TYPE_MULTI_REGULAR = "Multi_Regular";
	public static final String SHIP_TYPE_MULTI_PRIORITY = "Multi_Priority";
	public static final String SHIP_TYPE_MULTI_GIFT = "Multi_Gift";
	public static final String A_START_CART_NUMBER = "StartCartNo";
	public static final String A_PRINT_TYPE ="PrintType";
	public static final String V_SINGLE = "SINGLE";	
	public static final String a_CONTAINER_NET_WEIGHT = "ContainerNetWeight";
	public static final String V_SERVICE_TYPE_USG = "USG";
	public static final String A_SERVICE_TYPE = "ServiceType";
	public static String V_SERVICE_TYPE_USL = "USL";
	public static String A_EXTN_IS_PO_BOX = "ExtnIsPOBox";
	
	public static final String A_IS_PRODUCT_FAMILY_SHIPMENT = "isProductFamilyShipment";
	
	public static final String SERVICE_KOHLS_MULTIAPI_INPUT_SORT_BY_ITEMID_XSL = "KOHLSSortMultiApiInputForMoveLocationInventoryByItemID";
	public static final String V_SELLER_ORGANIZATION_CODE = "KOHLS.COM";
	public static final String A_TOTAL_NUMBER_RECORDS = "TotalNumberOfRecords";
	public static final String ATTR_TOTES_PER_CART = "TotesPerCart";
	public static final String E_AVAILABLE_CARTS = "AvailableCarts";
	public static final String A_CARTS_MULTI_REGULAR = "MultiRegular";
	
	public static final String A_CARTS_MULTI_PRIORITY = "MultiPriority";
	
	public static final String A_CARTS_MULTI_GIFT = "MultiGift";
	public static final String A_IS_PICKTICKET_PRINTED = "IsPickTicketPrinted";
	//Add by Kiran 17th Dec
	public static final String E_ITEM_NODE_DEFN_LIST = "ItemNodeDefnList";
	public static final String A_EXTN_PRODUCT_FAMILY = "ExtnProductFamily";
	public static final String API_GET_ITEM_NODE_DEFN_LIST = "getItemNodeDefnList";
	public static final String V_MULTI_PRODUCT_FAMILY = "Multi_Product_Family";
	public static final String A_EXTN_SHIPMENT_FAMILY = "ExtnShipmentFamily";
		//Start -- added for central stock transfer issue.
	public static final String WAREHOUSE_TRAN = "WAREHOUSE_TRAN";
	public static final String STATUS_SEND_RELEASE_TO_WMOS = "3200.01";
	public static final String TRAN_ID_SEND_TO_RELEASE_TO_WMOS = "KOHLS_SEND_TO_RELEASE.0006.ex";
	public static final String TO_DOCUMENT_TYPE = "0006";
	public static final String API_GET_ORDER_RELEASE_LIST = "getOrderReleaseList";
	public static final String TEMLATE_GET_SHIPMENT_LIST_TO_STAMP_BATCH_NO = "global/template/api/getShipmentListToStampBatchNo.xml";
	public static final String A_BATCH_NO = "BatchNo";
	public static final String V_AWAITING_PICK_LIST_PRINT = "1100.025";
	public static final String TEMLATE_GET_SHIPMENT_DETAILS_FOR_SHIPMENT_STATUS = "global/template/api/getShipmentDetailsForShipmentStatusOfBulkPrint.xml";
	public static final String A_SELECT_METHOD = "SelectMethod";
	public static final String V_NO_WAIT = "NO_WAIT";
	public static final String E_BATCH_PRINT = "BatchPrint";
	public static final String A_REPRINT_FLAG = "ReprintFlag";
	public static final String A_NO_OF_SHIPMENTS = "NoOfShipments";
	public static final String V_AWAITING_PRINT = "Awaiting Printing";
	public static final String A_USER = "User";
	public static final String A_BASE_DROP_STATUS = "BaseDropStatus";
	public static final String A_EXTN_BATCH_NO = "ExtnBatchNo";
	public static final String A_QTY_TO_PRINT = "QtyToPrint";
	
	public static final String MAX_NUMBER_OF_CARTS = "200";	
	//End -- added for central stock transfer issue.
	
	//Batch Print Drop 2/3 Begin
	public static final String A_NO_OF_SHIPMENT ="noOfShipment";
	public static final String A_FROM_CART_NO ="FromCartNo";
	public static final String A_TO_CART_NO ="ToCartNo";
	public static final String A_FROM ="From";
	public static final String A_TO ="To";
	public static final String A_PICKSLIP ="Pickslip";
	public static final String A_PRIMARY_SUPPLIER="PrimarySupplier";
	public static final String A_VENDOR="Vendor";
	public static final String A_ITEM_DESCRIPTION="ItemDescription";
	public static final String A_SHORT_DESCRIPTION="ShortDescription";
	public static final String A_DEPT="Department";
	public static final String A_ORGANIZATION="Organization";
	public static final String A_ORG_LIST="OrganizationList";
	public static final String A_ORG_NAME="OrganizationName";
	public static final String A_ORDER_DATE="OrderDate";
	public static final String A_UNIT_PRICE="UnitPrice";
	public static final String A_LINE_PRICE_INFO="LinePriceInfo";
	public static final String A_NOTE="Note";
	public static final String A_REASON_CODE="ReasonCode";
	public static final String A_NOTE_TEXT="NoteText";
	public static final String A_RECEIPTID="ReceiptID";
	public static final String A_STORE="Store";
	public static final String A_TABLE_KEY="TableKey";
	public static final String A_SEQUENCE_KEY="SequenceNo";
	public static final String A_SINGLES_PRINT="SINGLES_PRINT";
	public static final String KOHLS_SINGLE_BATCH_PRINT_SERVICE = "KOHLSSingleBatchPrintService";
	public static final String A_MULTI_PRINT="MULTI_PRINT"; 
	public static final String SERVICE_JASPER_BATCH_MULTI_PRINT_SERVICE = "KOHLSRDCMultiPickSlipPrintService";
	public static final String A_USER_ID="userId";
	public static final String A_PASSWORD="password";
	public static final String END_POINT_USER_GIV_LOC_INV_SUPPLY="END_POINT_USER_GIV_LOC_INV_SUPPLY";
	public static final String END_POINT_PWD_GIV_LOC_INV_SUPPLY="END_POINT_PWD_GIV_LOC_INV_SUPPLY";
	public static final String A_GET_LOCATION_NS3="ns3:getLocationInventorySupplyResponse";
	public static final String A_NS3="xmlns:ns3";
	public static final String A_NS3_VALUE="http://webservices.inventory.giv.kohls.com/";
	public static final String A_NS4="xmlns:ns4";
	public static final String A_NS4_VALUE="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/output";
	public static final String A_INP_INV_SUPPLY="inp:InventorySupply";
	public static final String A_NS4_ITEM="ns4:Item";
	public static final String A_NS4_SUPPLIES="ns4:Supplies";
	public static final String A_NS4_INV_SUPPLY="ns4:InventorySupply";
	public static final String A_SUPPLY_TYPE="SupplyType";
	public static final String A_STOCK_ROOM_EX="STOCK_ROOM.ex";
	public static final String A_SALES_FLOOR_EX="SALES_FLOOR.ex";
	public static final String A_RETURN="return";
	
	// BOPUS GIV namespaces
	public static final String BOPUS_GIV_MESSAGE_PREFIX_PROPERTY="SOAPACTIONURIPREFIX_GIV_VALUE";
	public static final String BOPUS_GIV_INPUT_PREFIX_PROPERTY="PROSHIPDATACONTRACTPREFIX_GIV_VALUE";
	public static final String BOPUS_GIV_ENV_ELEMENT_PROPERTY="SOAPENVIRONMENT_VALUE";
	public static final String BOPUS_GIV_INPUT_ELEMENT_PROPERTY="SOAPINPUT_VALUE";
	public static final String BOPUS_GIV_MESSAGE_ELEMENT_PROPERTY = "SOAPACTIONOPERATIONPARAM_GIV_VALUE";
	public static final String BOPUS_DEFAULT_PRODUCT_CLASS_PROPERTY = "DEFAULT_PRODUCT_CLASS";
	public static final String BOPUS_TRANSLATION_ORG_PREFIX = "TRANSLATION_ORG_";
	public static final String BOPUS_TRANSLATION_ITEM_PREFIX = "TRANSLATION_ITEM_";
	public static final String BOPUS_GIV_RESPONSE_NAMESPACE_PROPERTY = "GIVRESPONSE_OUTPUT_NAMESPACE";
	
	
	//Batch Print Drop 2/3 Begin
	
	public static final String A_SHIPMENT_CONTAINER_KEY = "ShipmentContainerKey";
	public static final String API_GET_SHIPMENT_CONTAINER_DETAILS = "getShipmentContainerDetails";
	public static final String API_GET_SHIPMENT_CONTAINER_DETAILS_FOR_PROSHIP_TEMPLATE_PATH = "global/template/api/getShipmentContainerDetailsForShip_Output.xml";
	public static final String A_CODE_LONG_DESC = "CodeLongDescription";
	public static final String A_EXTN_LBL_PRINT_FORMAT = "ExtnLblPrintFormat";
	public static final String API_SHIP_SYNC_WEB_SERVICE  = "KohlsShipSyncWebService";
	public static final String A_BASIC_FREIGHT_CHARGE = "BasicFreightCharge";
	public static final String A_TRACKING_NO = "TrackingNo";
	public static final String A_VOID_LABEL_ID = "ExtnVoidLabelId";
	public static final String API_LABEL_PRINT_SERVICE = "KOHLSLabelPrinterService";
	public static final String A_EXTN_PRINT_FORMAT = "EXTN_LBL_PRT_FORMAT";
	public static final String PROSHIP_ERROR = "ProShip Error";
	
	public static final String SERVICE_KOHLS_CHANGE_STATUS_MULTI_PRINT_DROP2 = "KOHLSChangeStatusMultiPrintService_Drop2";
	//For Void Label Flow:
	public static final String API_PACK_SHIPMENT_TEMPLATE_VOID_LABEL_PATH="global/template/api/changeShipmentStatus_VoidLabel.xml";
	public static final String MERGED_DOCUMENT = "MergedDocument";
	public static final String INPUT_DOCUMENT = "InputDocument";
	public static final String PROSHIP_DOCUMENT = "ProShipDocument";
	public static final String PROSHIP_RESPONSE = "PROSHIP_RESPONSE";
	public static final String A_ACTUAL_FREIGHT_CHARGE = "ActualFreightCharge";
	public static final String A_EXTN_VOID_LABEL_ID = "ExtnVoidLabelId";
	public static final String A_SHIPMENT_STATUS_AUDIT = "ShipmentStatusAudit";
	//for shipment Monitor
	public static final String A_SHIPMENT_PACKED_STATUS = "1300";
	public static final String A_STATUS_DATE_QRY_TYPE ="StatusDateQryType";
	public static final String A_LESS_THAN ="LT";
	public static final String A_SHIPMENT_CLOSED_FLAG="ShipmentClosedFlag";
	public static final String A_STATUS_DATE="StatusDate";
	public static final String A_SHIP_MONITOR_MAX_RECS = "500";
	public static final String SERVICE_KOHLS_SORT_PICK_SLIP_BY_SHIPMENT_KEY = "KohlsSortPickSlipByShipmentKey";
    public static final String ONE = "1";
	public static final String STATUS_AWAITING_STORE_PICK = "1100.70.06.10";
	
	// Added for Order Capture drop 2 - Juned S
	public static final String STORE_PICKUP= "STORE_PICKUP";
	public static final String BPS= "BPS";
	public static final String PICK= "PICK";

	// Added for Order Modification drop 2 - Juned S
	public static final String RESOLVE_HOLD_STATUS= "1300";
	public static final String ADD_HOLD_STATUS= "1100";
	public static final String ECOM_CHNG_ORDR_HOLD= "EComChngOrdrHold";
	public static final String ECOM_CHNG_ORDR_HOLD_REASON_TXT= "ECommerce Change Order Hold";
	public static final String CHANGE_ORDER_INP_TMPLT= "global/template/api/extn/changeOrder_input.xml";
	public static final String KOHLS_ECOMORDRREPIRCE_WEBSERVICE= "KOHLS_EComOrderReprice_WebService";
	
	// Added for Order Status changes drop 2 - Juned S
	public static final String ORDER_STATUS_SENT_TO_ECOMM = "ORDER_STATUS_SENT_TO_ECOMM";
	public static final String KOHLS_GETORDERLIST_TEMPLATE= "global/template/api/extn/KohlsGetOrderList_output.xml";

	// Added for Order Modification changes drop 2 - Juned S
	public static final String BPS_MODIFICATN_TYPE = "BPS_MODIFICATN_TYPE";
	// Added by Baijayanta
	public static final String KOHLS_ORDER_STATUS = "KOHLS_ORDER_STATUS";

	//Oasis SFS RTAM HTTP issue
    public static final String URL="URL";
    public static final String UserId="UserId";
    public static final String Password="Password";
    public static final String ApiName="ApiName";
	public static final String IsAPI = "IsAPI";
	
	//Start -- Added for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
	public static final String CHARGE_NAME_YES_WE_CAN="Yes_We_Can";
	//End -- Added for 08071,379,000 -- OASIS_SUPPORT 03/26/2014//
	//Added for Inventory Updates drop 2 - Ashalatha
	public static final String HOLD_AREA= "HOLD_AREA.ex";
	public static final String ADJUSTMENT="ADJUSTMENT";
	public static final String ONHAND="ONHAND";
	public static final String SALES_FLOOR= "SALES_FLOOR.ex";
	
	public static final String AWAIT_AUTH = "AWAIT_AUTH";
	 public static final String ZERO_INT = "0";
	 public static final String ONE_INT = "1";
	 public static final String NINE_INT = "999999";
	 public static final String THIRTEEN_INT = "13";
	 public static final String ZERO_TWO_INT= "02";
	 public static final String KOHLS_EXP_INTERVAL = "KOHLS_EXP_INTERVAL";
	 public static final String PPROCESS_TRANS_REQ = "ProcessTransactionRequest";
	 public static final String TYPE_CREDIT_CARD = "CREDIT_CARD";
	 public static final String HUNDRED_INT = "100";
	 public static final String SALE = "Sale";
	 public static final String VOID_SALE = "VoidSale";
	 public static final String INTERNAL = "internal";
	 public static final String PROTEGRITY =  "Protegrity";
	 public static final String DT_FORMAT_1 = "yyyyMMdd'T'HH:mm:ss";
	 public static final String DT_FORMAT_2 = "yyyy-MM-dd'T'HH:mm:ss.sss'Z'";
	 public static final String TYPE_DEBIT_CARD = "DEBIT_CARD";
	 public static final String PAYMENT_TYPE_DEBIT_CARD = "Debit Card";
	 public static final String PAYMENT_REQUEST = "PaymentRequest";
	 public static final String TENDER = "Tender";
	 public static final String DECIMAL_FORMAT = "0.00";
	 public static final String KEYED = "KEYED";
	 public static final String MERCHANDISE_RETURN_CREDIT = "MerchandiseReturnCredit";
	 public static final String STORE_873 = "873";
	 public static final String BOPUS_KOHLS_CHARGE_CARD ="KOHLS_CHARGE_CARD";
	 public static final String FALSE = "false";
	 public static final String KOHLS_CRD_VISA_EXP = "KOHLS_CRD_VISA_EXP";
	 public static final String	KOHLS_CRD_DISCVR_EXP = "KOHLS_CRD_DISCVR_EXP";
	 public static final String	KOHLS_CRD_MSTCRD_EXP = "KOHLS_CRD_MSTCRD_EXP";
	 public static final String	KOHLS_CRD_AMX_EXP = "KOHLS_CRD_AMX_EXP";
	 public static final String	KOHLS_CHARGE_EXP = "KOHLS_CHARGE_EXP";
	 public static final String	KOHLS_DEBIT_EXP = "KOHLS_DEBIT_EXP";
	 public static final String	KOHLS_STORE_VAL_EXP = "KOHLS_STORE_VAL_EXP";
	 public static final String KOHLS_DEFAULT_EXP = "KOHLS_DEFAULT_EXP";
	 
	//Added for BOPUS Invoice drop 3 - Sudhakar P
	public static final String TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX = "KOHLS_BOPUS_INVOICE.0001.ex";
	public static final String PLACED_IN_HOLD_LOCATION = "3350.14";
	public static final String API_GET_SHIPMENT_LIST_BOPUS_CONFIRM_SHIPMENT_TEMPLATE = "global/template/api/getShipmentListforBOPUSConfirmShipment_output.xml";

	//Added for EOD_Settelment drop 3 - Ashalatha

	public static final String AND_OPERATOR = "AND";

	public static final String GT_OPERATOR = "GT";
	public static final String LE_OPERATOR="LE";
	public static final String zero="0.00";

	public static final String TIMESTAMP_DATEFORMAT= "yyyy-MM-dd'T'kk:mm:ss";
	public static final String STATUS_VAL = "Awaiting Store Pick";
	public static final String ORDER_STATUS_AWAITING_STORE_PICK = "3350.12";

	 // Added for Alerts drop 4 - Juned S
	 public static final String CLOSED = "CLOSED";
 	 public static final String GETEXCEPTIONLIST_API = "getExceptionList";
	 public static final String MULTIAPI_API = "multiApi";
	 public static final String FROM_EMAIL_ID = "FromEmailID";
	 public static final String TO_EMAIL_IDS = "ToEmailIDs";
	 public static final String TO_EMAIL_SEPARATOR = "ToEmailSeparator";
	 public static final String EMAIL_SUBJECT = "EmailSubject";
	 public static final String KOHLS_STORE_MANAGER_QUEUE ="KOHLS_STORE_MANAGER_QUEUE";
	 public static final String KOHLS_STORE_ASSOCIATE_QUEUE ="KOHLS_STORE_ASSOCIATE_QUEUE";
	 
	 // Added drop 4 - Asha
	 public static final String GET_SHIPMENT_LIST_API = "getShipmentList";
	 public static final String TRAN_ID_CUSTOMER_PICK_0001_EX = "Customer Pick.0001.ex";
	 public static final String STATUS_PLACED_IN_HOLD_AREA = "1100.70.06.50.4";
	 public static final String SHIPMENT_READY_FOR_CUSTOMER_PICK_UP = "3350.035";

	 // Constants for LocationFeed Implementation.
	 public static final String ADJUSTMENT_TYPE = "AdjustmentType";
	 public static final String REASON_CODE = "ReasonCode";
	 public static final String REF_3 = "Reference_3";

	 // Constants added for creating new YFSEnvironment object.
	 public static final String USER_ID = "userId";
	 public static final String PROG_ID = "progId";
	 public static String GET_PROP_ELEMENT = "GetProperty";
	 public static String PROP_NAME_ATT = "PropertyName";
	 public static String GET_PROP_API = "getProperty";
	 public static String PROP_VALUE_ATT = "PropertyValue";

	 public static final String ADJUST_INV_API = "adjustInventory";
	 // Constants for NetCool logger implementation.
	 public static final String NET_COOL_ERR_MSG = "Input XML to NetCool logger is empty or null";

	 // Constants for LocationFeed Implementation.
	 public static final String XPATH_FOR_ORGANIZATION = "/LocationFeed/Organization/@OrganizationCode";	
	 //public static final String XPATH_FOR_ORGANIZATION = "LocationFeed/Organization";	
	 public static final String XPATH_FOR_CALENDAR = "/LocationFeed/Calendars/Calendar/@CalendarId";
	 //public static final String XPATH_FOR_CALENDAR = "LocationFeed/Calendars/Calendar";	
	 public static final String MANAGE_ORGANIZATION_HIERARCHY_API = "manageOrganizationHierarchy";	
	 public static final String CHANGE_CALENDAR_API = "changeCalendar";	
	 public static final String CREATE_CALENDAR_API = "createCalendar";	
	 public static final String GET_CALENDAR_LIST_API = "getCalendarList";	
	 public static final String GET_DISTRIBUTION_LIST_API = "getDistributionList";	
	 public static final String CREATE_DISTRIBUTION_API = "createDistribution";	
	 public static final String DG_NAME_PROPERTY = "DG_NAME";	
	 public static final String OWNER_KEY_PROPERTY = "OWNER_KEY";	
	 public static final String ITEM_SHIP_NODE_XML = "<ItemShipNode ActiveFlag=\'Y\' ItemId=\'ALL\' />";
	 public static final String ERROR_CODE_ATTRIBUTE = "ErrorCode";
	 public static final String ERROR_RELATED_MORE_INFO_ATTRIBUTE = "ErrorRelatedMoreInfo";
	 public static final String ERROR_DESCRIPTION = "ErrorDescription";
	 public static final String ERROR_CODE_EXTN000001 = "EXTN000001";
	 public static final String ERROR_CODE_EXTN000001_DESCRIPTION = "Organization already is associated to a Calendar";
	 public static final String CONST_ = "_";

	 //Added by Naveen for BOPUS Status Updates
	 public static final String KOHLS_GETORDERLIST_STATUSUPDATE_TEMPLATE= "global/template/api/extn/KohlsBOPUSGetOrderList_StatusUpdate.xml";
	 public static final String KOHLS_GETSHIPLIST_STATUSUPDATE_TEMPLATE= "global/template/api/extn/KohlsBOPUSGetShipmentList_StatusUpdate.xml";
	 public static final String KOHLS_GET_SHIPMENT_STORE_EVENTS = "getKohlsShipmentStoreEventsList";
	 public static final String KOHLS_SEND_SHIPMENT_STATUS_TO_ECOMM = "KohlsSendShipmentStatusToEcommSyncService";
	 public static final String KOHLS_GETORDERLIST_EXP_STATUSUPDATE_TEMPLATE= "global/template/api/extn/KohlsBOPUSGetOrderList_ExpiredStatusUpdate.xml";


	 //public static final String API_GET_SHIPMENT_LIST_BOPUS_CONFIRM_SHIPMENT_TEMPLATE = "global/template/api/getShipmentListforBOPUSConfirmShipment_output.xml";
	 
	 //PickedUp Confirm notification - Baijayanta
	 public static final String KOHLS_BPS_PICKEDUP_SYNC= "KohlsBpsPickedUpMsgToMrktng";
	 public static final String PICKUP_CONFIRMED = "CUSTOMER_PICKEDUP";
	 
	 
	 // Added for Customer Notification drop 4 - Juned S
	 public static final String ORDER_READY_FOR_PICK = "ORDER_READY_FOR_PICK";
	 public static final String KOHLS_READY_FOR_CUST_MSG_TO_MRKTNG = "KohlsBpsReadyForCustMsgToMrktng";
	 
	 // Added for Customer Notification drop 4 - Ravi
	 public static final String API_SHIPMENT_CONSOLIDATION_ORDER_LIST = "global/template/api/extn/KohlsShipConsolGetOrderList_output.xml";;

	// Added for Customer Notification drop 5 - Juned S
	 public static final String KOHLS_ORDER_DELAY_MSG_TO_MRKTNG = "KohlsBpsOrdrDelayMsgToMrktng";
	 public static final String CHANGE_ORDER_API = "changeOrder";	 
	 public static final String API_GET_ORDER_LIST_BOPUS_CONSOL_TEMPLATE = "global/template/api/extn/KohlsShipConsolGetOrderList_output.xml";
	 public static final String API_GET_SHIPMENT_LIST_BOPUS_CONSOL_TEMPLATE = "global/template/api/extn/KohlsShipConsolGetShipmentList_output.xml";
	 public static final String KOHLS_BPS_ORDR_MOD_CUST_MSG= "KohlsBpsOrdrModCustMsgToMrktng";

	 // Added for Customer Notification drop 5 - Ravi
	 public static final String A_EXTN_CUST_NOTIFICATION_SENT = "ExtnCustNotificationSent";
	 public static final String SERVICE_SHIPMENT_STORE_EVENTS = "KOHLS_ShipmentStoreEvents";
	 public static final String API_CREATE_ASYNC_REQUEST = "createAsyncRequest";

	 //Added for Stop demand update to GIV on create Order for BOPUS Lines
	 public static final String BOPUS_LINES = "BOPUS_LINES";

	 //Added for Drop 4 - Customer Notification
	 public static final String STATUS_READY_FOR_CUSTOMER = "1100.70.06.30";
	 public static final String PICKUP_REMINDER_DAYS = "pickupReminderDays";
	 public static final String FINAL_PICKUP_REMINDER_DAYS = "FinalPickupReminderDays";
	 public static final String PICKUP_REMINDER = "PICKUP_REMINDER";
	 public static final String FINAL_PICKUP_REMINDER = "FINAL_PICKUP_REMINDER";
	 
	 public static final String A_SHIPMENT_LINE_NO = "ShipmentLineNo";

	public static final String PPROCESS_TRANS_RESP = "ProcessTransactionResponse";
	public static final String A_UPDATE_LOCATION_ITEMS="KohlsUpdateLocationItems";
	public static final String TEMPLATE_SHIPMENTS_PICK_PROCESS="global/template/api/getShipmentList_PickingProcess.xml";
	public static final String TEMPLATE_CHANGE_SHIPMENTS_PICK_PROCESS="global/template/api/changeShipment_PickingProcess.xml";
	public static final String TEMPLATE_GET_SHIPMENT_LINE_LIST_PICK_PROCESS ="global/template/api/getShipmentLineList_PickingProcess.xml";

	 //GetOrderDetails Audit
 	 public static final String API_SHIPMENT_STORE_EVENTS = "getShipmentStoreEventsList";
 	 public static final String V_SHORT_PICK = "ShortPick";
 	 public static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";
 	 public static final String V_KOHLS_COM = "KOHLS.COM";
 	 public static final String V_CANCEL = "CANCEL";
 	 public static final String API_GET_ORDER_LIST_BOPUS_TEMPLATE = "global/template/api/extn/KohlsBOPUSGetOrderList_output.xml"; 	 
 	 public static final Object V_PICK = "PICK";
 	 public static final String V_CANCEL_STATUS = "9000";

	//Added MLS Integration
	public static final String GET_SHIPMENT_LINE_LIST_API = "getShipmentLineList";
	public static final String V_PROCESS_BACK_ROOM_PICK = "Process Backroom Pick.0001.ex";

	public static final String CONFIRM_SHIPMENT = "confirmShipment";
	public static final String CHANGE_SHIPMENT_STATUS = "changeShipmentStatus";
	public static final String CHANGE_TO_EXPIRED = "changeToExpired";


	public static final String API_GET_ORDER_AUDIT_LIST = "getOrderAuditList";
	public static final String GET_ORDER_AUDIT_LIST_OUTPUT_TMPLT = "global/template/api/extn/KohlsGetOrderAuditList_output.xml";
	public static final String ORDER_LINE = "ORDER_LINE";
	public static final String ORDER = "ORDER";

	public static final String YANTRA_HTTP_TESTER = "YantraHttpTester";
	public static final String YFSENV_PROG_ID= "YFSEnvironment.progId";
	public static final String INTEROP_API_NAME= "InteropApiName";
	public static final String IS_FLOW= "IsFlow";
	public static final String INTEROP_API_DATA= "InteropApiData";
	public static final String TEMPLATE_DATA= "TemplateData";
	public static final String YFSENV_USERID= "YFSEnvironment.userId";
	public static final String YFSENV_PWD= "YFSEnvironment.password";
	public static final String HTTP = "http";
	public static final String TRUE = "true";
	//public static final String EACH = "EACH";
	public static final String STOCK_ROOM= "STOCK_ROOM.ex";

	public static final String MIXED = "MIXED";
	public static final String BACK_ORDERED = "1300";
	public static final String V_OPEN_API_HEADER_KEY_PARAM = "X-APP-API_KEY";
	public static final String V_BYPASS_OPEN_API= "BYPASS_OPEN_API";
	public static final String V_OPEN_API_RESPONSE = "OPEN_API_RESPONSE";
	public static final String V_OPEN_API_ALT_TEXT = "OPEN_API_ALT_TEXT";
	public static final String V_OPEN_API_PROXY_HOST = "OPEN_API_PROXY_HOST";
	public static final String V_OPEN_API_PROXY_PORT = "OPEN_API_PROXY_PORT";

	public static final String CHANGE_SHIPMENT_API = "changeShipment";

	public static final String V_ITEM_BEING_PICKED = "Item is being picked";
	public static final String V_SHORT_PICKED = "Short Picked";
	public static final String V_PICK_COMPLETED = "Picking Completed";

	//public static final String GET_SHIPMENT_LINE_LIST_API = "getShipmentLineList";
	// public static final String V_PROCESS_BACK_ROOM_PICK = "Process Backroom Pick.0001.ex";
	 public static final String API_GET_SHIPMENT_LIST_MLS_TEMPLATE = "global/template/api/extn/KohlsMLSGetShipmentList.xml";
	 public static final String API_GET_SHIPMENT_LINE_LIST_BARCODE_TEMPLATE = "global/template/api/getShipmentLineList_barcodeLookup.xml";
	 
	 public static final String V_PICKING_IN_PROGRESS = "Picking in Progress";
	 public static final String V_ITEM_IS_BEING_PICKED = "Item is being picked";
	 //public static final String V_SHORT_PICKED = "Short Picked";
	 public static final String V_PICKING_SUSPENDED = "Picking Suspended";
	 public static final String V_PICKING_RESUMED = "Picking Resumed";
	 public static final String V_UNDO_PICKING = "Undo Picking";
	 public static final String V_PICKING_COMPLETED = "Picking Completed";
	 public static final String V_PLACED_IN_HOLD_AREA = "Placed in Hold Area";
	 public static final String V_READY_FOR_CUSTOMER_PICKUP = "Ready For Customer Pickup";
	 public static final String SERVICE_CRTE_PUB_SHIP_STR_EVNTS = "KOHLSCreateStoreEvents";
	 public static final String V_MIXED = "MIXED";
	 public static final String V_SRO = "SRO";
	 public static final String V_SFO = "SFO";
	 //public static final String STOCK_ROOM= "STOCK_ROOM.ex";
	 public static final String EACH = "EACH";
	 public static final String WS_MLS_INPUT_XSL = "global/template/xsl/KohlsInputForMLSGetItemLocationWS.xsl";
	 	
	 //public static final String SFO = "SFO";
	 //public static final String SRO = "SRO";
	 //public static final String MIXED = "MIXED";
	 //public static final String BACK_ORDERED = "1300";	
	 
	 public static final String CANCELLED  = "Cancelled";	 
	 public static final String KOHLS_BPS_ORDR_CANCEL_CUST_MSG ="KohlaBpsOrdrCnclCustMsgToMrktng";	 
	 public static final String REASON_CODE_CUST_INITIATED_MODIFICATION = "CUST_INITIATED_MODIFICATION";
	 public static final String ORDER_CANCELLATION_CUST_NOTIFICATION = "ORDER_CANCELLATION";
		 public static final String CALENDAR_GET_ORGANIZATION_HIERARCHY_TEMPLATE="global/template/api/extn/kohlsBOPUSGetOrganizationHierarchy_Output.xml";
		 
		  
		 //Drop 8
		 public static final String KOHLS_SHP_STATUS_GET_SHIPMENT_LIST_OUTPUT_TMPLT = "global/template/api/extn/KohlsBOPUSShpStatusGetShipmentList_output.xml";
		 public static final String KOHLS_GET_SHIPMENT_STORE_EVENTS_LIST_SERVICE = "KohlsGetShipmentStoreEventsListForShipStatus";
		 public static final String ACTION_Create ="Create";
		 	 
		 public static final String A_REQUESTED_SHIPMENT_DATE = "RequestedShipmentDate";
		 public static final String A_EARLIEST_SHIFT_START_TIME = "EarliestShiftStartTime";
		 public static final String A_LAST_SHIFT_END_TIME = "LastShiftEndTime";
		 
		 public static final String A_FROM_DATE = "FromDate";
		 public static final String A_TO_DATE = "ToDate";
		 
		  //Added MLS Integration
		 
		 
		 public static final String SERVICE_GET_INV_STOREPICK_WS = "KohlsGetInvDetailsforStorePickWebservice";
		 public static final String SERVICE_GET_ITEM_LOC_STOREPICK_WS = "KohlsGetItemLocationForStorePickService";
		 
		 //MLS Web service caller callEndpointMLS method in WebServiceCaller.java
		 public static final String V_SERVICENAME = "SterlingServices";
		 public static final String V_PORTNAME = "SterlingServicesSOAP";
		 public static final String V_SOAPACTION = "soapAction";
		 public static final String V_ENDPOINT = "endPoint";
		 public static final String V_TIMEOUT = "timeOut";
		 public static final String V_ENDPOINTURL = "endPointURL";
		 public static final String MLS_USE_DEFAULT_STORE = "MLS_USE_DEFAULT_STORE"; 
		 public static final String MLS_DEFAULT_STORE_LOOKUP = "MLS_DEFAULT_STORE_LOOKUP"; 
		 public static final String MLS_ENDPOINT_URL_PREFIX = "MLS_ENDPOINT_URL_PREFIX"; 
		 public static final String MLS_ENDPOINT_URL_SUFFIX = "MLS_ENDPOINT_URL_SUFFIX"; 
		 public static final String MLSSOAPACTIONURL_PROPERTY = "MLSSOAPACTIONURL";
		 public static final String MLSSOAPACTION_GETITEMLOCATIONS = "MLSSOAPACTION_GETITEMLOCATIONS";
		 public static final String MLSSOAPACTION_GETLOCATIONDETAILS = "MLSSOAPACTION_GETLOCATIONDETAILS";
		 public static final String MLSSOAPACTION_UPDATELOCATIONITEMS = "MLSSOAPACTION_UPDATELOCATIONITEMS";
		 public static final String END_POINT_USER_MLS="END_POINT_USER_MLS";
		 public static final String END_POINT_PWD_MLS="END_POINT_PWD_MLS";
		 public static final String V_END_POINT_USER = "endPointUser";
	         public static final String V_END_POINT_PWD = "endPointPassword";
	         public static final String A_UPDATE_LOCATION_ITMES="KohlsUpdateLocationItems";


		 // Open API Image Web service call - Ravi
		 public static final String V_OPEN_API_BASEURL = "OpenAPI_BaseURL";
		 public static final String V_OPEN_API_KEY = "OpenAPI_Key";
		 //public static final String V_OPEN_API_HEADER_KEY_PARAM = "X-APP-API_KEY";
		 public static final String V_SKU_DETAIL = "skuDetail";
		 //public static final String V_BYPASS_OPEN_API= "BYPASS_OPEN_API";
		 //public static final String V_OPEN_API_RESPONSE = "OPEN_API_RESPONSE";
		 public static final String V_CONNECTION_TIMEOUT = "connectionTimeout";
		 public static final String V_CONNECTION_READ_TIMEOUT = "connectionReadTimeout";
			
		 //Location Details Desc stamping at Shipment header - Ravi
		 public static final String API_GET_SHIPMENT_LIST_MLSLOCDESC_TEMPLATE = "global/template/api/extn/KohlsMLSLocationDescGetShipmentList.xml";
		 public static final String SERVICE_WS_MLS_LOC_DESC = "KohlsBOPUSGetLocationDetailsWS";
		 public static final String SERVICE_WS_MLS_UPD_PICK = "KohlsBOPUSUpdatePicking";
				
		 public static final String GET_ORGANIZATION_HIERARCHY_API = "getOrganizationHierarchy"; 
		 public static final String API_GET_CALENDAR_DAY_DETAILS_API="getCalendarDayDetails";
		 
	     public static final String SHIPMENT = "Shipment";
	   	 public static final String PICKING_SUSPENDED ="Picking_Suspended";
	   	 public static final String SHIPMENT_SUSPEND_HOLD ="Shipment suspend hold";
	   	 public static final String SUSPEND_STATUS="1100";
	   	 public static final String RESUME_STATUS = "1300";
	   	 public static final String SFO = "SalesFloor";
	   	 public static final String SRO = "Stockroom";  
		 
		 public static final String V_SHIPPED_STATUS = "Shipped";
		 public static String _JEW = "_JEW";
	   	 public static String JEW = "JEW";
		 
		 	public static final String V_ITEM_LEGAL_CALLBACK = "Item legal callback";
			public static final String PLU_MESSAGE = "MESSAGE";
			public static final String PLU = "PLU";
			public static final String REQUEST = "Request";
			public static final String DATE = "MM/dd/yy HH:mm:ss";
			
			public static final String TRAN_CONFIRM_SHIPMENT = "CONFIRM_SHIPMENT";
			public static final String TRAN_PROCESS_PENDING_RET = "PROCESSPENDINGRETURN.0001.ex";
			public static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	//Common Code
	 public static final String CODE_TYPE_ORD = "ORDER";
	 public static final String CODE_TYPE_SHP = "SHIPMENT";
	 public static final String V_CODE_TYPE_ORD = "BOPUS_ORD_STATUS";
	 public static final String V_CODE_TYPE_SHP = "BOPUS_SHPMNT_STATUS";

	 public static final String API_GET_COMMON_CODE_LIST_TEMPLATE = "global/template/api/extn/KohlsGetCommonCodeList.xml";

	 public static final String CC_DESC_SHIPMENT_CREATED = "Shipment_Created";
	 public static final String CC_DESC_AWAITING_STORE_PICK = "Awaiting_Store_Pick";
	 public static final String CC_DESC_PICKING_IN_PROGRESS = "Picking_In_Progress";
	 public static final String CC_DESC_STORE_PICK_COMPLETED = "Store_Pick_Completed";
	 public static final String CC_DESC_PLACED_IN_HOLD_LOCATION = "Placed_In_Hold_Location";
	 public static final String CC_DESC_READY_FOR_CUSTOMER = "Ready_For_Customer";
	 public static final String CC_DESC_SHIPMENT_SHIPPED = "Shipment_Shipped";
	 public static final String CC_DESC_CUSTOMER_PICKED_UP = "Customer_Picked_Up";
	 public static final String CC_DESC_EXPIRED_PICK_UP = "Expired_Pick_Up";
	 public static final String CC_DESC_RETURNED = "Returned";
	 public static final String CC_DESC_PENDING_EXPIRED_RETURN = "Pending_Expired_Return";
	 public static final String CC_DESC_SHIPMENT_CANCELLED = "Shipment_Cancelled";

	 public static final String CC_DESC_DRAFT_ORD_CREATED = "Draft_Order_Created";
	 public static final String CC_DESC_DRAFT_ORD_RESERVED = "Draft_Order_Reserved";
	 public static final String CC_DESC_CREATED = "Created";
	 public static final String CC_DESC_UNSCHEDULED = "UnScheduled";
	 public static final String CC_DESC_BACK_ORDERED = "Back_Ordered";
	 public static final String CC_DESC_SCHEDULED = "Scheduled";
	 public static final String CC_DESC_RELEASED = "Released";
	 public static final String CC_DESC_INCLUDED_IN_SHIPMENT = "Included_In_Shipment";
	 public static final String CC_DESC_PICK_UP_EXCEPTION = "Pick_Up_Exception";
	 public static final String CC_DESC_AWAITING_ORDER_CLOSURE = "Awating_Order_Closure";
	 public static final String CC_DESC_AWAITING_INVOICE_CREATION = "Awaiting_Invoice_Creation";
	 public static final String CC_DESC_INVOICED = "Invoiced";
	 public static final String CC_DESC_CANCELLED = "Cancelled";
	 public static final String CC_DESC_STORE_PICK_IN_PROGRESS = "Store_Pick_In_Progress";
	 public static final String CC_DESC_STORE_SHIPPED = "Shipped";
	 


	 public static final String TMP_TIER_FLAG = "TierFlag";
	 public static final String V_TIER2 = "TIER2";
	 public static final String V_TIER3 = "TIER3";
	 public static final String V_TIER0 = "";
	 
	 // constants for create order agent
	 public static final String API_CREATE_ORDER = "createOrder";

	 // Order Modifications Hold Check

	 public static final String API_GET_ORDER_HOLDS_TEMPLATE =  "global/template/api/extn/modOrderHoldCheck_output.xml" ;
	 public static final String API_GET_SHP_HOLDS_TEMPLATE = "global/template/api/extn/modShpHoldCheck_output.xml";
	 public static final String V_HOLD_CONFIRM_SHPMNT = "HOLD_CFRM_SHPMT";
	 public static final String API_SHPMNT_LIST_FOR_ORDR_TEMPLATE = "global/template/api/extn/shpmntListForOrder_output.xml";
	 public static final String ORDER_HOLD_TYPE = "EComChngOrdrHold";
	 public static final String SHPMNT_HOLD_TYPE = "HOLD_CFRM_SHPMT";
	 public static final String API_GET_ITEM_LIST_WITH_EXTN_FIELDS_TEMPLATE_PATH = "global/template/api/getItemListForExtnFields_output.xml";
	 
}