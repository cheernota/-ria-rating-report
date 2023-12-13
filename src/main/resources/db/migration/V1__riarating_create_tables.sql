create table if not exists region (
    region_id integer generated always as identity primary key,
    region_name varchar(255) not null
);

create table if not exists research (
    research_id int generated always as identity primary key,
    research_code varchar(255) not null,
    research_name text,
    research_date date,
    research_json text,
    json_version integer,
    unique (research_code)
);

create index if not exists idx_research_date on research (research_date);

create table if not exists region_research (
    research_id int references research (research_id),
    region_id int references region (region_id),
    region_place int,
    unique (research_id, region_id)
);