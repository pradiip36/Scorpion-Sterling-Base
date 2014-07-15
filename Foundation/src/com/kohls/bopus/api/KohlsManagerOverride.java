package com.kohls.bopus.api;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsManagerOverride {
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsManagerOverride.class.getName());
	

	/*
	 * <Validation AllowTransaction="" CallingOrganizationCode="DEFAULT" Domain="ORDER" ValidationID="KOHLS_CUST_VERIFICATION">
			<AppContextInfo Channel="null"/>
			<OrderApprovalRule DocumentType="0001" EvaluationDate="" RuleID="NameValidation" /> 
			<Input>
				<Shipment ShipmentId="sh0001" givenFname="Alex" dataCaptureFname="Alexis" givenLname="Davidsn" dataCaptureLname="Davidson"/>
			</Input>  
			<ValidationRule DocumentType="0001"/>
			<TransactionInfo OrderHeaderKey="" TransactionInfoID="" /> 
			<TransactionViolationReferenceList>
				<TransactionViolationReference ReferenceName="ShipmentId" ReferenceValue="sh0001"/> 
			</TransactionViolationReferenceList>
	 * </Validation>
	 * @param env
	 * @param inDoc
	 * @return
	 */
	public Document checkManagerOverride(YFSEnvironment env, Document inDoc){
	    log.debug("KohlsManagerOverride:checkManagerOverride:inDoc:"+SCXmlUtil.getString(inDoc));
		Document outDoc = null;		
	    try {
			Document apiInpDoc = createCheckMgrInputdoc(inDoc);
			log.debug("KohlsManagerOverride:checkManagerOverride:apiInpDoc"+SCXmlUtil.getString(apiInpDoc));
			Document apiOutDoc = KohlsCommonUtil.invokeAPI(env, KohlsConstants.CHECK_OVERRIDE_RULE_TEMPLATE,KohlsConstants.CHECK_OVERRIDE_RULE, apiInpDoc);
			log.debug("KohlsManagerOverride:checkManagerOverride:apiOutDoc"+SCXmlUtil.getString(apiOutDoc));
			outDoc = createCheckMgrOutputdoc(apiOutDoc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method KohlsManagerOverride:checkManagerOverride() returned Error."+e);
		}
		log.debug("KohlsManagerOverride:checkManagerOverride:outDoc"+SCXmlUtil.getString(outDoc));
		return outDoc;
	
	}
	

	private Document createCheckMgrInputdoc(Document inDoc) {
		if (YFCCommon.isVoid(inDoc)){
			throw new YFSException(
					"Input xml in KohlsManagerOverride:checkManagerOverride() is null. "+SCXmlUtil.getString(inDoc));
		}
		
		//Element shipmentEle = SCXmlUtil.getChildElement(inDoc.getDocumentElement(),KohlsXMLLiterals.SHIPMENT);
		
		Element shipmentEle = inDoc.getDocumentElement();
			 
		String shipmentId = null;
		String givenFname = null;
		String dcFname = null;
		String givenLname = null;
		String dcLname = null;
					 
		if(!YFCCommon.isVoid(shipmentEle)){
			shipmentId = shipmentEle.getAttribute(KohlsXMLLiterals.SHIPMENT_ID);
			givenFname = shipmentEle.getAttribute(KohlsXMLLiterals.GIVEN_FNAME);
			dcFname = shipmentEle.getAttribute(KohlsXMLLiterals.DATA_CAPTURE_FNAME);
			givenLname = shipmentEle.getAttribute(KohlsXMLLiterals.GIVEN_LNAME);
			dcLname = shipmentEle.getAttribute(KohlsXMLLiterals.DATA_CAPTURE_LNAME);
		}
		else{
			if (shipmentId == null || givenFname == null ||dcFname == null ||givenLname == null ||dcLname == null){
				throw new YFSException(
						"Element or attributes in input xml in KohlsManagerOverride:checkManagerOverride() are null."
								+ "Expected <Shipment ShipmentId=\"sh0001\" givenFname=\"Alex\" dataCaptureFname=\"Alexis\" givenLname=\"Davidsn\" "
								+ "dataCaptureLname=\"Davidson\"/> but was"+SCXmlUtil.getString(inDoc));
			}
		}
					
		Document apiInpDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.VALIDATION);
		Element rootEle= apiInpDoc.getDocumentElement();
		rootEle.setAttribute(KohlsXMLLiterals.CALLING_ORG_CODE, KohlsConstants.ORG_CODE_VAL);
		rootEle.setAttribute(KohlsXMLLiterals.DOMAIN, KohlsConstants.DOMAIN_VAL);
		rootEle.setAttribute(KohlsXMLLiterals.VALIDATION_ID, KohlsConstants.VALIDATION_ID_VAL);
		Element appCtxEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.APP_CTX_INFO);
		appCtxEle.setAttribute(KohlsXMLLiterals.CHANNEL, KohlsConstants.CHANNEL_VAL);
		Element orderApprovalEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.ORDER_APPR_RULE);
		orderApprovalEle.setAttribute(KohlsXMLLiterals.DOCUMENT_TYPE, KohlsConstants.DOCUMENT_TYPE_VAL);
		orderApprovalEle.setAttribute(KohlsXMLLiterals.RULE_ID, KohlsConstants.RULE_ID_VAL);
		
		Document inputDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.E_INPUT);
		SCXmlUtil.importElement(inputDoc.getDocumentElement(),shipmentEle);
		SCXmlUtil.importElement(rootEle,inputDoc.getDocumentElement());
		
		Element validationRuleEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.VALIDATION_RULE);
		validationRuleEle.setAttribute(KohlsXMLLiterals.DOCUMENT_TYPE, KohlsConstants.DOCUMENT_TYPE_VAL);
		Element violationRefListEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.TRANS_VIOLATION_REF_LIST);
		Element violationRefEle = SCXmlUtil.createChild(violationRefListEle, KohlsXMLLiterals.TRANS_VIOLATION_REF);
		violationRefEle.setAttribute(KohlsXMLLiterals.REFERENCE_NAME, KohlsXMLLiterals.SHIPMENT_ID);
		violationRefEle.setAttribute(KohlsXMLLiterals.REFERENCE_VALUE, shipmentId);
		Element transInfoEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.TRANSACTION_INFO);
		transInfoEle.setAttribute(KohlsXMLLiterals.ORDER_HEADER_KEY,"");
		transInfoEle.setAttribute(KohlsXMLLiterals.TRANS_INFO_ID,"");
		return apiInpDoc;
	}

	/**
	 * Converts the below xml
	 * <Validation CanApprovalBePostponed="N" ConfigurationGroup="StoreConfigOrder" Description="Checks the customer verification details for kohls" Domain="ORDER" IsSystemDefined="Y" OrganizationCode="DEFAULT" ValidationID="KOHLS_CUST_VERIFICATION" ValidationKey="KOHLS_CUST_VERIFICATION" ViolationEntity="Order">
			<TransactionViolationList AllowTransaction="N" NumberOfRulesEvaluated="1" NumberOfViolations="1">
				<TransactionViolation ApprovalRuleID="NameValidation" DocumentType="0001" IsApprovalRequired="Y" Operation="Create" OrganizationCode="DEFAULT" RefreshReferences="N" Status="1100" TransactionInfoID="100000002">
					<TransactionApprovalStatusList TotalNumberOfRecords="1">
						<TransactionApprovalStatus ApproverSequence="1" IsMandatory="Y" Status="1100"/>
					</TransactionApprovalStatusList>
					<TransactionViolationReferenceList TotalNumberOfRecords="1">
						<TransactionViolationReference Operation="" ReferenceName="ShipmentId" ReferenceValue="sh0001" TransactionViolationReferenceKey="20140423141841190868"/>
					</TransactionViolationReferenceList>
					<TransactionInfo OrderHeaderKey="" TransactionInfoID="100000002"/>
						<TransactionApproverList TotalNumberOfRecords="1">
							<TransactionApprover ApproverGroupKey="20140416160449187101" ApproverUserGroupID="WSC_STORE_MANAGER" ApproverUserTeam="KOHLS_MGR_TEAM"/>
						</TransactionApproverList>
					</TransactionViolation>
			</TransactionViolationList>
		</Validation>
		into 
		<Validation returnCode="Y" TransactionInfoID="100000002"/> or <Validation returnCode="N"/>
	 * @param apiOutDoc
	 * @return
	 */
	private Document createCheckMgrOutputdoc(Document apiOutDoc) {
		if (YFCCommon.isVoid(apiOutDoc)){
			throw new YFSException(
					"Output xml in KohlsManagerOverride:checkManagerOverride() is null. "+SCXmlUtil.getString(apiOutDoc));
		}
		
		Element validationEle = apiOutDoc.getDocumentElement();
		
		String noOfViolations = null;
		String transInfoID = null;
		Document outDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.VALIDATION);
		
		if(!YFCCommon.isVoid(validationEle)){
			Element transViolationListEle = SCXmlUtil.getChildElement(validationEle, KohlsXMLLiterals.TRANS_VIOLATION_LIST);
			if(YFCCommon.isVoid(transViolationListEle)){
				(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.RET_CODE, KohlsConstants.RET_CODE_N);
				return outDoc;
			}
			noOfViolations = transViolationListEle.getAttribute(KohlsXMLLiterals.NO_OF_VIOLATIONS);
			if (!YFCCommon.isVoid(noOfViolations) && (KohlsConstants.NO_OF_VIOLATIONS_VAL).equals(noOfViolations)){
				(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.RET_CODE, KohlsConstants.RET_CODE_N);
				return outDoc;
			}
			ArrayList transViolationEle = SCXmlUtil.getChildren(transViolationListEle, KohlsXMLLiterals.TRANS_VIOLATION);
			Element transInfoEle = SCXmlUtil.getChildElement((Element)transViolationEle.get(0), KohlsXMLLiterals.TRANSACTION_INFO);
			transInfoID = transInfoEle.getAttribute(KohlsXMLLiterals.TRANS_INFO_ID);
			(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.RET_CODE, KohlsConstants.RET_CODE_Y);
			(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.TRANS_INFO_ID, transInfoID);
		}
		return outDoc;
	}

	
	
	public Document saveManagerApproval(YFSEnvironment env, Document inDoc){
	    log.debug("KohlsManagerOverride:saveManagerApproval:inDoc:"+SCXmlUtil.getString(inDoc));
		Document outDoc = null;	
		Document apiInpDoc = null;
		Document apiOutDoc = null;
		String tokenId = null;
		String userId = null;
	    try {
				log.debug("KohlsManagerOverride:saveManagerApproval:login:apiInpDoc"+SCXmlUtil.getString(apiInpDoc));
				apiInpDoc = createMgrLoginInputdoc(inDoc);
				apiOutDoc = KohlsCommonUtil.invokeAPI(env, "", KohlsConstants.MGR_LOGIN, apiInpDoc);
				userId=env.getUserId();
				log.debug("KohlsManagerOverride:saveManagerApproval:login:apiOutDoc"+SCXmlUtil.getString(apiOutDoc));
			} catch (Exception e) {
				outDoc = createMgrLoginOutputdoc(apiOutDoc, e);
				return outDoc;	
			}
	    try{
				apiInpDoc = createRecordApprovalsInputdoc(inDoc,userId);
				log.debug("KohlsManagerOverride:saveManagerApproval:recordApprovals:apiInpDoc"+SCXmlUtil.getString(apiInpDoc));
				apiOutDoc = KohlsCommonUtil.invokeAPI(env, "", KohlsConstants.RECORD_APPROVALS, apiInpDoc);
				log.debug("KohlsManagerOverride:saveManagerApproval:recordApprovals:apiOutDoc"+SCXmlUtil.getString(apiOutDoc));
				outDoc = createRecordApprovalsOutputdoc(apiOutDoc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method KohlsManagerOverride:saveManagerApproval() returned Error."+e);
		}
		log.debug("KohlsManagerOverride:saveManagerApproval:outDoc"+SCXmlUtil.getString(outDoc));
		return outDoc;	
	}

	/**
	 * Converts the below xml
	 *		<ApproverResponse ApproverUserID="admin" OrganizationCode="">
				<ValidationList TotalNumberOfRecords="1">
					<Validation CanApprovalBePostponed="N" Domain="ORDER" OrganizationCode="DEFAULT" ValidationID="KOHLS_CUST_VERIFICATION" ValidationKey="KOHLS_CUST_VERIFICATION" ViolationEntity="Order">
						<TransactionViolationList TotalNumberOfRecords="1">
							<TransactionViolation ApprovalRuleID="NameValidation" DocumentType="0001" OrganizationCode="DEFAULT" SequenceNumber="1" Status="1200" TransactionInfoID="100000002" TransactionViolationKey="20140423152407191061">
								<TransactionApprovalStatusList TotalNumberOfRecords="1">
									<TransactionApprovalStatus ApproverSequence="1" ApproverUserID="admin" IsMandatory="Y" Status="1200" TransactionApproverKey="20140423152407191064"/>
								</TransactionApprovalStatusList>
								<TransactionViolationReferenceList TotalNumberOfRecords="1">
									<TransactionViolationReference ReferenceName="ShipmentId" ReferenceValue="sh0001" TransactionViolationReferenceKey="20140423152407191062"/>
								</TransactionViolationReferenceList>
								<TransactionInfo OrderHeaderKey=""/>
								<TransactionApproverList TotalNumberOfRecords="1">
									<TransactionApprover ApproverGroupKey="20140416160449187101" ApproverUserGroupID="WSC_STORE_MANAGER" ApproverUserTeam="KOHLS_MGR_TEAM" TransactionApproverKey="20140423152407191064"/>
								</TransactionApproverList>
							</TransactionViolation>
						</TransactionViolationList>
					</Validation>
				</ValidationList>
			</ApproverResponse>
	 *
	 *into 
		<User returnCode="Y" /> OR <User returnCode="N" errorCode="ERROR_MESSAGE"/>
	 * @param apiOutDoc
	 * @return
	 */
	private Document createRecordApprovalsOutputdoc(Document apiOutDoc) {
		Document outDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.USER);
		(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.RET_CODE, KohlsConstants.RET_CODE_Y);
		return outDoc;
	}


	/**
	 * <ApproverResponse ApproverUserID="admin">
		<AppContextInfo Channel="null"/>
 		<ValidationList>
			<Validation Domain="ORDER" OrganizationCode="DEFAULT" ValidationID="KOHLS_CUST_VERIFICATION" ValidationKey="KOHLS_CUST_VERIFICATION">
				<TransactionViolationList>
					<TransactionViolation ApprovalRuleID="NameValidation" Approved="Y" DocumentType="0001" OrganizationCode="DEFAULT" TransactionInfoID="100000002" Domain="ORDER">
						<TransactionInfo OrderHeaderKey="" /> 
						<TransactionViolationReferenceList>
							<TransactionViolationReference ReferenceName="ShipmentId" ReferenceValue="sh0001"/> 
						</TransactionViolationReferenceList>
					</TransactionViolation>
				</TransactionViolationList>
			</Validation>
		</ValidationList>
		</ApproverResponse>
	 * @param userId 
	 * @param env
	 * @param doc
	 * @return
	 */
	private Document createRecordApprovalsInputdoc(Document inDoc, String userId) {

		if (YFCCommon.isVoid(inDoc)){
			throw new YFSException(
					"Input xml in KohlsManagerOverride:saveManagerApproval() is null. "+SCXmlUtil.getString(inDoc));
		}
		// Get attributes from input element
		Element loginEle = inDoc.getDocumentElement();		 
		String username = loginEle.getAttribute(KohlsXMLLiterals.USERNAME);
		String transInfoID = loginEle.getAttribute(KohlsXMLLiterals.TRANS_INFO_ID);
		String shipmentId = loginEle.getAttribute(KohlsXMLLiterals.SHIPMENT_ID);
					
		// Form the new input xml for recordApprovals api call using attributes from input doc
		Document apiInpDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.APP_RES);
		Element rootEle= apiInpDoc.getDocumentElement();
		rootEle.setAttribute(KohlsXMLLiterals.APP_USER_ID, userId);
		Element appCtxEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.APP_CTX_INFO);
		appCtxEle.setAttribute(KohlsXMLLiterals.CHANNEL, KohlsConstants.CHANNEL_VAL);
		Element validationListEle = SCXmlUtil.createChild(rootEle, KohlsXMLLiterals.VALIDATION_LIST);
		Element validationEle = SCXmlUtil.createChild(validationListEle, KohlsXMLLiterals.VALIDATION);
		validationEle.setAttribute(KohlsXMLLiterals.ORG_CODE, KohlsConstants.ORG_CODE_VAL);
		validationEle.setAttribute(KohlsXMLLiterals.DOMAIN, KohlsConstants.DOMAIN_VAL);
		validationEle.setAttribute(KohlsXMLLiterals.VALIDATION_ID, KohlsConstants.VALIDATION_ID_VAL);
		validationEle.setAttribute(KohlsXMLLiterals.VALIDATION_KEY, KohlsConstants.VALIDATION_ID_VAL);
		Element transViolationListEle = SCXmlUtil.createChild(validationEle, KohlsXMLLiterals.TRANS_VIOLATION_LIST);
		Element transViolationEle = SCXmlUtil.createChild(transViolationListEle, KohlsXMLLiterals.TRANS_VIOLATION);
		transViolationEle.setAttribute(KohlsXMLLiterals.APP_RULE_ID, KohlsConstants.RULE_ID_VAL);
		transViolationEle.setAttribute(KohlsXMLLiterals.APPROVED, KohlsConstants.APPROVED_Y);
		transViolationEle.setAttribute(KohlsXMLLiterals.DOCUMENT_TYPE, KohlsConstants.DOCUMENT_TYPE_VAL);
		transViolationEle.setAttribute(KohlsXMLLiterals.ORG_CODE, KohlsConstants.ORG_CODE_VAL);
		transViolationEle.setAttribute(KohlsXMLLiterals.TRANSACTION_INFO, transInfoID);
		transViolationEle.setAttribute(KohlsXMLLiterals.DOMAIN, KohlsConstants.DOMAIN_VAL);
		Element violationRefListEle = SCXmlUtil.createChild(transViolationEle, KohlsXMLLiterals.TRANS_VIOLATION_REF_LIST);
		Element violationRefEle = SCXmlUtil.createChild(violationRefListEle, KohlsXMLLiterals.TRANS_VIOLATION_REF);
		violationRefEle.setAttribute(KohlsXMLLiterals.REFERENCE_NAME, KohlsXMLLiterals.SHIPMENT_ID);
		violationRefEle.setAttribute(KohlsXMLLiterals.REFERENCE_VALUE, shipmentId);
		return apiInpDoc;
	}


	private Document createMgrLoginOutputdoc(Document apiOutDoc, Exception e) {
		Document outDoc = SCXmlUtil.createDocument(KohlsXMLLiterals.USER);
		(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.RET_CODE, KohlsConstants.RET_CODE_N);
		(outDoc.getDocumentElement()).setAttribute(KohlsXMLLiterals.ERR_CODE, "Invalid Credentials");
		return outDoc;
	}
	
	private Document createMgrLoginInputdoc(Document inDoc) {
		if (YFCCommon.isVoid(inDoc)){
			throw new YFSException(
					"Input xml in KohlsManagerOverride:saveManagerApproval() is null. "+SCXmlUtil.getString(inDoc));
		}
		Element rootEle = inDoc.getDocumentElement();
		if (YFCCommon.isVoid(rootEle.getAttribute(KohlsXMLLiterals.USERNAME)) || YFCCommon.isVoid(rootEle.getAttribute(KohlsXMLLiterals.PASSWORD)) || YFCCommon.isVoid(rootEle.getAttribute(KohlsXMLLiterals.TRANS_INFO_ID))){
			throw new YFSException(
					"Element or attributes in input xml in KohlsManagerOverride:saveManagerApproval() are null."
				  + "Expected <Login UserName=\"person1\" Password=\"password\" TransactionInfoID=\"1000232233\"/> but was"+SCXmlUtil.getString(inDoc));
		}		
		rootEle.setAttribute(KohlsXMLLiterals.LOGIN_ID, SCXmlUtil.getAttribute(rootEle, KohlsXMLLiterals.USERNAME));
		
		return inDoc;
	}


}
