<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" indent="yes" />

   <xsl:template match="/">
      <Items>
        

         <Item>
            <xsl:attribute name="AdjustmentType">
               <xsl:value-of select="/Items/Item/@AdjustmentType" />
            </xsl:attribute>

            <xsl:attribute name="Availability">INFINITE</xsl:attribute>

            <xsl:attribute name="ItemID">
               <xsl:value-of select="/Items/Item/@ItemID" />
            </xsl:attribute>

            <xsl:attribute name="OrganizationCode">
               <xsl:value-of select="/Items/Item/@OrganizationCode" />
            </xsl:attribute>

            <xsl:attribute name="ProductClass">
               <xsl:value-of select="/Items/Item/@ProductClass" />
            </xsl:attribute>

            <xsl:attribute name="ReasonCode">DSVNULLIFY</xsl:attribute>

            <xsl:attribute name="ReasonText">
               <xsl:value-of select="/Items/Item/@ReasonText" />
            </xsl:attribute>

            <xsl:attribute name="ShipNode">
               <xsl:value-of select="/Items/Item/@ShipNode" />
            </xsl:attribute>

            <xsl:attribute name="SupplyType">
               <xsl:value-of select="/Items/Item/@SupplyType" />
            </xsl:attribute>

            <xsl:attribute name="UnitOfMeasure">
               <xsl:value-of select="/Items/Item/@UnitOfMeasure" />
            </xsl:attribute>

            <xsl:attribute name="Reference_1">
               <xsl:value-of select="/Items/Item/@Reference_1" />
            </xsl:attribute>

            <xsl:attribute name="Reference_2">
               <xsl:value-of select="/Items/Item/@Reference_2" />
            </xsl:attribute>

            <xsl:attribute name="Reference_3">
               <xsl:value-of select="/Items/Item/@Reference_3" />
            </xsl:attribute>

            <xsl:attribute name="Reference_4">
               <xsl:value-of select="/Items/Item/@Reference_4" />
            </xsl:attribute>
         </Item>
		 <xsl:copy-of select="/Items/Item" />
      </Items>
   </xsl:template>
</xsl:stylesheet>

