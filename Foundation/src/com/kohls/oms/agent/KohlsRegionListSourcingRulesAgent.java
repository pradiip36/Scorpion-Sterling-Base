package com.kohls.oms.agent;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.custom.util.xml.XMLUtil;
import com.kohls.common.util.KohlsConstant;
import com.kohls.common.util.KohlsXMLLiterals;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.log.YFCLogUtil;
import com.yantra.yfs.japi.YFSEnvironment;



    public class KohlsRegionListSourcingRulesAgent extends YCPBaseAgent{
	
	private  static final YFCLogCategory log = YFCLogCategory.instance(KohlsRegionListSourcingRulesAgent.class.getName());
	private  YIFApi api;
	
     /** Constructor Method  * */
		public KohlsRegionListSourcingRulesAgent() throws YIFClientCreationException {
	
			this.api = YIFClientFactory.getInstance().getLocalApi();
		}
	
   
/* Overriding the getJobs method from YFSBaseAgent Interface */		
	public List getJobs (YFSEnvironment env, Document inXML, Document lastMessage ) throws Exception { 
			
			ArrayList listSourcingRule = new ArrayList();
			if (YFCLogUtil.isDebugEnabled()) {
				log.debug("Input XML to getJobs method in KohlsRegionListSourcingRulesAgent start: " + XMLUtil.getXMLString(inXML));
		 }
		
				try {        	
						if (null != lastMessage) {
							return null;
		            }
		        
			Element eleRoot = inXML.getDocumentElement();
			String sOrganizationCode = eleRoot
								.getAttribute(KohlsXMLLiterals.A_ORGANIZATION_CODE);
			
			StringBuffer mergedSourcingRuleXML = new StringBuffer();
			mergedSourcingRuleXML.append("<SourcingRule> ");
	
			/* Appending getRegionList Api output to the StringBuffer variable */
		    Document outputXMLgetRegionList = getRegionList(env);
		    mergedSourcingRuleXML.append(XMLUtil.getXMLString(outputXMLgetRegionList));
				
			// Debug handler
				if (YFCLogUtil.isDebugEnabled()) {
							 log.debug(mergedSourcingRuleXML);
						 }	
				
				/* In the output XML of getSourcingRule , each SourcingRuleDetail Node will have different DistributionRuleId. Based on these DistributionRuleId's , we will 
				 *  call getDistributionList API and append the output XML to SourcingRuleDetail node . */
				// calling the private Method to fetch outputXML of getSourcingRuleList
			Document outputXMLgetSourcingRuleList= getSourcingRule(env);
				// call a private method to getDistributionlist for each and every DistributionRuleId 
			outputXMLgetSourcingRuleList = mergeDistributionRuleSourcingRuleList(env, outputXMLgetSourcingRuleList, sOrganizationCode);
				   
			    // Start of appending this XML with the String Buffer 
			 mergedSourcingRuleXML.append(XMLUtil.getXMLString(outputXMLgetSourcingRuleList));
			    // closing the mergedSourcingRuleXML
			 mergedSourcingRuleXML.append("</SourcingRule>");
			 Document docfinalSourcingRuleXML = XMLUtil.getDocument(mergedSourcingRuleXML.toString());
				  if (YFCLogUtil.isDebugEnabled()) {
					        log.debug("Final Output SourcingRule XML to be validated : " + XMLUtil.getXMLString(docfinalSourcingRuleXML));
				        }
			      //adding this XML to list
			  listSourcingRule.add(docfinalSourcingRuleXML);
			  mergedSourcingRuleXML=null;			       
					       
					}  catch (Exception ex) {
				        ex.printStackTrace();
				        throw ex;
					}		
			  return listSourcingRule;    	
	    }
    
    
    public void  executeJob(YFSEnvironment env , Document docSourcingRule)  throws Exception {  
    	
    		if (YFCLogUtil.isDebugEnabled()) {
    			log.debug("Input XML executeJob start: " + XMLUtil.getXMLString(docSourcingRule));
    				}	
		
    			try { 
    				this.api.executeFlow(env , KohlsConstant.SERVICE_KOHLS_SEND_REGION_SOURCING_RULE , docSourcingRule);
			
	    				}   catch (Exception e) {
					   		     e.printStackTrace();
					   		     throw e;
				               } 		
		}
  
    
    /* Private method to call getRegionList API and return RegionList output XML to the getJob Method */
    private Document getRegionList(YFSEnvironment env) throws Exception {
		    	    Document inputXMLgetRegionList = YFCDocument.parse("<Region />").getDocument();
			        
					if (YFCLogUtil.isDebugEnabled()) {
						log.debug("getRegionList Input XML" + XMLUtil.getXMLString(inputXMLgetRegionList));
					 }	
					Document outputXMLgetRegionList = this.api.getRegionList(env, inputXMLgetRegionList);
					return outputXMLgetRegionList;
		    	
		    }
	    
	/* Private method to call getSourcingRule API and return SourcingRules output XML to the getJob Method */
	private Document getSourcingRule(YFSEnvironment env) throws Exception {
				   /* Preparing the Input XML for getSourcingRule API	*/
				   Document inputXMLgetSourcingRuleXML = YFCDocument.parse("<SourcingRuleHeader OrganizationCode=' " +KohlsConstant.KOHLS_OrganizationCode+"' />").getDocument();
					
					   if (YFCLogUtil.isDebugEnabled()) {
								log.debug("getSourcingRule Input XML" + XMLUtil.getXMLString(inputXMLgetSourcingRuleXML));
							 }
	                //	 Calling the getSourcingRule API 
					Document outputXMLgetSourcingRuleList = this.api.getSourcingRuleList(env, inputXMLgetSourcingRuleXML);
				
						if (YFCLogUtil.isDebugEnabled()) {
							log.debug("getSourcingRule Output XML" + XMLUtil.getXMLString(outputXMLgetSourcingRuleList));
						 }
				    return outputXMLgetSourcingRuleList;
	 	    	
		    }


	private Document mergeDistributionRuleSourcingRuleList(YFSEnvironment env , Document inXMLSourcingRuleList, String sOrgCode)  throws Exception {
			    YFCDocument yfcXMLSourcingRuleList= YFCDocument.getDocumentFor(inXMLSourcingRuleList);	
		    	YFCElement eleoutXMLgetSourcingRuleList =   yfcXMLSourcingRuleList.getDocumentElement();
			    YFCNodeList nodeSourcingRuleDetail= eleoutXMLgetSourcingRuleList.getElementsByTagName("SourcingRuleDetail");
	   
			     if (0 != nodeSourcingRuleDetail.getLength()) {
							for (int i = 0; i < nodeSourcingRuleDetail.getLength(); i++) {
								YFCElement eleSourcingRuleDetail = eleoutXMLgetSourcingRuleList.getElementsByTagName("SourcingRuleDetail").item(i);
								String strDistributionRuleId=eleSourcingRuleDetail.getAttribute("DistributionRuleId");
								
							  // Using this DistributionRuleId , Input XML for getDistributionList API will be formed 
								Document inputXMLgetDistributionList = YFCDocument.parse("<ItemShipNode DistributionRuleId='"+ strDistributionRuleId+ "' OwnerKey='"+ sOrgCode+"' />").getDocument();
								if (YFCLogUtil.isDebugEnabled()) {
									log.debug("Input XML to getDistributionList : " + XMLUtil.getXMLString(inputXMLgetDistributionList));
								 }
								
							 // Calling the getDistributionList API to fetch ItemShipNodeList's and this will appended to the SourcingRuleDetail Node
								Document outputXMLgetDistributionList = this.api.getDistributionList(env, inputXMLgetDistributionList);
							 //System.out.println("Output XML of getDistributionList : " + XMLUtil.getXMLString(outputXMLgetDistributionList));
								if (YFCLogUtil.isDebugEnabled()) {
									log.debug("Output XML for getDistributionList API : " + XMLUtil.getXMLString(outputXMLgetDistributionList));
								 }
									String strXMLgetDistributionList=XMLUtil.getXMLString(outputXMLgetDistributionList);
							 
							 // append the outputXMLgetDistributionList to the Node SourcingRuleDetail						 
								eleSourcingRuleDetail.addXMLToNode(strXMLgetDistributionList);
							  }
			   }
			   
	                   // Appended output XML of SourcingRuleList with DistributionList Output
					       if (YFCLogUtil.isDebugEnabled()) {
						        log.debug("Appended XML SourcingRuleList : " + XMLUtil.getXMLString(inXMLSourcingRuleList));
							        }
								
						return inXMLSourcingRuleList;
					}
}



