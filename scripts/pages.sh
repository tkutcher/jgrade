
[[ -d .public ]] && rm -rf .public
[[ -d target/apidocs ]] || mvn javadoc:jar

mkdir -p .public/api/

cp -r static/* .public/
cp -r target/apidocs/* .public/api/.
