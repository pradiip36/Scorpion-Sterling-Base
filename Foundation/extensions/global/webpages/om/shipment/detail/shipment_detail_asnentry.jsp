<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/supervisorypanelpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
yfcDoNotPromptForChanges(true); 
<%
	String sEntity="poshipment";
	String sNodeBinding="xml:/Shipment/@ReceivingNode";
	String sNodeLabel="Receiving_Node";
	String sOtherNodeBinding="xml:/Shipment/@ShipNode";
	String sOtherNodeLabel="Ship_Node";
	String shipmentNo= resolveValue("xml:/Shipment/@ShipmentNo");
	String shipmentKey= resolveValue("xml:/Shipment/@ShipmentKey");
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
	String sLookup = "polookup";

	if(equals("omd", sAppCode) ){
		sEntity = "shipment";
		sNodeBinding="xml:/Shipment/@ShipNode";
		sNodeLabel="Ship_Node";
		sOtherNodeBinding="xml:/Shipment/@ReceivingNode";
		sOtherNodeLabel="Receiving_Node";
		sLookup = "orderlookup";
	}
	if(equals("omr", sAppCode) ){
		sEntity = "returnshipment";
		sOtherNodeLabel="Return_Ship_Node";
		sLookup = "returnlookup";
	}


    if((!isVoid(shipmentNo))&&(!isVoid(shipmentKey))){
	
	YFCDocument shipmentDoc = YFCDocument.createDocument("Shipment");
	shipmentDoc.getDocumentElement().setAttribute("ShipmentKey",resolveValue("xml:/Shipment/@ShipmentKey"));
	shipmentDoc.getDocumentElement().setAttribute("DocumentType",resolveValue("xml:/Shipment/@DocumentType"));
  
%>
	function changeToPOShipmentDetailView() {
          entityType = '<%=sEntity%>';
		  showDetailFor('<%=shipmentDoc.getDocumentElement().getString(false)%>');
    }
	window.attachEvent("onload", changeToPOShipmentDetailView);

<%}%>
</script>
<script>
	function hideLookUpIcon() {
		yfcChangeDetailView(getCurrentViewId());
	}
</script>

<table class="view" width="100%">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
		<jsp:param name="ScreenType" value="detail"/>
		<jsp:param name="ShowDocumentType" value="true"/>
		<jsp:param name="DocumentTypeBinding" value="xml:/Shipment/@DocumentType"/>
		<jsp:param name="ShowNode" value="true"/>
		<jsp:param name="EnterpriseCodeBinding" value="xml:/Shipment/@EnterpriseCode"/>
		<jsp:param name="RefreshOnDocumentType" value="true"/>
		<jsp:param name="RefreshOnNode" value="true"/>
        <jsp:param name="EnterpriseListForNodeField" value="true"/>
	    <jsp:param name="NodeBinding" value="<%=sNodeBinding%>"/>
		<jsp:param name="NodeLabel" value="<%=sNodeLabel%>"/>
		<jsp:param name="RefreshOnEnterpriseCode" value="true"/>
