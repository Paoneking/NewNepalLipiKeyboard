// SPDX-License-Identifier: GPL-3.0-only
package com.paoneking.nepallipikeyboard.settings.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
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
 * for this screen rather than by Callijatra -- headings and the screen title -- is
 * a string resource. Who supports the work is on [AboutAppScreen].
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

            AboutBody(
                "Callijatra Foundation is a youth-led initiative dedicated to the " +
                    "revival of the Ranjana script, Nepal Lipi, and the Nepalbhasa " +
                    "language, with a mission to preserve Nepal’s rich cultural " +
                    "heritage. By blending traditional practices with modern digital " +
                    "innovation, Callijatra bridges the past and the present — " +
                    "ensuring that ancient scripts, languages, and artistic traditions " +
                    "remain accessible and relevant in today’s digital age."
            )
            AboutBody(
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
            AboutBody(
                "Founded in 2017 as a creative challenge inviting participants to submit " +
                    "calligraphy in any script on a given theme, Callijatra has grown " +
                    "into a national movement — through Lipi mobile apps, " +
                    "script-based digital fonts, calligraphy tools, video tutorials and " +
                    "online courses, books and learning materials, and workshops, live " +
                    "calligraphy, exhibitions and cultural events."
            )

            AboutHeading(stringResource(R.string.about_callijatra_heading_keyboard))
            AboutBody(
                "Nepal Lipi Keyboard is one of those Lipi tools — a way to type " +
                    "Ranjana script, Nepal Lipi and Nepalbhasa on an ordinary phone, " +
                    "with layouts, dictionaries and transliteration for scripts that " +
                    "most keyboards have never supported. Writing a script every day is " +
                    "what keeps it alive; this is meant to make that ordinary."
            )

            AboutHeading(stringResource(R.string.about_callijatra_heading_mission))
            AboutBody(
                "To preserve, promote, and revitalize Nepal’s indigenous scripts " +
                    "and the Nepalbhasa language, so that these traditions remain:"
            )
            Point("relevant in modern society")
            Point("accessible through digital tools and learning resources")
            Point("actively practiced, taught, and celebrated")
            Point("sustainable for future generations")
            AboutBody(
                "By supporting artisans, calligraphers, and educators with platforms " +
                    "and opportunities, the Foundation also contributes to sustainable " +
                    "livelihoods within cultural and creative industries."
            )

            AboutHeading(stringResource(R.string.about_callijatra_heading_how))
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

            AboutHeading(stringResource(R.string.about_callijatra_heading_tools))
            AboutBody(
                "Open tools, fonts, apps and learning resources for Nepal’s " +
                    "indigenous scripts, collected in one place."
            )
            AboutLink("Lipi tools", "callijatra.github.io", "https://callijatra.github.io/")

            AboutHeading(stringResource(R.string.about_callijatra_heading_follow))
            AboutLink("Instagram", "@callijatra", "https://www.instagram.com/callijatra")
            AboutLink("Facebook", "/callijatra", "https://www.facebook.com/callijatra")
            AboutLink("YouTube", "@callijatra", "https://www.youtube.com/@callijatra")

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
