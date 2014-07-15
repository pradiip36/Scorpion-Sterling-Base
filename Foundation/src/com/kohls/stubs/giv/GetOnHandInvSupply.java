package com.kohls.stubs.giv;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

public class GetOnHandInvSupply implements YIFCustomApi{

	private static final String SUPPLY_TYPE = "SKU_SUPPLY_QTY";
	private YIFApi api;
	private YFCLogCategory log = YFCLogCategory
			.instance(GetOnHandInvSupply.class);

	public GetOnHandInvSupply() throws YIFClientCreationException {

		this.api = YIFClientFactory.getInstance().getLocalApi();
	}

	public int getRandomQty(int quantity){
		Random rand = new Random(); 
		int qty = rand.nextInt(quantity) + 1;
		return qty;
	}
	/**
	 * @param args
	 * @throws FactoryConfigurationError 
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 */
	public Document getExternalSupply(YFSEnvironment env, Document inDoc)
			throws YFSUserExitException, SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		// TODO Auto-generated method stub
		Document outTemp = null;

		if (!YFCCommon.isVoid(inDoc)) {
			log.debug("GetOnHandInvSupply has started::"+SCXmlUtil.getString(inDoc));		
			Element inEle = inDoc.getDocumentElement();
			outTemp = GetOnHandSupplyTemplate(env, inEle);
			return outTemp;
		} else {
			// throw Exception as input XML is invalid
			throw new YFSUserExitException("Invalid Input XML"+SCXmlUtil.getString(inDoc));
		}

	}

	private Document GetOnHandSupplyTemplate(YFSEnvironment env, Element inEle) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		// TODO Auto-generated method stub
		YFCDocument yfcInvSuppliesEle = YFCDocument.createDocument("Items");
		YFCElement yfcElemItemTemp = yfcInvSuppliesEle.getDocumentElement();
		yfcElemItemTemp.setAttribute("IgnoreCommunicatedDemands", "N");
		NodeList itemEle = inEle.getElementsByTagName("Item");
		NodeList inshipNodeList = inEle.getElementsByTagName("ShipNode");
		for (int i = 0; i < itemEle.getLength(); i++) {
			Element eleItem = (Element) itemEle.item(i);
			String ItemID = eleItem.getAttribute("ItemID");
			String orgCode = eleItem.getAttribute("OrganizationCode");	
			String supplyQty = getSupplyQty(env, ItemID, SUPPLY_TYPE);
			System.out.println("Supply Type from the CommonCode::"+supplyQty);
			if(supplyQty == null){
				supplyQty = String.valueOf(getRandomQty(1000));
			}
			for (int j = 0; j < inshipNodeList.getLength(); j++) {
				Element shipNodeEle = (Element) inshipNodeList.item(j);
				String shipNode = shipNodeEle.getAttribute("ShipNode");
				if (ItemID != null && !ItemID.trim().equalsIgnoreCase("")
						&& shipNode != null && orgCode != null
						&& !orgCode.trim().equalsIgnoreCase("")) {
					appendGetSupplyTypeInput(inEle, eleItem, shipNodeEle, yfcInvSuppliesEle, supplyQty);
					break;
				}
				else
					log.debug("Invalid Input XML parameters : Mandatory Elements are missing: ItemID,ShipNode,Organization Code");
			}
		}
		return  yfcInvSuppliesEle.getDocument();
	}

	private String getSupplyQty(YFSEnvironment env, String itemID, String CodeType) throws SAXException, IOException, ParserConfigurationException, FactoryConfigurationError {
		// TODO Auto-generated method stub
		String codeDesc = null;
		StringBuffer codeTypeCheck = new StringBuffer();
		codeTypeCheck.append("<CommonCode CodeType='"+CodeType+"'> </CommonCode>");	
		log.verbose("codeTypeCheck element ::"+codeTypeCheck);
		System.out.println("codeTypeCheck element ::"+codeTypeCheck);
		Document commonCodeInXML = XmlUtils.createFromString(codeTypeCheck.toString());
		log.verbose("CommonCode inputXML::"+SCXmlUtil.getString(commonCodeInXML));
		System.out.println("CommonCode inputXML::"+SCXmlUtil.getString(commonCodeInXML));
		Document itemIDsupplyType = this.api.getCommonCodeList(env, commonCodeInXML);
		log.verbose("Commoncodelist output::"+SCXmlUtil.getString(itemIDsupplyType));
		System.out.println("Commoncodelist output::"+SCXmlUtil.getString(itemIDsupplyType));
		Element commonCodeListEle = itemIDsupplyType.getDocumentElement();
		NodeList codeList = commonCodeListEle.getElementsByTagName("CommonCode");		
		for (int i = 0; i < codeList.getLength(); i++) {
			Element commoncodeEle = (Element) codeList.item(i); 
			String codeValue = commoncodeEle.getAttribute("CodeValue");
			if(itemID.equals(codeValue)){
				codeDesc = commoncodeEle.getAttribute("CodeShortDescription");
				break;
			}
		}
		return codeDesc;

	}

	private void appendGetSupplyTypeInput(Element inEle, Element eleItem, Element shipNodeEle, 
			YFCDocument yfcInvSuppliesEle, String supplyQty) {
		// TODO Auto-generated method stub
		YFCElement yfcElemItemTemp = yfcInvSuppliesEle.getDocumentElement();
		YFCElement yfcElemExtnTemp = yfcElemItemTemp.createChild("Item");
		yfcElemItemTemp.appendChild(yfcElemExtnTemp);
		yfcElemExtnTemp.setAttribute("ConsiderAllNodes", "Y");
		yfcElemExtnTemp.setAttribute("ConsiderAllSegments", "");
		yfcElemExtnTemp.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
		yfcElemExtnTemp.setAttribute("OrganizationCode", eleItem.getAttribute("OrganizationCode"));
		yfcElemExtnTemp.setAttribute("ProductClass", eleItem.getAttribute("ProductClass"));
		yfcElemExtnTemp.setAttribute("UnitOfMeasure", eleItem.getAttribute("UnitOfMeasure"));
		YFCElement yfcElemPrimaryInfo = yfcElemExtnTemp.createChild("Supplies");
		YFCElement yfsElemSupplyTemp = yfcElemPrimaryInfo.createChild("Supply");
		yfsElemSupplyTemp.setAttribute("AvailabilityType", "TRACK");
		yfsElemSupplyTemp.setAttribute("Quantity", supplyQty);
		yfsElemSupplyTemp.setAttribute("Segment", "");
		yfsElemSupplyTemp.setAttribute("SegmentType", "");
		yfsElemSupplyTemp.setAttribute("ShipNode", shipNodeEle.getAttribute("ShipNode"));
		yfsElemSupplyTemp.setAttribute("SupplyType", "ONHAND");
		yfcElemExtnTemp.appendChild(yfcElemPrimaryInfo);
	}


	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}