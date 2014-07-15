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

        if (document.all["OverrideManualShipmentEntry"].checked) {
            retVal.OverrideManualShipmentEntry = "Y";
        }
        else {
            retVal.OverrideManualShipmentEntry = "N";
        }
  		if (document.all["DoNotVerifyPalletContent"].checked) {
            retVal.DoNotVerifyPalletContent = "Y";
        }
        else {
            retVal.DoNotVerifyPalletContent = "N";
        }
        if (document.all["DoNotVerifyCaseContent"].checked) {
            retVal.DoNotVerifyCaseContent = "Y";
        }
        else {
            retVal.DoNotVerifyCaseContent = "N";
        }

        if (document.all["AllowOverage"].checked) {
            retVal.AllowOverage = "Y";
        }
        else {
            retVal.AllowOverage = "N";
        }
        
		if (document.all["AllowNewItemReceipt"].checked) {
            retVal.AllowNewItemReceipt = "SHIPMENT";
        }
        else {
            retVal.AllowNewItemReceipt = "DO_NOT_ALLOW";
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
            <yfc:i18n>Override_Manual_Shipment_Entry</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("OverrideManualShipmentEntry", "N", "Y")%>/>
        </td>
    </tr>
<%if(equals("oms", sAppCode) ){%>
	<tr>

        <td class="detaillabel"  >
            <yfc:i18n>Do_Not_Verify_Pallet_Content</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("DoNotVerifyPalletContent", "N", "Y")%>/>
        </td>
    </tr>
    <tr>

        <td class="detaillabel"  >
            <yfc:i18n>Do_Not_Verify_Case_Content</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("DoNotVerifyCaseContent", "N", "Y")%>/>
        </td>
    </tr>
<%}%>
	<tr>
        <td class="detaillabel"  >
            <yfc:i18n>Suppress_Overage_Check</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("AllowOverage", "N", "Y")%>/>
        </td>

    </tr>
	<tr>
        <td class="detaillabel"  width="180px" >
            <yfc:i18n>Allow_New_Item_Receipt</yfc:i18n>
        </td>
		<td >
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("AllowNewItemReceipt", "DO_NOT_ALLOW", "SHIPMENT")%>/>
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

	if (dlgVal.OverrideManualShipmentEntry == "Y")
		document.all["OverrideManualShipmentEntry"].checked = true;
	else
		document.all["OverrideManualShipmentEntry"].checked = false;
	
	if (dlgVal.DoNotVerifyPalletContent == "Y")
		document.all["DoNotVerifyPalletContent"].checked = true;
	else
		document.all["DoNotVerifyPalletContent"].checked = false;

	if (dlgVal.DoNotVerifyCaseContent == "Y")
		document.all["DoNotVerifyCaseContent"].checked = true;
	else
		document.all["DoNotVerifyCaseContent"].checked = false;

	if (dlgVal.AllowOverage == "Y")
		document.all["AllowOverage"].checked = true;
	else
		document.all["AllowOverage"].checked = false;
	
	if (dlgVal.AllowNewItemReceipt == "SHIPMENT")
		document.all["AllowNewItemReceipt"].checked = true;
	else
		document.all["AllowNewItemReceipt"].checked = false;

</script>



