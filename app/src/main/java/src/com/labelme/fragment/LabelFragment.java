package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import src.com.labelme.R;
import src.com.labelme.adapter.ListViewAdapter;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.helper.SessionManager;

public class LabelFragment extends Fragment {

    // Progress dialog
    private ProgressDialog pDialog;
    private ListView listView;

    // Creating JSON parser object
    JSONParser jParser = new JSONParser();

    ArrayList<HashMap<String, String>> labelsList;

    // Arraylist for storing image and title
    private ArrayList<String> images;
    private ArrayList<String> titles;

    // json labels JSONArray
    JSONArray json_labels = null;

    // session manager
    SessionManager session;
    private int success;

    public LabelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_labels, container, false);

        listView = (ListView) view.findViewById(R.id.listview);

        images = new ArrayList<>();
        titles = new ArrayList<>();

        // hashmap for ListView
        labelsList = new ArrayList<HashMap<String, String>>();

        // loading json_labels in background thread
        loadAllLabels();
        return view;
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {

    }

    private void loadAllLabels() {
        int result = CheckNetwork.isInternetAvailable(getActivity());
        if (result == 1) {
            new loadAllLabels().execute();
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

    /**
     * Background Async Task to load all labels by making HTTP request
     */
    class loadAllLabels extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread show progress dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
            pDialog.setMessage(getResources().getString(R.string.loading_labels));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting all lables from url
         */
        protected String doInBackground(String... args) {
            session = new SessionManager(getActivity());
            // building parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, session.getUser_id()));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.url_all_labels, "POST", params);

            // check your log cat for JSON response
            Log.d("All json_labels: ", json.toString());

            try {
                // checking for SUCCESS TAG
                success = json.getInt(AppConfig.TAG_SUCCESS);
                if (success == 1) {
                    // json_labels found
                    // getting array of labels
                    json_labels = json.getJSONArray(AppConfig.TAG_LABELS_ARRAY);

                    // looping through all labels
                    for (int i = 0; i < json_labels.length(); i++) {
                        JSONObject c = json_labels.getJSONObject(i);

                        // storing each json item in variable
                        String image = c.getString(AppConfig.TAG_CROPPED_IMAGE);
                        String label = c.getString(AppConfig.TAG_LABEL);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(AppConfig.TAG_CROPPED_IMAGE, image);
                        map.put(AppConfig.TAG_LABEL, label);

                        images.add(image);
                        titles.add(label);

                        labelsList.add(map);
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
            pDialog.dismiss();

            switch (success) {
                case 1:
                    // updating parsed JSON data into ListView
                    // creating ListViewAdapter obejct
                    ListViewAdapter listViewAdapter = new ListViewAdapter(getActivity(), images, titles);
                    // adding adapter to listview
                    listView.setAdapter(listViewAdapter);
                    break;
                case 0:
                    String toast_message = getResources().getString(R.string.no_personal_labels);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
