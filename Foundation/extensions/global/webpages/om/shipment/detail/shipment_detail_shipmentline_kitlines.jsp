<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="shipment.jspf" %>
<yfc:callAPI apiID="AP1"/>
<table class="table" width="100%" id="LineDetails">

<%	if(equals("BUNDLE",resolveValue("xml:/ShipmentLines/ShipmentLine/@KitCode"))) {
		YFCElement ShipmentInput = YFCDocument.parse("<Shipment/>").getDocumentElement();		ShipmentInput.setAttribute("ShipmentKey",resolveValue("xml:/ShipmentLines/ShipmentLine/@ShipmentKey"));
		YFCElement shipmentTemplate = YFCDocument.parse("<Shipment ShipmentHeaderKey=\"\"><ShipmentLines><ShipmentLine  ShipmentLineKey=\"\" ShipmentKey=\"\" ParentShipmentLineKey=\"\" KitCode=\"\" ItemID=\"\" ItemDesc=\"\" UnitOfMeasure=\"\" ProductClass=\"\" Quantity=\"\" WaveNo=\"\" KitQty=\"\" ReceivedQuantity=\"\" ></ShipmentLine></ShipmentLines></Shipment>").getDocumentElement();
		%>		
			 <yfc:callAPI apiName="getShipmentDetails" inputElement="<%=ShipmentInput%>"  outputNamespace="Shipment" templateElement="<%=shipmentTemplate%>"/> 
		<% 
		YFCElement ShipmentLineDoc = (YFCElement)request.getAttribute("ShipmentLine");
		YFCElement ShipmentDoc = (YFCElement)request.getAttribute("Shipment");
		rearrangeBundleComponents(ShipmentDoc,ShipmentLineDoc);
		} 
%>

<thead>
<tr>
	<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
    <td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemDesc")%>">
	<yfc:i18n>Description</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass")%>">
	<yfc:i18n>PC</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure")%>">
	<yfc:i18n>UOM</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes"
	style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@KitQty")%>"><yfc:i18n>Qty_Per_Kit</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@WaveNo")%>">
	<yfc:i18n>Wave_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@Quantity")%>">
	<yfc:i18n>Quantity</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ReceivedQuantity")%>">
	<yfc:i18n>Received_Quantity</yfc:i18n>
	</td>
</tr>
</thead>
<tbody>

<%if((equals("BUNDLE",resolveValue("xml:Kit:/ShipmentLines/ShipmentLine/@KitCode")))){
	%>
	<yfc:loopXML binding="xml:Shipment:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
		<yfc:makeXMLInput name="shipmentLineKey">
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentKey" value="xml:/ShipmentLine/@ShipmentKey"/>
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ItemID" value="xml:/ShipmentLine/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ProductClass" value="xml:/ShipmentLine/@ProductClass"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@EnterpriseCode" value="xml:/ShipmentLine/Shipment/@EnterpriseCode"/>
        </yfc:makeXMLInput>
    <tr>
	   <td class="checkboxcolumn" >
			<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
	   </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@AppendedBundleComponentID"/>
       </td>
	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemDesc"/>
       </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
       </td>

	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
       </td>
        <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@KitQty"/>
       </td>
       <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@WaveNo"/>
       </td>
       <td class="numerictablecolumn">
		   <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@Quantity"/>
       </td>
	    <td class="numerictablecolumn">
		   <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ReceivedQuantity"/>
       </td>
	</yfc:loopXML>
<%} else { %>

    <yfc:loopXML binding="xml:Kit:/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
	<%if(!(equals("0",resolveValue("xml:/ShipmentLine/@ShipmentSubLineNo")))){
	%>
		<yfc:makeXMLInput name="shipmentLineKey">
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentKey" value="xml:/ShipmentLine/Shipment/@ShipmentKey"/>
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ItemID" value="xml:/ShipmentLine/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ProductClass" value="xml:/ShipmentLine/@ProductClass"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@EnterpriseCode" value="xml:/ShipmentLine/Shipment/@EnterpriseCode"/>
        </yfc:makeXMLInput>
    <tr>
	   <td class="checkboxcolumn" >
			<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
	   </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
       </td>
	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemDesc"/>
       </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
       </td>

	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
       </td>
        <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@KitQty"/>
       </td>
       <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@WaveNo"/>
       </td>
       <td class="numerictablecolumn">
		   <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@Quantity"/>
       </td>
	   <td class="numerictablecolumn">
		   <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ReceivedQuantity"/>
       </td>
	   <%}%>
	 </yfc:loopXML> 
	 <%}%>
	</tr>
</tbody>
</table>