<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%
	String scacAndServiceKey=getValue("Shipment","xml:/Shipment/@ScacAndServiceKey");
	String docType= getValue("Shipment","xml:/Shipment/@DocumentType");

%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<%  
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;
%>
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
	 <% if (!isVoid(modifyView)) {%>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@PickticketNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
    <% } else { %>
		<td class="protectedtext">
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@PickticketNo"/>
		</td>
	<% } %>

    <td class="detaillabel" >
        <yfc:i18n>Plan_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:makeXMLInput name="DeliveryPlanKey" >
			<yfc:makeXMLKey binding="xml:/DeliveryPlan/@DeliveryPlanKey" value="xml:/Shipment/@DeliveryPlanKey" />
		</yfc:makeXMLInput>
		<% if(showDeliveryPlanNo(resolveValue("xml:/Shipment/DeliveryPlan/@OwnerOrganizationCode"))) { %>
			<a <%=getDetailHrefOptions("L02",getParameter("DeliveryPlanKey"),"")%> ><yfc:getXMLValue binding="xml:/Shipment/DeliveryPlan/@DeliveryPlanNo"/></a>
		<% } else { %>
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/DeliveryPlan/@DeliveryPlanNo"/>
		<%}%>
	</td>
</tr>

<tr>

		<td class="detaillabel">
			<yfc:i18n>Enterprise</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@EnterpriseCode"/>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Buyer</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:makeXMLInput name="buyerOrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Shipment/@BuyerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L03",getParameter("buyerOrganizationKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@BuyerOrganizationCode"/></a>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Seller</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:makeXMLInput name="sellerOrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Shipment/@SellerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L03",getParameter("sellerOrganizationKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@SellerOrganizationCode"/></a>
		</td>

</tr>

<tr>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Node</yfc:i18n> 
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="ShipNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ShipNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("ShipNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipNode"/></a>
    </td>

    <td class="detaillabel" >
        <yfc:i18n>Destination</yfc:i18n> 
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="receivingNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ReceivingNode" />
		</yfc:makeXMLInput>
		<%	String destNode = getValue("Shipment","xml:/Shipment/@ReceivingNode");
		if (!isVoid(destNode)) { %>
		<a <%=getDetailHrefOptions("L01",getParameter("receivingNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ReceivingNode"/></a>
		<%} else { %>
			<jsp:include page="/common/smalladdress.jsp" flush="true" >
				 <jsp:param name="Path" value="Shipment/ToAddress"/>
			</jsp:include>
		<%}%>
    </td>

	<td class="detaillabel" >
        <yfc:i18n>Status</yfc:i18n> 
    </td>
	<td class="protectedtext">
      <a <%=getDetailHrefOptions("L04",getParameter("startReceiptKey"),"")%> ><yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@StatusName"/></a>		
    </td>
</tr>
<tr>
	<% if(isVoid(resolveValue("xml:/Shipment/@OrderNo") ) )	{ %>	<!-- cr 35459 -->
		<td></td>
		<td></td>
	<% } else { %>
		<td class="detaillabel" >
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<a <%=getDetailHrefOptions("L05",getParameter("orderKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@OrderNo"/></a>	
		</td>
	<% } 

	if(equals("0",resolveValue("xml:/Shipment/@ReleaseNo")))	{	%>	<!-- cr 35459 -->
		<td></td>
		<td></td>
	<% } else { %>
		<td class="detaillabel" >
			<yfc:i18n>Release_#</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:getXMLValue  binding="xml:/Shipment/@ReleaseNo"/>

		</td>
	<%}%>
</tr>

</table>