<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Warehouse Management System
(C) Copyright IBM Corp. 2005, 2011 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>
<%int i=0;%>
<table class="anchor" cellpadding="7px"  cellSpacing="0" >
 <tr>
	 <td colspan="3">
		 <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="getRequestDOM" value="Y"/>
			<jsp:param name="RootNodeName" value="ShipmentLines"/>
			<jsp:param name="ChildLoopXMLName" value="ShipmentTagSerial"/>
         </jsp:include>
	 </td>
 <tr>
 <tr>
	 <td width="80%" height="100%" addressip="true">
		 <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
         </jsp:include>
	 </td>
     <td width="20%" height="100%" addressip="true">
		 <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
		    <jsp:param name="CurrentInnerPanelID" value="I07"/>
			<jsp:param name="Path" value="xml:/ShipmentLines/ShipmentLine/MarkForAddress"/>
			<jsp:param name="DataXML" value="ShipmentLines"/>
			<jsp:param name="AllowedModValue" value='Y'/>
		</jsp:include>
	</td>
 <tr>
  
  <tr>
   <yfc:hasXMLNode binding="xml:/ShipmentLines/ShipmentLine/ShipmentLineInvAttRequest">
	   <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
			<% YFCElement tagElem =(  YFCElement) request.getAttribute("Item");
			   if(tagElem!=null){
				   request.setAttribute("Item", tagElem);
			   }
			%>
			<td colspan="3">
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
						<jsp:param name="Modifiable" value='false'/>
						<jsp:param name="LabelTDClass" value='detaillabel'/>
						<jsp:param name="TagContainer" value='ShipmentLines'/>
						<jsp:param name="TagElement" value='ShipmentLineInvAttRequest'/>
					</jsp:include>
			</td>
		</yfc:hasXMLNode>
    </yfc:hasXMLNode>
</tr>
<%if(equals("N",resolveValue("xml:/ShipmentLines/ShipmentLine/@IsPickable"))){%>
<tr>
	 <td colspan="3">
		 <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
         </jsp:include>
	 </td>
</tr>
<%}else{%>
<tr>

	

			<% //show the inner panel only if the item is either tag controlled or serialized or time sensitive
				boolean  serialTracked=equals("Y",resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns"))||equals("Y",resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"));
				boolean  tagControlled=equals("Y",resolveValue("xml:ItemDetails:/Item/InventoryParameters/@TagControlFlag")) ||equals("S",resolveValue("xml:ItemDetails:/Item/InventoryParameters/@TagControlFlag"));
				boolean  timeSensitive=equals("Y",resolveValue("xml:ItemDetails:/Item/InventoryParameters/@TimeSensitive"));

                String sRecNode = resolveValue("xml:/ShipmentLines/ShipmentLine/Shipment/@ReceivingNode");
				String shipNode = resolveValue("xml:/ShipmentLine/@ShipNode");
				if(!isVoid(shipNode) || !isVoid(sRecNode)){
					boolean bCheckSerialFlagsInOutbound = false;
					YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\" ItemKey=\"\" TagCapturedInShipping=\"\"><PrimaryInformation SerializedFlag=\"\" SerialCapturedInReturns=\"\" SerialCapturedInShipping=\"\" SerialCapturedInInventory=\"\" SerialCapturedInReceiving=\"\" /><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/><InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item>").getDocumentElement();
					YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement(); 
					oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/ShipmentLine/@EnterpriseCode"));
					oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/ShipmentLine/@ItemID"));
					if(!isVoid(shipNode)){
						bCheckSerialFlagsInOutbound = true;
						oItemDetailsElement.setAttribute("Node",shipNode);
					}else if(!isVoid(sRecNode)){
						oItemDetailsElement.setAttribute("Node",sRecNode);
					}
					oItemDetailsElement.setAttribute("BuyerOrganization",resolveValue("xml:/ShipmentLine/@BuyerOrganizationCode"));
					oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/ShipmentLine/@UnitOfMeasure")); 
%>
					<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>			
<%
					tagControlled = equals("Y",getValue("ItemDetails","xml:/Item/@TagCapturedInShipping")) ||equals("S",getValue("ItemDetails","xml:/Item/@TagCapturedInShipping"));
					if(bCheckSerialFlagsInOutbound){
						serialTracked = equals("Y",resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturns")) || equals("Y",resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"));
					}else{
						serialTracked = equals("Y",resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReceiving"));
					}
				}			

				if(serialTracked||tagControlled||timeSensitive){
					YFCElement tagElem =(  YFCElement) request.getAttribute("Item");
					if(tagElem!=null){
						request.setAttribute("Item", tagElem);
					}
					%>
					<td colspan="3">
						<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
							<jsp:param name="CurrentInnerPanelID" value="I03"/>
							<jsp:param name="Modifiable" value='true'/>
							<jsp:param name="LabelTDClass" value='detaillabel'/>
							<jsp:param name="TagContainer" value='ShipmentLines'/>
							<jsp:param name="TagElement" value='ShipmentTagSerials'/>
							<jsp:param name="RepeatingElement" value='ShipmentTagSerials'/>
							<jsp:param name="TotalBinding" value='xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial'/>
							<jsp:param name="RootNodeName" value="ShipmentLines"/>
							<jsp:param name="RootNodeDifference" value="ShipmentLine"/>
							<jsp:param name="ChildLoopXMLName" value="ShipmentTagSerial"/>
							<jsp:param name="ChildLoopXMLSecondaryKeyName" value="ShipmentLineKey"/>
							<jsp:param name="ChildLoopParentNodeDifferenceForKey" value="Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial"/>
						</jsp:include>
					</td>
			<%}%>

</tr>
<%}%>
</table>