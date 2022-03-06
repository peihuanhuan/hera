VERSION=$1
jps -l | grep hera | awk '{print $1}' | xargs kill
sleep 1
nohup java  -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8089  -jar hera/hera-${VERSION}.jar  --spring.profiles.active=prod  >/dev/null 2>&1 &
tail -f -n100 /var/log/hera/info.log
~