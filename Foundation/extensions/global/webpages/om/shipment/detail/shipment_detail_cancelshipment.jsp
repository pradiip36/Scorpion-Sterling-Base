<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript">
    var myObject = new Object();
    myObject = dialogArguments;
    var parentWindow = myObject.currentWindow;

    function setClickedAttribute() {

        var retVal = new Object();
		if (document.all("xml:/Shipment/@BackOrderRemovedQuantity")) {
		if (document.all["xml:/Shipment/@BackOrderRemovedQuantity"].checked) {
					retVal["xml:/Shipment/@BackOrderRemovedQuantity"] = "Y";
		}
		else {
			retVal["xml:/Shipment/@BackOrderRemovedQuantity"] = "N";
		}
		}else{
			retVal["xml:/Shipment/@BackOrderRemovedQuantity"] = "N"
		}
		retVal["xml:/Shipment/ShipmentStatusAudit/@ReasonCode"]=document.all["xml:/ModificationReason/@ReasonCode"].value;	
		retVal["xml:/Shipment/ShipmentStatusAudit/@ReasonText"]=document.all["xml:/ModificationReason/@ReasonText"].value;	

        window.dialogArguments["OMReturnValue"] = retVal;
        window.dialogArguments["OKClicked"] = "YES";
        window.close();
    }
</script>

<table width="100%" class="view">
   <!--<%if(equals("Y",resolveValue("xml:/Shipment/@OrderAvailableOnSystem"))||(isVoid(resolveValue("xml:/Shipment/@OrderAvailableOnSystem"))) ) {%>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Backorder_cancelled_quantity</yfc:i18n> 
        </td>
        <td>
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/Shipment/@BackOrderRemovedQuantity", "N", "Y")%>/>
        </td>
    </tr>
	<%}%> -->
	<tr>
        <td align="right">
            <yfc:i18n>Cancellation_Reason_Code</yfc:i18n>
        </td>
        <td>
            <select name="xml:/ModificationReason/@ReasonCode" class="combobox">
                <yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" 
                    name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
	</tr>	
	<tr>
        <td align="right">
            <yfc:i18n>Reason_Text</yfc:i18n>
        </td>
        <td>
            <textarea class="unprotectedtextareainput" rows="3" cols="50" <%=getTextAreaOptions("xml:/ModificationReason/@ReasonText")%>></textarea>
        </td>
    </tr>
    <tr>
		<td/>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setClickedAttribute();return true;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
    <tr>
</table>