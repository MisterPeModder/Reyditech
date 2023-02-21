package eu.epitech.reyditech.test

import android.app.Activity
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController

internal class TestActivity : Activity()

internal fun activityTest(test: (ActivityController<TestActivity>) -> Unit) {
    Robolectric.buildActivity(TestActivity::class.java).use { controller ->
        controller.setup()
        test(controller)
    }
}
