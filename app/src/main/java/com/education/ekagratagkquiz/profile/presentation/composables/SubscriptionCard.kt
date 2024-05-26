package com.education.ekagratagkquiz.profile.presentation.composables

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.education.ekagratagkquiz.ChooseSubscription
import com.education.ekagratagkquiz.R
import com.education.ekagratagkquiz.contribute_quiz.domain.model.FirebaseUser
import com.education.ekagratagkquiz.core.data.UserStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionCard(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current
) {
    val TAG = "Subscription"
    val scope = rememberCoroutineScope()
    val store = UserStore(context)
    val user = store.getUserDetail.collectAsState(initial = FirebaseUser())
    val isSubscriptionActive = store.isSubscriptionActive.collectAsState(initial = false)

    val chooseSubscription = remember {
        ChooseSubscription(context as Activity)
    }

    LaunchedEffect(key1 = true) {
        Log.d(TAG, "SubscriptionCard: Setup")
        chooseSubscription.billingSetup()
        chooseSubscription.hasSubscription()
    }

    val currentSubscription by chooseSubscription.subscriptions.collectAsState()


    LaunchedEffect(key1 = currentSubscription) {
        Log.d(TAG, "SubscriptionCard: currentSubscription $currentSubscription")
        if (currentSubscription.contains("ad_free_sub")) {
            //"Purchased"
            scope.launch(Dispatchers.IO) {
                store.saveIsSubscriptionActive(true)
            }
        } else {
            //"Not Purchased"
            // TODO: Comment for only admin
            CoroutineScope(Dispatchers.IO).launch {
                store.saveIsSubscriptionActive(false)
            }
        }
    }


    Card(
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(.65f)
            ) {
                Text(text = "Ad Free Subscription", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.subs_description),
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Button(
                onClick = {
                    // TODO: Uncomment and Comment opposite for only admin
                    //or it can be monthly inspire of egq-01
                    if (!isSubscriptionActive.value){
                        chooseSubscription.checkSubscriptionStatus("egq-01")
                    }
                    else{
                        val link =
                            "https://play.google.com/store/account/subscriptions?sku=" + "egq-01" + "&package=" + context.packageName
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                        context.startActivity(intent)
                    }
//                    CoroutineScope(Dispatchers.IO).launch {
//                        store.saveIsSubscriptionActive(!isSubscriptionActive.value)
//                    }
                },
                modifier = Modifier
                    .weight(.35f)

            ) {
                Text(
                    text = if (!isSubscriptionActive.value) "Buy" else "Cancel",
                )
            }
        }

    }
}