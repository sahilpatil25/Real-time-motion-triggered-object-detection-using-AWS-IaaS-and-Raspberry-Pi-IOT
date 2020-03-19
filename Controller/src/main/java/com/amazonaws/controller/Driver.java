package com.amazonaws.controller;

import java.util.List;
import java.util.Map;

public class Driver {

	public static void main(String[] args) {
		int jobs;
		Controller controller = new Controller();
		
		//while()
		//{
			jobs = controller.checkJobQueue();
			System.out.println("Number of jobs in the queue: "+jobs);
			List<String> available = controller.getAvailabeInstances();
			if(available.size()==0)
				System.out.println("No EC2 Instances avaiable at the moment");
			for(int i=0; i<jobs && i<available.size();i++)
			{
				EC2Remote.startInstance(controller.getEc2(),available.get(i));
			}
			//Thread.sleep(1000);
		//}
	}

}
