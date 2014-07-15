<?xml version="1.0" encoding="utf-8"?>
<project name="kohlsocf" author="KOHLSOCF">
<target name="seaVendorSearch" file="/extn/scripts/seaVendorSearch.js" allowDynamicLoad="true" debug="true"
		shorthand="false" shorthand-list="" >
		<include name="/extn/sbc/kohls/item/eligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/BasicVendorSearch_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/BasicVendorSearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/VendorSearchResults_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/VendorSearchResults.js" />
	</target>
	<target name="seaVendorSearchForInelg" file="/extn/scripts/seaVendorSearchForInelg.js" allowDynamicLoad="true" debug="true"
		shorthand="false" shorthand-list="" >
		<include name="/extn/sbc/kohls/item/ineligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/BasicVendorSearch_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/BasicVendorSearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/VendorSearchResults_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/VendorSearchResults.js" />
	</target>
	<target name="sbckohlsitemineligibility" file="/extn/scripts/sbckohlsinelglatedtasks.js" allowDynamicLoad="true" debug="true"
		depends="">
		<include name="/extn/sbc/common/relatedtasks/actions/kohlsaddineligibility.js" />
		<include name="/extn/sbc/common/relatedtasks/actions/kohlslistineligibility.js" />
	</target>
	<target name="seaItemSearch3" file="/extn/scripts/seaitemsearch3.js" allowDynamicLoad="true" debug="true" shorthand="false"
		shorthand-list="" depends="seaSearch;sbckohlsitemineligibility">
		<include name="/extn/sbc/kohls/item/ineligibility/basicitemsearch_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/basicitemsearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/itemsearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/itemsearchresult_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/itemsearchresult.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/KohlsAddDatesPopUp_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/KohlsAddDatesPopUp.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/BasicVendorSearch_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/BasicVendorSearch.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/VendorSearchResults_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/VendorSearchResults.js" />
	</target>
	<target name="itemElgPopup" file="/extn/scripts/itemElgPopup.js" allowDynamicLoad="true" debug="true" shorthand="false" shorthand-list="" depends="">		
		<include name="/extn/sbc/kohls/item/eligibility/itemElgPopup_config.js"/>
		<include name="/extn/sbc/kohls/item/eligibility/itemElgPopup.js"/>	
		<include name="/extn/sbc/kohls/item/eligibility/selectitemelgpopup_config.js"/>
		<include name="/extn/sbc/kohls/item/eligibility/selectitemelgpopup.js"/>			
	</target>
	<target name="seaItemSearch4" file="/extn/scripts/seaitemsearch4.js" allowDynamicLoad="true" debug="true" shorthand="false"
		shorthand-list="" depends="seaSearch;sbckohlsitemineligibility;itemElgPopup">
		<include name="/extn/sbc/kohls/item/eligibility/basicitemsearch_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/basicitemsearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/itemsearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/itemsearchresult_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/itemsearchresult.js" />		
		<include name="/extn/sbc/kohls/item/eligibility/vendorsearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/BasicVendorSearch_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/BasicVendorSearch.js" />
		<include name="/extn/sbc/kohls/item/eligibility/VendorSearchResults_config.js" />
		<include name="/extn/sbc/kohls/item/eligibility/VendorSearchResults.js" />
	</target>
		<target name="seaKohlsItemSearch" file="/extn/scripts/seaKohlsItemSearch.js" allowDynamicLoad="true" debug="true" shorthand="false"
			shorthand-list="">
		<include name="/extn/sbc/common/relatedtasks/actions/kohlsaddineligibility.js" />
		<include name="/extn/sbc/common/relatedtasks/actions/kohlslistineligibility.js" />
			<include name="/extn/sbc/kohls/item/ineligibility/KohlsItemSearch_config.js" />
			<include name="/extn/sbc/kohls/item/ineligibility/KohlsItemSearch.js" />
	</target>
	<target name="seaKohlsItemInelgList" file="/extn/scripts/seaKohlsItemInelgList.js" allowDynamicLoad="true" debug="true" shorthand="false"
		shorthand-list="" depends="">
		<include name="/extn/sbc/common/relatedtasks/actions/kohlsaddineligibility.js" />
		<include name="/extn/sbc/common/relatedtasks/actions/kohlslistineligibility.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/ViewItemIneligibility_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/ViewItemIneligibility.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/KohlsModifyDatesPopUp_config.js" />
		<include name="/extn/sbc/kohls/item/ineligibility/KohlsModifyDatesPopUp.js" />
	</target>
	<!-- for Safety Factor Adjustment feature-->
	
	<target name="sbcKohlsSafetyFactorDetailScreen" file="/extn/scripts/sbcKohlsSafetyFactorDetailScreen.js" allowDynamicLoad="true" debug="true" shorthand="false"
		shorthand-list="" depends="sbcManageCatalogDetail">
		<include name="/extn/sbc/kohls/item/safetyfactor/sbcKohlsSafetyFactorDetailScreen_config.js" />
		<include name="/extn/sbc/kohls/item/safetyfactor/sbcKohlsSafetyFactorDetailScreen.js" />
	</target>
	
	<!-- for Safety Factor Adjustment feature -->
</project>