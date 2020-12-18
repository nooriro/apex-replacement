#!/system/bin/sh
MODDIR=/data/adb/modules/apex-replacement
LOG=/data/local/tmp/apex-replacement-log.txt
: > $LOG

function log_ls() {
  local SEP="------------------------------------"
  ls -alZid $1* >> $LOG; echo $SEP >> $LOG
}

function replace_apex() {
  local DIR="/apex/$1@$2"
  local DIR2="/apex/$1"
  local APEX_PAYLOAD_ZIP="$MODDIR/apex_payload_zip/$3_$2.zip"
  
  echo 'replace_apex()' >> $LOG
  echo '$#='$# >> $LOG
  echo '$1='$1 >> $LOG
  echo '$2='$2 >> $LOG
  echo '$3='$3 >> $LOG
  echo '$DIR='$DIR >> $LOG
  echo '$DIR2='$DIR2 >> $LOG
  echo '$APEX_PAYLOAD_ZIP='$APEX_PAYLOAD_ZIP >> $LOG
  
  [ $# -lt 3 ] && return 1
  [ -e "$DIR" ] && return 2
  log_ls $DIR2
  
  mkdir $DIR
  chmod 0755 $DIR
  chown system:system $DIR
  chcon u:object_r:system_file:s0 $DIR
  log_ls $DIR2
  
  unzip -qo $APEX_PAYLOAD_ZIP -d $DIR
  
  find $DIR/* -type d -exec chmod 0755 {} \;
  find $DIR/* -type d -exec chown root:shell {} \;
  chmod 0700 $DIR/lost+found
  chown root:root $DIR/lost+found
  find $DIR -type f -exec chmod 0644 {} \;
  find $DIR -type f -exec chown system:system {} \;
  
  umount $DIR2
  log_ls $DIR2
  
  mount $DIR $DIR2
  log_ls $DIR2
  
  return 0
}

replace_apex com.android.cellbroadcast 300900710 cellbroadcast
