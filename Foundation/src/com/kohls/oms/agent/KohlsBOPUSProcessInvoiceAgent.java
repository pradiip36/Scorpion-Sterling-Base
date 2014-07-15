package com.kohls.oms.agent;

import java.rmi.RemoteException;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.bopus.util.KOHLSCommonCodeList;
import com.kohls.bopus.util.KohlsStatusUpdateUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsInvoiceUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class is called by BOPUS_ORDER_INVOICE agent.
 * It creates invoices for BOPUS order lines
 * @author Sudhakar P
 *
 */
public class KohlsBOPUSProcessInvoiceAgent extends YCPBaseAgent{
	
	

	
	private YIFApi api;
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsBOPUSProcessInvoiceAgent.class.getName());
	public KohlsBOPUSProcessInvoiceAgent() throws YIFClientCreationException {
		api = YIFClientFactory.getInstance().getLocalApi();
	}

	

	@Override
	/**
	 * gets taks Q list in 'Placed in Hold Location' state for creating invoice
	 */
	public List<Document> getJobs(YFSEnvironment env, Document InXML,
			Document lastMessageCreated) throws Exception {
		
		List<Document> lstTask = KohlsInvoiceUtil.getJobsList(env, InXML, lastMessageCreated, KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX, api); 
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
		log.debug("the input doc is:"+SCXmlUtil.getString(InXML));
		//strOrderHeaderKey = KohlsInvoiceUtil.processInvoice(env, InXML, api);
		returnStr = KohlsInvoiceUtil.processInvoice(env, InXML, api);
		String[] tempTran = returnStr.split("=");
		strOrderHeaderKey = tempTran[0];
		//changeOrderStatus(env,strOrderHeaderKey);
		//added by Baijayanta
		if("true".equalsIgnoreCase(tempTran[1])) {
			KohlsStatusUpdateUtil updtUtil = new KohlsStatusUpdateUtil();
			updtUtil.sentOrderStatusToEcomm(env,strOrderHeaderKey);
		// delete task Q record after creating invoice
		
			Element elemRoot = InXML.getDocumentElement();
			elemRoot.setAttribute(KohlsXMLLiterals.A_OPERATION,
				KohlsConstant.DELETE);
			KohlsInvoiceUtil.manageTaskQ(env, InXML, api);
		}
	}

	
	private void changeOrderStatus(YFSEnvironment env, String strOrderHeaderKey) throws YFSException, RemoteException {
		try {
			api.changeOrderStatus(env, getChangeOrderStatusInpt(strOrderHeaderKey,env));
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
	}



	private Document getChangeOrderStatusInpt(String strOrderHeaderKey,YFSEnvironment env) throws Exception {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_BOPUS_ORDER_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		//On Invoice creation move the order status from "Placed in Hold Location" to "Shipment Ready For Customer Pick Up"
		//yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.SHIPMENT_READY_FOR_CUSTOMER_PICK_UP);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KOHLSCommonCodeList.statusValue(env,KohlsConstant.CC_DESC_READY_FOR_CUSTOMER, KohlsConstant.CODE_TYPE_ORD));
		return yfcDocChangeOrderStatus.getDocument();
	}
	

}
