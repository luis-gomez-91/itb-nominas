package org.itb.nominas.core.di

import com.luisdev.marknotes.data.remote.service.LoginService
import org.itb.nominas.core.data.service.AttendanceService
import org.itb.nominas.core.data.service.DeductionService
import org.itb.nominas.core.data.service.HomeService
import org.itb.nominas.core.data.service.MainService
import org.itb.nominas.core.data.service.PayRollService
import org.itb.nominas.core.data.service.TrackerService
import org.koin.dsl.module

val serviceModule = module {
    single { MainService(get()) }
    single { LoginService(get()) }
    single { HomeService(get()) }
    single { PayRollService(get()) }
    single { DeductionService(get()) }
    single { AttendanceService(get()) }
    single { TrackerService(get()) }
}