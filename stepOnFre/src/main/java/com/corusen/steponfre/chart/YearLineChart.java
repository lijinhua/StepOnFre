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
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.util.MathHelper;

import com.corusen.steponfre.R;
import com.corusen.steponfre.chart.ChartActivity.DemoObjectFragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.Display;
import android.view.WindowManager;

/**
 * Sales demo bar chart.
 */
public class YearLineChart extends AbstractDemoChart {

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Sales horizontal bar chart";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The monthly sales for the last 2 years (horizontal bar chart)";
	}

	public GraphicalView graphicalView(DemoObjectFragment demoObjectFragment, List<double[]> x, List<double[]> values,
			boolean screenLarge) {
		String[] titles = new String[] {
				demoObjectFragment.getString(R.string.daily_steps) + ":  ",
				demoObjectFragment.getString(R.string.daily_goal) + ":  ", }; // WL
		int[] colors = new int[] {
				demoObjectFragment.getResources().getColor(R.color.myorange),
				demoObjectFragment.getResources().getColor(R.color.myblue) };
		
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE,
				PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles,
				screenLarge);

		WindowManager wm = (WindowManager) demoObjectFragment.getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
//		Point size = new Point();
//		display.getSize(size);
//		int width = size.x;
//		int height = size.y;

		int width = display.getWidth();
		int height = display.getHeight();

		if (screenLarge) {
			if ((width >= 720) && (height >= 1280)) {
				// for 4.8" S3
				renderer.setAxisTitleTextSize(30);
				renderer.setChartTitleTextSize(33);
				renderer.setLabelsTextSize(27);
				renderer.setLegendTextSize(33); // WL change
				renderer.setPointSize(10f);
				renderer.setLegendHeight(100);	//1/27/13 
				renderer.setMargins(new int[] { 75, 75, 75, 30}); // WL change

			} else {
				// for 4" screen
				renderer.setAxisTitleTextSize(20);
				renderer.setChartTitleTextSize(22);
				renderer.setLabelsTextSize(18);
				renderer.setLegendTextSize(22); // WL change
				renderer.setPointSize(7f);
				renderer.setLegendHeight(70);	//1/27/13 
				renderer.setMargins(new int[] { 50, 50, 50, 20 }); // top, left, bottom, right
			}
		} else {
			// small screen
			renderer.setAxisTitleTextSize(11); // For smaller screen Aug. 31
			renderer.setChartTitleTextSize(12);
			renderer.setLabelsTextSize(11);
			renderer.setLegendTextSize(11);
			renderer.setPointSize(5f);
			renderer.setLegendHeight(40);	//1/27/13 
			renderer.setMargins(new int[] { 30, 30, 30, 10 });
		}

		
		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}

		double maxStep = 0.0;
		double maxToday = 0.0;
		double goalStep = ((double[]) values.get(1))[0];
		double[] dSteps = (double[]) values.get(0);
		for (int i = 0; i < dSteps.length; i++) {
			if (dSteps[i] < MathHelper.NULL_VALUE) {
				if (dSteps[i] > maxToday) {
					maxToday = dSteps[i];
				}
			}
		}
		maxStep = maxToday;

		if (goalStep > maxStep) {
			maxStep = goalStep;
		}

		titles[0] = titles[0] + ((Integer) (int) maxToday).toString() + ", "
				+ ((Integer) (int) (maxToday / goalStep * 100)).toString()
				+ "%";
		titles[1] = titles[1] + ((Integer) (int) goalStep).toString();

		double maxY;
		int q = (int) (maxStep / 2000);
		maxY = (double) (q + 1) * 2000.0;
		if (maxY < 12000) {
			maxY = 12000;
		}

		setChartSettings(renderer,
				demoObjectFragment.getString(R.string.hourly_step_counts),
				demoObjectFragment.getString(R.string.hour), "", 0.0, 365.0, 0, maxY,
				Color.WHITE, Color.WHITE);
		renderer.setXLabels(12);
		renderer.setYLabels(6);
		renderer.setXLabelsColor(Color.WHITE);	//WL 1/23/12
		renderer.setYLabelsColor(0, Color.WHITE);	//WL 1/23/12

		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setPanLimits(new double[] { 0.0, 365.0, 0, 30 });
		renderer.setZoomLimits(new double[] { 0.0, 365.0, 0, 30 });

		renderer.setMarginsColor(Color.DKGRAY);
		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);

		for (int i = 0; i < length; i++) {
			XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer
					.getSeriesRendererAt(i);
			seriesRenderer.setFillBelowLine(i == length - 2);
			seriesRenderer.setFillBelowLineColor(demoObjectFragment.getResources()
					.getColor(R.color.myllightorange));
			seriesRenderer.setLineWidth(3.5f);
		}

		GraphicalView mChartView = ChartFactory.getLineChartView(demoObjectFragment.getActivity().getBaseContext(),
				buildDataset(titles, x, values), renderer);
		return mChartView;
	}
}
