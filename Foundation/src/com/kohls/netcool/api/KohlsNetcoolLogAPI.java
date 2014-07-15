package com.kohls.netcool.api;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class is used for logging the error data from alert queues
 * 
 * @author Rohan Bhandary
 * 
 *         Copyright 2010, Sterling Commerce, Inc. All rights reserved.
 * */

public class KohlsNetcoolLogAPI implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsConstant.NET_LOG_APPENDER
			+ KohlsNetcoolLogAPI.class);
	String strLow;
	String strMedium;
	String strHigh;
	Properties args = new Properties();

	/**
	 * constructor to initialize api
	 * 
	 * @throws YIFClientCreationException
	 *             e
	 */
	public KohlsNetcoolLogAPI() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	/**
	 * Method to log the raised alerts
	 * 
	 * @param env
	 *            YFSEnvironment
	 * @param inXML
	 *            Document
	 * @throws Exception
	 *             e
	 */
	public void createNetcoolLog(YFSEnvironment env, Document inXML)
			throws Exception {

		Element eleinXML;
		Element eleXML;
		Element eleGetExList;
		Element eleInboxRefList;
		Element eleInboxRef;
		Element eleIntErrDetails;
		Element eleInbox;
		String strQueueKey = "";
		String strQueueId;
		String strErrorTxnId;
		NodeList ndInoxRefList;
		Document docOutputGetExcepList;
		Document docOutputIntErrDetails;
		String strExceptionType;
		String strLastOccurredOn;
		String strErrorCode;
		String strErrorString;
		String strGeneratedOn;
		String strFlowName;
		String strQId = "";
		String strAlQLow = "";
		String strAlQMedium = "";
		String strAlQHigh = "";
		String strExceptionId;
		String strAlertCnt;
		String strExcepType;
		String strSeverity = KohlsConstant.LOW;
		String strErrorData = "";

		eleinXML = inXML.getDocumentElement();

		eleXML = (Element) eleinXML.getElementsByTagName(KohlsXMLLiterals.A_XML).item(0);

		strQueueKey = eleXML.getAttribute(KohlsXMLLiterals.A_QUEUE_KEY);
		strQId = eleXML.getAttribute("Queue_Id");
		
		if (!strQueueKey.equals(KohlsConstant.BLANK)) {

			YFCDocument yfcDocGetExList = YFCDocument.createDocument(KohlsXMLLiterals.A_INBOX);
			YFCElement yfcEleGetExDetails = yfcDocGetExList
					.getDocumentElement();
			yfcEleGetExDetails.setAttribute(KohlsXMLLiterals.A_ACTIVITY_FLAG, "Y");

			YFCElement yfcEleQueue = yfcDocGetExList.createElement(KohlsXMLLiterals.A_QUEUE);
			yfcEleQueue.setAttribute(KohlsXMLLiterals.A_QUEUE_KEY_EX_LST, strQueueKey);
			yfcEleGetExDetails.appendChild(yfcEleQueue);

			docOutputGetExcepList = this.api.getExceptionList(env,
					yfcDocGetExList.getDocument());
			
			eleGetExList = docOutputGetExcepList.getDocumentElement();

			List nodeInbox = XMLUtil
					.getElementsByTagName(eleGetExList, KohlsXMLLiterals.A_INBOX);

			// Loop through the number of Exceptions for queue
			for (Iterator iterator = nodeInbox.iterator(); iterator.hasNext();) {
				eleInbox = (Element) iterator.next();

				strAlertCnt = eleInbox.getAttribute(KohlsXMLLiterals.A_CONSOLIDATION_COUNT);	
				strExcepType = eleInbox.getAttribute("ExceptionType");
				
				// check to see if individual alert thresholds are configured
				strAlQLow = args.getProperty(strQId+"."+KohlsConstant.LOW);
				strAlQMedium = args.getProperty(strQId+"."+KohlsConstant.MEDIUM);
				strAlQHigh = args.getProperty(strQId+"."+KohlsConstant.HIGH);
				
				if(null==strAlQLow || strAlQLow.equals("")){
					strAlQLow = this.strLow;
				}
				if(null==strAlQMedium || strAlQMedium.equals("")){
					strAlQMedium = this.strMedium;
				}
				if(null==strAlQHigh || strAlQHigh.equals("")){
					strAlQHigh = this.strHigh;
				}

				if (Integer.parseInt(strAlertCnt) >= Integer.parseInt(strAlQLow)) {

					if (Integer.parseInt(strAlertCnt) >= Integer
							.parseInt(strAlQMedium)) {
						strSeverity = KohlsXMLLiterals.A_MEDIUM;
					} if (Integer.parseInt(strAlertCnt) >= Integer
							.parseInt(strAlQHigh)) {
						strSeverity = KohlsXMLLiterals.A_HIGH;
					}
					
					eleInboxRefList = (Element) eleInbox.getElementsByTagName(
							KohlsXMLLiterals.A_INBOX_REF_LIST).item(0);

					ndInoxRefList = eleInboxRefList
							.getElementsByTagName(KohlsXMLLiterals.A_INBOX_REF);

					if (ndInoxRefList.getLength() > 0) {

						
						if(!strExcepType.equalsIgnoreCase("AGENTEXCEPTION")){
						
						eleInboxRef = (Element) eleInboxRefList
								.getElementsByTagName(KohlsXMLLiterals.A_INBOX_REF)
								.item(1);

						strErrorTxnId = eleInboxRef.getAttribute(KohlsXMLLiterals.A_VALUE);
						
						YFCDocument yfcDocIntErrDetails = YFCDocument
								.createDocument(KohlsXMLLiterals.A_INTEGRATION_ERROR);
						YFCElement yfcEleIntErrDetails = yfcDocIntErrDetails
								.getDocumentElement();
						yfcEleIntErrDetails.setAttribute(KohlsXMLLiterals.A_ERROR_TXN_ID,
								strErrorTxnId);

						docOutputIntErrDetails = this.api
								.getIntegrationErrorDetails(env,
										yfcDocIntErrDetails.getDocument());

						eleIntErrDetails = docOutputIntErrDetails
								.getDocumentElement();

						strExceptionType = eleInbox
								.getAttribute(KohlsXMLLiterals.A_EXCEPTION_TYPE);
						strLastOccurredOn = eleInbox
								.getAttribute(KohlsXMLLiterals.A_LAST_OCCURED_ON);
						strGeneratedOn = eleInbox.getAttribute(KohlsXMLLiterals.A_GENERATED_ON);

						strErrorCode = eleIntErrDetails
								.getAttribute(KohlsXMLLiterals.A_ERROR_CODE);
						strErrorString = eleIntErrDetails
								.getAttribute(KohlsXMLLiterals.A_ERROR_STRING);
						strFlowName = eleIntErrDetails.getAttribute(KohlsXMLLiterals.A_FLOW_NAME);
						strQueueId = eleIntErrDetails.getAttribute(KohlsXMLLiterals.A_QUEUE_ID);
						strExceptionId = eleIntErrDetails
								.getAttribute(KohlsXMLLiterals.A_EXCEPTION_ID);
						
					} else{									
							strExceptionType = eleInbox
								.getAttribute(KohlsXMLLiterals.A_EXCEPTION_TYPE);
							strLastOccurredOn = eleInbox
								.getAttribute(KohlsXMLLiterals.A_LAST_OCCURED_ON);
							strGeneratedOn = eleInbox.getAttribute(KohlsXMLLiterals.A_GENERATED_ON);
							strQueueId = eleInbox.getAttribute("QueueId");
							
							eleInboxRef = (Element)ndInoxRefList.item(0);
							strExceptionId = eleInboxRef.getAttribute("InboxKey");
							eleInboxRef = (Element)ndInoxRefList.item(9);
							strFlowName = eleInboxRef.getAttribute("Value");
							eleInboxRef = (Element)ndInoxRefList.item(2);
							strErrorCode = eleInboxRef.getAttribute("Value");
							eleInboxRef = (Element)ndInoxRefList.item(4);
							strErrorString = eleInboxRef.getAttribute("Value");						
						}
						strErrorData = KohlsXMLLiterals.A_FLOW_NAME+":" + strFlowName
						+ ", "+KohlsXMLLiterals.A_EXCEPTION_ID+":" + strExceptionId
						+ ", "+KohlsXMLLiterals.A_EXCEPTION_TYPE+":" + strExceptionType
						+ ", "+KohlsXMLLiterals.A_ERROR_CODE+":" + strErrorCode
						+ ", "+KohlsXMLLiterals.A_ERROR_STRING+":" + strErrorString
						+ ", "+KohlsXMLLiterals.A_SEVERITY+":" + strSeverity
						+ ", "+"Queue Name"+":" + strQueueId
						+ ", "+KohlsXMLLiterals.A_GENERATED_ON+":" + strGeneratedOn
						+ ", "+KohlsXMLLiterals.A_LAST_OCCURED_ON+":" + strLastOccurredOn;

						log.info(strErrorData);
					}
				}
			}
		}		
	}

	/**
	 * set the properties to global variables
	 * @param arg0 Properties
	 * 
	 * @throws Exception e
	 *
	 */
	public void setProperties(Properties arg0) throws Exception {
		
		args = arg0;
		this.strLow = arg0.getProperty(KohlsConstant.LOW);
		this.strMedium = arg0.getProperty(KohlsConstant.MEDIUM);
		this.strHigh = arg0.getProperty(KohlsConstant.HIGH);

	}
}
