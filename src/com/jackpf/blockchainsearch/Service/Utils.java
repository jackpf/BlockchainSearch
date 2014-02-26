package com.jackpf.blockchainsearch.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Lib.AddressFormatException;
import com.jackpf.blockchainsearch.Lib.Base58;

/**
 * Handy global utils class
 */
public class Utils
{
    public static ProcessedTransaction processTransaction(String address, JSONObject tx)
    {
        Long total = 0L;
        String addrIn = "", addrOut = "";
        
        for (Object _in : (JSONArray) tx.get("inputs")) {
            JSONObject in = (JSONObject) _in;
            JSONObject prev = (JSONObject) in.get("prev_out");
            
            if (address.equals((String) prev.get("addr"))) {
                total -= Long.parseLong(prev.get("value").toString());
            } else {
                addrOut = prev.get("addr").toString();
            }
        }
        
        for (Object _out : (JSONArray) tx.get("out")) {
            JSONObject out = (JSONObject) _out;
            
            if (address.equals((String) out.get("addr"))) {
                total += Long.parseLong(out.get("value").toString());
            } else {
                addrIn = out.get("addr").toString();
            }
        }
        
        return new ProcessedTransaction(total > 0L ? addrOut : addrIn, total);
    }
    public static class ProcessedTransaction
    {
        private String address;
        private Long amount;
        
        public ProcessedTransaction(String address, Long amount)
        {
            this.address = address;
            this.amount = amount;
        }
        
        public String getAddress()
        {
            return address;
        }
        
        public Long getAmount()
        {
            return amount;
        }
    }
    
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
	
	/**
	 * Validate a bitcoin address
	 * 
	 * @param address
	 * @return
	 */
	public static boolean validAddress(String address)
	{
		byte[] decoded = new byte[]{};
		
		try {
			decoded = Base58.decode(address);
		} catch (AddressFormatException e) {
			return false;
		}
		
		if (decoded.length != 25) {
			return false;
		}

		byte[] digest = Arrays.copyOfRange(decoded, 0, 21);
		byte[] checksum = Arrays.copyOfRange(decoded, 21, 25);
		
		MessageDigest sha256 = null;
		try {
			sha256 = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) { /* Just let it fly, we're pretty f'ked here */ }
		byte[] hash = sha256.digest(sha256.digest(digest));
		
		for (int i = 0; i < checksum.length; i++) {
			if (checksum[i] != hash[i]) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Validate a transaction hash
	 * 
	 * @param hash
	 * @return
	 */
	public static boolean validTransaction(String hash)
	{
		return hash.getBytes().length == 64;
	}
}
