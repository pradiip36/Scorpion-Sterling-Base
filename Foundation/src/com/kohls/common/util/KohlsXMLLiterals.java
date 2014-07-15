package com.kohls.common.util;

/**
 * 
 * Kohls XMLLiterals File
 *
 */
public class KohlsXMLLiterals {
	
	
	//XML Elements
	
	//PLU ELEMENTS AND ATTRIBUTES
	public static String E_ITEM_VALIDATION	   = "ItemValidation";	
	public static String A_CALL_BACK_INDICATOR = "CallbackIndicator";
	
	public static String E_HOLD_REASON_CODE	   = "HoldReasonCode";
    public static String E_ORDER               = "Order";
    public static String E_SHIPMENTS           = "Shipments";
    public static String E_SHIPMENT            = "Shipment";
    public static String E_DIBDOCKAPPTENTITIES = "DIBDockAppEntities";
    public static String E_STATUS              = "Status";
    public static String E_RECURRENCEAPPOINTMENT = "RecurrenceAppointment";
    public static String E_UPDATEAPPOINTMENT   = "UpdateAppointment";
    public static String E_CREATEAPPOINTMENT   = "CreateAppointment";
    public static String E_RECURRENCE		   = "Recurrence";
    public static String E_DOCKAPPOINTMENT	   = "DockAppointment";
    public static String E_EXTN	   			   = "Extn";
    public static String E_ORDERLIST   		   = "OrderList";
    public static String E_ASNLIST   		   = "ASNList";
    public static String E_ASN   			   = "ASN";
    public static String E_DIBDOCKAPPTENTLIST  = "DIBDockAppEntitiesList";
    public static String E_CAPACITYCONSUMPTION  = "CapacityConsumption";
    public static String E_CREATEAPPOINTMENTS   = "CreateAppointments"; 
    public static String E_CANCELAPPOINTMENTS   = "CancelAppointments";
    public static String E_DIBDOCKRECURAPPMODIFY= "DIBDockRecurAppModify";
    public static String E_MODIFYAPPOINTMENTS	= "ModifyAppointments";
    public static String E_DATE					= "Date";
    public static String E_DIBDOCKRECURAPPCANCEL= "DIBDockRecurAppCancel";
    public static String E_VENDOR 				= "Vendor";
    public static String E_SHIPMENT_LINE		= "ShipmentLine";
    public static String E_ORDER_LINE			= "OrderLine";
    public static String E_ORDER_LINE_DETAIL			= "OrderLineDetail";    
    public static String E_DIB_ORDER_SUMMARY	= "DIBOrderSummary";
    public static String E_ITEM					= "Item";
    public static String E_DIB_SHIPMENT_LINE_SUMMARY = "DIBShipmentLineSummary";
    public static String E_SHIPMENT_STATUS_AUDIT	 = "ShipmentStatusAudit";
    public static String E_RECEIPT_LINE_LIST	 = "ReceiptLineList";
    public static String E_ORDER_STATUSES		 = "OrderStatuses";
    public static String E_ORDER_STATUS			 = "OrderStatus";
    public static String E_BACK_ORDERED_FROM	 = "BackOrderedFrom";
    public static String E_DEPENDENCY	 = "Dependency";
    public static String E_ORDER_RELEASE_DETAIL	 = "OrderReleaseDetail";
    public static String E_REGISTER_PROCESS_COMPLETION_INPUT	 = "RegisterProcessCompletionInput";
    public static String E_CURRENT_TASK	 = "CurrentTask";
    public static String E_PERSON_INFO_SHIP_TO	 = "PersonInfoShipTo";
    public static String E_ORDER_RELEASE = 	"OrderRelease";
    public static String E_TO_ADDRESS = 	"ToAddress";
    public static String E_NOTES = 	"Notes";
    public static String E_NOTE = 	"Note";
    public static String E_CANCEL_LINES = 	"CancelLines";
    public static String E_CANCEL_LINE = 	"CancelLine";
    public static String E_SHIPMENT_ADVICES = 	"ShipmentAdvices";
    public static String E_SHIPMENT_ADVICE = 	"ShipmentAdvice";
    public static String E_ORDER_HOLD_TYPES = 	"OrderHoldTypes";
    public static String E_ORDER_HOLD_TYPE = 	"OrderHoldType";
    public static String E_INBOX = 	"Inbox";
    public static String E_ORDER_INVOICE_LIST = 	"OrderInvoiceList";
    public static String E_ORDER_INVOICE = 	"OrderInvoice";
    public static final String E_TOTAL_LINE_LIST = "TotalLineList";
    
    public static String A_ORD_QUANTITY			  = "OrderedQuantity";
	public static String A_DRAFT_ORDER_FLAG		  = "DraftOrderFlag";
	public static String A_EXTN_WRAP_TOGETHER_CODE		  = "ExtnWrapTogetherGroupCode";
	public static String A_TAX_GEO_CODE		  = "TaxGeoCode";
	public static String A_RESERVATION_ID		  = "ReservationID";
	public static String A_DISP_PAYMENT_REFERENCE_TYPE		  = "DisplayPaymentReference1";
	public static String A_PAYMENT_REFERENCE_ONE		  = "PaymentReference1";
    public static String A_HOLD_TYPE = 	"HoldType";
    public static String A_TOTAL_ORDER_INVOICE_LIST = 	"TotalOrderInvoiceList";
    
    
    // XML Element Process Inventory messages
    public static String E_RECEIPT				= "Receipt";
    public static String E_ITEMS				= "Items";
    public static String E_INV_SUPPLY           = "InventorySupply";
    public static String E_INVENTORY          = "Inventory";
    public static String E_AUDIT          = "Audit";
    public static String E_RECEIVING_DISCREPANCY= "ReceivingDiscrepancy";
    public static String E_RECEIPT_LINE= "ReceiptLine";
    public static String E_RECEIPT_LINES= "ReceiptLines";
    public static String E_LOAD= "Load";
    public static String E_ADJUST_LOCATION_INVENTORY= "AdjustLocationInventory";
    public static String E_SOURCE="Source";
    
    
    public static String E_INVENTORY_SNAP_SHOT="InventorySnapShot";
    
    public static String XP_RECEIPT_DISCREPANCY= "/ReceiptDiscrepancies/ReceiptDiscrepancy[@DiscrType='SHORT']";
    
    
    // XML Element - SCV Alerts
    public static String E_LOAD_STOP= "LoadStop";
    public static String E_LOAD_SHIPMENT= "LoadShipment";
    
    //XML Element - DD110
    public static String E_COMPLEX_QUERY= "ComplexQuery";
    public static String E_AND = "And";
    public static String E_OR= "Or";
    public static String E_EXP= "Exp";
    public static String E_DIBDSPYDTL="DIBDspyDtl";
    
    
//  XML Attributes DD110
    public static String A_OPERATOR="Operator";
    public static String A_DISCREPANCY_HDR= "DIBDspyHdr";
	public static String A_DISCREPANCY_DTL= "DIBDspyDtl";
	public static String A_DISCREPANCY_NO= "DiscrepancyNo";
	public static String A_DISCREPANCY_GRP= "DiscrepancyGroup";
	public static String A_DISCREPANCY_TYPE= "DiscrepancyType";
	public static String A_ItemID= "ItemID";
	public static String A_SHIP_QTY= "ShippedQty";
	public static String A_REC_QTY= "RecvdQty";
    
    //XML Attributes 
    public static String A_ORGANIZATION_CODE   = "OrganizationCode";
    public static String A_DOCKAPPOINTMENT_KEY = "DockAppointmentKey";
    public static String A_ORDER_HEADER_KEY    = "OrderHeaderKey";
    public static String A_ORDER_HEADER_KEY_FK    = "OrderHeaderKeyFk";
    public static String A_SHIPMENT_KEY        = "ShipmentKey";
    public static String A_STATUS_QRY_TYPE     = "StatusQryType";
    public static String A_TO_STATUS           = "ToStatus";
    public static String A_FROM_STATUS         = "FromStatus";
    public static String A_SHIPMENT_NO         = "ShipmentNo";
    public static String A_STATUS              = "Status";
    public static String A_DESCRIPTION         = "Description";
    public static String A_SHORT_DESCRIPTION   = "ShortDescription";
    public static String A_DOCKRECURRAPPTKEY   = "DockRecurApptKey";
    public static String A_BOLNO   			   = "BolNo";
    public static String A_CARRIER 			   = "Carrier";
    public static String A_NOTES   			   = "Notes";
    public static String A_PRONO   			   = "ProNo";
    public static String A_TRAILERNO		   = "TrailerNo";
    public static String A_ENDDATE   		   = "ExtnEndDate";
    public static String A_END_DATE   		   = "EndDate";
    public static String A_RECURRENCETYPE	   = "ExtnRecurrenceType";
    public static String A_DAYOFWEEK		   = "ExtnDaysOfWeek";
    public static String A_EXTNRECURAPPTREF	   = "ExtnRecurApptRef";
    public static String A_MINJOBS	   		   = "MinJobs";
    public static String A_APPOINTMENTNO	   = "AppointmentNo";
    public static String A_RECURRINGFLAGEXISTS = "RecurringFlagExists";
    public static String A_APPTREFKEYEXISTS	   = "ApptRefKeyExists";
    public static String A_ACTION	   		   = "Action";
    public static String A_ID	   		   	   = "Id";
    public static String A_ORGANIZATIONNAME	   = "OrganizationName";
    public static String A_APPOINTMENTDATE	   = "AppointmentDate";
    public static String A_STARTTIME	   	   = "StartTime";
    public static String A_ORDERNO		   	   = "OrderNo";
    public static String A_ASNNO		   	   = "ASNNo";
    public static String A_VENDORID		   	   = "VendorID";
    public static String A_EXTN_ARRIVAL_DATE   = "ExtnArrivalDate";
    public static String A_EXTN_APP_ARRIVAL_DATE   = "ExtnAppArrivalDate";
	public static String A_VENDORNAME	   	   = "VendorName";
	public static String A_NODE			   	   = "Node";
	public static String A_CARRIERNAME		   = "CarrierName";
	public static String A_EXTNCARRIERNAME	   = "ExtnCarrierName";
	public static String A_MAXJOBS			   = "MAXJOBS";
	public static String A_PRIMARYVENDORNAME   = "ExtnPrimaryVendorName";
	public static String A_STARTDATE   		   = "StartDate";
	public static String A_CONSUMPTIONDATE	   = "ConsumptionDate";
	public static String A_DATE				   = "Date";
	public static String A_ENDTIME			   = "EndTime";
	public static String A_LOCATIONID		   = "LocationId";
	public static String A_LOCATION_ID		   = "LocationID";
	public static String A_EXTNCONTACTNAME	   = "ExtnContactName";
	public static String A_EXTNCONTACTNUMBER   = "ExtnContactNumber";
    public static String A_OPERATION		   = "Operation";
    public static String A_EXTN_STATUS		   = "ExtnStatus";
    public static String A_CONTACT_NAME		   = "ContactName";
    public static String A_CONTACT_NUMBER	   = "ContactNumber";
    public static String A_CONTACT_NO		   = "ContactNo";
    public static String A_REASON_CODE		   = "ReasonCode";
    public static String A_REASON_TEXT		   = "ReasonText";
    public static String A_IS_EXCEPTION		   = "IsException";
    public static String A_MIN_ORDER_STATUS		   = "MinOrderStatus";
    public static String A_MIN_ORDER_RELEASE_STATUS		   = "MinOrderReleaseStatus";
    public static String A_MAX_ORDER_STATUS		   = "MaxOrderStatus";
    public static String A_MIN_ORDER_LINE_STATUS	 = "MinLineStatus";
    public static String A_MAX_ORDER_LINE_STATUS	 = "MaxLineStatus";
    public static String A_CHARGE_CATEGORY		 = "ChargeCategory";
    public static String A_CHARGE_NAME		 = "ChargeName";
    public static String A_CHARGE_NAME_KEY		 = "ChargeNameKey";
    public static String A_CHARGE_AMOUNT		 = "ChargeAmount";
    public static String A_INVOICE_CHARGE_PER_LINE		 = "InvoicedChargePerLine";
    public static String A_CHARGE_PER_LINE		 = "ChargePerLine";
    public static String A_CHARGE_PER_UNIT		 = "ChargePerUnit";
    public static String A_IS_BILLABLE		 = "IsBillable";
    public static String A_IS_DISCOUNT		 = "IsDiscount";
    public static String A_IS_SHIPPING_CHARGE		 = "IsShippingCharge";
    public static String A_ORIGINAL_CHARGE_PERLINE		 = "OriginalChargePerLine";
    public static String A_ORIGINAL_CHARGE_PERUNIT		 = "OriginalChargePerUnit";
    public static String A_CHAINED_FROM_ORDER_HEADER_KEY		 = "ChainedFromOrderHeaderKey";
    public static String A_CHAINED_FROM_ORDER_LINE_KEY		 = "ChainedFromOrderLineKey";
    public static String A_SHIP_ADVICE_NO 		 = "ShipAdviceNo";
    public static String A_CHANGE_IN_QUANTITY 		 = "ChangeInQuantity";
    public static String A_CONTAINER_DETAIL 		 = "ContainerDetail";
    public static String A_CONTAINER_SCM 		 = "ContainerScm";
    public static String A_CANCEL_EXISTS 		 = "CancelExists";
    public static String A_SALES_ORDER_NO		 = "SalesOrderNo";
    public static String A_BUYER_ORGANIZATION_CODE   = "BuyerOrganizationCode";
    public static String A_PURCHASE_ORDER_NO		 = "PurchaseOrderNo";
    public static String A_SA_NO		 = "SANo";
    public static String A_CANCELED_FROM		 = "CanceledFrom";
    public static String A_DETAILS		 = "Details";
    
