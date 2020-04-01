package com.amazonaws.compute;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

/*
 * Class for AWS S3 Operations
 */

public class S3Connect {
	
	private static AmazonS3 s3;
	private static String videoBucketName;
	private static String outputBucketName;
	
	public S3Connect() 
	{
		s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1") .build();
        
        System.out.println("===========================================");
        System.out.println("Initializing S3 Client");
        System.out.println("===========================================");
        
        videoBucketName = "cse546-s3-video-bucket";
        outputBucketName = "cse546-s3-output-bucket";
        
        s3.createBucket(videoBucketName);
        s3.createBucket(outputBucketName);
	}
	
	public void listBuckets() 
	{
		try {
			System.out.println("Listing buckets");
	        for (Bucket bucket : s3.listBuckets()) {
	            System.out.println(" - " + bucket.getName());
	        }
	        System.out.println();
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	public void addToS3(String key, String result) throws IOException {
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			s3.putObject(new PutObjectRequest(outputBucketName, key, createResultFile(result)));
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	public S3ObjectInputStream getFromS3(String key) throws IOException 
	{
		System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(videoBucketName, key));
		return object.getObjectContent();
	}
	
	public void printAllS3() 
	{
		try {
			System.out.println("Listing objects");
	        ObjectListing objectListing = s3.listObjects(new ListObjectsRequest()
	                .withBucketName(videoBucketName));
	        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
	            System.out.println(" - " + objectSummary.getKey() + "  " +
	                               "(size = " + objectSummary.getSize() + ")");
	        }
	        System.out.println();
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	public void deleteFromS3(String key) {
		try {
			System.out.println("Deleting an object\n");
			s3.deleteObject(videoBucketName, key);
		} catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to Amazon S3, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with S3, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
	}
	
	private static File createResultFile(String result) throws IOException {
	       File file = File.createTempFile("result", ".txt");
	       file.deleteOnExit();
	       Writer writer = new OutputStreamWriter(new FileOutputStream(file));
	       writer.write(result);
	       writer.close();
	       return file;
	}
}
