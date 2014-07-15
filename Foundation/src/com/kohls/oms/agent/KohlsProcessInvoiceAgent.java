package com.kohls.oms.agent;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsInvoiceUtil;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class is called by PROCESS_CREATE_INVOICE agent.
 * It creates invoices at order level, groups invoices by EFCS(PO grouped under 873)
 * @author Priyadarshini
 *
 */
public class KohlsProcessInvoiceAgent extends YCPBaseAgent{

	
	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsProcessInvoiceAgent.class.getName());
	public KohlsProcessInvoiceAgent() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getLocalApi();
	}

	

	@Override
	/**
	 * gets taks Q list in 'awaiting invoice creation' state for creating invoice
	 */
	
	public List<Document> getJobs(YFSEnvironment env, Document InXML,
			Document lastMessageCreated) throws Exception {
		List<Document> lstTask =KohlsInvoiceUtil.getJobsList(env, InXML, lastMessageCreated,  KohlsConstant.TRAN_ID_ORDER_INVOICE_0001_EX, api); 
		return lstTask;
	}


	/**
	 * Creates Invoices at Order level, grouped by EFCs(PO under 873)
	 */
	@Override
	public void executeJob(YFSEnvironment env, Document InXML)
			throws  Exception {
		String strOrderHeaderKey = null;
		String returnStr = null;
		//strOrderHeaderKey = KohlsInvoiceUtil.processInvoice(env, InXML, api);
		returnStr = KohlsInvoiceUtil.processInvoice(env, InXML, api);
		String[] tempTran = returnStr.split("=");
		strOrderHeaderKey = tempTran[0];
		changeOrderStatus(env,strOrderHeaderKey);
		// delete task Q record after creating invoice
		Element elemRoot = InXML.getDocumentElement();
		elemRoot.setAttribute(KohlsXMLLiterals.A_OPERATION,
				KohlsConstant.DELETE);
		KohlsInvoiceUtil.manageTaskQ(env, InXML, api);
	}


	private void changeOrderStatus(YFSEnvironment env, String strOrderHeaderKey) throws YFSException, RemoteException {
		// change Order Status to 'Invoiced'
		api.changeOrderStatus(env, getChangeOrderStatusInpt(strOrderHeaderKey));
		
	}

	private Document getChangeOrderStatusInpt(String strOrderHeaderKey) {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_ORDER_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.INVOICED);

		return yfcDocChangeOrderStatus.getDocument();
	}

}
