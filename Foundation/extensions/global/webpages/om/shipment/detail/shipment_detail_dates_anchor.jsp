<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<%	
	String sInnerPanel = null;
	if(equals("DEL", resolveValue("xml:/Shipment/@DeliveryMethod") ) ) 	{
		sInnerPanel = "I02";
	}
	else {
		sInnerPanel = "I04";
	}
%>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="<%=sInnerPanel%>" />
        </jsp:include>
    </td>
</tr>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
</table>
	