package com.example.feature.language;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class LanguageViewModel_Factory implements Factory<LanguageViewModel> {
  private final Provider<AppLocaleManager> localeManagerProvider;

  public LanguageViewModel_Factory(Provider<AppLocaleManager> localeManagerProvider) {
    this.localeManagerProvider = localeManagerProvider;
  }

  @Override
  public LanguageViewModel get() {
    return newInstance(localeManagerProvider.get());
  }

  public static LanguageViewModel_Factory create(Provider<AppLocaleManager> localeManagerProvider) {
    return new LanguageViewModel_Factory(localeManagerProvider);
  }

  public static LanguageViewModel newInstance(AppLocaleManager localeManager) {
    return new LanguageViewModel(localeManager);
  }
}
