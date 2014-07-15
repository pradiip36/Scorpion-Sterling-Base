WHENEVER OSERROR EXIT 16 
WHENEVER SQLERROR EXIT 12
SET ECHO OFF
SET PAUSE OFF
SET TAB OFF
SET TIMING OFF
SET TRIMOUT ON
SET TRIMSPOOL ON
SET TERMOUT ON
SET SHOWMODE ON
SET FEEDBACK ON
SET VERIFY ON

DEFINE spool_file_name = &1
DEFINE num_days_back = &2
SET SHOWMODE OFF
SET VERIFY OFF
SPOOL &&spool_file_name

SELECT extn_pick_ticket_no
FROM   sterling.yfs_order_release
WHERE  supplier_code IN (809, 873)
       AND modifyprogid = 'SENT_TO_WMOS'
       AND modifyts BETWEEN SYSDATE - &&num_days_back
                    AND     SYSDATE
MINUS  
(SELECT pkt_ctrl_nbr
 FROM   pkt_dtl@ol090055.kohls.com
 WHERE  create_date_time > SYSDATE - &&num_days_back
 UNION
 SELECT pkt_ctrl_nbr
 FROM   pkt_dtl@ol0092.kohls.com
 WHERE  create_date_time > SYSDATE - &&num_days_back);

SPOOL OFF
EXIT

