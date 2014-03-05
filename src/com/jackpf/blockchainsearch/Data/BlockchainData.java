package com.jackpf.blockchainsearch.Data;

public class BlockchainData
{
    public static final String
        BLOCKCHAIN_URL          = "https://blockchain.info",
        ADDRESS_URL             = "address/%s?format=json&offset=%d",
        TRANSACTION_URL         = "tx/%s?format=json",
        WS_URL                  = "ws://ws.blockchain.info/inv",
        Q_BLOCKCOUNT_URL        = "q/getblockcount",
        Q_DIFFICULTY_URL        = "q/getdifficulty",
        Q_TOTAL_BTC_URL         = "q/totalbc",
        Q_EXCHANGE_RATE_URL     = "ticker",
        Q_NEXT_BLOCK_TIME_URL   = "q/eta"
    ;
    
    public static final int TX_PER_PAGE = 50;
    
    public static final Long[] CONVERSIONS = {
        100000000L,
        100000L,
        100L,
        1L
    };
    public static final String[] FORMATS = {
        "\u0E3F%.8f",
        "\u006D\u0E3F%.0f",
        "\u03BC\u0E3F%.0f",
        "\u0024%.0f",
    };
}