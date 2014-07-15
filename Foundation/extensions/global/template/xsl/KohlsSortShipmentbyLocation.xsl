<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output indent="yes" />

   <xsl:template match="/">
      <PickSlip>
         <xsl:attribute name="PrinterID">
            <xsl:value-of select="Shipments/@PrinterID" />
         </xsl:attribute>
	   <xsl:attribute name="ShipNode">
            <xsl:value-of select="Shipments/@ShipNode" />
         </xsl:attribute>

	<xsl:attribute name="TotalCarts">
            <xsl:value-of select="Shipments/@TotalCarts" />
         </xsl:attribute>
	 <xsl:attribute name="CartNo">
            <xsl:value-of select="Shipments/@CartNo" />
         </xsl:attribute>
	 <xsl:attribute name="TotalCartsAvailable">
            <xsl:value-of select="Shipments/@TotalCartsAvailable" />
         </xsl:attribute>
	 <xsl:attribute name="CartNoAvailable">
            <xsl:value-of select="Shipments/@CartNoAvailable" />
         </xsl:attribute>
	 <xsl:attribute name="BatchID">
            <xsl:value-of select="Shipments/@BatchID" />
         </xsl:attribute>
         <FrontPage>
            <xsl:for-each select="Shipments/Shipment/ShipmentLines/ShipmentLine">               
               <xsl:sort select="@ExtnLocationID" data-type="text" />
               
               <xsl:apply-templates select="." />
            </xsl:for-each>
         </FrontPage>

         <BackPage>
            <xsl:for-each select="Shipments/Shipment">
               <xsl:sort select="@ToteId" data-type="number" />

               <xsl:apply-templates select="." />
            </xsl:for-each>
         </BackPage>
      </PickSlip>
   </xsl:template>

   <xsl:template match="@*|node()">
      <xsl:copy>
         <xsl:apply-templates select="@*|node()" />
      </xsl:copy>
   </xsl:template>
</xsl:stylesheet>