<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table width="100%" class="view">
<tr>
	<td class="detaillabel" >
		<yfc:i18n>Carrier_Account_#</yfc:i18n>
	</td>
	<td  class="protectedtext">
		<yfc:getXMLValue binding="xml:/Shipment/@CarrierAccountNo"/>
	</td>
</tr>
<tr>
	<td class="detaillabel" >
		<yfc:i18n>Freight_Terms</yfc:i18n>
	</td>
	<td  class="protectedtext">
		<%=getComboText("xml:FreightTermsList:/FreightTermsList/@FreightTerms", "ShortDescription", "FreightTerms", "xml:/Shipment/@FreightTerms",true)%>
	</td>
</tr>
<tr>
		<td class="detaillabel" >
			<yfc:i18n>COD_Pay_Method</yfc:i18n>
		</td>
		<td >
				<select  <%=yfsGetComboOptions("xml:/Shipment/@CODPayMethod", "xml:/Shipment/AllowedModifications")%>>
							<yfc:loopOptions binding="xml:CODPayMethodList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					 value="CodeValue" selected="xml:/Shipment/@CODPayMethod" isLocalized="Y"/>
				</select>
		</td>
</tr>
<tr>
	<td class="detaillabel" nowrap="true">
        <yfc:i18n>Estimated_Shipment_Charges</yfc:i18n>
    </td>
    <td class="protectednumber" nowrap="true" >
        <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@TotalEstimatedCharge"/>
        &nbsp;
        <%=curr0[1]%>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Actual_Shipment_Charges</yfc:i18n>
    </td>
    <td class="protectednumber" nowrap="true" >
        <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@TotalActualCharge"/>
        &nbsp;
        <%=curr1[1]%>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Freight_Charge</yfc:i18n>
    </td>
    <td class="protectednumber" nowrap="true" >
        <% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%> 
        &nbsp;
        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ActualFreightCharge"/>
        &nbsp;
        <%=curr2[1]%>
    </td>
</tr>
</table>