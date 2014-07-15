<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/inventory.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">



function setTagAttributes(obj){
	
	var myObject= window.dialogArguments;
	var parentTable = getParentTag(obj, "TABLE");
	var trInputs  = parentTable.getElementsByTagName("input");
	var tagAttrs= new Array(trInputs.length-1);

	for (var i = 0 ; i < trInputs.length ; i++)	 {
    		var trInput = trInputs.item(i);
			if(trInput.type=="text"){
			tagAttrs[i] = trInput;
			}
		}

   myObject.tagAttrs= tagAttrs;
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
	
	String bindingPrefix  = request.getParameter("BindingPrefix");
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

	


<table class="view" width="100%" id="TagTable" >

	<tr>
    	<%
		    for (Iterator i = identifierAttrMap.keySet().iterator(); i.hasNext();) {
		        String identifierAttr = (String) i.next();
		        String identifierAttrValue = (String) identifierAttrMap.get(identifierAttr);
    	%>
	    		<td class=<%=labelTDClass%>><yfc:i18n><%=identifierAttr%></yfc:i18n></td>
	    <%		
	    		if (isModifiable) { 
	    %>
	    		<td class=<%=inputTDClass%> nowrap="true"><input type="text" class="unprotectedinput" <%=getTextOptions(bindingPrefix + identifierAttr) %> /></td>
	    <%
	    		} else {
	    %>
		    	<td class="protectedtext"><%=identifierAttrValue%></td>	
		<%
				}
		   	
    		columnsToFill = totalColumns - currentColumn;	
    		if ((currentColumn % totalColumns) == 0) {
                   currentColumn = 1; 
      	%>
                   </tr><tr>
      	<%             
                } else {
                  	currentColumn++;
               	}
        	}
        	if (columnsToFill != 0) {
        		for (int n = 0; n < columnsToFill; n++) {
        %>        
        			<td></td><td></td>
        <%	
        		}
        	}
    	%>
    </tr>

    <tr>
    	<%
    		currentColumn = 1;
    		columnsToFill = 0;
		    for (Iterator i = descriptorAttrMap.keySet().iterator(); i.hasNext();) {
		        String descriptorAttr = (String) i.next();
		        String descriptorAttrValue = (String) descriptorAttrMap.get(descriptorAttr);
    	%>
		    	<td class=<%=labelTDClass%>><yfc:i18n><%=descriptorAttr%></yfc:i18n></td>
		<%
				if (isModifiable) { 
		%>	
		    	<td class=<%=inputTDClass%> nowrap="true"><input type="text" class="unprotectedinput" <%=getTextOptions(bindingPrefix + descriptorAttr) %> /></td>
	    <%
	    		} else {
	    %>
		    	<td class="protectedtext"><%=descriptorAttrValue%></td>	
    	<%
    			}
    		
    		columnsToFill = totalColumns - currentColumn;
    		if ((currentColumn % totalColumns) == 0) {
                   currentColumn = 1; 
      	%>
                   </tr><tr>
      	<%             
                } else {
                  	currentColumn++;
               	}
        	}
        	if (columnsToFill != 0) {
        		for (int n = 0; n < columnsToFill; n++) {
        %>        
        			<td></td><td></td>
        <%	
        		}
        	}
    	%>
    </tr>

  <tr>
        <td align=center colspan=6>
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setTagAttributes(this);window.close();"/>
        <td>
    <tr>
</table>