<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Warehouse Management System
(C) Copyright IBM Corp. 2005, 2011 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/yfsjspcommon/editable_util_lines.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/yfcscripts/yfc.js">
</script>
<script>
function processSaveRecordsForChildNode(){
   var addRow = window.document.getElementById("userOperation");
    var numRowsToAdd = window.document.getElementById("numRowsToAdd");
    if(addRow)
    {
        if(addRow.value != 'Y')
        {
            //reset numRowsToAdd attribute
            if(numRowsToAdd)
                numRowsToAdd.value="";
            yfcSpecialChangeNames("ShipmentTagSerials", false);
        }
    }
    else
        yfcSpecialChangeNames("ShipmentTagSerials", false);
}
</script>
<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForChildNode);
</script>
<table class="table" width="100%" initialRows="0" id="ShipmentTagSerials" >
<%
	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}  

	Map identifierAttrMap=null;
	Map descriptorAttrMap=null;
	Map extnIdentifierAttrMap=null;
	Map extnDescriptorAttrMap=null;


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
  <td sortable="no" class="checkboxheader"><input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/></td>
	<input type="hidden" id="userOperation" name="userOperation" value="" />
	<input type="hidden" id="numRowsToAdd" name="numRowsToAdd" value="" />
	<%String legendName = "";
	int i = 0;
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
			legendName = "Tag_Identifiers";
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
			legendName = "Tag_Attributes";
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
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
											<td class="tablecolumnheader" sortable="no"> <yfc:i18n><%=currentAttr%></yfc:i18n></td>
											
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
	<%
	   //prompt for serial no if serialized 
	  //Changes made for RQ-WMS-158 
	  if(equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInShipping")) || equals("Y",getValue("ItemDetails","xml:/Item/PrimaryInformation/@SerialCapturedInReceiving"))){%>
						<td sortable="no" class="tablecolumnheader"> 
							<yfc:i18n>Serial_#</yfc:i18n>
						</td>
	<%}%>
<td class="tablecolumnheader" sortable="no" ><yfc:i18n>Quantity</yfc:i18n></td>
</tr>
</thead>
<tbody>
<%
YFCElement oRep;
YFCElement root = (YFCElement)request.getAttribute(tagContainer);
if(root != null)
	{
		YFCElement oLine = root.getChildElement("ShipmentLine"); 
		YFCElement oTag= oLine.getChildElement("ShipmentTagSerials");
		if(oTag!= null)
			{
				int ShipmentTagSerialsCounter = -1;
				for (Iterator ij = oTag.getChildren(); ij.hasNext();)
				{			
				ShipmentTagSerialsCounter ++;
				oRep = (YFCElement) ij.next();
				if (!isVoid(oRep.getAttribute("ShipmentLineKey"))) { %>
	<tr>
  	<%
	YFCElement oShip = (YFCElement) request.getAttribute("ShipmentLine");	
		if(oShip!=null){
			oShip.setAttribute("ShipByDate","");
			oShip.setAttributes(oRep.getAttributes());
			YFCElement extnElem = oRep.getChildElement("Extn");
			if(!isVoid(extnElem)){
				oShip.setAttributes(extnElem.getAttributes());
			}
		}
	%>
	<yfc:makeXMLInput name="shipmentLineKey">
 	     <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/ShipmentLine/@ShipmentKey"/>
	     <yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/@ShipmentLineKey" value="xml:/ShipmentLine/@ShipmentLineKey"/>
		 <yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/ShipmentTagSerials/ShipmentTagSerial/@ShipmentTagSerialKey" value="xml:/ShipmentLine/@ShipmentTagSerialKey"/>
	</yfc:makeXMLInput>
	<td class="checkboxcolumn">
		<input type="checkbox" value='<%=getParameter("shipmentLineKey")%>' name="chkEntityKey" />
	</td>	
	<%
	String binding = request.getParameter("TotalBinding");
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
			legendName = "Tag_Identifiers";
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
			legendName = "Tag_Attributes";
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
	%>			 
								<%
									if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
										<td class="tablecolumn">
										<% String sBind="xml:/ShipmentLine/@"+currentAttr; %>	
											<yfc:getXMLValue binding="<%=sBind%>"/>
										</td>
									    	<%
								        }
								    }
								}
					        	j++;
					        }}
		i++;
}%>

<%//prompt for ship by date if time sensitive
  if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){ %>
<td class="tablecolumn">
	<input type="text" <%=yfsGetTextOptions("xml:/ShipmentLine/@ShipByDate", "xml:/ShipmentLine/@ShipByDate", "xml:/ShipmentLine/AllowedModifications")%>/>
</td>
<%}%>

<% //prompt for serial no if serialized 
	if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y") || equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReceiving"),"Y")) { %>
    <td class="tablecolumn">
		<yfc:makeXMLInput name="serialKey" >
			<yfc:makeXMLKey binding="xml:/Container/@SerialNo" value="xml:/ShipmentLine/@SerialNo" />
			<yfc:makeXMLKey binding="xml:/Container/@ItemID" value="xml:/ShipmentLine/@ItemID" />
			<yfc:makeXMLKey binding="xml:/Container/@ProductClass" value="xml:/ShipmentLine/@ProductClass" />
			<yfc:makeXMLKey binding="xml:/Container/@UnitOfMeasure" value="xml:/ShipmentLine/@UnitOfMeasure" />
		</yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("serialKey"),"")%> >
            <yfc:getXMLValue  name="ShipmentLine" binding="xml:/ShipmentLine/@SerialNo"/>
		</a>
    </td>
