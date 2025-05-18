package com.asm.taken.ui.page.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.asm.taken.R
import com.asm.taken.ui.PuzzleGeneralTitle
import com.asm.taken.utils.MessageResolver
import com.asm.taken.vm.LoginVM

@Composable
fun CreateAccountPage(
    loginVM: LoginVM,
    messageResolver: MessageResolver
) {
    PanelCreateAccount(
        loginVM = loginVM,
        messageResolver = messageResolver
    ) { email, password ->

    }
}

@Composable
fun PanelCreateAccount(
    loginVM: LoginVM,
    messageResolver: MessageResolver,
    createAccount: (String, String) -> Unit
) {
    Column(
       modifier = Modifier
           .fillMaxWidth()
           .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(250.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(vertical = 15.dp, horizontal = 10.dp)
            ) {
                PuzzleGeneralTitle(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(id = R.string.txt_ttl_form_create_account)
                )
                Spacer(modifier = Modifier.height(50.dp))

            }
        }
        Box(modifier = Modifier.height(250.dp))
    }
}