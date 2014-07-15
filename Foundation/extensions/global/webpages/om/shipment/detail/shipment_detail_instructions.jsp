<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>

<table class="table" width="100%" cellspacing="0" <%if (isModificationAllowed("xml:/@AddInstruction","xml:/Shipment/AllowedModifications")) {%> initialRows="1" <%}%>>
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
		<td class="tablecolumnheader" sortable="no"><yfc:i18n>Instruction_Type</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Text</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/Shipment/Instructions/@Instruction" id="Instruction">
        <tr>
        <yfc:makeXMLInput name="InstructionKey">
            <yfc:makeXMLKey binding="xml:/Instruction/@InstructionDetailKey" value="xml:/Instruction/@InstructionDetailKey" />
            <yfc:makeXMLKey binding="xml:/Instruction/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
        </yfc:makeXMLInput>
        <td class="checkboxcolumn">
            <input type="checkbox" value='<%=getParameter("InstructionKey")%>' name="chkEntityKey"/>
		</td>
            <td class="tablecolumn">
                <% String instTypeBinding = "xml:/Shipment/Instructions/Instruction_" + InstructionCounter + "/@InstructionType"; %>
                <select <%=yfsGetComboOptions(instTypeBinding, "xml:/Instruction/@InstructionType","xml:/Shipment/AllowedModifications")%>>
                    <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Instruction/@InstructionType" isLocalized="Y" targetBinding="<%=instTypeBinding%>"/>
                </select>
            </td>
            <td class="tablecolumn">
                <table class="view" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <textarea rows="3" cols="100" <%=yfsGetTextAreaOptions("xml:/Shipment/Instructions/Instruction_" + InstructionCounter + "/@InstructionText","xml:/Instruction/@InstructionText", "xml:/Shipment/AllowedModifications")%>><yfc:getXMLValue binding="xml:/Instruction/@InstructionText"/></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>
							<img align="absmiddle" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTION_URL, "Instruction_URL")%>/>
							<input type="text" <%=yfsGetTextOptions("xml:/Shipment/Instructions/Instruction_" + InstructionCounter + "/@InstructionURL", "xml:/Instruction/@InstructionURL","xml:/Shipment/AllowedModifications")%>/>
                            <input type="button" class="button" value="GO" onclick="javascript:goToURL('xml:/Shipment/Instructions/Instruction_<%=InstructionCounter%>/@InstructionURL');"/>
                        </td>
                        <td>
                            <input type="hidden" <%=getTextOptions("xml:/Shipment/Instructions/Instruction_" + InstructionCounter + "/@InstructionDetailKey", "xml:/Instruction/@InstructionDetailKey")%>/>
							<input type="hidden" <%=getTextOptions("xml:/Shipment/Instructions/Instruction_" + InstructionCounter + "/@Action", "Modify")%>/>
                        </td>
                    </tr>
                     <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="tablecolumn">
			<select <%=yfsGetTemplateRowOptions("xml:/Shipment/Instructions/Instruction_/@InstructionType", "xml:/Shipment/AllowedModifications", "ADD_INSTRUCTION", "combo")%>>
                <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="tablecolumn">
            <table class="view" cellspacing="0" cellpadding="0">
                <td>
                    <textarea rows="3" cols="100" <%=yfsGetTemplateRowOptions("xml:/Shipment/Instructions/Instruction_/@InstructionText", "xml:/Shipment/AllowedModifications", "ADD_INSTRUCTION", "textarea")%>></textarea>
                </td>
                <tr>
                    <td>
						<img align="absmiddle" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTION_URL, "Instruction_URL")%>/>
						<input type="text" <%=yfsGetTemplateRowOptions("xml:/Shipment/Instructions/Instruction_/@InstructionURL", "xml:/Shipment/AllowedModifications", "ADD_INSTRUCTION", "text")%>/> 
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
    <%if (isModificationAllowed("xml:/@AddInstruction","xml:/Shipment/AllowedModifications")) {%> 
    <tr>
    	<td nowrap="true" colspan="3">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>