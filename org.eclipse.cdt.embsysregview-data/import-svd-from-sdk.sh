#!/bin/sh
set -e

if [ -z "$1" ]; then
  echo "Usage: $0 ccsdk_home"
  exit 1
fi

ccsdk_home="$1"

self_dir=$(dirname "$(readlink -f "$0")")
cd "$self_dir"

for board in $(ls "$ccsdk_home/boards"); do
  svd_file="$ccsdk_home/boards/$board/board-svd.xml"
  if [ -f "$svd_file" ]; then
    echo "Importing board $board"
    cp "$svd_file" "data/ccproc/ChipCraft/$board.xml"
  fi
done
