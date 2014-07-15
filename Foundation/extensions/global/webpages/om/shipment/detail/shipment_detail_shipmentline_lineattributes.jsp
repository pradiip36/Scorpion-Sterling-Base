<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table width="100%" class="view">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Segment_Type</yfc:i18n>
    </td>
	<% 
	  if(!isVoid(resolveValue("xml:/ShipmentLines/ShipmentLine/@SegmentType"))){	
	   YFCElement commCodeElem = YFCDocument.createDocument("CommonCode").getDocumentElement();
	   commCodeElem.setAttribute("CodeType","SEGMENT_TYPE");
	   commCodeElem.setAttribute("CodeValue",resolveValue("xml:/ShipmentLines/ShipmentLine/@SegmentType"));
	   YFCElement templateElem = YFCDocument.parse("<CommonCode CodeName=\"\" CodeShortDescription=\"\" CodeType=\"\" CodeValue=\"\" CommonCodeKey=\"\" />").getDocumentElement();
	%>
		<yfc:callAPI apiName="getCommonCodeList" inputElement="<%=commCodeElem%>" 
													templateElement="<%=templateElem%>" outputNamespace=""/>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB  binding="xml:/CommonCodeList/CommonCode/@CodeShortDescription"/>
		</td>
	<% } else { %>
		<td class="tablecolumn">&nbsp;</td>
	<% } %>
    <td class="detaillabel" >
        <yfc:i18n>Segment</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@Segment"/>
	</td>

	<td class="detaillabel">
		<yfc:i18n>COO</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@CountryOfOrigin"/>
	</td>

</tr>
<tr>
	<td class="detaillabel">
		<yfc:i18n>FIFO_#</yfc:i18n>
	</td>
	<td class="protectedtext">
		<%=(int)getNumericValue("xml:/ShipmentLines/ShipmentLine/@FifoNo")%>
	</td>
	<td class="detaillabel" >
        <yfc:i18n>Net_Weight</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/ShipmentLines/ShipmentLine/@NetWeight"/>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Net_Weight_UOM</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@NetWeightUom"/>
	</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>WaveNo</yfc:i18n>
    </td>

	<td class="protectedtext" >
		<yfc:makeXMLInput name="WaveKey" >
            <yfc:makeXMLKey binding="xml:/WaveSummary/WaveList/Wave/@WaveNo" value="xml:/ShipmentLines/ShipmentLine/@WaveNo" />
            <yfc:makeXMLKey binding="xml:/WaveSummary/@Node" value="xml:/Shipment/@ShipNode" />

        </yfc:makeXMLInput>
		<a <%=getDetailHrefOptions("L01",getParameter("WaveKey"),"")%> >
			<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@WaveNo"/>
		</a>
	</td>
	
	<td class="detaillabel" >
        <yfc:i18n>Customer_PO_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@CustomerPoNo"/>
	</td>

	<td class="detaillabel" >
        <yfc:i18n>Department_Code</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@DepartmentCode"/>
	</td>
 </tr>
 <tr>
   <td class="detaillabel" >
        <yfc:i18n>Buyer_Mark_For_Node</yfc:i18n>
   </td>
   <td class="protectedtext">
		<yfc:getXMLValue binding="xml:/ShipmentLines/ShipmentLine/@BuyerMarkForNodeId"/>
   </td>
   <td></td>
   <td></td>
</tr>
</table>
