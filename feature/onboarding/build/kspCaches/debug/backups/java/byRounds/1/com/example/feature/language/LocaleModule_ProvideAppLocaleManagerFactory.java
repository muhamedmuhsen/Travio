package com.example.feature.language;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class LocaleModule_ProvideAppLocaleManagerFactory implements Factory<AppLocaleManager> {
  private final Provider<Context> contextProvider;

  public LocaleModule_ProvideAppLocaleManagerFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppLocaleManager get() {
    return provideAppLocaleManager(contextProvider.get());
  }

  public static LocaleModule_ProvideAppLocaleManagerFactory create(
      Provider<Context> contextProvider) {
    return new LocaleModule_ProvideAppLocaleManagerFactory(contextProvider);
  }

  public static AppLocaleManager provideAppLocaleManager(Context context) {
    return Preconditions.checkNotNullFromProvides(LocaleModule.INSTANCE.provideAppLocaleManager(context));
  }
}
