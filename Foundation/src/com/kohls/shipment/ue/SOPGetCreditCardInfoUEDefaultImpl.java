package com.kohls.shipment.ue;

import java.rmi.RemoteException;

import org.w3c.dom.Document;

import com.kohls.common.util.KohlsConstant;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.pca.sop.japi.ue.SOPGetCreditCardInfoUE;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class SOPGetCreditCardInfoUEDefaultImpl implements
		SOPGetCreditCardInfoUE {
	public Document getOrderOnCreditCard(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		YFSUserExitException ee;

		try {
			YFCDocument dInput = YFCDocument.getDocumentFor(inXML);
			if (dInput == null) {
				return null;
			}
			YFCElement eleInput = dInput.getDocumentElement();
			YFCElement eleOrderLineElem = eleInput
					.getChildElement(KohlsConstant.E_ORDER_LINE);
			YFCElement shipElem = eleInput
					.createChild(KohlsConstant.E_SHIPMENT);
			shipElem.setAttribute(KohlsConstant.A_SHIP_NODE, eleOrderLineElem
					.getAttribute(KohlsConstant.A_SHIP_NODE));
			eleInput.removeChild(eleOrderLineElem);
			YIFApi api = YIFClientFactory.getInstance().getApi();
			Document doc = api.getOrderList(env, dInput.getDocument());
			return doc;
		} catch (YIFClientCreationException e) {
			ee = new YFSUserExitException(e.getMessage());
			throw ee;
		} catch (RemoteException e) {
			ee = new YFSUserExitException(e.getMessage());
			throw ee;
		} catch (YFSException e) {
			ee = new YFSUserExitException(e.getMessage());
		}
		throw ee;
	}
}