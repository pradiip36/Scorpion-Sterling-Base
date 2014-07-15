<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:psws="http://psws.proshipservices.com/"
    xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="Container">
        <xsl:element name="psws:Void">
            <xsl:element name="psws:psVoidId"><xsl:value-of select="/Container/Extn/@ExtnVoidLabelId"/></xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>