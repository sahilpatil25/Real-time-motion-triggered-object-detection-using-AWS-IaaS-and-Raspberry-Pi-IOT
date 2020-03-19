package com.amazonaws.controller;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
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
import com.amazonaws.services.ec2.model.InstanceState;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

/**
* Describes all EC2 instances associated with an AWS account
*/
public class Controller
{
	private List<String> instanceList = new ArrayList<>();
	private AmazonEC2 ec2;
	private AmazonSQS sqs;
	private String queueURL;
	private String queueName="job__fifo_queue.fifo";
	
	
	public Controller() {
		AWSCredentials credentials = null;
		credentials = new ProfileCredentialsProvider("default").getCredentials();
		ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		sqs = AmazonSQSClientBuilder.standard()
        		.withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_1)
                .build();
		queueURL = sqs.getQueueUrl(queueName).getQueueUrl();
	}
	
	public void populateInstanceList()
	{
		  boolean done = false;
		  DescribeInstancesRequest request = new DescribeInstancesRequest();
		  while(!done) 
		  {
		      DescribeInstancesResult response = ec2.describeInstances(request);
		      for(Reservation reservation : response.getReservations()) 
		      {
		          for(Instance instance : reservation.getInstances()) 
		          {
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
		              instanceList.add(instance.getInstanceId());
		          }
		      }
		          request.setNextToken(response.getNextToken());
		          if(response.getNextToken() == null) 
		          {
		              done = true;
		          }
		  }
	}
	
	public int checkJobQueue()
	{
		// get all the attributes of the queue 
	    List<String> attributeNames = new ArrayList<>();
	    attributeNames.add("All");
	    // list the attributes of the queue we are interested in
	    GetQueueAttributesRequest request = new GetQueueAttributesRequest(queueURL);
	    request.setAttributeNames(attributeNames);
	    Map<String, String> attributes = sqs.getQueueAttributes(request)
	            .getAttributes();
	    int messages = Integer.parseInt(attributes
	            .get("ApproximateNumberOfMessages"));
	    System.out.println("Number of messages :"+messages);
	    int messagesNotVisible = Integer.parseInt(attributes
	            .get("ApproximateNumberOfMessagesNotVisible"));
	    System.out.println("Number of messages in flight :"+messagesNotVisible);
	    return messages;
	}
	
	public List<String> getAvailabeInstances()
	{
		  List<String> available =  new ArrayList<String>();
		  boolean done = false;
		  DescribeInstancesRequest request = new DescribeInstancesRequest();
		  while(!done) 
		  {
		      DescribeInstancesResult response = ec2.describeInstances(request);
		      for(Reservation reservation : response.getReservations()) 
		      {
		          for(Instance instance : reservation.getInstances()) 
		          {
		              if(instance.getState().getCode()==80);
		              {
		            	  available.add(instance.getInstanceId());
		              }
		          }
		      }
		          request.setNextToken(response.getNextToken());
		          if(response.getNextToken() == null) 
		          {
		              done = true;
		          }
		  }
		  return available;
	}

	public AmazonEC2 getEc2() {
		return ec2;
	}

	public AmazonSQS getSqs() {
		return sqs;
	}

	public String getQueueURL() {
		return queueURL;
	}

	public String getQueueName() {
		return queueName;
	}
}