#!/bin/bash

videoName=${1?Error: send video folder path}
videoPath=${2?Error: send video folder path}

# Setting Environment variables for AWS SDK to pick up.

# AWS SDK for Java attempts to find AWS credentials by
# using the default credential provider chain implemented by 
# the DefaultAWSCredentialsProviderChain class.

# The AWS SDK for Java uses the EnvironmentVariableCredentialsProvider class
# to load these credentials.

export AWS_ACCESS_KEY_ID = <Your AWS_ACCESS_KEY_ID>
export AWS_SECRET_KEY = <Your AWS_SECRET_KEY>

#Running uploader jar which uploads recorded videos to S3

java -jar Uploader-1.0.0.jar $videoName $videoPath video

