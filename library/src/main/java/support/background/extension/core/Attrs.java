package support.background.extension.core;

class Attrs {
    int strokeWidth;
    int strokeDashWidth;
    int strokeDashGap;
    int strokeColor;
    int strokePressedColor;
    int strokeCheckedColor;
    int strokeDisableColor;

    int shadowColor;
    int shadowRadius;
    int shadowOffsetX;
    int shadowOffsetY;
    int[] shadowPadding = new int[4];
    float[] cornerRadii = new float[8];

    void setCornerRadius(float tlRadius, float trRadius, float blRadius, float brRadius) {
        cornerRadii = new float[]{tlRadius, tlRadius, trRadius, trRadius, brRadius, brRadius, blRadius, blRadius};
    }

    void setStroke(int width, int color, int pressedColor, int checkedColor, int disableColor, int dashWidth, int dashGap) {
        strokeWidth = width;
        strokeColor = color;
        strokePressedColor = pressedColor;
        strokeCheckedColor = checkedColor;
        strokeDisableColor = disableColor;
        strokeDashGap = dashGap;
        strokeDashWidth = dashWidth;
        if (strokeDashGap == 0) strokeDashGap = dashWidth;
    }

    void setShadow(int color, int radius, int offsetX, int offsetY) {
        shadowColor = color;
        shadowRadius = radius;
        shadowOffsetX = offsetX;
        shadowOffsetY = offsetY;
        calculatePadding();
    }

    /**
     * calculate the drawable padding with shadow size
     */
    private void calculatePadding() {
        shadowPadding[0] = shadowRadius / 2 - shadowOffsetX;
        shadowPadding[1] = shadowRadius / 2 - shadowOffsetY;
        shadowPadding[2] = shadowRadius / 2 + shadowOffsetX;
        shadowPadding[3] = shadowRadius / 2 + shadowOffsetY;
        if (shadowPadding[0] < 0) shadowPadding[0] = 0;
        if (shadowPadding[1] < 0) shadowPadding[1] = 0;
        if (shadowPadding[2] < 0) shadowPadding[2] = 0;
        if (shadowPadding[3] < 0) shadowPadding[3] = 0;
    }
}
