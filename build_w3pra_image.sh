#mvn clean install

cd ./rpa-server

mvn clean install -Dmaven.test.skip=true

cd ./rpa-server-start


group=web3b
artifactId=w3rpa-server
version=1.0.0

docker build -t ${artifactId}:${version}  .
#docker build -t ${artifactId}:${version} -f DockfileBoot .

##
#docker login hkccr.ccs.tencentyun.com --username=xxxx
docker tag ${artifactId}:${version} hkccr.ccs.tencentyun.com/web3b/${artifactId}:${version}
docker push hkccr.ccs.tencentyun.com/web3b/${artifactId}:${version}

