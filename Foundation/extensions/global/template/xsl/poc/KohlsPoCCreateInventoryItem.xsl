<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="//Item">
      <Items>
         <Item AdjustmentType="ADJUSTMENT" Availability="TRACK" ItemID="{@ItemID}" OrganizationCode="{@OrganizationCode}" Quantity="0" ReasonCode="DUMMY_ADJ" ShipNode="9998" SupplyType="ONHAND" UnitOfMeasure="{@UnitOfMeasure}" />
		 <Item AdjustmentType="ADJUSTMENT" Availability="TRACK" ItemID="{@ItemID}" OrganizationCode="{@OrganizationCode}" ProductClass="Good" Quantity="0" ReasonCode="DUMMY_ADJ" ShipNode="9998" SupplyType="ONHAND" UnitOfMeasure="{@UnitOfMeasure}" />
      </Items>
   </xsl:template>
</xsl:stylesheet>
