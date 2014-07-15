<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:gif="http://giftcard.poc.service.kohls.com/GiftCardService/" version="1.0">
  <xsl:output method="xml" encoding="UTF-8" indent="yes" />

  <!-- root template  -->
  <xsl:template match="/">
        <gif:paymentRequest>
                   
          <gif:requestType>
            <xsl:value-of select="PaymentRequest/Transaction/@RequestType"/>
          </gif:requestType>
          
		  <gif:transactionNumber>
		     <xsl:value-of select="PaymentRequest/Transaction/@TransactionNumber"/>
		  </gif:transactionNumber>

		  <gif:tenderAmount>
		     <xsl:value-of select="PaymentRequest/Transaction/@TenderAmount"/>
		  </gif:tenderAmount>

		  <!--<gif:driversLicense>
		     <xsl:value-of select="PaymentRequest/Transaction/@DriversLicense"/>
		  </gif:driversLicense>-->


		  <gif:registerNumber>
		  <xsl:value-of select="PaymentRequest/Transaction/@RegisterNumber"/>
		  </gif:registerNumber>

		  <gif:entryMethod>
		     <xsl:value-of select="PaymentRequest/Transaction/@EntryMethod"/>
		  </gif:entryMethod>

		  <gif:storeNumber>
		     <xsl:value-of select="PaymentRequest/Transaction/@StoreNumber"/>
		  </gif:storeNumber>
		  <!--<gif:SVPinNo>
		     <xsl:value-of select="PaymentRequest/Transaction/@SVPinNo"/>
		  </gif:SVPinNo>-->

		  <gif:SVCno>
		     <xsl:value-of select="PaymentRequest/Transaction/@SVCno"/>
		  </gif:SVCno>

		  <gif:operatorID>
		     <xsl:value-of select="PaymentRequest/Transaction/@OperatorID"/>
		  </gif:operatorID>

		  <gif:paymentType>
		     <xsl:value-of select="PaymentRequest/Transaction/@PaymentType"/>
		  </gif:paymentType>
         
         <xsl:if test="PaymentRequest/Transaction/@Protected">
        	<gif:protected>
        		 <xsl:value-of select="PaymentRequest/Transaction/@Protected"/>
			</gif:protected>
      	</xsl:if>
          
        </gif:paymentRequest>
      
  </xsl:template>

</xsl:stylesheet>