    //RCP attribs
    public static String A_EXTN_ANALYST		   = "ExtnAnalyst";
    public static String A_USER_NAME = "UserName";
    public static String A_SELLER_ORGANIZATION_CODE   = "SellerOrganizationCode";
    public static String A_VENDOR_CLASSIFICATION_CODE = "VendorClassificationCode";
    public static String A_ORDER_LINE_KEY			  = "OrderLineKey";
    public static String A_REQ_SHIP_DATE			  = "ReqShipDate";
    public static String A_REQ_DELIVERY_DATE		  = "ReqDeliveryDate";
    public static String A_RECEIVING_NODE 			  = "ReceivingNode";
    public static String A_ACTUAL_DELIVERY_DATE		  = "ActualDeliveryDate";
    public static String A_QUANTITY					  = "Quantity";
    public static String A_ORDERED_QUANTITY			  = "OrderedQty";
    public static String A_ORIGINAL_ORDERED_QUANTITY			  = "OriginalOrderedQty";
    public static String A_SHIPPED_QUANTITY			  = "ShippedQty";
    public static String A_PO_QTY			  		  = "POQty";
    public static String A_SCORING_QTY			  	  = "ScoringQty";
    public static String A_SUMMARY_SCORING_QTY		  = "SummaryScoringQty";
    public static String A_ASN_NO			  		  = "ASNNo";
    public static String A_ORDER_SUMMARY_KEY		  = "OrderSummaryKey";
    public static String A_ARRIVAL_DATE				  = "ArrivalDate";
    public static String A_ORDER_FILL_RATE			  = "OrderFillRate";
    public static String A_REQ_FROM_DATE			  = "ReqFromDate";
    public static String A_REQ_TO_DATE			  	  = "ReqToDate";
    public static String A_RSC			  	  		  = "RSC";
    public static String A_PO_NO			  	  	  = "PONo";
    public static String A_SHIPMENT_LINE_KEY  	  	  = "ShipmentLineKey";
    public static String A_ON_TIME_SCORE	  	  	  = "OnTimeScore";
    public static String A_VENDOR_TYPE				  = "VendorType";
    public static String A_CARRIER_SERVICE_CODE		  = "CarrierServiceCode";
    public static String A_SCAC		  				  = "SCAC";
    public static String A_CARRIER_ID 				  = "CarrierId";
    public static String A_REQ_DATE					  = "ReqDate";
    public static String A_COMPLIANCE_DATE			  = "ComplianceDate";
    public static String A_SKU						  = "SKU";
    public static String A_ITEM_ID					  = "ItemID";
    public static String A_SHIPMENT_LINE_SUMMARY_KEY  = "ShipmentLineSummaryKey";
    public static String A_ACTUAL_SHIPMENT_DATE		  = "ActualShipmentDate";
    public static String A_EXTN_VENDOR_COMPLIANCE_DATE = "VendorComplianceDate"; 
    public static String A_RECEIVED_QUANTITY		  = "ReceivedQuantity";
    public static String A_ASN_QTY					  = "ASNQty";
    public static String A_RECEIPT_QTY 				  = "ReceiptQty";
    public static String A_LINE_SCORE				  = "LineScore";
    public static String A_IS_SHIPMENT_FINALISED	  = "IsShipmentFinalised";     
    public static String A_RECEIPT_DATE				  = "ReceiptDate";
    public static String A_NEW_STATUS				  = "NewStatus";
    public static String A_NEW_STATUS_DATE			  = "NewStatusDate";
    public static String A_HAS_SHIPPING_COMPLIANCE 	  = "HasShippingCompliance";
    public static String A_HAS_MARKING_COMPLIANCE  	  = "HasMarkingCompliance";
    public static String A_FINAL_DATE				  = "FinalDate";
    public static String A_CHARGE_BACK				  = "Chargeback";
    public static String A_FLP				  		  = "FLP";
    public static String A_MERCHANDISE_MGR			  = "MerchandiseMgr";
    public static String A_NODE_TYPE                  = "NodeType";
    public static String A_UOM						  = "UnitOfMeasure";
    public static String A_RECEIPT_LINE_KEY			  = "ReceiptLineKey";
    public static String A_SHIP_DATE			      = "ShipDate";
    public static String A_WAIT_FOR_UPDATE	          = "WaitForUpdate";
    
    
    // XML Attributes for Process Inventory 
    public static String A_SHIPNODE       	   = "ShipNode";
    public static String A_SHIPNODE_KEY       	   = "ShipnodeKey";
    public static String E_SHIPNODE_PERSON_INFO       	   = "ShipNodePersonInfo";
    public static String A_SUPPLYTYPE          = "SupplyType";
    public static String A_ETA           	   = "ETA";
    public static String A_DOCUMENTTYPE        = "DocumentType";
    public static String A_SHIPMENT_NUMBER 		= "ShipmentNo";
    public static String A_ENTERPRISE_CODE 		= "EnterpriseCode";
    public static String A_RECEIPTHEADERKEY 	= "ReceiptHeaderKey";
    public static String A_TYPE 				= "Type";
    public static String A_SELLERORGCODE 		= "SellerOrganizationCode";
    public static String A_ORDER_NUMBER         = "OrderNo";
    public static String A_BASEDROPSTATUS	   = "BaseDropStatus";
    public static String A_TRANSACTIONID       = "TransactionId";
    public static String A_RECIEVING_NODE       = "ReceivingNode";
    public static String A_TR_NUMBER				= "TRNumber"; 
    public static String A_DISCRQTY		       = "DiscrQty";
    public static String A_RECEIVING_DOCK	= "ReceivingDock";
    public static String A_LOAD_NO	= "LoadNo";
    public static String A_LOAD_KEY	= "LoadKey";
    public static String A_SHIPMENT_TYPE = "ShipmentType";
    public static String E_KOHLS_INV_SYNC_TIME_STAMP = "KOHLSInvSyncTimeStamp";
    public static String A_INV_SYNC_TIME_KEY = "InvSyncTimeKey";
    public static String A_REFERENCE_1 = "Reference_1";
    public static String A_REFERENCE_3 = "Reference_3";
    public static String A_TRANSACTION_NUMBER = "TransactionNumber";
	// Start - Added for PMR 64507,379,000 (PIX 500)
	public static final String A_REFERENCE_2  =  "Reference_2";
	public static final String A_REFERENCE_4  =  "Reference_4";
	public static final String A_AVAILABILITY = "Availability";
	// End - Added for PMR 64507,379,000 (PIX 500)
	
    // XML Attributes - SCV Alerts
    public static String A_STOP_NODE= "StopNode";
    public static String A_STOP_TYPE= "StopType";
    public static String A_STOP_SEQ_NO= "StopSequenceNo";
    public static String A_ACTUAL_ARRIVAL_DATE= "ActualArrivalDate"; 

    public static String XP_DOCK_APP_ASNS = "/DockAppointment/Extn/DIBDockAppEntitiesList/DIBDockAppEntities[@ShipmentKey != '']";
    public static String XP_DOCK_APP_POs = "/DockAppointment/Extn/DIBDockAppEntitiesList/DIBDockAppEntities[@OrderNo!='']";
	public static String XP_SHIPMENTS = "/Shipments/Shipment";
	public static String XP_ISRECURRING = "/DockAppointment/Extn/@ExtnIsRecurring";
	public static String XP_EXTNSTATUS = "/DockAppointment/Extn/@ExtnStatus";
	public static String XP_EXTN_CARRIERNAME = "/DockAppointment/Extn/@ExtnCarrierName";
	public static String XP_CAPACITY_CONSUM_DATE = "/DockAppointment/CapacityConsumption/@ConsumptionDate";
	public static String XP_CAPACITY_CONSUM_STARTIME = "/DockAppointment/CapacityConsumption/@StartTime";
	public static String XP_DOCKAPPTKEY = "/DockAppointment/@DockAppointmentKey";
	public static String XP_ORGANIZATION_NAME = "/OrganizationList/Organization/@OrganizationName";
	public static String XP_CALENDAR_TYPE = "/Calendar/Dates/Date[(@Type='1' or @Type='2')]";
	public static String XP_ORDER_SUMMARY = "/DIBOrderSummaryList/DIBOrderSummary";
	public static String XP_SHIPMENT_LINE_SUMMARY = "/DIBShipmentLineSummaryList/DIBShipmentLineSummary";
	public static String XP_ORDER_LINES = "/OrderList/Order/OrderLines/OrderLine";
	public static String XP_SHIPPING_DISCREPANCY = "/Shipments/Shipment/Extn/DIBDspyHdrList/DIBDspyHdr/DIBDspyDtlList/DIBDspyDtl[@DiscrepancyGroup='SHIPMENT']";
	public static String XP_MARKING_DISCREPANCY = "/Shipments/Shipment/Extn/DIBDspyHdrList/DIBDspyHdr/DIBDspyDtlList/DIBDspyDtl[@DiscrepancyGroup='ITEM']";
	public static String XP_SHIPTRANSPORT_DISCREPANCY = "/Shipments/Shipment/Extn/DIBDspyHdrList/DIBDspyHdr/DIBDspyDtlList/DIBDspyDtl[@DiscrepancyGroup='SHIPMENT' or @DiscrepancyGroup='TRANSPORT']";
	public static String XP_MARKING_DISCREPANCY_DTLS = "/DIBDspyHdr/DIBDspyDtlList/DIBDspyDtl[@DiscrepancyGroup='ITEM']";
	public static String XP_SHIPTRANSPORT_DISCREPANCY_DTLS = "/DIBDspyHdr/DIBDspyDtlList/DIBDspyDtl[@DiscrepancyGroup='SHIPMENT' or @DiscrepancyGroup='TRANSPORT']";
	public static String XP_SHIPMENT_LIST = "/ShipmentList/Shipment";
	public static String XP_DISCREPANCY = "/Shipment/Extn/DIBDscrpncyList/DIBDscrpncy";
	public static String XP_SHIPMENT_EXTN = "/ShipmentList/Shipment/Extn/DIBDspyHdrList/DIBDspyHdr";

	
	//XML Attributes - Batch report Agent
	public static String A_ALLOWED_OVERRIDDEN_CRITERIA = "AllowedOverriddenCriteria";
	public static String A_REPORT_CRITERIA = "ReportCriteria";
	public static String A_REPORT_ID = "ReportId";
	public static String A_REPORT_IDS = "ReportIds";
	public static String A_VENDOR_IDS = "VendorIds";
	public static String A_CODE_VALUE = "CodeValue";
	public static String A_CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	public static String A_CODE_LONG_DESCRIPTION = "CodeLongDescription";
	public static String A_CODE_TYPE = "CodeType";
	public static String A_VENDOR_ID = "VendorID";
	public static String A_VENDOR_Id = "VendorId";
	
	public static String XP_REPORT_DURATION = "/Configuration/@ReportDuration";
	public static String XP_GENERATE_DATE = "/Configuration/@GenerateDate";
	public static String XP_GENERATE_MONTH = "/Configuration/@GenerateMonth";
	public static String XP_GENERATE_YEAR = "/Configuration/@GenerateYear";
	public static String XP_CONFIG_REPORT= "/Configuration/Reports/Report";
	public static String XP_CONFIG_VENDOR= "/Configuration/Vendors/Vendor";
	public static String XP_COMMON_CODE = "/CommonCodeList/CommonCode";
	public static String XP_VENDOR = "/VendorList/Vendor";
	
