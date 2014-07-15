<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>

<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/css/scripts/editabletbl.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/yfcscripts/yfc.js"></script>
<script>
function processSaveRecordsForChildNode() {
   var addRow = window.document.getElementById("userOperation");
   var numRowsToAdd = window.document.getElementById("numRowsToAdd");
    if(addRow)
    {
        if(addRow.value != 'Y')
        {
            //reset numRowsToAdd attribute
            if(numRowsToAdd)
                numRowsToAdd.value="";
            yfcSpecialChangeNames("LineDetails", false);
        }
    }
    else
        yfcSpecialChangeNames("LineDetails", false);
}
</script>

<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>
<%
	boolean bAppendOldValue = false;
	if(!isVoid(errors) || equals(sOperation,"Y") || equals(sOperation,"DELETE")) 
		bAppendOldValue = true;

if (isModificationAllowedWithModType("SHIPMENT_ADD_LINE", "xml:/Shipment/AllowedModifications") && 	isModificationAllowedWithModType("SHIPMENT_REMOVE_LINE", "xml:/Shipment/AllowedModifications") ) {%> 
	<script language="javascript">
		window.attachEvent("onload", IgnoreChangeNames);
	</script>
<%}%> 

<%
    boolean enableShipmentLines=true;
    if(!equals(getValue("Shipment", "xml:/Shipment/@EnableShipmentLines"), "Y"))
        enableShipmentLines=false;
%>

<%
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	boolean bIsOutboundShipment = equals("omd",sAppCode);
	String sOrderAvailableOnSystem =  resolveValue("xml:/Shipment/@OrderAvailableOnSystem");
	String sDefaultSubLineNo = "0";
	boolean bOrderAvailableOnSystem = false;
	if(equals("Y",sOrderAvailableOnSystem)){
		sDefaultSubLineNo = "1";
		bOrderAvailableOnSystem = true;
	}else if(equals("M",sOrderAvailableOnSystem)){
		sDefaultSubLineNo = "1";
	}
	String sDocType = resolveValue("xml:/CurrentEntity/@DocumentType");
%>
<yfc:makeXMLInput name="shipmentKey">
       <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey"/>
</yfc:makeXMLInput>

<input type="hidden" Name="ShipmentKey" value='<%=HTMLEncode.htmlEscape(getParameter("shipmentKey"))%>'/>
<input type="hidden" name="xml:/Shipment/@ModificationReasonCode" />
<input type="hidden" name="xml:/Shipment/@ModificationReasonText"/>
<table class="table" width="100%" initialRows="0" id="LineDetails">
<thead>
<tr>
	<td sortable="no" class="checkboxheader">
		<input type="hidden" id="userOperation" name="userOperation" value="" />
		<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
		<input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
	</td>
    <td class="tablecolumnheader" nowrap="true" style="width:15px">&nbsp;</td>
    <td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/Order/@ShipmentLineNo")%>">
		<yfc:i18n>Shipment_Line_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/Order/@OrderNo")%>">
	<yfc:i18n>Order_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/OrderLine/@PrimeLineNo")%>">
	<yfc:i18n>Line_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/OrderRelease/@ReleaseNo")%>">
	<yfc:i18n>Release_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemDesc")%>">
	<yfc:i18n>Description</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass")%>">
	<yfc:i18n>PC</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure")%>">
	<yfc:i18n>UOM</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@RequestedSerialNo")%>">
	<yfc:i18n><%=sDocType%>_Requested_Serial_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@MarkForKey")%>">
	<yfc:i18n>MarkFor</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@Quantity")%>">
	<yfc:i18n>Quantity</yfc:i18n>
	</td>

	<%
		//System.out.println("sAppCode:" +sAppCode);
		if(bIsOutboundShipment) { %>
			<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@OverShipQuantity")%>">
			<yfc:i18n>Over_Ship_Quantity</yfc:i18n>
			</td>
<%	} if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) { %>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@Quantity")%>">
	<yfc:i18n>Shortage_Quantity</yfc:i18n>
	</td>
<%	}  %>
	<%if(bIsOutboundShipment) { %>
		<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@OriginalQuantity")	%>">
			<yfc:i18n>Original_Qty</yfc:i18n>
		</td>
	<%}else{%>
		<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ReceivedQuantity")	%>">
			<yfc:i18n>Received_Quantity</yfc:i18n>
		</td>
	<%}%>
