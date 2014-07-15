<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.inventory.mls.kohls.com/" xmlns:inp="http://webservices.inventory.mls.kohls.com/documentation/KohlsInventoryDemandService/getItemLocations/input">
	<xsl:template match="/">
		<web:getItemLocations>
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
				<xsl:for-each select="Shipment/OrderLines/OrderLine">
					<inp:RequestItem>
						<inp:ItemId>
							<xsl:value-of select="@ItemID"/>
						</inp:ItemId>
						<inp:ItemType>Sku</inp:ItemType>
						<inp:Quantity>
							<xsl:value-of select="@Quantity"/>
						</inp:Quantity>
					</inp:RequestItem>
				</xsl:for-each>
			</input>
		</web:getItemLocations>
	</xsl:template>
</xsl:stylesheet>