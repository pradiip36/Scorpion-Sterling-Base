<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:tem="http://tempuri.org/"
  version="1.0">
  <xsl:output method="xml" encoding="UTF-8" indent="yes" />
  
  
  <!-- root template  -->
  <xsl:template match="/">
    <tem:PLULookup>
      <tem:xmlIn>
        <MESSAGE>
          <xsl:if test="MESSAGE/@Content!=''">
            <xsl:attribute name="Content">
              <xsl:value-of select="MESSAGE/@Content"/>
            </xsl:attribute>
          </xsl:if>
          
          <xsl:if test="MESSAGE/@type!=''">
            <xsl:attribute name="type">
              <xsl:value-of select="MESSAGE/@type"/>
            </xsl:attribute>
          </xsl:if>
          
          <xsl:if test="MESSAGE/@Type!=''">
            <xsl:attribute name="Type">
              <xsl:value-of select="MESSAGE/@Type"/>
            </xsl:attribute>
          </xsl:if>
          
          <Component>
            <Parameters>
              
              <xsl:if test="MESSAGE/Component/Parameters/@TRANDATETIME!=''">
                <TRANDATETIME>
                  <xsl:value-of select="MESSAGE/Component/Parameters/@TRANDATETIME"/>
                </TRANDATETIME>
              </xsl:if>
              
              <LOOKUPTYPE>
                <xsl:value-of select="MESSAGE/Component/Parameters/@LookupType"/>
              </LOOKUPTYPE>
              
              
              <KEY>
                <xsl:value-of select="MESSAGE/Component/Parameters/@SKU"/> 
              </KEY>
              
              
              <STORE>
                <xsl:value-of select="MESSAGE/Component/Parameters/@Store"/>
              </STORE>
              
            </Parameters>
          </Component>
        </MESSAGE>
      </tem:xmlIn>
    </tem:PLULookup>
    
  </xsl:template>
  
  
</xsl:stylesheet>
