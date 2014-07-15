<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_status_change_include.jspf" %>
<table class="view">
<tr>
    <td class="detaillabel"><yfc:i18n>From_Status</yfc:i18n></td>
    <td class="protectedtext">
		<yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@Description"/>
		<input type="hidden" name="xml:/Shipment/@TransactionId" value="<%=request.getParameter("xml:/Transaction/@TransactionId")%>" />
	</td>
</tr> 
<tr>    
    <td class="detaillabel"><yfc:i18n>To_Status</yfc:i18n></td>
    <td>
		<select name="xml:/Shipment/@BaseDropStatus" class="combobox">
			<yfc:loopXML name="Shipment" binding="xml:/Shipment/AllowedTransactions/@Transaction" id="Transaction"> 
			<% if(equals(resolveValue("xml:Transaction:/Transaction/@ShowTransaction"),"Y")) { %>
				<yfc:loopOptions binding="xml:Transaction:/Transaction/TransactionDropStatusList/TransactionDropStatus/@Status" name="Description" value="Status" isLocalized="Y"/>
			 <% } %>
			</yfc:loopXML>
		</select>
	</td>
</tr>
<tr>
    <td class="detaillabel">
        <yfc:i18n>Reason_Code</yfc:i18n>
    </td>
    <td>
        <select name="xml:/Shipment/@ReasonCode" class="combobox" >
            <yfc:loopOptions binding="xml:ReasonCode:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
        </select>
    </td>
</tr> 
<tr>    
    <td class="detaillabel">
        <yfc:i18n>Reason_Text</yfc:i18n>
    </td>
    <td rowspan="3">
        <textarea class="unprotectedtextareainput" rows="3" cols="35" <%=getTextAreaOptions("xml:/Shipment/@ReasonText", "")%>></textarea>
    </td>
</tr>
</table>