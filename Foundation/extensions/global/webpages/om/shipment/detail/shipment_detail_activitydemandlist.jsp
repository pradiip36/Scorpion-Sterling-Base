<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<table class="table" width="100%" editable="true" ID="ActivityDemandList">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ShipmentLineNo")%>"><yfc:i18n>Shipment_Line_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@DemandLocationId")%>"><yfc:i18n>Location_Id</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ForActivityCode")%>"><yfc:i18n>Activity_Code</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@PalletId")%>"><yfc:i18n>Pallet_Id</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@CaseId")%>"><yfc:i18n>CaseId</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@ItemId")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ActivityDemand/@Priority")%>"><yfc:i18n>Priority</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/ActivityDemand/@DemandQty")%>"><yfc:i18n>Demand_Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/ActivityDemand/@SatisfiedQty") %>"><yfc:i18n>Satisfied_Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/ActivityDemand/@DemandSatisfied")%>"><yfc:i18n>Demand_Satisfied</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:ActivityDemandList:/ActivityDemands/@ActivityDemand" id="ActivityDemand">
        <tr>
            <yfc:makeXMLInput name="activitydemandKey">
                    <yfc:makeXMLKey binding="xml:/ActivityDemand/@ActivityDemandKey" value="xml:ActivityDemand:/ActivityDemand/@ActivityDemandKey"/>
            </yfc:makeXMLInput>
            <td class="checkboxcolumn">
                <input type="checkbox" value='<%=getParameter("activitydemandKey")%>' name="chkEntityKey"
                />
            </td>
            <td class="tablecolumn" >
	            <a <%=getDetailHrefOptions("L01",getParameter("activitydemandKey"),"")%>>
					<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/ShipmentLine/@ShipmentLineNo"/>
				</a>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@DemandLocationId"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@ForActivityCode"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@PalletId"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@CaseId"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@ItemId"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@Priority"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@DemandQty"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@SatisfiedQty"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:ActivityDemand:/ActivityDemand/@DemandSatisfied"/>
			</td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
