package com.kohls.oms.ue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kohls.common.util.KohlsConstant;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnHeaderChargeStruct;
import com.yantra.yfs.japi.YFSExtnInputHeaderChargesShipment;
import com.yantra.yfs.japi.YFSExtnOutputHeaderChargesShipment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetHeaderChargesForShipmentUE;
/**
 * This class is called during Create Shipment Invoice to calculate header charges
 * @author Priyadarshini
 *
 */
public class KohlsGetShipmentHeaderCharge implements
		YFSGetHeaderChargesForShipmentUE {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsGetShipmentHeaderCharge.class.getName());
	
	/**
	 * This method is customized to do the following:
	 * 1) Add shipping charges 
	 */
	public YFSExtnOutputHeaderChargesShipment getHeaderChargesForShipment(
			YFSEnvironment env,
			YFSExtnInputHeaderChargesShipment inputHeaderCharge)
			throws YFSUserExitException {
		YFSExtnOutputHeaderChargesShipment outputHeaderChargeShipment = new YFSExtnOutputHeaderChargesShipment();
		outputHeaderChargeShipment.newHeaderCharges = new ArrayList<YFSExtnHeaderChargeStruct>();
		//Added by Baijayanta for Defect 217
		if ("Y".equals(env.getTxnObject("IsBopusInvoice"))) {
//			env.setTxnObject("IsBopusInvoice", " ");
			return outputHeaderChargeShipment;
		}
		//Closed by Baijayanta for Defect 217
		String strOrderHeaderKey = inputHeaderCharge.orderHeaderKey;
		List<YFSExtnHeaderChargeStruct> chargesHdrList = inputHeaderCharge.orderHeaderCharges;			
			
		setShippingHeaderCharge(env, inputHeaderCharge,
				outputHeaderChargeShipment, strOrderHeaderKey, chargesHdrList);
		
		return outputHeaderChargeShipment;
	}
	

	private void setShippingHeaderCharge(YFSEnvironment env,
			YFSExtnInputHeaderChargesShipment inputHeaderCharge,
			YFSExtnOutputHeaderChargesShipment outputHeaderChargeShipment,
			String strOrderHeaderKey,
			List<YFSExtnHeaderChargeStruct> chargesHdrList) {
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Entered  setShippingHeaderCharge  Module--------------------------> ");
		}
		Iterator<YFSExtnHeaderChargeStruct> hdrChargeItr2 = chargesHdrList 
				.iterator();
		
		while (hdrChargeItr2.hasNext()) {
			YFSExtnHeaderChargeStruct inputCharge = (YFSExtnHeaderChargeStruct) hdrChargeItr2
					.next();			
			if ((inputCharge.chargeCategory.equals(KohlsConstant.ShippingChargeCategory) 
					|| inputCharge.chargeCategory.equals(KohlsConstant.DiscountChargeCategory))
					&& inputCharge.invoicedAmount == 0.0) {
				 
				YFSExtnHeaderChargeStruct newHeaderCharges = new YFSExtnHeaderChargeStruct();
				newHeaderCharges.chargeCategory = inputCharge.chargeCategory;
				newHeaderCharges.chargeName = inputCharge.chargeName;
				newHeaderCharges.chargeAmount = inputCharge.chargeAmount;
				newHeaderCharges.reference = inputCharge.reference;
				outputHeaderChargeShipment.newHeaderCharges
						.add(newHeaderCharges);
				if (YFCLogUtil.isDebugEnabled()) {
					log.debug("<---------------Added Shipping Header  Charge Amount--------------------------> " 
																					+ newHeaderCharges.chargeAmount);
				}
			}			

		}
		if (YFCLogUtil.isDebugEnabled()) {
			log.debug("<---------------Exit setShippingHeaderCharge  Module--------------------------> ");
		}
	}
	
}