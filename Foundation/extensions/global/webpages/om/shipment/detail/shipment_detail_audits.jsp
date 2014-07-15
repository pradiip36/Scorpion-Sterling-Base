<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="shipment_audit_include.jspf"%>

<div style="height:136px;overflow:auto">
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Shipment/@ExpectedDeliveryDate")%>" nowrap="true">
            <yfc:i18n>Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/@Modifyts")%>">
            <yfc:i18n>Modified_By</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/AuditDetail/@AuditType")%>" nowrap="true">
            <yfc:i18n>Context</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/AuditDetail/Attributes/Attribute/@Name")%>" nowrap="true">
            <yfc:i18n>Modification</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/AuditDetail/Attributes/Attribute/@OldValue")%>" nowrap="true">
            <yfc:i18n>Old_Value</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/AuditDetail/Attributes/Attribute/@NewValue")%>" nowrap="true">
            <yfc:i18n>New_Value</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/@ReasonCode")%>">
            <yfc:i18n>Reason_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AuditList/Audit/@ReasonText")%>">
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>

    </tr>
</thead>
<tbody>
<%
 massageAuditListForShipment((YFCElement) request.getAttribute("AuditList"),(YFCElement) request.getAttribute("LineAuditList"));

%>
    <yfc:loopXML name="AuditList" binding="xml:/AuditList/@Audit" id="Audit"> 
<%
		String sNameToShow = "", sOldValue="", sNewValue="";
%>
		<yfc:loopXML name="Audit" binding="xml:/Audit/AuditDetail/Attributes/@Attribute" id="Attribute">
<% 				
		sNameToShow = sNameToShow + getI18N(resolveValue("xml:/Attribute/@Name")) + "<BR>";
		// the below fix was done only for Unplaces Quantity which was not shown in correct format in German locale.
		// so making this change specific to only Unplacedquantity attribute.
		//This fix is required for ContainerizedQuantity also. So adding it with || condition.
		if("UnplacedQuantity".equals(getI18N(resolveValue("xml:/Attribute/@Name"))) ||
			"ContainerizedQuantity".equals(getI18N(resolveValue("xml:/Attribute/@Name")))){
		String thisOldValue = resolveValue("xml:/Attribute/@OldValue");
		String thisNewValue = resolveValue("xml:/Attribute/@NewValue");
		String modifiedOldValue = "";
		String modifiedNewValue = "";
		double dOldValue = 0;
		double dNewValue = 0;
		try{
			dOldValue = Double.parseDouble(thisOldValue);
			modifiedOldValue = getLocalizedStringFromDouble(getLocale(), dOldValue);
		}catch(Exception e){
			// If formatting exception don't modify old value. 
			modifiedOldValue = getI18N(resolveValue("xml:/Attribute/@OldValue"));
		}
		try{
			dNewValue = Double.parseDouble(thisNewValue);
			modifiedNewValue = getLocalizedStringFromDouble(getLocale(), dNewValue);
		}catch(Exception e){
			// If formatting exception don't modify new value. 
			modifiedNewValue = getI18N(resolveValue("xml:/Attribute/@NewValue"));
		}		
		sOldValue = sOldValue + modifiedOldValue + "<BR>" ;
		sNewValue = sNewValue + modifiedNewValue + "<BR>";			 					
		}else{
			sOldValue = sOldValue + getI18N(resolveValue("xml:/Attribute/@OldValue")) + "<BR>" ;
			sNewValue = sNewValue + getI18N(resolveValue("xml:/Attribute/@NewValue")) + "<BR>";			 					
		}
		
%>
		</yfc:loopXML>
<%
		if(!isVoid(sNameToShow) && sNameToShow.length() >= 4){
			sNameToShow = sNameToShow.substring(0, sNameToShow.length()-4);
		}
		if(!isVoid(sOldValue) && sOldValue.length() >= 4){
			sOldValue = sOldValue.substring(0, sOldValue.length()-4);
		}
		if(!isVoid(sNewValue) && sNewValue.length() >= 4){
			sNewValue = sNewValue.substring(0, sNewValue.length()-4);
		}
%>
			<tr>
				<td class="tablecolumn" nowrap="true">
					<yfc:getXMLValueI18NDB name="Audit" 
					binding="xml:/Audit/@Modifyts"/>
				</td>       
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB name="Audit" 
					binding="xml:/Audit/@Modifyuserid"/>
				</td>
				<td class="tablecolumn" nowrap="true">
					<yfc:getXMLValue name="Audit"
					binding="xml:/Audit/AuditDetail/@AuditType"/>
				</td>		
				<td class="tablecolumn" nowrap="true">				
					<%=sNameToShow%>
				</td>
<%
			if("LoadKey".equals(sNameToShow)){				
%>
			<yfc:makeXMLInput name="loadKey" >
				<yfc:makeXMLKey binding="xml:/Load/@LoadKey" value="xml:/Audit/AuditDetail/@LoadKey" />
			</yfc:makeXMLInput>			
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L01",getParameter("loadKey"),"")%> ><%=sOldValue%></a>            
				</td>
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L01",getParameter("loadKey"),"")%> ><%=sNewValue%></a>	
				</td>
<%
			}else if("ToAddressKey".equals(sNameToShow)){
%>
			<yfc:makeXMLInput name="toAddressKey" >
				<yfc:makeXMLKey binding="xml:/PersonInfo/@PersonInfoKey" value="xml:/Audit/AuditDetail/@ToAddressKey"/>
			</yfc:makeXMLInput>			
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L02",getParameter("toAddressKey"),"")%> ><%=sOldValue%></a>            
				</td>
            <yfc:makeXMLInput name="toAddressKey1" >
				<yfc:makeXMLKey binding="xml:/PersonInfo/@PersonInfoKey" value="xml:/Audit/AuditDetail/@ToAddressKey1"/>
			</yfc:makeXMLInput>			
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L02",getParameter("toAddressKey1"),"")%> ><%=sNewValue%></a>	
				</td>
<%
					
			}else if("FromAddressKey".equals(sNameToShow)){
%>
			<yfc:makeXMLInput name="fromAddressKey" >
				<yfc:makeXMLKey binding="xml:/PersonInfo/@PersonInfoKey" value="xml:/Audit/AuditDetail/@FromAddressKey"/>
			</yfc:makeXMLInput>			
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L03",getParameter("fromAddressKey"),"")%> ><%=sOldValue%></a>            
				</td>
            <yfc:makeXMLInput name="fromAddressKey1" >
				<yfc:makeXMLKey binding="xml:/PersonInfo/@PersonInfoKey" value="xml:/Audit/AuditDetail/@FromAddressKey1"/>
			</yfc:makeXMLInput>			
				<td class="tablecolumn">            
					<a <%=getDetailHrefOptions("L03",getParameter("fromAddressKey1"),"")%> ><%=sNewValue%></a>	
				</td>
<%
					
			}else{	
%>
				<td class="tablecolumn" nowrap="true">            
					<%=sOldValue%>            
				</td>
				<td class="tablecolumn" nowrap="true">            
					<%=sNewValue%>
				</td>
<%
			}	
%>
				<td class="tablecolumn">
					<yfc:getXMLValue name="Audit" 
					binding="xml:/Audit/@ReasonCode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="Audit" 
					binding="xml:/Audit/@ReasonText"/>
				</td>
			</tr>	 
	</yfc:loopXML> 
</tbody>
</table>
</div>