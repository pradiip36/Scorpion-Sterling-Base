package com.kohls.common.util;

/**
 * 
 * @author Sterling. This class maintains all the constants for Kohls sterling
 *         implementation.
 */
public class KohlsConstants {

	// STANDARD STERLING API NAMES
	public static final String CANCEL_RESERVATION = "cancelReservation";

	// SAKS SERVICE NAMES
	public static final String CANCEL_RESERVATION_SERVICE = "SAKSCancelReservation";
	public static final String MERGE_INP_OUT_DOC_SERVICE = "SAKSMergeInpAndOutDocUtil";

	// XPATHS
	public static final String CANCEL_RESERVATION_XPATH = "CancelReservations/CancelReservation";
	public static final String MERGED_OUT_CANCEL_RES = "MergedOutput/CancelReservation";
	public static final String ITEM_RESERVATION = "MergedOutput/Item/ItemReservations/ItemReservation";
	// Date constants
	public static final String PO_SERVICE_ORDER_TS_FORMAT = "yyMM";
	public static final String STERLING_TS_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";// 2011-02-10T11:40:13-00:00

	// General constants
	public static final String KEY = "Key";
	public static final String VALUE = "Value";
	public static final String GET = "get";
	public static final String ORDER_LINE = "OrderLine";
	public static final String ORDER_LINE_KEY = "OrderLineKey";
	public static final int TYPE_XML_STRING = 1;
	public static final String XML_DATA = "XMLData";
	public static final String DATA_MAP = "DataMap";
	public static final int TYPE_JAVA_MAP = 0;
	public static final String DATA = "Data";

	public static final String RAISE_EVENT = "RaiseEvent";
	public static final String EVENT_ID = "EventId";
	public static final String TRANSACTION_ID = "TransactionId";
	public static final String KEY_MAP = "KeyMap";
	public static final String DATA_TYPE = "DataType";
	public static final String BLANK = "";

	// EXception codes
	public static final String ERROR = "Error";
	public static final String INPUT = "Input";
	public static final String ERROR_DESCRIPTION = "ErrorDescription";
	public static final String ERROR_CODE = "ErrorCode";
	public static final String ERROR_EXCEPTION_CODE = "ITC0248";
	public static final String ERROR_DATATYPE_MISMATCH_CODE = "SAKS000000";
	public static final String INVALID_REQUEST = "INVALID_REQUEST";
	public static final String INVALID_ATTRIBUTE = "INVALID_ATTRIBUTE";
	public static final String INVALID_ARGUMENT = "INVALID_ARGUMENT";
	public static final String INVALID_RESERVATION = "INVALID_RESERVATION";

	// Constants for cancel reservation component
	public static final String SERVICE_NAME = "ServiceName";
	public static final String ROOT_TAG = "RootTag";
	public static final String SHIP_DATE = "ShipDate";
	public static final String RESERVATION_QUANTITY = "ReservationQuantity";
	public static final String SHIP_NODE = "ShipNode";
	public static final String QUANTITY_TO_CANCEL = "QtyToBeCancelled";
	public static final String WRAP_EXCEPTION = "WrapException";
	public static final String IS_API = "IsAPI";
	public static final String MERGED_TYPE = "MergeType";
	public static final String NO = "N";
	
	

	/**
	 * For Manage User
	 */
	// User Xpath
	public static final String USER_LINE_XPATH = "Users/User";
	public static final String USER_TEMPLATE_LOGIN_ID = "TemplateLoginid";
	// service to call getUserHierarchy API
	public static final String SAKS_MANAGE_USER_GET_TEMPLATE_USER_DETAILS = "SAKSManageUserGetTemplateUserDetails";
	public static final String SYNCHRONIZE_USER_API = "synchonniseUser";

	public static final String USER_GROUP_LISTS_XPATH = "User/UserGroupLists";
	public static final String USER_GROUP_LIST_XPATH = "User/UserGroupLists/UserGroupList";
	public static final String USER_GROUP_XPATH = "UserGroupList/UserGroup";
	public static final String USER_ELEMENT = "User";
	public static final String USER_GROUP_LISTS_ELEMENT = "UserGroupLists";
	public static final String USER_GROUP_LIST_ELEMENT = "UserGroupList";
	public static final String USER_GROUP_ID_ATTRIBUTE = "UsergroupId";
	public static final String RESET_ATTRIBUTE = "Reset";
	public static final String SYSTEM_NAME_ATTRIBUTE = "SystemName";
	public static final String RESET_YES = "Y";
	public static final String RESET_NO = "N";
	public static final String USER_LOGIN_ID_ATTRIBUTE = "Loginid";

