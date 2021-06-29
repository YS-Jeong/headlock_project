package com.example.headlock;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.ViewGroup;

import net.daum.android.map.MapViewEventListener;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //onCreate함수 호출
        setContentView(R.layout.activity_login); //setContentView = 화면에무엇을 보여줄것인지 설정

        MapView mapView = new MapView(this);

        ViewGroup mapViewContainer = (ViewGroup) findViewById(R.id.map_view);

        /*첨단정보통신융합산업기술원 위도 경도로 중심점 변경*/
        MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(35.877379, 128.736241);
        mapView.setMapCenterPoint(mapPoint, true); //중심점 설정

        mapViewContainer.addView(mapView);

        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("첨단정보통신융합산업기술원");
        marker.setTag(0); //태그 번호 0번으로 설정
        marker.setMapPoint(mapPoint);
        // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
        // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);
    }

}
/*
    MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(35.877379,128.736241);//mapView 클래스가 제공하는 메소드로 중심점 변경 위도, 경도

        mapView.setMapCenterPoint(mapPoint,true);


    MapPOIItem marker = new MapPOIItem();
        marker.setItemName("첨단정보통신융합산업기술원");
        marker.setTag(0);
    //marker.setMapPoint(mapPoint);
    // 기본으로 제공하는 BluePin 마커 모양.
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
    // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
        mapView.addPOIItem(marker);

}

    @Override
    public void onDaumMapOpenAPIKeyAuthenticationResult(MapView mapView, int i, String s) {

    }*/