package com.jackpf.blockchainsearch.Service;

import java.util.Locale;

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
	 * @return String
	 */
	public static String btcFormat(Long i)
	{
		return String.format(Locale.getDefault(), "%s%.8f", "\u0E3F", i.doubleValue() / BlockchainData.CURRENCY_MULTIPLIER);
	}
}
