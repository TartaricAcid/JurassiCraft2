package net.ilexiconn.jurassicraft.client.render.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.jurassicraft.entity.json.JsonCreature;
import net.ilexiconn.llibrary.client.render.entity.RenderMultiPart;
import net.minecraft.client.model.ModelBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderDinosaur extends RenderMultiPart
{
    public JsonCreature jsonCreature;
    public ResourceLocation texture;

    public RenderDinosaur(JsonCreature creature) throws Exception
    {
        super(creature.getModel(), creature.getShadowSize());
        jsonCreature = creature;
        texture = new ResourceLocation(creature.getTexture()); //TODO: Genders and random textures
    }

    public void preRenderCallback(EntityLivingBase entity, float side)
    {
        float scale = jsonCreature.getScaleAdjustment();
        shadowSize = scale * jsonCreature.getShadowSize();
        GL11.glScalef(scale, scale, scale);
    }

    public ResourceLocation getEntityTexture(Entity entity)
    {
        return texture;
    }
}