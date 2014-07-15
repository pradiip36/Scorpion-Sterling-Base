<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="1.0">
	<xsl:output indent="yes" />
	<xsl:template match="/">

		<xsl:variable name="OrderNo" select="OrderLine/Order/@OrderNo" />
		<xsl:variable name="DocumentType" select="OrderLine/Order/@DocumentType" />
		<xsl:variable name="EnterpriseCode" select="OrderLine/Order/@EnterpriseCode" />
		<xsl:variable name="ItemID" select="OrderLine/Item/@ItemID" />
		<xsl:variable name="PrimeLineNo" select="OrderLine/@PrimeLineNo" />
		<xsl:variable name="SubLineNo" select="OrderLine/@SubLineNo" />
		<xsl:variable name="UnitOfMeasure" select="OrderLine/Item/@UnitOfMeasure" />
		
		<xsl:element name="Order">
			<xsl:attribute name="DocumentType"><xsl:value-of select="$DocumentType" /></xsl:attribute>
			<xsl:attribute name="EnterpriseCode"><xsl:value-of select="$EnterpriseCode" /></xsl:attribute>
			<xsl:attribute name="OrderNo"><xsl:value-of select="$OrderNo" /></xsl:attribute>
			<xsl:if test="/OrderLine/FromStatuses/OrderStatus[@Status = '2100']">
			<OrderLines>
				<xsl:for-each
					select="OrderLine/ToStatuses/OrderStatus[@Status = '2100.001']">
					
					<xsl:element name="OrderLine">

						<xsl:attribute name="CarrierServiceCode" />
						<xsl:attribute name="ItemID"><xsl:value-of
							select="$ItemID" /> </xsl:attribute>
						<xsl:attribute name="OrderLineKey"><xsl:value-of
							select="@OrderLineKey" /></xsl:attribute>
						<xsl:attribute name="OrderedQty"><xsl:value-of
							select="@TotalQuantity" /></xsl:attribute>
						<xsl:attribute name="PrimeLineNo"><xsl:value-of
							select="$PrimeLineNo" /></xsl:attribute>
						<xsl:attribute name="Quantity"><xsl:value-of
							select="@StatusQty" /></xsl:attribute>
						<xsl:attribute name="SCAC"></xsl:attribute>
						<xsl:attribute name="SubLineNo"><xsl:value-of
							select="$SubLineNo" /></xsl:attribute>
						<xsl:attribute name="UnitOfMeasure"><xsl:value-of
							select="$UnitOfMeasure" /></xsl:attribute>

						<OrderStatuses>
							<xsl:element name="OrderStatus">
								<xsl:attribute name="ShipNode"><xsl:value-of
									select="@ShipNode" /></xsl:attribute>
								<xsl:attribute name="Status"><xsl:value-of
									select="@Status" /> </xsl:attribute>
								<xsl:attribute name="StatusDate"><xsl:value-of
									select="@StatusDate" /></xsl:attribute>
								<xsl:attribute name="StatusDescription"><xsl:value-of
									select="@StatusDescription" /></xsl:attribute>
								<xsl:attribute name="StatusQty"><xsl:value-of
									select="@StatusQty" /></xsl:attribute>
							</xsl:element>
						</OrderStatuses>

					</xsl:element>
					
				</xsl:for-each>
			</OrderLines>
			</xsl:if>
		</xsl:element>

	</xsl:template>
</xsl:stylesheet>
