<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<link rel="stylesheet" href="<%=request.getContextPath()+"/css/"+getTheme()+".css"%>" type="text/css">
<script language="javascript">

    var retVal = window.dialogArguments;	

    function setClickedAttribute() {

		if (document.all["AllowOverage"].checked) {
            retVal.AllowOverage = "Y";
        }
        else {
            retVal.AllowOverage = "N";
        }
        
		window.returnValue = retVal;
    }

    function onClose() {
		window.returnValue = dlgVal;
    }

</script>
<%String sAppCode = resolveValue("xml:/CurrentEntity/@ApplicationCode");%>
<table  width="100%" class="view" cellspacing="0" cellpadding="0" >
<center>
<table>
	<tr>
        <td class="detaillabel"  >
            <yfc:i18n>Allow_Overage</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("AllowOverage", "N", "Y")%>/>
        </td>

    </tr>
   	<tr>
        <td align=center colspan=2>
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setClickedAttribute();window.close();"/>
        <td>
    <tr>
</table>
</center>
</table>
<script language="javascript">

	var dlgVal = window.dialogArguments;		

	if (dlgVal.AllowOverage == "Y")
		document.all["AllowOverage"].checked = true;
	else
		document.all["AllowOverage"].checked = false;
	
</script>



