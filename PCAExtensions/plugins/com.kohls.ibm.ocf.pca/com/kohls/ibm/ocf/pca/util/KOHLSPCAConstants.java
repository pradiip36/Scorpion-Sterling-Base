package com.kohls.ibm.ocf.pca.util;

/**
 *
 * Constant Name should be in Uppercase. Attribute should start with A_
 * (example: A_ACTION) Element should start with E_ (example: E_ORDER) Value
 * should start with V_ (example: V_THREE) No constraint on Misc constants
 * (example: INBOUND_SERVICE)
 */

public interface KOHLSPCAConstants {

	// Pack Shipment Screen
	public static final String A_QUANTITY = "Quantity";

	public static final String A_QUANTITY_TO_PACK = "QuantityToPack";

	public static final String A_PLACED_QUANTITY = "PlacedQuantity";

	public static final String A_ENTERPRISE_CODE = "EnterpriseCode";

	public static final String E_CONTEXTUALINFO = "ContextualInfo";

	public static final String A_ORGANIZATION_CODE = "OrganizationCode";

	public static final String A_CALLING_ORGANIZATION_CODE = "CallingOrganizationCode";

	public static final String A_ITEM_GROUP_CODE = "ItemGroupCode";

	public static final String V_PROD = "PROD";

	public static final String A_IS_SHIPPING_CONTAINER = "IsShippingCntr";

	public static final String FLAG_Y = "Y";

	public static final String E_ITEM = "Item";

	public static final String E_BAR_CODE = "BarCode";

	public static final String V_ITEM = "Item";

	public static final String A_BAR_CODE_DATA = "BarCodeData";

	public static final String A_BAR_CODE_TYPE = "BarCodeType";

	public static final String V_CATALOG_ENTERPRISE = "KOHLS.COM";

	public static final String E_TRANSLATIONS = "Translations";

	public static final String A_TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";

	public static final String E_TRANSLATION = "Translation";

	public static final String A_ITEM_CONTEXTUAL_INFO = "ItemContextualInfo";

	public static final String A_ITEM_ID = "ItemID";

	public static final String A_SHIPMENT_LINES = "ShipmentLines";

	public static final String E_ITEM_LIST = "ItemList";

	public static final String E_CONTAINERS = "Containers";

	public static final String V_ZERO_NUMBER_OF_RECORDS = "0";

	public static final String E_SHIPMENT_LINES = "ShipmentLines";

	public static final String E_CONTAINER = "Container";

	public static final String A_IS_PACK_PROCESS_COMPLETE = "IsPackProcessComplete";

	public static final String A_CONTAINER_NO = "ContainerNo";

	public static final String V_INT_ONE = "1";

	public static final String E_CONTAINER_DETAILS = "ContainerDetails";

	public static final String E_CONTAINER_DETAIL = "ContainerDetail";

	public static final String A_PRODUCT_CLASS = "ProductClass";

	public static final String A_UOM = "UnitOfMeasure";

	public static final String A_SHIPMENT_KEY = "ShipmentKey";

	public static final String A_SHIPMENT_LINE_KEY = "ShipmentLineKey";

	public static final String E_SHIPMENT_LINE = "ShipmentLine";

	public static final String A_ITEM_DESC = "ItemDesc";

	public static final String A_CONTAINER_TYPE = "ContainerType";

	public static final String A_ADDING_COMPLETE = "AddingComplete";

	// Print Pick Slips

	public static final String A_TOTAL_ITEM_LIST = "TotalItemList";

	public static final String SHARED_TASK_ITEM_SEARCH_LOOKUP = "SOPItemLookupSharedTask";

	public static final String E_DEVICE = "Device";

	public static final String A_DEVICE_TYPE = "DeviceType";

	public static final String V_PRINTER = "Printer";

	public static final String A_SHIP_NODE = "ShipNode";

	public static final String E_TOTE = "Tote";

	public static final String A_TOTES_PER_CART = "TotesPerCart";

	public static final String A_SHIPMENT_TYPE = "ShipmentType";

	public static final String A_TOTAL_SHIPMENTS = "TotalShipments";

	public static final String A_PRINTER_ID = "PrinterID";

	public static final String A_PRINT_TYPE = "PrintType";

	public static final String V_PRINT_TYPE_SINGLE = "SINGLE";

	public static final String V_PRINT_TYPE_MULTI = "MULTI";

	public static final String A_TOTAL_CARTS = "TotalCarts";

	public static final String V_BLANK = "";

	public static final String A_CONTAINER_NET_WT = "ContainerNetWeight";

	public static final String A_CONTAINER_NET_UOM = "ContainerNetWeightUOM";

	public static final String A_STATUS = "Status";

	public static final String V_DUMMY = "Dummy";

	public static final String STATUS_SHIPMENT_PICK_LIST_PRINTED = "1100.03";

	public static final String STATUS_SHIPMENT_PACK_IN_PROGRESS = "1100.04";

	public static final String STATUS_SHIPMENT_PACKED = "1300";

	public static final String STATUS_SHIPMENT_SHIPPED = "1400";

	//SOP INI PROPERTIES,

	public static final String INI_PROPERTY_MUTLI_CONTAINER_TYPE = "default.pack.container";

	public static final String INI_PROPERTY_TOTES = "shipments.per.tote";

	public static final String INI_PROPERTY_USER_DIRECTORY = "worldship.directory";

	public static final String INI_PROPERTY_USER_LOGGED_IN_STORE = "store";

	public static final String INI_PROPERTY_USER_LOGGED_IN_ENTERPRISE = "enterprise";

	public static final String A_IS_NEW_CONTAINER = "IsNewContainer";

	public static final String E_SHIPMENT = "Shipment";

	public static final String A_ACTION = "Action";

	public static final String V_DELETE = "Delete";

	public static final String A_SHIPMENT_CONTAINER_KEY = "ShipmentContainerKey";

	public static final String A_CONTAINER_SCM = "ContainerScm";

