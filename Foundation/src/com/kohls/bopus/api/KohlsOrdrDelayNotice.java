package com.kohls.bopus.api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.kohls.bopus.util.KohlsOrderNotificationUtil;
import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.kohls.common.util.XMLUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsOrdrDelayNotice {
	
	private static final YFCLogCategory log = YFCLogCategory
			.instance(KohlsOrdrDelayNotice.class.getName());
	
	public Document kohlsBOPUSOrdrDelayNotice(YFSEnvironment env, Document inDoc) {

		log.debug("in kohlsBOPUSOrdrDelayNotice inDoc:"
				+ SCXmlUtil.getString(inDoc));

		try {

			Element eleOrder = SCXmlUtil.getChildElement(
					inDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER);

			if (eleOrder != null) {

				Element orderLinesEle = SCXmlUtil.getChildElement(eleOrder,
						KohlsXMLLiterals.E_ORDER_LINES);

				NodeList orderLineNl = orderLinesEle
						.getElementsByTagName(KohlsXMLLiterals.E_ORDER_LINE);

				for (int k = 0; k < orderLineNl.getLength(); k++) {

					Element orderLineEle = (Element) orderLineNl.item(k);
//					String orderLineKeyStr = orderLineEle
//							.getAttribute(KohlsXMLLiterals.A_ORDER_LINE_KEY);

					// Element eleShipnode = SCXmlUtil.getChildElement(
					// orderLineEle, KohlsXMLLiterals.E_SHIPNODE);
					// Document docItemDOC =
					// getDocumentFromElement(eleShipnode);
					// // eleShipnode.setAttribute(KohlsXMLLiterals.A_STORE_HRS,
					// // strStoreHrs);
					//

					// Code to fetch Item Size and Color Description
					Element elemItem = SCXmlUtil.getChildElement(orderLineEle,
							KohlsXMLLiterals.E_ITEM);
					Document docItem = KohlsOrderNotificationUtil
							.getDocumentFromElement(elemItem);

					Document itemListDoc = KohlsCommonUtil
							.invokeAPI(
									env,
									KohlsConstant.API_GET_ITEM_LIST_WITH_EXTN_FIELDS_TEMPLATE_PATH,
									KohlsConstant.API_GET_ITEM_LIST, docItem);

					Element eleItem = SCXmlUtil.getChildElement(
							itemListDoc.getDocumentElement(),
							KohlsXMLLiterals.E_ITEM);
					Element eleItemExtn = SCXmlUtil.getChildElement(eleItem,
							KohlsXMLLiterals.E_EXTN);
					String colorDesc = eleItemExtn
							.getAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC);
					String sizeDesc = eleItemExtn
							.getAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC);

					Element extnElem = SCXmlUtil.createChild(elemItem,
							KohlsXMLLiterals.E_EXTN);
					extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_COLOR_DESC,
							colorDesc);
					extnElem.setAttribute(KohlsXMLLiterals.A_EXTN_SIZE_DESC,
							sizeDesc);

				}

				Element orderElement = SCXmlUtil.getChildElement(
						inDoc.getDocumentElement(), KohlsXMLLiterals.E_ORDER);
				Document docOrder = KohlsOrderNotificationUtil
						.getDocumentFromElement(orderElement);

				log.debug("KOHLS_ORDER_DELAY_MSG_TO_MRKTNG Service Input Doc:"
						+ SCXmlUtil.getString(docOrder));

				KohlsCommonUtil
						.invokeService(env,
								KohlsConstant.KOHLS_ORDER_DELAY_MSG_TO_MRKTNG,
								docOrder);
				log.debug("called KOHLS_ORDER_DELAY_MSG_TO_MRKTNG Service ");

			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new YFSException(
					"The method kohlsBOPUSOrdrDelayNotice() returned Error."
							+ e);
		}

		return inDoc;
	}	
	
	
	private void testOrderDelayInput() {
		File file = new File(
				"C:\\MyBriefcase\\Official\\Program\\ApplicationDocuments\\KOHLS\\CustomerNotification\\MonitorConsolidationInput.xml");
		try {
			Document doc = XMLUtil.getDocument(new FileInputStream(file));
			System.out.println("in kohlsBOPUSOrdrDelayNotice inDoc:"
					+ SCXmlUtil.getString(doc));
			this.kohlsBOPUSOrdrDelayNotice(null, doc);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
