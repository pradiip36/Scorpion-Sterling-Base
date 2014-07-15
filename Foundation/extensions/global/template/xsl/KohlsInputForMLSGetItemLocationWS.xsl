<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tem="http://tempuri.org/" 
xmlns:koh="Kohls.InventoryDemand.DataContracts" 
xmlns:koh1="http://schemas.datacontract.org/2004/07/Kohls.InventoryDemand.DataContracts">
<xsl:template match="/">
	<tem:GetItemLocations >
		<xsl:if test="string-length(Shipment/env/userId/text()) > 0">
			<env>
				<userId><xsl:value-of select="Shipment/env/userId/text()"/></userId>	
				<password><xsl:value-of select="Shipment/env/password/text()"/></password>
			</env>
		</xsl:if>
		<tem:message>
			<koh:Request >
				<koh:Data >
					<koh:Items>
						<xsl:for-each select="Shipment/OrderLines/OrderLine">
							<koh1:RequestItem>
								<koh1:ItemId><xsl:value-of select="@ItemID"></xsl:value-of></koh1:ItemId>
								<koh1:ItemType>Sku</koh1:ItemType>
								<koh1:Quantity><xsl:value-of select="@Quantity"></xsl:value-of></koh1:Quantity>
							</koh1:RequestItem>
						</xsl:for-each>
					</koh:Items>
					<koh:LocationFilters>
						<xsl:if test="Shipment/OrderLines/@LocationFilterTypeEnum='Y' ">
							<koh1:LocationFilterTypeEnum>SalesFloor</koh1:LocationFilterTypeEnum>
    						<koh1:LocationFilterTypeEnum>Stockroom</koh1:LocationFilterTypeEnum>
						</xsl:if>
					</koh:LocationFilters>
				</koh:Data>
				<koh:Header>
    				<koh:UserId>
    					<xsl:value-of select="Shipment/OrderLines/@UserId"></xsl:value-of>
    				</koh:UserId>
    			</koh:Header>
			</koh:Request>
		</tem:message>
	</tem:GetItemLocations>
</xsl:template>
</xsl:stylesheet>