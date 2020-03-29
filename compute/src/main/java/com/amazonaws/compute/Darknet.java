package com.amazonaws.compute;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Darknet
{
    public static void main(String[] args) {
        ProcessBuilder processBuilder = new ProcessBuilder();
        String argument = args[0];
        processBuilder.command("/home/ubuntu/darknet/darknet.sh",argument);

        try {
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
                System.out.println(output);
                System.exit(0);
            } else {
            	System.out.println("Abnormal!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
