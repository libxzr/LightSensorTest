package moe.xzr.lightsensortest

import android.os.Bundle
import android.preference.PreferenceActivity

class MainActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addPreferencesFromResource(R.xml.main_preference)
    }
}