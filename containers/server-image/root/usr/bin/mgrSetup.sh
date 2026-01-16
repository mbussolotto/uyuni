#!/bin/sh

echo "================ ENVIRONMENT VARIABLES ================"
env | sort
echo "======================================================="

if test -e /root/.MANAGER_SETUP_COMPLETE; then
  echo "Server appears to be already configured. Installation options may be ignored."
  exit 0
fi

if [ "$DEBUG_JAVA" = "true" ]; then
  # Note: $JAVA_OPTS inside single quotes is NOT expanded here. 
  # It assumes the target file is a shell script that will source this line later.
  echo 'JAVA_OPTS=" $JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=*:8003,server=y,suspend=n" ' >> /etc/tomcat/conf.d/remote_debug.conf
  echo 'JAVA_OPTS=" $JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=*:8001,server=y,suspend=n" ' >> /etc/rhn/taskomatic.conf
  echo 'JAVA_OPTS=" $JAVA_OPTS -Xdebug -Xrunjdwp:transport=dt_socket,address=*:8002,server=y,suspend=n" ' >> /usr/share/rhn/config-defaults/rhn_search_daemon.conf
fi

/usr/lib/susemanager/bin/mgr-setup
RESULT=$?

if test -n "$ADMIN_PASS"; then
  echo "starting tomcat..."
  # Start in background
  (su -s /usr/bin/sh -g tomcat -G www -G susemanager tomcat /usr/lib/tomcat/server start) &

  echo "starting apache2..."
  /usr/sbin/start_apache2 -k start

  echo "Creating first user..."
  
  if [ "$NO_SSL" = "Y" ]; then
	CURL_SCHEME="http"
  else
	CURL_SCHEME="-L -k https"
  fi

  echo "Waiting for Tomcat..."
  curl -o /tmp/curl-retry -s --retry 7 $CURL_SCHEME://localhost/rhn/newlogin/CreateFirstUser.do

  HTTP_CODE=$(curl -o /dev/null -s -w %{http_code} $CURL_SCHEME://localhost/rhn/newlogin/CreateFirstUser.do)
  
  if test "$HTTP_CODE" = "200"; then
    echo "Creating administration user"

    curl -s -o /tmp/curl_out \
      --data-urlencode "orgName=$ORG_NAME" \
      --data-urlencode "adminLogin=$ADMIN_USER" \
      --data-urlencode "adminPassword=$ADMIN_PASS" \
      --data-urlencode "firstName=$ADMIN_FIRST_NAME" \
      --data-urlencode "lastName=$ADMIN_LAST_NAME" \
      --data-urlencode "email=$MANAGER_ADMIN_EMAIL" \
      "$BASE_URL/rhn/manager/api/org/createFirst"

    if ! grep -q '^{"success":true' /tmp/curl_out ; then
      echo "Failed to create the administration user"
      cat /tmp/curl_out

    fi
    rm -f /tmp/curl_out
  elif test "$HTTP_CODE" = "403"; then
    echo "Administration user already exists, reusing"
  else
    RESULT=1
  fi
fi

exit $RESULT
