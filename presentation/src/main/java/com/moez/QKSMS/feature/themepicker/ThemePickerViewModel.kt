/*
 * Copyright (C) 2017 Moez Bhatti <moez.bhatti@gmail.com>
 *
 * This file is part of QKSMS.
 *
 * QKSMS is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * QKSMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with QKSMS.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.moez.QKSMS.feature.themepicker

import com.f2prateek.rx.preferences2.Preference
import com.moez.QKSMS.common.Navigator
import com.moez.QKSMS.common.androidxcompat.scope
import com.moez.QKSMS.common.base.QkViewModel
import com.moez.QKSMS.common.util.BillingManager
import com.moez.QKSMS.common.util.Colors
import com.moez.QKSMS.util.Preferences
import com.uber.autodispose.kotlin.autoDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.withLatestFrom
import javax.inject.Inject
import javax.inject.Named

class ThemePickerViewModel @Inject constructor(
        @Named("threadId") threadId: Long,
        private val billingManager: BillingManager,
        private val colors: Colors,
        private val navigator: Navigator,
        private val prefs: Preferences
) : QkViewModel<ThemePickerView, ThemePickerState>(ThemePickerState(threadId = threadId)) {

    private val theme: Preference<Int> = prefs.theme(threadId)

    override fun bindView(view: ThemePickerView) {
        super.bindView(view)

        theme.asObservable()
                .autoDisposable(view.scope())
                .subscribe { color -> view.setCurrentTheme(color) }

        // Update the theme when a material theme is clicked
        view.themeSelectedIntent
                .autoDisposable(view.scope())
                .subscribe { color -> theme.set(color) }

        // Update the color of the apply button
        view.hsvThemeSelectedIntent
                .doOnNext { color -> newState { copy(newColor = color) } }
                .map { color -> colors.textPrimaryOnThemeForColor(color) }
                .doOnNext { color -> newState { copy(newTextColor = color) } }
                .autoDisposable(view.scope())
                .subscribe()

        // Toggle the visibility of the apply group
        Observables.combineLatest(theme.asObservable(), view.hsvThemeSelectedIntent) { old, new -> old != new }
                .autoDisposable(view.scope())
                .subscribe { themeChanged -> newState { copy(applyThemeVisible = themeChanged) } }

        // Update the theme, when apply is clicked
        view.hsvThemeAppliedIntent
                .withLatestFrom(view.hsvThemeSelectedIntent) { _, color -> color }
                .withLatestFrom(billingManager.upgradeStatus) { color, upgraded ->
                    if (!upgraded) {
                        view.showQksmsPlusSnackbar()
                    } else {
                        theme.set(color)
                    }
                }
                .autoDisposable(view.scope())
                .subscribe()

        // Show QKSMS+ activity
        view.viewQksmsPlusIntent
                .autoDisposable(view.scope())
                .subscribe { navigator.showQksmsPlusActivity("settings_theme") }

        // Reset the theme
        view.hsvThemeClearedIntent
                .withLatestFrom(theme.asObservable()) { _, color -> color }
                .autoDisposable(view.scope())
                .subscribe { color -> view.setCurrentTheme(color) }
    }

}