<%@ page import="com.yantra.yfc.dom.YFCElement" %>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">
	function setEnableReasonText(obj, sReasonID)	{
		var oReasonID = document.all(sReasonID);
		oReasonID.disabled=(obj.value == '');
		if(obj.value == '')	{
			oReasonID.value='';
		}
	}

</script>

<%
	String sComboBinding = "xml:/AvailableHolds/@HoldType";

	String sAllowModElementName = "xml:/Shipment/AllowedModifications";
	String sOverride = "xml:/Shipment/@OverrideModificationRules";
	String sDefaultTemplateRootName = "Shipment";
	String sDefaultTemplateHoldTypeNodeName = "ShipmentHoldTypes";
	String sComboName = "xml:/Shipment/ShipmentHoldTypes/ShipmentHoldType/@HoldType";
	String sReasonTextBinding = "xml:/Shipment/ShipmentHoldTypes/ShipmentHoldType/@ReasonText";

	

	YFCElement eAdditionalHolds = formAdditionalHoldsXML(getElement(sDefaultTemplateRootName).getChildElement(sDefaultTemplateHoldTypeNodeName), getElement("HoldTypeList"), getElement("HoldTypeCommonCodeList"));


	request.setAttribute("AvailableHolds", eAdditionalHolds);
%>

<table class="view" cellspacing="0" width="100%" ID="specialChange">

        <tr>
            <td class="detaillabel" ><yfc:i18n>Hold_Type</yfc:i18n></td>
			<td   >
	            <input type="hidden" name='<%=sOverride%>' value="" />
				
				<select name="<%=sComboName%>" class="combobox" OldValue="" <%=getModTypeComboOptions(sComboName, sAllowModElementName)%> onChange="setEnableReasonText(this, '<%=sReasonTextBinding%>');" >
					<yfc:loopOptions binding="<%=sComboBinding%>" name="HoldTypeDescription"
					value="HoldType" selected=" " isLocalized="Y" />
				</select>
			</td>
			<td></td>
        </tr>

		<tr>
            <td class="detaillabel" ><yfc:i18n>Reason</yfc:i18n></td>

			<td  >
				<textarea rows="2" disabled cols="50" <%=getModTypeTextAreaOptions(sReasonTextBinding, sAllowModElementName, "HOLD_TYPE")%> <%=getTextAreaOptions(sReasonTextBinding )%>></textarea>
			</td>
			<td></td>
		</tr>

</table>