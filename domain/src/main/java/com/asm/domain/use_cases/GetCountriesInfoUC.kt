package com.asm.domain.use_cases

import com.asm.domain.entities.CountryInfo
import com.asm.domain.entities.Result
import com.asm.domain.entities.asSuccessful
import com.asm.domain.entities.toFailure
import com.asm.domain.entities.toSuccessful
import com.asm.domain.errors.Failure
import com.asm.domain.repositories.CountryInfoRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetCountriesInfoUC @Inject constructor(
    private val logger: Logger,
    private val countryInfoRepository: CountryInfoRepository
): UseCaseSync<List<CountryInfo>, Unit>() {

    companion object {
        const val TAG = "GetCountriesCallCodeUC"
    }

    override suspend fun run(params: Unit): Result<List<CountryInfo>> {
        return try {
            val resultCountriesInfo = countryInfoRepository.getCountriesInfoSortedByName()
            if (resultCountriesInfo.isFailure) return resultCountriesInfo
            val countriesInfo = resultCountriesInfo.asSuccessful().data
            if (countriesInfo.isNotEmpty()) return countriesInfo.toSuccessful()
            countryInfoRepository.downloadCountriesInfo()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Failure.UnknownFailure.toFailure()
        }
    }
}