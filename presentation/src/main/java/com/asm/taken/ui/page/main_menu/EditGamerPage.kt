package com.asm.taken.ui.page.main_menu

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.asm.taken.vm.EditGamerVM

@Composable
fun EditGamerPage(
    gamerId: String,
    snackBarHostState: SnackbarHostState,
    editGamerVM: EditGamerVM
) {
    BackgroundMainSection {
        EditGamerSection(editGamerVM)
    }
}

@Composable
fun EditGamerSection(editGamerVM: EditGamerVM) {


    PanelEditGamerForm()

}

@Composable
fun PanelEditGamerForm() {

}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun EditGamerPagePreview() {

}