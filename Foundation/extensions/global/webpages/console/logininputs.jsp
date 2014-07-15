<%@include file="/yfc/util.jspf" %>
<%@page import="com.yantra.yfs.ui.backend.*"%>
<%@page import="com.yantra.yfc.ui.backend.YFCUIBackendInstance"%>
<%@page import=" java.math.BigDecimal "%>
<%
	String errorMsg = (String)getParameter("ErrorMsg");
	String errorMsgDetail = (String)getParameter("ErrorMsgDetail");
	YFCUIBackendInstance.getInstance().setContextPath(request.getContextPath());
%>
<script language="javascript">
    var contextPath="<%=request.getContextPath()%>";
</script>
<script language="javascript" src="../yfcscripts/modalDialog_mb.js" >
</script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/login.js" >
</script>
<style>
.loginmiddletable	{
	background-color:#ffffff;
	width:300px;
	border:0;
	vertical-align:bottom;
	filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#c0c0c0, endColorstr=#ffffff);
}

.loginlabel {
	font:normal normal bold 8pt Tahoma; 
	text-align:right; 
	align: right;
}
.logininput   {
    font: normal normal normal 8pt Tahoma;
	border-width:1px; 
	border-style:solid; 
}
.errormsgstyle	{
    font: normal normal normal 8pt Tahoma;
	color: #DC143C;
}
.loginbutton {
	color: #000000;
	/*filter: progid:DXImageTransform.Microsoft.gradient(startColorstr=#ffffff, endColorstr=#c0c0c0);
	border-bottom: 2px solid black;
	background-color: #ffffff;*/
	font: normal normal bold 8pt Tahoma;
}

</style>
<form name="loginform" id="loginform" method='POST' action="login.jsp" AUTOCOMPLETE="off">
<%
		Object referrer =request.getAttribute("scui-login-page-referrer");
		if (!isVoid(request.getAttribute("scui-login-page-referrer"))) {
			if (referrer instanceof BigDecimal && ((BigDecimal)referrer).intValue() == 0){
				referrer = null;
				request.setAttribute("scui-login-page-referrer",null);
			}
			else{
%>
<input type="hidden" name="scui-login-page-referrer" value="<%=request.getAttribute("scui-login-page-referrer") %>"/>
<%
		}
	}
%>
<table class="loginmiddletable" cellspacing=8 cellpadding=2>
	<tr>
		<td style="padding-top:20px" class="loginlabel"><yfc:i18n>Login_ID</yfc:i18n>&nbsp;
			<INPUT type="text" class="logininput" value='' name="UserId" >
		</td>
	</tr>
	<tr>
		<td class="loginlabel"><yfc:i18n>Password</yfc:i18n>&nbsp;
			<INPUT type=password class="logininput" value='' name="Password" >
		</td>
	</tr>
	<tr>
		<td align="right" class="errormsgstyle"><yfc:i18n><%=HTMLEncode.htmlEscape(errorMsg)%></yfc:i18n>&nbsp;
			<input class=loginbutton type=submit name="btnLogin" value='<yfc:i18n>Sign_In</yfc:i18n>' title="<yfc:i18n>Click_to_sign_in_to_Yantra</yfc:i18n>" onclick="window.status='<yfc:i18n>Signing_in._Please_wait...</yfc:i18n>';doLogin();">
		</td>
	</tr>
	<tr>
		<td align="left" class="errormsgstyle"><yfc:i18n><%=HTMLEncode.htmlEscape(errorMsgDetail)%></yfc:i18n>&nbsp;
		</td>
	</tr>
<%
		Properties prop = new Properties();
		InputStream in = getClass().getClassLoader()
				.getResourceAsStream(
						"customer_overrides.properties");
		prop.load(in);
		request.setAttribute("websealLink",(String)prop.get("sso.webseal.link"));
			 
%>	
	<tr>
		<!--<td class="loginlabel"><yfc:i18n></yfc:i18n>&nbsp;
			 <a href="<%=request.getAttribute("websealLink") %>">WebSeal Login Link</a> 
		</td>-->
	</tr>
</table>
</form>
<script>
	if(self.name && parent.yfcFramesArray && parent.yfcFramesArray[self.name] != null && parent.yfcFramesArray[self.name] != "") {
		window.dialogArguments.returnedValue = "Refresh";
		window.close();
	}
	else {
		window.status = '<%=getI18N("Please_sign_in_to_Yantra")%>';
		window.focus();
		loginform.UserId.focus();
		
	}
	document.body.scroll = "yes";
</script>