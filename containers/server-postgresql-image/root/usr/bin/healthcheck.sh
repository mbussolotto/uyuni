#!/bin/bash
set -e

# Set threshold to env var DISKTHRESHOLD, default to 95 if not set
THRESHOLD=${DISKTHRESHOLD:-95}
PGUSER=${POSTGRES_USER:-postgres}
PGPORT=${POSTGRES_PORT:-5432}

# Get current disk usage percentage
DISK_USAGE=$(df --output=pcent /var/lib/pgsql/data | tail -1 | tr -d '%')

if [ "$DISK_USAGE" -gt "$THRESHOLD" ]; then
    echo "Healthcheck failed: Disk usage is at ${DISK_USAGE}%, which exceeds the ${THRESHOLD}% threshold."
    exit 1
fi

if ! pg_isready -U "$PGUSER" -h localhost -p "$PGPORT" > /dev/null; then
    echo "Healthcheck failed: PostgreSQL is not ready (user: ${PGUSER}, host: localhost, port: ${PGPORT})."
    exit 1
fi

# Check if database is running a reindex operation
REINDEX_RUNNING=$(echo "SELECT check_reindex_status();" | spacewalk-sql --select-mode - 2>/dev/null | tail -1 | tr -d ' ')
if [ "$REINDEX_RUNNING" = "t" ]; then
    echo "Healthcheck passed: Database reindex in progress (Disk usage: ${DISK_USAGE}%, Threshold: ${THRESHOLD}%)."
    exit 0
fi

echo "Healthcheck passed: Disk usage is at ${DISK_USAGE}% (Threshold: ${THRESHOLD}%), PostgreSQL is ready."
exit 0
