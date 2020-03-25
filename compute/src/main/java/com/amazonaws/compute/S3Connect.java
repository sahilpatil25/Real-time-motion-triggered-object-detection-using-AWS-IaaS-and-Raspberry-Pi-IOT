package com.amazonaws.compute;
/*
 * Copyright 2010-2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;

public class S3Connect {
	
	private static AmazonS3 s3;
	private static String videoBucketName;
	private static String outputBucketName;
	
	public S3Connect() {
		/*
		 * AWSCredentials credentials = null; try { credentials = new
		 * ProfileCredentialsProvider("default").getCredentials(); } catch (Exception e)
		 * { throw new AmazonClientException(
		 * "Cannot load the credentials from the credential profiles file. " +
		 * "Please make sure that your credentials file is at the correct " +
		 * "location (C:\\Users\\kastu\\.aws\\credentials), and is in valid format.",
		 * e); }
		 * 
		 * try {
		 * 
		 * } catch (AmazonServiceException ase) { System.out.
		 * println("Caught an AmazonServiceException, which means your request made it "
		 * + "to Amazon S3, but was rejected with an error response for some reason.");
		 * System.out.println("Error Message:    " + ase.getMessage());
		 * System.out.println("HTTP Status Code: " + ase.getStatusCode());
		 * System.out.println("AWS Error Code:   " + ase.getErrorCode());
		 * System.out.println("Error Type:       " + ase.getErrorType());
		 * System.out.println("Request ID:       " + ase.getRequestId()); } catch
		 * (AmazonClientException ace) { System.out.
		 * println("Caught an AmazonClientException, which means the client encountered "
		 * + "a serious internal problem while trying to communicate with S3, " +
		 * "such as not being able to access the network.");
		 * System.out.println("Error Message: " + ace.getMessage()); }
		 * 
		 * s3 = AmazonS3ClientBuilder.standard() .withCredentials(new
		 * AWSStaticCredentialsProvider(credentials)) .withRegion("us-east-1") .build();
		 */
		
		s3 = AmazonS3ClientBuilder.standard().withRegion("us-east-1") .build();
        
        System.out.println("===========================================");
        System.out.println("Getting Started with Amazon S3");
        System.out.println("===========================================\n");
        
        videoBucketName = "cse546-s3-video-bucket";
        outputBucketName = "cse546-s3-output-bucket";
        System.out.println("Creating bucket " + videoBucketName + "\n");
        s3.createBucket(videoBucketName);
        
        System.out.println("Creating bucket " + outputBucketName + "\n");
        s3.createBucket(outputBucketName);
        
	}
	
	public void listBuckets() {
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
	
	public void addToS3(String key, File file) {
		try {
			System.out.println("Uploading a new object to S3 from a file\n");
			s3.putObject(new PutObjectRequest(outputBucketName, key, file));
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
	
	public S3ObjectInputStream getFromS3(String key) throws IOException {
		System.out.println("Downloading an object");
        S3Object object = s3.getObject(new GetObjectRequest(videoBucketName, key));
		try {
			System.out.println("Content-Type: "  + object.getObjectMetadata().getContentType());
	        displayTextInputStream(object.getObjectContent());
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
	    
		return object.getObjectContent();
	}
	
	public void printAllS3() {
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

    private static void displayTextInputStream(InputStream input) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null) break;

            System.out.println("    " + line);
        }
        System.out.println();
    }

}
