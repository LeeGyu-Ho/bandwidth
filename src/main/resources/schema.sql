CREATE TABLE fileupload(
    id serial PRIMARY KEY,
    file_size integer,
    start_time timestamp not null,
    end_time timestamp not null
);