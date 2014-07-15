<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.core.YFSSystem" %>

<%!  String sLoadNo = "";
     String sIsBreakBulkLoad = ""; %>
<%String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	boolean outboundShipment = false;
	if(equals(sAppCode,"omd")){
		outboundShipment = true;
	}
%>

<%if("1".equals(resolveValue("xml:/Shipment/LoadShipments/@TotalNumberOfRecords"))) {
    sLoadNo = resolveValue("xml:/Shipment/LoadShipments/LoadShipment/Load/@LoadNo"); 
	sIsBreakBulkLoad = resolveValue("xml:/Shipment/LoadShipments/LoadShipment/Load/@IsBreakBulkLoad");
	}else { %>	
		<yfc:loopXML binding="xml:/Shipment/LoadShipments/@LoadShipment" id="LoadShipment">
			<%if((equals(resolveValue("xml:/LoadShipment/Load/@DestinationNode"),
				  resolveValue("xml:/Shipment/@ReceivingNode")))||
				  (equals(resolveValue("xml:/LoadShipment/Load/@DestinationAddressKey"),
				  resolveValue("xml:/Shipment/@ToAddressKey"))) ) {
				  sLoadNo = resolveValue("xml:/Shipment/LoadShipments/LoadShipment/Load/@LoadNo"); }%>	
		</yfc:loopXML>
<%}%>	

<table width="100%" class="view">
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Delivery_Method</yfc:i18n>
		</td>
		<td  class="protectedtext">	<%=getComboText("xml:DeliveryMethodList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Shipment/@DeliveryMethod",true)%>
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Ship_Mode</yfc:i18n>
		</td>
		<td  class="protectedtext">
			<%=getComboText("xml:ShipmentModeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/Shipment/@ShipMode",true)%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Carrier_Service</yfc:i18n>
		</td>
		<% if(YFCCommon.isVoid(sLoadNo)) { %>
			<td class="protectedtext">
				<%if(equals(resolveValue("xml:/Shipment/@HasOneManifestedContainer"),"Y")){ %> <yfc:getXMLValueI18NDB binding="xml:/Shipment/ScacAndService/@ScacAndServiceDesc"/>
				<%}else{%>
				<select   <%=yfsGetComboOptions("xml:/Shipment/ScacAndService/@ScacAndServiceKey", "xml:/Shipment/ScacAndService/@ScacAndServiceKey", "xml:/Shipment/AllowedModifications")%>>
					<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
					 value="ScacAndServiceKey" selected="xml:/Shipment/ScacAndService/@ScacAndServiceKey" isLocalized="Y"/>
				</select>
				<%}%>
			</td>
		<%}else { %>
			<td  class="protectedtext">	
			<%
			if(!YFCCommon.isVoid(sIsBreakBulkLoad) && !equals(resolveValue("xml:/Shipment/@HasOneManifestedContainer"),"Y")){%>
			<select   <%=yfsGetComboOptions("xml:/Shipment/ScacAndService/@ScacAndServiceKey", "xml:/Shipment/ScacAndService/@ScacAndServiceKey", "xml:/Shipment/AllowedModifications")%>>
					<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
					 value="ScacAndServiceKey" selected="xml:/Shipment/ScacAndService/@ScacAndServiceKey" isLocalized="Y"/>
				</select>
			<%}else{%>
				<yfc:getXMLValueI18NDB binding="xml:/Shipment/ScacAndService/@ScacAndServiceDesc"/>
			<%}%>
			</td>
		<%}%>

		<% sLoadNo=""; %>
		<td class="detaillabel" >
			<yfc:i18n>Manifest_#</yfc:i18n> 
		</td>
		<td class="protectedtext" >
			<%
			String svalue = YFSSystem.getProperty("yfs.manifest.manifestAtContainerLevelForDomesticParcelShipment");			
			if(YFCCommon.isVoid(svalue) || svalue.equals("N")){%>
			<yfc:getXMLValue  binding="xml:/Shipment/@ManifestNo"/>
			<%}%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Trailer_#</yfc:i18n> 
		</td>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@TrailerNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>	
		<td class="detaillabel">
			<yfc:i18n>BOL_#</yfc:i18n> 
		</td>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@BolNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Pro_#</yfc:i18n> 
		</td>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@ProNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Seal_#</yfc:i18n> 
		</td>
		<td nowrap="true">
			<input type="text" <%=yfsGetTextOptions("xml:/Shipment/@SealNo", "xml:/Shipment/AllowedModifications")%>/>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Routing_Source</yfc:i18n> 
		</td>
		<td  class="protectedtext">			
			<%
				String str="";
				String sRoutSrc=resolveValue("xml:/Shipment/@RoutingSource");
				if(equals(sRoutSrc,"01"))
				{
					str="Yantra_Assigned";
				}
				else if(equals(sRoutSrc,"02"))
				{
					str="Pre_Assigned";
				}
				else if(equals(sRoutSrc,"03"))
				{
					str="Assigned_Externally";
				}
				else if(equals(sRoutSrc,"04"))
				{
					str="User_Assigned";
				}
			%>
			<yfc:i18n><%=str%></yfc:i18n> 
		</td>

		<td class="detaillabel">
			<yfc:i18n>Load_#</yfc:i18n> 
		</td>
		<td  class="protectedtext">
		
		<%if("1".equals(resolveValue("xml:/Shipment/LoadShipments/@TotalNumberOfRecords"))){%>
			<yfc:makeXMLInput name="LoadKey" >
				<yfc:makeXMLKey binding="xml:/Load/@LoadKey" value="xml:/Shipment/LoadShipments/LoadShipment/Load/@LoadKey" />
			</yfc:makeXMLInput>
			<a <%=getDetailHrefOptions("L01",getParameter("LoadKey"),"")%> ><yfc:getXMLValue binding="xml:/Shipment/LoadShipments/LoadShipment/Load/@LoadNo"/></a>
		<%} else {%>	
			<yfc:loopXML binding="xml:/Shipment/LoadShipments/@LoadShipment" id="LoadShipment">
				<%if((equals(resolveValue("xml:/LoadShipment/Load/@DestinationNode"),resolveValue("xml:/Shipment/@ReceivingNode")))||(equals(resolveValue("xml:/LoadShipment/Load/@DestinationAddressKey"),resolveValue("xml:/Shipment/@ToAddressKey"))) ){%>
					<yfc:makeXMLInput name="LoadKey" >
						<yfc:makeXMLKey binding="xml:/Load/@LoadKey" value="xml:/LoadShipment/Load/@LoadKey" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L01",getParameter("LoadKey"),"")%> ><yfc:getXMLValue binding="xml:/LoadShipment/Load/@LoadNo"/></a>
				<%}%>	
			</yfc:loopXML>
		<%}%>	
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Routing_Error_Code</yfc:i18n> 
		</td>
		<td  class="protectedtext">			
			<%
			   String sRoutError=resolveValue("xml:/Shipment/@RoutingErrorCode");
				if(equals(sRoutError,"01"))
				{
					sRoutError="Could_not_determine_SCAC";
				}
				if(equals(sRoutError,"02"))
				{
					sRoutError="Could_not_determine_SCAC_for_second_leg";
				}
			%>
			<yfc:i18n><%=sRoutError%></yfc:i18n> 
		</td>

		<td class="detaillabel" >
			<yfc:i18n>Requested_Carrier_Service_Code</yfc:i18n>
		</td>
