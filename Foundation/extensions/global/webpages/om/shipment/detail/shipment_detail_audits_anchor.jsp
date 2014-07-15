<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" height="300" width="100%">
<div style="overflow:auto">
 <tr height="20%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
</div>
<div style="overflow:auto">
<tr height="30%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
</div>
<div style="overflow:auto">
<tr height="50%">
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
</div>
</table>