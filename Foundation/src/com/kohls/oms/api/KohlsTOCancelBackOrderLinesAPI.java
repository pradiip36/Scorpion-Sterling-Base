package com.kohls.oms.api;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
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
/**
 * This class is called on the ON_BACKORDER event of scheduleOrder In Transfer order Pipe line.This class looks for any existing dependency 
 * on any line and remove them and if not cancel them partial cancellation is allowed.
 *  
 * @author OASIS
 * Added for 69773,379,000 -- OASIS_SUPPORT 3/1/2013 
 * Added for Inventory transfer Management .
 */
public class KohlsTOCancelBackOrderLinesAPI implements YIFCustomApi {
	
	
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsTOCancelBackOrderLinesAPI.class.getName());
	private  YIFApi api;



	public KohlsTOCancelBackOrderLinesAPI() throws YIFClientCreationException {		 
			api = YIFClientFactory.getInstance().getLocalApi(); 		 
	}
	
	public void invoke(YFSEnvironment env, Document inXML) throws Exception
	{	
		HashMap<String, String> hm = new HashMap<String, String>();
		
		if(YFCLogUtil.isDebugEnabled()){

			log.debug("<!-------- Begining of KohlsTOCancelBackOrderLinesAPI invoke method ----------- >" + XMLUtil.getXMLString(inXML));
		}

		Element eleRoot = inXML.getDocumentElement();
		String sOrderHeaderKey = eleRoot.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);

		YFCDocument yfcDocGetOrderList = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcEleGetOrderList = yfcDocGetOrderList.getDocumentElement();
		yfcEleGetOrderList.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);

		env.setApiTemplate(KohlsConstant.API_GET_ORDER_LIST, getOrderListTemp());

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Input XML : " + XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}

		Document docGetOrderListOutput = api.getOrderList(env, yfcDocGetOrderList.getDocument());
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_LIST);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList output XML : " + XMLUtil.getXMLString(yfcDocGetOrderList.getDocument()));
		}

		

			YFCDocument yfcDocChangeOrder = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDER);
			YFCElement yfcEleOrder = yfcDocChangeOrder.getDocumentElement();
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.SO_DOCUMENT_TYPE);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_DOCUMENTTYPE, KohlsConstant.TO_DOCUMENT_TYPE);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE, eleRoot.getAttribute(KohlsXMLLiterals.A_ENTERPRISE_CODE));
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, sOrderHeaderKey);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
			


			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_CODE, KohlsConstant.REASON_CODE_NO_INV);
			yfcEleOrder.setAttribute(KohlsXMLLiterals.A_MODIFICATION_REASON_TEXT, KohlsConstant.REASON_TEXT_NO_INV);

			YFCElement yfcEleOrderLines = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINES);
			yfcEleOrder.appendChild(yfcEleOrderLines);


			NodeList ndlstOrderLines = eleRoot.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

			for(int i=0;i<ndlstOrderLines.getLength();i++){

				Element eleOrderLine = (Element)ndlstOrderLines.item(i);
				String sOrderedQty = eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDERED_QTY);
				NodeList ndlstBackOrderedFrom = eleOrderLine.getElementsByTagName(KohlsXMLLiterals.E_BACK_ORDERED_FROM);
				Element eleBackOrderedFrom = (Element)ndlstBackOrderedFrom.item(0);
				String sBackOrderedQty = eleBackOrderedFrom.getAttribute(KohlsXMLLiterals.A_BACK_ORDERED_QTY);
				Double iNewOrderedQty = (Double.parseDouble(sOrderedQty) - Double.parseDouble(sBackOrderedQty));
				if(YFCLogUtil.isDebugEnabled()){			
					log.debug("new Ordered Qty : " + String.valueOf(iNewOrderedQty));
				}

				YFCElement yfcEleOrderLine = yfcDocChangeOrder.createElement(KohlsXMLLiterals.E_ORDER_LINE);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, eleOrderLine.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY));
				//yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A__QTY, String.valueOf(iNewOrderedQty));
				yfcEleOrderLine.setAttribute(KohlsXMLLiterals.A_ORDERED_QTY, String.valueOf(iNewOrderedQty));
				yfcEleOrderLines.appendChild(yfcEleOrderLine);
			}
			if(YFCLogUtil.isDebugEnabled()){			
				log.debug("Input XML to changeOrder: " + XMLUtil.getXMLString(yfcDocChangeOrder.getDocument()));
			}

			api.changeOrder(env, yfcDocChangeOrder.getDocument());
		}

	//}
/**
 * Method to get orderList tempalate
 */
	private Document getOrderListTemp(){

		YFCDocument yfcDocGetOrderListTemp = YFCDocument.createDocument(KohlsXMLLiterals.E_ORDERLIST);
		YFCElement yfcEleDocGetOrderListTemp = yfcDocGetOrderListTemp.getDocumentElement();

		YFCElement yfcEleOrderTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER);
		yfcEleOrderTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");

		YFCElement yfcEleOrderLinesTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINES);
		YFCElement yfcEleOrderLineTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_ORDER_LINE);
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY, "");
		yfcEleOrderLineTemp.setAttribute(KohlsXMLLiterals.A_PARENT_DEPENDENT_GROUP, "");

		YFCElement yfcEleExtnTemp = yfcDocGetOrderListTemp.createElement(KohlsXMLLiterals.E_EXTN);
		yfcEleExtnTemp.setAttribute(KohlsXMLLiterals.A_EXTN_WRAP_TOGETHER_GROUP_CODE, "");
		yfcEleOrderLineTemp.appendChild(yfcEleExtnTemp);
		yfcEleOrderLinesTemp.appendChild(yfcEleOrderLineTemp);
		yfcEleOrderTemp.appendChild(yfcEleOrderLinesTemp);
		yfcEleDocGetOrderListTemp.appendChild(yfcEleOrderTemp);

		if(YFCLogUtil.isDebugEnabled()){			
			log.debug("getOrderList Template : " + XMLUtil.getXMLString(yfcDocGetOrderListTemp.getDocument()));
		}
		
		return yfcDocGetOrderListTemp.getDocument();


	}


	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
