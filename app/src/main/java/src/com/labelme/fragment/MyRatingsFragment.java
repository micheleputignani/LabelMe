package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.RatedLabelsAdapter;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.helper.SessionManager;
import src.com.labelme.model.RatingsItem;

/**
 * Fragment relativo alle annotazioni votate dall'utente collegato all'applicazione.
 */

public class MyRatingsFragment extends Fragment {

    // Progress dialog
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // Creating JSON parser object
    JSONParser jsonParser = new JSONParser();

    List<RatingsItem> ratingsList;

    // session manager
    SessionManager session;
    private int success;

    public MyRatingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ratings_labels, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        // loading json_ratings in background thread
        loadRatingsLabels();
        return view;
    }

    private void loadRatingsLabels() {
        int result = CheckNetwork.isInternetAvailable(getActivity());
        if (result == 1) {
            new loadRatingsLabels().execute();
        } else {
            showToast(result);
        }
    }

    private void showToast(int type) {
        String message = "";
        switch (type) {
            case -1:
                message = getResources().getString(R.string.airplane_mode_on);
                break;
            case 0:
                message = getResources().getString(R.string.connection_off);
                break;
        }
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {

    }

    /**
     * Background Async Task to load all ratings labels by making HTTP request
     */
    class loadRatingsLabels extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
            progressDialog.setMessage(getResources().getString(R.string.loading_ratings_labels));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * getting all ratings labels from url
         */
        protected String doInBackground(String... args) {
            session = new SessionManager(getActivity());
            // building parameters
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, session.getUser_id()));
            Log.e("AUTHOR: ", session.getUser_id());
            // getting JSON string from URL
            JSONObject json = jsonParser.makeHttpRequest(AppConfig.url_ratings_labels, "POST", params);

            // check your log cat for JSON response
            Log.d("Create response: ", json.toString());

            try {
                // checking for SUCCESS TAG
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    // json_labels found
                    // getting array of ratings labels
                    JSONArray json_ratings = json.optJSONArray(AppConfig.TAG_RATINGS_ARRAY);
                    ratingsList = new ArrayList<>();

                    // looping through all ratings labels
                    for (int i = 0; i < json_ratings.length(); i++) {
                        JSONObject post = json_ratings.optJSONObject(i);
                        RatingsItem item = new RatingsItem();
                        item.setThumbnail(post.optString(AppConfig.TAG_CROPPED_IMAGE));
                        item.setLabel(post.optString(AppConfig.TAG_LABEL));
                        item.setAuthor(post.optString(AppConfig.TAG_AUTHOR));
                        item.setAverage(post.optString(AppConfig.TAG_AVERAGE));
                        item.setPersonalRating(post.optString(AppConfig.TAG_PEOPLE_RATING));

                        ratingsList.add(item);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all labels
            progressDialog.dismiss();

            String toast_message;
            switch (success) {
                case 1:
                    // sort average
                    sortList(ratingsList);
                    RatedLabelsAdapter adapter = new RatedLabelsAdapter(getActivity(), ratingsList);
                    recyclerView.setAdapter(adapter);
                    break;
                case 0:
                    toast_message = getResources().getString(R.string.no_personal_ratings_labels_found);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_LONG).show();
                    break;
                case -1:
                    toast_message = getResources().getString(R.string.required_fields);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_LONG).show();
                    break;
            }

        }
    }

    private void sortList(List<RatingsItem> ratingsList) {
        Collections.sort(ratingsList, new Comparator<RatingsItem>() {
            @Override
            public int compare(RatingsItem lhs, RatingsItem rhs) {
                return rhs.getAverage().compareTo(lhs.getAverage());
            }
        });
    }
}