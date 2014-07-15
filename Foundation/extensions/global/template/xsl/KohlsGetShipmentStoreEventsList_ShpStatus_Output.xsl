<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	 <xsl:output method="xml" indent="yes" />
	<xsl:template match="/">	
		<ShipmentStoreEventsList>
			<xsl:for-each select="/ShipmentStoreEventsList/ShipmentStoreEvents">
				<ShipmentStoreEvents  Createts="{@Createts}" EventType="{@EventType}" LocationId="{@LocationId}" Quantity="{@Quantity}" ReasonCode="{@ReasonCode}" 
					ReasonText="{@ReasonText}" ShipmentLineNo="{@ShipmentLineNo}" UserId="{@UserId}"/>
			</xsl:for-each>			
		</ShipmentStoreEventsList>
	</xsl:template>
</xsl:stylesheet>