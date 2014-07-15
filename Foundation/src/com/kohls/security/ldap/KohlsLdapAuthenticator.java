package com.kohls.security.ldap;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.yantra.shared.ycp.YCPErrorCodes;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.util.YFSAuthenticator;

public class KohlsLdapAuthenticator implements YFSAuthenticator {

	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsLdapAuthenticator.class.getName());

	public Map authenticate(String sLoginID, String sPassword) throws Exception {


		log.debug("Inside method : authenticate()");
		log.debug("****************** Entered KohlsLdapAuthenticator - authenticate method ********************");
		log.debug("***** Login *********      " + sLoginID);

		
		String ldapFactory = YFSSystem.getProperty("yfs.security.ldap.factory");
		log.debug("*********ldap factory property obtained" + ldapFactory);
		String adminUserDN = YFSSystem.getProperty("yfs.security.ldap.defaultuserdn");
		String adminUserCredentials = YFSSystem.getProperty("yfs.security.ldap.defaultuserpwd");
		String securityProtocol = YFSSystem.getProperty("yfs.security.ldap.protocol");
		
		log.debug("*******  ldapFactory **************" + ldapFactory);
		log.debug("*******  adminUserDN **************" + adminUserDN);
		log.debug("*******  security protocol **************" + securityProtocol);
		
		YFSException yfe = new YFSException();
		String ldapURL = YFSSystem.getProperty("yfs.security.ldap.url");
		
		log.debug("*******  ldapURL **************    " + ldapURL);
		
		// if any of the ldap params are not set, throw exception
		if (YFCObject.isVoid(ldapURL) || YFCObject.isVoid(ldapFactory)) {
			YFCException ex = new YFCException(
					YCPErrorCodes.YCP_INVALID_LDAP_AUTHENTICATOR_CONFIGURATION);
			//ex.setAttribute("yfs.yfs.security.ldap.factory", ldapFactory);
			//ex.setAttribute("yfs.yfs.security.ldap.url", ldapURL);
			log.debug("*******  if any of the ldap params are not set, throw exception **************    ");
			throw ex;
		} else {
			log.debug("********ldap params are set hence no exception so continue **********************");
			log.debug("*******ldapFactory***********" +ldapFactory);
			log.debug("********ldapURL*************" +ldapURL);
			log.debug("********adminUserDN**************" +adminUserDN);
			log.debug("********adminUserCredentials********" +adminUserCredentials);
			Hashtable env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY, ldapFactory);
			env.put(Context.SECURITY_AUTHENTICATION, YFSSystem.getProperty("yfs.security.ldap.auth"));
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_PRINCIPAL, adminUserDN);
			env.put(Context.SECURITY_CREDENTIALS, adminUserCredentials);
			if(!YFCObject.isNull(securityProtocol)){
				env.put(Context.SECURITY_PROTOCOL, securityProtocol);
			}
			

			log.debug("LDAP URL: " + ldapURL);
			
			DirContext ctx = null;
			DirContext userCtx = null;
			log.debug("**********************set the context now ******************");

			try {
				// Create the initial context for admin binding
				log.debug("*******  Creating initial context  **************    ");
				ctx = new InitialDirContext(env);
				//log.info(">>>>>>>>>>"
				//		+ (ctx.getEnvironment().get(Context.PROVIDER_URL))
				//				.toString());

				SearchControls ctls = new SearchControls();
				ctls.setSearchScope(ctls.SUBTREE_SCOPE);

				log.debug("Admin App Authentication Success");

				NamingEnumeration results = ctx.search("", "uid=" + sLoginID,
						ctls);
				log.debug("********************** post results ********************");

				if (!(results.hasMoreElements())) {
					log.debug("*******  no results  obtained **************    ");
					log.debug("Invalid Credential");
					yfe.setErrorCode("LDAP_002");
					yfe.setErrorDescription("Unable to find user: " + sLoginID
							+ " in LDAP directory");
					throw yfe;

				} else {
					log.debug("*******  results obtained, hence proceed **************    ");
					// Get fully qualified "dn" for the logged in user
					log.debug("UserID: " + sLoginID);
					SearchResult sr = (SearchResult) results.next();
					String userDN = sr.getName();
					log.debug("User DN is:" + userDN);
					// Do User Bind using users "dn"
					Hashtable userBindEnv = new Hashtable();
					userBindEnv.put(Context.INITIAL_CONTEXT_FACTORY,
							ldapFactory);
					userBindEnv.put(Context.SECURITY_AUTHENTICATION, YFSSystem.getProperty("yfs.security.ldap.auth"));
					userBindEnv.put(Context.PROVIDER_URL, ldapURL);
					userBindEnv.put(Context.SECURITY_PRINCIPAL, userDN);
					userBindEnv.put(Context.SECURITY_CREDENTIALS, sPassword);
					if(!YFCObject.isNull(securityProtocol)){
						userBindEnv.put(Context.SECURITY_PROTOCOL, securityProtocol);
					}
					
					
					// Create the initial context for User bind.
					userCtx = new InitialDirContext(userBindEnv);
					log.debug("User Bind Successful");
				}
			} catch (Exception e) {
				log.debug("in Catch block  - Ldap error: " + e.toString());
				e.printStackTrace();
				throw new YFSException("LDAP Authentication Failed "
						+ e.getMessage());
			} finally {
				if (ctx != null) {
					log.debug("ctx not null");
					ctx.close();
				}
				if (userCtx != null) {
					log.debug("userctx not null");
					userCtx.close();
				}
			}
		}
		log.debug("*******  Authenticated **************    ");
		return null;
	
	}
}
