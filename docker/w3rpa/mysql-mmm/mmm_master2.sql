create user 'sync'@'44.203.200.35' identified WITH caching_sha2_password by 'rpa2022sync';
-- ALTER USER 'sync'@'44.203.200.35' IDENTIFIED WITH caching_sha2_password BY 'rpa2022sync';
grant replication slave on *.* to 'sync'@'44.203.200.35';
flush PRIVILEGES;

sudo docker exec -it w3rpa-mysql
mysql -u sync -prpa2022sync -h 44.203.200.35 -P3306 --get-server-public-key

flush table with read lock;

show master status;

change master to master_host='44.203.200.35', master_user='sync', master_password='rpa2022sync', master_port=3306, GET_MASTER_PUBLIC_KEY=1
, master_log_file='mysql-bin.000003', master_log_pos=17093;

unlock tables;

start slave;
show slave status;

--stop slave;
--reset slave;