<%		String sCarrierServiceCode = resolveValue("xml:/Shipment/@CarrierServiceCode");
		Map carrServMap=new HashMap();		
		YFCElement carrServListElem= (YFCElement ) request.getAttribute("CarrierServiceList");
		YFCNodeList carrServList= null;
		if (isVoid(sCarrierServiceCode)) { 
				if(carrServListElem!=null){	
					carrServList= carrServListElem.getElementsByTagName("CarrierService");
					if(carrServList!=null){
						for(int l=0; l < carrServList.getLength() ; l++ ){
							YFCElement carrServ=(YFCElement) carrServList.item(l);
							carrServMap.put(carrServ.getAttribute("CarrierServiceCode"), carrServ);
						}
					}
				}
			
			YFCElement newCarrServList = YFCDocument.parse("<NewCarrierServiceList/>").getDocumentElement();
			if(carrServMap!=null){
				Iterator iter=carrServMap.keySet().iterator();
				while(iter.hasNext()){
					YFCElement carrServ=(YFCElement) carrServMap.get(iter.next());
					newCarrServList.importNode(carrServ);
		
				}
			}
			request.setAttribute("NewCarrierServiceList",newCarrServList);
			%>
			<td>
				<select  <%=yfsGetComboOptions("xml:/Shipment/@RequestedCarrierServiceCode", "xml:/Shipment/@RequestedCarrierServiceCode", "xml:/Shipment/AllowedModifications")%>>
					<yfc:loopOptions binding="xml:/NewCarrierServiceList/@CarrierService" name="CarrierServiceDesc"
					 value="CarrierServiceCode" selected="xml:/Shipment/@RequestedCarrierServiceCode" isLocalized="Y"/>
				</select>
			</td>
<%		} else {	%>
			<td class="protectedtext" >
				<yfc:getXMLValue  binding="xml:/Shipment/@RequestedCarrierServiceCode"/>
			</td>
<%		}	%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Airway_Bill_No</yfc:i18n> 
		</td>
		<td  class="protectedtext">			
			<yfc:getXMLValue  binding="xml:/Shipment/@AirwayBillNo"/>
		</td>

		<%if(outboundShipment){%>
	<td class="detaillabel" >
		<yfc:i18n>Return_Carrier_Service</yfc:i18n> 
	 </td>
	<td  class="protectedtext">	
			  <select   <%=yfsGetComboOptions("xml:/Shipment/@ReturnCarrierService", "xml:/Shipment/@ReturnCarrierService", "xml:/Shipment/AllowedModifications")%>>
					<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
					 value="ScacAndService" selected="xml:/Shipment/@ReturnCarrierService" isLocalized="Y"/>
				</select>
	</td>
	<%}%>
	</tr>
	<tr>
	    <td class="detaillabel" >
			<yfc:i18n>Is_Revised</yfc:i18n> 
	  </td>
		<td  class="protectedtext">			
			<yfc:getXMLValue  binding="xml:/Shipment/@IsRevised"/>
		</td>

	</tr>
</table>