</tr>
</thead>
<tbody>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
<%	String orderHeaderKey = resolveValue("xml:/ShipmentLine/@OrderHeaderKey");
	String sOrderAvailableOnSystemLineLevel =  resolveValue("xml:/ShipmentLine/@OrderAvailableOnSystem");
	boolean bOrderAvailableOnSystemLineLevel = false;
	if(equals("Y",sOrderAvailableOnSystemLineLevel)){
		bOrderAvailableOnSystemLineLevel = true;
	}
	if(   equals("0",resolveValue("xml:ShipmentLine:/ShipmentLine/@ShipmentSubLineNo")) && isVoid(resolveValue("xml:ShipmentLine:/ShipmentLine/@ParentShipmentLineKey"))   ){


			if(bAppendOldValue) {
				String sShipmentLineKey = resolveValue("xml:ShipmentLine:/ShipmentLine/@ShipmentLineKey");
				if(oMap.containsKey(sShipmentLineKey)) {
					request.setAttribute("OrigAPIShipmentLine",(YFCElement)oMap.get(sShipmentLineKey));
					//System.out.println("1..OrigAPIShipmentLine-------->" + ((YFCElement)oMapShipment.get(sShipmentLineKey)).toString());
				}
			} else {
					//System.out.println("2..OrigAPIShipmentLine-------->" + ((YFCElement)pageContext.getAttribute("ShipmentLine")).toString());
					request.setAttribute("OrigAPIShipmentLine",(YFCElement)pageContext.getAttribute("ShipmentLine"));
			}
		%>
		<yfc:makeXMLInput name="shipmentLineKey">
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentKey" value="xml:/Shipment/@ShipmentKey"/>
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ItemID" value="xml:/ShipmentLine/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ProductClass" value="xml:/ShipmentLine/@ProductClass"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipNode" value="xml:/Shipment/@ShipNode"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@BuyerOrganizationCode" value="xml:/Shipment/@BuyerOrganizationCode"/>
        </yfc:makeXMLInput>
		<yfc:makeXMLInput name="OrderHeaderKey" >
            <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey" />
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="OrderLineKey" >
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/ShipmentLine/@OrderLineKey" />
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey"/>
        </yfc:makeXMLInput>
		<yfc:makeXMLInput name="OrderReleaseKey" >
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey" />
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@DocumentType" value="xml:/Shipment/@DocumentType" />
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@ReleaseNo" value="xml:/ShipmentLine/@ReleaseNo" />
			<yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode"/>
            <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderNo" value="xml:/ShipmentLine/@OrderNo" />
        </yfc:makeXMLInput>
    <tr>
	   <td class="checkboxcolumn" >
			<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
			<%if (isModificationAllowedWithModType("SHIPMENT_ADD_LINE", "xml:/Shipment/AllowedModifications") &&
				isModificationAllowedWithModType("SHIPMENT_REMOVE_LINE", "xml:/Shipment/AllowedModifications") ) {
					if(!isVoid(resolveValue("xml:/ShipmentLine/@OrderNo"))) {%> 
					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@OrderNo", "xml:/ShipmentLine/@OrderNo")%> />
					<% } %>
					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@ShipmentLineKey", "xml:/ShipmentLine/@ShipmentLineKey")%> />
					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@ReleaseNo", "xml:/ShipmentLine/@ReleaseNo")%> />
					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@PrimeLineNo", "xml:/ShipmentLine/@PrimeLineNo")%> />

					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@SubLineNo", "xml:/ShipmentLine/@SubLineNo")%> />
					<input type="hidden" NewName="true" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@RequestedTagNo", "xml:/ShipmentLine/@RequestedTagNo")%> />

				<%}%> 
	   </td>
		<td class="tablecolumn" nowrap="true">
			<%if(equals("LK",resolveValue("xml:/ShipmentLine/@KitCode"))){%>
					<a <%=getDetailHrefOptions("L05", getParameter("shipmentLineKey"), "")%>>
						<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Kit_Components")%>></a>
			<%}%>
			<%if(equals("BUNDLE",resolveValue("xml:/ShipmentLine/@KitCode"))){%>
					<a <%=getDetailHrefOptions("L05", getParameter("shipmentLineKey"), "")%>>
						<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Bundle_Components")%>></a>
			<%}%>
		</td>
        <td class="numerictablecolumn">
			<a <%=getDetailHrefOptions("L05",getParameter("shipmentLineKey"),"")%> >
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ShipmentLineNo"/>
			</a>
       </td>
	   <td class="numerictablecolumn">
			 <%	if(!bOrderAvailableOnSystemLineLevel) { %>
				  <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/>  
			 <%} else {%>
					<% if(showOrderNo("Shipment","Shipment")) {%>
						<a <%=getDetailHrefOptions("L01", resolveValue("xml:/Shipment/@DocumentType"), getParameter("OrderHeaderKey"),"")%> >
							<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/>  
						</a>
					<%} else {%>
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/> <%}%> 
			 <%}%>
	   </td>       
       <td class="numerictablecolumn">
			<% if(!bOrderAvailableOnSystemLineLevel) { %>
				<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
			<%} else {%>
				<% if(showOrderLineNo("Shipment","Shipment")) {%>
					<a <%=getDetailHrefOptions("L02",resolveValue("xml:/Shipment/@DocumentType"), getParameter("OrderLineKey"),"")%> >
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
					</a>
				<%} else {%>
					<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
				<%}%>
			<%}%>
	   </td>       
       <td class="numerictablecolumn">
			<% if(!bOrderAvailableOnSystemLineLevel) { 
					if(!equals("0",resolveValue("xml:/ShipmentLine/@ReleaseNo"))){%>
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ReleaseNo"/>        
    				<%}
			} else {%>
				<%if(!equals("0",resolveValue("xml:/ShipmentLine/@ReleaseNo"))){%>
					<a <%=getDetailHrefOptions("L03",resolveValue("xml:/Shipment/@DocumentType"), getParameter("OrderReleaseKey"),"")%> >
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ReleaseNo"/>            
					</a>
				<%}%>
			<%}%>
	   </td>       
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
       </td>
	   <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ItemDesc"/>
       </td>
       <td class="tablecolumn">
			<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
       </td>

	   <td class="tablecolumn">
			<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
       </td>
       <td class="tablecolumn">
            <yfc:makeXMLInput name="serialKey" >
				<yfc:makeXMLKey binding="xml:/Container/@SerialNo" value="xml:/ShipmentLine/@RequestedSerialNo" />
				<yfc:makeXMLKey binding="xml:/Container/@ItemID" value="xml:/ShipmentLine/@ItemID" />
				<yfc:makeXMLKey binding="xml:/Container/@ProductClass" value="xml:/ShipmentLine/@ProductClass" />
				<yfc:makeXMLKey binding="xml:/Container/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L06",getParameter("serialKey"),"")%> >
                <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@RequestedSerialNo"/>
			</a>
       </td>
       <td class="tablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/MarkForAddress/@FirstName"/>
	   </td>
       <td class="numerictablecolumn">
		<%if (isModificationAllowedWithModType("SHIPMENT_ADD_LINE", "xml:/Shipment/AllowedModifications") && isModificationAllowedWithModType("SHIPMENT_REMOVE_LINE", "xml:/Shipment/AllowedModifications") ) {%> 
		   <input type="text" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter+ "/@Quantity","xml:/ShipmentLine/@Quantity","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE","text")%> style='width:60px'/>
		   <%}else{%>
			   <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@Quantity"/>
		   <%}%>
       </td>
		<%if(bIsOutboundShipment) { %>
			<td class="numerictablecolumn">
				<yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@OverShipQuantity"/>
			</td>
<% }if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) { %>
       <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ShortageQty"/>
       </td>
<%		}	%>
       	<%if(bIsOutboundShipment) { %>
	   <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@OriginalQuantity"/>
       </td>
	<%}else{%>
	   <td class="numerictablecolumn">
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ReceivedQuantity"/>
       </td>
	<%}%>
