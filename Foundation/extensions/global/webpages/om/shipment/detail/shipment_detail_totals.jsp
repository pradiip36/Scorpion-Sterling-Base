<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%String docType= getValue("Shipment","xml:/Shipment/@DocumentType");%>
<%String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	boolean outboundShipment = false;
	if(equals(sAppCode,"omd")){
		outboundShipment = true;
	}
%>
<script language="javascript">
function processSaveRecordsWeight() {
        yfcSpecialChangeNames("WeightDetails", false);
}
</script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsWeight);
</script>
<table width="100%" class="view" id="WeightDetails">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Weight</yfc:i18n>
    </td>
	<td nowrap="true">

		<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@TotalWeight", "xml:/Shipment/AllowedModifications")%>/>


	</td>
	<td>

		<select  <%=yfsGetComboOptions("xml:/Shipment/@TotalWeightUOM", "xml:/Shipment/AllowedModifications")%>>
			<yfc:loopOptions binding="xml:WeightUomList:/UomList/@Uom" name="UomDescription" value="Uom" selected="xml:/Shipment/@TotalWeightUOM" isLocalized="Y"/>
		</select>
	</td>	
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Volume</yfc:i18n>
    </td>
	<td nowrap="true">

		<input type="text"  <%=yfsGetTextOptions("xml:/Shipment/@TotalVolume", "xml:/Shipment/AllowedModifications")%>/>


	</td>
	<td>

		<select  <%=yfsGetComboOptions("xml:/Shipment/@TotalVolumeUOM", "xml:/Shipment/AllowedModifications")%>>
			<yfc:loopOptions binding="xml:VolumeUomList:/UomList/@Uom" name="UomDescription" value="Uom" selected="xml:/Shipment/@TotalVolumeUOM" isLocalized="Y"/>
		</select>
		
	</td>	
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>No_of_Containers</yfc:i18n>
    </td>
	<% if (outboundShipment){%>
	    <td class="protectedtext">
		  <yfc:getXMLValue binding="xml:ShipmentContainerList:/Containers/@TotalNumberOfRecords"/>
	    </td>
	<%}else {%>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/Shipment/Containers/@TotalNumberOfRecords"/>	
	    </td>
	<%}%>
	<td>&nbsp;</td>
</tr>
</table>