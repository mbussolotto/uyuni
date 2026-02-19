--
-- Copyright (c) 2026 SUSE LLC
--
-- This software is licensed to you under the GNU General Public License,
-- version 2 (GPLv2). There is NO WARRANTY for this software, express or
-- implied, including the implied warranties of MERCHANTABILITY or FITNESS
-- FOR A PARTICULAR PURPOSE. You should have received a copy of GPLv2
-- along with this software; if not, see
-- http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt.
--

create or replace function
get_pgsql_disk_usage_percent()
returns setof text
as
$$
BEGIN
     CREATE TEMP TABLE IF NOT EXISTS tmp_sys_df (content text) ON COMMIT DROP;
     COPY tmp_sys_df FROM program 'df --output=pcent /var/lib/pgsql/data/ | tail -1';
	 select content into result from tmp_sys_df;
     drop table tmp_sys_df;
     return trim(both '%' from result)::integer;
END;
$$ language plpgsql;
