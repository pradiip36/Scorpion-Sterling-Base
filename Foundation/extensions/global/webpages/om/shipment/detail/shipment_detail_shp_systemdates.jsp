<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Requested_Shipment_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@RequestedShipmentDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@RequestedShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@RequestedShipmentDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@RequestedShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Expected_Shipment_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ExpectedShipmentDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@ExpectedShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ExpectedShipmentDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@ExpectedShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Actual_Shipment_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ActualShipmentDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@ActualShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ActualShipmentDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@ActualShipmentDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@RequestedDeliveryDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@RequestedDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@RequestedDeliveryDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
		<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@RequestedDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Expected_Delivery_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ExpectedDeliveryDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@ExpectedDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ExpectedDeliveryDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@ExpectedDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Actual_Delivery_Date</yfc:i18n>
    </td>
	<td nowrap="true">
		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ActualDeliveryDate_YFCDATE", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Shipment/@ActualDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
		 <input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ActualDeliveryDate_YFCTIME", "xml:/Shipment/AllowedModifications")%>/>
        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/Shipment/@ActualDeliveryDate", "xml:/Shipment/AllowedModifications")%>/>
	</td>
</tr>
</table>
