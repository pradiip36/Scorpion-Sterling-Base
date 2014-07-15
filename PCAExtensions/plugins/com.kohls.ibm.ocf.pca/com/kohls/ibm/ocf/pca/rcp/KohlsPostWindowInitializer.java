package com.kohls.ibm.ocf.pca.rcp;

import org.w3c.dom.Element;

import com.yantra.yfc.rcp.IYRCPostWindowOpenInitializer;
import com.yantra.yfc.rcp.YRCPlatformUI;

public class KohlsPostWindowInitializer implements IYRCPostWindowOpenInitializer{

	public void postWindowOpen() {
		Element eleSIMList = KohlsApplicationInitializer.elePrinterTerminalPropertiesForUI;
		if(!YRCPlatformUI.isVoid(eleSIMList)){
		
			if(!eleSIMList.hasChildNodes()){
				YRCPlatformUI.showInformation(YRCPlatformUI
						.getString("KHLS_INFO"), YRCPlatformUI
						.getString("NOT_REG"));
				
			}
			
		}
		
		
	}

}
