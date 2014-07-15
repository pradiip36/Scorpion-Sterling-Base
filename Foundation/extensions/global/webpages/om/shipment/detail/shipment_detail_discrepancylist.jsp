<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>


<table class="table" width="100%" ID="ShipmentLines" suppressRowColoring="false">
<thead>
	<tr>
		<td class="tablecolumnheader">
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Line</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Item_ID</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Product_Class</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Unit_Of_Measure</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Discrepancy_Type</yfc:i18n>
		</td>

		<td class="tablecolumnheader">
			<yfc:i18n>Discrepancy_Qty</yfc:i18n>
		</td>

	</tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/ReceiptDiscrepancies/@ReceiptDiscrepancy" id="ReceiptDiscrepancy">
        <tr>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/Order/@OrderNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/OrderLine/@PrimeLineNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/OrderLine/Item/@ItemID"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/OrderLine/Item/@ProductClass"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/OrderLine/Item/@UnitOfMeasure"/>
			</td>
            <td class="tablecolumn" >
				<yfc:i18n><yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/@DiscrType"/></yfc:i18n>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceiptDiscrepancy:/ReceiptDiscrepancy/@DiscrQty"/>
			</td>
		</tr>
    </yfc:loopXML> 
</tbody>
</table>
