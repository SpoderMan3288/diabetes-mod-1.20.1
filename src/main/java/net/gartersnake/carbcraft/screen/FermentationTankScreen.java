package net.gartersnake.carbcraft.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.gartersnake.carbcraft.CarbCraft;
import net.gartersnake.carbcraft.screen.renderer.FluidStackRenderer;
import net.gartersnake.carbcraft.util.FluidStack;
import net.gartersnake.carbcraft.util.MouseUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Optional;

public class FermentationTankScreen extends HandledScreen<FermentationTankScreenHandler> {
    public static final Identifier TEXTURE =
            new Identifier(CarbCraft.MOD_ID, "textures/gui/fermentation_tank_gui.png");
    private FluidStackRenderer fluidStackRenderer;
    private final long maxCapacity = FluidConstants.DROPLET * 100;

    public FermentationTankScreen(FermentationTankScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;

        assignFluidStackRenderer();
    }

    private void assignFluidStackRenderer() {
        fluidStackRenderer = new FluidStackRenderer(maxCapacity, true, 15, 52);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        if (handler.isExtracting()) {
            switch (handler.operation()) {
                case 1 : renderRightArrow(context, x, y); renderSyringeSlot(context, x, y); break;
                case 2 : renderDownArrow(context, x, y); break;
                case 3 : renderLeftArrow(context, x, y); break;
            }
        } else {
            renderSyringeSlot(context, x, y);
        }

        fluidStackRenderer.renderInsulinTank(context, handler.fluidStack, maxCapacity,
                handler.fluidStack.amount, x + 98, y + 19, 0, 52, 16);
    }



    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);

        renderUnitMarkers(context);
        renderFluidTooltip(context, mouseX, mouseY, x, y, handler.fluidStack, 98, 19, fluidStackRenderer);
    }

    private void renderFluidTooltip(DrawContext context, int mouseX, int mouseY, int x, int y, FluidStack fluidStack,
                                    int offsetX, int offsetY, FluidStackRenderer renderer) {
        if(isMouseAboveArea(mouseX, mouseY, x, y, offsetX, offsetY, renderer)) {
            context.drawTooltip(textRenderer, renderer.getTooltip(fluidStack, TooltipContext.BASIC),
                    Optional.empty(), mouseX - x, mouseY - y);
        }
    }

    private void renderRightArrow(DrawContext context, int x, int y){
        context.drawTexture(TEXTURE, x+80, y+19, 176, 0, handler.getScaledProgress(), 8);
    }

    private void renderLeftArrow(DrawContext context, int x, int y){
        context.drawTexture(TEXTURE, x+95 - handler.getScaledProgress(), y+64, 191 - handler.getScaledProgress(), 8, handler.getScaledProgress() + 1, 8);
    }

    private void renderDownArrow(DrawContext context, int x, int y){
        context.drawTexture(TEXTURE, x+62, y+37, 192, 0, 8, handler.getScaledProgress());
    }

    private void renderSyringeSlot(DrawContext context, int x, int y) {
        context.drawTexture(TEXTURE, x+62, y+55, 192, 16, 16, 16);
    }

    private void renderUnitMarkers(DrawContext context){
        context.drawTexture(TEXTURE, 98, 19, 176, 16, 16, 52);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context);
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }

    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidStackRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }
}
