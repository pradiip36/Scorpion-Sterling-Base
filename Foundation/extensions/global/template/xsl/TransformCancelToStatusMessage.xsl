<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:ex="http://exslt.org/dates-and-times"    
    extension-element-prefixes="ex">
	<xsl:template match="/Order">
		<Order>
			<xsl:attribute name="DocumentType"><xsl:value-of select="@DocumentType"/></xsl:attribute>
			<xsl:attribute name="EnterpriseCode"><xsl:value-of select="@EnterpriseCode"/></xsl:attribute>
			<xsl:attribute name="OrderNo"><xsl:value-of select="@OrderNo"/></xsl:attribute>
			<OrderLines>
				<xsl:for-each select="/Order/OrderLines/OrderLine">
					<OrderLine >
						<xsl:attribute name="OrderedQty"><xsl:value-of select="@OrderedQty"/></xsl:attribute>
						<xsl:attribute name="PrimeLineNo"><xsl:value-of select="@PrimeLineNo"/></xsl:attribute>
						<xsl:attribute name="SubLineNo"><xsl:value-of select="@SubLineNo"/></xsl:attribute>
						<xsl:attribute name="ItemID"><xsl:value-of select="./Item/@ItemID"/></xsl:attribute>
						<xsl:attribute name="UnitOfMeasure"><xsl:value-of select="./Item/@UnitOfMeasure"/></xsl:attribute>
						<xsl:attribute name="Quantity"><xsl:value-of select="./@ChangeInOrderedQty"/></xsl:attribute>
						<OrderStatuses>
							<xsl:for-each select="./OrderStatuses/OrderStatus">
							<xsl:choose>
							<xsl:when test="@Status='9000'">
							<OrderStatus ShipNode="">
							<xsl:attribute name="Status"><xsl:value-of select="@Status"/></xsl:attribute>
							<xsl:attribute name="StatusDate"><xsl:value-of select="@StatusDate"/> </xsl:attribute>
							<xsl:attribute name="StatusDescription"><xsl:value-of select="@StatusDescription"/></xsl:attribute>
							<xsl:attribute name="StatusQty"><xsl:value-of select="number(-../../@ChangeInOrderedQty)"/></xsl:attribute>
						</OrderStatus>
						</xsl:when>
						</xsl:choose>
						</xsl:for-each>
						</OrderStatuses>
					</OrderLine>
				</xsl:for-each>
			</OrderLines>
		</Order>
	</xsl:template>
</xsl:stylesheet>