	// service name
	public static final String ASSIGN_USERGROUP_TO_USERS = "SAKSAssignUserGroupToUsers";

	// For SSO user Exit
	public static final String CUSTOMER_OVERRIDES = "customer_overrides";
	public static final String SAKS_DEFAULT_LOGIN_REDIRECT_URL = "yfs.yfs.login.redirect.url";

	// Find Store Inventory availability module constants

	public static final String NODE = "Node";
	public static final String OPTION_NO = "OptionNo";
	public static final String ASSIGNMENT = "Assignment";
	public static final String STACK = "Stack";
	public static final String ERRORS = "Errors";
	public static final Object XPATH = "XPATH";

	// out of sequence change.
	public static final String QUANTITY_TO_BE_ADJUSTED = "QuantityToBeAdjusted";
	public static final String ITEM_ID = "ItemID";
	public static final String ITEMS = "Items";
	public static final String ITEM = "Item";
	public static final String ITEM_DETAILS = "ItemDetails";
	public static final String QUANTITY = "Quantity";
	public static final String ADJUSTMENT_TYPE = "AdjustmentType";
	public static final String ADJUSTMENT = "ADJUSTMENT";
	public static final String ORGANIZATION_CODE = "OrganizationCode";
	public static final String SAKS_USA = "SaksUSA";
	public static final String PRODUCT_CLASS = "ProductClass";
	public static final String GOOD = "GOOD";
	public static final String UNIT_OF_MEASURE = "UnitOfMeasure";
	public static final String EACH = "EACH";
	public static final String SUPPLY_TYPE = "SupplyType";
	public static final String ON_HAND = "ONHAND";
	public static final String ADSJUST_INVENTORY = "adjustInventory";
	public static final String CREATE_ASYNC_REQ = "CreateAsyncRequest";
	public static final String API = "API";
	public static final String IS_SERVICE = "IsService";
	public static final String CREATE_ASYNC_REQ_API = "createAsyncRequest";
	public static final String SAKS_MERGE_INP_OUT_DOC = "SAKSMergeInputAndOutputDocUtil";
	public static final String SAKS_ITERATE_AND_CANCEL = "SAKSIterateAndCancelReservation";
	public static final String AUTHENTICATE_URL = "AutheniticateURL";
	public static final String STORE_FINDER_URL = "StoreFinderURL";
	public static final String UNAME = "Uname";
	public static final String PASSWORD = "Password";
	public static final String API_SUCCESS = "APISuccess";
	public static final String API_SUCCESS_PATH = "APISuccessPath";
	public static final String INVALID_ITEM = "sInvalidItem";
	public static final String INVALID_ITEM_PATH = "sInvalidItemPath";
	public static final String INVALID_REQ = "sInvalidRequest";
	public static final String INVALID_REQ_PATH = "sInvalidRequestPath";
	public static final String INVALID_STORE = "sInvalidStore";
	public static final String INVALID_STOTE_PATH = "sInvalidStorePath";
	public static final String NO_STORE = "sNoStore";
	public static final String NO_STOTE_PATH = "sNoStorePath";
	public static final String DISTANCE_TO_CONSIDER = "DistanceToConsider";
	public static final String ZIPCODE = "ZipCode";
	public static final String PRODUCTSOURCING = "PRODUCT_SOURCING";

	public static final String EMPTY_STRING = "";
	public static final String CODE = "Code";
	public static final String YFS10051 = "YFS10051";
	public static final String MESSAGE = "Message";
	public static final String YFS10054 = "YFS10054";
	public static final String YFS10055 = "YFS10055";
	public static final String NAME="Name";
	public static final String SERVICENAME = "Name";
	public static final String STORENAME = "Number";

	/*
	 * The following constants are used to validate the reason code, for sending the 
	 * 'InventoryShortag' information to the customer.
	 */
	 
	public static final String ORDER_LINE_XPATH = "Order/OrderLines/OrderLine";
	public static final String ITEM_XPATH = 	"OrderLine/Item";
	public static final String CANCELED_FROM_DETAILS_XPATH = "OrderLine/StatusBreakupForCanceledQty/CanceledFrom/Details";
	
	public static final String GETATP_ELEMENT 	= "GetATP";
	public static final String GETATP_SERVICE 	= "getATP";
	public static final String INVENTORY_INFORMATION 	= "InventoryInformation";
	public static final String AVAILABLE_TO_SELL = "AvailableToSell";
	public static final String AVAILABLE_TO_PROMISE_INVENTORY = "AvailableToPromiseInventory";
	public static final String INVENTORY_ITEM_XPATH  = "InventoryInformation/Item";
	public static final String AVAILABLE_TO_PROMISE_INVENTORY_XPATH  = "InventoryInformation/Item/AvailableToPromiseInventory";
	
