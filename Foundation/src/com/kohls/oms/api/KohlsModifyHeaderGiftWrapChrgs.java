package com.kohls.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

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
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
/**
 * This class does a changeOrder if the Header Gift Wrap charge is invoiced at order line level.
 * It modifies the charge amount to zero for all such Header Gift Wrap charges and changes the ChargeAmount
 * at line level to amount invoiced.
 * @author Priyadarshini
 *
 *Modified on 7th April for GiftWrap Tax to exhibit similar behavior as GW charge
 */
public class KohlsModifyHeaderGiftWrapChrgs implements YIFCustomApi {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsModifyHeaderGiftWrapChrgs.class.getName());
	private static   YIFApi api;
	
	public KohlsModifyHeaderGiftWrapChrgs() throws YIFClientCreationException{
		
			api = YIFClientFactory.getInstance().getLocalApi();
		
	}
	/**
	 * If the Header Gift Wrap charge is invoiced at order line,
	 * call changeOrder API to change it to zero.
	 * @param env
	 * @param inXML
	 * @throws RemoteException 
	 * @throws YFSException 
	 * @throws TransformerException 
	 */
	public void modifyHdrGiftWrapChrgs(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, TransformerException{
		log.debug("############ Modify GW charges taxes input XML##################"+ KohlsUtil.extractStringFromNode(inXML));
		// call getOrderDetails
		env.setApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS,
				getOrderDetailsTemp());
		String strOrderHeaderKey = ((Element) inXML.getElementsByTagName(
				KohlsXMLLiterals.E_ORDER_INVOICE).item(0))
				.getAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY);
		Document docOrderDetails = api.getOrderDetails(env,
				getOrderDetailsInput(strOrderHeaderKey));
		env.clearApiTemplate(KohlsConstant.API_GET_ORDER_DETAILS);
		
		Element elemLineDetails = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_LINE_DETAILS).item(0);
		NodeList nlLineDetail = elemLineDetails.getElementsByTagName(KohlsXMLLiterals.E_LINE_DETAIL);
		
		for (int k = 0; k < nlLineDetail.getLength(); k++) {
			modifyGWChrg(env, docOrderDetails, (Element)nlLineDetail.item(k), strOrderHeaderKey);			
		}
		
		Element elemTaxBeakUpList = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_TAX_BREAK_LIST).item(0);
		NodeList nlTaxBrkUp = elemTaxBeakUpList.getElementsByTagName(KohlsXMLLiterals.E_TAX_BREAK_UP);
		for (int i = 0; i < nlTaxBrkUp.getLength(); i++) {
			modifyGWTax(env, docOrderDetails, (Element)nlTaxBrkUp.item(i), strOrderHeaderKey);			
		}
		
	}

	private void modifyGWTax(YFSEnvironment env, Document docOrderDetails,
			Element elemTaxBrkUp, String strOrderHeaderKey) throws YFSException, RemoteException {
		
		
		Element elemHeaderTaxes = (Element) docOrderDetails
				.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAXES)
				.item(0);

		if (elemHeaderTaxes == null || !elemHeaderTaxes.hasChildNodes()) {
			return;
		}

		// loop through header taxes to check gift wrap charge exist
		NodeList nlHeaderTax = elemHeaderTaxes
				.getElementsByTagName(KohlsXMLLiterals.E_HEADER_TAX);
		for (int j = 0; j < nlHeaderTax.getLength(); j++) {
			Element elemHeaderTax = (Element) nlHeaderTax.item(j);
			String strChargeName = elemHeaderTax
					.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
			String strChargeCategory = elemHeaderTax
					.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY);
			String strTax = elemHeaderTax
					.getAttribute(KohlsXMLLiterals.A_TAX);
			String strTaxName = elemHeaderTax
			.getAttribute(KohlsXMLLiterals.A_TAX_NAME);
			double dChargeAmount = Double.valueOf(strTax);
			if (strChargeCategory.equals(KohlsConstant.GiftTaxCategory)
					&& dChargeAmount != 0.00
					&& env.getTxnObject(strChargeName+strChargeCategory) != null) {
				// changeOrder to change tax to 00.00
				api.changeOrder(
						env,
						changeOrderInptGWTax(strOrderHeaderKey, (String)env.getTxnObject(strChargeName+strChargeCategory),
								strChargeName, strChargeCategory, strTax, strTaxName));
			}
		}
		
	}

	private Document changeOrderInptGWTax(String strOrderHeaderKey, String strOrderLineKey, String strLineChargeName, 
			String strLineChargeCategory, String strAmountInvoiced, String strTaxName) {
		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement eleOrder = yfcDocOrder.getDocumentElement();
		eleOrder.setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		eleOrder.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		eleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		
		
		YFCElement eleHeaderTaxes = eleOrder.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		YFCElement eleHeaderTax = eleHeaderTaxes.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		eleHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, strLineChargeName);
		eleHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, strLineChargeCategory);
		eleHeaderTax.setAttribute(KohlsXMLLiterals.A_TAX, "0.00");
		eleHeaderTax.setAttribute(KohlsXMLLiterals.A_TAX_PERCENTAGE, "0.00");
		eleHeaderTax.setAttribute(KohlsConstant.ATTR_RMNG_TAX, "0.00");

		return yfcDocOrder.getDocument();
	}

	private void modifyGWChrg(YFSEnvironment env, Document docOrderDetails,
			Element elemLineDetail, String strOrderHeaderKey) throws RemoteException {
				
		Element elemLineChargeList = (Element) elemLineDetail
				.getElementsByTagName(KohlsXMLLiterals.E_LINE_CHARGE_LIST)
				.item(0);
		if (elemLineChargeList == null) {
			return;
		}
		if (Integer.valueOf(elemLineChargeList
				.getAttribute(KohlsXMLLiterals.A_TOTAL_NUMBER_OF_RECORDS)) == 0) {
			return;
		}
		
		Element elemHeaderCharges = (Element) docOrderDetails
				.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGES)
				.item(0);

		if (elemHeaderCharges == null || !elemHeaderCharges.hasChildNodes()) {
			return;
		}

		// loop through header charges to check gift wrap charge
		NodeList nlHeaderCharge = elemHeaderCharges
				.getElementsByTagName(KohlsXMLLiterals.A_HEADER_CHARGE);
		for (int j = 0; j < nlHeaderCharge.getLength(); j++) {
			Element elemHeaderCharge = (Element) nlHeaderCharge.item(j);
			String strChargeName = elemHeaderCharge
					.getAttribute(KohlsXMLLiterals.A_CHARGE_NAME);
			String strChargeCategory = elemHeaderCharge
					.getAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY);
			String strChargeAmt = elemHeaderCharge
					.getAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT);
			double dChargeAmount = Double.valueOf(strChargeAmt);
			if (strChargeCategory.equals(KohlsConstant.GiftChargeCategory)
					&& dChargeAmount != 0.00
					&& env.getTxnObject(strChargeName+strChargeCategory) != null) {
				// changeOrder to change chargeAmount to 00.00
				api.changeOrder(
						env,
						changeOrderInput(strOrderHeaderKey, (String)env.getTxnObject(strChargeName+strChargeCategory),
								strChargeName, strChargeCategory, strChargeAmt));
			}
		}
		
	}
	
	private Document getOrderDetailsInput(String strOrderHeaderKey) {
		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		yfcDocOrder.getDocumentElement().setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		return yfcDocOrder.getDocument();
	}
	
	private Document changeOrderInput(String strOrderHeaderKey, String strOrderLineKey, String strLineChargeName, 
												String strLineChargeCategory, String strAmountInvoiced) {
		YFCDocument yfcDocOrder = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement eleOrder = yfcDocOrder.getDocumentElement();
		eleOrder.setAttribute(
				KohlsXMLLiterals.A_ORDER_HEADER_KEY, strOrderHeaderKey);
		eleOrder.setAttribute(KohlsXMLLiterals.A_ACTION, KohlsConstant.ACTION_MODIFY);
		eleOrder.setAttribute(KohlsXMLLiterals.A_OVERRIDE, KohlsConstant.YES);
		
		
		YFCElement eleHeaderCharges = eleOrder.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
		YFCElement eleHeaderCharge = eleHeaderCharges.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);
		eleHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, strLineChargeName);
		eleHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, strLineChargeCategory);
		eleHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, "0.00");
			
		return yfcDocOrder.getDocument();
	}
	
	private Document getOrderDetailsTemp() {
		YFCDocument yfcDocGetOrderDetailsTemp = YFCDocument
		.createDocument(KohlsXMLLiterals.E_ORDER);
		YFCElement yfcElemOrderDetailsTemp = yfcDocGetOrderDetailsTemp.getDocumentElement();
		yfcElemOrderDetailsTemp.setAttribute(KohlsXMLLiterals.A_ORDER_HEADER_KEY, "");
		
		YFCElement yfcElemHeaderCharges = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.A_HEADER_CHARGES);
		YFCElement yfcElemHeaderCharge = yfcElemHeaderCharges.createChild(KohlsXMLLiterals.A_HEADER_CHARGE);
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, " ");
		yfcElemHeaderCharge.setAttribute(KohlsXMLLiterals.A_CHARGE_AMOUNT, " ");	
		
		YFCElement yfcElemHeaderTaxes = yfcElemOrderDetailsTemp.createChild(KohlsXMLLiterals.E_HEADER_TAXES);
		YFCElement yfcElemHeaderTax = yfcElemHeaderTaxes.createChild(KohlsXMLLiterals.E_HEADER_TAX);
		yfcElemHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_NAME, " ");
		yfcElemHeaderTax.setAttribute(KohlsXMLLiterals.A_CHARGE_CATEGORY, " ");
		yfcElemHeaderTax.setAttribute(KohlsXMLLiterals.A_TAX, " ");	
				
		return yfcDocGetOrderDetailsTemp.getDocument();
	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
}
