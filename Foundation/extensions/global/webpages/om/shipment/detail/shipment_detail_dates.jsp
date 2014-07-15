<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>

<table class="view" width="100%">
	<tr>
		<yfc:makeXMLInput name="resPoolKey">
			<yfc:makeXMLKey binding="xml:/ResourcePool/@Node" value="xml:/Shipment/@ShipNode"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@ItemGroupCode" value="xml:/Shipment/ServiceLine/@ItemGroupCode"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/ServicedItem/@ItemID" value="xml:/Shipment/ServiceLine/Item/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@ResourcePoolKey" value="xml:/Shipment/ServiceLine/CapacityAllocations/CapacityAllocation/@ResourcePoolKey"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@OrganizationCode" value="xml:/Shipment/@SellerOrganizationCode"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@ShipmentKey" value="xml:/Shipment/@ShipmentKey"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@ApptStartTimestamp" value="xml:/Shipment/@PromisedApptStartDate"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@ApptEndTimestamp" value="xml:/Shipment/@PromisedApptEndDate"/>
			<yfc:makeXMLKey binding="xml:/ResourcePool/@Timezone" value="xml:/Shipment/ServiceLine/@Timezone"/>
		</yfc:makeXMLInput>
		<td class="detaillabel" >
			<yfc:i18n>Appointment</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%=displayTimeWindow(resolveValue("xml:/Shipment/@PromisedApptStartDate"), resolveValue("xml:/Shipment/@PromisedApptEndDate"), resolveValue("xml:/Shipment/ServiceLine/@Timezone") )%>
			<a <%=getDetailHrefOptions("L01", getParameter("resPoolKey"), "")%>><%=showAppointmentIcon(resolveValue("xml:/Shipment/ServiceLine/@ApptStatus"), getI18N("Plan_Service_Appointment") ) %></a>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Appointment_Status</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%=getI18N(resolveValue("xml:/Shipment/ServiceLine/@ApptStatus") )%>
		</td>
	</tr>
</table>

