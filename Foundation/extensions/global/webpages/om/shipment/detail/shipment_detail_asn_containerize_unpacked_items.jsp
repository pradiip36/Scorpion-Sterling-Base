<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<table class="table" width="100%" >
<thead>
<tr>
    <td sortable="no" class="checkboxheader">&nbsp;</td>
    
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Order_#</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Line_#</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Release_#</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Unit_Of_Measure</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Product_Class</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Requested_Lot_#</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Unpacked_Quantity</yfc:i18n>
	</td>

	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:")%>">
	<yfc:i18n>Quantity</yfc:i18n>
	</td>

</tr>
</thead>
<tbody>
</tbody>

</table>
