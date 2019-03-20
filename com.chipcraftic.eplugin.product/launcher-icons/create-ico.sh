#!/bin/sh
 convert icon256.png  -background white \
          \( -clone 0 -resize 16x16 -extent 16x16 \) \
          \( -clone 0 -resize 32x32 -extent 32x32 \) \
          \( -clone 0 -resize 48x48 -extent 48x48 \) \
          \( -clone 0 -resize 16x16 -extent 16x16 -colors 256 \) \
          \( -clone 0 -resize 32x32 -extent 32x32 -colors 256 \) \
          \( -clone 0 -resize 48x48 -extent 48x48 -colors 256 \) \
          -delete 0 -alpha off icon.ico
