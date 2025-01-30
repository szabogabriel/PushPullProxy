package com.github.szabogabriel.ppp;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class AbstractServer {
	
	private static final int THREAD_POOL_SIZE = 10;

	private Executor executor;
	
	protected AbstractServer() {
		executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}
	
	protected void execute(Runnable r) {
		executor.execute(r);
	}

}
