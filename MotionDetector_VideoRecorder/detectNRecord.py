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

sensor = 12

GPIO.setwarnings(False)
GPIO.setmode(GPIO.BOARD)
GPIO.setup(sensor, GPIO.IN)

camera = PiCamera()
camera.exposure_mode = 'antishake'

path1 = '/home/pi/Videos/'

recordDuration = 5
j=500
start=0
while True:
    i = GPIO.input(sensor)
    if i == 1:
        print("Motion detected")
        j = str(j)
        video = 'video_'+j+'.h264'
        output = 'video_'+j+'.txt'
        print("Recording video"+j)
        path = path1+video
        camera.start_recording(path)
        sleep(recordDuration)
        camera.stop_recording()
        print("Recording stopped")
        print("Recorded video" + str(j))
        if not start:
            start = 1
            print("Processing video on Raspberry Pi")
            output = 'video_'+j+'.txt'
            darknetThread = subprocess.Popen(['./darknet.sh', path, output])
            j = int(j) + 1
            continue
        darknetThreadPoll = darknetThread.poll()
        if darknetThreadPoll == None:
            print("Uploading video to cloud for processing")
            subprocess.Popen(['./uploader.sh', video, path])
        else:
            print("Processing video on Raspberry Pi")
            output = 'video_'+j+'.txt'
            darknetThread = subprocess.Popen(['./darknet.sh', path, output])

        j = int(j)+1
    else:
        print("No motion")
        sleep(1)

        ##subprocess.call(['sudo','chrt','-i','0','python','take_snapshot.py','frame.jpg'])
        ##subprocess.call(['sudo', 'chrt', '-i','0', './facedetect', 'frame.jpg'])

