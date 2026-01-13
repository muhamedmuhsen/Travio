package com.example.feature.login;

import com.example.data.local.datastore.CredentialsManager;
import com.example.data.local.datastore.PreferencesManager;
import com.example.domain.usecase.auth.GoogleLoginUseCase;
import com.example.domain.usecase.auth.GoogleSignInUseCase;
import com.example.domain.usecase.auth.LoginUseCase;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<LoginUseCase> loginUseCaseProvider;

  private final Provider<GoogleSignInUseCase> googleSignInUseCaseProvider;

  private final Provider<GoogleLoginUseCase> googleLoginUseCaseProvider;

  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<CredentialsManager> credentialsManagerProvider;

  public LoginViewModel_Factory(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<GoogleSignInUseCase> googleSignInUseCaseProvider,
      Provider<GoogleLoginUseCase> googleLoginUseCaseProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<CredentialsManager> credentialsManagerProvider) {
    this.loginUseCaseProvider = loginUseCaseProvider;
    this.googleSignInUseCaseProvider = googleSignInUseCaseProvider;
    this.googleLoginUseCaseProvider = googleLoginUseCaseProvider;
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.credentialsManagerProvider = credentialsManagerProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(loginUseCaseProvider.get(), googleSignInUseCaseProvider.get(), googleLoginUseCaseProvider.get(), preferencesManagerProvider.get(), credentialsManagerProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<LoginUseCase> loginUseCaseProvider,
      Provider<GoogleSignInUseCase> googleSignInUseCaseProvider,
      Provider<GoogleLoginUseCase> googleLoginUseCaseProvider,
      Provider<PreferencesManager> preferencesManagerProvider,
      Provider<CredentialsManager> credentialsManagerProvider) {
    return new LoginViewModel_Factory(loginUseCaseProvider, googleSignInUseCaseProvider, googleLoginUseCaseProvider, preferencesManagerProvider, credentialsManagerProvider);
  }

  public static LoginViewModel newInstance(LoginUseCase loginUseCase,
      GoogleSignInUseCase googleSignInUseCase, GoogleLoginUseCase googleLoginUseCase,
      PreferencesManager preferencesManager, CredentialsManager credentialsManager) {
    return new LoginViewModel(loginUseCase, googleSignInUseCase, googleLoginUseCase, preferencesManager, credentialsManager);
  }
}
