<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
    xmlns:void="http://psws.proshipservices.com/">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/void:VoidResponse"  >
       
        <xsl:apply-templates select="void:VoidResult"/>
       
        
    </xsl:template>
    
    <xsl:template match="void:VoidResult">
        <xsl:element name="Container">
            <xsl:attribute name="ExtnResponse">
                <xsl:value-of select="current()"/>
            </xsl:attribute>
        </xsl:element>   
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
</xsl:stylesheet>
