package com.paoneking.nepallipikeyboard.settings.dialogs

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.paoneking.nepallipikeyboard.latin.LatinIME
import com.paoneking.nepallipikeyboard.latin.RichInputMethodManager
import com.paoneking.nepallipikeyboard.latin.utils.Theme

// This ugly workaround is required as Android Compose freaks out when you use a Dialog outside of
// an activity (i.e. in an input method service)
data class DialogComposeView(
    val dialog: Dialog,
    val composeView: ComposeView
)

fun DialogComposeView.show() {
    dialog.show()
}

fun DialogComposeView.dismiss() {
    dialog.dismiss()
}

fun DialogComposeView.isShowing(): Boolean = dialog.isShowing

fun createDialogComposeView(
    latinIME: LatinIME,
    maxWidthProportion: Float = 0.9f,
    maxHeightProportion: Float = 0.75f,
    dimAmount: Float = 0.5f,
    onDismiss: () -> Unit = { },
    content: @Composable (Dialog) -> Unit,
): DialogComposeView {
    val context: Context = latinIME

    val composeView = ComposeView(context)

    val dialog = Dialog(context).apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(composeView)

        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        setOnDismissListener {
            onDismiss()
        }
    }

    val window = dialog.window
    window?.addFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    window?.attributes?.token = latinIME.windowToken
    window?.attributes?.type = WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG

    window?.apply {
        val displayMetrics = context.resources.displayMetrics
        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        val maxWidth = (width * maxWidthProportion).toInt()
        val maxHeight = (height * maxHeightProportion).toInt()

        setLayout(maxWidth, maxHeight)
        setGravity(Gravity.CENTER)

        setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
//        setBackgroundDrawable(AppCompatResources.getDrawable(context, R.drawable.empty))
        setDimAmount(dimAmount)
    }

    composeView.setViewTreeLifecycleOwner(latinIME)
    composeView.setViewTreeSavedStateRegistryOwner(latinIME)

    composeView.setContent { content(dialog) }

    return DialogComposeView(dialog, composeView)
}

object DialogComposeViewUtils {
    @JvmStatic
    fun createLanguageSwitcherDialog(latinIME: LatinIME): DialogComposeView {
        return createDialogComposeView(latinIME, maxHeightProportion = 0.85f) { dialog ->
            Theme {
                LanguageSwitcherDialog(
                    latinIme = latinIME,
                    richImm = RichInputMethodManager.getInstance(),
                    onDismiss = { dialog.dismiss() },
                )
            }
        }
    }

    @JvmStatic
    fun createTransliterationHelpDialog(latinIME: LatinIME, title: String, helpText: String): DialogComposeView {
        return createDialogComposeView(latinIME) { dialog ->
            Theme {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column(modifier = Modifier.padding(16.dp).fillMaxHeight()) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Box(modifier = Modifier.weight(1f)) {
                            Text(
                                text = helpText,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .verticalScroll(rememberScrollState())
                                    .padding(bottom = 8.dp)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(onClick = { dialog.dismiss() }) {
                                Text(stringResource(android.R.string.ok))
                            }
                        }
                    }
                }
            }
        }
    }
}
