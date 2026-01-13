package com.example.feature.language;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class LocaleModule_ProvideContextFactory implements Factory<Context> {
  private final Provider<Context> contextProvider;

  public LocaleModule_ProvideContextFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public Context get() {
    return provideContext(contextProvider.get());
  }

  public static LocaleModule_ProvideContextFactory create(Provider<Context> contextProvider) {
    return new LocaleModule_ProvideContextFactory(contextProvider);
  }

  public static Context provideContext(Context context) {
    return LocaleModule.INSTANCE.provideContext(context);
  }
}
