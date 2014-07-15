# Property to handle logging.

# This property points to the location of the log4j configuration xml file.
yfs.log4j.configuration=/resources/kohls.log4jconfig.custom.xml

# This property sets the time to wait on receiving eof
yfs.yfs.eof.wait.time=300

# This property constant sets the path to generate mismatch report
yfs.MismatchReport=/logs/stress/of/of_a

######### Agent Server JMS Configs ########
yfs.KOHLS_AGENT_PROVIDER_URL=t3://xloms-ofmc001m.tst.kohls.com:15882
yfs.KOHLS_AGENT_QCF=TEST_QCF

######### Integration Server JMS Configs ########
yfs.KOHLS_INT_PROVIDER_URL=t3://xloms-ofmc001m.tst.kohls.com:15882
yfs.KOHLS_INT_AGENT_QCF=TEST_QCF

yfs.interopservlet.security.enabled=false
yfs.api.security.token.enabled=false
yfs.api.security.mode=DEBUG
yfs.api.security.enabled=N

############ Agent Queue Names #########
yfs.KOHLS_INT_OMS_SA_REL_AGT_Q=KOHLS_INT_OMS_SA_REL_AGT_Q
yfs.KOHLS_INT_OMS_REL_AGT_Q=KOHLS_INT_OMS_REL_AGT_Q
yfs.KOHLS_INT_OMS_ORDER_PRG=KOHLS_INT_OMS_ORDER_PRG
yfs.KOHLS_INT_OMS_SHIPMENT_PRG_Q=KOHLS_INT_OMS_SHIPMENT_PRG_Q
yfs.SYS_INT_SCHEDULE_ORDER_Q=SYS_INT_SCHEDULE_ORDER_Q
yfs.SYS_INT_RELEASE_ORDER_Q=SYS_INT_RELEASE_ORDER_Q
yfs.SYS_INT_PAYMENT_EXECUTION_Q=SYS_INT_PAYMENT_EXECUTION_Q
yfs.SYS_INT_PAYMENT_COLLECTION_Q=SYS_INT_PAYMENT_COLLECTION_Q
yfs.SYS_INT_CLOSE_ORDER_Q=SYS_INT_CLOSE_ORDER_Q
yfs.SYS_INT_CLOSE_SHIPMENT_Q=SYS_INT_CLOSE_SHIPMENT_Q
yfs.SYS_INT_PO_RELEASE_ORDER_Q=SYS_INT_PO_RELEASE_ORDER_Q
yfs.KOHLS_IN_OMS_DSV_INV_Q=KOHLS_IN_OMS_DSV_INV_Q
yfs.KOHLS_IN_OMS_DSV_INV_Q=KOHLS_IN_OMS_DSV_INV_Q
yfs.KOHLS_OUT_OMS_CANCEL_MSG_TO_EDW_Q=KOHLS_OUT_OMS_CANCEL_MSG_TO_EDW_Q
yfs.KOHLS_IN_OMS_VENDOR_Q=KOHLS_IN_OMS_VENDOR_Q
yfs.KOHLS_IN_OMS_809INVADJ_Q=KOHLS_IN_OMS_809INVADJ_Q
yfs.KOHLS_IN_OMS_873INVADJ_Q=KOHLS_IN_OMS_873INVADJ_Q
yfs.KOHLS_IN_OMS_819INVADJ_Q=KOHLS_IN_OMS_819INVADJ_Q

############ Integration Queue Names #########

yfs.KOHLS_OUT_SEND_SHIP_CONFIRMATION_MSG_TO_ECOMM_Q=OF.OUTB.SHIP_CNFRM.QR
yfs.KOHLS_IN_OMS_SHIPMENT_CONFIRMATION_Q=OF.INB.SHIP_CNFRM.Q
yfs.KOHLS_IN_OMS_CREATE_ORDER_Q=OF.INB.CRT_ORDER.Q
yfs.KOHLS_IN_OMS_INVSYNC_Q=OF.INB.INV_SYNC.Q
yfs.KOHLS_IN_OMS_ITEM_Q=OF.ITEM.Q
yfs.KOHLS_COLLECT_INVENTORY_MISMATCH_Q=OF.OUTB.OMS_SYNC_MONITOR.Q

#Both Relase and Ship Confirmation will go to same queue
yfs.KOHLS_OUT_OMS_RELEASE_TO_WMoS_Q=OF.OUTB.ORD_REL.Q
yfs.KOHLS_OUT_OMS_SHIP_ALONE_MSG_TO_WMoS_Q=OF.OUTB.ORD_REL.Q
yfs.KOHLS_GIFT_CARD_REFUND_Q=KOHLS_GIFT_CARD_REFUND_Q
yfs.KOHLS_IN_OMS_REPRICE_INFO_Q=KOHLS_IN_OMS_REPRICE_INFO_Q

