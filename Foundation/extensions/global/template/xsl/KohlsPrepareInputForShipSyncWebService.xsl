<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:psws="http://psws.proshipservices.com/"
    xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices" version="1.0" >
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/">
        <psws:Ship xmlns:psws="http://psws.proshipservices.com/" xmlns:pros="http://schemas.datacontract.org/2004/07/ProShipWebServices">
            <xsl:apply-templates/>
        </psws:Ship>
   </xsl:template>
    <xsl:template match="Container">
        <!-- determine the service level -->
        <xsl:variable name="service">
            <xsl:call-template name="local-determine-service-level">
                <xsl:with-param name="container" select="current()"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:element name="psws:shipment">
            <xsl:element name="pros:ConsigneeAddress1"><xsl:value-of select="./Shipment/ToAddress/@AddressLine1"/></xsl:element>
            <xsl:element name="pros:ConsigneeAddress2"><xsl:value-of select="./Shipment/ToAddress/@AddressLine2"/></xsl:element>
            <xsl:element name="pros:ConsigneeCity"><xsl:value-of select="./Shipment/ToAddress/@City"/></xsl:element>
            <xsl:element name="pros:ConsigneeContact"><xsl:value-of select="concat(concat(./Shipment/ToAddress/@FirstName, ' ') , ./Shipment/ToAddress/@LastName)"/></xsl:element>
            <xsl:element name="pros:ConsigneeCountry"><xsl:value-of select="./Shipment/ToAddress/@Country"/></xsl:element>
            <xsl:element name="pros:ConsigneePhone"><xsl:value-of select="./Shipment/ToAddress/@DayPhone"/></xsl:element>
            <xsl:element name="pros:ConsigneePostalcode"><xsl:value-of select="/Container/Shipment/ToAddress/@ZipCode"/></xsl:element>
            <xsl:element name="pros:ConsigneeResidential"><xsl:value-of select="'True'"/></xsl:element>
            <xsl:element name="pros:ConsigneeState"><xsl:value-of select="/Container/Shipment/ToAddress/@State"/></xsl:element>
            <xsl:element name="pros:CustomNodes">
                <xsl:element name="pros:ProShipCustomNodeItem">
                    <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_CARTON_ID'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="/Container/@ContainerNo"/></xsl:element>
                </xsl:element>
                <!-- Start changes for SFS-June Release changes-->
                 <xsl:element name="pros:ProShipCustomNodeItem">
				 <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_CUSTOMER_ORDER_DATE'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="/Container/Shipment/@OrderDate"/></xsl:element>
              	</xsl:element>
              	<!-- End changes for SFS-June Release Proship changes -->
                <xsl:element name="pros:ProShipCustomNodeItem">
                    <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_SERVICE_LEVEL'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="$service"/></xsl:element>
                </xsl:element>
                <xsl:element name="pros:ProShipCustomNodeItem">
                    <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_LABEL_OUTPUT_TYPE'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="/Container/Extn/@ExtnLblPrintFormat"/></xsl:element>
                </xsl:element>
                <xsl:element name="pros:ProShipCustomNodeItem">
                    <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_KOHLS_DESCRIPTION'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="concat('Kohls.com - ', /Container/ContainerDetails/ContainerDetail[1]/ShipmentLine[1]/@OrderNo)"/></xsl:element>
                </xsl:element>
                <xsl:element name="pros:ProShipCustomNodeItem">
                    <xsl:element name="pros:NodePath"><xsl:value-of select="'CCN_KOHLS_STORE_NUMBER'"/></xsl:element>
                    <xsl:element name="pros:Value"><xsl:value-of select="/Container/Shipment/ShipNode/@ShipnodeKey"/></xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="pros:Packages">
                <xsl:element name="pros:ProShipPackage">
                    <xsl:element name="pros:Dimension"><xsl:value-of select="concat(concat(concat(concat(/Container/@ContainerLength, 'x') , /Container/@ContainerWidth), 'x'), /Container/@ContainerHeight)"/></xsl:element>
                    <xsl:element name="pros:MiscReference1"><xsl:value-of select="/Container/@ShipmentContainerKey"/></xsl:element>
                    <xsl:element name="pros:Packaging"><xsl:value-of select="'CUSTOM'"/></xsl:element>
                    <xsl:element name="pros:Weight"><xsl:value-of select="/Container/@ActualWeight"/></xsl:element>
                </xsl:element>
            </xsl:element>
            <xsl:element name="pros:ShipperReference"><xsl:value-of select="/Container/@ShipmentKey"/></xsl:element>
            <xsl:element name="pros:Terms"><xsl:value-of select="'SHIPPER'"/></xsl:element>
        </xsl:element>
    </xsl:template>
    <!--
        /**
         * A template that determines the service level of the shipment based on the requested
         * carrier service code.
         *
         * @param the container
         * @return the service level, either Priority or Standard
         */
    -->
    <xsl:template name="local-determine-service-level">
        <xsl:param name="container"/>
        <xsl:variable name="serviceCode" select="$container/Shipment/@RequestedCarrierServiceCode"/>
        <xsl:variable name="service">
            <xsl:choose>
                <xsl:when test="$serviceCode='Priority Air'">
                    <xsl:value-of select="'Priority'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'Standard'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:value-of select="$service"/>
    </xsl:template>
</xsl:stylesheet>