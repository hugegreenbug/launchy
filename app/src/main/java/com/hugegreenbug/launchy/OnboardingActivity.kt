package com.hugegreenbug.launchy
import android.os.Bundle
import androidx.fragment.app.FragmentActivity

/*
 * OnboardingActivity for OnboardingFragment
 */
class OnboardingActivity : FragmentActivity() {
    /**
     * Called when the activity is first created.
     */
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding)
    }
}