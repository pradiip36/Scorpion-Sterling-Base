<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:psws="http://psws.proshipservices.com/"
    xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/psws:ShipResponse">
        <xsl:apply-templates select="psws:ShipResult"/>
    </xsl:template>
    <xsl:template match="/Errors">
        
        <xsl:element name="Errors">
            <xsl:apply-templates select="Error"/>                           
        </xsl:element>
                       
    </xsl:template>
   
   <xsl:template match="Error">
       <xsl:element name="Error">
           <xsl:copy-of select="@*|node()"/>
       </xsl:element>   
              
       
   </xsl:template>
    <xsl:template match="psws:ShipResult">
        <xsl:element name="Shipment">
            <xsl:attribute name="CarrierServiceCode"><xsl:value-of select="./pros:Service[1]"/></xsl:attribute>
            <xsl:attribute name="SCAC"><xsl:value-of select="'UPS'"/></xsl:attribute>
            <xsl:attribute name="ShipmentKey"><xsl:value-of select="./pros:ShipperReference"/></xsl:attribute>
            <xsl:element name="Containers">
                <xsl:apply-templates select="./pros:Packages/pros:ProShipPackage"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    <xsl:template match="pros:ProShipPackage">
        <xsl:element name="Container">
            <xsl:attribute name="ExtnB64Label"><xsl:value-of select="./ancestor::psws:ShipResult[1]/pros:CustomNodes/pros:ProShipCustomNodeItem[pros:NodePath='CCN_LABEL'][1]/pros:Value"/></xsl:attribute>
            <xsl:attribute name="BasicFreightCharge"><xsl:value-of select="./ancestor::psws:ShipResult[1]/pros:Total"/></xsl:attribute>
            <xsl:attribute name="ContainerScm"/>
            <xsl:attribute name="ShipmentContainerKey"><xsl:value-of select="./pros:MiscReference1"/></xsl:attribute>
            <xsl:attribute name="TrackingNo"><xsl:value-of select="./pros:TrackingNumber"/></xsl:attribute>
            <xsl:element name="Extn">
                <xsl:attribute name="ExtnLblPrintFormat"><xsl:value-of select="./ancestor::psws:ShipResult[1]/pros:CustomNodes/pros:ProShipCustomNodeItem[pros:NodePath='CCN_LABEL_OUTPUT_TYPE'][1]/pros:Value"/></xsl:attribute>
                <xsl:attribute name="ExtnVoidLabelId"><xsl:value-of select="./pros:PsVoidId"/></xsl:attribute>
            </xsl:element>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>

