<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:S="http://schemas.xmlsoap.org/soap/envelope/" xmlns:ns2="http://websevices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/input"
 xmlns:ns3="http://webservices.inventory.giv.kohls.com/" xmlns:ns4="http://webservices.inventory.giv.kohls.com/documentation/GetLocationInventorySupply/getLocationInventorySupply/output" version="1.0">
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
    <xsl:template match="text()"/>
    <xsl:template match="Shipments">
        <xsl:element name="ShipmentLines">
            <xsl:for-each select="/Shipments/Shipment/ShipmentLines/ShipmentLine">
                <xsl:sort select="./@ItemID"/>
                <xsl:sort select="./@UnitOfMeasure"/>
                <xsl:sort select="./@ProductClass"/>
                <xsl:sort select="./@Quantity" data-type="number" order="ascending"/>
                <xsl:variable name="sortKey">
                    <xsl:value-of select="./@ItemID"/>
                    <xsl:value-of select="'_'"/>
                    <xsl:value-of select="./@UnitOfMeasure"/>
                    <xsl:value-of select="'_'"/>
                    <xsl:value-of select="./@ProductClass"/>
                </xsl:variable>
                <xsl:copy>
                    <xsl:attribute name="ExtnSortKey"><xsl:value-of select="$sortKey"/></xsl:attribute>
                    <xsl:apply-templates select="./@*"/>
                    <xsl:apply-templates/>
                    <xsl:apply-templates select="./ancestor::Shipment[1]"/>
                 </xsl:copy>
            </xsl:for-each>
            <xsl:apply-templates select="./ns3:getLocationInventorySupplyResponse/return"/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="/Shipments/Shipment/ShipmentLines"/>
    <xsl:template match="/Shipments/ns3:getLocationInventorySupplyResponse/return">
        <xsl:element name="ItemList">
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>
    <xsl:template match="/Shipments/ns3:getLocationInventorySupplyResponse/return/ns4:Item">
    	<xsl:element name="Item">
	        <xsl:variable name="sortKey">
	            <xsl:value-of select="./@ItemID"/>
	            <xsl:value-of select="'_'"/>
	            <xsl:value-of select="./@UnitOfMeasure"/>
	            <xsl:value-of select="'_'"/>
	            <xsl:value-of select="./@ProductClass"/>
	        </xsl:variable>
	       
	        <xsl:attribute name="ExtnSortKey"><xsl:value-of select="$sortKey"/></xsl:attribute>
	        <xsl:attribute name="ItemID"><xsl:value-of select="./@ItemID"/></xsl:attribute>
	        <xsl:attribute name="OrganizationCode"><xsl:value-of select="./@OrganizationCode"/></xsl:attribute>
	        <xsl:attribute name="ProductClass"><xsl:value-of select="./@ProductClass"/></xsl:attribute>
	        <xsl:attribute name="UnitOfMeasure"><xsl:value-of select="./@UnitOfMeasure"/></xsl:attribute>
			<xsl:attribute name="Upc01"><xsl:value-of select="./@Upc01"/></xsl:attribute>
	        <xsl:attribute name="StockRoom"><xsl:value-of select="./ns4:Supplies/ns4:InventorySupply[@SupplyType='STOCK_ROOM.ex']/@Quantity"/></xsl:attribute>
	        <xsl:attribute name="Floor"><xsl:value-of select="./ns4:Supplies/ns4:InventorySupply[@SupplyType='SALES_FLOOR.ex']/@Quantity"/></xsl:attribute>        
         </xsl:element>
    </xsl:template>
</xsl:stylesheet>
