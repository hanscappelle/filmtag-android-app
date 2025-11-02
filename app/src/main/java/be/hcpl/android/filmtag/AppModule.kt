package be.hcpl.android.filmtag

import be.hcpl.android.filmtag.ui.activity.MainViewModel
import com.google.gson.Gson
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    viewModelOf(::MainViewModel)

    //factoryOf(::WeatherTransformerImpl) { bind<WeatherTransformer>() }
    //factoryOf(::WeatherRepositoryImpl) { bind<WeatherRepository>() }

    singleOf(::Gson)
}