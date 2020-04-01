package com.amazonaws.controller;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DryRunResult;
import com.amazonaws.services.ec2.model.DryRunSupportedRequest;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesRequest;

/**
* Starts or stops and EC2 instance
*/
public class EC2Remote
{
  public static void startInstance(AmazonEC2 ec2, String instance_id)
  {
      DryRunSupportedRequest<StartInstancesRequest> dry_request =
          () -> {
          StartInstancesRequest request = new StartInstancesRequest()
              .withInstanceIds(instance_id);

          return request.getDryRunRequest();
      };

      DryRunResult<StartInstancesRequest> dry_response = ec2.dryRun(dry_request);

      if(!dry_response.isSuccessful()) {
          System.out.printf(
              "Failed dry run to start instance %s", instance_id);

          throw dry_response.getDryRunResponse();
      }

      StartInstancesRequest request = new StartInstancesRequest()
          .withInstanceIds(instance_id);

      ec2.startInstances(request);

      System.out.printf("Starting instance %s", instance_id);
      System.out.println();
  }

  public static void stopInstance(AmazonEC2 ec2, String instance_id)
  {
      DryRunSupportedRequest<StopInstancesRequest> dry_request =
          () -> {
          StopInstancesRequest request = new StopInstancesRequest()
              .withInstanceIds(instance_id);

          return request.getDryRunRequest();
      };

      DryRunResult<StopInstancesRequest> dry_response = ec2.dryRun(dry_request);

      if(!dry_response.isSuccessful()) {
          System.out.printf(
              "Failed dry run to stop instance %s", instance_id);
          throw dry_response.getDryRunResponse();
      }

      StopInstancesRequest request = new StopInstancesRequest()
          .withInstanceIds(instance_id);

      ec2.stopInstances(request);

      System.out.printf("Successfully stop instance %s", instance_id);
  }
}