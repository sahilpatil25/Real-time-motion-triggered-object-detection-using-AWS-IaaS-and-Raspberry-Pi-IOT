package com.amazonaws.compute;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class Driver {
	
	private static S3Connect s3obj;
	private static SQSConnect sqsobj;
	private static boolean pi=false;
	private static String videoPath_EC2 = "/home/ubuntu/videos/";
	private static String videoPath_Pi = "/home/pi/Videos/";
	private static String videoFilePath;
	
	public Driver() {
		s3obj = new S3Connect();
		sqsobj = new SQSConnect();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		  Driver d = new Driver(); 
		  int len = sqsobj.getQueueSize();
		  String uname = System.getProperty("user.name").toString();
		  if(uname.equals("pi"))
			  d.pi=true;
		  System.out.println("Current length of queue is "+len);
		  boolean keepRunning=true;
		  while(keepRunning) 
		  {
			  d.compute();
			  len = sqsobj.getQueueSize();
			  System.out.println("Current length of queue is "+len);
			  if(len==0)
			  {
				  System.out.println("Queue empty!");
				  int count = 0;
				  while(len==0 && count<7)
				  {
					  Thread.sleep(5000);
					  len =sqsobj.getQueueSize();
					  count++;
				  }
				  //If Queue is empty after waiting and the processor is EC2 (and not pi) then shutdown the EC2 instance
				  if(len==0 && !pi)
					  shutdown();
			  }
		  }
	}
	
	public void compute() throws InterruptedException {
		String key = sqsobj.getFromQueue();
		if(key == null)
			return;
		try {
			System.out.println("Processing " + key);
			S3ObjectInputStream objectContent = s3obj.getFromS3(key);
			if(pi)
			{
				IOUtils.copy(objectContent, new FileOutputStream(videoPath_Pi + key));
				videoFilePath = videoPath_Pi +key;
			}
			else
			{
				//For EC2
				IOUtils.copy(objectContent, new FileOutputStream(videoPath_EC2 + key));
				videoFilePath = videoPath_EC2 +key;
			}
			
			System.out.println("Video downloaded at : "+videoFilePath);
			String[] temp = key.split("\\.");
			String newKey = temp[0];
			System.out.println("Preparing to run YOLO on: "+newKey);
			ProcessBuilder processBuilder = new ProcessBuilder();
			//Invoke Darknet Thread
			if(pi)
			{
		        processBuilder.command("/home/pi/demo/darknet.sh", videoFilePath);
			}
			else
			{
				//For EC2
		        processBuilder.command("/home/ubuntu/darknet/darknet.sh", videoFilePath);
			}
			
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
 
            } 
            else 
            {
                System.out.println("Darknet command failed!");
            }
            
            StringBuilder result = new StringBuilder();
            
            if(output.length() > 0) {
            
	            String[] first = output.toString().split("\\n");
	    		Set<String> set = new HashSet<String>();
	    		Pattern p = Pattern.compile("[a-z]+$");
	    		for(String x : first) 
	    		{
	    			if(x.contains("%")) {
	        			String[] second = x.split(":");
	        			set.add(second[0].toLowerCase());
	    			}
	    		}
	            System.out.println("Parsing done!");
	        		
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
            } else {
            	result.append("no object detected");
            }
            System.out.println("Uploading output to S3");

            s3obj.addToS3(newKey, result.toString());
            System.out.println("Output uploaded on S3");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static int shutdown() throws IOException
	{
		  if(pi)
		  {
			  return 0;
		  }
		  else {
			  //For EC2
			  Runtime.getRuntime().exec("sudo shutdown -h now");
		  }
		return 0;
	}

}
