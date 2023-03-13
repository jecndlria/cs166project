source ./startPostgreSQL.sh
sleep 1
pg_ctl status
sleep 1
source ./createPostgreDB.sh
sleep 1
cp data/*csv /tmp/$USER/myDB/data
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < sql/src/create_tables.sql
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < sql/src/create_indexes.sql
sleep 1
psql -h localhost -p $PGPORT $USER"_DB" < sql/src/load_data.sql

