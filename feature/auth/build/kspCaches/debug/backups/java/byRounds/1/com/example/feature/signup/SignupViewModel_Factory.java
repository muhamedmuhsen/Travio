package com.example.feature.signup;

import com.example.domain.usecase.auth.SignupUseCase;
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
public final class SignupViewModel_Factory implements Factory<SignupViewModel> {
  private final Provider<SignupUseCase> signupUseCaseProvider;

  public SignupViewModel_Factory(Provider<SignupUseCase> signupUseCaseProvider) {
    this.signupUseCaseProvider = signupUseCaseProvider;
  }

  @Override
  public SignupViewModel get() {
    return newInstance(signupUseCaseProvider.get());
  }

  public static SignupViewModel_Factory create(Provider<SignupUseCase> signupUseCaseProvider) {
    return new SignupViewModel_Factory(signupUseCaseProvider);
  }

  public static SignupViewModel newInstance(SignupUseCase signupUseCase) {
    return new SignupViewModel(signupUseCase);
  }
}
