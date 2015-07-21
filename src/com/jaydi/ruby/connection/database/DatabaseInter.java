package com.jaydi.ruby.connection.database;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.appspot.ruby_mine.rubymine.model.Rubyzone;
import com.jaydi.ruby.threading.ThreadManager;
import com.jaydi.ruby.threading.Work;

public class DatabaseInter {

	public static <T> void addRubyzone(final Context context, Handler handler, final Rubyzone rubyzone) {
		ThreadManager.execute(new Work<T>() {

			@Override
			public T work() throws IOException {
				DatabaseHandler dbHandler = new DatabaseHandler(context);
				dbHandler.addRubyzone(rubyzone);
				return null;
			}

		}, handler);
	}

	public static <T> void addRubyzones(final Context context, Handler handler, final List<Rubyzone> rubyzones) {
		ThreadManager.execute(new Work<T>() {

			@Override
			public T work() throws IOException {
				DatabaseHandler dbHandler = new DatabaseHandler(context);
				dbHandler.addRubyzones(rubyzones);
				return null;
			}

		}, handler);
	}

	public static <T> void getRubyzones(final Context context, Handler handler) {
		ThreadManager.execute(new Work<T>() {

			@SuppressWarnings("unchecked")
			@Override
			public T work() throws IOException {
				DatabaseHandler dbHandler = new DatabaseHandler(context);
				return (T) dbHandler.getRubyzones();
			}

		}, handler);
	}

	public static <T> void deleteRubyzone(final Context context, Handler handler, final long id) {
		ThreadManager.execute(new Work<T>() {

			@Override
			public T work() throws IOException {
				DatabaseHandler dbHandler = new DatabaseHandler(context);
				dbHandler.deleteRubyzone(id);
				return null;
			}

		}, handler);
	}

	public static <T> void deleteRubyzoneAll(final Context context, Handler handler) {
		ThreadManager.execute(new Work<T>() {

			@Override
			public T work() throws IOException {
				DatabaseHandler dbHandler = new DatabaseHandler(context);
				dbHandler.deleteRubyzoneAll();
				return null;
			}

		}, handler);
	}

}
