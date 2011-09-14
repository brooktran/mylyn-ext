#!/bin/bash

JAVA_HOME=$HOME/tools/jvm/sun-jdk-1.7.0
MVN=$HOME/bin/mvn

targetRepo=$HOME/eclipse/targets/eclipse-rcp-indigo

# build

cd "$HOME/git/mylyn-ext/ralfstx.mylyn.bugview.releng"

$MVN -DtargetRepo=$targetRepo clean package || exit 1

# publish

version=`ls -1 target/repository/features/*.jar | sed -e 's/.*_\([0-9\.-]*\)\.jar/\1/'`
if [ -n "$version" ]; then
  echo "Could not determine feature version"
  exit 1
fi
echo "Version: $version"

rsync -av repository/target/repository/ $HOME/git/ralfstx.github.com/update/eclipse-addons/mylyn-bugview-$VERSION

