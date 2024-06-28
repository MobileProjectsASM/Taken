package com.asm.domain.use_cases

import com.asm.domain.entities.CountryCallCode
import com.asm.domain.entities.Result
import com.asm.domain.entities.toFailure
import com.asm.domain.errors.Error
import com.asm.domain.repositories.CountryRepository
import com.asm.domain.use_cases.base.UseCaseSync
import com.asm.domain.utils.Logger
import javax.inject.Inject

class GetCountriesCallCodeUC @Inject constructor(
    private val logger: Logger,
    private val countryRepository: CountryRepository
): UseCaseSync<List<CountryCallCode>, Unit>() {

    companion object {
        const val TAG = "GetCountriesCallCodeUC"
    }

    override suspend fun run(params: Unit): Result<List<CountryCallCode>> {
        return try {
            countryRepository.getCountriesCallCode()
        } catch (exception: Exception) {
            logger.logE(TAG, exception)
            Error.UnknownError.toFailure()
        }
    }
}