<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/confirmshipmentpopup.js"></script>
<table width="100%" class="view">
    <tr>
		<td align="center">
            <yfc:i18n>BackOrder_Unshipped_Quantity</yfc:i18n>&nbsp;&nbsp;&nbsp;
			<input class="checkbox" type="checkbox" name="xml:/Shipment/@BackOrderNotShippedQuantity" value="Y" />
        </td>
    </tr>
    <tr>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setOKClicked();return true;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
    <tr>
</table>