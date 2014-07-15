<?xml version="1.0" encoding="UTF-8"?>
<!--
    /**
     * A transformation that removes ExceptionShift attributes from a cloned calendar.
     *
     * @author Roy Nicholls
     */
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes"/>
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
    <xsl:template match="text()"/>
    <xsl:template match="ExceptionShifts">
        <xsl:if test="./*">
            <xsl:copy>
                <xsl:apply-templates select="./@*"/>
                <xsl:apply-templates/>
            </xsl:copy>
        </xsl:if>
    </xsl:template>
    <xsl:template match="ExceptionShift/@*"/>
</xsl:stylesheet>
