package com.jackpf.blockchainsearch.Data;

public class BlockchainData
{
    public static final String
        BLOCKCHAIN_URL  = "https://blockchain.info",
        ADDRESS_URL     = "address/%s?format=json",
        TRANSACTION_URL = "tx/%s?format=json",
        BLOCKCOUNT_URL  = "q/getblockcount",
        WS_URL          = "ws://ws.blockchain.info/inv"
    ;
    
    public static final Long[] CONVERSIONS = {
        100000000L,
        1L
    };
    public static final String[] FORMATS = {
        "\u0E3F%.8f",
        "\u0024%.0f",
    };
}