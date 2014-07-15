<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tem="http://tempuri.org/" 
xmlns:koh="Kohls.InventoryDemand.DataContracts" 
xmlns:koh1="http://schemas.datacontract.org/2004/07/Kohls.InventoryDemand.DataContracts">
<xsl:template match="/">
	<tem:FulfillBopusOrderItems>
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
					<koh1:InventoryTransactionItem>
						<koh1:DetailsIndicator>false</koh1:DetailsIndicator>
						<koh1:ItemId>
							<xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ItemID"></xsl:value-of>
						</koh1:ItemId>
						<koh1:ItemIdType>Sku</koh1:ItemIdType>
						<koh1:Quantity>1</koh1:Quantity>
						<koh1:ShortPickIndicator>
							<xsl:choose>
							<xsl:when test="Shipment/ShipmentLines/ShipmentLine/Extn[@ExtnShortPickIndicator = 'Y']">true</xsl:when>
							<xsl:otherwise>false</xsl:otherwise>
							</xsl:choose>
						</koh1:ShortPickIndicator>
						</koh1:InventoryTransactionItem>
					</koh:Items>
					<koh:SourceLocation>
                     <koh1:LocationId>
                     	<xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/Location/@LocationId"></xsl:value-of>
					 </koh1:LocationId>
                     <koh1:LocationType><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/Location/@LocationContext"></xsl:value-of></koh1:LocationType>
                    </koh:SourceLocation>
					<koh:SubTransactionFunction>BOPUS</koh:SubTransactionFunction>
                  	<koh:TransactionFunction>Fulfillment</koh:TransactionFunction>
				</koh:Data>
				<koh:Header>
    				<koh:UserId>
    					<xsl:value-of select="Shipment/@AssignedToUserId"></xsl:value-of>
    				</koh:UserId>
    			</koh:Header>
			</koh:Request>
		</tem:message>
	</tem:FulfillBopusOrderItems>
</xsl:template>
</xsl:stylesheet>