	public static String XP_VENDOR_MERCMGR = "SellerOrganization/Extn/@ExtnMerchandiseMgr";
	public static String XP_VENDOR_FLP = "SellerOrganization/Extn/@ExtnFLP";
	
	public static String XP_ORDER_VC_DATE="/Order/OrderAudit/OrderAuditLevels/OrderAuditLevel/OrderAuditDetails/OrderAuditDetail/Attributes/Attribute[@Name='VendorComplianceDate']";
	
	//CR003 - Dock Scheduling changes
	public static String XP_SHIPID_ORDERS="/Shipment/ShipID/OrderList/Order";
	public static String XP_SHIPID_ASNS="/Shipment/ShipID/ASNList/ASN";
	public static String XP_AUDIT_DOCKKEY="/DIBAppointmentAuditList/DIBAppointmentAudit[@ReasonCode='CREATE APPOINTMENT']/@DockAppointmentKey";
	public static String XP_DOCKAPP_ENTITIES="/DockAppointment/Extn/DIBDockAppEntitiesList";
	public static String XP_AUDIT_DOCKAUD_ELM="/DIBAppointmentAuditList/DIBAppointmentAudit[@ReasonCode='CREATE APPOINTMENT']";
	public static String E_ORGANIZATION ="Organization";
	public static String E_DOCK ="Dock";
	public static String A_NUM_MATCH_APPTS ="Dates/Date/UnavailableSlots/@NoOfMatchingAppointments";
	public static String E_UNAVSLOT_DOCKAPP ="Dates/Date/UnavailableSlots/UnavailableSlot/DockAppointment";
	
	public static String XP_ORGANIZATION = "/OrganizationList/Organization";
	
	
	//RTD113 - summary changes
	public static String A_REP_DATE="ReportDate";
	public static String A_HIGH="High";
	public static String A_LOW="Low";
	public static String A_GRADE="Grade";
	public static String A_OFR_POQTY="OFRPOQty";
	public static String A_OFR_SCORINGQTY="OFRScoringQty";
	public static String A_COMPL_ASN_COUNT="ComplAsnCount";
	public static String A_ASN_WITH_SC="AsnsWithShipcompl";
	public static String A_ASN_WITH_MC="AsnsWithMarkcompl";
	public static String A_SHIP_COMPL_SCORE="ShipComplScore";
	public static String A_MARK_COMPL_SCORE="MarkComplScore";
	public static String A_ACC_ASN_COUNT="AccAsnCount";
	public static String A_TOTAL_ACC_ASN_COUNT="NoAccurateAsns";
	public static String A_ACCURACY_SCORE="AccuracyScore";
	public static String A_ONTIME_ASN_CNT="NoOntimeAsns";
	public static String A_ONTIMEASN_SCORE="OnTimeScore";
	
	public static String A_TOTAL_ONTIME_ASNS="OntimeAsnCount";
	public static String E_REP_METRIC ="DIBReportMetric";
	
	
	
	// Xpath for UOM issue 
	
	public static final String XPATH_SHIPMENT_LINE = "/Shipment/ShipmentLines/ShipmentLine";
	public static final String XPATH_ORDER_LINE = "/Order/OrderLines/OrderLine";
	public static final String XPATH_ORDER_LIST_ORD_LINE = "/OrderList/Order/OrderLines/OrderLine";
	
	
	public static final String E_ORDER_LINES = "OrderLines";
	public static final String E_PRICE_INFO = "PriceInfo";
	public static final String A_MAX_RECS = "MaximumRecords";
	public static final String A_MAX_PER_CALL = "MaxPerCall";
	public static final String E_ORGANIZATION_LIST = "OrganizationList";
	public static final String A_EXTN_FLP = "ExtnFLP";
	public static final String E_FLP = "FLP";
	public static final String A_PRODUCT_CLASS = "ProductClass";
	public static final String E_SHIPMENT_LINES = "ShipmentLines";
	public static final String E_SHIPMENT_LIST = "ShipmentList";
	public static final String E_SHIP_TO = "ShipTo";
	public static final String E_SA_LINES = "SALines";
	public static final String E_SA_LINE = "SALine";
	public static final String E_CHAINED_FROM_ORDER_LINE = "ChainedFromOrderLine";
	 
	
	public static final String A_PRIME_LINE_NO = "PrimeLineNo";
	public static final String A_SUB_LINE_NO = "SubLineNo";
	public static final String A_SHIPMENT_SORT_LOCATION_ID = "ShipmentSortLocationId";
	public static final String E_LOADS = "Loads";
	public static final String E_LOAD_STOPS = "LoadStops";
	public static final String E_LOAD_SHIPMENTS = "LoadShipments";
	//public static final String E_STATUS_AUDIT = "StatusAudit";
	public static final String A_TRANPORT_STATUS_CODE = "TransportStatusCode";
	public static final String A_TRANPORT_STATUS_TEXT = "TransportationStatusText";
	public static final String E_LOAD_STATUS_AUDIT = "LoadStatusAudit";
	public static final String E_LOAD_STATUS_AUDITS = "LoadStatusAudits";
	public static final String A_CITY = "City";
	public static final String A_COMPANY = "Company";
	public static final String A_STATE = "State";
	public static final String A_LOAD_REACHED_DESTINATION = "LoadReachedDestination";
	public static final String A_ACCEPT_OOS_UPDATES = "AcceptOutOfSequenceUpdates";
	public static final String A_STOP_REACHED = "StopReached";
	public static final String A_Scac = "Scac";
	public static final String A_LOAD_LEFT_ORIGIN = "LoadLeftOrigin";
	public static final String A_IS_IN_TRANSIT = "IsIntransit";
	public static final String A_DROP_AT_SHIPMENT_DESTINATION = "DropAtShipmentDestination";
	public static final String A_LOAD_SHIPMENT_KEY = "LoadShipmentKey";
	public static final String A_IS_SHIPMENT_DELIVERED = "IsShipmentDelivered";
	public static final String E_GET_SHIPMENT_RECEIPT_DISCREPANCY = "GetShipmentReceiptDiscrepancy";
	public static final String E_RECEIPT_DISCREPANCIES = "ReceiptDiscrepancies";
	public static final String E_RECEIPT_DISCREPANCY = "ReceiptDiscrepancy";
	public static final String A_DISCR_QTY = "DiscrQty";
	public static final String A_DISCR_TYPE = "DiscrType";
	public static final String E_ORDER_STATUS_CHANGE = "OrderStatusChange";
	public static final String A_IGNORE_TRAN_DEPENDENCIES = "IgnoreTransactionDependencies";
	public static final String A_MODIFICATION_REASON_CODE = "ModificationReasonCode";
	public static final String A_MODIFICATION_REASON_TEXT = "ModificationReasonText";
	public static final String A_TRANSPORT_STATUS_DATE = "TransportStatusDate";
	public static final String A_ACTUAL_DEPARTURE_DATE = "ActualDepartureDate";
	public static final String E_ORDER_LINE_LIST = "OrderLineList";
	public static final String E_RECEIPT_LIST = "ReceiptList";
	public static final String E_DIB_ORDER_SUMMARY_LIST = "DIBOrderSummaryList";
	public static final String E_DIB_SHIPMENT_LINE_SUMMARY_LIST = "DIBShipmentLineSummaryList";
	public static final String E_DIB_VS_SUMMARY_REPORT = "DIBVsSummaryReport";
	public static final String A_SUMMARY_REPORT_KEY = "SummaryReportKey";
	public static final String A_METRIC_TYPE = "MetricType";
	public static final String E_DIB_REPORT_METRIC_LIST = "DIBReportMetricList";
	public static final String E_DIB_REPORT_METRIC = "DIBReportMetric";
	public static final String A_RECEIPT_NO = "ReceiptNo";
	public static final String E_SHIPMENT_STATUS_AUDITS = "ShipmentStatusAudits";
	public static final String A_STATUS_DATE = "StatusDate";
	public static final String A_MARK_COMPL_ASN_LINE_COUNT = "MarkComplAsnLineCount";
	public static final String E_INVENTORY_ITEM = "InventoryItem";
	public static final String E_INVENTORY_LIST = "InventoryList";
	public static final String A_TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";
	public static final String E_MULTI_API = "MultiApi";
	public static final String E_API = "API";
	public static final String A_NAME = "Name";
	public static final String E_INPUT = "Input";
	public static final String E_GET_SHIP_NODE_INVENTORY = "getShipNodeInventory";
	public static final String A_CONSIDER_ALL_NODES = "ConsiderAllNodes";
	public static final String A_ITEM_ORGANIZATION_CODE = "ItemOrganizationCode";
	public static final String E_SHIP_NODE_INVENTORY = "ShipNodeInventory";
	public static final String E_SHIP_NODES = "ShipNodes";
	public static final String E_SHIP_NODE = "ShipNode";
	public static final String A_TOTAL_SUPPLY = "TotalSupply";
	public static final String E_SUPPLIES = "Supplies";
	public static final String E_SUPPLY_DETAILS = "SupplyDetails";
	public static final String E_DEMAND_DETAILS = "DemandDetails";
	public static final String E_INVENTORY_SUPPLY_TYPE = "InventorySupplyType";
	public static final String A_ON_HAND_SUPPLY = "OnhandSupply";
	public static final String A_SUPPLY_TYPE = "SupplyType";
	public static final String A_DEMAND_TYPE = "DemandType";
	public static final String E_PRIMARY_INFORMATION = "PrimaryInformation";
	public static final String A_IMAGE_LOCATION = "ImageLocation";
	public static final String A_IMAGE_LABEL = "ImageLabel";
	public static final String A_IMAGE_ID = "ImageID";
	public static final String E_ITEM_LIST = "ItemList";
	public static final String A_ERR_FLAG = "ErrorFlag";
	public static final String A_ERR_DESC = "ErrorDesc";
	public static final String E_VENDOR_LIST = "VendorList";
	public static final String E_SERVER = "Server";
	public static final String E_REPORT = "Report";
	public static final String E_COMMON_CODE = "CommonCode";
	public static final String E_COMMON_CODE_LIST = "CommonCodeList";
	public static final String E_REPORTS = "Reports";
	public static final String E_COMMITTED_SCHEDULE = "CommittedSchedule";
	public static final String A_COMMITTED_QUANTITY = "CommittedQuantity";
	public static final String E_DIB_DSPY_HDR = "DIBDspyHdr";
	public static final String E_DIB_DSPY_DTL = "DIBDspyDtl";
	public static final String A_FIELD = "Field";
	public static final String A_VALUE = "Value";
	public static final String E_RESULTS = "Results";
	public static final String E_RESULT = "Result";
	public static final String A_CUSTOMS_PAPERWORK_RECEIVED = "ExtnCustomsPaperworkReceived";
	public static final String A_ALERT_SERVICE = "PendingCustomsPaperworkAlertService";
	public static final String A_NUM_RECS_TO_BUFFER = "NumRecordsToBuffer";
	public static final String A_CUSTOMS_PAPERWORK_DELAY_LIMIT = "CustomsPaperworkDelayLimit";
	public static final String A_VENDOR_CODE = "VendorCode";
	public static final String E_RSCS = "Rscs";
	public static final String E_RSC = "Rsc";
	public static final String E_DOCKAPPOINTMENTS = "DockAppointments";
	public static final String A_APPOINTMENT_NO = "AppointmentNo";
	public static final String A_ADJUSTMENT_TYPE = "AdjustmentType";
	public static final String E_DOCK_APP_ENTITIES = "DIBDockAppEntities";
	public static final String E_SHORT_ORDER = "ShortOrder";
	public static final String A_SHORT_FOR_ALL_AVAILABLE_QTY = "ShortForAllAvailableQty";
	public static final String A_EXPECTED_DELIVERY_DATE = "ExpectedDeliveryDate";
	public static final String A_MANUALLY_ENTERED = "ManuallyEntered";
	public static final String A_STATUS_QUANTITY = "StatusQuantity";
	public static final String A_STATUS_QTY = "StatusQty";
	public static final String A_IGNORE_ORDERING = "IgnoreOrdering";
	public static final String A_SHIPMENT_CLOSED = "ShipmentClosedFlag";
	public static final String A_CREATETS = "Createts";
	public static final String E_TRANSACTION_LIST = "TransactionList"; 
	public static final String E_TRANSACTION = "Transaction"; 
	public static final String A_TRAN_ID = "Tranid"; 
	public static final String A_TRAN_NAME = "Tranname"; 
	public static final String E_TRAN_PICKUP_STAUTS_LIST = "TransactionPickupStatusList"; 
	public static final String E_TRAN_PICKUP_STAUTS = "TransactionPickupStatus"; 
	public static final String A_RECEIVED_QTY = "ReceivedQty"; 
	public static final String A_OVERRIDE = "Override";
	public static final String A_ORDERED_QTY ="OrderedQty";
	public static final String A_BACK_ORDERED_QTY ="BackOrderedQuantity";
	public static final String A_PARENT_DEPENDENT_GROUP ="ParentOfDependentGroup";
	public static final String A_DEPENDENT_PRIME_LINENO ="DependentOnPrimeLineNo";
	public static final String A_DEPENDENT_SUB_LINENO ="DependentOnSubLineNo";
	public static final String A_DATA_TYPE ="DataType";
	public static final String A_DATA_KEY ="DataKey";
	public static final String A_ORDER_RELEASE_KEY	 = "OrderReleaseKey";
	public static final String A_PRODUCT_LINE	 = "ProductLine";
    public static final String A_TO_ADDRESS_KEY = "ToAddressKey";
    public static final String A_EXPECTED_SHIPMENT_DATE = "ExpectedShipmentDate";
    public static final String A_PICKTICKET_NO = "PickticketNo";
    public static final String A_KEEP_TASK_OPEN = "KeepTaskOpen";
    public static final String A_TASK_Q_KEY = "TaskQKey";
    public static final String A_IS_HAZMAT = "IsHazmat";
    public static final String A_ADD_LINE_1 = "AddressLine1";
    public static final String A_ADD_LINE_2 = "AddressLine2";
    public static final String A_ADD_LINE_3 = "AddressLine3";
    public static final String A_ADD_LINE_4 = "AddressLine4";
    public static final String A_EXTN_PICK_TICKET_NO = "ExtnPickTicketNo";
    public static final String A_EXTN_WMS_INST_TYPE = "ExtnWMSInstType";
    public static final String A_PACK_LIST_TYPE = "PackListType";
    public static final String A_SHIP_TO_KEY = "ShipToKey";
    public static final String A_RELEASE_NO = "ReleaseNo";
    public static final String A_FIRST_NAME = "FirstName";
    public static final String A_MIDDLE_NAME = "MiddleName";
    public static final String A_LAST_NAME = "LastName";
    public static final String A_COUNTRY = "Country";
    public static final String A_ZIP_CODE = "ZipCode";
    public static final String A_DAY_PHONE = "DayPhone";
    public static final String A_EMAIL_ID = "EMailID";
    public static final String A_GIFT_FLAG = "GiftFlag";
    public static final String A_GIFT_WRAP = "GiftWrap";
    public static final String A_ORDER_DATE = "OrderDate";
    public static final String A_SHIPMENT_LINE_NO = "ShipmentLineNo";
    public static final String A_NOTE_TEXT = "NoteText";
    public static final String A_OPEN_QTY = "OpenQty";
    public static final String A_UPC_CODE = "UPCCode";
    public static final String A_STATUS_DESCRIPTION = "StatusDescription";
    public static final String A_STATUS_REASON = "StatusReason";
 
    
	
	
	//Extn Attributes
	public static final String A_EXTN_IS_HAZARDOUS ="ExtnIsHazardous";
	public static final String A_EXTN_SHIP_ALONE ="ExtnShipAlone";
	public static final String A_PICK_TICKET_NO = "PickticketNo";
	public static final String A_OVERRIDE_MOD_RULES = "OverrideModificationRules";
	public static final String A_EXTN_IS_SHIP_ALONE = "ExtnIsShipAlone";
	public static final String E_ORDER_LINE_SOUR_CONTRLS = "OrderLineSourcingControls";
	public static final String E_ORDER_LINE_SOUR_CONTRL = "OrderLineSourcingCntrl";
	public static final String A_SUPPRESS_SOURCING = "SuppressSourcing";
	public static final String A_ITEM_TYPE = "ItemType";
	public static final String A_HEADER_CHARGES = "HeaderCharges";
	public static final String A_HEADER_CHARGE = "HeaderCharge";
	public static final String A_LINE_CHARGES = "LineCharges";
	public static final String A_LINE_CHARGE = "LineCharge";
	public static final String A_REFERENCE = "Reference";
	public static final String A_TOTAL_ORDER_LIST = "TotalOrderList";
	public static final String A_PAYMENT_STATUS = "PaymentStatus";
	public static final String E_PAYMENT_METHODS = "PaymentMethods";
	public static final String E_PAYMENT_METHOD = "PaymentMethod";
	public static final String E_PAYMENT_DETAILS = "PaymentDetails";
	public static final String E_PAYMENT_DETAILS_LIST = "PaymentDetailsList";
	public static final String E_CREDIT_CARD_TRANSACTIONS = "CreditCardTransactions";
	public static final String E_CREDIT_CARD_TRANSACTION = "CreditCardTransaction";
	
	
	
