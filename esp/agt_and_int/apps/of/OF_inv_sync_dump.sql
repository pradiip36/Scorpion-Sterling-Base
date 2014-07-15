SET pagesize 0
SET trimspool ON
SET echo off
SET feedback off
SET linesize 1000
SET serveroutput off
SET termout off
SET head off
SET trims ON
SET heading off
SET verify off


SELECT 'ITEM,' || 'WMS_QTY,' || 'OMS_QTY,' || 'CHANGE_TYPE' FROM DUAL;

SELECT EXTRACT(Xmltype(MESSAGE),'/Item/@ItemID').Getstringval()
  || ','
  || SUBSTR(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ExpectedQuantity').getStringVal(),1,LENGTH(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ExpectedQuantity').getStringVal())-3)
  || ','
  || SUBSTR(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ActualQuantity').getStringVal(),1,LENGTH(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ActualQuantity').getStringVal())-3)
  || ','
  || SUBSTR(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ChangedQuantity').getStringVal(),1,LENGTH(EXTRACT(xmltype(MESSAGE),'/Item/Supplies/Supply/@ChangedQuantity').getStringVal())-3)
FROM sterling.yfs_export
WHERE Flow_Name = 'MismatchInvExportSyncService'
AND TRIM(EXTRACT(Xmltype(message),'/Item/@YantraMessageGroupID').Getstringval())='&1'
AND CREATETS    > (TRUNC(sysdate) - 2); 
