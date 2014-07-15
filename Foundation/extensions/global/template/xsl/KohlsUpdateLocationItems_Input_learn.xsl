<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.inventory.mls.kohls.com/" xmlns:inp="http://webservices.inventory.mls.kohls.com/documentation/KohlsInventoryUpdateToMLSSTUB/updateLocationItems/input">
	<xsl:template match="/">
		<web:updateLocationItems>
			<xsl:if test="string-length(Shipment/env/userId/text()) > 0">
				<env>
					<userId>
						<xsl:value-of select="Shipment/env/userId/text()"/>
					</userId>	
					<password>
						<xsl:value-of select="Shipment/env/password/text()"/>
					</password>
				</env>
			</xsl:if>
			<input>
				<inp:Items>	
					<inp:RequestLocationItem>
						<inp:DetailsIndicator>false</inp:DetailsIndicator>
						<inp:ItemId>
							<xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ItemID"/>
						</inp:ItemId>
						<inp:ItemIdType>Sku</inp:ItemIdType>
						<inp:Quantity>1</inp:Quantity>
						<inp:ShortPickIndicator>
							<xsl:choose>
								<xsl:when test="Shipment/ShipmentLines/ShipmentLine/Extn[@ExtnShortPickIndicator = 'Y']">true</xsl:when>
								<xsl:otherwise>false</xsl:otherwise>
							</xsl:choose>
						</inp:ShortPickIndicator>
					</inp:RequestLocationItem>
				</inp:Items>
				<inp:SourceLocation>
					<inp:LocationId>
						<xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/Location/@LocationId"/>
					</inp:LocationId>
					<inp:LocationType>Stockroom</inp:LocationType>
				</inp:SourceLocation>
				<inp:SubTransactionFunction>BOPUS</inp:SubTransactionFunction>
				<inp:TransactionFunction>Fulfillment</inp:TransactionFunction>
				<inp:Header>
					<inp:UserId>
						<xsl:value-of select="Shipment/@AssignedToUserId"/>
					</inp:UserId>
				</inp:Header>
			</input>
		</web:updateLocationItems>
	</xsl:template>
</xsl:stylesheet>
