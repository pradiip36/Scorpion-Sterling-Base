<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
<%String str = "Ship_To";
	if(equals("0003",getValue("Shipment","xml:/Shipment/@DocumentType"))){
		str = "Return_To";
}%>
    <td width="25%" height="100%" id="shipToAddress">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Shipment/ToAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
			<jsp:param name="Title" value="<%=str%>"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ToAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>
        <td width="75%" height="100%" >
	        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			    <jsp:param name="CurrentInnerPanelID" value="I03"/>
		    </jsp:include>
    </td>
</tr>
 <yfc:callAPI apiID='AP1'/>
<tr>
<% YFCElement tagElem =(  YFCElement) request.getAttribute("Item");
   if(tagElem!=null){
	   request.setAttribute("Item", tagElem);
   }
%>
		<td colspan="3">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
		            <jsp:param name="CurrentInnerPanelID" value="I04"/>
	                <jsp:param name="Modifiable" value='true'/>
	                <jsp:param name="LabelTDClass" value='detaillabel'/>
	                <jsp:param name="TagContainer" value='Shipment'/>
	                <jsp:param name="TagElement" value='ShipmentTagSerials'/>
					<jsp:param name="RepeatingElement" value='ShipmentTagSerials'/>
					<jsp:param name="TotalBinding" value='xml:/Shipment/Containers/ContainerDetails/ContainerDetail/ShipmentTagSerials/ShipmentTagSerial'/>
	            </jsp:include>
		</td>
	
</tr>
</table>