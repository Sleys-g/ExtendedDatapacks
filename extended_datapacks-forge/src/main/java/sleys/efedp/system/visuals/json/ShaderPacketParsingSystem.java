package sleys.efedp.system.visuals.json;

import com.google.gson.JsonObject;
import sleys.sl.library.util.data.color.RGB;
import sleys.sl.library.util.io.GsonUtilities;
import sleys.sl.shaders.chains.ShaderEffectList;
import sleys.sl.shaders.data.*;

import java.util.Locale;

public class ShaderPacketParsingSystem {
    public static IShaderParameters tryToGetSealedShaderPacket(JsonObject object, String value) {
        return switch ((ShaderEffectList.valueOf(value.toUpperCase(Locale.ROOT)))) {
            /// Shader I
            case ADVANCED_CHROMATIC_ABERRATION -> tryToParseThisAdvancedChromaticAberration(object);
            case CHROMATIC_ABERRATION -> tryToParseThisChromaticAberration(object);
            case RADIAL_BLUR_OUT -> tryToParseThisRadialBlurOut(object);
            case RADIAL_BLUR_IN -> tryToParseThisRadialBlurIn(object);
            case COLOR_OVERLAY -> tryToParseThisColorOverlay(object);
            case NOISE_OVERLAY -> tryToParseThisNoiseOverlay(object);  /// One-shot
            case IMPACT_FRAME -> tryToParseThisImpactFrame(object);  /// One-shot

            /// Shader II
            case COLORED_IMPACT_FRAME -> tryToParseThisColoredImpactFrame(object); /// One-shot
            case BI_COLOR_OVERLAY -> tryToParseThisBiColorOverlay(object);
            case CRT_FILTER -> tryToParseThisCRTScanFilter(object);
            case PHASE_NOISE -> tryToParseThisPhaseNoise(object);
            case FOCUS_BLUR -> tryToParseThisFocusBlur(object);
            case SHARPEN -> tryToParseThisSharpen(object);
            case GLOW -> tryToParseThisGlow(object);

            /// Shader III
            case COLOR_WAVE_DISTORTION -> tryToParseThisColorWaveDistortion(object);
            case SCREEN_DISTORTION -> tryToParseThisScreenDistortion(object);
            case WAVE_DISTORTION -> tryToParseThisWaveDistortion(object);
            case HUE_ROTATION -> tryToParseThisHueRotation(object);
            case COLOR_GRADE -> tryToParseThisColorGrade(object);
            case POSTERIZE -> tryToParseThisPosterize(object);
            case BLOOM -> tryToParseThisBloom(object);

            default -> null;
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

    /// Shaders III - Parser Methods
    private static ColorWaveDistortionParams tryToParseThisColorWaveDistortion(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);
        float amplitude = GsonUtilities.getAsFloat(obj, "amplitude", 0.01F);
        float frequency = GsonUtilities.getAsFloat(obj, "frequency", 16F);
        float speed = GsonUtilities.getAsFloat(obj, "speed", 0.1F);
        RGB color = GsonUtilities.getAsRGB(obj, "color", RGB.DEFAULT);
        float colorStrength = GsonUtilities.getAsInteger(obj, "colorStrength", 0);

        return new ColorWaveDistortionParams(time_in, time_out, time_hold, amplitude, frequency, speed, color, colorStrength);
    }

    private static ScreenDistortionParams tryToParseThisScreenDistortion(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);
        float amplitude = GsonUtilities.getAsFloat(obj, "amplitude", 0.01F);
        float ringWidth = GsonUtilities.getAsFloat(obj, "ringWidth", 1F);
        float speed = GsonUtilities.getAsFloat(obj, "speed", 1F);

        return new ScreenDistortionParams(time_in, time_out, time_hold, amplitude, ringWidth, speed);
    }

    private static WaveDistortionParams tryToParseThisWaveDistortion(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);
        float amplitude = GsonUtilities.getAsFloat(obj, "amplitude", 0.01F);
        float frequency = GsonUtilities.getAsFloat(obj, "frequency", 16F);
        float speed = GsonUtilities.getAsFloat(obj, "speed", 0.1F);

        return new WaveDistortionParams(time_in, time_out, time_hold, amplitude, frequency, speed);
    }

    private static HueRotationParams tryToParseThisHueRotation(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);
        float base_hue = GsonUtilities.getAsFloat(obj, "base_hue", 0F);
        float speed = GsonUtilities.getAsFloat(obj, "speed", 0F);

        return new HueRotationParams(time_in, time_out, time_hold, base_hue, speed);
    }

    private static ColorGradeParams tryToParseThisColorGrade(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        float saturation = GsonUtilities.getAsFloat(obj, "saturation", 0F);
        float temperature = GsonUtilities.getAsFloat(obj, "temperature", 0F);
        float brightness = GsonUtilities.getAsFloat(obj, "brightness", 1F);
        float contrast = GsonUtilities.getAsFloat(obj, "contrast", 1F);

        return new ColorGradeParams(time_in, time_out, time_hold, temperature, brightness, contrast, saturation);
    }

    private static PosterizeParams tryToParseThisPosterize(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        float levels = GsonUtilities.getAsFloat(obj, "levels", 12F);
        float intensity = GsonUtilities.getAsFloat(obj, "intensity", 0.5F);
        return new PosterizeParams(time_in, time_out, time_hold, levels, intensity);
    }

    private static BloomParams tryToParseThisBloom(JsonObject obj) {
        int time_in = GsonUtilities.getAsInteger(obj, "time_in", 0);
        int time_out = GsonUtilities.getAsInteger(obj, "time_out", 0);
        int time_hold = GsonUtilities.getAsInteger(obj, "time_hold", Integer.MAX_VALUE);

        float intensity = GsonUtilities.getAsFloat(obj, "intensity", 1F);
        float threshold = GsonUtilities.getAsFloat(obj, "threshold", 0F);
        float softness = GsonUtilities.getAsFloat(obj, "softness", 0.4F);
        float radius = GsonUtilities.getAsFloat(obj, "radius", 2F);
        RGB color = GsonUtilities.getAsRGB(obj, "color", RGB.DEFAULT);

        return new BloomParams(time_in, time_out, time_hold, intensity, threshold, softness, radius, color);
    }
}