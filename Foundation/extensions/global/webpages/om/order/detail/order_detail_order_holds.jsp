<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">
	document.body.attachEvent("onunload", processSaveRecords);

	function checkReasonText(checkboxObj, iCounter)	{
		var oReasonText = document.all("xml:/Order/OrderHoldTypes/OrderHoldType_" + iCounter + "/@ReasonText");
		oReasonText.value = "";
		oReasonText.disabled = !checkboxObj.checked;
	}

	function setEnableReasonText(obj, sReasonID, sStatusValue)	{
		var oReasonID = document.all(sReasonID);
		oReasonID.disabled=(obj.value == '');
		if(obj.value == '' || obj.value != sStatusValue)	{
			oReasonID.value='';
		}
	}

</script>

<%
	String sAllowModElementName = "xml:/Order/AllowedModifications";
	String sBindingPrefix = "xml:/Order/OrderHoldTypes";
	String sBinding = sBindingPrefix + "/@OrderHoldType";
	String sName = "Order";
	String sXMLListName = "OrderHoldTypes";
	String sXMLName = "OrderHoldType";
	String sForWorkOrder = getParameter("ForWorkOrder");
	if(YFCCommon.equals("Y", sForWorkOrder))	{
		sAllowModElementName = "xml:/WorkOrder/AllowedModifications";
		sName = "WorkOrder";
		sBindingPrefix = "/WorkOrder/WorkOrderHoldTypes";
		sBinding = "xml:" + sBindingPrefix + "/@WorkOrderHoldType";
		sXMLListName = "WorkOrderHoldTypes";
		sXMLName = "WorkOrderHoldType";
	}	
	YFCElement eOrderHoldList = getElement(sName).getChildElement(sXMLListName);
	formMasterHoldXML(sXMLName, eOrderHoldList, getElement("HoldTypeList"), getElement("HoldStatus") );
%>

<table class="table" cellspacing="0" width="100%" ID="specialChange">
    <thead>
        <tr>
            <td class="tablecolumnheader" sortable="no">&nbsp;</td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>Hold_Description</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>Hold_Status</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>Hold_Comment</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>Action</yfc:i18n></td>
            <td class="tablecolumnheader" sortable="no"><yfc:i18n>Reason</yfc:i18n></td>
        </tr>
    </thead>
	<%
		String sHoldType = "xml:HoldType:/" + sXMLName + "/@HoldType";
		String sReasonText = "xml:HoldType:/" + sXMLName + "/@ReasonText";
		String sStatus = "xml:HoldType:/" + sXMLName + "/@Status";
		eOrderHoldList.sortChildren(new String[]{"Status"}, false);
	%>
    <tbody>
        <yfc:loopXML name="<%=sName%>" binding="<%=sBinding%>" id="HoldType">
			<%	YFCElement eHoldType = getElement("HoldType");
				if(!equals("1300", eHoldType.getAttribute("Status") ) )	{
					String extraParams = getExtraParamsForTargetBinding("HoldType", resolveValue(sHoldType) );
					String sComboStatusName = "xml:" + sBindingPrefix + "/" + sXMLName + "_" + HoldTypeCounter + "/@Status";
					String sHoldTypeBinding = "xml:" + sBindingPrefix + "/" + sXMLName + "_" + HoldTypeCounter + "/@HoldType";
					String sHoldTypeReasonBinding = "xml:" + sBindingPrefix + "/" + sXMLName + "_" + HoldTypeCounter + "/@ReasonText";
					String sComboBinding = "xml:HoldType:/" + sXMLName + "/Permissions/@Permission";
					String s1ComboBinding = "xml:HoldType:/" + sXMLName + "/Permissions/Permission";

					YFCElement ePermissions = eHoldType.getChildElement("Permissions", true);
					if(ePermissions.hasChildNodes())	{
			%>

			<tr>
				<td class="tablecolumn" nowrap="true">
				<% if(YFCCommon.equals("Y", sForWorkOrder))	{	%>
						<yfc:makeXMLInput name="HoldHistoryKey">
							<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey"/>
						</yfc:makeXMLInput>
					<%	}	else	{	%>
						<yfc:makeXMLInput name="HoldHistoryKey">
							<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
						</yfc:makeXMLInput>
					<%	}	%>

					<a <%=getDetailHrefOptions("L01", getParameter("HoldHistoryKey"), extraParams)%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "View_History")%>></a>
				</td>


				<td class="tablecolumn" >
					 <%=getComboText("xml:/HoldTypeList/@HoldType" ,"HoldTypeDescription" ,"HoldType" , sHoldType, true)%>
				</td>
				<td class="tablecolumn" >
					 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription" ,"CodeValue" , sStatus, true)%>
				</td>
				<td class="tablecolumn" >
					<yfc:getXMLValueI18NDB binding='<%=sReasonText%>'/>
				</td>
				<td class="tablecolumn" >
					<select 
					<%=getModTypeComboOptions(sComboStatusName, sAllowModElementName, "HOLD_TYPE")%>
					name="<%=sComboStatusName%>" class="combobox" OldValue="" 
					onChange="setEnableReasonText(this, '<%=HoldTypeCounter%>', '<%=resolveValue(sStatus)%>');" >
						<yfc:loopOptions binding="<%=sComboBinding%>" name="AllowDesc"
						value="Allow" selected=" " />
					</select>

		            <input type="hidden" name="<%=sHoldTypeBinding%>" value='<%=resolveValue(sHoldType)%>'/>
				</td>

				<td class="tablecolumn"  >
					<textarea disabled rows="2" ID='<%=HoldTypeCounter%>' cols="40" <%=getModTypeTextAreaOptions(sHoldTypeReasonBinding, sAllowModElementName, "HOLD_TYPE")%> <%=getTextAreaOptions(sHoldTypeReasonBinding, "xml:HoldType:/" + sXMLName + "/@ReasonText" )%>></textarea>
				</td>
			</tr>
			<%	}	
			}%>

        </yfc:loopXML>
        <yfc:loopXML name="<%=sName%>" binding="<%=sBinding%>" id="HoldType">
			<%	YFCElement eHoldType = getElement("HoldType");
				if(!equals("1300", eHoldType.getAttribute("Status") ) )	{
					String extraParams = getExtraParamsForTargetBinding("HoldType", resolveValue(sHoldType) );

					YFCElement ePermissions = eHoldType.getChildElement("Permissions");
					if(!ePermissions.hasChildNodes())	{
			%>

			<tr>
				<td class="tablecolumn" nowrap="true">
				<% if(YFCCommon.equals("Y", sForWorkOrder))	{	%>
						<yfc:makeXMLInput name="HoldHistoryKey">
							<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey"/>
						</yfc:makeXMLInput>
					<%	}	else	{	%>
						<yfc:makeXMLInput name="HoldHistoryKey">
							<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
						</yfc:makeXMLInput>
					<%	}	%>

					<a <%=getDetailHrefOptions("L01", getParameter("HoldHistoryKey"), extraParams)%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "View_History")%>></a>
				</td>


				<td class="tablecolumn" >
					 <%=getComboText("xml:/HoldTypeList/@HoldType" ,"HoldTypeDescription" ,"HoldType" , sHoldType, true)%>
				</td>
				<td class="tablecolumn" >
					 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription" ,"CodeValue" , sStatus, true)%>
				</td>
				<td class="tablecolumn" >
					<yfc:getXMLValueI18NDB binding='<%=sReasonText%>'/>
				</td>
				<td class="tablecolumn" >
				</td>

				<td class="tablecolumn" >
				</td>
			</tr>
			<%	}	
			}	%>
        </yfc:loopXML>
    </tbody>
</table>
