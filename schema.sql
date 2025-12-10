create table ticket_store_db.orders
(
    id             bigint auto_increment
        primary key,
    created_at     datetime(6)                                       null,
    currency       varchar(255)                                      null,
    discount_code  varchar(255)                                      null,
    event_id       bigint                                            null,
    is_resale      bit                                               not null,
    payment_method varchar(255)                                      null,
    status         enum ('PENDING', 'PAID', 'CANCELLED', 'REFUNDED') null,
    total_amount   decimal(38, 2)                                    null,
    updated_at     datetime(6)                                       null,
    user_id        binary(16)                                        null
);

create table ticket_store_db.order_items
(
    id             bigint auto_increment
        primary key,
    price          decimal(38, 2) null,
    quantity       int            not null,
    ticket_code    varchar(255)   null,
    ticket_type_id bigint         null,
    order_id       bigint         null,
    constraint FKbioxgbv59vetrxe0ejfubep1w
        foreign key (order_id) references ticket_store_db.orders (id)
);

create table ticket_store_db.payment_info
(
    id             bigint auto_increment
        primary key,
    amount         decimal(38, 2)                        null,
    method         varchar(255)                          null,
    paid_at        datetime(6)                           null,
    status         enum ('PENDING', 'SUCCESS', 'FAILED') null,
    transaction_id varchar(255)                          null,
    order_id       bigint                                null,
    constraint UK_23m0vw9ubxx75bkv6ohvbuu9w
        unique (order_id),
    constraint FKlvi5j82l41gxfinwo8npi37qc
        foreign key (order_id) references ticket_store_db.orders (id)
);

create table ticket_store_db.payment_transactions
(
    id             bigint auto_increment
        primary key,
    amount         decimal(38, 2)                                    null,
    created_at     datetime(6)                                       null,
    order_id       bigint                                            null,
    payment_method varchar(255)                                      null,
    status         enum ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') null,
    transaction_id varchar(255)                                      null,
    updated_at     datetime(6)                                       null,
    vnpay_txn_ref  varchar(255)                                      null
);

create table ticket_store_db.permissions
(
    id          bigint auto_increment
        primary key,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint UKpnvtwliis6p05pn6i3ndjrqt2
        unique (name)
);

create table ticket_store_db.reservations
(
    id             bigint auto_increment
        primary key,
    event_id       bigint                                                null,
    expire_at      datetime(6)                                           null,
    quantity       int                                                   null,
    seat_id        bigint                                                null,
    status         enum ('PENDING', 'CONFIRMED', 'EXPIRED', 'CANCELLED') null,
    ticket_type_id bigint                                                null,
    user_id        binary(16)                                            null
);

create table ticket_store_db.roles
(
    id          bigint auto_increment
        primary key,
    description varchar(255) null,
    name        varchar(255) not null,
    constraint UKofx66keruapi6vyqpv6f2or37
        unique (name)
);

create table ticket_store_db.role_permissions
(
    role_id       bigint not null,
    permission_id bigint not null,
    primary key (role_id, permission_id),
    constraint FKegdk29eiy7mdtefy5c7eirr6e
        foreign key (permission_id) references ticket_store_db.permissions (id),
    constraint FKn5fotdgk8d1xvo8nav9uv3muc
        foreign key (role_id) references ticket_store_db.roles (id)
);

create table ticket_store_db.users
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

create table ticket_store_db.organizations
(
    id                        bigint auto_increment
        primary key,
    contact_email             varchar(255)                            null,
    created_at                datetime(6)                             null,
    description               varchar(255)                            null,
    name                      varchar(255)                            not null,
    owner_user_id             binary(16)                              not null,
    status                    enum ('ACTIVE', 'PENDING', 'SUSPENDED') null,
    updated_at                datetime(6)                             null,
    cancellation_policy       text                                    null,
    fees_and_taxes            varchar(255)                            null,
    refund_policy             text                                    null,
    supported_payment_methods varchar(255)                            null,
    constraint UKp9pbw3flq9hkay8hdx3ypsldy
        unique (name),
    constraint FK37dv86ymr1mh8lhcosssu5rc6
        foreign key (owner_user_id) references ticket_store_db.users (id)
);

create table ticket_store_db.user_organization_roles
(
    id              bigint auto_increment
        primary key,
    created_at      datetime(6) null,
    updated_at      datetime(6) null,
    organization_id bigint      not null,
    role_id         bigint      not null,
    user_id         binary(16)  not null,
    constraint UKpu1ubqmdylk9u1q1oxs2nn421
        unique (user_id, organization_id),
    constraint FK1wsh1dm281tb63txeoxme18f6
        foreign key (organization_id) references ticket_store_db.organizations (id),
    constraint FK96wjp3pil3r8a4tq6h2vwlfe3
        foreign key (user_id) references ticket_store_db.users (id),
    constraint FKfov8obvofaw2ifxlm2jcqad55
        foreign key (role_id) references ticket_store_db.roles (id)
);

create table ticket_store_db.venues
(
    id        bigint auto_increment
        primary key,
    address   varchar(255) null,
    capacity  int          null,
    city      varchar(255) null,
    map_image varchar(255) null,
    name      varchar(255) null
);

create table ticket_store_db.events
(
    id                         bigint auto_increment
        primary key,
    allow_attendee_name_change bit                                      null,
    allow_ticket_transfer      bit                                      null,
    buyer_count                int                                      null,
    category                   varchar(255)                             null,
    cover_image                varchar(255)                             null,
    created_at                 datetime(6)                              null,
    description                text                                     null,
    end_time                   datetime(6)                              null,
    format                     varchar(255)                             null,
    name                       varchar(255)                             null,
    organizer_id               binary(16)                               null,
    refund_deadline_hours      int                                      null,
    refund_enabled             bit                                      null,
    refund_fee_percent         double                                   null,
    seat_configuration         text                                     null,
    start_time                 datetime(6)                              null,
    status                     enum ('DRAFT', 'PUBLISHED', 'CANCELLED') null,
    updated_at                 datetime(6)                              null,
    venue_id                   bigint                                   null,
    constraint FKqdxygdernwwt74hdvix9u5nr3
        foreign key (venue_id) references ticket_store_db.venues (id)
);

create table ticket_store_db.discounts
(
    id                   bigint auto_increment
        primary key,
    code                 varchar(255)   null,
    discount_amount      decimal(38, 2) null,
    discount_percent     int            null,
    minimum_order_amount decimal(38, 2) null,
    usage_limit          int            null,
    used_count           int            null,
    valid_from           datetime(6)    null,
    valid_to             datetime(6)    null,
    event_id             bigint         null,
    constraint FKc4fy1byqccjplvy7y1nxm5pky
        foreign key (event_id) references ticket_store_db.events (id)
);

create table ticket_store_db.ticket_types
(
    id             bigint auto_increment
        primary key,
    end_sale       datetime(6)    null,
    name           varchar(255)   null,
    price          decimal(38, 2) null,
    purchase_limit int            null,
    quota          int            null,
    start_sale     datetime(6)    null,
    event_id       bigint         null,
    constraint FKl83j9knh8jrssp3skaeubrrk
        foreign key (event_id) references ticket_store_db.events (id)
);

create table ticket_store_db.seats
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
        foreign key (ticket_type_id) references ticket_store_db.ticket_types (id),
    constraint FKn8dwqflg9k82ygrbsseghd7ca
        foreign key (event_id) references ticket_store_db.events (id)
);

