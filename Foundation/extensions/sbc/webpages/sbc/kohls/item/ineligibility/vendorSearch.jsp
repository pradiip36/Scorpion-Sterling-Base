<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2010, 2012 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>
 
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIUserPreferences"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="com.sterlingcommerce.security.dv.SCEncoder"%>

<%
SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);
String stringSearchInputData = "";
String searchContext="";
searchContext=uiContext.getRequest().getParameter("searchContext");
if(searchContext == null){
searchContext = "";
}
stringSearchInputData = (String)uiContext.getRequest().getParameter("inputdata");
if(stringSearchInputData == null){
	stringSearchInputData = "";
}
%>

function handleApprovalGetSavedSearchDetails(res, options){
	var result = Ext.decode(res.responseText).output;
	var vendorSearch = new sc.sbc.kohls.item.ineligibility.VendorSearch();
	containerUtil.drawScreen(vendorSearch, {title:"Find Vendor"});
};

function seaLoadVendorSearch() {
	var searchKey='<%=SCEncoder.getEncoder().encodeForJavaScript(stringSearchInputData)%>';
	var searchContext='<%=SCEncoder.getEncoder().encodeForJavaScript(searchContext)%>';
	if(searchKey && searchContext)
	{
		sc.sbc.common.savedsearch.savedsearchutils.callGetSavedSearchDetails(searchKey,{callback : handleApprovalGetSavedSearchDetails},searchContext);
	} else {
		var vendorSearch = new sc.sbc.kohls.item.ineligibility.VendorSearch();
		containerUtil.drawScreen(vendorSearch, {title:"Find Vendor"});
	}
};

sc.plat.JSLibManager.loadLibrary("seaSearch");
sc.plat.JSLibManager.loadLibrary("seaVendorSearchForInelg", seaLoadVendorSearch);


