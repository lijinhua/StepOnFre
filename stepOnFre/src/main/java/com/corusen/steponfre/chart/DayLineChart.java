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
import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.chart.ChartActivity.DemoObjectFragment;

import android.content.Context;
import android.graphics.Paint.Align;
import android.view.Display;
import android.view.WindowManager;

/**
 * Average temperature demo chart.
 */
public class DayLineChart extends AbstractDemoChart {
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

	public GraphicalView graphicalView(DemoObjectFragment demoObjectFragment, List<double[]> x, List<double[]> values, boolean screenLarge) {
		String[] titles = new String[] { demoObjectFragment.getString(R.string.daily_steps) + ":  ", demoObjectFragment.getString(R.string.daily_goal) + ":  ", }; // WL
		int[] colors = new int[] { demoObjectFragment.getResources().getColor(AccuService.mScreenChartDayLineRender1),
				demoObjectFragment.getResources().getColor(AccuService.mScreenChartDayLineRender2) };
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE, PointStyle.POINT };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles, screenLarge);

		WindowManager wm = (WindowManager) demoObjectFragment.getActivity().getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		// Point size = new Point();
		// display.getSize(size);
		// int width = size.x;
		// int height = size.y;

		int width = display.getWidth();
		int height = display.getHeight();

		if (width >= 1080) {
			// for 5.0" S4
			renderer.setAxisTitleTextSize(40);
			renderer.setChartTitleTextSize(44);
			renderer.setLabelsTextSize(36);
			renderer.setLegendTextSize(44); // WL change
			renderer.setPointSize(13f);
			renderer.setChartValuesTextSize(36); // text size on the bar
			renderer.setLegendHeight(125); // 1/27/13
			renderer.setMargins(new int[] { 100, 110, 100, 40 }); // WL change
		} else if (width >= 720) {
			// for 4.8" S3
			renderer.setAxisTitleTextSize(30);
			renderer.setChartTitleTextSize(33);
			renderer.setLabelsTextSize(27);
			renderer.setLegendTextSize(33); // WL change
			renderer.setPointSize(10f);
			renderer.setChartValuesTextSize(27);
			renderer.setLegendHeight(100); // 1/27/13
			renderer.setMargins(new int[] { 75, 78, 75, 30 }); // WL change
		} else if (width >= 480) {
			// for 4" screen
			renderer.setAxisTitleTextSize(20);
			renderer.setChartTitleTextSize(22);
			renderer.setLabelsTextSize(18);
			renderer.setLegendTextSize(22); // WL change
			renderer.setPointSize(7f);
			renderer.setChartValuesTextSize(18);
			renderer.setLegendHeight(70); // 1/27/13
			renderer.setMargins(new int[] { 50, 55, 50, 20 }); // top, left,
																// bottom,
																// right
		} else if (width >= 320) {
			// 3.2" screen
			renderer.setAxisTitleTextSize(12); // For smaller screen Aug. 31
			renderer.setChartTitleTextSize(16);
			renderer.setLabelsTextSize(14);
			renderer.setLegendTextSize(16);
			renderer.setPointSize(5f);
			renderer.setChartValuesTextSize(14);
			renderer.setLegendHeight(50); // 1/27/13
			renderer.setMargins(new int[] { 30, 40, 25, 10 });
		} else {
			// small screen
			renderer.setAxisTitleTextSize(10); // For smaller screen Aug. 31
			renderer.setChartTitleTextSize(12);
			renderer.setLabelsTextSize(10);
			renderer.setLegendTextSize(10);
			renderer.setPointSize(3f);
			renderer.setChartValuesTextSize(8);
			renderer.setLegendHeight(40); // 1/27/13
			renderer.setMargins(new int[] { 20, 30, 10, 8 });
		}

		int length = renderer.getSeriesRendererCount();
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
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

		titles[0] = titles[0] + ((Integer) (int) maxToday).toString() + ", " + ((Integer) (int) (maxToday / goalStep * 100)).toString() + "%";
		titles[1] = titles[1] + ((Integer) (int) goalStep).toString();

		double maxY;
		int q = (int) (maxStep / 2000);
		maxY = (double) (q + 1) * 2000.0;

		if (maxY < 12000) {
			maxY = 12000;
		}
		renderer.setYLabels(1); // WL change
		renderer.setXLabels(12);

		if (maxY <= 12000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(2000, "2K ");
			renderer.addYTextLabel(4000, "4K ");
			renderer.addYTextLabel(6000, "6K ");
			renderer.addYTextLabel(8000, "8K ");
			renderer.addYTextLabel(10000, "10K ");
			renderer.addYTextLabel(12000, "12K ");
		} else if (maxY <= 16000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(2000, "2K ");
			renderer.addYTextLabel(4000, "4K ");
			renderer.addYTextLabel(6000, "6K ");
			renderer.addYTextLabel(8000, "8K ");
			renderer.addYTextLabel(10000, "10K ");
			renderer.addYTextLabel(12000, "12K ");
			renderer.addYTextLabel(14000, "14K ");
			renderer.addYTextLabel(16000, "16K ");
		} else if (maxY <= 20000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(2000, "2K ");
			renderer.addYTextLabel(4000, "4K ");
			renderer.addYTextLabel(6000, "6K ");
			renderer.addYTextLabel(8000, "8K ");
			renderer.addYTextLabel(10000, "10K ");
			renderer.addYTextLabel(12000, "12K ");
			renderer.addYTextLabel(14000, "14K ");
			renderer.addYTextLabel(16000, "16K ");
			renderer.addYTextLabel(18000, "18K ");
			renderer.addYTextLabel(20000, "20K ");
		} else if (maxY <= 30000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(5000, "5K ");
			renderer.addYTextLabel(10000, "10K ");
			renderer.addYTextLabel(15000, "15K ");
			renderer.addYTextLabel(20000, "20K ");
			renderer.addYTextLabel(25000, "25K ");
			renderer.addYTextLabel(30000, "30K ");
		} else if (maxY <= 50000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(10000, "10K ");
			renderer.addYTextLabel(20000, "20K ");
			renderer.addYTextLabel(30000, "30K ");
			renderer.addYTextLabel(40000, "40K ");
			renderer.addYTextLabel(50000, "50K ");
		} else {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(20000, "20K ");
			renderer.addYTextLabel(40000, "40K ");
			renderer.addYTextLabel(60000, "60K ");
			renderer.addYTextLabel(80000, "80K ");
			renderer.addYTextLabel(100000, "100K");
			renderer.addYTextLabel(120000, "120K");
		}

		setChartSettings(renderer, demoObjectFragment.getString(R.string.hourly_step_counts), demoObjectFragment.getString(R.string.hour), "", 0.0, 24.0, 0,
				maxY, AccuService.mScreenChartDayLineSetting, AccuService.mScreenChartDayLineSetting);

		renderer.setShowGrid(true);
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		renderer.setXLabelsColor(AccuService.mScreenChartDayLineSetting); // WL 1/23/12
		renderer.setYLabelsColor(0, AccuService.mScreenChartDayLineSetting); // WL 1/23/12
		renderer.setPanLimits(new double[] { 0.0, 24.0, 0, 30 });
		renderer.setZoomLimits(new double[] { 0.0, 24.0, 0, 30 });

		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(demoObjectFragment.getResources().getColor(AccuService.mScreenChartBackground));
		renderer.setMarginsColor(demoObjectFragment.getResources().getColor(AccuService.mScreenChartDayLineRenderMargin));

		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);

		for (int i = 0; i < length; i++) {
			XYSeriesRenderer seriesRenderer = (XYSeriesRenderer) renderer.getSeriesRendererAt(i);
			seriesRenderer.setFillBelowLine(i == length - 2);
			seriesRenderer.setFillBelowLineColor(demoObjectFragment.getResources().getColor(AccuService.mScreenChartDayLineRender3));
			seriesRenderer.setLineWidth(3.5f);
		}

		GraphicalView mChartView = ChartFactory.getLineChartView(demoObjectFragment.getActivity().getBaseContext(), buildDataset(titles, x, values), renderer);
		return mChartView;
	}

}