	public static final String ACCUMULATED_SHORTAGE = "AccumulatedShortage";
	public static final String END_DATE = "EndDate";
	public static final String FROM_DATE = "FromDate";
	public static final String PROJECTED_ON_HAND_QTY="ProjectedOnhandQty";
	public static final String SUPPLY = "Supply";
	public static final String ENTERPRISE_CODE = "EnterpriseCode";

	//change for adding the errorcode for invalid zipcode
	public static final String YFS10049 = "YFS10049";
	

	// The following constants are used for(BR2) Online Inventory Reservation component
	public static final String PROMISE_LINE_XPATH = "Promise/PromiseLines/PromiseLine/ReservationParameters";
	public static final String MERGED_OUTPUT_XPATH = "MergedOutput";
	public static final String RESERVATION_PARAMETERS_XPATH = "Promise/ReservationParameters";	
	public static final String RESERVATION_ID ="ReservationID";
	public static final String EXPIRATION_DATE ="ExpirationDate";
	public static final String INVENTORY_RESERVATIONS_XPATH ="MergedOutput/InventoryReservations/InventoryReservation";	
	public static final String CANCEL_ONLINE_RESERVATION_SERVICE ="SAKSCancelOnlineInventoryReservation";
	public static final String CANCEL_ONLINE_RESERVATION_FOR_ITEM_SERVICE ="SAKSCancelOnlineInventoryReservationForItem";
	public static final String ITEM_RESERVATIONS_LIST_XPATH ="MergedOutput/Item/ItemReservations/ItemReservation";
	public static final String CANCEL_RESERVATION_INPUT_XPATH ="MergedOutput/CancelReservation";

	public static final String GET_INVENTORY_RESERVATION_LIST_API ="getInventoryReservationList";
	public static final String GET_INVENTORY_RESERVATION_XPATH = "InventoryReservations/InventoryReservation";
 
	public static final String PROMISE_LINE_RESERVATION_PARAMETERS_XPATH = "PromiseLine/ReservationParameters";
	public static final String INVENTORY_RESERVATION = "InventoryReservation" ;
	public static final String CANCEL_RESERVATIONS_ONLINE_XPATH = "CancelReservationList/CancelReservation";
	public static final String CANCEL_RESERVATION_LIST = "CancelReservationList"; 
	public static final String INVENTORY_RESERVATIONS = "InventoryReservations";
	
	//order status is 3200 == Released
	//public static final String ORDER_RELEASE_KEY_XPATH = "Order/OrderStatuses/OrderStatus[@Status='3200']";
	
	public static final String ORDER_RELEASE_KEY =  "OrderReleaseKey";
	public static final String ORDER_RELEASE_EXTN_XPATH = "OrderRelease/Extn";
	public static final String TEMPLATE_CHANGE_ORDER = "global/template/api/getOrderReleaseList.changeOrder.xml";
	public static final String RTAM_AVAILABILITY_CHANGE_ITEM_XPATH = "AvailabilityChange/Item";
	
	// defect 2209
	public static final String ITEM_INVENTORY_SUPPLY = "Item/Supplies/InventorySupply";
	public static final String ITEM_RESERVATION_XPATH = "Item/ItemReservations/ItemReservation";
	public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String FIND_INV = "findInventory";
	public static final String AVAILABILITY = "Availability";
	public static final String UNAVAILABLE_XPATH = "Promise/SuggestedOption/UnavailableLines/UnavailableLine";
	public static final String AVAILABLE = "Available";
	public static final String LINE_ID = "LineId";
	public static final String DISTRIBUTION_RULE_ID = "DistributionRuleId";
	public static final String FULL_LINE_STORES = "SaksUSFullLineStores";
	public static final String WEB = "Web";
	public static final String ASSIGNED_QTY = "AssignedQty";
	public static final String ON_ORDER_SUPPLY = "ON_ORDER.ex";
	public static final String ON_ORDER = "ON_ORDER";
	public static final String GET_INV_SUPPLY = "getInventorySupply";
	public static final String ETA = "ETA";
	public static final String PDT_AVAIL_DATE = "ProductAvailabilityDate";
	public static final String GET_RESERVATION_API = "getReservation";
	public static final String RESERVATIONS = "Reservations";
	public static final String TOTAL_NO_OF_RESV = "TotalNumberOfReservations";
	public static final String ONHAND_RESV_QTY = "OnhandReservationQty";
	public static final String ONORDER_RESV_QTY = "OnOrderReservedQty";
	
