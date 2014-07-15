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
    <xsl:template match="@Name">
        <xsl:variable name="elementName">
            <xsl:variable name="elementRoot" select="substring-before(current(), '/@')"/>
            <xsl:variable name="pathType"><xsl:value-of select="./ancestor::PathList[1]/@Type"/></xsl:variable>
            <xsl:choose>
                <xsl:when test="$pathType='CopyOrganization'">
                    <xsl:value-of select="concat('/MultiApi/API/Output/OrganizationList', $elementRoot)"/>
                </xsl:when>
                <xsl:when test="$pathType='CopyCalendar'">
                    <xsl:value-of select="concat('/MultiApi/API/Output', $elementRoot)"/>
                </xsl:when>
                <xsl:when test="$pathType='CopyResourcePool'">
                    <xsl:value-of select="concat('/MultiApi/API/Output', $elementRoot)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$elementRoot"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:attribute name="Element"><xsl:value-of select="$elementName"/></xsl:attribute>
        <xsl:attribute name="Attribute"><xsl:value-of select="substring-after(current(), '/@')"/></xsl:attribute>
    </xsl:template>
  </xsl:stylesheet>
