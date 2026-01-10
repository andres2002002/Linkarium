package com.habitiora.linkarium.di

import com.habitiora.linkarium.data.exporters.JsonExporter
import com.habitiora.linkarium.domain.model.Exporter
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ExportModule {

    @Binds
    @IntoSet
    abstract fun bindJsonExporter(
        exporter: JsonExporter
    ): Exporter
}
