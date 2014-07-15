<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/yfsjspcommon/editable_util_header.jspf" %>
 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%  

	String sRequestDOM = request.getParameter("getRequestDOM");
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

	String sHiddenDraftOrderFlag = getValue("Order", "xml:/Order/@DraftOrderFlag");
    String driverDate = getValue("Order", "xml:/Order/@DriverDate");
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
	extraParams += "&" + getExtraParamsForTargetBinding("xml:/Order/@OrderHeaderKey", resolveValue("xml:/Order/@OrderHeaderKey"));
	extraParams += "&" + getExtraParamsForTargetBinding("IsStandaloneService", "Y");
	extraParams += "&" + getExtraParamsForTargetBinding("hiddenDraftOrderFlag", sHiddenDraftOrderFlag);
%>

<script language="javascript">
	// this method is used by 'Add Service Request' action on order header detail innerpanel
	function callPSItemLookup()	{
		yfcShowSearchPopupWithParams('','itemlookup',900,550,new Object(), 'psItemLookup', '<%=extraParams%>');
	}
</script>

<table class="view" width="100%">
    <yfc:makeXMLInput name="orderKey">
        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
    </yfc:makeXMLInput>
    <tr>
        <td>
            <input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
            <input type="hidden" name="xml:/Order/@ModificationReasonCode" />
            <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
            <input type="hidden" name="xml:/Order/@Override" value="N"/>
            <input type="hidden" name="hiddenDraftOrderFlag" value='<%=sHiddenDraftOrderFlag%>'/>
            <input type="hidden" name="chkWOEntityKey" value='<%=HTMLEncode.htmlEscape(getParameter("orderKey"))%>'/>
            <input type="hidden" name="chkCopyOrderEntityKey" value='<%=HTMLEncode.htmlEscape(getParameter("orderKey"))%>' />
			<input type="hidden" name="chkOrderHeaderKey" value='<%=HTMLEncode.htmlEscape(getParameter("orderKey"))%>' />
			<input type="hidden" name="xml:/Order/@EnterpriseCode" value='<%=getValue("Order", "xml:/Order/@EnterpriseCode")%>'/>
			<input type="hidden" name="xml:/Order/@DocumentType" value='<%=getValue("Order", "xml:/Order/@DocumentType")%>'/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
			<%if (isModificationAllowed("xml:/Order/@BuyerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
                <td nowrap="true">
					 <input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@BuyerOrganizationCode")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@BuyerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
                    <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','xml:/Order/@EnterpriseCode','xml:/Order/@DocumentType')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@BuyerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
                </td>
            <% } else { %>
				<td class="protectedtext">        
					<yfc:makeXMLInput name="OrganizationKey" >
						<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@BuyerOrganizationCode" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L02",getParameter("OrganizationKey"),"")%> >
						<yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/>
					</a>
				</td>
            <% } %>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
			<%if (isModificationAllowed("xml:/Order/@SellerOrganizationCode","xml:/Order/AllowedModifications") && !isVoid(modifyView)) { %>
                <td nowrap="true">
                    <input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@SellerOrganizationCode")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
                    <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','xml:/Order/@EnterpriseCode','xml:/Order/@DocumentType')" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization", "xml:/Order/@SellerOrganizationCode", "xml:/Order/AllowedModifications")%>/>
                </td>
            <% } else { %>
				<td class="protectedtext">
					<yfc:makeXMLInput name="OrganizationKey" >
						<yfc:makeXMLKey binding="xml:/Organization/@OrganizationKey" value="xml:/Order/@SellerOrganizationCode" />
					</yfc:makeXMLInput>
					<a <%=getDetailHrefOptions("L03",getParameter("OrganizationKey"),"")%> >
						<yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/>
					</a>
				</td>
            <% } %>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td class="protectedtext"><a <%=getDetailHrefOptions("L06", resolveValue("xml:/Order/@DocumentType"), getParameter("orderKey"),"")%> ><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></a></td>

        <td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
        <td class="protectedtext">
            <% if (isVoid(getValue("Order", "xml:/Order/@Status"))) {%>
                [<yfc:i18n>Draft</yfc:i18n>]
            <% } else { %>
                <a <%=getDetailHrefOptions("L01", getParameter("orderKey"), "ShowReleaseNo=Y")%>><%=displayOrderStatus(getValue("Order","xml:/Order/@MultipleStatusesExist"),getValue("Order","xml:/Order/@MaxOrderStatusDesc"),true)%></a>
            <% } %>
            <% if (equals("Y", getValue("Order", "xml:/Order/@HoldFlag"))) { %>

	            <% if (isVoid(modifyView) || isTrue("xml:/Rules/@RuleSetValue")) {%>
					<img onmouseover="this.style.cursor='default'" class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held")%>>
				<%	}	else	{	%>
					<a <%=getDetailHrefOptions("L05", getParameter("orderKey"), "")%>><img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_is_held\nclick_to_add/remove_hold")%>></a>
				<%	}	%>

            <% } %>
            <% if (equals("Y", getValue("Order", "xml:/Order/@SaleVoided"))) { %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.SALE_VOIDED, "This_sale_is_voided")%>/>
            <% } %>
            <% if (equals("Y", getValue("Order","xml:/Order/@isHistory") )){ %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order")%>/>
            <% } %>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderDate"/></td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
        <td class="protectedtext"><%=getComboText("xml:OrderTypeList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/Order/@OrderType",true)%></td>    
        <td class="detaillabel" ><yfc:i18n>Carrier_Service</yfc:i18n></td>
        <td>
            <select  <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}  if (equals(sRequestDOM,"Y")) {%> OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ScacAndServiceKey")%>"  <%}%> <%=yfsGetComboOptions("xml:/Order/@ScacAndServiceKey", "xml:/Order/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
                value="ScacAndServiceKey" selected="xml:/Order/@ScacAndServiceKey" isLocalized="Y"/>
            </select>
        </td>
        <% // Show ReqDeliveryDate if that is the driver date (determined by the "DriverDate" attribute output by getOrderDetails)
           // else show ReqShipDate as the driver date.
           if (equals(driverDate, "02")) { %>
            <td class="detaillabel" ><yfc:i18n>Requested_Delivery_Date</yfc:i18n></td>
            <% if (!isVoid(modifyView)) {%>
                <td nowrap="true">
                    <input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqDeliveryDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqDeliveryDate", "xml:/Order/AllowedModifications")%>/>
                    <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqDeliveryDate", "xml:/Order/AllowedModifications")%>/>
                </td>
            <% } else { %>
                <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqDeliveryDate"/><td>
            <% } %>
        <% } else { %>
            <td class="detaillabel" ><yfc:i18n>Requested_Ship_Date</yfc:i18n></td>
            <% if (!isVoid(modifyView)) {%>
                <td nowrap="true">
                    <input type="text"   <% if (equals(sRequestDOM,"Y")) {%>  OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ReqShipDate")%>"  <%}%> <%=yfsGetTextOptions("xml:/Order/@ReqShipDate", "xml:/Order/AllowedModifications")%>/>
                    <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/Order/@ReqShipDate", "xml:/Order/AllowedModifications")%>/>
                </td>
            <% } else { %>
                <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@ReqShipDate"/><td>
            <% } %>
        <% } %>
    </tr>

    <tr>
	<%  
		if(equals("true", modifyView))
		{
	%>
		<td class="detaillabel" >
			<yfc:i18n>Document_Type</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
		</td>
	<%  }	%>

	<% 
		String exchangeType = "";
		if (equals(sRequestDOM,"Y")) {
			exchangeType = getValue("OrigAPIOutput", "xml:/Order/@ExchangeType");		
		}else{
			exchangeType = getValue("Order", "xml:/Order/@ExchangeType");
		}
		if(!isVoid(exchangeType)){ 
		
	//call master data for exchange type

		// Call API to get the data for the Document Type field.
			String exchangeTypeStr = "<CommonCode CodeType=\"EXCHANGE_TYPE\"/>";
			
			YFCElement exchangeTypeInput = YFCDocument.parse(exchangeTypeStr).getDocumentElement();
			YFCElement exchangeTypeTemplate = YFCDocument.parse("<CommonCodeList TotalNumberOfRecords=\"\"><CommonCode CodeType=\"\" CodeShortDescription=\"\" CodeValue=\"\" CommonCodeKey=\"\"/></CommonCodeList>").getDocumentElement();
     %>

	        <yfc:callAPI apiName="getCommonCodeList" inputElement="<%=exchangeTypeInput%>" templateElement="<%=exchangeTypeTemplate%>" outputNamespace="ExchangeTypeList"/>

		<td class="detaillabel" ><yfc:i18n>Exchange_Type</yfc:i18n></td>
		<td>
			<select  <% if (isVoid(modifyView)) {%> <%=getProtectedComboOptions()%> <%}  if (equals(sRequestDOM,"Y")) {%> OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@ExchangeType")%>"  <%}%> <%=yfsGetComboOptions("xml:/Order/@ExchangeType", "xml:/Order/AllowedModifications")%>>
				<yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
			</select>
		</td>
	<% } %>

	<% if(!isVoid(getValue("Order", "xml:/Order/@ReturnOrderHeaderKeyForExchange"))){ %>
      <yfc:makeXMLInput name="ReturnOHKeyForExchange">
			<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderHeaderKey" />
       </yfc:makeXMLInput>
		<td class="detaillabel" ><yfc:i18n>Created_For_Return_#</yfc:i18n></td>
		<td class="protectedtext">
			<a <%=getDetailHrefOptions("L04",getParameter("ReturnOHKeyForExchange"),"")%> >
				<yfc:getXMLValue binding="xml:/Order/ReturnOrdersForExchange/ReturnOrderForExchange/@OrderNo"/>
			</a> 
		</td>
	<% } %>
	</tr>
</table>