<% } %>
<td class="numerictablecolumn">
<yfc:getXMLValue binding="xml:/ShipmentLine/@Quantity"/>
</td>
</tr>
<%
				} else if (isVoid(oRep.getAttribute("ShipmentTagSerialsKey"))) { //If (!isVoid()) %>
				<tr DeleteRowIndex="<%=ShipmentTagSerialsCounter%>">
					<td class="checkboxcolumn">
					        &nbsp;
						<img class="icon" onclick="deleteRow(this);" <%=getImageOptions(YFSUIBackendConsts.DELETE_ICON, "Remove_Row")%>/>
<%					    String binding = request.getParameter("TotalBinding"); %>
						<input type="hidden"  <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter+"/@DeleteRow",  "")%> />
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
								legendName = "Tag_Identifiers";
							} else {
								normalMap = descriptorAttrMap;
								extnMap = extnDescriptorAttrMap;
								legendName = "Tag_Attributes";
							}		
							if ((normalMap != null) || (extnMap != null)) {%>

											<%while (j < 2) {
													String sExtnBinding = "/@";
													boolean isExtn = false;
													if (j == 0) {
														currentMap = normalMap;
														isExtn = false;
												
													} else {
														currentMap = extnMap;
														isExtn = true;
														sExtnBinding = "/Extn/@";
													}
													if (currentMap != null) {
														if (!currentMap.isEmpty()) {
															for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
																String currentAttr = (String) k.next();
																String currentAttrValue = (String) currentMap.get(currentAttr);%>
							<td nowrap="true" class="tablecolumn">
								<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter + sExtnBinding +currentAttr,oRep.getAttribute(currentAttr)) %>/>
								<%	if(currentAttr=="ManufacturingDate"){%>
			                    <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
		                        <%}%>
						   </td>
																
																<%
															}
														}
													}
													
													j++;
												}}
							i++;
					}
           
		            if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>
						<td nowrap="true" class="tablecolumn">
							<input type="text" class="dateinput" <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter + "/@ShipByDate",oRep.getAttribute("ShipByDate")) %>/>
							<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
						</td>
<%					}
					if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y") || equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturnsReceiving"),"Y")) { %>
						<td nowrap="true" class="tablecolumn">
							<input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter + "/@SerialNo", oRep.getAttribute("SerialNo") ) %>/>
						</td>
					<% } %>
						<td nowrap="true" class="tablecolumn">
							<input type="text" OldValue="" class="unprotectedinput" <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter + "/@Quantity",  oRep.getAttribute("Quantity") ) %>/>
							<input type="hidden"   OldValue=""  <%=getTextOptions(binding + "_" + ShipmentTagSerialsCounter +  "/@Action", "Create", "Create" ) %> />
					    </td>
				</tr>
<%				}//else 
			}
		}
	}%>
</tbody>
<%if(equals("Y",resolveValue("xml:/ShipmentLines/ShipmentLine/@RecordingTagsAllowed"))){%>
 <tfoot>
  <tr style='display:none' TemplateRow="true">
       <td class="checkboxcolumn"></td>
	<%
	String binding = request.getParameter("TotalBinding");
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
			legendName = "Tag_Identifiers";
		} else {
			normalMap = descriptorAttrMap;
			extnMap = extnDescriptorAttrMap;
			legendName = "Tag_Attributes";
		}		
		if ((normalMap != null) || (extnMap != null)) {%>

						<%while (j < 2) {
								String sExtnBinding = "_/@";								
                                boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
							
								} else {
									currentMap = extnMap;
									isExtn = true;
									sExtnBinding = "_/Extn/@";								
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
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

<%//prompt for ship by date if time sensitive
  if(equals("Y",getValue("ItemDetails","xml:/Item/InventoryParameters/@TimeSensitive"))){%>

	    <td nowrap="true" class="tablecolumn">
            <input type="text" class="dateinput" <%=getTextOptions(binding + "_/@ShipByDate" ) %>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        </td>
<%}%>
        
<% if(equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInShipping"),"Y")|| equals(resolveValue("xml:ItemDetails:/Item/PrimaryInformation/@SerialCapturedInReturnsReceiving"),"Y")) { %>
    		<td nowrap="true" class="tablecolumn">
                <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_/@SerialNo" ) %>/>
            </td>
<% } %>
		<td nowrap="true" class="tablecolumn">
            <input type="text" class="unprotectedinput" <%=getTextOptions(binding + "_/@Quantity" ) %>/>
			<input type="hidden"   OldValue=""  <%=getTextOptions(binding + "_/@Action", "Create", "Create" ) %> />
       </td>
 <tr>
 <tr>
    	<td nowrap="true" colspan="15">
    		<jsp:include page="/common/editabletbl.jsp" flush="true">
                <jsp:param name="ReloadOnAddLine" value="N"/>
    		</jsp:include>
    	</td>
    </tr>
</tfoot>
<%}%>
</table> 