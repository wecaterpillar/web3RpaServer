-- master 1
-- m1 18.139.169.175
-- m2 43.159.193.47

create user 'sync'@'43.159.193.47' identified WITH caching_sha2_password by 'rpa2022sync';
grant replication slave on *.* to 'sync'@'43.159.193.47';
flush PRIVILEGES;

sudo docker exec -it w3rpa-mysql
mysql -u sync -prpa2022sync -h 43.154.15.227 -P3306 --get-server-public-key

flush table with read lock;
show master status;

change master to master_host='43.159.193.47', master_user='sync', master_password='rpa2022sync', master_port=3306, GET_MASTER_PUBLIC_KEY=1
, master_log_file='mysql-bin.000002', master_log_pos=19276;

unlock tables;

start slave;
show slave status;

--stop slave;
--reset slave;