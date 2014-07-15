<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="table" cellspacing="0" width="100%">
<thead>
	<tr>
		<td class="tablecolumnheader" style="width:30px" sortable="no"><yfc:i18n>Tag_Serial</yfc:i18n></td>
			
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@OrderNo")%>" nowrap="true" sortable="no">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@PrimeLineNo")%>" nowrap="true" sortable="no">
            <yfc:i18n>Line_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@ReleaseNo")%>" nowrap="true" sortable="no">
            <yfc:i18n>Release_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@ItemID")%>" nowrap="true" sortable="no">
            <yfc:i18n>Item_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@ProductClass")%>" nowrap="true" sortable="no">
            <yfc:i18n>PC</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@UnitOfMeasure")%>" nowrap="true" sortable="no">
            <yfc:i18n>UOM</yfc:i18n>
        </td>

		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@Quantity")%>" nowrap="true" sortable="no">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentLine/@Quantity")%>" nowrap="true" sortable="no">
            <yfc:i18n>Pack_Quantity</yfc:i18n>
        </td>
	</tr>
	<%String className="oddrow";%>
    <yfc:loopXML binding="xml:/ShipmentLines/@ShipmentLine" id="ShipmentLine">
	  <% double linePackedQty=0;
		 double lineQty=getNumericValue("xml:/ShipmentLine/@Quantity");	
	  %>
			    <yfc:loopXML  name="ShipmentLine" binding="xml:/ShipmentLine/ContainerDetails/@ContainerDetail" id="ContainerDetail" >
			      <% double containerQty= getNumericValue("xml:/ContainerDetail/@Quantity");
				  
					 linePackedQty+=containerQty;
				  %> 	
				 	
				</yfc:loopXML> 

	<% 
		double lineUnPackedQty= lineQty-linePackedQty;
		
		if(lineUnPackedQty>0){
		
		%> 
			<%
				if (equals("oddrow",className))
						className="evenrow";
					else
						className="oddrow";													
			%>

				<%	
            YFCElement container = (YFCElement) request.getAttribute("Shipment");
			container.setAttribute("ItemID",resolveValue("xml:/ShipmentLine/@ItemID"));
			container.setAttribute("ProductClass",resolveValue("xml:/ShipmentLine/@ProductClass"));
			container.setAttribute("UnitOfMeasure",resolveValue("xml:/ShipmentLine/@UnitOfMeasure"));
			String sB= "xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_"+ShipmentLineCounter+"/ShipmentTagSerials/ShipmentTagSerial";		
			%>
				<yfc:callAPI apiID='AP1'/>
			<%	
			
				String  NumSecondarySerials =getValue("ItemDetails","xml:/Item/PrimaryInformation/@NumSecondarySerials");
				//boolean  captureSerials=equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked"));
				boolean  captureSerials=equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerializedFlag"));
				boolean  tagControlled=equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag"))||equals("S",getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag"));
				boolean  timeSensitive=equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"));
				
				if((tagControlled || captureSerials) && !isVoid(resolveValue("xml:/Shipment/@ShipNode"))){
					YFCElement tempItemElement = YFCDocument.parse("<Item ItemID=\"\" ItemKey=\"\" TagCapturedInShipping=\"\"><PrimaryInformation SerializedFlag=\"\" SerialCapturedInReturns=\"\" SerialCapturedInShipping=\"\" SerialCapturedInInventory=\"\" SerialCapturedInReceiving=\"\" /><InventoryParameters TagControlFlag=\"\" IsSerialTracked=\"\" TimeSensitive=\"\"/><InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\"><Extn/></InventoryTagAttributes> </Item>").getDocumentElement();
					YFCElement oItemDetailsElement = YFCDocument.createDocument("Item").getDocumentElement(); 
					oItemDetailsElement.setAttribute("CallingOrganizationCode",resolveValue("xml:/Shipment/@EnterpriseCode"));
					oItemDetailsElement.setAttribute("Node",resolveValue("xml:/Shipment/@ShipNode"));
					oItemDetailsElement.setAttribute("BuyerOrganization",resolveValue("xml:/Shipment/@BuyerOrganizationCode"));
					oItemDetailsElement.setAttribute("ItemID",resolveValue("xml:/ShipmentLine/@ItemID"));
					oItemDetailsElement.setAttribute("ProductClass",resolveValue("xml:/ShipmentLine/@ProductClass"));					oItemDetailsElement.setAttribute("UnitOfMeasure",resolveValue("xml:/ShipmentLine/@UnitOfMeasure"));								
%>
					<yfc:callAPI apiName="getNodeItemDetails" inputElement="<%=oItemDetailsElement%>" templateElement="<%=tempItemElement%>" outputNamespace="ItemDetails"/>
<%
					tagControlled = equals("Y",getValue("ItemDetails","xml:/Item/@TagCapturedInShipping")) ||equals("S",getValue("ItemDetails","xml:/Item/@TagCapturedInShipping"));

					captureSerials=equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInShipping"))||equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReturns"));
					
					
                    //disable serial capture if item is not serial tracked,Shipment is transfer shipment and shipNode excludes serial capture on transfers
                   	boolean bIsTransferShipment= false;
	                boolean bNodeCapturesSerialOnTransfer= true;
					boolean isSerialTracked= equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked"));		
					String sKey=resolveValue("xml:/Shipment/@ShipmentKey");
	                String sShipNode=resolveValue("xml:/Shipment/@ShipNode"); 

					if(!isSerialTracked){
						   if(!isVoid(sKey)){
							//calling getShipmentDetails
							YFCDocument inputDoc = YFCDocument.parse("<Shipment ShipmentKey=\""+sKey +"\"/>",true);
							YFCDocument templateDoc= YFCDocument.parse("<Shipment DocumentType=\"\" EnterpriseCode=\"\" SCAC=\"\" SellerOrganizationCode=\"\" IsTransferShipment=\"\" BuyerOrganizationCode=\"\" ShipmentNo=\"\" ShipmentKey=\"\" DoNotVerifyCaseContent=\"\" DoNotVerifyPalletContent=\"\" IsSingleOrder=\"\"   OrderNo=\"\" ShipNode=\"\" ReceivingNode=\"\" ><ShipmentLines><ShipmentLine ShipmentLineKey=\"\" OrderNo=\"\" /></ShipmentLines>	</Shipment> ");
							%>
								<yfc:callAPI  apiName="getShipmentDetails" inputElement='<%=inputDoc.getDocumentElement()%>' outputNamespace='Shipment' templateElement='<%=templateDoc.getDocumentElement()%>'  />
							<%						
							//check if shipment is Transfer Shipment	
							
							  YFCElement shipmentReceived = (YFCElement)request.getAttribute("Shipment");
								  if(!isVoid(shipmentReceived)){
									   bIsTransferShipment=shipmentReceived.getBooleanAttribute("IsTransferShipment");		
								 }
					       }
						if(bIsTransferShipment){
							YFCDocument inDoc = YFCDocument.parse("<ShipNode  ShipNode =\""+sShipNode +"\"/>",true);
							YFCDocument outDoc= YFCDocument.parse("<ShipNodeList> <ShipNode SerialTrackedOnTransfer=\"\" /> 	</ShipNodeList> ");
							%>
								<yfc:callAPI  apiName="getShipNodeList" inputElement='<%=inDoc.getDocumentElement()%>' outputNamespace='ShipNodeList' templateElement='<%=outDoc.getDocumentElement()%>'  />
							<%
								//check if shipNode captures serial for transfer
								YFCElement shipNodeListElem = (YFCElement)request.getAttribute("ShipNodeList");
								YFCElement shipNodeElem = shipNodeListElem.getChildElement("ShipNode");
								bNodeCapturesSerialOnTransfer=shipNodeElem.getBooleanAttribute("SerialTrackedOnTransfer");		 
						}
						if(bIsTransferShipment && !bNodeCapturesSerialOnTransfer) {
								   captureSerials = false ;
						}		
					}
				}

			%>
	
