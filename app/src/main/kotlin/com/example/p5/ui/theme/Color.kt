package com.example.p5.ui.theme

import androidx.compose.ui.graphics.Color

// ============================================================================
// OBSIDIAN BASE PALETTE (Deep OLED & Luxury Dark Foundations)
// ============================================================================
val ObsidianBlack = Color(0xFF07080A)           // Ultra-deep rich OLED black
val ObsidianBackground = Color(0xFF090B0E)      // Base background tone
val ObsidianSurface = Color(0xFF0F1217)         // Primary surface container
val ObsidianSurfaceVariant = Color(0xFF161A22)  // Slightly elevated surface
val ObsidianCard = Color(0xFF13171F)            // Distinct card background
val ObsidianCardHigh = Color(0xFF1A1F2C)        // High-elevation card
val ObsidianFloating = Color(0xEB0C0F14)        // Floating dock / island background (92% alpha)

// ============================================================================
// TRANSLUCENT GLASS TOKENS (Glassmorphism Surfaces & Specular Borders)
// ============================================================================
val GlassFillSubtle = Color(0x0AFFFFFF)          // ~4% white tint
val GlassFillLight = Color(0x14FFFFFF)           // ~8% white tint
val GlassFillMedium = Color(0x1FFFFFFF)          // ~12% white tint
val GlassFillStrong = Color(0x2BFFFFFF)          // ~17% white tint
val GlassFillSolid = Color(0x3DFFFFFF)           // ~24% white tint

val GlassBorderSubtle = Color(0x1FFFFFFF)        // ~12% white subtle border
val GlassBorderMedium = Color(0x33FFFFFF)        // ~20% white medium border
val GlassBorderBright = Color(0x59FFFFFF)        // ~35% specular reflection highlight

val GlassSurfaceDark = Color(0xCC0D1016)         // 80% translucent dark obsidian glass

// ============================================================================
// AURORA & TOSCA ACCENTS (Modern Electric Tosca & Ambient Glows)
// ============================================================================
val ToscaPrimary = Color(0xFF00E5BF)             // Vibrant electric Tosca Green
val ToscaAccent = Color(0xFF00F5D4)              // Radiant high-luminance neon Tosca
val ToscaLight = Color(0xFF70FFDF)               // Soft radiant mint
val ToscaDark = Color(0xFF00A88B)                // Deep rich emerald Tosca
val ToscaContainer = Color(0xFF042922)           // Deep dark Tosca container

val ToscaGlow = Color(0x3D00F5D4)                // Ambient radial glow (24%)
val ToscaGlowSubtle = Color(0x1800F5D4)          // Soft ambient glow (9%)
val ToscaGlassBorder = Color(0x6600F5D4)         // Glowing Tosca glass border (40%)

// Aurora depth accents (for subtle ambient gradients & player aurora mesh)
val AuroraCyan = Color(0xFF00D4FF)
val AuroraMint = Color(0xFF5FF281)
val AuroraDeepIndigo = Color(0xFF181534)

// ============================================================================
// TYPOGRAPHY & NEUTRALS
// ============================================================================
val TextPrimary = Color(0xFFF8FAFC)              // Slate 50 - Ultra crisp
val TextSecondary = Color(0xFF94A3B8)            // Slate 400 - Clear readable subtext
val TextTertiary = Color(0xFF64748B)             // Slate 500 - Meta details
val TextMuted = Color(0xFF475569)                // Slate 600 - Timestamps & subtle icons
val DividerColor = Color(0x1AFFFFFF)             // 10% translucent divider

// ============================================================================
// BACKWARD-COMPATIBILITY ALIASES
// ============================================================================
val BlackBackground = ObsidianBackground
val DarkSurface = ObsidianSurface
val DarkSurfaceVariant = ObsidianSurfaceVariant

val OrangePrimary = ToscaPrimary
val OrangeLight = ToscaLight
val OrangeDark = ToscaDark
val OrangeContainer = ToscaContainer
