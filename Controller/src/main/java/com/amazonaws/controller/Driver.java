package com.amazonaws.controller;

import java.util.List;

public class Driver {

	public static void main(String[] args) throws InterruptedException {
		int jobs;
		Controller controller = new Controller();
		while(true)
		{
			jobs = controller.checkJobQueue();
			System.out.println("Number of jobs in the queue: "+jobs);
			
			//If there is sufficient load in the queue. Then scale out
			if(jobs >=  5)
			{
				//Get currently available(Ready to start) EC2 instance list.
				List<String> available = controller.getAvailabeInstances();
				int len = available.size();
				if(len == 0)
				{
					System.out.println("No EC2 Instances avaiable at the moment");
				}
				else
				{
					if(len>5) 
					{
						//Limit starting of new EC2s to 5
						len = 5;
					}
					for(int i = 0 ; i < len ;i++)
					{
						EC2Remote.startInstance(controller.getEc2(), available.get(i));
					}
					//Wait for EC2 instances to boot up and pick up videos from the queue
					Thread.sleep(60000);
				}
			}
			else
			{
				Thread.sleep(5000);
			}
		}
	}
}
