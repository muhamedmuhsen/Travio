package com.example.feature.code;

import com.example.domain.usecase.auth.SendVerificationCodeUseCase;
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
public final class CodeViewModel_Factory implements Factory<CodeViewModel> {
  private final Provider<SendVerificationCodeUseCase> verificationCodeUseCaseProvider;

  public CodeViewModel_Factory(
      Provider<SendVerificationCodeUseCase> verificationCodeUseCaseProvider) {
    this.verificationCodeUseCaseProvider = verificationCodeUseCaseProvider;
  }

  @Override
  public CodeViewModel get() {
    return newInstance(verificationCodeUseCaseProvider.get());
  }

  public static CodeViewModel_Factory create(
      Provider<SendVerificationCodeUseCase> verificationCodeUseCaseProvider) {
    return new CodeViewModel_Factory(verificationCodeUseCaseProvider);
  }

  public static CodeViewModel newInstance(SendVerificationCodeUseCase verificationCodeUseCase) {
    return new CodeViewModel(verificationCodeUseCase);
  }
}