	public static final String E_WORDLDSHIP_DOC_TAG = "OpenShipments";

	public static final String E_WORDLDSHIP_OPEN_SHIPMENT = "OpenShipment";

	public static final String A_SHIPMENT_NO = "ShipmentNo";

	public static final String A_CONTAINER_DETAIL = "ContainerDetail";

	public static final String INI_PROPERTY_PRINTER_ID = "default.sim.printerid";

	public static final String E_XML_DATA = "XMLData";

	public static final String V_SHIPMENT = "Shipment";

	public static final String V_SPACE = " ";

	public static final String SOP_GENERIC_SHIPMENT_DETAILS_SCREEN_FORM_ID = "com.yantra.pca.sop.rcp.tasks.outboundexecution.searchshipment.wizardpages.SOPGenericShipmentDetails";

	public static final String V_XMLNS = "x-schema:OpenShipments.xdr";

	public static final String A_XMLNS = "xmlns";

	public static final String SOP_RECORD_SHORTAGE_PERMISSION_ID = "SOPRCPKOHLS5000";

	public static final String E_SHIP_NODE = "ShipNode";

	public static final String A_BACKORDERED_REMOVED_QTY = "BackOrderRemovedQuantity";

	public static final String V_ACTION_MODIFY = "Modify";

	public static final String A_SHIPMENT_LINE_NO = "ShipmentLineNo";

	public static final String A_FORM_ID = "FormID";

	public static final String V_SHIPMENT_STATUS_PICK_LIST_PRINTED = "1100.03";

	public static final String V_SHIPMENT_STATUS_READY_FOR_CUSTOMER = "1100.70.06.30";

	public static final String A_BASE_DROP_STATUS = "BaseDropStatus";

	public static final String A_TRANSACTION_ID = "TransactionId";

	public static final String V_TRANSACTION_PREPARE_FOR_CUSTOMER_PICK = "READY_FOR_CUSTOMER.0001.ex";
	public static final String V_TRANSACTION_PICK_LIST_PRINT = "PICK_LIST_PRINT.0001.ex";

	public static final String E_STATUS = "Status";

	public static final String A_DESCRIPTION = "Description";

	public static final String V_BUTTON_PREPARE_FOR_CUST_PICK_DEFAULT = "Prepare For Customer Pick Up";

	public static final String V_BUTTON_PREPARE_FOR_SHIP = "Prepare For Shipping";

	public static final String V_HOT_KEY_PREPARE_CUST_PICK = "Ctrl+K";

	public static final String A_UNIT_WEIGHT_UOM = "UnitWeightUOM";

	public static final String A_UNIT_WEIGHT = "UnitWeight";

	public static final String E_PRIMARY_INFORMATION = "PrimaryInformation";

	public static final String A_ACTUAL_WEIGHT = "ActualWeight";

	//added by puneet 30 jun 2012 start

	public static final String V_AWAITING_PICKLIST_PRINT = "1100.025";

	public static final String V_Y = "Y";

	public static final String A_LABEL_PICK_TICKET = "LabelPickTicket";

	public static final String V_N = "N";

	public static final String V_BASE_PROCESS_TYPE_SHIPMENT = "ORDER_DELIVERY";

	public static final String A_BASE_PROCESS_TYPE = "BaseProcessType";

	public static final String A_DOCUMENT_TYPE = "DocumentType";

	public static final String A_STATUS_TEMP = "StatusTemp";

	public static final String A_NODE_TYPE = "NodeType";

	public static final String V_STORE = "STORE";

	public static final String V_MULTI_PREFIX = "Multi";

	public static final String A_STATUS_FROM_TEMP = "StatusFromTemp";

	public static final String A_STATUS_FROM = "FromStatus";

	public static final String A_STATUS_TO = "ToStatus";

	public static final String A_STATUS_TO_TEMP = "StatusToTemp";

	public static final String V_STATUS_QUERY_TYPE = "BETWEEN";

	public static final String A_STATUS_QUERY_TYPE = "StatusQryType";


	//added by puneet 30 jun 2012 end

    //added by puneet 25 july 2012 start
	public static final String V_SHIPMENT_SEARCH_STATUS = "1100.025;1100.03;1100.04;1100.70.06.30;1300;1400;9000";

	public static final String A_FORM_REQUESTED_SHIPMENT_DATE = "FromRequestedShipmentDate";

	//added by puneet 25 july 2012 end

	//	added by puneet on 09 Aug 2012 - start
	public static final String A_UPC_BARCODE_PICK_TICKET = "UpcBarcodePrint";
	//added by puneet on 09 Aug 2012 - End

	//	added by puneet on 21 Aug 2012 - start
	public static final String V_SHIPMENT_SEARCH_SHIPMENT_TYPE = "Single_Regular;Single_Gift;Single_Priority;Multi_Regular;Multi_Gift;Multi_Priority;Multi_Color";
	public static final String E_SHIPMENT_TYPE_LIST = "ShipmentTypeList";
	public static final String E_SHIPMENT_TYPE = "ShipmentType";

	public static final String A_SHIPMENT_CONTAINERIZED_FLAG = "ShipmentContainerizedFlag";

	public static final String V_SHIPMENT_PACKED = "03";

	public static final String V_CLOSE_CONTAINER_BARCODE = "close_container";

	public static final String V_KOHLS_RE_PACKSHIP_JASPER = "/KohlsRePackShip.jasper";
	public static final String V_KOHLS_PACKSHIP_JASPER = "/KohlsPackShip.jasper";
	
	//Start changes for SFS June Release
	
	public static final String V_KOHLS_PACKSHIP_JASPER_COLLATE = "/KohlsPackShipCollate.jasper";
	
	//End changes for SFS June Release

	public static final String E_PRINT_PACK_DOC = "PrintPackDoc";

    //	added by puneet on 21 Aug 2012 - END

