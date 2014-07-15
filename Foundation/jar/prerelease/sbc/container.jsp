<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2008, 2012 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>

<%@page import="com.yantra.yfc.ui.backend.util.APIManager"%>
<%
	response.setContentType("text/html;charset="+APIManager.getInstance().getDefaultEncoding());
%>

<%@include file="/sbc/jspf/sbcutil.jspf" %>
<%@ taglib uri="/WEB-INF/scui.tld" prefix="scuitag" %>
<%@ taglib uri="/WEB-INF/scuiimpl.tld" prefix="scuiimpltag" %>
<%@ taglib uri="/struts-tags" prefix="s" %>
<%@page import="com.sterlingcommerce.sbc.helpers.SBCOrgContextHelper"%>
<%@page import="com.sterlingcommerce.sbc.helpers.SBCOrgContextMetaDataHelper"%>
<%@page import="com.sterlingcommerce.sbc.helpers.SBCOnlineHelper"%>
<%@page import="com.sterlingcommerce.sbc.core.utils.SBCSessionUtils"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIUtils"%>
<%@page import="com.sterlingcommerce.sbc.core.utils.SBCJSONUtils"%>
<%@page import="com.sterlingcommerce.security.dv.SCEncoder"%>
<%@page import="com.yantra.yfc.dom.YFCElement"%>
<%@page import="org.w3c.dom.NodeList"%>


<%
	SCUIContext scuiContext = SCUIContextHelper.getUIContext(request, response);
	String orgContextsJson = SBCOrgContextHelper.getContextOrgsJSON(request,response);
	if(orgContextsJson == null)
		orgContextsJson = "";
	String getCurrentOrgJson = SBCOrgContextHelper.getCurrentOrgJson(request,response);
	String organizationCode=SBCOrgContextHelper.getContextOrganizationCode(scuiContext).trim();
	boolean isPricingOrg = SBCOrgContextHelper.isPricingOrgContext(scuiContext);
	boolean isCatalogOrg = SBCOrgContextHelper.isCatalogOrgContext(scuiContext);
	boolean isSubCatalogOrg = SBCOrgContextHelper.isSubCatalogOrgContext(scuiContext);
	boolean isEnterpriseOrg = SBCOrgContextHelper.isEnterpriseOrgContext(scuiContext);
	boolean isEnterpriseContext=SBCOrgContextHelper.isEnterpriseContext(scuiContext);
	boolean isConfigurationOrg = SBCOrgContextHelper.isConfigurationOrgContext(scuiContext);
	boolean isNodeOrg  = SBCOrgContextHelper.isNodeOrgContext(scuiContext);
	boolean isSellerOrg=SBCOrgContextHelper.isSellerOrgContext(scuiContext);
	boolean isTemplateOrg=SBCOrgContextHelper.isTemplateContext(scuiContext);
	String useOrgJson = SBCOrgContextHelper.getUserOrgJson(request,response);
	String orgCtxMetaData = SBCOrgContextMetaDataHelper.getScreenMetaDataJSON(scuiContext);
	String onlineHelpURL = SBCOnlineHelper.getOnlineHelpURL();
	if(onlineHelpURL == null){
		onlineHelpURL = "";
	}
	String sHeader = (String)SBCSessionUtils.getObjFromSession(request.getSession(),"SBCHeader");
	Object activeChangerequestObj = SBCSessionUtils.getObjFromSession(request.getSession(),"ActiveChangeRequest");
	Element activeChangeRequestElem = null;
	if(activeChangerequestObj instanceof YFCElement){
		activeChangeRequestElem = ((YFCElement)activeChangerequestObj).getOwnerDocument().getDocument().getDocumentElement();
	}else{
		activeChangeRequestElem = (Element)activeChangerequestObj;
	}
	
	String activeChangeRequest = "";
	if(!SCUIUtils.isVoid(activeChangeRequestElem)){
		if(!activeChangeRequestElem.getNodeName().equals("ChangeRequest")){
			NodeList nl = activeChangeRequestElem.getElementsByTagName("ChangeRequest");
			if(nl.getLength() != 0){
				activeChangeRequestElem = (Element)nl.item(0);
			}else{
				activeChangeRequestElem = null;
			}
		}
		activeChangeRequest = SBCJSONUtils.getJSONFromXML(request, response, activeChangeRequestElem);
		if(activeChangeRequest == null){
			activeChangeRequest = "";
		}
	}
	
	String fileName = "/includeMenu/includeMenu_" + request.getSession().getId() 
		+ SCEncoder.getEncoder().encodeForURL(organizationCode)+ SCEncoder.getEncoder().encodeForURL(scuiContext.getSecurityContext().getLoginId()) + ".js";
	fileName = SCUIUtils.makeCacheableAndLocalizabel(fileName, false, "always", scuiContext);
	fileName = SCUIUtils.addAdditionalParams(fileName, (HttpServletRequest) pageContext.getRequest(),false);
	fileName = ((HttpServletRequest) pageContext.getRequest()).getContextPath() + fileName;
