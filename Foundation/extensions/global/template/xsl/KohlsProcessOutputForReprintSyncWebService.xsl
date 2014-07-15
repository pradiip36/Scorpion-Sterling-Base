<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:psws="http://psws.proshipservices.com/"
    xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates select="/psws:ShipResponse/psws:ShipResult"/>
    </xsl:template>
    <xsl:template match="psws:ShipResult">
        <xsl:element name="Container">
            <xsl:attribute name="ExtnB64Label"><xsl:value-of select="./pros:CustomNodes/pros:ProShipCustomNodeItem[pros:NodePath='CCN_LABEL']/pros:Value"/></xsl:attribute>
            <xsl:attribute name="TrackingNo"><xsl:value-of select="./pros:CustomNodes/pros:ProShipCustomNodeItem[pros:NodePath='CCN_REPRINT_TN']/pros:Value"/></xsl:attribute>
            <xsl:element name="Shipment">
                <xsl:attribute name="CarrierServiceCode"><xsl:value-of select="./pros:CustomNodes/pros:ProShipCustomNodeItem[pros:NodePath='CCN_REPRINT_SERVICE']/pros:Value"/></xsl:attribute>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>
