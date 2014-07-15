<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@CreateUserName")%>">
            <yfc:i18n>Modified_By</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@OldStatus")%>">
            <yfc:i18n>Old_Status</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@OldStatusDate")%>">
            <yfc:i18n>Old_Status_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@NewStatus")%>">
            <yfc:i18n>New_Status</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@NewStatusDate")%>">
            <yfc:i18n>New_Status_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@ReasonCode")%>">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentStatusAudits/ShipmentStatusAudit/@ReasonText")%>">
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentStatusAudits/@ShipmentStatusAudit" id="ShipmentStatusAudit" > 
		<tr>
			<td class="tablecolumn">
                <%
                    String sUser = resolveValue("xml:/ShipmentStatusAudit/@CreateUserName");
                    if(isVoid(sUser) )
                        sUser = resolveValue("xml:/ShipmentStatusAudit/@Createuserid");
                %>
                <%=sUser%>
            </td>       
			<td class="tablecolumn">
				<yfc:getXMLValueI18NDB name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/OldStatus/@Description"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/@OldStatusDate"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValueI18NDB name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/NewStatus/@Description"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/@NewStatusDate"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/@ReasonCode"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ShipmentStatusAudit" binding="xml:/ShipmentStatusAudit/@ReasonText"/>
			</td>
		</tr>
    </yfc:loopXML> 
</tbody>
</table>
