package com.moez.QKSMS.feature.settings.about

import com.moez.QKSMS.R
import com.moez.QKSMS.common.Navigator
import com.moez.QKSMS.common.base.QkPresenter
import com.uber.autodispose.kotlin.autoDisposable
import javax.inject.Inject

class AboutPresenter @Inject constructor(
        private val navigator: Navigator
) : QkPresenter<AboutView, Unit>(Unit) {

    override fun onCreate(view: AboutView) {
        super.onCreate(view)

        view.preferenceClickIntent
                .autoDisposable(view.scope())
                .subscribe { preference ->
                    when (preference.id) {
                        R.id.developer -> navigator.showDeveloper()

                        R.id.source -> navigator.showSourceCode()

                        R.id.changelog -> navigator.showChangelog()

                        R.id.contact -> navigator.showSupport()

                        R.id.license -> navigator.showLicense()
                    }
                }
    }

}