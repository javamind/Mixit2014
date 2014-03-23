/*
 * Copyright 2014 Guillaume EHRET
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ehret.mixit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.ehret.mixit.R;
import com.ehret.mixit.domain.twitter.RechercheTweet;
import com.ehret.mixit.domain.twitter.Tweet;
import com.ehret.mixit.adapter.ListTweetAdapter;
import com.ehret.mixit.utils.UIUtils;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Ce fragment permet d'afficher les tweets contenant le mot clé mix-it
 */
public class TwitterFragment extends Fragment {
    public static final String TAG = "TwitterFragment";
    private ViewGroup mRootView;
    private LayoutInflater mInflater;
    private ListView mTweetView;
    private TextView searchTweet;
    private AtomicBoolean isLoaded;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mInflater = inflater;
        mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_list_tweets, container);

        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DownloadTweetsTask mTweetReaderRunnable = new DownloadTweetsTask(this);
        //mTweetReaderRunnable.execute("http://search.twitter.com/search.json?q=mixIT_lyon+OR+mixit13+OR+from%23AmixIT_lyon+OR+to%3AmixIT_lyon&src=typd");
        mTweetReaderRunnable.execute("http://search.twitter.com/search.json?q=mixIT_lyon+OR+mixit14&src=typd");
    }

    /**
     * Cette classe se charge de charger les tweets dans un thread a part
     */
    private class DownloadTweetsTask extends AsyncTask<String, Integer, Void> {
        private final static String TAG = "DownloadTweetsTask";
        private JsonFactory jsonFactory;
        private ObjectMapper objectMapper;
        private List<Tweet> tweetToDisplay;
        private TwitterFragment fragment;

        public DownloadTweetsTask(TwitterFragment fragment) {
            this.jsonFactory = new JsonFactory();
            this.objectMapper = new ObjectMapper();
            this.tweetToDisplay = new ArrayList<Tweet>();
            this.fragment = fragment;
        }

        @Override
        protected Void doInBackground(String... urls) {
            this.tweetToDisplay.clear();
            //On ne fait la recup que si on a du reseau
            if (UIUtils.isNetworkAvailable(mRootView.getContext())) {
                for (String adr : urls) {
                    HttpURLConnection urlConnection = null;
                    try {
                        URL url = new URL(adr);
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.setDoOutput(true);
                        urlConnection.connect();
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        //Parse JSON et ajout des tweets lus
                        tweetToDisplay.addAll(parseJson(urlConnection.getInputStream()));
                    } catch (IOException e) {
                        Log.e(TAG, "Impossible de récupérer les données de twitter", e);
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                    }

                    // Escape early if cancel() is called
                    if (isCancelled()) break;
                }
                if(fragment!=null && fragment.getActivity()!=null){
                    //On met à jour l'écran. Pour celà on doit se raccrocher avec le thread dédie à l'UI
                    if (searchTweet == null) {
                        searchTweet = (TextView) fragment.getActivity().findViewById(R.id.searchTweet);

                    }
                    if (progress == null) {
                        progress = (ProgressBar) fragment.getActivity().findViewById(R.id.tweetprogressbar);
                    }

                    fragment.getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!tweetToDisplay.isEmpty()) {
                                mRootView.removeAllViews();
                                mTweetView = (ListView) mInflater.inflate(R.layout.tweet_list, mRootView, false);
                                mTweetView.setClickable(true);
                                mTweetView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Tweet tweet = (Tweet) mTweetView.getItemAtPosition(position);
                                        fragment.getActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + tweet.getFrom_user())));
                                    }
                                });
                                ListTweetAdapter adapter = new ListTweetAdapter(mTweetView.getContext(), tweetToDisplay);
                                mTweetView.setAdapter(adapter);
                                mRootView.addView(mTweetView);
                                isLoaded = new AtomicBoolean(true);
                            } else {
                                progress.setVisibility(View.INVISIBLE);
                                Log.i(TAG, "Pas de reseau donc pas de recherche");
                                searchTweet.setText(R.string.tweetnoreseau);
                            }
                        }
                    });
                }

            }
            else if (isLoaded == null || !isLoaded.get()) {
                if (searchTweet == null) {
                    searchTweet = (TextView) fragment.getActivity().findViewById(R.id.searchTweet);
                }
                mRootView.removeAllViews();
                searchTweet.setText(R.string.tweetnoreseau);
            }
            return null;
        }

        /**
         * Cette méthode parse le flux JSON et l'unmarshal
         */
        private List<Tweet> parseJson(InputStream stream) {
            try {
                JsonParser jp = this.jsonFactory.createJsonParser(stream);
                RechercheTweet a = this.objectMapper.readValue(jp, RechercheTweet.class);
                return a.getResults();
            } catch (IOException e) {
                Log.e(TAG, "Erreur de parsing des messages twitter", e);
                return new ArrayList<Tweet>();
            }
        }

    }
}
