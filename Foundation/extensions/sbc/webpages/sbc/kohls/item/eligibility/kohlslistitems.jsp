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
Object ret = uiContext.getAttribute("kohlsitemList_output");
Document doc = ((Element)ret).getOwnerDocument();
String json = SBCJSONUtils.getJSONFromXML(request, response, doc);
System.out.println(json);
%>

function loadItemSearch(){
	var itemSearchScr = new sc.sbc.kohls.item.ineligibility.KohlsViewItems();
	containerUtil.drawScreen(itemSearchScr, {title : itemSearchScr.b_ItemSearch,
											taskXML: "kohlsItemIneligibility"
											});
	itemSearchScr.initialize(<%=json%>);
};

sc.plat.JSLibManager.loadLibrary("seaKohlsItemInelgList", loadItemSearch);

