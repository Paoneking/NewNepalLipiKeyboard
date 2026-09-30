// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.compose.foundation.shape.RoundedCornerShape
import com.paoneking.nepallipikeyboard.latin.BuildConfig
import com.paoneking.nepallipikeyboard.latin.R
import com.paoneking.nepallipikeyboard.latin.utils.Theme
import com.paoneking.nepallipikeyboard.latin.utils.previewDark
import com.paoneking.nepallipikeyboard.settings.SearchSettingsScreen
import com.paoneking.nepallipikeyboard.settings.SettingsActivity
import com.paoneking.nepallipikeyboard.settings.SettingsContainer

/**
 * Who makes this keyboard, and why.
 *
 * The narrative paragraphs are deliberately held in English here rather than in
 * strings.xml: they are brand copy supplied by Callijatra, word for word the same
 * text the Callijatra app ships, and pushing them into 117 locale folders would
 * land as untranslated English in every one of them anyway. Everything authored
 * for this screen rather than by Callijatra -- headings, the funding label, the
 * screen title -- is a string resource. "Global Greengrants Fund" stays a literal
 * because it is an organisation's name, not a phrase to translate.
 */
@Composable
fun AboutCallijatraScreen(
    onClickBack: () -> Unit,
) {
    SearchSettingsScreen(
        onClickBack = onClickBack,
        title = stringResource(R.string.settings_screen_about_callijatra),
        settings = emptyList(),
    ) {
        Scaffold(contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Bottom)) { innerPadding ->
        Column(
            Modifier
                .verticalScroll(rememberScrollState())
                .then(Modifier.padding(innerPadding))
                .padding(start = 24.dp, top = 8.dp, end = 24.dp, bottom = 40.dp)
        ) {
            Logo()

            Body(
                "Callijatra Foundation is a youth-led initiative dedicated to the " +
                    "revival of the Ranjana script, Nepal Lipi, and the Nepalbhasa " +
                    "language, with a mission to preserve Nepal’s rich cultural " +
                    "heritage. By blending traditional practices with modern digital " +
                    "innovation, Callijatra bridges the past and the present — " +
                    "ensuring that ancient scripts, languages, and artistic traditions " +
                    "remain accessible and relevant in today’s digital age."
            )
            Body(
                "The initiative brings together a community of artists, calligraphers, " +
                    "designers, developers, teachers, and cultural enthusiasts, " +
                    "fostering learning through workshops, creative collaborations, " +
                    "calligraphy challenges, educational content, mobile applications, " +
                    "fonts, and public exhibitions."
            )
            Pull(
                "The word “Jatra” means festival in Nepali — " +
                    "Callijatra is a festival of calligraphy."
            )
            Body(
                "Founded in 2017 as a creative challenge inviting participants to submit " +
                    "calligraphy in any script on a given theme, Callijatra has grown " +
                    "into a national movement — through Lipi mobile apps, " +
                    "script-based digital fonts, calligraphy tools, video tutorials and " +
                    "online courses, books and learning materials, and workshops, live " +
                    "calligraphy, exhibitions and cultural events."
            )

            Heading(stringResource(R.string.about_callijatra_heading_keyboard))
            Body(
                "Nepal Lipi Keyboard is one of those Lipi tools — a way to type " +
                    "Ranjana script, Nepal Lipi and Nepalbhasa on an ordinary phone, " +
                    "with layouts, dictionaries and transliteration for scripts that " +
                    "most keyboards have never supported. Writing a script every day is " +
                    "what keeps it alive; this is meant to make that ordinary."
            )

            Heading(stringResource(R.string.about_callijatra_heading_mission))
            Body(
                "To preserve, promote, and revitalize Nepal’s indigenous scripts " +
                    "and the Nepalbhasa language, so that these traditions remain:"
            )
            Point("relevant in modern society")
            Point("accessible through digital tools and learning resources")
            Point("actively practiced, taught, and celebrated")
            Point("sustainable for future generations")
            Body(
                "By supporting artisans, calligraphers, and educators with platforms " +
                    "and opportunities, the Foundation also contributes to sustainable " +
                    "livelihoods within cultural and creative industries."
            )

            Heading(stringResource(R.string.about_callijatra_heading_how))
            Point(
                "Hands-on workshops on Ranjana script, Nepal Lipi and traditional " +
                    "writing systems, and training for youth, students and educators " +
                    "to learn and teach them"
            )
            Point(
                "Lipi mobile apps, script fonts, calligraphy tools and practice " +
                    "materials for self-paced learning"
            )
            Point(
                "Calligraphy challenges, exhibitions, festivals and public " +
                    "demonstrations, giving local artisans a place to teach, earn and " +
                    "show their work"
            )
            Point(
                "Books, practice guides and educational texts, and the documenting of " +
                    "traditional scripts in print and digitally"
            )
            Point(
                "Partnerships with schools, cultural institutions and libraries, and " +
                    "mentorship between script experts and new learners"
            )

            Heading(stringResource(R.string.about_callijatra_heading_tools))
            Body(
                "Open tools, fonts, apps and learning resources for Nepal’s " +
                    "indigenous scripts, collected in one place."
            )
            Link("Lipi tools", "callijatra.github.io", "https://callijatra.github.io/")

            Heading(stringResource(R.string.about_callijatra_heading_follow))
            Link("Instagram", "@callijatra", "https://www.instagram.com/callijatra")
            Link("Facebook", "/callijatra", "https://www.facebook.com/callijatra")
            Link("YouTube", "@callijatra", "https://www.youtube.com/@callijatra")

            Footer()
        }
        }
    }
}

