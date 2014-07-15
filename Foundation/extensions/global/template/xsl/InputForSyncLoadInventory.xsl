<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	
	<xsl:template match="EOF"> 

   <Inventory>   
   <xsl:attribute name="ApplyDifferences">          
          <xsl:text disable-output-escaping = "yes" >Y</xsl:text>  			
    </xsl:attribute> 
    <xsl:attribute name="CompleteInventoryFlag">
            <xsl:text disable-output-escaping = "yes" >Y</xsl:text>			
    </xsl:attribute>     
	<xsl:attribute name="ReasonCode">
		<xsl:value-of select="@ReasonCode" />
	</xsl:attribute>
	<xsl:attribute name="ReasonText">
		<xsl:value-of select="@ReasonText" />
	</xsl:attribute>
	<xsl:attribute name="ShipNode">
		<xsl:value-of select="@ShipNode" />
	</xsl:attribute>
	<xsl:attribute name="YantraMessageGroupID">
		<xsl:value-of select="@YantraMessageGroupID" />
	</xsl:attribute>
	</Inventory>  
</xsl:template>	

</xsl:stylesheet>