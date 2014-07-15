<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
	
	<xsl:template match="/">  	
	<xsl:choose>
		<xsl:when test="normalize-space(./Shipment/CancelLines/@*)" >			
	<Order>	
		<xsl:attribute name="DocumentType">
			<xsl:text disable-output-escaping = "yes" >0005</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:text disable-output-escaping = "yes" >KOHLS.COM</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./Shipment/CancelLines/@PONumber"/>
		</xsl:attribute>
	</Order>			
	</xsl:when>
	<xsl:when test="normalize-space(./CancelLines/@*)" >			
	<Order>	
		<xsl:attribute name="DocumentType">
			<xsl:text disable-output-escaping = "yes" >0005</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:text disable-output-escaping = "yes" >KOHLS.COM</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./CancelLines/@PONumber"/>
		</xsl:attribute>
	</Order>			
	</xsl:when>		
	<xsl:when test="normalize-space(./Shipment/ShipmentLines/ShipmentLine/@*)" >	
			<Order>	
		<xsl:attribute name="DocumentType">
			<xsl:text disable-output-escaping = "yes" >0005</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="EnterpriseCode">
			<xsl:text disable-output-escaping = "yes" >KOHLS.COM</xsl:text>
		</xsl:attribute>
		<xsl:attribute name="OrderNo">
			<xsl:value-of select="./Shipment/ShipmentLines/ShipmentLine/@OrderNo"/>
		</xsl:attribute>
	</Order>			
	</xsl:when>
</xsl:choose>
</xsl:template>
</xsl:stylesheet>