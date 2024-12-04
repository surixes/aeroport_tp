create table administrators
(
    admin_id  serial
        primary key,
    full_name varchar(100) not null,
    position  varchar(50) default 'Administrator'::character varying,
    username  varchar(100) not null
        unique,
    password  varchar(100) not null
        unique
);

alter table administrators
    owner to postgres;

