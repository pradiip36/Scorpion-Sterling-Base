 package com.kohls.oms.ue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.kohls.common.util.KohlsConstant;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSExtnHeaderTaxCalculationInputStruct;
import com.yantra.yfs.japi.YFSExtnTaxBreakup;
import com.yantra.yfs.japi.YFSExtnTaxCalculationOutStruct;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSRecalculateHeaderTaxUE;
/**
 * This class is called during Create Shipment Invoice to calculate header taxes
 * @author Priyadarshini
 *
 */
public class KohlsRecalculateHeaderTaxUE implements
		YFSRecalculateHeaderTaxUE {
	
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsRecalculateHeaderTaxUE.class.getName());
		
	@Override
	public YFSExtnTaxCalculationOutStruct recalculateHeaderTax(
			YFSEnvironment env, YFSExtnHeaderTaxCalculationInputStruct inputStruct)
			throws YFSUserExitException {
		YFSExtnTaxCalculationOutStruct outputHeaderTax = new YFSExtnTaxCalculationOutStruct();
		outputHeaderTax.colTax = new ArrayList<YFSExtnTaxBreakup>();
		
		List<YFSExtnTaxBreakup> chargesHdrTaxList = inputStruct.colTax;		
		Iterator<YFSExtnTaxBreakup> hdrTaxItr = chargesHdrTaxList.iterator();
		while (hdrTaxItr.hasNext()) {
			YFSExtnTaxBreakup inputTax = (YFSExtnTaxBreakup) hdrTaxItr.next();
			if (inputTax.chargeCategory.equals(KohlsConstant.SHIPPING_TAX_CHARGE_CATEGORY) && 
					inputTax.invoicedTax == 0.0) {
				log.verbose("########## INSIDE  Header Tax ###########");
				
				if ("Y".equals(env.getTxnObject("IsBopusInvoice")) && inputStruct.bForInvoice) {
					continue;
				}
				
				YFSExtnTaxBreakup newHeaderTax = new YFSExtnTaxBreakup();
				newHeaderTax.chargeCategory = inputTax.chargeCategory;
				newHeaderTax.chargeName = inputTax.chargeName;
				newHeaderTax.tax = inputTax.tax;
				newHeaderTax.taxName = inputTax.taxName;
				newHeaderTax.taxPercentage = inputTax.taxPercentage;
				newHeaderTax.reference1 = inputTax.reference1;
				newHeaderTax.reference2 = inputTax.reference2;
				newHeaderTax.reference3 = inputTax.reference3;				
				outputHeaderTax.colTax.add(newHeaderTax);

			}
			
		}
		return outputHeaderTax;
	}

}