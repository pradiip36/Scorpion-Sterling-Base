<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	
	<xsl:template match="/">  
		<xsl:for-each select="/Shipment">			

	<xsl:choose>
		<xsl:when test="normalize-space(./PickTicketCancellation/ShipmentLine/@*) and normalize-space(./ShipmentLines/ShipmentLine/@*)" >	
			<MergeDoc IschangeReleaseReq="Y">	
	<Shipment>		
		<xsl:copy-of select="./@*"/>   
	<Containers>
		<xsl:copy-of select="./Containers/*"/>
	</Containers>
	<ShipmentLines>
		<xsl:copy-of select="./ShipmentLines/*"/>
	</ShipmentLines>	
	<Extn>		
		<xsl:copy-of select="./Extn/@*"/>		
		</Extn>
		<xsl:choose>
	         			<xsl:when test="normalize-space(./ToAddress/@*)"> 
		<ToAddress>
		
		<xsl:copy-of select="./ToAddress/@*"/>
		
		</ToAddress>	
		</xsl:when>
	         			</xsl:choose>				
	</Shipment>
	
	<OrderRelease Action="MODIFY">	
		<xsl:attribute name="DocumentType">
			<xsl:value-of select="@DocumentType"/>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="@EnterpriseCode"/>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./PickTicketCancellation/ShipmentLine/@OrderNo"/>
		</xsl:attribute>
		<xsl:attribute name="ShipNode">
			<xsl:value-of select="@ShipNode"/>
		</xsl:attribute>
		<xsl:attribute name="ReleaseNo">
			<xsl:value-of select="@ReleaseNo"/>
		</xsl:attribute>
		   	<OrderLines>
         			
         		<xsl:for-each select="./PickTicketCancellation/ShipmentLine">
         			<OrderLine Action="BACKORDER">
         			
         			<xsl:attribute name="PrimeLineNo">
            			<xsl:value-of select="@PrimeLineNo"/>
         			</xsl:attribute>
         			
         			<xsl:attribute name="ChangeInQuantity">
            			<xsl:text disable-output-escaping = "yes" >-</xsl:text><xsl:value-of select="@Quantity"/>
         			</xsl:attribute>
         			
         			<xsl:attribute name="SubLineNo">
            			<xsl:value-of select="@SubLineNo"/>
         			</xsl:attribute>
         			
         				<Item>
         					<xsl:attribute name="ItemID">
            					<xsl:value-of select="@ItemID"/>
         					</xsl:attribute>
         					<xsl:attribute name="ProductClass">
            					<xsl:value-of select="@ProductClass"/>
         					</xsl:attribute>
         					<xsl:attribute name="UnitOfMeasure">
            					<xsl:value-of select="@UnitOfMeasure"/>
         					</xsl:attribute>
         				</Item>         			
         			</OrderLine>         			
         		</xsl:for-each>         			
         	</OrderLines>
         	<Extn>		
		<xsl:copy-of select="./Extn/@*"/>		
		</Extn>         			
       </OrderRelease>
	
	</MergeDoc>
	</xsl:when>
	
	
	
	<xsl:when test="normalize-space(./PickTicketCancellation/ShipmentLine/@*)" >
		
	
			<MergeDoc IschangeReleaseReq="Y">	
	
	
	<OrderRelease Action="MODIFY">	
		<xsl:attribute name="DocumentType">
			<xsl:value-of select="@DocumentType"/>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="@EnterpriseCode"/>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./PickTicketCancellation/ShipmentLine/@OrderNo"/>
		</xsl:attribute>
		<xsl:attribute name="ShipNode">
			<xsl:value-of select="@ShipNode"/>
		</xsl:attribute>
		<xsl:attribute name="ReleaseNo">
			<xsl:value-of select="@ReleaseNo"/>
		</xsl:attribute>
		   	<OrderLines>
         			
         		<xsl:for-each select="./PickTicketCancellation/ShipmentLine">
         			<OrderLine Action="BACKORDER">
         			
         			<xsl:attribute name="PrimeLineNo">
            			<xsl:value-of select="@PrimeLineNo"/>
         			</xsl:attribute>
         			
         			<xsl:attribute name="ChangeInQuantity">
            			<xsl:text disable-output-escaping = "yes" >-</xsl:text><xsl:value-of select="@Quantity"/>
         			</xsl:attribute>
         			
         			<xsl:attribute name="SubLineNo">
            			<xsl:value-of select="@SubLineNo"/>
         			</xsl:attribute>
         			
         				<Item>
         					<xsl:attribute name="ItemID">
            					<xsl:value-of select="@ItemID"/>
         					</xsl:attribute>
         					<xsl:attribute name="ProductClass">
            					<xsl:value-of select="@ProductClass"/>
         					</xsl:attribute>
         					<xsl:attribute name="UnitOfMeasure">
            					<xsl:value-of select="@UnitOfMeasure"/>
         					</xsl:attribute>
         				</Item>         			
         			</OrderLine>         			
         		</xsl:for-each>         			
         	</OrderLines>
         	<Extn>		
		<xsl:copy-of select="./Extn/@*"/>		
		</Extn>         			
       </OrderRelease>
	
	</MergeDoc>
	</xsl:when>
	
  
	<xsl:otherwise>
	<MergeDoc IschangeReleaseReq="N">
	
	<Shipment>
		
		<xsl:copy-of select="./@*"/>
 	<Containers>
		<xsl:copy-of select="./Containers/*"/>
	</Containers>
	<ShipmentLines>
		<xsl:copy-of select="./ShipmentLines/*"/>
	</ShipmentLines>
		
	         			<Extn>
		
		<xsl:copy-of select="./Extn/@*"/>
		
		</Extn>	         			
	
	<xsl:choose>
	         			<xsl:when test="normalize-space(./ToAddress/@*)"> 
		<ToAddress>
		
		<xsl:copy-of select="./ToAddress/@*"/>
		
		</ToAddress>	
		</xsl:when>
	         			</xsl:choose>
	
	</Shipment>
	 </MergeDoc>
	</xsl:otherwise>
</xsl:choose>


      	</xsl:for-each>
	
</xsl:template>
</xsl:stylesheet><!-- Stylus Studio meta-information - (c)1998-2004. Sonic Software Corporation. All rights reserved.
<metaInformation>
<scenarios ><scenario default="yes" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\..\..\..\..\ShipConfirmIP.xml" htmlbaseurl="" outputurl="" processortype="internal" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/><scenario default="no" name="Scenario1" userelativepaths="yes" externalpreview="no" url="..\..\..\..\..\..\..\ShipConfirmIP.xml" htmlbaseurl="" outputurl="" processortype="internal" profilemode="0" profiledepth="" profilelength="" urlprofilexml="" commandline="" additionalpath="" additionalclasspath="" postprocessortype="none" postprocesscommandline="" postprocessadditionalpath="" postprocessgeneratedext=""/></scenarios><MapperMetaTag><MapperInfo srcSchemaPathIsRelative="yes" srcSchemaInterpretAsXML="no" destSchemaPath="" destSchemaRoot="" destSchemaPathIsRelative="yes" destSchemaInterpretAsXML="no"/><MapperBlockPosition></MapperBlockPosition></MapperMetaTag>
</metaInformation>
-->