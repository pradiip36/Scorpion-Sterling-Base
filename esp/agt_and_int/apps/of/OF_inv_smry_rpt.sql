--Declare local variables

  DECLARE
    inv_sync_id     varchar2(23);
    inv_sync_count  NUMBER;
    change_qty      NUMBER;
    expected_qty    NUMBER;
    actual_qty      NUMBER;
    total_adj       NUMBER;
    total_oms_items NUMBER;
    total_wms_items NUMBER;

  -- Adjustment Counters
    adj_p1          NUMBER;
    adj_p2          NUMBER;
    adj_p3          NUMBER;
    adj_p4          NUMBER;
    adj_p5          NUMBER;
    adj_n1          NUMBER;
    adj_n2          NUMBER;
    adj_n3          NUMBER;
    adj_n4          NUMBER;
    adj_n5          NUMBER;

  -- Total Counters
    total_pos_adj   NUMBER;
    total_neg_adj   NUMBER;
    total_variance  NUMBER;
    total_dsv_count NUMBER;
    total_zero_skus NUMBER;

    in_whse         varchar2(4) := &1;

    CURSOR c_sync_results IS
        SELECT
          to_number(SUBSTR(MESSAGE,instr(MESSAGE,'ChangedQuantity=',1,1) +17,instr(MESSAGE,'.00"',1,2)-(instr(MESSAGE,'ChangedQuantity=',1,1)+17)))  AS CHANGE_TYPE,
          to_number(SUBSTR(MESSAGE,instr(MESSAGE,'ExpectedQuantity=',1,1)+18,instr(MESSAGE,'.00"',1,3)-(instr(MESSAGE,'ExpectedQuantity=',1,1)+18))) AS EXPECTED_QTY
        FROM sterling.yfs_export
        WHERE Flow_Name                                                                   = 'MismatchInvExportSyncService'
        AND TRIM(EXTRACT(Xmltype(message),'/Item/@YantraMessageGroupID').Getstringval()) = inv_sync_id
        AND CREATETS                                                                    > (TRUNC(sysdate) - 2);

  BEGIN

  --Assign parameter inputs to variables

  -- Assign Values
  total_adj := 0;
  total_oms_items := 0;
  total_wms_items := 0;
  total_zero_skus := 0;

  adj_p1 := 0;
  adj_p2 := 0;
  adj_p3 := 0;
  adj_p4 := 0;
  adj_p5 := 0;
  adj_n1 := 0;
  adj_n2 := 0;
  adj_n3 := 0;
  adj_n4 := 0;
  adj_n5 := 0;

  total_pos_adj := 0;
  total_neg_adj := 0;

  total_variance := 0;
  total_dsv_count := 0;

  SELECT COUNT(*) INTO total_oms_items FROM sterling.yfs_inventory_supply
     WHERE TRIM(SHIPNODE_KEY) = in_whse
       AND QUANTITY > 0;

  SELECT MAX(yantra_message_group_id) INTO inv_sync_id
  FROM
    sterling.yfs_inventory_supply_temp
    WHERE yantra_message_group_id LIKE in_whse||'%'
      AND CREATETS > (trunc(sysdate) - 2);

 
  SELECT COUNT(*) INTO total_dsv_count
  FROM
	sterling.yfs_item yi , sterling.yfs_inventory_item yii,
  sterling.YFS_INVENTORY_SUPPLY_temp t
    WHERE yi.item_id =yii.item_id
    AND yii.inventory_item_key = t.inventory_item_key
             AND yi.item_type='DS'
             AND t.inventory_supply_temp_key >TO_CHAR (SYSDATE, 'YYYYMMDD')
             AND t.YANTRA_MESSAGE_GROUP_ID = inv_sync_id;

  SELECT COUNT(*) INTO total_wms_items
  FROM
    sterling.yfs_inventory_supply_temp yist
  WHERE yist.YANTRA_MESSAGE_GROUP_ID = inv_sync_id;

  FOR v_sync_results IN c_sync_results LOOP

    change_qty := v_sync_results.CHANGE_TYPE;
    expected_qty := v_sync_results.EXPECTED_QTY;
    --actual_qty := v_sync_results.ACTUAL_QTY;

    total_adj := total_adj + 1;

    IF change_qty = 1 THEN     adj_p1 := adj_p1 + 1;
    ELSIF change_qty = 2 THEN  adj_p2 := adj_p2 + 1;
    ELSIF change_qty = 3 THEN  adj_p3 := adj_p3 + 1;
    ELSIF change_qty = 4 THEN  adj_p4 := adj_p4 + 1;
    ELSIF change_qty > 4 THEN  adj_p5 := adj_p5 + 1;
    ELSIF change_qty = -1 THEN adj_n1 := adj_n1 + 1;
    ELSIF change_qty = -2 THEN adj_n2 := adj_n2 + 1;
    ELSIF change_qty = -3 THEN adj_n3 := adj_n3 + 1;
    ELSIF change_qty = -4 THEN adj_n4 := adj_n4 + 1;
    ELSIF change_qty < -4 THEN adj_n5 := adj_n5 + 1;
    END IF;

    IF expected_qty = 0 THEN
      total_zero_skus := total_zero_skus + 1;
    END IF;

    total_variance := total_variance + change_qty;

  END LOOP;

  total_pos_adj := (adj_p1 + adj_p2 + adj_p3 + adj_p4 + adj_p5);
  total_neg_adj := (adj_n1 + adj_n2 + adj_n3 + adj_n4 + adj_n5);

  -- Output information for subsequent processing

  DBMS_OUTPUT.PUT_LINE (inv_sync_id);

  -- need to check if the loop wasn't incremented (0 count)

  DBMS_OUTPUT.PUT_LINE (' ' || total_adj );
  DBMS_OUTPUT.PUT_LINE (' ' || adj_p1 || CHR(10));
  DBMS_OUTPUT.PUT_LINE (' ' || adj_p2 || CHR(10));
  DBMS_OUTPUT.PUT_LINE (' ' || adj_p3);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_p4);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_p5);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_n1);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_n2);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_n3);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_n4);
  DBMS_OUTPUT.PUT_LINE (' ' || adj_n5);
  DBMS_OUTPUT.PUT_LINE (' ' || total_pos_adj);
  DBMS_OUTPUT.PUT_LINE (' ' || total_neg_adj);
  DBMS_OUTPUT.PUT_LINE (' ' || total_variance);
  DBMS_OUTPUT.PUT_LINE (' ' || total_oms_items);
  DBMS_OUTPUT.PUT_LINE (' ' || total_dsv_count);
  DBMS_OUTPUT.PUT_LINE (' ' || total_zero_skus);
  DBMS_OUTPUT.PUT_LINE (' ' || total_wms_items);

  END;
