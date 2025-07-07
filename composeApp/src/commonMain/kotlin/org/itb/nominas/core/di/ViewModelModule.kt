package org.itb.nominas.core.di

import org.itb.nominas.core.utils.MainViewModel
import org.itb.nominas.features.attendance.ui.AttendanceViewModel
import org.itb.nominas.features.deductions.ui.DeductionViewModel
import org.itb.nominas.features.history.ui.HistoryViewModel
import org.itb.nominas.features.home.ui.HomeViewModel
import org.itb.nominas.features.login.ui.LoginViewModel
import org.itb.nominas.features.payroll.ui.PayRollViewModel
import org.itb.nominas.features.tracker.ui.TrackerViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    single { MainViewModel(get(), get(), get(), get()) }
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::PayRollViewModel)
    viewModelOf(::DeductionViewModel)
    viewModelOf(::AttendanceViewModel)
    viewModelOf(::TrackerViewModel)
    viewModelOf(::HistoryViewModel)
}