create table worker
(
    employee_id serial
        primary key,
    username    varchar(100) not null
        unique,
    password    varchar(100) not null
        unique,
    full_name   varchar(100) not null,
    position    varchar(50)  not null,
    salary      numeric(10, 2) default 0.00,
    hired_at    date           default CURRENT_DATE
);

alter table worker
    owner to postgres;