	//Order Modification DD 
	
	public static final String LINE_TYPE_DROPSHIP = "DROPSHIP";
	public static final String HOLD_TYPE_AUTH_HOLD = "AUTH_HOLD";
	public static final String AUTH_HOLD_AUTHORIZED = "Authorized";
	public static final String AUTH_HOLD_REJECTED = "Rejected"; 
	
 
	
	
	//SAKS BR2: PreOrder
	public static final String ORDER_RELEASE_ORDER_LINES_XPATH = "OrderRelease/Order/OrderLines"; 
	public static final String ORDER_RELEASE_ORDER_LINE_XPATH = "OrderRelease/Order/OrderLines/OrderLine";
	public static final String LINETYPE = "LineType";
	public static final String REGULAR_LINE_TYPE = "REGULAR";
	public static final String SAKS_UPDATE_BARCODE_SERVICE = "SAKSUpdateBarcodeToRelease";
	public static final String ORDER_RELEASE = "OrderRelease";
	public static final String EXTN = "Extn";
	public static final String ORDER = "Order";
	public static final String ORDER_RELEASE_ORDER_XPATH = "OrderRelease/Order";
	public static final String ORDER_RELEASE_ORDER_EXTN_XPATH = "OrderRelease/Order/Extn";
	public static final String PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static final String ORDER_RELEASE_SHIP_TO_XPATH = "OrderRelease/Order/PersonInfoShipTo";
	public static final String PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	public static final String ORDER_RELEASE_BILL_TO_XPATH = "OrderRelease/Order/PersonInfoBillTo";
	public static final String PAYMENT_METHODS = "PaymentMethods";
	public static final String ORDER_RELEASE_PAYMENT_METHOD_XPATH = "OrderRelease/Order/PaymentMethods/PaymentMethod";
	public static final String INSTRUCTIONS = "Instructions";
	public static final String ORDER_RELEASE_INSTRUCTION_XPATH = "OrderRelease/Order/Instructions/Instruction";
	public static final String ORDER_LINES = "OrderLines";
	public static final String ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String ORDER_HOLD_TYPES = "OrderHoldTypes";
	public static final String ORDER_HOLD_TYPE = "OrderHoldType";
	public static final String HOLD_TYPE = "HoldType";
	public static final String PRE_ORDER_AUTH_HOLD = "PRE_ORD_AUTH";
	public static final String REASON_TEXT = "ReasonText";
	public static final String PRE_ORDER_AUTH_TXT = "PREORDERAUTH Hold applied for Authorization";
	public static final String STATUS = "Status";
	public static final String HOLD_TYPE_APPLY_STATUS_CODE = "1100";
	public static final String HOLD_TYPE_REJECT_STATUS_CODE = "1200";
	public static final String HOLD_TYPE_RESOLVE_STATUS_CODE = "1300";
	public static final String MIN_LINE_STATUS = "MinLineStatus";
	public static final String PRE_AUTH_HOLD_XPATH = "OrderLine/OrderHoldTypes/OrderHoldType[@HoldType='PRE_ORD_AUTH']";
	public static final String PREORDER_LINE_TYPE = "PREORDER";
	public static final String RELEASED_STATUS_VALUE = "3200";
	public static final String ACTION_ATTRIBUTE = "Action";
	public static final String ACTION_MODIFY = "MODIFY";
	public static final String CHANGE_ORDER_API = "changeOrder";
	public static final String PUBLISH_ORDER_TO_BM = "SAKSPublishOrderDetailsToBM";
	
	
	public static final String CODE_VALUE = "CodeValue";
	public static final String DISTANCE_CAP_REQUIRED = "DISTANCE_CAP_REQUIRED";
	public static final String YES = "Y";
	public static final String CODE_LONG_DESC = "CodeLongDescription";
	public static final String COMMON_CODE = "CommonCode";
	public static final String DISTANCE_CAP_REQ = "DISTANCE_CAP_REQUIRE"; 
	public static final String MAX_CAPPING = "MAX_CAPPING"; 
	public static final String OREDER_REFERENCE = "OrderReference";
	public static final String SUPPLIES = "Supplies";
	public static final String Extn = "ExtnIneleigible";
	public static final String PROMISE_LINE = "PromiseLine";
	public static final String PROMISE_LINES = "PromiseLines";
	public static final String ITEM_TYPE = "ItemType";
	public static final String EXTN_SHIP_ALONE = "ExtnShipAlone";
	public static final String EXTN_NOMADIC = "ExtnNomadic";
	public static final String EXTN_BREAKABLE = "ExtnBreakable";
	public static final String EXTN_CAGE = "ExtnCageItem";
	public static final String UOM = "UOM";
	public static final String EXTN_SHIP_NODE_SOURCE = "ExtnShipNodeSource";
	public static final String REGULER = "Regular";
	public static final String STORE = "STORE";
	public static final String PROMISE_TYPE = "PromiseType";
	public static final String OTHERS = "Others";
	public static final String NONZN1STORE = "NonZone1Store";
	public static final String MIXED = "Mixed";
	public static final String GET_ITEM_DETAILS = "getItemDetails";
	public static final String USER_ID = "userId";
	public static final String PROG_ID = "progId";
	public static final String CODE_TYPE = "CodeType"; 
	public static final String GET_COMMON_CODE_LIST = "getCommonCodeList"; 
	public static final String YFS_ORDER_HEADER = "YFS_ORDER_HEADER";
	public static final String OMP = "OMP";
	public static final String KOHLS_BULK_EXCLUSION = "KOHLS_BULK_EXCLUSION"; 
	public static final String DEFAULT = "DEFAULT"; 
	public static final String PRIMARY_INFORMATION = "PrimaryInformation";  
	public static final String ISHAZMAT = "IsHazmat";
	public static final String KOHLS_IS_HAZMAT = "KOHLS_IS_HAZMAT";
	public static final String KOHLS_IS_LIQUID = "KOHLS_IS_LIQUID";
	public static final String IS_LIQUID = "IsLiquid";
	public static final String EXTN_CATGE_ITEM = "ExtnCageItem";
	public static final String KOHLS_EXTN_BREAKABLE = "KOHLS_EXTN_BREAKABLE";
	public static final String KOHLS_EXTN_SHIP_ALONE = "KOHLS_EXTN_SHIP_ALONE";
	public static final String KOHLS_EXTN_CAGE_ITEM = "KOHLS_EXTN_CAGE_ITEM";
	public static final String CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	public static final String IOM_UTIL_0001 = "IOM_UTIL_0001";
	public static final String GET_SURROUNDING_NODE_LIST = "GetSurroundingNodeList";
	public static final String DISTANCE_TO_CONSIDER_UOM = "DistanceToConsiderUOM";
	public static final String MILE = "MILE"; 
	public static final String NODE_TYPE = "NodeType"; 
	public static final String CITY = "City";
	public static final String STATE = "State";
	public static final String COUNTRY = "Country";
	public static final String SHIP_TO_ADDRESS = "ShipToAddress";
	public static final String GETSURROUNDING_NODE_LIST = "getSurroundingNodeList";
	public static final String GET_ORDER_DETAILS = "getOrderDetails";
	public static final String ITEM_ELIGIBILITY = "ItemEligibility";
	public static final String ITEM_KEY = "ItemKey";
	public static final String ITEM_LIST = "ItemList";
	public static final String get_ALL_ITEM_ELG_RECS = "getAllItemElgRecs";
	public static final String PROCESSED = "Processed";
	public static final String EXTN_STYLE = "ExtnStyle";
	public static final String EXTN_SUBCLASS = "ExtnSubClass";
	public static final String EXTN_CLASS = "ExtnClass";
	public static final String EXTN_DEPT = "ExtnDept";
	public static final String FLIKE = "FLIKE";
	public static final String PRIMARY_SUPPLIER_QRY_TYPE = "PrimarySupplierQryType";
	public static final String PRIMARY_SUPPLIER = "PrimarySupplier";
	public static final String START_ITEM_KEY = "StartItemKey";
	public static final String MAXIMUM_RECORDS = "MaximumRecords";
	public static final String MAXIMUM_RECORDS_5000 = "5000";
	public static final String MAXIMUM_RECORDS_10 = "1";
	public static final String GET_ITEMLIST = "getItemList";
	public static final String UPDATE_ITEMELG = "updateItemElg";
	public static final String EXTN_STORE_ELIGBL_KEY = "ExtnItemEligblKey";
	public static final String LAST_ITEM_KEY = "LastItemKey";
	public static final String TOTAL_ITEMLIST = "TotalItemList";
	public static final String TOTAL_NO_OF_RECORDS = "TotalNumberOfRecords";
	public static final String SET_ELIGIBLE_AS = "SetEligibleAs";
	public static final String STYLE = "Style";
	public static final String VENDORID = "VendorId";
	public static final String CAT_SUBCLASS = "CatSubClass";
	public static final String CAT_CLASS = "CatClass";
	public static final String CAT_DEPARTMENT = "CatDepartment"; 
	public static final String MANAGE_ITEM = "manageItem";
	public static final String EXTN_ITEM_SAFETY_FACTOR = "ExtnItemSafetyFactor";
	public static final String ONHAND_SAFTY_FACTORY_QUANTITY = "OnhandSafetyFactorQuantity";
	public static final String INVENTORY_PARAMETERS = "InventoryParameters";
	public static final String KOHLS_ITEM_SAFTY_FACTOR_LIST = "KohlsItemSafetyFactorList";
	public static final String EXTN_ITEM_SAFTY_FACTOR_LIST = "ExtnItemSafetyFactorList";
    public static final String SAFTY_FACTOR = "SafetyFactor";
    public static final String SAFTY_OPERATION = "SafetyOperation";
    public static final String SFT_FLAG = "SftFlag";
    public static final String I = "I";
    public static final String P = "P";
    public static final String EXTN_ITEM_SAFTY_FACTOR_KEY = "ExtnItemSafetyFactorKey";	
    public static final String KOHLS_ITEM_SAFETY_CHANGE = "KohlsItemSafetyFactorChange";	
    
