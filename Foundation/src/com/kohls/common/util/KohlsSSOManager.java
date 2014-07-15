package com.kohls.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.yantra.ycp.japi.util.YCPSSOManager;
import com.yantra.yfc.log.YFCLogCategory;

public class KohlsSSOManager implements YCPSSOManager {


private static Properties prop = null;
private static InputStream in = null;
private static String strHeaderName = null;
private static boolean initialized = false;

private static final YFCLogCategory log = YFCLogCategory.instance(KohlsConstant.SSO_LOGGER
+ KohlsSSOManager.class);

public void initialize() {
System.out.println("**************INSIDE KOHLSSSOMANAGER INITIALIZE");
System.out.println("**************INSIDE KOHLSSSOMANAGER INITIALIZE - USING PROPERTIES FILE: " + KohlsConstant.CUSTOMER_OVERRIDES_PROPERTIES);

prop = new Properties();
in = KohlsSSOManager.class.getClassLoader().getResourceAsStream(KohlsConstant.CUSTOMER_OVERRIDES_PROPERTIES);

System.out.println("************** SEARCHING: " + KohlsSSOManager.class.getClassLoader().getSystemResource(KohlsConstant.CUSTOMER_OVERRIDES_PROPERTIES));
System.out.println("************** SEARCHING WITH /: " + KohlsSSOManager.class.getClassLoader().getSystemResource("/" + KohlsConstant.CUSTOMER_OVERRIDES_PROPERTIES));
System.out.println("**************LOADED PROPERTY FILE" + in);
try {
prop.load(in);
System.out.println("**************LOADED PROPERTY FILE" + prop);
strHeaderName = (String) prop.get(KohlsConstant.SSO_HEADER_USERNAME);
System.out.println("**************GOT HEADER NAME" + strHeaderName);
initialized = true;
} catch (IOException e) {
System.out.println("**************EXCEPTION INITIALIZING!!!!" + e);
log.debug("Loading customer_overrides.properties failed" + e.getStackTrace());	
}	
}	
 
@Override
public String getUserData(HttpServletRequest req, HttpServletResponse res)
throws Exception {
if (!initialized) initialize();
System.out.println("**************INSIDE GETUSERDATA");

String strUserName = (String) req.getHeader(strHeaderName);
System.out.println("**************INSIDE GETUSERDATA: got from header:" + strUserName);

if (strUserName != null && strUserName != "")
strUserName = strUserName.trim();

System.out.println("Single Sign On input user name "+  strHeaderName + " from WebSeal: "
+ strUserName);	
System.out.println("Single Sign On input user name iv_user from WebSeal: "
+ ((String) req.getHeader("iv_user")));	
log.debug("Single Sign On input user name "+  strHeaderName + " from WebSeal: "
+ strUserName);	

return strUserName;


}

}