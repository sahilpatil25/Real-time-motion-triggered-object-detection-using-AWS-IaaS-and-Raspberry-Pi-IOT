#!/bin/bash

darknetPath="/home/pi/darknet"
videoFolder=${1?Error: send video folder path}
outputFile=${1?Error: send video folder path}
cmd="./darknet detector demo cfg/coco.data cfg/yolov3-tiny.cfg yolov3-tiny.weights"

cd $darknetPath
sudo $cmd $videoFolder | sudo tee $outputFile
python parser.py $outputFile
java -jar cse546upload-1.0.0.jar $outputFile $videoFolder

