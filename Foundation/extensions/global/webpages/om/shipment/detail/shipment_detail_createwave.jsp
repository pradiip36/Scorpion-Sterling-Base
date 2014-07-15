<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@include file="/dm/deliveryplan/detail/deliveryplan_detail_include.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>

<script language="javascript">
	yfcDoNotPromptForChanges(true);
	function showWaveDetail()
	{
		var sEntityKey = document.all("hidWaveDetailKey");
		var sVal = document.all("hidWaveKey").value;
		if(sVal != "")
		{
			//alert("sEntityKey.value" + sEntityKey.value);
			showDetailForOnAdvancedList('wave', ' ', sEntityKey.value); 
		}
	}

    function checkBlankAttribute() {

		if(!document.all("xml:/Wave/@Node").value){
            alert(YFCMSG077);//Node_not_Passed
			return false;
		}else{
			var tmp = document.all("createOrModifyWaveRadio");
			if (tmp[0].checked)			{
				if(!document.all("xml:/Wave/@ShipmentGroupId").value){
					alert(YFCMSG099);//Shipment_Group_not_Passed
					return false;
				}
			}
			else if (tmp[1].checked)			{
				if(!document.all("xml:/Wave/@WaveNo").value){
					alert(YFCMSG100);//Wave Number Not Passed
					return false;
				}
			}
		}
		return true;
    }

</script>

<script>
    window.attachEvent("onload",showWaveDetail);
</script>


<table width="50%" >
	<tr>
		<td align="left" colspan="3">
			<table width="20%">
				<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
					<jsp:param name="ScreenType" value="detail"/>
					<jsp:param name="ShowDocumentType" value="false"/>
					<jsp:param name="ShowEnterpriseCode" value="false"/>
					<jsp:param name="ShowNode" value="true"/>
					<jsp:param name="NodeBinding" value="xml:/Wave/@Node"/>
					<jsp:param name="RefreshOnNode" value="true"/>        
				</jsp:include>
			</table>
		</td>
		<%if(isShipNodeUser()){%>
			<yfc:callAPI apiID="AP2"/>
		<%}else{%>
		    <yfc:callAPI apiID="AP1"/>
		<%}%>

    <yfc:loopXML binding="xml:/Shipments/@Shipment" id="Shipment">
        <input type="hidden" <%=getTextOptions("xml:/Wave/ShipmentList/Shipment_" + ShipmentCounter + "/@ShipmentNo", "xml:/Shipment/@ShipmentNo")%> />
        <input type="hidden" <%=getTextOptions("xml:/Wave/ShipmentList/Shipment_" + ShipmentCounter + "/@ShipmentKey", "xml:/Shipment/@ShipmentKey")%> />
        <input type="hidden" <%=getTextOptions("xml:/Wave/ShipmentList/Shipment_" + ShipmentCounter + "/@Action", "ADD") %> />
    </yfc:loopXML> 

    <yfc:makeXMLInput name="waveDetailKey">
        <yfc:makeXMLKey binding="xml:/Wave/@WaveNo" value="xml:SelectedWave:/Wave/@WaveNo" />
        <yfc:makeXMLKey binding="xml:/Wave/@WaveKey" value="xml:SelectedWave:/Wave/@WaveKey" />
        <yfc:makeXMLKey binding="xml:/Wave/@Node" value="xml:SelectedWave:/Wave/@Node" />
    </yfc:makeXMLInput>

	<input type="hidden" name="hidWaveDetailKey" value='<%=getParameter("waveDetailKey")%>' />
	<input type="hidden" <%=getTextOptions("hidWaveKey","xml:SelectedWave:/Wave/@WaveKey")%> />
	<tr>
		<td style="width:20px;align:left">
			<%
				String isDisabled = "false";
				String val=resolveValue("xml:/Wave/@Action");
				if(isVoid(val))
				{
					val = "CREATE";
					isDisabled = "true";
				}
			%>
			<input type="radio" id="createOrModifyWaveRadio" onclick="createWaveEnableDisableFields()"  <%=getRadioOptions("xml:/Wave/@Action", val , "CREATE")%>>
		</td>
		<td class="searchlabel" ><yfc:i18n>Create_New_Wave_With_Shipment_Group</yfc:i18n></td>
		<td>
			<select <%=getComboOptions("xml:/Wave/@ShipmentGroupId")%> class="combobox" >
				<yfc:loopOptions binding="xml:ShipmentGroupList:/ShipmentGroupList/@ShipmentGroup" name="Description" value="ShipmentGroupId" isLocalized="Y"/>
			</select>
		</td>
	</tr>
	<tr>
		<td style="width:20px;align:left">
			<input type="radio" id="createOrModifyWaveRadio" onclick="createWaveEnableDisableFields()" <%=getRadioOptions("xml:/Wave/@Action","xml:/Wave/@Action", "MODIFY")%>>
		</td>
		<td class="searchlabel" ><yfc:i18n>Add_To_Wave_#</yfc:i18n></td>
		<%
			String extraParam = getExtraParamsForTargetBinding("xml:/Wave/@Node", resolveValue("xml:/Wave/@Node") );
		%>
		<td class="searchcriteriacell" nowrap="true" >
			 <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Wave/@WaveNo")%> disabled="<%=isDisabled%>" />
			 <img class="lookupicon" id="imageId" onclick="callLookup(this,'wavelookup', '<%=extraParam%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Wave") %> disabled="<%=isDisabled%>" />
		</td>
	</tr>
</table>