	//XPATH
	
	public static String XP_ORDERRELEASE_ORDER_LINE = "/OrderRelease/OrderLines/OrderLine";
	public static String XP_ORDERRELEASE_PERSON_INFO = "/OrderRelease/PersonInfoShipTo";
	public static String XP_ORDERRELEASE_ORDER = "/OrderRelease/Order";
	public static String XP_ORDERRELEASE_ORDERLINES = "/OrderRelease/OrderLines";
	
	
		//Extn Attributes
	public static final String A_EXTN_WRAP_TOGETHER_GROUP_CODE ="ExtnWrapTogetherGroupCode";
	public static final String A_EXTN_GIFT_WRAP_LINE_NO ="ExtnGiftWrapLineNo";
	public static final String A_EXTN_GIFT_ITEM_ID ="ExtnGiftItemID";
	
	public static final String A_CREATE_TS ="Createts";
	public static final String A_EXTN_CAGE_ITEM ="ExtnCageItem";
	public static final String A_EXTN_SIZE_DESC ="ExtnSizeDesc";
	public static final String A_EXTN_COLOR_DESC ="ExtnColorDesc";
	public static final String A_LINE_TYPE ="LineType";
	public static final String A_EXTN_PO_RECEIPT_ID =  "ExtnPOReceiptID";
	public static final String A_LINE_COUNT ="LineCount";
	public static final String A_RECEIPT_ID = "ReceiptID";
	public static String E_PERSON_INFO_BILL_TO	 = "PersonInfoBillTo";
	public static String A_EXTN_SERVICE_SEQ	 = "ExtnShippingServiceLevelSeq";
	public static String A_EXTN_BAGGAGE	 = "ExtnBaggable";
	
	public static String XP_ORDERRELEASE_TO_ADDRESS = "/Shipment/ToAddress";
	public static String A_EXTN_IS_PO_BOX = "ExtnIsPOBox";
	public static String A_EXTN_IS_MILITARY = "ExtnIsMilitary";
	public static String A_EXTN_HDR_GFT_RECP_ID = "ExtnHdrGiftReceiptID";
	public static String A_ITEM_ALIAS_LIST = "ItemAliasList";
	public static String A_ITEM_ALIAS = "ItemAlias";
	public static String A_EXTN_GIFT_FROM = "ExtnGiftFrom";
	public static String A_EXTN_GIFT_MESSAGE = "ExtnGiftMessage";
	public static String A_EXTN_GIFT_TO = "ExtnGiftTo";
	public static String A_EXTN_LINE_GFT_RECP_ID = "ExtnLineGiftReceiptID";
	public static String A_EXTN_UPC = "ExtnUPC";
	public static String A_ALIAS_NAME = "AliasName";
	public static String A_ALIAS_VALUE = "AliasValue";
	public static String A_EXTN_SHIP_VIA = "ExtnShipVia";
	public static String A_EXTN_TOTAL_REL_UNITS = "ExtnTotalReleaseUnits";
	public static String A_EXTN_RG = "ExtnRG";
	public static String A_EXTN_CARTON_TYPE = "ExtnCartonType";
	public static String A_EXTN_WRAP_SINGLE_TOG = "ExtnWrapSingleTogether";
	
	public static String A_ITEM_DESC = "ItemDesc";
	public static String A_ITEM_SHORT_DESC = "ShortDescription";
	public static String A_ITEM_UNIT_COST = "UnitCost";
	public static String E_CHARGE_TRANSACTION_DETAIL = "ChargeTransactionDetail";
	public static String E_CHARGE_TRANSACTION_DETAILS = "ChargeTransactionDetails";
	public static String A_CHARGE_TRANSACTION_KEY = "ChargeTransactionKey";
	public static String A_CHARGE_TYPE = "ChargeType";
	public static String A_OPEN_AUTHORIZED_AMOUNT = "OpenAuthorizedAmount";
	public static String A_AUTHORIZATION_ID = "AuthorizationID";
	public static String A_CREDIT_CARD_TYPE = "CreditCardType";
	public static String A_AUTH_TIME = "AuthTime";
	public static String A_AUTH_EXPIRY_DATE = "AuthorizationExpirationDate";
	public static String A_PAYMENT_TYPE = "PaymentType";
	public static String A_PAYMENT_RULE_ID = "PaymentRuleId";
	public static String A_REQUEST_AMOUNT = "RequestAmount";
	public static String A_PROCESSED_AMOUNT = "ProcessedAmount";
	public static String A_SVC_NO = "SvcNo";
	public static String A_DISPLAY_SVC_NO = "DisplaySvcNo";
	public static String A_TOTAL_CHARGED = "TotalCharged";
	public static String A_MAX_CHARGE_LIMIT = "MaxChargeLimit";
	public static String A_UNLIMITED_CHARGES = "UnlimitedCharges";
	public static String A_TOTAL_CREDITS = "TotalCredits";
	public static String A_TOTAL_OPEN_AUTHORIZATION = "TotalOpenAuthorizations";
	public static String A_TOTAL_AMOUNT = "TotalAmount";
	public static String A_BOOK_AMOUNT = "BookAmount";
	public static String A_CREDIT_AMOUNT = "CreditAmount";
	public static String A_HOLD_AGAINST_BOOK = "HoldAgainstBook";
	public static String A_AUTH_RETURN_CODE = "AuthReturnCode";
	public static String A_AUTH_AVS = "AuthAvs";
	public static String A_AUTH_RETURN_MESSAGE = "AuthReturnMessage";
	public static String A_AUTH_AMOUNT = "AuthAmount";
	public static String A_AUTH_CODE = "AuthCode";
	public static String A_TRAN_RETURN_CODE = "TranReturnCode";
	public static String A_TRAN_RETURN_MESSAGE = "TranReturnMessage";


	
	// For NetcoolLog
	
	public static String A_XML = "XML";
	public static String A_QUEUE_KEY = "Queue_Key";
	public static String A_INBOX = "Inbox";
	public static String A_ACTIVITY_FLAG = "ActiveFlag";
	public static String A_QUEUE = "Queue";
	public static String A_QUEUE_KEY_EX_LST = "QueueKey";
	public static String A_CONSOLIDATION_COUNT = "ConsolidationCount";
	public static String A_MEDIUM = "Medium";
	public static String A_INBOX_REF_LIST = "InboxReferencesList";
	public static String A_INBOX_REF = "InboxReferences";
	public static String A_INTEGRATION_ERROR = "IntegrationError";
	public static String A_ERROR_TXN_ID = "ErrorTxnId";
	public static String A_EXCEPTION_TYPE = "ExceptionType";
	public static String A_LAST_OCCURED_ON = "LastOccurredOn";
	public static String A_GENERATED_ON = "GeneratedOn";
	public static String A_ERROR_CODE = "ErrorCode";
	public static String A_ERROR_STRING = "ErrorString";
	public static String A_FLOW_NAME = "FlowName";
	public static String A_QUEUE_ID = "QueueId";
	public static String A_EXCEPTION_ID = "ExceptionId";
	public static String A_SEVERITY = "Severity";
	
