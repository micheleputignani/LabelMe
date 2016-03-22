package src.com.labelme.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
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
import src.com.labelme.adapter.GridViewAdapter;
import src.com.labelme.core.ExistingLabels;
import src.com.labelme.core.FirstCropActivity;
import src.com.labelme.helper.AppConfig;
import src.com.labelme.helper.CheckNetwork;
import src.com.labelme.helper.JSONParser;
import src.com.labelme.helper.SessionManager;

public class HomeFragment extends Fragment {

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

    // json_images JSONArray
    JSONArray json_images = null;

    private int success;

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
        return v;
    }

    private void populateGridView() {
        int result = CheckNetwork.isInternetAvailable(getActivity());
        if (result == 1) { // network available
            // Loading json_images in background thread
            new loadAllImages().execute();
        } else {
            showToast(result);
        }
    }

    @Override
    public void onViewCreated(View view, final Bundle savedInstanceState) {
        // on selecting single image
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                int result = CheckNetwork.isInternetAvailable(getActivity());
                if (result == 1) {
                    checkExistingLabels(position);
                } else {
                    showToast(result);
                }
            }
        });
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

    private void checkExistingLabels(int position) {
        new checkExistingLabels(position).execute();
    }

    class checkExistingLabels extends AsyncTask<String, String, String> {

        int original_image_id;
        int original_image_position;

        checkExistingLabels(int position) {
            this.original_image_id = position + 1;
            this.original_image_position = position;
        }

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
            progressDialog.setMessage(getResources().getString(R.string.check_existing_labels));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... args) {
            // building parameters
            SessionManager session = new SessionManager(getActivity());
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(AppConfig.TAG_ORIGINAL_IMAGE, String.valueOf(original_image_id)));
            params.add(new BasicNameValuePair(AppConfig.TAG_AUTHOR, session.getUser_id()));
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.url_check_existing_labels, "POST", params);

            // check log cat for JSON response
            Log.d("Existing labels: ", json.toString());

            try {
                // checking for SUCCESS TAG
                success = json.getInt(AppConfig.TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after checking existing labels
            progressDialog.dismiss();
            switch (success) {
                case 1:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialog);
                    builder.setTitle(getResources().getString(R.string.information));
                    builder.setIcon(R.drawable.ic_priority);
                    builder.setMessage(getResources().getString(R.string.existing_labels_found));
                    builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), ExistingLabels.class);
                            i.putExtra(AppConfig.TAG_ORIGINAL_IMAGE, original_image_id);
                            getActivity().startActivity(i);
                        }
                    });
                    builder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getActivity(), FirstCropActivity.class);
                            i.putExtra(AppConfig.TAG_IMAGE_ID, original_image_position);
                            getActivity().startActivity(i);
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    break;
                case 0:
                    Intent i = new Intent(getActivity(), FirstCropActivity.class);
                    i.putExtra(AppConfig.TAG_IMAGE_ID, original_image_position);
                    getActivity().startActivity(i);
                    break;
                case -1:
                    Toast.makeText(getActivity(), getResources().getString(R.string.required_fields), Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }


    /**
     * Background Async Task to Load all images by making HTTP Request
     */
    class loadAllImages extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Progress dialog
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dialog);
            progressDialog.setMessage(getResources().getString(R.string.loading_images));
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        /**
         * getting All images from url
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            // getting JSON string from URL
            JSONObject json = jParser.makeHttpRequest(AppConfig.url_all_images, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Create response: ", json.toString());

            try {
                // Checking for SUCCESS TAG
                success = json.getInt(AppConfig.TAG_SUCCESS);

                if (success == 1) {
                    // json_images found
                    // Getting Array of Images
                    json_images = json.getJSONArray(AppConfig.TAG_IMAGES);

                    // looping through All Images
                    for (int i = 0; i < json_images.length(); i++) {
                        JSONObject c = json_images.getJSONObject(i);

                        // Storing each json item in variable
                        String id = c.getString(AppConfig.TAG_IMAGE_ID);
                        String link = c.getString(AppConfig.TAG_IMAGE_LINK);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(AppConfig.TAG_IMAGE_ID, id);
                        map.put(AppConfig.TAG_IMAGE_LINK, link);

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

            switch (success) {
                case 1:
                    // Updating parsed JSON data into GridView
                    // Creating GridViewAdapter Object
                    GridViewAdapter gridViewAdapter = new GridViewAdapter(getActivity(), ids, images);

                    // Adding adapter to gridView
                    gridView.setAdapter(gridViewAdapter);
                    break;
                case 0:
                    String toast_message = getResources().getString(R.string.no_images_found);
                    Toast.makeText(getActivity(), toast_message, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}

