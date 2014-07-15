<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:element name="MultiApi">
            <xsl:apply-templates select="/MultiApi/PathLists/PathList[@Type='CopyOrganization']"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="PathList">
        <xsl:variable name="currentList" select="current()"/>
        <xsl:variable name="modelOrgKey" select="./@ModelOrganizationKey"/>
        <xsl:variable name="modelOrg" select="/MultiApi/API/Output/OrganizationList/Organization[@OrganizationKey=$modelOrgKey]"/>
        <xsl:element name="API">
            <xsl:attribute name="Name"><xsl:value-of select="'manageOrganizationHierarchy'"/></xsl:attribute>
            <xsl:element name="Input">
                <xsl:call-template name="local-process-element">
                    <xsl:with-param name="currentList" select="$currentList"/>
                    <xsl:with-param name="currentNode" select="$modelOrg"/>
                </xsl:call-template>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="local-process-element">
        <xsl:param name="currentList"/>
        <xsl:param name="currentNode"/>
        <xsl:variable name="xpath">
            <xsl:for-each select="$currentNode/ancestor-or-self::*">
                <xsl:value-of select="concat('/', name())"/>
            </xsl:for-each>
        </xsl:variable>
        <xsl:element name="{name($currentNode)}">
            <xsl:for-each select="$currentNode/@*">
                <xsl:variable name="attrName" select="name()"/>
                <xsl:variable name="attrValue">
                    <xsl:choose>
                        <xsl:when test="$currentList/Path[@Element=$xpath and @Attribute=$attrName]">
                            <xsl:value-of select="$currentList/Path[@Element=$xpath and @Attribute=$attrName]/@Value"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="current()"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:attribute name="{$attrName}"><xsl:value-of select="$attrValue"/></xsl:attribute>
            </xsl:for-each>
            <xsl:for-each select="$currentNode/*">
                <xsl:call-template name="local-process-element">
                    <xsl:with-param name="currentNode" select="current()"/>
                    <xsl:with-param name="currentList" select="$currentList"/>
                </xsl:call-template>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
