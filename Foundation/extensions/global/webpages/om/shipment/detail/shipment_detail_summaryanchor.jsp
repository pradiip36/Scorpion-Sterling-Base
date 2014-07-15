<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>

<%

setHoldFuncFlags((YFCElement) request.getAttribute("Shipment"), isTrue("xml:/Rules/@RuleSetValue"));

YFCElement shipElem = (YFCElement) request.getAttribute("Shipment");
String useholdvalue = shipElem.getAttribute("UseNewHoldFunctionality");

%>

<script language="javascript">
	function callSingleTaskComplete(){
		yfcShowDetailPopupWithKeys('YWMD128', 'Complete_Task', '1010', '650',new Object(),'shipmentTaskKey', 'shipment');  
	}
	function callMultiTaskComplete(){
		yfcShowDetailPopupWithKeys('YWMD120', 'Task_Detail', '1010', '650',new Object(),'shipmentTaskKey', 'shipment');  
	}
</script>

<%!	boolean hasOneLoadContainerManifested = false;
		boolean hasOneManifestedConatiner = false;
		boolean isWMSNode = false;%>

<%
	if("1".equals(resolveValue("xml:/Shipment/LoadShipments/@TotalNumberOfRecords"))) {
    hasOneLoadContainerManifested = !isVoid(resolveValue("xml:/Shipment/LoadShipments/LoadShipment/Load/@ManifestKey")); 
	}else { %>	
		<yfc:loopXML binding="xml:/Shipment/LoadShipments/@LoadShipment" id="LoadShipment">
			<%	if( !hasOneLoadContainerManifested && 			(!isVoid(resolveValue("xml:/LoadShipment/Load/@ManifestKey")))){
					hasOneLoadContainerManifested= true;
				}%>
		</yfc:loopXML>
<%} isWMSNode = equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")));
   if( (isWMSNode && !isVoid(resolveValue("xml:/Shipment/@ManifestKey")))
	    || (isWMSNode && hasOneLoadContainerManifested)){
		hasOneManifestedConatiner = true;
   }else{
		hasOneManifestedConatiner = false;
   }
 %>

<%
//Here, take the output of findReceipt API for open receipts to 
//set the visible binding for start receipt &  receive options.
double isOpen = getNumericValue("xml:/Receipts/@TotalNumberOfRecords");

YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
boolean shipmentInWave = false;
YFCNodeList shipmentLineList = elemShip.getElementsByTagName("ShipmentLine");
for(int i=0;i<shipmentLineList.getLength();i++){
	YFCElement shipLineElem = (YFCElement)shipmentLineList.item(i);
	if(!isVoid(shipLineElem.getAttribute("WaveNo"))){
		shipmentInWave = true;
		break;
	}
}

if(elemShip != null){
	elemShip.setAttribute("IsIncludedInWave",shipmentInWave);
	elemShip.setAttribute("EnableSplServicesEntry", !hasOneManifestedConatiner);
	elemShip.setAttribute("HasOneManifestedContainer", hasOneManifestedConatiner);
	if (elemShip.getBooleanAttribute("ShipmentConfirmUpdatesDone")) {
		elemShip.setAttribute("ConfirmFlag", false);
	}
	else {
		elemShip.setAttribute("ConfirmFlag", true);
	}
	elemShip.setAttribute("WMSNode", false);
	elemShip.setAttribute("NonWMSNode", true);
	
	if (isWMSNode) {
		elemShip.setAttribute("WMSNode", true);
		elemShip.setAttribute("NonWMSNode", false);
	}

    YFCElement oAllowedTransactions = elemShip.getChildElement("AllowedTransactions");
	if(!isVoid(oAllowedTransactions)){
		YFCNodeList AllowedTransactionsList = oAllowedTransactions.getElementsByTagName("Transaction");
		for(int k=0;k<AllowedTransactionsList.getLength();k++){
		YFCElement transactionElem = (YFCElement)AllowedTransactionsList.item(k);
		  if(equals(transactionElem.getAttribute("BaseTransactionKey"),"PRINT_PICK_LIST")){
			elemShip.setAttribute("DisplayPickListFlag", true);
			break;
		}
		  else {
			elemShip.setAttribute("DisplayPickListFlag", false);
		 }
	 }
		 }else {
			  elemShip.setAttribute("DisplayPickListFlag", false);
	 }

    if(isVoid(resolveValue("xml:/Shipment/@PickListNo"))){
           elemShip.setAttribute("VisiblePickListFlag", "Y");
  } else {
		elemShip.setAttribute("VisiblePickListFlag", "");
  }

	elemShip.setAttribute("EnableCancel", true); 
	if("1300".compareTo(resolveValue("xml:/Shipment/@Status")) <= 0 ) {
		elemShip.setAttribute("EnableCancel", false);
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
	   //check if shipment is in receivable status. If yes, enable the actions.	
	   if(elemShip != null){
			if(!(equals("1100", elemShip.getAttribute("Status")))){
					elem.setAttribute("StartReceiptFlag", true); 
					
			}

		}
	}
}
	
	String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");
