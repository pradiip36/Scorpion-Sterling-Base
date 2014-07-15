<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="*">
        <xsl:copy>
            <xsl:apply-templates select="./@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="@*">
        <xsl:copy-of select="current()"/>
    </xsl:template>
    <xsl:template match="Shipments">
        <xsl:variable name="from" select="./@From"/>
        <xsl:variable name="to" select="./@To"/>
        <xsl:copy>
            <xsl:for-each select="./Shipment">
                <xsl:if test="position() &gt;=number($from) and position() &lt;=number($to)">
                    <xsl:apply-templates select="current()"/>
                </xsl:if>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>