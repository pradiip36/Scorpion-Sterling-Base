<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>

<%
	YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
	if(elemShip != null){
		if (equals("Y",elemShip.getAttribute("ShipmentConfirmUpdatesDone"))) {
			elemShip.setAttribute("ConfirmFlag", false);
		}
		else {
			elemShip.setAttribute("ConfirmFlag", true);
		}
	}
%>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td width="34%" height="100%" id="shipToAddress">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Shipment/ToAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ToAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>  

	<td width="33%" height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>

	<td width="33%" height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
</tr>
<tr>
	<td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
        </jsp:include>
    </td>
</tr>
</table>