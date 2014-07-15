/*
 * YFSGetReceiptNoUE.java
 *
 * Created on July 9, 2003, 11:53 AM
 */

package com.kohls.oms.ue;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsCommonUtil;
import com.yantra.shared.omp.OMPTransactionCache;
import com.yantra.shared.ycp.YFSContext;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetDistanceForNodeListUE;

/**
 *  Default implementation example of YFSGetReceiptNoUE.  
 */
public class KohlsGetDistanceForNodeListUE implements YFSGetDistanceForNodeListUE {
	public static YFCLogCategory kohlsLogger = YFCLogCategory.instance(KohlsGetDistanceUE.class.getName());
	
	public Document getDistanceForNodeList(YFSEnvironment env, Document inXML)	throws YFSUserExitException {
		
		YFCDocument ydoc = YFCDocument.getDocumentFor(inXML);
		YFCElement elem = ydoc.getDocumentElement();
		
		System.out.println("elem::"+elem);
		kohlsLogger.debug("KohlsGetDistanceForNodeListUE logs ----------- UE elem" + elem);
		
		String orderHeaderKey = KohlsCommonUtil.readOHKFromCache(env);
		
		System.out.println("orderHeaderKey::"+orderHeaderKey);
		kohlsLogger.debug("KohlsGetDistanceForNodeListUE logs ----------- UE orderHeaderKey" + orderHeaderKey);
		return inXML;
	} 
	
}