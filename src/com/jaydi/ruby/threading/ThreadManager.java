package com.jaydi.ruby.threading;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class ThreadManager<T> implements Runnable {
	private static ExecutorService executorService = Executors.newCachedThreadPool();

	public static <T> void execute(final Work<T> work, final Handler handler) {
		executorService.execute(new ThreadManager<T>(work, handler));
	}

	private Work<T> work;
	private Handler handler;

	public ThreadManager(Work<T> work, Handler handler) {
		super();
		this.work = work;
		this.handler = handler;
	}

	private int rc = 0;

	@Override
	public void run() {
		try {
			T response = (T) work.work();
			if (handler != null) {
				Message m = new Message();
				m.obj = response;
				handler.sendMessage(m);
			}
			
			rc = 0;

		} catch (IOException e) {
			retry();
		}
	}
	
	private void retry() {
		if (rc < 2) {
			rc ++;
			Log.i("WORK", "work crahsed, try again " + rc);
			run();
		} else
			rc = 0;
	}
}
