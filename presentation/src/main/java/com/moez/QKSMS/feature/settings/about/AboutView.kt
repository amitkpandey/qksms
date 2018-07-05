package com.moez.QKSMS.feature.settings.about

import com.moez.QKSMS.common.base.QkConductorView
import com.moez.QKSMS.common.widget.PreferenceView
import io.reactivex.subjects.Subject

interface AboutView : QkConductorView<Unit> {

    val preferenceClickIntent: Subject<PreferenceView>

}