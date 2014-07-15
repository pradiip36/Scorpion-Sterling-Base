package com.kohls.shipment.api;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KOHLSBaseApi;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.XMLUtil;

import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class KOHLSWriteXmlForPickPrintInDirAPI extends KOHLSBaseApi
{
	private static final YFCLogCategory log = YFCLogCategory.instance(
			KOHLSWriteXmlForPickPrintInDirAPI.class.getName());

	public Document writeInDirectory(YFSEnvironment env,
			Document inputDoc) throws Exception
			{

		try{
			HashMap<String,String> DirMap = new HashMap<String,String>();
			String sCodeType = KohlsConstant.V_COMMON_CODE_TYPE_FOR_SHIPNODE_DIR;
			Element eleCommonCodeList = callGetCommonCodeList(env,sCodeType);
			NodeList ndlCommonCode = eleCommonCodeList.getElementsByTagName(KohlsConstant.E_COMMON_CODE);
			for(int i=0; i<ndlCommonCode.getLength(); i++){
				Element eleCommonCode = (Element)ndlCommonCode.item(i);
				DirMap.put(eleCommonCode.getAttribute(KohlsConstant.A_CODE_VALUE), eleCommonCode.getAttribute(KohlsConstant.A_CODE_SHORT_DESC));

			}
			Element eleInput = inputDoc.getDocumentElement();
			String sShipNode = eleInput.getAttribute(KohlsConstant.A_SHIP_NODE);
			String sDirToWrite = DirMap.get(sShipNode);
			NodeList ndlShipmentList = eleInput.getElementsByTagName(KohlsConstant.E_SHIPMENT);
			Element eleFirstShipmentElem =(Element) ndlShipmentList.item(0);
			Element eleLastShipmentElem =(Element) ndlShipmentList.item(ndlShipmentList.getLength()-1);
			String sFirstShipmentNo = eleFirstShipmentElem.getAttribute(KohlsConstant.A_SHIPMENT_NO);
			String sLastShipmentNo = eleLastShipmentElem.getAttribute(KohlsConstant.A_SHIPMENT_NO);
			Calendar cal = Calendar.getInstance();
			DateFormat dateFormat = new SimpleDateFormat( KohlsConstant.V_DATE_TIME_FORMAT);
			String sTime = dateFormat.format(cal.getTime());
			String strFileName=sShipNode+"_"+sFirstShipmentNo+"_"+sLastShipmentNo+ "_"+sTime+".xml";
			String sFilepath = "";
			sFilepath = sDirToWrite + File.separator + strFileName;
			Source source = new DOMSource(inputDoc);
			File file = new File(sFilepath);
			Result result = new StreamResult(file.toURI().getPath());

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(source, result);


		}catch(Exception ex)
		{
			log.debug("Error In Writing to the Directory. Please Check Configuration");
		}

		return inputDoc;

			}

	private Element callGetCommonCodeList(YFSEnvironment env ,String sCodeType) throws Exception {
		Document outDoc = null;
		Document docInputForGetCommonCodeList = XMLUtil.createDocument(KohlsConstant.E_COMMON_CODE);
		Element eleCommonCodeList = docInputForGetCommonCodeList.getDocumentElement();
		eleCommonCodeList.setAttribute(KohlsConstant.A_CODE_TYPE,sCodeType);
		outDoc= KOHLSBaseApi.invokeAPI(env, KohlsConstant.API_GET_COMMON_CODE_LIST, docInputForGetCommonCodeList);
		return outDoc.getDocumentElement();
	}
}