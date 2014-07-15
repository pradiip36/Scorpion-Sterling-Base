package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsChangeOrderStatusAPI implements YIFCustomApi {

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KohlsChangeOrderStatusAPI.class.getName());
	private  YIFApi api;
	
	public KohlsChangeOrderStatusAPI() throws YIFClientCreationException{		
		api = YIFClientFactory.getInstance().getLocalApi();
		
	}
	@Override
	public void setProperties(Properties arg0) throws Exception {

	}

	public void changeOrderStatus(YFSEnvironment env, Document inXML)
			throws YFSException, RemoteException, TransformerException {
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("######### changeOrderStatus input XML #########"+ KohlsUtil.extractStringFromNode(inXML));
		}
		String strOrderHeaderKey = inXML.getDocumentElement().getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		// change Order Status to 'Awaiting Invoice Creation'
		api.changeOrderStatus(env, getChangeOrderStatusInpt(strOrderHeaderKey));
		
	}
	private Document getChangeOrderStatusInpt(String strOrderHeaderKey) {		
		YFCDocument yfcDocChangeOrderStatus = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER_STATUS_CHANGE);
		YFCElement yfcEleChangeOrderStatus = yfcDocChangeOrderStatus.getDocumentElement();
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_TRANSACTIONID, KohlsConstant.TRAN_ID_INCLUDE_INVOICE_0001_EX);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		yfcEleChangeOrderStatus.setAttribute(KohlsXMLLiterals.A_BASE_DROP_STATUS, KohlsConstant.AWAITING_INVOICE_CREATION);

		return yfcDocChangeOrderStatus.getDocument();
	}
	

}