<%		} else if (isVoid(resolveValue("xml:ShipmentLine:/ShipmentLine/@ShipmentLineKey"))) { %>
			<tr DeleteRowIndex="<%=ShipmentLineCounter%>">
				<td class="checkboxcolumn"> 
					<img class="icon" onclick="setDeleteOperationForRow(this,'xml:/Shipment/ShipmentLines/ShipmentLine')" <%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%>/>
				</td>
				<td class="tablecolumn" >
					<input type="hidden"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_"+ShipmentLineCounter+"/@DeleteRow",  "")%> />
				</td>
				<td class="tablecolumn" >
				</td>
<%				String sLookup = "orderlookup";
				if (equals("oms", sAppCode) )
					sLookup = "polookup";
				else if(equals("omr", sAppCode) )
					sLookup = "returnlookup";	%>
				<td nowrap="true" class="tablecolumn">
<%				if (isVoid(resolveValue("xml:/Shipment/@OrderNo"))) {	%>
					<input type="text" class="unprotectedinput" OldValue="<%=resolveValue("xml:/Shipment/@OrderNo")%>"
					<%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@OrderNo","xml:/ShipmentLine/@OrderNo","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE", "text")%>/>
<%				} else {	%>
					<input type="text" class="unprotectedinput" OldValue="<%=resolveValue("xml:/Shipment/@OrderNo")%>"
					<%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@OrderNo","xml:/Shipment/@OrderNo","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE", "text")%>/>
<%				}
				if(bOrderAvailableOnSystemLineLevel || bOrderAvailableOnSystem) { %>
					<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>','xml:/Order/@DocumentType=<%=resolveValue("xml:/Shipment/@DocumentType")%>&xml:/Order/@EnterpriseCode=<%=resolveValue("xml:/Shipment/@EnterpriseCode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/>
<%				}	%>
				</td>
				<td nowrap="true" class="tablecolumn">
					<input type="text" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@PrimeLineNo","xml:/ShipmentLine/@PrimeLineNo")%>/>
<%				if(bOrderAvailableOnSystemLineLevel || bOrderAvailableOnSystem) {// || condition added for CR 74550%>
					<img class="lookupicon" onclick="templateRowCallOrderLineLookup(this,'PrimeLineNo','ReleaseNo','ItemID','ProductClass','UnitOfMeasure','Quantity','orderlineitemlookup','xml:/OrderLineStatus/@EnterpriseCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value  + '&xml:/OrderLineStatus/@DocumentType=' + document.all['xml:/Shipment/@DocumentType'].value + '&xml:/OrderLineStatus/@OrderHeaderKey=' + document.all['xml:/Shipment/@OrderHeaderKey'].value + '&xml:/OrderLineStatus/@OrderNo=' + document.all['xml:/Shipment/ShipmentLines/ShipmentLine_' + <%=ShipmentLineCounter%> +'/@OrderNo'].value)" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order_Line")%>/>
<%				}	%>
					<input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@SubLineNo","xml:/ShipmentLine/@SubLineNo",sDefaultSubLineNo)%>/>
					<input type="hidden" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter + "/@Action","xml:/ShipmentLine/@Action","Create")%>/>
				</td>
				<td nowrap="true" class="tablecolumn">
					<input type="text" class="unprotectedinput" OldValue="" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@ReleaseNo","xml:/ShipmentLine/@ReleaseNo")%>/> 
				</td>
				<td nowrap="true" class="tablecolumn"  >
					<input type="text"  class="unprotectedinput" OldValue="" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@ItemID","xml:/ShipmentLine/@ItemID","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE", "text")%>/> 
					<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item','xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
				</td>
				<td class="tablecolumn" >
				</td>
				<td nowrap="true" class="tablecolumn" >
                    <% String prodClassBinding = "xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@ProductClass"; %>
					<select class="combobox" <%=getComboOptions(prodClassBinding, "xml:/ShipmentLine/@ProductClass")%>>
					<yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
						name="CodeValue" value="CodeValue" selected="xml:/ShipmentLine/@ProductClass" targetBinding="<%=prodClassBinding%>"/>
					</select>
				</td>
				<td nowrap="true" class="tablecolumn">
                    <% String uomBinding = "xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@UnitOfMeasure"; %>
					<select OldValue="" <%=getComboOptions(uomBinding, "xml:/ShipmentLine/@UnitOfMeasure")%> class="combobox">
						<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/ShipmentLine/@UnitOfMeasure" targetBinding="<%=uomBinding%>"/>
					</select>
				</td>
				<td class="tablecolumn">
					<%if(!bOrderAvailableOnSystemLineLevel){%>
					<input type="text" OldValue="" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@RequestedSerialNo","xml:/ShipmentLine/@RequestedSerialNo")%> style='width:60px'/> 
					<%}%>
				</td>
				<td class="tablecolumn" >
				</td>
				<td class="numerictablecolumn">
					<input type="text" OldValue="" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_" + ShipmentLineCounter +"/@Quantity","xml:/ShipmentLine/@Quantity")%> style='width:60px'/> 
				</td>
				<%if(bIsOutboundShipment) { %>
					<td class="tablecolumn">
					</td>
				<%}if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) {	%>
				<td class="tablecolumn" >
				</td>
<%				}	%>
				<td class="tablecolumn" >
				</td>
			</tr>
