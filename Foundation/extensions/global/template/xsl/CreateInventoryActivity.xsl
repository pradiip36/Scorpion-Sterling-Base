<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >

<xsl:template match="/">

<InventoryActivity>

<xsl:attribute name="ItemID">
<xsl:value-of select="ItemList/Item/@ItemID"/>
</xsl:attribute>

<xsl:attribute name="OrganizationCode">
<xsl:value-of select="ItemList/Item/@OrganizationCode"/>
</xsl:attribute>

<xsl:attribute name="UnitOfMeasure">
<xsl:value-of select="ItemList/Item/@UnitOfMeasure"/>
</xsl:attribute>

<xsl:variable name="ProdClass" select="ItemList/Item/PrimaryInformation/@DefaultProductClass"/>

<xsl:attribute name="ProductClass">
   <xsl:choose>
			<xsl:when test="$ProdClass !='' ">
				  <xsl:value-of select="$ProdClass"/>
			</xsl:when>
			<xsl:otherwise>
					<xsl:value-of select="'Good'"/>				
			</xsl:otherwise>
	</xsl:choose>

</xsl:attribute>
	
</InventoryActivity>

</xsl:template>
</xsl:stylesheet>
