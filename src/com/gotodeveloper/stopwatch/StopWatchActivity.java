package com.gotodeveloper.stopwatch;

import com.scanadu.stopwatch.R;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class StopWatchActivity extends Activity implements OnClickListener {

	private Button startButton;
	private Button stopButton;
	private Button lapButton;

	private TextView hoursTextView;
	private TextView minutesTextView;
	private TextView secondsTextView;

	private ClockAsyncTask asyncTask;

	private Handler handler;

	@SuppressWarnings("rawtypes")
	public class ClockAsyncTask extends AsyncTask {
		private Handler handler;

		public ClockAsyncTask(Handler handler) {
			this.handler = handler;
		}

		@Override
		protected void onProgressUpdate(Object... values) {

			int hours = (Integer) values[0];
			int minutes = (Integer) values[1];
			int seconds = (Integer) values[2];

			Log.d("STOPTWATCH", "hours: " + hours + " minutes: " + minutes
					+ " seconds:" + seconds);

			Message msg = new Message();
			Bundle bundle = new Bundle();
			bundle.putInt("hours", hours);
			bundle.putInt("minutes", minutes);
			bundle.putInt("seconds", seconds);
			msg.setData(bundle);
			handler.dispatchMessage(msg);

			// ((TextView) findViewById(R.id.hours)).setText(hours);
			// ((TextView) findViewById(R.id.minutes)).setText(minutes);
			// ((TextView) findViewById(R.id.seconds)).setText(seconds);

			super.onProgressUpdate(values);
		}

		@Override
		protected Object doInBackground(Object... params) {

			// in a loop keep updating the values for hours, minutes, seconds
			// and
			// call publishProgress(values)
			int hours = 0;
			int minutes = 0;
			int seconds = 0;

			while (true) {
				if (isCancelled()) {
					return null;
				}

				try {
					synchronized (this) {
						wait(1000);
					}
					seconds++;
					if (seconds % 60 == 0) {
						seconds = 0;
						minutes++;
						if (minutes % 60 == 0) {
							minutes = 0;
							hours++;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				publishProgress(hours, minutes, seconds);

			}
		}
	}

	Callback callback = new Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			hoursTextView.setText(Integer
					.valueOf(msg.getData().getInt("hours")).toString());
			minutesTextView.setText(Integer.valueOf(
					msg.getData().getInt("minutes")).toString());
			secondsTextView.setText(Integer.valueOf(
					msg.getData().getInt("seconds")).toString());

			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_stop_watch);

		startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(this);
		stopButton = (Button) findViewById(R.id.stopButton);
		stopButton.setOnClickListener(this);
		lapButton = (Button) findViewById(R.id.lapButton);
		lapButton.setOnClickListener(this);

		hoursTextView = (TextView) findViewById(R.id.hours);
		minutesTextView = (TextView) findViewById(R.id.minutes);
		secondsTextView = (TextView) findViewById(R.id.seconds);

		handler = new Handler(callback);

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.startButton) {
			Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT)
					.show();
			asyncTask = new ClockAsyncTask(handler);
			asyncTask.execute();
		} else if (v.getId() == R.id.stopButton) {
			Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT)
					.show();
			asyncTask.cancel(true);
		} else if (v.getId() == R.id.lapButton) {
			Toast.makeText(getApplicationContext(), "Lap", Toast.LENGTH_SHORT)
					.show();
		}

	}

}
