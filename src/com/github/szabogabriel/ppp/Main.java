package com.github.szabogabriel.ppp;

public class Main {

	public static void main(String[] args) {
		RuntimeArguments arguments = new RuntimeArguments(args);
		
		switch(arguments.getServerType()) {
		case PULL:
			new PullServerMain(arguments);
			break;
		case PUSH:
			new PushServerMain(arguments);
			break;
		default:
			System.err.println("No server type set.");
			System.exit(-1);
		}
	}
	
}
