package com.amazonaws.controller;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
//snippet-sourcedescription:[DescribeInstances.java demonstrates how to get a description of all EC2 instances associated with an AWS account.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-05-22]
//snippet-sourceauthor:[soo-aws]
/*
* Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
*
* Licensed under the Apache License, Version 2.0 (the "License").
* You may not use this file except in compliance with the License.
* A copy of the License is located at
*
*  http://aws.amazon.com/apache2.0
*
* or in the "license" file accompanying this file. This file is distributed
* on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
* express or implied. See the License for the specific language governing
* permissions and limitations under the License.
*/
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;

/**
* Describes all EC2 instances associated with an AWS account
*/
public class Controller
{
  public static void main(String[] args)
  {
	  AWSCredentials credentials = null;
	  credentials = new ProfileCredentialsProvider("default").getCredentials();
	  final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
      boolean done = false;

      DescribeInstancesRequest request = new DescribeInstancesRequest();
      while(!done) {
          DescribeInstancesResult response = ec2.describeInstances(request);
          for(Reservation reservation : response.getReservations()) {
              for(Instance instance : reservation.getInstances()) {
                  System.out.printf(
                      "Instance ID : %s, " +
                      "AMI : %s, " +
                      "state : %s " +
                      "name: %s",
                      instance.getInstanceId(),
                      instance.getImageId(),
                      instance.getState().getName(),
                      instance.getTags().get(0).getValue());
                  System.out.println();
              }
          }

          request.setNextToken(response.getNextToken());

          if(response.getNextToken() == null) {
              done = true;
          }
      }
  }
}