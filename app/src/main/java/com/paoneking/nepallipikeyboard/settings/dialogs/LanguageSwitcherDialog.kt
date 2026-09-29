package com.paoneking.nepallipikeyboard.settings.dialogs

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paoneking.nepallipikeyboard.latin.LatinIME
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.RichInputMethodManager
import com.paoneking.nepallipikeyboard.latin.utils.SubtypeLocaleUtils.displayName
import com.paoneking.nepallipikeyboard.latin.utils.SubtypeSettings
import com.paoneking.nepallipikeyboard.settings.SettingsActivity

@Composable
fun LanguageSwitcherDialog(
    latinIme: LatinIME,
    richImm: RichInputMethodManager,
    onDismiss: () -> Unit = { },
) {
    val context = LocalContext.current
    val pm = latinIme.packageManager
    val thisImi = richImm.inputMethodInfoOfThisIme
    val currentSubtype = richImm.currentSubtype.rawSubtype
    val enabledImis = richImm.inputMethodManager.enabledInputMethodList
        .sortedBy { it.hashCode() }.sortedBy { it.loadLabel(pm).toString() } // first label, then hashCode
    /*val otherEnableSubtypes = enabledImis.filter { it.packageName != latinIme.packageName }
    val otherEnableImeSubtypePairs = mutableListOf<Pair<InputMethodInfo, InputMethodSubtype?>>()
    otherEnableSubtypes.forEach { imi ->
        val subtypes = if (imi != thisImi) richImm.getEnabledInputMethodSubtypes(imi, true)
        else richImm.getEnabledInputMethodSubtypes(imi, true).sortedBy { it.displayName() }
        if (subtypes.isEmpty()) {
            otherEnableImeSubtypePairs.add(imi to null)
        } else {
            subtypes.forEach {
                if (!it.isAuxiliary) {
                    otherEnableImeSubtypePairs.add(imi to it)
                }
            }
        }
    }*/
    val ownEnableSubtypes = SubtypeSettings.getEnabledSubtypes()
    val otherEnableSubtypes = enabledImis.filter { it.packageName != latinIme.packageName }
    val state = rememberLazyListState()
    LaunchedEffect(Unit) {
        val index = ownEnableSubtypes.indexOf(currentSubtype)
        if (index > 0) state.scrollToItem(maxOf(0, index - 5))  // instant, centers item
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column {
            Text(
                stringResource(R.string.select_input_method),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Medium, fontSize = 20.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(0.dp, 16.dp)
            )

            LazyColumn(state = state, modifier = Modifier.weight(1.0f)) {
                items(ownEnableSubtypes) { subtype ->
                    val selected = currentSubtype == subtype
                    val subtypeName = subtype.displayName()
                    val title = subtypeName.ifBlank { thisImi.loadLabel(pm) } ?: thisImi.loadLabel(pm)
                    val subtitle = "${thisImi.loadLabel(pm)}"

                    val item = @Composable {
                        NavigationItem(
                            title = title.toString(),
                            subtitle = subtitle,
                            style = NavigationItemStyle.MiscNoArrow,
                            navigate = {
                                latinIme.switchToSubtype(subtype)
                                onDismiss()
                            },
                            icon = if (selected) painterResource(R.drawable.check_circle) else painterResource(R.drawable.circle)
                        )
                    }

                    if (selected) {
                        Surface(color = MaterialTheme.colorScheme.primary) {
                            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
                                item()
                            }
                        }
                    } else {
                        item()
                    }
                }

                item {
                    if (ownEnableSubtypes.isNotEmpty() && otherEnableSubtypes.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                    }
                }

                items(otherEnableSubtypes) { imi ->

                    val title = imi.loadLabel(pm)
                    NavigationItem(
                        title = title.toString(),
                        style = NavigationItemStyle.MiscNoArrow,
                        navigate = {
                            latinIme.switchInputMethod(imi.id)
                        },
                        icon = painterResource(R.drawable.circle)
                    )
                }
            }

            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                TextButton(onClick = {
                    onDismiss()
                }) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.weight(1.0f))
                TextButton(onClick = {
                    val inputMethodManager =
                        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.showInputMethodPicker()

                    onDismiss()
                }) {
                    Text("Switch Keyboard")
                }
                TextButton(onClick = {
                    val intent = Intent()
                    intent.setClass(context, SettingsActivity::class.java)
                    intent.setFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    )
                    intent.putExtra("navDest", "languages")
                    context.startActivity(intent)

                    onDismiss()
                }) {
                    Text(stringResource(R.string.settings))
                }
            }
        }
    }
}

