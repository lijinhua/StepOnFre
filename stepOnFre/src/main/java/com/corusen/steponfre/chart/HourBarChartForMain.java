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

import android.content.Context;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

import com.corusen.steponfre.base.AccuService;
import com.corusen.steponfre.base.Pedometer.ObjectFragment;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer.Orientation;

import java.util.List;

/**
 * Average temperature demo chart.
 */
public class HourBarChartForMain extends AbstractDemoChartForMain {
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

	public GraphicalView graphicalView(ObjectFragment demoObjectFragment,
			List<double[]> xx, List<double[]> values, boolean screenLarge) {
		String[] titles = new String[] { "" }; // WL change

		int[] colors = new int[] { demoObjectFragment.getResources().getColor(
				AccuService.mScreenChartDayLineRender1) };

		double maxStep = 0.0;
		//int totalSteps = 0;
		//int aveSteps = 0;
		//int count = 0;

		if (values.size() > 0) {
			double[] dSteps = values.get(0); //(double[]) values.get(0);
			for (double dStep : dSteps) {
				if (dStep > maxStep) { maxStep = dStep; }
				//if (dStep >= 1.0) { count++; }
				//totalSteps = totalSteps + (int) dStep;
			}
		} else {
			maxStep = 0;
		}


//		if (count == 0) {
//			aveSteps = 0;
//		} else {
//			aveSteps = (int) (totalSteps / count);
//		}
		// titles[0] = titles[0] + ((Integer) totalSteps).toString() + ", "
		// + context.getString(R.string.ave) + ": "
		// + ((Integer) aveSteps).toString();

		double maxY;
		int q = (int) (maxStep / 2000);
		maxY = (double) (q + 1) * 2000.0;
		if (maxY < 2000) {
			maxY = 2000;
		}

		XYMultipleSeriesRenderer renderer = buildBarRenderer(colors,
				screenLarge);
		renderer.setOrientation(Orientation.HORIZONTAL);

		WindowManager wm = (WindowManager) demoObjectFragment.getActivity()
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        //int height = size.y;

//		int width = display.getWidth();
//		int height = display.getHeight();

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
		//
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
		// renderer.setMargins(new int[] { 15, 30, 10, 15 }); // WL change top,
		// left, bottom, right
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

		if (width >= 1440) {
			// for 5.7" Note 4
			renderer.setAxisTitleTextSize(50);
			renderer.setChartTitleTextSize(60);
			renderer.setLabelsTextSize(50);
			renderer.setLegendTextSize(55); // WL change
			renderer.setPointSize(16f);
			renderer.setMargins(new int[] { 45, 90, 0, 35 }); // JS WL change
			renderer.setShowLegend(false);
		} else if (width >= 1080) {
			// for 5.0" S4
			renderer.setAxisTitleTextSize(40);
			renderer.setChartTitleTextSize(44);
			renderer.setLabelsTextSize(36);
			renderer.setLegendTextSize(44); // WL change
			renderer.setPointSize(13f);
			renderer.setMargins(new int[] { 35, 70, 0, 25 }); // JS WL change
			renderer.setShowLegend(false);
		} else if (width >= 720) {
			// for 4.8" S3
			renderer.setAxisTitleTextSize(30);
			renderer.setChartTitleTextSize(33);
			renderer.setLabelsTextSize(27);
			renderer.setLegendTextSize(33); // WL change
			renderer.setPointSize(10f);
			renderer.setMargins(new int[] { 25, 50, 0, 20 }); // JS WL change
			renderer.setShowLegend(false);

		} else if (width >= 480) {
			// for 4" screen
			/*
			 * renderer.setAxisTitleTextSize(20);
			 * renderer.setChartTitleTextSize(22);
			 * renderer.setLabelsTextSize(18); renderer.setLegendTextSize(22);
			 * // WL change renderer.setPointSize(7f); renderer.setMargins(new
			 * int[] { 20, 50, 50, 10 }); // WL change
			 */
			renderer.setAxisTitleTextSize(20);
			renderer.setChartTitleTextSize(22);
			renderer.setLabelsTextSize(18);
			renderer.setLegendTextSize(22); // WL change
			renderer.setPointSize(7f);
			renderer.setMargins(new int[] { 15, 30, 10, 15 }); // WL change top,
																// left, bottom,
																// right
			// renderer.setShowLegend(false);
		} else {
			// small screen
			renderer.setAxisTitleTextSize(11); // For smaller screen Aug. 31
			renderer.setChartTitleTextSize(12);
			renderer.setLabelsTextSize(11);
			renderer.setLegendTextSize(11);
			renderer.setMargins(new int[] { 10, 25, 8, 10 });
			// renderer.setShowLegend(false);
		}

		setChartSettings(renderer, "", "", "", 0.0, 24.0, 0, maxY, AccuService.mScreenChartDayLineSetting,
				AccuService.mScreenChartDayLineSetting);
		renderer.setXLabels(1);
		renderer.setYLabels(1); // WL change
		renderer.addXTextLabel(0, "0");
		renderer.addXTextLabel(4, "4");
		renderer.addXTextLabel(8, "8");
		renderer.addXTextLabel(12, "12");
		renderer.addXTextLabel(16, "16");
		renderer.addXTextLabel(20, "20");
		renderer.addXTextLabel(24, "24");

		if (maxY <= 2000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(1000, "1K ");
			renderer.addYTextLabel(2000, "2K ");
		} else if (maxStep <= 4000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(2000, "2K ");
			renderer.addYTextLabel(4000, "4K ");
		} else if (maxY <= 6000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(3000, "3K ");
			renderer.addYTextLabel(6000, "6K ");
		} else if (maxY <= 8000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(4000, "4K ");
			renderer.addYTextLabel(8000, "8K ");
		} else if (maxY <= 10000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(5000, "5K ");
			renderer.addYTextLabel(10000, "10K ");
		} else if (maxY <= 12000.0) {
			renderer.addYTextLabel(0, "");
			renderer.addYTextLabel(6000, "6K ");
			renderer.addYTextLabel(12000, "12K ");
		} else if (maxY <= 14000.0){
			renderer.addYTextLabel(7000, "7K ");
			renderer.addYTextLabel(14000, "14K ");
		} else if (maxY <= 16000.0){
            renderer.addYTextLabel(8000, "8K ");
            renderer.addYTextLabel(16000, "16K ");
        } else if (maxY <= 18000.0){
            renderer.addYTextLabel(9000, "9K ");
            renderer.addYTextLabel(18000, "18K ");
        } else if (maxY <= 20000.0){
            renderer.addYTextLabel(10000, "10K ");
            renderer.addYTextLabel(20000, "20K ");
        } else if (maxY <= 30000.0){
            renderer.addYTextLabel(15000, "15K ");
            renderer.addYTextLabel(30000, "30K ");
        } else if (maxY <= 40000.0){
            renderer.addYTextLabel(20000, "20K ");
            renderer.addYTextLabel(40000, "40K ");
        } else if (maxY <= 50000.0){
            renderer.addYTextLabel(25000, "25K ");
            renderer.addYTextLabel(50000, "50K ");
        } else if (maxY <= 60000.0){
            renderer.addYTextLabel(30000, "30K ");
            renderer.addYTextLabel(60000, "60K ");
        } else if (maxY <= 70000.0){
            renderer.addYTextLabel(35000, "35K ");
            renderer.addYTextLabel(70000, "70K ");
        } else if (maxY <= 80000.0){
            renderer.addYTextLabel(40000, "40K ");
            renderer.addYTextLabel(80000, "80K ");
        } else {
            renderer.addYTextLabel(45000, "45K ");
            renderer.addYTextLabel(90000, "90K ");
        }

		renderer.setBarSpacing(0.2f); // WL 1/23/12
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT, 0);
		renderer.setXLabelsColor(AccuService.mScreenChartDayLineSetting); // WL 1/23/12
		renderer.setYLabelsColor(0, AccuService.mScreenChartDayLineSetting); // WL 1/23/12

		renderer.setShowGrid(false);

		renderer.setDisplayChartValues(false);
		// SimpleSeriesRenderer.setDisplayChartValues(false);

		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(demoObjectFragment.getResources().getColor(AccuService.mScreenChartBackground));

		renderer.setMarginsColor(demoObjectFragment.getResources().getColor(
				AccuService.mScreenChartDayLineRenderMargin));

		renderer.setPanEnabled(false, false);
		renderer.setZoomEnabled(false, false);
		return ChartFactory.getBarChartView(demoObjectFragment.getActivity(),
				buildBarDataset(titles, values), renderer, Type.DEFAULT);

	}

}
