<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>

<table class="view" width="100%">
	<tr>
		<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
		<input type="hidden" name="xml:/Order/@Override" value="N"/>
		<input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
		<input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
		<input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>
		<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("OrderLine", "xml:/OrderLine/Order/@DraftOrderFlag")%>'/>
		<input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey","xml:/OrderLine/@OrderHeaderKey")%> />
		<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderLineKey","xml:/OrderLine/@OrderLineKey")%> />

		<yfc:makeXMLInput name="statusKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/Shipment/ServiceLine/@OrderLineKey" />
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="orderLineKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/Shipment/ServiceLine/@OrderLineKey"/>
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNo" />
			<yfc:makeXMLKey binding="xml:/Order/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
		</yfc:makeXMLInput>
		<td class="detaillabel" >
			<yfc:i18n>Line_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<a <%=getDetailHrefOptions("L02",getParameter("orderLineKey"),"")%>><yfc:getXMLValue binding="xml:/Shipment/ServiceLine/@PrimeLineNo"></yfc:getXMLValue></a>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Status</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Shipment/ServiceLine/@Status"></yfc:getXMLValue>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<% if(showOrderNo("OrderLine","Order")) {%>
				<a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>><yfc:getXMLValue binding="xml:/Shipment/ShipmentLines/ShipmentLine/Order/@OrderNo"></yfc:getXMLValue></a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/Shipment/ShipmentLines/ShipmentLine/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
		</td>

	</tr>

	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Item_ID</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Shipment/ServiceLine/Item/@ItemID"></yfc:getXMLValue>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Description</yfc:i18n>
		</td>
		<td class="protectedtext" colspan="3">
			<yfc:getXMLValue binding="xml:/Shipment/ServiceLine/Item/@ItemDesc"></yfc:getXMLValue>
		</td>
	</tr>

</table>
