<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite - Foundation
(C) Copyright IBM Corp. 2005, 2012 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 *******************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript">
    var myObject = new Object();
    myObject = dialogArguments;
    var parentWindow = myObject.currentWindow;

    function setClickedAttribute() {

        var retVal = new Object();
        retVal["xml:/Shipment/Tmp/ScacAndService/@ScacAndServiceKey"] = document.all["xml:/Shipment/Tmp/ScacAndService/@ScacAndServiceKey"].value;

		retVal["xml:/Shipment/Tmp/@ShipmentSortLocationId"] = document.all["xml:/Shipment/Tmp/@ShipmentSortLocationId"].value;
        retVal["xml:/Shipment/Tmp/@CarrierSortLocationId"] = document.all["xml:/Shipment/Tmp/@CarrierSortLocationId"].value;
		
		var expShipDate=document.all["xml:/Shipment/Tmp/@ExpectedShipmentDate_YFCDATE"].value+' '+document.all["xml:/Shipment/Tmp/@ExpectedShipmentDate_YFCTIME"].value;
		retVal["xml:/Shipment/Tmp/@ExpectedShipmentDate"] = expShipDate;

        window.dialogArguments["OMReturnValue"] = retVal;
        window.dialogArguments["OKClicked"] = "YES";
        window.close();
    }
</script>

<table width="100%" class="view">
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Carrier_Service</yfc:i18n> 
        </td>
        <td>
            <select name="xml:/Shipment/Tmp/ScacAndService/@ScacAndServiceKey" class="combobox">
				<yfc:loopOptions binding="xml:ScacAndServiceList:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" isLocalized="Y"/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Expected_Ship_Date</yfc:i18n>
        </td>
		<td nowrap="true">
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/Tmp/@ExpectedShipmentDate_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Shipment/Tmp/@ExpectedShipmentDate_YFCTIME")%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" 
			<%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %> />
		</td>
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Shipment_Sort_Lane</yfc:i18n>
        </td>
		<td nowrap="true">
		   <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Tmp/@ShipmentSortLocationId")%> />
		   <img class="lookupicon" onclick="callLookup(this,'location',' ')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		</td>
    </tr>
	<tr>
        <td class="detaillabel">
            <yfc:i18n>Carrier_Sort_Lane</yfc:i18n>
        </td>
		<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/Tmp/@CarrierSortLocationId")%> />
		   <img class="lookupicon" onclick="callLookup(this,'location',' ')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Location") %> />
		</td>
    </tr>

    <tr>
        <td></td>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setClickedAttribute();return true;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
    <tr>
</table>