// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.paoneking.nepallipikeyboard.latin.R

// The type shared by About this app and About Callijatra, so the two pages read as one set.

@Composable
internal fun AboutHeading(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 26.dp, bottom = 10.dp),
    )
}

@Composable
internal fun AboutBody(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        modifier = Modifier.padding(bottom = 14.dp),
    )
}

/** A row that opens [url] in the browser. */
@Composable
internal fun AboutLink(label: String, handle: String, url: String) {
    val ctx = LocalContext.current
    AboutRow(label, handle, R.drawable.external_link, onClick = {
        try {
            ctx.startActivity(Intent(Intent.ACTION_VIEW, url.toUri()).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (_: ActivityNotFoundException) {
            // Only reachable on a device with nothing registered for http(s),
            // but a crash from an about-page link would be absurd.
        }
    })
}

/** A row that opens another settings page. */
@Composable
internal fun AboutPageLink(label: String, handle: String, onClick: () -> Unit) {
    // the settings' own next-screen chevron: the left arrow, mirrored unless the layout is RTL
    AboutRow(label, handle, R.drawable.ic_arrow_left, onClick, mirrorIcon = LocalLayoutDirection.current == LayoutDirection.Ltr)
}

@Composable
private fun AboutRow(label: String, handle: String, icon: Int, onClick: () -> Unit, mirrorIcon: Boolean = false) {
    val shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, SolidColor(MaterialTheme.colorScheme.outlineVariant), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = handle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 13.sp,
            modifier = Modifier.padding(start = 10.dp).weight(1f),
        )
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp).then(if (mirrorIcon) Modifier.scale(-1f, 1f) else Modifier),
        )
    }
}