    //Templates
    public static final String GET_ITEM_DETAILS_PROMISE_TYPE="global/template/api/getItemDetailsOutTemplate.xml";
    public static final String GET_ORDER_DETAILS_OUT_TEMPLATE="global/template/api/getOrderDetailsOutTemplate.xml";
    public static final String GET_ORDER_DETAILS_OUT_TEMPLATE_DISTANCE_UE="global/template/api/getOrderDetailsTemplateForDistanceUE.xml";
    public static final String GET_SURROUNDING_NODE_LIST_TEMPLATE="global/template/api/getSurroundingNodeListTemplate.xml";
	public static final String GET_SHIP_NODE_LIST="global/template/api/getShipNodeList.xml";
	public static final String GET_ITEM_DETAILS_SUPPLY_CORRECTIONS="global/template/api/getItemDetailsAPI.getSupplyCorrectionsUE.xml";
	public static final String GET_ITEM_LIST="global/template/api/getItemListAPI.xml";
	public static final String GET_ITEM_LIST_ITEM_ELIGIBILITY="global/template/api/getItemListAPIForItemEligibility.xml";
	//For Drop 2 Batch Print
	public static final String GET_SHIPMENT_LIST_FOR_BATCH_PRINT="global/template/api/getShipmentListForBatchPrint.xml";
	public static final String GET_ITEM_UPC_CODE_FOR_BATCH_PRINT="global/template/api/getItemDetailsUPCCodeForBatchPrint.xml";
	public static final String GET_DEPT_NAME_FOR_BATCH_PRINT="global/template/api/getDepartmentNameForBatchPrint.xml";
	
	
    //XPATHS
	public static final String XPATH_SURROUNDING_LIST="/GetSurroundingNodeList/NodeList/Node[@NodeType='STORE'] [@ShipNode='";
	public static final String XPATH_SURROUNDING_LIST_SQUARE_BRACKET="']";
	public static final String XPATH_EXTN_ITEM_INELIGIBILITY="/ExtnItemInEligibilityList/ExtnItemInEligibility[@ItemID='";
	public static final String XPATH_EXTN_ITEM_INELIGIBILITY_SHIP_NODE="[@ShipNode='";
	public static final String XPATH_EXTN_ITEM_INELIGIBILITY_UOM="[@UnitOfMeasure='" ;
	public static final String XPATH_EXTN_ITEM_INELIGIBILITY_CLASS_ID="/ExtnItemInEligibilityList/ExtnItemInEligibility[@ClassID='";
	public static final String XPATH_EXTN_ITEM_INELIGIBILITY_STYLE_ID="/ExtnItemInEligibilityList/ExtnItemInEligibility[@Style='";
	
