package com.amazonaws.controller;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
/**
* Describes all EC2 instances associated with an AWS account
*/
public class Controller
{
	private List<String> instanceList = new ArrayList<String>();
	private AmazonEC2 ec2;
	private AmazonSQS sqs;
	private String queueURL;
	private String queueName="job__fifo_queue.fifo";
	
	public Controller() {
		ec2 = AmazonEC2ClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		sqs = AmazonSQSClientBuilder.standard()
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
	    List<String> attributeNames = new ArrayList<String>();
	    attributeNames.add("All");
	    // list the attributes of the queue we are interested in
	    GetQueueAttributesRequest request = new GetQueueAttributesRequest(queueURL);
	    request.setAttributeNames(attributeNames);
	    Map<String, String> attributes = sqs.getQueueAttributes(request)
	            .getAttributes();
	    int messages = Integer.parseInt(attributes
	            .get("ApproximateNumberOfMessages"));
	    System.out.println("Number of messages :"+messages);
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
		        	  //If instance state is 'stopped'
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