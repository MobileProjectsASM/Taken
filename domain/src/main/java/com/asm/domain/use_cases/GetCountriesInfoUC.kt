package com.asm.domain.use_cases

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.errors.GeneralError
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetCountriesInfoUC @Inject constructor(
    private val logger: Logger,
    private val countryInfoRepository: CountryInfoRepository
): UseCaseSync<Result<List<CountryInfo>, GeneralError>, Unit>() {

    companion object {
        const val TAG = "GetCountriesCallCodeUC"
    }

    override suspend fun run(params: Unit): Result<List<CountryInfo>, GeneralError> {
        return try {
            val resultCountriesInfo = countryInfoRepository.getCountriesInfoSortedByName()
            val countriesInfo = when (resultCountriesInfo) {
                is Result.Successful<List<CountryInfo>> -> resultCountriesInfo.data
                is Result.Unsuccessful<GeneralError> -> return resultCountriesInfo
            }
            if (countriesInfo.isNotEmpty()) return resultCountriesInfo
            when (val downloadCountriesResult = countryInfoRepository.downloadCountriesInfo()) {
                is Result.Successful<Unit> -> countryInfoRepository.getCountriesInfoSortedByName()
                is Result.Unsuccessful<GeneralError> -> return downloadCountriesResult
            }
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Result.Unsuccessful(GeneralError.Unknown)
        }
    }
}