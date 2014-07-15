<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/om/shipment/detail/shipment_detail_newdates_include.jspf" %>
<% String sDateTypeId ="" ; %>
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader">
            <yfc:i18n>Date_Type</yfc:i18n>
        </td>
		<td class="tablecolumnheader" sortable="no">
            <yfc:i18n>Requested</yfc:i18n>
        </td>
		<td class="tablecolumnheader" sortable="no">
            <yfc:i18n>Expected</yfc:i18n>
        </td>
		<td class="tablecolumnheader" sortable="no">
            <yfc:i18n>Actual</yfc:i18n>
			<input type="hidden" name="xml:/Shipment/AdditionalDates/@Replace" value="N" />
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML name="DateTypeList" binding="xml:/DateTypeList/@DateType" id="DateType"> 
	<% String sSystemDefined = getValue("DateType","xml:/DateType/@SystemDefined") ;
		sDateTypeId = getValue("DateType","xml:/DateType/@DateTypeId") ;
		if (!equals(sSystemDefined, "Y" )){ 
	%> 
    <tr>
        <td class="tablecolumn">
			<input type="hidden" <%=getTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@Action","Create-Modify")%> />
		<%
			String sReqDate = "";
			String sExpDate ="";
			String sActDate = "";

			YFCDate tmpDate = (YFCDate)oMap.get(sDateTypeId + "Req");
			if(tmpDate != null)
				sReqDate = tmpDate.getString(getLocale(),true);

			tmpDate = (YFCDate)oMap.get(sDateTypeId + "Exp");
			if(tmpDate != null)
				sExpDate = tmpDate.getString(getLocale(),true);
	
			tmpDate = (YFCDate)oMap.get(sDateTypeId + "Act");
			if(tmpDate != null)
				sActDate = tmpDate.getString(getLocale(),true);

		%>
			<yfc:i18n><yfc:getXMLValue name="DateType" binding="xml:/DateType/@DateTypeId" /></yfc:i18n>
			<input type="hidden" <%=getTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@DateTypeId","xml:DateType:/DateType/@DateTypeId")%> />
        </td>
        <td class="tablecolumn">
			<% if(equals(resolveValue("xml:DateType:/DateType/@RequestedFlag"),"Y")) {%>
		        <input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + 	"/@RequestedDate_YFCDATE",getDateOrTimePart("YFCDATE",sReqDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Date_Lookup" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + 	"/@RequestedDate_YFCTIME",getDateOrTimePart("YFCTIME",sReqDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Time_Lookup" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />

			<%}%>
        </td>
        <td class="tablecolumn">
			<% if(equals(resolveValue("xml:DateType:/DateType/@ExpectedFlag"),"Y")) {%>
		        <input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@ExpectedDate_YFCDATE",getDateOrTimePart("YFCDATE",sExpDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Date_Lookup" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@ExpectedDate_YFCTIME",getDateOrTimePart("YFCTIME",sExpDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Time_Lookup" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
			<%}%>
        </td>
		<td class="tablecolumn">
			<% if(equals(resolveValue("xml:DateType:/DateType/@ActualFlag"),"Y")) {%>
				<input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@ActualDate_YFCDATE",getDateOrTimePart("YFCDATE",sActDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Date_Lookup" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
				<input type="text" <%=yfsGetTextOptions("xml:/Shipment/AdditionalDates/AdditionalDate_" + DateTypeCounter + "/@ActualDate_YFCTIME",getDateOrTimePart("YFCTIME",sActDate),"xml:/Shipment/AllowedModifications")%>/>
				<img class="lookupicon" name="Time_Lookup" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
			<%}%>
		</td>
    </tr>
	<%}%>
    </yfc:loopXML> 
</tbody>
</table>