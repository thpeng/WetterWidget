package ch.fancy.tools.proto;

import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MeteoWidget extends AppWidgetProvider {
	public static MeteoWidget Widget = null;
	public static Context context;
	public static AppWidgetManager appWidgetManager;
	public static int appWidgetIds[];
	public static final Map<String, Integer> PICMAP = initMap();
	public static boolean init = false;
	private FetchData fetcher;

	private static Map<String, Integer> initMap() {
		//no guava in android? what a shame. 
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("1.gif", R.drawable.img1);
		map.put("2.gif", R.drawable.img2);
		map.put("3.gif", R.drawable.img3);
		map.put("4.gif", R.drawable.img4);
		map.put("5.gif", R.drawable.img5);
		map.put("6.gif", R.drawable.img6);
		map.put("7.gif", R.drawable.img7);
		map.put("8.gif", R.drawable.img8);
		map.put("9.gif", R.drawable.img9);
		map.put("10.gif", R.drawable.img10);
		map.put("11.gif", R.drawable.img11);
		map.put("12.gif", R.drawable.img12);
		map.put("13.gif", R.drawable.img13);
		map.put("14.gif", R.drawable.img14);
		map.put("15.gif", R.drawable.img15);
		map.put("16.gif", R.drawable.img16);
		map.put("17.gif", R.drawable.img17);
		map.put("18.gif", R.drawable.img18);
		map.put("19.gif", R.drawable.img19);
		map.put("20.gif", R.drawable.img20);
		map.put("21.gif", R.drawable.img21);
		map.put("22.gif", R.drawable.img22);
		map.put("23.gif", R.drawable.img23);
		map.put("24.gif", R.drawable.img24);
		map.put("25.gif", R.drawable.img25);
		map.put("26.gif", R.drawable.img26);
		map.put("27.gif", R.drawable.img27);
		map.put("28.gif", R.drawable.img28);
		return map;
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		if (null == context)
			context = MeteoWidget.context;
		if (null == appWidgetManager)
			appWidgetManager = MeteoWidget.appWidgetManager;
		if (null == appWidgetIds)
			appWidgetIds = MeteoWidget.appWidgetIds;

		MeteoWidget.Widget = this;
		MeteoWidget.context = context;
		MeteoWidget.appWidgetManager = appWidgetManager;
		MeteoWidget.appWidgetIds = appWidgetIds;

		Log.i("meteowidget", "###########################onUpdate");

		final int N = appWidgetIds.length;
		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];

			updateAppWidget(context, appWidgetManager, appWidgetId);
		}

	}

	void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
			int appWidgetId) {

		Intent intent = new Intent(context, UpdateService.class);
		PendingIntent pendingIntent = PendingIntent.getService(context, 0,
				intent, 0);

		RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		remoteViews.setOnClickPendingIntent(R.id.LinearLayout01, pendingIntent);

		MeteoModel model = null;
		try {
			if (fetcher == null) {
				fetcher = new FetchData(context);
			}

			model = fetcher.fetch(context);
		} catch (Exception e) {
			// make sure that this app doesn't crash.
			Log.e("meteowidget", "failed with exc: ", e);
			if (!init) {
				Log.d("meteowidget","first run crash. will revert to default model");
				// only make a default model for the first run of this widget
				model = new MeteoModel();
			}
			else
			{
				Log.d("meteowidget", "crashed, but recovering with old model");
			}
		}
		//mark the widget as started. 
		//all following updates with crashes will revert to the latest model
		init = true;
		// update all fields (only with a valid model)
		if (model != null) {
			int baseSym = R.id.widget_day1;
			int baseDate = R.id.date1;
			int baseTemp = R.id.temp1;
			for (int i = 0; i < 6; i++) {
				//update remoteview
				remoteViews.setImageViewResource(baseSym + i,
						PICMAP.get(model.getSyms()[i]));
				remoteViews.setTextViewText(baseDate + i, model.getDates()[i]);
				remoteViews.setTextViewText(baseTemp + i, model.getTemps()[i]);
			}
			remoteViews.setTextViewText(R.id.place, model.getLocation());
			remoteViews.setTextViewText(R.id.zip, model.getZip());

			// Tell the widget manager
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	public static class UpdateService extends Service {
		@Override
		public void onStart(Intent intent, int startId) {
			MeteoWidget.Widget
					.onUpdate(context, appWidgetManager, appWidgetIds);
			Toast.makeText(context, "Update Widget", Toast.LENGTH_SHORT).show();
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return null;
		}
	}

}