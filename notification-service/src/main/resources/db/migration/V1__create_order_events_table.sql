create sequence order_event_id_seq start with 1 increment by 50;

create table order_events
(
    id         bigint primary key default nextval('order_event_id_seq'),
    event_id   text    not null unique,
    created_at timestamp not null,
    updated_at timestamp
);