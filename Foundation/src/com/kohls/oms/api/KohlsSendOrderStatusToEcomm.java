package com.kohls.oms.api;

/**************************************************************************
 * File : KohlsSendOrderStatusToEcomm.java Author : IBM 
 * Created : January 27 2014
 * Modified :
 * Version : 0.1
 ***************************************************************************** 
 * HISTORY 
 ***************************************************************************** 
 * V0.1 27/01/2014 IBM First Cut.

 ***************************************************************************** 
 * TO DO : 
 * ***************************************************************************
 * Copyright @ 2014. This document has been prepared and written by IBM Global
 * Services on behalf of Kohls, and is copyright of Kohls
 * 
 ***************************************************************************** 
 ***************************************************************************** 
 * This file forms the document to be sent to Ecomm on status update
 * 
 * @author Baijayanta
 * @version 0.1
 *****************************************************************************/

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.KohlsXMLUtil;
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

public class KohlsSendOrderStatusToEcomm implements YIFCustomApi {

	private YIFApi api;
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsSendOrderStatusToEcomm.class.getName());
	
	
	public KohlsSendOrderStatusToEcomm() throws YIFClientCreationException {
		 this.api = YIFClientFactory.getInstance().getLocalApi();
	}
	
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	public void statusSentToEcomm(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, TransformerException {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("inside statusSentToEcomm ");
		 }

		String orderLineStat = "";
		Element eleOrder =   inXML.getDocumentElement();
		NodeList nodeOrderLine= eleOrder.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);
		ArrayList codeList = new ArrayList();

		try {
			
		//Generate the document for calling getCommonCodeList API
		YFCDocument yfcDocGetReturnCodeList = YFCDocument.createDocument(KohlsXMLLiterals.E_COMMON_CODE);
		YFCElement yfcEleReturnCodeList = yfcDocGetReturnCodeList.getDocumentElement();
		yfcEleReturnCodeList.setAttribute(KohlsXMLLiterals.A_CODE_TYPE, KohlsConstant.KOHLS_ORDER_STATUS);
		
		Document docGetCommonCodeOutputXML = api.getCommonCodeList(env, yfcDocGetReturnCodeList.getDocument());
	
		NodeList ndlstCommonCodeList = docGetCommonCodeOutputXML.getElementsByTagName(KohlsXMLLiterals.E_COMMON_CODE);
		
		//loop through the commoncodeList and add it to an arrayList
		for(int j=0;j<ndlstCommonCodeList.getLength();j++){

			Element eleCommonCode = (Element)ndlstCommonCodeList.item(j);
			codeList.add(eleCommonCode.getAttribute(KohlsXMLLiterals.A_CODE_VALUE));		
		}
	
		for(int i=0;i<nodeOrderLine.getLength();i++){

			Element eleOrderLine = (Element) nodeOrderLine.item(i);
			
			orderLineStat = eleOrderLine.getAttribute(KohlsXMLLiterals.A_MAX_ORDER_LINE_STATUS);
		
			if(codeList.contains(orderLineStat)) {
		
		
				YFCDocument yfcDocOrderStatusToECommXML = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
				YFCElement yfcEleOrderStatusToECommXML = yfcDocOrderStatusToECommXML.getDocumentElement();

				yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ORDERNO, eleOrder.getAttribute(KohlsXMLLiterals.A_ORDER_NUMBER) );
				yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, eleOrder.getAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE));
				yfcEleOrderStatusToECommXML.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleOrder.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));

				YFCElement yfcEleOrderLines = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINES);
				yfcEleOrderStatusToECommXML.appendChild(yfcEleOrderLines);
								
					YFCElement yfcEleOrderLine = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_LINE);
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_PRIME_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SUB_LINE_NO, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SUB_LINE_NO));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_SCAC, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SCAC));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_CARRIER_SERVICE_CODE));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
					
					Element eleItem = KohlsXMLUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ITEM);

					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ITEM_ID,eleItem.getAttribute(KohlsXMLLiterals.A_ITEM_ID));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_UOM, eleItem.getAttribute(KohlsXMLLiterals.A_UOM));
					yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_QUANTITY, eleItem.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY));
					
					YFCElement yfcEleOrderstatuses = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_STATUSES);
					YFCElement yfcEleOrderstatus = yfcDocOrderStatusToECommXML.createElement(KohlsXMLLiterals.E_ORDER_STATUS);
					
					Element eleOrderStatusesfrominput = KohlsXMLUtil.getChildElement(eleOrderLine, KohlsXMLLiterals.E_ORDER_STATUSES);
					Element eleOrderStatusfrominput = KohlsXMLUtil.getChildElement(eleOrderStatusesfrominput, KohlsXMLLiterals.E_ORDER_STATUS);
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_SHIPNODE, eleOrderLine.getAttribute(KohlsXMLLiterals.A_SHIPNODE));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS, eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_DATE,eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_DATE));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION, eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_DESCRIPTION));
					yfcEleOrderstatus.setAttribute(KohlsXMLLiterals.A_STATUS_QTY,eleOrderStatusfrominput.getAttribute(KohlsXMLLiterals.A_STATUS_QTY));

					
					yfcEleOrderLines.appendChild(yfcEleOrderLine);
					yfcEleOrderLine.appendChild(yfcEleOrderstatuses);
					yfcEleOrderstatuses.appendChild(yfcEleOrderstatus);
					this.api.executeFlow(env, KohlsConstant.SERVICE_KOHLS_SEND_RELEASE_STATUS_TO_ECOMM, yfcDocOrderStatusToECommXML.getDocument());
					
			}

		}
	
	}
		catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException("Exception in statusSentToEcomm method"+e.getStackTrace());
		}

	}
	
}
	

