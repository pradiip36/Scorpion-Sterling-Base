<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
	String sNode = resolveValue ("xml:/Shipment/@ShipNode");
	if (isVoid(sNode)) {
		sNode = resolveValue ("xml:/Shipment/@ReceivingNode");
	}
	String sShipmentKey = resolveValue("xml:/Shipment/@ShipmentKey");

	YFCElement getShipmentInput = YFCDocument.parse("<Shipment  ShipmentKey=\"" + sShipmentKey + "\"/>").getDocumentElement();

	YFCElement getShipmentTemplate = YFCDocument.parse("<Shipment><DockAppointmentList>	<DockAppointment  AppointmentType=\"\" AppointmentNo=\"\" AppointmentDate=\"\" EndTime=\"\"  LocationId=\"\"   StartTime=\"\"  /> </DockAppointmentList></Shipment>").getDocumentElement();
	
    %>

	<yfc:callAPI apiName="getShipmentDetails" inputElement="<%=getShipmentInput%>" templateElement="<%=getShipmentTemplate%>" outputNamespace="getShipmentDockDetails"/>

    <%
	YFCElement shipmentList= (YFCElement) request.getAttribute("getShipmentDockDetails");
	YFCElement dockAppointments = null;
	YFCNodeList dockAppointmentList = null;
	if(!isVoid(shipmentList))
	{
		dockAppointments = shipmentList.getChildElement("DockAppointmentList");
	}
	if(!isVoid(dockAppointments))
	{
		dockAppointmentList = dockAppointments.getElementsByTagName("DockAppointment");
	}
	if(!isVoid(dockAppointmentList))
	{
		for(int k=0; k<dockAppointmentList.getLength(); k++)
		{
			YFCElement dockAppt =(YFCElement) dockAppointmentList.item(k);			
			%>
			<table class="view" width="100%">
    <tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Location</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
			<%=dockAppt.getAttribute("LocationId")%>
		</td>
		<td/>
		<td/>	
	</tr>
	<tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Appointment_#</yfc:i18n>
		</td>
        <td class="protectedtext"  >
		<%=dockAppt.getAttribute("AppointmentNo")%>			
		</td>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Appointment_Date</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
		 <%=getLocalizedValue("Date",dockAppt.getAttribute("AppointmentDate"))%>			
		</td>	
	</tr>
	<tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Start_Time</yfc:i18n>
		</td>
        <td class="protectedtext"  >
		<%=dockAppt.getAttribute("StartTime")%>
			
		</td> 
		<td class="detaillabel" nowrap="true" >		
		    <yfc:i18n>End_Time</yfc:i18n>
		</td>
        <td class="protectedtext"  >
		<%=dockAppt.getAttribute("EndTime")%>			
		</td>		
	</tr>
</table>
		<%}
	}%>
	
