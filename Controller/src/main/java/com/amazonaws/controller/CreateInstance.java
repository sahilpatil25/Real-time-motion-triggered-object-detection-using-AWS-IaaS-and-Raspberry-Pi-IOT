package com.amazonaws.controller;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
//snippet-sourcedescription:[CreateInstance.java demonstrates how to create an EC2 instance.]
//snippet-keyword:[Java]
//snippet-sourcesyntax:[java]
//snippet-keyword:[Code Sample]
//snippet-keyword:[Amazon EC2]
//snippet-service:[ec2]
//snippet-sourcetype:[full-example]
//snippet-sourcedate:[2018-05-22]
//snippet-sourceauthor:[soo-aws]
/* Copyright 2010-2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
//snippet-start:[ec2.java.create_instance.complete]
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.InstanceType;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.CreateTagsRequest;
import com.amazonaws.services.ec2.model.CreateTagsResult;

/**
* Creates an EC2 instance
*/
public class CreateInstance
{
  public static void create(String instance_name)
  {
	  AWSCredentials credentials = null;
	  credentials = new ProfileCredentialsProvider("default").getCredentials();
      /*final String USAGE =
          "To run this example, supply an instance name and AMI image id\n" +
          "Ex: CreateInstance <instance-name> <ami-image-id>\n";

      /*if (args.length != 2) {
          System.out.println(USAGE);
          System.exit(1);
      }*/

      String name = instance_name;
      String ami_id = "ami-0903fd482d7208724";
      
      final AmazonEC2 ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

      RunInstancesRequest run_request = new RunInstancesRequest()
          .withImageId(ami_id)
          .withInstanceType(InstanceType.T2Micro)
          .withMaxCount(1)
          .withMinCount(1);

      RunInstancesResult run_response = ec2.runInstances(run_request);

      String reservation_id = run_response
    		  .getReservation()
    		  .getInstances()
    		  .get(0)
    		  .getInstanceId();
      
      CreateTagsRequest tag_request = new CreateTagsRequest()
    		  .withResources(reservation_id)
    		  .withTags(new Tag().withKey("Name").withValue(name));

      CreateTagsResult tag_response = ec2.createTags(tag_request);

      System.out.printf(	
          "Successfully started EC2 instance %s based on AMI %s",
          reservation_id, ami_id);
  }
}
//snippet-end:[ec2.java.create_instance.complete]
