
alter table rhnISSMaster add is_current_master char(1) default 'N' not null
    constraint rhn_issm_master_yn check (is_current_master in ('Y', 'N'));

alter table rhnISSMaster add ca_cert varchar(1024);

create unique index rhn_issm_only_one_default on rhnISSMaster
    (is_current_master) where is_current_master = 'Y';
