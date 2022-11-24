package net.messer.mystical_index.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.messer.mystical_index.MysticalIndex;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.Map;

public abstract class ItemCirclesRenderer<T> extends DrawableHelper {
    public static final Identifier CIRCLES_TEXTURE = MysticalIndex.id("textures/gui/circles.png");
    public static final int CIRCLE_TEXTURES_SIZE = 256;
    public static final Map<Integer, Identifier> CIRCLE_TEXTURES = Map.of(
            24, MysticalIndex.id("textures/gui/circle_24.png"),
            48, MysticalIndex.id("textures/gui/circle_48.png")
    );
    public static final int SECONDARY_CIRCLE_ITEM_COUNT = 7;
    public static final int TERNARY_CIRCLE_ITEM_COUNT = 19;

    public void render(double x, double y, double z, MatrixStack matrices, List<T> stacks) {
        if (stacks.isEmpty()) return;

        var primary = stacks.get(0);
        var secondary = stacks.size() > 1 ?
                stacks.subList(1, Math.min(SECONDARY_CIRCLE_ITEM_COUNT, stacks.size())) : null;
        var ternary = stacks.size() > SECONDARY_CIRCLE_ITEM_COUNT ?
                stacks.subList(SECONDARY_CIRCLE_ITEM_COUNT, Math.min(TERNARY_CIRCLE_ITEM_COUNT, stacks.size())) : null;

        if (secondary != null) drawItemCircle(matrices, x, y, z, 24, secondary);
        if (ternary != null) drawItemCircle(matrices, x, y, z, 48, ternary);
        drawItemCircle(matrices, x, y, z, true);
        drawItem(matrices, x, y, z, primary);
    }

    private void drawItemCircle(MatrixStack matrices, double x, double y, double z, int radius, List<T> items) {
        var itemCount = items.size();
        var circleTexture = CIRCLE_TEXTURES.get(radius);

        RenderSystem.setShaderTexture(0, circleTexture);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();

        drawTexture(
                matrices,
                (int) x - CIRCLE_TEXTURES_SIZE / 2, (int) y - CIRCLE_TEXTURES_SIZE / 2,
                0, 0, CIRCLE_TEXTURES_SIZE, CIRCLE_TEXTURES_SIZE
        );

        for (int i = 0; i < itemCount; i++) {
            var stack = items.get(i);
            var offset = (2 * Math.PI) / itemCount * i - (Math.PI / 2);

            var itemX = (int) x + (radius * Math.cos(offset));
            var itemY = (int) y + (radius * Math.sin(offset));

            drawItemCircle(matrices, itemX, itemY, z, false);
            drawItem(matrices, itemX, itemY, z, stack);
        }
    }

    protected abstract void drawItemCircle(MatrixStack matrices, double x, double y, double z, boolean isPrimary);

    protected abstract void drawItem(MatrixStack matrices, double x, double y, double z, T item);
}