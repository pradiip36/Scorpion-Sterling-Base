<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" indent="yes" omit-xml-declaration="no" />

   <xsl:template match="/">
      <Shipments>
         <xsl:copy>
            <xsl:for-each select="./Shipments/PrintRePackDocs/PrintRePackDoc/Shipment">
               <xsl:copy>
                  <xsl:apply-templates select="." />

                  <ToAddress>
                     <xsl:apply-templates select="./ToAddress" />
                  </ToAddress>

                  <BillToAddress>
                     <xsl:apply-templates select="./BillToAddress" />
                  </BillToAddress>

                  <Containers>
                     <Container>
                        <ContainerDetails>
                           <ContainerDetail>
                           <xsl:attribute name="Quantity">
                           <xsl:value-of select="./ShipmentLines/ShipmentLine/@Quantity"/>
                           </xsl:attribute>
                              <ShipmentCharges>
                                 <xsl:copy-of select="./ShipmentCharges/@*" />

                                 <xsl:for-each select="./ShipmentCharges/ShipmentCharge">
                                    <ShipmentCharge>
                                       <xsl:copy-of select="./@*" />
                                    </ShipmentCharge>
                                 </xsl:for-each>
                              </ShipmentCharges>

                              <xsl:for-each select="./ShipmentLines/ShipmentLine">
                                 <ShipmentLine>
                                   <xsl:copy-of select="./@*" />
                                    <xsl:copy-of select="*" />
                                 </ShipmentLine>
                              </xsl:for-each>

                              <PopulateTaxes>
                                 <xsl:for-each select="./PopulateTaxes/PopulateTax">
                                    <PopulateTax>
                                       <xsl:copy-of select="./@*" />
                                    </PopulateTax>
                                 </xsl:for-each>
                              </PopulateTaxes>

                              <Status>
                                 <xsl:copy-of select="./Status/@*" />
                              </Status>
                           </ContainerDetail>
                        </ContainerDetails>
                     </Container>
                  </Containers>

                  <Extn>
                   <xsl:copy-of select="./Extn/@*" />
                  </Extn>
               </xsl:copy>
            </xsl:for-each>
         </xsl:copy>
      </Shipments>
   </xsl:template>

   <xsl:template match="/Shipments/PrintRePackDocs/PrintRePackDoc/Shipment">
      <xsl:copy-of select="./@*" />
   </xsl:template>

   <xsl:template match="/Shipments/PrintRePackDocs/PrintRePackDoc/Shipment/ToAddress">
      <xsl:copy-of select="./@*" />
   </xsl:template>

   <xsl:template match="/Shipments/PrintRePackDocs/PrintRePackDoc/Shipment/BillToAddress">
      <xsl:copy-of select="./@*" />
   </xsl:template>

</xsl:stylesheet>

