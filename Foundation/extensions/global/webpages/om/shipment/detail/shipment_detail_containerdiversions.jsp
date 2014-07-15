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

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@PalletId")%>">
	<yfc:i18n>Pallet_ID</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/ActivityDemand/@CaseId")%>">
	<yfc:i18n>Case_ID</yfc:i18n>
	</td>

	<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@Priority")%>">
	<yfc:i18n>Priority</yfc:i18n>
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
		String CaseID =  resolveValue("xml:/ActivityDemand/@CaseId");
		String PalletID =  resolveValue("xml:/ActivityDemand/@PalletId");
		if (!isVoid(CaseID)||!isVoid(PalletID)) { %>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@DemandLocationId"/></td>
		<td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@ForActivityCode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@PalletId"/></td>     
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@CaseId"/></td>  
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@Priority"/></td>
	    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ActivityDemand/@DemandSatisfied"/></td>
		<%}%>
    </tr>
    </yfc:loopXML> 

</tbody>

</table>
