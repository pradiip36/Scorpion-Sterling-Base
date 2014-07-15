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
sh.shipnode_key AS str_nbr,
shl.order_no AS ORD_NBR,
sh.modifyts as PKT_STAT_LAST_UPD_TMST,
case when shc.NBR_OF_CARTS >0 then shc.NBR_OF_CARTS else 0 end as NBR_OF_CARTS,
case when shc.NBR_OF_CARTS >0 then 'Y' else 'N' end as BOL_IND,
case when sh.status = '1100.01' then 'I' when sh.status = '1400' then 'C' when sh.status = '9000' then'C' end as PKT_STAT_CDE
FROM sterling.yfs_shipment sh left join sterling.yfs_shipment_line shl on sh.shipment_key=shl.shipment_key 
left join (
select shipment_container_key,Shipment_key, count(Shipment_key) as NBR_OF_CARTS
from sterling.yfs_shipment_container group by shipment_container_key,shipment_key) shc 
on (sh.shipment_key=shc.shipment_key) 
where sh.createts > sysdate-30 and sh.pickticket_no not in (select extn_pick_ticket_no from sterling.yfs_order_release)
Union ALL
SELECT distinct(odr.extn_pick_ticket_no) AS pkt_nbr,
odr.shipnode_key AS str_nbr,
orNo as ORD_NBR,
odr.modifyts as PKT_STAT_LAST_UPD_TMST,
case when NBR_OF_CARTS >0 then NBR_OF_CARTS else 0 end as NBR_OF_CARTS,
case when NBR_OF_CARTS >0 then 'Y' else 'N' end as BOL_IND,
case when ors.dis_status <3700 and not ors.dis_status>=3700 then'I'
when ors.dis_status < 3700 and ors.dis_status >= 3700 then'P'
when ors.dis_status >= 3700 and not ors.dis_status <3700 then'C' end as PKT_STAT_CDE
FROM sterling.yfs_order_release odr 
left join (
select 
shl.order_release_key as orkey,
shl.shipment_key,
shl.order_no as orNo,
sum(shc.NBR_OF_CARTS) as NBR_OF_CARTS
from sterling.yfs_shipment_line shl inner join (
select Shipment_key, count(Shipment_key) as NBR_OF_CARTS
from sterling.yfs_shipment_container group by shipment_key) shc
on (shl.shipment_key=shc.shipment_key) group by shl.order_release_key, shl.shipment_key, shl.order_no) on
(odr.order_release_key=orkey) 
left join (select order_release_key, status as dis_status
from sterling.yfs_order_release_status where status_quantity>0 and status>=3200.03 group by order_release_key,status) ors on 
(odr.order_release_key=ors.order_release_key) 
where odr.createts > sysdate-30 and odr.extn_pick_ticket_no != ' ';

SPOOL OFF
EXIT