	public static final String EXTN_ELIGIBLE = "ItemEligibility";   
    public static final String APPLY_NEW_ITEM = "ApplyNewItem";
    public static final String COMPLEX_QUERY = "ComplexQuery";
    public static final String OPERATOR = "Operator";
    public static final String QRY_TYPE = "QryType";
    public static final String EXP = "Exp";
    public static final String AND = "AND";
    public static final String AND1 = "And";
    public static final String OR = "Or";
    public static final String KOHL_SAFETY_FACTOR_REQ = "KohlsSafetyFactorReq";
    public static final String DEFAULT_SAFETY_FACTOR = "DefaultSafetyFactor";
    public static final String KOHLS_GET_ITEM_SAFETY_FACTOR_CHANGE = "KohlsItemSafetyFactorChange";
    public static final String EQ = "EQ";
    public static final String KOHLS_GET_ITEM_ELIGIBILITY = "KohlsgetItemEligiblity";
    public static final String 	EXTN_STORE_PEAK_PRD = "EXTN_STORE_PEAK_PRD";
	public static final String 	GET_REGIONLIST = "getRegionList";
	public static final String 	PERSON_INFO = "PersonInfo";
	public static final String 	KOHLS_STORES = "KohlsStores";
	public static final String 	REGIONS_SCHEMA_NAME = "RegionSchemaName";
	public static final String  SCHEMA_NAME = "SchemaName";
	public static final String  EXTN_REGN_SCHEMA = "EXTN_REGN_SCHEMA";
	public static final String 	REGION = "Region";
	public static final String 	REGION_NAME = "RegionName";
	public static final String 	PARENT_REGION = "Regions/RegionSchema/Region/ParentRegion";
	public static final String 	REGION_SCHEMA = "RegionSchema";
	public static final String 	NODE_LIST = "NodeList";
	public static final String 	ORDER_PERSON_INFO_SHIP_TO_XPATH = "Order/PersonInfoShipTo";
	public static final String 	SHIP_FROM_NODE = "ShipFromNode";
	public static final String 	ZONE1_STORE = "Zone1Store";
	public static final String 	COST = "Cost";
	public static final String 	RDC = "RDC";
	public static final String 	EFC = "DC";
	public static final String 	EXTN_NON_PK_STORE = "EXTN_NON_PK_STORE";
	public static final String 	EXTN_NON_PK_OTHER = "EXTN_NON_PK_OTHER";
	public static final String 	EXTN_PK_EFC = "EXTN_PK_eFC";
	public static final String 	EXTN_PK_RDC = "EXTN_PK_RDC";
	public static final String  EXTN_PK_NONZN1_STORE = "EXTN_PK_NONZN1_STORE";
	public static final String 	EXTN_PK_STORE = "EXTN_PK_STORE";
	public static final String 	COST_200000 = "8000";
	public static final String 	ROUTABLE =  "RoutableCarriers/RoutableCarrier/Routable";
	public static final String 	CARRIER_OPTION = "RoutableCarriers/RoutableCarrier/CarrierOptions/CarrierOption";
	public static final String 	MIXED_ORDER = "MIXED_ORDER";
	public static final String 	FULFILLMENT_TYPE = "FulfillmentType";
	public static final String 	KOHLS_ITEM_IN_ELIGIBILITY = "KohlsGetItemIneligibilityList";
	public static final String 	EXTN_ITEM_IN_ELIGIBILITY = "ExtnItemInEligibility";
	public static final String  KOHLS_ADD_ITEM_INELIGIBILITYU="KohlsAddItemIneligibility";
	public static final String KOHLS_DELETE_ITEM_INELIGIBILITY_LIST="KohlsDeleteItemIneligibilityList";
	public static final String ITEMS_ITEM = "Items/Item";
	public static final String 	COMMON_CODE_LIST_COMMONCODE = "CommonCodeList/CommonCode";
	public static final String 	ITEM_EXTN = "Item/Extn";
	public static final String 	TO_DATE = "ToDate";
	public static final String 	GET_SHIP_NODE_LIST_API = "getShipNodeList";
	public static final String 	START_DATE = "StartDate";
	public static final String KOHLS_ELIGIBLE_BULK_MARK_REQ = "KohlsEligibleBulkMarkReq";
	public static final String KOHLS_GET_ITEM_SAFETY_FACTOR = "KohlsgetItemSafetyFactor";
	public static final String SCAC="SCAC";
	public static final String FEDX="FEDX";
	public static final String SCAC_AND_SERVICE="ScacAndService";
	public static final String SCAC_AND_SERVICE_VALUE="2 Day";
	public static final String KOHLS_SAFETY_FACTOR_REQ="KohlsSafetyFactorReq";
	public static final String DATE_FORMAT="yyyy-MM-dd";
	public static final String EMAIL_NOTIFY_SAFETY="EmailNotifySafety";
	public static final String EMAIL_NOTIFY_ELIGIBILITY="EmailNotifyEligibility";
	public static final String EMAIL_NOTIFICATION="EmailNotification";
	public static final String TO_MAILID="TO_MAILID";
	public static final String CC_MAILID="CC_MAILID";
	public static final String BCC_ADDRESS_MAILID="BCC_ADDRESS_MAILID";
	public static final String ITEM_ELIGIBILITY_AGENT="ITEM_ELIGIBILITY_AGENT";
	public static final String SAFETY_FACTOR_AGENT="SAFETY_FACTOR_AGENT";
	public static final String TO_ADDRESS="ToAddress";
	public static final String CC_ADDRESS="CcAddress";
	public static final String BCC_ADDRESS="BccAddress";
	public static final String ITEMELIGIBILITY_AGENT="ItemEligibilityAgent";
	public static final String SAFETYFACTOR_AGENT="SafetyFactorAgent";
	
	
	
