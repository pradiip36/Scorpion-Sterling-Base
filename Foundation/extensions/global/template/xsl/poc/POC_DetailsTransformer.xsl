<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cus="http://org.kohls.com/CustomerLookUpRequest" version="1.0">
	
	<xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<!-- root template  -->
	<xsl:template match="/">
		
		<xsl:element name="Item">
			
			<xsl:attribute name="ItemID">
				<xsl:value-of select="Item/@ItemID"/>
			</xsl:attribute>
			
			<xsl:attribute name="OrganizationCode">
				<xsl:value-of select="Item/@OrganizationCode"/>
			</xsl:attribute>
			
			<xsl:attribute name="UnitOfMeasure">
				<xsl:value-of select="Item/@UnitOfMeasure"/>
			</xsl:attribute>
			
			<xsl:attribute name="Dept">
				<xsl:value-of select="Item/Extn/@ExtnDept"/>
			</xsl:attribute>
			
			<xsl:attribute name="Class">
				<xsl:value-of select="Item/Extn/@ExtnClass"/>
			</xsl:attribute>
			
			<xsl:attribute name="SubClass">
				<xsl:value-of select="Item/Extn/@ExtnSubClass"/>
			</xsl:attribute>
			
			<xsl:attribute name="POCMerchandiseDescription">
				<xsl:value-of select="Item/AdditionalAttributeList/AdditionalAttribute/@Value"></xsl:value-of>
			</xsl:attribute>
			
		</xsl:element>
		
	</xsl:template>
	
</xsl:stylesheet>
