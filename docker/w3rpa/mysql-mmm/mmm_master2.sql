-- master2
-- m1 18.139.169.175
-- m2 43.159.193.47

-- change your password in prod

create user 'sync'@'18.139.169.175' identified WITH caching_sha2_password by 'rpa2022sync';
grant replication slave on *.* to 'sync'@'18.139.169.175';
flush PRIVILEGES;

sudo docker exec -it w3rpa-mysql
mysql -u sync -prpa2022sync -h 13.212.70.231 -P3306 --get-server-public-key

flush table with read lock;

show master status;

change master to master_host='18.139.169.175', master_user='sync', master_password='rpa2022sync', master_port=3306, GET_MASTER_PUBLIC_KEY=1
, master_log_file='mysql-bin.000001', master_log_pos=1406642;

unlock tables;

start slave;
show slave status;

--stop slave;
--reset slave;