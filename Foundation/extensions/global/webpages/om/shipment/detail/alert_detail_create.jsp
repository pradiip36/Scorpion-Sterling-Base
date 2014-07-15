<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
    // Default the enterprise code
    String enterpriseCode = (String) request.getParameter("xml:/Inbox/@EnterpriseCode");
    if (isVoid(enterpriseCode)) {
        enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
        request.setAttribute("xml:/Inbox/@EnterpriseCode", enterpriseCode);
    }

%>
	<table class="view" width="100%">
    <yfc:callAPI apiID="AP1"/>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>
	<tr>
        <td class="detaillabel" ><yfc:i18n>Alert_Type</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Inbox/@ExceptionType")%>>
                <yfc:loopOptions binding="xml:ExceptionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Inbox/@ExceptionType" isLocalized="Y"/>
            </select>
        </td>
		<td class="detaillabel" ><yfc:i18n>Owner</yfc:i18n></td>
       	<td>
		 <select class="combobox" <%=getComboOptions("xml:/Inbox/@EnterpriseCode")%>>
            <Option name="EnterpriseCode" value=""></Option>
			<Option name="EnterpriseCode" value='<%=resolveValue("xml:/Shipment/@EnterpriseCode")%>' selected><yfc:getXMLValue binding="xml:/Shipment/@EnterpriseCode"/></Option>
			<Option name="EnterpriseCode" value='<%=resolveValue("xml:/Shipment/@ShipNode")%>'><yfc:getXMLValue binding="xml:/Shipment/@ShipNode"/></Option>
			<Option name="EnterpriseCode" value='<%=resolveValue("xml:/Shipment/@ReceivingNode")%>'><yfc:getXMLValue binding="xml:/Shipment/@ReceivingNode"/></Option>
		</select>
    	<td class="detaillabel" ><yfc:i18n>Queue</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Inbox/@QueueId")%>>
                <yfc:loopOptions binding="xml:QueueList:/QueueList/@Queue" name="QueueName"
                value="QueueId" selected="xml:/Inbox/@QueueId"/>
            </select>
        </td>
	 </tr>
    <tr>
	    <td  class="detaillabel" ><yfc:i18n>Priority</yfc:i18n></td>
		<td>
        <select class="combobox" <%=getComboOptions("xml:/Inbox/@Priority")%>>
            <yfc:loopOptions binding="xml:Priority:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Inbox/@Priority" isLocalized="Y"/>
        </select>
	    </td>
        <td class="detaillabel">
            <yfc:i18n>Assign_To</yfc:i18n> 
        </td>
        <td nowrap="true">
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Inbox/@AssignedToUserId","xml:/Inbox/@AssignedToUserId")%>/>
			<img class="lookupicon" onclick="callLookup(this,'userlookup')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_User") %> />
        </td>
		<td class="detaillabel"><yfc:i18n>Description</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Inbox/@Description","xml:/Inbox/@Description")%>/>
        </td>
    </tr>
    <tr>
	    <td class="detaillabel"><yfc:i18n>Detail_Description</yfc:i18n></td>
		<td>
			<textarea class="unprotectedtextareainput" rows="3" cols="25" <%=getTextAreaOptions("xml:/Inbox/@DetailDescription","xml:/Inbox/@DetailDescription")%>></textarea>
        </td>
		<td/>
		<td/>
		<td>
		</td>
		<td>
		</td>
	
    </tr>
</table>
<input type="hidden" name="xml:/Inbox/@ShipmentNo" value='<%=resolveValue("xml:/Shipment/@ShipmentNo")%>'/>
<input type="hidden" name="xml:/Inbox/@ShipmentKey" value='<%=resolveValue("xml:/Shipment/@ShipmentKey")%>'/>
<input type="hidden" name="xml:/Inbox/@ShipNodeKey" value='<%=resolveValue("xml:/Shipment/@ShipNode")%>'/>

