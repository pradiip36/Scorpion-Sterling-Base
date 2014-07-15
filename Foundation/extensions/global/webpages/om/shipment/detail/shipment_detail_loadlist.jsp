<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>

<table class="table" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/@LoadNo")%>">
            <yfc:i18n>Load_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/@LoadType")%>">
            <yfc:i18n>Load_Type</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/@Scac")%>">
            <yfc:i18n>Carrier_/_Service</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/@OriginNode")%>">
            <yfc:i18n>Origin</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/@DestinationNode")%>">
            <yfc:i18n>Destination</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Load/Status/@Description")%>">
            <yfc:i18n>Status</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
    <yfc:loopXML name="Loads" binding="xml:/Loads/@Load" id="Load"  keyName="LoadKey" > 
        <yfc:makeXMLInput name="loadKey" >
            <yfc:makeXMLKey binding="xml:/Load/@LoadKey" value="xml:/Load/@LoadKey" />
        </yfc:makeXMLInput>
    <tr>
        <td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("loadKey")%>' name="chkEntityKey"/>
        </td>
        <td class="tablecolumn">
			<% if(showLoadNo(resolveValue("xml:/Load/@OwnerOrganizationCode"))) { %>
	            <a <%=getDetailHrefOptions("L01",getParameter("loadKey"),"")%> >
		            <yfc:getXMLValue name="Load" binding="xml:/Load/@LoadNo"/>
			    </a>
			<%} else { %>
				<yfc:getXMLValue name="Load" binding="xml:/Load/@LoadNo"/>
			<%}%>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValue name="Load" binding="xml:/Load/@LoadType"/>
        </td>
        <td class="tablecolumn">
		    <yfc:getXMLValue name="Load" binding="xml:/Load/@Scac"/>
			<%	if(!isVoid(getValue("Load","xml:/Load/@Scac")) && !isVoid(getValue("Load","xml:/Load/@CarrierServiceCode"))) {%>
			/
			<%}%>
			<yfc:getXMLValue name="Load" binding="xml:/Load/@CarrierServiceCode"/>
        </td>
		<td class="tablecolumn">
			<% if (!isVoid(getValue("Load","xml:/Load/@OriginNode"))) { %>
				<yfc:getXMLValue binding="xml:/Load/@OriginNode"/>
			<%} else { 
				if (equals(getValue("Load","xml:/Load/@PickupFromShipmentOrigin"), "Y" )) { %>	
					<yfc:loopXML name="Load" binding="xml:/Load/LoadStops/@LoadStop" id="LoadStop"> 
					    <% if (equals(getValue("LoadStop","xml:/LoadStop/@StopType"), "O" )) { %>	
							<% request.setAttribute("LoadStop", pageContext.getAttribute("LoadStop")); %>
							<jsp:include page="/common/smalladdress.jsp" flush="true" >
								 <jsp:param name="Path" value="LoadStop/StopAddress"/>
							</jsp:include>
						<%}%>			
				    </yfc:loopXML> 
				<%} else {%>
						<% request.setAttribute("Load", pageContext.getAttribute("Load")); %>
						<jsp:include page="/common/smalladdress.jsp" flush="true" >
							 <jsp:param name="Path" value="Load/OriginAddress"/>
						</jsp:include>
				<%}
			}%>
        </td>
		<td class="tablecolumn">
			<%if (!isVoid(getValue("Load","xml:/Load/@DestinationNode"))) { %>
			    <yfc:getXMLValue binding="xml:/Load/@DestinationNode"/>
            <%} else { 
                if (equals(getValue("Load","xml:/Load/@DropAtShipmentDestination"), "Y" )) { %>	
                    <yfc:loopXML name="Load" binding="xml:/Load/LoadStops/@LoadStop" id="LoadStop"> 
                        <%if (equals(getValue("LoadStop","xml:/LoadStop/@StopType"), "D" )) { %>	
							<% request.setAttribute("LoadStop", pageContext.getAttribute("LoadStop")); %>
							<jsp:include page="/common/smalladdress.jsp" flush="true" >
								 <jsp:param name="Path" value="LoadStop/StopAddress"/>
							</jsp:include>
                        <%}%>			
                    </yfc:loopXML> 
                <%} else { %>
						<% request.setAttribute("Load", pageContext.getAttribute("Load")); %>
						<jsp:include page="/common/smalladdress.jsp" flush="true" >
							 <jsp:param name="Path" value="Load/DestinationAddress"/>
						</jsp:include>
                <%}
            }%>
        </td>
        <td class="tablecolumn">
            <yfc:getXMLValueI18NDB name="Load" binding="xml:/Load/Status/@Description"/>
        </td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
