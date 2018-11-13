package com.ongtonnesoup.konvert

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.vision.CameraSource
import com.ongtonnesoup.konvert.detection.DetectionFragment
import com.ongtonnesoup.konvert.detection.mobilevision.MobileVisionOcrGateway

class MainActivity : AppCompatActivity(), MobileVisionOcrGateway.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, DetectionFragment(), DetectionFragment.TAG)
                .commit()
    }

    override fun onCameraSourceAvailable(cameraSource: CameraSource) {
        val fragment = supportFragmentManager.findFragmentByTag(DetectionFragment.TAG)
        fragment?.let {
            (it as DetectionFragment).onCameraSourceAvailable(cameraSource)
        }
    }

    override fun onCameraSourceReleased() {
        val fragment = supportFragmentManager.findFragmentByTag(DetectionFragment.TAG)
        fragment?.let {
            (it as DetectionFragment).onCameraSourceReleased()
        }
    }
}
