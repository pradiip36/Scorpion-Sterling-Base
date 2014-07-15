package com.kohls.bopus.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsConstants;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXPathUtil;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class KohlsBOPUSStoreHoursUtil {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsBOPUSStoreHoursUtil.class.getName());

	public static String CalculateStoreHours(String strShipmentNo, YFSEnvironment env){
		String strShipNode = null;
		String strCalenderId = null;
                  String strCalenderKey = null;
		String strFromDate = null;
		String strEarliestShiftStartTime = null;
		String strLastShiftEndTime = null;
		String strTimeSlot = null;
		try{
			Document inputShipmentDoc = prepareGetShipmentListInput(strShipmentNo);
			log.debug("Input to getshipmentList API "+SCXmlUtil.getString(inputShipmentDoc));
			Document outputShipmentListDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.TEMPLATE_SHIPMENTS_PICK_PROCESS,
					KohlsConstants.API_GET_SHIPMENT_LIST, inputShipmentDoc);
			log.debug("getshipmentList API output doc: "+SCXmlUtil.getString(outputShipmentListDoc));

			Element eleShipment = KohlsXPathUtil.getElementByXpath(outputShipmentListDoc,"Shipments/Shipment");
			if (!YFCCommon.isVoid(eleShipment)) {
				strShipNode = eleShipment.getAttribute(KohlsXMLLiterals.E_SHIP_NODE);
				strFromDate = eleShipment.getAttribute(KohlsXMLLiterals.A_REQUESTED_SHIPMENT_DATE);
			}

			Document getCalendarListInputDocument = prepareGetOrganizationHierarchyInput(strShipNode);
			Document outputDocument = KohlsLocationFeedUtil.callAPI(env,
					getCalendarListInputDocument, KohlsConstant.GET_ORGANIZATION_HIERARCHY_API, KohlsConstant.CALENDAR_GET_ORGANIZATION_HIERARCHY_TEMPLATE);
			log.debug("getOrganizationHierarchy API output doc: "+SCXmlUtil.getString(outputDocument));

			NodeList calendarList = outputDocument.getElementsByTagName(KohlsXMLLiterals.E_CALENDAR_ELEMENT);
			if(calendarList.getLength() > 0)
			{
				Element eleCalendar = KohlsXPathUtil.getElementByXpath(outputDocument,"Organization/Calendars/Calendar");
				strCalenderId =eleCalendar.getAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE);
                                     strCalenderKey = eleCalendar.getAttribute("CalendarKey");

			}

			if(strCalenderId != null){
				/* getCalendarDayDetails Input XML
				 * <Calendar CalendarId="BSP_829NodeCalendar" FromDate="2014-02-05T19:03:08-05:00" OrganizationCode="829" ToDate="2014-02-05T19:03:08-05:00" /> */
				Document inputGetCalendarDetailsDocument = prepareGetCalendarDayDetailsInput(strCalenderId,strShipNode, strFromDate, strCalenderKey);
				log.debug("Input to getCalendarDayDetails API "+SCXmlUtil.getString(inputGetCalendarDetailsDocument));
				Document outputCalendarDetailsDoc = KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_GET_CALENDAR_DAY_DETAILS_API, inputGetCalendarDetailsDocument);
				log.debug("output to getCalendarDayDetails API "+SCXmlUtil.getString(outputCalendarDetailsDoc));
				Element eleCalendarDate = KohlsXPathUtil.getElementByXpath(outputCalendarDetailsDoc,"Calendar/Dates/Date");
				if (!YFCCommon.isVoid(eleCalendarDate)) {
					strEarliestShiftStartTime = eleCalendarDate.getAttribute(KohlsXMLLiterals.A_EARLIEST_SHIFT_START_TIME);
					log.debug("strEarliestShiftStartTime: "+strEarliestShiftStartTime);
					strLastShiftEndTime = eleCalendarDate.getAttribute(KohlsXMLLiterals.A_LAST_SHIFT_END_TIME);
					log.debug("strLastShiftEndTime "+strLastShiftEndTime);
					
					SimpleDateFormat time24HrsFormat = new SimpleDateFormat("HH:mm:ss");
					SimpleDateFormat time12HrsFormat = new SimpleDateFormat("hh:mm aa");
					String strStartShiftTime = "";
					String strEndShiftTime = "";
					
					try {
						Date startShiftTime = time24HrsFormat.parse(strEarliestShiftStartTime);
						Date endShiftTime = time24HrsFormat.parse(strLastShiftEndTime);
						strStartShiftTime = time12HrsFormat.format(startShiftTime);
						strEndShiftTime = time12HrsFormat.format(endShiftTime);
						log.debug("strLastShiftEndTime "+strLastShiftEndTime);
						
					} catch (ParseException e) {
						
						e.printStackTrace();
					}

					strTimeSlot = strStartShiftTime + " - " + strEndShiftTime;
					//timeSlot(strEarliestShiftStartTime, strLastShiftEndTime);
				}
			}
		}catch(Exception e){
			e.getStackTrace();
			throw new YFSException("Exception in CalculateStoreHours method is: "+e.getMessage());
		}
		return strTimeSlot;
	}


	public static String timeSlot(String startTime, String endTime) throws Exception {
		String strTimeSlot ="";
        DateFormat format = new SimpleDateFormat("HH:mm");
        Date start = format.parse(startTime);
        Date end = format.parse(endTime);

        long difference = end.getTime() - start.getTime();

        long hours = TimeUnit.MILLISECONDS.toHours(difference);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(difference) % 60;

        strTimeSlot = strTimeSlot+hours+":"+minutes;
        return strTimeSlot;
    }


	/**
	 * This method prepares input xml to getShipmentList API
	 * @param strShipmentNo
	 * @return
	 * @throws Exception
	 */
	private static Document prepareGetShipmentListInput(String strShipmentNo) throws Exception {
		Document document = YFCDocument.createDocument(KohlsXMLLiterals.E_SHIPMENT).getDocument();
		Element ele =  document.getDocumentElement();
		ele.setAttribute(KohlsXMLLiterals.A_SHIPMENT_NO, strShipmentNo);
		return document;
	}

	/**
	 * This method prepares input XML to getOrganizationHierarchy API.
	 * <Organization OrganizationCode="Required" OrganizationKey="Required" />
	 *
	 * @param shipNode
	 * @return
	 * @throws Exception
	 */
	private static Document prepareGetOrganizationHierarchyInput(String shipNode)
			throws Exception {
		Document document = XMLUtil.createDocument(KohlsXMLLiterals.E_ORGANIZATION);
		Element rootElement = document.getDocumentElement();
		rootElement.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE_ATTRIBUTE, shipNode);
		rootElement.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_KEY, shipNode);
		return document;
	}

	/**
	 * This method prepares input xml to getCalendarDayDetails API
	 * @param strShipmentNo
	 * @return
	 * @throws Exception
	 *
	 * <Calendar CalendarId="BSP_829NodeCalendar" FromDate="2014-02-05T19:03:08-05:00" OrganizationCode="829" ToDate="2014-02-05T19:03:08-05:00" />
	 */
	private static Document prepareGetCalendarDayDetailsInput(String strCalenderId, String strShipNode, String strFromDate, String strCalenderKey)throws Exception {
		Document document = YFCDocument.createDocument(KohlsXMLLiterals.E_CALENDAR_ELEMENT).getDocument();
		Element ele =  document.getDocumentElement();
		ele.setAttribute(KohlsXMLLiterals.A_CALENDAR_ID_ATTRIBUTE, strCalenderId);
                  ele.setAttribute("CalendarKey", strCalenderKey);
		ele.setAttribute(KohlsXMLLiterals.A_FROM_DATE, strFromDate);
		ele.setAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE_ATTRIBUTE, strShipNode);
		ele.setAttribute(KohlsXMLLiterals.A_TO_DATE, strFromDate);
		return document;
	}
	
	public static void main (String arg[] ) {
		
		String strEarliestShiftStartTime = "02:40:00";
		String strLastShiftEndTime = "22:00:00";
		
		SimpleDateFormat time24HrsFormat = new SimpleDateFormat("HH:mm:ss");
		SimpleDateFormat time12HrsFormat = new SimpleDateFormat("hh:mm aa");
		String strStartShiftTime = "";
		String strEndShiftTime = "";
		
		try {
			Date startShiftTime = time24HrsFormat.parse(strEarliestShiftStartTime);
			Date endShiftTime = time24HrsFormat.parse(strLastShiftEndTime);
			strStartShiftTime = time12HrsFormat.format(startShiftTime);
			strEndShiftTime = time12HrsFormat.format(endShiftTime);
			log.debug("strLastShiftEndTime "+strLastShiftEndTime);
			
		} catch (ParseException e) {
			
			e.printStackTrace();
		}

		String strTimeSlot = strStartShiftTime + " - " + strEndShiftTime;
		System.out.println("Slot " + strTimeSlot);
		
	}
}
