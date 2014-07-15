package com.kohls.oms.ue;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetAvailabilityCorrectionsUE;

/**
 *  This class will create a map of Ship Node and Available inv picture
 *  and save the map into the env which will be collated and posted into
 *  a queue for getting the inventory picture at time of Scheduling
 */
//Start -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //
public class KohlsYFSGetAvailabilityCorrectionsUE implements YFSGetAvailabilityCorrectionsUE{

	private static final YFCLogCategory log =  YFCLogCategory.instance(KohlsYFSGetAvailabilityCorrectionsUE.class.getName());

	@Override
	public Document getAvailabilityCorrections(YFSEnvironment env,
			Document inXML) throws YFSUserExitException {
		// TODO Auto-generated method stub
		if(YFCLogUtil.isDebugEnabled()){
			log.debug("<----------- Begining of KohlsYFSGetAvailabilityCorrectionsUE ----------->");
		}
		//create Map to store the available inv picture
		Map<String,String> mAvailabilityCorrection = new HashMap<String,String>();
		
		Element eleItem=(Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ITEM).item(0);
		String sItem=eleItem.getAttribute(KohlsXMLLiterals.A_ItemID);
		if(eleItem.getElementsByTagName(KohlsXMLLiterals.A_SUPPLIES).getLength()>0){
			Element eleSupplies=(Element)eleItem.getElementsByTagName(KohlsXMLLiterals.A_SUPPLIES).item(0);
			NodeList nlSupply=eleSupplies.getElementsByTagName(KohlsXMLLiterals.A_SUPPLY);
			if(nlSupply.getLength()>0){
				for(int i=0;i<nlSupply.getLength();i++){
					Element eleSupply=(Element)nlSupply.item(i);
					String sShipNode=eleSupply.getAttribute(KohlsXMLLiterals.A_SHIPNODE);
					String sAvailable=eleSupply.getAttribute(KohlsXMLLiterals.A_QUANTITY);
					if(YFCLogUtil.isDebugEnabled()){
						log.debug("ItemID---->"+sItem+"----ShipnNode---->"+sShipNode+"----Supply----->"+sAvailable);
			
					}
				//add ship node and available inv to the map
				mAvailabilityCorrection.put(sShipNode, sAvailable);
			}
		}
		//add available inv map to the env
		env.setTxnObject(KohlsConstant.AVAILABILITY_CORRECTION+sItem, mAvailabilityCorrection);
	}
	return inXML;
	}
}
//End -- Added for 74501,379,000 -- OASIS_SUPPORT 07/08/2012 //