	//Send Invoice to COSA
	public static final String A_EXTN_RECEIPT_ID = "ExtnReceiptID";
	public static final String E_INVOICE_HEADER = "InvoiceHeader";
	public static final String E_LINE_DETAILS = "LineDetails";
	public static final String E_LINE_DETAIL = "LineDetail";
	public static final String A_EXTN_DEPT = "ExtnDept";
	public static final String A_EXTN_CLASS = "ExtnClass";
	public static final String A_EXTN_SUB_CLASS = "ExtnSubClass";
	public static final String A_ENTERED_BY = "EnteredBy";
	public static final String A_EXTN_CURRENT_ITEM_STATUS = "ExtnCurrentItemStatus";
	public static final String A_EXTN_BOGO_PRORATION = "ExtnBOGOProration";
	public static final String A_EXTN_BOGO_SEQ = "ExtnBOGOSeq";
	public static final String A_EXTN_BOGO_RECEIPT_MARK_DOWN = "ExtnBOGOReceiptMarkDown";
	public static final String A_EXTN_BOGO_RETURN_TAX = "BOGOReturnTax";
	public static final String A_EXTN_RETURN_PRICE = "ExtnReturnPrice";
	public static final String E_AWARDS = "Awards";
	public static final String E_AWARD = "Award";
	public static final String E_AWARD_ID = "AwardId";
	public static final String E_PROMOTION_ID = "PromotionId";
	public static final String E_PROMOTION_TYPE = "PromotionType";
	public static final String E_PROMOTION_GROUP = "PromotionGroup";
	public static final String E_PROMOTIONS = "Promotions";
	public static final String E_PROMOTION = "Promotion";
	public static final String E_EXTN_COUPON_NO = "ExtnCouponNo";
	public static final String E_EXTN_COUPON_EVENT_ID = "ExtnCouponEventID";
	public static final String E_EXTN_COUPON_ALGORITHM = "ExtnCouponAlgorithm";
	public static final String E_EXTN_COUPON_BALANCE = "ExtnCouponBalance";
	public static final String E_EXTN_COUPON_AMOUNT = "ExtnCouponAmount";
	public static final String A_EXTN_COSA_WEEK = "ExtnCosaWeek";
	public static final String A_EXTN_COSA_MONTH = "ExtnCosaMonth";
	public static final String A_EXTN_COSA_YEAR = "ExtnCosaYear";
	public static final String A_EXTN_COSA_DAY = "ExtnCosaDay";
	public static final String A_EXTN_SHIPPING_GEO_CODE = "ExtnShippingGeoCode";
	public static final String A_EXTN_COUPON_DISCOUNT_PERCENTAGE = "ExtnCouponDiscountPercentage";
	public static final String A_SIZE_CODE = "SizeCode";
	public static final String A_COLOR_CODE = "ColorCode";
	public static final String A_UPC = "UPC";
	public static final String A_ITEM_SIZE = "ItemSize";
	public static final String A_ITEM_COLOR = "ItemColor";
	public static final String A_ITEM_DESCRIPTION = "ItemDescription";
	public static final String E_BILL_TO = "BillTo";
	public static final String EXTN_PER_PRORATE_DISC = "ExtnPercentProratedDisc";
	public static final String EXTN_VALUE1 = "ExtnValue1";
	public static final String EXTN_PROMO_SCHEME = "ExtnPromotionScheme";
	public static final String EXTN_PROMO_CODE = "ExtnPromotionCode";
	public static final String EXTN_PROMO_ID = "ExtnPromotionId";
	public static final String E_LINE_CHARGE_LIST = "LineChargeList";
	public static final String E_RECORD_EXTERNAL_CHARGES = "RecordExternalCharges";
	public static final String A_CREDIT_CARD_NO = "CreditCardNo";
	 
	
	//Extn values for - Receipt ID
	
	public static String A_EXTN_SA_RCPT_ID ="ExtnSAReceiptID";
	public static String A_EXTN_SA_LINE_GIFT_RCPT_ID ="ExtnSALineGiftReceiptID";
	
	//Values for Line Receipt ID
	public static String A_NOTE_KEY ="NoteKey";

	public static final String A_EXTN_PREENC_PO_RECEIPT_ID = "ExtnPreEncPOReceiptID";
	public static final String A_EXTN_PREENC_HDR_RECP_ID ="ExtnPreEncReceiptID";
	public static final String A_EXTN_PREENC_SA_RECP_ID ="ExtnPreEncSAReceiptID";
	public static final String A_EXTN_PREENC_SA_LINE_GIFT_RCPT_ID = "ExtnPreEncSALineGiftRptID";

	public static final String A_BASE_DROP_STATUS = "BaseDropStatus";
	
		
	public static final String E_CONTAINERS = "Containers";
	public static final String E_CONTAINER = "Container";
	public static final String E_CONTAINER_DETAILS = "ContainerDetails";
	public static final String A_PO_NUMBER = "PONumber";
	
	// Kohls Cash Coupon
	public static final String E_KOHLS_CASH_TABLE_LIST = "KOHLSCashTableList";
	public static final String E_KOHLS_CASH_TABLE = "KOHLSCashTable";
	public static final String A_KOHLS_CASH_AMOUNT = "KohlsCashAmount";
	public static final String A_KOHLS_CASH_SEQ = "KohlsCashSeq";
	public static final String A_TABLE_KEY = "TableKey";
	
	// Gift Wrap
	public static final String E_HEADER_TAXES = "HeaderTaxes";
	public static final String E_HEADER_TAX = "HeaderTax";
	public static final String E_LINE_TAXES = "LineTaxes";
	public static final String E_LINE_TAX = "LineTax";
	public static final String A_TAX = "Tax";
	public static final String A_TAX_NAME = "TaxName";
	public static final String A_TAX_PERCENTAGE = "TaxPercentage";
	public static final String A_TAXABLE_FLAG = "TaxableFlag";
	public static final String E_TAX_BREAK_LIST = "TaxBreakupList";
	public static final String E_TAX_BREAK_UP = "TaxBreakup";
	public static final String A_LINE_KEY = "LineKey";
	
	// Inventory Snapshot
	
	public static final String A_MAX_RECORDS = "MaxRecords";
	public static final String E_GET_INV_SNAP_SHOT = "GetInventorySnapShot";
	public static final String A_MAX_NO_OF_ITEMS = "MaximumNumberOfItems";
	public static final String A_SNAP_SHOT_TIME = "SnapShotTimeStamp";
	public static final String A_INV_ITEM_KEY = "InventoryItemKey";
	public static final String A_LAST_INV_ITEM_KEY = "LastInventoryItemKey";
	
	public static final String A_SELECT_METHOD = "SelectMethod";
	public static final String E_TOTAL_SUMMARY = "TotalSummary";
	public static final String E_CHARGE_SUMMARY = "ChargeSummary";
	public static final String E_CHARGE_SUMMARY_DETAIL = "ChargeSummaryDetail";
	
	//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/19/2012
	public static final String A_COMPLETE_INV_FALG = "CompleteInventoryFlag";
	//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/19/2012
	
	//Start --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
	public static final String A_SEGMENT = "Segment";
	public static final String A_SEGMENT_TYPE = "SegmentType";
	public static final String A_SHIP_BY_DATE = "ShipByDate";
	public static final String A_SUPPLY_REFERENCE = "SupplyReference";
	public static final String A_SUPPLY_REFERENCE_TYPE = "SupplyReferenceType";
	public static final String A_DEMAND_SHIP_DATE = "DemandShipDate";
	//End --- Added for SF Case # 00392408 -- OASIS_SUPPORT 1/25/2012
	
	
	
	// DSV Inventory
	
	public static final String E_ITEM_SHIP_NODE = "ItemShipNode";
	public static final String A_OWNER_KEY = "OwnerKey";
	public static final String A_DISTRIBUTION_RULE_ID = "DistributionRuleId";
	public static final String A_PRIORITY = "Priority";
	public static final String A_ITEMID = "ItemId";
	public static final String E_ITEM_SHIP_NODE_LIST = "ItemShipNodeList";
	public static final String A_VENDOR_OPERATION = "Operation";
	
	
	public static final String A_ORDER_INVOICE_KEY = "OrderInvoiceKey";
	public static final String A_EXTN_KC_USED = "ExtnKohlsCashCouponUsed";
	public static final String A_NUMBER_OF_NOTES = "NumberOfNotes";
	
	// new order invoice design changes
	
	public static final String A_LAST_TASK_Q_KEY = "LastTaskQueueKey";
	public static final String E_GET_TASK_Q_DATA_INPUT = "GetTaskQueueDataInput";
	public static final String E_TASK_Q = "TaskQueue";
	public static final String A_NEXT_TASK_Q_INTERVAL = "NextTaskQInterval";
	public static final String A_AVAILABLE_DATE = "AvailableDate";
	public static final String A_EXTN_SHIP_NODE = "ExtnShipNode";
	public static final String A_KOHLS_CASH_ID = "KohlsCashID";
	public static final String A_IGNORE_STATUS_CHECK = "IgnoreStatusCheck";
	
	public static final String E_COLLECITON_DETAILS = "CollectionDetails";
	public static final String E_COLLECITON_DETAIL = "CollectionDetail";
	
	public static final String A_AMOUNT_COLLECTED   =  "AmountCollected";
	public static final String A_AWAIT_AUTH_INTERFACE_AMT   =  "AwaitingAuthInterfaceAmount";
	public static final String A_AWAIT_CHARGE_INTERFACE_AMT  =  "AwaitingChargeInterfaceAmount";
	public static final String A_CHARGE_SEQ  =  "ChargeSequence";
	public static final String A_CHECK_NO  =  "CheckNo";
	public static final String A_CHECK_REF  =  "CheckReference";
	public static final String A_CREDIT_CARD_EXP_DATE  =  "CreditCardExpDate";
	public static final String A_CREDIT_CARD_EXP_NAME  =  "CreditCardName";
	
	public static final String A_CUSTOMER_ACC_NO  =  "CustomerAccountNo";
	public static final String A_CUSTOMER_PO_NO  =  "CustomerPONo";
	public static final String A_DISP_CREDIT_CARD_NO  =  "DisplayCreditCardNo";
	public static final String A_DISP_PAYMENT_REF_1  =  "DisplayPaymentReference1";
	public static final String A_INCOMPLETE_PAYMENT_TYPE  =  "IncompletePaymentType";	
	public static final String A_PAYMENT_KEY  =  "PaymentKey";
	public static final String A_PAYMENT_REF_1  =  "PaymentReference1";
	public static final String A_PAYMENT_REF_2  =  "PaymentReference2";
	public static final String A_PAYMENT_REF_3  =  "PaymentReference3";	
	public static final String A_REQUESTED_AMT  =  "RequestedAuthAmount";
	public static final String A_REQUESTED_CHARGE_AMT  =  "RequestedChargeAmount";	
	public static final String A_SUSPENDED_ANYMORE_CHARGES  =  "SuspendAnyMoreCharges";
	public static final String A_TOTAL_AUTHORIZED =  "TotalAuthorized";			
	public static final String A_TOTAL_REFUNDED_AMT  =  "TotalRefundedAmount";
	public static final String A_TOTAL_LINES  =  "TotalLines";
	public static final String A_SUSPEND_ANY_MORE_CHARGES  =  "SuspendAnyMoreCharges";
	public static final String A_REFERENCE1  =  "Reference1";
	public static final String A_REFERENCE2  =  "Reference2";
	public static final String A_ADD_EXPECTED_AUTHS  =  "AdditionalExpectedAuthorizations";
	public static final String A_EXTN_KCE_COUPON_NO  =  "ExtnKCECouponNo";
	public static final String A_EXTN_KCE_COUPON_ALGORITHM  =  "ExtnKCECouponAlgorithm";
	public static final String A_EXTN_KCE_COUPON_EVENT_ID  =  "ExtnKCECouponEventID";
	public static final String A_EXTN_KCE_COUPON_BALANCE  =  "ExtnKCECouponBalance";
	public static final String A_EXTN_KCE_COUPON_AMT  =  "ExtnKCECouponAmount";
	
	
	// For Virtual and Plastic Gift Cards sent to COSA
	public static final String A_EXTN_VIRTUAL_GC_NUM  =  "ExtnVirtualGCNumber";
	public static final String A_EXTN_GC_UPC  =  "ExtnGCUPC";
	public static final String A_EXTN_GC_CHECK_DIGIT  =  "ExtnGCCheckDigit";
	public static final String A_EXTN_GC_SERIAL_NO  =  "ExtnGCSerialNo";
	
	
	public static final String E_SHIPMENT_TAG_SERIALS  =  "ShipmentTagSerials";
	public static final String E_SHIPMENT_TAG_SERIAL  =  "ShipmentTagSerial";
	public static final String A_SERIAL_NO  =  "SerialNo";
	
