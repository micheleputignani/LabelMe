package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.GridViewAdapter;
import src.com.labelme.core.FirstCropActivity;
import src.com.labelme.helper.JSONParser;

public class HomeFragment extends Fragment {

    // Variables for scroll listener
    int mVisibleThreshold = 5;
    int mCurrentPage = 0;
    int mPreviousTotal = 0;
    boolean mLoading = true;
    boolean mLastPage = false;
    boolean userScrolled = false;

    // Progress Dialog
    private ProgressDialog progressDialog;
    private GridView gridView;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    // images array from server
    ArrayList<HashMap<String, String>> imagesList;

    // Arraylist for storing image id and urls
    private ArrayList<String> ids;
    private ArrayList<String> images;

    // url to get all memberships list
    private static String url_all_images = "http://androidlabelme.altervista.org/url_all_images.php";

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_IMAGES = "images";
    private static final String TAG_IID = "id";
    private static final String TAG_LINK = "link_image";
    // json_images JSONArray
    JSONArray json_images = null;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        gridView = (GridView) v.findViewById(R.id.gridview);
        ids = new ArrayList<>();
        images = new ArrayList<>();
        // Hashmap for GridView
        imagesList = new ArrayList<HashMap<String, String>>();
        populateGridView();
        implementScrollListener();
        return v;
    }

    private void populateGridView() {
        // Loading json_images in background thread
        new loadAllImages().execute();
    }

    // implement scroll listener
    private void implementScrollListener() {
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // if scroll state is touch scroll then set userScrolled true
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // now check if userScrolled is true and also check if the item is end
                // then update grid view and set userScrolled to false
                if (userScrolled && firstVisibleItem + visibleItemCount == totalItemCount) {
                    userScrolled = false;
                    updateGridView();
                }
            }
        });
    }

    // Method for repopulating grid view
    private void updateGridView() {
        // handler to show refresh for a period of time you can use async task
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // REQUEST NEW ITEMS

                // ADD DATA TO ARRAYLIST

                // SET ADAPTER

            }
        }, 5000);
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        // on selecting single image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(), FirstCropActivity.class);
                // passing array index
                i.putExtra(TAG_IID, position);
                getActivity().startActivity(i);
            }
        });
    }

    /**
     * Background Async Task to Load all members by making HTTP Request
     */
    class loadAllImages extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Loading images, please wait...");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * getting All images from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(url_all_images, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("All json_images: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                int success = json.getInt(TAG_SUCCESS);

                if (success == 1) {
                    // json_images found
                    // Getting Array of Images
                    json_images = json.getJSONArray(TAG_IMAGES);

                    // looping through All Images
                    for (int i = 0; i < json_images.length(); i++) {
                        JSONObject c = json_images.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(TAG_IID);
                        String link = c.getString(TAG_LINK);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_IID, id);
                        map.put(TAG_LINK, link);

                        ids.add(id);
                        images.add(link);

                        // adding HashList to ArrayList
                        imagesList.add(map);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all images
            progressDialog.dismiss();

            // updating UI from Background Thread
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into GridView
                     */

                    // Creating GridViewAdapter Object
                    GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity(), ids, images);

                    // Adding adapter to gridview
                    gridView.setAdapter(gridViewAdapter);
                }
            });
        }
    }
}

