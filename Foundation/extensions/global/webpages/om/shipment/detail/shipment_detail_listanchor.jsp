<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/supervisorypanelpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreasonpopup.js"></script>
<yfc:callAPI apiID="AP1"/>
<%
YFCElement elem = (YFCElement) request.getAttribute("Shipments");
if(elem.getChildElement("Shipment")==null)
{
%>
<script language="javascript">
	myObject.temp=true;	
	window.close();
</script>
<%}%>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="4" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>
</table>