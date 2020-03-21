package com.amazonaws.compute;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.amazonaws.services.s3.model.S3ObjectInputStream;

public class Driver {
	
	private static S3Connect s3obj = new S3Connect();
	private static SQSConnect sqsobj = new SQSConnect();
	
	public static void main(String[] args) throws IOException, InterruptedException {
		int len = sqsobj.getQueueLength();
		while(len != 0) {
			TimeUnit.SECONDS.sleep(10);
			new Driver().compute();
			TimeUnit.SECONDS.sleep(10);
			len = sqsobj.getQueueLength();
		}
	}
	
	public void compute() throws InterruptedException {
		String key = sqsobj.getFromQueue();
		try {
			System.out.println("Processing " + key);
			S3ObjectInputStream objectContent = s3obj.getFromS3(key);
			IOUtils.copy(objectContent, new FileOutputStream("C:\\Users\\kastu\\Downloads\\CC\\" + key));
			
			String newKey = key.substring(0, key.indexOf("."));
			//dummy code to process output
			File output = createSampleFile();
			s3obj.addToS3("/results/" + newKey + ".txt", output);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static File createSampleFile() throws IOException {
        File file = File.createTempFile("aws-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("01234567890112345678901234\n");
        writer.write("!@#$%^&*()-=[]{};':',.<>/?\n");
        writer.write("01234567890112345678901234\n");
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.close();

        return file;
    }

}
