<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
   
   <xsl:template match="PathLists">   
   		  	<xsl:element name="MultiApi">    
           <xsl:for-each select="./PathList[@Type='CopyCalendar']">
           	           			               
                     <xsl:element name="API">
	           <xsl:attribute name="Name" ><xsl:value-of select="'manageOrganizationHierarchy'" /></xsl:attribute>
	           <xsl:element name="Input">
	               <xsl:element name="Organization">
	               			<xsl:attribute name="Operation"><xsl:value-of select="'Modify'"/></xsl:attribute>
	               			<xsl:attribute name="OrganizationKey">
		               			<xsl:for-each select="./Path">
							   		<xsl:if test="./@Attribute = 'OrganizationCode'">		
										<xsl:value-of select="./@Value" />
									</xsl:if>            				         
							   </xsl:for-each>
						   </xsl:attribute>
						   	               			
	               			<xsl:element name="Node">
	               				<xsl:for-each select="./Path">
						   		<xsl:if test="./@Attribute='OrganizationCode'">		
									<xsl:attribute name="ShipNode"><xsl:value-of select="./@Value" /></xsl:attribute>
								</xsl:if>
								<xsl:if test="./@Attribute='CalendarKey'">		
									<xsl:attribute name="ShippingCalendarKey"><xsl:value-of select="./@Value" /></xsl:attribute>
								</xsl:if>               				         
						   		</xsl:for-each>               				
	               			</xsl:element>	
	               			        		                   
	               </xsl:element>
	           </xsl:element>
	       </xsl:element>                
       			          
           </xsl:for-each>
           </xsl:element> 
   </xsl:template> 
   
	
</xsl:stylesheet>
