<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>

<%
//Here, take the output of findReceipt API for open receipts to 
//set the visible binding for start receipt &  receive options.
double isOpen = getNumericValue("xml:/Receipts/@TotalNumberOfRecords");

YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
if(elemShip != null){
	if (equals("Y",elemShip.getAttribute("ShipmentConfirmUpdatesDone"))) {
		elemShip.setAttribute("ConfirmFlag", false);
	}
	else {
		elemShip.setAttribute("ConfirmFlag", true);
	}
	elemShip.setAttribute("WMSNode", false);
	elemShip.setAttribute("NonWMSNode", true);
	
	if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) {
		elemShip.setAttribute("WMSNode", true);
		elemShip.setAttribute("NonWMSNode", false);
	}
}
YFCElement elem = (YFCElement) request.getAttribute("Receipts");
if(elem != null){
	elem.setAttribute("StartReceiptFlag", false); 
	elem.setAttribute("ReceiveFlag", false);
	if ( isOpen > 0) {
		elem.setAttribute("ReceiveFlag", true);

	}
	else {
		elem.setAttribute("StartReceiptFlag", true);
	}
}
%>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
		    <jsp:param name="RootNodeName" value="Shipment"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td width="33%" height="100%" addressip="true">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I07"/>
            <jsp:param name="Path" value="xml:/Shipment/FromAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("FromAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>  

    <td width="33%" height="100%" addressip="true">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Shipment/ToAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ToAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>
    <td width="34%" height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
   

</tr>
<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
        </jsp:include>
    </td>
 <td  height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>

</tr>
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
        </jsp:include>
    </td>
</tr>
</table>