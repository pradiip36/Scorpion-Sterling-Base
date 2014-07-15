<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script>
	function prepareInputWithSplServices(counter, chkValue, serValue){
		if(chkValue==true){
			document.all("xml:/Shipment/SpecialServices/SpecialService_"+counter+"/@SpecialServicesCode").value= serValue;	
		}
	}
</script>

<script language="JavaScript">
	function deleteBlankCheckBoxValues(){
		var i = 1;
		var SSCheckbox = document.all("xml:/Shipment/SpecialServices/SpecialService_" + i + "/@SpecialServicesCode");

		while (SSCheckbox) {
			if(!SSCheckbox.checked){
				SSCheckbox.name="REMOVED"+SSCheckbox.name
			}
			i++;
			SSCheckbox = document.all("xml:/Shipment/SpecialServices/SpecialService_" + i + "/@SpecialServicesCode");
		}    
		window.noRefreshForPopup="Y";
		return true;
	}
	document.body.attachEvent("onunload", deleteBlankCheckBoxValues);
</script>

<script language="javascript">
	function processCachedGroupDetails(){
        yfcSpecialChangeNames("SpecialServiceHeader", true);
	}
    document.body.attachEvent("onunload",processCachedGroupDetails); 
</script>

<%   Map mp  = new HashMap(); %>
<table class="table" editable="false" width="100%" cellspacing="0" id="SpecialServiceHeader">
	<thead> 
        <tr>
           <td class="tablecolumnheader"><yfc:i18n>Special_Services</yfc:i18n></td>
		    <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" 
				<%   if( !isVoid(resolveValue("xml:/Shipment/@ManifestKey"))){%>
				DISABLED
				<%}%>
				onclick="doCheckAll(this);"/>

            </td>
		 </tr>
    </thead>
    <tbody>
	
		<yfc:loopXML binding="xml:/Shipment/SpecialServices/@SpecialService" id="SpecialService">
			<%	    mp.put(resolveValue("xml:/SpecialService/@SpecialServicesCode"),"");%>
		</yfc:loopXML>
        <yfc:loopXML binding="xml:/SpecialServicesList/@SpecialServices" id="SpecialServices">
            <tr>
                <yfc:makeXMLInput name="SpecialService">
                    <yfc:makeXMLKey binding="xml:/SpecialServicesList/SpecialServices/@SpecialServicesDescription" value="xml:/SpecialServicesList/SpecialServices/@SpecialServicesDescription" />
                </yfc:makeXMLInput>
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB binding="xml:/SpecialServices/@SpecialServicesDescription"/>
				</td>
				<% String sSerCode = resolveValue("xml:/SpecialServices/@SpecialServicesCode");%>
					<td class="checkboxcolumn"> 

				<input type="checkbox" id="SpecialService" 
				<%   if( !isVoid(resolveValue("xml:/Shipment/@ManifestKey"))){%>
				DISABLED
				<%}%>	<%=getCheckBoxOptions("xml:/Shipment/SpecialServices/SpecialService_"+SpecialServicesCounter+"/@SpecialServicesCode" , "xml:/Shipment/SpecialServices/SpecialService_"+SpecialServicesCounter+"/@SpecialServicesCode", resolveValue("xml:/SpecialServices/@SpecialServicesCode"))%> 

				<%	if(mp.containsKey(resolveValue("xml:/SpecialServices/@SpecialServicesCode"))){ %>
					checked
				<% }%>
						yfcCheckedValue='<%=resolveValue("xml:/SpecialServices/@SpecialServicesCode")%>' yfcUnCheckedValue=' ' />
                </td>

	           </tr>
		</yfc:loopXML> 
   </tbody>
</table>
<input type="hidden" <%=getTextOptions("xml:/Shipment/SpecialServices/@Replace","Y")%>/>