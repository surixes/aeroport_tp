create table flights
(
    flight_id      serial
        primary key,
    departure_city varchar(100)   not null,
    arrival_city   varchar(100)   not null,
    departure_time timestamp      not null,
    arrival_time   timestamp      not null,
    price          numeric(10, 2) not null
);

alter table flights
    owner to postgres;

