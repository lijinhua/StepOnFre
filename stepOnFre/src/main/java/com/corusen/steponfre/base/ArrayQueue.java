package com.corusen.steponfre.base;

public class ArrayQueue {

	protected float[][] array;
	protected int start, end;
	protected boolean full;

	final int numColumn = 7;
	//final int numRow = 1001; //add 1 more for 1000
	final int numRow = 10001; //add 1 more for 1000

    private static ArrayQueue instance = null;

    public static ArrayQueue getInstance() {
        if (instance == null) {
            instance = new ArrayQueue();
        }
        return instance;
    }
	
	private ArrayQueue() {
		array = new float[numRow][numColumn];
		start = end = 0;
		full = false;
	}

	public boolean isEmpty() {
		return ((start == end) && !full);
	}

//	public void insert(float time, float[] a, float[] b, float c) {
//		if (!full) {
//			start = ++start % array.length;
//			array[start][0] = time;
//			array[start][1] = a[0];
//			array[start][2] = a[1];
//			array[start][3] = a[2];
//			
//			array[start][4] = b[0];
//			array[start][5] = b[1];
//			array[start][6] = b[2];
//			
//			array[start][7] = c;
//		}
//		if (start == end) {
//			// full = true;
//			end = ++end % array.length;
//		}
//	}
//	
	public void insert(float time, float[] a, float steps, float steptime, float axis) {
		if (!full) {
			start = ++start % array.length;
			array[start][0] = time;
			array[start][1] = a[0];
			array[start][2] = a[1];
			array[start][3] = a[2];
			array[start][4] = steps;
			array[start][5] = steptime;
			array[start][6] = axis;
		}
		if (start == end) {
			// full = true;
			end = ++end % array.length;
		}
	}

	public float[] remove() {
		if (full) {
			full = false;
		} else if (isEmpty()) {
			return null;
		}
		return array[end = (++end % array.length)];
	}

	public void reset() {
		start = end = 0;
		full = false;
	}
}
