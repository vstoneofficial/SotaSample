classpath=".\
:/home/vstone/lib/*\
:/home/vstone/vstonemagic/*\
"

echo "java -classpath $classpath -Dfile.encoding=UTF8 $1"
java -classpath "$classpath" -Dfile.encoding=UTF8 $1
