<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	<xsl:template match="/">
   <Mismatches>
			<xsl:for-each select="/Item">
				<Items>
         			<xsl:attribute name="ShipNode">
            			<xsl:apply-templates select="@ShipNode"/>
         			</xsl:attribute>
         			<xsl:attribute name="Date">
            			<xsl:apply-templates select="@ReasonText"/>
         			</xsl:attribute>
         			<xsl:attribute name="ItemID">
            			<xsl:apply-templates select="@ItemID"/>         		
         			</xsl:attribute>
         			<xsl:attribute name="ActualQuantity">
            			<xsl:apply-templates select="./Supplies/Supply/@ActualQuantity"/>
         			</xsl:attribute>
         			<xsl:attribute name="ChangedQuantity">
            		<xsl:apply-templates select="./Supplies/Supply/@ChangedQuantity"/>
         			</xsl:attribute>
         			<xsl:attribute name="ExpectedQuantity">
            			<xsl:apply-templates select="./Supplies/Supply/@ExpectedQuantity"/>
         			</xsl:attribute>         		
			</Items>
      	</xsl:for-each>
	</Mismatches>
</xsl:template>
</xsl:stylesheet>