# All status messages will goto same queue
yfs.KOHLS_OUT_OMS_SEND_STATUS_MSG_TO_ECOMM_Q=OF.OUTB.ORD_STATUS.Q
yfs.KOHLS_OUT_OMS_SEND_SHIP_ALONE_STATUS_MSG_TO_ECOMM_Q=OF.OUTB.ORD_STATUS.Q
yfs.KOHLS_OUT_OMS_CANCEL_MSG_TO_ECOMM_Q=OF.OUTB.ORD_STATUS.Q
yfs.KOHLS_LOAD_INVENTORY_MISMATCH_Q=OF.OUTB.LOAD_INVENTORY_MISMATCH_Q

dbclassCache.YFS_ITEM_OPTION.enabled=false
dbclassCache.YFS_ITEM_PRICE_SET.enabled=false
dbclassCache.YFS_ITEM_PRICE_SET_DTL.enabled=false
dbclassCache.YFS_ITEM_SERVICE_ASSC_SKILL.enabled=false
dbclassCache.YFS_ITEM_SERVICE_ASSOC.enabled=false
dbclassCache.YFS_ITEM_SERVICE_SKILL.enabled=false
dbclassCache.YFS_ITEM_SHIP_NODE.enabled=false
dbclassCache.YFS_ITEM_STATUS_RULES.enabled=false
dbclassCache.YFS_ITEM_TAG.enabled=false
dbclassCache.YFS_ITEM_UOM.enabled=false
dbclassCache.YFS_ITEM_UOM_MASTER.enabled=false
dbclassCache.YFS_ITEM.enabled=false
dbclassCache.YFS_ITEM_ALIAS.enabled=false
dbclassCache.YFS_ITEM_ATTR.enabled=false
dbclassCache.YFS_ITEM_ATTR_GROUP.enabled=false
dbclassCache.YFS_ITEM_ATTR_GROUP_LOCALE.enabled=false
dbclassCache.YFS_ITEM_ATTR_GROUP_TYPE.enabled=false
dbclassCache.YFS_ITEM_ATTR_GRP_TYPE_LOCALE.enabled=false
dbclassCache.YFS_ITEM_CNSPTN_PTTN_DTL.enabled=false
dbclassCache.YFS_ITEM_CNSPTN_PTTN_HDR.enabled=false
dbclassCache.YFS_ITEM_COUNT_STATE.enabled=false
dbclassCache.YFS_ITEM_EXCLUSION.enabled=false
dbclassCache.YFS_ITEM_INSTRUCTION.enabled=false
dbclassCache.YFS_ITEM_INSTRUCTION_CODE.enabled=false
dbclassCache.YFS_ITEM_LOCALE.enabled=false
dbclassCache.YFS_ITEM_NODE_DEFN.enabled=false

####### SSO properties ######
#sso.header.username=iv-user
#sso.webseal.link=http://intra-qa.tst.kohls.com/omsqa/smcfs/console/home.detail
yfs.yfs.security.singlesignon.enabled=N
#yfs.yfs.login.singlesignon.class=com.kohls.common.util.KohlsSSOManager
yfs.yfs.login.singlesignon.checkuser=N

####### JMS Queue parameter #####
yfs.yantra.jms.receive.timeout=10000

####### Kohls Job Path #######
yfs.kohls_job_path=/logs/stress/of_a

######Purge Queues in Release C ###########

yfs.KOHLS_INT_OMS_INV_AUDIT_PRG=OfInv_Audit_PrgQ
yfs.KOHLS_INT_ITEM_AUDIT_PRG=OfItem_Audit_PrgQ
yfs.KOHLS_INT_ORG_AUDIT_PRG=OfOrg_Audit_PrgQ
yfs.KOHLS_INT_PER_INFO_PRG=OfPer_Info_PrgQ
yfs.KOHLS_INT_PER_INFO_HIS_PRG=OfPer_Info_PrgQ
yfs.KOHLS_INT_USER_ACT_PRG=OfUser_Act_PrgQ
yfs.KOHLS_INT_USER_ACT_AUDIT_PRG=OfUser_Act_Audit_PrgQ
yfs.KOHLS_INT_AUDIT_PRG=OfInt_Audit_PrgQ
yfs.KOHLS_INT_INBOX_PRG=OfInt_Inbox_PrgQ
yfs.KOHLS_INT_OMS_HIST_ORDER_PRG=OfPo_HistprgQ
yfs.KOHLS_INT_OMS_SHIPMENT_HIST_PRG_Q=OfShipment_HistprgQ
