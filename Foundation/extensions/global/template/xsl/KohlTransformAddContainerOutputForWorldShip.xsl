<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output indent="yes" />
     <xsl:template match="/">
     <OutPut>
         
              <xsl:attribute name="ShipmentNo">
                                         <xsl:value-of select="Shipment/@ShipmentNo" />
                                         </xsl:attribute>
	       <xsl:attribute name="ShipNode">
                                         <xsl:value-of select="Shipment/@ShipNode" />
                                         </xsl:attribute>
	       <xsl:attribute name="Action">
                 <xsl:text>Pack</xsl:text>
            </xsl:attribute>
                                                          
	
		<WorldShipFile>
		 <OpenShipments>
		  
		  <OpenShipment>
		   <xsl:attribute name="ShipmentOption">
		    <xsl:text></xsl:text>
		   </xsl:attribute>
		   <xsl:attribute name="ProcessStatus">
		    <xsl:text></xsl:text>
		   </xsl:attribute>
		   <ShipTo>
		    <xsl:element name="CompanyOrName">
		     <xsl:value-of select="Shipment/ToAddress/@FirstName"/>  
		     <xsl:text> </xsl:text>
		     <xsl:value-of select="Shipment/ToAddress/@LastName"/> 
		    </xsl:element>
		    <xsl:element name="Address1">
		     <xsl:value-of select="Shipment/ToAddress/@AddressLine1"/>  
		    </xsl:element>
		    <xsl:element name="Address2">
		     <xsl:value-of select="Shipment/ToAddress/@AddressLine2"/>  
		    </xsl:element>
		    <xsl:element name="CountryTerritory">
		     <xsl:value-of select="Shipment/ToAddress/@Country"/>  
		    </xsl:element>
		    <xsl:element name="PostalCode">
		     <xsl:value-of select="Shipment/ToAddress/@ZipCode"/>  
		    </xsl:element>
		    <xsl:element name="CityOrTown">
		     <xsl:value-of select="Shipment/ToAddress/@City"/> 
		      </xsl:element>
		     <xsl:element name="StateProvinceCounty">
		     <xsl:value-of select="Shipment/ToAddress/@State"/> 
		     </xsl:element>
		     <xsl:element name="Telephone">
		     <xsl:value-of select="Shipment/ToAddress/@DayPhone"/> 
		     </xsl:element>
		     <xsl:element name="EmailAddress">
		     <xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/Order/@CustomerEMailID"/> 
		    </xsl:element>
		   </ShipTo>
		   <ShipmentInformation>
		   <xsl:element name="ServiceType">
		     
		   <xsl:variable name="hhref" select="Shipment/ShipmentLines/ShipmentLine/OrderLine/@CarrierServiceCode" />
		   <xsl:variable name="hhref22" select="Shipment/@StampServiceType" />
                   <xsl:if test="$hhref22='Y'">
		     <xsl:value-of select="Shipment/@ServiceType"/>
		   </xsl:if>
		   <xsl:if test="$hhref22='N'">
		   <xsl:choose>
		    <xsl:when test="$hhref='Priority Air'">
		    <xsl:text>2DA</xsl:text>
		    </xsl:when>
		    <xsl:when test="$hhref='Standard Ground'">
		    <xsl:text>GND</xsl:text>
		    </xsl:when>
		    <xsl:when test="$hhref='PO Box'">
		    <xsl:text>SurePost</xsl:text>
		    </xsl:when>
		    <xsl:when test="$hhref='APO/FPO'">
		    <xsl:text>SurePost</xsl:text>
		    </xsl:when>
		    <xsl:otherwise>
	   		<xsl:text>GND</xsl:text>
     		</xsl:otherwise>
			</xsl:choose>
			  </xsl:if>
			</xsl:element>
			<xsl:element name="NumberOfPackages">
		     <xsl:text>1</xsl:text>
		    </xsl:element>
		    <xsl:element name="ShipmentActualWeight">
		    <xsl:variable name="hhref2" select="Shipment/@LatestContainerScm" />
		    <xsl:for-each select="Shipment/Containers/Container">
                     <xsl:if test="$hhref2=@ContainerScm">
		   <xsl:value-of select="@ContainerNetWeight"/>
		   </xsl:if>
		   </xsl:for-each>
		   
		   
		    </xsl:element>
		    <xsl:element name="DescriptionOfGoods">
		    <xsl:text>Kohls.com–</xsl:text>
		    <xsl:value-of select="Shipment/ShipmentLines/ShipmentLine/Order/@OrderNo"/> 
		    </xsl:element>
		    <xsl:element name="BillingOption">
		     <xsl:text>PP</xsl:text>
		    </xsl:element>
		  </ShipmentInformation>
		   <Package>
		    <xsl:element name="PackageType">		 
		     <xsl:text>Package</xsl:text>
		    </xsl:element>
		    <xsl:variable name="hhref2" select="Shipment/@LatestContainerScm" />
		    <xsl:for-each select="Shipment/Containers/Container">
                     <xsl:if test="$hhref2=@ContainerScm">
		    <xsl:element name="Length">
		     <xsl:value-of select="@ContainerLength"/>  
		    </xsl:element>
		    <xsl:element name="Width">
		     <xsl:value-of select="@ContainerWidth"/>  
		    </xsl:element>
		    <xsl:element name="Height">
		     <xsl:value-of select="@ContainerHeight"/>  
		    </xsl:element>
		     </xsl:if>
		   </xsl:for-each>
		    <xsl:element name="Reference1">
		     <xsl:value-of select="Shipment/@ShipmentNo"/>  
		    </xsl:element>
		    <xsl:element name="Reference2">
		    <xsl:variable name="hhref4" select="Shipment/@LatestContainerScm" />
		    <xsl:for-each select="Shipment/Containers/Container">
                     <xsl:if test="$hhref4=@ContainerScm">
		     <xsl:value-of select="@ContainerScm"/>
		   </xsl:if>
		   </xsl:for-each>
		      </xsl:element>
		     <xsl:element name="Reference3">
		     <xsl:value-of select="Shipment/@ShipNode"/> 
		     </xsl:element>
		     <xsl:element name="Reference4">
		      <xsl:text>UPS</xsl:text>
		     </xsl:element>
		     
		   </Package>

		  </OpenShipment>
		 </OpenShipments>
	    </WorldShipFile>
	    </OutPut>
      
   </xsl:template>
</xsl:stylesheet>