<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:template match="/PathLists">
       <xsl:for-each select="./PathList[@Type='CopyDG']">
        	<MultiApi>
        		<API Name="manageDistributionRule">
        			<Input>
        				<DistributionRule Purpose="SOURCING" ItemGroupCode="PROD">
        					<xsl:attribute name="DistributionRuleId"><xsl:value-of select="./@DistributionRuleId" /></xsl:attribute>
        					<xsl:attribute name="OwnerKey"><xsl:value-of select="./@OwnerKey" /></xsl:attribute>
        					<ItemShipNodes>
        					 <xsl:for-each select="./PathElement">        						
        							<ItemShipNode ItemId="ALL" ActiveFlag="Y">
        								<xsl:for-each select="./Path">       
        								<xsl:if test="./@Name = '/ItemShipNodes/ItemShipNode/@ShipnodeKey'">
        									<xsl:attribute name="ShipnodeKey"><xsl:value-of select="./@Value" /></xsl:attribute>
        								</xsl:if>
        									<xsl:if test="./@Name = '/ItemShipNodes/ItemShipNode/@Priority'">
        									<xsl:attribute name="Priority"><xsl:value-of select="./@Value" /></xsl:attribute>
        								</xsl:if>
        									<xsl:if test="./@Name = '/ItemShipNodes/ItemShipNode/@EffectiveStartDate'">
        									<xsl:attribute name="EffectiveStartDate"><xsl:value-of select="./@Value" /></xsl:attribute>
        								</xsl:if> 
        							</xsl:for-each>              								
        							</ItemShipNode>	        					
        					 </xsl:for-each>
        					</ItemShipNodes>
        				</DistributionRule>	
        			</Input>
 				</API>
        	</MultiApi>
        	
       </xsl:for-each> 	
    </xsl:template>
</xsl:stylesheet>