package com.kohls.common.util;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;

public class KohlsNotificationUtil {
	
	
	public static boolean checkExtnCustNotificationSent(Element eleShipment, String notificationType){
		
		String extnCustNotificationSent = eleShipment.getAttribute("ExtnCustNotificationSent");
		List<String> notificationTypesList = new ArrayList<String>();
		if(extnCustNotificationSent != null){
			if(extnCustNotificationSent.indexOf(";") !=-1){
				String[] aryExtnCustNotificationSent = extnCustNotificationSent.split(";");
				for(int i=0; i< aryExtnCustNotificationSent.length; i++)
					notificationTypesList.add(aryExtnCustNotificationSent[i].trim());
				if(!notificationTypesList.isEmpty()){
					if(notificationTypesList.contains(notificationType))					
						return true;
					else{
						notificationTypesList.add(notificationType);
						
						//logic to set the attribute
						for(int i=0; i< notificationTypesList.size(); i++){
							if(i==0)
								extnCustNotificationSent = notificationTypesList.get(i);
							else
								extnCustNotificationSent += ";"+notificationTypesList.get(i);
						}
						
						eleShipment.setAttribute("ExtnCustNotificationSent", extnCustNotificationSent);
						return false;
					}
				}
			}
			else if(extnCustNotificationSent.equals(notificationType)){
				return true;
			}
			else{
				eleShipment.setAttribute("ExtnCustNotificationSent", notificationType);
				return false;
			}
				
		}
		
		return true;
		
	}

}
