<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
<%
	String scacAndServiceKey=getValue("Shipment","xml:/Shipment/@ScacAndServiceKey");
	String docType= getValue("Shipment","xml:/Shipment/@DocumentType");
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	String sIsDomesticShipment=getValue("Shipment","xml:/Shipment/@IsDomesticShipment");

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
		<yfc:makeXMLInput name="startReceiptKey" >
			<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentNo" value="xml:/Shipment/@ShipmentNo" />
			<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
			<yfc:makeXMLKey binding="xml:/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
			<yfc:makeXMLKey binding="xml:/Shipment/@ExpectedDeliveryDate" value="xml:/Shipment/@ExpectedDeliveryDate"/>
			<yfc:makeXMLKey binding="xml:/Shipment/@ReceivingNode" value="xml:/Shipment/@ReceivingNode"/>
			<yfc:makeXMLKey binding="xml:/Shipment/@BuyerOrganizationCode" value="xml:/Shipment/@BuyerOrganizationCode"/>
			<yfc:makeXMLKey binding="xml:/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode"/>
			<yfc:makeXMLKey binding="xml:/Shipment/@DocumentType" value="xml:/Shipment/@DocumentType" />
		</yfc:makeXMLInput>
		<input name="startReceiptKey" type="hidden" value='<%=getParameter("startReceiptKey")%>'/>
		<yfc:makeXMLInput name="findReceiptKey" >
			<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
			<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/Shipment/@DocumentType" />
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/Shipment/@ReceivingNode" />
			<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode"/>
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="orderKey" >
			<yfc:makeXMLKey binding="xml:/Order/@OrderNo" value="xml:/Shipment/@OrderNo" />
			<yfc:makeXMLKey binding="xml:/Order/@DocumentType" value="xml:/Shipment/@DocumentType" />
			<yfc:makeXMLKey binding="xml:/Order/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
		</yfc:makeXMLInput>
		<input name="FindReceiptKey" type="hidden" value='<%=getParameter("findReceiptKey")%>'/>

        <yfc:makeXMLInput name="containerPackKey" >
			<yfc:makeXMLKey binding="xml:/Container/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
			<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ReceivingNode" />

			<%  
				if( "0001".equals(docType) ) { %>
					
					<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ShipNode" />

			<% } %>

		</yfc:makeXMLInput>
        <yfc:makeXMLInput name="ContainerHSDEPackKey" >
			<yfc:makeXMLKey binding="xml:/Container/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
			<yfc:makeXMLKey binding="xml:/Container/PackLocation/@Node" value="xml:/Shipment/@ShipNode" />
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="shipmentPrintKey">
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipNode" value="xml:/Shipment/@ShipNode" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@SCAC" value="xml:/Shipment/@SCAC" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@BuyerOrganizationCode" value="xml:/Shipment/@BuyerOrganizationCode" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@ShipmentType" value="xml:/Shipment/@ShipmentType" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@PickListNo" value="xml:/Shipment/@PickListNo" />
			<yfc:makeXMLKey binding="xml:/Print/Shipment/@HazardousMaterialFlag" value="xml:/Shipment/@HazardousMaterialFlag" />
		</yfc:makeXMLInput>
		<input type="hidden" name="PrintEntityKey" value='<%=getParameter("shipmentPrintKey")%>'/>
		<input name="containerPackKey" type="hidden" value='<%=getParameter("containerPackKey")%>'/>
		<input name="containerHSDEPackKey" type="hidden" value='<%=getParameter("ContainerHSDEPackKey")%>'/>
		<input type="hidden" value='<%=userHasOverridePermissions()%>' name="userHasOverridePermissions" />
		<input type="hidden" name="xml:/Shipment/@DataElementPath" value="xml:/Shipment"/>
		<input type="hidden" name="xml:/Shipment/@ApiName" value="getShipmentDetails"/>
		<input type="hidden" name="xml:/OrderRelease/Order/@BuyerOrganizationCode" value='<%=resolveValue("xml:/Shipment/@BuyerOrganizationCode")%>' />
		<input type="hidden" name="xml:/OrderRelease/Order/@SellerOrganizationCode" value='<%=resolveValue("xml:/Shipment/@SellerOrganizationCode")%>' />
		<input type="hidden" name="pbReceiptKey" value='<%=getParameter("findReceiptKey")%>'/>

    <td class="detaillabel" >
        <yfc:i18n>Shipment_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>

		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipmentNo","xml:/Shipment/@ShipmentNo")%> />
         <input type="hidden" name="xml:/Shipment/@OverrideModificationRules" value="N"/>
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

	<td class="detaillabel" >
        <yfc:i18n>ITN_#</yfc:i18n>
    </td>
	 <% if ("N".equals(sIsDomesticShipment)) {%>
		<td nowrap="true">
			<input type="text" maxLength="24" <%=yfsGetTextOptions("xml:/Shipment/@ITNNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
    <% } else { %>
		<td class="protectedtext">
			<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ITNNo"/>
		</td>
	<% } %>
	
</tr>

<tr>

		<td class="detaillabel">
			<yfc:i18n>Enterprise</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue  binding="xml:/Shipment/@EnterpriseCode"/>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@EnterpriseCode","xml:/Shipment/@EnterpriseCode")%> />
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Buyer</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:makeXMLInput name="buyerOrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Shipment/@BuyerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L03",getParameter("buyerOrganizationKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@BuyerOrganizationCode"/></a>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@BuyerOrganizationCode","xml:/Shipment/@BuyerOrganizationCode")%> />
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Seller</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:makeXMLInput name="sellerOrganizationKey" >
				<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Shipment/@SellerOrganizationCode" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L03",getParameter("sellerOrganizationKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@SellerOrganizationCode"/></a>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@SellerOrganizationCode","xml:/Shipment/@SellerOrganizationCode")%> />
		</td>
</tr>

<tr>
    <td class="detaillabel" >
    <%if(equals("omr",sAppCode)){%>
		<yfc:i18n>Return_From_Node</yfc:i18n> 
	<%}else{%>
		<yfc:i18n>Ship_Node</yfc:i18n> 
	<%}%>
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="ShipNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ShipNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("ShipNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipNode"/></a>
		<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipNode","xml:/Shipment/@ShipNode")%> />
    </td>

    <td class="detaillabel" >
		<%if(equals("omr",sAppCode)){%>
			<yfc:i18n>Return_To_Node</yfc:i18n>
		<%}else{%>
			<yfc:i18n>Receiving_Node</yfc:i18n> 
		<%}%>
    </td>
    <td class="protectedtext">
		<yfc:makeXMLInput name="receivingNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@ReceivingNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("receivingNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ReceivingNode"/></a>
	</td>

	<td class="detaillabel" >
        <yfc:i18n>Status</yfc:i18n> 
    </td>
	<td class="protectedtext">
      <a <%=getDetailHrefOptions("L04",getParameter("startReceiptKey"),"")%> ><yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@StatusName"/></a>
	  <%
		  if (equals("Y", getValue("Shipment", "xml:/Shipment/@HoldFlag"))) { %>

	            <% if (isVoid(modifyView) || isTrue("xml:/Rules/@RuleSetValue")) {%>
					<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_shipment_is_held")%>>
				<%	}	else	{	%>
					<a <%=getDetailHrefOptions("L06", getParameter("shipmentKey"), "")%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_shipment_is_held\nclick_to_add/remove_hold")%>></a>
				<%	}	%>

            <% } %>
	  
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
	<% if(!isVoid(resolveValue("xml:/Shipment/@OrderHeaderKey"))){%>
				<a <%=getDetailHrefOptions("L05",getParameter("orderKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@OrderNo"/></a>	
		<%}else{%>
			<yfc:getXMLValue  binding="xml:/Shipment/@OrderNo"/>
		<%}%>
			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@OrderNo","xml:/Shipment/@OrderNo")%>/ >
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
			<input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ReleaseNo","xml:/Shipment/@ReleaseNo")%> />
		</td>
	<%}%>
	<td ></td>
	<td ></td>		
</tr>
<tr>
	<td class="detaillabel" nowrap="true" >
        <yfc:i18n>Has_Hazardous_Item(s)</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/Shipment/@HazardousMaterialFlag"/>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Merge_Node</yfc:i18n>
    </td>
	 <td class="protectedtext">
		<yfc:makeXMLInput name="MergeNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@MergeNode" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("MergeNodeKey"),"")%> ><yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@MergeNode"/></a>
    </td>
<% if(isVoid(resolveValue("xml:/Shipment/@BreakBulkLoadKey") ) )	{ %>
    <td></td>
	<td></td>
<% } else { %>
	<td class="detaillabel">
        <yfc:i18n>Break_Bulk_Node</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:makeXMLInput name="BreakBulkNodeKey" >
			<yfc:makeXMLKey binding="xml:/ShipNode/@ShipNode" value="xml:/Shipment/@BreakBulkNode" />
		</yfc:makeXMLInput>
<a <%=getDetailHrefOptions("L07",getParameter("BreakBulkNodeKey"),"")%> ><yfc:getXMLValue  binding="xml:/Shipment/@BreakBulkNode"/></a>		
	</td>
<% }%>
</tr>
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@DocumentType","xml:/Shipment/@DocumentType")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@OrderHeaderKey","xml:/Shipment/@OrderHeaderKey")%> />
	<input type="hidden" <%=getTextOptions("xml:/Shipment/@OrderAvailableOnSystem","xml:/Shipment/@OrderAvailableOnSystem")%> />
</table>
