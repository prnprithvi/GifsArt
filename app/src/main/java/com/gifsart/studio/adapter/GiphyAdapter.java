package com.gifsart.studio.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.gifsart.studio.R;
import com.gifsart.studio.gifutils.GiphyApiRequest;
import com.gifsart.studio.item.GiphyItem;
import com.gifsart.studio.utils.GifsArtConst;
import com.gifsart.studio.utils.GlideLoader;
import com.gifsart.studio.utils.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Tigran on 9/8/15.
 */
public class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.ViewHolder> {

    private ArrayList<GiphyItem> giphyItems = new ArrayList<>();
    private ArrayList<Integer> colors = new ArrayList<>();
    private LayoutInflater inflater = null;
    private Context context;
    private int limit = GifsArtConst.GIPHY_LIMIT_COUNT;
    private int offset = 0;
    private String tag;
    private int selectedPosition = -1;
    private boolean isSticker = false;
    private Random random = new Random();

    GlideLoader glideLoader;

    public GiphyAdapter(ArrayList<GiphyItem> giphyItems, String tag, boolean isSticker, Context context) {
        this.giphyItems = giphyItems;
        this.tag = tag;
        this.isSticker = isSticker;
        this.context = context;
        glideLoader = new GlideLoader(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = LayoutInflater.from(parent.getContext());
        }
        colors.add(ContextCompat.getColor(context, R.color.blue));
        colors.add(ContextCompat.getColor(context, R.color.pink));
        colors.add(ContextCompat.getColor(context, R.color.yellow));
        colors.add(ContextCompat.getColor(context, R.color.green));
        colors.add(ContextCompat.getColor(context, R.color.orange));
        return new ViewHolder(inflater.inflate(R.layout.giphy_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.giphyImageView.setBackgroundColor(colors.get(random.nextInt(colors.size())));
        //Uri uri = Uri.parse(giphyItems.get(position).getDownsampledGifUrl());
        glideLoader.loadWithParamsAsGifDrawable(giphyItems.get(position).getDownsampledGifUrl(), holder.giphyImageView, RequestOptions.noTransform(), null);
        //Glide.with(context).load(uri).asGif().override(200, 200).centerCrop().into(holder.giphyImageView);
        // Giphy paging duaring giphy activity scralling
        if (position + 1 == limit + offset) {
            if (Utils.haveNetworkConnection(context)) {
                offset = offset + limit;

                GiphyApiRequest giphyApiRequest = new GiphyApiRequest(context, tag, isSticker, offset, limit);
                giphyApiRequest.requestGiphy();
                giphyApiRequest.setOnDownloadedListener(new GiphyApiRequest.GiphyListener() {
                    @Override
                    public void onGiphyDownloadFinished(ArrayList<GiphyItem> items) {
                        giphyItems.addAll(items);
                        notifyDataSetChanged();
                    }
                });
            } else {
                Toast.makeText(context, context.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
            }
        }

        if (giphyItems.get(position).isSelected() == true) {
            holder.corner.setVisibility(View.VISIBLE);
        } else {
            holder.corner.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return giphyItems.size();
    }

    public void addItem(GiphyItem item) {
        giphyItems.add(item);
        notifyDataSetChanged();
    }

    public void addItems(ArrayList<GiphyItem> giphyItems) {
        this.giphyItems.addAll(giphyItems);
        notifyDataSetChanged();
    }

    public void clear() {
        limit = GifsArtConst.GIPHY_LIMIT_COUNT;
        offset = 0;
        giphyItems.clear();
        notifyDataSetChanged();
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public GiphyItem getItem(int position) {
        return giphyItems.get(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView giphyImageView = null;
        public ImageView corner;

        public ViewHolder(View itemView) {
            super(itemView);
            giphyImageView = (ImageView) itemView.findViewById(R.id.giphy_image_view);
            corner = (ImageView) itemView.findViewById(R.id.giphy_corner);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(context.getResources().getDisplayMetrics().widthPixels / 2, context.getResources().getDisplayMetrics().widthPixels / 2);
            giphyImageView.setLayoutParams(layoutParams);
            corner.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, context.getResources().getDisplayMetrics().widthPixels / 2));
            corner.setVisibility(View.GONE);
        }
    }

}
