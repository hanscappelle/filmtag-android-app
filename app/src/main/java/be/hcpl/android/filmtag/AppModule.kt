package be.hcpl.android.filmtag

import be.hcpl.android.filmtag.domain.FilmRollRepository
import be.hcpl.android.filmtag.domain.SharedPreferencesProvider
import be.hcpl.android.filmtag.ui.activity.AboutViewModel
import be.hcpl.android.filmtag.ui.activity.EditRollViewModel
import be.hcpl.android.filmtag.ui.activity.FilmRollViewModel
import be.hcpl.android.filmtag.ui.activity.EditFrameViewModel
import be.hcpl.android.filmtag.ui.activity.MainViewModel
import be.hcpl.android.filmtag.ui.tranformer.FrameUiModelTransformer
import be.hcpl.android.filmtag.ui.tranformer.TextTransformer
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
    viewModelOf(::EditFrameViewModel)

    factoryOf(::FilmRollRepository)

    singleOf(::SharedPreferencesProvider)
    singleOf(::Gson)

    factoryOf(::FrameUiModelTransformer)
    factoryOf(::TextTransformer)
}