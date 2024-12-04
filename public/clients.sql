create table clients
(
    client_id     serial
        primary key,
    full_name     varchar(100) not null,
    email         varchar(100) not null
        unique,
    passport_data varchar(100) not null
        unique
);

alter table clients
    owner to postgres;

