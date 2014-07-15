<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	
	<xsl:template match="/">  
		<xsl:for-each select="/MergeDoc">
	<xsl:choose>
		<xsl:when test="normalize-space(./Shipment/@*)" >			
	<Order>	
		<xsl:attribute name="DocumentType">
			<xsl:value-of select="./Shipment/@DocumentType"/>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="./Shipment/@EnterpriseCode"/>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./Shipment/ShipmentLines/ShipmentLine/@OrderNo"/>
		</xsl:attribute>
	</Order>			
	</xsl:when>		
	<xsl:when test="normalize-space(./OrderRelease/@*)" >	
			<Order>	
		<xsl:attribute name="DocumentType">
			<xsl:value-of select="./OrderRelease/@DocumentType"/>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:value-of select="./OrderRelease/@EnterpriseCode"/>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./OrderRelease/@OrderNo"/>
		</xsl:attribute>
	</Order>			
	</xsl:when>
</xsl:choose>
</xsl:for-each>	
</xsl:template>
</xsl:stylesheet>