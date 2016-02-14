package com.adamwilson.golf;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.adamwilson.golf.DataModel.GolfDB;
import com.adamwilson.golf.DataModel.StrokesDB;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Objects;

public class RangeFinder implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public GoogleMap googleMap;
    public MapFragment mapFragment;
    public GoogleApiClient mGoogleApiClient;
    public Location golferLocation;
    private HoleActivity context;
    private ArrayList<String[]> histroyStrokes = new ArrayList<String[]>();
    private ArrayList<Marker> historyPins;
    //private GolfDB strokesDB;
    //private ArrayList<GroundOverlay> groundOverlays = new ArrayList<GroundOverlay>();
    BitmapFactory.Options options = new BitmapFactory.Options();

    public RangeFinder(MapFragment map, HoleActivity c, String[] mapData) {
        context = c;
        // Required empty public constructor
        this.mapFragment = map;
        googleMap = mapFragment.getMap();
        UiSettings settings = googleMap.getUiSettings();
        settings.setScrollGesturesEnabled(false);
        settings.setZoomControlsEnabled(false);
        settings.setZoomGesturesEnabled(false);
        settings.setRotateGesturesEnabled(false);
        settings.setMyLocationButtonEnabled(false);

        googleMap.setMyLocationEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        //strokesDB = GolfDB.getGolfDatabase(this.context);
        histroyStrokes = context.roundsDB.getAllStrokes();

        //String[] hole = ((HoleActivity) context).currentHole;

        String[] firstPin = new String[]{"B9", "", "41.730681", "-83.582807"};
        histroyStrokes.add(firstPin);

        String[] secondPin = new String[]{"B9", "", "41.731775", "-83.582973"};
        histroyStrokes.add(secondPin);

        String[] thirdPin = new String[]{"B9", "", "41.732476", "-83.583553"};
        histroyStrokes.add(thirdPin);

        LatLng sw_point = new LatLng(Double.parseDouble(mapData[0]),Double.parseDouble(mapData[1]));
        LatLng ne_point = new LatLng(Double.parseDouble(mapData[2]),Double.parseDouble(mapData[3]));

        addWorldOverlay(mapData[4].toLowerCase(), sw_point, ne_point);
    }

    //hole[3],hole[4] cLATLNG
    //hole[5],hole[6] swLATLNG
    //hole[7],hole[8] neLATLNG
    //hole[8] = zoom
    //Bitmap holeBitmap;
    //BitmapDescriptor holeImage;
    public void loadMap(String[] hole) {
    /*    if (groundOverlays.size() != 0){
            for (GroundOverlay groundOverlay : groundOverlays){
                groundOverlay.remove();
            }
            groundOverlays.clear();
        }
        */
        //googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(hole[3]), Double.parseDouble(hole[4])), Float.parseFloat(hole[9])));
        String rotate = hole[10];
        if (rotate.equalsIgnoreCase("270")){
            rotate = "90";
        }else if (rotate.equalsIgnoreCase("90")){
            rotate = "270";
        }

        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenDensity = metrics.densityDpi;

        Float zoom = Float.parseFloat(hole[9]);
        if (screenDensity == DisplayMetrics.DENSITY_MEDIUM) {
            if ((context.getResources().getConfiguration().screenLayout &
                    Configuration.SCREENLAYOUT_SIZE_MASK) ==
                    Configuration.SCREENLAYOUT_SIZE_NORMAL) {
                zoom -= .4f;
            }else{
                zoom += .5f;
            }
        } else if (screenDensity == DisplayMetrics.DENSITY_HIGH) {
            zoom -= .23f;
        }


        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Double.parseDouble(hole[3]), Double.parseDouble(hole[4])),zoom,0.0f,Float.parseFloat(rotate))));
    /*
        LatLng sw_point = new LatLng(Double.parseDouble(hole[5]),Double.parseDouble(hole[6]));
        LatLng ne_point = new LatLng(Double.parseDouble(hole[7]),Double.parseDouble(hole[8]));
        addWorldOverlay(sw_point,ne_point);

        options.inSampleSize = 2;
        holeBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.holelayouttest, options);
        holeImage = BitmapDescriptorFactory.fromBitmap(holeBitmap);
        holeBitmap.recycle();
        holeBitmap = null;

        LatLngBounds bounds = new LatLngBounds(sw_point,ne_point);
        GroundOverlay groundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                                        .image(holeImage)
                                        .positionFromBounds(bounds));
        groundOverlays.add(groundOverlay);
        holeImage = null;
        */
    }

    @Override
    public void onConnected(Bundle connectionHint) {
    }

    @Override
    public void onConnectionSuspended(int connection){

    }

    @Override
    public void onConnectionFailed(ConnectionResult result){

    }

    private Polyline markerLine;
    private Marker pinMarker;

    private final double tapRadius = .0003;
    public void onMapClick(LatLng point) {
        LatLngBounds tapBounds = new LatLngBounds(new LatLng(point.latitude - tapRadius, point.longitude - tapRadius),
                new LatLng(point.latitude + tapRadius, point.longitude + tapRadius));

        //golfer location
        golferLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        LatLng golferLatLng = new LatLng(golferLocation.getLatitude(), golferLocation.getLongitude());

        //if tap contains golfer
        if (tapBounds.contains(golferLatLng)) {
            saveStrokeLocation();
            return;
        }

        if (pinMarker != null) {
            markerLine.remove();
            pinMarker.remove();
            pinMarker = null;
            return;
        }

        Location pin = new Location("Pin");
        pin.setLatitude(point.latitude);
        pin.setLongitude(point.longitude);

        float meters = golferLocation.distanceTo(pin);
        float yards = meters * 1.093f;
        String distance = Integer.toString(Math.round(yards));

        Bitmap distancePinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.distance_pin, options);
        BitmapDescriptor distancePinimage = BitmapDescriptorFactory.fromBitmap(distancePinBitmap);
        options.inSampleSize = 2;
        distancePinBitmap.recycle();

        pinMarker = googleMap.addMarker(new MarkerOptions().position(point).title(distance + " yrds").icon(distancePinimage));
        pinMarker.showInfoWindow();

        PolylineOptions lineOptions = new PolylineOptions() .add(point) .add(golferLatLng) .color(Color.argb(100, 0, 255, 0));
        markerLine = googleMap.addPolyline(lineOptions);
    }

    private void deleteHistoryPin(final Marker marker){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete stroke?");
        // Add the buttons
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                marker.remove();
                historyPins.remove(marker);
                context.roundsDB.deleteStroke(Double.toString(marker.getPosition().latitude));
            }
        });
        builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void saveStrokeLocation(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save stroke location?");
        // Add the buttons
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                String[] hole = ((HoleActivity) context).currentHole;

                String[] stroke = new String[4];
                stroke[0] = hole[0];
                stroke[1] = "";
                stroke[2] = Double.toString(golferLocation.getLatitude());
                stroke[3] = Double.toString(golferLocation.getLongitude());
                histroyStrokes.add(stroke);

                context.roundsDB.insertStroke(stroke[0],stroke[1],stroke[2],stroke[3]);
            }
        });
        builder.setNeutralButton("cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean onMarkerClick(Marker marker){
        if (historyPins != null) {
            for (Marker m : historyPins) {
                if (marker.equals(m)){
                    deleteHistoryPin(marker);
                    break;
                }
            }
        }
        return true;
    }

    Bitmap worldBitmap;
    BitmapDescriptor worldImage;
    private void addWorldOverlay(String imageName, LatLng sw, LatLng ne){
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inDither = true;

        int colorID = context.getResources().getIdentifier("world_bg", "drawable", context.getPackageName());
        options.inSampleSize = 4;
        Bitmap bgBitmap = BitmapFactory.decodeResource(context.getResources(), colorID, options);
        BitmapDescriptor bgImage = BitmapDescriptorFactory.fromBitmap(bgBitmap);
        bgBitmap.recycle();

        //LatLngBounds bgBounds = new LatLngBounds(new LatLng(sw.latitude-1000,sw.longitude-1000), ne);
        LatLngBounds bgBounds = new LatLngBounds(new LatLng(sw.latitude-1,sw.longitude-1),
                new LatLng(ne.latitude+1,ne.longitude+1));
        GroundOverlay bgOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(bgImage)
                .positionFromBounds(bgBounds));

        imageName = imageName.substring(0,imageName.length()-4);

        int id = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        options.inSampleSize = 1;
        worldBitmap = BitmapFactory.decodeResource(context.getResources(), id, options);
        worldImage = BitmapDescriptorFactory.fromBitmap(worldBitmap);
        worldBitmap.recycle();

        LatLngBounds bounds = new LatLngBounds(sw,ne);
        GroundOverlay groundOverlay = googleMap.addGroundOverlay(new GroundOverlayOptions()
                .image(worldImage)
                .positionFromBounds(bounds));

    }

    public void toggleHistory(boolean shouldHide){
        if (shouldHide){
            //remove all history pins
            for (Marker marker : historyPins){
                marker.remove();
            }
            historyPins = null;
        }else{
            //display history pins
            historyPins = showAllHistoryPins();
        }
    }



    private ArrayList<Marker> showAllHistoryPins(){

        ArrayList<Marker> markers = new ArrayList<Marker>();
        String[] hole = ((HoleActivity) context).currentHole;

        Bitmap historyPinBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.history_pin, options);
        BitmapDescriptor historyPinImage = BitmapDescriptorFactory.fromBitmap(historyPinBitmap);
        options.inSampleSize = 2;
        historyPinBitmap.recycle();

        for (String[] stroke : histroyStrokes){
            if (stroke[0].equalsIgnoreCase(hole[0])){
                LatLng latLng = new LatLng(Double.parseDouble(stroke[2]),Double.parseDouble(stroke[3]));
                Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).title("").icon(historyPinImage));
                marker.showInfoWindow();
                markers.add(marker);
            }
        }

        return markers;
    }

    public void prepareForFinish(){
        googleMap.clear();
        googleMap = null;
        worldBitmap = null;
        worldImage = null;
        mGoogleApiClient.disconnect();
    }
}
