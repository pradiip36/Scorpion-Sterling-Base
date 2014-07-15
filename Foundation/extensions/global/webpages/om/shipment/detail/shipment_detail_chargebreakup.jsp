<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>
<script language="javascript">
window.attachEvent("onload", IgnoreChangeNames);
document.body.attachEvent("onunload", processSaveRecordsForCharges);
</script>

<%
boolean editable = true;
Map cChargeCategory = getChargeCategoryMap((YFCElement)request.getAttribute("ChargeCategoryList"), (YFCElement)request.getAttribute("ChargeNameList"));

Set cChargeCat = cChargeCategory.keySet();
for(Iterator it2 = cChargeCat.iterator(); it2.hasNext(); )	{
	String sChargeCat = (String)it2.next();
	YFCElement e = (YFCElement)cChargeCategory.get(sChargeCat);
	request.setAttribute(sChargeCat, e);
}

%>

<table class="table" ID="ChargeBreakup" cellspacing="0" width="100%" <%if (editable) {%> initialRows="1" <%} %> >
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">&nbsp;</td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
		<td class="numerictablecolumnheader" sortable="no"> <yfc:i18n>Estimated_Charge</yfc:i18n></td>   
        <td class="numerictablecolumnheader" sortable="no"> <yfc:i18n>Actual_Charge</yfc:i18n></td>   
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentCharges/@ShipmentCharge" id="ShipmentCharge"> 
    <tr>
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="tablecolumn">            
            <input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_" + ShipmentChargeCounter + "/@ChargeName", "xml:/ShipmentCharge/@ChargeName")%>/>
            <input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_" + ShipmentChargeCounter + "/@ChargeCategory", "xml:/ShipmentCharge/@ChargeCategory")%>/>

			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/ShipmentCharge/@ChargeCategory", true)%>
        </td>
		<td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/ShipmentCharge/@ChargeCategory"), resolveValue("xml:/ShipmentCharge/@ChargeName"), 
				(YFCElement)request.getAttribute("ChargeNameList") )%>
		</td>

		<td class="numerictablecolumn">
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
                <input type="text" <%=yfsGetTextOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_" + ShipmentChargeCounter + "/@EstimatedCharge","xml:/ShipmentCharge/@EstimatedCharge", "xml:/Shipment/AllowedModifications")%>/>
                &nbsp;<%=curr0[1]%>
        </td>
		<td class="numerictablecolumn">
            <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;
                <input type="text" <%=yfsGetTextOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_" + ShipmentChargeCounter + "/@ActualCharge","xml:/ShipmentCharge/@ActualCharge", "xml:/Shipment/AllowedModifications")%>/>
                &nbsp;<%=curr1[1]%>
        </td>
    </tr>                                                        
    </yfc:loopXML>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" ID="DONTHIDE">&nbsp;</td>

        <td class="tablecolumn" ID="DONTHIDE" >
            <input type="hidden" name= "xml:/Shipment/ShipmentCharges/ShipmentCharge_/@Action" value="Create"/>
            <select  onChange="displayChargeNameDropDown(this)" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_/@ChargeCategory", "xml:/Shipment/AllowedModifications", "SHIPMENT_MODIFY_CHARGE", "combo")%>>
                <yfc:loopOptions binding="xml:/ChargeCategoryList/@ChargeCategory" name="Description" isLocalized="Y" value="ChargeCategory"/>
            </select>
        </td>
		<%	
			Set cCC = cChargeCategory.keySet();
			boolean bShowEmptyTD = true;
			for(Iterator it2 = cCC.iterator(); it2.hasNext(); )	{
				String sChargeCat = (String) it2.next();
				String loopBinding = "xml:" + sChargeCat + ":/ChargeNameList/@ChargeName";
				if(bShowEmptyTD)	{
		%>			<td></td>
		<%			bShowEmptyTD = false;
				}

				if(isTrue("xml:/Rules/@RuleSetValue") )	{		%>
				<td class="tablecolumn" style='display:none' ID="<%=sChargeCat%>" >

					<select  <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_/@ChargeName", "xml:/Shipment/AllowedModifications", "SHIPMENT_MODIFY_CHARGE", "combo")%>>
						<yfc:loopOptions binding="<%=loopBinding%>" name="Description" value="ChargeName" isLocalized="Y"/>
					</select>
				</td>
			<%	}	else	{	%>
					<td ID="<%=sChargeCat%>" style='display:none'>
						<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_/@ChargeName")%>	/> 
					</td>
		<%		}
			}	%>

        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_/@EstimatedCharge", "xml:/Shipment/AllowedModifications", "SHIPMENT_MODIFY_CHARGE", "text")%>/>
            &nbsp;<%=curr2[1]%>
        </td>
        <td class="numerictablecolumn" ID="DONTHIDE">
            <% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%>&nbsp;
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentCharges/ShipmentCharge_/@ActualCharge", "xml:/Shipment/AllowedModifications", "SHIPMENT_MODIFY_CHARGE", "text")%>/>
            &nbsp;<%=curr3[1]%>
        </td>

    </tr>
	<%if (editable) {%>
    <tr>
    	<td nowrap="true" colspan="5">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>
