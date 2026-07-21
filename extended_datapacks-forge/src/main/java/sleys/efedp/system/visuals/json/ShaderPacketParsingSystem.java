package sleys.efedp.system.visuals.json;

import com.google.gson.JsonObject;
import sleys.sl.library.util.color.RGB;
import sleys.sl.library.util.file.GsonUtilities;
import sleys.sl.shaders.chains.ShaderEffectList;
import sleys.sl.shaders.data.*;

import java.util.Locale;

public class ShaderPacketParsingSystem {
    public static IShaderParameters tryToGetSealedShaderPacket(JsonObject object, String value) {
        return switch ((ShaderEffectList.valueOf(value.toUpperCase(Locale.ROOT)))) {
            /// Shader I
            case COLOR_OVERLAY -> tryToParseThisColorOverlay(object);
            case IMPACT_FRAME -> tryToParseThisImpactFrame(object);  /// One-shot
            case NOISE_OVERLAY -> tryToParseThisNoiseOverlay(object);  /// One-shot
            case RADIAL_BLUR_IN -> tryToParseThisRadialBlurIn(object);
            case RADIAL_BLUR_OUT -> tryToParseThisRadialBlurOut(object);
            case CHROMATIC_ABERRATION -> tryToParseThisChromaticAberration(object);
            case ADVANCED_CHROMATIC_ABERRATION -> tryToParseThisAdvancedChromaticAberration(object);

            /// Shader II
            case BI_COLOR_OVERLAY -> tryToParseThisBiColorOverlay(object);
            case FOCUS_BLUR -> tryToParseThisFocusBlur(object);
            case CRT_FILTER -> tryToParseThisCRTScanFilter(object);
            case GLOW -> tryToParseThisGlow(object);
            case COLORED_IMPACT_FRAME -> tryToParseThisColoredImpactFrame(object); /// One-shot
            case PHASE_NOISE -> tryToParseThisPhaseNoise(object);
            case SHARPEN -> tryToParseThisSharpen(object);
        };
    }

    /// Shaders I - Parser Methods
    private static ColorOverlayParams tryToParseThisColorOverlay(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);
        var color = GsonUtilities.getAsRGB(obj, "color", RGB.DEFAULT);

        return new ColorOverlayParams(time_in, time_out, time_hold, intensity, color);
    }

    private static ImpactFrameParams tryToParseThisImpactFrame(JsonObject obj) {
        var radius = GsonUtilities.getAsFloat(obj, "radius", 20F);
        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 1F);

        var atLook = GsonUtilities.getAsBoolean(obj, "atLook", false);
        var useAberration = GsonUtilities.getAsBoolean(obj, "useAberration", false);

        return new ImpactFrameParams(0, 8, 2, intensity, 1, useAberration, radius, atLook);
    }

    private static NoiseOverlayParams tryToParseThisNoiseOverlay(JsonObject obj) {
        var hold = GsonUtilities.getAsInteger(obj, "time_hold", 0);
        var scale = GsonUtilities.getAsFloat(obj, "scale", 0F);
        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);

        var color = GsonUtilities.getAsRGB(obj, "color", RGB.DEFAULT);

        return new NoiseOverlayParams(hold, scale, intensity, color);
    }

    private static RadialBlurInParams tryToParseThisRadialBlurIn(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var samples = GsonUtilities.getAsInteger(obj, "samples", 12);
        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);

        return new RadialBlurInParams(time_in, time_out, time_hold, samples, intensity);
    }

    private static RadialBlurOutParams tryToParseThisRadialBlurOut(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var samples = GsonUtilities.getAsInteger(obj, "samples", 12);
        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);

        return new RadialBlurOutParams(time_in, time_out, time_hold, samples, intensity);
    }

    private static ChromaticAberrationParams tryToParseThisChromaticAberration(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);

        return new ChromaticAberrationParams(time_in, time_out, time_hold, intensity);
    }

    private static AdvancedChromaticAberrationParams tryToParseThisAdvancedChromaticAberration(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);
        var radius = GsonUtilities.getAsFloat(obj, "radius", 1F);

        return new AdvancedChromaticAberrationParams(time_in, time_out, time_hold, intensity, radius);
    }

    /// Shaders II - Parser Methods
    private static BiColorOverlayParams tryToParseThisBiColorOverlay(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);
        var contrast = GsonUtilities.getAsFloat(obj, "contrast", 1F);

        var darkColor = GsonUtilities.getAsRGB(obj, "dark_color", RGB.DEFAULT);
        var lightColor = GsonUtilities.getAsRGB(obj, "light_color", RGB.DEFAULT);

        return new BiColorOverlayParams(time_in, time_out, time_hold, intensity, contrast, darkColor, lightColor);
    }

    private static FocusBlurParams tryToParseThisFocusBlur(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 1F);
        var radius = GsonUtilities.getAsFloat(obj, "radius", 0.25F);
        var fall_off = GsonUtilities.getAsFloat(obj, "fall_off", 0F);
        var max_blur = GsonUtilities.getAsFloat(obj, "max_blur", 0F);

        return new FocusBlurParams(time_in, time_out, time_hold, intensity, radius, fall_off, max_blur);
    }

    private static CRTScanFilterParams tryToParseThisCRTScanFilter(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var lineWidth = GsonUtilities.getAsFloat(obj, "line_width", 1F);
        var darkFactor = GsonUtilities.getAsFloat(obj, "dark_factor", 0.25F);
        var distortion = GsonUtilities.getAsFloat(obj, "distortion", 0F);

        return new CRTScanFilterParams(time_in, time_out, time_hold, lineWidth, darkFactor, distortion);
    }

    private static GlowParams tryToParseThisGlow(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);
        var threshold = GsonUtilities.getAsFloat(obj, "threshold", 1F);
        var color = GsonUtilities.getAsRGB(obj, "color", RGB.DEFAULT);

        return new GlowParams(time_in, time_out, time_hold, intensity, threshold, color);
    }

    private static ColoredImpactFrameParams tryToParseThisColoredImpactFrame(JsonObject obj) {
        var radius = GsonUtilities.getAsFloat(obj, "radius", 20F);
        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 1F);

        var dark_color = GsonUtilities.getAsRGB(obj, "dark_color", RGB.DEFAULT);
        var light_color = GsonUtilities.getAsRGB(obj, "light_color", RGB.DEFAULT);

        var contrast = GsonUtilities.getAsFloat(obj, "contrast", 1F);
        var atLook = GsonUtilities.getAsBoolean(obj, "atLook", false);

        var useAberration = GsonUtilities.getAsBoolean(obj, "useAberration", false);

        return new ColoredImpactFrameParams(0, 8, 2, intensity, 1,
                dark_color, light_color, contrast, useAberration, radius, atLook
        );
    }

    private static PhaseNoiseParams tryToParseThisPhaseNoise(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var intensity = GsonUtilities.getAsFloat(obj, "intensity", 0F);

        return new PhaseNoiseParams(time_in, time_out, time_hold, intensity);
    }

    private static SharpenParams tryToParseThisSharpen(JsonObject obj) {
        var time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        var time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        var time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        var amount = GsonUtilities.getAsFloat(obj, "amount", 1F);
        var radius = GsonUtilities.getAsFloat(obj, "radius", 1F);
        var threshold = GsonUtilities.getAsFloat(obj, "threshold", 0F);

        return new SharpenParams(time_in, time_out, time_hold, amount, radius, threshold);
    }
}