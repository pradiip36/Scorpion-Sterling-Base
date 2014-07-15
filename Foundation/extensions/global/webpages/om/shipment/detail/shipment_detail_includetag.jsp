<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">
yfcDoNotPromptForChanges(true); 
</script>
<SCRIPT LANGUAGE="JavaScript">
	function toggleSerialScanning(sCounter) {
		var docInputs=document.getElementsByTagName("tr");		
		for (var i=0;i<docInputs.length;i++) {
		var docInput=docInputs.item(i);
		if(docInput.getAttribute("action") == "ADD"){
			var tds = docInput.getElementsByTagName("td");
			for(var j=0;j<tds.length;j++){
				var td = tds.item(j);
				if( td.getAttribute("id") == "singleserialtext"+sCounter){
					if(td.style.display == "none")		
						td.style.display = '';
					else
						td.style.display = 'none'; 		
				}
				if(td.getAttribute("id") == "serialrangefromtext"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangefromlabel"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangetotext"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangetolabel"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
			}
		}
		if (docInput.getAttribute("TemplateRow") == "true") {
			var tds = docInput.getElementsByTagName("td");
			
			for(var j=0;j<tds.length;j++){
				var td = tds.item(j);
				if( td.getAttribute("id") == "singleserialtext"+sCounter){
					if(td.style.display == "none")		
						td.style.display = '';
					else
						td.style.display = 'none'; 		
				}
				if(td.getAttribute("id") == "serialrangefromtext"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangefromlabel"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangetotext"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
				if(td.getAttribute("id") == "serialrangetolabel"+sCounter){
					if(td.style.display == "none")
						td.style.display = "";
					else
						td.style.display = "none";
				}
			}
			
		}
		
	}


}
</SCRIPT>
<%int tagtrack=0;%>
<table class="table" >
<tbody>
    <tr>
		<td width="10%" >
			&nbsp;
		</td>
<td width="80%" style="border:1px solid black">
<table class="table" editable="true" width="100%" cellspacing="0">
<%
	Map identifierAttrMap=null;
	Map descriptorAttrMap=null;
	Map extnIdentifierAttrMap=null;
	Map extnDescriptorAttrMap=null;
	String sOptionCounter = request.getParameter("optionSetBelongingToLine");
	int NoSecSerials = 0;
	String sNoSecSerials = request.getParameter("NumSecondarySerials");
	boolean isSerialized = false;
	if(!isVoid(resolveValue("xml:/Shipment/@ShipNode"))){
		isSerialized = equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInShipping"))||equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReturns"));
		
	}else{
		isSerialized = equals("Y", request.getParameter("isSerialized"));
	}


	if(!isVoid(sNoSecSerials)){
		NoSecSerials = (new Integer(sNoSecSerials)).intValue();
	}	 

	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}
	%>  
	 <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	<%
		prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
		identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
		descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
		extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
		extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
	%>
	 </yfc:hasXMLNode>
	<%
	String modifiable = request.getParameter("Modifiable");
	boolean isModifiable = false;
	if (equals(modifiable,"true")) {
		isModifiable = true;
	}%>
    <thead> 
   <tr>
   <td class="tablecolumnheader">&nbsp
   </td>
	<%
	int i = 0;
	while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {
			tagtrack=1;%>
					
							<%while (j < 2) {
							    boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader"><yfc:i18n><%=currentAttr%></yfc:i18n></td>
								    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>	
		<%
	   //prompt for ship by date if time sensitive
	   if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
						<td sortable="no" class="tablecolumnheader"> 
							<yfc:i18n>Ship_By_Date</yfc:i18n>
						</td>
		<%}%>
			<%
	   //prompt for serial no if serialized 
	   
	   if(isSerialized){%>
						<td sortable="no" class="tablecolumnheader" colspan="4" >
							<yfc:i18n>Serial_#</yfc:i18n>
						</td>
		<%}%>
		<% 
		if(!equals("Y",(request.getParameter("SerialTracked"))))
		   {
	    %>
	
						<td sortable="no" class="tablecolumnheader"> 
							<yfc:i18n>Quantity</yfc:i18n>
						</td>

		<% } %>
		<% if(NoSecSerials==0){ %>
		<td>
			<img class="lookupicon" name="search" onclick="toggleSerialScanning(<%=sOptionCounter%>);return false" <%=getImageOptions(YFSUIBackendConsts.MILESTONE_COLUMN, "Toggle_Serial_Entry") %> />
		</td>
		<% } %>
</tr>
</thead>
<tbody>
	      <yfc:loopXML binding="xml:/ShipmentLines/@ShipmentLine" id="ShipmentLine">
			 <%
			 String sLineNo=getParameter("optionSetBelongingToLine");
			 String sLineKey=getParameter("shipmentLineKey");
			 Integer myInteger=new Integer(Integer.parseInt(sLineNo));
			if (equals(myInteger,ShipmentLineCounter)){%>
			 <yfc:loopXML binding="xml:/ShipmentLine/ShipmentTagSerials/@ShipmentTagSerial" id="ShipmentTagSerial">
			 <tr>
			 <td class="checkboxcolumn">
			</td>	
	<%
	i = 0;
	while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
								}

								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											String sbind="xml:/ShipmentTagSerial/@"+currentAttr;
											if(isExtn){
												sbind="xml:/ShipmentTagSerial/Extn/@"+currentAttr;
											}
%>

										<td class="tablecolumn">
											<%=resolveValue(sbind)%>
										</td>
		<input type="hidden" 	<%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_"+ShipmentLineCounter+"/ShipmentTagSerials/ShipmentTagSerial_" + ShipmentTagSerialCounter + "/@"+currentAttr, sbind)%> />
										
									    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }%>
		<%}
		i++;
	}%>
				<%//prompt for ship by date if time sensitive
				if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@ShipByDate"/>
				</td>
				<%}%>
				<%//prompt for serial no if serialized 
	   
				if(isSerialized){%>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@SerialNo"/>
				</td>
				<%}%>

				<% 
		       if(!equals("Y",(request.getParameter("SerialTracked"))))
		        {
	            %>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@Quantity"/>
				</td>
                <% } %> 

				<td nowrap="true" class="tablecolumn">
				<input type="hidden" 	<%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_"+ShipmentLineCounter+"/ShipmentTagSerials/ShipmentTagSerial_" + ShipmentTagSerialCounter + "/@SerialNo", "xml:/ShipmentTagSerial/@SerialNo")%> />
				<input type="hidden" 	<%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_"+ShipmentLineCounter+"/ShipmentTagSerials/ShipmentTagSerial_" + ShipmentTagSerialCounter + "/@ShipByDate", "xml:/ShipmentTagSerial/@ShipByDate")%> />

				<!-- <input type="text"  class="unprotectedinput" 			    <%=getTextOptions("xml:/Shipment/Containers/Container/ContainerDetails/ContainerDetail_"+ShipmentLineCounter+"/ShipmentTagSerials/ShipmentTagSerial_" + ShipmentTagSerialCounter + "/@Quantity", "")%>/> -->
				</td>		
		</tr>
	    </yfc:loopXML>
		<%}%> 
		</yfc:loopXML>  
 </tbody>
 <tfoot> 
 <tr style='display:none' TemplateRow="true">
    <td class="checkboxcolumn" >
 </td>
	<%
	String binding = request.getParameter("TotalBinding");
	i = 0;
	while (i < 2) { 
		int j = 0;
		Map normalMap = null;
		Map extnMap = null;
		Map currentMap = null;
		if (i == 0) {
			normalMap = identifierAttrMap;
			extnMap = extnIdentifierAttrMap;
	
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
	
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);
											String sExtnBinding = "_/@" ;
											if(isExtn){
												sExtnBinding = "_/Extn/@";
											}
%>
	    <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + sExtnBinding +currentAttr) %>/>
       </td>
											
									    	<%
								        }
								    }
								}
					        	
					        	j++;
					        }}
		i++;
}%>

	
	<%
	   //prompt for ship by date if time sensitive
	   if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
	   <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_/@ShipByDate" ) %>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
       </td>
	<%}%>

	<%
	   //prompt for serial no if serialized 
	   
	   if(isSerialized){%>
	   <td nowrap="true" id='<%="singleserialtext"+sOptionCounter%>' class="tablecolumn" style='display:' colspan="4">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_/@SerialNo" ) %>/>
       </td>
	   <td class="detaillabel" id='<%="serialrangefromtext"+sOptionCounter%>' style='display:none'>
            <yfc:i18n>From_Serial_#</yfc:i18n>
		</td>

		<td nowrap="true" id='<%="serialrangefromlabel"+sOptionCounter%>' class="tablecolumn" style='display:none' >
			<input type="text" class="unprotectedinput"   <%=getTextOptions(binding + "_/SerialRange@FromSerialNo")%>/>		     
		</td>

		<td class="detaillabel" id='<%="serialrangetolabel"+sOptionCounter%>' style='display:none'>
            <yfc:i18n>To_Serial_#</yfc:i18n>
		</td> 

		<td nowrap="true" id='<%="serialrangetotext"+sOptionCounter%>' class="tablecolumn" style='display:none' >
		   <input type="text" class="unprotectedinput"   <%=getTextOptions(binding + "_/SerialRange@ToSerialNo")%>/>
		</td> 
	<%}%>
		<% 
		if(! "Y".equalsIgnoreCase(request.getParameter("SerialTracked")) )
		   {
	    %>
	   <td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_/@Quantity" ) %>/>
       </td>
	   	<% } %>
	<tr>
	 <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
</tfoot>
</table>
</td>
</tr>
</tbody>
</table>	