@Composable
fun NavigationItem(
    title: String,
    style: NavigationItemStyle,
    navigate: () -> Unit,
    icon: Painter? = null,
    subtitle: String? = null,
) {
    SettingItem(
        title = title,
        subtitle = subtitle,
        onClick = navigate,
        icon = {
            icon?.let {
                when (style) {
                    NavigationItemStyle.HomePrimary -> MaterialTheme.colorScheme.primaryContainer
                    NavigationItemStyle.HomeSecondary -> MaterialTheme.colorScheme.secondaryContainer
                    NavigationItemStyle.HomeTertiary -> MaterialTheme.colorScheme.tertiaryContainer

                    NavigationItemStyle.MiscNoArrow,
                    NavigationItemStyle.Misc,
                    NavigationItemStyle.ExternalLink,
                    NavigationItemStyle.Mail -> Color.Transparent
                }

                val iconColor = when (style) {
                    NavigationItemStyle.HomePrimary -> MaterialTheme.colorScheme.onPrimaryContainer
                    NavigationItemStyle.HomeSecondary -> MaterialTheme.colorScheme.onSecondaryContainer
                    NavigationItemStyle.HomeTertiary -> MaterialTheme.colorScheme.onTertiaryContainer

                    NavigationItemStyle.MiscNoArrow,
                    NavigationItemStyle.Mail,
                    NavigationItemStyle.ExternalLink,
                    NavigationItemStyle.Misc -> LocalContentColor.current.copy(alpha = 0.75f)
                }

                /*Canvas(modifier = Modifier.size(24.dp)) {
                    drawCircle(circleColor, this.size.maxDimension / 2.4f)
                    translate(
                        left = this.size.width / 2.0f - icon.intrinsicSize.width / 2.0f,
                        top = this.size.height / 2.0f - icon.intrinsicSize.height / 2.0f
                    ) {
                        with(icon) {
                            draw(icon.intrinsicSize, colorFilter = ColorFilter.tint(iconColor))
                        }
                    }
                }*/
                Icon(
                    icon,
                    "",
                    Modifier.padding(end = 0.dp),
                    tint = iconColor
                )
            }
        }
    ) {
        when (style) {
            NavigationItemStyle.Misc -> Icon(Icons.Default.ArrowForward, contentDescription = null)
            NavigationItemStyle.Mail -> Icon(Icons.Default.Send, contentDescription = null)
            NavigationItemStyle.ExternalLink -> Icon(painterResource(R.drawable.external_link), contentDescription = null)
            else -> {}
        }
    }
}

enum class NavigationItemStyle {
    HomePrimary,
    HomeSecondary,
    HomeTertiary,
    MiscNoArrow,
    Misc,
    ExternalLink,
    Mail
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    disabled: Boolean = false,
    modifier: Modifier = Modifier,
    subcontent: (@Composable () -> Unit)? = null,
    onSubmenuNavigate: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val textColor = when (LocalContentColor.current) {
        MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.onTertiary -> LocalContentColor.current

        else -> MaterialTheme.colorScheme.onSurface
    }

    val subTextColor = when (textColor) {
        MaterialTheme.colorScheme.onPrimary,
        MaterialTheme.colorScheme.onSecondary,
        MaterialTheme.colorScheme.onTertiary -> textColor.copy(alpha = 0.6f)

        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(
                0.dp, 48.dp
            )
            .let {
                if (onClick != null && onSubmenuNavigate == null) {
                    it.clickable(enabled = !disabled, onClick = {
                        if (!disabled) {
                            onClick()
                        }
                    })
                } else if (onSubmenuNavigate != null) {
                    it.clickable(enabled = !disabled, onClick = {
                        if (!disabled) {
                            onSubmenuNavigate()
                        }
                    })
                } else {
                    it
                }
            }
            .height(intrinsicSize = IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            Modifier
                .weight(1.0f)
                .fillMaxHeight()
                .padding(0.dp, 6.dp)
        ) {
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .width(24.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    if (icon != null) {
                        icon()
                    }
                }
            }

            Spacer(Modifier.width(20.dp))

            Row(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .alpha(
                        if (disabled) {
                            0.5f
                        } else {
                            1.0f
                        }
                    )
            ) {
                SpacedColumn(0.dp) {
                    Text(
                        title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = textColor,
                        modifier = Modifier.heightIn(min = 24.dp)
                    )

                    if (subtitle != null) {
                        Text(
                            subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = subTextColor
                        )
                    } else if (subcontent != null) {
                        subcontent()
                    }
                }
            }
            if (onSubmenuNavigate != null) {
                Spacer(Modifier.width(8.dp))
            }
        }

        if (onSubmenuNavigate != null) {
            VerticalDivider(
                Modifier.height(64.dp),
                color = MaterialTheme.colorScheme.outline
            )
        } else {
            Spacer(Modifier.width(4.dp))
        }

        Row(
            Modifier
                .let {
                    if (onSubmenuNavigate != null && onClick != null) {
                        it.clickable(enabled = !disabled, onClick = {
                            if (!disabled) {
                                onClick()
                            }
                        })
                    } else {
                        it
                    }
                }
                .fillMaxHeight()) {
            if (onSubmenuNavigate != null) {
                Spacer(Modifier.width(8.dp))
            }
            Box(modifier = Modifier.align(Alignment.CenterVertically), contentAlignment = Alignment.Center) {
                content()
            }

            Spacer(modifier = Modifier.width(12.dp))
            Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
fun SpacedColumn(
    gap: Dp,
    modifier: Modifier = Modifier,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(gap), horizontalAlignment = horizontalAlignment) {
        content()
    }
}
