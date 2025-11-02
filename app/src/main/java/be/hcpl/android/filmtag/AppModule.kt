package be.hcpl.android.filmtag

import be.hcpl.android.filmtag.domain.repository.FilmRollRepository
import be.hcpl.android.filmtag.domain.repository.SharedPreferencesProvider
import be.hcpl.android.filmtag.ui.activity.AboutViewModel
import be.hcpl.android.filmtag.ui.activity.EditRollViewModel
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel
import be.hcpl.android.filmtag.ui.activity.MainViewModel
import com.google.gson.Gson
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::MainViewModel)
    viewModelOf(::FilmRollViewModel)
    viewModelOf(::AboutViewModel)
    viewModelOf(::EditRollViewModel)

    factoryOf(::FilmRollRepository)

    singleOf(::SharedPreferencesProvider)
    singleOf(::Gson)
}