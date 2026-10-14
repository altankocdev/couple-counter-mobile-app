package com.altankoc.couplecounter.presentation.theme

import androidx.compose.ui.graphics.Color
import com.altankoc.couplecounter.domain.model.ThemeId

object ThemePalettes {

    val Nocturne = ThemePalette(
        id = ThemeId("theme_01"),
        name = "Nocturne",
        gradientStops = listOf(
            0.00f to Color(0xFFD4C4A8),
            0.12f to Color(0xFFD4C4A8),
            0.42f to Color(0xFF415A77),
            0.65f to Color(0xFF1B263B),
            1.00f to Color(0xFF0D1B2A)
        ),
        contentTop = Color(0xFF1B263B),
        contentBottom = Color(0xFFF4F1DE),
        contentSecondary = Color(0xFFD4C4A8),
        accent = Color(0xFF778D7A),
        onAccent = Color(0xFFF4F1DE),
        surface = Color(0xFF0D1B2A),
        isTopLight = true,
        isBottomLight = false
    )

    val Velvet = ThemePalette(
        id = ThemeId("theme_02"),
        name = "Velvet",
        gradientStops = listOf(
            0.00f to Color(0xFFFBE4D8),
            0.10f to Color(0xFFDFB6B2),
            0.38f to Color(0xFF854F6C),
            0.58f to Color(0xFF522B5B),
            0.80f to Color(0xFF2B124C),
            1.00f to Color(0xFF190019)
        ),
        contentTop = Color(0xFF2B124C),
        contentBottom = Color(0xFFFBE4D8),
        contentSecondary = Color(0xFFDFB6B2),
        accent = Color(0xFF854F6C),
        onAccent = Color(0xFFFBE4D8),
        surface = Color(0xFF190019),
        isTopLight = true,
        isBottomLight = false
    )

    val Mist = ThemePalette(
        id = ThemeId("theme_03"),
        name = "Mist",
        gradientStops = listOf(
            0.00f to Color(0xFFE9E5E2),
            0.12f to Color(0xFFDCD6D2),
            0.40f to Color(0xFF7A6F74),
            0.60f to Color(0xFF4A4352),
            0.82f to Color(0xFF2A2838),
            1.00f to Color(0xFF0F0D14)
        ),
        contentTop = Color(0xFF2A2838),
        contentBottom = Color(0xFFECE8E5),
        contentSecondary = Color(0xFFC9C1BE),
        accent = Color(0xFF9A8FA0),
        onAccent = Color(0xFF0F0D14),
        surface = Color(0xFF0F0D14),
        isTopLight = true,
        isBottomLight = false
    )

    val Terracotta = ThemePalette(
        id = ThemeId("theme_04"),
        name = "Terracotta",
        gradientStops = listOf(
            0.00f to Color(0xFFE09A8A),
            0.32f to Color(0xFFC86A5A),
            0.52f to Color(0xFF9A4454),
            0.72f to Color(0xFF6D2E3A),
            1.00f to Color(0xFF4B1D27)
        ),
        contentTop = Color(0xFF4B1D27),
        contentBottom = Color(0xFFFDEDE7),
        contentSecondary = Color(0xFFF3C6B6),
        accent = Color(0xFFC86A5A),
        onAccent = Color(0xFFFDEDE7),
        surface = Color(0xFF4B1D27),
        isTopLight = true,
        isBottomLight = false
    )

    val Aurora = ThemePalette(
        id = ThemeId("theme_05"),
        name = "Aurora",
        gradientStops = listOf(
            0.00f to Color(0xFF4C1D3D),
            0.22f to Color(0xFF852E4E),
            0.45f to Color(0xFFA33757),
            0.70f to Color(0xFFC9506A),
            0.88f to Color(0xFFE07278),
            1.00f to Color(0xFFF08F86)
        ),
        contentTop = Color(0xFFFFE8DC),
        contentBottom = Color(0xFFFFF1EA),
        contentSecondary = Color(0xFFFFCFB0),
        accent = Color(0xFFFFBB94),
        onAccent = Color(0xFF4C1D3D),
        surface = Color(0xFF4C1D3D),
        isTopLight = false,
        isBottomLight = false
    )

    val Wisteria = ThemePalette(
        id = ThemeId("theme_06"),
        name = "Wisteria",
        gradientStops = listOf(
            0.00f to Color(0xFFF4EFF3),
            0.12f to Color(0xFFDCD3E0),
            0.30f to Color(0xFFB7AFC9),
            0.46f to Color(0xFF8A82A6),
            0.66f to Color(0xFF585272),
            1.00f to Color(0xFF3A3550)
        ),
        contentTop = Color(0xFF3A3550),
        contentBottom = Color(0xFFF4EFF3),
        contentSecondary = Color(0xFFDCD3E0),
        accent = Color(0xFF8A82A6),
        onAccent = Color(0xFFF4EFF3),
        surface = Color(0xFF3A3550),
        isTopLight = true,
        isBottomLight = false
    )

    val Dusk = ThemePalette(
        id = ThemeId("theme_07"),
        name = "Dusk",
        gradientStops = listOf(
            0.00f to Color(0xFFA8303F),
            0.12f to Color(0xFFC25A5A),
            0.26f to Color(0xFFF0A285),
            0.48f to Color(0xFF8E5C72),
            0.70f to Color(0xFF4A4466),
            1.00f to Color(0xFF242E49)
        ),
        contentTop = Color(0xFFFFE7DA),
        contentBottom = Color(0xFFFFE7DA),
        contentSecondary = Color(0xFFFDA481),
        accent = Color(0xFFD9485A),
        onAccent = Color(0xFFFFF1EA),
        surface = Color(0xFF242E49),
        isTopLight = false,
        isBottomLight = false
    )

