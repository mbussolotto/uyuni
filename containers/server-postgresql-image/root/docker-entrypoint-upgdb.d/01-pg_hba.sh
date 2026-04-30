#!/usr/bin/env bash
# SPDX-FileCopyrightText: 2026 SUSE LLC
#
# SPDX-License-Identifier: GPL-2.0-Only

# we need to make sure there is correct access for our services

HBA_FILE=/var/lib/pgsql/data/pg_hba.conf

if [ -z "$(sed -n '/^local all all peer$/p' "$HBA_FILE")" ]; then
    echo "local all all peer" >> "$HBA_FILE"
fi

if [ -z "$(sed -n '/^local replication all peer$/p' "$HBA_FILE")" ]; then
    echo "local replication all peer" >> "$HBA_FILE"
fi

if [ -z "$(sed -n '/^host all all all scram-sha-256$/p' "$HBA_FILE")" ]; then
    echo "host all all all scram-sha-256" >> "$HBA_FILE"
fi
