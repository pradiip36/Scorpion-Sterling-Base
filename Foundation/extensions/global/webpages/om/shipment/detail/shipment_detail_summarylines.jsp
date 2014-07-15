<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="table" width="100%">
<thead>
    <tr> 
		<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/Order/@ShipmentLineNo")%>">
		<yfc:i18n>Shipment_Line_#</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNo")%>">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@PrimeLineNo")%>">
            <yfc:i18n>Line_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ReleaseNo")%>">
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
     
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@CountryOfOrigin")%>">
			<yfc:i18n>COO</yfc:i18n>
		</td>
		<td class=tablecolumnheader style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@NetWeight")%>">
			<yfc:i18n>Net_Weight</yfc:i18n>
		</td>
		<td class=tablecolumnheader style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@NetWeightUom")%>">
			<yfc:i18n>Net_Weight_UOM</yfc:i18n>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
 <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
		<yfc:makeXMLInput name="OrderHeaderKey" >
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey" />
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="OrderLineKey" >
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/ShipmentLine/@OrderLineKey" />
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="OrderReleaseKey" >
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey" />
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@ReleaseNo" value="xml:/ShipmentLine/@ReleaseNo" />
        </yfc:makeXMLInput>
    <tr>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ShipmentLineNo"/>
       </td>
		<td class="tablecolumn">
			<% if(showOrderNo("Shipment","Shipment")) {%>
	            <a <%=getDetailHrefOptions("L01",getParameter("OrderHeaderKey"),"")%> >
		            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/>  
			    </a>
			<%} else {%>
				<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/>  
			<%}%>
        </td>
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:ShipmentLine:/ShipmentLine/@PrimeLineNo")%>">
			<% if(showOrderLineNo("Shipment","Shipment")) {%>
	            <a <%=getDetailHrefOptions("L02",getParameter("OrderLineKey"),"")%> >
		            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
			    </a>
			<%} else {%>
				<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
			<%}%>
        </td>       
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:ShipmentLine:/ShipmentLine/@ReleaseNo")%>">
		<%if(!equals("0",resolveValue("xml:/ShipmentLine/@ReleaseNo"))){%>
			<a <%=getDetailHrefOptions("L03",getParameter("OrderReleaseKey"),"")%> >
                <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ReleaseNo"/>            
            </a>
		<%}%>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
        </td>
		<td class="tablecolumn">
			<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@CountryOfOrigin"/>
		</td>
		<td class="numerictablecolumn">
			<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@NetWeight"/>
		</td>
		<td class="tablecolumn">
			<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@NetWeightUom"/>
		</td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:ShipmentLine:/ShipmentLine/@Quantity")%>">
            <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@Quantity"/>
        </td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
