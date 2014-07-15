<%
// Licensed Materials - Property of IBM
// IBM Sterling Business Center
// (C) Copyright IBM Corp. 2009, 2011 All Rights Reserved.
// US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
%>

<%@page import="com.sterlingcommerce.ui.web.framework.utils.SCUIContextHelper"%>
<%@page import="com.sterlingcommerce.ui.web.framework.context.SCUIContext"%>
<%@page import="com.sterlingcommerce.sbc.core.utils.SBCJSONUtils"%> 
<%@page import="com.sterlingcommerce.framework.utils.SCXmlUtils"%>
<%@page import="org.w3c.dom.Element"%>
<%@page import="org.w3c.dom.Document"%>
<%@page import="java.io.*"%>
<%@page import="javax.xml.transform.*"%>
<%@page import="javax.xml.transform.dom.*"%>
<%@page import="javax.xml.transform.stream.*"%>
<%
response.setContentType("text/javascript");
SCUIContext uiContext = SCUIContextHelper.getUIContext(request, response);
Object ret = uiContext.getAttribute("itemSearch_output");
Document doc = ((Element)ret).getOwnerDocument();
String json = SBCJSONUtils.getJSONFromXML(request, response, doc);
%>
{"output":<%=json%>}["output"]
