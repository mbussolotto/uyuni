#! /bin/bash

/sbin/sysctl -w kernel.shmmax=4067832832
su - postgres -c '/usr/lib/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ start'
cp /root/rhn.conf /etc/rhn/rhn.conf
mkdir -p /manager/python/spacewalk/reports
pytest -v --junit-xml /manager/python/spacewalk/reports/pgsql_tests.xml /manager/python/test/integration/
EXIT=$?
su - postgres -c '/usr/lib/postgresql/bin/pg_ctl -D /var/lib/pgsql/data/ stop'
exit $?