	//for OnBoarding 
	public static final String KEY_SUPRESS_EXCEPTION="SuppressException";
	public static final String ELEMENT_MULTI_APIS="MultiApis";
	
	
	
	//Pawan - Start Drop2
	public static final String TEMPLATE_SHIPMENTS_PICK_LIST_PRINT="global/template/api/getShipmentList_PickSlipPrint.xml";
	public static final String API_GET_SHIPMENT_LIST="getShipmentList";
	public static final String XPATH_EXTN_DEPT="OrderLine/ItemDetails/Extn/@ExtnDept";
	public static final String MULTI_DEPT_SHIPMENT="Multi-Department";
	//Pawan - End - Drop2
	//Bhaskar - Start - Drop2
	public static final String TEMPLATE_SHIPMENTS_PICK_LIST_PRINT_GET_CATEGORY_LIST="global/template/api/categoryList_PickSlip.xml";
	public static final String GET_CATEGORY_LIST = "getCategoryList";
	public static final String EXTN_SHIPMENT_DEPT_DESC = "ExtnShipmentDepartmentDesc";
	public static final String CATEGORY = "Category";
	public static final String DESCRIPTION = "Description";
	public static final String Extn_SHIPMENT_DEPT = "ExtnShipmentDepartment";
	public static final String CATEGORY_ID = "CategoryID"; 
	public static final String PARENT_CATEGORY_KEY = "ParentCategoryKey";
	
	
	//Bhaskar - End - Drop2
	 
