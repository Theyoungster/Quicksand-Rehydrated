package net.mokai.quicksandrehydrated.client;

public class EngulfClient {
    private static boolean engulfed = false;
    private static int struggleProgress = 0;
    private static float wobbleTime = 0f;
    private static float intensity = 0f;

    public static void setEngulfedClient(boolean value, int progress) {
        engulfed = value;
        struggleProgress = value ? progress : 0;
        if (!value) {
            intensity = 0f;
        }
    }

    public static boolean isEngulfed() {
        return engulfed;
    }

    public static int getStruggleProgress() {
        return struggleProgress;
    }

    public static void tickClient() {
        float dt = 1f / 20f;
        if (engulfed) {
            wobbleTime += dt;
            intensity = Math.min(1f, intensity + 0.04f);
        } else {
            intensity = Math.max(0f, intensity - 0.08f);
        }
    }

    public static float getPitchOffset() {
        return (float) (Math.sin(wobbleTime * 3.2) * 1.5f * intensity);
    }

    public static float getRollOffset() {
        return (float) (Math.sin((wobbleTime * 2.6) + 1.2) * 1.2f * intensity);
    }
}
