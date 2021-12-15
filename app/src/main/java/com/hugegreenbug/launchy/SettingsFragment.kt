package com.hugegreenbug.launchy

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.content.Intent
import android.graphics.*
import android.view.View.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.*
import androidx.leanback.preference.LeanbackPreferenceFragmentCompat
import android.provider.Settings
import androidx.leanback.preference.LeanbackSettingsFragmentCompat
import androidx.preference.*
import android.content.SharedPreferences
import androidx.core.content.res.ResourcesCompat

class SettingsFragment : LeanbackSettingsFragmentCompat() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.requestFocus()

        return view
    }

    override fun onPreferenceStartInitialScreen() {
        startPreferenceFragment(LauncherFragment())
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        val args = pref.extras
        val f: Fragment = childFragmentManager.fragmentFactory.instantiate(
            requireActivity().classLoader, pref.fragment
        )
        f.arguments = args
        childFragmentManager.beginTransaction().add(f, preferenceFragTag).addToBackStack(null).commit()
        if (f is PreferenceFragmentCompat
            || f is PreferenceDialogFragmentCompat
        ) {
            startPreferenceFragment(f)
        } else {
            startImmersiveFragment(f)
        }
        return true
    }

    override fun onPreferenceStartScreen(
        caller: PreferenceFragmentCompat?,
        pref: PreferenceScreen
    ): Boolean {
        val fragment: Fragment = LauncherFragment()
        val args = Bundle(1)
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.key)
        fragment.arguments = args
        startPreferenceFragment(fragment)
        return true
    }

    companion object {
        const val settingsTagFragment:String="LAUNCHER_SETTINGS_FRAG"
        const val preferenceFragTag:String = "preferenceFragTag"

    }
}

/**
 * The fragment that is embedded in SettingsFragment
 */
class LauncherFragment : LeanbackPreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Load the preferences from an XML resource
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val androidSettings: Preference? = findPreference(getString(R.string.android_settings)) as Preference?
        androidSettings?.setOnPreferenceClickListener {
            activity?.onBackPressed()
            val intent = Intent(Settings.ACTION_SETTINGS)
            startActivity(intent)
            return@setOnPreferenceClickListener true
        }

        val listPreference: ListPreference? =
            findPreference(getString(R.string.pref_background)) as ListPreference?
        if (listPreference != null) {
            listPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    var wall: Drawable? = null
                    when (newValue) {
                        "Black" -> {
                            wall = ResourcesCompat.getDrawable(resources, R.drawable.blackbg, null)
                        }
                        "DarkGrey" -> {
                            wall = ResourcesCompat.getDrawable(resources, R.drawable.greybg, null)
                        }
                        "UnsplashNature" -> {
                            startUnsplashActivity(UnsplashActivity.nature)
                        }
                        "UnsplashAnimals" -> {
                            startUnsplashActivity(UnsplashActivity.animals)
                        }
                        "UnsplashTextures" -> {
                            startUnsplashActivity(UnsplashActivity.textures)
                        }
                        "UnsplashDroneCaptures" -> {
                            startUnsplashActivity(UnsplashActivity.drone)
                        }
                        "UnsplashWallpapers" -> {
                            startUnsplashActivity(UnsplashActivity.wallpapers)
                        }
                        "UnsplashEditorial" -> {
                            startUnsplashActivity(UnsplashActivity.editorial)
                        }
                        else -> {
                            wall = ResourcesCompat.getDrawable(resources, R.drawable.blackbg, null)
                        }
                    }

                    if (wall != null) {
                        this.activity?.let { BitmapUtils.setWallpaper(wall, it) }
                    }

                    true
                }
        }

        val dimPreference: ListPreference? =
            findPreference(getString(R.string.pref_dim_background)) as ListPreference?
        if (dimPreference != null) {
            if (dimPreference.value == null) {
                // to ensure we don't get a null value
                // set first value by default
                val defaultValue = "Light"
                dimPreference.value = defaultValue
                setBackgroundDim(defaultValue)
            }
            setBackgroundDim(dimPreference.value)
            dimPreference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _, newValue ->
                    setBackgroundDim(newValue as String)
                    true
                }
        }
    }

    private fun setBackgroundDim(value:String) {
        when (value) {
            "None" -> {
                activity?.window?.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.transparentbg, null)
                )
            }
            "Light" -> {
                activity?.window?.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.blacktransparentbg_light, null)
                )
            }
            "Medium" -> {
                activity?.window?.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.blacktransparentbg_medium, null)
                )
            }
            "Dark" -> {
                activity?.window?.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.blacktransparentbg_dark, null)
                )
            }
            else -> {
                activity?.window?.setBackgroundDrawable(
                    ResourcesCompat.getDrawable(resources, R.drawable.blacktransparentbg_light, null)
                )
            }
        }

        val sh = context?.getSharedPreferences(MainActivity.launchPrefs, MODE_PRIVATE)
        val prefEditor: SharedPreferences.Editor? = sh?.edit()
        prefEditor?.putString(MainActivity.backgroundDim, value)
        prefEditor?.apply()
    }

    private fun startUnsplashActivity(value:String) {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this.activity, UnsplashActivity::class.java)
            intent.putExtra(UnsplashActivity.unsplashKey, value)
            startActivity(intent)
        }, 1200)
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        return super.onPreferenceTreeClick(preference)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        view?.requestFocus()

        return view
    }
}






