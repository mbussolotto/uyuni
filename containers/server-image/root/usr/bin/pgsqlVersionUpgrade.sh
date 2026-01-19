#!/bin/bash
echo "PostgreSQL version upgrade"

# Define backup directory based on the env var BACKUP_PATH passed from Go
BACKUP_DIR="${BACKUP_PATH}/backup"

echo "Testing presence of postgresql$NEW_VERSION..."
test -d /usr/lib/postgresql$NEW_VERSION/bin
echo "Testing presence of postgresql$OLD_VERSION..."
test -d /usr/lib/postgresql$OLD_VERSION/bin

echo "Create a database backup at $BACKUP_DIR"
test -d "$BACKUP_DIR" && mv "$BACKUP_DIR" "${BACKUP_DIR}$(date '+%Y%m%d_%H%M%S')"
mkdir -p "$BACKUP_DIR"
chown postgres:postgres "$BACKUP_DIR"
chmod 700 "$BACKUP_DIR"
shopt -s dotglob
mv /var/lib/pgsql/data/* "$BACKUP_DIR"

echo "Create new database directory..."
chown -R postgres:postgres /var/lib/pgsql

if [ -e /etc/pki/tls/private/pg-spacewalk.key ]; then
    echo "Enforce key permission"
    chown postgres:postgres /etc/pki/tls/private/pg-spacewalk.key
    chown postgres:postgres /etc/pki/tls/certs/spacewalk.crt
fi

echo "Initialize new postgresql $NEW_VERSION database..."
. /etc/sysconfig/postgresql 2>/dev/null # Load locale for SUSE
PGHOME=$(getent passwd postgres | cut -d ":" -f6)
if [ -z $POSTGRES_LANG ]; then
    POSTGRES_LANG="en_US.UTF-8"
    [ ! -z $LC_CTYPE ] && POSTGRES_LANG=$LC_CTYPE
fi

echo "Running initdb using postgres user"
echo "Any suggested command from the console should be run using postgres user"
su -s /bin/bash - postgres -c "initdb -D /var/lib/pgsql/data --locale=$POSTGRES_LANG"
echo "Successfully initialized new postgresql $NEW_VERSION database."

echo "Temporarily disable SSL in the old posgresql configuration"
cp "${BACKUP_DIR}/postgresql.conf" "${BACKUP_DIR}/postgresql.conf.bak"
sed 's/^ssl/#ssl/' -i "${BACKUP_DIR}/postgresql.conf"

su -s /bin/bash - postgres -c "pg_upgrade --old-bindir=/usr/lib/postgresql$OLD_VERSION/bin --new-bindir=/usr/lib/postgresql$NEW_VERSION/bin --old-datadir=\"$BACKUP_DIR\" --new-datadir=/var/lib/pgsql/data"

echo "Enable SSL again"
cp "${BACKUP_DIR}/postgresql.conf.bak" "${BACKUP_DIR}/postgresql.conf"

echo "DONE"
