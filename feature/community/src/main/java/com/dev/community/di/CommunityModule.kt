package com.dev.community.di

import android.content.Context
import com.dev.feature.community.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CommunityModule {

    @Provides
    @Singleton
    @Named("comment_author_you")
    fun provideCommentAuthorYou(@ApplicationContext context: Context): String = context.getString(R.string.comment_author_you)
}
