<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
   <xsl:template match="/">
        <xsl:apply-templates/>   
   </xsl:template>
   <xsl:template match="PathLists">
       <xsl:element name="MultiApi">
           <xsl:for-each select="./PathList[@Type='CopyOrganization']">
               <xsl:variable name="modelKey" select="./@ModelOrganizationKey"/>
               <xsl:if test="generate-id(current()) = generate-id(/PathLists/PathList[@Type='CopyOrganization' and @ModelOrganizationKey=$modelKey][1])">
                   <xsl:apply-templates select="current()"/>
               </xsl:if>
           </xsl:for-each>
       </xsl:element>
   </xsl:template>
   <xsl:template match="PathList">
       <xsl:element name="API">
           <xsl:copy-of select="./@FlowName"/>
           <xsl:element name="Input">
               <xsl:element name="Organization">
                   <xsl:attribute name="OrganizationKey"><xsl:value-of select="./@ModelOrganizationKey"/></xsl:attribute>
               </xsl:element>
           </xsl:element>
       </xsl:element>
   </xsl:template>
</xsl:stylesheet>
