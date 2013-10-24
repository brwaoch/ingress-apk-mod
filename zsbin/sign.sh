#!/usr/bin/env bash

K=`dirname $0`/keystore
T="$1"

jarsigner -sigalg SHA1withRSA -digestalg SHA1 -keystore $K -storepass almafa -keypass almafa $T zsingress
jarsigner -verify $T
