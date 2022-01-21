package com.fgm.fgmanager.Models

import android.widget.ProgressBar
import android.widget.Toast
import com.fgm.fgmanager.MainActivity
import com.fgm.fgmanager.STORAGE
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import io.grpc.InternalChannelz.id

class modelAdvertisment(val activity: MainActivity) {

    //var interAd : InterstitialAd? = null

    val mActivity : MainActivity = activity as MainActivity
    var mAdView = mActivity.findViewById<AdView>(com.fgm.fgmanager.R.id.adViewFirstBanner)

    fun loadInterAD(){
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(activity, "ca-app-pub-6587897644468158/5029076221", adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                STORAGE.interAd = null
            }

            override fun onAdLoaded(p0: InterstitialAd) {
                super.onAdLoaded(p0)
                STORAGE.interAd = p0
            }
        })
    }

    fun showInterAd(){
        if(STORAGE.interAd != null){
            STORAGE.interAd?.fullScreenContentCallback = object  : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    STORAGE.interAd = null
                    loadInterAD()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    STORAGE.interAd = null
                    loadInterAD()
                }

                override fun onAdShowedFullScreenContent() {
                    STORAGE.interAd = null
                    loadInterAD()
                }
            }
            STORAGE.interAd?.show(activity)
        }else{
            Toast.makeText(activity, "Error Ad", Toast.LENGTH_SHORT).show()
        }
    }

    fun initAdMob(){
        MobileAds.initialize(activity)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)
    }
}