package com.github.vfyjxf.neiutilities.nei;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.*;
import codechicken.nei.api.GuiInfo;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.*;
import com.github.vfyjxf.neiutilities.config.NeiUtilitiesConfig;
import com.github.vfyjxf.neiutilities.config.SplittingMode;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.gui.GuiDraw.getMousePosition;
import static com.github.vfyjxf.neiutilities.config.NeiUtilitiesConfig.useRows;

public class AdvancedItemPanel extends ItemPanel implements ICraftingHandler, IUsageHandler, IContainerInputHandler {

    public static final AdvancedItemPanel INSTANCE = new AdvancedItemPanel();

    public boolean isMouseOverHistory = false;

    public AdvancedItemPanel() {
        this.grid = new AdvancedItemPanelGrid();
    }

    public void addHistoryItem(Object... results) {
        if (results.length > 0 && results[0] instanceof ItemStack) {
            this.getAdvancedGrid().addHistoryItem((ItemStack) results[0]);
        }
    }

    public AdvancedItemPanelGrid getAdvancedGrid() {
        return (AdvancedItemPanelGrid) this.grid;
    }

    protected static class AdvancedItemPanelGrid extends ItemPanelGrid {

        private int startIndex;
        private final List<ItemStack> historyItems = new ArrayList<>();
        private boolean[] validSlotMap;

        public ItemStack getHistoryItem(int slotIndex) {
            return this.historyItems.get(slotIndex - startIndex);
        }

        public Rectangle4i getHistoryRect() {

            if (columns > 0 && rows > 0) {
                Rectangle4i rect = getSlotRect(startIndex);
                rect.w = rect.w * this.columns;
                rect.h = rect.h * useRows;
                return rect;
            } else {
                //Some guis are too big so there is no place to display the history
                return new Rectangle4i(0, 0, 0, 0);
            }

        }

        @Override
        public void setGridSize(int mLeft, int mTop, int w, int h) {
            super.setGridSize(mLeft, mTop, w, h);
            rows = (height / SLOT_SIZE) - useRows;
            this.startIndex = this.columns * this.rows;
        }

        @Override
        public ItemPanelSlot getSlotMouseOver(int mouseX, int mouseY) {

            if (!contains(mouseX, mouseY)) {
                return null;
            }

            final int overRow = (mouseY - marginTop) / SLOT_SIZE;
            final int overColumn = (mouseX - marginLeft - (width % SLOT_SIZE) / 2) / SLOT_SIZE;

            if (overColumn < columns) {
                if (overRow < rows) {
                    final int slt = columns * overRow + overColumn;
                    int idx = page * perPage + slt;

                    for (int i = 0; i < slt; i++) {
                        if (invalidSlotMap[i]) {
                            idx--;
                        }
                    }

                    return idx < size() ? new ItemPanelSlot(idx, realItems.get(idx)) : null;
                }

                if (overRow <= rows + useRows) {
                    for (int i = 0; i < validSlotMap.length && i < historyItems.size(); i++) {
                        if (validSlotMap[i]) {
                            if (getSlotRect(startIndex + i).contains(mouseX, mouseY)) {
                                return new ItemPanelSlot(startIndex + i, historyItems.get(i));
                            }
                        }
                    }
                }

            }

            return null;
        }

        @Override
        public void refresh(GuiContainer gui) {
            super.refresh(gui);
            updateValidSlots();
        }

        public void addHistoryItem(ItemStack itemStack) {
            if (itemStack != null) {
                ItemStack is = itemStack.copy();
                is.stackSize = 1;
                historyItems.removeIf(stack -> stack.isItemEqual(is));
                historyItems.add(0, is);
                if (historyItems.size() > (useRows * columns)) {
                    historyItems.remove((useRows * columns) - 1);
                }
            }
        }

        public void updateValidSlots() {
            this.validSlotMap = new boolean[this.columns * useRows];
            for (int i = 0; i < validSlotMap.length; i++) {
                if (slotValid(NEIClientUtils.getGuiContainer(), i)) {
                    this.validSlotMap[i] = true;
                }
            }
        }