%>





<head>
		<title>IBM Sterling Business Center</title>
        <scuitag:inclPlatformDependencies />
		<scuiimpltag:inclPlatformImplDependencies />
		<script type="text/javascript" src='<%=fileName%>'>
		</script>
		<script type="text/javascript" src="<%=request.getContextPath()%>/platform/scripts/menuPaint.js"></script>
		
		<scuitag:includeCSS path="/sbc/resources/core/css/sbcCommon" isCommonCSS="true"/>
		<scuitag:includeCSS path="/appcommon/core/css/appcommon" isCommonCSS="true"/>
		
		<link rel="SHORTCUT ICON" href="<%=request.getContextPath()%>/sbc/resources/core/icons/logo_window.ico"/>
		<scuitag:includeAdditionalCss />
		<script>


			Ext.namespace("sc.sbc.LoadUtil");


			var context = sc.plat.info.Application.getApplicationContext();
			var isPricingOrg = '<%=isPricingOrg%>';
			var isCatalogOrg = '<%=isCatalogOrg%>';
			var isSubCatalogOrg = '<%=isSubCatalogOrg%>';
			var isEnterpriseOrg = '<%=isEnterpriseOrg%>';
			var isConfigurationOrg = '<%=isConfigurationOrg%>';
			var isNodeOrg = '<%=isNodeOrg%>';
			var isSellerOrg='<%=isSellerOrg%>'; 
			var isEnterpriseContext='<%=isEnterpriseContext%>';
			var isTemplateOrg='<%=isTemplateOrg%>'; 
			
	    	sc.sbc.LoadUtil.loadContainer = function(){
				Ext.onReady(function() {

				    Ext.QuickTips.init();
				    sc.sbc.core.helper.OrgContextHelper.setCurrentOrg(<%=getCurrentOrgJson%>);
				    sc.sbc.core.helper.OrgContextHelper.setCurrentOrgType(isPricingOrg,isCatalogOrg,isEnterpriseOrg,isSubCatalogOrg,isConfigurationOrg,isNodeOrg,isSellerOrg,isEnterpriseContext,isTemplateOrg);
				    sc.sbc.core.helper.OrgContextHelper.setUserOrg(<%=useOrgJson%>);
				    sc.sbc.core.helper.OrgContextHelper.setOrgCtxMetaData(<%=orgCtxMetaData%>);
				    var container = containerUtil.loadContainer();
    			    sc.sbc.core.helper.OrgContextHelper.paintOrgContext(<%=orgContextsJson%>);
				    sc.sbc.core.OnlineHelpLauncher.setURL('<%=onlineHelpURL%>');
				    if (<%=SCEncoder.getEncoder().encodeForJavaScript(sHeader)%>===false){
				    	var headerpanel = container.find("sciId","seaAppHeaderPanel")[0];
				    	headerpanel.setVisible(false);
				    }    
					if('<%=activeChangeRequest%>' != ''){
				    	var activeCRPanel = sc.sbc.containerUtil.getActiveChangeRequestPanel();
				    	if(activeCRPanel){
						    activeCRPanel.setActiveChangeRequest(<%=activeChangeRequest%>);
						}
					}
				});		
	    	};	
			sc.plat.JSLibManager.loadLibrary("seaCore", sc.sbc.LoadUtil.loadContainer,sc.sbc.LoadUtil);
		</script>
	</head>
	<body>
		<script>
			<s:action name="getcacheableResults" namespace="/sbc" executeResult="true"/>
		</script>
    	<script>
      		<s:action name="%{#request.sbcAction}" namespace="%{#request.sbcNs}" executeResult="true"/>
         </script>
	</body>

