<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<table class="view" width="100%">


    <tr>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n> </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Shipment/@EnterpriseCode"/></td>

		<td class="detaillabel"  > <yfc:i18n>Buyer</yfc:i18n> </td>
        <td class="protectedtext" > <yfc:getXMLValue binding="xml:/Shipment/@BuyerOrganizationCode" />	</td>
   

        <td class="detaillabel"  ><yfc:i18n>Seller</yfc:i18n> </td>
        <td class="protectedtext" > <yfc:getXMLValue binding="xml:/Shipment/@SellerOrganizationCode" />	</td>

    </tr>
	    
    <tr>
        <td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n> </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Shipment/@ShipmentNo" />	</td>

		<td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n> </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Shipment/@ReceivingNode" />	</td>

		<td class="detaillabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Shipment/@ShipNode" />	</td>
     </tr>


     <tr>

        <td class="detaillabel" ><yfc:i18n>BOL_#</yfc:i18n> </td>
        <td class="protectedtext" > <yfc:getXMLValue binding="xml:/Shipment/@BolNo" />	</td>

        <td class="detaillabel"  >	<yfc:i18n>Trailer_#</yfc:i18n> </td>
        <td class="protectedtext" > <yfc:getXMLValue binding="xml:/Shipment/@TrailerNo" />	</td>

		<td class="detaillabel"><yfc:i18n>Packslip_#</yfc:i18n> </td>
        <td class="protectedtext" >             
			<yfc:getXMLValue binding="xml:/Shipment/@PickticketNo" />   </td>
			<input type="hidden" <%=getTextOptions( "xml:/Shipment/@PickticketNo", "xml:/Shipment/@PickticketNo")%>/>
		</td>


    </tr>
	 

</table>
