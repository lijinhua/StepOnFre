/**
 * Copyright (C) 2009, 2010 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corusen.steponfre.chart;

import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import com.corusen.steponfre.R;
import com.corusen.steponfre.base.AccuService;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Display;
import android.view.WindowManager;

/**
 * Average temperature demo chart.
 */
public class HourBarChart2 extends AbstractDemoChart2 {
	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Average temperature";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The average temperature in 4 Greek islands (line chart)";
	}

	public GraphicalView graphicalView(Context context, List<double[]> x, List<double[]> values, boolean screenLarge) {
		String[] titles = new String[] { "" }; // WL change

		int[] colors = new int[] { context.getResources().getColor(R.color.myblue) };

		double maxStep = 0.0;
		// int totalSteps = 0;
		// int aveSteps = 0;
		// int count = 0;
		double[] xlabels = (double[]) x.get(0);
		double[] dSteps = (double[]) values.get(0);

		for (int i = 0; i < dSteps.length; i++) {
			if (dSteps[i] > maxStep) {
				maxStep = dSteps[i];
			}
			// if (dSteps[i] >= 1.0) {
			// count++;
			// }
			// totalSteps = totalSteps + (int) dSteps[i];
		}

		// if (count == 0) {
		// aveSteps = 0;
		// } else {
		// aveSteps = (int) (totalSteps / count);
		// }
		// titles[0] = titles[0] + ((Integer) totalSteps).toString() + ", "
		// + context.getString(R.string.ave) + ": "
		// + ((Integer) aveSteps).toString();

		double maxY;
		int q = (int) (maxStep / 200);
		maxY = (double) (q + 1) * 200.0;
		if (maxY < 200) {
			maxY = 200;
		}

		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors, screenLarge);
		renderer.setOrientation(Orientation.HORIZONTAL);

		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x;
		// int height = size.y;

		int width = display.getWidth();
		int height = display.getHeight();

		// if (screenLarge) {
		// if ((width >= 720) && (height >= 1280)) {
		// // for 4.8" S3
		// renderer.setAxisTitleTextSize(30);
		// renderer.setChartTitleTextSize(33);
		// renderer.setLabelsTextSize(27);
		// renderer.setLegendTextSize(33); // WL change
		// renderer.setPointSize(10f);
		// renderer.setMargins(new int[] { 25, 50, 0, 20 }); //JS WL change
		// renderer.setShowLegend(false);
		// } else {
		// // for 4" screen
		// /* renderer.setAxisTitleTextSize(20);
		// renderer.setChartTitleTextSize(22);
		// renderer.setLabelsTextSize(18);
		// renderer.setLegendTextSize(22); // WL change
		// renderer.setPointSize(7f);
		// renderer.setMargins(new int[] { 20, 50, 50, 10 }); // WL change
		// */
		// renderer.setAxisTitleTextSize(20);
		// renderer.setChartTitleTextSize(22);
		// renderer.setLabelsTextSize(18);
		// renderer.setLegendTextSize(22); // WL change
		// renderer.setPointSize(7f);
		// renderer.setMargins(new int[] { 15, 30, 10, 15 }); // WL change top, left, bottom, right
		// //renderer.setShowLegend(false);
		// }
		// } else {
		// // small screen
		// renderer.setAxisTitleTextSize(11); // For smaller screen Aug. 31
		// renderer.setChartTitleTextSize(12);
		// renderer.setLabelsTextSize(11);
		// renderer.setLegendTextSize(11);
		// renderer.setMargins(new int[] { 10, 25, 8, 10 });
		// //renderer.setShowLegend(false);
		// }

		if (width >= 1080) {
			// for 5.0" S4
			renderer.setAxisTitleTextSize(40);
			renderer.setChartTitleTextSize(44);
			renderer.setLabelsTextSize(36);
			renderer.setLegendTextSize(44); // WL change
			renderer.setPointSize(13f);
			renderer.setMargins(new int[] { 35, 75, 0, 25 }); // JS WL change
			renderer.setShowLegend(false);
		} else if (width >= 720) {
			// for 4.8" S3
			renderer.setAxisTitleTextSize(30);
			renderer.setChartTitleTextSize(33);
			renderer.setLabelsTextSize(27);
			renderer.setLegendTextSize(33); // WL change
			renderer.setPointSize(10f);
			renderer.setMargins(new int[] { 25, 65, 0, 20 }); // JS WL change
			renderer.setShowLegend(false);

		} else if (width >= 480) {
			// for 4" screen
			/*
			 * renderer.setAxisTitleTextSize(20); renderer.setChartTitleTextSize(22); renderer.setLabelsTextSize(18); renderer.setLegendTextSize(22); // WL
			 * change renderer.setPointSize(7f); renderer.setMargins(new int[] { 20, 50, 50, 10 }); // WL change
			 */
			renderer.setAxisTitleTextSize(20);
			renderer.setChartTitleTextSize(22);
			renderer.setLabelsTextSize(18);
			renderer.setLegendTextSize(22); // WL change
			renderer.setPointSize(7f);
			renderer.setMargins(new int[] { 15, 40, 10, 15 }); // WL change top,
																// left, bottom,
																// right
			// renderer.setShowLegend(false);
		} else {
			// small screen
			renderer.setAxisTitleTextSize(11); // For smaller screen Aug. 31
			renderer.setChartTitleTextSize(12);
			renderer.setLabelsTextSize(11);
			renderer.setLegendTextSize(11);
			renderer.setMargins(new int[] { 10, 30, 8, 10 });
			// renderer.setShowLegend(false);
		}

		setChartSettings(renderer, "", "", "", 0.0, 60.0, 0, maxY, Color.WHITE, Color.WHITE); // x-axis
																																					// title,
																																					// y-axis
																																					// title
		renderer.setXLabels(1);
		renderer.setYLabels(1); // WL change

		renderer.addXTextLabel(0, ((Integer) (int) xlabels[0]).toString());
		renderer.addXTextLabel(10, ((Integer) (int) xlabels[1]).toString());
		renderer.addXTextLabel(20, ((Integer) (int) xlabels[2]).toString());
		renderer.addXTextLabel(30, ((Integer) (int) xlabels[3]).toString());
		renderer.addXTextLabel(40, ((Integer) (int) xlabels[4]).toString());
		renderer.addXTextLabel(50, ((Integer) (int) xlabels[5]).toString());
		renderer.addXTextLabel(60, ((Integer) (int) xlabels[6]).toString());

		if (maxY <= 200.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(100, "100 ");
			renderer.addYTextLabel(200, "200 ");
		} else if (maxY <= 400.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(200, "200 ");
			renderer.addYTextLabel(400, "400 ");
		} else if (maxY <= 600.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(300, "300 ");
			renderer.addYTextLabel(600, "600 ");
		} else if (maxY <= 800.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(400, "400 ");
			renderer.addYTextLabel(800, "800 ");
		} else {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(500, "500 ");
			renderer.addYTextLabel(1000, "1K ");
		}

		renderer.setBarSpacing(0.2f); // WL 1/23/12

		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setXLabelsColor(Color.WHITE); // WL 1/23/12
		renderer.setYLabelsColor(0, Color.WHITE); // WL 1/23/12

		renderer.setShowGrid(false);

		renderer.setDisplayChartValues(false);
		// SimpleSeriesRenderer.setDisplayChartValues(false);

		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(context.getResources().getColor(R.color.myddarkgray));

		renderer.setMarginsColor(context.getResources().getColor(R.color.myddarkgray));
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		return ChartFactory.getBarChartView(context, buildBarDataset(titles, values), renderer, Type.DEFAULT);

	}

}
