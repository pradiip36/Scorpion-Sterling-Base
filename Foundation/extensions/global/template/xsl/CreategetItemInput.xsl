<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
  <xsl:template match="/">
  
       <Item>
                <xsl:attribute name="ItemID">
                       <xsl:value-of select="Item/@ItemID" />
                </xsl:attribute>
                
                <xsl:attribute name="ItemKey">
					<xsl:value-of select="Item/@ItemKey" />
                </xsl:attribute>
                
                <xsl:attribute name="OrganizationCode">
					<xsl:value-of select="Item/@OrganizationCode"/>
				</xsl:attribute>

				<xsl:attribute name="UnitOfMeasure">
					<xsl:value-of select="Item/@UnitOfMeasure"/>
				</xsl:attribute>
				
       </Item>  
  
  
  </xsl:template>	
	
</xsl:stylesheet>
