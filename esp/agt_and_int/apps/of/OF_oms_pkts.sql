SET ECHO OFF
REM ********************************************************************
REM Name:         OF_oms_pkts.sql
REM Author:       Craig Kehoe
REM Date Created: 2011, May
REM Date Changed: 2011, September
REM Date Changed: 2013, May - Mike Phillips 
REM ********************************************************************
REM
REM Purpose
REM -------
REM To retrieve the list of order-release picktickets sent from OMS
REM to the WMOSes within the specified datetimes.
REM
REM Mapping abbreviations
REM ---------------------
REM SA = ShipAlone = SA
REM ST = Standard (Non-ShipAlone)
REM PH = pkt_hdr = PH
REM OR = yfs_order_release
REM SH = yfs_shipment
REM
REM Mapping between WMOS and OMS
REM ----------------------------
REM WMOS PH.cust_po_nbr       <- OMS OR.order_release_key
REM WMOS PH.whse              <- OMS OR.supplier_code
REM WMOS PH.pkt_ctrl_nbr (ST) <- OMS OR.extn_pick_ticket_no
REM WMOS PH.pkt_ctrl_nbr (SA) <- OMS SH.pickticket_no
REM
REM Query Outline
REM -------------
REM 1. Filter the yfs_order_release table to the open order_releases in
REM    our date range, and that have a supplier code consisting only
REM    digits (WMOS whse values only consist of digits, and OMS has in
REM    the past filled the supplier code with alpha characters,
REM    eg. 'SVC').
REM 2. Left-join #1 to the order statuses that are complete, and then
REM    keep only those order releases that are incomplete.
REM 3. Inner-join #2 to the order header table to get order numbers.
REM 4. Left-join #3 to the shipment line table in order to get shipment
REM    keys for those order releases that have shipment lines (most are
REM    standardn(not ship-alone), so most will not have shipment lines).
REM 5. Left-join #4 to the shipment table in order to get the pickticket
REM    number for those order releases that are ship-alone orders.
REM ********************************************************************
WHENEVER OSERROR EXIT 16
WHENEVER SQLERROR EXIT 12
SET LINESIZE 82
SET HEADING OFF
SET FEEDBACK OFF
SET PAGESIZE 0
SET TAB OFF
SET PAUSE OFF
SET TAB OFF
SET TIMING OFF
SET TRIMOUT ON
SET TRIMSPOOL ON
SET TERMOUT ON
SET VERIFY OFF

CLEAR COLUMNS
DEFINE spool_file_name = &1
DEFINE max_days_back = &2
DEFINE min_days_back = &3

-- Compute the dates here--saves a second or two of processing time to
-- do it here than to do it in the main query.
COLUMN computed_min_date NOPRINT NEW_VALUE min_date
COLUMN computed_max_date NOPRINT NEW_VALUE max_date
SELECT TO_CHAR(SYSDATE - &max_days_back, 'YYYY-MM-DD"T"HH24:MI:SS')
       AS computed_min_date
      ,TO_CHAR(SYSDATE - &min_days_back, 'YYYY-MM-DD"T"HH24:MI:SS')
       AS computed_max_date
FROM   dual;

SPOOL &spool_file_name
SET TERMOUT OFF

-- Main query
SELECT        '{'
           || 'cust_po_nbr: ' || H.order_no
           || ', '
           || 'whse: '        || O.supplier_code
           || ', '
           || CASE O.extn_pick_ticket_no
                WHEN ' '
                  THEN
                       'pkt_ctrl_nbr: ' || S.pickticket_no
                    || ', '
                    || 'ord_type: ShipAlone'
                  ELSE
                       'pkt_ctrl_nbr: ' || O.extn_pick_ticket_no
                    || ', '
                    || 'ord_type: Standard'
                  END
          || '}' AS json_record
FROM      (SELECT    R.order_header_key
                    ,R.order_release_key
                    ,R.supplier_code
                    ,R.extn_pick_ticket_no
           FROM      (SELECT order_header_key
                            ,order_release_key
                            ,supplier_code
                            ,extn_pick_ticket_no
                      FROM   STERLING.yfs_order_release
                      WHERE  modifyts
                                 BETWEEN
                                   TO_DATE('&min_date'
                                          ,'YYYY-MM-DD"T"HH24:MI:SS')
                                 AND
                                   TO_DATE('&max_date'
                                          ,'YYYY-MM-DD"T"HH24:MI:SS')
                             AND supplier_code IN ('809','819','873','829')             
                             AND modifyprogid IN ('SENT_TO_WMOS'
                                                 ,'RELEASE.0001')
                             AND REGEXP_LIKE(supplier_code
                                            ,'^[[:digit:]]+$'))  R
           LEFT JOIN (SELECT order_header_key
                            ,order_release_key
                            ,status
                      FROM   STERLING.yfs_order_release_status
                      WHERE  status = '3700') T
                     ON R.order_release_key = T.order_release_key
           WHERE     T.status IS NULL) O
JOIN      STERLING.yfs_order_header H
          ON O.order_header_key = H.order_header_key
LEFT JOIN STERLING.yfs_shipment_line L
          ON     H.order_no = L.order_no
             AND O.order_release_key = L.order_release_key
             AND L.shipment_line_no = 1
             AND L.shipment_sub_line_no = 0
LEFT JOIN STERLING.yfs_shipment S
          ON     L.shipment_key = S.shipment_key
             AND S.order_release_key IS NULL
             AND S.order_header_key IS NULL
             AND S.order_no IS NULL;

SPOOL OFF
EXIT

