cd zhongshi-etc-basics/zhongshi-etc-basics-bootadmin/target
java -Dfile.encoding=utf-8 -server -Xmx2500M -Xms2500M -Xmn600M -XX:PermSize=500M -XX:MaxPermSize=500M -Xss256K -XX:+DisableExplicitGC -XX:SurvivorRatio=1 -XX:+CMSParallelRemarkEnabled -XX:+CMSClassUnloadingEnabled -XX:LargePageSizeInBytes=128M -XX:+UseCMSInitiatingOccupancyOnly -XX:CMSInitiatingOccupancyFraction=70 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -jar zhongshi-etc-basics-bootadmin.jar
  
  
  
  
  
  
 
  
  
  
  
 
 
