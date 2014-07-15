<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/Shipment">
      <xsl:element name="Shipment">
         <xsl:attribute name="SCAC">
            <xsl:value-of select="@SCAC" />
         </xsl:attribute>

         <xsl:attribute name="ShipmentNo">
            <xsl:value-of select="@ShipmentNo" />
         </xsl:attribute>
          <xsl:attribute name="DocumentType">
            <xsl:value-of select="@DocumentType" />
         </xsl:attribute>
          <xsl:attribute name="SellerOrganizationCode">
            <xsl:value-of select="@SellerOrganizationCode" />
         </xsl:attribute>

		<xsl:attribute name="EnterpriseCode">
            <xsl:value-of select="@EnterpriseCode" />
         </xsl:attribute>
         <xsl:attribute name="FromNode">
            <xsl:value-of select="/Shipment/@ShipNode" />
         </xsl:attribute>

         <xsl:attribute name="ToNode">
            <xsl:value-of select="/Shipment/@ToNode" />
         </xsl:attribute>

         <xsl:element name="ShipmentLines">
            <xsl:for-each select="ShipmentLines/ShipmentLine">
               <xsl:element name="ShipmentLine">
                  <xsl:attribute name="ItemID">
                     <xsl:value-of select="@ItemID" />
                  </xsl:attribute>

                  <xsl:attribute name="ItemDesc">
                     <xsl:value-of select="@ItemDesc" />
                  </xsl:attribute>

                  <xsl:attribute name="Quantity">
                     <xsl:value-of select="@Quantity" />
                  </xsl:attribute>

                  <xsl:attribute name="UnitOfMeasure">
                     <xsl:value-of select="@UnitOfMeasure" />
                  </xsl:attribute>

                  <xsl:attribute name="ProductClass">
                     <xsl:value-of select="@ProductClass" />
                  </xsl:attribute>

                  <xsl:attribute name="OrderNo">
                     <xsl:value-of select="@OrderNo" />
                  </xsl:attribute>
                  
                  <xsl:attribute name="PrimeLineNo">
                     <xsl:value-of select="@PrimeLineNo" />
                  </xsl:attribute>
                  
                  <xsl:attribute name="ShipmentLineNo">
                     <xsl:value-of select="@ShipmentLineNo" />
                  </xsl:attribute>	
                  
                  <xsl:attribute name="SubLineNo">
                     <xsl:value-of select="@SubLineNo" />
                  </xsl:attribute>	
                  
                  <xsl:attribute name="ReleaseNo">
                     <xsl:value-of select="@ReleaseNo" />
                  </xsl:attribute>				 
                  <xsl:element name="Extn">
                     <xsl:attribute name="ExtnDept">
                        <xsl:value-of select="Extn/@ExtnDept" />
                     </xsl:attribute>

                     <xsl:attribute name="ExtnClass">
                        <xsl:value-of select="Extn/@ExtnClass" />
                     </xsl:attribute>

                     <xsl:attribute name="ExtnSubClass">
                        <xsl:value-of select="Extn/@ExtnSubClass" />
                     </xsl:attribute>
                  </xsl:element>
               </xsl:element>
            </xsl:for-each>
         </xsl:element>
      </xsl:element>
   </xsl:template>
</xsl:stylesheet>

