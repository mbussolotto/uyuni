create or replace function get_pgsql_disk_usage_percent()
returns integer
as
$$
declare
    result text;
begin
    create temp table if not exists tmp_sys_df (content text) on commit drop;
    truncate tmp_sys_df;
    copy tmp_sys_df from program 'df --output=pcent /var/lib/pgsql/data/ | tail -1';
    select content into result from tmp_sys_df;
    drop table tmp_sys_df;
    return trim(both ' %' from result)::integer;
end;
$$ language plpgsql;
