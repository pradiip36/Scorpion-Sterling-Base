<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="PathLists">
        <xsl:element name="MultiApi">
            <xsl:apply-templates select="./PathList[@Type='CopyCalendar']"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="PathList">
        <xsl:variable name="modelCalendarId" select="./@CalendarId"/>
        <xsl:if test="generate-id(current()) = generate-id(/PathLists/PathList[@Type='CopyCalendar' and @CalendarId=$modelCalendarId][1])">
            <xsl:element name="API">
                <xsl:copy-of select="./@FlowName"/>
                <xsl:element name="Input">
                    <xsl:element name="Calendar">
                        <xsl:copy-of select="./@CalendarId"/>
                        <xsl:copy-of select="./@OrganizationCode"/>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>