<%	}
%>
	 </yfc:loopXML> 
	</tr>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" >
        </td>
		<td class="tablecolumn" >
        </td>
        <%	String sLookup = "orderlookup";
			if (equals("oms", sAppCode) )
				sLookup = "polookup";
			else if(equals("omr", sAppCode) )
				sLookup = "returnlookup";
		%>
		<td class="tablecolumn" >
        </td>
       	<td nowrap="true" class="tablecolumn">
            <input type="text"			<%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@OrderNo","xml:/Shipment/@OrderNo","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE","text")%>/>
                 <%	if(bOrderAvailableOnSystem) { %>
					<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>','xml:/Order/@DocumentType=<%=resolveValue("xml:/Shipment/@DocumentType")%>&xml:/Order/@EnterpriseCode=<%=resolveValue("xml:/Shipment/@EnterpriseCode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order")%>/>
			<% } %>
        </td>
        <td nowrap="true" class="tablecolumn">
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@PrimeLineNo","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE","text")%>/>
<%			if(bOrderAvailableOnSystem) {%>
				<img class="lookupicon" onclick="templateRowCallOrderLineLookup(this,'PrimeLineNo','ReleaseNo','ItemID','ProductClass','UnitOfMeasure','Quantity','orderlineitemlookup','xml:/OrderLineStatus/@EnterpriseCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value  + '&xml:/OrderLineStatus/@DocumentType=' + document.all['xml:/Shipment/@DocumentType'].value + '&xml:/OrderLineStatus/@OrderHeaderKey=' + document.all['xml:/Shipment/@OrderHeaderKey'].value)" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order_Line")%>/>
<%			}	%>
			<input type="hidden" name= "xml:/Shipment/ShipmentLines/ShipmentLine_/@SubLineNo" value="<%=sDefaultSubLineNo%>" OldValue="<%=sDefaultSubLineNo%>"/>
			<input type="hidden" name= "xml:/Shipment/ShipmentLines/ShipmentLine_/@Action" value="Create" OldValue="Create"/>
			
        </td>
        <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput"  <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@ReleaseNo","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE","text")%>/> 
        </td>
        <td nowrap="true" class="tablecolumn"  >
            <input type="text" OldValue="" <%=yfsGetTemplateRowOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@ItemID","xml:/Shipment/AllowedModifications","SHIPMENT_ADD_LINE","text")%>/> 
			<img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','UnitOfMeasure','item','xml:/Item/@CallingOrganizationCode=' +  document.all['xml:/Shipment/@EnterpriseCode'].value )" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
        </td>
		<td class="tablecolumn" >
        </td>
        <td nowrap="true" class="tablecolumn" >
			<select class="combobox"  <%=getComboOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@ProductClass")%>>
				<yfc:loopOptions binding="xml:ProductClass:/CommonCodeList/@CommonCode" 
					name="CodeValue" value="CodeValue" selected="xml:/Shipment/ShipmentLines/ShipmentLine_/@ProductClass"/>
			</select>
        </td>

		<td nowrap="true" class="tablecolumn"  >
			<select <%=getComboOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@UnitOfMeasure")%> class="combobox"  >
				<yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/Shipment/ShipmentLines/ShipmentLine_/@UnitOfMeasure"/>
			</select>
        </td>
        <td class="tablecolumn">
			<%if(!bOrderAvailableOnSystem){%>
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@RequestedSerialNo")%>	style='width:60px'/> 
			<%}%>

		</td>
		<td class="tablecolumn" >
        </td>
        <td class="numerictablecolumn">
            <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine_/@Quantity")%> style='width:60px'/> 
        </td>
		<td class="tablecolumn" >
        </td>
	</tr>

    <% if ((isModificationAllowedWithModType("SHIPMENT_ADD_LINE", "xml:/Shipment/AllowedModifications") )) {%> 
	<tr>
    	<td nowrap="true" colspan="16">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
                <jsp:param name="ReloadOnAddLine" value="Y"/>
    		</jsp:include>
    	</td>
    </tr>
	<%	} %>
</tfoot>
</table>