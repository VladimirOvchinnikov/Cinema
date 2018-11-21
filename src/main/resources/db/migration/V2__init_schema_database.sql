create table if not exists users (
    id serial primary key,
    first_name character varying(100),
    middle_name character varying(100),
    last_name character varying(100),
    birthday date,
    login character varying(100) UNIQUE,
    password character varying(100),
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone
);
comment on table users is 'Таблица с пользователями кинотеатра';
comment on column users.first_name is 'Имя';
comment on column users.middle_name is 'Отчество';
comment on column users.last_name is 'Фамилия';
comment on column users.birthday is 'Дата рождения пользователя';
comment on column users.login is 'Логин пользователя';
comment on column users.password is 'Пароль пользователя. Зашифрован в md5';

create or replace view v_users as select * from users where deleted_at is null;

create table if not exists films (
    id serial primary key,
    name character varying(100),
    poster text,
    duration interval,
    price double precision not null,
    start_of_hire date,
    end_of_hire date,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone
);
comment on table films is 'Таблица с кинофильмами';
comment on column films.name is 'Названия фильма';
comment on column films.poster is 'Плакат фильма в формате base64';
comment on column films.duration is 'Продолжительность фильма';
comment on column films.price is 'Базавая стоимость фильма';
comment on column films.start_of_hire is 'Дата начала проката';
comment on column films.end_of_hire is 'Дата конца проката';

create or replace view v_films as select * from films where deleted_at is null;

create table if not exists halls (
    id serial primary key,
    name character varying(100),
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone
);

comment on table halls is 'Таблица с залами';
comment on column halls.name is 'Название зала';

create or replace view v_halls as select * from halls where deleted_at is null;

create table if not exists rows (
    id serial primary key,
    row_number integer not null,
    hall_id integer not null,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone,
    foreign key (hall_id) references halls(id)
);

comment on table rows is 'Таблица с рядами в зале';
comment on column rows.row_number is 'Номер ряда';
comment on column rows.hall_id is 'Ссылка на зал в котором находится ряд';

create or replace view v_rows as select * from rows where deleted_at is null;

create table if not exists places (
    id serial primary key,
    place_number integer not null,
    is_vip boolean default false,
    price_coefficient double precision default 1.0,
    row_id integer not null,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone,
    foreign key (row_id) references rows(id)
);

comment on table places is 'Таблица с местами в зале';
comment on column places.place_number is 'Номер места';
comment on column places.is_vip is 'Флаг указывающий что место является VIP';
comment on column places.price_coefficient is 'Коэффициент стоимости места';
comment on column places.row_id is 'Ссылка на ряд в котором находится место';

create or replace view v_places as select * from places where deleted_at is null;

create table if not exists seances (
    id serial primary key,
    film_id integer not null,
    hall_id integer not null,
    date_seance date not null,
    time_seance time not null,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone,
    foreign key (film_id) references films(id),
    foreign key (hall_id) references halls(id)
);

comment on table seances is 'Таблица с сеансами';
comment on column seances.film_id is 'Ссылка на фильм';
comment on column seances.hall_id is 'Ссылка на зал';
comment on column seances.date_seance is 'Дата проведения сеанса';
comment on column seances.time_seance is 'Время начала сеанса';

CREATE INDEX if not exists seances_film_id_idx
ON public.seances
USING btree (film_id);

CREATE INDEX if not exists seances_hall_id_idx
ON public.seances
USING btree (hall_id);

create or replace view v_seances as select * from seances where deleted_at is null;

create table if not exists orders (
    id serial primary key,
    ticket_count integer,
    bonus_count integer,
    user_id integer not null,
    total_price double precision,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone,
    foreign key (user_id) references users(id)
);

comment on table orders is 'Таблица с заказами';
comment on column orders.ticket_count is 'Количество билетов в заказе';
comment on column orders.bonus_count is 'Количество бонусов в заказе';
comment on column orders.user_id is 'Ссылка на пользователя, что производит заказ';
comment on column orders.total_price is 'Общая сумма заказа с учетом всех акций';

create or replace view v_orders as select * from orders where deleted_at is null;

create table if not exists tickets (
    id serial primary key,
    seance_id integer not null,
    place_id integer not null,
    order_id integer,
    code character varying(100),
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone,
    foreign key (seance_id) references seances(id),
    foreign key (order_id) references orders(id),
    foreign key (place_id) references places(id)
);

comment on table tickets is 'Таблица с билетами';
comment on column tickets.seance_id is 'Ссылка на сеанс';
comment on column tickets.place_id is 'Ссылка на место';
comment on column tickets.order_id is 'Ссылка на заказ/чек(Если поле null, то билет еще не продан)';
comment on column tickets.code is 'Уникальныйй код билета';

CREATE INDEX if not exists tickets_seance_id_idx
ON public.tickets
USING btree (seance_id);

create or replace view v_tickets as select * from tickets where deleted_at is null;

--Это может быть и Роль
create table if not exists user_types(
    id serial primary key,
    name character varying(100)
);

comment on table user_types is 'Таблица с типами пользователей';
comment on column user_types.name is 'Название типа бпользователя';

create table if not exists bonus_types(
    id serial primary key,
    name character varying(100)
);

comment on table bonus_types is 'Таблица с типами бонусов';
comment on column bonus_types.name is 'Название типа бонуса';

create table if not exists bonuses (
    id serial primary key,
    name character varying(100),
    description text,
    bonus_type_id integer not null,
    user_type_id integer not null,
    discount integer not null,
    condition text,
    created_at timestamp without time zone default now(),
    deleted_at timestamp without time zone
);

comment on table bonuses is 'Таблица с бонусами';
comment on column bonuses.name is 'Название бонуса';
comment on column bonuses.description is 'Описание бонуса';
comment on column bonuses.bonus_type_id is 'Тип бонус';
comment on column bonuses.user_type_id is 'Тип пользователей для которых используется бонус';
comment on column bonuses.discount is 'Скидка по бонусу';
comment on column bonuses.condition is 'Услови получение скидка в коде!!!!!';

create or replace view v_bonuses as select * from bonuses where deleted_at is null;











