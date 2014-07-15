<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>

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
System.out.println("hello");

Object ret=uiContext.getAttribute("itemSearch_output");
System.out.println("hello1");
Document doc=((Element)ret).getOwnerDocument();
String json=SCUIJSONUtils.getJSONFromXML(doc);
System.out.println("json is " + json);

%>
