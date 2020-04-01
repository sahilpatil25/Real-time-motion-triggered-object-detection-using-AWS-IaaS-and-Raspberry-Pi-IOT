#!/bin/bash

#Script to run darknet YOLO

#Path to darknet binaries/resources
darknetPath="/home/pi/darknet"

#Video name with fully qualified path
videoFolder=${1?Error: send video folder path}

#Darknet command
cmd="./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights"

cd $darknetPath

#To supress display
Xvfb :1 & export DISPLAY=:1

#Running the command with video as the argument
sudo $cmd $videoFolder