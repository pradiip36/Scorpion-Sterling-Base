<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tem="http://tempuri.org/" 
xmlns:koh="Kohls.InventoryDemand.DataContracts" 
xmlns:arr="http://schemas.microsoft.com/2003/10/Serialization/Arrays" 
xmlns:koh1="http://schemas.datacontract.org/2004/07/Kohls.InventoryDemand.DataContracts">
<xsl:template match="/">
	<tem:GetLocationDetails >
		<xsl:if test="string-length(Shipment/env/userId/text()) > 0">
			<env>
				<userId><xsl:value-of select="Shipment/env/userId/text()"/></userId>	
				<password><xsl:value-of select="Shipment/env/password/text()"/></password>
			</env>
		</xsl:if>
		<tem:message>
			<koh:Request >
				<koh:Data >
					<koh:LocationFilter>
						<xsl:choose>
						<xsl:when test = "Shipment/@HoldLocationFlag='N'">Stockroom</xsl:when>
						<xsl:otherwise>BOPUS</xsl:otherwise>
						</xsl:choose>
					</koh:LocationFilter>
					<koh:LocationIds>
						<xsl:for-each select="Shipment/Locations/Location">
							<arr:string>
								<xsl:value-of select="@LocationID"></xsl:value-of>
							</arr:string>
						</xsl:for-each>
					</koh:LocationIds>
				</koh:Data>
				<koh:Header>
    				<koh:UserId>
    					<xsl:value-of select="Shipment/@UserID"></xsl:value-of>
    				</koh:UserId>
    			</koh:Header>
			</koh:Request>
		</tem:message>
	</tem:GetLocationDetails>
</xsl:template>
</xsl:stylesheet>
