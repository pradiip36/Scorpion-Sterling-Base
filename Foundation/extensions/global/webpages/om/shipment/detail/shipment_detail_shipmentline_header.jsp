<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<%
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	boolean bIsOutboundShipment = equals("omd",sAppCode);
%>
<input type="hidden" name="xml:/Shipment/ShipmentLines/ShipmentLine/@ShipmentLineKey" value='<%=resolveValue("xml:/ShipmentLines/ShipmentLine/@ShipmentLineKey")%>'/>
<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipmentKey","xml:/ShipmentLines/ShipmentLine/@ShipmentKey")%>/>
<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Shipment_Line_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@ShipmentLineNo"/>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Shipment_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/Shipment/@ShipmentNo"/>
	</td>

	<td class="detaillabel">
		<yfc:i18n>Enterprise</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/Shipment/@EnterpriseCode"/>
	</td>

</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Order_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@OrderNo"/>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Order_Line_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@PrimeLineNo"/>
	</td>

	<td class="detaillabel">
		<yfc:i18n>Release_#</yfc:i18n>
	</td>
	<td class="protectedtext">
		<%if(!equals("0",resolveValue("xml:/ShipmentLines/ShipmentLine/@ReleaseNo"))){%>
			<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@ReleaseNo"/>        
		<%}%>
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@ItemID"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Description</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@ItemDesc"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Is_Hazardous_Item</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@IsHazmat"/>
    </td>
</tr>
<tr>
	<td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@ProductClass"/>
	</td>
	<td class="detaillabel">
		<yfc:i18n>Unit_Of_Measure</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@UnitOfMeasure"/>
	</td>
	<td class="detaillabel">
		<yfc:i18n>Requested_Serial_#</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@RequestedSerialNo"/>
	</td>	
</tr>
<tr>
	<td class="detaillabel">
		<yfc:i18n>Quantity</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@Quantity"/>
	</td>
	<%
		if(bIsOutboundShipment) {
	%>
		<td class="detaillabel" >
			<yfc:i18n>Over_Ship_Quantity</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@OverShipQuantity"/>
		</td>
	<%
		}else{%>
		<td class="detaillabel" >
			<yfc:i18n>Received_Quantity</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@ReceivedQuantity"/>
		</td>
	<%}%>
	<td class="detaillabel" >
		<yfc:i18n>Original_Qty</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@OriginalQuantity"/>
	</td>
</tr>
</table>
