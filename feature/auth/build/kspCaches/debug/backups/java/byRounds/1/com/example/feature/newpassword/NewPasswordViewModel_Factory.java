package com.example.feature.newpassword;

import com.example.domain.usecase.auth.ResetPasswordUseCase;
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
public final class NewPasswordViewModel_Factory implements Factory<NewPasswordViewModel> {
  private final Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider;

  public NewPasswordViewModel_Factory(Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider) {
    this.resetPasswordUseCaseProvider = resetPasswordUseCaseProvider;
  }

  @Override
  public NewPasswordViewModel get() {
    return newInstance(resetPasswordUseCaseProvider.get());
  }

  public static NewPasswordViewModel_Factory create(
      Provider<ResetPasswordUseCase> resetPasswordUseCaseProvider) {
    return new NewPasswordViewModel_Factory(resetPasswordUseCaseProvider);
  }

  public static NewPasswordViewModel newInstance(ResetPasswordUseCase resetPasswordUseCase) {
    return new NewPasswordViewModel(resetPasswordUseCase);
  }
}
