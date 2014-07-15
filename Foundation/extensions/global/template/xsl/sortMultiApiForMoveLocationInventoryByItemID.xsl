<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="MultiApi">
		<MultiApi>
			<xsl:for-each select="API">
				<xsl:sort select="Input/MoveLocationInventory/Source/Inventory/InventoryItem/@ItemID" data-type="text"/>				
				<xsl:copy-of select="." /> 
			</xsl:for-each>
		</MultiApi>
	</xsl:template>
</xsl:stylesheet>