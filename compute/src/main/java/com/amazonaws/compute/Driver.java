package com.amazonaws.compute;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class Driver {
	
	private static S3Connect s3obj;
	private static SQSConnect sqsobj;
	
	public Driver() {
		s3obj = new S3Connect();
		sqsobj = new SQSConnect();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		
		  Driver d = new Driver(); int len = sqsobj.getQueueLength(); while(len != 0) {
		  TimeUnit.SECONDS.sleep(10); d.compute(); TimeUnit.SECONDS.sleep(10); len =
		  sqsobj.getQueueLength(); }
		 

	}
	
	public void compute() throws InterruptedException {
		TimeUnit.SECONDS.sleep(10);
		String key = sqsobj.getFromQueue();
		try {
			System.out.println("Processing " + key);
			S3ObjectInputStream objectContent = s3obj.getFromS3(key);
			IOUtils.copy(objectContent, new FileOutputStream("/home/ubuntu/videos/" + key));
			
			String path = "/home/ubuntu/videos/" + key;
			String[] temp = key.split("\\.");
			System.out.println("Acquired key split " + temp.length);
			String newKey = temp[0];

			ProcessBuilder processBuilder = new ProcessBuilder();
	        processBuilder.command("/home/ubuntu/darknet/darknet.sh", path);
	        
	        Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }

            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("Success!");
                
        		String[] first = output.toString().split("FPS:");
        		Set<String> set = new HashSet<String>();
        		Pattern p = Pattern.compile("[a-z]+$");
        		for(String x : first) {
        			String[] second = x.split(":");
        			Matcher m = p.matcher(second[1].toLowerCase());
        			if(second.length > 0 && m.find())
        				set.add(second[1].toLowerCase());
        		}
        		
        		StringBuilder result = new StringBuilder();
        		
        		if(set.isEmpty()) {
        			result.append("no object detected");
        		} else {
            		Iterator<String> itr = set.iterator();
            		while(itr.hasNext()) {
            			result.append(itr.next() + " ");
            		}
        		}
        		
    			s3obj.addToS3(newKey + ".txt", createResultFile(result.toString()));
                
				/*
				 * AmazonEC2 ec2Client = AmazonEC2ClientBuilder.standard()
				 * .withRegion(Regions.US_EAST_1) .build();
				 * 
				 * String instanecID = "i-0739c0ea4202ef40f"; StopInstancesRequest
				 * stopInstancesRequest = new StopInstancesRequest()
				 * .withInstanceIds(instanecID);
				 * 
				 * ec2Client.stopInstances(stopInstancesRequest) .getStoppingInstances() .get(0)
				 * .getPreviousState() .getName();
				 * System.out.println("Stopped the Instnace with ID: " + instanecID);
				 */
                
                System.exit(0);
            } else {
                System.out.println("Darknet command failed!");
            }

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
