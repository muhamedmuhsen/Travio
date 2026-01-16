//package com.example.domain.usecase.auth
//
//import com.example.common.errorhandler.AppError
//import com.example.common.errorhandler.Result
//import com.example.domain.repository.auth.GoogleSignIn
//import javax.inject.Inject
//
//class GoogleLoginUseCase @Inject constructor(private val googleSignIn: GoogleSignIn) {
//    suspend operator fun invoke(webClientId: String): Result<String, AppError>{
//        return googleSignIn.signIn(webClientId)
//    }
//}