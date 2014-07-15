<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:psws="http://psws.proshipservices.com/"
    xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="Container">
        <xsl:element name="psws:Ship">
            <xsl:element name="psws:shipment">
                <xsl:element name="pros:CustomNodes">
                    <xsl:element name="pros:ProShipCustomNodeItem">
                        <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_REPRINT'"/></xsl:element>
                        <xsl:element name="pros:Value"><xsl:value-of select="'True'"/></xsl:element>
                    </xsl:element>
                    <xsl:element name="pros:ProShipCustomNodeItem">
                        <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_REPRINT_TN'"/></xsl:element>
                        <xsl:element name="pros:Value"><xsl:value-of select="/Container/@TrackingNo"/></xsl:element>
                    </xsl:element>
                    <xsl:element name="pros:ProShipCustomNodeItem">
                        <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_REPRINT_SERVICE'"/></xsl:element>
                        <xsl:element name="pros:Value"><xsl:value-of select="/Container/@ExternalReference1"/></xsl:element>
                    </xsl:element>
                </xsl:element>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
