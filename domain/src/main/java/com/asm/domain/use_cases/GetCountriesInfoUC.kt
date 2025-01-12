package com.asm.domain.use_cases

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralErrorType
import com.asm.domain.errors.GeneralFailure
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetCountriesInfoUC @Inject constructor(
    private val logger: Logger,
    private val countryInfoRepository: CountryInfoRepository
): UseCaseSync<Result<List<CountryInfo>, GeneralFailure>, Unit>() {

    companion object {
        const val TAG = "GetCountriesCallCodeUC"
    }

    override suspend fun run(params: Unit): Result<List<CountryInfo>, GeneralFailure> {
        return try {
            val resultCountriesInfo = countryInfoRepository.getCountriesInfoSortedByName()
            if (resultCountriesInfo is Result.Unsuccessful) return resultCountriesInfo
            val countriesInfo = resultCountriesInfo.asSuccessful().data
            if (countriesInfo.isNotEmpty()) return resultCountriesInfo
            val downloadCountriesResult = countryInfoRepository.downloadCountriesInfo()
            if (downloadCountriesResult is Result.Unsuccessful) return downloadCountriesResult
            countryInfoRepository.getCountriesInfoSortedByName()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralFailure.OtherError(GeneralErrorType.UNKNOWN))
        }
    }
}