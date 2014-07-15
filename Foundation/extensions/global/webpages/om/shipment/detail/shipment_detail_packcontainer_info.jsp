<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<table width="100%" class="view">
<tr>
	 <yfc:makeXMLInput name="shipmentContainerKey">
		<yfc:makeXMLKey binding="xml:/Container/@ShipmentContainerKey" value="xml:packedShipment:/Shipment/Containers/Container/@ShipmentContainerKey" />
		<yfc:makeXMLKey binding="xml:/Container/@ShipmentKey" value="xml:packedShipment:/Shipment/@ShipmentKey" />
	</yfc:makeXMLInput>
    <td class="detaillabel" >
        <yfc:i18n>Container_Type</yfc:i18n> 
		<input type="hidden" name="hidLabelEntityKey" value='<%=getParameter("shipmentContainerKey")%>' />&nbsp;
		<input type="hidden" <%=getTextOptions("hidContainerKey","xml:packedShipment:/Shipment/Containers/Container/@ShipmentContainerKey")%> />&nbsp;
		<input type="hidden" <%=getTextOptions("xml:/Shipment/Containers/Container/@Scac","xml:/Shipment/@SCAC")%> />&nbsp;
		<input type="hidden" <%=getTextOptions("xml:/Shipment/Containers/Container/@CarrierServiceCode","xml:/Shipment/@CarrierServiceCode")%> />&nbsp;
		<input type="hidden" <%=getTextOptions("xml:/Shipment/Containers/Container/@ShipToKey","xml:/Shipment/@ToAddressKey")%> />&nbsp;
    </td>
	<td nowrap="true">
        <input type="Radio" class="radiobutton" <%=getRadioOptions("xml:/Shipment/Containers/Container/@ContainerType", "Case", "Case" )%> />
        <yfc:i18n>Case</yfc:i18n>
		<input type="Radio" class="radiobutton" 
        <%=getRadioOptions("xml:/Shipment/Containers/Container/@ContainerType", "Case" , "Pallet")%> />
        <yfc:i18n>Pallet</yfc:i18n>
	</td>
    <td class="detaillabel" >
        <yfc:i18n>Container_SCM</yfc:i18n> 
    </td>
	<td>
  	    <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@ContainerScm", "", "")%>/>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Tracking_#</yfc:i18n> 
    </td>
    <td>
  	    <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@TrackingNo", "", "")%>/>  	
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Declared_Insurance_Value</yfc:i18n> 
    </td>
    <td class="protectedtext">
		<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>
		&nbsp;
  	    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@DeclaredValue", "", "")%>/>  	
        &nbsp;
        <%=curr0[1]%>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Gross_Weight</yfc:i18n> 
    </td>
    <td>
  	    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@ContainerGrossWeight", "", "")%>/>  
		<select class="combobox" <%=getComboOptions("xml:/Shipment/Containers/Container/@ContainerGrossWeightUOM")%>>
			<yfc:loopOptions binding="xml:WeightUomList:/UomList/@Uom" name="UomDescription" value="Uom" isLocalized="Y"/>
		</select>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Net_Weight</yfc:i18n> 
    </td>
    <td>
        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/Containers/Container/@ContainerNetWeight"/>
        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/Containers/Container/@ContainerNetWeightUOM"/>
    </td>
</tr>
<tr>

	<td class="detaillabel" >
        <yfc:i18n>Height</yfc:i18n> 
    </td>
    <td>
  	    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@ContainerHeight", "", "")%>/> 
                <select class="combobox" <%=getComboOptions("xml:/Shipment/Containers/Container/@ContainerHeightUOM")%>>
			<yfc:loopOptions binding="xml:DimensionUomList:/UomList/@Uom" name="UomDescription" value="Uom" isLocalized="Y"/>
		</select>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Width</yfc:i18n> 
    </td>
    <td>
  	    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@ContainerWidth", "", "")%>/> 
		<select class="combobox" <%=getComboOptions("xml:/Shipment/Containers/Container/@ContainerWidthUOM")%>>
			<yfc:loopOptions binding="xml:DimensionUomList:/UomList/@Uom" name="UomDescription" value="Uom" isLocalized="Y"/>
		</select>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Length</yfc:i18n> 
    </td>
    <td>
  	    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Shipment/Containers/Container/@ContainerLength", "", "")%>/> 
		<select class="combobox" <%=getComboOptions("xml:/Shipment/Containers/Container/@ContainerLengthUOM")%>>
			<yfc:loopOptions binding="xml:DimensionUomList:/UomList/@Uom" name="UomDescription" value="Uom" isLocalized="Y"/>
		</select>
	</td>
</tr>
</table>