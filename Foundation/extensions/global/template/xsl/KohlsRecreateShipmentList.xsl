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
    <xsl:template match="ShipmentLines">
        <xsl:element name="Shipments">
        <xsl:for-each select="./ShipmentLine">
            <xsl:variable name="shipKey" select="./@ShipmentKey"/>
            <xsl:if test="generate-id(current()) = generate-id(/ShipmentLines/ShipmentLine[@ShipmentKey=$shipKey][1])">
                <xsl:apply-templates select="./Shipment"/>
            </xsl:if>
        </xsl:for-each>
        <xsl:apply-templates select="/ShipmentLines/ItemList"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="Shipment">
        <xsl:variable name="shipKey" select="./@ShipmentKey"/>
        <xsl:copy>
            <xsl:call-template name="local-write-shipment-pick-type">
                <xsl:with-param name="shipKey" select="$shipKey"/>
            </xsl:call-template>
            <xsl:apply-templates select="./@*"/>
            <xsl:apply-templates/>
            <xsl:element name="ShipmentLines">
                <xsl:apply-templates select="/ShipmentLines/ShipmentLine[@ShipmentKey=$shipKey]"/>
            </xsl:element>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="ShipmentLine">
        <xsl:copy>
            <xsl:apply-templates select="./@*"/>
            <xsl:for-each select="./*">
                <xsl:if test="name()!='Shipment'">
                    <xsl:apply-templates/>
                </xsl:if>
            </xsl:for-each>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="ShipmentLine/@ExtnSortKey"/>
     <!-- 
        /**
         * A template that writes the pick type at the shipment level, by examining
         * the pick types of the individual lines.
         *
         * @param shipKey the current shipment key
         */
    -->
    <xsl:template name="local-write-shipment-pick-type">
        <xsl:param name="shipKey"/>
        <xsl:variable name="stockRoomCount" select="count(/ShipmentLines/ShipmentLine[@ShipmentKey=$shipKey and @ExtnLocationId='10'])"/>
        <xsl:variable name="floorCount" select="count(/ShipmentLines/ShipmentLine[@ShipmentKey=$shipKey and @ExtnLocationId='20'])"/>
        <xsl:variable name="mixedCount" select="count(/ShipmentLines/ShipmentLine[@ShipmentKey=$shipKey and @ExtnLocationid='30'])"/>
        <xsl:choose>
            <xsl:when test="number($floorCount)=0 and number($mixedCount)=0">
                <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'10'"/></xsl:attribute>
                <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'STOCK_ROOM'"/></xsl:attribute>
            </xsl:when>
            <xsl:when test="number($stockRoomCount)=0 and number($mixedCount)=0">
                <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'20'"/></xsl:attribute>
                <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'FLOOR'"/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'30'"/></xsl:attribute>
                <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'MIXED_ORDER'"/></xsl:attribute>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
