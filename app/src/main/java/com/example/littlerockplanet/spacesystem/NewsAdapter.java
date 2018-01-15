package com.example.littlerockplanet.spacesystem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder>{
    private Context mCtx;
    private ArrayList<ArticleObject> articles;

    public NewsAdapter(Context context, ArrayList<ArticleObject> articles){
        this.mCtx = context;
        this.articles = articles;
    }

    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.news_card, null);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        ArticleObject articleToBind = articles.get(position);

        holder.headline.setText(articleToBind.headline);
        holder.description.setText(articleToBind.description);
        holder.date.setText(articleToBind.datePublished);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView headline;
        TextView description;
        TextView date;

        public NewsViewHolder(View itemView) {
            super(itemView);
            headline = itemView.findViewById(R.id.articleTitle);
            description = itemView.findViewById(R.id.articleInformation);
            date = itemView.findViewById(R.id.articleDate);
            itemView.findViewById(R.id.articleContainer).setOnClickListener(this);
        }

        //WHENEVER AN ARTICLE IS CLICKED, LAUNCH IT IN BROWSER
        @Override
        public void onClick(View view) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(articles.get(getAdapterPosition()).link));
            mCtx.startActivity(i);
        }
    }
}