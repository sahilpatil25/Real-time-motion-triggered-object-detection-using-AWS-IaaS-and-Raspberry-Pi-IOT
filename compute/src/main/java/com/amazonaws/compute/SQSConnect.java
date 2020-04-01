package com.amazonaws.compute;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueAttributesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.QueueDoesNotExistException;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;

/*
 * Class for AWS SQS Operations
 */

public class SQSConnect {
	
	private static AmazonSQS sqs;
	private String queueURL;

	public SQSConnect() {
		sqs = AmazonSQSClientBuilder.standard().withRegion(Regions.US_EAST_1).build();
		String queueName = "job__fifo_queue.fifo";
		boolean need_creation = false;
		try {
			queueURL = sqs.getQueueUrl(queueName).getQueueUrl();
		} catch (QueueDoesNotExistException e) {
			System.out.println("Queue " + queueName + " does not exist. Need to create a new queue.");
			need_creation = true;
		}
		
		System.out.println("===========================================");
		System.out.println("Initializing SQS Client");
		System.out.println("===========================================\n");

		try {
			if (need_creation) {
				// Create a FIFO queue.
				System.out.println("Creating a new Amazon SQS FIFO queue called " + queueName);
				final Map<String, String> attributes = new HashMap<String, String>();

				// A FIFO queue must have the FifoQueue attribute set to true.
				attributes.put("FifoQueue", "true");
				
				attributes.put("ContentBasedDeduplication", "true");
				//attributes.put("ReceiveMessageWaitTimeSeconds", "1");
				
				final CreateQueueRequest createQueueRequest = new CreateQueueRequest(queueName)
						.withAttributes(attributes);
				queueURL = sqs.createQueue(createQueueRequest).getQueueUrl();
			} else {
				System.out.println("Queue " + queueName + " exists.");
			}
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}

	}

	public void addToQueue(String message) {
		try {
			System.out.println("Sending a message to job__fifo_queue.\n");
			final SendMessageRequest sendMessageRequest = new SendMessageRequest(this.queueURL, message);
			sendMessageRequest.setMessageGroupId("messageGroup1");
			sqs.sendMessage(sendMessageRequest);
			final SendMessageResult sendMessageResult = sqs.sendMessage(sendMessageRequest);
			final String sequenceNumber = sendMessageResult.getSequenceNumber();
			final String messageId = sendMessageResult.getMessageId();
			System.out.println(
					"SendMessage succeed with messageId " + messageId + ", sequence number " + sequenceNumber + "\n");
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public void printQueue() {
		try {
			System.out.println("Receiving messages from MyQueue.\n");
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueURL);
			receiveMessageRequest.setMaxNumberOfMessages(10);
			receiveMessageRequest.withMaxNumberOfMessages(10).withWaitTimeSeconds(20);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			for (Message message : messages) {
				System.out.println("  Message");
				System.out.println("    MessageId:     " + message.getMessageId());
				System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
				System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
				System.out.println("    Body:          " + message.getBody());
				for (Entry<String, String> entry : message.getAttributes().entrySet()) {
					System.out.println("  Attribute");
					System.out.println("    Name:  " + entry.getKey());
					System.out.println("    Value: " + entry.getValue());
				}
			}
			System.out.println();
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}

	public String getFromQueue() {
		String key = "";
		try {
			List<Message> messages = sqs.receiveMessage(this.queueURL).getMessages();
			if(messages.size() == 0)
				return null;
			System.out.println("Getting a message");
			key = messages.get(0).getBody();
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(this.queueURL, messageReceiptHandle));
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} catch(IndexOutOfBoundsException iobe) {
			return null;
		}
		return key;
	}
	
	public int getQueueSize() {
		  int len = 0; 
		  try 
		  { 
			  GetQueueAttributesRequest getQueueAttributesRequest = new GetQueueAttributesRequest(this.queueURL).withAttributeNames("ApproximateNumberOfMessages");
			  GetQueueAttributesResult getQueueAttributesResult = sqs.getQueueAttributes(getQueueAttributesRequest);
			  len = Integer.parseInt(getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessages"));
			  System.out.println("Current queue length - " + len);
		  } 
		  catch(AmazonServiceException ase) 
		  { 
			  System.out.println("Caught an AmazonServiceException, which means your request made it "
					  + "to Amazon SQS, but was rejected with an error response for some reason.");
			  System.out.println("Error Message:    " + ase.getMessage());
			  System.out.println("HTTP Status Code: " + ase.getStatusCode());
			  System.out.println("AWS Error Code:   " + ase.getErrorCode());
			  System.out.println("Error Type:       " + ase.getErrorType());
			  System.out.println("Request ID:       " + ase.getRequestId());
		  } 
		  catch(AmazonClientException ace) 
		  { 
			  System.out.println("Caught an AmazonClientException, which means the client encountered "+
					  "a serious internal problem while trying to communicate with SQS, such as not "
					  + "being able to access the network."); System.out.println("Error Message: "+ ace.getMessage());
		  }
		  return len;
	}

	public void deleteFromQueue() {
		try {
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(this.queueURL);
			List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
			System.out.println("Deleting a message.\n");
			String messageReceiptHandle = messages.get(0).getReceiptHandle();
			sqs.deleteMessage(new DeleteMessageRequest(this.queueURL, messageReceiptHandle));
		} catch (AmazonServiceException ase) {
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		}
	}
}