<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2009, 2012 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
// Customized JSP
%>


<%@page import="com.sterlingcommerce.framework.utils.SCXmlUtils"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.sbc.core.utils.SBCJSONUtils"%> 
<%@page import="org.w3c.dom.Element"%>
<%@page import="com.sterlingcommerce.security.dv.SCEncoder"%>

<%
SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);

Object apiOut = uiContext.getAttribute("getCategoryDomainDetails_output");
Element outElem = (Element)apiOut;
String outString = SBCJSONUtils.getJSONFromXML(request, response, outElem);

String backRequired = request.getParameter("back");
if(backRequired == null){
	backRequired = "";
}
String deleteScreenCache = request.getParameter("deleteScreenCache");
if(deleteScreenCache == null){
	deleteScreenCache = "";
}
%>

function loadCatalogDetail(){
	 
	var backLink = '<%=SCEncoder.getEncoder().encodeForJavaScript(backRequired)%>';
	if(backLink === "Y"){
		backLink = true;
    }else{
		backLink = false;
	}
	var cacheRequired = '<%=SCEncoder.getEncoder().encodeForJavaScript(deleteScreenCache)%>';
	if(cacheRequired === "Y"){
		cacheRequired = false;
    }else{
		cacheRequired = true;
	}
	var titleConfig = {
						title: sc.sbc.containerUtil.getBundleValue("b_ItemSearch"), 
						back : backLink,
						buttons : {btype:sc.sbc.helper.ScreenHelper.SAVE}, 
						showRelatedTask : true,
						cacheRequired : cacheRequired
						};
	var kohlsbaseCategoryDetail = new sc.sbc.kohls.item.safetyfactor.KohlsCatalogDetailsScreen();
	kohlsbaseCategoryDetail.onLoad(<%=outString%>);	
	containerUtil.drawScreen(kohlsbaseCategoryDetail, titleConfig);	
}

sc.plat.JSLibManager.loadLibrary("seaKohlsManageSafetyFactor", loadCatalogDetail);
