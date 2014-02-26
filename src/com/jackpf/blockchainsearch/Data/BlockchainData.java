package com.jackpf.blockchainsearch.Data;

public class BlockchainData
{
	public static final String
		BLOCKCHAIN_URL	= "https://blockchain.info",
		ADDRESS_URL		= "address/%s?format=json",
		TRANSACTION_URL	= "tx/%s?format=json",
		BLOCKCOUNT_URL	= "q/getblockcount",
		WS_URL          = "ws://ws.blockchain.info/inv"
	;
	
	public static final Long
		CURRENCY_MULTIPLIER	= 100000000L
	;
}