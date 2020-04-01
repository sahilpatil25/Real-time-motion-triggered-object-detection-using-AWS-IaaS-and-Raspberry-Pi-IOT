#!/usr/bin/python

'''
SETUP:
    -   -->     GND     -->     PIN6
    +   -->     5V      -->     PIN4
    S   -->     GPIO18  -->     PIN12

'''

from picamera import PiCamera
from time import sleep
import RPi.GPIO as GPIO
import subprocess
import sys

# Spawing a compute (java)thread in background
# to look for(in SQS), process uploaded
# videos and upload results in S3
subprocess.Popen(['./compute.sh'])

sensor = 12

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(sensor, GPIO.IN)

camera = PiCamera()
camera.exposure_mode = 'antishake'

#Location to locally save recorded videos
path1 = '/home/pi/Videos/'
recordDuration = 5
#Filename suffix for videos
j=1000

while True:
	#Checking for signal from motion sensor
    i = GPIO.input(sensor)
    if i == 1:
        print("Motion detected")
        j = str(j)
        video = 'video_'+j+'.h264'
        print("Recording video"+j)
        path = path1+video
        camera.start_recording(path)
        sleep(recordDuration)
        camera.stop_recording()
        print("Recording stopped")
        print("Recorded video" + str(j))
        print("Uploading video to cloud for processing")
        #Running uploader program's (java)thread in background to upload the recorded video to S3
        subprocess.Popen(['./uploader.sh', video, path])
        j = int(j)+1
    else:
        print("No motion")
        sleep(1)