	public static final String A_LAST_ORDER_INVOICE_KEY  =  "LastOrderInvoiceKey"; 
	
	public static String A_RELEASE = "Release";
	public static String A_SEQUENCE_NO = "SequenceNo";
	
	// Hard Totals
	
	public static String E_KL_HARD_TOTALS = "KLHardTotals";
	public static String A_STORE_NUMBER = "StoreNumber";
	public static String A_MAXIMUM_RECORDS = "MaximumRecords";
	public static String A_TRAN_DATE = "TransactionDate";
	public static String E_ORDER_BY = "OrderBy";
	public static String E_ATTRIBUTE = "Attribute";
	public static String A_DESC = "Desc";
	public static String A_HARD_TOTALS_KEY = "HardTotalsKey";
	public static String A_HARD_TOTALS_KEY_QRY_TP = "HardTotalsKeyQryType";
	public static String A_GIFT_CARDS_TENDERED_CNT = "GiftCardsTenderedCount";
	public static String A_KMRC_TENDERED_CNT = "KMRCTenderedCount";
	public static String A_KOHLS_CHRG_TENDERED_CNT = "KohlsChrgTenderedCount";
	public static String A_NOVUS3PL_TENDERED_CNT = "Novus3PLNovusTenderedCount";
	public static String A_AMEX3PL_TENDERED_CNT = "Amex3PLAmexTenderedCount";
	public static String A_BANK_CARD_TENDERED_CNT = "BankCardTenderedCount";
	public static String A_AMEX_SALE_READ = "AmexSaleRead";
	public static String A_NOVUS_SALE_READ = "NovusSaleRead";
	public static String A_AMNT_CHARGED_TO_GFT_CARD = "AmntChargedToGiftCard";
	public static String A_AMNT_CHARGED_TO_KMRC = "AmntChargedToKMRC";
	public static String A_AMNT_CHARGED_TO_KOHLS_CHRG_CARD = "AmntChargedToKohlsChrgCard";
	public static String A_MASTER_VISA_SALE_READ = "MasterVisaSaleRead";
	public static String A_SALES_TRANS_CNT = "SalesTransactionsCount";
	public static String A_TRANS_WITH_TAXES = "TransactionsWithTaxes";
	public static String A_MERCHANDISE_TOTAL = "MerchandiseTotal";
	public static String A_GIFT_CARDS_ISSUED_CNT = "GiftCardsIssuedCount";
	public static String A_TOTAL_TAX_ON_INVOICE = "TotalTaxOnInvoice";
	public static String A_TOTAL_VALUE_OF_GIFT_CARDS = "TotalValueOfGiftCards";
	public static String E_HARD_TOTALS = "HardTotals";
	public static String E_REGISTER = "Register";
	public static String E_HEADER = "Header";
	public static String E_STORE_NUMBER = "StoreNumber";
	public static String E_TRANS_SALES_DATE = "TransactionSalesDate";
	public static String E_POS_DATE = "POSDate";
	public static String E_TRANS_TIME = "TransactionTime";
	public static String E_TRANS_NUM = "TransactionNumber";
	public static String E_TERMINAL_REG_ID = "TerminalorRegisterID";
	public static String E_CLOSING_MAJOR_SEQ = "ClosingMajorSequence";
	public static String E_GIFT_CARDS_SOLD_READ = "Gift_Cards_Sold_Read";
	public static String E_GIFT_CARDS_TEND_READ = "Gift_Cards_Tendered_Read";
	public static String E_KMRC_TEND_READ = "KMRC_Tendered_Read";
	public static String E_KOHLS_CHG_SALE_READ = "Kohls_Charge_Sale_Read";
	public static String E_BANK_CARD_SALE_READ = "BankCard_Sale_Read";
	public static String E_NOVUS_SALE_READ = "Novus_Sale_Read";
	public static String E_AMEX_SALE_READ = "Amex_Sale_Read";
	public static String E_CNTRL_TOTAL_READ = "Control_Total_Read";
	public static String E_SALES_TAX_CNT = "Sales_Tax_Count";
	public static String E_SALES_CNT = "Sale_Count";
	public static String E_GIFT_CARDS_CNT = "Gift_Cards_Count";
	public static String E_GIFT_CARD_TEND_CNT = "Gift_Card_Tendered_Count";
	public static String E_KMRC_TEND_CNT = "KMRC_Tendered_Count";
	public static String E_KOHLS_CHRG_SALE_CNT = "Kohls_Charge_Sale_Count";
	public static String E_BANK_CARD_SALE_CNT = "BankCard_Sale_Count";
	public static String E_NOVUS_SALE_CNT = "Novus_Sale_Count";
	public static String E_AMEX_SALE_CNT = "Amex_Sale_Count";
	public static String E_CNTRL_TOTAL_CNT = "Control_Total_Count";
	public static String E_SALES_TAX_READ = "Sales_Tax_Read";
	public static String E_SALE_READ = "Sale_Read";
	public static String E_KL_COSA_REP_RUN = "KLCosaReportRun";
	public static String A_LAST_RUN_DATE = "LastRunDate";
	public static String A_TIME_STAMP = "TimeStamp";
	public static String A_INVOICE_NO = "InvoiceNo";
	public static String A_TOTAL_TAX = "TotalTax";
	public static String A_TOTAL_DISCS = "TotalDiscounts";
	public static String A_TOTAL_DISC = "TotalDiscount";
	public static String A_POS_DATE = "PosDate";
	public static String A_UNIT_PRICE = "UnitPrice";
	public static String A_UNIT_COST = "UnitCost";
	public static String A_VISA_CARD_TEND_CNT = "VisaCardTenderedCount";
	public static String A_MSTR_CARD_TEND_CNT = "MasterCardTenderedCount";
	public static String A_NOVUS_TEND_CNT = "NovusTenderedCount";
	public static String A_AMEX_TEND_CNT = "AmexTenderedCount";
	public static String A_AMNT_CHRGD_TO_VISA_CARD = "AmntChargedToVisaCard";
	public static String A_AMNT_CHRGD_TO_MSTR_CARD = "AmntChargedToMasterCard";
	public static String A_AMNT_CHRGD_TO_DISC_CARD = "AmntChargedToDiscoverCard";
	public static String A_AMNT_CHRGD_TO_AMEX_CARD = "AmntChargedToAmexCard";
	public static String A_SHIPPING_CHARGE = "ShippingCharge";
	public static String E_KOHLS_ACCT_YEAR = "KohlsAccountingYear";
	public static String E_KOHLS_ACCT_MONTH = "KohlsAccountingMonth";
	public static String E_KOHLS_ACCT_WEEK = "KohlsAccountingWeek";
	public static String E_DAY_OF_THE_WEEK = "DayoftheWeek";
	
	
	// Net Taxable Amount Changes
	public static String E_LINE_PRICE_INFO = "LinePriceInfo";
	public static String A_RETAIL_PRICE = "RetailPrice";
	public static String A_EXTENDED_PRICE = "ExtendedPrice";	
	public static String A_CHARGES = "Charges";
	public static String A_EXTN_TAXABLE_AMT = "ExtnTaxableAmount";
	

	// DSV Availability Snapshot
	public static String E_AVAILABILITY_SNAPSHOT = "AvailabilitySnapShot";
	public static String E_AVAILABILITY_DETAILS = "AvailabilityDetails";
	public static String A_INV_ORG_CODE = "InventoryOrganizationCode";
	public static String A_EXTN_DSV_COST = "ExtnDSVCost";
	
	// RelC fields	
	public static String A_EXTN_COUPON_PER = "ExtnCouponPercentage";
	public static String A_EXTN_COUPON_AMNT = "ExtnCouponAmount";
	public static String E_INSTRUCTIONS = "Instructions";
	public static String A_NUM_OF_INSTRUCTIONS = "NumberOfInstructions";
	public static String E_INSTRUCTION = "Instruction";
	public static String A_INSTRUCTION_TYPE = "InstructionType";
	public static String A_INSTRUCTION_TEXT = "InstructionText";
	public static String E_LINE_CHARGES = "LineCharges";
	public static String E_LINE_CHARGE = "LineCharge";
	public static String A_BOGO_INSTR = "BOGOInstruction";
	public static String A_GIFT_WRAP_CHARGE = "GiftWrapCharge";
	public static String A_SHIPPING_SURF_CHARGE = "ShippingSurCharge";
	public static final String A_PROMOTION_ID = "PromotionId";
	
	// Msg Generator
	public static final String A_DEPARTMENT = "Department";
	public static final String E_CONTAINER_DETAIL = "ContainerDetail";
	
	public static final String A_BASIC_FREIGHT_CHARGE = "BasicFreightCharge";
	public static final String A_CONT_GROSS_WEIGHT = "ContainerGrossWeight";
	public static final String A_CONT_GROSS_WEIGHT_UOM = "ContainerGrossWeightUOM";
	public static final String A_TRACKING_NO = "TrackingNo";
	public static final String A_TRACK = "Track";
	public static final String A_EXTN_WMS_SHIPMENT_NO = "ExtnWMSShipmentNo";
	public static final String A_EXTN_DSV_LINE_HANDL_CHRG = "ExtnDSVLineHandlingChrg";
	public static final String A_EXTN_DSV_UNIT_COST = "ExtnDSVUnitCost";
	public static final String A_EXTN_DSV_MISC_CHRG = "ExtnDSVMiscChrg";
	public static final String A_EXTN_DSV_INVOICE_NUM = "ExtnDSVInvoiceNumber";
	
	// Release C CR
	public static final String A_EXTN_VENDOR_STYLE_NO = "ExtnVendorStyleNo";
	public static final String A_EXTN_H_OFFER_PERCENT = "ExtnHOfferPercent";
	public static final String A_EXTN_L_COUPON_NO = "ExtnLOfferCouponNo";
	
	// Start -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	public static final String A_GIFT_WRAP_TAX = "GiftWrapTax";
	// End -- Added for 71461,379,000 -- OASIS_SUPPORT 27/04/2012 //
	// Start -- Added for 74501,379,000 -- OASIS_SUPPORT 06/08/2012 //
	public static final String E_SOURCINGAUDIT = "SourcingAudit";
	public static final String E_AVAILABLETOPROMISEINV = "AvailableToPromiseInventory";
	public static final String A_SUPPLIES= "Supplies";
	public static final String A_SUPPLY= "Supply";
	public static final String A_DEMAND= "Demand";
	public static final String A_ORDERREFERENCE="OrderReference";
	// End -- Added for 74501,379,000 -- OASIS_SUPPORT 06/08/2012 //
	// Start -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
	public static final String A_REMAINING_TAX="RemainingTax";
	public static final String A_REMAINING_CHARGE_AMOUNT="RemainingChargeAmount";
	// End -- Added for 22421,379,000-- OASIS_SUPPORT 31/08/2012 //
	// Start -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 //
	//  -- Added for Inventory transfer Management .
	public static final String A_BILL_TO_ID="BillToID";
	public static final String A_DELIVERY_METHOD="DeliveryMethod";
	public static final String A_DIVISION ="Division";
	public static final String A_ORDER_NAME ="OrderName";
	public static final String A_PERSONINFOKEY ="PersonInfoKey";
	public static final String A_ITEM_WEIGHT_UOM ="ItemWeightUOM";
	public static final String A_ITEM_WEIGHT ="ItemWeight";
	public static final String A_EXTN_TO_TOTAL_RELEASE_UNITS ="ExtnTotalReleaseUnits";
	 public static final String A_ADD_LINE_5 = "AddressLine5";
	 public static final String A_ADD_LINE_6 = "AddressLine6";
	 public static final String A_ALTERNATE_EMAIL_ID = "AlternateEmailID";
	 public static final String A_BEEPER = "Beeper";
	 public static final String A_DAY_FAX_NO = "DayFaxNo";
	 public static final String A_EVENING_FAX_NO = "EveningFaxNo";
	 public static final String A_EVENING_PH_NO = "EveningPhone";
	 public static final String A_JOB_TITLE = "JobTitle";
	 public static final String A_PERSON_ID= "PersonID";
	 public static final String A_SUFFIX= "Suffix";
	 public static final String A_MOBILE_PHONE= "MobilePhone";
	 public static final String A_OTHER_PHONE= "OtherPhone";
	 public static final String A_SHIP_TO_ID= "ShipToID";
	 public static final String E_ORDER_RELEASE_lIST= "OrderReleaseList";
	 public static final String A_CHANGE_IN_ORDERED_QTY = "ChangeInOrderedQty";
	 public static final String E_ITEM_DETAILS = "ItemDetails";
	 public static final String E_EXPIRATION_DATE = "ExpirationDate";
	 
