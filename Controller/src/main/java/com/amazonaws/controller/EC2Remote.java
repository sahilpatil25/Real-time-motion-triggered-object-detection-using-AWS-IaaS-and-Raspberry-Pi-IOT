package com.amazonaws.controller;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
//snippet-sourcedescription:[StartStopInstance.java demonstrates how to start or stop an EC2 instance.]
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
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

/**
* Starts or stops and EC2 instance
*/
public class EC2Remote
{
  public static void startInstance(AmazonEC2 ec2, String instance_id)
  {
      DryRunSupportedRequest<StartInstancesRequest> dry_request =
          () -> {
          StartInstancesRequest request = new StartInstancesRequest()
              .withInstanceIds(instance_id);

          return request.getDryRunRequest();
      };

      DryRunResult dry_response = ec2.dryRun(dry_request);

      if(!dry_response.isSuccessful()) {
          System.out.printf(
              "Failed dry run to start instance %s", instance_id);

          throw dry_response.getDryRunResponse();
      }

      StartInstancesRequest request = new StartInstancesRequest()
          .withInstanceIds(instance_id);

      ec2.startInstances(request);

      System.out.printf("Starting instance %s", instance_id);
      System.out.println();
  }

  public static void stopInstance(AmazonEC2 ec2, String instance_id)
  {
      DryRunSupportedRequest<StopInstancesRequest> dry_request =
          () -> {
          StopInstancesRequest request = new StopInstancesRequest()
              .withInstanceIds(instance_id);

          return request.getDryRunRequest();
      };

      DryRunResult dry_response = ec2.dryRun(dry_request);

      if(!dry_response.isSuccessful()) {
          System.out.printf(
              "Failed dry run to stop instance %s", instance_id);
          throw dry_response.getDryRunResponse();
      }

      StopInstancesRequest request = new StopInstancesRequest()
          .withInstanceIds(instance_id);

      ec2.stopInstances(request);

      System.out.printf("Successfully stop instance %s", instance_id);
  }
}