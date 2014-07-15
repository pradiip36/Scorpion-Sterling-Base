<script type="text/javascript">

var tcp_session_cookie_name = "PD-H-SESSION-ID";
var ssl_session_cookie_name = "PD-S-SESSION-ID";

function getCookie(cookieName) {
var docCookie =" " + document.cookie;

var cookieindex = docCookie.indexOf(" "+ cookieName + "=");
if (cookieindex == -1) cookieindex = docCookie.indexOf(";"+cookieName+"=");
if (cookieindex == -1 || cookieName=="") return "";
var cookievalueindex = docCookie.indexOf(";", cookieindex + 1);
if (cookievalueindex == -1) cookievalueindex = cookieindex.length; 
var cookie_value = unescape(docCookie.substring(cookieindex + cookieName.length+2,cookievalueindex));

     return cookie_value;
 }

function launchSIM() {
var tcp_session_cookie_value = getCookie(tcp_session_cookie_name);
var ssl_session_cookie_value = getCookie(ssl_session_cookie_name);

var simUrl = "ibmsterlingrcp://" + tcp_session_cookie_name + "-" + tcp_session_cookie_value + "_NEXT_" + ssl_session_cookie_name + "-" + ssl_session_cookie_value + "_END_" ;
window.open(simUrl,"_blank","status=no,location=no,menubar=no,toolbar=no,resizable=yes,width=243,height=215,directories=no;");
 }

document.body.onload=launchSIM;
</script>
</head>

<yfc:i18n>SIM LAUNCHED</yfc:i18n>
<!--<input type="button" onclick="launchSIM();" name="LaunchSIM" value="Launch SIM"/>-->