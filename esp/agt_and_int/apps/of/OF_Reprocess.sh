#!/usr/bin/ksh

export REPROCESS_LOG=/logs/prod/of/of_reprocess.log
export INCOMING_DIR=/prod/apps/of/Reprocess/Incoming
date >> $REPROCESS_LOG
#Set outbound directory to read only.
ssh ag000038 chmod 755 /prod/file/Outbound >> $REPROCESS_LOG

# SCP files from ag000038 to am000050
scp -p ag000038:/prod/file/Outbound/* /prod/apps/of/Reprocess/Incoming >> $REPROCESS_LOG

#  Remove files from /prod/file/Outbound
ssh ag000038 rm -f /prod/file/Outbound/* >> $REPROCESS_LOG

#  Set Outbound directory back to writable
ssh ag000038 chmod 777 /prod/file/Outbound >> $REPROCESS_LOG

#  List files that were copied over.
for filename in `ls $INCOMING_DIR`
do
    echo "File to be Reprocessed: ${filename}"  >> $REPROCESS_LOG
done

exit

