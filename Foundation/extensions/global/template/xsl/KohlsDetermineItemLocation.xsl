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
    <xsl:template match="/ShipmentLines/ShipmentLine">
        <xsl:variable name="sortKey" select="./@ExtnSortKey"/>
        <xsl:if test="generate-id(current()) = generate-id(/ShipmentLines/ShipmentLine[@ExtnSortKey=$sortKey][1])">
            <xsl:variable name="stockRoomQty" select="/ShipmentLines/ItemList/Item[@ExtnSortKey=$sortKey]/@StockRoom"/>
            <xsl:variable name="floorQty" select="/ShipmentLines/ItemList/Item[@ExtnSortKey=$sortKey]/@Floor"/>
            <xsl:call-template name="local-process-shipment-lines-for-item">
                <xsl:with-param name="shipLine" select="current()"/>
                <xsl:with-param name="stockRoomQty" select="$stockRoomQty"/>
                <xsl:with-param name="floorQty" select="$floorQty"/>
				<xsl:with-param name="givStockRoom" select="$stockRoomQty"/>
                <xsl:with-param name="givFloor" select="$floorQty"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
     <xsl:template name="local-process-shipment-lines-for-item">
        <xsl:param name="shipLine"/>
        <xsl:param name="stockRoomQty"/>
        <xsl:param name="floorQty"/>
		<xsl:param name="givStockRoom"/>
        <xsl:param name="givFloor"/>
        <xsl:variable name="qty" select="$shipLine/@Quantity"/>
        <xsl:variable name="pickFromStockRoom">
            <xsl:choose>
				<xsl:when test="string(number($stockRoomQty))='NaN'">0</xsl:when>
             	<xsl:when test="string(number($floorQty))='NaN'">0</xsl:when>  
                <xsl:when test="number($qty) &lt;= number($stockRoomQty)">
                    <xsl:value-of select="$qty"/>
                </xsl:when>
                <xsl:when test="number($qty) &lt;= number($floorQty)">
                    <xsl:value-of select="number('0')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="number($stockRoomQty)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="pickFromFloor">
            <xsl:choose>
				<xsl:when test="string(number($stockRoomQty))='NaN'">0</xsl:when>
             	<xsl:when test="string(number($floorQty))='NaN'">0</xsl:when> 
                <xsl:when test="number($pickFromStockRoom) = number($qty)">
                    <xsl:value-of select="number('0')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="number($qty) - number($pickFromStockRoom)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="ShipmentLine">
            <xsl:choose>
                <xsl:when test="number($pickFromStockRoom) != 0 and number($pickFromFloor) = 0">
                    <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'10'"/></xsl:attribute>
                    <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'STOCK_ROOM'"/></xsl:attribute>
                </xsl:when>
                <xsl:when test="number($pickFromStockRoom) = 0 and number($pickFromFloor) != 0">
                    <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'20'"/></xsl:attribute>
                    <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'FLOOR'"/></xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="ExtnLocationId"><xsl:value-of select="'30'"/></xsl:attribute>
                    <xsl:attribute name="ExtnLocationName"><xsl:value-of select="'MIXED_ORDER'"/></xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:apply-templates select="$shipLine/@*"/>
			<xsl:attribute name="ExtnGIVStockRoom">
				<xsl:choose>
					<xsl:when test="string(number($givStockRoom))='NaN'">0</xsl:when>
					<xsl:when test="string(number($givStockRoom))=''">0</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="number($givStockRoom)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:attribute name="ExtnGIVFloor">
				<xsl:choose>
					<xsl:when test="string(number($givFloor))='NaN'">0</xsl:when>
					<xsl:when test="string(number($givFloor))=''">0</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="number($givFloor)"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			           
            <xsl:attribute name="ExtnFromStockRoom"><xsl:value-of select="$pickFromStockRoom"/></xsl:attribute>
            <xsl:attribute name="ExtnFromFloor"><xsl:value-of select="$pickFromFloor"/></xsl:attribute>
            <xsl:apply-templates select="$shipLine/*"/>
        </xsl:element>
         <xsl:variable name="sortKey" select="$shipLine/@ExtnSortKey"/>
         <xsl:if test="$shipLine/following-sibling::ShipmentLine[@ExtnSortKey=$sortKey]">
             <xsl:call-template name="local-process-shipment-lines-for-item">
                 <xsl:with-param name="shipLine" select="$shipLine/following-sibling::ShipmentLine[@ExtnSortKey=$sortKey][1]"/>
                 <xsl:with-param name="stockRoomQty" select="number($stockRoomQty) - number($pickFromStockRoom)"/>
                 <xsl:with-param name="floorQty" select="number($floorQty) - number($pickFromFloor)"/>
				 <xsl:with-param name="givStockRoom" select="$givStockRoom"/>
                 <xsl:with-param name="givFloor" select="$givFloor"/>
             </xsl:call-template>
         </xsl:if>
     </xsl:template>
</xsl:stylesheet>
