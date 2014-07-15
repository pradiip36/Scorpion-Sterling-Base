<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output indent="yes" />

   <xsl:template match="/">
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
   </xsl:template>

   <xsl:template match="Shipments">
      <xsl:copy>
         <xsl:apply-templates select="@*" />

         <xsl:apply-templates select="Shipment">
            <xsl:sort select="@ToteId" data-type="number" />
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>

   <xsl:template match="Shipments/ShipmentLines">
      <xsl:copy>
         <xsl:apply-templates select="ShipmentLines">
         </xsl:apply-templates>
      </xsl:copy>
   </xsl:template>

   <xsl:template match="Shipment">
      <xsl:copy>
         <xsl:apply-templates select="@*" />

         <ShipmentLines>
            <xsl:apply-templates select="ShipmentLines/ShipmentLine">
               <xsl:sort select="@ExtnLocationID" data-type="text" />
            </xsl:apply-templates>
         </ShipmentLines>
      </xsl:copy>
   </xsl:template>

   <xsl:template match="@*|node()">
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
   </xsl:template>
</xsl:stylesheet>