%>
<% double iBlankTemplates = 8;
	if (!isVoid(resolveValue("xml:/Shipment/@OrderHeaderKey"))) { %>
	<yfc:callAPI apiID='AP1'/>
	<%	if (0 != getNumericValue("xml:Order:/Order/OrderLines/@TotalNumberOfRecords")) {
			iBlankTemplates = getNumericValue("xml:Order:/Order/OrderLines/@TotalNumberOfRecords");
	}
}	%> 

<yfc:callAPI apiID='AP2'/>
<%
double dTotalTasks = getNumericValue("xml:TaskList:/TaskList/@TotalNumberOfRecords");
if (dTotalTasks > 0) {
	if(dTotalTasks == 1){
		elemShip.setAttribute("SingleTaskComplete", true);
		elemShip.setAttribute("MultiTaskComplete", false);
	}else{
		elemShip.setAttribute("SingleTaskComplete", false);
		elemShip.setAttribute("MultiTaskComplete", true);
	}
}else{
	elemShip.setAttribute("SingleTaskComplete", false);
	elemShip.setAttribute("MultiTaskComplete", false);
}
%>
<yfc:loopXML binding="xml:TaskList:/TaskList/@Task" id="Task"> 
	<yfc:makeXMLInput name="taskKey">
		<yfc:makeXMLKey binding="xml:/Task/@TaskKey" value="xml:/Task/@TaskKey" />
	</yfc:makeXMLInput>
	<input type="hidden" value='<%=getParameter("taskKey")%>' name="shipmentTaskKey"/>
</yfc:loopXML>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td>
        <yfc:makeXMLInput name="shipmentKey">
            <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("shipmentKey")%>' name="ShipmentEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/Shipment/@ShipmentKey")%>/>
    </td>
</tr>
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
		    <jsp:param name="RootNodeName" value="Shipment"/>
			<jsp:param name="NoOfBlankTemplates" value="<%=String.valueOf(iBlankTemplates)%>"/>
        </jsp:include>
    </td>
</tr>
<tr>
<%	if (equals(sAppCode,"omd")) {	 //in case of outbound order, put execution status inner panel%>
				<td width="45%" height="100%" addressip="true">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I09"/>
					</jsp:include>
				</td>  
				<td width="25%" height="100%" id="shipToAddress">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
						<jsp:param name="Path" value="xml:/Shipment/ToAddress"/>
						<jsp:param name="DataXML" value="Shipment"/>
						<jsp:param name="AllowedModValue" value='Y'/>
					</jsp:include>
				</td>
				<td width="30%" height="100%" >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I03"/>
					</jsp:include>
				</td>
<%	}	else	{	%>
				<td width="33%" height="100%" id="shipFromAddress">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I07"/>
						<jsp:param name="Path" value="xml:/Shipment/FromAddress"/>
						<jsp:param name="DataXML" value="Shipment"/>
					</jsp:include>
				</td>  
				<td width="33%" height="100%" id="shipToAddress">
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
<%	}	%>

</tr>
<%	
	String sInnerPanel = null;
	if(equals("DEL", resolveValue("xml:/Shipment/@DeliveryMethod") ) ) 	{
		sInnerPanel = "I08";
	}
	else {
		sInnerPanel = "I05";
	}
%>

<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="<%=sInnerPanel%>" />
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
		    <jsp:param name="RootNodeName" value="Shipment"/>
		    <jsp:param name="ChildLoopXMLSecondaryKeyName" value="ShipmentSubLineNo"/>
			<jsp:param name="NoOfBlankTemplates" value="<%=String.valueOf(iBlankTemplates)%>"/>
        </jsp:include>
    </td>
</tr>
</table>