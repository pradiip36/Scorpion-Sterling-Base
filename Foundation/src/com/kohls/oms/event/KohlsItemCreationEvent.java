package com.kohls.oms.event;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.kohls.common.util.KohlsCommonUtil;
import com.kohls.common.util.KohlsConstants;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class KohlsItemCreationEvent {

	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsItemCreationEvent.class.getName());

	public static Document updateItemEligiblityandSafetyFactor(YFSEnvironment env, Document inXML) throws Exception {

		try {
			Element itemEle = inXML.getDocumentElement();
			Element extnEle = (Element) itemEle.getElementsByTagName(KohlsConstants.EXTN).item(0);
			Element ele1 = (Element) itemEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS).item(0);
			String onHandSafetyfactor = null;

			if (YFCCommon.isVoid(ele1)) {
				Element ele2 = SCXmlUtil.createChild(itemEle, KohlsConstants.INVENTORY_PARAMETERS);
			} else {
				onHandSafetyfactor = ele1.getAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY);
			}

			if (YFCCommon.isVoid(extnEle)) {
				Element ele2 = SCXmlUtil.createChild(itemEle, KohlsConstants.EXTN);
				ele2.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, "");
				if (YFCCommon.isVoid(onHandSafetyfactor)) {
					((Element) itemEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS).item(0)).setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, "0");
				}
				return inXML;
			}
			// update the ExtnShipNodeSource and KohlsEligibleBulkMarkReq in
			// input xml of manageItem API
			/*
			 * <Item Action="Modify" ItemKey="" > <Extn ExtnShipNodeSource=""
			 * KohlsEligibleBulkMarkReq="" /> </Item>
			 */
			String extnShipNodeSource = extnEle.getAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE);

			/*
			 * To check of the ExtnShipNodeSource is passed as a part of input
			 * XML to manageItem API
			 */
			if (YFCCommon.isVoid(extnShipNodeSource)) {

				Document inDocforeligibility = inputItemeligibility(inXML);

				Document docItemEligibilityOutDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_GET_ITEM_ELIGIBILITY, inDocforeligibility);
				Element extnEligibileEle = (Element) docItemEligibilityOutDoc.getElementsByTagName(KohlsConstants.EXTN_ELIGIBLE).item(0);

				/*
				 * Extn_Ship_Node_Source is not present in the DB for item's
				 * Dept/Class/SubClass then it is set to empty.
				 * 
				 */
				if (YFCCommon.isVoid(extnEligibileEle)) {
					extnEle.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, "");
				}
				else
				{
				String setEligible = extnEligibileEle.getAttribute(KohlsConstants.SET_ELIGIBLE_AS);
				String eligibleKey = extnEligibileEle.getAttribute(KohlsConstants.EXTN_STORE_ELIGBL_KEY);
				extnEle.setAttribute(KohlsConstants.EXTN_SHIP_NODE_SOURCE, setEligible);
				extnEle.setAttribute(KohlsConstants.KOHLS_ELIGIBLE_BULK_MARK_REQ, eligibleKey);
				}
			}
			if (YFCCommon.isVoid(onHandSafetyfactor)) {
				/*
				 * input to KohlsgetItemSafetyFactor service
				 * <ExtnItemSafetyFactor CatDepartment="" CatClass=""
				 * CatSubClass=""/>
				 * 
				 */
				String department = extnEle.getAttribute(KohlsConstants.EXTN_DEPT);
				String classs = extnEle.getAttribute(KohlsConstants.EXTN_CLASS);
				String subclass = extnEle.getAttribute(KohlsConstants.EXTN_SUBCLASS);

				Document extnSafetyfctrDoc = SCXmlUtil.createDocument(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR);
				Element extnSafetyFactrEle = extnSafetyfctrDoc.getDocumentElement();
				extnSafetyFactrEle.setAttribute(KohlsConstants.CAT_DEPARTMENT, department);
				extnSafetyFactrEle.setAttribute(KohlsConstants.CAT_CLASS, classs);
				extnSafetyFactrEle.setAttribute(KohlsConstants.CAT_SUBCLASS, subclass);
				Document docItemSftyfctrOutDoc = KohlsCommonUtil.invokeService(env, KohlsConstants.KOHLS_GET_ITEM_SAFETY_FACTOR, extnSafetyfctrDoc);
				extnSafetyFactrEle = (Element) docItemSftyfctrOutDoc.getElementsByTagName(KohlsConstants.EXTN_ITEM_SAFETY_FACTOR).item(0);

				/*
				 * OnhandSafetyFactorQuantity set to zero when record not found
				 * in k_oft_safety_stk table
				 */

				if (YFCCommon.isVoid(extnSafetyFactrEle)) {
					((Element) itemEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS).item(0)).setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, "0");
				} else {
					String safetyFactor = extnSafetyFactrEle.getAttribute(KohlsConstants.DEFAULT_SAFETY_FACTOR);

					/*
					 * OnhandSafetyFactorQuantity set to zero when
					 * DefaultSafetyFactor is null in the k_oft_safety_stk
					 */

					if (YFCCommon.isVoid(safetyFactor)) {
						((Element) itemEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS).item(0)).setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, "0");
					} else {
						((Element) itemEle.getElementsByTagName(KohlsConstants.INVENTORY_PARAMETERS).item(0)).setAttribute(KohlsConstants.ONHAND_SAFTY_FACTORY_QUANTITY, safetyFactor);

					}
					String safetyFactorKey = extnSafetyFactrEle.getAttribute(KohlsConstants.EXTN_ITEM_SAFTY_FACTOR_KEY);
					extnEle.setAttribute(KohlsConstants.KOHL_SAFETY_FACTOR_REQ, safetyFactorKey);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		return inXML;

	}

	// get the data matching the department, subclass, class, style, vendor
	// (build a complex query to get the data from EXTN_ITEM_ELIGIBLITY)
	/**
	 * <ExtnEligible > <ComplexQuery Operator="AND"> <Or> <And> <Exp
	 * Name="Department" Value="dept1" QryType="FLIKE"/> <Exp Name="ClassID"
	 * Value="class1" QryType="FLIKE"/> <Exp Name="SubClass" Value="subclass1"
	 * QryType="FLIKE"/> </And> <Or> <Exp Name="VendorId" Value="vend1"/> </Or>
	 * <Or> <Exp Name="Style" Value="style1"/> </Or> </Or> </ComplexQuery>
	 * </ExtnEligible>
	 */

	private static Document inputItemeligibility(Document inXML) {

		Element itemEle = inXML.getDocumentElement();

		Element primaryInfoEle = (Element) itemEle.getElementsByTagName(KohlsConstants.PRIMARY_INFORMATION).item(0);
		String vendor = null;
		if (YFCCommon.isVoid(primaryInfoEle)) {
			vendor = "";
		} else
			vendor = primaryInfoEle.getAttribute(KohlsConstants.PRIMARY_SUPPLIER);
		Element extnEle = (Element) itemEle.getElementsByTagName(KohlsConstants.EXTN).item(0);
		String department = extnEle.getAttribute(KohlsConstants.EXTN_DEPT);
		String classs = extnEle.getAttribute(KohlsConstants.EXTN_CLASS);
		String subclass = extnEle.getAttribute(KohlsConstants.EXTN_SUBCLASS);
		String style = extnEle.getAttribute(KohlsConstants.EXTN_STYLE);
		Document extnEligibilityDoc = SCXmlUtil.createDocument(KohlsConstants.EXTN_ELIGIBLE);
		Element extnEligibileEle = extnEligibilityDoc.getDocumentElement();
		extnEligibileEle.setAttribute(KohlsConstants.APPLY_NEW_ITEM, KohlsConstants.YES);

		Element complexQry = extnEligibilityDoc.createElement(KohlsConstants.COMPLEX_QUERY);
		extnEligibileEle.appendChild(complexQry);
		complexQry.setAttribute(KohlsConstants.OPERATOR, KohlsConstants.AND);

		Element orElement1 = extnEligibilityDoc.createElement(KohlsConstants.OR);
		complexQry.appendChild(orElement1);

		Element andElement1 = extnEligibilityDoc.createElement(KohlsConstants.AND1);
		orElement1.appendChild(andElement1);

		Element expElement1 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
		expElement1.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_DEPARTMENT);
		expElement1.setAttribute(KohlsConstants.VALUE, department);
		expElement1.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
		andElement1.appendChild(expElement1);

		Element expElement2 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
		expElement2.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_CLASS);
		expElement2.setAttribute(KohlsConstants.VALUE, classs);
		expElement2.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
		andElement1.appendChild(expElement2);

		Element expElement3 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
		expElement3.setAttribute(KohlsConstants.NAME, KohlsConstants.CAT_SUBCLASS);
		expElement3.setAttribute(KohlsConstants.VALUE, subclass);
		expElement3.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
		andElement1.appendChild(expElement3);

		Element orElement2 = extnEligibilityDoc.createElement(KohlsConstants.OR);
		orElement1.appendChild(orElement2);

		Element expElement4 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
		expElement4.setAttribute(KohlsConstants.NAME, KohlsConstants.VENDORID);
		expElement4.setAttribute(KohlsConstants.VALUE, vendor);
		expElement4.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
		orElement2.appendChild(expElement4);

		Element orElement3 = extnEligibilityDoc.createElement(KohlsConstants.OR);
		orElement1.appendChild(orElement3);

		Element expElement5 = extnEligibilityDoc.createElement(KohlsConstants.EXP);
		expElement5.setAttribute(KohlsConstants.NAME, KohlsConstants.STYLE);
		expElement5.setAttribute(KohlsConstants.VALUE, style);
		expElement5.setAttribute(KohlsConstants.QRY_TYPE, KohlsConstants.EQ);
		orElement3.appendChild(expElement5);
		return extnEligibilityDoc;

	}
}
