-- MPA values
MERGE INTO mpa key(mpa_id) VALUES (1, 'G');
MERGE INTO mpa key(mpa_id) VALUES (2, 'PG');
MERGE INTO mpa key(mpa_id) VALUES (3, 'PG-13');
MERGE INTO mpa key(mpa_id) VALUES (4, 'R');
MERGE INTO mpa key(mpa_id) VALUES (5, 'NC-17');

-- Genre values
MERGE INTO genres key(genre_id) VALUES (1, 'Комедия');
MERGE INTO genres key(genre_id) VALUES (2, 'Драма');
MERGE INTO genres key(genre_id) VALUES (3, 'Мультфильм');
MERGE INTO genres key(genre_id) VALUES (4, 'Триллер');
MERGE INTO genres key(genre_id) VALUES (5, 'Документальный');
MERGE INTO genres key(genre_id) VALUES (6, 'Боевик');
