#!/bin/bash

darknetPath="/home/ubuntu/darknet"
videoFolder=${1?Error: send video folder path}
cmd="./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights"

cd $darknetPath
Xvfb :1 & export DISPLAY=:1
sudo $cmd $videoFolder