<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/supervisorypanelpopup.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreasonpopup.js"></script>
<%  String destNode ="";
	Integer iCounter=null;
%>
<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td class="tablecolumnheader"><yfc:i18n>Shipment_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Carrier</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Service</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Pro_#</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Expected_Ship_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Actual_Ship_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Expected_Delivery_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Actual_Delivery_Date</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Origin</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Destination</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Mode</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/Shipments/@Shipment" id="Shipment">
            <tr>
                <yfc:makeXMLInput name="shipmentKey">
                    <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
                    <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentNo" value="xml:/Shipment/@ShipmentNo" />
                    <yfc:makeXMLKey binding="xml:/Shipment/@EnterpriseCode" value="xml:/Shipment/@EnterpriseCode" />
                </yfc:makeXMLInput>
                <td class="tablecolumn"><a href="javascript:showPopupDetailFor('<%=getParameter("shipmentKey")%>','poshipment','920','600',' ');">
                    <yfc:getXMLValue binding="xml:/Shipment/@ShipmentNo"/></a>
                </td>
				<td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Shipment/@SCAC"/>
                </td>
				<td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Shipment/@CarrierServiceCode"/>
                </td>
				<td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Shipment/@ProNo"/>
                </td>

                <td class="tablecolumn">
                    <yfc:getXMLValueI18NDB binding="xml:/Shipment/Status/@Description"/>
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:/Shipment/@ExpectedShipmentDate")%>">
							<yfc:getXMLValue binding="xml:/Shipment/@ExpectedShipmentDate"/>
				</td>
				<td class="tablecolumn"  sortValue="<%=getDateValue("xml:/Shipment/@ActualShipmentDate")%>">
							<yfc:getXMLValue binding="xml:/Shipment/@ActualShipmentDate"/>
				</td>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/Shipment/@ExpectedDeliveryDate"/>
				</td>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/Shipment/@ActualDeliveryDate"/>
				</td>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/Shipment/@ShipNode"/>
				</td>
				<td class="tablecolumn">
				<%	destNode = getValue("Shipment","xml:/Shipment/@ReceivingNode");
					if (!isVoid(destNode)) { %>
						<yfc:getXMLValue binding="xml:/Shipment/@ReceivingNode"/>
					<%} else { %>
						<yfc:getXMLValue binding="xml:/Shipment/ToAddress/@City"/>&nbsp; 
						<yfc:getXMLValue binding="xml:/Shipment/ToAddress/@State"/> &nbsp; 
						<yfc:getXMLValue binding="xml:/Shipment/ToAddress/@Country"/>
					<%}%>
				</td>
				<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/Shipment/@ShipMode"/>
				</td>
            </tr>
			<%iCounter=ShipmentCounter;%>
        </yfc:loopXML> 
</tbody>
<tfoot>
<tr>
		<td/><td/><td/><td/><td/>
		<td align="right">
            <input type="button" class="button" value='<%=getI18N("Create_Shipment")%>' onclick='myObject.temp=true;window.close();'/>
		</td>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        </td>
		<td/><td/><td/>
		<td/>
		<td/>
</tr>
</tfoot>
</table>

