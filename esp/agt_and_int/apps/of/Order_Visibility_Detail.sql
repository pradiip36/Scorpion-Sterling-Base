SET ECHO OFF
SET PAUSE OFF
SET TAB OFF
SET TIMING OFF
SET TRIMOUT ON
SET TRIMSPOOL ON
SET TERMOUT ON
SET FEEDBACK ON

DEFINE spool_file_name = &1

SPOOL &&spool_file_name

SELECT distinct(sh.pickticket_no) AS pkt_nbr, 
orno as ORD_NBR,
SKU_NBR_TM as SKU_NBR,
LNE_NBR_TM as LNE_NBR,
case when sh.status = '1100.01' then 'I' when sh.status = '1400' then'S' when sh.status = '9000' then'C' end as LNE_STAT_CDE,
LNE_REQD_QTY_TM as LNE_REQD_QTY,
LNE_SNT_QTY_TM as LNE_SNT_QTY 
FROM sterling.yfs_shipment sh left join (
select shl.shipment_key as shkey,
shl.order_line_key,
shl.order_no AS orno,
SKU_NBR_TM_K as SKU_NBR_TM,
LNE_NBR_TM_K as LNE_NBR_TM,
LNE_REQD_QTY_TM_K as LNE_REQD_QTY_TM,
LNE_SNT_QTY_TM_K as LNE_SNT_QTY_TM
from sterling.yfs_shipment_line shl inner join (
select odl.item_id as SKU_NBR_TM_K,
odl.prime_line_no as LNE_NBR_TM_K,
odl.original_ordered_qty as LNE_REQD_QTY_TM_K,
odl.shipped_quantity as LNE_SNT_QTY_TM_K,
odl.order_line_key
from sterling.yfs_order_line odl) odl
on (shl.order_line_key =odl.order_line_key) group by shl.shipment_key, shl.order_line_key, 
shl.order_no,SKU_NBR_TM_K,LNE_NBR_TM_K,LNE_NBR_TM_K,LNE_REQD_QTY_TM_K,LNE_SNT_QTY_TM_K) on
(sh.shipment_key=shkey) 
where sh.createts > sysdate-90 and sh.pickticket_no not in (select extn_pick_ticket_no from sterling.yfs_order_release)
Union ALL
SELECT distinct(odr.extn_pick_ticket_no) AS pkt_nbr,
orno as ORD_NBR,
SKU_NBR_TM as SKU_NBR,
LNE_NBR_TM as LNE_NBR,
case when displaystatus = 9000 then 'C' 
when displaystatus <3700 and not displaystatus>=3700 then'I'
when displaystatus < 3700 and displaystatus >= 3700 then'P'
when displaystatus >= 3700 and not displaystatus <3700 then'S' end as LNE_STAT_CDE,
LNE_REQD_QTY_TM as LNE_REQD_QTY,
LNE_SNT_QTY_TM as LNE_SNT_QTY
from sterling.yfs_order_release odr left join (
select distinct odl.order_line_key as odlinekey, odl.order_header_key as oddheadkey,
extn_ship_alone as shipalone,
odl.item_id as SKU_NBR_TM,
odl.prime_line_no as LNE_NBR_TM,
odl.original_ordered_qty as LNE_REQD_QTY_TM,
odl.shipped_quantity as LNE_SNT_QTY_TM,
odrs.disstate as displaystatus
from sterling.yfs_order_line odl 
inner join (
select order_release_key, order_line_key as odrlnkey, status as disstate
from sterling.yfs_order_release_status where status_quantity>0 and status>=3200.03 group by order_line_key,order_release_key,status) odrs
on (odl.order_line_key=odrlnkey))
on (odr.order_header_key=oddheadkey) left join (
select order_header_key, order_no as orno 
from sterling.yfs_order_header group by order_header_key, order_no) od on (odr.order_header_key=od.order_header_key)
where odr.createts > sysdate-90 and odr.extn_pick_ticket_no != ' ' and shipalone != 'Y';

SPOOL OFF
EXIT
