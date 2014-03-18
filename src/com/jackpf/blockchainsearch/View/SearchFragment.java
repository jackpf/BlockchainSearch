package com.jackpf.blockchainsearch.View;

import java.io.IOException;
import java.util.Date;

import org.ocpsoft.prettytime.PrettyTime;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.BtcStats;

public class SearchFragment extends SherlockFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout._main_search, container, false);
        
        BtcStats stats = BtcStats.getInstance();
        stats.update(new BtcStats.UpdateListener() {
           @Override
           public void update(BtcStats stats, IOException e) {
               if (e != null) {
                   return;
               }
               
               Long m = Math.abs(stats.getNextBlockTime()) / 60;
               String[] values = {
                   Long.toString(stats.getBlockCount()),
                   stats.getNextBlockTime() >= 0 ? "in about " + m + " minute" + (m != 1 ? "s" : "") : "about " + m + " minute" + (m != 1 ? "s" : "") + " ago",
                   Long.toString(stats.getDifficulty()),
                   Long.toString(stats.getTotalBitcoins() / BlockchainData.CONVERSIONS[0])
               };
               int[] holders = {
                   R.id.stats_block_height,
                   R.id.stats_next_block_time,
                   R.id.stats_difficulty,
                   R.id.stats_total_bitcoins
               };
               int[] loaders = {
                   R.id.stats_block_height_loader,
                   R.id.stats_next_block_time_loader,
                   R.id.stats_difficulty_loader,
                   R.id.stats_total_bitcoins_loader
               };
               
               for (int i = 0; i < values.length; i++) {
                   TextView tv = (TextView) rootView.findViewById(holders[i]);
                   ProgressBar pb = (ProgressBar) rootView.findViewById(loaders[i]);

                   pb.setVisibility(View.GONE);
                   tv.setText(values[i]);
                   tv.setVisibility(View.VISIBLE);
               }
               
               ((TextView) rootView.findViewById(R.id.stats_updated_at)).setText("Updated " + new PrettyTime().format(new Date(stats.getUpdatedAt().getTime())));
           }
        });
        
        return rootView;
    }
}
