<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/im.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>


<table class="view" width="100%">
    <tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Overage_Allowed</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@AllowOverage"/></yfc:i18n>
		</td>

        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Manually_Entered</yfc:i18n>
		</td>
        <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@ManuallyEntered"/></yfc:i18n>
		</td>

        <td class="detaillabel" nowrap="true">
		    <yfc:i18n>Order_Available_On_System</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
			  <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@OrderAvailableOnSystem"/></yfc:i18n>
		</td>   

  </tr>
  <tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Case_Content_Verification_Not_Required</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@DoNotVerifyCaseContent"/></yfc:i18n>
		</td>

        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Pallet_Content_Verification_Not_Required</yfc:i18n>
		</td>
        <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@DoNotVerifyPalletContent"/></yfc:i18n>
		</td> 
		<td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Shipment_Entry_Overridden</yfc:i18n>
		</td>
        <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@OverrideManualShipmentEntry"/></yfc:i18n>
		</td>     
  </tr>
  <tr>
        <td class="detaillabel" nowrap="true" >
		    <yfc:i18n>Gift</yfc:i18n>
		</td>
		 <td class="protectedtext"  >
			 <yfc:i18n><yfc:getXMLValue  name="Shipment" binding="xml:/Shipment/@GiftFlag"/></yfc:i18n>
		</td>

        <td colspan="4">&nbsp;</td>     
  </tr>
</table>
