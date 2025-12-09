create table orders
(
    id             bigint auto_increment
        primary key,
    created_at     datetime(6)                                       null,
    currency       varchar(255)                                      null,
    discount_code  varchar(255)                                      null,
    event_id       bigint                                            null,
    payment_method varchar(255)                                      null,
    status         enum ('CANCELLED', 'PAID', 'PENDING', 'REFUNDED') null,
    total_amount   decimal(38, 2)                                    null,
    updated_at     datetime(6)                                       null,
    user_id        binary(16)                                        null
);

create table order_items
(
    id             bigint auto_increment
        primary key,
    price          decimal(38, 2) null,
    ticket_type_id bigint         null,
    order_id       bigint         null,
    constraint FKbioxgbv59vetrxe0ejfubep1w
        foreign key (order_id) references orders (id)
);

create table payment_info
(
    id             bigint auto_increment
        primary key,
    amount         decimal(38, 2)                        null,
    method         varchar(255)                          null,
    paid_at        datetime(6)                           null,
    status         enum ('FAILED', 'PENDING', 'SUCCESS') null,
    transaction_id varchar(255)                          null,
    order_id       bigint                                null,
    constraint UK23m0vw9ubxx75bkv6ohvbuu9w
        unique (order_id),
    constraint FKlvi5j82l41gxfinwo8npi37qc
        foreign key (order_id) references orders (id)
);

create table payment_transactions
(
    id             bigint auto_increment
        primary key,
    amount         decimal(38, 2)                        null,
    created_at     datetime(6)                           null,
    gateway        varchar(255)                          null,
    order_id       bigint                                null,
    status         enum ('PENDING', 'SUCCESS', 'FAILED') null,
    transaction_id varchar(255)                          null,
    updated_at     datetime(6)                           null,
    payment_method varchar(255)                          null
);

create table permissions
(
    id          bigint auto_increment
        primary key,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint UKpnvtwliis6p05pn6i3ndjrqt2
        unique (name)
);

create table reservations
(
    id             bigint auto_increment
        primary key,
    event_id       bigint                                                null,
    expire_at      datetime(6)                                           null,
    quantity       int                                                   null,
    seat_id        bigint                                                null,
    status         enum ('CANCELLED', 'CONFIRMED', 'EXPIRED', 'PENDING') null,
    ticket_type_id bigint                                                null,
    user_id        binary(16)                                            null
);

create table roles
(
    id          bigint auto_increment
        primary key,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint UKofx66keruapi6vyqpv6f2or37
        unique (name)
);

create table role_permissions
(
    role_id       bigint not null,
    permission_id bigint not null,
    primary key (role_id, permission_id),
    constraint FKegdk29eiy7mdtefy5c7eirr6e
        foreign key (permission_id) references permissions (id),
    constraint FKn5fotdgk8d1xvo8nav9uv3muc
        foreign key (role_id) references roles (id)
);

create table tickets
(
    id             bigint auto_increment
        primary key,
    attendee_email varchar(255)                                          null,
    attendee_name  varchar(255)                                          null,
    created_at     datetime(6)                                           null,
    seat_id        bigint                                                null,
    status         enum ('ISSUED', 'REFUNDED', 'SCANNED', 'TRANSFERRED') null,
    ticket_code    varchar(255)                                          null,
    updated_at     datetime(6)                                           null,
    order_item_id  bigint                                                null,
    constraint FK1avmygv76x52clnkdj625qr7h
        foreign key (order_item_id) references order_items (id)
);

create table check_in_logs
(
    id            bigint auto_increment
        primary key,
    check_in_time datetime(6)  null,
    device_id     varchar(255) null,
    event_id      bigint       null,
    gate          varchar(255) null,
    user_id       binary(16)   null,
    ticket_id     bigint       null,
    constraint FKaiw5xm3oh56ivxulj6g80evp6
        foreign key (ticket_id) references tickets (id)
);

