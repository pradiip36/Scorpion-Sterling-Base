<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<% // Call getWorkOrderDetails to get information about the work order and its appointment
   // if WorkOrderKey is populated on the OrderRelease.
   if (!isVoid(resolveValue("xml:/Shipment/@WorkOrderKey"))) {
%>
       <yfc:callAPI apiID="AP1"/>

       <% // If the OrderRelease also has a work order appointment populated, then set the
          // appropriate WorkOrderAppointment element into the request with the 
          // "CurrentWorkOrderAppointment" namespace.
          String appointmentKey = resolveValue("xml:/Shipment/@WorkOrderApptKey");
       %>
          <yfc:loopXML binding="xml:/WorkOrder/WorkOrderAppointments/@WorkOrderAppointment" id="WorkOrderAppointment">
              <%
                 YFCElement apptElem = (YFCElement) pageContext.getAttribute("WorkOrderAppointment");
                 YFCElement apptListElem = apptElem.getParentElement();
                 // Set an attribute on this element to be used to display in the appointment combo box
                 String timeWindow = displayTimeWindow(resolveValue("xml:/WorkOrderAppointment/@PromisedApptStartDate"), resolveValue("xml:/WorkOrderAppointment/@PromisedApptEndDate"), resolveValue("xml:/WorkOrder/@Timezone"));
                 String apptSequence = resolveValue("xml:/WorkOrderAppointment/@ApptSeq");
                 apptElem.setAttribute("DisplayAppointment", apptSequence + " - " + timeWindow);
                 boolean matchFound = false;
                 if (equals(appointmentKey, resolveValue("xml:/WorkOrderAppointment/@WorkOrderApptKey"))) {
                     request.setAttribute("CurrentWorkOrderAppointment", apptElem);
                     matchFound = true;
                 }
                 // If the appointment is not the current one saved on the shipment, and it is not
                 // open, then remove it from the API output so that it does not show up in the combo box
                 if ((!matchFound) && (!equals("OPEN", apptElem.getAttribute("ApptStatus")))) {
                     apptListElem.removeChild(apptElem);
                 }
              %>
          </yfc:loopXML>
       <%
   } %>

<table width="100%" class="view">
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Delivery_Method</yfc:i18n>
        </td>
        <td class="protectedtext">
            <yfc:getXMLValue binding="xml:/Shipment/@DeliveryMethod"/>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Work_Order_#</yfc:i18n>
        </td>
        <yfc:makeXMLInput name="workOrderKey">
                <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/Shipment/@WorkOrderKey" ></yfc:makeXMLKey>
            </yfc:makeXMLInput>
        <td class="protectedtext">
            <a <%=getDetailHrefOptions("L01", getParameter("workOrderKey"),"")%>><yfc:getXMLValue name="WorkOrder" binding="xml:/WorkOrder/@WorkOrderNo"/></a>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Appointment</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Shipment/@WorkOrderApptKey", "xml:/Shipment/@WorkOrderApptKey", "xml:/Shipment/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/WorkOrder/WorkOrderAppointments/@WorkOrderAppointment" name="DisplayAppointment"
                    value="WorkOrderApptKey" selected="xml:/Shipment/@WorkOrderApptKey"/>
            </select>
        </td>
    </tr>
</table>