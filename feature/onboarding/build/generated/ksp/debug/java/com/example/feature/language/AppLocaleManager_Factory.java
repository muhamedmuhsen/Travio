package com.example.feature.language;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppLocaleManager_Factory implements Factory<AppLocaleManager> {
  private final Provider<Context> contextProvider;

  public AppLocaleManager_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public AppLocaleManager get() {
    return newInstance(contextProvider.get());
  }

  public static AppLocaleManager_Factory create(Provider<Context> contextProvider) {
    return new AppLocaleManager_Factory(contextProvider);
  }

  public static AppLocaleManager newInstance(Context context) {
    return new AppLocaleManager(context);
  }
}
