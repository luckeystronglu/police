package com.yzh.rfidbike_police.activity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yzh.rfid.app.response.Bike;
import com.yzh.rfidbike_police.R;
import com.yzh.rfidbike_police.base.BaseActivity;
import com.yzh.rfidbike_police.util.Utils;
import com.yzh.rfidbike_police.view.widgets.Header;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by appadmin on 2016/12/30.
 */

public class BaseStationMapActivity extends BaseActivity implements Header.headerListener, OnGetGeoCoderResultListener {
    @BindView(R.id.header)
    Header mHeader;
    private double lat, lng;
    private String platenum;
    private MapView mMapView;
    private BaiduMap map;
    private Marker marker;
    private View showview;
    private Bike.BikeMessage deviceMessage;
    GeoCoder mSearch = null;
    private TextView location_tv1;
    private InfoWindow mInfoWindow;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_basestation_mapshow);
        ButterKnife.bind(this);
        mHeader.setListener(this);
        Intent intent = getIntent();
        deviceMessage = (Bike.BikeMessage) intent.getSerializableExtra("DeviceMessage");
        if (deviceMessage.getLatitude() > 0.1 && deviceMessage.getLatitude() < 90.0) {
            lat = deviceMessage.getLatitude();
            lng = deviceMessage.getLongitude();
        } else {
            //默认北京中心点
            lat = 39.963175;
            lng = 116.400244;
        }

        platenum = deviceMessage.getPlateNumber();
        mMapView = findViewByIds(R.id.map_mark);
        map = mMapView.getMap();
        mHeader.setTitle(platenum);
        final LatLng point = new LatLng(lat, lng);

        MapStatusUpdate mapstatusUpdatePoint = MapStatusUpdateFactory.newLatLngZoom(point, 14);
        map.setMapStatus(mapstatusUpdatePoint);

        // 隐藏 百度地图logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView)) {
            child.setVisibility(View.INVISIBLE);
        }

        map.setBuildingsEnabled(true);
        map.setCompassPosition(new Point(Utils.dpToPx(20, getResources()), Utils.dpToPx(20, getResources())));
        UiSettings settings = map.getUiSettings();
        settings.setOverlookingGesturesEnabled(false);// 屏蔽双指下拉时变成3D地图
        settings.setZoomGesturesEnabled(true);// 获取是否允许缩放手势返回:是否允许缩放手势
        settings.setCompassEnabled(true);

        if (deviceMessage.getLatitude() > 0.1 && deviceMessage.getLatitude() < 90.0) {
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.device_mark);
            OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            marker = (Marker) map.addOverlay(option);
        }else
        {
            mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
            mLocationClient.registerLocationListener(myListener); //注册监听函数
            initLocation();
            mLocationClient.start();//开始定位
        }



        map.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showview = LayoutInflater.from(BaseStationMapActivity.this).inflate(R.layout.map_marker_view, null);
                showview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        map.hideInfoWindow();
                    }
                });

                //创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量
                mInfoWindow = new InfoWindow(showview, point, 90);
                location_tv1 = (TextView) showview.findViewById(R.id.basestation_location_tv);
                mSearch = GeoCoder.newInstance();
                mSearch.setOnGetGeoCodeResultListener(BaseStationMapActivity.this);
                mSearch.reverseGeoCode(new ReverseGeoCodeOption()
                        .location(point));
                //显示InfoWindow
                map.showInfoWindow(mInfoWindow);
                return true;
            }

        });

        map.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mInfoWindow != null) {
                    map.hideInfoWindow();
                }

            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });






    }
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span= 0; //每隔1秒定位一次
        //        int span= 5000; //每隔5秒定位一次
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);

    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    //经纬度转换成地址
    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(getApplicationContext(), "找不到该地址!", Toast.LENGTH_SHORT).show();
        }
        location_tv1.setText(result.getAddress());

    }

    @Override
    public void onClickLeftIcon() {
        finish();
    }

    @Override
    public void onClickRightText() {

    }
    public class MyLocationListener implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            lat = location.getLatitude();
            lng = location.getLongitude();
            LatLng point = new LatLng(lat,lng);
            // 构建Marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.orp);
            MarkerOptions option = new MarkerOptions().position(point).icon(bitmap);
            //调整地图的中心位置及缩放比例
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(point).zoom(14.0f);
            map.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

            map.addOverlay(option);
            initCircleOverlay(point);

        }
    }
    public void initCircleOverlay(LatLng latlng_item) {


    }

}