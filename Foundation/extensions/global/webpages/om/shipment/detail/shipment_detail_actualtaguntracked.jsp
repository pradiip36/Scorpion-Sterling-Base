<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="table" editable="true" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
		    <td class="tablecolumnheader"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
            <% if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y") ||equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns"),"Y")) { %>
			    <td class="tablecolumnheader"><yfc:i18n>Serial_#</yfc:i18n></td>
            <% } %>
            <td class="tablecolumnheader"><yfc:i18n>Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/ShipmentLines/ShipmentLine/ShipmentTagSerials/@ShipmentTagSerial" id="ShipmentTagSerial">
            <tr>
			 <yfc:makeXMLInput name="shipmentLineKey">
		        <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/ShipmentLine/@ShipmentKey"/>
		        <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
				<yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@ShipByDate" value="xml:/ShipmentTagSerial/@ShipByDate"/>
				<yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@SerialNo" value="xml:/ShipmentTagSerial/@SerialNo"/>
				<yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@Quantity" value="xml:/ShipmentTagSerial/@Quantity"/>
		 </yfc:makeXMLInput>
				<td class="checkboxcolumn">
				<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
			    </td>
				<td class="tablecolumn">
					<input type="text" <%=yfsGetTextOptions("xml:/ShipmentTagSerial/@ShipByDate", "xml:/ShipmentTagSerial/@ShipByDate", "xml:/ShipmentLine/AllowedModifications")%>/>
				</td>
                <% if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y")|| equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns"),"Y")) { %>
				    <td class="tablecolumn">
						<yfc:makeXMLInput name="serialKey" >
							<yfc:makeXMLKey binding="xml:/Container/@SerialNo" value="xml:/ShipmentTagSerial/@SerialNo" />
						</yfc:makeXMLInput>
						<a <%=getDetailHrefOptions("L01",getParameter("serialKey"),"")%> >
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@SerialNo"/>
						</a>
				    </td>
                <%  } %>
				<td class="numerictablecolumn">
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@Quantity"/>
				</td>
			    </tr>
        </yfc:loopXML> 
   </tbody>
   <tfoot>
    <tr style='display:none' TemplateRow="true">
       <td class="checkboxcolumn" >
	   &nbsp;
       </td>
		<td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial_/@ShipByDate")%>/>
			  <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        </td>
        <% if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y") ||equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns"),"Y")) { %>
		    <td nowrap="true" class="tablecolumn">
                <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial_/@SerialNo")%>/>
            </td>
        <%  } %>
		  <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial_/@Quantity")%>/>
        </td>
	</tr>
    <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
</tfoot>
</table>


