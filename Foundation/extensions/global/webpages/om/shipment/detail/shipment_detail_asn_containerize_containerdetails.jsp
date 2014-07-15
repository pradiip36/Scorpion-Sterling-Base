<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<table class="view" width="100%">
	<tr>
		<td>
			<table class="view" width="100%">
			<tr>
				<td class="detaillabel"   >
					<yfc:i18n>Container_Type</yfc:i18n> 
				</td>
				<td class="searchcriteriacell"  >
				   <input type="radio" class="radiobutton"  <%=getRadioOptions("xml:/Shipment/ContainerDetails/ContainerDetail/@ContainerType","Carton")%>/>
                   <yfc:i18n>Carton</yfc:i18n>

				</td>

				<td class="searchcriteriacell"  >
				   <input type="radio" class="radiobutton" <%=getRadioOptions("xml:/Shipment/ContainerDetails/ContainerDetail/@ContainerType")%>/>
                   <yfc:i18n>Pallet</yfc:i18n>

				</td>
			</tr>

            <tr> 

				<td class="detaillabel"  > 
					<yfc:i18n>Tracking_#</yfc:i18n> 
				</td>
				<td class="searchcriteriacell" colspan="2" >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@TrackingNo")%> />
				</td>
			</tr>
			<tr>

				<td class="detaillabel"  > 
					<yfc:i18n>Weight</yfc:i18n> 
				</td>
				<td class="searchcriteriacell"  >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@Weight")%> />
				</td>
				<td class="searchcriteriacell"  >
					<select name="xml:/Shipment/ContainerDetails/ContainerDetail/Container/@WeightUOM" class="combobox" >
						<yfc:loopOptions binding="xml:/Shipment/ContainerDetails/ContainerDetail/@Container" 
		                name="WeightUOM" value="WeightUOM" selected="xml:/Shipment/ContainerDetails/ContainerDetail/@Container"/>
					</select>
		
				</td>
			</tr>
			<tr>

				<td class="detaillabel"  > 
					<yfc:i18n>Width</yfc:i18n> 
				</td>
				<td class="searchcriteriacell"  >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@Width")%> />
				</td>
				<td class="searchcriteriacell"  >
					<select name="xml:/Shipment/ContainerDetails/ContainerDetail/Container/@WidthUOM" class="combobox" >
						<yfc:loopOptions binding="xml:/Shipment/ContainerDetails/ContainerDetail/@Container" 
		                name="WidthUOM" value="WidthUOM" selected="xml:/Shipment/ContainerDetails/ContainerDetail/@Container"/>
					</select>
		
				</td>


			</tr>

            </table>		
		</td>
		<td>
			<table class="view" width="100%">
			<tr>
				<td class="detaillabel"   >
					<yfc:i18n>Container_SCM</yfc:i18n> 
				</td>
				<td class="searchcriteriacell" colspan="2" >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@ContainerSCM" )%> />
				</td>

			
			</tr>

            <tr> 

				<td class="detaillabel"  > 
					<yfc:i18n>Declared_Insurance_Value</yfc:i18n> 
				</td>
				<td class="detaillabel"  > 
					<yfc:i18n>$</yfc:i18n> 					
				</td>

				<td class="searchcriteriacell">
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@DeclaredValue" )%> />
				</td>
			</tr>
			<tr>

				<td class="detaillabel"  > 
					<yfc:i18n>Height</yfc:i18n> 
				</td>
				<td class="searchcriteriacell"  >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@Height")%> />
				</td>
				<td class="searchcriteriacell"  >
					<select name="xml:/Shipment/ContainerDetails/ContainerDetail/Container/@HeightUOM" class="combobox" >
						<yfc:loopOptions binding="xml:/Shipment/ContainerDetails/ContainerDetail/@Container" 
		                name="HeightUOM" value="HeightUOM" selected="xml:/Shipment/ContainerDetails/ContainerDetail/@Container"/>
					</select>
		
				</td>
			</tr>
			<tr>

				<td class="detaillabel"  > 
					<yfc:i18n>Length</yfc:i18n> 
				</td>
				<td class="searchcriteriacell"  >
					<input type="text" class="unprotectedinput"  <%=getTextOptions("xml:/Shipment/ContainerDetails/ContainerDetail/Container/@Length")%> />
				</td>
				<td class="searchcriteriacell"  >
					<select name="xml:/Shipment/ContainerDetails/ContainerDetail/Container/@LengthUOM" class="combobox" >
						<yfc:loopOptions binding="xml:/Shipment/ContainerDetails/ContainerDetail/@Container" 
		                name="LengthUOM" value="LengthUOM" selected="xml:/Shipment/ContainerDetails/ContainerDetail/@Container"/>
					</select>
		
				</td>


			</tr>

           </table>		
		</td>

	 </tr>

</table>