</thead>

<tbody>




			<tr class='<%=className%>'>
				<input type="hidden" NewName="true" 
				<%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_" + ShipmentLineCounter + "/@ShipmentLineKey", "xml:/ShipmentLine/@ShipmentLineKey")%> />
				<%if(captureSerials||tagControlled||timeSensitive){%>
				<td>
				
				<img onclick="expandCollapseDetails('optionSet_<%=ShipmentLineCounter%>','<%=replaceI18N("Click_To_See_Tag_Info")%>','<%=replaceI18N("Click_To_Hide_Tag_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')"  style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER,"Click_To_See_Tag_Info")%> />
				</td>
				<%} else{ %>
				<td></td>
				<%}%>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@OrderNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@PrimeLineNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@ReleaseNo" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@ItemID" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@ProductClass" />
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentLine/@UnitOfMeasure" />
				</td>
				<td class="numerictablecolumn" title="Total Unpacked Quantity is <%=getFormattedDouble(lineUnPackedQty)%>">
					<%=getFormattedDouble(lineUnPackedQty)%>
				</td>
				<td nowrap="true" class="tablecolumn">
					<input type="text"  class="unprotectedinput" 
					<%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_" + ShipmentLineCounter + "/@Quantity", "")%>/>
			   </td>
				<tr id='<%="optionSet_"+ShipmentLineCounter%>' class='<%=className%>' style="display:none">
		
					<td colspan="7" >
						<jsp:include page="/om/shipment/detail/shipment_detail_includetag.jsp" flush="true">
							<jsp:param name="optionSetBelongingToLine" value='<%=String.valueOf(ShipmentLineCounter)%>'/>
							<jsp:param name="shipmentLineKey" value='<%=resolveValue("xml:/ShipmentLine/@ShipmentLineKey")%>'/>
							<jsp:param name="Modifiable" value='true'/>
							<jsp:param name="LabelTDClass" value='detaillabel'/>
			                <jsp:param name="TagContainer" value='ShipmentLines'/>
					        <jsp:param name="TagElement" value='ShipmentTagSerials'/>
							<jsp:param name="RepeatingElement" value='ShipmentTagSerials'/>
							<jsp:param name="TotalBinding" value='<%=sB%>'/>
							<jsp:param name="isSerialized" value='<%=getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerializedFlag")%>'/> 
							<jsp:param name="SerialTracked" value='<%=getValue("ItemDetails","xml:/Item/InventoryParameters/@IsSerialTracked")%>' />
							<jsp:param name="NumSecondarySerials" value='<%=NumSecondarySerials%>' />
						</jsp:include>
					</td>
				</tr>
			</tr>
		<%}%>
   </yfc:loopXML>
</tbody>
</table>