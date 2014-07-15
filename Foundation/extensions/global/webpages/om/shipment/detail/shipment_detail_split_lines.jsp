<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript">
	yfcDoNotPromptForChanges(true);
   document.body.attachEvent("onunload",processSaveRecordsForShipmentLines);
//processSaveRecordsForShipmentLines
</script>
<script>
	function disableEnableQuantity(counter,value){
		if(value==false){
			value=true 
		}else{
			value=false;
		}
		document.all("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+counter+"/@Quantity").value="";	
		document.all("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+counter+"/@Quantity").disabled=value;

	}

	function disableEnableEntireQuantity(value){
		if(value==false){
			value=true 
		}else{
			value=false;
		}
		
		var i=1;
		while(document.all("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+i+"/@Quantity")) 
		{
			document.all("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+i+"/@Quantity").value="";	
			document.all("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+i+"/@Quantity").disabled=value;
			i++;
		}

	}
</script>
<table class="table" width="100%" initialRows="0" ID="ShipmentLines">
<thead>
<tr>
	<td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="disableEnableEntireQuantity(this.checked);doCheckAll(this);"/></td>
    <td class="tablecolumnheader" nowrap="true" style="width:15px">&nbsp;</td>
    <td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/Order/@ShipmentLineNo")%>">
		<yfc:i18n>Shipment_Line_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/Order/@OrderNo")%>">
	<yfc:i18n>Order_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/OrderLine/@PrimeLineNo")%>">
	<yfc:i18n>Line_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/OrderRelease/@ReleaseNo")%>">
	<yfc:i18n>Release_#</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemID")%>">
	<yfc:i18n>Item_ID</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ItemDesc")%>">
	<yfc:i18n>Description</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@ProductClass")%>">
	<yfc:i18n>PC</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure")%>">
	<yfc:i18n>UOM</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@MarkForKey")%>">
	<yfc:i18n>MarkFor</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@Quantity")%>">
	<yfc:i18n>Quantity</yfc:i18n>
	</td>
	<td class=tablecolumnheader sortable="yes" style="width:<%=getUITableSize("xml:/Shipment/ShipmentLines/ShipmentLine/@OriginalQuantity") %>">
	<yfc:i18n>Split_Quantity</yfc:i18n>
	</td>
</tr>
</thead>
<tbody>
<%
	YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
%>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine" > 
	  
	  <% double linePackedQty=getNumericValue("xml:/ShipmentLine/@ContainerizedQuantity");
		 double lineQty=getNumericValue("xml:/ShipmentLine/@Quantity");	
		 double lineUnPackedQty= lineQty-linePackedQty;
		
		if(lineUnPackedQty>0){
		if(elemShip!=null){
			elemShip.setDoubleAttribute("Quantity",lineUnPackedQty);
		}
		%> 
  <%if((equals("0",resolveValue("xml:/ShipmentLine/@ShipmentSubLineNo")))){%>
  <yfc:makeXMLInput name="shipmentLineKey">
	        <yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@SellerOrganizationCode" value="xml:/Shipment/@SellerOrganizationCode"/>
			<yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@ShipNode" value="xml:/Shipment/@ShipNode"/>
		    <yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
<yfc:makeXMLKey binding="xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey" />	      
        </yfc:makeXMLInput>
			<yfc:makeXMLInput name="shipmentKitKey">
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentKey" value="xml:/ShipmentLine/@ShipmentKey"/>
	        <yfc:makeXMLKey binding="xml:/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ItemID" value="xml:/ShipmentLine/@ItemID"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@ProductClass" value="xml:/ShipmentLine/@ProductClass"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure"/>
			<yfc:makeXMLKey binding="xml:/ShipmentLine/@EnterpriseCode" value="xml:/ShipmentLine/Shipment/@EnterpriseCode"/>
        </yfc:makeXMLInput>
    <tr>
	  <% String sCheckBox="xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+ShipmentLineCounter+"/@Selected";%>
	   <td class="checkboxcolumn" >
			<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="LineKey" 
			yfcMultiSelectCounter='<%=ShipmentLineCounter%>' onClick="disableEnableQuantity(<%=ShipmentLineCounter%>,this.checked)"
			yfcMultiSelectValue1='<%=resolveValue("xml:/ShipmentLine/@ShipmentLineKey")%>'/>
	   </td>
		<td class="tablecolumn" nowrap="true">
			<%if((equals("LK",resolveValue("xml:/ShipmentLine/@KitCode")))){%>
					<a <%=getDetailHrefOptions("L01", getParameter("shipmentKitKey"), "")%>>
						<img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Kit_Components")%>></a>
			<%}%>
		</td>
        <td class="tablecolumn">
			<a <%=getDetailHrefOptions("L01",getParameter("shipmentKitKey"),"")%> >
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@ShipmentLineNo"/>
			</a>
       </td>
	   <td class="tablecolumn">
						  <yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@OrderNo"/>  
			
	   </td>       
       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@PrimeLineNo"/> 
	   </td>       
       <td class="tablecolumn">
			<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ReleaseNo"/>            
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
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@MarkForKey"/>
       </td>
       <td class="numerictablecolumn">
		 <%=getFormattedDouble(lineUnPackedQty)%>
	   </td>
       <td class="tablecolumn">
			<input type="text" class="unprotectedinput" DISABLED title='<%=getI18N("Select_the_line_to_edit_Quantity")%>' <%=getTextOptions("xml:/SplitShipment/Source/Shipment/ShipmentLines/ShipmentLine_"+ShipmentLineCounter+"/@Quantity","")%>/>
       </td>
	   <%}}%>
	 </yfc:loopXML> 
	</tr>
</tbody>
<input type="hidden" name="xml:/SplitShipment/@NewShipmentNo"/>
</table>