    // added by puneet on 11 sept start
	public static final String SOP_RECORD_CUSTOMER_PICK_PERMISSION_ID = "SOPRCPKOHLS5005";
	// added by puneet on 11 sept end

	public static final String A_PRINT_BATCH_NUMBER = "PrintBatchNo";

	public static final String V_DATE_TIME_FORMAT = "yyyyMMddHHmmss";

	public static final String E_PRINT_PACK_SHIPMENTS = "PrintPackShipments";

	public static final String E_PRINT_PACK_SHIPMENT = "PrintPackShipment";

	public static final String INI_PROPERTY_DEFAULT_PACK_STATION = "default.pack.station";

	public static final String A_COMMITMENT_CODE = "CommitmentCode";

	public static final String A_BATCH_ID = "BatchID";
	//added by Zubair on 01 Oct 2012
	public static final String INI_PROPERTY_PICK_PRINTER_ID = "default.sim.pick.printerid";

	public static final String V_MULTI_REGULAR = "Multi_Regular";

	public static final String V_MULTI_GIFT = "Multi_Gift";

	public static final String V_MULTI_PRIORITY = "Multi_Priority";

	public static final String V_MULTIREGULAR = "MultiRegular";

	public static final String V_MULTIGIFT = "MultiGift";

	public static final String V_MULTIPRIORITY = "MultiPriority";

	public static final String A_OPTION = "Option";

	public static final String A_CART_NUMER = "CartNo";

	public static final String A_FROM_CART_NUMER = "FromCartNo";

	public static final String A_TO_CART_NUMER = "ToCartNo";

	public static final String A_PROFILE_ID = "ProfileID";

	public static final String A_RESULT = "Result";

	//Srikanth Added for bacth print
	public static final String A_BATCH_USER_ID = "User";
	public static final String A_BATCH_STATUS = "Status";
	public static final String A_LOGIN_ID = "Loginid";
	public static final String A_PRINT_ST_PRINTED = "Printed";
	public static final String A_PRINT_ST_AWAIT_PRINT = "Awaiting Printing";
	public static final String A_PRINT_ST_REPRINT = "Reprinted";
	public static final String A_PRINT_ST_FAILED = "Print Failed";
	public static final String A_SINGLES_PRINT = "SINGLES_PRINT";
	public static final String A_MULTI_PRINT = "MULTI_PRINT";
	public static final String A_JASPER_PRJ_ROOT="com.kohls.ibm.ocf.pca";
		//SIM INI - Drop2

	public static final String KOHLS_SIM_INI_PROP="SIMINIProperties";

	public static final String KOHLS_TERMINAL_ID = "TerminalID";
	
	//pawan - Start - Drop2 
	public static final String A_STATUS_AWAIT_PICK_LIST_PRINT = "1100.025";
	public static final String A_STATUS_SHIPPED = "1400";
	public static final String M_SHIPMENTS_AWAIT_PICK_LIST_PRINT = "ShipmentList_AwaitingPickSlipPrint";
	public static final String M_SHIPMENTS_SELECTED_PICK_LIST_PRINT="ShipmentsSelectedForPrint";
	public static final String INI_PROPERTY_DEFAULT_PRINTER="DefaultPrinter";
	public static final String INI_PROPERTY_DEFAULT_PACK_PRINTER="DefaultPackPrinter";
	public static final String INI_PROPERTY_DEFAULT_LABEL_PRINTER="DefaultLabelPrinter";
	public static final String INI_PROPERTY_PACK_STATION="DefaultPackStation";
	//pawan - End - Drop2 
	//Change Shipping Address -Drop2 -start
	public static final String A_LAST_NAME = "LastName";
	public static final String A_FIRST_NAME = "FirstName";
	public static final String A_ADDRESS_LINE1 = "AddressLine1";
	public static final String A_ADDRESS_LINE2 = "AddressLine2";
	public static final String A_STATE = "State";
	public static final String A_CITY = "City";
	public static final String A_ZIP_CODE = "ZipCode";
	public static final String A_EAMIL_ID = "EMailID";
	public static final String A_DAY_PHONE = "DayPhone";
	public static final String E_TO_ADDRESS = "ToAddress";
	public static final String A_ADDRESS_LINE3 = "AddressLine3";
	public static final String A_ADDRESS_LINE4 = "AddressLine4";
	public static final String A_ADDRESS_LINE5 = "AddressLine5";
	public static final String A_ADDRESS_LINE6 = "AddressLine6";
	public static final String A_ALTERNATE_EMAIL_ID = "AlternateEmailID";
	public static final String A_BEEPER = "Beeper";
	public static final String A_COUNTRY = "Country";
	public static final String A_DAY_FAX_NO = "DayFaxNo";
	public static final String A_DEPARTMENT = "Department";
	public static final String A_COMPANY = "Company";
	public static final String A_EVENING_PHONE = "EveningPhone";
	public static final String A_IS_COMMERCIAL_ADDRESS = "IsCommercialAddress";
	public static final String A_JOB_TITLE = "JobTitle";
	public static final String A_MIDDLE_NAME = "MiddleName";
	public static final String A_MOBILE_PHONE = "MobilePhone";
	public static final String A_OTHER_PHONE = "OtherPhone";
	public static final String A_PERSON_ID = "PersonID";
	public static final String A_PREFERRED_SHIP_ADDRESS = "PreferredShipAddress";
	public static final String A_TAX_GEO_CODE = "TaxGeoCode";
	public static final String A_TITLE = "Title";
	public static final String A_USE_COUNT = "UseCount";
	public static final String A_VERIFICATION_STATUS = "VerificationStatus";
	
	
	
	
	
	
	public static final String E_CHANGE_SHIPMENT_ADDRESS = "ChangeShipmentAddress";
	
