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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.IOUtils;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class Driver {
	
	private static S3Connect s3obj;
	private static SQSConnect sqsobj;
	
	public Driver() {
		s3obj = new S3Connect();
		sqsobj = new SQSConnect();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		  Driver d = new Driver(); 
		  int len = sqsobj.getQueueSize();
		  System.out.println("Current length of queue is "+len);
		  while(len != 0) 
		  {
			  d.compute();
			  len = sqsobj.getQueueSize();
			  System.out.println("Current length of queue is "+len);
			  if(len==0)
			  {
				  System.out.println("Queue empty!");
				  System.out.println("Waiting for 30 seconds to check again");
				  Thread.sleep(30000);
				  len =sqsobj.getQueueSize();
			  }
		  }
		  System.out.println("No more messages found in the queue after waiting. Shuttting down!");
		  shutdown();
	}
	public void compute() throws InterruptedException {
		String key = sqsobj.getFromQueue();
		try {
			System.out.println("Processing " + key);
			S3ObjectInputStream objectContent = s3obj.getFromS3(key);
			IOUtils.copy(objectContent, new FileOutputStream("/home/ubuntu/videos/" + key));
			String path = "/home/ubuntu/videos/" + key;
			System.out.println("Video downloaded at : "+path);
			String[] temp = key.split("\\.");

			String newKey = temp[0];

			System.out.println("Preparing to run YOLO on: "+newKey);
			ProcessBuilder processBuilder = new ProcessBuilder();
	        processBuilder.command("/home/ubuntu/darknet/darknet.sh", path);
			System.out.println("Invoked Darknet script");
	        Process process = processBuilder.start();
            StringBuilder output = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            int exitVal = process.waitFor();
			System.out.println("Darknet terminated with exit code : "+exitVal);
            if (exitVal == 0) 
            {
                System.out.println("Success!");
                System.out.println("Parsing results");

        		String[] first = output.toString().split("\\n");
        		Set<String> set = new HashSet<String>();
        		Pattern p = Pattern.compile("[a-z]+$");
        		for(String x : first) 
        		{
        			if(x.contains("%")) {
	        			String[] second = x.split(":");
	        			//Matcher m = p.matcher(second[1].toLowerCase());
	        			//if(second.length > 0 && m.find())
	        			set.add(second[0].toLowerCase());
        			}
        		}
                System.out.println("Parsing done!");

	        	StringBuilder result = new StringBuilder();
	        		
	        	if(set.isEmpty()) {
	    			result.append("no object detected");
	    		} 
	        	else 
	        	{
	        		Iterator<String> itr = set.iterator();
	        		while(itr.hasNext()) 
	        		{
	        			result.append(itr.next() + " ");
	        		}
	    		}
                System.out.println("Uploading output to S3");

                s3obj.addToS3(newKey, result.toString());
                System.out.println("Output uploaded on S3");

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
				 * System.out.println("Stopped the Instnace with ID: " + instanecID);*/
                //System.exit(0);
            } 
            else 
            {
                System.out.println("Darknet command failed!");
            }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void shutdown() throws IOException
	{
		Runtime.getRuntime().exec("sudo shutdown -h now");
	}

}
