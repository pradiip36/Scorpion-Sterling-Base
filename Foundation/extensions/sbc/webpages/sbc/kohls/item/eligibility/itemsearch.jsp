<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>


<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIUtils"%><%@page import="com.sterlingcommerce.sbc.core.utils.SBCJSONUtils"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIUserPreferences"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>



<%
SCUIUserPreferences userPref = (SCUIUserPreferences)request.getSession().getAttribute("SCUI_USER_PREFERENCE");
String locale = userPref.getLocale().getLocaleCode();
SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);
Object obj = uiContext.getAttribute("itemStatus_output");
String itemStatusList =  SBCJSONUtils.getJSONFromXML(request, response,(Element)obj);

obj = uiContext.getAttribute("getItemTypeListOutput");
String itemTypeList =  SBCJSONUtils.getJSONFromXML(request, response,(Element)obj);

//For Saved Search
String stringSearchInputData = "";
String searchContext="";
searchContext=uiContext.getRequest().getParameter("searchContext");
stringSearchInputData = (String)uiContext.getRequest().getParameter("inputdata");
if(stringSearchInputData == null){
	stringSearchInputData = "";
}
//For Saved Search - End

//For Quick Item Search
String itemSearchInput = "";
itemSearchInput = (String)uiContext.getRequest().getParameter("itemSearch_input");
if(SCUIUtils.isVoid(itemSearchInput)){
	itemSearchInput = "''";
}
//For Quick Item Search - End
%>
var locale = '<%=locale%>';

function handleGetSavedSearchDetails(res, options){
	var result = Ext.decode(res.responseText).output;
	var itemSearch = initSearch();
	itemSearch.handleGetSavedSearchDetails(result);
};

function loadItemSearch(){
	var searchKey='<%=stringSearchInputData%>';
		var searchContext='<%=searchContext%>';
	if(searchKey && searchContext)
	{
		sc.sbc.common.savedsearch.savedsearchutils.callGetSavedSearchDetails(searchKey,{callback : handleGetSavedSearchDetails},searchContext);
	}else {
		var itemSearch = initSearch();
	}
};

function initSearch(){
	itemManager.setItemStatusList(<%=itemStatusList %>);
	itemManager.setItemTypeList(<%=itemTypeList %>);
	var itemSearch = null; 
	var isSearchInputPresent = false;
	if(<%=itemSearchInput%>){
		itemSearch = new sc.sbc.kohls.item.eligibility.ItemSearch({SearchInput:<%=itemSearchInput%>});
		isSearchInputPresent = true;
	}else{
		itemSearch = new sc.sbc.kohls.item.eligibility.ItemSearch();
	}
	var titleConfig = {
						title: "Set Eligible Items for Store", 						
						showRelatedTask : false						
						};
	containerUtil.drawScreen(itemSearch, titleConfig);
	itemSearch.initialize();
	if(isSearchInputPresent){
		itemSearch.handleSearch();
	}
	return itemSearch;
}

sc.plat.JSLibManager.loadLibrary("seaItemSearch4", loadItemSearch);

