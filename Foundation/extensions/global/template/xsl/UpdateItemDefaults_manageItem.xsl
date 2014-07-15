<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">     
   <xsl:output indent="yes" />
   <xsl:template match="/">
    <ItemList>
   <Item>
  	<xsl:attribute name="ItemID">
  		<xsl:value-of select="./Item/@ItemID" /> 
  	</xsl:attribute>
  		<xsl:attribute name="OrganizationCode">
  		<xsl:value-of select="./Item/@OrganizationCode" /> 
  	</xsl:attribute>
  		<xsl:attribute name="UnitOfMeasure">
  		<xsl:value-of select="./Item/@UnitOfMeasure" /> 
  	</xsl:attribute>
	<InventoryParameters>
	 <xsl:attribute name="OnhandSafetyFactorQuantity">
  		<xsl:value-of select="./Item/InventoryParameters/@OnhandSafetyFactorQuantity" /> 
  	</xsl:attribute>
	</InventoryParameters>
	<Extn>
	 <xsl:attribute name="KohlsEligibleBulkMarkReq">
  		<xsl:value-of select="./Item/Extn/@KohlsEligibleBulkMarkReq" /> 
  	</xsl:attribute>
  	<xsl:attribute name="ExtnShipNodeSource">
  		<xsl:value-of select="./Item/Extn/@ExtnShipNodeSource" /> 
  	</xsl:attribute>
  		<xsl:attribute name="KohlsSafetyFactorReq">
  		<xsl:value-of select="./Item/Extn/@KohlsSafetyFactorReq" /> 
  	</xsl:attribute>
  	</Extn>
</Item>      
       </ItemList>
   </xsl:template>
</xsl:stylesheet>

