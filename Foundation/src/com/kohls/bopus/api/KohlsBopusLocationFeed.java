package com.kohls.bopus.api;

import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.bopus.util.KohlsLocationFeedUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsBopusLocationFeed {
	
	private Properties props;
	/**
	 * This method is invoked by SDF to pass API arguments as properties to the class.
	 * @param props
	 */
	public void setProperties(Properties props) {
		this.props = props;
	}

	private static final String CLASSNAME = KohlsBopusLocationFeed.class.getName();
	private static final YFCLogCategory log = YFCLogCategory.instance(CLASSNAME);


	public Document processLocationFeed(final YFSEnvironment env, Document inXML)
			throws YFSException, ParserConfigurationException, Throwable {
		try {
			String calendarKey = null;
			boolean isOrganizationDataPresent = validateData(inXML,
					KohlsConstant.XPATH_FOR_ORGANIZATION);
			boolean isCalendarDataPresent = validateData(inXML,
					KohlsConstant.XPATH_FOR_CALENDAR);

			//If organization element is present then create or change organization. 
			if (isOrganizationDataPresent) {
				Document manageOrgInputDocument = prepareManageOrganizationInput(
						inXML, calendarKey);
				KohlsLocationFeedUtil.callAPI(env, manageOrgInputDocument,
						KohlsConstant.MANAGE_ORGANIZATION_HIERARCHY_API, null);
				
				//Add newly created Store to DG
				addLocationToDG(env, manageOrgInputDocument);
			}

			//If calendar element is present then create or change calendar.
			if (isCalendarDataPresent) {
				//If calendar exists then change calendar
				boolean isCalendarAlreadyPresent = checkForCalendar(inXML, env);
				if (isCalendarAlreadyPresent) {
					Document changeCalendarInputDocument = prepareChangeORCreateCalendarInput(inXML);
					Document outputDocument = KohlsLocationFeedUtil
							.callAPI(env, changeCalendarInputDocument,
									KohlsConstant.CHANGE_CALENDAR_API, null);
					Element calendarElement = (Element) outputDocument
							//.getElementsByTagName(KohlsConstant.CALENDAR_ELEMENT).item(0);
							.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT).item(0);
					calendarKey = calendarElement.getAttribute(KohlsXMLLiterals.A_CALENDAR_KEY_ATTRIBUTE);
				} else {
					//If calendar doesn't exist create calendar
					Document createCalendarInputDocument = prepareChangeORCreateCalendarInput(inXML);
					Document outputDocument = KohlsLocationFeedUtil
							.callAPI(env, createCalendarInputDocument,
									KohlsConstant.CREATE_CALENDAR_API, null);
					Element calendarElement = (Element) outputDocument
							.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT).item(0);
					calendarKey = calendarElement.getAttribute(KohlsXMLLiterals.A_CALENDAR_KEY_ATTRIBUTE);
				}
			}
			
			//If calendar and organization is present then invoke manageOrganizationHierarchy API to stamp calendarKey.
			if (isOrganizationDataPresent && isCalendarDataPresent) {
				Document manageOrgInputDocument = prepareManageOrganizationInput(
						inXML, calendarKey);
				KohlsLocationFeedUtil.callAPI(env, manageOrgInputDocument,
						KohlsConstant.MANAGE_ORGANIZATION_HIERARCHY_API, null);
			}
		} catch (YFSException exception) {
			// Log any exception that may arise while executing the API.
			log.error("YFSException occured" + exception.getMessage());
			//throw new YFSException("YFSException occured" + exception);
			throw exception;
		}
		return inXML;
	}

	/**
	 * This method adds a location to a distribution group.
	 * @param env
	 * @param manageOrgInputDocument
	 * @throws Exception
	 */
	private void addLocationToDG(YFSEnvironment env,
			Document manageOrgInputDocument) throws Exception {
		String orgCode = manageOrgInputDocument.getDocumentElement()
				.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE_ATTRIBUTE);
		String dgName = props.getProperty(KohlsConstant.DG_NAME_PROPERTY);
		String ownerKey = props.getProperty(KohlsConstant.OWNER_KEY_PROPERTY);
		String dgXMLString = KohlsConstant.ITEM_SHIP_NODE_XML;
		Document dgXML = XMLUtil.getDocument(dgXMLString);
		dgXML.getDocumentElement().setAttribute(KohlsXMLLiterals.A_DISTRIBUTION_RULE_ID_ATTRIBUTE, dgName);
		dgXML.getDocumentElement().setAttribute(KohlsXMLLiterals.A_OWNER_KEY_ATTRIBUTE, ownerKey);
		dgXML.getDocumentElement().setAttribute(KohlsXMLLiterals.A_SHIP_NODE_KEY_ATTRIBUTE, orgCode);
		// check if location is already added to DG else add it
		if (!KohlsLocationFeedUtil.callAPI(env, dgXML, KohlsConstant.GET_DISTRIBUTION_LIST_API, null)
				.getDocumentElement().hasChildNodes()) {
			KohlsLocationFeedUtil.callAPI(env, dgXML, KohlsConstant.CREATE_DISTRIBUTION_API, null);
		}
	}

	/**
	 * This method is used to check a particular XPath exists in input XML.
	 * @param inXML
	 * @param xPath
	 * @return
	 * @throws Exception 
	 */
	private static boolean validateData(Document inXML, String xPath)
			throws Exception {
		log.debug("inside validateData");
		boolean isEntityPresent = false;
		Element eleInXml = inXML.getDocumentElement();
		log.debug("the element is:"+XMLUtil.getElementXMLString(eleInXml));
		log.debug("xpath is:"+ xPath);
		String attributeValue = XPathUtil.getString(eleInXml, xPath);
		isEntityPresent = ((attributeValue == null) || (attributeValue == "")) ? false
				: true;
		log.debug(CLASSNAME + "  Method Name :  validateData()   "
				+ isEntityPresent);
		return isEntityPresent;
	}

	/**
	 * This method checks whether given calendar already exists in system.
	 * @param inXML
	 * @param env
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private static boolean checkForCalendar(Document inXML, YFSEnvironment env)
			throws IllegalArgumentException, Exception {
		NodeList inputCalendarList = inXML.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT);
		Element calendar = (Element) inputCalendarList.item(0);
		String bpsCalenderId = "";
		if(!calendar.getAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE).substring(0, 3).equalsIgnoreCase(KohlsConstant.BPS)) {
			bpsCalenderId = KohlsConstant.BPS + KohlsConstant.CONST_ + calendar.getAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE);
			calendar.setAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE, bpsCalenderId);
		}
		String calendarId = calendar.getAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE);
		Document getCalendarListInputDocument = prepareGetCalendarListInput(inXML);
		Document outputDocument = KohlsLocationFeedUtil.callAPI(env,
				getCalendarListInputDocument, KohlsConstant.GET_CALENDAR_LIST_API, null);
		NodeList calendarList = outputDocument.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT);
		String existingCalenderId;
		if(calendarList.getLength() > 0)
		{
			for(int i=0;i<calendarList.getLength();i++)
			{
				existingCalenderId = ((Element)calendarList.item(i)).getAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE);
				if(calendarId.equals(existingCalenderId))
				{
					return true;
				}
			}

			YFSException ex = new YFSException(KohlsConstant.ERROR_CODE_EXTN000001_DESCRIPTION);
			throw ex;
	
		}
		return false;
	}


	/**
	 * This method prepares input XML to getCalendarList API.
	 * @param inXML
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private static Document prepareGetCalendarListInput(Document inXML)
			throws IllegalArgumentException, Exception {
		// Extract data from the input XML.
		NodeList calendarList = inXML.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT);
		Element calendar = (Element) calendarList.item(0);
		//String calendarId = calendar.getAttribute(KohlsConstant.CALENDAR_ID_ATTRIBUTE);
		String organizationCode = calendar.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE_ATTRIBUTE);

		Document document = XMLUtil.createDocument(KohlsXMLLiterals.E_CALENDAR_ELEMENT);
		Element rootElement = document.getDocumentElement();
		//rootElement.setAttribute(KohlsConstant.CALENDAR_ID_ATTRIBUTE, calendarId);
		rootElement.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE_ATTRIBUTE, organizationCode);

		return document;
	}


	/**
	 * This method creates input XML for createCalendar and changeCalendar API.
	 * @param inXML
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private static Document prepareChangeORCreateCalendarInput(Document inXML)
			throws IllegalArgumentException, Exception {
		Element eleCalender = (Element) inXML.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT)
				.item(0);
		//Document docCalender = XMLUtil.getDocument(eleCalender, true);
		Document docCalender = XMLUtil.getDocumentForElement(eleCalender);
		return docCalender;
		
		
		
	}

	/**
	 * This method creates the input XML for manageOrganizationHierarchy API.
	 * @param inXMLCopy
	 * @param calendarKey
	 * @return
	 * @throws IllegalArgumentException
	 * @throws Exception
	 */
	private static Document prepareManageOrganizationInput(Document inXMLCopy,
			String calendarKey) throws IllegalArgumentException, Exception {
		Element eleOrg = (Element) inXMLCopy.getElementsByTagName(
				KohlsXMLLiterals.E_ORGANIZATION_ELEMENT).item(0);
		//If calendarKey is not null stamp it as business calendar key
		if (calendarKey != null)
			eleOrg.setAttribute(KohlsXMLLiterals.A_BUSINESS_CALENDAR_KEY_ATTRIBUTE, calendarKey);
		//Document docOrg = XMLUtil.getDocument(eleOrg, true);
		Document docOrg = XMLUtil.getDocumentForElement(eleOrg);
		return docOrg;
	}

}