create table users
(
    id            binary(16)                              not null
        primary key,
    created_at    datetime(6)                             null,
    email         varchar(255)                            not null,
    full_name     varchar(255)                            null,
    password_hash varchar(255)                            not null,
    phone         varchar(255)                            null,
    status        enum ('ACTIVE', 'PENDING', 'SUSPENDED') null,
    updated_at    datetime(6)                             null,
    constraint UK6dotkott2kjsp8vw4d0m25fb7
        unique (email)
);

create table organizations
(
    id                        binary(16)                   not null
        primary key,
    cancellation_policy       text                         null,
    created_at                datetime(6)                  null,
    fees_and_taxes            varchar(255)                 null,
    name                      varchar(255)                 not null,
    refund_policy             text                         null,
    status                    enum ('ACTIVE', 'SUSPENDED') null,
    supported_payment_methods varchar(255)                 null,
    owner_user_id             binary(16)                   null,
    constraint FK37dv86ymr1mh8lhcosssu5rc6
        foreign key (owner_user_id) references users (id)
);

create table user_organization_roles
(
    id              bigint auto_increment
        primary key,
    organization_id binary(16) not null,
    role_id         bigint     not null,
    user_id         binary(16) not null,
    constraint FK1wsh1dm281tb63txeoxme18f6
        foreign key (organization_id) references organizations (id),
    constraint FK96wjp3pil3r8a4tq6h2vwlfe3
        foreign key (user_id) references users (id),
    constraint FKfov8obvofaw2ifxlm2jcqad55
        foreign key (role_id) references roles (id)
);

create table venues
(
    id        bigint auto_increment
        primary key,
    address   varchar(255) null,
    capacity  int          null,
    city      varchar(255) null,
    map_image varchar(255) null,
    name      varchar(255) null
);

create table events
(
    id                         bigint auto_increment
        primary key,
    category                   varchar(255)                             null,
    cover_image                varchar(255)                             null,
    created_at                 datetime(6)                              null,
    description                text                                     null,
    end_time                   datetime(6)                              null,
    name                       varchar(255)                             null,
    organizer_id               binary(16)                               null,
    start_time                 datetime(6)                              null,
    status                     enum ('CANCELLED', 'DRAFT', 'PUBLISHED') null,
    updated_at                 datetime(6)                              null,
    venue_id                   bigint                                   null,
    allow_attendee_name_change bit                                      null,
    allow_ticket_transfer      bit                                      null,
    refund_deadline_hours      int                                      null,
    refund_enabled             bit                                      null,
    refund_fee_percent         double                                   null,
    constraint FKqdxygdernwwt74hdvix9u5nr3
        foreign key (venue_id) references venues (id)
);

create table discounts
(
    id                   bigint auto_increment
        primary key,
    code                 varchar(255)   null,
    discount_percent     int            null,
    usage_limit          int            null,
    used_count           int            null,
    valid_from           datetime(6)    null,
    valid_to             datetime(6)    null,
    event_id             bigint         null,
    discount_amount      decimal(38, 2) null,
    minimum_order_amount decimal(38, 2) null,
    constraint FKc4fy1byqccjplvy7y1nxm5pky
        foreign key (event_id) references events (id)
);

create table ticket_types
(
    id             bigint auto_increment
        primary key,
    end_sale       datetime(6)    null,
    name           varchar(255)   null,
    price          decimal(38, 2) null,
    quota          int            null,
    start_sale     datetime(6)    null,
    event_id       bigint         null,
    purchase_limit int            null,
    constraint FKl83j9knh8jrssp3skaeubrrk
        foreign key (event_id) references events (id)
);

create table seats
(
    id             bigint auto_increment
        primary key,
    is_available   bit          null,
    locked         bit          null,
    row_label      varchar(255) null,
    seat_category  varchar(255) null,
    seat_number    varchar(255) null,
    section        varchar(255) null,
    event_id       bigint       null,
    ticket_type_id bigint       null,
    constraint FKk0va4h6b6inoh8ac1ejh66ywm
        foreign key (ticket_type_id) references ticket_types (id),
    constraint FKn8dwqflg9k82ygrbsseghd7ca
        foreign key (event_id) references events (id)
);


