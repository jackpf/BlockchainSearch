package com.jackpf.blockchainsearch.Service;

import java.util.Locale;

import com.jackpf.blockchainsearch.Data.BlockchainData;

public class Utils
{
	public static String btcFormat(Long i)
	{
		return String.format(Locale.getDefault(), "%s%.8f", "\u0E3F", i.doubleValue() / BlockchainData.CURRENCY_MULTIPLIER);
	}
}