	// End -- Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 .
	 
	 //Pawan - Store Pick Slip Print - Drop2 - Start
	 public static final String A_EXTN_SHIP_DEPT="ExtnShipmentDepartment";
	 //
	 //settlement design -Start
	 public static final String A_EXTN_NODE_TYPE = "ExtnNodeType";
	 //settlement design -End
	 
	 
	 // Added for Order Capture drop 2 - Juned S
	 public static final String A_FULFILLMENT_TYPE = "FulfillmentType";
	 public static final String A_EXTN_OCF = "ExtnOCF";
	 public static final String A_SUPRESS_NODE_CAPACITY = "SuppressNodeCapacity";

	 // Added for Order Modification drop 2 - Juned S
	 public static final String A_MODIFICATION_TYPES = "ModificationTypes";
	 public static final String A_MODIFICATION_TYPE = "ModificationType";

	 // Added by Baijayanta
	 public static final String A_EXPIRATION_DATE = "ExtnExpirationDate";

	 //Added for Inventory Updates drop 2 - Ashalatha
	 public static final String STORE_PICK= "StorePick";
	 public static final String ITEM_ID = "ItemID";
	 	 
	 public static final String E_RESPONSE ="Response";
	 public static final String A_ACTION_CODE = "ActionCode";
	 public static final String E_REQUEST = "Request";
	 public static final String E_TOKEN = "Token";
	 public static final String A_CARD_NUMBER = "CardNumber";
	 public static final String A_PROVIDER = "Provider";
	 public static final String A_TRANS_REQ_TYPE = "TransactionRequestType";
	 public static final String A_AMOUNT = "Amount";
	 public static final String A_CARD_EXP_DATE = "CardExpirationDate";
	 public static final String A_TRANSACTION_TYPE = "TransactionType";
	 public static final String A_REQUEST_TYPE = "RequestType";
	 public static final String E_STORE = "Store";
	 public static final String A_TERMINAL_NUMBER = "TerminalNumber";
	 public static final String A_CHANNEL = "Channel";
	 public static final String A_DATE_TIME = "DateTime";
	 public static final String A_AVSRESP = "AVSResp";
	 public static final String A_APPROVAL_NUMBER = "ApprovalNumber";
	 public static final String A_CVVRESP= "CvvResp";
	 public static final String A_CARD_LEVEL = "CardLevel";
	 public static final String A_TENDER_AMOUNT = "TenderAmount";
	 public static final String A_ENTRY_METHOD = "EntryMethod";
	 public static final String A_SVP_INFO = "SVPinNo";
	 public static final String A_REGISTER_NUMBER = "RegisterNumber";
	 public static final String A_REMAINING_AMOUNT_TO_AUTH = "RemainingAmountToAuth";
	 public static final String A_AUTHRESPONSE = "AuthResponse";
	 public static final String	A_AUTH_SOURCE = "AuthSource";
	 public static final String A_REMAINING_BALANCE = "RemainingBalance";
	 public static final String A_APPROVED_AMOUNT = "ApprovedAmount";
	 public static final String A_CVVCODE = "CVVCode";
	 public static final String A_IS_SWIPED = "IsSwiped";

	 
	 //Added for EOD_Settelment drop 3 - Ashalatha
	 
	 public static final String VISA_SALE_AMOUNT =  "AmntChargedToVisaCard";
	 public static final String MASTERCARD_SALE_AMOUNT =  "AmntChargedToMasterCard";
	 public static final String DISCOVERY_SALE_AMOUNT =  "AmntChargedToDiscoverCard";
	 public static final String AMEX_SALE_AMOUNT = "AmntChargedToAmexCard" ;
	 public static final String KOHLS_CHARGE_SALE_AMOUNT = "AmntChargedToKohlsChrgCard";
	 
	 public static final String OMS_EOD_RECORD = "OMSEODRECORD";
	 public static final String FILE_CREATION_DATE = "FILE-CREATION-DATE";
	 public static final String START_DATE_TIME = "START-DATE-TIME";
	 public static final String END_DATE_TIME = "END-DATE-TIME";
	 public static final String TOTAL_VISA_SALE_AMOUNT  ="TOTAL-VISA-SALE-AMOUNT";
	 public static final String TOTAL_VISA_REFUND_AMOUNT = "TOTAL-VISA-REFUND-AMOUNT";
	 public static final String TOTAL_MASTERCARD_SALE_AMOUNT = "TOTAL-MASTERCARD-SALE-AMOUNT";
	 public static final String TOTAL_MASTERCARD_REFUND_AMOUNT =  "TOTAL-MASTERCARD-REFUND-AMOUNT";
	 public static final String TOTAL_DISCOVERY_SALE_AMOUNT= "TOTAL-DISCOVERY-SALE-AMOUNT";
	 public static final String TOTAL_DISCOVERY_REFUND_AMOUNT = "TOTAL-DISCOVERY-REFUND-AMOUNT";
	 public static final String TOTAL_AMEX_SALE_AMOUNT = "TOTAL-AMEX-SALE-AMOUNT";
	 public static final String TOTAL_AMEX_REFUND_AMOUNT = "TOTAL-AMEX-REFUND-AMOUNT";
	 public static final String TOTAL_KOHLS_CHARGE_SALE_AMOUNT = "TOTAL-KOHLS_CHARGE-SALE-AMOUNT";
	 public static final String TOTAL_KOHLS_CHARGE_REFUND_AMOUNT =  "TOTAL-KOHLS_CHARGE-REFUND-AMOUNT";

	 public static final String TRANSASCTION_START_DATE =  "TransactionStartDate";
	 public static final String TRANSASCTION_END_DATE =  "TransactionEndDate";
	 public static final String COMPLEX_QUERY =  "ComplexQuery";
	 public static final String OPERATOR = "Operator";
	 public static final String EXP ="Exp";
	 public static final String QRY_TYPE ="QryType";
	 public static final String LIKE ="LIKE";
	 public static final String LT ="LT";
	 public static final String AND ="AND";

	 public static final String A_EXTN_EXPECTED_SHIPMENT_DATE = "ExtnExpectedShipmentDate";

	 
	 // Added for Alerts drop 4 - Juned S
	 public static final String A_RESOLVE_EXCEPTION = "resolveException";
	 public static final String E_RESOLUTION_DETAILS = "ResolutionDetails";
	 public static final String E_MONITOR_CONSOLIDATION = "MonitorConsolidation";
	 
	 //Added by Baijayanta for Location feed
	 public static final String E_CALENDAR_ELEMENT = "Calendar";
	 public static final String A_CALENDAR_KEY_ATTRIBUTE = "CalendarKey";
	 public static final String A_ORGANIZATION_CODE_ATTRIBUTE = "OrganizationCode";
	 public static final String A_DISTRIBUTION_RULE_ID_ATTRIBUTE ="DistributionRuleId";
	 public static final String A_OWNER_KEY_ATTRIBUTE ="OwnerKey";	
	 public static final String A_SHIP_NODE_KEY_ATTRIBUTE = "ShipnodeKey";	
	 public static final String A_CALENDAR_ID_ATTRIBUTE = "CalendarId";
	 public static final String E_ORGANIZATION_ELEMENT = "Organization";
	 public static final String A_BUSINESS_CALENDAR_KEY_ATTRIBUTE = "BusinessCalendarKey";	

	//Added by Naveen
	 public static final String E_To_STATUSES = "ToStatuses";
	 public static final String E_SHIPNODE = "Shipnode";

	 //Added for Drop 4- Customer Notification
	 public static final String REQ_CANCEL_DATE = "ReqCancelDate"; 
	 public static final String A_EXTN_CUST_NOTIFICATION_SENT = "ExtnCustNotificationSent";
	 public static final String A_CUST_NOTIFICATION_TYPE = "CustNotificationType";

	 // Added for Customer Notification drop 5 - Juned S
	 public static final String A_EXTN_UNIT_PRICE = "ExtnUnitPrice";
	 public static final String A_EXTN_ORDERED_QTY = "ExtnOrderedQty";

	 //Added by Ravi for Customer Notification in Drop 5

	 public static final String A_SHIP_NODE ="ShipNode";
	 public static final String E_SHIPMENT_KEY = "ShipmentKey";
	 public static final String A_EXTN_NOTIFICATION_FLAG = "ExtnNotificationFlag";
	 public static final String A_CUST_NOTIFICATION_SENT = "CustNotificationType";
	 public static final String E_CREATE_ASYNC_REQUEST = "CreateAsyncRequest";
	 public static final String A_IS_SERVICE = "IsService";
	 public static final String A_NOTIF_SERVICE_REQ = "NotificationWebServiceRequest";	

	 //Added for Stop demand update to GIV on create Order for BOPUS Lines
	 public static final String PRIME_LINE_NO = "PrimeLineNo";
	 
	 public static final String A_PICKED_QTY="PickedQty";
	 public static final String A_BACKROOM_PICKED_QUANTITY="BackroomPickedQuantity";
	 public static final String A_PICKING_LOCATION="PickLocation";
	 public static final String A_SHORT_PICK_INDICATOR="ShortPickIndicator";
	 public static final String A_EXTN_SHORT_PICK_INDICATOR="ExtnShortPickIndicator";
	 public static final String A_EXTN_SHORT_PICKED_QTY="ExtnShortPickedQty";
	 public static final String A_EXTN_PICKED_QTY="ExtnPickedQty";
	 public static final String XPATH_SHIPMENT_LINES = "/Shipments/Shipment/ShipmentLines";
	 public static final String A_ASSIGNED_TO_USER = "AssignedToUser";
	 public static final String A_EXTN_MLS_LOC_ID_BARCODE = "ExtnMlsLocationIdBarcode";
	 public static final String A_LOCATION_CONTEXT = "LocationContext";
	 public static final String E_GET_LOCATION_DETAILS_RESPONSE = "GetLocationDetailsResponse";
	 public static final String E_GET_LOCATION_DETAILS_RESULT = "GetLocationDetailsResult";
	 public static final String A_FOUND_MLS_LOCATION = "FoundMLSLocation";
	 public static final String A_EXISTING_LOCATION = "ExistingLocation";
	 
 	 public static final String A_SEQUENCE_NUMBER="SequenceNumber";
	 public static final String A_BANK_RESPONSE_CODE = "BankResponseCode";
	 public static final String A_POSTING_DATE = "PostingDate";
	 public static final String A_OPTIONS ="Options";
	 public static final String A_PS2000Data="PS2000Data";
	 public static final String A_DEPOSIT_DATA = "DepositData";
	 
	 	 //GetOrderDetails Audit
	 public static final String A_ORIG_ORDERED_QUANTITY = "OriginalOrderedQty";
	 public static final String E_SHIPMENT_STORE_EVENTS = "ShipmentStoreEvents";
	 public static final String E_ORDERLINE_TRAN_QTY = "OrderLineTranQuantity";
	 public static final String A_EVENT_TYPE = "EventType";
	 public static final String A_MODIFY_USERID = "Modifyuserid";
	 public static final String A_MODIFY_TS = "Modifyts";
	 public static final String E_ORDER_AUDIT_LIST = "OrderAuditList";
	 public static final String E_ORDER_AUDIT = "OrderAudit";
	 public static final String A_ORDERED_QTY_NV = "OrderedQtyNewValue";
	 public static final String A_ORDERED_QTY_OV = "OrderedQtyOldValue";
	 
