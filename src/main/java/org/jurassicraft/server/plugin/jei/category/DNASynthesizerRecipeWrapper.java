package org.jurassicraft.server.plugin.jei.category;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jurassicraft.server.item.ItemHandler;
import org.jurassicraft.server.plugin.jei.category.ingredient.SynthesizerInput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DNASynthesizerRecipeWrapper implements IRecipeWrapper {
    private final SynthesizerInput input;

    public DNASynthesizerRecipeWrapper(SynthesizerInput input) {
        this.input = input;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        int metadata = this.input.getMetadata();
        List<ItemStack> inputs = new ArrayList<>();
        ItemStack data = new ItemStack(ItemHandler.STORAGE_DISC);
        data.setTagCompound(this.input.getTag());
        inputs.add(data);
        ingredients.setInputs(ItemStack.class, inputs);
        ItemStack output = new ItemStack(this.input.getItem(), 1, metadata);
        ingredients.setOutput(ItemStack.class, output);
    }

    @Override
    public List getInputs() {
        return Collections.emptyList();
    }

    @Override
    public List getOutputs() {
        return Collections.emptyList();
    }

    @Override
    public List<FluidStack> getFluidInputs() {
        return Collections.emptyList();
    }

    @Override
    public List<FluidStack> getFluidOutputs() {
        return Collections.emptyList();
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
    }

    @Override
    public void drawAnimations(Minecraft minecraft, int recipeWidth, int recipeHeight) {
    }

    @Override
    public List<String> getTooltipStrings(int mouseX, int mouseY) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
        return false;
    }
}
