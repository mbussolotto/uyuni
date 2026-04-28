#!/bin/bash
# SPDX-FileCopyrightText: 2026 SUSE LLC
#
# SPDX-License-Identifier: Apache-2.0

run_sql() {
    PGHOST='' PGHOSTADDR='' psql -v ON_ERROR_STOP=1 \
        -p "${PGPORT:-5432}" \
        -U "$POSTGRES_USER" \
        --no-password --no-psqlrc -d susemanager
}

cat << EOF | run_sql
CREATE OR REPLACE FUNCTION check_reindex_status()
RETURNS boolean
SECURITY DEFINER
AS
\$\$
DECLARE
    reindex_count integer;
BEGIN
    SELECT COUNT(*) INTO reindex_count
    FROM pg_stat_activity
    WHERE query LIKE '%REINDEX%' AND state = 'active';

    RETURN reindex_count > 0;
EXCEPTION
    WHEN OTHERS THEN
        RAISE WARNING 'Reindex status check failed. Error: % (SQLSTATE: %)', SQLERRM, SQLSTATE;
        RETURN false;
END;
\$\$ LANGUAGE plpgsql;
EOF
