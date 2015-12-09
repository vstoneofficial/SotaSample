classpath=".\
:./bin/*\
:./lib/*\
:/home/vstone/lib/*\
:/home/vstone/vstonemagic/*\
"

echo "javac -encoding utf-8 -classpath $classpath -Dfile.encoding=UTF8 $1"
javac -encoding utf-8 -classpath  "$classpath"  -Dfile.encoding=UTF8 $1

