<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>

<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIJSONUtils"%>
<%@page import="com.sterlingcommerce.framework.utils.SCXmlUtils"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>

<%
response.setContentType("text/javascript");
SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);
Object ret = uiContext.getAttribute("getItemTypeListOutput");
Document doc = ((Element)ret).getOwnerDocument();
String json = SCUIJSONUtils.getJSONFromXML(doc);
%>
{"output":<%=json%>}
