<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:lxslt="http://xml.apache.org/xslt"
                version="1.0">
<xsl:template match="/">
<HTML>
<xsl:comment>RECIPIENTS=<xsl:value-of select="/MonitorConsolidation/Shipment/@ToEMailIDs"/></xsl:comment>
<xsl:comment>FROM=<xsl:value-of select="/MonitorConsolidation/Shipment/@FromToEMailID"/></xsl:comment>
<xsl:comment>SUBJECT=<xsl:value-of select="/MonitorConsolidation/Shipment/@EMailSubject"/></xsl:comment>
<xsl:comment>CONTENT_TYPE=text/html</xsl:comment>
<HEAD>
	<title>Shipment Ready For Pick</title>
</HEAD>

<BODY topmargin="0" leftmargin="0">
	<BR/><BR/><font>Hi,</font><BR/><BR/>
	<font>Your Shipment # <xsl:value-of select="/MonitorConsolidation/Shipment/@ShipmentNo"/> for the order # <xsl:value-of select="/MonitorConsolidation/Shipment/ShipmentLines/ShipmentLine/@OrderNo"/> with Item(s)
    <xsl:for-each select="/MonitorConsolidation/Shipment/ShipmentLines/ShipmentLine">
	<xsl:value-of select="@ItemDesc"/><BR/>
	</xsl:for-each>
	is ready for pick.<BR/><BR/><BR/>
	</font>
</BODY>
</HTML>
</xsl:template>
	
</xsl:stylesheet>
