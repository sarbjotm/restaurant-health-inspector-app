package com.example.restauranthealthinspector;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class CustomClusterRenderer extends DefaultClusterRenderer<ClusterPin> {

    private final IconGenerator mClusterIconGenerator;
    private final Context mContext;

    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager<ClusterPin> clusterManager) {
        super(context, map, clusterManager);
        //
        mContext = context;
        mClusterIconGenerator = new IconGenerator(mContext);
    }

    @Override
    protected void onBeforeClusterItemRendered(ClusterPin item, MarkerOptions markerOptions) {

        if (item.getType() == 1) {
            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red);
            markerOptions.icon(markerDescriptor).snippet(item.getSnippet());
        } else {
            BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_marker_red);
            markerOptions.icon(markerDescriptor).snippet(item.getSnippet());
        }
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<ClusterPin> cluster, MarkerOptions markerOptions) {

        if (cluster.getSize() > 4) {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_cluster_circle));
        } else {
            mClusterIconGenerator.setBackground(ContextCompat.getDrawable(mContext, R.drawable.bg_cluster_circle2));
        }

        mClusterIconGenerator.setColor(Color.BLUE);

        mClusterIconGenerator.setTextAppearance(R.style.AppTheme_WhiteTextAppearance);

        String clusterTitle = String.valueOf(cluster.getSize());
        Bitmap icon = mClusterIconGenerator.makeIcon(clusterTitle);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

}
