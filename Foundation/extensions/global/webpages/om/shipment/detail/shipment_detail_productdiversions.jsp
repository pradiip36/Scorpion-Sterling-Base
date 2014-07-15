<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table class="table" border="0" cellspacing="0" width="100%">
<thead>
<tr>
  
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@DemandLocationId")%>">
	<yfc:i18n>Location</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@ForActivityCode")%>">
	<yfc:i18n>Activity_Code</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@ItemId")%>">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@ProductClass")%>">
	<yfc:i18n>PC</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@UnitOfMeasure")%>">
	<yfc:i18n>UOM</yfc:i18n>
	</td>

	<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@Priority")%>">
	<yfc:i18n>Priority</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@DemandQty")%>">
	<yfc:i18n>Demand_Quantity</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@SatisfiedQty")%>">
	<yfc:i18n>Satisfied_Quantity</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@DemandSatisfied")%>">
	<yfc:i18n>Demand_Satisfied</yfc:i18n>
	</td>

</tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/ActivityDemands/@ActivityDemand" id="ActivityDemand" > 
    <tr> 
	<% 
		String ItemID =  resolveValue("xml:/ActivityDemand/@ItemId");
		if (!isVoid(ItemID)) { %>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@DemandLocationId"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@ForActivityCode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@ItemId"/></td>  		
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@ProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@UnitOfMeasure"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@Priority"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@DemandQty"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@SatisfiedQty"/></td>
	    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@DemandSatisfied"/></td>
	<%}%>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
