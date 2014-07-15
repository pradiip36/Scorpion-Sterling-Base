package com.kohls.shipment.api;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSReprintPackSlipDocumentAPI extends KOHLSBaseApi {

	private static final YFCLogCategory log = YFCLogCategory
	.instance(KOHLSReprintPackSlipDocumentAPI.class.getName());

	public Document reprintOfPackSlip(YFSEnvironment env, Document inXML)
	throws Exception {
		Document outPutDoc = XMLUtil.createDocument("PrintPackDocs");
		Element outPutElem = outPutDoc.getDocumentElement();
		Element eleindoc = inXML.getDocumentElement();
		String printerid = eleindoc.getAttribute(KohlsConstant.A_PRINTER_ID);
		// INVOKE GETSHIPMENTDETAILS with the template
		Document OutDoc = KOHLSBaseApi.invokeAPI(env,
				KohlsConstant.API_PACK_SHIPMENT_TEMPLATE_PATH,
				KohlsConstant.API_GET_SHIPMENT_DETAILS, inXML);

		// getting the exact number of container elements
		NodeList containerlist = OutDoc
		.getElementsByTagName(KohlsConstant.E_CONTAINER);
		
		log.debug("KOHLSReprintPackSlipDocumentAPI: OutDoc" + XMLUtil.getXMLString(OutDoc));
		
		env.setTxnObject("OutDoc", OutDoc);

		//

		if(!YFCObject.isVoid(containerlist)){

			int length = containerlist.getLength();

			for (int i = 0; i <length; i++) {
				// cloning the output of getshipment details output
				Document docOutputClone = KOHLSBaseApi.cloneDocument(OutDoc);
				// getting the ith node for container
				NodeList cloneContainerlist = docOutputClone
				.getElementsByTagName(KohlsConstant.E_CONTAINER);

				if(!YFCObject.isVoid(cloneContainerlist) && cloneContainerlist.getLength() > 0){

					Element eleCloneContainer = (Element) cloneContainerlist.item(i);
					// getting the exact position of Containers element
					Element docCloneContainer = docOutputClone.getDocumentElement();

					docCloneContainer.setAttribute(KohlsConstant.A_PRINTER_ID, printerid);
					// removing the containers element
					Element clonecontainers = (Element) docOutputClone
					.getElementsByTagName(KohlsConstant.E_CONTAINERS).item(0);
					clonecontainers.getParentNode().removeChild(clonecontainers);
					// add the containers element
					Element cloneContainers = docOutputClone
					.createElement(KohlsConstant.E_CONTAINERS);
					docCloneContainer.appendChild(cloneContainers);
					// add the ith container element
					cloneContainers.appendChild(eleCloneContainer);

					KOHLSGiftReceiptID.hidePriceInfo(docOutputClone);
					
					Document PrintOutDoc = KOHLSBaseApi.invokeService(env,
							KohlsConstant.SERVICE_PACK_SLIP_PRINT_SERVICE, docOutputClone);
					
					log.debug("KOHLSReprintPackSlipDocumentAPI: PrintOutDoc" + XMLUtil.getXMLString(PrintOutDoc));
					
					KOHLSGiftReceiptID.appendRePrintPackElement(env, PrintOutDoc, outPutDoc, true);
					
					ArrayList<String> alContainerDetailProcessed = new ArrayList<String>();
					ArrayList<String> alContainerDetailPrinted = new ArrayList<String>();
					
					env.setTxnObject("alContainerDetailProcessed", alContainerDetailProcessed);
					env.setTxnObject("alContainerDetailPrinted", alContainerDetailPrinted);
					
					PrintOutDoc = KOHLSGiftReceiptID.printReprintPackSlipForGiftLines(env, docOutputClone, outPutDoc);
				}

			}

		}
		log.debug("outPutDoc: " + XMLUtil.getXMLString(outPutDoc));
		
		return outPutDoc;
		//
	}

}
