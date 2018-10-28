package com.explore.protocol.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
public class ThreadClient {

	public static void main(String[] args) {
		ThreadClient threadClient = new ThreadClient();
		threadClient.triggerStoreRequests();
		threadClient.triggerFetchRequests();
	}
	public void triggerStoreRequests() {
		for (int i = 1; i <= 10; i++) {
			final int cnt =i;
		Runnable runnable = () -> {
			try {
					Socket socket = new Socket("192.168.0.14", 9000);
					PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
					printWriter.println("store? "+"index"+cnt+"="+"value"+cnt);
					printWriter.flush();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String response = reader.readLine();
					System.out.println(response);
					socket.close();
				}catch (Exception exception) {
				exception.printStackTrace();
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		}
	}
	public void triggerFetchRequests() {
		for (int i = 1; i <= 10; i++) {
			final int cnt =i;
		Runnable runnable = () -> {
			try {
					Socket socket = new Socket("192.168.0.14", 9000);
					PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
					printWriter.println("fetch?index"+cnt);
					printWriter.flush();
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String response = reader.readLine();
					System.out.println(response);
					socket.close();
				}catch (Exception exception) {
				exception.printStackTrace();
			}
		};
		Thread thread = new Thread(runnable);
		thread.start();
		}
	}
}
