package com.example.data.di

import com.example.data.local.preferences.FavoriteTabPreferenceRepositoryImpl
import com.example.data.repository.auth.EmailVerificationRepositoryImpl
import com.example.data.repository.auth.LoginRepositoryImpl
import com.example.data.repository.auth.PasswordResetRepositoryImpl
import com.example.data.repository.auth.SessionRepositoryImpl
import com.example.data.repository.auth.SignupRepositoryImpl
import com.example.data.repository.auth.TokenManagerImpl
import com.example.data.repository.community.CommunityRepositoryImpl
import com.example.data.repository.destinations.DestinationsRepositoryImpl
import com.example.data.repository.destinations.LocationRepositoryImpl
import com.example.data.repository.destinations.RecentlyViewedRepositoryImpl
import com.example.data.repository.favorite.FavoriteDestinationRepositoryImpl
import com.example.data.repository.favorite.FavoritePlaceRepositoryImpl
import com.example.data.repository.favorite.FavoritePostRepositoryImpl
import com.example.data.repository.favorite.FavoriteTripRepositoryImpl
import com.example.data.repository.review.ReviewRepositoryImpl
import com.example.data.repository.search.RecentSearchRepositoryImpl
import com.example.data.repository.survey.SurveyPreferencesRepositoryImpl
import com.example.domain.repository.auth.EmailVerificationRepository
import com.example.domain.repository.auth.LoginRepository
import com.example.domain.repository.auth.PasswordResetRepository
import com.example.domain.repository.auth.SessionRepository
import com.example.domain.repository.auth.SignupRepository
import com.example.domain.repository.auth.TokenManager
import com.example.domain.repository.community.CommunityRepository
import com.example.domain.repository.destinations.DestinationsRepository
import com.example.domain.repository.destinations.LocationRepository
import com.example.domain.repository.destinations.RecentlyViewedRepository
import com.example.domain.repository.favorite.FavoriteDestinationRepository
import com.example.domain.repository.favorite.FavoritePlaceRepository
import com.example.domain.repository.favorite.FavoritePostRepository
import com.example.domain.repository.favorite.FavoriteTabPreferenceRepository
import com.example.domain.repository.favorite.FavoriteTripRepository
import com.example.domain.repository.review.ReviewRepository
import com.example.domain.repository.search.RecentSearchRepository
import com.example.domain.repository.survey.SurveyPreferencesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmailVerificationRepository(
        emailVerificationRepositoryImpl: EmailVerificationRepositoryImpl
    ): EmailVerificationRepository

    @Binds
    @Singleton
    abstract fun bindPasswordResetRepository(passwordResetRepositoryImpl: PasswordResetRepositoryImpl): PasswordResetRepository

    @Binds
    @Singleton
    abstract fun bindLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    @Singleton
    abstract fun bindSessionRepository(sessionRepositoryImpl: SessionRepositoryImpl): SessionRepository

    @Binds
    @Singleton
    abstract fun bindSignupRepository(signupRepositoryImpl: SignupRepositoryImpl): SignupRepository

    @Binds
    @Singleton
    abstract fun bindFavoritePlaceRepository(favoritePlaceRepositoryImpl: FavoritePlaceRepositoryImpl): FavoritePlaceRepository

    @Binds
    @Singleton
    abstract fun bindFavoritePostRepository(favoritePostRepositoryImpl: FavoritePostRepositoryImpl): FavoritePostRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteDestinationRepository(
        favoriteDestinationRepositoryImpl: FavoriteDestinationRepositoryImpl
    ): FavoriteDestinationRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteTripRepository(favoriteTripRepositoryImpl: FavoriteTripRepositoryImpl): FavoriteTripRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteTabPreferenceRepository(
        favoriteTabPreferenceRepositoryImpl: FavoriteTabPreferenceRepositoryImpl
    ): FavoriteTabPreferenceRepository

    @Binds
    @Singleton
    abstract fun bindTokenManager(tokenManagerImpl: TokenManagerImpl): TokenManager

    @Binds
    @Singleton
    abstract fun bindLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository

    @Binds
    @Singleton
    abstract fun bindDestinationRepository(destinationsRepositoryImpl: DestinationsRepositoryImpl): DestinationsRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyViewedRepository(recentlyViewedRepositoryImpl: RecentlyViewedRepositoryImpl): RecentlyViewedRepository

    @Binds
    @Singleton
    abstract fun bindRecentSearchRepository(recentSearchRepositoryImpl: RecentSearchRepositoryImpl): RecentSearchRepository

    @Binds
    @Singleton
    abstract fun bindCommunityRepository(communityRepositoryImpl: CommunityRepositoryImpl): CommunityRepository

    @Binds
    @Singleton
    abstract fun bindSurveyPreferencesRepository(
        surveyPreferencesRepositoryImpl: SurveyPreferencesRepositoryImpl
    ): SurveyPreferencesRepository

    @Binds
    @Singleton
    abstract fun bindReviewRepository(reviewRepositoryImpl: ReviewRepositoryImpl): ReviewRepository

    @Binds
    @Singleton
    abstract fun bindTopFlightOffersRepository(
        topFlightOffersRepositoryImpl: com.example.data.repository.flights.TopFlightOffersRepositoryImpl
    ): com.example.domain.repository.flights.TopFlightOffersRepository
}