	public static final String INVOKED_FROM = "InvokedFrom";
	public static final String GET_DISTANCE = "GetDistance";
	public static final String DISTANCE = "Distance";
	public static final String SCHEDULE_ORDER = "scheduleOrder";
	public static final String SUPPLY_CORRECTIONS_UE = "INVSuplyCorrectionUE";
	public static final String PERSON_INFO_SHIP_FROM = "PersonInfoShipFrom";
	public static final String EXTN_ACTUAL_DISTANCE = "ExtnActualDistance";
	public static final String EXTN_ADDED_COST = "ExtnAddedCost";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	public static final String ZIP_CODE_LOCATION = "ZipCodeLocation";
	public static final String SCHEDULE_ORDER_AGENT = "SCHEDULE";
	public static final String AGENTS = "AGENTS";
	
	//ManagerOverrides - Drop 12 UI 2.0
	
			public static final String ORG_CODE_VAL= "KOHLS.COM";
			public static final String DOMAIN_VAL= "ORDER";
			public static final String VALIDATION_ID_VAL= "KOHLS_CUST_VERIFICATION";
			public static final String CHANNEL_VAL = "null";
			public static final String DOCUMENT_TYPE_VAL = "0001";
			public static final String RULE_ID_VAL = "NameValidation";
			public static final String NO_OF_VIOLATIONS_VAL = "0";
			public static final String RET_CODE_Y = "Y";
			public static final String RET_CODE_N = "N";
			public static final String APPROVED_Y = "Y";
			public static final String CHECK_OVERRIDE_RULE = "checkOverriddenRules";
			public static final String MGR_LOGIN = "login";
			public static final String RECORD_APPROVALS = "recordApprovals";
			public static final String CHECK_OVERRIDE_RULE_TEMPLATE = "<Validation CanApprovalBePostponed=\"\" Domain=\"\" ValidationID=\"\">"+
					" <TransactionViolationList AllowTransaction=\"\" NumberOfRulesEvaluated=\"\" NumberOfViolations=\"\">" +
					" <TransactionViolation ApprovalRuleID=\"\" DocumentType=\"\" IsApprovalRequired=\"\" TransactionInfoID=\"\"/>" +
					" </TransactionViolationList> </Validation>";
	
	//BOPUS Mashup Ids ~ Adam Dunmars
	public static final String MASHUP_GET_USER_LIST = "getUserList";
	public static final String MASHUP_GET_MLS_GIV_DETAILS = "extn_getMLSGIVDetails";
	public static final String MASHUP_SET_USER_NAME = "setUsername";
}

