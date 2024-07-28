package com.hyperring.core
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.hyperring.core.data.mfa.AESMFAChallengeData
import com.hyperring.core.data.mfa.JWTMFAChallengeData
import com.hyperring.core.data.nfc.AESHRData
import com.hyperring.core.data.nfc.JWTHRData
import com.hyperring.core.ui.theme.HyperRingCoreTheme
import com.hyperring.sdk.core.data.HyperRingMFAChallengeInterface
import com.hyperring.sdk.core.data.MFAChallengeResponse
import com.hyperring.sdk.core.mfa.HyperRingMFA
import com.hyperring.sdk.core.nfc.HyperRingNFC
import com.hyperring.sdk.core.nfc.HyperRingTag
import com.hyperring.sdk.core.nfc.NFCStatus
import io.jsonwebtoken.Jwts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.crypto.SecretKey

// Yoonil
import com.hyperring.core.api.IssuanceID
import android.app.AlertDialog
import android.widget.EditText
import android.widget.TextView


/**
 * Demo Application
 */
class MainActivity : ComponentActivity() {
    private lateinit var mainViewModel : MainViewModel

    companion object {
        var mainActivity: ComponentActivity? = null
        val jwtKey: SecretKey = Jwts.SIG.HS256.key().build()
    }

    override fun onResume() {
        mainActivity = this
        super.onResume()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("JWT", "$jwtKey")
        mainActivity = this
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        lifecycleScope.launch {
            // If application is started, init nfc status
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainViewModel.initNFCStatus(this@MainActivity)
            }
        }