        private boolean slotValid(GuiContainer gui, int idx) {
            Rectangle4i rect = getSlotRect(this.startIndex + idx);
            for (INEIGuiHandler handler : GuiInfo.guiHandlers) {
                if (handler.hideItemPanelSlot(gui, rect.x, rect.y, rect.w, rect.h)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void draw(int mouseX, int mouseY) {
            super.draw(mouseX, mouseY);
            GuiContainerManager.enableMatrixStackLogging();
            //draw history highlighted area
            Rectangle4i firstRect = getSlotRect(this.startIndex);
            if (NeiUtilitiesConfig.getSplittingMode() == SplittingMode.BACKGROUND) {
                GuiDraw.drawRect(firstRect.x, firstRect.y, this.columns * firstRect.w, useRows * firstRect.h, NeiUtilitiesConfig.historyColor);
            } else {
                drawSplittingArea(firstRect.x, firstRect.y, this.columns * firstRect.w, useRows * firstRect.h, NeiUtilitiesConfig.historyColor);
            }
            for (int i = 0; i < this.validSlotMap.length && i < historyItems.size(); i++) {
                if (validSlotMap[i]) {
                    Rectangle4i rect = getSlotRect(startIndex + i);
                    ItemPanelSlot slot = getSlotMouseOver(mouseX, mouseY);
                    if (slot != null && slot.slotIndex == startIndex + i) {
                        GuiDraw.drawRect(rect.x, rect.y, rect.w, rect.h, 0xee555555);//highlight
                    }
                    GuiContainerManager.drawItem(rect.x + 1, rect.y + 1, historyItems.get(i));
                }
            }
            GuiContainerManager.disableMatrixStackLogging();
        }

        private void drawSplittingArea(int x, int y, int width, int height, int color) {


            float alpha = (float) (color >> 24 & 255) / 255.0F;
            float red = (float) (color >> 16 & 255) / 255.0F;
            float green = (float) (color >> 8 & 255) / 255.0F;
            float blue = (float) (color & 255) / 255.0F;

            int maxX = x + width;
            int maxY = y + height;

            GL11.glPushMatrix();

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glEnable(GL11.GL_LINE_STIPPLE);
            GL11.glColor4f(red,green,blue,alpha);
            GL11.glLineWidth(2F);
            GL11.glLineStipple(2, (short) 0x00FF);

            GL11.glBegin(GL11.GL_LINE_LOOP);

            GL11.glVertex2i(x, y);
            GL11.glVertex2i(x + width, y);
            GL11.glVertex2i(x + width, y + height);
            GL11.glVertex2i(x, y + height);

            GL11.glEnd();

            GL11.glLineStipple(1, (short)0xFFFF);
            GL11.glLineWidth(1F);
            GL11.glDisable(GL11.GL_LINE_STIPPLE);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1F, 1F, 1F, 1F);

            GL11.glPopMatrix();

        }

    }

    @Override
    public boolean handleClick(int mouseX, int mouseY, int button) {
        boolean result = super.handleClick(mouseX, mouseY, button);
        this.isMouseOverHistory = this.getAdvancedGrid().getHistoryRect().contains(mouseX, mouseY);
        return result;
    }

    /**
     * We should not open the recipe in GuiRecipe because it has already been processed by RecipeItemInputHandler,
     * this will cause the recipe to be opened twice and therefore the history item will not be added properly
     */
    @Override
    public void mouseUp(int mouseX, int mouseY, int button) {

        ItemPanelSlot hoverSlot = getSlotMouseOver(mouseX, mouseY);

        if (hoverSlot != null && hoverSlot.slotIndex == mouseDownSlot && draggedStack == null) {
            ItemStack item = hoverSlot.item;

            if (!(NEIController.manager.window instanceof GuiRecipe) && !NEIClientConfig.canCheatItem(item)) {

                if (button == 0)
                    GuiCraftingRecipe.openRecipeGui("item", item);
                else if (button == 1)
                    GuiUsageRecipe.openRecipeGui("item", item);

                mouseDownSlot = -1;
                return;
            }
            boolean isOverHistory = this.getAdvancedGrid().getHistoryRect().contains(mouseX, mouseY);
            NEIClientUtils.cheatItem(getDraggedStackWithQuantity(isOverHistory, hoverSlot.slotIndex), button, -1);
        }

        mouseDownSlot = -1;

    }

    @Override
    public void mouseDragged(int mouseX, int mouseY, int button, long heldTime) {
        if (mouseDownSlot >= 0 && draggedStack == null && NEIClientUtils.getHeldItem() == null &&
                NEIClientConfig.hasSMPCounterPart() && !GuiInfo.hasCustomSlots(NEIClientUtils.getGuiContainer())) {
            ItemPanelSlot mouseOverSlot = getSlotMouseOver(mouseX, mouseY);

            if (mouseOverSlot == null || mouseOverSlot.slotIndex != mouseDownSlot || heldTime > 500) {
                draggedStack = this.getDraggedStackWithQuantity(isMouseOverHistory, mouseDownSlot);
                mouseDownSlot = -1;
                isMouseOverHistory = false;
            }

        }
    }

    /**
     * In fact, this method is specifically designed for {@link AdvancedItemPanel#mouseDragged(int, int, int, long)},
     * because it is not credible to determine whether the mouse
     * is over the history area in {@link AdvancedItemPanel#getDraggedStackWithQuantity(int)}
     */
    public ItemStack getDraggedStackWithQuantity(boolean isHistory, int mouseDownSlot) {
        ItemStack stack = isHistory ? this.getAdvancedGrid().getHistoryItem(mouseDownSlot) : grid.getItem(mouseDownSlot);

        if (stack != null) {
            int amount = NEIClientConfig.getItemQuantity();

            if (amount == 0) {
                amount = stack.getMaxStackSize();
            }

            return NEIServerUtils.copyStack(stack, amount);
        }

        return null;
    }

    @Override
    protected ItemStack getDraggedStackWithQuantity(int mouseDownSlot) {
        Point mousePos = getMousePosition();
        boolean isMouseOverHistoryItem = this.getAdvancedGrid().getHistoryRect().contains(mousePos.x, mousePos.y);
        ItemStack stack = isMouseOverHistoryItem ? this.getAdvancedGrid().getHistoryItem(mouseDownSlot) : grid.getItem(mouseDownSlot);

        if (stack != null) {
            int amount = NEIClientConfig.getItemQuantity();

            if (amount == 0) {
                amount = stack.getMaxStackSize();
            }

            return NEIServerUtils.copyStack(stack, amount);
        }

        return null;
    }

    @Override
    public boolean mouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {
        return this.getAdvancedGrid().getHistoryRect().contains(mouseX, mouseY);
    }


    @Override
    public ICraftingHandler getRecipeHandler(String outputId, Object... results) {
        this.addHistoryItem(results);
        return this;
    }

    @Override
    public IUsageHandler getUsageHandler(String inputId, Object... ingredients) {
        this.addHistoryItem(ingredients);
        return this;
    }

    @Override
    public boolean keyTyped(GuiContainer gui, char keyChar, int keyCode) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer gui, char keyChar, int keyID) {

    }

