<?xml version="1.0" encoding="UTF-8"?>
<!--
    /**
     * A transformation that when applied to a shipment list list, extracts
     * all the distinct item ids, and formats the results in the way needed
     * by the GIV store location interface.
     *
     * @author IBM SWG Professional Services Team
     */
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:web="http://webservices.inventory.giv.kohls.com/"
    xmlns:inp="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
    <xsl:template match="Shipments">
		<xsl:variable name="userId" select="./@userId"/>
        <xsl:variable name="password" select="./@password"/>
          <web:getLocationInventorySupply xmlns:web="http://webservices.inventory.giv.kohls.com/"
	            xmlns:inp="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input">
	            <xsl:element name="env">
	                <xsl:element name="password"><xsl:value-of select="$password"/></xsl:element>
	                <xsl:element name="userId"><xsl:value-of select="$userId"/></xsl:element>
	            </xsl:element>
	            <xsl:element name="input">
	                <xsl:apply-templates select="//ShipmentLine"/>
	            </xsl:element>
	        </web:getLocationInventorySupply>   
    </xsl:template>
    <xsl:template match="ShipmentLine">
        <xsl:variable name="itemId" select="./@ItemID"/>
        <xsl:variable name="productClass" select="./@ProductClass"/>
        <xsl:variable name="uom" select="./@UnitOfMeasure"/>
        <xsl:if test="generate-id(current()) = generate-id(//ShipmentLine[@ItemID=$itemId and @ProductClass=$productClass and @UnitOfMeasure=$uom][1])">
            <xsl:element name="inp:InventorySupply">
                <xsl:copy-of select="./ancestor::Shipment[1]/@ShipNode"/>
                <xsl:attribute name="OrganizationCode"><xsl:value-of select="'DEFAULT'"/></xsl:attribute>
                <xsl:copy-of select="./@ItemID"/>
                <xsl:copy-of select="./@ProductClass"/>
                <xsl:copy-of select="./@UnitOfMeasure"/>
                <xsl:attribute name="ExtnTotalQuantity"><xsl:value-of select="sum(//ShipmentLine[@ItemID=$itemId and @ProductClass=$productClass and @UnitOfMeasure=$uom]/@Quantity)"/></xsl:attribute>
            </xsl:element>
        </xsl:if>
    </xsl:template>
    <xsl:template match="text()"/>
</xsl:stylesheet>
