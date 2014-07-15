<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservices.inventory.mls.kohls.com/" xmlns:inp="http://webservices.inventory.mls.kohls.com/documentation/KohlsgetHoldLocationDescriptionSTUB/getLocationDetails/input">
	<xsl:template match="/">
		<web:getLocationDetails>
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
				<inp:LocationFilter>BOPUS</inp:LocationFilter>
				<inp:LocationIds>
					<inp:string>
					<xsl:for-each select="Shipment/Locations/Location">
							<xsl:value-of select="@LocationID"/>
					</xsl:for-each>
					</inp:string>
				</inp:LocationIds>
				<inp:Header>
					<inp:UserId>
						<xsl:value-of select="Shipment/@UserID"/>
					</inp:UserId>
				</inp:Header>
			</input>
		</web:getLocationDetails>
	</xsl:template>
</xsl:stylesheet>