	  //MLS Integration 
	 public static final String E_INVENTORY_SUPPLY = "InventorySupply";
	 public static final String A_LOCATION_FILTER_ENUM = "LocationFilterTypeEnum";
	 public static final String A_USER_ID = "UserId";
	 public static final String INVENTORY_SUPPLIES = "InventorySupplies";
	 public static final String E_GET_ITEM_LOCATONS_RESPONSE = "GetItemLocationsResponse";
	 public static final String E_GET_ITEM_LOCATIONS_RESULT = "GetItemLocationsResult";
	 public static final String E_DATA = "Data";
	 public static final String E_ITEMLOCATIONS = "ItemLocations";
	 public static final String E_RESPONSE_ITEM_LOCATIONS = "ResponseItemLocations";
	 public static final String E_BRAND_LABEL = "BrandLabel";
	 public static final String E_BRAND = "Brand";
	 public static final String E_FLOORPAD = "Floorpad";
	 public static final String E_SKU_STATUS = "SkuStatus";
	 public static final String E_SKU_STATUS_DESCRIPTION = "SkuStatusDescription";
	 public static final String E_SALES_FLOOR_LOCATIONS = "SalesFloorLocations";
	 public static final String E_SALES_FLOOR_LOCATION = "SalesFloorLocation";
	 public static final String E_STOCK_ROOM_LOCATIONS = "StockroomLocations";
	 public static final String E_STOCK_ROOM_LOCATION = "StockroomLocation";
	 public static final String E_ESIGNS = "ESigns";
	 public static final String E_ESIGN = "ESign";
	 public static final String A_ESIGN_ID = "ESignId";
	 public static final String E_FLOORPAD_ID = "FloorPadId";
	 public static final String E_FLOORPAD_DESC = "FloorPadDescription";
	 public static final String E_AREA_DESC = "AreaDescription";
	 public static final String E_FULL_DESC_CODE = "FullDescriptionCode";
	 public static final String E_BAR_CODE_NUM = "BarcodeNumber";
	 public static final String E_SECTION_MAX = "SectionMax";
	 public static final String E_QUANTITY = "Quantity";
	 public static final String E_ESIGN_ID = "ESignId";
	 public static final String E_ESIGN_LCASE = "Esign";
	 public static final String E_BOX_NUMBER = "BoxNumber";
	 public static final String A_BOX_ID = "BoxID";
	 
	 public static final String A_ESIGN_LOCATION ="ESignLocation";
	 public static final String A_LOCATION = "Location";
	 public static final String A_BARCODE = "BarCode";
	 
	 public static final String E_SKU = "Sku";
	 public static final String A_SR_SF_ID_DESC = "SrSfIdDesc";
	 public static final String A_SR_ID_BAR_CODE = "SrIdBarcode";
	 public static final String A_SF_ID_ESIGN_ID = "SfIdEsignId";
	 public static final String A_EXTN_MLS_LOC_ID_DESC = "ExtnMlsLocationIdDesc";
	// public static final String A_EXTN_MLS_LOC_ID_BARCODE = "ExtnMlsLocationIdBarcode";
	 public static final String A_EXTN_MLS_LOC_ID_ESIGNID = "ExtnMlsLocationIdEsignID";
	 public static final String A_TRANSACTION_ID = "TransactionId";
	
	public static final String E_ORDER_AUDIT_LEVELS = "OrderAuditLevels";
	public static final String E_ORDER_AUDIT_LEVEL = "OrderAuditLevel";
	public static final String A_MODIFICATION_LEVEL = "ModificationLevel";
	public static final String A_ORDER_AUDIT_DETAILS = "OrderAuditDetails";
	public static final String A_ORDER_AUDIT_DETAIL = "OrderAuditDetail";
	public static final String E_ATTRIBUTES = "Attributes";
	
	 //Added for Interop HTTP servlet 
	 
	 public static final String INTEROP_API_NAME ="InteropApiName";
	 public static final String YFSENV_USERID ="YFSEnvironment.userId";
	 public static final String YFSENV_PWD ="YFSEnvironment.password";
	 public static final String INTEROP_API_DATA ="InteropApiData";
	 public static final String YFSENV_PROGID = "YFSEnvironment.progId";
	 public static final String URL= "URL";
	 public static final String API_SUCCESS = "ApiSuccess";
	 public static final String IS_FLOW =  "IsFlow";
	 public static final String PROXY_SET = "proxySet";
	 public static final String PROXY_HOST ="proxyHost";	
	 
	 public static final String A_CREATE_PROGID = "Createprogid";
	 public static final String A_MODIFY_PROGID = "Modifyprogid";

	 public static final String A_EXTN_FILTER_VAL ="ExtnFilterVal";	 

	 public static final String A_PICKING_FLAG = "PickingFlag";
	
	 public static final String SHIP_NODE ="Shipnode";
	 
	 public static final String A_FROM_DATE = "FromDate";
	 public static final String A_TO_DATE = "ToDate";
	 public static final String A_REQUESTED_SHIPMENT_DATE = "RequestedShipmentDate";
	 public static final String A_EARLIEST_SHIFT_START_TIME = "EarliestShiftStartTime";
	 public static final String A_LAST_SHIFT_END_TIME = "LastShiftEndTime";
	 public static final String A_ORGANIZATION_KEY = "OrganizationKey";
	 
	 
	  //public static final String A_SHIP_NODE ="ShipNode";	 
	  
	 //Drop 8
	 public static final String SHIPMENT_STORE_EVENTS_LIST ="ShipmentStoreEventsList";
	 public static final String SHIPMENT_STORE_EVENTS = "ShipmentStoreEvents";
	 public static final String EXTN_CUST_CNCL_NOTICE ="ExtnCustCancelNotice";   

	 
	 public static final String A_STORE_HRS = "StoreHours";	
	  
	 public static final String CALENDAR_GET_ORGANIZATION_HIERARCHY_TEMPLATE="global/template/api/extn/kohlsBOPUSGetOrganizationHierarchy_Output.xml";
	 public static final String GET_ORGANIZATION_HIERARCHY_API = "getOrganizationHierarchy"; 
	 public static final String API_GET_CALENDAR_DAY_DETAILS_API="getCalendarDayDetails";
	 
	
	 
	//Open API Related parameters
	public static final String E_ITEM_URLS = "ItemURLs";
	public static final String E_ITEM_URL = "ItemURL";
	public static final String E_IMAGES = "images";
	public static final String E_IMAGE = "image";
	public static final String A_URL = "URL";
	public static final String E_URL = "url";
	public static final String E_ALT_TEXT = "altText";
	public static final String A_EXTN_HOLD_LOCATION_ID = "ExtnHoldLocationID";
	public static final String A_EXTN_HOLD_LOCATION_DESC = "ExtnHoldLocationDesc";
	public static final String E_LOCATIONS = "Locations";
	public static final String E_LOCATION = "Location";
	
    public static final String E_SHIPMENT_HOLD_TYPES = "ShipmentHoldTypes";
    public static final String E_SHIPMENT_HOLD_TYPE = "ShipmentHoldType";
	
	//CR - Shipment Expired changes
	public static final String E_ADDITIONAL_DATES = "AdditionalDates";
	public static final String E_ADDITIONAL_DATE = "AdditionalDate";
	public static final String A_DATE_TYPE_ID = "DateTypeId";
	public static final String A_ACTUAL_DATE = "ActualDate";
	
	public static String A_SHIPMENT_INDICATOR = "ExtnShipmentIndicator";
	
	public static final String A_TYPE_PLU = "type";
	 public static final String A_CONTENT = "Content";
	 public static final String A_TYPE_REQUEST = "Type";
	 public static final String E_COMPONENT = "Component";
	 public static final String E_PARAMETERS = "Parameters";
	 public static final String A_TRAN_DATE_TIME = "TRANDATETIME";
	 public static final String A_LOOKUPTYPE = "LookupType";
	 public static final String A_ORDER_LINE_NO = "OrderLineNo";
	 public static final String A_CALLBACK_INDICATOR="CallbackIndicator";
	 public static final String E_RECORD ="RECORD";
	 public static final String A_CALLBACK="CALLBACK";
	 public static final String EXTN_SUSPENDED_PICKUP = "ExtnSuspendedPickup";
	 
	 public static final String E_INVENTORY_LOCATIONS = "InventoryLocations";
	 
	 public static final String A_EXTN_TIER_ALERTS_FLAG = "ExtnTiersAlertFlag";
	 public static final String E_LOCATION_ID = "LocationId";
	 
	 // constants for create order agent
	 public static final String A_CUSTOMER_EMAIL_ID = "CustomerEMailID";
	 public static final String A_DOCUMENT_TYPE = "DocumentType";
	 public static final String E_SUB_LINE_NO = "SubLineNo";
	 public static final String E_PRIME_LINE_NO = "PrimeLineNo";
	 public static final String E_ORDERED_QTY = "OrderedQty";
	 public static final String E_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	 public static final String E_DELIVERY_METHOD = "DeliveryMethod";
	 public static final String A_EXTN_TAX_AMOUNT = "ExtnTaxAmount";
	 public static final String A_LIST_PRICE = "ListPrice";
	 public static final String A_IS_PRICE_LOCKED = "IsPriceLocked";
	 public static final String E_ORDER_LINE_RESERVATIONS = "OrderLineReservations";
	 public static final String E_ORDER_LINE_RESERVATION = "OrderLineReservation";
	 public static final String E_DEMAND_TYPE = "DemandType";
	 public static final String A_CREDIT_CARD_EXPIRY__DATE = "CreditCardExpDate";
	 public static final String A_AUTH_RETURN_FLAG = "AuthReturnFlag";
	 public static final String A_AUTH_ID = "AuthorizationID";
	 public static final String A_HOLD_BOOK = "HoldAgainstBook";
	 
		//ManagerOverrides - Drop 12 UI 2.0
	 
	 public static final String VALIDATION = "Validation";
	 public static final String CALLING_ORG_CODE = "CallingOrganizationCode";
	 public static final String ORG_CODE = "OrganizationCode";
	 public static final String DOMAIN = "Domain";
	 public static final String VALIDATION_ID = "ValidationID";
	 public static final String APP_CTX_INFO = "AppContextInfo";
	 public static final String CHANNEL = "Channel";
	 public static final String ORDER_APPR_RULE = "OrderApprovalRule";
	 public static final String DOCUMENT_TYPE = "DocumentType";
	 public static final String RULE_ID = "RuleID";
	 public static final String INPUT = "Input";
	 public static final String SHIPMENT = "Shipment";
	 public static final String SHIPMENT_ID = "ShipmentId";
	 public static final String GIVEN_FNAME = "givenFname";
	 public static final String DATA_CAPTURE_FNAME = "dataCaptureFname";
	 public static final String GIVEN_LNAME = "givenLname";
	 public static final String DATA_CAPTURE_LNAME = "dataCaptureLname";
	 public static final String VALIDATION_RULE = "ValidationRule";
	 public static final String TRANS_VIOLATION_REF_LIST = "TransactionViolationReferenceList";
	 public static final String TRANS_VIOLATION_REF = "TransactionViolationReference";
	 public static final String REFERENCE_NAME = "ReferenceName";
	 public static final String REFERENCE_VALUE = "ReferenceValue";
	 public static final String TRANSACTION_INFO = "TransactionInfo";
	 public static final String ORDER_HEADER_KEY = "OrderHeaderKey";
	 public static final String TRANS_INFO_ID = "TransactionInfoID";
	 public static final String TRANS_VIOLATION_LIST = "TransactionViolationList";
	 public static final String NO_OF_VIOLATIONS = "NumberOfViolations";
	 public static final String RET_CODE = "returnCode";
	 public static final String LOGIN = "Login";
	 public static final String USERNAME = "UserName";
	 public static final String LOGIN_ID = "LoginID";
	 public static final String PASSWORD = "Password";
	 public static final String USER = "User";
	 public static final String ERR_CODE = "errorCode";
	 public static final String APP_RES ="ApproverResponse";
	 public static final String APP_USER_ID ="ApproverUserID";
	 public static final String VALIDATION_LIST ="ValidationList";
	 public static final String VALIDATION_KEY ="ValidationKey";
	 public static final String TRANS_VIOLATION ="TransactionViolation";
	 public static final String APP_RULE_ID ="ApprovalRuleID";
	 public static final String APPROVED ="Approved";
	
	 //MLS Elements and attributes ~ Adam Dunmars
	 public static final String E_UPCS = "Upcs";
	 public static final String E_UPC = "Upc";
	 public static final String ID = "Id";
	 public static final String ID_TYPE = "IdType";
	 
	 //OMS Extensions Elements and attributes ~ Adam Dunmars
	 public static final String A_EXTN_USERNAME = "ExtnUsername";
	 
	 //OOB OMS Elements and attributes ~ Adam Dunmars
	 public static final String E_USER_LIST = "UserList";
	 public static final String E_USER = "User";
	 public static final String A_USERNAME = "Username";
	 public static final String A_LOGIN_ID = "Loginid";
	 public static final String A_ASSIGNED_TO_USER_ID = "AssignedToUserId";
	 
	 //BOPUS UI Elements and attributes ~ Adam Dunmars
	 public static final String A_KOHLS_UPC = "KohlsUPC";
	 
	 //Defect - 581
	 public static final String IS_INVOICE_CREATION = "isInvoceCreation";
}	