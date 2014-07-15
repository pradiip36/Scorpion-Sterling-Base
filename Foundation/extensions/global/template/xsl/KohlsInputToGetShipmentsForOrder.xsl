<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" />

<xsl:template match="/">
<Order>
	
	      <xsl:attribute  name = "OrderHeaderKey">
	      	<xsl:value-of select="/OrderHoldType/@OrderHeaderKey"/>
	      </xsl:attribute>
</Order>
</xsl:template>

</xsl:stylesheet>