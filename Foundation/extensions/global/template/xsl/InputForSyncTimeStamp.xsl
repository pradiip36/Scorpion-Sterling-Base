<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	
	<xsl:template match="EOF"> 

   <KOHLSInvSyncTimeStamp>      
	<xsl:attribute name="ReasonText">
		<xsl:value-of select="@ReasonText" />
	</xsl:attribute>
	<xsl:attribute name="ShipNode">
		<xsl:value-of select="@ShipNode" />
	</xsl:attribute>
	<xsl:attribute name="TransactionNumber">
		<xsl:value-of select="@TransactionNumber" />
	</xsl:attribute>	
	</KOHLSInvSyncTimeStamp>  
</xsl:template>	

</xsl:stylesheet>