<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
   <xsl:template match="/">
        <xsl:apply-templates/>   
   </xsl:template>
   <xsl:template match="PathLists">
       <xsl:element name="MultiApi">
           <xsl:for-each select="./PathList[@Type='CopyResourcePool']">
               <xsl:variable name="modelKey" select="./@OrganizationCode"/>
               <xsl:if test="generate-id(current()) = generate-id(/PathLists/PathList[@Type='CopyResourcePool' and @OrganizationCode=$modelKey][1])">
                   <xsl:apply-templates select="current()"/>
               </xsl:if>
           </xsl:for-each>
       </xsl:element>
   </xsl:template>
   <xsl:template match="PathList">
       <xsl:element name="API">
           <xsl:copy-of select="./@FlowName"/>
           <xsl:element name="Input">
               <xsl:element name="ResourcePool">
                   <xsl:attribute name="ResourcePoolId"><xsl:value-of select="./@OrganizationCode"/></xsl:attribute>
				   <xsl:attribute name="Node"><xsl:value-of select="./@OrganizationCode"/></xsl:attribute>
				   <xsl:attribute name="CapacityOrganizationCode"><xsl:value-of select="./@CapacityOrganizationCode"/></xsl:attribute>
               </xsl:element>
           </xsl:element>
       </xsl:element>
   </xsl:template>
</xsl:stylesheet>
