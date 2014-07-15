<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table width="100%" class="view">
<tr>
		
    <td class="detaillabel" >
        <yfc:i18n>Shipment_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/GetLinesToReceive/@ShipmentNo"/>
	</td>
	
	<td class="detaillabel">
			<yfc:i18n>Enterprise</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/GetLinesToReceive/@EnterpriseCode"/>
	</td>


</table>