	//Bhaskar-Change Shipping Address -Drop2 -End
	//Avinash - Drop2 - Reprint Label functionality - Start
	public static final String E_CURRENT_CONTAINER_SOURCE = "CurrentContainerSource";
	public static final String E_SHIPMENT_CONTAINER_KEY = "ShipmentContainerKey";
	public static final String CONTAINER = "Container";
	//Avinash - Drop2 - Reprint Label functionality - End
	public static final String A_TRACKING_NO = "TrackingNo";
	public static final String A_REASON_CODE = "ReasonCode";
	public static final String E_COMMON_CODE = "CommonCode";
	public static final String A_CODE_TYPE = "CodeType";
	public static final String A_QTY_TO_PRINT = "QtyToPrint";
	
	//Tulasi
	
	public static final String DOC_FRONT_PAGE = "FrontPage";
	public static final String SEL_ORG_CODE= "SellerOrganizationCode";
	public static final String SELECTED_LABEL_PRINTER = "LabelPrinter";
	public static final String INPUT_DOCUMENT = "InputDocument";
	public static final String PROSHIP_DOCUMENT = "ProShipDocument";
	public static final String A_CONTAINER_GROSS_WT = "ContainerGrossWeight";
	public static final String SERIAL_PORT = "SerialPort";
	public static final String INI_SHIPMENT_PER_TOTES="ShipmentsperTote";


