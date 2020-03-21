package com.amazonaws.upload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

public class Driver {
	
	public static void main(String[] args) throws IOException {
		S3Connect s3obj = new S3Connect();
		SQSConnect sqsobj = new SQSConnect();
		
		String filename = args[0];
		String filepath = args[1];
		String fileCategory = args[2];
		
		File file = new File(filepath);
		
		s3obj.addToS3(filename, file, fileCategory);
		//s3obj.printAllS3();
		if(fileCategory.equals("video"))
			sqsobj.addToQueue(filename);
		//sqsobj.printQueue();
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
