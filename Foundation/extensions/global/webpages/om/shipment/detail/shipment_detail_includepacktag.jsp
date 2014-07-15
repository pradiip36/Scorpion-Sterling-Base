<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
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
	
	
	String bindingPrefix  = request.getParameter("BindingPrefix");
    String targetBindingPrefix = request.getParameter("TargetBindingPrefix");
	String extnBindingPrefix = bindingPrefix + "/Extn";
    String targetExtnBindingPrefix = targetBindingPrefix + "/Extn";
	String noOfColumns  = request.getParameter("NoOfColumns");
	String modifiable = request.getParameter("Modifiable");
	String labelTDClass = request.getParameter("LabelTDClass");
	String inputTDClass = request.getParameter("InputTDClass");    
	boolean isModifiable = false;
	int totalColumns = 0;
	int currentColumn = 1;
	int columnsToFill = 0;
	if (!isVoid(noOfColumns)) {
		totalColumns = (new Integer(noOfColumns)).intValue();		
	} else {
		totalColumns = 20;
	}
	
	if (equals(modifiable,"true")) {
		isModifiable = true;
	}
	
	if (isVoid(labelTDClass)) {
		labelTDClass = "detaillabel";
	}
	
	if (isVoid(inputTDClass)) {
		inputTDClass = "";
	}
%>
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
			tagtrack=1;
		%>

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

	<%//prompt for ship by date if time sensitive
	   if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
						<td sortable="no" class="tablecolumnheader"> 
							<yfc:i18n>Ship_By_Date</yfc:i18n>
						</td>
		<%}%>
	<%//prompt for serial no if serialized 
	   
	   if(equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInShipping"))||equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReturns"))){%>
						<td sortable="no" class="tablecolumnheader"> 
							<yfc:i18n>Serial_#</yfc:i18n>
						</td>
	<%}%>
						<td class="tablecolumnheader"> 
							<yfc:i18n>Quantity</yfc:i18n>
						</td>
</tr>
</thead>
<tbody>
	      <yfc:loopXML binding="xml:/Container/ContainerDetails/@ContainerDetail" id="ContainerDetail">
			 <%
			 String sLineNo=getParameter("optionSetBelongingToLine");
			 String sLineKey=getParameter("shipmentLineKey");
			 Integer myInteger=new Integer(Integer.parseInt(sLineNo));
			if (equals(myInteger,ContainerDetailCounter)){%>
			 <yfc:loopXML binding="xml:/ContainerDetail/ShipmentTagSerials/@ShipmentTagSerial" id="ShipmentTagSerial">
			 <tr>
			 <td class="checkboxcolumn">
			</td>	
	<%
	i = 0;
	while (i < 2) { 
		int j = 0;
		currentColumn = 1;
		columnsToFill = 0;
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
	   		if(equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInShipping")) ||equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReturns"))){%>

				<td class="tablecolumn">
				<yfc:makeXMLInput name="serialKey" >
					<yfc:makeXMLKey binding="xml:/Container/@SerialNo" value="xml:/ShipmentTagSerial/@SerialNo" />
					<yfc:makeXMLKey binding="xml:/Container/@ItemID" value="xml:/ContainerDetail/@ItemID"/>
					<yfc:makeXMLKey binding="xml:/Container/@ProductClass" value="xml:/ContainerDetail/@ProductClass"/>
					<yfc:makeXMLKey binding="xml:/Container/@UnitOfMeasure" value="xml:/ContainerDetail/@UnitOfMeasure"/>
				</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L04",getParameter("serialKey"),"")%> >
							<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@SerialNo"/>
					</a>
				</td>
			<%}%>
			<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/ShipmentTagSerial/@Quantity"/>
			</td>
		</tr>
	    </yfc:loopXML>
		<%}%> 
		</yfc:loopXML>  
 </tbody>
</table>
</tr>
</tbody>
</table>	