<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table width="100%" class="view">
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Shipment_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Shippers_Ref_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@PickticketNo"/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Status</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@StatusName"/>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Execution_Status</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%=getI18N((resolveValue("xml:/Shipment/@ExecutionStatus")))%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>No_Of_Expected_Cartons</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@NumOfCartons"/>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>No_Of_Expected_Pallets</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@NumOfPallets"/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Ship_Complete</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@ShipComplete"/>
		</td>
		<td class="detaillabel" nowrap="true">
			<yfc:i18n>Ship_Line_Complete</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@ShipLineComplete"/>
		</td>
	</tr>
</table>