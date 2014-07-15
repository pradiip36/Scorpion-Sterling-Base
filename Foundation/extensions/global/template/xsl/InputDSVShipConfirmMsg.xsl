<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl = "http://www.w3.org/1999/XSL/Transform" version = "1.0">
		<xsl:output indent="yes"/>
    	<xsl:template match="/">  
    	
    	<xsl:element name="Shipment">
			<xsl:attribute name="DocumentType"><xsl:value-of select="Shipment/@DocumentType"/></xsl:attribute>
			<xsl:attribute name="EnterpriseCode"><xsl:value-of select="Shipment/@EnterpriseCode"/></xsl:attribute>
			<xsl:attribute name="CarrierServiceCode"><xsl:value-of select="Shipment/@CarrierServiceCode"/></xsl:attribute>
			<xsl:attribute name="SellerOrganizationCode"><xsl:value-of select="Shipment/@SellerOrganizationCode"/></xsl:attribute>
			<xsl:attribute name="ShipNode"><xsl:value-of select="Shipment/@ShipNode"/></xsl:attribute>
			<xsl:attribute name="ShipmentNo"><xsl:value-of select="Shipment/@ShipmentNo"/></xsl:attribute>
			<xsl:attribute name="SCAC"><xsl:value-of select="Shipment/@SCAC"/></xsl:attribute>
			
			<ShipmentLines>
			<xsl:element name="ShipmentLine"> 
			<xsl:attribute name="ItemID"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ItemID"/></xsl:attribute>
			<xsl:attribute name="OrderNo"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ChainedFromOrderLineKey"/></xsl:attribute>
			<xsl:attribute name="ProductClass"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ProductClass"/></xsl:attribute>
			<xsl:attribute name="Quantity"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@Quantity"/></xsl:attribute>
			<xsl:attribute name="ShipmentLineNo"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@ShipmentLineNo"/></xsl:attribute>
			<xsl:attribute name="UnitOfMeasure"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@UnitOfMeasure"/></xsl:attribute>
			<xsl:attribute name="PurchaseOrderNo"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@OrderNo"/></xsl:attribute>
			<xsl:attribute name="PrimeLineNo"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@PrimeLineNo"/></xsl:attribute>
			<xsl:attribute name="SubLineNo"><xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/@SubLineNo"/></xsl:attribute>
			</xsl:element>
			</ShipmentLines>
			
			<Containers>
			<xsl:element name="Container"> 
			<xsl:attribute name="ContainerScm"><xsl:value-of select="Shipment/Containers/Container/@ContainerScm"/></xsl:attribute>
			<xsl:attribute name="TrackingNo"><xsl:value-of select="Shipment/Containers/Container/@TrackingNo"/></xsl:attribute>
			<xsl:attribute name="ContainerGrossWeight"><xsl:value-of select="Shipment/Containers/Container/@ContainerGrossWeight"/></xsl:attribute>
			<xsl:attribute name="ContainerGrossWeightUOM"><xsl:value-of select="Shipment/Containers/Container/@ContainerGrossWeightUOM"/></xsl:attribute>
			<ContainerDetails>
			<xsl:element name="ContainerDetail"> 
			<xsl:attribute name="Quantity"><xsl:value-of select="Shipment/Containers/Container/ContainerDetails/ContainerDetail/@Quantity"/></xsl:attribute>
			
			</xsl:element>
			</ContainerDetails>
			
			</xsl:element>
			
			</Containers>
			
			<ToAddress>
				<xsl:attribute name="AddressLine1"><xsl:value-of select="Shipment/ToAddress/@AddressLine1"/></xsl:attribute>
				<xsl:attribute name="AddressLine2"><xsl:value-of select="Shipment/ToAddress/@AddressLine2"/></xsl:attribute>
				<xsl:attribute name="City"><xsl:value-of select="Shipment/ToAddress/@City"/></xsl:attribute>
				<xsl:attribute name="Country"><xsl:value-of select="Shipment/ToAddress/@Country"/></xsl:attribute>
				<xsl:attribute name="DayPhone"><xsl:value-of select="Shipment/ToAddress/@DayPhone"/></xsl:attribute>
				<xsl:attribute name="FirstName"><xsl:value-of select="Shipment/ToAddress/@FirstName"/></xsl:attribute>
				<xsl:attribute name="LastName"><xsl:value-of select="Shipment/ToAddress/@LastName"/></xsl:attribute>
				<xsl:attribute name="State"><xsl:value-of select="Shipment/ToAddress/@State"/></xsl:attribute>
				<xsl:attribute name="ZipCode"><xsl:value-of select="Shipment/ToAddress/@ZipCode"/></xsl:attribute>
			</ToAddress>
			
		</xsl:element>	
					
	
	
</xsl:template>
</xsl:stylesheet>