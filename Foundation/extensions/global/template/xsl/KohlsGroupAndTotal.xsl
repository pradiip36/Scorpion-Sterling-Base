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
        <xsl:element name="StoreShipments">
            <xsl:element name="StockRoomShipments">
                 <xsl:call-template name="local-write-type-totals">
                     <xsl:with-param name="locationId" select="'10'"/>
                 </xsl:call-template>
            </xsl:element>
            <xsl:element name="FloorShipments">
                <xsl:call-template name="local-write-type-totals">
                    <xsl:with-param name="locationId" select="'20'"/>
                </xsl:call-template>
            </xsl:element>
            <xsl:element name="MixedShipments">
                <xsl:call-template name="local-write-type-totals">
                    <xsl:with-param name="locationId" select="'30'"/>
                </xsl:call-template>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template name="local-write-type-totals">
        <xsl:param name="locationId"/>
        <xsl:attribute name="OrderCount"><xsl:value-of select="count(/Shipments/Shipment[@ExtnLocationId=$locationId])"/></xsl:attribute>
        <xsl:attribute name="TotalUnits"><xsl:value-of select="sum(/Shipments/Shipment[@ExtnLocationId=$locationId]/ShipmentLines/ShipmentLine/@Quantity)"/></xsl:attribute>
        <xsl:element name="ItemCounts">
            <xsl:for-each select="/Shipments/Shipment[@ExtnLocationId=$locationId]/ShipmentLines/ShipmentLine">
                <xsl:sort select="./@ItemID"/>
                <xsl:variable name="itemId" select="./@ItemID"/>
				<xsl:variable name="upc01" select="/Shipments/ItemList/Item[@ItemID=$itemId]/@Upc01"/>
				<xsl:variable name="vendor" select="/Shipments/ItemList/Item[@ItemID=$itemId]/@Vendor"/>
				<xsl:variable name="department" select="/Shipments/ItemList/Item[@ItemID=$itemId]/@Department"/>
				<xsl:variable name="itemDescription" select="/Shipments/ItemList/Item[@ItemID=$itemId]/@ItemDescription"/>
                <xsl:if test="generate-id(current()) = generate-id(/Shipments/Shipment[@ExtnLocationId=$locationId]/ShipmentLines/ShipmentLine[@ItemID=$itemId][1])">
                    <xsl:element name="ItemCount">
                        <xsl:attribute name="ItemID"><xsl:value-of select="$itemId"/></xsl:attribute>
						<xsl:attribute name="Upc01"><xsl:value-of select="$upc01"/></xsl:attribute>
						<xsl:attribute name="Vendor"><xsl:value-of select="$vendor"/></xsl:attribute>						
						<xsl:attribute name="Department"><xsl:value-of select="$department"/></xsl:attribute>
						<xsl:attribute name="ItemDescription"><xsl:value-of select="$itemDescription"/></xsl:attribute>
                        <xsl:attribute name="RequestedQty"><xsl:value-of select="sum(/Shipments/Shipment[@ExtnLocationId=$locationId]/ShipmentLines/ShipmentLine[@ItemID=$itemId]/@Quantity)"/></xsl:attribute>
                        <xsl:variable name="stockRoomQty" select="number(/Shipments/ItemList/Item[@ItemID=$itemId]/@StockRoom)"/>
                        <xsl:variable name="floorQty" select="number(/Shipments/ItemList/Item[@ItemID=$itemId]/@Floor)"/>
                        <xsl:choose>
												
                            <xsl:when test="$locationId='10'">
                                <xsl:attribute name="OnhandQty">
								<xsl:choose>
									<xsl:when test="string(number($stockRoomQty))='NaN'">0</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="number($stockRoomQty)"/>
									</xsl:otherwise>
								</xsl:choose>
								</xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$locationId='20'">
                                <xsl:attribute name="OnhandQty">
								<xsl:choose>
									<xsl:when test="string(number($floorQty))='NaN'">0</xsl:when>
									<xsl:otherwise>
										<xsl:value-of select="number($floorQty)"/>
									</xsl:otherwise>
								</xsl:choose>
								</xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="StockRoom"><xsl:value-of select="number($stockRoomQty)"/></xsl:attribute>
                                <xsl:attribute name="SalesFloor"><xsl:value-of select="number($floorQty)"/></xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:element>
                </xsl:if>
            </xsl:for-each>
        </xsl:element>
        <xsl:element name="Shipments">
            <xsl:apply-templates select="/Shipments/Shipment[@ExtnLocationId=$locationId]">
                <xsl:sort select="./@ShipmentKey"/>
            </xsl:apply-templates>
        </xsl:element>
    </xsl:template>
    <xsl:template match="ShipmentLines">
        <xsl:copy>
            <xsl:apply-templates select="./@*"/>
            <xsl:apply-templates select="./ShipmentLine">
                <xsl:sort select="./@ShipmentLineKey"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
 </xsl:stylesheet>
