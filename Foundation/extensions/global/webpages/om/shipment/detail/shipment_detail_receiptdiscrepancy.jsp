<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%!
	String sDisable = null;
	String isDisabled(boolean disable)
	{
		String s1 = null;
		if (disable)
			sDisable = "disabled";
		else
			sDisable = "enabled";

		return sDisable;
	}
%>


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

		<td class="tablecolumnheader">
			<yfc:i18n>Discrepancy_Reason</yfc:i18n>
		</td>
		<td class="tablecolumnheader">
			<yfc:i18n>Reason_Qty</yfc:i18n>
		</td>
	</tr>
</thead>

<tbody>
	<%int innerCtr = 0;%>
    <yfc:loopXML binding="xml:/ReceivingDiscrepancies/@ReceivingDiscrepancy" id="ReceivingDiscrepancy">
        <tr>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/Order/@OrderNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/OrderLine/@PrimeLineNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/OrderLine/Item/@ItemID"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/OrderLine/Item/@ProductClass"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/OrderLine/Item/@UnitOfMeasure"/>
			</td>
            <td class="tablecolumn" >
				<yfc:i18n><yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@DiscrType"/></yfc:i18n>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@DiscrQty"/>
			</td>

			<%
				boolean bFirstRow = true;

				// we have to enter ReceivingDiscrepancyDtl loop atleast once.  Following 'dummy' call of resolveValue will create ReceivingDiscrepancyDtl element if it doesnt exist.  
				String sCreateEl = resolveValue("xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/ReceivingDiscrepancyDtls/ReceivingDiscrepancyDtl/@hasValue");
			%>

		    <yfc:loopXML binding="xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/ReceivingDiscrepancyDtls/@ReceivingDiscrepancyDtl" id="ReceivingDiscrepancyDtl">

			<%if(!bFirstRow) {%>
					</tr>
					<tr>
						<td></td><td></td><td></td><td></td><td></td><td></td><td></td>
			<%	} else {
					bFirstRow = false;
				}
			%>
				<td class="tablecolumn" NewResetValue="true" nowrap="true" ShouldCopy="true">
					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/@Operation", "Modify")%>/>

					<img class="icon" IconName="addSplitLine" onclick="yfcSplitLine(this,'');return false;" <%=getImageOptions(YFSUIBackendConsts.ADD_ROW, "Split_Line")%> <%=isDisabled(isTrue("xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@ReasonEntryComplete"))%> />	
				
					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_"+ ReceivingDiscrepancyCounter + "/@ReceivingDiscrepancyKey", "xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@ReceivingDiscrepancyKey")%> />

					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_"+ ReceivingDiscrepancyCounter + "/@ShipmentKey", "xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@ShipmentKey")%> />

					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_"+ ReceivingDiscrepancyCounter + "/@OrderHeaderKey", "xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@OrderHeaderKey")%> />

					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/ReceivingDiscrepancyDtls/ReceivingDiscrepancyDtl_" + ReceivingDiscrepancyDtlCounter + "/@ReceivingDiscrepancyDtlKey", "xml:ReceivingDiscrepancyDtl:/ReceivingDiscrepancyDtl/@ReceivingDiscrepancyDtlKey")%> />

					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/@DiscrType", "xml:ReceivingDiscrepancy:/ReceivingDiscrepancy/@DiscrType")%> />
		
					<% request.setAttribute("ReceivingDiscrepancy", pageContext.getAttribute("ReceivingDiscrepancy")); %>		
					<yfc:callAPI apiID="AP2"/>

                    <% String recDiscrBinding = "xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/ReceivingDiscrepancyDtls/ReceivingDiscrepancyDtl_" + ReceivingDiscrepancyDtlCounter + "/@DiscrReasonCode"; %>
					<select class="combobox" NewResetValue="true"  NewName="true"  <%=getComboOptions(recDiscrBinding, "xml:ReceivingDiscrepancyDtl:/ReceivingDiscrepancyDtl/@DiscrReasonCode")%> <%=sDisable%>>
						<yfc:loopOptions binding="xml:ReceivingDiscrReasonList:/ReceivingDiscrReasonList/@ReceivingDiscrReason" 
							value="DiscrReasonCode" name="DiscrReasonDescription" selected="xml:ReceivingDiscrepancyDtl:/ReceivingDiscrepancyDtl/@DiscrReasonCode" targetBinding="<%=recDiscrBinding%>"/>
					</select>
				</td>
				<td class="tablecolumn" ShouldCopy="true" >
					<input type="hidden" <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/ReceivingDiscrepancyDtls/ReceivingDiscrepancyDtl_" + ReceivingDiscrepancyDtlCounter + "/@Operation", " ")%> id='Operation_<%=innerCtr%>' />

					<input class="combobox" NewResetValue="true"  type="text"  NewName="true"  <%=getTextOptions("xml:/ReceivingDiscrepancies/ReceivingDiscrepancy_" + ReceivingDiscrepancyCounter + "/ReceivingDiscrepancyDtls/ReceivingDiscrepancyDtl_" + ReceivingDiscrepancyDtlCounter + "/@ReasonQty",  "xml:ReceivingDiscrepancyDtl:/ReceivingDiscrepancyDtl/@ReasonQty" ) %> id='Qty_<%=innerCtr%>' <%=sDisable%>/>
				</td>
				<% innerCtr++; %>
		    </yfc:loopXML> 

		</tr>
    </yfc:loopXML> 
</tbody>
</table>
