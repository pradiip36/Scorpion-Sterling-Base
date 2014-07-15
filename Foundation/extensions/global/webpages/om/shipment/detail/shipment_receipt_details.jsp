<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>


<%
	String docType= getValue("Shipment","xml:/Shipment/@DocumentType");
    boolean enableShipmentLines=true;
    if(!equals(getValue("Shipment", "xml:/Shipment/@EnableShipmentLines"), "Y"))
        enableShipmentLines=false;
   
%>
<table class="table" width="100%" initialRows="10">
<thead>
<tr>
 
	<td class=tablecolumnheader sortable="no" >	<yfc:i18n>Order_#</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="no" >	<yfc:i18n>Line_#</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="no" >	<yfc:i18n>Release_#</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="yes" ><yfc:i18n>Item_ID</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="no" >	<yfc:i18n>PC</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="no" >	<yfc:i18n>UOM</yfc:i18n></td>

	<td class=tablecolumnheader sortable="yes"><yfc:i18n>Quantity_Shipped</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="yes"><yfc:i18n>Quantity_Received</yfc:i18n>	</td>

	<td class=tablecolumnheader sortable="yes"><yfc:i18n>Discrepancy</yfc:i18n>	</td>

</tr>
</thead>
<tbody>

    <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
    <tr>
	   <td class="checkboxcolumn" >
			<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
	   </td>

       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ShipmentLineNo"/>  
       </td>
       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/Order/@OrderNo"/> 
	   </td>       
       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/OrderLine/@PrimeLineNo"/> 
	   </td>       
       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/OrderRelease/@ReleaseNo"/> 
	   </td>       

       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
       </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
       </td>

	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
       </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@CountryOfOrigin"/>
       </td>

    </tr>
    </yfc:loopXML> 

</tbody>

</table>
