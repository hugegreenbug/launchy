package com.hugegreenbug.launchy
import android.annotation.SuppressLint
import android.view.ViewGroup
import android.view.LayoutInflater
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.annotation.Nullable
import androidx.core.content.res.ResourcesCompat
import androidx.leanback.app.OnboardingSupportFragment

class OnboardingFragment : OnboardingSupportFragment() {
    private val pageImages = intArrayOf(
        R.drawable.page1,
        R.drawable.page2,
        R.drawable.page3
    )
    private var mContentView: ImageView? = null

    @Nullable
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onFinishFragment() {
        super.onFinishFragment()
        requireActivity().finish()
    }

    override fun getPageCount(): Int {
        return pageTitles.size
    }

    override fun getPageTitle(pageIndex: Int): String {
        return getString(pageTitles[pageIndex])
    }

    override fun getPageDescription(pageIndex: Int): String {
        return getString(pageDescriptions[pageIndex])
    }

    override fun onCreateBackgroundView(inflater: LayoutInflater, container: ViewGroup): View {
        val bgView = View(activity)
        bgView.setBackgroundColor(ResourcesCompat.getColor(
            resources,
            R.color.fastlane_background,
            null))
        return bgView
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Nullable
    override fun onCreateContentView(inflater: LayoutInflater, container: ViewGroup): View? {
        mContentView = ImageView(activity)
        mContentView!!.scaleType = ImageView.ScaleType.CENTER
        mContentView!!.setPadding(0, 32, 0, 32)
        mContentView!!.setImageDrawable(ResourcesCompat.getDrawable(
            resources,
            R.drawable.launchy_welcome,
            null))
        return mContentView
    }

    @Nullable
    override fun onCreateForegroundView(inflater: LayoutInflater, container: ViewGroup): View? {
        return null
    }

    override fun onPageChanged(newPage: Int, previousPage: Int) {
        mContentView!!.setImageDrawable(
            ResourcesCompat.getDrawable(
            resources,
            pageImages[newPage-1],
            null))
    }

    companion object {
        private val pageTitles = intArrayOf(
            R.string.onboarding_title_welcome,
            R.string.onboarding_title_favorites,
            R.string.onboarding_title_applications,
            R.string.onboarding_title_settings
        )
        private val pageDescriptions = intArrayOf(
            R.string.onboarding_description_welcome,
            R.string.onboarding_description_favorites,
            R.string.onboarding_description_applications,
            R.string.onboarding_description_settings
        )
    }
}