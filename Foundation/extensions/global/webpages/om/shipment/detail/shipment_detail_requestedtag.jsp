<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">
	function changeTagBindings(newBinding) {
		
		var newExtnBinding = newBinding + "Extn/";

	    var tagAttributeTable = document.all("TagAttributeTable");
		var inputs = tagAttributeTable.getElementsByTagName("INPUT");

		for ( var i = 0; i < inputs.length ; i++ ) {
		    var input = inputs.item(i);
		    var inputName = input.name;
		    var j = inputName.lastIndexOf("@");
		    var attributeName = inputName.substring(j,inputName.length);
		    var isExtn = input.isExtn;
		    if (isExtn == "false") {
				input.name = newBinding + attributeName; 		    	
				alert(input.name + " = " + input.value);
		    } else {
		    	input.name = newExtnBinding + attributeName;
		    }
		}
	}	       
</script>
<%
	String tagContainer  = request.getParameter("TagContainer");
	if (isVoid(tagContainer)) {
		tagContainer = "TagContainer";
	}
	
	String tagElement  = request.getParameter("TagElement");
	if (isVoid(tagElement)) {
		tagElement = "Tag";
	}  
	prepareTagDetails ((YFCElement) request.getAttribute(tagContainer),tagElement,(YFCElement) request.getAttribute("ItemDetails"));
	Map identifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"IdentifierAttributes");
	Map descriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"DescriptorAttributes");
	Map extnIdentifierAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnIdentifierAttributes");
	Map extnDescriptorAttrMap = getTagAttributesMap((YFCElement) request.getAttribute("ItemDetails"),"ExtnDescriptorAttributes");
	
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
		totalColumns = 3;
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

<table class="view" width="100%" id="TagAttributeTable">
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
			<tr>
				<td>
					<fieldset>
					<legend><yfc:i18n><%=legendName%></yfc:i18n></legend> 
						<table class="view" width="100%">
						    <tr>
							<%while (j < 2) {
								String currentBindingPrefix = "";
                                String currentTargetBindingPrefix = "";
								boolean isExtn = false;
								if (j == 0) {
									currentMap = normalMap;
									isExtn = false;
									currentBindingPrefix = bindingPrefix;
                                    currentTargetBindingPrefix = targetBindingPrefix;
								} else {
									currentMap = extnMap;
									isExtn = true;
									currentBindingPrefix = extnBindingPrefix;
                                    currentTargetBindingPrefix = targetExtnBindingPrefix;
								}
								if (currentMap != null) {
									if (!currentMap.isEmpty()) {
										for (Iterator k = currentMap.keySet().iterator(); k.hasNext();) {
									        String currentAttr = (String) k.next();
									        String currentAttrValue = (String) currentMap.get(currentAttr);%>
									        
											<td class=<%=labelTDClass%>><yfc:i18n><%=currentAttr%></yfc:i18n></td>
											
									    	<%if (isModifiable) { %>
									    	      
                                                  <%if ((!isVoid(targetBindingPrefix)) && (!isVoid(bindingPrefix))) { 
													%>
                                                  
                                                       <td class=<%=inputTDClass%> nowrap="true"><input type="text" class="unprotectedinput" isExtn=<%=isExtn%> <%=getTextOptions(currentTargetBindingPrefix + "/@" +currentAttr ,currentBindingPrefix + "/@" +currentAttr) %> /></td>                                           
									    		     									    		
									    	      <% } else {
												
													  %>
									    	      
                                                       <td class=<%=inputTDClass%> nowrap="true"><input type="text" class="unprotectedinput" isExtn=<%=isExtn%> <%=getTextOptions(currentBindingPrefix + "/@" +currentAttr) %> /></td>
									    	      
									    	      <% } %>
									    		
									    	<%} else {
											%>
									    	
										    	<td class="protectedtext"><%=currentAttrValue%></td>	
										    	
											<%}
								    		columnsToFill = totalColumns - currentColumn;	
								    		if ((currentColumn % totalColumns) == 0) {
								                   currentColumn = 1; %>
								                   
								                   </tr><tr>
								                   
								      		<%} else { 
								                  	currentColumn++;
								            }
								        }
								    }
								}
					        	if ((j == 1) && (columnsToFill != 0)) {
					        		for (int n = 0; n < columnsToFill; n++) {%>  
					        			<td></td><td></td>
					        		<%}
					        	}
					        	j++;
					        }%>
					        </tr>
					   	</table>
					</fieldset>	
				</td>
			</tr>
		<%}
		i++;
	}%>		        	
</table>