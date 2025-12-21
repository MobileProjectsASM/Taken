package com.asm.taken.vm

import androidx.lifecycle.ViewModel
import com.asm.domain.use_cases.GetGamerUC
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditGamerVM @Inject constructor(
    private val getGamerUC: GetGamerUC,
    private val saveGamerUC: GetGamerUC
): ViewModel() {

}