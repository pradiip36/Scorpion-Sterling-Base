<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/supervisorypanelpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreasonpopup.js"></script>
<table width="100%" class="view">
<tr>
<td height="100%" colspan="4">
<fieldset>
<legend><b><yfc:i18n>For_Entered_Shipment_Info</yfc:i18n></b></legend> 
<table height="100%" width="100%" >
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Seller</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@SellerOrganizationcode"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Buyer</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@BuyerOrganizationcode"/>
    </td>
	<td class="detaillabel" >
        <yfc:i18n>Order_#</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@Orderno"/>
    </td>
</tr>
<tr/>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Pro_#</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@ProNo"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>BOL_#</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@Bolno"/>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Trailer_#</yfc:i18n>
    </td>
    <td class="protectedtext" nowrap="true" >
        <yfc:getXMLValue binding="xml:/Shipment/@Trailerno"/>
    </td>
</tr>
<tr/>
<tr>
		<td/><td/>
		<td/>
		<td>
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick='myObject.temp=true;window.close();'/>
		</td>
		<td/><td/>
</tr>
</table>
</fieldset>
</td>
</tr>
</table>