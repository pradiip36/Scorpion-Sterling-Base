<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
    <input type="hidden" <%=getTextOptions("xml:/Shipment/@ShipmentKey")%>/>
</tr>
<%
	YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
	YFCElement allowedModElem = elemShip.getChildElement("AllowedModifications");
	boolean bEnableContainers = false;
	if(allowedModElem != null){
		for (Iterator oIter = allowedModElem.getChildren();oIter.hasNext();) {
			YFCElement allowedModElemChild = (YFCElement) oIter.next();
			if(equals(allowedModElemChild.getAttribute("ModificationType"),"SHIPMENT_UNPACK")){
				bEnableContainers = true;
			}
		}
	}	
	if(equals(resolveValue("xml:/Shipment/Containers/@TotalNumberOfRecords"),"0")){
		elemShip.setAttribute("EnableDeleteContainers",false);
	}else if(!isVoid(resolveValue("xml:/Shipment/Containers/@TotalNumberOfRecords"))){
		elemShip.setAttribute("EnableDeleteContainers",bEnableContainers);
	}
%>
<%if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) {%>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
<%}else{%>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<%}%>
</table>
	