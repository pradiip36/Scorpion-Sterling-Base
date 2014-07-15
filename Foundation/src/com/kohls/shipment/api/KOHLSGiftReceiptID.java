package com.kohls.shipment.api;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;
import com.kohls.common.util.XPathUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class comprises all changes related to Gift Receipt ID
 * 
 * @author Saravana
 *
 */
public class KOHLSGiftReceiptID extends KOHLSBaseApi{

	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSGiftReceiptID.class.getName());

	/**
	 * This method will be called to hide the UnitPrice info for all Gift Lines.
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception 
	 */
	
	public static Document hidePriceInfo(Document inputDoc) throws Exception{
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" hidePriceInfo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
				
		boolean bGiftOrder = false;
		
		NodeList nlContainerList = XPathUtil.getNodeList(
				inputDoc.getDocumentElement(), "/Shipment/Containers/Container");
		
		if (!YFCObject.isVoid(nlContainerList)
				&& nlContainerList.getLength() > 0) {

			for (int intContainerCount = 0; intContainerCount < nlContainerList
			.getLength(); intContainerCount++) {
				
				Element eleContainer = (Element) nlContainerList.item(intContainerCount);
				
				NodeList nlContainerDetailList = eleContainer.getElementsByTagName("ContainerDetail");
				
				if (!YFCObject.isVoid(nlContainerDetailList)
						&& nlContainerDetailList.getLength() > 0) {
					
					for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
					.getLength(); intContainerDetailCount++) {
						
						Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
						
						Element eleOrderLine = (Element) eleContainerDetail.getElementsByTagName("OrderLine").item(0);
						
						if (!YFCObject.isVoid(eleOrderLine)){
						
							if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
																
								bGiftOrder = true;
								break;								
							}
						}
					}
				}
				break;
			}
			
			if(bGiftOrder){
				
				inputDoc.getDocumentElement().setAttribute("IsGiftOrder", "Y");
				
				for (int intContainerCount = 0; intContainerCount < nlContainerList
				.getLength(); intContainerCount++) {
					
					Element eleContainer = (Element) nlContainerList.item(intContainerCount);
					
					NodeList nlContainerDetailList = eleContainer.getElementsByTagName("ContainerDetail");
					
					if (!YFCObject.isVoid(nlContainerDetailList)
							&& nlContainerDetailList.getLength() > 0) {
						
						for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
						.getLength(); intContainerDetailCount++) {
														
							Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
							
							Element eleOrderLine = (Element) eleContainerDetail.getElementsByTagName("OrderLine").item(0);
							Element eleLinePriceInfo = (Element) eleOrderLine.getElementsByTagName("LinePriceInfo").item(0);
							eleLinePriceInfo.setAttribute("UnitPrice", "");
						}
					}
				}
			} else{
				inputDoc.getDocumentElement().setAttribute("IsGiftOrder", "N");
			}
		}		
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- End of KOHLSGiftReceiptID" +
					" hidePriceInfo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		return inputDoc;
	}
	
	/**
	 * This method will be called to hide the UnitPrice info for all Gift Lines.
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception 
	 */
	
	public static Document hidePriceInfo(Document inputDoc, String strXPath) throws Exception{
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" hidePriceInfo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
				
		boolean bGiftOrder = false;
		
		NodeList nlXPathList = XPathUtil.getNodeList(
				inputDoc.getDocumentElement(), strXPath);
		
		if (!YFCObject.isVoid(nlXPathList)
				&& nlXPathList.getLength() > 0) {

			for (int intXPathCount = 0; intXPathCount < nlXPathList
			.getLength(); intXPathCount++) {
				
				Element eleXPath = (Element) nlXPathList.item(intXPathCount);
				Element eleOrderLine = (Element) eleXPath.getElementsByTagName("OrderLine").item(0);
				
				if (!YFCObject.isVoid(eleOrderLine)){
				
					if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
											
						bGiftOrder = true;
						break;
					}
				}
			}
			
			if(bGiftOrder){
				
				inputDoc.getDocumentElement().setAttribute("IsGiftOrder", "Y");
				
				for (int intXPathCount = 0; intXPathCount < nlXPathList
				.getLength(); intXPathCount++) {
					
					Element eleXPath = (Element) nlXPathList.item(intXPathCount);
					Element eleOrderLine = (Element) eleXPath.getElementsByTagName("OrderLine").item(0);
					
					if (!YFCObject.isVoid(eleOrderLine)){
					
						Element eleLinePriceInfo = (Element) eleOrderLine.getElementsByTagName("LinePriceInfo").item(0);
						eleLinePriceInfo.setAttribute("UnitPrice", "");
					}
				}
			} else{
				inputDoc.getDocumentElement().setAttribute("IsGiftOrder", "N");
			}
		}		
	
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- End of KOHLSGiftReceiptID" +
					" hidePriceInfo method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
				
		return inputDoc;
	}


	/**
	 * This method prints PackSlip for every Gift line
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	
	public static Document printPackSlipForGiftLines(YFSEnvironment yfsEnvironment, Document inputDoc, Document output, Document PrintPackDocs) 
	throws Exception{
		
		Document PrintOutDoc = null;
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" printPackSlipForGiftLines method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
				
		Element eleInputDoc = inputDoc.getDocumentElement();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docPackSlipPrintServiceInput = builder.newDocument();
        Element eleImported = (Element) docPackSlipPrintServiceInput.importNode(eleInputDoc, true);
        docPackSlipPrintServiceInput.appendChild(eleImported);
		
		NodeList nlContainerDetailsList = docPackSlipPrintServiceInput.getDocumentElement().getElementsByTagName("ContainerDetails");	
		
		if (!YFCObject.isVoid(nlContainerDetailsList)
				&& nlContainerDetailsList.getLength() > 0) {
			
			for (int intContainerDetailsCount = 0; intContainerDetailsCount < nlContainerDetailsList
			.getLength(); intContainerDetailsCount++) {
								
				Element eleContainerDetails = (Element) nlContainerDetailsList.item(intContainerDetailsCount);
				
				if (!YFCObject.isVoid(eleContainerDetails)){
					
					NodeList nlContainerDetailList = eleContainerDetails.getElementsByTagName("ContainerDetail");

					int iNoOfContainerDetail = nlContainerDetailList.getLength();
					
					String strContainerDetailsKey = null;
					
					ArrayList<String> alContainerDetailProcessed = (ArrayList<String>) yfsEnvironment.getTxnObject("alContainerDetailProcessed");
					ArrayList<String> alContainerDetailPrinted = (ArrayList<String>) yfsEnvironment.getTxnObject("alContainerDetailPrinted");
					
					String strCurrentContainerDetailsKey = null;
					boolean bGiftLine = false;
					
					if (!YFCObject.isVoid(nlContainerDetailList)
							&& nlContainerDetailList.getLength() > 0) {
						
						for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
						.getLength(); intContainerDetailCount++) {
											
							Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
							strContainerDetailsKey = eleContainerDetail.getAttribute("ContainerDetailsKey");
							
							if(!YFCObject.isVoid(alContainerDetailProcessed) && !alContainerDetailProcessed.contains(strContainerDetailsKey)){
							
								alContainerDetailProcessed.add(strContainerDetailsKey);
								
								Element eleOrderLine = (Element) eleContainerDetail.getElementsByTagName("OrderLine").item(0);
								
								if(!YFCObject.isVoid(eleOrderLine)){
									
									if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
										
										strCurrentContainerDetailsKey = strContainerDetailsKey;
										bGiftLine = true;
										break;
									}
								}
							}
						}
						
						if(bGiftLine){
							
							for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
							.getLength(); intContainerDetailCount++) {
												
								Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
								strContainerDetailsKey = eleContainerDetail.getAttribute("ContainerDetailsKey");
								
								if(!YFCObject.isVoid(eleContainerDetail)
										&& !eleContainerDetail.getAttribute("ContainerDetailsKey").equals(strCurrentContainerDetailsKey)){
									
									XMLUtil.removeChild(eleContainerDetails, eleContainerDetail);
									intContainerDetailCount--;
								}
							}
							
							if(!YFCObject.isVoid(alContainerDetailPrinted) && !alContainerDetailPrinted.contains(strCurrentContainerDetailsKey)){
																
								PrintOutDoc = KOHLSBaseApi.invokeService(yfsEnvironment, KohlsConstant.SERVICE_PACK_SLIP_PRINT_SERVICE, docPackSlipPrintServiceInput);
								alContainerDetailPrinted.add(strCurrentContainerDetailsKey);
								
								appendPrintPackElement(yfsEnvironment, PrintOutDoc, output, PrintPackDocs, false);
							}
						}
					}
															
					if((alContainerDetailProcessed.size() < iNoOfContainerDetail)){
						
						yfsEnvironment.setTxnObject("alContainerDetailProcessed", alContainerDetailProcessed);
						yfsEnvironment.setTxnObject("alContainerDetailPrinted", alContainerDetailPrinted);
												
						KOHLSGiftReceiptID.printPackSlipForGiftLines(yfsEnvironment, inputDoc, output, PrintPackDocs);
					}
				}
			}
		}
		
		return PrintOutDoc;
	}
	
	
	/**
	 * This method appends one PrintPackDoc element to the output document for every Gift Line.
	 * 
	 * @param yfsEnvironment
	 * @param docSource
	 * @param docDestination
	 * @param PrintPackDocs
	 * @param bFirstExecution
	 * @throws Exception
	 */
	public static void appendPrintPackElement(YFSEnvironment yfsEnvironment, Document docSource, 
			Document docDestination, Document PrintPackDocs, boolean bFirstExecution) throws Exception{
		
		if(!YFCObject.isVoid(docSource) && bFirstExecution){
			
			Element elePrintPackDocs = PrintPackDocs.getDocumentElement();
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintPackDoc = PrintPackDocs.createElement(KohlsConstant.E_PRINT_PACK_DOC);
			elePrintPackDocs.appendChild(elePrintPackDoc);
			
			Element eleImported = (Element) PrintPackDocs.importNode(eleSource, true);
			elePrintPackDoc.appendChild(eleImported);
			Element eleImportedPrintPackDoc = (Element) docDestination.importNode(elePrintPackDocs, true);
			Element eleOutPut = (Element) docDestination.getDocumentElement().getElementsByTagName("OutPut").item(0);
			if(!YFCObject.isVoid(eleOutPut)){ 
				
				eleOutPut.appendChild(eleImportedPrintPackDoc);
			}
		} else if (!bFirstExecution){
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintPackDocs = (Element) docDestination.getDocumentElement().getElementsByTagName("PrintPackDocs").item(0);
						
			if(!YFCObject.isVoid(elePrintPackDocs)){ 
				
				Element elePrintPackDoc = docDestination.createElement(KohlsConstant.E_PRINT_PACK_DOC);				
				elePrintPackDocs.appendChild(elePrintPackDoc);
				
				Element eleSourceOrderLine = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),
				"/Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine/OrderLine").item(0);
				
				String strSourcePrimeLineNo = eleSourceOrderLine.getAttribute("PrimeLineNo");
								
				NodeList nlShipmentLineList = XPathUtil.getNodeList(docDestination.getDocumentElement(),"/Shipment/ShipmentLines/ShipmentLine");

				String ExtnStoreRdcReceiptID = "";
				String strGiftMessage = "";
				String strGiftFrom = "";
				String strGiftTo = "";
				
				if (!YFCObject.isVoid(nlShipmentLineList)
						&& nlShipmentLineList.getLength() > 0) {

					for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList.getLength(); intShipmentLineCount++) {
						
						Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
						Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
						String strPrimeLineNo = eleShipmentLine.getAttribute("PrimeLineNo");
						
						if(strSourcePrimeLineNo.equals(strPrimeLineNo)){
							
							Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName("Extn").item(0);
							
							if(!YFCObject.isVoid(eleOrderLineExtn)){
								
								strGiftMessage = eleOrderLineExtn.getAttribute("ExtnGiftMessage");
								strGiftFrom = eleOrderLineExtn.getAttribute("ExtnGiftFrom");
								strGiftTo = eleOrderLineExtn.getAttribute("ExtnGiftTo");
							}							
														
							NodeList nlNote = eleOrderLine.getElementsByTagName("Note");
							
							if (!YFCObject.isVoid(nlNote)
									&& nlNote.getLength() > 0) {
							
								for (int intNoteCount = 0; intNoteCount < nlNote.getLength(); intNoteCount++) {
									
									Element eleNote = (Element) nlNote.item(intNoteCount);
									Element eleNoteExtn = (Element) eleNote.getElementsByTagName("Extn").item(0);
									
									if(!YFCObject.isVoid(eleNoteExtn)){
										
										String strExtnOrderLineNo = eleNoteExtn.getAttribute("ExtnOrderLineNo");
										
										if(strPrimeLineNo.equals(strExtnOrderLineNo)){
											
											ExtnStoreRdcReceiptID = eleNote.getAttribute("NoteText");
											break;
										}
									}
								}
							}
							
							break;
						}
					}
				}
				
				docSource.getDocumentElement().setAttribute("IsGiftLine", "Y");
				Element eleSourceOrderLineExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(), 
				"/Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine/OrderLine/Extn").item(0);
		
				if(YFCObject.isVoid(eleSourceOrderLineExtn)){
			
					eleSourceOrderLineExtn = docSource.createElement("Extn");
					eleSourceOrderLine.appendChild(eleSourceOrderLineExtn);
				}
				
				eleSourceOrderLineExtn.setAttribute("ExtnGiftMessage", strGiftMessage);
				eleSourceOrderLineExtn.setAttribute("ExtnGiftFrom", strGiftFrom);
				eleSourceOrderLineExtn.setAttribute("ExtnGiftTo", strGiftTo);				
								
				Element eleSourceExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),"/Shipment/Extn").item(0);
				eleSourceExtn.setAttribute("ExtnStoreRdcReceiptID", ExtnStoreRdcReceiptID);
								
				Element eleImportedElement = (Element) docDestination.importNode(eleSource, true);
				elePrintPackDoc.appendChild(eleImportedElement);
			}
		}
	}


	/**
	 * This method prints RePackSlip for every Gift line
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	
	public static Document printRePackSlipForGiftLines(YFSEnvironment yfsEnvironment, Document inputDoc, Document PrintPackDocs) 
	throws Exception{
		
		Document PrintOutDoc = null;
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" printRePackSlipForGiftLines method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
						
		Element eleInputDoc = inputDoc.getDocumentElement();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docRePackSlipPrintServiceInput = builder.newDocument();
        Element eleImported = (Element) docRePackSlipPrintServiceInput.importNode(eleInputDoc, true);
        docRePackSlipPrintServiceInput.appendChild(eleImported);
		
		Element eleShipmentLines = (Element) docRePackSlipPrintServiceInput.getDocumentElement().getElementsByTagName("ShipmentLines").item(0);	
		
		if (!YFCObject.isVoid(eleShipmentLines)){
			
			NodeList nlShipmentLineList = eleShipmentLines.getElementsByTagName("ShipmentLine");

			int iNoOfShipmentLine = nlShipmentLineList.getLength();
			
			String strShipmentLineKey = null;
			
			ArrayList<String> alShipmentLineProcessed = (ArrayList<String>) yfsEnvironment.getTxnObject("alShipmentLineProcessed");
			ArrayList<String> alShipmentLinePrinted = (ArrayList<String>) yfsEnvironment.getTxnObject("alShipmentLinePrinted");
			
			String strCurrentShipmentLineKey = null;
			boolean bGiftLine = false;
			
			if (!YFCObject.isVoid(nlShipmentLineList)
					&& nlShipmentLineList.getLength() > 0) {
				
				for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList
				.getLength(); intShipmentLineCount++) {
									
					Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
					strShipmentLineKey = eleShipmentLine.getAttribute("ShipmentLineKey");
					
					if(!YFCObject.isVoid(alShipmentLineProcessed) && !alShipmentLineProcessed.contains(strShipmentLineKey)){
					
						alShipmentLineProcessed.add(strShipmentLineKey);
						
						Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
						
						if(!YFCObject.isVoid(eleOrderLine)){
							
							if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
								
								strCurrentShipmentLineKey = strShipmentLineKey;
								bGiftLine = true;
								break;
							}
						}
					}
				}
				
				if(bGiftLine){
					
					for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList
					.getLength(); intShipmentLineCount++) {
										
						Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
						strShipmentLineKey = eleShipmentLine.getAttribute("ShipmentLineKey");
						
						if(!YFCObject.isVoid(eleShipmentLine)
								&& !eleShipmentLine.getAttribute("ShipmentLineKey").equals(strCurrentShipmentLineKey)){
							
							log.debug("Removed ShipmentLineKey: "+eleShipmentLine.getAttribute("ShipmentLineKey"));
							XMLUtil.removeChild(eleShipmentLines, eleShipmentLine);
							//intShipmentLineCount--;
							intShipmentLineCount--;
						}
					}
					
					if(!YFCObject.isVoid(alShipmentLinePrinted) && !alShipmentLinePrinted.contains(strCurrentShipmentLineKey)){
						
						
//						PrintOutDoc = KOHLSBaseApi.invokeService(yfsEnvironment, KohlsConstant.SERVICE_RE_PRINT_SINGLE_PICK_SHIPMENT, 
//								docRePackSlipPrintServiceInput);
						PrintOutDoc=KOHLSBaseApi.invokeService(yfsEnvironment,
								KohlsConstant.KOHLS_SINGLE_BATCH_PRINT_SERVICE, docRePackSlipPrintServiceInput);
						alShipmentLinePrinted.add(strCurrentShipmentLineKey);						
						if(!YFCObject.isVoid(PrintOutDoc)){
							
							appendPrintRePackElement(yfsEnvironment, PrintOutDoc, PrintPackDocs, false);
						}
					}
				}
			}
						
			if((alShipmentLineProcessed.size() < iNoOfShipmentLine)){
				
				yfsEnvironment.setTxnObject("alShipmentLineProcessed", alShipmentLineProcessed);
				yfsEnvironment.setTxnObject("alShipmentLinePrinted", alShipmentLinePrinted);
								
				KOHLSGiftReceiptID.printRePackSlipForGiftLines(yfsEnvironment, inputDoc, PrintPackDocs);
			}
		}
		
		return PrintOutDoc;
	}


	
	/**
	 * This method appends one PrintRePackDoc element to the output document for every Gift Line.
	 * 
	 * @param yfsEnvironment
	 * @param docSource
	 * @param docDestination
	 * @param PrintRePackDocs
	 * @param bFirstExecution
	 * @throws Exception
	 */
	public static void appendPrintRePackElement(YFSEnvironment yfsEnvironment, Document docSource, 
			Document PrintRePackDocs, boolean bFirstExecution) throws Exception{
		
		if(!YFCObject.isVoid(docSource) && bFirstExecution){
			
			Element elePrintRePackDocs = PrintRePackDocs.getDocumentElement();
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintRePackDoc = PrintRePackDocs.createElement("PrintRePackDoc");
			elePrintRePackDocs.appendChild(elePrintRePackDoc);
			
			Element eleImported = (Element) PrintRePackDocs.importNode(eleSource, true);
			elePrintRePackDoc.appendChild(eleImported);		
			
		} else if (!bFirstExecution){
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintRePackDocs = PrintRePackDocs.getDocumentElement();
						
			if(!YFCObject.isVoid(elePrintRePackDocs)){ 
				
				Element elePrintRePackDoc = PrintRePackDocs.createElement("PrintRePackDoc");				
				elePrintRePackDocs.appendChild(elePrintRePackDoc);
				
				Element eleSourceOrderLine = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),
				"/Shipment/ShipmentLines/ShipmentLine/OrderLine").item(0);
				
				String strSourcePrimeLineNo = eleSourceOrderLine.getAttribute("PrimeLineNo");
								
				Document docShipmentDetailsOutput = (Document) yfsEnvironment.getTxnObject("docShipmentDetailsOutput");
								
				NodeList nlShipmentLineList = XPathUtil.getNodeList(docShipmentDetailsOutput.getDocumentElement(),"/Shipment/ShipmentLines/ShipmentLine");

				String ExtnStoreRdcReceiptID = "";
								
				if (!YFCObject.isVoid(nlShipmentLineList)
						&& nlShipmentLineList.getLength() > 0) {

					for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList.getLength(); intShipmentLineCount++) {
						
						Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
						Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
						String strPrimeLineNo = eleOrderLine.getAttribute("PrimeLineNo");
						
						if(strSourcePrimeLineNo.equals(strPrimeLineNo)){

							NodeList nlNote = eleOrderLine.getElementsByTagName("Note");
							
							if (!YFCObject.isVoid(nlNote)
									&& nlNote.getLength() > 0) {
							
								for (int intNoteCount = 0; intNoteCount < nlNote.getLength(); intNoteCount++) {
									
									Element eleNote = (Element) nlNote.item(intNoteCount);
									Element eleNoteExtn = (Element) eleNote.getElementsByTagName("Extn").item(0);
																		
									if(!YFCObject.isVoid(eleNoteExtn)){
										
										String strExtnOrderLineNo = eleNoteExtn.getAttribute("ExtnOrderLineNo");
										
										if(strPrimeLineNo.equals(strExtnOrderLineNo)){
											
											ExtnStoreRdcReceiptID = eleNote.getAttribute("NoteText");
											break;
										}
									}
								}
							}
							
							break;
						}
					}
				}
				
				docSource.getDocumentElement().setAttribute("IsGiftLine", "Y");
				
				Element eleSourceOrderLineExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(), 
						"/Shipment/ShipmentLines/ShipmentLine/OrderLine/Extn").item(0);
				
				if(YFCObject.isVoid(eleSourceOrderLineExtn)){
					
					eleSourceOrderLineExtn = docSource.createElement("Extn");
					eleSourceOrderLine.appendChild(eleSourceOrderLineExtn);
				}
				
				Element eleSourceExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),"/Shipment/Extn").item(0);
				eleSourceExtn.setAttribute("ExtnStoreRdcReceiptID", ExtnStoreRdcReceiptID);
								
				Element eleImportedElement = (Element) PrintRePackDocs.importNode(eleSource, true);
				elePrintRePackDoc.appendChild(eleImportedElement);
			}
		}
	}
	
	
	
	
	/**
	 * This method prints PackSlip for every Gift line
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	
	public static Document printReprintPackSlipForGiftLines(YFSEnvironment yfsEnvironment, Document inputDoc, Document PrintPackDocs) 
	throws Exception{
		
		Document PrintOutDoc = null;
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" printPackSlipForGiftLines method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
		
		Element eleInputDoc = inputDoc.getDocumentElement();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docPackSlipPrintServiceInput = builder.newDocument();
        Element eleImported = (Element) docPackSlipPrintServiceInput.importNode(eleInputDoc, true);
        docPackSlipPrintServiceInput.appendChild(eleImported);
		
        NodeList nlContainerDetailsList = docPackSlipPrintServiceInput.getDocumentElement().getElementsByTagName("ContainerDetails");	
		
		if (!YFCObject.isVoid(nlContainerDetailsList)
				&& nlContainerDetailsList.getLength() > 0) {
			
			for (int intContainerDetailsCount = 0; intContainerDetailsCount < nlContainerDetailsList
			.getLength(); intContainerDetailsCount++) {
								
				Element eleContainerDetails = (Element) nlContainerDetailsList.item(intContainerDetailsCount);
				
				if (!YFCObject.isVoid(eleContainerDetails)){
					
					NodeList nlContainerDetailList = eleContainerDetails.getElementsByTagName("ContainerDetail");

					int iNoOfContainerDetail = nlContainerDetailList.getLength();
					
					String strContainerDetailsKey = null;
					
					ArrayList<String> alContainerDetailProcessed = (ArrayList<String>) yfsEnvironment.getTxnObject("alContainerDetailProcessed");
					ArrayList<String> alContainerDetailPrinted = (ArrayList<String>) yfsEnvironment.getTxnObject("alContainerDetailPrinted");
					
					String strCurrentContainerDetailsKey = null;
					boolean bGiftLine = false;
					
					if (!YFCObject.isVoid(nlContainerDetailList)
							&& nlContainerDetailList.getLength() > 0) {
						
						for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
						.getLength(); intContainerDetailCount++) {
											
							Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
							strContainerDetailsKey = eleContainerDetail.getAttribute("ContainerDetailsKey");
							
							if(!YFCObject.isVoid(alContainerDetailProcessed) && !alContainerDetailProcessed.contains(strContainerDetailsKey)){
							
								alContainerDetailProcessed.add(strContainerDetailsKey);
								
								Element eleOrderLine = (Element) eleContainerDetail.getElementsByTagName("OrderLine").item(0);
								
								if(!YFCObject.isVoid(eleOrderLine)){
									
									if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
										
										strCurrentContainerDetailsKey = strContainerDetailsKey;
										bGiftLine = true;
										break;
									}
								}
							}
						}
						
						if(bGiftLine){
							
							for (int intContainerDetailCount = 0; intContainerDetailCount < nlContainerDetailList
							.getLength(); intContainerDetailCount++) {
												
								Element eleContainerDetail = (Element) nlContainerDetailList.item(intContainerDetailCount);
								strContainerDetailsKey = eleContainerDetail.getAttribute("ContainerDetailsKey");
								
								if(!YFCObject.isVoid(eleContainerDetail)
										&& !eleContainerDetail.getAttribute("ContainerDetailsKey").equals(strCurrentContainerDetailsKey)){
									
									log.debug("Removed ContainerDetailsKey: "+eleContainerDetail.getAttribute("ContainerDetailsKey"));
									XMLUtil.removeChild(eleContainerDetails, eleContainerDetail);
									intContainerDetailCount --;
								}
							}
							
							if(!YFCObject.isVoid(alContainerDetailPrinted) && !alContainerDetailPrinted.contains(strCurrentContainerDetailsKey)){
																
								PrintOutDoc = KOHLSBaseApi.invokeService(yfsEnvironment, KohlsConstant.SERVICE_PACK_SLIP_PRINT_SERVICE, docPackSlipPrintServiceInput);
								alContainerDetailPrinted.add(strCurrentContainerDetailsKey);
								
								appendRePrintPackElement(yfsEnvironment, PrintOutDoc, PrintPackDocs, false);
							}
						}
					}
									
					
					if((alContainerDetailProcessed.size() < iNoOfContainerDetail)){
						
						yfsEnvironment.setTxnObject("alContainerDetailProcessed", alContainerDetailProcessed);
						yfsEnvironment.setTxnObject("alContainerDetailPrinted", alContainerDetailPrinted);
						
						KOHLSGiftReceiptID.printReprintPackSlipForGiftLines(yfsEnvironment, inputDoc, PrintPackDocs);
					}
				}
			}
		}
				
		return PrintOutDoc;
	}
	
	
	/**
	 * This method appends one PrintPackDoc element to the output document for every Gift Line.
	 * 
	 * @param yfsEnvironment
	 * @param docSource
	 * @param docDestination
	 * @param PrintPackDocs
	 * @param bFirstExecution
	 * @throws Exception
	 */
	public static void appendRePrintPackElement(YFSEnvironment yfsEnvironment, Document docSource, 
			Document PrintPackDocs, boolean bFirstExecution) throws Exception{
		
		if(!YFCObject.isVoid(docSource) && bFirstExecution){
			
			Element elePrintPackDocs = PrintPackDocs.getDocumentElement();
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintPackDoc = PrintPackDocs.createElement(KohlsConstant.E_PRINT_PACK_DOC);
			elePrintPackDocs.appendChild(elePrintPackDoc);
			
			Element eleImported = (Element) PrintPackDocs.importNode(eleSource, true);
			elePrintPackDoc.appendChild(eleImported);
			
		} else if (!bFirstExecution){
			
			Element eleSource = docSource.getDocumentElement();
			Element elePrintPackDocs = PrintPackDocs.getDocumentElement();
						
			if(!YFCObject.isVoid(elePrintPackDocs)){ 
				
				Element elePrintPackDoc = PrintPackDocs.createElement(KohlsConstant.E_PRINT_PACK_DOC);				
				elePrintPackDocs.appendChild(elePrintPackDoc);
				
				Element eleSourceOrderLine = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),
				"/Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine/OrderLine").item(0);
				
				String strSourcePrimeLineNo = eleSourceOrderLine.getAttribute("PrimeLineNo");
				
				Document docOutputShipmentDetails = (Document) yfsEnvironment.getTxnObject("OutDoc");
								
				NodeList nlShipmentLineList = XPathUtil.getNodeList(docOutputShipmentDetails.getDocumentElement(),"/Shipment/ShipmentLines/ShipmentLine");

				String ExtnStoreRdcReceiptID = "";
				String strGiftMessage = "";
				String strGiftFrom = "";
				String strGiftTo = "";
				
				if (!YFCObject.isVoid(nlShipmentLineList)
						&& nlShipmentLineList.getLength() > 0) {

					for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList.getLength(); intShipmentLineCount++) {
						
						Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
						Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
						String strPrimeLineNo = eleShipmentLine.getAttribute("PrimeLineNo");
						
						if(strSourcePrimeLineNo.equals(strPrimeLineNo)){
							
							Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName("Extn").item(0);
							
							if(!YFCObject.isVoid(eleOrderLineExtn)){
								
								strGiftMessage = eleOrderLineExtn.getAttribute("ExtnGiftMessage");
								strGiftFrom = eleOrderLineExtn.getAttribute("ExtnGiftFrom");
								strGiftTo = eleOrderLineExtn.getAttribute("ExtnGiftTo");
							}							
							
							NodeList nlNote = eleOrderLine.getElementsByTagName("Note");
							
							if (!YFCObject.isVoid(nlNote)
									&& nlNote.getLength() > 0) {
							
								for (int intNoteCount = 0; intNoteCount < nlNote.getLength(); intNoteCount++) {
									
									Element eleNote = (Element) nlNote.item(intNoteCount);
									Element eleNoteExtn = (Element) eleNote.getElementsByTagName("Extn").item(0);
							
									if(!YFCObject.isVoid(eleNoteExtn)){
										
										String strExtnOrderLineNo = eleNoteExtn.getAttribute("ExtnOrderLineNo");
										
										if(strPrimeLineNo.equals(strExtnOrderLineNo)){
											
											ExtnStoreRdcReceiptID = eleNote.getAttribute("NoteText");
											break;
										}
									}
								}
							}
							
							break;
						}
					}
				}
				
				docSource.getDocumentElement().setAttribute("IsGiftLine", "Y");
				Element eleSourceOrderLineExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(), 
				"/Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine/OrderLine/Extn").item(0);
		
				if(YFCObject.isVoid(eleSourceOrderLineExtn)){
			
					eleSourceOrderLineExtn = docSource.createElement("Extn");
					eleSourceOrderLine.appendChild(eleSourceOrderLineExtn);
				}
				
				eleSourceOrderLineExtn.setAttribute("ExtnGiftMessage", strGiftMessage);
				eleSourceOrderLineExtn.setAttribute("ExtnGiftFrom", strGiftFrom);
				eleSourceOrderLineExtn.setAttribute("ExtnGiftTo", strGiftTo);				
								
				Element eleSourceExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),"/Shipment/Extn").item(0);
				eleSourceExtn.setAttribute("ExtnStoreRdcReceiptID", ExtnStoreRdcReceiptID);
				
				Element eleImportedElement = (Element) PrintPackDocs.importNode(eleSource, true);
				elePrintPackDoc.appendChild(eleImportedElement);
			}
		}
	}
	
	
	/**
	 * This method will be called for Print Mass Singles for printing the Pick slip.
	 * 
	 * @param yfsEnvironment
	 * @param cloneOutDoc
	 * @throws Exception
	 */
	public static Document printMassSingles(YFSEnvironment yfsEnvironment, Document cloneOutDoc) throws Exception{
		
		Element eleCloneOutDoc = cloneOutDoc.getDocumentElement();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docSinglePickRePrintService = builder.newDocument();
        Element eleImported = (Element) docSinglePickRePrintService.importNode(eleCloneOutDoc, true);
        docSinglePickRePrintService.appendChild(eleImported);
		
        Element eleShipmentLines = (Element) docSinglePickRePrintService.getDocumentElement().getElementsByTagName("ShipmentLines").item(0);	
		
		if (!YFCObject.isVoid(eleShipmentLines)){
			
			NodeList nlShipmentLineList = XPathUtil.getNodeList(
					docSinglePickRePrintService.getDocumentElement(), "/Shipment/ShipmentLines/ShipmentLine");

			int iNoOfShipmentLine = nlShipmentLineList.getLength();
						
			if (!YFCObject.isVoid(nlShipmentLineList)
					&& nlShipmentLineList.getLength() > 0) {
				
				for (int intShipmentLineCount = 0; intShipmentLineCount < iNoOfShipmentLine; intShipmentLineCount++) {
									
					Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
					
					Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
					
					if(!YFCObject.isVoid(eleOrderLine)){
					
						Element eleOrderLineExtn = (Element) eleOrderLine.getElementsByTagName("Extn").item(0);
						
						if(!YFCObject.isVoid(eleOrderLineExtn)){
							
							eleOrderLineExtn.setAttribute("ExtnGiftMessage", "");
							eleOrderLineExtn.setAttribute("ExtnGiftFrom", "");
							eleOrderLineExtn.setAttribute("ExtnGiftTo", "");
						}
					}
				}
			}
		}
        
		//changing to new service to get to send the report data back 
//		KOHLSBaseApi.invokeService(yfsEnvironment,
//				KohlsConstant.SERVICE_RE_PRINT_SINGLE_PICK_SHIPMENT, docSinglePickRePrintService);
		Document docOutput=KOHLSBaseApi.invokeService(yfsEnvironment,
				KohlsConstant.KOHLS_SINGLE_BATCH_PRINT_SERVICE, docSinglePickRePrintService);
		return docOutput;
	}
	
	
	
	
	/**
	 * This method prints RePackSlip for every Gift line for Mass Singles
	 * 
	 * @param yfsEnvironment
	 * @param inputDoc
	 * @return
	 * @throws Exception
	 */
	
	public static Document printRePackSlipForMassSinglesGiftLines(YFSEnvironment yfsEnvironment, Document inputDoc, Document PrintPackDocs) 
	throws Exception{
		
		Document PrintOutDoc = null;
		
		if (YFCLogUtil.isDebugEnabled())
		{
			KOHLSGiftReceiptID.log.debug("<!-- Begining of KOHLSGiftReceiptID" +
					" printRePackSlipForMassSinglesGiftLines method -- >"
					+ XMLUtil.getXMLString(inputDoc));
		}
				
		Element eleInputDoc = inputDoc.getDocumentElement();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document docRePackSlipPrintServiceInput = builder.newDocument();
        Element eleImported = (Element) docRePackSlipPrintServiceInput.importNode(eleInputDoc, true);
        docRePackSlipPrintServiceInput.appendChild(eleImported);
		
		Element eleShipmentLines = (Element) docRePackSlipPrintServiceInput.getDocumentElement().getElementsByTagName("ShipmentLines").item(0);	
		
		if (!YFCObject.isVoid(eleShipmentLines)){
			
			NodeList nlShipmentLineList = eleShipmentLines.getElementsByTagName("ShipmentLine");

			int iNoOfShipmentLine = nlShipmentLineList.getLength();
			
			String strShipmentLineKey = null;
			
			ArrayList<String> alShipmentLineProcessed = (ArrayList<String>) yfsEnvironment.getTxnObject("alShipmentLineProcessed");
			ArrayList<String> alShipmentLinePrinted = (ArrayList<String>) yfsEnvironment.getTxnObject("alShipmentLinePrinted");
			
			String strCurrentShipmentLineKey = null;
			boolean bGiftLine = false;
			
			if (!YFCObject.isVoid(nlShipmentLineList)
					&& nlShipmentLineList.getLength() > 0) {
				
				for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList
				.getLength(); intShipmentLineCount++) {
									
					Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
					strShipmentLineKey = eleShipmentLine.getAttribute("ShipmentLineKey");
					
					if(!YFCObject.isVoid(alShipmentLineProcessed) && !alShipmentLineProcessed.contains(strShipmentLineKey)){
					
						alShipmentLineProcessed.add(strShipmentLineKey);
						
						Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
						
						if(!YFCObject.isVoid(eleOrderLine)){
							
							if(!YFCObject.isVoid(eleOrderLine.getAttribute("GiftFlag")) && eleOrderLine.getAttribute("GiftFlag").equals("Y")){
								
								strCurrentShipmentLineKey = strShipmentLineKey;
								bGiftLine = true;
								break;
							}
						}
					}
				}
				
				if(bGiftLine){
					
					for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList
					.getLength(); intShipmentLineCount++) {
										
						Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
						strShipmentLineKey = eleShipmentLine.getAttribute("ShipmentLineKey");
						
						if(!YFCObject.isVoid(eleShipmentLine)
								&& !eleShipmentLine.getAttribute("ShipmentLineKey").equals(strCurrentShipmentLineKey)){
							
							log.debug("Removed ShipmentLineKey: "+eleShipmentLine.getAttribute("ShipmentLineKey"));
							XMLUtil.removeChild(eleShipmentLines, eleShipmentLine);
							intShipmentLineCount--;
						}
					}
					
					if(!YFCObject.isVoid(alShipmentLinePrinted) && !alShipmentLinePrinted.contains(strCurrentShipmentLineKey)){
						
						addReceiptIDForGiftLine(yfsEnvironment, docRePackSlipPrintServiceInput);
						
						//changing to new service to get to send the report data back 
//						KOHLSBaseApi.invokeService(yfsEnvironment, KohlsConstant.SERVICE_RE_PRINT_SINGLE_PICK_SHIPMENT, 
//								docRePackSlipPrintServiceInput);
						PrintOutDoc=KOHLSBaseApi.invokeService(yfsEnvironment,
								KohlsConstant.KOHLS_SINGLE_BATCH_PRINT_SERVICE, docRePackSlipPrintServiceInput);
						alShipmentLinePrinted.add(strCurrentShipmentLineKey);						
					}
				}
			}
			
			if((alShipmentLineProcessed.size() < iNoOfShipmentLine)){
				
				yfsEnvironment.setTxnObject("alShipmentLineProcessed", alShipmentLineProcessed);
				yfsEnvironment.setTxnObject("alShipmentLinePrinted", alShipmentLinePrinted);
				
				KOHLSGiftReceiptID.printRePackSlipForGiftLines(yfsEnvironment, inputDoc, PrintPackDocs);
			}
		}
		
		return PrintOutDoc;
	}
	
	
	
	/**
	 * This method adds the Receipt ID for every Gift Line from Mass Print singles screen.
	 * 
	 * @param yfsEnvironment
	 * @param docSource
	 * @param docDestination
	 * @param PrintRePackDocs
	 * @param bFirstExecution
	 * @throws Exception
	 */
	public static void addReceiptIDForGiftLine(YFSEnvironment yfsEnvironment, Document docSource) throws Exception{
		
		Element eleSourceOrderLine = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),
		"/Shipment/ShipmentLines/ShipmentLine/OrderLine").item(0);
		
		String strSourcePrimeLineNo = eleSourceOrderLine.getAttribute("PrimeLineNo");
		
		Document docShipmentDetailsOutput = (Document) yfsEnvironment.getTxnObject("docShipmentDetailsOutput");
		
		NodeList nlShipmentLineList = XPathUtil.getNodeList(docShipmentDetailsOutput.getDocumentElement(),"/Shipment/ShipmentLines/ShipmentLine");

		String ExtnStoreRdcReceiptID = "";
				
		if (!YFCObject.isVoid(nlShipmentLineList)
				&& nlShipmentLineList.getLength() > 0) {

			for (int intShipmentLineCount = 0; intShipmentLineCount < nlShipmentLineList.getLength(); intShipmentLineCount++) {
				
				Element eleShipmentLine = (Element) nlShipmentLineList.item(intShipmentLineCount);
				Element eleOrderLine = (Element) eleShipmentLine.getElementsByTagName("OrderLine").item(0);
				String strPrimeLineNo = eleOrderLine.getAttribute("PrimeLineNo");
				
				if(strSourcePrimeLineNo.equals(strPrimeLineNo)){
					
					NodeList nlNote = eleOrderLine.getElementsByTagName("Note");
					
					if (!YFCObject.isVoid(nlNote)
							&& nlNote.getLength() > 0) {
					
						for (int intNoteCount = 0; intNoteCount < nlNote.getLength(); intNoteCount++) {
							
							Element eleNote = (Element) nlNote.item(intNoteCount);
							Element eleNoteExtn = (Element) eleNote.getElementsByTagName("Extn").item(0);
							
							if(!YFCObject.isVoid(eleNoteExtn)){
								
								String strExtnOrderLineNo = eleNoteExtn.getAttribute("ExtnOrderLineNo");
								
								if(strPrimeLineNo.equals(strExtnOrderLineNo)){
									
									ExtnStoreRdcReceiptID = eleNote.getAttribute("NoteText");
									break;
								}
							}
						}
					}
					
					break;
				}
			}
		}
		
		docSource.getDocumentElement().setAttribute("IsGiftLine", "Y");
		
		Element eleSourceOrderLineExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(), 
				"/Shipment/ShipmentLines/ShipmentLine/OrderLine/Extn").item(0);
		
		if(YFCObject.isVoid(eleSourceOrderLineExtn)){
			
			eleSourceOrderLineExtn = docSource.createElement("Extn");
			eleSourceOrderLine.appendChild(eleSourceOrderLineExtn);
		}										
		
		Element eleSourceExtn = (Element) XPathUtil.getNodeList(docSource.getDocumentElement(),"/Shipment/Extn").item(0);
		eleSourceExtn.setAttribute("ExtnStoreRdcReceiptID", ExtnStoreRdcReceiptID);		
	}	
}