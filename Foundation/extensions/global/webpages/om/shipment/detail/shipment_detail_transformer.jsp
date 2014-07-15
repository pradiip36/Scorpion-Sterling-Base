<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%@ page import="com.yantra.yfc.date.*"%>
<%@ page import="com.yantra.yfc.util.YFCDateUtils"%>

<% 
	YFCElement eGetResPoolCap = (YFCElement) request.getAttribute("Node");
	if(eGetResPoolCap == null)
		return;

	YFCElement eResPool = (YFCElement) request.getAttribute("ResourcePool");

	YFCDocument dPromise = YFCDocument.createDocument();
	YFCElement ePromise = dPromise.createElement("Promise");
	YFCElement eSuggestedOption = ePromise.createChild("SuggestedOption");
	YFCElement eOption = eSuggestedOption.createChild("Option");
	YFCElement eInteractions = eOption.createChild("Interactions");
	YFCElement eInteraction = eInteractions.createChild("Interaction");
	YFCElement eNonWorkingDays = eInteraction.createChild("NonWorkingDays");

	String sStartDate = eGetResPoolCap.getAttribute("StartDate");
	String sEndDate = eGetResPoolCap.getAttribute("EndDate");
	eInteraction.setAttribute("EarliestDate", sStartDate );
	eInteraction.setAttribute("LastDate", sEndDate );
	int iNumOfDays = YFCDateUtils.diffDays(eGetResPoolCap.getDateAttribute("StartDate"), eGetResPoolCap.getDateAttribute("EndDate") );
	ePromise.setIntAttribute("ServiceSearchWindow", iNumOfDays + 1);

	YFCElement ePromiseServiceLines = eOption.createChild("PromiseServiceLines");
	YFCElement ePromiseServiceLine = ePromiseServiceLines.createChild("PromiseServiceLine");
	ePromiseServiceLine.setAttribute("DeliveryStartSearchDate", sStartDate );
	ePromiseServiceLine.setAttribute("DeliveryEndSearchDate", sStartDate );

	YFCElement eAssignments = ePromiseServiceLine.createChild("Assignments");
	YFCElement eAssignment = eAssignments.createChild("Assignment");

	String ApptStartTimestamp = eResPool.getAttribute("ApptStartTimestamp");
	String ApptEndTimestamp = eResPool.getAttribute("ApptEndTimestamp");
	
	// Need the WorkOrderKey available for modifyWorkOrder...
	String workOrderKey = eResPool.getAttribute("WorkOrderKey");
	ePromise.setAttribute("WorkOrderKey", workOrderKey );

	eAssignment.setAttribute("ApptStartTimestamp", ApptStartTimestamp );
	eAssignment.setAttribute("ApptEndTimestamp", ApptEndTimestamp );

	YFCElement eSlots = ePromiseServiceLine.createChild("Slots");
	eSlots.setAttribute("TimeZone", eResPool.getAttribute("Timezone") );

	ArrayList lServiceSlots = getLoopingElementList("xml:/Node/ResourcePools/ResourcePool/ServiceSlots/@ServiceSlot");
	for(int i = 0; i < lServiceSlots.size(); i++)
	{
		YFCElement eServiceSlot = (YFCElement)lServiceSlots.get(i);

		YFCElement eSlot = (YFCElement) eSlots.createChild("Slot");
		eSlot.setAttribute("StartTime", eServiceSlot.getAttribute("StartTime") );
		eSlot.setAttribute("EndTime", eServiceSlot.getAttribute("EndTime") );
		eSlot.setAttribute("ServiceSlotDesc", eServiceSlot.getAttribute("ServiceSlotDesc") );

		YFCElement eAvailableDates = (YFCElement) eSlot.createChild("AvailableDates");

		YFCNodeList nlDate = eServiceSlot.getElementsByTagName("Date");
		for(int j = 0; j < nlDate.getLength(); j++ )
		{
			YFCElement eDate = (YFCElement) nlDate.item(j);
			if(eDate.getBooleanAttribute("IsWorkingDay") )
			{
				YFCElement eAvailableDate = eAvailableDates.createChild("AvailableDate");
				eAvailableDate.setAttribute("Date", eDate.getAttribute("Date") );
				eAvailableDate.setAttribute("Confirmed", "Y" );
			}
			else
			{
				YFCElement eNonWorkingDay = eNonWorkingDays.createChild("NonWorkingDay");
				eNonWorkingDay.setAttribute("Date", eDate.getAttribute("Date") );
			}
		}
	}
	dPromise.appendChild(ePromise);

	request.setAttribute("Promise", ePromise);
%>

<tr>
	<td width="100%" colspan="6" >
		<jsp:include page="/om/servicerequest/detail/servicerequest_detail_appointment_dates.jsp" flush="true"/>
	</td>
</tr>
