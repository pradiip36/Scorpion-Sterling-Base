<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td width="100%" height="100%" id="shipToAddress">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="Path" value="xml:/PersonInfoList/PersonInfo"/>
            <jsp:param name="DataXML" value="PersonInfoList"/>
        </jsp:include>
    </td>
</tr>