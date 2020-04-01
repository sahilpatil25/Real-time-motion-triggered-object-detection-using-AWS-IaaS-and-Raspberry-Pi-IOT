package com.amazonaws.upload;

import java.io.File;
import java.io.IOException;

public class Driver {
	
	public static void main(String[] args) throws IOException {
		S3Connect s3obj = new S3Connect();
		SQSConnect sqsobj = new SQSConnect();
		
		String filename = args[0];
		String filepath = args[1];
		String fileCategory = args[2];
		
		File file = new File(filepath);
		s3obj.addToS3(filename, file, fileCategory);
		
		if(fileCategory.equals("video"))
			sqsobj.addToQueue(filename);
	}
}
