<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
	String sPassedHoldType = getParameter("HoldType");
	
	//System.out.println("ShipmentKey is "+resolveValue("xml:/Shipment/@ShipmentKey"));
	String sLoopName = "ShipmentHistory";
	String sLoopBinding = "xml:ShipmentHistory:/Shipment/ShipmentHoldTypes/@ShipmentHoldType";
	String sID = "ShipmentHoldType";

	String sInnerLoopName = "ShipmentHoldType";
	String sHoldTypeLog = "xml:HoldType:/ShipmentHoldType/ShipmentHoldTypeLogs/@ShipmentHoldTypeLog";
	String sLogID = "ShipmentHoldTypeLog";

	

	String sCreatetsBinding = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@Createts";
	String sAuditKey = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@ShipmentAuditKey";
	String sUserId = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@UserId";
	String sStatus = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@Status";
	String sReasonText = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@ReasonText";
	String sTransactionId = "xml:HoldTypeLog:/ShipmentHoldTypeLog/@TransactionId";

	
%>

<table class="view" width="20%">
<tr>
    <td class="detaillabel" nowrap="true" ><yfc:i18n>Hold_Type</yfc:i18n></td>
	<td class="protectedtext" >
		 <%=getComboText("xml:/HoldTypeList/@HoldType" ,"HoldTypeDescription" ,"HoldType" , sPassedHoldType, true)%>
	</td>
</tr>
</table>

<table class="table" cellspacing="0" width="100%" ID="specialChange">
    <thead>
        <tr>
            <td class="tablecolumnheader" ><yfc:i18n>Date/Time</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>User_ID</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Comment</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Hold_Transaction</yfc:i18n></td>

	            <td class="tablecolumnheader" ><yfc:i18n>Audit</yfc:i18n></td>

        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="<%=sLoopName%>" binding="<%=sLoopBinding%>" id="HoldType">
			<%	
			if(YFCCommon.equals(sPassedHoldType, resolveValue("xml:HoldType:/" + sID + "/@HoldType") ) )	{	

				// first sort the element based on createts
				YFCElement eHoldType = getElement("HoldType");
				request.setAttribute("CurrentHistoryRecord", eHoldType);
                
				YFCElement eHoldTypeLogs = eHoldType.getChildElement("ShipmentHoldTypeLogs");
				YFCDate sCreateTime = eHoldType.getDateTimeAttribute("Createts");
				
				eHoldTypeLogs.sortChildren(new String[]{"Createts"} );
	            
				String sDateTimeString="";
				sDateTimeString=sCreateTime.getDateTimeString(getLocale());

			%>
		        <yfc:loopXML name="HoldType" binding="<%=sHoldTypeLog%>" id="HoldTypeLog">

				<%
					YFCElement eHoldType1 = getElement("HoldTypeLog");
					YFCDate sDateTime = eHoldType1.getDateTimeAttribute("Createts");
				%>

				<tr>
					<td class="protectedtext" >
						<%=sDateTimeString%>
					</td>
					<% sDateTimeString = sDateTime.getDateTimeString(getLocale()); %>
					<td class="tablecolumn" >
						<yfc:getXMLValue binding='<%=sUserId%>'/>
					</td>
					<td class="tablecolumn" >
						 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription", "CodeValue", sStatus, true)%>
					</td>
					<td class="tablecolumn" >
						<yfc:getXMLValue binding='<%=sReasonText%>'/>
					</td>
					<td class="tablecolumn" >
						 <%=getComboText("xml:/TransactionList/@Transaction" ,"Tranname", "Tranid", sTransactionId, true)%>
					</td>

				 
						<td class="tablecolumn" >
							<%	if(!isVoid(resolveValue(sAuditKey) ) )	{	%>
									<yfc:makeXMLInput name="auditKey" >
										<yfc:makeXMLKey binding="xml:/ShipmentAudit/@ShipmentAuditKey" value="<%=sAuditKey%>" />
										<yfc:makeXMLKey binding="xml:/ShipmentAudit/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
									</yfc:makeXMLInput>
									<a <%=getDetailHrefOptions("L01", getParameter("auditKey"), "")%> ><yfc:i18n>View_Audit</yfc:i18n></a>
							<%	}	%>
						</td>


				</tr>
	        </yfc:loopXML>
			<%	} 	%>
        </yfc:loopXML>

<%
	
		YFCElement eCurrentHistory = (YFCElement)request.getAttribute("CurrentHistoryRecord");
		YFCDate sDateTime = eCurrentHistory.getDateTimeAttribute("Modifyts");
		String sDateTimeString = sDateTime.getDateTimeString(getLocale());

		sAuditKey = "xml:CurrentHistoryRecord:/ShipmentHoldType/@ShipmentAuditKey";
		sUserId = "xml:CurrentHistoryRecord:/ShipmentHoldType/@Modifyuserid";
		sStatus = "xml:CurrentHistoryRecord:/ShipmentHoldType/@Status";
		sReasonText = "xml:CurrentHistoryRecord:/ShipmentHoldType/@ReasonText";
		sTransactionId = "xml:CurrentHistoryRecord:/ShipmentHoldType/@TransactionId";
	
%>
		<tr>
			<td class="protectedtext" >
				<%=sDateTimeString%>
			</td>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding='<%=sUserId%>'/>
			</td>
			<td class="tablecolumn" >
				 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription", "CodeValue", sStatus, true)%>
			</td>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding='<%=sReasonText%>'/>
			</td>
			<td class="tablecolumn" >
				 <%=getComboText("xml:/TransactionList/@Transaction" ,"Tranname", "Tranid", sTransactionId, true)%>
			</td>

					<td class="tablecolumn" >
						<%	if(!isVoid(resolveValue(sAuditKey) ) )	{	%>
								<yfc:makeXMLInput name="auditKey" >
									<yfc:makeXMLKey binding="xml:/ShipmentAudit/@ShipmentAuditKey" value="<%=sAuditKey%>" />
									<yfc:makeXMLKey binding="xml:/ShipmentAudit/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
								</yfc:makeXMLInput>
								<a <%=getDetailHrefOptions("L01", getParameter("auditKey"), "")%> ><yfc:i18n>View_Audit</yfc:i18n></a>
						<%	}	%>
					</td>


		</tr>
    </tbody>
</table>