</jsp:include>
    <yfc:callAPI apiID="AP1"/>
    <tr>
        <td class="detaillabel"  nowrap="true" >
		<yfc:i18n>Shipment_#</yfc:i18n> 
		</td>
        <td class="searchcriteriacell" nowrap="true"  >
		<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@ShipmentNo")%> />
		</td>
	    <td class="detaillabel" nowrap="true" > 
			<yfc:i18n><%=sOtherNodeLabel%></yfc:i18n> 
		</td>
    	<td class="searchcriteriacell"  nowrap="true">
		    <input type="text" class="unprotectedinput"  <%=getTextOptions(sOtherNodeBinding)%> />

			<% String extraParams = getExtraParamsForTargetBinding("xml:/ShipNode/Organization/EnterpriseOrgList/OrgEnterprise/@EnterpriseOrganizationKey", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>

			<img class="lookupicon" onclick="callLookup(this,'nodelookup','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_ShipNode") %> />
		</td>
		<td class="detaillabel"  nowrap="true">
			<yfc:i18n>Buyer</yfc:i18n> 
		</td>
		<td class="searchcriteriacell"  nowrap="true">
			<input type="text" class="unprotectedinput"<%=getTextOptions("xml:/Shipment/@BuyerOrganizationCode")%>/>
            <% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Shipment/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
      	</td>
   </tr>
   <tr>
        <td class="detaillabel"  nowrap="true">
			<yfc:i18n>Seller</yfc:i18n> 
		</td>
		<td class="searchcriteriacell" nowrap="true" >
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@SellerOrganizationCode")%> />
            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Shipment/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
		<td class="detaillabel" nowrap="true" > 
            <yfc:i18n>Order_#</yfc:i18n> 
		</td>
		<td class="searchcriteriacell"  nowrap="true">
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@OrderNo")%> />
			<% if(!equals("N",resolveValue("xml:/Shipment/@OrderAvailableOnSystem"))) { %>
				<img class="lookupicon" onclick="callLookup(this,'<%=sLookup%>','xml:/Order/@DocumentType=<%=getValue("CommonFields", "xml:/CommonFields/@DocumentType")%>&xml:/Order/@EnterpriseCode=<%=getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Order") %> />
			<% } %>
		</td>
		<td class="detaillabel" nowrap="true" > 
			<yfc:i18n>Release_#</yfc:i18n> 
		</td>
		<td class="searchcriteriacell" nowrap="true" >
			 <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@ReleaseNo")%> />
		</td>
    </tr>
    <tr>
		<td class="detaillabel" nowrap="true" > 
			<yfc:i18n>Pro_#</yfc:i18n> 
		</td>

		<td class="searchcriteriacell" nowrap="true" >
			 <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ProNo")%> />

		</td>
		<td class="detaillabel" >
			<yfc:i18n>Carrier_Service</yfc:i18n>
		</td>
		<td>
		<select name="xml:/Shipment/ScacAndService/@ScacAndServiceKey" class="combobox">
			<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/Shipment/@ScacAndServiceKey" isLocalized="Y"/>
		</select>
		</td>
		<td class="detaillabel" nowrap="true" >
		<yfc:i18n>BOL_#</yfc:i18n> 
		</td>
		<td class="searchcriteriacell" nowrap="true" >
	    <input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@BolNo")%> />
		</td>
</tr>
<tr>
		<td class="detaillabel" nowrap="true" >
			<yfc:i18n>Trailer_#</yfc:i18n> 
		</td>
		<td class="searchcriteriacell" nowrap="true" >
			<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/@TrailerNo")%> />
		</td>
		<td/>
		<td/>
		<td/>
		<td/>

	</tr>


</table>
<%if(resolveValue("xml:/Shipment/@OverrideManualShipmentEntry")!=""){%>
<input type="hidden" name="xml:/Shipment/@OverrideManualShipmentEntry" value='<%=resolveValue("xml:/Shipment/@OverrideManualShipmentEntry")%>'/>
<%}
else{
if (equals("oms", sAppCode)){
%>

	<input type="hidden" name="xml:/Shipment/@OverrideManualShipmentEntry" value="N"/>
<%}else{%>
	<input type="hidden" name="xml:/Shipment/@OverrideManualShipmentEntry" value="Y"/>
<%}}%>

<input type="hidden" name="xml:/Shipment/@OrderAvailableOnSystem" value=""/>

<%if(resolveValue("xml:/Shipment/@DoNotVerifyPalletContent")!=""){%>
<input type="hidden" name="xml:/Shipment/@DoNotVerifyPalletContent" value='<%=resolveValue("xml:/Shipment/@DoNotVerifyPalletContent")%>'/>
<%}
else{
%>
<input type="hidden" name="xml:/Shipment/@DoNotVerifyPalletContent" value="N"/>
<%}%>
<%if(resolveValue("xml:/Shipment/@DoNotVerifyPalletContent")!=""){%>
<input type="hidden" name="xml:/Shipment/@DoNotVerifyCaseContent" value='<%=resolveValue("xml:/Shipment/@DoNotVerifyCaseContent")%>'/>
<%}
else{
%>
<input type="hidden" name="xml:/Shipment/@DoNotVerifyCaseContent" value="N"/>
<%}%>
<%if(resolveValue("xml:/Shipment/@AllowOverage")!=""){%>
<input type="hidden" name="xml:/Shipment/@AllowOverage" value='<%=resolveValue("xml:/Shipment/@AllowOverage")%>'/>
<%}
else{
%>
<input type="hidden" name="xml:/Shipment/@AllowOverage" value="N"/>
<%}%>
<%if(resolveValue("xml:/Shipment/@AllowNewItemReceipt")!=""){%>
<input type="hidden" name="xml:/Shipment/@AllowNewItemReceipt" value='<%=resolveValue("xml:/Shipment/@AllowNewItemReceipt")%>'/>
<%}
else{
%>
<input type="hidden" name="xml:/Shipment/@AllowNewItemReceipt" value="DO_NOT_ALLOW"/>
<%}%>
<input type="hidden" name="xml:/Shipment/@ManuallyEntered" value="Y"/>
