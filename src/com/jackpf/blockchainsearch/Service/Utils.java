package com.jackpf.blockchainsearch.Service;

import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;

import com.jackpf.blockchainsearch.Data.BlockchainData;

/**
 * Handy global utils class
 */
public class Utils
{
	/**
	 * Format given value into a bitcoin currency value
	 * 
	 * @param i
	 * @return
	 */
	public static String btcFormat(Long i)
	{
		return String.format(Locale.getDefault(), "%s%.8f", "\u0E3F", i.doubleValue() / BlockchainData.CURRENCY_MULTIPLIER);
	}
	
	/**
	 * Draw a confirmation counter
	 * 
	 * @param confirmations
	 * @param targetConfirmations
	 * @param color1
	 * @param color2
	 * @param size
	 * @return
	 */
	public static Bitmap drawConfirmationsArc(int confirmations, int targetConfirmations, int color1, int color2, int size)
	{
		Bitmap image = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(image);
		canvas.drawColor(0, Mode.CLEAR);
		
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth(1);

		final RectF oval = new RectF();
		oval.set(0, 0, size, size);
		
		float f = (float) confirmations / (float) targetConfirmations;
		f = f > 1f ? 1f : f;
		int deg = Math.round(f * 360f);
		int deg_r = 360 - deg;

		paint.setColor(color1);
		canvas.drawArc(oval, -90, deg, true, paint);
		
		paint.setColor(color2);
		canvas.drawArc(oval, -90 + deg, deg_r, true, paint);
		
		return image;
	}
}
