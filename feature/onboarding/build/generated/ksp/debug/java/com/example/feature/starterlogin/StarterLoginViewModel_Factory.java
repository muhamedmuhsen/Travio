package com.example.feature.starterlogin;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class StarterLoginViewModel_Factory implements Factory<StarterLoginViewModel> {
  @Override
  public StarterLoginViewModel get() {
    return newInstance();
  }

  public static StarterLoginViewModel_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StarterLoginViewModel newInstance() {
    return new StarterLoginViewModel();
  }

  private static final class InstanceHolder {
    private static final StarterLoginViewModel_Factory INSTANCE = new StarterLoginViewModel_Factory();
  }
}
