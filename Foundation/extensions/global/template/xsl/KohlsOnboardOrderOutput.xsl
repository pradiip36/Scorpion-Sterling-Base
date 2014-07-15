<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="node()">
        <xsl:copy>
            <xsl:apply-templates select="./@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="@*">
        <xsl:copy-of select="current()"/>
    </xsl:template>
    <xsl:template match="text()"/>
    <xsl:template match="MultiApis">
        <xsl:copy>
            <xsl:comment>Step 1: Create stores (in MC)</xsl:comment>
            <xsl:apply-templates select="./MultiApi/API[@Name='manageOrganizationHierarchy']/Input/Organization/OrgRoleList/ancestor::MultiApi[1]"/>
            <xsl:comment>Step 2: Create store calendars (in MC)</xsl:comment>
            <xsl:apply-templates select="./MultiApi/API[@Name='createCalendar']/ancestor::MultiApi[1]"/>
            <xsl:comment>Step 3: Set calendars as shipping calendar for stores (in MC)</xsl:comment>
            <xsl:apply-templates select="./MultiApi/API[@Name='manageOrganizationHierarchy']/Input/Organization/Node[@ShippingCalendarKey]/ancestor::MultiApi[1]"/>
            <xsl:comment>Step 4: Create resource pools (in MC)</xsl:comment>
            <xsl:apply-templates select="./MultiApi/API/Input/ResourcePool/ancestor::MultiApi[1]"/>
            <xsl:comment>Step 5: Create capacity (in PROD)</xsl:comment>
            <xsl:element name="MultiApi">
                <xsl:for-each select="./MultiApi/API/Input/ResourcePool/ancestor::API[1]">
                    <xsl:copy>
                        <xsl:attribute name="Name"><xsl:value-of select="'changeResourcePool'"/></xsl:attribute>
                        <xsl:element name="Input">
                            <xsl:call-template name="local-change-res-pool">
                                <xsl:with-param name="pool" select="./Input/ResourcePool"/>
                            </xsl:call-template>
                        </xsl:element>
                    </xsl:copy>
                </xsl:for-each>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="ResourcePool/EffectiveDateRanges"/>
    <xsl:template name="local-change-res-pool">
        <xsl:param name="pool"/>
        <xsl:for-each select="$pool">
            <xsl:copy>
                <xsl:copy-of select="./@CapacityOrganizationCode"/>
                <xsl:copy-of select="./@Node"/>
                <xsl:copy-of select="./@ResourcePoolId"/>
                <xsl:element name="EffectiveDateRanges">
                    <xsl:attribute name="Reset"><xsl:value-of select="'Y'"/></xsl:attribute>
                    <xsl:copy-of select="./EffectiveDateRanges/*"/>
                </xsl:element>
            </xsl:copy>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
