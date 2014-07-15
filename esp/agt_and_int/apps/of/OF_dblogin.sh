case ${1} in
  efc1 ) DBLOGIN='readonly/readonly@OP090055_STB' ;;
  efc2 ) DBLOGIN='readonly/readonly@OP0092_STB' ;;
  efc3 ) DBLOGIN='readonly/readonly@OP0104_STB' ;;
  efc4 ) DBLOGIN='wsteam/wsteam@OP0132_STB' ;;
  oms  ) . ${KPSH}/ksetdblogin of ;;
esac

export DBLOGIN