    val Bordeaux = ThemePalette(
        id = ThemeId("theme_08"),
        name = "Bordeaux",
        gradientStops = listOf(
            0.00f to Color(0xFFB65C72),
            0.30f to Color(0xFF8E2B44),
            0.52f to Color(0xFF6D1D32),
            0.75f to Color(0xFF4B0F1E),
            1.00f to Color(0xFF2A0A12)
        ),
        contentTop = Color(0xFFF7D6DC),
        contentBottom = Color(0xFFF7D6DC),
        contentSecondary = Color(0xFFE07A94),
        accent = Color(0xFFCC5671),
        onAccent = Color(0xFFFFF0F3),
        surface = Color(0xFF2A0A12),
        isTopLight = false,
        isBottomLight = false
    )

    val Aqua = ThemePalette(
        id = ThemeId("theme_09"),
        name = "Aqua",
        gradientStops = listOf(
            0.00f to Color(0xFF213A58),
            0.22f to Color(0xFF0C6478),
            0.42f to Color(0xFF15919B),
            0.62f to Color(0xFF09D1C7),
            0.82f to Color(0xFF46DFB1),
            1.00f to Color(0xFF80EE98)
        ),
        contentTop = Color(0xFFE6FFF0),
        contentBottom = Color(0xFF0F2E44),
        contentSecondary = Color(0xFF0C6478),
        accent = Color(0xFF15919B),
        onAccent = Color(0xFFFFFFFF),
        surface = Color(0xFF80EE98),
        isTopLight = false,
        isBottomLight = true
    )

    val Pastel = ThemePalette(
        id = ThemeId("theme_10"),
        name = "Pastel",
        gradientStops = listOf(
            0.00f to Color(0xFFC8D9D6),
            0.22f to Color(0xFFA6BDCC),
            0.42f to Color(0xFFA0A6CC),
            0.58f to Color(0xFFB3A3CA),
            0.74f to Color(0xFFD3A8C0),
            0.88f to Color(0xFFE6BAA9),
            1.00f to Color(0xFFF0D6C2)
        ),
        contentTop = Color(0xFF4A3F6B),
        contentBottom = Color(0xFF4A3F6B),
        contentSecondary = Color(0xFF8C6E9E),
        accent = Color(0xFF6A5BA8),
        onAccent = Color(0xFFFFFFFF),
        surface = Color(0xFFF0D6C2),
        isTopLight = true,
        isBottomLight = true
    )

    val Nimbus = ThemePalette(
        id = ThemeId("theme_11"),
        name = "Nimbus",
        gradientStops = listOf(
            0.00f to Color(0xFFF9D6FC),
            0.25f to Color(0xFFE3C4F5),
            0.50f to Color(0xFFC8B6F5),
            0.75f to Color(0xFFBAC0FA),
            1.00f to Color(0xFFBDCFFF)
        ),
        contentTop = Color(0xFF3D3A6B),
        contentBottom = Color(0xFF3D3A6B),
        contentSecondary = Color(0xFF6B66B0),
        accent = Color(0xFF8A6FD6),
        onAccent = Color(0xFFFFFFFF),
        surface = Color(0xFFBDCFFF),
        isTopLight = true,
        isBottomLight = true
    )

    val Silver = ThemePalette(
        id = ThemeId("theme_12"),
        name = "Silver",
        gradientStops = listOf(
            0.00f to Color(0xFF868686),
            0.35f to Color(0xFFA6A6A6),
            0.65f to Color(0xFFC4C4C4),
            0.85f to Color(0xFFDCDCDC),
            1.00f to Color(0xFFF4F4F4)
        ),
        contentTop = Color(0xFFF7F7F7),
        contentBottom = Color(0xFF2B2B2B),
        contentSecondary = Color(0xFF5E5E5E),
        accent = Color(0xFF4A4A4A),
        onAccent = Color(0xFFF4F4F4),
        surface = Color(0xFFF4F4F4),
        isTopLight = false,
        isBottomLight = true
    )

    val Azur = ThemePalette(
        id = ThemeId("theme_13"),
        name = "Azur",
        gradientStops = listOf(
            0.00f to Color(0xFFC1E8FF),
            0.14f to Color(0xFFA9CBEB),
            0.38f to Color(0xFF7DA0CA),
            0.56f to Color(0xFF5483B3),
            0.78f to Color(0xFF052659),
            1.00f to Color(0xFF021024)
        ),
        contentTop = Color(0xFF052659),
        contentBottom = Color(0xFFEAF6FF),
        contentSecondary = Color(0xFF9CC4E8),
        accent = Color(0xFF7DA0CA),
        onAccent = Color(0xFF052659),
        surface = Color(0xFF021024),
        isTopLight = true,
        isBottomLight = false
    )

    val Default = Nocturne

    val all: List<ThemePalette> = listOf(
        Nocturne,
        Velvet,
        Mist,
        Terracotta,
        Aurora,
        Wisteria,
        Dusk,
        Bordeaux,
        Aqua,
        Pastel,
        Nimbus,
        Silver,
        Azur
    )

    fun byId(id: ThemeId?): ThemePalette = all.firstOrNull { it.id == id } ?: Default
}