        setContent {
            HyperRingCoreTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        NFCBox(context = LocalContext.current, viewModel = mainViewModel)
                        // MFABox()
                        HyperRingIDBox(context = LocalContext.current, viewModel = mainViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun HyperRingIDBox(context: Context, modifier: Modifier = Modifier, viewModel: MainViewModel) {
    Column(modifier = modifier.padding(10.dp)) {
        Box(modifier = modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth()
            .height((250.dp))) {
            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                ) {
                    Text(
                        text = "HyperRing Platform Menu",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp)
                ) {
                    Text(
                        text = "HyperRing ID Issuance and Authentication",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 15.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        requestHyperRingIDIssuangeDialog(context, autoDismiss=true)
                    }) {
                    Text("HyperRing ID Issuance", textAlign = TextAlign.Center)
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        requestMFADialog(autoDismiss=true)
                    }) {
                    Text("HyperRing Authentication Request", textAlign = TextAlign.Center)
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        requestMFADialog(autoDismiss=true)
                    }) {
                    Text("HyperRing Authentication Response", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun MFABox(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(10.dp)) {
        Box(modifier = modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth()
            .height((200.dp))) {
            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                ) {
                    Text(
                        text = "MFA",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(60.dp)
                ) {
                    Text(
                        text = "If HyperRing has Data(id:10), Success.\nNFC Tab -> Writing Mode -> [Write] to Any Tag(data 10)",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 15.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        requestMFADialog()
                    }) {
                    Text("Open requestAuthPage(autoDismiss=false)", textAlign = TextAlign.Center)
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        requestMFADialog(autoDismiss=true)
                    }) {
                    Text("Open requestAuthPage(autoDismiss=true)", textAlign = TextAlign.Center)
                }
            }
        }
    }
}

@Composable
fun NFCBox(context: Context, modifier: Modifier = Modifier, viewModel: MainViewModel) {
    Column(modifier = modifier.padding(10.dp)) {
        Box(modifier = modifier
            .background(Color.LightGray)
            .padding(10.dp)
            .fillMaxWidth()
            .height((340.dp))) {
            Column(
                modifier = modifier
                    .align(Alignment.TopCenter)
            ) {
                Box(modifier = modifier
                    .fillMaxWidth()
                    .height(40.dp)
                ) {
                    Text(
                        text = "NFC",
                        modifier = modifier.fillMaxWidth(),
                        style = TextStyle(fontSize = 22.sp),
                        textAlign = TextAlign.Center,
                    )
                }
                FilledTonalButton(
                    modifier = modifier.fillMaxWidth(),
                    onClick = {
                        checkAvailable(context, viewModel)
                    }) {
                    Text("isAvailable(): ${viewModel.uiState.collectAsState().value.nfcStatus.name}", textAlign = TextAlign.Center)
                }
                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
                Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            onClick = {
                                togglePolling(context, viewModel, viewModel.uiState.value.isPolling)
                            }) {
                            Text("[readHyperRing]\n" + "isPolling: ${viewModel.uiState.collectAsState().value.isPolling}", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            onClick = {
                                toggleNFCMode(viewModel)
                            }
                        ) {
                            Text("[HyperRing] ${if(viewModel.uiState.collectAsState().value.isWriteMode)"Writing Mode" else "Reading Mode"}", textAlign = TextAlign.Center)
                        }
                    }
                }
                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
                    if(!viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(
                                viewModel.uiState.collectAsState().value.dateType == "AES"
                                && viewModel.uiState.collectAsState().value.targetReadId == 10L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel, "AES")
                                setReadTargetId(viewModel, 10)
                            }) {
                            Text("[Read-AES] Read to only ID-10 TAG", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(
                                viewModel.uiState.collectAsState().value.dateType == "AES"
                                && viewModel.uiState.collectAsState().value.targetReadId == null)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel, "AES")
                                setReadTargetId(viewModel, null)
                            }
                        ) {
                            Text("[Read-AES] Read to Any HyperRing TAG", textAlign = TextAlign.Center)
                        }
                    }
                }

                Box(
                    modifier = modifier
                        .background(Color.LightGray)
                        .height(10.dp)
                        .fillMaxWidth())
                if(!viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(
                                viewModel.uiState.collectAsState().value.dateType == "JWT"
                                && viewModel.uiState.collectAsState().value.targetReadId == null)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel, "JWT")
                                setReadTargetId(viewModel, null)
                            }
                        ) {
                            Text("[Read-JWT] Read to Any HyperRing TAG", textAlign = TextAlign.Center)
                        }
                    }
                    Box(modifier = modifier
                        .background(Color.LightGray)
                        .width(10.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                    }
                }
                if(viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if (
                                viewModel.uiState.collectAsState().value.dateType == "JWT")
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel,"JWT")
                                setWriteTargetId(viewModel, null, 10)
                            }
                        ) {
                            Text("[Write-JWT] to Any TAG(data id:10, data: John Doe)", textAlign = TextAlign.Center)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if(viewModel.uiState.collectAsState().value.dateType == "AES"
                                && viewModel.uiState.collectAsState().value.targetWriteId == 10L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel,"AES")
                                setWriteTargetId(viewModel, 10, 10)
                            }) {
                            Text("[Write-AES] to ID-10 TAG", textAlign = TextAlign.Center)
                        }
                    }
                }
                if(viewModel.uiState.collectAsState().value.isWriteMode) Row() {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)
                    ) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if (
                                viewModel.uiState.collectAsState().value.dateType == "AES"
                                && viewModel.uiState.collectAsState().value.targetWriteId == null
                                && viewModel.uiState.collectAsState().value.dataTagId == 10L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel,"AES")
                                setWriteTargetId(viewModel, null, 10)
                            }
                        ) {
                            Text("[Write-AES] to Any TAG(data 10)", textAlign = TextAlign.Center)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .wrapContentWidth(Alignment.Start)
                    ) {
                        FilledTonalButton(
                            modifier = modifier.fillMaxWidth(),
                            colors = if (
                                viewModel.uiState.collectAsState().value.dateType == "AES"
                                && viewModel.uiState.collectAsState().value.targetWriteId == null
                                && viewModel.uiState.collectAsState().value.dataTagId == 15L)
                                ButtonDefaults.filledTonalButtonColors(containerColor = Color.Green)
                            else ButtonDefaults.outlinedButtonColors(),
                            onClick = {
                                setDataType(viewModel,"AES")
                                setWriteTargetId(viewModel, null, 15)
                            }
                        ) {
                            Text("[Write-AES] to Any TAG(data 15)", textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

fun requestMFADialog(autoDismiss: Boolean=false) {
    if(MainActivity.mainActivity != null) {
        val mfaData: MutableList<HyperRingMFAChallengeInterface> = mutableListOf()
        // Custom Challenge
        // AES Type
        mfaData.add(AESMFAChallengeData(10, "dIW6SbrLx+dfb2ckLIMwDOScxw/4RggwXMPnrFSZikA\u003d\n", null))
        // JWT Type
        mfaData.add(JWTMFAChallengeData(15, "John Doe", null, MainActivity.jwtKey))
        HyperRingMFA.initializeHyperRingMFA(mfaData= mfaData.toList())

        fun onDiscovered(dialog: Dialog?, response: MFAChallengeResponse?) {
            Log.d("MainActivity", "requestMFADialog result: ${response}}")
            HyperRingMFA.verifyHyperRingMFAAuthentication(response).let {
                showToast(MainActivity.mainActivity!!, if(it) "Success" else "Failed")
                if(it && autoDismiss) {
                    dialog?.dismiss()
                }
            }
        }

        HyperRingMFA.requestHyperRingMFAAuthentication(
            activity = MainActivity.mainActivity!!,
            onNFCDiscovered = ::onDiscovered,
            autoDismiss = autoDismiss)
    }
}

fun requestHyperRingIDIssuangeDialog(context: Context, autoDismiss: Boolean = false) {
    if (MainActivity.mainActivity != null) {
            val mfaData: MutableList<HyperRingMFAChallengeInterface> = mutableListOf()
            // Custom Challenge
            // AES Type
            mfaData.add(AESMFAChallengeData(10, "dIW6SbrLx+dfb2ckLIMwDOScxw/4RggwXMPnrFSZikA\u003d\n", null))
            // JWT Type
            mfaData.add(JWTMFAChallengeData(15, "John Doe", null, MainActivity.jwtKey))
            HyperRingMFA.initializeHyperRingMFA(mfaData = mfaData.toList())

            fun onDiscovered(dialog: Dialog?, response: MFAChallengeResponse?) {
                Log.d("MainActivity", "requestMFADialog result: ${response}}")
                HyperRingMFA.verifyHyperRingMFAAuthentication(response).let {
                    showToast(MainActivity.mainActivity!!, if (it) "Success" else "Failed")
                    if (it && autoDismiss) {
                        dialog?.dismiss()
                    }
                }
            }

            // Create an EditText
            val input = EditText(context).apply {
                hint = "(Optional) John Doe"
            }

            // Create a dialog with the EditText
            val dialog = AlertDialog.Builder(context)
                .setTitle("HyperRing ID Issuance")
                .setMessage("1. Tag your HyperRing\n 2. Please input your nickname only if you want")
                .setView(input)
                .setPositiveButton("Submit") { dialog, _ ->
                    val userInput = input.text.toString()
                    Log.d("MainActivity", "User input: $userInput")

                    val apiReqIssuanceID = IssuanceID()
                    apiReqIssuanceID.req { responseBody ->
                            Log.d("[YOONIL]", "User input: $responseBody")
                        // Set the responseBody text to the EditText view
                        //input.setText(responseBody)

                        // Show the responseBody in an AlertDialog
                        (context as Activity).runOnUiThread {
                            AlertDialog.Builder(context)
                                .setTitle("Response")
                                .setMessage(responseBody)
                                .setPositiveButton("OK", null)
                                .show()
                            }
                    }


                    /*
                    val userInput = input.text.toString()
                    // Use the user input if necessary
                    Log.d("MainActivity", "User input: $userInput")

                    // Proceed with MFA authentication
                    HyperRingMFA.requestHyperRingMFAAuthentication(
                        activity = MainActivity.mainActivity!!,
                        onNFCDiscovered = ::onDiscovered,
                        autoDismiss = autoDismiss
                    )
                    */
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.cancel()
                }
                .create()

            dialog.show()
/*
        // Get the positive button from the dialog
        val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)

// Set a custom click listener for the positive button
        positiveButton.setOnClickListener {
            val userInput = input.text.toString()
            Log.d("MainActivity", "User input: $userInput")
            val apiReqIssuanceID = IssuanceID()
            apiReqIssuanceID.req { responseBody ->
                Log.d("[YOONIL]", "User input: $responseBody")
                // Set the responseBody text to the EditText view
                input.setText(responseBody)
            }
 */

            /*
            val userInput = input.text.toString()
            // Use the user input if necessary
            Log.d("MainActivity", "User input: $userInput")

            // Proceed with MFA authentication
            HyperRingMFA.requestHyperRingMFAAuthentication(
                activity = MainActivity.mainActivity!!,
                onNFCDiscovered = ::onDiscovered,
                autoDismiss = autoDismiss
            )
            */

    }
}


fun setReadTargetId(viewModel: MainViewModel, id: Long?) {
    viewModel.setReadTargetId(id)
}

fun setWriteTargetId(viewModel: MainViewModel, id: Long?, dataId: Long) {
    viewModel.setWriteTargetId(id, dataId)
}

fun setDataType(viewModel: MainViewModel, dataType: String) {
    viewModel.setDataType(dataType)
}

fun toggleNFCMode(viewModel: MainViewModel) {
    viewModel.toggleNFCMode()
}

fun togglePolling(context: Context, viewModel: MainViewModel, isPolling: Boolean) {
    if(isPolling) {
        stopPolling(context, viewModel)
    } else {
        startPolling(context, viewModel)
    }
}

fun startPolling(context: Context, viewModel: MainViewModel) {
    viewModel.startPolling(context)
}

fun stopPolling(context: Context, viewModel: MainViewModel) {
    viewModel.stopPolling(context)
}

fun checkAvailable(context: Context, viewModel: MainViewModel) {
    viewModel.initNFCStatus(context)
}

private fun showToast(context: Context, text: String) {
    Log.d("MainActivity", "text: $text")
    val handler = Handler(Looper.getMainLooper())
    handler.postDelayed({
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show() }, 0)
}

data class MainUiState(
    // UI state flags
    val nfcStatus: NFCStatus = NFCStatus.NFC_UNSUPPORTED,
    val isPolling: Boolean = false,
    var isWriteMode: Boolean = false,
    var targetWriteId: Long? = null,
    var targetReadId: Long? = null,
    var dataTagId: Long? = 10,
    val dateType: String = "AES"
) {

}

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun initNFCStatus(context: Context) {
        // init HyperRingNFC
        HyperRingNFC.initializeHyperRingNFC(context).let {
            _uiState.update { currentState ->
                currentState.copy(
                    nfcStatus = HyperRingNFC.getNFCStatus(),
                )
            }
        }
    }

    private fun onDiscovered(hyperRingTag: HyperRingTag) : HyperRingTag {
        if(_uiState.value.isWriteMode) {
            /// Writing Data to Any HyperRing NFC TAG
            val isWrite = HyperRingNFC.write(uiState.value.targetWriteId, hyperRingTag,
                // Default HyperRingData
//                HyperRingData.createData(10, mutableMapOf("age" to 25, "name" to "홍길동")))
                // Demo custom Data
                if(_uiState.value.dateType == "AES") AESHRData.createData(uiState.value.dataTagId?:10, "Jenny Doe")
                else JWTHRData.createData(10, "John Doe", MainActivity.jwtKey)
            )

            if(isWrite && MainActivity.mainActivity != null)
                showToast(MainActivity.mainActivity!!, "[write] Success [${uiState.value.dataTagId}]")
        } else {
            if(hyperRingTag.isHyperRingTag()) {
                Log.d("MainActivity", "hyperRingTag.data: ${hyperRingTag.data}")
                val readTag: HyperRingTag? = HyperRingNFC.read(uiState.value.targetReadId, hyperRingTag)
                if(readTag != null) {
                    if(MainActivity.mainActivity != null) showToast(MainActivity.mainActivity!!, "[read]${hyperRingTag.id}")
                    if(_uiState.value.dateType == "AES") {
                        val demoNFCData = AESHRData(readTag.id, readTag.data.data)
                        Log.d("MainActivity", "[READ-AES] : ${demoNFCData.data} / ${demoNFCData.decrypt(demoNFCData.data)}")
                    } else {
                        val demoNFCData = JWTHRData(readTag.id, readTag.data.data, MainActivity.jwtKey)
                        Log.d("MainActivity", "[READ-JWT]1 : ${demoNFCData.data} / ${demoNFCData.decrypt(demoNFCData.data)}")
                    }
                }
            }
        }
        return hyperRingTag
    }

    fun startPolling(context: Context) {
        HyperRingNFC.startNFCTagPolling(
            context as Activity, onDiscovered = :: onDiscovered).let {
            _uiState.update { currentState ->
                currentState.copy(
                    isPolling = HyperRingNFC.isPolling
                )
            }
        }
    }

    fun stopPolling(context: Context) {
        HyperRingNFC.stopNFCTagPolling(context as Activity).let {
            _uiState.update { currentState ->
                currentState.copy(
                    isPolling = HyperRingNFC.isPolling
                )
            }
        }
    }

    fun toggleNFCMode() {
        _uiState.update { currentState ->
            currentState.copy(
                isWriteMode = !_uiState.value.isWriteMode
            )
        }
    }

    fun setDataType(dataType: String) {
        _uiState.update { currentState ->
            currentState.copy(
                dateType = dataType
            )
        }
    }

    fun setReadTargetId(id: Long?) {
        _uiState.update { currentState ->
            currentState.copy(
                targetReadId = id
            )
        }
    }

    fun setWriteTargetId(id: Long?, dataId: Long) {
        _uiState.update { currentState ->
            currentState.copy(
                targetWriteId = id,
                dataTagId = dataId
            )
        }
    }
}