@Composable
private fun Logo() {
    Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), Alignment.Center) {
        Image(
            painter = painterResource(R.drawable.callijatra_foundation),
            // The mark ships as an alpha mask with no colour of its own, so it
            // takes the theme's ink and stays legible on light and dark alike.
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(230.dp),
            contentDescription = "Callijatra Foundation",
        )
    }
}

@Composable
private fun Heading(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = 17.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 26.dp, bottom = 10.dp),
    )
}

@Composable
private fun Body(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        modifier = Modifier.padding(bottom = 14.dp),
    )
}

/// The one line worth setting apart: what the name actually means.
@Composable
private fun Pull(text: String) {
    Row(
        Modifier
            .padding(bottom = 16.dp)
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            fontStyle = FontStyle.Italic,
            modifier = Modifier.padding(start = 14.dp, top = 12.dp, end = 14.dp, bottom = 12.dp),
        )
    }
}

@Composable
private fun Point(text: String) {
    Row(Modifier.padding(bottom = 10.dp)) {
        Box(
            Modifier
                .padding(top = 7.dp, end = 10.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.primary)
        )
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp,
            lineHeight = 21.sp,
        )
    }
}

@Composable
private fun Link(label: String, handle: String, url: String) {
    val ctx = LocalContext.current
    val shape: Shape = RoundedCornerShape(4.dp)
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth()
            .heightIn(min = 44.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, SolidColor(MaterialTheme.colorScheme.outlineVariant), shape)
            .clickable {
                try {
                    ctx.startActivity(
                        Intent(Intent.ACTION_VIEW, url.toUri())
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                } catch (_: ActivityNotFoundException) {
                    // Only reachable on a device with nothing registered for
                    // http(s), but a crash from an about-page link would be absurd.
                }
            }
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
            painter = painterResource(R.drawable.external_link),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp),
        )
    }
}

@Composable
private fun Footer() {
    Column(
        Modifier.padding(top = 28.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Text(
            text = stringResource(R.string.english_ime_name),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            text = stringResource(R.string.version_text, BuildConfig.VERSION_NAME),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
        Text(
            text = stringResource(R.string.about_callijatra_supported_by),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 26.dp),
        )
        Text(
            text = "Global Greengrants Fund",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Preview
@Composable
private fun Preview() {
    SettingsActivity.settingsContainer = SettingsContainer(LocalContext.current)
    Theme(previewDark) {
        Surface {
            AboutCallijatraScreen { }
        }
    }
}
