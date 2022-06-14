create table if not exists rule_template
(
    id         int auto_increment
        primary key,
    created_at timestamp  default CURRENT_TIMESTAMP null,
    updated_at TIMESTAMP                            NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    name       varchar(50)                          not null,
    arguments  varchar(2048)                        null,
    flink_job  varchar(50)                          not null,
    active     tinyint(1) default 1                 not null,
    created_by varchar(255)                         null,
    updated_by varchar(255)                         null,
    constraint rule_template_flink_job_uindex
        unique (flink_job)
);
