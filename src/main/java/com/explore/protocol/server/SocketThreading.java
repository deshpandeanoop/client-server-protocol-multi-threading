package com.explore.protocol.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketThreading {
	private Map<String, String> data = new HashMap<>();
	private Object lock = new Object();
	private static final String REQUEST_TYPE_STORE = "store";
	private static final String REQUEST_TYPE_FETCH = "fetch";

	@SuppressWarnings("resource")
	public static void main(String[] args) throws IOException {
		ServerSocket listener = new ServerSocket(9000);
		SocketThreading socketThreading = new SocketThreading();
		while (true) {
			Socket socket = listener.accept();
			Runnable runnable = () -> socketThreading.handleRequest(socket);
			Thread thread = new Thread(runnable);
			thread.start();
		}
	}

	@SuppressWarnings("static-access")
	public void handleRequest(Socket request) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String requestMessage = reader.readLine();
			PrintWriter printWriter = new PrintWriter(request.getOutputStream());
			printWriter.println(routeRequest(requestMessage));
			printWriter.flush();
		} catch (IOException ioException) {
			System.out.println("Something went wrong while writing response");
		}
	}

	private String routeRequest(String requestMessage) {
		String[] requestTokens = requestMessage.split("\\?");
		if (REQUEST_TYPE_STORE.equalsIgnoreCase(requestTokens[0])) {
			synchronized (lock) {
				String[] data = requestTokens[1].split("=");
				this.data.put(data[0].trim(), data[1].trim());
				lock.notifyAll();
				//return "";
				return "Data " + data[0] + ";" + data[1] + "   is stored";
			}
		} else if (REQUEST_TYPE_FETCH.equalsIgnoreCase(requestTokens[0])) {
			synchronized (lock) {
				while(true) {
					if(null == data.get(requestTokens[1])) {
						try {
							lock.wait();
						}
						catch(InterruptedException interruptedException) {
							System.out.println("Fetching thread is been interupted");
						}
					}
					if(data.get(requestTokens[1])!=null) {
						break;
					}
				}
				return data.get(requestTokens[1]);
			}
		} else {
			return "Malformed URL, cannot process the request";
		}
	}
}