	public static final String A_OVERALL_TAX = "OverallTax";
	public static final String A_TAX_EXEMPT_FLAG = "TaxExemptFlag";
	public static final String A_UNIT_HEIGHT_U_O_M = "UnitHeightUOM";
	public static final String A_SCAC_AND_SERVICE_KEY = "ScacAndServiceKey";
	public static final String A_FUNDS_AVAILABLE = "FundsAvailable";
	public static final String A_MAX_ORDER_QUANTITY = "MaxOrderQuantity";
	public static final String E_PAYMENT_METHODS = "PaymentMethods";
	public static final String A_INVOICED_EXTENDED_PRICE = "InvoicedExtendedPrice";
	public static final String A_ACTUAL_PRICING_QTY = "ActualPricingQty";
	public static final String A_SEARCH_CRITERIA_2 = "SearchCriteria2";
	public static final String A_MULTIPLE_STATUSES_EXIST = "MultipleStatusesExist";
	public static final String A_SHIP_TO_KEY = "ShipToKey";
	public static final String A_LINE_TYPE = "LineType";
	public static final String E_ITEM_ALIAS = "ItemAlias";
	public static final String A_REQUESTED_AUTH_AMOUNT = "RequestedAuthAmount";
	public static final String A_MAX_LINE_STATUS = "MaxLineStatus";
	public static final String A_REQUESTED_CHARGE_AMOUNT = "RequestedChargeAmount";
	public static final String A_OVER_SHIP_QUANTITY = "OverShipQuantity";
	public static final String A_EXTN_IS_VIRTUAL_GIFT_CARD = "ExtnIsVirtualGiftCard";
	public static final String A_TAX = "Tax";
	public static final String A_UNIT_COST = "UnitCost";
	public static final String A_CUSTOMER_ITEM = "CustomerItem";
	public static final String A_INVOICED_PRICING_QTY = "InvoicedPricingQty";
	public static final String A_IS_BILLABLE = "IsBillable";
	public static final String A_FIXED_PRICING_QTY_PER_LINE = "FixedPricingQtyPerLine";
	public static final String A_UPC_0_1 = "Upc01";
	public static final String A_COMPLIMENTARY_GIFT_BOX_QTY = "ComplimentaryGiftBoxQty";
	public static final String A_CREDIT_CARD_EXP_DATE = "CreditCardExpDate";
	public static final String A_DISCOUNT_PERCENTAGE = "DiscountPercentage";
	public static final String A_IS_FIRM_PREDEFINED_NODE = "IsFirmPredefinedNode";
	public static final String A_REQUESTED_SERIAL_NO = "RequestedSerialNo";
	public static final String A_CHARGE_NAME_KEY = "ChargeNameKey";
	public static final String A_BUNDLE_FULFILLMENT_MODE = "BundleFulfillmentMode";
	public static final String A_PRIORITY_NUMBER = "PriorityNumber";
	public static final String A_ASSUME_INFINITE_INVENTORY = "AssumeInfiniteInventory";
	public static final String A_HARMONIZED_CODE = "HarmonizedCode";
	public static final String A_ORDERED_QTY = "OrderedQty";
	public static final String A_N_M_F_C_CODE = "NMFCCode";
	public static final String A_IS_PRICE_LOCKED = "IsPriceLocked";
	public static final String A_ORDER_RELEASE_KEY = "OrderReleaseKey";
	public static final String A_CREATED_AT_NODE = "CreatedAtNode";
	public static final String A_PERSONALIZE_CODE = "PersonalizeCode";
	public static final String A_RESERVATION_POOL = "ReservationPool";
	public static final String A_REFERENCE___1 = "Reference_1";
	public static final String A_SEGMENT = "Segment";
	public static final String A_IS_MANUAL = "IsManual";
	public static final String A_PICKABLE_FLAG = "PickableFlag";
	public static final String A_HOLD_REASON_CODE = "HoldReasonCode";
	public static final String A_HAS_SERVICE_LINES = "HasServiceLines";
	public static final String A_SUPPLIER_ITEM_DESC = "SupplierItemDesc";
	public static final String A_MODIFYUSERID = "Modifyuserid";
	public static final String A_UNIT_VOLUME_U_O_M = "UnitVolumeUOM";
	public static final String A_HAS_PRODUCT_LINES = "HasProductLines";
	public static final String A_PENDING_TRANSFER_IN = "PendingTransferIn";
	public static final String A_PRIME_LINE_NO = "PrimeLineNo";
	public static final String E_ORDER = "Order";
	public static final String A_EXTENDED_DESCRIPTION = "ExtendedDescription";
	public static final String A_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	public static final String A_RESERVATION_I_D = "ReservationID";
	public static final String A_MODIFYTS = "Modifyts";
	public static final String A_NET_WEIGHT_UOM = "NetWeightUom";
	public static final String A_LINE_SEQ_NO = "LineSeqNo";
	public static final String A_ORIGINAL_TAX = "OriginalTax";
	public static final String A_SEARCH_CRITERIA_1 = "SearchCriteria1";
	public static final String A_TOTAL_CHARGED = "TotalCharged";
	public static final String A_WAVE_NO = "WaveNo";
	public static final String A_TOTAL_ADJUSTMENT_AMOUNT = "TotalAdjustmentAmount";
	public static final String A_INVOICED_CHARGE_AMOUNT = "InvoicedChargeAmount";
	public static final String A_GIFT_FLAG = "GiftFlag";
	public static final String A_BUNDLE_TOTAL = "BundleTotal";
	public static final String A_ACTUAL_QUANTITY = "ActualQuantity";
	public static final String A_IS_AIR_SHIPPING_ALLOWED = "IsAirShippingAllowed";
	public static final String A_PRICING_U_O_M_STRATEGY = "PricingUOMStrategy";
	public static final String A_LIST_PRICE = "ListPrice";
	public static final String A_INCOMPLETE_PAYMENT_TYPE = "IncompletePaymentType";
	public static final String A_UNIT_LENGTH_U_O_M = "UnitLengthUOM";
	public static final String A_RETURN_REASON = "ReturnReason";
	public static final String A_IS_SUB_ON_ORDER_ALLOWED = "IsSubOnOrderAllowed";
	public static final String A_COMPUTED_UNIT_COST = "ComputedUnitCost";
	public static final String A_ALL_ADDRESSES_VERIFIED = "AllAddressesVerified";
	public static final String A_SIZE_CODE = "SizeCode";
	public static final String A_ENTERED_BY = "EnteredBy";
	public static final String A_ORDER_TYPE = "OrderType";
	public static final String A_CHARGE_AMOUNT = "ChargeAmount";
	public static final String A_CHAINED_FROM_ORDER_LINE_KEY = "ChainedFromOrderLineKey";
	public static final String A_EXTN_SHIP_NODE_SOURCE = "ExtnShipNodeSource";
	public static final String A_SHORT_DESCRIPTION = "ShortDescription";
	public static final String A_DISPLAY_ITEM_DESCRIPTION = "DisplayItemDescription";
	public static final String A_ORDER_AVAILABLE_ON_SYSTEM = "OrderAvailableOnSystem";
	public static final String A_UNLIMITED_CHARGES = "UnlimitedCharges";
	public static final String A_MIN_ORDER_STATUS_DESC = "MinOrderStatusDesc";
	public static final String A_CREATETS = "Createts";
	public static final String A_ORIGINAL_TOTAL_AMOUNT = "OriginalTotalAmount";
	public static final String A_EXTN_VENDOR_STYLE_NO = "ExtnVendorStyleNo";
	public static final String A_OTHER_CHARGES = "OtherCharges";
	public static final String A_CUST_CUST_P_O_NO = "CustCustPONo";
	public static final String A_FIFO_NO = "FifoNo";
	public static final String A_BUNDLE_PRICING_STRATEGY = "BundlePricingStrategy";
	public static final String A_CREDIT_CARD_NAME = "CreditCardName";
	public static final String A_KIT_CODE = "KitCode";
	public static final String A_SALE_VOIDED = "SaleVoided";
	public static final String A_EXTN_NOMADIC = "ExtnNomadic";
	public static final String A_SCHED_FAILURE_REASON_CODE = "SchedFailureReasonCode";
	public static final String A_CARRIER_ACCOUNT_NO = "CarrierAccountNo";
	public static final String A_INTENTIONAL_BACKORDER = "IntentionalBackorder";
	public static final String A_SERVICE_TYPE_I_D = "ServiceTypeID";
	public static final String A_INVOLVES_SEGMENT_CHANGE = "InvolvesSegmentChange";
	public static final String A_TOTAL_REFUNDED_AMOUNT = "TotalRefundedAmount";
	public static final String E_TAX_SUMMARY = "TaxSummary";
	public static final String A_CAPACITY_QUANTITY_STRATEGY = "CapacityQuantityStrategy";
	public static final String A_REFERENCE___3 = "Reference_3";
	public static final String A_TRAN_DISCREPANCY_QTY = "TranDiscrepancyQty";
	public static final String A_HAS_DELIVERY_LINES = "HasDeliveryLines";
	public static final String A_GIFT_WRAP = "GiftWrap";
	public static final String A_PAYMENT_TYPE = "PaymentType";
	public static final String A_UNIT_PRICE = "UnitPrice";
	public static final String A_N_M_F_C_DESCRIPTION = "NMFCDescription";
	public static final String A_ORDER_NO = "OrderNo";
	public static final String A_DISPLAY_PAYMENT_REFERENCE_1 = "DisplayPaymentReference1";
	public static final String A_REFERENCE = "Reference";
	public static final String A_EXTN_WEB_I_D = "ExtnWebID";
	public static final String A_DRAFT_ORDER_FLAG = "DraftOrderFlag";
	public static final String A_EXTN_SUB_CLASS = "ExtnSubClass";
	public static final String A_EXTN_RESTRICTED_SHIP_METHOD = "ExtnRestrictedShipMethod";
	public static final String A_CREDIT_CARD_NO = "CreditCardNo";
	public static final String A_DISPLAY_CREDIT_CARD_NO = "DisplayCreditCardNo";
	public static final String A_ALIAS_VALUE = "AliasValue";
	public static final String A_SUBSTITUTE_ITEM_I_D = "SubstituteItemID";
	public static final String A_TAXABLE_FLAG = "TaxableFlag";
	public static final String A_INVOICED_QTY = "InvoicedQty";
	public static final String A_SETTLED_QUANTITY = "SettledQuantity";
	public static final String A_NET_WEIGHT = "NetWeight";
	public static final String A_ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String E_HEADER_CHARGE = "HeaderCharge";
	public static final String A_HOLD_FLAG = "HoldFlag";
	public static final String A_CONDITION_VARIABLE_1 = "ConditionVariable1";
	public static final String A_EXTN_DEFAULT_CONTAINER_TYPE = "ExtnDefaultContainerType";
	public static final String A_TAX_EXEMPTION_CERTIFICATE = "TaxExemptionCertificate";
	public static final String A_ALLOCATION_DATE = "AllocationDate";
	public static final String A_TOTAL_AUTHORIZED = "TotalAuthorized";
	public static final String A_PENDING_CHANGES_USER_I_D = "PendingChangesUserID";
	public static final String A_CUSTOMER_FIRST_NAME = "CustomerFirstName";
	public static final String A_ALIAS_NAME = "AliasName";
	public static final String A_COST_CURRENCY = "CostCurrency";
	public static final String A_EXTN_SHIPPING_SERVICE_LEVEL_SEQ = "ExtnShippingServiceLevelSeq";
	public static final String E_PAYMENT_METHOD = "PaymentMethod";
	public static final String A_TAX_SYMBOL = "TaxSymbol";
	public static final String A_IS_RETURNABLE = "IsReturnable";
	public static final String A_ORDERED_PRICING_QTY = "OrderedPricingQty";
	public static final String A_UNIT_WIDTH_U_O_M = "UnitWidthUOM";
	public static final String A_TAX_PERCENTAGE = "TaxPercentage";
	public static final String A_COUNTRY_OF_ORIGIN = "CountryOfOrigin";
	public static final String A_CUSTOMER_ITEM_DESC = "CustomerItemDesc";
	public static final String A_IS_HAZMAT = "IsHazmat";
	public static final String A_CHARGE_SEQUENCE = "ChargeSequence";
	public static final String E_LINE_TAX = "LineTax";
	public static final String A_SCHEDULE_B_CODE = "ScheduleBCode";
	public static final String A_TIMEZONE = "Timezone";
	public static final String E_HEADER_CHARGES = "HeaderCharges";
	public static final String A_IMPORT_LICENSE_NO = "ImportLicenseNo";
	public static final String A_CONDITION_VARIABLE_2 = "ConditionVariable2";
	public static final String A_DIVISION = "Division";
	public static final String A_MINIMUM_CAPACITY_QUANTITY = "MinimumCapacityQuantity";
	public static final String A_EXTN_DEPT_CLASS_SUB_CLASS = "ExtnDeptClassSubClass";
	public static final String A_KOHLS_ELIGIBLE_BULK_MARK_REQ = "KohlsEligibleBulkMarkReq";
	public static final String A_ITEM_TYPE = "ItemType";
	public static final String A_PRICING_U_O_M = "PricingUOM";
	public static final String A_INVOICE_BASED_ON_ACTUALS = "InvoiceBasedOnActuals";
	public static final String A_AVAILABLE_QTY_FOR_STOP_DELIVERY = "AvailableQtyForStopDelivery";
	public static final String A_EXTN_RED_PACK_LIST_TYPE = "ExtnRedPackListType";
	public static final String A_IS_VALID = "IsValid";
	public static final String A_TAX_PRODUCT_CODE = "TaxProductCode";
	public static final String A_BACKORDER_NOTIFICATION_QTY = "BackorderNotificationQty";
	public static final String A_SETTLED_AMOUNT = "SettledAmount";
	public static final String E_LINE_PRICE_INFO = "LinePriceInfo";
	public static final String A_REMAINING_CHARGE_AMOUNT = "RemainingChargeAmount";
	public static final String A_MAX_CHARGE_LIMIT = "MaxChargeLimit";
	public static final String A_IS_FREEZER_REQUIRED = "IsFreezerRequired";
	public static final String A_EXTN_DEPT = "ExtnDept";
	public static final String E_ORDER_LINE = "OrderLine";
	public static final String A_EXTN_BAGGABLE = "ExtnBaggable";
	public static final String A_IS_DISCOUNT = "IsDiscount";
	public static final String A_ORDER_LINE_KEY = "OrderLineKey";
	public static final String A_BACKROOM_PICKED_QUANTITY = "BackroomPickedQuantity";
	public static final String A_CREDIT_CARD_TYPE = "CreditCardType";
	public static final String A_PRICING_QUANTITY_STRATEGY = "PricingQuantityStrategy";
	public static final String E_HEADER_TAXES = "HeaderTaxes";
	public static final String A_IS_PICKABLE = "IsPickable";
	public static final String A_PARENT_OF_DEPENDENT_GROUP = "ParentOfDependentGroup";
	public static final String A_EXTN_DIRECT_SHIP_ITEM = "ExtnDirectShipItem";
	public static final String A_RETURN_WINDOW = "ReturnWindow";
	public static final String A_DISPLAY_SVC_NO = "DisplaySvcNo";
	public static final String A_HAS_DERIVED_CHILD = "HasDerivedChild";
	public static final String A_ENTRY_TYPE = "EntryType";
	public static final String A_PRICING_QUANTITY_CONV_FACTOR = "PricingQuantityConvFactor";
	public static final String A_MAX_LINE_STATUS_DESC = "MaxLineStatusDesc";
	public static final String A_PACK_LIST_TYPE = "PackListType";
	public static final String A_DEFAULT_PRODUCT_CLASS = "DefaultProductClass";
	public static final String A_AWAITING_AUTH_INTERFACE_AMOUNT = "AwaitingAuthInterfaceAmount";
	public static final String A_EXTN_STYLE = "ExtnStyle";
	public static final String A_SUB_LINE_NO = "SubLineNo";
	public static final String A_MANUFACTURER_ITEM = "ManufacturerItem";
	public static final String A_RELEASE_NO = "ReleaseNo";
	public static final String A_CHAINED_FROM_ORDER_HEADER_KEY = "ChainedFromOrderHeaderKey";
	public static final String A_AWAITING_DELIVERY_REQUEST = "AwaitingDeliveryRequest";
	public static final String A_RECEIVED_QUANTITY = "ReceivedQuantity";
	public static final String A_FILL_QUANTITY = "FillQuantity";
	public static final String E_HEADER_TAX = "HeaderTax";
	public static final String A_ITEM_SHORT_DESC = "ItemShortDesc";
	public static final String A_RESERVATION_MANDATORY = "ReservationMandatory";
	public static final String A_CUSTOMER_PO_LINE_NO = "CustomerPoLineNo";
	public static final String A_OPEN_QTY = "OpenQty";
	public static final String A_HAS_CHAINED_LINES = "HasChainedLines";
	public static final String A_SCAC_AND_SERVICE = "ScacAndService";
	public static final String A_PIPELINE_KEY = "PipelineKey";
	public static final String A_EXTN_SIZE_DESC = "ExtnSizeDesc";
	public static final String A_DELIVERY_METHOD = "DeliveryMethod";
	public static final String A_REMAINING_QTY = "RemainingQty";
	public static final String A_PURPOSE = "Purpose";
	public static final String A_REPRICING_QTY = "RepricingQty";
	public static final String E_TAX_SUMMARY_DETAIL = "TaxSummaryDetail";
	public static final String A_PRIMARY_ENTERPRISE_CODE = "PrimaryEnterpriseCode";
	public static final String A_ORDER_DATE = "OrderDate";
	public static final String A_RUN_QUANTITY = "RunQuantity";
	public static final String A_RETURN_BY_GIFT_RECIPIENT = "ReturnByGiftRecipient";
	public static final String A_MAX_ORDER_STATUS = "MaxOrderStatus";
	public static final String A_INVOICED_LINE_TOTAL = "InvoicedLineTotal";
	public static final String A_IS_SHIPPING_CHARGE = "IsShippingCharge";
	public static final String A_PAYMENT_STATUS = "PaymentStatus";
	public static final String A_TAX_NAME = "TaxName";
	public static final String A_CHARGE_ACTUAL_FREIGHT_FLAG = "ChargeActualFreightFlag";
	public static final String A_SPLIT_QTY = "SplitQty";
	public static final String A_SUBSTITUTE_ITEM_U_O_M = "SubstituteItemUOM";
	public static final String A_KOHLS_SAFETY_FACTOR_REQ = "KohlsSafetyFactorReq";
	public static final String A_S_C_A_C = "SCAC";
	public static final String A_IS_RETURN_SERVICE = "IsReturnService";
	public static final String A_SERIALIZED_FLAG = "SerializedFlag";
	public static final String E_PROMOTIONS = "Promotions";
	public static final String A_DISPLAY_CUSTOMER_ACCOUNT_NO = "DisplayCustomerAccountNo";
	public static final String A_PAYMENT_REFERENCE_1 = "PaymentReference1";
	public static final String A_GET_FUNDS_AVAILABLE_USER_EXIT_INVOKED = "GetFundsAvailableUserExitInvoked";
	public static final String A_UNIT_WIDTH = "UnitWidth";
	public static final String A_ITEM_WEIGHT_U_O_M = "ItemWeightUOM";
	public static final String A_MIN_LINE_STATUS = "MinLineStatus";
	public static final String A_NOTIFY_AFTER_SHIPMENT_FLAG = "NotifyAfterShipmentFlag";
	public static final String A_ITEM_WEIGHT = "ItemWeight";
	public static final String A_IS_SHIPPING_ALLOWED = "IsShippingAllowed";
	public static final String A_CUSTOMER_E_MAIL_I_D = "CustomerEMailID";
	public static final String A_PAYMENT_RULE_ID = "PaymentRuleId";
	public static final String A_SHORTAGE_QTY = "ShortageQty";
	public static final String A_CREDIT_W_O_RECEIPT = "CreditWOReceipt";
	public static final String A_FREIGHT_TERMS = "FreightTerms";
	public static final String A_PRIORITY_CODE = "PriorityCode";
	public static final String A_HAS_DERIVED_PARENT = "HasDerivedParent";
	public static final String A_CUSTOMER_LAST_NAME = "CustomerLastName";
	public static final String A_I_S_B_N = "ISBN";
	public static final String A_NUM_SECONDARY_SERIALS = "NumSecondarySerials";
	public static final String A_IS_PARCEL_SHIPPING_ALLOWED = "IsParcelShippingAllowed";
	public static final String A_DELIVERY_CODE = "DeliveryCode";
	public static final String A_MAX_ORDER_STATUS_DESC = "MaxOrderStatusDesc";
	public static final String A_CHAIN_TYPE = "ChainType";
	public static final String A_ORDER_NAME = "OrderName";
	public static final String E_LINE_CHARGES = "LineCharges";
	public static final String A_PRICING_QTY_CONVERSION_FACTOR = "PricingQtyConversionFactor";
	public static final String A_EXTN_VENDOR_STYLE_DESC = "ExtnVendorStyleDesc";
	public static final String A_NO_OF_AUTH_STRIKES = "NoOfAuthStrikes";
	public static final String A_EXTN_BREAKABLE = "ExtnBreakable";
	public static final String A_IS_DELIVERY_ALLOWED = "IsDeliveryAllowed";
	public static final String A_MIN_ORDER_STATUS = "MinOrderStatus";
	public static final String A_HAS_PENDING_CHANGES = "HasPendingChanges";
	public static final String A_ORDERING_QUANTITY_STRATEGY = "OrderingQuantityStrategy";
	public static final String A_CAN_ADD_SERVICE_LINES = "CanAddServiceLines";
	public static final String A_EXTN_SHIP_ALONE = "ExtnShipAlone";
	public static final String A_MANUFACTURER_NAME = "ManufacturerName";
	public static final String A_EXTN_VENDOR_NUMBER = "ExtnVendorNumber";
	public static final String A_LINE_TOTAL = "LineTotal";
	public static final String A_MIN_LINE_STATUS_DESC = "MinLineStatusDesc";
	public static final String A_FULFILLMENT_TYPE = "FulfillmentType";
	public static final String A_BILL_TO_KEY = "BillToKey";
	public static final String A_PAYMENT_REFERENCE_2 = "PaymentReference2";
	public static final String A_PRODUCT_LINE = "ProductLine";
	public static final String A_UNIT_HEIGHT = "UnitHeight";
	public static final String A_MANUFACTURER_ITEM_DESC = "ManufacturerItemDesc";
	public static final String A_IS_ELIGIBLE_FOR_SHIPPING_DISCOUNT = "IsEligibleForShippingDiscount";
	public static final String A_SHIPMENT_SUB_LINE_NO = "ShipmentSubLineNo";
	public static final String A_ALLOW_GIFT_WRAP = "AllowGiftWrap";
	public static final String A_CHARGE_NAME = "ChargeName";
	public static final String A_APPT_STATUS = "ApptStatus";
	public static final String A_INVOICED_TAX = "InvoicedTax";
	public static final String A_RETAIL_PRICE = "RetailPrice";
	public static final String A_SUPPLIER_ITEM = "SupplierItem";
	public static final String A_SHIP_TOGETHER_NO = "ShipTogetherNo";
	public static final String A_EXTN_COLOR_DESC = "ExtnColorDesc";
	public static final String A_EXTN_CLASS = "ExtnClass";
	public static final String A_COLOR_CODE = "ColorCode";
	public static final String A_ORDER_COMPLETE = "OrderComplete";
	public static final String A_CUSTOMER_PHONE_NO = "CustomerPhoneNo";
	public static final String A_MIN_ORDER_QUANTITY = "MinOrderQuantity";
	public static final String A_SEGMENT_TYPE = "SegmentType";
	public static final String A_PAYMENT_KEY = "PaymentKey";
	public static final String A_U_P_C_CODE = "UPCCode";
	public static final String A_IS_BUNDLE_PARENT = "IsBundleParent";
	public static final String A_CUSTOMER_P_O_NO = "CustomerPONo";
	public static final String A_EXTN_CAGE_ITEM = "ExtnCageItem";
	public static final String A_TAX_PAYER_ID = "TaxPayerId";
	public static final String A_SOURCE_I_P_ADDRESS = "SourceIPAddress";
	public static final String A_CHECK_REFERENCE = "CheckReference";
	public static final String A_UNIT_VOLUME = "UnitVolume";
	public static final String A_IS_PICKUP_ALLOWED = "IsPickupAllowed";
	public static final String A_N_M_F_C_CLASS = "NMFCClass";
	public static final String A_STATUS_QUANTITY = "StatusQuantity";
	public static final String A_SUSPEND_ANY_MORE_CHARGES = "SuspendAnyMoreCharges";
	public static final String A_CUSTOMER_PO_NO = "CustomerPoNo";
	public static final String A_E_C_C_N_NO = "ECCNNo";
	public static final String A_CUSTOMER_ZIP_CODE = "CustomerZipCode";
	public static final String A_CHARGE_CATEGORY = "ChargeCategory";
	public static final String A_CONTAINER_DETAILS_KEY = "ContainerDetailsKey";
	public static final String A_DRIVER_DATE = "DriverDate";
	public static final String A_IS_HISTORY = "isHistory";
	public static final String A_INVOICED_QUANTITY = "InvoicedQuantity";
	public static final String A_SERIAL_NO = "SerialNo";
	public static final String A_ALLOCATION_LEAD_TIME = "AllocationLeadTime";
	public static final String A_REQUIRES_PROD_ASSOCIATION = "RequiresProdAssociation";
	public static final String E_EXTN = "Extn";
	public static final String A_CHECK_NO = "CheckNo";
	public static final String A_IS_PRICE_MATCHED = "IsPriceMatched";
	public static final String A_MASTER_CATALOG_I_D = "MasterCatalogID";
	public static final String A_TAX_JURISDICTION = "TaxJurisdiction";
	public static final String A_UNIT_LENGTH = "UnitLength";
	public static final String A_IS_STANDALONE_SERVICE = "IsStandaloneService";
	public static final String A_PRIMARY_SUPPLIER = "PrimarySupplier";
	public static final String A_FIXED_CAPACITY_QTY_PER_LINE = "FixedCapacityQtyPerLine";
	public static final String E_LINE_TAXES = "LineTaxes";
	public static final String A_PAYMENT_REFERENCE_3 = "PaymentReference3";
	public static final String A_CAPACITY_PER_ORDERED_QTY = "CapacityPerOrderedQty";
	public static final String A_DEPARTMENT_CODE = "DepartmentCode";
	public static final String A_TERMS_CODE = "TermsCode";
	public static final String A_PERSONALIZE_FLAG = "PersonalizeFlag";
	public static final String A_EXTN_IS_PLASTIC_GIFT_CARD = "ExtnIsPlasticGiftCard";
	public static final String A_AWAITING_CHARGE_INTERFACE_AMOUNT = "AwaitingChargeInterfaceAmount";
	public static final String A_REFERENCE___2 = "Reference_2";
	public static final String A_CUSTOMER_LINE_P_O_NO = "CustomerLinePONo";
	public static final String A_ORIGINAL_ORDERED_QTY = "OriginalOrderedQty";
	public static final String E_ITEM_ALIAS_LIST = "ItemAliasList";
	public static final String A_CREATEUSERID = "Createuserid";
	public static final String A_REMAINING_TAX = "RemainingTax";

	//Start changes for SFS June Release
	
	public static final int TOTATL_NO_PAGE_VALUE = 6;
	public static final int TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_5 = 5;
	public static final int TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_4 = 4;
	public static final int TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_2 = 2;
	public static final int TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_3 = 3;
	public static final int TOTAL_LINE_SPACE_ADDED_TO_LINE_SINGLE_1 = 1;
	
	//End Changes for SFS June Release
	
}