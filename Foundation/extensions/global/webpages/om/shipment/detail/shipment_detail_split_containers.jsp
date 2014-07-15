<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<% 
   String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
   boolean outboundShipment = false;
   if(equals(sAppCode,"omd")){
       outboundShipment = true;
   }

%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@ContainerNo")%>">
            <yfc:i18n>Container_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader">
			<yfc:i18n>Container_Type</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Status</yfc:i18n>
		</td>
		<%if(outboundShipment) {%>
				<td class="tablecolumnheader">
					<yfc:i18n>Manifested</yfc:i18n>
				</td>
		<%}%>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@TrackingNo")%>">
            <yfc:i18n>Tracking_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@ContainerScm")%>">
            <yfc:i18n>Container_SCM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no" nowrap="true">
            <yfc:i18n>Net_Weight</yfc:i18n>
                  
        </td>
        <td class="tablecolumnheader" sortable="no" nowrap="true">
            <yfc:i18n>Gross_Weight</yfc:i18n>
        
        </td>
		<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Container/@ActualFreightCharge")%>">
            <yfc:i18n>Freight_Charge</yfc:i18n>
        </td>
				<td class="tablecolumnheader">
			<yfc:i18n>Ship_Date</yfc:i18n>
		</td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/Containers/@Container" id="Container" > 
        <yfc:makeXMLInput name="ShipmentContainerKey" >
            <yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/Containers/Container/@ShipmentContainerKey" value="xml:/Container/@ShipmentContainerKey" />
	        <yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
		    <yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode"/>
			<yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@ShipNode" value="xml:/Shipment/@ShipNode"/>
		
        </yfc:makeXMLInput>
    <tr>
        <td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("ShipmentContainerKey")%>' name="ContainerKey"
			yfcMultiSelectCounter='<%=ContainerCounter%>' 
			yfcMultiSelectValue1='<%=resolveValue("xml:/Container/@ShipmentContainerKey")%>'/>
        </td>
      
        <td class="tablecolumn">
            <a <%=getDetailHrefOptions("L01",getParameter("ShipmentContainerKey"),"")%> ><yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerNo"/></a>
        </td>
        <td class="tablecolumn">
		<yfc:getXMLValue binding="xml:/Container/@ContainerType"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValueI18NDB binding="xml:/Container/Status/@Description"/>
		</td>
		<%if(outboundShipment) {%>
			<td class="tablecolumn">
				<yfc:i18n><yfc:getXMLValue binding="xml:/Container/@IsManifested"/></yfc:i18n>
			</td>
		<%}%>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@TrackingNo"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerScm"/>
        </td>
        <td class="numerictablecolumn"	sortValue="<%=getNumericValue("xml:Container:/Container/@ContainerNetWeight")%>">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerNetWeight"/>&nbsp;
       &nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ContainerNetWeightUOM" />&nbsp;
        </td>
        <td class="numerictablecolumn"	sortValue="<%=getNumericValue("xml:Container:/Container/@ContainerGrossWeight")%>">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerGrossWeight"/>&nbsp;
        &nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ContainerGrossWeightUOM" />&nbsp;
        </td>
        <td class="tablecolumn">
	        <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ActualFreightCharge" />&nbsp;
		    <%=curr0[1]%>
        </td>
		<td class="tablecolumn">
			<yfc:getXMLValue binding="xml:/Container/@ShipDate"/>
		</td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
