package com.kohls.oms.ue;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.OMPConfirmRefundDistributionUE;
import com.yantra.yfc.log.YFCLogCategory;
/**
 * This class is called when requestionCollection API is invoked to 
 * 1) Check if the gift card no and value are in the env map
 * 2) Assign the refund amount to the SVC card as per the reprice XML
 * @author OASIS 2/25/2013 PMR 04158,999,000
 *
 */
public class KohlsOMPConfirmRefundDistributionUE implements OMPConfirmRefundDistributionUE{
	private static final YFCLogCategory log = YFCLogCategory.instance(KohlsOMPConfirmRefundDistributionUE.class.getName());
	@Override
	public Document confirmRefundDistribution(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		
		YFCDocument yfsOutputXML = null;
		Element elemOrder = (Element)inXML.getElementsByTagName(KohlsXMLLiterals.E_ORDER).item(0);
		String strOrderNo = elemOrder.getAttribute(KohlsXMLLiterals.A_ORDERNO);	
		Element elemPaymentMethods = (Element) elemOrder.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHODS).item(0);
		NodeList nlPaymentMethod = elemPaymentMethods.getElementsByTagName(KohlsXMLLiterals.E_PAYMENT_METHOD);
		log.debug(">>>>>confirmRefundDistribution InXML>>>>"+XMLUtil.getXMLString(inXML));
		//get the object from the env
		Map<String, String> mapScvProcessedAmount=(Map<String, String>)env.getTxnObject(("GiftCardAmount"+strOrderNo));
		Element elemRefundPaymentMethods = (Element)elemOrder.getElementsByTagName("RefundPaymentMethods").item(0);
		NodeList nlRefundPaymentMethod = elemRefundPaymentMethods.getElementsByTagName("RefundPaymentMethod");
		
		
		yfsOutputXML = YFCDocument.createDocument("Order");
		YFCElement yfsOutputElement = yfsOutputXML.getDocumentElement();
		yfsOutputElement.setAttribute("DocumentType", "0001");
		yfsOutputElement.setAttribute("EnterpriseCode", "KOHLS.COM");
		yfsOutputElement.setAttribute("OrderNo", strOrderNo);
		YFCElement yfcRefundPaymentMethods = yfsOutputElement.createChild("RefundPaymentMethods");
		yfcRefundPaymentMethods.setAttribute("RefundAmountWithAssignments",elemRefundPaymentMethods.getAttribute("RefundAmountWithAssignments"));
		yfcRefundPaymentMethods.setAttribute("RefundAmountWithoutAssignments",elemRefundPaymentMethods.getAttribute("RefundAmountWithoutAssignments"));
		yfcRefundPaymentMethods.setAttribute("TotalAmountToBeRefunded",elemRefundPaymentMethods.getAttribute("TotalAmountToBeRefunded"));
		yfcRefundPaymentMethods.setAttribute("TotalTransferredOut",elemRefundPaymentMethods.getAttribute("TotalTransferredOut"));
		if(mapScvProcessedAmount!=null){			
			for(Entry<String, String> entry : mapScvProcessedAmount.entrySet())
		    {
					            
		           String SVCno=entry.getKey();
		           log.debug(">>>>>SVCno"+SVCno);
		           String amount=entry.getValue();
		           log.debug(">>>>>Amount"+amount);
		           //replace - from the amount
		           Double sProcessedAmount= Double.valueOf( amount.replaceFirst("-","" ));
		           for(int i=0;i<nlPaymentMethod.getLength();i++){
		            	DecimalFormat df= new DecimalFormat("#.##");
		            	Element elemPaymentMethod=(Element)nlPaymentMethod.item(i);
		            	String giftno=elemPaymentMethod.getAttribute("SvcNo");
		            	if(!giftno.equalsIgnoreCase(SVCno)){
			            		continue;
		            	}else
			            	{		
		            			//set the amount from the env to output XML
					            YFCElement yfcRefundPaymentMethod = yfcRefundPaymentMethods.createChild("RefundPaymentMethod");
								yfcRefundPaymentMethod.setAttribute("CheckNo",elemPaymentMethod.getAttribute("CheckNo"));
								yfcRefundPaymentMethod.setAttribute("CheckReference",elemPaymentMethod.getAttribute("CheckReference"));
								yfcRefundPaymentMethod.setAttribute("CreditCardExpDate",elemPaymentMethod.getAttribute("CreditCardExpDate"));
								yfcRefundPaymentMethod.setAttribute("CreditCardName",elemPaymentMethod.getAttribute("CreditCardName"));
								yfcRefundPaymentMethod.setAttribute("CreditCardNo",elemPaymentMethod.getAttribute("CreditCardNo"));
								yfcRefundPaymentMethod.setAttribute("CreditCardType",elemPaymentMethod.getAttribute("CreditCardType"));
								yfcRefundPaymentMethod.setAttribute("CustomerAccountNo",elemPaymentMethod.getAttribute("CustomerAccountNo"));
								yfcRefundPaymentMethod.setAttribute("CustomerPONo",elemPaymentMethod.getAttribute("CustomerPONo"));
								yfcRefundPaymentMethod.setAttribute("DisplayCreditCardNo",elemPaymentMethod.getAttribute("DisplayCreditCardNo"));
								yfcRefundPaymentMethod.setAttribute("DisplayCustomerAccountNo",elemPaymentMethod.getAttribute("DisplayCustomerAccountNo"));
								yfcRefundPaymentMethod.setAttribute("DisplayPaymentReference1",elemPaymentMethod.getAttribute("DisplayPaymentReference1"));
								yfcRefundPaymentMethod.setAttribute("DisplaySvcNo",elemPaymentMethod.getAttribute("DisplaySvcNo"));
								yfcRefundPaymentMethod.setAttribute("FundsAvailable",elemPaymentMethod.getAttribute("FundsAvailable"));
								yfcRefundPaymentMethod.setAttribute("GetFundsAvailableUserExitInvoked",elemPaymentMethod.getAttribute("GetFundsAvailableUserExitInvoked"));
								yfcRefundPaymentMethod.setAttribute("PaymentKey",elemPaymentMethod.getAttribute("PaymentKey"));
								yfcRefundPaymentMethod.setAttribute("PaymentReference1",elemPaymentMethod.getAttribute("PaymentReference1"));
								yfcRefundPaymentMethod.setAttribute("PaymentReference2",elemPaymentMethod.getAttribute("PaymentReference2"));
								yfcRefundPaymentMethod.setAttribute("PaymentReference3",elemPaymentMethod.getAttribute("PaymentReference3"));
								yfcRefundPaymentMethod.setAttribute("PaymentType",elemPaymentMethod.getAttribute("PaymentType"));
								yfcRefundPaymentMethod.setAttribute("RefundAmount",df.format(sProcessedAmount));
								yfcRefundPaymentMethod.setAttribute("SvcNo",elemPaymentMethod.getAttribute("SvcNo"));
								yfcRefundPaymentMethods.appendChild(yfcRefundPaymentMethod);
					        }
				      	}
				      }			
					            
				     
		}else{
			//if env map null, return input XML 
			for(int i=0;i<nlRefundPaymentMethod.getLength();i++){
				Element elemRefundPaymentMethod=(Element)nlRefundPaymentMethod.item(i);
				YFCElement yfcRefundPaymentMethod = yfcRefundPaymentMethods.createChild("RefundPaymentMethod");
				yfcRefundPaymentMethod.setAttribute("CheckNo",elemRefundPaymentMethod.getAttribute("CheckNo"));
				yfcRefundPaymentMethod.setAttribute("CheckReference",elemRefundPaymentMethod.getAttribute("CheckReference"));
				yfcRefundPaymentMethod.setAttribute("CreditCardExpDate",elemRefundPaymentMethod.getAttribute("CreditCardExpDate"));
				yfcRefundPaymentMethod.setAttribute("CreditCardName",elemRefundPaymentMethod.getAttribute("CreditCardName"));
				yfcRefundPaymentMethod.setAttribute("CreditCardNo",elemRefundPaymentMethod.getAttribute("CreditCardNo"));
				yfcRefundPaymentMethod.setAttribute("CreditCardType",elemRefundPaymentMethod.getAttribute("CreditCardType"));
				yfcRefundPaymentMethod.setAttribute("CustomerAccountNo",elemRefundPaymentMethod.getAttribute("CustomerAccountNo"));
				yfcRefundPaymentMethod.setAttribute("CustomerPONo",elemRefundPaymentMethod.getAttribute("CustomerPONo"));
				yfcRefundPaymentMethod.setAttribute("DisplayCreditCardNo",elemRefundPaymentMethod.getAttribute("DisplayCreditCardNo"));
				yfcRefundPaymentMethod.setAttribute("DisplayCustomerAccountNo",elemRefundPaymentMethod.getAttribute("DisplayCustomerAccountNo"));
				yfcRefundPaymentMethod.setAttribute("DisplayPaymentReference1",elemRefundPaymentMethod.getAttribute("DisplayPaymentReference1"));
				yfcRefundPaymentMethod.setAttribute("DisplaySvcNo",elemRefundPaymentMethod.getAttribute("DisplaySvcNo"));
				yfcRefundPaymentMethod.setAttribute("FundsAvailable",elemRefundPaymentMethod.getAttribute("FundsAvailable"));
				yfcRefundPaymentMethod.setAttribute("GetFundsAvailableUserExitInvoked",elemRefundPaymentMethod.getAttribute("GetFundsAvailableUserExitInvoked"));
				yfcRefundPaymentMethod.setAttribute("PaymentKey",elemRefundPaymentMethod.getAttribute("PaymentKey"));
				yfcRefundPaymentMethod.setAttribute("PaymentReference1",elemRefundPaymentMethod.getAttribute("PaymentReference1"));
				yfcRefundPaymentMethod.setAttribute("PaymentReference2",elemRefundPaymentMethod.getAttribute("PaymentReference2"));
				yfcRefundPaymentMethod.setAttribute("PaymentReference3",elemRefundPaymentMethod.getAttribute("PaymentReference3"));
				yfcRefundPaymentMethod.setAttribute("PaymentType",elemRefundPaymentMethod.getAttribute("PaymentType"));
				yfcRefundPaymentMethod.setAttribute("RefundAmount",elemRefundPaymentMethod.getAttribute("RefundAmount"));
				yfcRefundPaymentMethod.setAttribute("SvcNo",elemRefundPaymentMethod.getAttribute("SvcNo"));
				yfcRefundPaymentMethods.appendChild(yfcRefundPaymentMethod);
			}
			
		}
		
					yfsOutputElement.appendChild(yfcRefundPaymentMethods);
					
					log.debug(">>>> After confirmRefund Output XML>>>>"+XMLUtil.getXMLString(yfsOutputXML.getDocument()));
			return yfsOutputXML.getDocument();
		}

}