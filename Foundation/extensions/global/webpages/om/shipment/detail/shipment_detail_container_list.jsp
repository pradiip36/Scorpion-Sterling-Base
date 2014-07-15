<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Warehouse Management System
(C) Copyright IBM Corp. 2005, 2011 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
*******************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%
    String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	boolean outboundShipment = false;
	if(equals(sAppCode,"omd")){
		outboundShipment = true;
	}
	boolean enableShipmentLines=true;
    if(!equals(getValue("Shipment", "xml:/Shipment/@EnableShipmentLines"), "Y"))
        enableShipmentLines=false;
	boolean bWMSNode=false;
	if(equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) {
		bWMSNode=true;
	}
%>
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@ContainerNo")%>">
            <yfc:i18n>Container_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@TrackingNo")%>">
            <yfc:i18n>Tracking_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@ContainerScm")%>">
            <yfc:i18n>Container_SCM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" sortable="no" nowrap="true">
            <yfc:i18n>Net_Weight</yfc:i18n>
                  
        </td>
        <td class="tablecolumnheader" sortable="no" nowrap="true">
            <yfc:i18n>Gross_Weight</yfc:i18n>
        
        </td>
		<td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/Container/@ActualFreightCharge")%>">
            <yfc:i18n>Freight_Charge</yfc:i18n>
        </td>
        <%if(bWMSNode){%>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@Status")%>">
            <yfc:i18n>Status</yfc:i18n>
        </td>
		<%}%>
		<%if(bWMSNode&&equals(resolveValue("xml:/CurrentEntity/@ApplicationCode"),"omd")){%>
		<td class="tablecolumnheader" style="width:60px">
            <yfc:i18n>Manifested</yfc:i18n>
        </td>
		<%}%>	
		<%if(!outboundShipment){%>
		<td class="tablecolumnheader" style="width:50px">
            <yfc:i18n>Received</yfc:i18n>
        </td>
		<%}%>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Shipment" binding="xml:/Shipment/Containers/@Container" id="Container" > 
        <yfc:makeXMLInput name="ShipmentContainerKey" >
            <yfc:makeXMLKey binding="xml:/Container/@ShipmentContainerKey" value="xml:/Container/@ShipmentContainerKey" />
        </yfc:makeXMLInput>
	<% if (outboundShipment && isVoid(resolveValue("xml:/Container/@ParentContainerNo")) && (resolveValue("xml:/Container/@ContainerGroup").equals("SHIPMENT"))
            || (!outboundShipment)
            || "LOAD".equals(resolveValue("xml:/Container/@ParentContainerGroup"))
			|| ("INVENTORY".equals(resolveValue("xml:/Container/@ParentContainerGroup")) && ("SHIPMENT".equals(resolveValue("xml:/Container/@ContainerGroup")) || "LOAD".equals(resolveValue("xml:/Container/@ContainerGroup")))))
		{
	%>
    <tr>
        <td class="checkboxcolumn"> 
            <input type="checkbox" value='<%=getParameter("ShipmentContainerKey")%>' name="chkEntityKey"/>
        </td>
      
        <td class="tablecolumn">
            <a <%=getDetailHrefOptions("L01",getParameter("ShipmentContainerKey"),"")%> ><yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerNo"/></a>
        </td>
        <td class="tablecolumn">
			<%=replaceBlanksWithNoBlockSpaces(resolveValue("xml:/Container/@TrackingNo"))%>
	         <input type="hidden" <%=getTextOptions( "xml:/Container/@TrackingNo", "xml:/Container/@TrackingNo")%>/>
		</td>
        <td class="tablecolumn">
           <%=replaceBlanksWithNoBlockSpaces(resolveValue("xml:/Container/@ContainerScm"))%>
        </td>
        <td class="numerictablecolumn"	sortValue="<%=getNumericValue("xml:Container:/Container/@ContainerNetWeight")%>">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerNetWeight"/>&nbsp;
       &nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ContainerNetWeightUOM" />&nbsp;
        </td>
        <td class="numerictablecolumn"	sortValue="<%=getNumericValue("xml:Container:/Container/@ContainerGrossWeight")%>">
            <yfc:getXMLValue name="Container" binding="xml:/Container/@ContainerGrossWeight"/>&nbsp;
        &nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ContainerGrossWeightUOM" />&nbsp;
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:Container:/Container/@ActualFreightCharge")%>">
	        <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;
			<yfc:getXMLValue binding="xml:/Container/@ActualFreightCharge" />&nbsp;
		    <%=curr0[1]%>
        </td>
	   <%if(bWMSNode){

		   boolean isContainerizationModifiable=false;
			%>
		<yfc:loopXML name="Container" binding="xml:/Container/ActivityList/@Activity" id="Activity" > 
			<%

				if(equals(resolveValue("xml:/Activity/@ActivityCode"),"RESOLVE-SHORT-PICK")){
					isContainerizationModifiable=true;
					break;
				}
   		
			%>
		</yfc:loopXML> 

		
		<td class="tablecolumn">

			<%if(isContainerizationModifiable){	%>
			 <a <%=getDetailHrefOptions("L02",getParameter("ShipmentContainerKey"), "&xml:/Container/@TransactionId=RESOLVE_SHORT_PICK.5001.ex")%> > 
			 <yfc:getXMLValueI18NDB binding="xml:/Container/Status/@Description"/>
			  <img class="icon" <%=getImageOptions(YFSUIBackendConsts.GO_ICON,"Resolve_Short_Pick")%> />
			 </a>

			<%}else{%>
			 <yfc:getXMLValueI18NDB binding="xml:/Container/Status/@Description"/>
			<%}%>
		</td>
		<%}%>
		<%if(bWMSNode&&equals(resolveValue("xml:/CurrentEntity/@ApplicationCode"),"omd")){%>
	    <td class="tablecolumn">
			<yfc:i18n><yfc:getXMLValue binding="xml:/Container/@IsManifested"/></yfc:i18n>
        </td>
		<%}%>
       	<%if(!outboundShipment){%>
		<td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/Container/@IsReceived"/>
        </td>
		<%}%>
    </tr>
	<%}%>
    </yfc:loopXML> 
</tbody>
<input type="hidden" name="xml:/Container/@ReasonCode"/>
<input type="hidden" name="xml:/Container/@ReasonText"/>
<input type="hidden" name="xml:/Shipment/@OrderAvailableOnSystem"/>    
<input type="hidden" 
	<%=getTextOptions("xml:/Shipment/@SCAC","xml:/Shipment/@SCAC")
	%>/>
	<input type="hidden" 
	<%=getTextOptions("xml:/Shipment/@IsShipmentLevelIntegration","xml:/Shipment/@IsShipmentLevelIntegration")
	%>/>
	<input type="hidden" 
	<%=getTextOptions("xml:/Shipment/@AllCntrsPrinted","xml:/Shipment/@AllCntrsPrinted")
	%>/>
</table>