    @Override
    public boolean lastKeyTyped(GuiContainer gui, char keyChar, int keyID) {
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer gui, int mouseX, int mouseY, int button) {

    }

    @Override
    public void onMouseUp(GuiContainer gui, int mouseX, int mouseY, int button) {

    }

    @Override
    public boolean mouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer gui, int mouseX, int mouseY, int scrolled) {

    }

    @Override
    public void onMouseDragged(GuiContainer gui, int mouseX, int mouseY, int button, long heldTime) {

    }

    @Override
    public String getRecipeName() {
        return null;
    }

    @Override
    public int numRecipes() {
        return 0;
    }

    @Override
    public void drawBackground(int recipe) {

    }

    @Override
    public void drawForeground(int recipe) {

    }

    @Override
    public List<PositionedStack> getIngredientStacks(int recipe) {
        return null;
    }

    @Override
    public List<PositionedStack> getOtherStacks(int recipeType) {
        return null;
    }

    @Override
    public PositionedStack getResultStack(int recipe) {
        return null;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
        return false;
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer gui, int recipe) {
        return null;
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer gui, int recipe) {
        return null;
    }

    @Override
    public int recipiesPerPage() {
        return 0;
    }

    @Override
    public List<String> handleTooltip(GuiRecipe gui, List<String> currentTip, int recipe) {
        return null;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe gui, ItemStack stack, List<String> currentTip, int recipe) {
        return null;
    }

    @Override
    public boolean keyTyped(GuiRecipe gui, char keyChar, int keyCode, int recipe) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiRecipe gui, int button, int recipe) {
        return true;
    }

}
